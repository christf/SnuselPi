package com.penguineering.snuselpi;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IcsProcessor {
	Map<String, String> lines = new HashMap<String, String>();

	public String get(String key) {
		return lines.get(key);
	}

	public void init(String filename) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;
		boolean schreibeabhier = false;
		while ((line = br.readLine()) != null) {
			if (line.matches("BEGIN:VEVENT")) {
				schreibeabhier = true;
			}

			if (schreibeabhier)
				lines.put(line.substring(0, line.indexOf(":")), line.substring(line.indexOf(":") + 1));
			// System.out.println(line.substring(0, line.indexOf(":")) +
			// line.substring(line.indexOf(":")));
		}
		br.close();
	}
}
