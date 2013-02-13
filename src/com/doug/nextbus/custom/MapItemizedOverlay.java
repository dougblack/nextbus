package com.doug.nextbus.custom;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class MapItemizedOverlay extends ItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();

	public MapItemizedOverlay(Drawable defaultMarker) {
		super(boundCenter(defaultMarker));
		populate();
	}

	@Override
	public int size() {
		return overlays.size();
	}

	public void addOverlay(OverlayItem overlay) {
		if (overlay instanceof BusOverlayItem) {
			((BusOverlayItem) overlay).rotateDrawable();
		}
		overlay.setMarker(boundCenter (overlay.getMarker(0)));
		overlays.add(overlay);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return overlays.get(i);
	}

	public void clear() {
		overlays.clear();
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		if (!shadow) {
			super.draw(canvas, mapView, false);
		}
	}

}
