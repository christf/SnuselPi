package org.snuselpi;
import org.dmfs.rfc5545.recur.*;
/**
 * Hello world!
 *
 */
public class App 
{
	public static void main( String[] args )
	{
		//	    "/home/christof/.calendars/christof/bd2bba8e-ba62-450b-a811-46797762a2a8.1451149756383.ics"
		System.out.println( "Hello World!" );

    // create a recurence set
    RecurrenceSet rset = new RecurrenceSet();

    // add instances from a recurrence rule
    // you can add any number of recurrence rules or RDATEs (RecurrenceLists).
    rset.addInstances(new RecurrenceRuleAdapter(rule));

    // optionally add exceptions
    // rset.addExceptions(new RecurrenceList(timestamps));

    // get an iterator
    RecurrenceSetIterator iterator = rset.iterator(start.getTimeZone(), start.getTimestamp());

    while (iterator.hasNext() && --limit >= 0)
    {
        long nextInstance = iterator.next();
        // do something with nextInstance
    }
	}
}
