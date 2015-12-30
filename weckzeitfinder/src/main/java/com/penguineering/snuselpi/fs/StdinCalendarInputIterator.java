package com.penguineering.snuselpi.fs;

import net.jcip.annotations.NotThreadSafe;

/**
 * A calendar input iterator over stdin.
 * 
 * <p>
 * The implementation is designed to deliver stdin exactly one, as if
 * &quot;/dev/stdin&quot; were provided on a *nix system.
 * </p>
 * 
 * @author Stefan Haun <tux@netz39.de>
 *
 */
@NotThreadSafe
public class StdinCalendarInputIterator implements CalendarInputIterator {
	public static StdinCalendarInputIterator newInstance() {
		return new StdinCalendarInputIterator();
	}

	private boolean firstCall;

	/**
	 * instance control: hide the constructor
	 */
	private StdinCalendarInputIterator() {
		this.firstCall = true;
	}

	@Override
	public CalendarInput next() {
		if (!firstCall)
			return null;

		firstCall = false;
		return CalendarInput.forValues("STDIN", System.in);
	}
}
