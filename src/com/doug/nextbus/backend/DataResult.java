package com.doug.nextbus.backend;

import java.util.ArrayList;
import java.util.Hashtable;

public class DataResult {
	public ArrayList<Route> route;
	final static String[] stringReturnType = {};

	public class Route {
		public String tag;
		public String title;
		// String latMin;
		// String latMax;
		// String lonMin;
		// String lonMax;

		public ArrayList<Stop> stop;
		public ArrayList<Direction> direction;
		/* for quick access. key: stop tag, value: Stop */
		public Hashtable<String, Stop> stopTagTable;

		public Direction getDefaultDirection() {
			return direction.get(0);
		}

		public Direction getDirection(String directionTitle) {
			for (Direction dir : this.direction) {
				if (dir.title.equals(directionTitle))
					return dir;
			}
			// if can't find anything, return default
			return getDefaultDirection();
		}

		public String[] getDirectionTitles() {
			ArrayList<String> dirTitles = new ArrayList<String>();
			for (Direction dir : direction)
				dirTitles.add(dir.title);
			return dirTitles.toArray(stringReturnType);
		}

		public Stop getStop(String stopTag) {
			return stopTagTable.get(stopTag);
		}

		public String[] getStopTitles(String directionTitle) {
			ArrayList<String> al = new ArrayList<String>();
			for (int i = 0; i < direction.size(); i++) {
				if (direction.get(i).title.equals(directionTitle)) {
					Direction currDirection = direction.get(i);

					for (int j = 0; j < currDirection.stop.size(); j++) {
						PathStop pStop = currDirection.stop.get(j);
						Stop stop = stopTagTable.get(pStop.tag);
						al.add(stop.title);
					}
				}
			}

			return al.toArray(stringReturnType);
		}

		public Stop getStop(String directionTitle, int index) {
			String stopTag = getPathStop(directionTitle, index).tag;
			return getStop(stopTag);

		}

		public Stop getStopFromDefaultDirection(int index) {
			String stopTag = getDefaultDirection().stop.get(index).tag;
			return getStop(stopTag);
		}

		public PathStop getPathStop(String directionTitle, int index) {
			for (Direction dir : direction) {
				if (dir.title.equals(directionTitle)) {
					return dir.stop.get(index);
				}
			}
			return null;
		}

		@Override
		public String toString() {
			return title + ": " + tag;
		}

		public class Direction {
			public String tag;
			public String title;
			public ArrayList<PathStop> stop;

			@Override
			public String toString() {
				return title + ": " + tag;
			}
		}

		public class PathStop {
			public String tag;
		}

		public class Stop {
			public String tag;
			public String title;
			// String lat;
			// String lon;
			public String stopid;

			@Override
			public String toString() {
				return title + ":  " + tag;
			}
		}
	}

}
