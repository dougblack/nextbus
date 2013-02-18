package com.doug.nextbus.custom;

import java.util.ArrayList;

import com.doug.nextbus.backend.RouteDirectionStop;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/* Array adapter for multicolored cells */
public class OtherArrivalsArrayAdapter extends ArrayAdapter<String> {

	private ArrayList<Drawable> drawableList;
	boolean deadCellOnly;
	RouteDirectionStop[] rads;

	public OtherArrivalsArrayAdapter(Context context, int textViewResourceId,
			String[] data, ArrayList<Drawable> cellDrawables,
			boolean deadCellOnly, RouteDirectionStop[] rads) {

		super(context, textViewResourceId, data);
		drawableList = cellDrawables;
		this.deadCellOnly = deadCellOnly;
		this.rads = rads;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView view = (TextView) super.getView(position, convertView, parent);
		view.setBackgroundDrawable(drawableList.get(position));

		if (deadCellOnly) {
			Log.i("INFO", "DEAD CELL ONLY.");
			view.setGravity(Gravity.CENTER);
		} else {
			view.setText(Html.fromHtml(htmlFormatted(rads[position])));

		}
		return view;
	}

	private String htmlFormatted(RouteDirectionStop rad) {
		if (rad.route.hasManyDirections())
			return rad.route.title + " <small> <font color='#A0A0A0'> ("
					+ rad.direction.title + ") </font> </small>";
		else
			return rad.route.title;

	}
}
