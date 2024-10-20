package Appointment;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;


public class AppointmentService {
	public ArrayList<Appointment> appointmentList = new ArrayList<Appointment>();
	
	public String addAppointment(Date date, String description) {
		String myID = generateUUID();
		Appointment appointment = new Appointment(myID, date, description);
		appointmentList.add(appointment);
		return myID;
	}
	public void addAppointment(String ID, Date date, String description) {
		Appointment appointment = new Appointment(ID, date, description);
		appointmentList.add(appointment);
	}
	public Appointment findAppointment(String ID) {
		for(Appointment appointment : appointmentList) { //loop through contactList
			if(appointment.getID().contentEquals(ID)) {
				return appointment;
			}
		}
		return null; //return null if cannot find contact
	}
	public void deleteAppointment(String ID) {
		appointmentList.remove(findAppointment(ID));
	}
	public String generateUUID() {
		String uuid = UUID.randomUUID().toString();
		uuid = uuid.replaceAll("-", "");
		return uuid.substring(0,10);
	}
}
