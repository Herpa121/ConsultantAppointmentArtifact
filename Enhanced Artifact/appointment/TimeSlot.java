package appointment;

import java.time.LocalTime;

public class TimeSlot {
	private LocalTime start;
	private LocalTime end;
	// Constructor
	public TimeSlot(LocalTime start, LocalTime end) {
		this.start = start;
		this.end = end;
	}
	// Easy to read time frame
	@Override
	public String toString() {
		return "Available from: " + start + " to " + end;
	}
}
