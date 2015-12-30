package com.penguineering.snuselpi;

import java.io.IOException;

import org.apache.commons.collections4.Predicate;

import com.penguineering.snuselpi.fs.CalendarInput;
import com.penguineering.snuselpi.model.CalendarEventBuilderFactory;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;

/**
 * The task of calculating the recurrences of a VEVENT.
 * 
 * <p>
 * Thread safety: Run in a distinct thread.
 * </p>
 * 
 * @author Stefan Haun <tux@netz39.de>
 *
 */
public class CalendarRecurrenceTask implements Runnable {
	/**
	 * Create a new recurrence calculation task.
	 * 
	 * @param ci
	 *            The calendar input stream.
	 * @param period
	 *            The relevant period.
	 * @param sink
	 *            The result processor.
	 * @param evtBF
	 *            the event builder factory.
	 * @return A calculation task which can be executed directly or handed over
	 *         to an execution manager.
	 */
	public static CalendarRecurrenceTask forValues(CalendarInput ci, Period period, CalendarRecurrenceSink sink,
			CalendarEventBuilderFactory evtBF) {
		if (ci == null)
			throw new NullPointerException("Input stream must not be null!");
		if (period == null)
			throw new NullPointerException("Period must not be null!");
		if (sink == null)
			throw new NullPointerException("Sink must not be null!");
		if (evtBF == null)
			throw new NullPointerException("Event builder factory must not be null!");

		return new CalendarRecurrenceTask(ci, period, sink, evtBF);
	}

	private final CalendarInput ci;
	private final Period period;
	private final CalendarRecurrenceSink sink;
	private final CalendarEventBuilderFactory evtBF;

	private Predicate<Component> inclPred = null;

	private CalendarRecurrenceTask(CalendarInput ci, Period period, CalendarRecurrenceSink sink,
			CalendarEventBuilderFactory evtBF) {
		super();
		this.ci = ci;
		this.period = period;
		this.sink = sink;
		this.evtBF = evtBF;
	}

	public Period getPeriod() {
		return period;
	}

	public CalendarInput getCalendarInput() {
		return ci;
	}

	public CalendarRecurrenceSink getCalendarEventSink() {
		return sink;
	}

	public Predicate<Component> getInclusionPredicate() {
		return inclPred;
	}

	/**
	 * Set an inclusion predicate. Only when this predicate is true, events will
	 * be expanded. Ignored if <code>null</code>.
	 * 
	 * @param inclPred
	 *            An inclusion predicate or <code>null</code> to ignore.
	 */
	public void setInclusionPredicate(Predicate<Component> inclPred) {
		this.inclPred = inclPred;
	}

	@Override
	public void run() {
		try {
			final Calendar calendar = new CalendarBuilder().build(ci.getIs());

			// iterate all VEVENT components
			for (final Component component : calendar.getComponents(Component.VEVENT)) {
				// check if the component matches,
				// but only if there is an inclusion predicate
				if ((inclPred == null) || inclPred.evaluate(component))
					retrieveCalendarEvents(component);
			}
		} catch (ParserException e) {
			sink.failedCalendarInput(ci, e);
		} catch (IOException e) {
			sink.failedCalendarInput(ci, e);
		}
	}

	/**
	 * Retrieve calendar events for a VEVENT in the given period.
	 * 
	 * @param component
	 *            The component to retrieve events from
	 */
	protected void retrieveCalendarEvents(Component component) {

		PropertyList properties = component.getProperties();
		final String uid = properties.getProperty(Property.UID).getValue();

		com.penguineering.snuselpi.model.CalendarEventBuilder evtB = evtBF.newBuilder();
		evtB.setUUID(uid);

		final PeriodList r = component.calculateRecurrenceSet(period);

		for (final Period p : r) {
			evtB.setStart(p.getStart()).setEnd(p.getEnd());
			evtB.setSummary(properties.getProperty(Property.SUMMARY).getValue());

			try {
				final com.penguineering.snuselpi.model.CalendarEvent evt = evtB.create();

				sink.processCalendarEvent(ci, evt);
			} catch (final IllegalArgumentException iae) {
				sink.failedRecurrence(ci, component, p);
			}
		}
	}

}
