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
import com.doug.nextbus.backend.RouteDataGSON.Route;
import com.doug.nextbus.backend.RouteDataGSON.Route.Direction;
import com.doug.nextbus.backend.RouteDataGSON.Route.PathStop;
import com.doug.nextbus.backend.RouteDataGSON.Route.Stop;
import com.google.gson.Gson;

/**
 * This class controls reading and writing local files as well as persisting
 * current state data. This class is a catch all for the methods I need, I
 * usually come and clean this up a great deal
 */

public class Data {

	private static Context sCtx;
	/** Used for JSON parsing */
	private static RouteDataGSON sRouteDataGSON;
	/** Key: routeTag, Value: Route object */
	final private static HashMap<String, Route> sRouteData;
	/** Key: stopTitle, Value: RouteDirectionStop objects that share the stop */
	final private static HashMap<String, HashSet<RouteDirectionStop>> sSharedStops;
	/** For holding the favorites */
	private static FavoritesGSON sFavorites;

	public static final String SHOW_ACTIVE_ROUTES_PREF;
	public static final String[] DEFAULT_ALL_ROUTES;

	static {
		sRouteData = new HashMap<String, Route>();
		sSharedStops = new HashMap<String, HashSet<RouteDirectionStop>>();
		DEFAULT_ALL_ROUTES = new String[] { "blue", "red", "trolley", "night",
				"green", "emory" };
		SHOW_ACTIVE_ROUTES_PREF = "showActiveRoutes";
	}

	/** Reads the data into memory if it already doesn't exist */
	public static void setConfig(Context ctx) {
		Data.sCtx = ctx;
		if (sRouteDataGSON == null)
			readData();
	}

	/** Loads route information and populates the necessary data structures */
	private static void readData() {
		InputStream is = (InputStream) sCtx.getResources().openRawResource(
				R.raw.routeconfig);
		Reader reader = new InputStreamReader(is);
		try {
			sRouteDataGSON = new Gson().fromJson(reader, RouteDataGSON.class);
		} catch (Exception e) {
			System.out.println(e);
		}

		for (Route route : sRouteDataGSON.route) {
			for (Stop stop : route.stop) {
				if (route.stopTagTable == null)
					route.stopTagTable = new Hashtable<String, RouteDataGSON.Route.Stop>();
				route.stopTagTable.put(stop.tag, stop);
			}
			sRouteData.put(route.tag, route);

			for (Direction direction : route.direction)
				for (PathStop pathStop : direction.stop) {

					Stop stop = route.getStop(pathStop.tag);

					if (sSharedStops.get(stop.title) == null) {
						sSharedStops.put(stop.title,
								new HashSet<RouteDirectionStop>());
					}

					sSharedStops.get(stop.title).add(
							new RouteDirectionStop(route, direction, stop));
				}
		}

	}

	/**
	 * Finds all route/direction/stops with that share the same stop title.
	 * Excludes Rds with given routeTag and directionTag
	 */
	public static RouteDirectionStop[] getAllRdsWithStopTitle(String stopTitle,
			String routeTag, String directionTag) {
		ArrayList<RouteDirectionStop> rdsList = new ArrayList<RouteDirectionStop>();

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(Data.sCtx);
		boolean onlyActiveRoutes = prefs.getBoolean(
				Data.SHOW_ACTIVE_ROUTES_PREF, false);

		// Get the default list of routes and overwrite if active routes is true
		String[] currentRoutes = Data.DEFAULT_ALL_ROUTES;
		if (onlyActiveRoutes)
			currentRoutes = APIController.getActiveRoutesList(Data.sCtx);

		/*
		 * Iterate through route and direction if the rds matches the given
		 * route/direction or is not in the activeRoutes then continue
		 */
		Iterator<RouteDirectionStop> iter = sSharedStops.get(stopTitle)
				.iterator();
		while (iter.hasNext()) {
			RouteDirectionStop rds = iter.next();
			if ((rds.route.tag.equals(routeTag) && rds.direction.tag
					.equals(directionTag))
					|| !isInArray(currentRoutes, rds.route.tag))
				continue;
			rdsList.add(rds);
		}

		// Sorting to put the blues, reds, etc together
		Collections.sort(rdsList);
		RouteDirectionStop[] rdsArray = {};
		return rdsList.toArray(rdsArray);
	}

	public static Route getRouteWithTag(String routeTag) {
		return sRouteData.get(routeTag);
	}

	public static int getColorFromRouteTag(String routeTag) {
		int color = R.color.blue; // default color
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
		int bg = R.drawable.cell_red; // default cell type
		if (routeTag.equals("red"))
			bg = R.drawable.cell_red;
		else if (routeTag.equals("blue"))
			bg = R.drawable.cell_blue;
		else if (routeTag.equals("green"))
			bg = R.drawable.cell_green;
		else if (routeTag.equals("trolley"))
			bg = R.drawable.cell_yellow;
		else if (routeTag.equals("emory"))
			bg = R.drawable.cell_pink;
		else if (routeTag.equals("night"))
			bg = R.drawable.cell_night;
		return sCtx.getResources().getDrawable(bg);

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

	/** Reads the path data for a given route */
	public static JSONArray getRoutePathData(String route) {

		InputStream is = null;

		if (route.equals("red")) {
			is = (InputStream) sCtx.getResources().openRawResource(
					R.raw.redroute);
		} else if (route.equals("blue")) {
			is = (InputStream) sCtx.getResources().openRawResource(
					R.raw.blueroute);
		} else if (route.equals("green")) {
			is = (InputStream) sCtx.getResources().openRawResource(
					R.raw.greenroute);
		} else if (route.equals("trolley")) {
			is = (InputStream) sCtx.getResources().openRawResource(
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

	public static boolean isRouteActive(String routeTag) {
		String[] activeRoutes = APIController.getActiveRoutesList(Data.sCtx);
		return isInArray(activeRoutes, routeTag);
	}

	public static boolean isInArray(String[] arr, String str) {
		for (String route : arr) {
			if (route.equals(str))
				return true;
		}
		return false;
	}

	public static boolean toggleFavorite(Favorite favorite) {
		if (Data.sFavorites == null)
			loadFavoritesData();
		boolean ret = sFavorites.toggleFavorite(favorite);
		saveFavoriteData();
		return ret;

	}

	private static void loadFavoritesData() {
		try {
			FileInputStream fis = sCtx.openFileInput("favorites.txt");
			Reader reader = new InputStreamReader(fis);
			Data.sFavorites = new Gson().fromJson(reader, FavoritesGSON.class);
		} catch (Exception e) {
			System.out.println(e);
			Data.sFavorites = new FavoritesGSON();
		}
	}

	private static void saveFavoriteData() {
		try {
			Data.sFavorites.sort();
			String toSave = new Gson().toJson(Data.sFavorites);
			FileOutputStream fos = Data.sCtx.openFileOutput("favorites.txt",
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
		if (Data.sFavorites == null)
			loadFavoritesData();
		return Data.sFavorites.contains(favorite);
	}

	public static int getFavoritesSize() {
		if (Data.sFavorites == null)
			loadFavoritesData();
		return Data.sFavorites.getSize();
	}

	public static Favorite getFavorite(int index) {
		if (Data.sFavorites == null)
			loadFavoritesData();
		return Data.sFavorites.getFavorite(index);
	}
}
