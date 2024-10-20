package appointmentTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import appointment.Appointment;
import appointment.Appointment.AppointmentStatus;
import appointment.Appointment.ConsultationType;
import appointment.AppointmentValidator;

public class AppointmentValidatorTest {

	private AppointmentValidator validator;
	private Appointment appointment;;

	// Set up new validator with appointment
	@BeforeEach
	public void setUp() {
		validator = new AppointmentValidator();

		// Create a default valid Appointment object
		appointment = new Appointment("0393adc0-4333-413b-bf5b-a6cc250ab9ab", LocalDate.of(2025, 10, 15),
				LocalTime.of(9, 0), "Meeting room Office 2", "Bill Clientson", "John Doe",
				"Database Enhancements Consultation", 60, ConsultationType.IN_PERSON, AppointmentStatus.SCHEDULED);
	}

	// Test for valid consultation type through ENUM
	@Test
	public void testValidateConsultationType_Valid() {
		appointment.setConsultationType(ConsultationType.IN_PERSON);

		String result = validator.validate(appointment);
		assertNull(result); // Expect no validation error
	}

	// Test for invalid consultation type through null
	@Test
	public void testValidateConsultationType_Null() {
		appointment.setConsultationType(null); // Invalid type

		String result = validator.validate(appointment);
		assertNotNull(result); // Expect validation error
		assertEquals("Consultation type cannot be empty.", result);
	}
	// Test for valid status trough ENUM
	@Test
	public void testValidateStatus_Valid() {
		appointment.setStatus(AppointmentStatus.SCHEDULED);

		String result = validator.validate(appointment);
		assertNull(result); // Expect no validation error
	}
	// Test for invalid status through null
	@Test
	public void testValidateStatus_Null() {
		appointment.setStatus(null); // Invalid status

		String result = validator.validate(appointment);
		assertNotNull(result); // Expect validation error
		assertEquals("Status cannot be empty.", result);
	}
	// Test for valid date by using tomorrow
	@Test
	public void testValidateDate_Valid() {
		appointment.setDate(LocalDate.now().plusDays(1)); // Valid future date

		String result = validator.validate(appointment);
		assertNull(result); // Expect no validation error
	}
	// Test for invalid date using yesterday
	@Test
	public void testValidateDate_Invalid_LowerBoundExceeded() {
		appointment.setDate(LocalDate.now().minusDays(1)); // Invalid past date

		String result = validator.validate(appointment);
		assertNotNull(result); // Expect validation error
		assertEquals("Appointment date cannot be in the past.", result);
	}
	// Test for valid time
	@Test
	public void testValidateTime_Valid() {
		appointment.setTime(LocalTime.of(14, 0)); // Valid time

		String result = validator.validate(appointment);
		assertNull(result); // Expect no validation error
	}
	// Test for invalid time through null
	@Test
	public void testValidateTime_Null() {
		appointment.setTime(null); // Invalid null time

		String result = validator.validate(appointment);
		assertNotNull(result); // Expect validation error
		assertEquals("Appointment time cannot be empty.", result);
	}
	// Test for valid location
	@Test
	public void testValidateLocation_Valid() {
		appointment.setLocation("Meeting Room 1"); // Valid location

		String result = validator.validate(appointment);
		assertNull(result); // Expect no validation error
	}
	// Test for invalid location through empty string
	@Test
	public void testValidateLocation_EmptyString() {
		appointment.setLocation(""); // Invalid empty location

		String result = validator.validate(appointment);
		assertNotNull(result); // Expect validation error
		assertEquals("Location cannot be empty.", result);
	}
	// Test for invalid location through null
	@Test
	public void testValidateLocation_Null() {
		appointment.setLocation(null); // Invalid empty location

		String result = validator.validate(appointment);
		assertNotNull(result); // Expect validation error
		assertEquals("Location cannot be empty.", result);
	}
	// Test for valid client name
	@Test
	public void testValidateClientName_Valid() {
		appointment.setClientName("John Doe"); // Valid name

		String result = validator.validate(appointment);
		assertNull(result); // Expect no validation error
	}
	// Test for invalid client name through empty string
	@Test
	public void testValidateClientName_EmptyString() {
		appointment.setClientName(""); // Invalid empty client name

		String result = validator.validate(appointment);
		assertNotNull(result); // Expect validation error
		assertEquals("Client name cannot be empty.", result);
	}
	// Test for invalid client name through null
	@Test
	public void testValidateClientName_Null() {
		appointment.setClientName(null); // Invalid empty client name

		String result = validator.validate(appointment);
		assertNotNull(result); // Expect validation error
		assertEquals("Client name cannot be empty.", result);
	}
	// Test for valid consultant name
	@Test
	public void testValidateConsultantName_Valid() {
		appointment.setClientName("John Doe");

		String result = validator.validate(appointment);
		assertNull(result); // Expect no validation error
	}
	// Test for invalid consultant name through empty string
	@Test
	public void testValidateConsultantName_EmptyString() {
		appointment.setConsultantName(""); // Invalid empty client name

		String result = validator.validate(appointment);
		assertNotNull(result); // Expect validation error
		assertEquals("Consultant name cannot be empty.", result);
	}
	// Test for invalid consultant name through null
	@Test
	public void testValidateConsultantName_Null() {
		appointment.setConsultantName(null); // Invalid empty client name

		String result = validator.validate(appointment);
		assertNotNull(result); // Expect validation error
		assertEquals("Consultant name cannot be empty.", result);
	}
	// Test for valid duration
	@Test
	public void testValidateDuration_Valid() {
		appointment.setDuration(120); // Valid duration

		String result = validator.validate(appointment);
		assertNull(result); // Expect no validation error

	}
	// Test for invalid duration through zero
	@Test
	public void testValidateDuration_Zero() {
		appointment.setDuration(0); // Invalid duration zero

		String result = validator.validate(appointment);
		assertNotNull(result); // Expect validation error
		assertEquals("Duration must be greater than zero.", result);
	}
	// test for invalid duration through upper bound limit
	@Test
	public void testValidateDuration_UpperBoundExceeded() {
		appointment.setDuration(999); // Invalid duratin too high

		String result = validator.validate(appointment);
		assertNotNull(result); // Expect validation error
		assertEquals("Duration cannot be longer than 5 hours", result);
	}

}
