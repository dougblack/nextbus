package com.doug.nextbus.backend;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Locale;

import com.doug.nextbus.R;
import com.doug.nextbus.activities.StopViewActivity;
import com.doug.nextbus.backend.RouteDataGSON.Route;
import com.doug.nextbus.backend.RouteDataGSON.Route.Direction;
import com.doug.nextbus.backend.RouteDataGSON.Route.PathStop;
import com.doug.nextbus.backend.RouteDataGSON.Route.Stop;
import com.doug.nextbus.custom.RoutePagerAdapter;
import com.google.gson.Gson;

/**
 * This class controls reading and writing local files as well as persisting
 * current state data. This class is a catch all for the methods I need, I
 * usually come and clean this up a great deal
 */

public class Data {

	/** Used for JSON parsing */
	private static RouteDataGSON sRouteDataGSON;
	/** Key: routeTag, Value: Route object */
	final private static HashMap<String, Route> sRouteData;
	/** Key: stopTitle, Value: RouteDirectionStop objects that share the stop */
	final private static HashMap<String, HashSet<RouteDirectionStop>> sSharedStops;

	public static HashMap<String, Route> getRouteData() {
		if (sRouteData.size() == 0) {
			InputStream is = (InputStream) RoutePagerAdapter.mCtx
					.getResources().openRawResource(R.raw.routeconfig);
			Data.readData(is);
		}
		return sRouteData;
	}

	public static HashMap<String, HashSet<RouteDirectionStop>> getSharedStops() {
		if (sSharedStops.size() == 0) {
			InputStream is = (InputStream) StopViewActivity.mCtx.getResources()
					.openRawResource(R.raw.routeconfig);
			Data.readData(is);
		}

		return sSharedStops;
	}

	/** For holding the favorites */

	public static final String SHOW_ACTIVE_ROUTES_PREF;
	public static final String[] DEFAULT_ALL_ROUTES;

	static {
		sRouteData = new HashMap<String, Route>();
		sSharedStops = new HashMap<String, HashSet<RouteDirectionStop>>();
		DEFAULT_ALL_ROUTES = new String[] { "blue", "red", "trolley", "night",
				"green", "emory" };
		SHOW_ACTIVE_ROUTES_PREF = "showActiveRoutes";
	}

	/** Loads route information and populates the necessary data structures */
	public static void readData(InputStream is) {

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

	public static boolean isRouteActive(String routeTag) {
		String[] activeRoutes = APIController.getActiveRoutesList();
		return isInArray(activeRoutes, routeTag);
	}

	public static boolean isInArray(String[] arr, String str) {
		for (String route : arr) {
			if (route.equals(str))
				return true;
		}
		return false;
	}

}
