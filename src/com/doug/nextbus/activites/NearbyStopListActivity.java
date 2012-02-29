package com.doug.nextbus.activites;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.doug.nextbus.R;
import com.doug.nextbus.R.color;
import com.doug.nextbus.R.drawable;
import com.doug.nextbus.R.id;
import com.doug.nextbus.R.layout;
import com.doug.nextbus.backend.APIController;
import com.doug.nextbus.backend.Data;
import com.doug.nextbus.custom.RainbowArrayAdapter;
import com.doug.nextbus.location.LocationController;
import com.doug.nextbus.location.LocationResult;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class NearbyStopListActivity extends Activity implements LocationResult {

	ListView nearestStopsList;
	ProgressBar gpsProgressBar;
	LocationController locationController;
	ArrayList<String> routeOrder;
	ArrayList<Drawable> routeDrawables;
	ArrayList<String> stopidOrder;
	ArrayList<String> titlesOrder;
	static NearbyStopListActivity thisActivity;

	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.nearby_stop_list);
		locationController = new LocationController();
		nearestStopsList = (ListView) findViewById(R.id.nearestStopsList);
		gpsProgressBar = (ProgressBar) findViewById(R.id.gpsProgressBar);
		locationController.getLocation(this.getApplicationContext(), this);
		thisActivity = this;

	}

	
	public void gotLocation(Location location) {

		if (location != null) {
			Log.i("INFO", "Got location.");

			gpsProgressBar.setVisibility(View.INVISIBLE);

			Object[] ret = APIController.findNearestStops(location);
			HashMap<String, String> titlesHash = (HashMap<String, String>) ret[0];
			TreeMap<String, LinkedList<String>> distanceTM = (TreeMap<String, LinkedList<String>>) ret[1];
			HashMap<String, LinkedList<String>> routesHash = (HashMap<String, LinkedList<String>>) ret[2];
			titlesOrder = new ArrayList<String>();
			stopidOrder = new ArrayList<String>();
			routeDrawables = new ArrayList<Drawable>();
			routeOrder = new ArrayList<String>();
			ArrayList<String> activeRoutes = RoutePickerActivity.getActiveRoutes();

			for (Entry<String, LinkedList<String>> entry : distanceTM.entrySet()) {
				ListIterator<String> distItr = entry.getValue().listIterator();
				while (distItr.hasNext()) {
					// for each stopId at a given distance
					String stopid = distItr.next();
					String title = titlesHash.get(stopid);
					ListIterator<String> routeItr = routesHash.get(stopid).listIterator();
					while (routeItr.hasNext()) {
						// for each route in given stopId
						String route = routeItr.next();
						if (activeRoutes.contains(route)) { // if the route is
															// active
							Drawable cellDrawable = null;
							if (route.equals("red")) {
								cellDrawable = getResources().getDrawable(R.drawable.redcell);
							} else if (route.equals("blue")) {
								cellDrawable = getResources().getDrawable(R.drawable.bluecell);
							} else if (route.equals("trolley")) {
								cellDrawable = getResources().getDrawable(R.drawable.yellowcell);
							} else if (route.equals("green")) {
								cellDrawable = getResources().getDrawable(R.drawable.greencell);
							} else if (route.equals("night")) {
								cellDrawable = getResources().getDrawable(R.drawable.nightcell);
							}
							titlesOrder.add(title);
							routeDrawables.add(cellDrawable);
							routeOrder.add(route);
							stopidOrder.add(stopid);
						}
					}
				}
			}

			String[] titles = Data.convertToStringArray(titlesOrder);

			nearestStopsList.setAdapter(new RainbowArrayAdapter(this.getApplicationContext(),
					R.layout.customarrivallist, titles, routeDrawables, false));
			nearestStopsList.setOnItemClickListener(new OnItemClickListener() {

				
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

					Intent intent = new Intent(thisActivity.getApplicationContext(), StopViewActivity.class);
					intent.putExtra("stopid", stopidOrder.get(position));
					intent.putExtra("route", routeOrder.get(position));
					intent.putExtra("stop", titlesOrder.get(position));
					startActivity(intent);
				}

			});

			Log.i("INFO", "TitleOrder=" + titlesOrder.toString());
			Log.i("INFO", "RouteOrder=" + routeOrder.toString());
			Log.i("INFO", "StopIdOrder=" + stopidOrder.toString());

		} else {
		}
	}

}