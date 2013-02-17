package com.doug.nextbus.custom;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/* Array adapter for multicolored cells */
public class RainbowArrayAdapter extends ArrayAdapter<String> {

	String[] data;
	ArrayList<Drawable> drawableList;
	Context context;
	boolean deadCellOnly;

	public RainbowArrayAdapter(Context context, int textViewResourceId,
			String[] data, ArrayList<Drawable> cellDrawables,
			boolean deadCellOnly) {
		super(context, textViewResourceId, data);
		this.context = context;
		drawableList = cellDrawables;
		this.deadCellOnly = deadCellOnly;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView view = (TextView) super.getView(position, convertView, parent);
		view.setBackgroundDrawable(drawableList.get(position));
		if (deadCellOnly) {
			Log.i("INFO", "DEAD CELL ONLY.");
			view.setGravity(Gravity.CENTER);
		}
		return view;
	}

}
