package com.doug.nextbus.backend;

import com.doug.nextbus.backend.DataResult.Route;
import com.doug.nextbus.backend.DataResult.Route.Direction;
import com.doug.nextbus.backend.DataResult.Route.Stop;

public class RouteAndDirection implements Comparable<RouteAndDirection> {

	public Route route;
	public Direction direction;
	public Stop stop;

	public RouteAndDirection(Route route, Direction direction, Stop stop) {
		super();
		this.route = route;
		this.direction = direction;
		this.stop = stop;
	}

	@Override
	public String toString() {
		return route.title + " (" + direction.title + ")";

	}

	@Override
	public int compareTo(RouteAndDirection another) {
		return toString().compareTo(another.toString());
	}

}
