package com.penguineering.snuselpi;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.GregorianCalendar;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.VFS;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.PropertyList;

public class Weckzeitfinder {

	public static void main(String[] args)
			throws InvalidRecurrenceRuleException, IOException, FileNotFoundException, ParserException, ParseException {
		// IcsProcessor ip = new IcsProcessor();
		DateTime start, end;

		// Termine zwischen dem aktuellen Zeitpunkt und dem Intervall in Tagen
		// betrachten (also heute und morgen)
		start = new DateTime();
		int intervall = +10;
		java.util.Calendar cal = GregorianCalendar.getInstance();
		cal.add(java.util.Calendar.DAY_OF_YEAR, intervall);
		java.util.Date enddate = cal.getTime();
		end = new DateTime(enddate);

		/*
		 * We are looking for appointments with instances in this period.
		 */
		final Period period = new Period(start, end);

		final FileSystemManager fsManager = VFS.getManager();
		// FileObject icsFile =
		// fsManager.resolveFile("file:///home/christof/Projekte/SnuselPi/SnuselPi-github/weckzeitfinder/ical_examples");
		// fsManager.resolveFile("file:///home/christof/.calendars/christof/");
		FileObject icsFile = fsManager.resolveFile(
				"file:////home/christof/.calendars/christof/bd2bba8e-ba62-450b-a811-46797762a2a8.1451149756383.ics");

		FileObject[] children = { icsFile };
		if (icsFile.getType() == FileType.FOLDER) {
			children = icsFile.getChildren();
		}

		/*
		 * Initialize the calendar parser
		 */
		System.setProperty("ical4j.unfolding.relaxed", "true");
		final CalendarBuilder builder = new CalendarBuilder();

		for (final FileObject fo : children) {
			final String filename = fo.getName().getPath();
			System.out.println(filename);

			final FileInputStream fin = new FileInputStream(filename);

			final Calendar calendar = builder.build(fin);

			final String vevent = "VEVENT";
			for (final Component component : calendar.getComponents()) {
				// only work on VEVENTS, ignore VTODO
				if (vevent.equals(component.getName())) {
					System.out.println("Component [" + component.getName() + "]");
					
					final PeriodList r = component.calculateRecurrenceSet(period);

					if (r.size() > 0) {
						System.out.println("der folgende Termin liegt mit folgenden Ereignissen im Intervall:");
						for (final Period p : r) {
							PropertyList properties = component.getProperties();
							System.out.println(p + " " + properties.getProperty("UID").getValue() + " "
									+ properties.getProperty("SUMMARY").getValue());
						}
					}
				}
			}
		}
	}
}
