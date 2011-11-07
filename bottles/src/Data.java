

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import processing.core.PApplet;
import au.com.bytecode.opencsv.*;

/**
 * Loads and parses the information and offers static access to the normalized database.
 */
public class Data {

	// data structure: a value <for every indicator <for every region <for every year>>>
	private static HashMap<String, HashMap<String, HashMap<String, Float>>> database;
	private static HashMap<String, HashMap<String, HashMap<String, Float>>> normalizedDatabase;

	// resource loader
	private static ClassLoader classLoader = Data.class.getClassLoader();

	static {

		parseFiles();
		normalizeDatabase();
		interpolateHoles();
	}

	/**
	 * Parse CSV files.
	 */
	private static void parseFiles() {

		database = new HashMap<String, HashMap<String, HashMap<String, Float>>>();

		parse("data/cellularPer100.csv");
		parse("data/co2.csv");
		parse("data/fdi.csv");
		parse("data/gdp.csv");
		parse("data/healthExpendeture.csv");
		parse("data/incomeHighest10.csv");
		parse("data/incomeLowest10.csv");
		parse("data/incomeThird20.csv");
		parse("data/internetUsersPer100.csv");
		parse("data/literacyFemale.csv");
		parse("data/literacyMale.csv");
		parse("data/literacyTotal.csv");
		parse("data/oda.csv");
		parse("data/popThousands.csv");
		parse("data/povertyRatio.csv");
		parse("data/schoolFemale.csv");
		parse("data/schoolMale.csv");
		parse("data/schoolTotal.csv");
		parse("data/telephoneSubscription.csv");
		parse("data/undernourishment.csv");
		parse("data/unemploymentFemale.csv");
		parse("data/unemploymentMale.csv");
		parse("data/unemploymentTotal.csv");
		parse("data/urbanPop.csv");
		parse("data/waterAccess.csv");

		// world.csv
	}

	/**
	 * Normalize database.
	 */
	private static void normalizeDatabase() {

		normalizedDatabase = new HashMap<String, HashMap<String, HashMap<String, Float>>>();
		HashMap<String, HashMap<String, Float>> byCountry;
		HashMap<String, Float> byYear;
		float originalValue, normalizedValue, localMin, localMax;

		for (String indicator : database.keySet()) {

			byCountry = new HashMap<String, HashMap<String, Float>>();
			normalizedDatabase.put(indicator, byCountry);

			for (String country : database.get(indicator).keySet()) {

				byYear = new HashMap<String, Float>();
				byCountry.put(country, byYear);

				if (database.get(indicator).get(country).values().isEmpty()) {
					continue;
				}

				localMin = Collections.min(database.get(indicator).get(country).values());
				localMax = Collections.max(database.get(indicator).get(country).values());

				for (String year : database.get(indicator).get(country).keySet()) {
					originalValue = database.get(indicator).get(country).get(year);
					normalizedValue = PApplet.map(originalValue, localMin, localMax, 40f, 125f);
					byYear.put(year, normalizedValue);
				}
			}
		}
	}

	/**
	 * Interpolate the normalized database.
	 */
	private static void interpolateHoles() {

		HashMap<String, Float> byYear;
		float copyValue;
		float y1, y2;
		int x1, x2;
		float slope;

		for (String indicator : normalizedDatabase.keySet()) {
			for (String country : normalizedDatabase.get(indicator).keySet()) {
				for (int year = 1990; year <= 2010; year++) {
					byYear = normalizedDatabase.get(indicator).get(country);

					if (byYear.size() == 0) { // no data at all
						break;
					}
					if (byYear.size() == 1) { // one data point copied to all
						copyValue = byYear.values().iterator().next();
						for (int yearIndex = 1990; yearIndex <= 2010; yearIndex++) {
							byYear.put(yearIndex + "", copyValue);
						}
						break;
					}
					if (byYear.containsKey(year + "")) { // data point exists
						continue;
					}
					if (year == 1990) { // extrapolate from future and future
						x1 = getNextValidYear(indicator, country, year);
						x2 = getNextValidYear(indicator, country, x1);
						y1 = byYear.get(x1 + "");
						y2 = byYear.get(x2 + "");
						slope = (y2 - y1) / (x2 - x1);
						byYear.put(year + "", (year * slope) + y1 - (slope * x1));
						continue;
					}
					try { // interpolate from past and future
						x1 = getPreviousValidYear(indicator, country, year);
						x2 = getNextValidYear(indicator, country, year);
						y1 = byYear.get(x1 + "");
						y2 = byYear.get(x2 + "");
						slope = (y2 - y1) / (x2 - x1);
						byYear.put(year + "", (year * slope) + y1 - (slope * x1));
					} catch (IllegalArgumentException e) { // extrapolate from
															// past and past
						x1 = getPreviousValidYear(indicator, country, year);
						x2 = getPreviousValidYear(indicator, country, x1);
						y1 = byYear.get(x1 + "");
						y2 = byYear.get(x2 + "");
						slope = (y2 - y1) / (x2 - x1);
						byYear.put(year + "", (year * slope) + y1 - (slope * x1));
					}
				}
			}
		}
	}

	/**
	 * @param indicator The type of data requested.
	 * @param country The country of the data requested.
	 * @param year The year from which a value is searched for.
	 * @return The next valid year in the Map of indicator by country by year.
	 * @throws IllegalArgumentException if there is no next valid year.
	 */
	private static int getNextValidYear(String indicator, String country, int year) {

		System.out.println("next(" + year + ")");
		for (int yearInteger = year + 1; yearInteger <= 2010; yearInteger++) {
			System.out.println(yearInteger);
			if (normalizedDatabase.get(indicator).get(country).containsKey(yearInteger + ""))
				return yearInteger;
		}
		throw new IllegalArgumentException();
	}

	/**
	 * @param indicator The type of data requested.
	 * @param country The country of the data requested.
	 * @param year The year from which a value is searched for.
	 * @return The next valid year in the Map of indicator by country by year.
	 * @throws IllegalArgumentException if there is no next valid year.
	 */
	private static int getPreviousValidYear(String indicator, String country, int year) {

		System.out.println("previous(" + year + ")");
		for (int yearInteger = year - 1; yearInteger >= 1990; yearInteger--) {
			System.out.println(yearInteger);
			if (normalizedDatabase.get(indicator).get(country).containsKey(yearInteger + ""))
				return yearInteger;
		}
		throw new IllegalArgumentException();
	}

	/**
	 * @return The normalized database.
	 */
	public static HashMap<String, HashMap<String, HashMap<String, Float>>> normalizedDatabase() {

		return normalizedDatabase;
	}

	/**
	 * @param indicator The type of data requested
	 * @param country The country requested.
	 * @param year The year requested.
	 * @return Float for the given parameters.
	 */
	public static float get(String indicator, String country, String year) {

		return normalizedDatabase.get(indicator).get(country).get(year);
	}

	/**
	 * Parses fileName and puts the data into database.
	 * 
	 * @param fileName CSV file to be parsed.
	 */
	private static void parse(String fileName) {

		System.out.println(classLoader);
		CSVReader reader = new CSVReader(new InputStreamReader(classLoader.getResourceAsStream(fileName)));

		HashMap<String, HashMap<String, Float>> data = new HashMap<String, HashMap<String, Float>>();
		List<String[]> parsedFile;
		try {
			parsedFile = reader.readAll();
			HashMap<String, Float> hashedRow;
			for (String[] row : parsedFile) {
				hashedRow = new HashMap<String, Float>();
				data.put(row[0], hashedRow);
				for (int i = 2; i < row.length; i++) {
					if (!(row[i].equals(""))) {
						hashedRow.put(1988 + i + "", Float.parseFloat(row[i].replaceAll(",", "")));
					}
				}
			}
			database.put(fileName.substring(5, fileName.length() - 4), data);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}