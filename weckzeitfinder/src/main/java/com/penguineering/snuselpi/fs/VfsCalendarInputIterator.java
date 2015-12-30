package com.penguineering.snuselpi.fs;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.VFS;

import net.jcip.annotations.NotThreadSafe;

/**
 * VFS implementation of the {@link CalendarInputIterator}.
 * 
 * <p>
 * Note: Iteration over the file system has two possible inconsistencies:
 * <ul>
 * <li>A file, that has been added to the queue, does not exist anymore. In this
 * case a {@link FileNotFoundException} is thrown.</li>
 * <li>A file has been added after a directory was scanned. This file will not
 * be seen by the iterator.</li>
 * </ul>
 * 
 * @author Stefan Haun <tux@netz39.de>
 *
 */
@NotThreadSafe
public class VfsCalendarInputIterator implements CalendarInputIterator {
	public static VfsCalendarInputIterator forPath(String path) throws CalendarIteratorException {
		final VfsCalendarInputIterator calIter = new VfsCalendarInputIterator();

		FileObject fo;
		try {
			fo = VFS.getManager().resolveFile(path);
		} catch (FileSystemException e) {
			throw new CalendarIteratorException(String.format("Cannot resolve the file path: %s", e.getMessage()), e);
		}
		calIter.inbox.add(fo);

		return calIter;
	}

	/**
	 * New items go here
	 */
	private final Queue<FileObject> inbox;

	/**
	 * Directories we want to expand later
	 */
	private final Queue<FileObject> directories;

	private VfsCalendarInputIterator() {
		this.directories = new LinkedList<FileObject>();
		this.inbox = new LinkedList<FileObject>();
	}

	protected void expandNextDirectory() throws CalendarIteratorException {
		if (directories.isEmpty())
			return;

		final FileObject dir = directories.poll();

		if (dir != null)
			try {
				final FileObject[] children = dir.getChildren();
				for (final FileObject child : children)
					inbox.add(child);
			} catch (FileSystemException e) {
				throw new CalendarIteratorException(
						String.format("Cannot expand directory %s: %s", dir.getName().toString(), e.getMessage()), e);
			}
	}

	@Override
	public CalendarInput next() throws CalendarIteratorException {
		/*
		 * Short version of the following code: Look through the inbox until
		 * there is a file. If the inbox is empty, scan the next directory, then
		 * search the inbox again. If inbox and directories are empty, we are
		 * out of files.
		 * 
		 * Any exception happening in between will be thrown w/o losing the
		 * following files.
		 */

		FileObject fo = null;
		while (fo == null) {
			// if the inbox is empty and there are more directories, expand a
			// directory until we've got an inbox item
			while (inbox.isEmpty() && !directories.isEmpty())
				expandNextDirectory();

			// walk through the inbox to get a file
			while (!inbox.isEmpty() && (fo == null)) {
				fo = inbox.poll();

				// put directories in the directory queue
				try {
					if (fo.getType() == FileType.FOLDER) {
						directories.add(fo);
						fo = null;
					}
				} catch (FileSystemException fse) {
					throw new CalendarIteratorException(
							String.format("Cannot expand FS entry %s: %S", fo.getName().getPath(), fse.getMessage()),
							fse);
				}
			}

			// cancel if we have exhausted all options
			if (inbox.isEmpty() && directories.isEmpty())
				break;
		}

		/*
		 * Finally create a calendar input for the file item. This can fail if
		 * the file does not exist anymore.
		 */
		CalendarInput ci = null;

		if (fo != null) {
			final String filename = fo.getName().getPath();
			InputStream is;
			try {
				is = new FileInputStream(filename);
			} catch (FileNotFoundException fnfe) {
				throw new CalendarIteratorException(
						String.format("Cannot find path %s to open a stream: %s", filename, fnfe.getMessage()), fnfe);
			}

			ci = CalendarInput.forValues(filename, is);
		}

		return ci;
	}
}
