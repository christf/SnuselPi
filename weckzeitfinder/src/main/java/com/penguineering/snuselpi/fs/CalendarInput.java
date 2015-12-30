package com.penguineering.snuselpi.fs;

import java.io.InputStream;

/**
 * Calendar Input item consisting of an input stream and the source's name, e.g.
 * a file path.
 * 
 * @author Stefan Haun <tux@netz39.de>
 *
 */
public class CalendarInput {
	public static CalendarInput forValues(String name, InputStream is) {
		return new CalendarInput(name, is);
	}

	private final String name;
	private final InputStream is;

	private CalendarInput(String name, InputStream is) {
		super();
		this.name = name;
		this.is = is;
	}

	public String getName() {
		return name;
	}

	public InputStream getIs() {
		return is;
	}
}
