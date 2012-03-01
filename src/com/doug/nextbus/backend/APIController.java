package com.doug.nextbus.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.util.Log;

public class APIController {

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

		Log.i("Info", "Getting prediction for stoptag=" + stoptag + " and route=" + route);
		String finalURL = createYQLUrl(route, stoptag);

		JSONObject stopPredictionJSON = null;
		try {
			stopPredictionJSON = readJsonFromUrl(finalURL);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
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
	private static ArrayList<Integer> parseResults(JSONObject stopPredictionJSON) throws JSONException {

		ArrayList<Integer> predictions = new ArrayList<Integer>();

		JSONArray predictionsJSON = stopPredictionJSON.getJSONObject("query").getJSONObject("results")
				.getJSONArray("p");

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

	private static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}

	private static String createYQLUrl(String route, String stoptag) {
		return "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html%20where%20url%3D%22http"
				+ "%3A%2F%2Fwww.nextbus.com%2Fpredictor%2FfancyBookmarkablePredictionLayer.shtml%3Fa%3Dgeorgi"
				+ "a-tech%26r%3D" + route + "%26d%3Dnull%26s%3D" + stoptag + "%26ts%3Dfitten%22%20and%20xpath"
				+ "%3D%22%2F%2Ftd%5B%40class%3D'predictionNumberForFirstPred'%5D%2Fdiv%2Fp%7C%2F%2Ftd%5B%40cl"
				+ "ass%3D'predictionNumberForOtherPreds'%5D%2Fdiv%2Fp%22&format=json";
	}

	public static Object[] findNearestStops(Location location) {

		HashMap<String, String> titlesHash = new HashMap<String, String>();
		HashMap<String, LinkedList<String>> routesHash = new HashMap<String, LinkedList<String>>();
		TreeMap<Double, LinkedList<String>> distanceTM = new TreeMap<Double, LinkedList<String>>();

		try {

			JSONObject data = Data.getData();
			JSONArray routes = data.getJSONArray("route");
			for (int i = 0; i < routes.length(); i++) {
				JSONObject route = routes.getJSONObject(i);
				String routeName = route.getString("tag");
				JSONArray stops = route.getJSONArray("stop");
				for (int j = 0; j < stops.length(); j++) {
					JSONObject stop = stops.getJSONObject(j);
					Location stopLocation = new Location("GPS");
					stopLocation.setLatitude(stop.getDouble("lat"));
					stopLocation.setLongitude(stop.getDouble("lon"));
					double distance = location.distanceTo(stopLocation);
					if (distance < 300) {
						String title = stop.getString("title");
						String stopid = stop.getString("stopid");
						Log.i("INFO", "Close Stop Found -- Route: " + routeName + ", Title: " + title);

						if (routesHash.containsKey(stopid)) {
							((LinkedList<String>) routesHash.get(stopid)).add(routeName);
						} else {
							LinkedList<String> routeLL = new LinkedList<String>();
							routeLL.add(routeName);
							routesHash.put(stopid, routeLL);
						}

						if (distanceTM.containsKey(distance)) {
							if (!distanceTM.get(distance).contains(stopid)) {
								((LinkedList<String>) distanceTM.get(distance)).add(stopid);
							}
						} else {

							LinkedList<String> stopLL = new LinkedList<String>();
							stopLL.add(stopid);
							distanceTM.put(distance, stopLL);

						}

						titlesHash.put(stopid, title);

					}
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Object[] ret = new Object[3];
		ret[0] = titlesHash;
		ret[1] = distanceTM;
		ret[2] = routesHash;

		Log.i("INFO", "TitlesHash=" + titlesHash.toString());
		Log.i("INFO", "DistanceTree=" + distanceTM.toString());
		Log.i("INFO", "RoutesHash=" + routesHash.toString());

		return ret;

	}

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
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	private static JSONArray readJsonArrayFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONArray json = new JSONArray(jsonText);
			return json;
		} finally {
			is.close();
		}
	}

	private static ArrayList<String[]> parseLocationResults(JSONArray busLocationsJSON) throws JSONException {

		ArrayList<String[]> busLocations = new ArrayList<String[]>();

		for (int i = 0; i < busLocationsJSON.length(); i++) {
			JSONObject busData = busLocationsJSON.getJSONObject(i);
			String[] entry = { busData.getString("color"), busData.getString("id"), busData.getString("lat"),
					busData.getString("lng"), busData.getString("plat"), busData.getString("plng") };
			busLocations.add(entry);
		}

		return busLocations;

	}

}