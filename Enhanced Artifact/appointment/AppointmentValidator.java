package appointment;

import java.time.LocalDate;
import java.time.LocalTime;

import appointment.Appointment.AppointmentStatus;
import appointment.Appointment.ConsultationType;

//Enhancement To-do:
//expand validation for time, date, and duration to check for more cases

public class AppointmentValidator {

	// Validate the entire appointment modularly
	public String validate(Appointment appointment) {
		String errorMessage;

		errorMessage = validateId(appointment.getId());
		if (errorMessage != null)
			return errorMessage;

		errorMessage = validateDate(appointment.getDate());
		if (errorMessage != null)
			return errorMessage;

		errorMessage = validateTime(appointment.getTime());
		if (errorMessage != null)
			return errorMessage;

		errorMessage = validateLocation(appointment.getLocation());
		if (errorMessage != null)
			return errorMessage;

		errorMessage = validateClientName(appointment.getClientName());
		if (errorMessage != null)
			return errorMessage;

		errorMessage = validateConsultantName(appointment.getConsultantName());
		if (errorMessage != null)
			return errorMessage;

		errorMessage = validateDescription(appointment.getDescription());
		if (errorMessage != null)
			return errorMessage;

		errorMessage = validateDuration(appointment.getDuration());
		if (errorMessage != null)
			return errorMessage;

		errorMessage = validateConsultationType(appointment.getConsultationType());
		if (errorMessage != null)
			return errorMessage;

		errorMessage = validateStatus(appointment.getStatus());
		if (errorMessage != null)
			return errorMessage;

		return null; // No validation errors
	}

	// Validation methods for each field
	private String validateId(String id) {
		if (id == null || id.isEmpty()) {
			return "Appointment ID cannot be empty.";
		}
		return null;
	}

	private String validateDate(LocalDate date) {
		if (date == null || date.isBefore(LocalDate.now())) {
			return "Appointment date cannot be in the past.";
		}
		return null;
	}

	private String validateTime(LocalTime time) {
		if (time == null) {
			return "Appointment time cannot be empty.";
		}
		return null;
	}

	private String validateLocation(String location) {
		if (location == null || location.isEmpty()) {
			return "Location cannot be empty.";
		}
		return null;
	}

	private String validateClientName(String clientName) {
		if (clientName == null || clientName.isEmpty()) {
			return "Client name cannot be empty.";
		}
		return null;
	}

	private String validateConsultantName(String consultantName) {
		if (consultantName == null || consultantName.isEmpty()) {
			return "Consultant name cannot be empty.";
		}
		return null;
	}

	private String validateDescription(String description) {
		if (description == null || description.isEmpty()) {
			return "Description cannot be empty.";
		}
		return null;
	}

	private String validateDuration(int duration) {
		if (duration <= 0) {
			return "Duration must be greater than zero.";
		}
		else if (duration > 300) {
			return "Duration cannot be longer than 5 hours";
		}
		return null;
	}
	
	private String validateConsultationType(ConsultationType consultationType) {
	    if (consultationType == null) {
	        return "Consultation type cannot be empty.";
	    }
	    return null;
	}

	private String validateStatus(AppointmentStatus status) {
	    if (status == null) {
	        return "Status cannot be empty.";
	    }
	    return null;
	}

}
