package appointment;

import java.time.LocalTime;
import java.time.LocalDate;

//Isolated object class, validation is now external
public class Appointment {
	private String id;
	private LocalDate date;
	private LocalTime time;
	private String location;
	private String clientName;
	private String consultantName;
	private String description;
	private int duration; // In minutes
	private ConsultationType consultationType;
	private AppointmentStatus status;
	
	public enum ConsultationType {
		IN_PERSON, PHONE, VIDEO;
	}
	public enum AppointmentStatus {
		SCHEDULED, COMPLETED, CANCELED;
	}
	

	// Constructor
	public Appointment(String id, LocalDate date, LocalTime time, String location, String clientName,
			String consultantName, String description, int duration, ConsultationType consultationType, AppointmentStatus status) {
		this.id = id;
		this.date = date;
		this.time = time;
		this.location = location;
		this.clientName = clientName;
		this.consultantName = consultantName;
		this.description = description;
		this.duration = duration;
		this.consultationType = consultationType;
		this.status = status;
	}

	// Getters and Setters for each field
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getConsultantName() {
		return consultantName;
	}

	public void setConsultantName(String consultantName) {
		this.consultantName = consultantName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public ConsultationType getConsultationType() {
		return consultationType;
	}

	public void setConsultationType(ConsultationType consultationType) {
		this.consultationType = consultationType;
	}

	public AppointmentStatus getStatus() {
		return status;
	}

	public void setStatus(AppointmentStatus status) {
		this.status = status;
	}
}
