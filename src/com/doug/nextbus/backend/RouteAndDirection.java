package com.doug.nextbus.backend;

import com.doug.nextbus.backend.DataResult.Route;
import com.doug.nextbus.backend.DataResult.Route.Direction;

public class RouteAndDirection implements Comparable<RouteAndDirection> {

	public Route route;
	public Direction direction;

	public RouteAndDirection(Route route, Direction direction) {
		super();
		this.route = route;
		this.direction = direction;
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
