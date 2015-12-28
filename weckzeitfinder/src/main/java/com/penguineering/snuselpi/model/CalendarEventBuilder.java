package com.penguineering.snuselpi.model;

import net.fortuna.ical4j.model.DateTime;
import net.jcip.annotations.NotThreadSafe;

/**
 * Builder for calendar event instances.
 * 
 * <p>
 * Threading: no synchronization, use with thread containment.
 * </p>
 * 
 * @author Stefan Haun <tux@netz39.de>
 *
 */
@NotThreadSafe
public interface CalendarEventBuilder {
	/**
	 * Create a {@link CalendarEvent} instance with the configured properties.
	 * 
	 * <p>
	 * For this method to return a result the start time must occur before the
	 * end time, otherwise an {@link IllegalArgumentException} is thrown.
	 * </p>
	 * 
	 * @return A new instance an a {@link CalendarEvent}
	 * @throws IllegalArgumentException
	 *             if the values do not fit an instance
	 */
	public CalendarEvent create() throws IllegalArgumentException;

	/**
	 * Set the calendar entry's UUID.
	 * 
	 * @param UUID
	 *            A valid VEVENT UUID
	 * @return The {@link CalendarEventBuilder} instance for call chaining.
	 */
	public CalendarEventBuilder setUUID(String UUID);

	/**
	 * Set the event's start time.
	 * 
	 * <p>
	 * The start time must occur before the end time.
	 * </p>
	 * 
	 * @param start
	 *            The calendar event's start time.
	 * @return The {@link CalendarEventBuilder} instance for call chaining.
	 */
	public CalendarEventBuilder setStart(DateTime start);

	/**
	 * Set the event's end time.
	 * 
	 * <p>
	 * The end time must occur after the start time.
	 * </p>
	 * 
	 * @param end
	 *            The calendar event's end time.
	 * @return The {@link CalendarEventBuilder} instance for call chaining.
	 */
	public CalendarEventBuilder setEnd(DateTime end);

	/**
	 * Set the event's summary.
	 * 
	 * @param summary
	 *            The summary of the event's source.
	 * @return The {@link CalendarEventBuilder} instance for call chaining.
	 */
	public CalendarEventBuilder setSummary(String summary);	
}
