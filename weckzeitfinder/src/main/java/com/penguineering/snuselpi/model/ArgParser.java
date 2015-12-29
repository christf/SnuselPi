package com.penguineering.snuselpi.model;

import java.util.HashMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import com.penguineering.snuselpi.Weckzeitfinder;

final public class ArgParser {
	static Logger log = Logger.getLogger(Weckzeitfinder.class);
	private int interval;
	private String inputfile = new String();
	private String searchstring = new String();

	public void forArgs(String[] args) throws ParseException  {

		Options options = new Options();
		options.addOption("l", true, "set debug level. Valid input: ALL,TRACE,DEBUG,INFO,WARN,ERROR,FATAL,OFF");
		options.addOption("s", true, "set summary pattern to be matched against ics");
		options.addOption("i", true, "set length of interval (in days) to be considered");
		options.addOption("f", true, "set input file/directory");
		options.addOption("h", false, "help");

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;

		cmd = parser.parse(options, args);

		if (cmd.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("weckzeitfinder",
					"Weckzeitfinder will display events that occur during the next <interval> days", options, "", true);
			System.exit(0);
		}

		if (cmd.hasOption("f")) {
			setInputfile(cmd.getOptionValue("f"));
		} else {
			// TODO - this will only work on *nix, find a platform-independent
			// way
			setInputfile("/dev/stdin");
			log.info("reading ics-data from stdin");
		}
		log.debug("parsing searchstring");
		if (cmd.hasOption("s")) {
			log.debug("setting searchstring");
			setSearchstring(cmd.getOptionValue("s"));
		}

		if (cmd.hasOption("i")) {
			setInterval(Integer.parseInt(cmd.getOptionValue("i")));
		}

		if (cmd.hasOption("l")) {
			HashMap<String, Level> logLevels = new HashMap<String, Level>();
			logLevels.put("ALL", Level.ALL);
			logLevels.put("TRACE", Level.TRACE);
			logLevels.put("DEBUG", Level.DEBUG);
			logLevels.put("INFO", Level.INFO);
			logLevels.put("WARN", Level.WARN);
			logLevels.put("ERROR", Level.ERROR);
			logLevels.put("FATAL", Level.FATAL);
			logLevels.put("OFF", Level.OFF);

			if (logLevels.containsKey(cmd.getOptionValue("l")))
				LogManager.getRootLogger().setLevel(logLevels.get(cmd.getOptionValue("l")));
			else
				throw new ParseException("unknown log4j log-level on command-Line");

		} else {
			LogManager.getRootLogger().setLevel(Level.INFO);
		}
	}

	public int getInterval() {
		return interval;
	}

	public String getInputfile() {
		return inputfile;
	}

	public String getSearchstring() {
		return searchstring;
	}

	private void setInterval(int interval) {
		this.interval = interval;
	}

	private void setInputfile(String inputfile) {
		this.inputfile = inputfile;
	}

	private void setSearchstring(String searchstring) {
		this.searchstring = new String(searchstring);
	}
}
