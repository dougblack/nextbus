package com.doug.nextbus.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.doug.nextbus.R;
import com.doug.nextbus.activities.RoutePickerActivity;
import com.doug.nextbus.backend.DataResult.Route;
import com.doug.nextbus.backend.DataResult.Route.Direction;
import com.doug.nextbus.backend.DataResult.Route.PathStop;
import com.doug.nextbus.backend.DataResult.Route.Stop;
import com.google.gson.Gson;

/* This class controls reading and writing local files as well as persisting current state data. */
public class Data {

	private static Context context;
	private static DataResult dataResult;
	final private static HashMap<String, Route> hm;
	private static HashMap<String, HashSet<RouteAndDirection>> sharedStops;

	final static String[] stringReturnType = {};
	static {
		hm = new HashMap<String, Route>();
		sharedStops = new HashMap<String, HashSet<RouteAndDirection>>();
	}

	/** Reads the data into memory */
	public static void setConfigData(Context context) {
		Data.context = context;
		if (dataResult == null)
			ReadData();
	}

	public static void ReadData() {
		InputStream is = (InputStream) context.getResources().openRawResource(
				R.raw.routeconfig);
		Reader reader = new InputStreamReader(is);
		try {
			dataResult = new Gson().fromJson(reader, DataResult.class);
		} catch (Exception e) {
			System.out.println(e);
		}

		for (Route route : dataResult.route) {
			for (Stop stop : route.stop) {
				if (route.stopTagTable == null)
					route.stopTagTable = new Hashtable<String, DataResult.Route.Stop>();
				route.stopTagTable.put(stop.tag, stop);
			}
			hm.put(route.tag, route);

			for (Direction direction : route.direction)
				for (PathStop pathStop : direction.stop) {

					Stop stop = route.getStop(pathStop.tag);

					HashSet<RouteAndDirection> hs = sharedStops.get(stop.title) == null ? new HashSet<RouteAndDirection>()
							: sharedStops.get(stop.title);
					hs.add(new RouteAndDirection(route, direction, stop));
					sharedStops.put(stop.title, hs);
				}
		}

	}

	public static RouteAndDirection[] getAllRadsWithStopTitle(String stopTitle,
			String route, String directionTag) {
		ArrayList<RouteAndDirection> radsList = new ArrayList<RouteAndDirection>();

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(Data.context);
		boolean onlyActiveRoutes = prefs.getBoolean("showActiveRoutes", false);

		// Get the default list of routes and overwrite if active routes is true
		String[] activeRoutes = RoutePickerActivity.allRoutes;
		if (onlyActiveRoutes)
			activeRoutes = APIController.getActiveRoutesList(Data.context);

		/*
		 * Iterate through route and direction if the rad matches the given
		 * route/direction or is not in the activeRoutes the continue
		 */
		Iterator<RouteAndDirection> iter = sharedStops.get(stopTitle)
				.iterator();
		while (iter.hasNext()) {
			RouteAndDirection rad = iter.next();
			if ((rad.route.tag.equals(route) && rad.direction.tag
					.equals(directionTag))
					|| notInArray(activeRoutes, rad.route.tag))
				continue;
			radsList.add(rad);
		}

		// Sorting to put the reds, blues, etc together
		Collections.sort(radsList);
		RouteAndDirection[] rads = {};
		return radsList.toArray(rads);
	}

	private static boolean notInArray(String[] activeRoutes, String val) {
		for (String routes : activeRoutes) {
			if (routes.equals(val))
				return false;
		}
		return true;
	}

	public static Route getRouteWithTag(String routeTag) {
		return hm.get(routeTag);
	}

	public static int getColorFromRouteTag(String routeTag) {
		int color = 0;
		if (routeTag.equals("red")) {
			color = R.color.red;
		} else if (routeTag.equals("blue")) {
			color = R.color.blue;
		} else if (routeTag.equals("green")) {
			color = R.color.green;
		} else if (routeTag.equals("trolley")) {
			color = R.color.yellow;
		} else if (routeTag.equals("night")) {
			color = R.color.night;
		} else if (routeTag.equals("emory")) {
			color = R.color.pink;
		}
		return color;
	}

	/* Capitalize a string */
	public static String capitalize(String route) {

		char[] chars = route.toLowerCase(Locale.getDefault()).toCharArray();
		boolean found = false;
		for (int i = 0; i < chars.length; i++) {
			if (!found && Character.isLetter(chars[i])) {
				if (i > 0 && Character.isDigit(chars[i - 1])) {
					found = true;
				} else {
					chars[i] = Character.toUpperCase(chars[i]);
					found = true;
				}
			} else if (Character.isWhitespace(chars[i]) || chars[i] == '.'
					|| chars[i] == '\'') {
				found = false;
			}
		}
		return String.valueOf(chars);
	}

	/* Reads the path data for a given route */
	public static JSONArray getRoutePathData(String route) {

		InputStream is = null;

		if (route.equals("red")) {
			is = (InputStream) context.getResources().openRawResource(
					R.raw.redroute);
		} else if (route.equals("blue")) {
			is = (InputStream) context.getResources().openRawResource(
					R.raw.blueroute);
		} else if (route.equals("green")) {
			is = (InputStream) context.getResources().openRawResource(
					R.raw.greenroute);
		} else if (route.equals("trolley")) {
			is = (InputStream) context.getResources().openRawResource(
					R.raw.trolleyroute);
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuffer lines = null;
		JSONObject routeData = null;
		JSONArray routePathData = null;
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
			routeData = new JSONObject(lines.toString());
			routePathData = routeData.getJSONObject("body")
					.getJSONObject("route").getJSONArray("path");
		} catch (JSONException e) {
			Log.e("ERROR", "Failed to make into JSON.");
		}

		return routePathData;

	}

	/* Make ArrayList of integers into an int array */
	private static int[] convertIntegers(List<Integer> integers) {
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

	private static Object convertToBooleanArray(ArrayList<Boolean> list) {
		boolean[] ret = new boolean[list.size()];
		Iterator<Boolean> iterator = list.iterator();
		for (int i = 0; i < ret.length; i++) {
			ret[i] = iterator.next();
		}
		return ret;
	}

}
