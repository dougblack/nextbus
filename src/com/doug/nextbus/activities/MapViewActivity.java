package com.doug.nextbus.activities;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.doug.nextbus.R;
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
	
	static ProgressBar pg;
	MapView mapView;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_view);
		
		mapView = (MapView) findViewById(R.id.mapview);
		pg = (ProgressBar) findViewById(R.id.mapprogressbar);
		pg.setVisibility(View.INVISIBLE);
		
		
		mapOverlays = mapView.getOverlays();
		mapController = mapView.getController();
		mapController.setZoom(17);
		drawable = this.getResources().getDrawable(R.drawable.ic_menu_compass);
		itemizedOverlay = new MapItemizedOverlay(drawable);
		
		GeoPoint centerPoint = new GeoPoint(33776381,-84399759);
		mapController.animateTo(centerPoint);
		
		
//		GeoPoint point = new GeoPoint(19240000, -99120000);
//		OverlayItem overlayItem = new OverlayItem(point, "", "");
//		itemizedOverlay.addOverlay(overlayItem);
//		mapOverlays.add(itemizedOverlay);
		
	}

	@Override
	protected boolean isRouteDisplayed() {		
		return false;
	}
	
}