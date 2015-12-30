package com.penguineering.snuselpi.fs;

import java.io.InputStream;

import net.jcip.annotations.NotThreadSafe;

/**
 * An iterator for calendar files.
 * 
 * <p>
 * The file name source depends on the implementation. Iterates over
 * {@link InputStream}S containing calendar data.
 * </p>
 * 
 * <p>
 * The streams may contain arbitrary calendar data.
 * </p>
 * 
 * <p>
 * Thread safety: This interface is not thread safe.
 * </p>
 * 
 * @author Stefan Haun <tux@netz39.de>
 *
 */
@NotThreadSafe
public interface CalendarInputIterator {
	/**
	 * Get the next item in the iterator.
	 * 
	 * @return the next item or <code>null</code> if there are no more items
	 * @throws CalendarIteratorException
	 *             if getting the next item fails. Subsequent calls are still
	 *             valid.
	 */
	public CalendarInput next() throws CalendarIteratorException;
}
