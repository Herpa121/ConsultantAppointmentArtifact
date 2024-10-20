package appointmentTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import appointment.Appointment;
import appointment.AppointmentRepository;
import appointment.AppointmentService;
import appointment.AppointmentValidator;
import appointment.Appointment.AppointmentStatus;
import appointment.Appointment.ConsultationType;
import security.User;
import security.User.UserRole;

//Mockito injection tests for isolation and inclusion assurance
public class AppointmentServiceTest {

	@Mock
	private AppointmentRepository appointmentRepository;

	@Mock
	private AppointmentValidator appointmentValidator;

	@Mock
	private User currentUser;

	@InjectMocks
	private AppointmentService appointmentService;

	private Appointment appointment;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);

		// Mock currentUser and set up role
		when(currentUser.getRole()).thenReturn(UserRole.CONSULTANT);

		// Initialize sample appointment
		appointment = new Appointment(UUID.randomUUID().toString(), LocalDate.of(2025, 10, 10), LocalTime.of(10, 30),
				"Office 1", "Client1", "Consultant1", "Discussion", 60, Appointment.ConsultationType.IN_PERSON,
				Appointment.AppointmentStatus.SCHEDULED);
	}

	@Test
    public void testAddAppointmentSuccess() throws SQLException {
        // Mock the validator and repository functionality
        when(appointmentValidator.validate(appointment)).thenReturn(null);  // No validation error
        when(appointmentRepository.getAppointmentsByConsultantAndDate(anyString(), any(LocalDate.class)))
            .thenReturn(new ArrayList<>());  // No existing appointments
        //doNothing().when(appointmentRepository).saveAppointment(any(Appointment.class)); // Class has optional type, cannot doNothing

        // Perform service call
        String result = appointmentService.addAppointment(appointment);

        // Assertions - expect successful
        assertEquals("Appointment successfully scheduled.", result);
        verify(appointmentRepository).saveAppointment(appointment);
    }

	@Test
    public void testAddAppointmentValidationError() throws SQLException {
        // Mock validation error
        when(appointmentValidator.validate(appointment)).thenReturn("Validation Error");

        // Perform service call
        String result = appointmentService.addAppointment(appointment);

        // Assertions - expect validation error
        assertEquals("Validation Error", result);
        verify(appointmentRepository, never()).saveAppointment(any(Appointment.class));  // Save should not be called
    }

	@Test
    public void testAddAppointmentTimeConflict() throws SQLException {
        // Mock no validation errors but a time conflict with another appointment
        when(appointmentValidator.validate(appointment)).thenReturn(null);
        
        // Simulate an existing appointment at the same time
        List<Appointment> existingAppointments = new ArrayList<>();
        existingAppointments.add(new Appointment(UUID.randomUUID().toString(), LocalDate.of(2025, 10, 10),
				LocalTime.of(10, 0), "Meeting room Office 2", "Consultant1", "John Doe",
				"Database Enhancements Consultation", 60, ConsultationType.IN_PERSON, AppointmentStatus.SCHEDULED));
        
        when(appointmentRepository.getAppointmentsByConsultantAndDate("Consultant1", LocalDate.of(2025, 10, 10)))
            .thenReturn(existingAppointments);

        // Perform the service call
        String result = appointmentService.addAppointment(appointment);

        // Assertions - expect not available
        assertEquals("The requested time slot is not available.", result);
        verify(appointmentRepository, never()).saveAppointment(any(Appointment.class));  // Save should not be called
    }

	@Test
    public void testRescheduleAppointmentSuccess() throws SQLException {
        // Mock existing appointment in repository
        when(appointmentRepository.getAppointmentById(appointment.getId())).thenReturn(appointment);
        
        // Mock validator and repository functionality
        when(appointmentValidator.validate(any(Appointment.class))).thenReturn(null);  // No validation error
        when(appointmentRepository.getAppointmentsByConsultantAndDate(anyString(), any(LocalDate.class)))
            .thenReturn(new ArrayList<>());  // No existing appointments at new time
        doNothing().when(appointmentRepository).updateAppointment(any(Appointment.class));

        // Perform service call for rescheduling
        String result = appointmentService.rescheduleAppointment(appointment.getId(), LocalDate.of(2025, 10, 12), LocalTime.of(14, 0));

        // Assertions - successful reschedule
        assertEquals("Appointment successfully rescheduled.", result);
        verify(appointmentRepository, never()).updateAppointment(any(Appointment.class));  // Make sure the update is not called
    }

	@Test
    public void testRescheduleAppointmentTimeConflict() throws SQLException {
        // Mock existing appointment in the repository
        when(appointmentRepository.getAppointmentById(appointment.getId())).thenReturn(appointment);
        
        // Mock no validation errors but a time conflict with another appointment
        when(appointmentValidator.validate(any(Appointment.class))).thenReturn(null);
        
        // Simulate/mock an existing appointment at new requested time
        // Returning a list of appointments similar to getAppointmentsByConsultantandDate
        List<Appointment> conflictingAppointments = new ArrayList<>();
        conflictingAppointments.add(new Appointment(UUID.randomUUID().toString(), LocalDate.of(2025, 10, 10),
				LocalTime.of(14, 0), "Meeting room Office 2", "Consultant1", "John Doe",
				"Database Enhancements Consultation", 60, ConsultationType.IN_PERSON, AppointmentStatus.SCHEDULED));
        
        when(appointmentRepository.getAppointmentsByConsultantAndDate("Consultant1", LocalDate.of(2025, 10, 10)))
            .thenReturn(conflictingAppointments);

        // Perform service call for rescheduling
        String result = appointmentService.rescheduleAppointment(appointment.getId(), LocalDate.of(2025, 10, 10), LocalTime.of(14, 0));

        // Assertions - time slot issue reschedule
        assertEquals("Error: The requested time slot is not available for rescheduling.", result);
        verify(appointmentRepository, never()).updateAppointment(any(Appointment.class));  // Ensure update is not called
    }

}
