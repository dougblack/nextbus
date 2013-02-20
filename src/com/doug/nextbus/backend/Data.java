package com.doug.nextbus.backend;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.Log;

import com.doug.nextbus.R;
import com.doug.nextbus.activities.RoutePickerActivity;
import com.doug.nextbus.backend.JSONDataResult.Route;
import com.doug.nextbus.backend.JSONDataResult.Route.Direction;
import com.doug.nextbus.backend.JSONDataResult.Route.PathStop;
import com.doug.nextbus.backend.JSONDataResult.Route.Stop;
import com.google.gson.Gson;

/**
 * This class controls reading and writing local files as well as persisting
 * current state data. This class is a catch all for the methods I need, I
 * usually come and clean this up a great deal
 */

public class Data {

	private static Context ctx;
	/** Used for JSON parsing */
	private static JSONDataResult jsonDataResult;
	/** Key: routeTag, Value: Route object */
	final private static HashMap<String, Route> hm;
	/** Key: stopTitle, Value: RouteDirectionStop objects that share the stop */
	final private static HashMap<String, HashSet<RouteDirectionStop>> sharedStops;
	/** For holding the favorites */
	private static Favorites favorites;

	final static String[] stringReturnType = {};

	static {
		hm = new HashMap<String, Route>();
		sharedStops = new HashMap<String, HashSet<RouteDirectionStop>>();
	}

	/** Reads the data into memory if it already doesn't exist */
	public static void setConfig(Context ctx) {
		Data.ctx = ctx;
		if (jsonDataResult == null)
			readData();
	}

	/** Loads route information and populates the necessary data structures */
	private static void readData() {
		InputStream is = (InputStream) ctx.getResources().openRawResource(
				R.raw.routeconfig);
		Reader reader = new InputStreamReader(is);
		try {
			jsonDataResult = new Gson().fromJson(reader, JSONDataResult.class);
		} catch (Exception e) {
			System.out.println(e);
		}

		for (Route route : jsonDataResult.route) {
			for (Stop stop : route.stop) {
				if (route.stopTagTable == null)
					route.stopTagTable = new Hashtable<String, JSONDataResult.Route.Stop>();
				route.stopTagTable.put(stop.tag, stop);
			}
			hm.put(route.tag, route);

			for (Direction direction : route.direction)
				for (PathStop pathStop : direction.stop) {

					Stop stop = route.getStop(pathStop.tag);

					if (sharedStops.get(stop.title) == null) {
						sharedStops.put(stop.title,
								new HashSet<RouteDirectionStop>());
					}

					sharedStops.get(stop.title).add(
							new RouteDirectionStop(route, direction, stop));
				}
		}

	}

	/** Finds all route/direction/stops with that share the same stop title */
	public static RouteDirectionStop[] getAllRdsWithStopTitle(String stopTitle,
			String routeTag, String directionTag) {
		ArrayList<RouteDirectionStop> rdsList = new ArrayList<RouteDirectionStop>();

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(Data.ctx);
		boolean onlyActiveRoutes = prefs.getBoolean("showActiveRoutes", false);

		// Get the default list of routes and overwrite if active routes is true
		String[] currentRoutes = RoutePickerActivity.defaultAllRoutes;
		if (onlyActiveRoutes)
			currentRoutes = APIController.getActiveRoutesList(Data.ctx);

		/*
		 * Iterate through route and direction if the rds matches the given
		 * route/direction or is not in the activeRoutes then continue
		 */
		Iterator<RouteDirectionStop> iter = sharedStops.get(stopTitle)
				.iterator();
		while (iter.hasNext()) {
			RouteDirectionStop rad = iter.next();
			if ((rad.route.tag.equals(routeTag) && rad.direction.tag
					.equals(directionTag))
					|| isNotInArray(currentRoutes, rad.route.tag))
				continue;
			rdsList.add(rad);
		}

		// Sorting to put the blues, reds, etc together
		Collections.sort(rdsList);
		RouteDirectionStop[] rads = {};
		return rdsList.toArray(rads);
	}

	/** Returns true if val is not in array, false it is in the array */
	private static boolean isNotInArray(String[] arr, String val) {
		for (String routes : arr) {
			if (routes.equals(val))
				return false;
		}
		return true;
	}

	public static Route getRouteWithTag(String routeTag) {
		return hm.get(routeTag);
	}

	public static int getColorFromRouteTag(String routeTag) {
		int color = 0; // default color
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

	public static Drawable getDrawableForRouteTag(String routeTag) {
		int bg = R.drawable.redcell; // default cell type
		if (routeTag.equals("red"))
			bg = R.drawable.redcell;
		else if (routeTag.equals("blue"))
			bg = R.drawable.bluecell;
		else if (routeTag.equals("green"))
			bg = R.drawable.greencell;
		else if (routeTag.equals("trolley"))
			bg = R.drawable.yellowcell;
		else if (routeTag.equals("emory"))
			bg = R.drawable.pinkcell;
		else if (routeTag.equals("night"))
			bg = R.drawable.nightcell;
		return ctx.getResources().getDrawable(bg);

	}

	/** Capitalize a string */
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
			is = (InputStream) ctx.getResources().openRawResource(
					R.raw.redroute);
		} else if (route.equals("blue")) {
			is = (InputStream) ctx.getResources().openRawResource(
					R.raw.blueroute);
		} else if (route.equals("green")) {
			is = (InputStream) ctx.getResources().openRawResource(
					R.raw.greenroute);
		} else if (route.equals("trolley")) {
			is = (InputStream) ctx.getResources().openRawResource(
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

	private static String[] convertToStringArray(ArrayList<String> list) {

		String[] ret = new String[list.size()];
		Iterator<String> iterator = list.iterator();
		for (int i = 0; i < ret.length; i++) {
			ret[i] = iterator.next();
		}
		return ret;

	}

	public static boolean isRouteActive(String routeTag) {
		String[] activeRoutes = APIController.getActiveRoutesList(Data.ctx);
		return !isNotInArray(activeRoutes, routeTag);
	}

	public static boolean toggleFavorite(Favorite favorite) {
		if (Data.favorites == null)
			loadFavoritesData();
		boolean ret = favorites.toggleFavorite(favorite);
		saveFavoriteData();
		return ret;

	}

	private static void loadFavoritesData() {
		try {
			FileInputStream fis = ctx.openFileInput("favorites.txt");
			Reader reader = new InputStreamReader(fis);
			Data.favorites = new Gson().fromJson(reader, Favorites.class);
		} catch (Exception e) {
			System.out.println(e);
		}
		Data.favorites = new Favorites();

	}

	private static void saveFavoriteData() {
		try {
			String toSave = new Gson().toJson(Data.favorites);
			FileOutputStream fos = Data.ctx.openFileOutput("favorites.txt",
					Context.MODE_PRIVATE);
			fos.write(toSave.getBytes());
			fos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean isFavorite(Favorite favorite) {
		if (Data.favorites == null)
			loadFavoritesData();
		return Data.favorites.contains(favorite);
	}

	public static int getFavoritesSize() {
		if (Data.favorites == null)
			loadFavoritesData();
		return Data.favorites.getSize();
	}

	public static Favorite getFavorite(int index) {
		if (Data.favorites == null)
			loadFavoritesData();
		return Data.favorites.getFavorite(index);
	}
}
