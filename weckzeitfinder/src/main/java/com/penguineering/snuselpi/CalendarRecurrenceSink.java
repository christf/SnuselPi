package com.penguineering.snuselpi;

import com.penguineering.snuselpi.fs.CalendarInput;
import com.penguineering.snuselpi.model.CalendarEvent;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Period;

/**
 * Sink for results from the recurrence expansion.
 * 
 * <p>
 * Thread safety: When used in a multi-thread environment, the implementation
 * should be thread-safe.
 * </p>
 * 
 * @author Stefan Haun <tux@netz39.de>
 *
 */
public interface CalendarRecurrenceSink {
	/**
	 * Process an expanded calendar event
	 * 
	 * @param ci
	 *            The input calendar stream
	 * @param evt
	 *            The expanded event
	 */
	public void processCalendarEvent(CalendarInput ci, CalendarEvent evt);

	/**
	 * Process a calendar parsing failure.
	 * 
	 * @param ci
	 *            The input calendar stream
	 * @param e
	 *            The exception that occurred during processing
	 */
	public void failedCalendarInput(CalendarInput ci, Exception e);

	/**
	 * Process a failing recurrence instance
	 * 
	 * @param ci
	 *            The input calendar stream
	 * @param component
	 *            The failing VEVENT component
	 * @param period
	 *            The failing recurrence period
	 */
	public void failedRecurrence(CalendarInput ci, Component component, Period period);
}
