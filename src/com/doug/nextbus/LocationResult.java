package com.doug.nextbus;

import android.location.Location;

public interface LocationResult {
	public abstract void gotLocation(Location location);
}