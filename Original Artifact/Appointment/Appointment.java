package Appointment;

import java.util.Date;
import java.util.UUID;

public class Appointment {
	
	private final String appointmentID;
	private Date date;
	private String description;
	
	public Appointment(String ID, Date date, String description) {
		if(ID == null) {
			throw new IllegalArgumentException("ID cannot be null");

		}
		if(ID.length() > 10) {
			throw new IllegalArgumentException("ID cannot be longer than 10 characters");
		}
		else {
			this.appointmentID = ID;
		}
		updateDate(date);
		
		updateDescription(description);

	}
	public Appointment(Date date, String description) {
		this.appointmentID = generateUUID();
		updateDate(date);
		updateDescription(description);
	}
	public void updateDate(Date date) {
		if(date == null) {
			throw new IllegalArgumentException("Date cannot be null");

		}
		Date currentDate = new Date();
		if(currentDate.before(date) || currentDate.equals(date)) {
			this.date = date;
		}
		else
			throw new IllegalArgumentException("Date cannot be in the past");
	}
	//method for generating unique ID's when not already specified
		public String generateUUID() {
			String uuid = UUID.randomUUID().toString();
			uuid = uuid.replaceAll("-", "");
			return uuid.substring(0, 10);
		}
		
		public void updateDescription(String description) {
			if(description == null) {
				throw new IllegalArgumentException("Description cannot be null");
			}
			if(description.length() > 50) {
				throw new IllegalArgumentException("Description cannot be longer than 50 characters");
			}
			else
				this.description = description;
		}
		public String getID() {
			return appointmentID;
		}
		public Date getDate() {
			return date;
		}
		public String getDescription() {
			return description;
		}
}
