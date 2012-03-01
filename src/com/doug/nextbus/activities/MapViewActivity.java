package com.doug.nextbus.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
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
	Drawable drawable;
	MapItemizedOverlay itemizedOverlay;
	MapController mapController;
	ImageView mapRefreshButton;
	static ProgressBar pg;
	MapView mapView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_view);

		mapView = (MapView) findViewById(R.id.mapview);
		// pg = (ProgressBar) findViewById(R.id.mapprogressbar);
		// pg.setVisibility(View.INVISIBLE);
		mapRefreshButton = (ImageView) findViewById(R.id.mapRefreshButton);

		mapOverlays = mapView.getOverlays();
		mapController = mapView.getController();
		mapController.setZoom(16);
		drawable = this.getResources().getDrawable(R.drawable.ic_menu_compass);
		itemizedOverlay = new MapItemizedOverlay(drawable);

		GeoPoint centerPoint = new GeoPoint(33776381, -84399759);
		mapController.animateTo(centerPoint);

		mapRefreshButton.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View arg0, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					mapRefreshButton.setBackgroundColor(R.color.black);
					return true;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					mapRefreshButton.setBackgroundColor(0);
					refreshMap();
					return true;
				}
				return true;
			}
		});
		
		refreshMap();

	}
	
	public void refreshMap() {
		ArrayList<String[]> busLocations = APIController.getBusLocations();
		
		for(String[] entry : busLocations) {

			int latitude = (int)(Float.parseFloat(entry[2]) * 1e6);
			int longitude = (int)(Float.parseFloat(entry[3])* 1e6);
			GeoPoint busLocation = new GeoPoint(latitude, longitude);
			Log.i("INFO", ""+busLocation.getLatitudeE6()+"," + busLocation.getLongitudeE6());
			OverlayItem overlayItem = new OverlayItem(busLocation, "", "");
			itemizedOverlay.addOverlay(overlayItem);

		}

		mapOverlays.add(itemizedOverlay);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}