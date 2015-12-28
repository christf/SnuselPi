package com.penguineering.snuselpi.model;

import net.fortuna.ical4j.model.DateTime;
import net.jcip.annotations.Immutable;

/**
 * Representation of an actual calendar event.
 * 
 * <p>
 * This item represents an actual instance of an occurring event, which may be
 * equal to the original item or be the result of recurrence expansion.
 * </p>
 * 
 * <p>
 * Threading: Represents an immutable object. If write operations are permitted,
 * proper synchronization of the whole implementation must be ensured and is
 * beneath this interface.
 * </p>
 * 
 * @author Stefan Haun <tux@netz39.de>
 *
 */
@Immutable
public interface CalendarEvent {
	/**
	 * Get the UUID of the item's source object, that is the VEVENT that lead to
	 * this recurrence.
	 * 
	 * @return A valid VEVENT UUID
	 */
	public String getSourceUUID();

	/**
	 * Get the start time of this recurrence.
	 * 
	 * @return A calendar VEVENT start time.
	 */
	public DateTime getStart();

	/**
	 * Get the end time of this recurrence.
	 * 
	 * <p>
	 * The end time will always occur after the start time.
	 * </p>
	 * 
	 * @return A calendar VEVENT end time.
	 */
	public DateTime getEnd();

	/**
	 * Get the summary of the VEVENT that lead to this occurrence.
	 * 
	 * @return A VEVENT summary.
	 */
	public String getSummary();
}
