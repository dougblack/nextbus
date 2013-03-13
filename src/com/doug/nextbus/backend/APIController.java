package com.doug.nextbus.backend;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.content.Context;
import android.text.format.Time;
import android.util.Log;

import com.google.gson.Gson;

/**
 * This class handles all request to the NextBus API. It curates the returned
 * data for easy consumption by other objects in the app.
 */
public class APIController {
	/** How many times to retry before giving up */
	private static final int NUM_RETRIES = 2;

	private static String createURL(String route, String direction, String stop) {
		// final String localHost = "http://10.0.2.2:3000/bus/";

		final String remoteHost = "http://desolate-escarpment-6039.herokuapp.com/bus/";
		String url = String.format("%sget?route=%s&direction=%s&stop=%s",
				remoteHost, route, direction, stop);

		// final String pythonHost = "http://quiet-fjord-4717.herokuapp.com/";
		// String url = "http://quiet-fjord-4717.herokuapp.com/" + route + "/"
		// + direction + "/" + stop;

		return url;

	}

	public static ArrayList<String> getPrediction(String route,
			String direction, String stop) {
		String target = createURL(route, direction, stop);
		for (int i = 0; i < NUM_RETRIES; i++) {
			try {
				Reader reader = getReaderFromURL(target);

				PredictionResultGSON result = new Gson().fromJson(reader,
						PredictionResultGSON.class);
				return result.predictions;
			} catch (Exception e) {
				e.printStackTrace();
				Log.d("error", e.getClass().toString());
			}
		}
		ArrayList<String> al = new ArrayList<String>();
		al.add("-1"); // Returning -1 if there was an error
		return al;

	}

	/**
	 * This method returns the list of active routes by NextBus official
	 * schedule.
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
				activeRoutesList.add("blue");
				activeRoutesList.add("red");
			}
			if ((hour >= 5) && (hour <= 22)) {
				// 5:15am - 11:00pm
				activeRoutesList.add("trolley");
			}
			if ((day != 4) && (hour >= 21) || (hour <= 3)) {
				// 8:45pm - 3:30am
				activeRoutesList.add("night");
			}
			if ((hour >= 7) && (hour <= 21)) {
				// 6:15am - 9:45pm
				activeRoutesList.add("green");
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
			if ((hour >= 20) || (hour <= 3)) {
				// 8:45pm - 3:30am
				activeRoutesList.add("night");

			}
		}

		String[] strings = {};
		return activeRoutesList.toArray(strings);

	}

	private static Reader getReaderFromURL(String target) throws Exception {
		URL url = new URL(target);

		URLConnection urlConnection = url.openConnection();
		int time = 2000;
		urlConnection.setConnectTimeout(time);
		urlConnection.setReadTimeout(time * 2);

		urlConnection.setRequestProperty("Accept", "application/json");

		InputStream input = urlConnection.getInputStream();

		Reader reader = new InputStreamReader(input, "UTF-8");
		return reader;
	}

	/** For Gson parsing */
	private class PredictionResultGSON {
		ArrayList<String> predictions;
	}

}
