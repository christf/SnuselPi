package com.penguineering.snuselpi;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.dmfs.rfc5545.DateTime;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;
import org.dmfs.rfc5545.recur.RecurrenceRule;
import org.dmfs.rfc5545.recurrenceset.RecurrenceRuleAdapter;
import org.dmfs.rfc5545.recurrenceset.RecurrenceSet;
import org.dmfs.rfc5545.recurrenceset.RecurrenceSetIterator;

public class App {
	public static void main(String[] args) throws InvalidRecurrenceRuleException,IOException, FileNotFoundException {

		int limit = 10;
		IcsProcessor ip = new IcsProcessor();
		
			ip.init(args[0]);
//			ip.init("/home/christof/.calendars/christof/bd2bba8e-ba62-450b-a811-46797762a2a8.1451149756383.ics");
		
			System.out.println(ip.get("RRULE"));
			
		DateTime start = new DateTime(2015, 0, 1);
		RecurrenceRule rule = new RecurrenceRule("FREQ=YEARLY;BYMONTHDAY=23;BYMONTH=5");

		// "/home/christof/.calendars/christof/bd2bba8e-ba62-450b-a811-46797762a2a8.1451149756383.ics"

		// create a recurence set
		RecurrenceSet rset = new RecurrenceSet();

		// add instances from a recurrence rule
		// you can add any number of recurrence rules or RDATEs
		// (RecurrenceLists).
		rset.addInstances(new RecurrenceRuleAdapter(rule));

		// optionally add exceptions
		// rset.addExceptions(new RecurrenceList(timestamps));

		// get an iterator
		RecurrenceSetIterator iterator = rset.iterator(start.getTimeZone(), start.getTimestamp());

		while (iterator.hasNext() && --limit >= 0) {
			long nextInstance = iterator.next();
			// do something with nextInstance

		}
	}
}
