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

import net.jcip.annotations.Immutable;

@Immutable
final public class OptionsRecord {
	static Logger log = Logger.getLogger(OptionsRecord.class);

	public static OptionsRecord forArgs(String[] args) throws ParseException {

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

		int interval = 10;
		// TODO find a platform-independent way
		String inputfile = "/dev/stdin";
		String searchstring = "Wecker";

		if (cmd.hasOption("f")) {
			inputfile = cmd.getOptionValue("f");
		} else {
			log.info("reading ics-data from stdin");
		}

		if (cmd.hasOption("i")) {
			try {
				interval = Integer.parseInt(cmd.getOptionValue("i"));
			} catch (NumberFormatException nfe) {
				throw new ParseException("Illegal number value for interval option!");
			}
		}

		log.debug("parsing searchstring");
		if (cmd.hasOption("s")) {
			log.debug("setting searchstring");
			searchstring = cmd.getOptionValue("s");
		}
		
		return new OptionsRecord(interval, inputfile, searchstring);
	}

	private int interval;
	private String inputfile;
	private String searchstring;

	public OptionsRecord(int interval, String inputfile, String searchstring) {
		super();
		this.interval = interval;
		this.inputfile = inputfile;
		this.searchstring = searchstring;
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
}
