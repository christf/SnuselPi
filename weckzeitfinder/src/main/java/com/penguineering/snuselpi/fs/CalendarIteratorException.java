package com.penguineering.snuselpi.fs;

/**
 * This exception is thrown when iteration through calendar inputs fails.
 * 
 * <p>
 * When thrown during a call to <code>next</code>, only one item is
 * &quot;lost&quot; and next should be called again, until there are definitely
 * no more item.
 * </p>
 * 
 * @author Stefan Haun <tux@netz39.de>
 *
 */
public class CalendarIteratorException extends Exception {
	private static final long serialVersionUID = -3873178315424781835L;

	public CalendarIteratorException() {
	}

	public CalendarIteratorException(String message) {
		super(message);
	}

	public CalendarIteratorException(Throwable cause) {
		super(cause);
	}

	public CalendarIteratorException(String message, Throwable cause) {
		super(message, cause);
	}

	public CalendarIteratorException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
