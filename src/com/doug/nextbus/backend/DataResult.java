package com.doug.nextbus.backend;

import java.util.ArrayList;
import java.util.Hashtable;

import com.doug.nextbus.backend.DataResult.Route.Direction;
import com.doug.nextbus.backend.DataResult.Route.PathStop;

public class DataResult {
	public ArrayList<Route> route;

	public String[] findRoutesWithStopTag(String stopTag) {
		ArrayList<String> al = new ArrayList<String>();

		for (Route currRoute : route) {
			for (Direction currDirection : currRoute.direction) {
				for (PathStop currStop : currDirection.stop) {
					if (currStop.tag.equals(stopTag))
						al.add(currRoute.title);

				}
			}
		}

		String[] strings = {};
		return al.toArray(strings);

	}

	public class Route {
		public String tag;
		public String title;
		// String latMin;
		// String latMax;
		// String lonMin;
		// String lonMax;

		public ArrayList<Stop> stop;
		public ArrayList<Direction> direction;
		public Hashtable<String, Stop> stopTable;

		{
			stopTable = new Hashtable<String, DataResult.Route.Stop>();
		}

		public PathStop getPathStopForDirandIndex(String dir, int index) {
			for (Direction direction : this.direction) {
				if (direction.title.equals(dir)) {
					return direction.stop.get(index);
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
