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
	final static String[] stringReturnType = {};
	private static HashMap<String, HashSet<RouteAndDirection>> similar;
	static {
		hm = new HashMap<String, Route>();
		similar = new HashMap<String, HashSet<RouteAndDirection>>();

	}

	/** Reads the data into memory */
	public static void setConfigData(Context context) {
		Data.context = context;
		ReadData();

	}

	public static RouteAndDirection[] getAllRoutesWithStopTitle(
			String stopTitle, String route, String directionTag) {
		ArrayList<RouteAndDirection> al = new ArrayList<RouteAndDirection>();
		Iterator<RouteAndDirection> iter = similar.get(stopTitle).iterator();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(Data.context);

		boolean onlyActiveRoutes = prefs.getBoolean("showActiveRoutes", false);
		String[] activeRoutes = RoutePickerActivity.allRoutes;
		if (onlyActiveRoutes)
			activeRoutes = APIController.getActiveRoutesList(Data.context);

		while (iter.hasNext()) {
			RouteAndDirection rad = iter.next();
			if ((rad.route.tag.equals(route) && rad.direction.tag
					.equals(directionTag))
					|| !checkInArray(activeRoutes, rad.route.tag))
				continue;
			al.add(rad);

		}

		Collections.sort(al);
		RouteAndDirection[] rads = {};
		return al.toArray(rads);
	}

	public static boolean checkInArray(String[] activeRoutes, String val) {
		for (String routes : activeRoutes) {
			if (routes.equals(val))
				return true;
		}
		return false;
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
		for (int i = 0; i < dataResult.route.size(); i++) {
			for (int j = 0; j < dataResult.route.get(i).stop.size(); j++) {
				Stop stop = dataResult.route.get(i).stop.get(j);
				if (dataResult.route.get(i).stopTable == null)
					dataResult.route.get(i).stopTable = new Hashtable<String, DataResult.Route.Stop>();
				dataResult.route.get(i).stopTable.put(stop.tag, stop);
			}

			hm.put(dataResult.route.get(i).tag, dataResult.route.get(i));
		}

		for (Route route : dataResult.route) {
			for (Direction direction : route.direction)
				for (PathStop pathStop : direction.stop) {
					Stop stop = Data.getStopObjFromRouteAndStopTag(route.tag,
							pathStop.tag);
					if (stop == null) {
						System.out.println("test");
					}
					HashSet<RouteAndDirection> hs = similar.get(stop.title) == null ? new HashSet<RouteAndDirection>()
							: similar.get(stop.title);
					hs.add(new RouteAndDirection(route, direction));
					similar.put(stop.title, hs);
				}

		}
		System.out.println("temp");

	}

	public static Route getRoute(String route) {
		return hm.get(route);
	}

	public static PathStop getPathStop(String route, String dir, int index) {
		for (Direction direction : hm.get(route).direction) {
			if (direction.title.equals(dir)) {
				return direction.stop.get(index);
			}
		}

		return null;
	}

	public static String[] getRouteTitlesFromStopTag(String stopTag) {
		ArrayList<String> al = new ArrayList<String>();

		for (Route currRoute : dataResult.route) {
			for (Direction currDirection : currRoute.direction) {
				for (PathStop currStop : currDirection.stop) {
					if (currStop.tag.equals(stopTag))
						al.add(currRoute.title);

				}
			}
		}

		return al.toArray(stringReturnType);

	}

	/** Returns the directions titles for a route */
	public static String[] getDirectionTitlesFromRoute(String route) {
		ArrayList<String> dirList = new ArrayList<String>();
		for (int i = 0; i < hm.get(route).direction.size(); i++)
			dirList.add(hm.get(route).direction.get(i).title);

		return dirList.toArray(stringReturnType);
	}

	public static String[] getStopTitlesFromRouteAndDir(String route, String dir) {
		Route curr_route = hm.get(route);
		ArrayList<String> al = new ArrayList<String>();
		for (int i = 0; i < curr_route.direction.size(); i++) {
			if (curr_route.direction.get(i).title.equals(dir)) {
				Direction currDirection = curr_route.direction.get(i);

				for (int j = 0; j < currDirection.stop.size(); j++) {
					PathStop pStop = currDirection.stop.get(j);
					Stop stop = curr_route.stopTable.get(pStop.tag);
					al.add(stop.title);
				}
			}
		}

		return al.toArray(stringReturnType);
	}

	public static String getStopTitleFromRouteAndStopTag(String route,
			String stopTag) {
		return hm.get(route).stopTable.get(stopTag).title;

	}

	public static Stop getStopObjFromRouteAndStopTag(String route,
			String stopTag) {
		return hm.get(route).stopTable.get(stopTag);

	}

	/** Gets direction tag from route and direction title */
	public static String getDirTagFromRouteAndDir(String route, String direction) {
		return getDirObjFromRouteAndDir(route, direction).tag;
	}

	public static Stop getStopFromDefaultDir(String route, int index) {
		PathStop pStop = hm.get(route).direction.get(0).stop.get(index);
		return getStopObjFromRouteAndStopTag(route, pStop.tag);
	}

	public static Direction getDirObjFromRouteAndDir(String route,
			String direction) {
		Route currRoute = hm.get(route);
		for (Direction dir : currRoute.direction) {
			if (dir.title.equals(direction))
				return dir;
		}
		// if can't find anything, return default
		return hm.get(route).getDefaultDirection();
	}

	public static Stop getStop(String route, String direction, int index) {
		PathStop pStop = Data.getPathStop(route, direction, index);
		return getStopObjFromRouteAndStopTag(route, pStop.tag);

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

}
