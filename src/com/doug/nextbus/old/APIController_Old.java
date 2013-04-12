package com.doug.nextbus.old;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.format.Time;
import android.util.Log;

import com.doug.nextbus.R;

/* This class handles all request to the NextBus API.
 * It curates the returned data for easy consumption by other objects in the app. */
public class APIController_Old {

	/**
	 * This grabs the prediction data for a given stop and returns it in an
	 * ordered array list
	 * 
	 * @param route
	 *            the route
	 * @param stoptag
	 *            the stop tag
	 * @return the ordered array list
	 */
	public static ArrayList<Integer> getPrediction(String route, String stoptag) {

		Log.i("Info", "Getting prediction for stoptag=" + stoptag
				+ " and route=" + route);
		String finalURL = createYQLUrl(route, stoptag);
		Log.i("APIController", "Final URL: " + finalURL);

		JSONObject stopPredictionJSON = null;
		try {
			stopPredictionJSON = readJsonFromUrl(finalURL);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			return parseResults(stopPredictionJSON);
		} catch (Exception e) {
			ArrayList<Integer> errorPredictionFlag = new ArrayList<Integer>();
			errorPredictionFlag.add(-1);
			return errorPredictionFlag;
		}
	}

	/**
	 * This takes in the JSON returned from the nextbus feed and puts the
	 * predictions into an ArrayList
	 * 
	 * @param stopPredictionJSON
	 *            the nextbus feed
	 * @return the prediction values in an array list
	 * @throws JSONException
	 *             if something fails while grabbing the values
	 */
	private static ArrayList<Integer> parseResults(JSONObject stopPredictionJSON)
			throws JSONException {

		ArrayList<Integer> predictions = new ArrayList<Integer>();

		JSONArray predictionsJSON = stopPredictionJSON.getJSONObject("query")
				.getJSONObject("results").getJSONArray("p");

		if (predictionsJSON.isNull(0)) {
			return null;
		} else {
			for (int i = 0; i < predictionsJSON.length(); i++) {
				String element = predictionsJSON.getString(i);
				String trimmedElement = element.trim();
				Integer timestamp = null;
				try {
					timestamp = Integer.parseInt(trimmedElement);
				} catch (NumberFormatException nfe) {
					timestamp = Integer.parseInt(trimmedElement.substring(1));
				}
				predictions.add(timestamp);
			}
		}

		return predictions;
	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	private static JSONObject readJsonFromUrl(String url) throws IOException,
			JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is,
					Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}

	/*
	 * Okay. So here's where stuff gets tricky. There isn't actually a
	 * public-facing API for grabbing NextBus data for Georgia Tech, so we
	 * actually use the Yahoo Query Language (YQL) to strip the relevant data
	 * out of NextBus site and consume it as JSON. It works. For now.
	 */
	private static String createYQLUrl(String route, String stoptag) {

		return "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html%20where%20url%3D%22http"
				+ "%3A%2F%2Fwww.nextbus.com%2Fpredictor%2FfancyBookmarkablePredictionLayer.shtml%3Fa%3Dgeorgi"
				+ "a-tech%26r%3D"
				+ route
				+ "%26d%3DCounterclo%26s%3D"
				+ stoptag
				+ "%26ts%3Dnull%22%20and%20xpath"
				+ "%3D%22%2F%2Ftd%5B%40class%3D'predictionNumberForFirstPred'%5D%2Fdiv%2Fp%7C%2F%2Ftd%5B%40cl"
				+ "ass%3D'predictionNumberForOtherPreds'%5D%2Fdiv%2Fp%22&format=json";
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
		} catch (NullPointerException e) {
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
	public static Object[] getActiveRoutesList(Context context) {

		int red = context.getResources().getColor(R.color.red);
		int blue = context.getResources().getColor(R.color.blue);
		int green = context.getResources().getColor(R.color.green);
		int yellow = context.getResources().getColor(R.color.yellow);
		int night = context.getResources().getColor(R.color.night);
		boolean activeRoutesExist = true;

		ArrayList<String> activeRoutesList = new ArrayList<String>();
		ArrayList<Integer> activeColorsList = new ArrayList<Integer>();
		ArrayList<Boolean> activeRoutesHasDirectionsList = new ArrayList<Boolean>();

		Time time = new Time();
		time.switchTimezone("EST");

		time.setToNow();
		int hour = time.hour;
		int day = time.weekDay - 1;
		if (day < 5) {
			// Monday - Friday
			if ((hour >= 7) && (hour <= 19)) {
				// 6:45am - 10:45pm
				activeRoutesList.add("red");
				activeColorsList.add(red);
				activeRoutesHasDirectionsList.add(false);
				activeRoutesList.add("blue");
				activeColorsList.add(blue);
				activeRoutesHasDirectionsList.add(false);
			}
			if ((hour >= 7) && (hour <= 18)) {
				// 6:15am - 9:45pm
				activeRoutesList.add("green");
				activeColorsList.add(green);
				activeRoutesHasDirectionsList.add(true);
			}
			if ((hour >= 5) && (hour <= 21)) {
				// 5:15am - 11:00pm
				activeRoutesList.add("trolley");
				activeColorsList.add(yellow);
				activeRoutesHasDirectionsList.add(true);
			}
			if ((day != 4) && (hour >= 21) || (hour <= 2)) {
				// 8:45pm - 3:30am
				activeRoutesList.add("night");
				activeColorsList.add(night);
				activeRoutesHasDirectionsList.add(true);
			}
		} else if (day == 5) {
			// Saturday
			if ((hour >= 10) && (hour <= 17)) {
				// 9:30am - 7:00pm
				activeRoutesList.add("trolley");
				activeColorsList.add(yellow);
				activeRoutesHasDirectionsList.add(true);
			}
		} else if (day == 6) {
			// Sunday
			if ((hour >= 15) && (hour <= 20)) {
				// 2:30pm - 6:30pm
				activeRoutesList.add("trolley");
				activeColorsList.add(yellow);
				activeRoutesHasDirectionsList.add(true);
			}
			if ((hour >= 20 && time.minute >= 45)
					|| (hour <= 3 && time.minute <= 30)) {
				// 8:45pm - 3:30am
				activeRoutesList.add("night");
				activeColorsList.add(night);
				activeRoutesHasDirectionsList.add(true);
			}
		}

		if (activeRoutesList.size() == 0) {
			activeRoutesExist = false;
		}

		Object[] returnData = {
				Data_Old.convertToStringArray(activeRoutesList),
				Data_Old.convertIntegers(activeColorsList),
				Data_Old.convertToBooleanArray(activeRoutesHasDirectionsList),
				activeRoutesExist };

		return returnData;

	}

}