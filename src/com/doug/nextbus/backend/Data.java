package com.doug.nextbus.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.doug.nextbus.R;
import com.doug.nextbus.backend.DataResult.Route;
import com.doug.nextbus.backend.DataResult.Route.Direction;
import com.doug.nextbus.backend.DataResult.Route.PathStop;
import com.doug.nextbus.backend.DataResult.Route.Stop;
import com.google.gson.Gson;

/* This class controls reading and writing local files as well as persisting current state data. */
public class Data {

	static Context context;
	public static DataResult dataResult;
	public static HashMap<String, Route> hm;

	static {
		hm = new HashMap<String, Route>();
	}

	/**
	 * This read the text file and instantiates it as a static JSONObject, data.
	 * 
	 * @param context
	 *            application context
	 */
	public static void setConfigData(Context context) {

		setContext(context);

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
		for (int i = 0; i < dataResult.route.size(); i++) {
			for (int j = 0; j < dataResult.route.get(i).stop.size(); j++) {
				Stop stop = dataResult.route.get(i).stop.get(j);
				if (dataResult.route.get(i).stopTable == null)
					dataResult.route.get(i).stopTable = new Hashtable<String, DataResult.Route.Stop>();
				dataResult.route.get(i).stopTable.put(stop.tag, stop);
			}

			hm.put(dataResult.route.get(i).tag, dataResult.route.get(i));
		}

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

	private static void setContext(Context ctx) {
		Data.context = ctx;
	}

	public static String[] getDirList(String route) {
		ArrayList<String> dirList = new ArrayList<String>();
		for (int i = 0; i < hm.get(route).direction.size(); i++)
			dirList.add(hm.get(route).direction.get(i).title);
		String[] strings = {};

		return dirList.toArray(strings);
	}

	public static String[] getPath(String route, String dir) {
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
		String[] strings = {};

		return al.toArray(strings);
	}

	public static String getDirectionTag(String route, String direction) {
		Route currRoute = hm.get(route);
		for (Direction dir : currRoute.direction) {
			if (dir.title.equals(direction))
				return dir.tag;
		}
		return "Not Found";
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
			} else if (Character.isWhitespace(chars[i]) || chars[i] == '.'
					|| chars[i] == '\'') {
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

}
