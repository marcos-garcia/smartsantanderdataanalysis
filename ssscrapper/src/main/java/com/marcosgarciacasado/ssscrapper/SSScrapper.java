package com.marcosgarciacasado.ssscrapper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Web content scrapper.
 * 
 * @author Marcos García Casado
 *
 */
public class SSScrapper {

	public static final int MIN_INTERVAL_LIMIT = 10;

	public static Options options;
	public static int timeInterval;
	public static ArrayList<URL> urlsGetData;
	public static String kafkaLocation;

	private static void buildOptions() {
		options = new Options();

		// URLs
		Option urlsOption = new Option("u", true,
				"Comma separated URL's of the pages to be downloaded");

		// Interval
		Option intervalOption = new Option("i", true,
				"Interval of seconds between downloads (10 seconds min)");

		// Kafka location
		Option kafkaLocationOption = new Option("k", true,
				"The Apache Kafka server:port where the content will be uploaded");

		options.addOption(urlsOption);
		options.addOption(intervalOption);
		options.addOption(kafkaLocationOption);
	}

	private static boolean parseArguments(String[] args) {

		boolean parseOk = true;

		// create the parser
		CommandLineParser parser = new BasicParser();
		CommandLine line = null;
		try {
			// parse the command line arguments
			line = parser.parse(options, args);
		} catch (ParseException exp) {
			// oops, something went wrong
			parseOk = false;
			System.err.println("Parsing failed.  Reason: " + exp.getMessage());
		}

		// interval argument
			try {
				String textInterval = line.getOptionValue("i", "15");
				timeInterval = Math.max(
						Integer.parseInt(textInterval),MIN_INTERVAL_LIMIT);
			} catch (NumberFormatException exp) {
				// oops, something went wrong
				parseOk = false;
				HelpFormatter f = new HelpFormatter();
				f.printHelp("Options", options);
				System.err.println("Parsing number of parameter -i failed.  Reason: "
						+ exp.getMessage());
			}

		// urls argument
		urlsGetData = new ArrayList<URL>();
		if (line.hasOption("u")) {
			String urls = line.getOptionValue("u");
			try {
				String[] urlList = urls.split(",");
				for (String url : urlList) {
					urlsGetData.add(new URL(url));
				}
			} catch (MalformedURLException e1) {
				parseOk = false;
				e1.printStackTrace();
			}
		}

		// Kafka location argument
		if (line.hasOption("k")) {
			kafkaLocation = line.getOptionValue("k");
		} else {
			kafkaLocation = "localhost:9092";
		}

		return parseOk;
	}

	public static void main(String[] args) {

		buildOptions();

		boolean parseOk = parseArguments(args);

		if (parseOk) {
			// Thread initialization
			ArrayList<Thread> threadList = new ArrayList<Thread>();
			for (URL url : urlsGetData) {
				URLPeriodicDownload pd = new URLPeriodicDownload(url);
				Thread t = new Thread(pd);
				t.setName("Thread " + url.toString());
				threadList.add(t);
				t.start();
			}
		}

	}

}
