package com.doug.nextbus.backend;

import com.doug.nextbus.backend.RouteDataGSON.Route;
import com.doug.nextbus.backend.RouteDataGSON.Route.Direction;
import com.doug.nextbus.backend.RouteDataGSON.Route.Stop;

/** Stores a specific route, direction, and stop */
public class RouteDirectionStop implements Comparable<RouteDirectionStop> {
	final public Route route;
	final public Direction direction;
	final public Stop stop;

	public RouteDirectionStop(Route route, Direction direction, Stop stop) {
		this.route = route;
		this.direction = direction;
		this.stop = stop;
	}

	@Override
	public String toString() {
		if (route.direction.size() > 1)
			return route.title + " (" + direction.title + ")";
		else
			return route.title;
	}

	@Override
	public int compareTo(RouteDirectionStop another) {
		return toString().compareTo(another.toString());
	}

}
