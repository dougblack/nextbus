package com.doug.nextbus.custom;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

/* The custom overaly corresponding to a bus arrow icon. */
public class BusOverlayItem extends OverlayItem {

	GeoPoint currentGeoPoint;
	String title;
	String snippet;
	GeoPoint oldGeoPoint;
	Point currentPoint;
	Point oldPoint;
	MapView mapView;
	Drawable defaultDrawable;
	Drawable marker;

	public BusOverlayItem(GeoPoint point, String title, String snippet, GeoPoint oldPoint, MapView mapView,
			Drawable defaultDrawable) {
		super(point, title, snippet);
		this.currentGeoPoint = point;
		this.title = title;
		this.snippet = snippet;
		this.oldGeoPoint = oldPoint;
		this.defaultDrawable = defaultDrawable;
		this.mapView = mapView;
	}

	public void rotateDrawable() {
		Projection projection = mapView.getProjection();
		currentPoint = new Point();
		oldPoint = new Point();
		projection.toPixels(currentGeoPoint, currentPoint);
		projection.toPixels(oldGeoPoint, oldPoint);

		double dlon = currentPoint.x - oldPoint.x;
		double dlat = currentPoint.y - oldPoint.y;
		double angle = Math.atan2(dlat, dlon);
		Bitmap oldBmp = ((BitmapDrawable) defaultDrawable).getBitmap();
		Bitmap canvasBitmap = oldBmp.copy(Bitmap.Config.ARGB_8888, true);
		canvasBitmap.eraseColor(0x00000000);

		Canvas tempCanvas = new Canvas(canvasBitmap);
		Matrix rotateMatrix = new Matrix();
		rotateMatrix.setRotate((float) Math.toDegrees(angle), tempCanvas.getWidth() / 2, tempCanvas.getHeight() / 2);
		tempCanvas.drawBitmap(oldBmp, rotateMatrix, null);
		BitmapDrawable tempBMD = new BitmapDrawable(canvasBitmap);
		tempBMD.setAntiAlias(true);
		marker = tempBMD;
		marker.setBounds(0, 0, defaultDrawable.getIntrinsicWidth(), defaultDrawable.getIntrinsicHeight());

	}

	@Override
	public Drawable getMarker(int stateBitset) {
		super.getMarker(stateBitset);

		return marker;

	}

}
