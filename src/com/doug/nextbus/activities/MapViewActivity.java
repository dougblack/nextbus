package com.doug.nextbus.activities;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.doug.nextbus.R;
import com.doug.nextbus.old.APIController_Old;
import com.doug.nextbus.old.Data_Old;
import com.doug.nextbus.custom.BusOverlayItem;
import com.doug.nextbus.custom.MapItemizedOverlay;
import com.doug.nextbus.custom.RouteOverlay;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/*
 * The map activity. Shows routes and bus locations overlaid on a google map. 
 */
public class MapViewActivity extends MapActivity {

	/* Bus icon arrow things */
	Drawable redArrow;
	Drawable blueArrow;
	Drawable greenArrow;
	Drawable yellowArrow;
	Drawable purpleArrow;

	/* Bus icon arrow things in as map overlays */
	List<Overlay> mapOverlays;
	MapItemizedOverlay redOverlay;
	MapItemizedOverlay blueOverlay;
	MapItemizedOverlay greenOverlay;
	MapItemizedOverlay yellowOverlay;
	MapItemizedOverlay purpleOverlay;

	/* The buttons to control currently display route */
	TextView redButton;
	TextView blueButton;
	TextView greenButton;
	TextView yellowButton;

	/* The path segments for each bus route */
	List<Overlay> redPath;
	List<Overlay> bluePath;
	List<Overlay> greenPath;
	List<Overlay> yellowPath;

	/* Points used to move center the map view */
	GeoPoint centerPoint;
	GeoPoint yellowCenterPoint;

	MapController mapController;
	MapView mapView;

	static ProgressBar pg;
	static ImageView backButton;
	Handler refreshHandler;
	Runnable updateMapTask;
	String displayRoute;
	boolean routesAreSet;
	int whiteColor, redColor, blueColor, greenColor, yellowColor;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_view);

		Data_Old.setConfigData(this);

		/* Initialize tons of shit */
		redColor = this.getResources().getColor(R.color.red);
		blueColor = this.getResources().getColor(R.color.blue);
		greenColor = this.getResources().getColor(R.color.green);
		yellowColor = this.getResources().getColor(R.color.yellow);
		whiteColor = this.getResources().getColor(R.color.white);

		mapView = (MapView) findViewById(R.id.mapview);

		mapOverlays = mapView.getOverlays();
		mapController = mapView.getController();

		// Set map zoom. 17 is too close, 15 is too far.
		mapController.setZoom(16);

		redButton = (TextView) findViewById(R.id.redButton);
		blueButton = (TextView) findViewById(R.id.blueButton);
		greenButton = (TextView) findViewById(R.id.greenButton);
		yellowButton = (TextView) findViewById(R.id.yellowButton);
		backButton = (ImageView) findViewById(R.id.mapBackButton);

		redPath = new LinkedList<Overlay>();
		bluePath = new LinkedList<Overlay>();
		greenPath = new LinkedList<Overlay>();
		yellowPath = new LinkedList<Overlay>();
		routesAreSet = false;
		centerPoint = new GeoPoint(33776499, -84398400);
		yellowCenterPoint = new GeoPoint(33777390, -84393024);

		resetButtonTransparencies();
		displayRoute = "red";
		redButton.setBackgroundColor(getResources().getColor(R.color.black));
		redButton.setTextColor(redColor);

		/* Set listeners for each button to change the displayed route */
		redButton.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				resetButtonTransparencies();
				redButton.setBackgroundColor(getResources().getColor(
						R.color.black));
				redButton.setTextColor(redColor);
				mapController.animateTo(centerPoint);
				displayRoute = "red";
				refreshMap();
			}
		});
		blueButton.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				resetButtonTransparencies();
				blueButton.setBackgroundColor(getResources().getColor(
						R.color.black));
				blueButton.setTextColor(blueColor);
				mapController.animateTo(centerPoint);
				displayRoute = "blue";
				refreshMap();
			}
		});
		yellowButton.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				resetButtonTransparencies();
				yellowButton.setBackgroundColor(getResources().getColor(
						R.color.black));
				yellowButton.setTextColor(yellowColor);
				mapController.animateTo(yellowCenterPoint);
				displayRoute = "yellow";
				refreshMap();
			}
		});
		greenButton.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				resetButtonTransparencies();
				greenButton.setBackgroundColor(getResources().getColor(
						R.color.black));
				greenButton.setTextColor(greenColor);
				mapController.animateTo(centerPoint);
				displayRoute = "green";
				refreshMap();
			}
		});

		backButton.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View arg0, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					backButton.setBackgroundColor(getResources().getColor(
							R.color.black));
					return true;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					backButton.setBackgroundColor(0);
					finish();
					return true;
				}
				return true;
			}
		});

		/* Grab all the drawables for the bus dots. */
		redArrow = this.getResources().getDrawable(R.drawable.red_arrow);
		blueArrow = this.getResources().getDrawable(R.drawable.blue_arrow);
		greenArrow = this.getResources().getDrawable(R.drawable.green_arrow);
		yellowArrow = this.getResources().getDrawable(R.drawable.yellow_arrow);
		purpleArrow = this.getResources().getDrawable(R.drawable.purple_arrow);

		/* The map bus icons */
		purpleOverlay = new MapItemizedOverlay(purpleArrow);
		redOverlay = new MapItemizedOverlay(redArrow);
		blueOverlay = new MapItemizedOverlay(blueArrow);
		greenOverlay = new MapItemizedOverlay(greenArrow);
		yellowOverlay = new MapItemizedOverlay(yellowArrow);

		/* Center the map on Tech's campus */
		mapController.animateTo(centerPoint);

		refreshHandler = new Handler();

		/* Refresh bus locations every two seconds */
		updateMapTask = new Runnable() {
			public void run() { // Refreshes map every two seconds
				new refreshBusLocations().execute();
				refreshHandler.postDelayed(this, 5000);
			}
		};
	}

	/* Draw route lines. Only called once. */
	public void setRoutes() {

		Log.i("MapViewActivity", "Setting routes...");
		try {
			for (int x = 0; x < 4; x++) {
				JSONArray routePathData = null;

				/* Get path data for each route */
				switch (x) {
				case 0:
					routePathData = Data_Old.getRoutePathData("red");
					break;
				case 1:
					routePathData = Data_Old.getRoutePathData("blue");
					break;
				case 2:
					routePathData = Data_Old.getRoutePathData("trolley");
					break;
				case 3:
					routePathData = Data_Old.getRoutePathData("green");
					break;
				}

				GeoPoint oldPoint = null;
				GeoPoint newPoint = null;

				/* Draw route */
				for (int i = 0; i < routePathData.length(); i++) {

					JSONObject path = routePathData.getJSONObject(i);
					JSONArray points = path.getJSONArray("point");
					for (int j = 0; j < points.length(); j++) {

						JSONObject coords = points.getJSONObject(j);
						int latitude = (int) (Float.parseFloat(coords
								.getString("lat")) * 1e6);
						int longitude = (int) (Float.parseFloat(coords
								.getString("lon")) * 1e6);
						newPoint = new GeoPoint(latitude, longitude);
						if (oldPoint != null) {
							switch (x) {
							case 0:
								redPath.add(new RouteOverlay(oldPoint,
										newPoint, 2, Color
												.parseColor("#e63f3f")));
								break;
							case 1:
								bluePath.add(new RouteOverlay(oldPoint,
										newPoint, 2, Color
												.parseColor("#0078ff")));
								break;
							case 2:
								yellowPath.add(new RouteOverlay(oldPoint,
										newPoint, 2, Color
												.parseColor("#ffd200")));
								break;
							case 3:
								greenPath.add(new RouteOverlay(oldPoint,
										newPoint, 2, Color
												.parseColor("#02d038")));
								break;
							}

						}
						oldPoint = newPoint;
					}
					oldPoint = null;
				}
			}
		} catch (JSONException je) {
			/*
			 * Should never, ever happen. We control the JSON data so we know
			 * it's valid.
			 */
		}
		routesAreSet = true;
		Log.i("MapViewActivity", "Routes set.");
	}

	public void resetButtonTransparencies() {
		redButton.setBackgroundColor(0);
		redButton.setTextColor(whiteColor);
		greenButton.setBackgroundColor(0);
		greenButton.setTextColor(whiteColor);
		blueButton.setBackgroundColor(0);
		blueButton.setTextColor(whiteColor);
		yellowButton.setBackgroundColor(0);
		yellowButton.setTextColor(whiteColor);
	}

	/* Refreshes/redraws a route path */
	public void refreshMap() {
		mapOverlays.clear();

		if (displayRoute.equals("red")) {
			mapOverlays.add(redOverlay);
			mapOverlays.addAll(redPath);
		} else if (displayRoute.equals("blue")) {
			mapOverlays.add(blueOverlay);
			mapOverlays.addAll(bluePath);
		} else if (displayRoute.equals("yellow")) {
			mapOverlays.add(yellowOverlay);
			mapOverlays.addAll(yellowPath);
		} else if (displayRoute.equals("green")) {
			mapOverlays.add(greenOverlay);
			mapOverlays.addAll(greenPath);
		}

		mapView.postInvalidate();
	}

	/*
	 * This grabs the current bus locations from the m.gatech.edu URL and plots
	 * the color-coded locations of the buses. As mentioned above, this gets
	 * called every two seconds.
	 */

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	/*
	 * Stop updating location when paused.
	 */
	@Override
	public void onPause() {
		super.onPause();
		refreshHandler.removeCallbacks(updateMapTask);
	}

	/*
	 * Resume updating location when resumed.
	 */
	@Override
	public void onResume() {
		super.onResume();
		refreshHandler.post(updateMapTask);
	}

	/* Refresh bus locations. Implemented as AsyncTask so UI thread is free. */
	private class refreshBusLocations extends
			AsyncTask<Void, Void, ArrayList<MapItemizedOverlay>> {

		protected ArrayList<MapItemizedOverlay> doInBackground(Void... voids) {

			if (!routesAreSet) {
				setRoutes();
			}

			/* Here's where the actual location request happens. */
			ArrayList<String[]> busLocations = APIController_Old.getBusLocations();

			ArrayList<MapItemizedOverlay> overlays = new ArrayList<MapItemizedOverlay>();

			/*
			 * We create overlays for every route, so that the user can switch
			 * to new routes instantaneously without having to wait for an api
			 * request to return
			 */
			MapItemizedOverlay backgroundPurpleOverlay = new MapItemizedOverlay(
					purpleArrow);
			MapItemizedOverlay backgroundRedOverlay = new MapItemizedOverlay(
					redArrow);
			MapItemizedOverlay backgroundGreenOverlay = new MapItemizedOverlay(
					greenArrow);
			MapItemizedOverlay backgroundYellowOverlay = new MapItemizedOverlay(
					yellowArrow);
			MapItemizedOverlay backgroundBlueOverlay = new MapItemizedOverlay(
					blueArrow);

			overlays.add(backgroundPurpleOverlay);
			overlays.add(backgroundRedOverlay);
			overlays.add(backgroundBlueOverlay);
			overlays.add(backgroundYellowOverlay);
			overlays.add(backgroundGreenOverlay);

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

					if (route.equals("red")) {
						busOverlayItem = new BusOverlayItem(busLocation, "",
								"", pBusLocation, mapView, redArrow);
						backgroundRedOverlay.addOverlay(busOverlayItem);
					} else if (route.equals("blue")) {
						busOverlayItem = new BusOverlayItem(busLocation, "",
								"", pBusLocation, mapView, blueArrow);
						backgroundBlueOverlay.addOverlay(busOverlayItem);
					} else if (route.equals("yellow")) {
						busOverlayItem = new BusOverlayItem(busLocation, "",
								"", pBusLocation, mapView, yellowArrow);
						backgroundYellowOverlay.addOverlay(busOverlayItem);
					} else if (route.equals("green")) {
						busOverlayItem = new BusOverlayItem(busLocation, "",
								"", pBusLocation, mapView, greenArrow);
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

		/* Return to UI thread */
		public void onPostExecute(ArrayList<MapItemizedOverlay> overlays) {

			purpleOverlay = overlays.get(0);
			redOverlay = overlays.get(1);
			blueOverlay = overlays.get(2);
			yellowOverlay = overlays.get(3);
			greenOverlay = overlays.get(4);

			refreshMap();
			Log.v("RefreshMap", "Map refreshed");
		}

	}

}
