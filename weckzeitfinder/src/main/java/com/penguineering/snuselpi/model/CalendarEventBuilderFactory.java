package com.penguineering.snuselpi.model;

/**
 * Factory for {@link CalendarEventBuilder}S
 * 
 * @author Stefan Haun <tux@netz39.de>
 */
public interface CalendarEventBuilderFactory {
	/**
	 * Create a new builder instance with no pre-set properties.
	 * 
	 * @return A new builder instance.
	 */
	public CalendarEventBuilder newBuilder();

	/**
	 * Create a new builder instance and pre-set properties based on the
	 * provided event.
	 * 
	 * @return A new builder instance with pre-set properties.
	 */
	public CalendarEventBuilder copyBuilder(CalendarEvent event);
}
