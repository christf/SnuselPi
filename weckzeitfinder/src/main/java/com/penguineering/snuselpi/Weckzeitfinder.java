package com.penguineering.snuselpi;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.Predicate;
import org.apache.log4j.Logger;

import com.penguineering.snuselpi.fs.CalendarInput;
import com.penguineering.snuselpi.fs.CalendarInputIterator;
import com.penguineering.snuselpi.fs.CalendarIteratorException;
import com.penguineering.snuselpi.fs.StdinCalendarInputIterator;
import com.penguineering.snuselpi.fs.VfsCalendarInputIterator;
import com.penguineering.snuselpi.model.BeanCalendarEventBuilderFactory;
import com.penguineering.snuselpi.model.CalendarEvent;
import com.penguineering.snuselpi.model.CalendarEventBuilderFactory;
import com.penguineering.snuselpi.model.StartTimeComparator;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;

public class Weckzeitfinder {
	static Logger log = Logger.getLogger(Weckzeitfinder.class);

	/**
	 * Retrieve calendar events for a VEVENT in the given period.
	 * 
	 * @param component
	 *            The component to retrieve events from
	 * @param period
	 *            The relevant time period
	 * @param evtBF
	 *            Builder Factory for {@link CalendarEvent}S
	 * @param events
	 *            new {@link CalendarEvent}S are added to this list
	 * @param failed
	 *            if not <code>null</code>, failed periods will be stored here
	 * @return true if there have been failures to build a {@link CalendarEvent}
	 */
	public static boolean retrieveCalendarEvents(Component component, Period period, CalendarEventBuilderFactory evtBF,
			List<CalendarEvent> events, List<Period> failed) {
		boolean hasFailed = false;

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
				events.add(evt);
			} catch (final IllegalArgumentException iae) {
				hasFailed = true;

				if (failed != null)
					failed.add(p);
			}
		}

		return hasFailed;
	}

	/**
	 * Get the next calendar input item, log exceptions on the way.
	 * 
	 * @param iter
	 *            The calendar input iterator.
	 * @return An item or <code>null</code> if there are no more items.
	 */
	public static CalendarInput nextCalendarInput(CalendarInputIterator iter) {
		CalendarInput ci = null;

		boolean again = true;
		while (again) {
			try {
				ci = iter.next();
				again = false;
			} catch (CalendarIteratorException cie) {
				log.error(cie);
			}
		}

		return ci;
	}

	public static void main(String[] args) throws IOException, FileNotFoundException {

		OptionsRecord options;
		try {
			options = OptionsRecord.forArgs(args);
		} catch (org.apache.commons.cli.ParseException e) {
			log.error("could not parse arguments, exiting", e);
			System.exit(1);

			/*
			 * This is dead code, but needed by the compiler to recognize that
			 * this try-catch block will not leave an uninitialized options
			 * record. The semantics of System.exit stopping the code are not
			 * built in.
			 */
			return;
		}

		// extract the relevant values
		final String searchstring = options.getSearchstring();
		final String inputfile = options.getInputfile();
		final Period period = options.getPeriod();

		// set the predicate only if there is a search string
		final Predicate<Component> inclPred;
		if (searchstring == null)
			inclPred = null;
		else
			inclPred = SummaryInclusionPredicate.getInstance(searchstring);

		/*
		 * Initialize the calendar parser
		 */
		// System.setProperty("ical4j.unfolding.relaxed", "true");

		final CalendarEventBuilderFactory evtBF = BeanCalendarEventBuilderFactory.getInstance();

		// recurrences will be stored in this list
		final List<CalendarEvent> events = new ArrayList<>();

		/*
		 * Get the calendar input iterator.
		 * 
		 * If this fails we have to abandon the mission.
		 */
		final CalendarInputIterator calIter;
		if ("/dev/stdin".equals(inputfile))
			calIter = StdinCalendarInputIterator.newInstance();
		else
			try {
				calIter = VfsCalendarInputIterator.forPath(inputfile);
			} catch (CalendarIteratorException e1) {
				log.error(String.format("Faild to initialize provided path %s: %s", inputfile, e1.getMessage()), e1);
				System.exit(-1);
				return;
			}

		/*
		 * Evaluate items from all input streams.
		 */
		CalendarInput ci;
		while ((ci = nextCalendarInput(calIter)) != null) {
			try {
				final Calendar calendar = new CalendarBuilder().build(ci.getIs());

				// iterate all VEVENT components
				for (final Component component : calendar.getComponents(Component.VEVENT)) {
					// check if the component matches,
					// but only if there is an inclusion predicate
					if ((inclPred == null) || inclPred.evaluate(component)) {
						final boolean hasFailed = retrieveCalendarEvents(component, period, evtBF, events, null);

						if (hasFailed)
							log.error(String.format("Found invalid recurrences in VEVENT with UID %s!",
									component.getProperties().getProperty(Property.UID).getValue()));
					}
				}
			} catch (ParserException e) {
				log.error(String.format("File %s could not be parsed: %s", ci.getName(), e.getMessage()), e);
			}
		}

		// sort the events by start date
		Collections.sort(events, new StartTimeComparator());

		for (CalendarEvent evt : events)
			System.out.println(evt);
	}
}
