package dhbw.timetable.rablabla.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * An immutable struct concept based class for storing the appointment data of the lectures.
 * 
 * Created by Hendrik Ulbrich (C) 2017
 */
public final class Appointment {
	private LocalDateTime startDate, endDate;
	private String course, info;

	Appointment(LocalDateTime startDate, LocalDateTime endDate, String course, String info) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.course = course;
		this.info = info;
	}

	public String getStartTime() {
		return startDate.format(DateTimeFormatter.ofPattern("HH:mm", Locale.GERMANY));
	}

	public String getEndTime() {
		return endDate.format(DateTimeFormatter.ofPattern("HH:mm", Locale.GERMANY));
	}

	public String getDate() {
		return endDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMANY));
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public String getCourse() {
		return course;
	}

	public String getInfo() {
		return info;
	}

	@Override
	public boolean equals(Object o) {
		if (o != null) {
			if (o instanceof Appointment) {
				Appointment that = (Appointment) o;
				return that.toString().equals(this.toString());
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return new StringBuilder().append(getDate())
				.append("\t").append(getStartTime()).append("-").append(getEndTime())
				.append("\t").append(course)
				.append("\t").append(info).toString();
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
}