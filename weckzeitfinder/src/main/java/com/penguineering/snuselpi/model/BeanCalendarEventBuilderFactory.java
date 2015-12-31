package com.penguineering.snuselpi.model;

import java.text.SimpleDateFormat;

import net.fortuna.ical4j.model.DateTime;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

/**
 * Bean implementation of the {@link CalendarEvent} suite.
 * 
 * @author Stefan Haun <tux@netz39.de>
 *
 */
@ThreadSafe
public class BeanCalendarEventBuilderFactory implements CalendarEventBuilderFactory {
	/**
	 * Get an instance of the builder factory.
	 * 
	 * @return An instance of the factory for bean-based calendar events.
	 */
	public static BeanCalendarEventBuilderFactory getInstance() {
		return new BeanCalendarEventBuilderFactory();
	}

	/**
	 * Block public constructor access
	 */
	private BeanCalendarEventBuilderFactory() {
	}

	@Override
	public CalendarEventBuilder newBuilder() {
		return BeanCalendarEventBuilder.newInstance();
	}

	@Override
	public CalendarEventBuilder copyBuilder(CalendarEvent event) {
		return BeanCalendarEventBuilder.newInstance().setUUID(event.getSourceUUID()).setStart(event.getStart())
				.setEnd(event.getEnd()).setSummary(event.getSummary());
	}

}

class BeanCalendarEventBuilder implements CalendarEventBuilder {
	public static BeanCalendarEventBuilder newInstance() {
		return new BeanCalendarEventBuilder();
	}

	private String sourceUUID;
	private DateTime start;
	private DateTime end;
	private String summary;

	/**
	 * Block public constructor access.
	 */
	private BeanCalendarEventBuilder() {
	}

	@Override
	public CalendarEvent create() throws IllegalArgumentException {
		// IllegalArgumentException may be thrown by createInstance
		return BeanCalendarEvent.newInstance(sourceUUID, start, end, summary);
	}

	@Override
	public CalendarEventBuilder setUUID(String UUID) {
		this.sourceUUID = UUID;
		return this;
	}

	@Override
	public CalendarEventBuilder setStart(DateTime start) {
		this.start = start;
		return this;
	}

	@Override
	public CalendarEventBuilder setEnd(DateTime end) {
		this.end = end;
		return this;
	}

	@Override
	public CalendarEventBuilder setSummary(String summary) {
		this.summary = summary;
		return this;
	}
}

@Immutable
class BeanCalendarEvent implements CalendarEvent {
	public static BeanCalendarEvent newInstance(String sourceUUID, DateTime start, DateTime end, String summary)
			throws IllegalArgumentException {
		// Check the time arguments: start must occur before end
		if (start.after(end))
			throw new IllegalArgumentException("Start time occurs after end time!");

		// Create the instance
		return new BeanCalendarEvent(sourceUUID, start, end, summary);
	}

	private final String sourceUUID;
	private final DateTime start;
	private final DateTime end;
	private final String summary;

	/**
	 * Block public constructor access.
	 */
	private BeanCalendarEvent(String sourceUUID, DateTime start, DateTime end, String summary) {
		this.sourceUUID = sourceUUID;
		this.start = start;
		this.end = end;
		this.summary = summary;
	}

	@Override
	public String getSourceUUID() {
		return sourceUUID;
	}

	@Override
	public DateTime getStart() {
		return start;
	}

	@Override
	public DateTime getEnd() {
		return end;
	}

	@Override
	public String getSummary() {
		return summary;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();

		SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssZ" );
		sb.append(df.format(start));
		sb.append("_");
		sb.append(df.format(end));
		sb.append(" [");
		sb.append(sourceUUID);
		sb.append("] ");
		sb.append(summary);
		
		return sb.toString();
	}
}