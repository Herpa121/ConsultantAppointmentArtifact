package appointment;

import security.PasswordUtils;
import security.User;
import security.User.UserRole;
import security.UserRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import appointment.Appointment.AppointmentStatus;
import appointment.Appointment.ConsultationType;

public class AppointmentDriverExample {

	// Sample showcase of logging in and viewing available time slots before and
	// after attempting to add an appointment
	public static void main(String[] args) {
		try {
			// Set up DB connection
			Connection connection = HikariCPDataSource.getConnection();

			// Create repository objects
			AppointmentRepository appointmentRepository = new AppointmentRepository(connection);
			AppointmentValidator appointmentValidator = new AppointmentValidator();
			UserRepository userRepository = new UserRepository(connection);

			// Create a new user
			String consultantUsername = "Billy Mays";
			String consultantPassword = "password";
			
			// If new user to be added
			//String hashedPassword = PasswordUtils.hashPassword(consultantPassword);
			//User consultant = new User(UUID.randomUUID().toString(), consultantUsername, hashedPassword, UserRole.CONSULTANT);
			// userRepository.saveUser(consultant); // only perform if new user

			// Login process
			User loggedInUser = userRepository.getUserByUsername(consultantUsername);

			// Check if the password matches
			if (loggedInUser != null
					&& PasswordUtils.hashPassword(consultantPassword).equals(loggedInUser.getPasswordHash())) {
				System.out.println("Login successful. User Name: " + loggedInUser.getUsername() + ". User role: " + loggedInUser.getRole());

				// Initialize the appointment service with the logged in user
				AppointmentService appointmentService = new AppointmentService(appointmentRepository,
						appointmentValidator, loggedInUser);

				// Define working hours
				LocalTime workingStart = LocalTime.of(3, 0);
				LocalTime workingEnd = LocalTime.of(19, 0);
				// Define date
				LocalDate specificDate = LocalDate.of(2025, 10, 15);
				LocalTime specificTime = LocalTime.of(9, 00);

				// Get available time slots for the consultant on a specific date
				List<TimeSlot> availableSlots = appointmentService.getAvailableTimeSlots(loggedInUser.getUsername(),
						specificDate, workingStart, workingEnd);

				// Output the available time slots
				System.out.println("Available time slots for " + loggedInUser.getUsername() + " on " + specificDate);
				availableSlots.forEach(System.out::println);

				// Add an appointment
				Appointment newAppointment = new Appointment(UUID.randomUUID().toString(), specificDate,
						specificTime, "Meeting room Office 2", "Bill Clientson", loggedInUser.getUsername(),
						"Database Enhancements Consultation", 60, ConsultationType.IN_PERSON, AppointmentStatus.SCHEDULED);

				String result = appointmentService.addAppointment(newAppointment);
				System.out.println(result);

				availableSlots = appointmentService.getAvailableTimeSlots(loggedInUser.getUsername(), specificDate,
						workingStart, workingEnd);

				// Output the available time slots again
				System.out.println("Available time slots for " + loggedInUser.getUsername() + " on " + specificDate);
				availableSlots.forEach(System.out::println);

			} else {
				System.out.println("Login failed: Invalid credentials.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
