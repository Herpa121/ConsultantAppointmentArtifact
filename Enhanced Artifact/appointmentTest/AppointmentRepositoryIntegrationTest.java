package appointmentTest;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import appointment.Appointment;
import appointment.HikariCPDataSource;
import appointment.Appointment.AppointmentStatus;
import appointment.Appointment.ConsultationType;
import appointment.AppointmentRepository;

class AppointmentRepositoryIntegrationTest {
	private static Connection connection; // Sql.connection
	private AppointmentRepository appointmentRepository;

	// Before each, set up connection, create repository object, empty out database
	@BeforeEach
	public void setup() throws SQLException {
		// Set up connection
		connection = HikariCPDataSource.getConnection();
		// Initialize repository with connection
		appointmentRepository = new AppointmentRepository(connection);
		// Empty out database
		tearDown();
	}

	// After each, empty out database in case external additions occured
	@AfterEach
	public void tearDown() throws SQLException {
		// Clean up the database
		try (Statement stmt = connection.createStatement()) {
			stmt.execute("DELETE FROM appointments");
		}
	}

	// After tests, close database connection to prevent automatic closing unless
	// being tested
	@AfterAll
	public static void closeDatabaseConnection() throws SQLException {
		if (connection != null) {
			connection.close();
		}
	}

	// Test for adding an appointment with valid arguments
	@Test
	public void testAddAppointment() throws SQLException {
		// Arrange: Insert an appointment
		Appointment appointment = new Appointment(UUID.randomUUID().toString(), LocalDate.of(2025, 10, 15),
				LocalTime.of(9, 0), "Meeting room Office 2", "Bill Clientson", "John Doe",
				"Database Enhancements Consultation", 60, ConsultationType.IN_PERSON, AppointmentStatus.SCHEDULED);

		// Act: Save the appointment
		Appointment success = appointmentRepository.saveAppointment(appointment);

		// Assert: Return object
		assertNotNull(success, "Appointment should be successfully added to the database.");

		// Verify by querying the database
		String query = "SELECT * FROM appointments WHERE clientName = 'Bill Clientson'";
		try (PreparedStatement pstmt = connection.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

			assertTrue(rs.next(), "Result should contain the added appointment.");
			assertEquals("Meeting room Office 2", rs.getString("location"), "Location should match.");
			assertEquals("Database Enhancements Consultation", rs.getString("description"),
					"Description should match.");
			assertEquals("John Doe", rs.getString("consultantName"), "Consultant name should match.");
			assertEquals(60, rs.getInt("duration"), "Duration should match.");
		}
	}

	// Test for deleting an appointment
	@Test
	public void testDeleteAppointment() throws SQLException {
		// Arrange: Insert an appointment
		Appointment appointment = new Appointment(UUID.randomUUID().toString(), LocalDate.of(2025, 10, 15),
				LocalTime.of(9, 0), "Meeting room Office 2", "Bill Clientson", "John Doe",
				"Database Enhancements Consultation", 60, ConsultationType.IN_PERSON, AppointmentStatus.SCHEDULED);
		appointmentRepository.saveAppointment(appointment);

		// Act: delete the appointment
		boolean isDeleted = appointmentRepository.deleteAppointment(appointment.getId());

		// Assert: Return true
		assertTrue(isDeleted, "Appointment should be successfully deleted.");

		// Verify by querying the database
		String query = "SELECT * FROM appointments WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, appointment.getId());
			ResultSet rs = pstmt.executeQuery();

			assertFalse(rs.next(), "Result should not contain the deleted appointment.");
		}
	}

	// Test for getting appointment object from Id query
	@Test
	public void testGetAppointmentById() throws SQLException {
		// Arrange: Insert an appointment
		Appointment appointment = new Appointment(UUID.randomUUID().toString(), LocalDate.of(2025, 10, 15),
				LocalTime.of(9, 0), "Meeting room Office 2", "Bill Clientson", "John Doe",
				"Database Enhancements Consultation", 60, ConsultationType.IN_PERSON, AppointmentStatus.SCHEDULED);
		appointmentRepository.saveAppointment(appointment);

		// Act: Receive the appointment by id
		Appointment retrievedAppointment = appointmentRepository.getAppointmentById(appointment.getId());

		// Assert: Return object
		assertNotNull(retrievedAppointment, "Retrieved appointment should not be null.");

		assertEquals(appointment.getId(), retrievedAppointment.getId(), "ID should match.");
		assertEquals(appointment.getLocation(), retrievedAppointment.getLocation(), "Location should match.");
	}

	// Test for updating appointment status
	@Test
	public void testUpdateAppointmentStatus() throws SQLException {
		// Arrange: Insert an appointment
		Appointment appointment = new Appointment(UUID.randomUUID().toString(), LocalDate.of(2025, 10, 15),
				LocalTime.of(9, 0), "Meeting room Office 2", "Bill Clientson", "John Doe",
				"Database Enhancements Consultation", 60, ConsultationType.IN_PERSON, AppointmentStatus.SCHEDULED);
		appointmentRepository.saveAppointment(appointment);

		// Act: Update the status of the appointment
		AppointmentStatus newStatus = AppointmentStatus.CANCELED;
		appointmentRepository.updateAppointmentStatus(appointment.getId(), newStatus);

		// Assert: Query
		String query = "SELECT status FROM appointments WHERE id = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, appointment.getId());
			ResultSet rs = stmt.executeQuery();

			assertTrue(rs.next(), "Appointment should exist in the database.");
			assertEquals(newStatus.toString(), rs.getString("status"), "The status should be updated to CANCELED.");
		}
	}

	// Test for updating appointment time
	@Test
	public void testUpdateAppointmentTime() throws SQLException {
		// Arrange: Insert an appointment
		Appointment appointment = new Appointment(UUID.randomUUID().toString(), LocalDate.of(2025, 10, 15),
				LocalTime.of(9, 0), "Meeting room Office 2", "Bill Clientson", "John Doe",
				"Database Enhancements Consultation", 60, ConsultationType.IN_PERSON, AppointmentStatus.SCHEDULED);
		appointmentRepository.saveAppointment(appointment);

		// Act: Update the date and time of the appointment
		LocalDate newDate = LocalDate.of(2025, 10, 20); // Example new date
		LocalTime newTime = LocalTime.of(14, 0); // Example new time
		appointmentRepository.updateAppointmentTime(appointment.getId(), newDate, newTime);

		// Assert: Query
		String query = "SELECT date, time FROM appointments WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, appointment.getId());
			ResultSet rs = pstmt.executeQuery();

			assertTrue(rs.next(), "Appointment should exist in the database.");

			assertEquals(newDate, rs.getDate("date").toLocalDate(), "The date should be updated to 2025-10-20.");
			assertEquals(newTime, rs.getTime("time").toLocalTime(), "The time should be updated to 14:00.");
		}
	}

}
