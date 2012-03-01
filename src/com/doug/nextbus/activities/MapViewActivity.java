package com.doug.nextbus.activities;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;

import com.doug.nextbus.R;
import com.doug.nextbus.backend.APIController;
import com.doug.nextbus.backend.BusOverlayItem;
import com.doug.nextbus.backend.MapItemizedOverlay;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MapViewActivity extends MapActivity {

	List<Overlay> mapOverlays;
	Drawable redArrow;
	Drawable blueArrow;
	Drawable greenArrow;
	Drawable yellowArrow;
	Drawable purpleArrow;
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
		redArrow = this.getResources().getDrawable(R.drawable.red_arrow);
		redOverlay = new MapItemizedOverlay(redArrow);

		blueArrow = this.getResources().getDrawable(R.drawable.blue_arrow);
		blueOverlay = new MapItemizedOverlay(blueArrow);
		greenArrow = this.getResources().getDrawable(R.drawable.green_arrow);
		greenOverlay = new MapItemizedOverlay(greenArrow);
		yellowArrow = this.getResources().getDrawable(R.drawable.yellow_arrow);
		yellowOverlay = new MapItemizedOverlay(yellowArrow);
		purpleArrow = this.getResources().getDrawable(R.drawable.purple_arrow);
		purpleOverlay = new MapItemizedOverlay(purpleArrow);

		// Center the map
		GeoPoint centerPoint = new GeoPoint(33776499, -84398400);
		mapController.animateTo(centerPoint);
		while (mapView.getProjection() == null) {
			Log.i(":LAKSJ", "STILL NULL");
			mapView.postInvalidate();
		}
		refreshHandler = new Handler();
		updateMapTask = new Runnable() {

			public void run() { // Refreshes map every two seconds
				Log.e("019823740192837401928374", "REFRESH CALLLEDDDDDDDD");
				new refreshBusLocations().execute();
				refreshHandler.postDelayed(this, 2000);

			}

		};

		refreshHandler.post(updateMapTask);

	}

	/**
	 * This grabs the current bus locations from the m.gatech.edu URL and plots
	 * the color-coded locations of the buses. As mentioned above, this gets
	 * called every two seconds.
	 */

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

	private class refreshBusLocations extends AsyncTask<Void, Void, ArrayList<MapItemizedOverlay>> {

		protected ArrayList<MapItemizedOverlay> doInBackground(Void... voids) {

			ArrayList<String[]> busLocations = APIController.getBusLocations();

			// clear all current overlays
			ArrayList<MapItemizedOverlay> overlays = new ArrayList<MapItemizedOverlay>();

			MapItemizedOverlay backgroundPurpleOverlay = new MapItemizedOverlay(purpleArrow);
			MapItemizedOverlay backgroundRedOverlay = new MapItemizedOverlay(redArrow);
			MapItemizedOverlay backgroundGreenOverlay = new MapItemizedOverlay(greenArrow);
			MapItemizedOverlay backgroundYellowOverlay = new MapItemizedOverlay(yellowArrow);
			MapItemizedOverlay backgroundBlueOverlay = new MapItemizedOverlay(blueArrow);

			for (String[] entry : busLocations) {

				try { // TODO - parseFloat fails sometimes?
					int latitude = (int) (Float.parseFloat(entry[2]) * 1e6);
					int longitude = (int) (Float.parseFloat(entry[3]) * 1e6);
					int platitude = (int) (Float.parseFloat(entry[4]) * 1e6);
					int plongitude = (int) (Float.parseFloat(entry[5]) * 1e6);
					GeoPoint busLocation = new GeoPoint(latitude, longitude);
					GeoPoint pBusLocation = new GeoPoint(platitude, plongitude);
					BusOverlayItem busOverlayItem;

					String route = entry[0];
					String[] currentRoutes = (String[]) (RoutePickerActivity.getActiveRoutesList())[0];

					// Add to the correct overlay.
					if (currentRoutes.length == 1 && currentRoutes[0].equals("night") && route.equals("red")) {
						busOverlayItem = new BusOverlayItem(busLocation, "", "", pBusLocation, mapView, purpleArrow);
						backgroundPurpleOverlay.addOverlay(busOverlayItem);
					}

					else if (route.equals("red")) {
						busOverlayItem = new BusOverlayItem(busLocation, "", "", pBusLocation, mapView, redArrow);
						backgroundRedOverlay.addOverlay(busOverlayItem);
					}

					else if (route.equals("blue")) {
						busOverlayItem = new BusOverlayItem(busLocation, "", "", pBusLocation, mapView, blueArrow);
						backgroundBlueOverlay.addOverlay(busOverlayItem);
					} else if (route.equals("yellow")) {
						busOverlayItem = new BusOverlayItem(busLocation, "", "", pBusLocation, mapView, yellowArrow);
						backgroundYellowOverlay.addOverlay(busOverlayItem);
					} else if (route.equals("green")) {
						busOverlayItem = new BusOverlayItem(busLocation, "", "", pBusLocation, mapView, greenArrow);
						backgroundGreenOverlay.addOverlay(busOverlayItem);
					}

				} catch (NumberFormatException nfe) {
					Log.e("RefreshMap", "Couldn't parse coordinates");
				}
			}
			
			overlays.add(backgroundPurpleOverlay);
			overlays.add(backgroundRedOverlay);
			overlays.add(backgroundBlueOverlay);
			overlays.add(backgroundYellowOverlay);
			overlays.add(backgroundGreenOverlay);

			return overlays;
		}
		
		public void onPostExecute(ArrayList<MapItemizedOverlay> overlays) {
			mapOverlays.clear();
			mapOverlays.add(overlays.get(0));
			mapOverlays.add(overlays.get(1));
			mapOverlays.add(overlays.get(2));
			mapOverlays.add(overlays.get(3));
			mapOverlays.add(overlays.get(4));
			mapView.postInvalidate();
			Log.v("RefreshMap", "Map refreshed");
		}

	}

}