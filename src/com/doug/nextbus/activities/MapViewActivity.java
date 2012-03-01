package com.doug.nextbus.activities;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;

import com.doug.nextbus.R;
import com.doug.nextbus.backend.APIController;
import com.doug.nextbus.backend.MapItemizedOverlay;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MapViewActivity extends MapActivity {

	List<Overlay> mapOverlays;
	Drawable redDot;
	Drawable blueDot;
	Drawable greenDot;
	Drawable yellowDot;
	Drawable purpleDot;
	MapItemizedOverlay redOverlay;
	MapItemizedOverlay blueOverlay;
	MapItemizedOverlay greenOverlay;
	MapItemizedOverlay yellowOverlay;
	MapItemizedOverlay purpleOverlay;
	MapController mapController;
	static ProgressBar pg;
	MapView mapView;
	Handler refreshHandler;
	Runnable updateMapTask;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_view);

		mapView = (MapView) findViewById(R.id.mapview);

		mapOverlays = mapView.getOverlays();
		mapController = mapView.getController();
		
		// Set map zoom. 15 is too close, 17 is too far.
		mapController.setZoom(16);
		
		// Grab all the drawables for the bus dots.
		redDot = this.getResources().getDrawable(R.drawable.red_dot);
		redOverlay = new MapItemizedOverlay(redDot);
		blueDot = this.getResources().getDrawable(R.drawable.blue_dot);
		blueOverlay = new MapItemizedOverlay(blueDot);
		greenDot = this.getResources().getDrawable(R.drawable.green_dot);
		greenOverlay = new MapItemizedOverlay(greenDot);
		yellowDot = this.getResources().getDrawable(R.drawable.yellow_dot);
		yellowOverlay = new MapItemizedOverlay(yellowDot);
		purpleDot = this.getResources().getDrawable(R.drawable.purple_dot);
		purpleOverlay = new MapItemizedOverlay(purpleDot);

		// Center the map
		GeoPoint centerPoint = new GeoPoint(33776381, -84399759);
		mapController.animateTo(centerPoint);
		
		refreshHandler = new Handler(); 
		updateMapTask = new Runnable() { 

			public void run() { // Refreshes map every two seconds
				refreshMap();
				refreshHandler.postDelayed(this, 2000);

			}

		};

		refreshHandler.post(updateMapTask);

	}

	/**
	 * This grabs the current bus locations from the m.gatech.edu URL and plots
	 * the color-coded locations of the buses. As mentioned above, this gets called
	 * every two seconds.
	 */
	public void refreshMap() {
		
		
		ArrayList<String[]> busLocations = APIController.getBusLocations();

		// clear all current overlays
		redOverlay.clear(); 
		blueOverlay.clear();
		yellowOverlay.clear();
		greenOverlay.clear();
		purpleOverlay.clear();

		for (String[] entry : busLocations) {

			try { // TODO - parseFloat fails sometimes?
				int latitude = (int) (Float.parseFloat(entry[2]) * 1e6);
				int longitude = (int) (Float.parseFloat(entry[3]) * 1e6);
				GeoPoint busLocation = new GeoPoint(latitude, longitude);
				
				OverlayItem overlayItem = new OverlayItem(busLocation, "", "");
	
				String route = entry[0];
				String[] currentRoutes = (String[]) (RoutePickerActivity.getActiveRoutesList())[0];
				
				// Add to the correct overlay.
				if (currentRoutes.length == 1 && currentRoutes[0].equals("night") && route.equals("red"))
					purpleOverlay.addOverlay(overlayItem);
				else if (route.equals("red"))
					redOverlay.addOverlay(overlayItem);
				else if (route.equals("blue"))
					blueOverlay.addOverlay(overlayItem);
				else if (route.equals("yellow"))
					yellowOverlay.addOverlay(overlayItem);
				else if (route.equals("green"))
					greenOverlay.addOverlay(overlayItem);
				
			} catch (NumberFormatException nfe) {
				Log.e("RefreshMap", "Couldn't parse coordinates");
			}

		}
		
		// Add all the drawable lists to the map.
		mapOverlays.add(redOverlay);
		mapOverlays.add(blueOverlay);
		mapOverlays.add(yellowOverlay);
		mapOverlays.add(greenOverlay);
		mapOverlays.add(purpleOverlay);
		mapView.postInvalidate();
		Log.v("RefreshMap", "Map refreshed");
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	/**
	 * Stop updating location when paused.
	 */
	public void onPause() {
		super.onPause();
		refreshHandler.removeCallbacks(updateMapTask);
	}
	
	/**
	 * Resume updating location when resumed.
	 */
	public void onResume() {
		super.onResume();
		refreshHandler.post(updateMapTask);
	}

}