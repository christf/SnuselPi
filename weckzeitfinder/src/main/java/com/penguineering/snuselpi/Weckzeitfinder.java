package com.penguineering.snuselpi;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.VFS;
import org.apache.log4j.Logger;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;

import com.penguineering.snuselpi.model.BeanCalendarEventBuilderFactory;
import com.penguineering.snuselpi.model.CalendarEvent;
import com.penguineering.snuselpi.model.CalendarEventBuilder;
import com.penguineering.snuselpi.model.CalendarEventBuilderFactory;
import com.penguineering.snuselpi.model.StartTimeComparator;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.PropertyList;

public class Weckzeitfinder {
	static Logger log = Logger.getLogger(Weckzeitfinder.class);

	public static void main(String[] args)
			throws InvalidRecurrenceRuleException, IOException, FileNotFoundException, ParserException, ParseException {

		DateTime start, end;

		// Termine zwischen dem aktuellen Zeitpunkt und dem Intervall in Tagen
		// betrachten
		start = new DateTime();
		int intervall = +100;
		java.util.Calendar cal = GregorianCalendar.getInstance();
		cal.add(java.util.Calendar.DAY_OF_YEAR, intervall);
		java.util.Date enddate = cal.getTime();
		end = new DateTime(enddate);

		/*
		 * We are looking for appointments with instances in this period.
		 */
		final Period period = new Period(start, end);

		final FileSystemManager fsManager = VFS.getManager();
		 FileObject icsFile = fsManager
		//		.resolveFile("file:///home/christof/Projekte/SnuselPi/SnuselPi-github/weckzeitfinder/ical_examples");
		 .resolveFile("file:///home/christof/.calendars/christof/");
		// .resolveFile(
		// "file:////home/christof/.calendars/christof/bd2bba8e-ba62-450b-a811-46797762a2a8.1451149756383.ics");

		FileObject[] children = { icsFile };
		if (icsFile.getType() == FileType.FOLDER) {
			children = icsFile.getChildren();
		}

		/*
		 * Initialize the calendar parser
		 */
		System.setProperty("ical4j.unfolding.relaxed", "true");
		final CalendarBuilder builder = new CalendarBuilder();

		final CalendarEventBuilderFactory evtBF = BeanCalendarEventBuilderFactory.getInstance();
		
		// recurrences will be stored in this list
		final List<CalendarEvent> events = new ArrayList<>();

		for (final FileObject fo : children) {
			final String filename = fo.getName().getPath();
			log.debug("parsing " + filename);

			final FileInputStream fin = new FileInputStream(filename);

			try {
				final Calendar calendar = builder.build(fin);

				final String vevent = "VEVENT";
				for (final Component component : calendar.getComponents()) {
					final boolean componentIsVEVENT = vevent.equals(component.getName());
					if (componentIsVEVENT) {
						PropertyList properties = component.getProperties();
						final String uid = properties.getProperty("UID").getValue();

						CalendarEventBuilder evtB = evtBF.newBuilder();
						evtB.setUUID(uid);

						final PeriodList r = component.calculateRecurrenceSet(period);

						final boolean componentRecursWithinPeriod = (r.size() > 0);
						if (componentRecursWithinPeriod) {

							for (final Period p : r) {
								evtB.setStart(p.getStart()).setEnd(p.getEnd());
								evtB.setSummary(properties.getProperty("SUMMARY").getValue());

								try {
									final CalendarEvent evt = evtB.create();
									events.add(evt);
								} catch (final IllegalArgumentException iae) {
									log.error(String.format("Found invalid recurrence event with UID %s!", uid));
									log.error(iae);
								}
							}
						}
					}
				}
			} catch (ParserException e) {
				log.error("the following file could not be parsed: " + filename);
				log.error(e.getMessage());
			}
		}
		
		// sort the events by start date
		Collections.sort(events, new StartTimeComparator());
		
		// print list of events
		System.out.println("List of Events in the given period:");
		for (final CalendarEvent evt : events)
			System.out.println(evt);
	}
}
