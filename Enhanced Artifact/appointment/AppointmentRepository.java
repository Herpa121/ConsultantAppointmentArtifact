package appointment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
//import java.time.LocalDate;
//import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import appointment.Appointment.AppointmentStatus;
import appointment.Appointment.ConsultationType;

//Enhancements
//Prepared statements prevent SQL injection attacks and integrity of input data
//Specialized SQL queries
//Connection pooling
//Automatic resource closing
public class AppointmentRepository {

	private final Connection connection; // Connection session with database

	public AppointmentRepository(Connection connection) {
		this.connection = connection;
	}

	// Add a new appointment to the database
	// Returns appointment object when specified
	public <optional> Appointment saveAppointment(Appointment appointment) throws SQLException {
		String query = "INSERT INTO appointments (id, date, time, location, clientName, consultantName, description, duration, consultationType, status) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, appointment.getId());
			stmt.setDate(2, java.sql.Date.valueOf(appointment.getDate()));
			stmt.setTime(3, java.sql.Time.valueOf(appointment.getTime()));
			stmt.setString(4, appointment.getLocation());
			stmt.setString(5, appointment.getClientName());
			stmt.setString(6, appointment.getConsultantName());
			stmt.setString(7, appointment.getDescription());
			stmt.setInt(8, appointment.getDuration());
			stmt.setString(9, appointment.getConsultationType().name());
			stmt.setString(10, appointment.getStatus().name());

			stmt.executeUpdate();

			return appointment;
		}
	}

	// Update an existing appointment
	public void updateAppointment(Appointment appointment) throws SQLException {
		String query = "UPDATE appointments SET date = ?, time = ?, location = ?, clientName = ?, consultantName = ?, description = ?, duration = ?, consultationType = ?, status = ? "
				+ "WHERE id = ?";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setDate(1, java.sql.Date.valueOf(appointment.getDate()));
			stmt.setTime(2, java.sql.Time.valueOf(appointment.getTime()));
			stmt.setString(3, appointment.getLocation());
			stmt.setString(4, appointment.getClientName());
			stmt.setString(5, appointment.getConsultantName());
			stmt.setString(6, appointment.getDescription());
			stmt.setInt(7, appointment.getDuration());
			stmt.setString(8, appointment.getConsultationType().name());
			stmt.setString(9, appointment.getStatus().name());
			stmt.setString(10, appointment.getId());

			stmt.executeUpdate();
		}
	}

	// Retrieve an appointment by ID
	public Appointment getAppointmentById(String id) throws SQLException {
		String query = "SELECT * FROM appointments WHERE id = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, id);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				return new Appointment(rs.getString("id"), rs.getDate("date").toLocalDate(),
						rs.getTime("time").toLocalTime(), rs.getString("location"), rs.getString("clientName"),
						rs.getString("consultantName"), rs.getString("description"), rs.getInt("duration"),
						ConsultationType.valueOf(rs.getString("consultationType")),
						AppointmentStatus.valueOf(rs.getString("status")));

			}
			return null;
		}
	}

	// Delete an appointment and return a boolean indicating success
	public boolean deleteAppointment(String id) throws SQLException {
		String query = "DELETE FROM appointments WHERE id = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, id);

			int rowsAffected = stmt.executeUpdate();
			return rowsAffected > 0; // true if at least one row was deleted
		}
	}

	// Retrieve all appointments
	public List<Appointment> getAllAppointments() throws SQLException {
		List<Appointment> appointments = new ArrayList<>();
		String query = "SELECT * FROM appointments";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				Appointment appointment = new Appointment(rs.getString("id"), rs.getDate("date").toLocalDate(),
						rs.getTime("time").toLocalTime(), rs.getString("location"), rs.getString("clientName"),
						rs.getString("consultantName"), rs.getString("description"), rs.getInt("duration"),
						ConsultationType.valueOf(rs.getString("consultationType")), // Convert to enum
						AppointmentStatus.valueOf(rs.getString("status")) // Convert to enum
				);

				appointments.add(appointment);
			}
		}

		return appointments;
	}

	// Get appointments by consultant and date
	public List<Appointment> getAppointmentsByConsultantAndDate(String consultantName, LocalDate date)
			throws SQLException {
		String query = "SELECT * FROM appointments WHERE consultantName = ? AND date = ?";
		List<Appointment> appointments = new ArrayList<>();

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, consultantName);
			stmt.setDate(2, java.sql.Date.valueOf(date));

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				Appointment appointment = new Appointment(rs.getString("id"), rs.getDate("date").toLocalDate(),
						rs.getTime("time").toLocalTime(), rs.getString("location"), rs.getString("clientName"),
						rs.getString("consultantName"), rs.getString("description"), rs.getInt("duration"),
						ConsultationType.valueOf(rs.getString("consultationType")), // Convert to enum
						AppointmentStatus.valueOf(rs.getString("status")) // Convert to enum
				);
				appointments.add(appointment);
			}
		}
		return appointments;
	}

	// Update status
	public void updateAppointmentStatus(String appointmentId, AppointmentStatus status) throws SQLException {
		String query = "UPDATE appointments SET status = ? WHERE id = ?";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, status.toString());
			stmt.setString(2, appointmentId);
			stmt.executeUpdate();
		}
	}

	// Update date and time
	public void updateAppointmentTime(String appointmentId, LocalDate newDate, LocalTime newTime) throws SQLException {
		// TODO Auto-generated method stub
		String query = "UPDATE appointments SET date = ?, time = ? WHERE id = ?";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setDate(1, java.sql.Date.valueOf(newDate));
			stmt.setTime(2, java.sql.Time.valueOf(newTime));
			stmt.setString(3, appointmentId);
			stmt.executeUpdate();
		}

	}

	// Update duration
	public void updateAppointmentDuration(String appointmentId, int duration) throws SQLException {
		// TODO Auto-generated method stub
		String query = "UPDATE appointments SET duration = ? WHERE id = ?";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, duration);
			stmt.setString(2, appointmentId);
			stmt.executeUpdate();
		}

	}

}
