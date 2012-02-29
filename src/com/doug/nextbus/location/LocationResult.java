package com.doug.nextbus.location;

import android.location.Location;

public interface LocationResult {
	public abstract void gotLocation(Location location);
}