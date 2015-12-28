package com.penguineering.snuselpi;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.GregorianCalendar;
import java.util.Iterator;

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

		String filename = new String();
		// Locate the Jar file
		FileSystemManager fsManager = VFS.getManager();
		// FileObject icsFile =
		// fsManager.resolveFile("file:///home/christof/Projekte/SnuselPi/SnuselPi-github/weckzeitfinder/ical_examples");
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

				boolean componentIsVEVENT = vevent.equals(component.getName());
				if (componentIsVEVENT) {
					Period period = new Period(start, end);
					PeriodList r = component.calculateRecurrenceSet(period);
					boolean componentRecursWithinPeriod = (r.size() > 0);
					if (componentRecursWithinPeriod) {
						System.out.println("der folgende Termin liegt mit folgenden Ereignissen im Intervall:");
						for (Iterator<Period> i = r.iterator(); i.hasNext();) {
							PropertyList properties = component.getProperties();
							System.out.println((Period) i.next() + " " + properties.getProperty("UID").getValue() + " "
									+ properties.getProperty("SUMMARY").getValue());
						}
					}
				}
			}
		}
	}
}
