package main.util;

public class Date {

	private static final int WORKING_DAY_HOURS = 8;

	private int days;
	private int hours;
	private int minutes;

	public Date() {
		days = 0;
		hours = 0;
		minutes = 0;
	}


	public Date(int days, int hours, int minutes) {
		this();
		addDays(days);
		addHours(hours);
		addMinutes(minutes);
	}


	public void addDays(int days) {
		this.days += days;
	}


	public void addHours(int hours) {
		this.hours += hours;
		while (this.hours >= WORKING_DAY_HOURS) {
			this.hours -= WORKING_DAY_HOURS;
			addDays(1);
		}
	}


	public void addMinutes(int minutes) {
		this.minutes += minutes;
		while (this.minutes >= 60) {
			this.minutes -= 60;
			addHours(1);
		}
	}


	public void addDate(Date date) {
		addDays(date.days);
		addHours(date.hours);
		addMinutes(date.minutes);
	}


	public int getDays() {
		return days;
	}


	public int getHours() {
		return hours;
	}


	public int getMinutes() {
		return minutes;
	}
}
