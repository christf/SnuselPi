package com.penguineering.snuselpi.model;

import java.util.Comparator;

/**
 * Compare events by their start date.
 * 
 * @author Stefan Haun <tux@netz39.de>
 */
public class StartTimeComparator implements Comparator<CalendarEvent> {
	/**
	 * Compare the events by start date.
	 * 
	 * @param o1 first event to compare
	 * @param o2 second event to compare
	 * @return 0 if equal, -1 if o1 is before o2, otherwise 1
	 * @throws NullPointerException if one of the arguments is <code>null</code>.
	 */
	@Override
	public int compare(CalendarEvent o1, CalendarEvent o2) {
		return o1.getStart().compareTo(o2.getStart());
	}
}
