package com.doug.nextbus.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.doug.nextbus.R;
import com.doug.nextbus.backend.Data;
import com.doug.nextbus.backend.RouteDirectionStop;

public class ArrivalAdapter extends BaseAdapter {

	final private RouteDirectionStop[] rads;
	final private Context ctx;

	public ArrivalAdapter(Context ctx, RouteDirectionStop[] rads) {
		this.ctx = ctx;
		this.rads = rads;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;

		if (convertView == null) {
			vi = View.inflate(ctx, R.layout.arrival_row, null);
		}

		TextView routeFavView = (TextView) vi.findViewById(R.id.routeFavView);
		TextView directionFavView = (TextView) vi
				.findViewById(R.id.directionFavView);

		// If no RADs, then return the dead cell
		if (rads.length == 0) {
			vi.setBackgroundDrawable(ctx.getResources().getDrawable(
					R.drawable.deadcell));
			routeFavView.setText("None");
			directionFavView.setText("");
			return vi;
		}

		RouteDirectionStop rad = rads[position];
		routeFavView.setText(Data.capitalize(rad.route.tag));
		directionFavView.setText(Data.capitalize(rad.direction.title));
		Drawable drawable = Data.getDrawableForRouteTag(rad.route.tag);
		vi.setBackgroundDrawable(drawable);

		return vi;
	}

	@Override
	public int getCount() {
		if (rads.length != 0)
			return rads.length;
		else
			return 1;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}
