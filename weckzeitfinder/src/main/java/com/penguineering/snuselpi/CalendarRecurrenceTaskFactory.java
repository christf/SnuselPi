package com.penguineering.snuselpi;

import org.apache.commons.collections4.Predicate;

import com.penguineering.snuselpi.fs.CalendarInput;
import com.penguineering.snuselpi.model.CalendarEventBuilderFactory;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Period;
import net.jcip.annotations.ThreadSafe;

/**
 * Factory to create recurrence tasks with pre-set properties.
 * 
 * @author Stefan Haun <tux@netz39.de>
 *
 */
@ThreadSafe
public class CalendarRecurrenceTaskFactory {
	public static CalendarRecurrenceTaskFactory forValues(Period period, CalendarRecurrenceSink sink,
			CalendarEventBuilderFactory evtBF) {
		return new CalendarRecurrenceTaskFactory(period, sink, evtBF);
	}

	private final Period period;
	private final CalendarRecurrenceSink sink;
	private final CalendarEventBuilderFactory evtBF;

	private Predicate<Component> inclPred = null;

	public CalendarRecurrenceTaskFactory(Period period, CalendarRecurrenceSink sink,
			CalendarEventBuilderFactory evtBF) {
		super();
		this.period = period;
		this.sink = sink;
		this.evtBF = evtBF;
	}

	public Period getPeriod() {
		return period;
	}

	public CalendarRecurrenceSink getSink() {
		return sink;
	}

	public CalendarEventBuilderFactory getEvtBF() {
		return evtBF;
	}

	public Predicate<Component> getInclusionPredicate() {
		return inclPred;
	}

	public void setInclusionPredicate(Predicate<Component> inclPred) {
		this.inclPred = inclPred;
	}

	public CalendarRecurrenceTask createRecurrenceTask(CalendarInput ci) {
		final CalendarRecurrenceTask task = CalendarRecurrenceTask.forValues(ci, period, sink, evtBF);
		task.setInclusionPredicate(inclPred);
		return task;
	}
}
