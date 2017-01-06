package org.dspace.app.stats;

import java.lang.Object;
import java.sql.SQLException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.lang.Exception;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import org.dspace.core.Context;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;

public class CountryFix {
	private static boolean isVerbose = false;
	private static Date datainicial = null;
	private static Date datafinal = null;
	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyyMMdd");
	private static List tables = new ArrayList();
	private static String tablesLine = null;

	public static void main(String[] argv) throws Exception {
		readCommandLineOptions(argv);

		Context context = null;

		try {
			context = new Context();
			fixCountries(context, datainicial, datafinal);
			context.commit();
		} catch (SQLException e) {
			System.out.println("Database error: " + e.getMessage());
		} finally {
			if ((context != null) && context.isValid())
				context.abort();
		}
	}

	private static Options setCommandLineOptions() {

		// create an options object and populate it
		Options options = new Options();

		OptionBuilder.withLongOpt("dates");
		OptionBuilder.withValueSeparator(',');
		OptionBuilder
				.withDescription("Run Country Fix for the period of time defined.");

		Option dates = OptionBuilder.create('d');
		dates.setArgs(2);
		options.addOption(dates);

		OptionBuilder.withLongOpt("tables");
		OptionBuilder.withDescription("Select table for country fix.");

		Option tables = OptionBuilder.create('t');
		tables.setArgs(1);
		options.addOption(tables);

		options.addOption("v", "verbose", false, "verbose");
		options.addOption("h", "help", false, "help");

		return options;
	}

	private static void readCommandLineOptions(String[] argv) {
		// set up command line parser
		CommandLineParser parser = new PosixParser();
		CommandLine line = null;

		Options options = setCommandLineOptions();

		try {
			line = parser.parse(options, argv);
		} catch (MissingArgumentException e) {
			System.out.println("Missing Argument: " + e.getMessage());
			new HelpFormatter().printHelp("CountryFix\n", options);
			System.exit(1);
		} catch (ParseException e) {
			System.out.println("ERROR: " + e.getMessage());
			new HelpFormatter().printHelp("CountryFix\n", options);
			System.exit(1);
		}

		if (line.hasOption('h')) {
			new HelpFormatter().printHelp("CountryFix\n", options);
			System.exit(0);
		}

		if (line.hasOption('v')) {
			isVerbose = true;
		}

		String tempD[] = null;
		if (line.hasOption('d')) {
			tempD = line.getOptionValues('d');
		}
		if (tempD == null || tempD.length == 0) {
			new HelpFormatter().printHelp("CountryFix\n", options);
			System.exit(0);
		}

		String temp[] = null;
		if (line.hasOption('t')) {
			temp = line.getOptionValues('t');
		}
		if (temp == null || temp.length == 0) {
			tables.add("view");
			tables.add("download");
		} else {

			if (temp[0].equals("all")) {
				tables.add("view");
				tables.add("download");
			}
			if (temp[0].equals("view"))
				tables.add("view");
			else if (temp[0].equals("download"))
				tables.add("download");
		}

		try {
			datainicial = dateFormat.parse(tempD[0].trim());
			datafinal = dateFormat.parse(tempD[1].trim());			
		} catch (Exception e) {
			System.out.println("ERROR parsing dates: " + e.getMessage());
		}

		tablesLine = "";
		for (int i = 0; i < tables.size(); i++)
			tablesLine = tablesLine + tables.get(i)
					+ (i == tables.size() - 1 ? "" : ", ");

	}

	private static void fixCountries(Context context, Date startDate,
			Date endDate) throws SQLException {

		if (isVerbose) {
			System.out.println("Start date: " + startDate.toString());
			System.out.println("End Date: " + endDate.toString());
		}

		long endTime = endDate.getTime(); // create your endtime here, possibly
											// using Calendar or Date
		long startTime = startDate.getTime();

		for (int i = 0; i < tables.size(); i++) {
			if (tables.get(i).equals("view")) {
				if (isVerbose)
					System.out.println("Processing table view...");
				fixCountriesByTable(context, "stats.view", startTime, endTime);
				if (isVerbose)
					System.out.println("End processing table view...");
			}
			if (tables.get(i).equals("download")) {
				if (isVerbose)
					System.out.println("Processing table download...");
				fixCountriesByTable(context, "stats.download", startTime,
						endTime);
				if (isVerbose)
					System.out.println("End processing table download...");
			}
		}
	}

	private static void checkCountry(Context context, String countryCode,
			String countryName) throws SQLException {

		TableRow row = DatabaseManager.findByUnique(context, "stats.country",
				"code", countryCode);

		// If the country does not exist in the table, insert it
		if (row == null) {
			String sql = "insert into stats.country (code, name) values (?, ?)";
			DatabaseManager.updateQuery(context, sql, countryCode, countryName);
		}
	}

	private static void fixCountriesByTable(Context context, String table,
			long StartTime, long EndTime) throws SQLException {

		while (StartTime <= EndTime) {

			int numberOfEvents = 0;
			int numberOfCorrectedEvents = 0;
			long interval = 24 * 1000 * 60 * 60; // 1 hour in millis

			DateFormat df = new SimpleDateFormat("yyyyMMdd");

			String sql = null;
			String reportDate = df.format(new Date(StartTime));

			if (isVerbose)
				System.out.print("Processing " + reportDate + " ... ");

			if (table.equals("stats.view")) {
				sql = "select view_id, ip, country_code from stats.view where date=to_date('"
						+ reportDate + "','yyyymmdd')";
			}

			if (table.equals("stats.download")) {
				sql = "select download_id, ip, country_code from stats.download where date=to_date('"
						+ reportDate + "','yyyymmdd')";
			}

			TableRowIterator iterator = DatabaseManager.query(context, sql);

			while (iterator.hasNext()) {
				numberOfEvents++;

				TableRow row = iterator.next();
				int view_id = 0;
				int download_id = 0;

				if (table.equals("stats.view")) {
					view_id = row.getIntColumn("view_id");
				}

				if (table.equals("stats.download")) {
					download_id = row.getIntColumn("download_id");
				}

				String ip = row.getStringColumn("ip");
				String country_code = row.getStringColumn("country_code");

				String country = Country.getCountry(context, ip);

				String[] temp = country.split(";");

				if (temp.length == 2) {
					String countryCode = temp[0];
					String countryName = temp[1];
					String sqlUpdate = null;
					checkCountry(context, countryCode, countryName);

					if (!country_code.equals(countryCode)) {
						if (table.equals("stats.view")) {

							sqlUpdate = "update " + "stats.view" + " "
									+ "set country_code='" + countryCode + "' "
									+ "where view_id=" + view_id;
						}

						if (table.equals("stats.download")) {
							sqlUpdate = "update " + "stats.download" + " "
									+ "set country_code='" + countryCode + "' "
									+ "where download_id=" + download_id;
						}

						DatabaseManager.updateQuery(context, sqlUpdate);

						numberOfCorrectedEvents++;
					}
				}
			}

			if (isVerbose)
				System.out.println("events: " + numberOfEvents + " fixed: "
						+ numberOfCorrectedEvents);

			context.commit();

			StartTime += interval;
		}
	}
}