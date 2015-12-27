package com.penguineering.snuselpi;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.Property;

public class Weckzeitfinder {

	private static boolean isinbetween(Component component, DateTime start, DateTime end) {

		// DateTime start = new DateTime(event.getStartDate().getDate());
		// DateTime end = new DateTime(calEnd.getTime());
		Period period = new Period(start, end);
		PeriodList r = component.calculateRecurrenceSet(period);
		System.out.println(r);
		return false;

	}

	public static void main(String[] args)
			throws InvalidRecurrenceRuleException, IOException, FileNotFoundException, ParserException {
		// IcsProcessor ip = new IcsProcessor();
		DateTime lowerdatetime, upperdatetime;

		// Termine zwischen 27.12.2015 und 27.12. +20 Tagen
		lowerdatetime = new DateTime(1451254583 * 1000);
		upperdatetime = new DateTime(1452982583 * 1000);

		String filename = new String();

		// Locate the Jar file
		FileSystemManager fsManager = VFS.getManager();
		// FileObject icsFile =
		// fsManager.resolveFile("file:///home/christof/.calendars/christof/");
		FileObject icsFile = fsManager.resolveFile(
				"file:////home/christof/.calendars/christof/bd2bba8e-ba62-450b-a811-46797762a2a8.1451149756383.ics");

		FileObject[] children = { icsFile };
		if (icsFile.getType().toString().equals("folder")) {
			children = icsFile.getChildren();
		}
		for (int f = 0; f < children.length; f++) {
			System.out.println(children[f].getName().getPath());
			filename = children[f].getName().getPath();

			FileInputStream fin = new FileInputStream(filename);
			CalendarBuilder builder = new CalendarBuilder();

			System.setProperty("ical4j.unfolding.relaxed", "true");

			Calendar calendar = builder.build(fin);

			String vevent = new String("VEVENT");
			for (final Component component : calendar.getComponents()) {
				// only work on VEVENTS, ignore VTODO
				if (vevent.equals(component.getName())) {
					System.out.println("Component [" + component.getName() + "]");
					if (isinbetween(component, lowerdatetime, upperdatetime)) {
						System.out.println("der folgende Termin liegt im Intervall:");
						for (Property property : component.getProperties()) {
							System.out.println("Property [" + property.getName() + ", " + property.getValue() + "]");
						}
					}

					System.out.println("RRULE: " + component.getProperties("RRULE") + " DTSTART: "
							+ component.getProperties("DTSTART") + " EXRULE: " + component.getProperties("EXRULE")
							+ " EXDATE: " + component.getProperties("EXDATE") + " RDATE: "
							+ component.getProperties("RDATE"));
				}
			}
		}
		// System.out.println(ip.get("RRULE"));

		// TODO: EXRULE
		// TODO: RDATE
		// TODO: EXDATE

		/*
		 * System.out.println(ip.get("DTSTART")); DateTime start = new
		 * DateTime(2015, 0, 1); RecurrenceRule rule = new
		 * RecurrenceRule("FREQ=YEARLY;BYMONTHDAY=23;BYMONTH=5");
		 * 
		 * //
		 * "/home/christof/.calendars/christof/bd2bba8e-ba62-450b-a811-46797762a2a8.1451149756383.ics"
		 * 
		 * // create a recurence set RecurrenceSet rset = new RecurrenceSet();
		 * 
		 * // add instances from a recurrence rule // you can add any number of
		 * recurrence rules or RDATEs // (RecurrenceLists).
		 * rset.addInstances(new RecurrenceRuleAdapter(rule));
		 * 
		 * // optionally add exceptions // rset.addExceptions(new
		 * RecurrenceList(timestamps));
		 * 
		 * // get an iterator RecurrenceSetIterator iterator =
		 * rset.iterator(start.getTimeZone(), start.getTimestamp());
		 * 
		 * while (iterator.hasNext() && --limit >= 0) { long nextInstance =
		 * iterator.next(); // do something with nextInstance
		 * 
		 * }
		 */
	}
}
