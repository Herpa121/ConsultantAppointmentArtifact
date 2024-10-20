package Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import Appointment.AppointmentService;
import Appointment.Appointment;

class AppointmentServiceTest {

	@Test
	void testAddAppointmentCustomID() {
		AppointmentService ApptServ = new AppointmentService();
		String id = "1";
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH,+5);
		Date testDate = calendar.getTime();
		
		assertNull(ApptServ.findAppointment(id)); //initially empty
		ApptServ.addAppointment(id, testDate, "test description");
		assertNotNull(ApptServ.findAppointment(id));
		Appointment Appt = ApptServ.findAppointment(id);
		assertNotNull(Appt);
		assertEquals(1, ApptServ.appointmentList.size());
	}
	@Test
	void testAddAppointmentWithoutID() {
		AppointmentService ApptServ = new AppointmentService();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH,+5);
		Date testDate = calendar.getTime();
		
		assertEquals(0, ApptServ.appointmentList.size()); //initially empty
		String newID = ApptServ.addAppointment(testDate, "test description");
		assertNotNull(ApptServ.findAppointment(newID));
		Appointment Appt = ApptServ.findAppointment(newID);
		assertNotNull(Appt);
		assertEquals(1, ApptServ.appointmentList.size());
	}
	
	@Test
	void testDeleteAppointment() {
		AppointmentService ApptServ = new AppointmentService();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH,+5);
		Date testDate = calendar.getTime();
		
		assertEquals(0, ApptServ.appointmentList.size()); // no items
		String newID = ApptServ.addAppointment(testDate, "test description");
		assertEquals(1, ApptServ.appointmentList.size()); // one item
		ApptServ.deleteAppointment(newID);
		assertEquals(0, ApptServ.appointmentList.size()); // item deleted
	}

}
