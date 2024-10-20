package Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.Calendar;

import org.junit.jupiter.api.Test;

import Appointment.Appointment;
//import Task.Task;

class AppointmentTest {

	@Test
	void testCreateAppointmentWithoutID() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH,+5);
		Date testDate = calendar.getTime();
		String testDescription = "Test2 Test3";
		Appointment Appt = new Appointment(testDate, testDescription);
		
		 assertTrue(Appt.getID().length() == 10); // UUID is trimmed to 10 characters
		 assertTrue(Appt.getDate() == testDate);
		 assertTrue(Appt.getDescription() == testDescription);
	}
	@Test
	void testCreateAppointmentWithID() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH,+5);
		Date testDate = calendar.getTime();
		String testDescription = "Test2 Test3";
		String testID = "1234567890";
		Appointment Appt = new Appointment(testID, testDate, testDescription);
		
		 assertTrue(Appt.getID() == testID); // UUID is trimmed to 10 characters
		 assertTrue(Appt.getDate() == testDate);
		 assertTrue(Appt.getDescription() == testDescription);
	}
	@Test 
	void testLongAppointmentIDThrowsException() {
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
			String longID = "12345678901";
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH,+5);
			Date testDate = calendar.getTime();
			String testDescription = "Test5 Test6";
            new Appointment(longID, testDate, testDescription);
        });
        assertEquals("ID cannot be longer than 10 characters", thrown.getMessage());
	}
	@Test
	void testNullAppointmentIDThrowsException() {
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH,+5);
			Date testDate = calendar.getTime();
			String nullID = null;
			String testDescription = "Test5 Test6";
            new Appointment(nullID, testDate, testDescription);
        });
		assertEquals("ID cannot be null", thrown.getMessage());
	}
	@Test
	void testPastDateThrowsException() {
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH,-5);
			Date pastDate = calendar.getTime();
		
	
			String testDescription = "Test2 Test3";
			new Appointment(pastDate, testDescription);
		});
		assertEquals("Date cannot be in the past", thrown.getMessage());
	}
	@Test
		void testNullDateThrowsException() {
			IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
				String testDescription = "Test2 Test3";
				Date nullDate = null;
				new Appointment(nullDate, testDescription);
			});
			assertEquals("Date cannot be null", thrown.getMessage());
		}
	@Test
	void testLongDescriptionThrowsException() {
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
			String testDescription = "SuperLongStringSuperLongStringSuperLongStringSuperLongStringSuperLongStringSuperLongStringSuperLongStringSuperLongStringSuperLongStringSuperLongStringSuperLongStringSuperLongStringSuperLongStringSuperLongStringSuperLongStringSuperLongString";
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH,+5);
			Date testDate = calendar.getTime();
			new Appointment(testDate, testDescription);
		});
		assertEquals("Description cannot be longer than 50 characters", thrown.getMessage());
	}
	@Test
	void testNullDescriptionThrowsException() {
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
			String testDescription = null;
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH,+5);
			Date testDate = calendar.getTime();
			new Appointment(testDate, testDescription);
		});
		assertEquals("Description cannot be null", thrown.getMessage());
	}
}
