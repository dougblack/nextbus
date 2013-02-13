package com.doug.nextbus.backend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.doug.nextbus.R;

/* This class controls reading and writing local files as well as persisting current state data. */
public class Data {

	static JSONObject data;
	static Context context;

	/**
	 * This read the text file and instantiates it as a static JSONObject, data.
	 * 
	 * @param context
	 *            application context
	 */
	public static void setConfigData(Context context) {

		setContext(context);

		InputStream is = (InputStream) context.getResources().openRawResource(R.raw.routeconfig);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuffer lines = null;
		try {
			lines = new StringBuffer();
			String line = br.readLine();
			while (line != null) {
				lines.append(line);
				line = br.readLine();

			}
		} catch (IOException ie) {
			Log.e("ERROR", "Failed to parse file.");
		}

		try {
			data = new JSONObject(lines.toString());
		} catch (JSONException e) {
			Log.e("ERROR", "Failed to make into JSON.");
		}

	}
  
  /* Reads the path data for a given route */
	public static JSONArray getRoutePathData(String route) {
		
		InputStream is = null;
		
		if (route.equals("red")) {
			is = (InputStream) context.getResources().openRawResource(R.raw.redroute);
		} else if (route.equals("blue")) {
			is = (InputStream) context.getResources().openRawResource(R.raw.blueroute);
		} else if (route.equals("green")) {
			is = (InputStream) context.getResources().openRawResource(R.raw.greenroute);
		} else if (route.equals("trolley")) {
			is = (InputStream) context.getResources().openRawResource(R.raw.trolleyroute);
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuffer lines = null;
		JSONObject redRouteData = null;
		JSONArray redRoutePathData = null;
		try {
			lines = new StringBuffer();
			String line = br.readLine();
			while (line != null) {
				lines.append(line);
				line = br.readLine();

			}
		} catch (IOException ie) {
			Log.e("ERROR", "Failed to parse file.");
		}

		try {
			redRouteData = new JSONObject(lines.toString());
			redRoutePathData = redRouteData.getJSONObject("body").getJSONObject("route").getJSONArray("path");
		} catch (JSONException e) {
			Log.e("ERROR", "Failed to make into JSON.");
		}
		
		return redRoutePathData;

	}

	public static JSONObject getData() {
		return data;
	}

	private static void setContext(Context context2) {
		context = context2;
	}

	/**
	 * This returns the JSONObject for the specified route.
	 * 
	 * @param route
	 *            the route to get the JSONObject from
	 * @return the JSONObject for the given route.  */
	public JSONObject getRoute(String route) {

		JSONObject thisroute = new JSONObject();

		try {
			JSONArray routes = data.getJSONArray("route");
			for (int i = 0; i < routes.length(); i++) {
				thisroute = routes.getJSONObject(i);
				if (route.equals(thisroute.getString("tag")))
					return thisroute;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return new JSONObject();
	}

	/**
	 * This returns a 2D array of both the stop titles and stopids for the given
	 * route.
	 * 
	 * @param routeStr
	 *            the route to check
	 * @return the 2D array
	 */
	public Object[] getStopList(String routeStr) {
		Object[] finalList = { "", "" };
		String[] strings = {};
		String[] integers = {};
		ArrayList<String> stopList = new ArrayList<String>();
		ArrayList<String> stopListTags = new ArrayList<String>();

		JSONArray stops;
		try {
			stops = getRoute(routeStr).getJSONArray("stop");
			for (int i = 0; i < stops.length(); i++) {
				String title = stops.getJSONObject(i).getString("title");
				String stoptag = stops.getJSONObject(i).getString("tag");
				if (title != null && stoptag != null) {
					stopList.add(title);
					stopListTags.add(stoptag);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		finalList[0] = stopList.toArray(strings);
		finalList[1] = stopListTags.toArray(integers);
		return finalList;
	}

	/**
	 * This returns an array of all the given directions for a specified stop
	 * 
	 * @param routeStr
	 *            the stop to get directions for
	 * @return the String[] with directions
	 */
	public String[] getDirectionList(String routeStr) {

		String[] finalList = { "" };
		ArrayList<String> directionList = new ArrayList<String>();

		JSONArray directions;

		try {
			directions = getRoute(routeStr).getJSONArray("direction");
			for (int i = 0; i < directions.length(); i++) {
				directionList.add(directions.getJSONObject(i).getString("title"));
			}
		} catch (JSONException e) {
			Log.e("ERROR", "Couldn't parse directions.");
		}

		return directionList.toArray(finalList);
	}

	/**
	 * Returns the a dual list of stop titles and stop ids for a given route and
	 * direction
	 * 
	 * @param route
	 *            the given route
	 * @param direction
	 *            the given direction
	 * @return the stop titles and stop ids in one array
	 */
	public Object[] getListForRoute(String route, String direction) {

		Object[] finalList = { "", "" };
		String[] strings = {};
		String[] integers = {};
		ArrayList<String> stopList = new ArrayList<String>();
		ArrayList<String> stopListTags = new ArrayList<String>();
		HashMap<String, String> stopHash = new HashMap<String, String>();

		JSONArray directions;
		JSONObject directionObj = null;
		try {
			directions = getRoute(route).getJSONArray("direction");
			for (int i = 0; i < directions.length(); i++) {
				if (directions.getJSONObject(i).getString("title").equals(direction)) {
					directionObj = directions.getJSONObject(i);
				}
			}
			JSONArray directionObjStops = directionObj.getJSONArray("stop");
			for (int i = 0; i < directionObjStops.length(); i++) {
				stopHash.put(directionObjStops.getJSONObject(i).getString("tag"), "Hi");
			}

			JSONArray allStops = getRoute(route).getJSONArray("stop");
			for (int i = 0; i < allStops.length(); i++) {
				if (stopHash.containsKey(allStops.getJSONObject(i).getString("tag")))
					stopList.add(allStops.getJSONObject(i).getString("title"));
				stopListTags.add(allStops.getJSONObject(i).getString("tag"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		finalList[0] = stopList.toArray(strings);
		finalList[1] = stopListTags.toArray(integers);
		return finalList;

	}

  /* Capitalize a string */
	public static String capitalize(String route) {

		char[] chars = route.toLowerCase().toCharArray();
		boolean found = false;
		for (int i = 0; i < chars.length; i++) {
			if (!found && Character.isLetter(chars[i])) {
				if (i > 0 && Character.isDigit(chars[i - 1])) {
					found = true;
				} else {
					chars[i] = Character.toUpperCase(chars[i]);
					found = true;
				}
			} else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'') {
				found = false;
			}
		}
		return String.valueOf(chars);
	}

  /* Make ArrayList of integers into an int array */
	public static int[] convertIntegers(List<Integer> integers) {
		int[] ret = new int[integers.size()];
		Iterator<Integer> iterator = integers.iterator();
		for (int i = 0; i < ret.length; i++) {
			ret[i] = iterator.next().intValue();
		}
		return ret;
	}

	public static String[] convertToStringArray(ArrayList<String> list) {

		String[] ret = new String[list.size()];
		Iterator<String> iterator = list.iterator();
		for (int i = 0; i < ret.length; i++) {
			ret[i] = iterator.next();
		}
		return ret;

	}

	public static Object convertToBooleanArray(ArrayList<Boolean> list) {
		boolean[] ret = new boolean[list.size()];
		Iterator<Boolean> iterator = list.iterator();
		for (int i = 0; i < ret.length; i++) {
			ret[i] = iterator.next();
		}
		return ret;
	}

	public static JSONObject readStopData() {
		JSONObject stopData = null;
		try {
			BufferedReader input = new BufferedReader(new FileReader(context.getFilesDir().toString()
					+ "/favoritestop.txt"));
			stopData = new JSONObject(input.readLine());

		} catch (FileNotFoundException e) {
			Log.i("Data.load()", "Properties.txt not found.");
			e.printStackTrace();
		} catch (JSONException e) {
			Log.i("Data.load()", "Error converting workingData to JSONObject.");
			e.printStackTrace();
		} catch (IOException e) {
			Log.i("Data.load()", "Error parsing Properties.txt.");
			e.printStackTrace();
		}
		return stopData;
	}

	private static void writeStopData(JSONObject settings) {

		try {
			Writer output = new BufferedWriter(new FileWriter(context.getFilesDir().toString() + "/favoritestop.txt"));
			output.write(settings.toString());
			output.close();
		} catch (IOException e) {
			Log.i("Data.save()", "Error loading file.");
			e.printStackTrace();
		}

	}

	public static ArrayList<String> getAllRoutesForStop(String stoptag) {

		ArrayList<String> routesForThisStop = new ArrayList<String>();

		try {
			JSONArray routes = data.getJSONArray("route");
			for (int i = 0; i < routes.length() - 1; i++) {
				JSONObject route = routes.getJSONObject(i);
				String routeName = route.getString("tag");
				JSONArray stops = route.getJSONArray("stop");
				for (int j = 0; j < stops.length() - 1; j++) {
					JSONObject stop = stops.getJSONObject(j);
					if (stop.getString("tag").equals(stoptag)) {
						routesForThisStop.add(routeName);
					}
				}
			}
		} catch (JSONException je) {
			// Do nothing. Just don't add any routes...
		}
		return routesForThisStop;

	}

}
