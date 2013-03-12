package com.doug.nextbus.backend;

import java.util.ArrayList;
import java.util.Hashtable;

public class RouteDataGSON {
	protected ArrayList<Route> route;
	final static String[] stringReturnType = {};

	public class Route {
		public String tag;
		public String title;
		// String latMin;
		// String latMax;
		// String lonMin;
		// String lonMax;

		/** All of the stops in this route */
		protected ArrayList<Stop> stop;

		protected ArrayList<Direction> direction;

		/** key: stop tag, value: Stop */
		protected Hashtable<String, Stop> stopTagTable;

		public boolean hasMultipleDirections() {
			if (direction.size() > 1)
				return true;
			else
				return false;

		}

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
			ArrayList<String> directionTitles = new ArrayList<String>();
			for (Direction dir : direction)
				directionTitles.add(dir.title);
			return directionTitles.toArray(stringReturnType);
		}

		public Stop getStop(String stopTag) {
			return stopTagTable.get(stopTag);
		}

		/** Gets all the titles of stops for a given direction */
		public String[] getStopTitles(String directionTitle) {
			ArrayList<String> al = new ArrayList<String>();

			for (Direction dir : direction) {
				if (dir.title.equals(directionTitle)) {
					for (PathStop pathStop : dir.stop) {
						Stop stop = stopTagTable.get(pathStop.tag);
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
			protected String tag;
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
