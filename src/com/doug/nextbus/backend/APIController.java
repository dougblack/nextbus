package com.doug.nextbus.backend;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import android.location.Location;
import android.util.Log;

public class APIController {

	static String baseURL = "http://www.nextmuni.com/s/COM.NextBus.Servlets.XMLFeed?command=predictions&a=georgia-tech";

	public static TreeMap<Integer, Object> getPrediction(String route, String stopid) {

		Log.i("Info", "Getting prediction for stopID=" + stopid + " and route=" + route);
		HashMap<String, Object> predictionData = null;
		String finalURL = baseURL + "&stopId=" + stopid;

		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp;
		try {
			sp = spf.newSAXParser();
			DataHandler dataHandler = new DataHandler();
			URL predictionXMLURL = new URL(finalURL);
			InputStream conn = predictionXMLURL.openConnection().getInputStream();
			sp.parse(conn, dataHandler);
			predictionData = dataHandler.getXMLData();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			HashMap<String, Object> predictionDataMinusBodyTags = (HashMap<String, Object>) predictionData.get("body");
			return parseResults(predictionDataMinusBodyTags);
		} catch (NullPointerException npe) {
			TreeMap<Integer, Object> errorTreeMap = new TreeMap<Integer, Object>();
			errorTreeMap.put(-1, "error");
			return errorTreeMap;
		}

	}

	@SuppressWarnings("unchecked")
	public static TreeMap<Integer, Object> parseResults(HashMap<String, Object> predictionData) {

		TreeMap<Integer, Object> predictionTreeMap = new TreeMap<Integer, Object>();

		Object predictionObject = predictionData.get("predictions");
		int deadRoutes = 0;
		if (predictionObject instanceof ArrayList) {
			// Multiple routes
			ArrayList<HashMap<String, Object>> predictionArrayList = (ArrayList<HashMap<String, Object>>) predictionObject;
			for (HashMap<String, Object> routeHash : predictionArrayList) {
				String route = (String) routeHash.get("routeTag");
				HashMap<String, Object> direction = (HashMap<String, Object>) routeHash.get("direction");
				if ((direction != null) && (direction.get("prediction") != null)) {

					// Get list of predictions.
					try {
						ArrayList<HashMap<String, String>> predictions = (ArrayList<HashMap<String, String>>) direction
								.get("prediction");

						// Cycle through each prediction.
						for (HashMap<String, String> prediction : predictions) {

							predictionTreeMap = addPredictionToTreeMap(prediction, predictionTreeMap, route);
						}
					} catch (ClassCastException e) {
						predictionTreeMap = addPredictionToTreeMap(
								(HashMap<String, String>) direction.get("prediction"), predictionTreeMap, route);
					}
				} else {
					// If no predictions exist, put flag in TreeMap.
					deadRoutes--;
					Log.i("INFO", "No predictions. Added (" + deadRoutes + ", " + route + ") to TreeMap.");
					predictionTreeMap.put(deadRoutes, route);
				}
			}

		} else {
			Log.i("Info", "One route.");

			HashMap<String, Object> predictionHash = (HashMap<String, Object>) predictionObject;
			String route = (String) predictionHash.get("routeTag");
			HashMap<String, Object> direction = (HashMap<String, Object>) predictionHash.get("direction");
			if ((direction != null) && (direction.get("prediction") != null)) {
				Log.i("Info", "Direction non null");
				// Get list of predictions.
				try {
					ArrayList<HashMap<String, String>> predictions = (ArrayList<HashMap<String, String>>) direction
							.get("prediction");

					// Cycle through each prediction.
					for (HashMap<String, String> prediction : predictions) {
						// Put in tree map > (time, route)
						Integer minutes = Integer.parseInt(prediction.get("minutes"));
						predictionTreeMap.put(minutes, route);
					}
				} catch (ClassCastException e) {
					Log.i("INFO", "Class cast exception.");
					HashMap<String, String> prediction = (HashMap<String, String>) direction.get("prediction");
					predictionTreeMap.put(Integer.parseInt(prediction.get("minutes")), route);
				}
			} else {
				// If no predictions exist, put flag in TreeMap.
				deadRoutes--;
				predictionTreeMap.put(deadRoutes, route);
			}

		}
		Log.i("INFO", "TreeMap=" + predictionTreeMap.toString());
		return predictionTreeMap;

	}

	public static TreeMap<Integer, Object> addPredictionToTreeMap(HashMap<String, String> prediction,
			TreeMap<Integer, Object> predictionTreeMap, String route) {
		// Put in tree map > (time, route)
		Integer minutes = Integer.parseInt(prediction.get("minutes"));
		if (predictionTreeMap.containsKey(minutes)) {
			Log.i("INFO", "Collision!");
			LinkedList<String> value;
			if (predictionTreeMap.get(minutes) instanceof LinkedList) {
				Log.i("INFO", "Added to old LinkedList");
				value = (LinkedList<String>) predictionTreeMap.get(minutes);
				value.add(route);
			} else {
				Log.i("INFO", "Converted value to LinkedList");
				value = new LinkedList<String>();
				value.add((String) predictionTreeMap.get(minutes));
				value.add(route);
			}
			predictionTreeMap.put(minutes, value);
		} else {
			predictionTreeMap.put(minutes, route);
		}

		return predictionTreeMap;
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

}