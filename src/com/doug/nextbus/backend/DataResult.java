package com.doug.nextbus.backend;

import java.util.ArrayList;
import java.util.Hashtable;

public class DataResult {
	public ArrayList<Route> route;

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
