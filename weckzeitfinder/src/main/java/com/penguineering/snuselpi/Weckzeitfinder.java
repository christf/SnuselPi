package com.penguineering.snuselpi;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;
import org.dmfs.rfc5545.DateTime;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;
import org.dmfs.rfc5545.recur.RecurrenceRule;
import org.dmfs.rfc5545.recurrenceset.RecurrenceRuleAdapter;
import org.dmfs.rfc5545.recurrenceset.RecurrenceSet;
import org.dmfs.rfc5545.recurrenceset.RecurrenceSetIterator;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;

public class Weckzeitfinder {
	public static void main(String[] args)
			throws InvalidRecurrenceRuleException, IOException, FileNotFoundException, ParserException {

		int limit = 10;
		// IcsProcessor ip = new IcsProcessor();
		
		System.setProperty("ical4j.unfolding.relaxed", "true");

		String filename = new String(
				"/home/christof/.calendars/christof/bd2bba8e-ba62-450b-a811-46797762a2a8.1451149756383.ics");
				// filename = args[0];

		// Locate the Jar file
		FileSystemManager fsManager = VFS.getManager();
		FileObject icsFile = fsManager.resolveFile("file:///home/christof/.calendars/christof/");

		// List the children of the Jar file
		FileObject[] children = icsFile.getChildren();
		System.out.println("Children of " + icsFile.getName().getURI());
		for (int f = 0; f < children.length; f++) {

			System.out.println(children[f].getName().getPath() );
			filename = children[f].getName().getPath();

			FileInputStream fin = new FileInputStream(filename);
			CalendarBuilder builder = new CalendarBuilder();
			Calendar calendar = builder.build(fin);

			for (final Component component : calendar.getComponents()) {
				System.out.println("Component [" + component.getName() + "]");

				for (final Property property : component.getProperties()) {
					System.out.println("Property [" + property.getName() + ", " + property.getValue() + "]");
				}

				System.out.println(component.getProperties("RRULE"));
				System.out.println(component.getProperties("DTSTART"));
				System.out.println(component.getProperties("EXRULE"));
				System.out.println(component.getProperties("EXDATE"));
				System.out.println(component.getProperties("RDATE"));
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
