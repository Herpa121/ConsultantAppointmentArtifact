package appointment;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import security.User;
import security.User.UserRole;

public class AppointmentService {

	private final AppointmentRepository appointmentRepository;
	private final AppointmentValidator appointmentValidator;
	// private final Map<String, Appointment> appointmentCache = new HashMap<>();
	private User currentUser;

	public AppointmentService(AppointmentRepository appointmentRepository, AppointmentValidator appointmentValidator,
			User currentUser) {
		this.appointmentRepository = appointmentRepository;
		this.appointmentValidator = appointmentValidator;
		this.currentUser = currentUser;

	}

	// Add an appointment
	public String addAppointment(Appointment appointment) {
		if (!currentUser.getRole().equals(UserRole.CONSULTANT)) {
			return "Access Denied: Must be a consultant to add appointments";
		}

		// Validate the appointment
		String validationError = appointmentValidator.validate(appointment);
		if (validationError != null) {
			return validationError; // Return validation error message
		}

		// Perform business logic
		if (!checkAvailabilitySpecific(appointment)) {
			return "The requested time slot is not available.";
		}

		try {
			// Save the appointment in the database
			appointmentRepository.saveAppointment(appointment);
			// appointmentCache.put(appointment.getId(), appointment);

			return "Appointment successfully scheduled.";
		} catch (SQLException e) {
			// Handle database errors
			return "Failed to schedule appointment due to a database error: " + e.getMessage();
		}
	}

	// Update an existing appointment
	public String updateAppointment(Appointment appointment) {
		if (!currentUser.getRole().equals(UserRole.CONSULTANT)) {
			return "Access Denied: Must be a consultant to update appointments";
		}
		// Validate the updated appointment details
		String validationError = appointmentValidator.validate(appointment);
		if (validationError != null) {

			return validationError; // Return validation error message
		}

		try {
			// Update the database
			appointmentRepository.updateAppointment(appointment);
			// appointmentCache.put(appointment.getId(), appointment);
			return "Appointment successfully updated.";
		} catch (SQLException e) {
			// Handle database errors
			return "Failed to update appointment due to a database error: " + e.getMessage();
		}
	}

	// Get an appointment by ID
	public Appointment getAppointmentById(String id) {
		// Check if the appointment exists in the local cache - obsolete
		try {
			// If not in cache, retrieve from the database
			Appointment appointment = appointmentRepository.getAppointmentById(id);
			return appointment;
		} catch (SQLException e) {
			// Handle database errors
			System.err.println("Failed to retrieve appointment due to a database error: " + e.getMessage());

			return null;
		}
	}

	// Delete an appointment by ID
	public String deleteAppointment(String id) {
		if (!currentUser.getRole().equals(UserRole.CONSULTANT)) {
			return "Access Denied: Must be a consultant to delete appointments";
		}
		try {
			// Remove from both database
			appointmentRepository.deleteAppointment(id);
			// appointmentCache.remove(id);
			return "Appointment successfully deleted.";
		} catch (SQLException e) {
			// Handle database errors
			return "Failed to delete appointment due to a database error: " + e.getMessage();
		}
	}

	// Retrieve all appointments
	public List<Appointment> getAllAppointments() {
		try {
			// Retrieve all appointments from the database
			return appointmentRepository.getAllAppointments();
		} catch (SQLException e) {
			// Handle database errors
			System.err.println("Failed to retrieve appointment(s) due to a database error: " + e.getMessage());

			return null;
		}
	}

	// Helper method checking if two appointments overlap
	private boolean isOverlapping(Appointment existingAppointment, LocalTime newAppointmentStartTime,
			LocalTime newAppointmentEndTime) {
		// Calculate end time from duration into localTime
		LocalTime existingAppointmentEndTime = existingAppointment.getTime()
				.plusMinutes(existingAppointment.getDuration());

		// Overlap is true if the new appointment starts before the existing one ends,
		// and ends after the existing one starts
		// Inclusive of edge overlaps
		// return newAppointmentStartTime.isBefore(existingAppointmentEndTime)
		// && newAppointmentEndTime.isAfter(existingAppointment.getTime());

		// strict overlap, exclusive of edge overlaps
		return !newAppointmentEndTime.equals(existingAppointment.getTime())
				&& newAppointmentStartTime.isBefore(existingAppointmentEndTime)
				&& newAppointmentEndTime.isAfter(existingAppointment.getTime());

	}

	// Checks for overlap of all appointments of the specific day for the consultant
	boolean checkAvailabilitySpecific(Appointment appointment) {
		System.out.println("Consultant: " + appointment.getConsultantName());

		try {
			// Retrieve all appointments for the consultant on the same day from the
			// database
			List<Appointment> consultantAppointments = appointmentRepository
					.getAppointmentsByConsultantAndDate(appointment.getConsultantName(), appointment.getDate());

			// Calculate the end time of the new appointment
			LocalTime newAppointmentEndTime = appointment.getTime().plusMinutes(appointment.getDuration());

			// Check if the new appointment overlaps with any existing appointments
			return consultantAppointments.stream().noneMatch(existingAppointment -> isOverlapping(existingAppointment,
					appointment.getTime(), newAppointmentEndTime));
		} catch (SQLException e) {
			// Handle database errors
			System.err.println("Failed to check appointment availability due to a database error: " + e.getMessage());
			return false;
		}
	}

	// Method to get available time slots for a consultant on a given date
	public List<TimeSlot> getAvailableTimeSlots(String consultantName, LocalDate date, LocalTime workingStart,
			LocalTime workingEnd) {
		try {
			// Retrieve all booked appointments for the consultant on the given date
			List<Appointment> bookedAppointments = appointmentRepository
					.getAppointmentsByConsultantAndDate(consultantName, date);

			// TreeSet to store appointments sorted by start time
			TreeSet<Appointment> sortedAppointments = new TreeSet<>(Comparator.comparing(Appointment::getTime));
			sortedAppointments.addAll(bookedAppointments);

			// List to store available time slots
			List<TimeSlot> availableSlots = new ArrayList<>();

			// Check time before the first appointment
			LocalTime startOfWorkingDay = workingStart;

			for (Appointment appointment : sortedAppointments) {
				LocalTime appointmentStartTime = appointment.getTime();

				// If there is a gap between the start of the working day and the appointment
				if (startOfWorkingDay.isBefore(appointmentStartTime)) {
					availableSlots.add(new TimeSlot(startOfWorkingDay, appointmentStartTime));
				}

				// Move the start to the end of the current appointment
				startOfWorkingDay = appointment.getTime().plusMinutes(appointment.getDuration());
			}

			// Check for availability after the last appointment
			if (startOfWorkingDay.isBefore(workingEnd)) {
				availableSlots.add(new TimeSlot(startOfWorkingDay, workingEnd));
			}

			return availableSlots;

		} catch (SQLException e) {
			System.err.println("Error retrieving appointments: " + e.getMessage());
			return Collections.emptyList(); // Safe return null
		}
	}

	// Reschedule an appointment after checking for time conflicts while filtering
	// out current appointment
	public String rescheduleAppointment(String appointmentId, LocalDate newDate, LocalTime newTime)
			throws SQLException {
		if (!currentUser.getRole().equals(UserRole.CONSULTANT)) {
			return "Access Denied: Must be a consultant to delete appointments";
		}
		try {
			// Get the appointment to be rescheduled
			Appointment appointment = appointmentRepository.getAppointmentById(appointmentId);
			if (appointment == null) {
				return "Error: Appointment not found";
			}

			// Calculate the new end time based on the appointment's duration
			LocalTime newEndTime = newTime.plusMinutes(appointment.getDuration());

			// Get all appointments for the consultant on the same day
			List<Appointment> consultantAppointments = appointmentRepository
					.getAppointmentsByConsultantAndDate(appointment.getConsultantName(), newDate);

			// Exclude the current appointment being rescheduled
			consultantAppointments = consultantAppointments.stream()
					.filter(existingAppointment -> !existingAppointment.getId().equals(appointmentId))
					.collect(Collectors.toList());

			// Check for time conflicts with other appointments
			boolean isTimeSlotAvailable = consultantAppointments.stream()
					.noneMatch(existingAppointment -> isOverlapping(existingAppointment, newTime, newEndTime));

			if (!isTimeSlotAvailable) {
				return "Error: The requested time slot is not available for rescheduling.";
			}

			// If no conflict, update the appointment's date and time in the database
			appointmentRepository.updateAppointmentTime(appointmentId, newDate, newTime);
			return "Appointment successfully rescheduled.";
		} catch (SQLException e) {
			// Handle database errors
			return "Failed to reschedule appointment due to a database error: " + e.getMessage();
		}
	}

}
