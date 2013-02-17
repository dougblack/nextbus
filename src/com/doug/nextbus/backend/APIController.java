package com.doug.nextbus.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.format.Time;
import android.util.Log;

import com.google.gson.Gson;

/* This class handles all request to the NextBus API.
 * It curates the returned data for easy consumption by other objects in the app. */
public class APIController {

	private static String createURL(String route, String direction, String stop) {
		final String localHost = "http://10.0.2.2:3000/bus/";
		// final String remoteHost =
		// "http://desolate-escarpment-6039.herokuapp.com/bus/";
		String url = String.format("%sget?route=%s&direction=%s&stop=%s",
				localHost, route, direction, stop);

		return url;

	}

	public static ArrayList<String> getPrediction(String route,
			String direction, String stop) {

		String target = createURL(route, direction, stop);
		try {
			Reader reader = getReaderFromURL(target);

			PredictionResult result = new Gson().fromJson(reader,
					PredictionResult.class);
			return result.predictions;

		} catch (Exception e) {
			e.printStackTrace();
			Log.d("error", e.getClass().toString());
		}
		return new ArrayList<String>();

	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	/* Gets bus locations from the Georgia Tech mobile app. */
	public static ArrayList<String[]> getBusLocations() {

		String busLocUrl = "http://m.gatech.edu/proxy/walkpath.cip.gatech.edu/bus_position.php";

		ArrayList<Object> busLocations = new ArrayList<Object>();
		JSONArray busLocationsJSON = null;
		try {
			busLocationsJSON = readJsonArrayFromUrl(busLocUrl);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			return parseLocationResults(busLocationsJSON);
		} catch (JSONException e) {
			e.printStackTrace();
			return new ArrayList<String[]>();
		}

	}

	/* Gets JSON from URL array */
	private static JSONArray readJsonArrayFromUrl(String url)
			throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is,
					Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONArray json = new JSONArray(jsonText);
			return json;
		} finally {
			is.close();
		}
	}

	/* Curate JSON location results */
	private static ArrayList<String[]> parseLocationResults(
			JSONArray busLocationsJSON) throws JSONException {

		ArrayList<String[]> busLocations = new ArrayList<String[]>();

		for (int i = 0; i < busLocationsJSON.length(); i++) {
			JSONObject busData = busLocationsJSON.getJSONObject(i);
			String[] entry = { busData.getString("color"),
					busData.getString("id"), busData.getString("lat"),
					busData.getString("lng"), busData.getString("plat"),
					busData.getString("plng") };
			busLocations.add(entry);
		}

		return busLocations;

	}

	/**
	 * This method returns the list of active routes by NextBus official
	 * schedule.
	 * 
	 * @return active routes
	 */
	public static String[] getActiveRoutesList(Context context) {

		ArrayList<String> activeRoutesList = new ArrayList<String>();

		Time time = new Time();
		time.switchTimezone("EST");

		time.setToNow();
		int hour = time.hour;
		int day = time.weekDay;
		if (1 <= day && day <= 5) {
			// Monday - Friday
			if ((hour >= 7) && (hour <= 22)) {
				// 6:45am - 10:45pm
				activeRoutesList.add("red");
				activeRoutesList.add("blue");
			}
			if ((hour >= 7) && (hour <= 21)) {
				// 6:15am - 9:45pm
				activeRoutesList.add("green");
			}
			if ((hour >= 5) && (hour <= 22)) {
				// 5:15am - 11:00pm
				activeRoutesList.add("trolley");
			}
			if ((day != 4) && (hour >= 21) || (hour <= 3)) {
				// 8:45pm - 3:30am
				activeRoutesList.add("night");
			}
		} else if (day == 6) {
			// Saturday
			if ((hour >= 10) && (hour <= 18)) {
				// 9:30am - 7:00pm
				activeRoutesList.add("trolley");
			}
		} else if (day == 0) {
			// Sunday
			if ((hour >= 15) && (hour <= 21)) {
				// 2:30pm - 6:30pm
				activeRoutesList.add("trolley");
			}
			if ((hour >= 20 && time.minute >= 45)
					|| (hour <= 3 && time.minute <= 30)) {
				// 8:45pm - 3:30am
				activeRoutesList.add("night");

			}
		}

		String[] strings = {};
		return activeRoutesList.toArray(strings);

	}

	public static Reader getReaderFromURL(String target) throws Exception {
		URL url = new URL(target);

		URLConnection urlConnection = url.openConnection();
		urlConnection.setConnectTimeout(3000);

		urlConnection.setRequestProperty("Accept", "application/json");

		InputStream input = urlConnection.getInputStream();

		Reader reader = new InputStreamReader(input, "UTF-8");
		return reader;
	}

	private class PredictionResult {
		ArrayList<String> predictions;
	}

}
