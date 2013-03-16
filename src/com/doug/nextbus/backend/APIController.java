package com.doug.nextbus.backend;

import hirondelle.date4j.DateTime;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.TimeZone;

import android.content.Context;
import android.text.format.Time;
import android.util.Log;

import com.google.gson.Gson;

/**
 * This class handles all request to the NextBus API. It curates the returned
 * data for easy consumption by other objects in the app.
 */
public class APIController {
	/** How many times to retry to make connection */
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

		DateTime allNow = DateTime.now(TimeZone.getDefault());

		DateTime now = DateTime
				.forTimeOnly(allNow.getHour(), allNow.getMinute(),
						allNow.getSecond(), allNow.getNanoseconds());

		ArrayList<String> activeRoutesList = new ArrayList<String>();

		Time time = new Time();

		time.setToNow();
		int day = time.weekDay;

		if (1 <= day && day <= 5) { // Monday - Friday
			if (now.gteq(new DateTime("06:50:00"))
					&& now.lteq(new DateTime("22:10:00"))) {
				// 7:00am - 9:45pm
				activeRoutesList.add("blue");
				activeRoutesList.add("red");
			}
			if (now.gteq(new DateTime("05:35:00"))
					&& now.lteq(new DateTime("22:40:00"))) {
				// 5:45am - 10:30pm
				activeRoutesList.add("trolley");
			}
			if ((day != 5) && (now.gteq(new DateTime("20:50:00")))) {
				// 9:00pm - 3:00am
				// 9:00pm - midnight on everyday except Friday
				activeRoutesList.add("night");
			}

			if ((now.lteq(new DateTime("03:10:00")))) {
				// midnight - 3:00am on all weekdays
				activeRoutesList.add("night");
			}
			if (now.gteq(new DateTime("06:35:00"))
					&& now.lteq(new DateTime("21:10:00"))) {
				// 6:45am - 9:00pm
				activeRoutesList.add("green");
			}
			if (now.gteq(new DateTime("07:05:00"))
					&& now.lteq(new DateTime("19:25:00"))) {
				// 7:15am - 7:12pm
				activeRoutesList.add("emory");
			}
		} else if (day == 6) { // Saturday
			if (now.gteq(new DateTime("09:50:00"))
					&& now.lteq(new DateTime("18:40:00"))) {
				// 10:00am - 6:30pm
				activeRoutesList.add("trolley");
			}
		} else if (day == 0) { // Sunday
			if (now.gteq(new DateTime("14:50:00"))
					&& now.lteq(new DateTime("21:55:00"))) {
				// 3:00pm - 9:45pm
				activeRoutesList.add("trolley");
			}
			if (now.gteq(new DateTime("20:50:00"))) {
				// 9:00pm - 3:00am
				activeRoutesList.add("night");
			}
			if (now.lteq(new DateTime("03:10:00"))) {
				// 9:00pm - 3:00am
				activeRoutesList.add("night");
			}
		}

		String[] strings = {};
		return activeRoutesList.toArray(strings);
	}

	private static Reader getReaderFromURL(String target) throws Exception {
		URL url = new URL(target);

		URLConnection urlConnection = url.openConnection();
		int time = 2000; // time logic is weird, I know, but it works
		urlConnection.setConnectTimeout(time);
		urlConnection.setReadTimeout(time * 2);

		urlConnection.setRequestProperty("Accept", "application/json");

		new Thread(new InterruptThread(Thread.currentThread(), urlConnection,
				time * (3 / 2))).start();

		InputStream input = urlConnection.getInputStream();

		Reader reader = new InputStreamReader(input, "UTF-8");
		return reader;
	}

	/** For Gson parsing */
	private class PredictionResultGSON {
		ArrayList<String> predictions;
	}
}

class InterruptThread implements Runnable {
	Thread parent;
	URLConnection con;
	long timeout;

	public InterruptThread(Thread parent, URLConnection con, long timeout) {
		this.parent = parent;
		this.con = con;
		this.timeout = timeout;
	}

	public void run() {
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {

		}
		// Timer thread forcing parent to quit connection
		((HttpURLConnection) con).disconnect();
		System.out
				.println("Timer thread closed connection held by parent, exiting");
	}
}
