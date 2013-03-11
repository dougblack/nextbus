package com.doug.nextbus.custom;

import java.util.Locale;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.doug.nextbus.R;
import com.doug.nextbus.backend.Data;
import com.doug.nextbus.backend.RouteDirectionStop;

public class ArrivalsAdapter extends BaseAdapter {

	private final Context mCtx;
	private final RouteDirectionStop[] mRdsArray;

	public ArrivalsAdapter(Context ctx, RouteDirectionStop[] rdsArray) {
		this.mCtx = ctx;
		this.mRdsArray = rdsArray;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;

		if (convertView == null) {
			vi = View.inflate(mCtx, R.layout.arrival_row, null);
		}

		TextView routeFavView = (TextView) vi.findViewById(R.id.routeFavView);
		TextView directionFavView = (TextView) vi
				.findViewById(R.id.directionFavView);

		// If no rds, then return the dead cell
		if (mRdsArray.length == 0) {
			// vi.setBackgroundDrawable(mCtx.getResources().getDrawable(
			// R.drawable.deadcell));
			routeFavView.setText("");
			directionFavView.setText("None");
			return vi;
		}

		RouteDirectionStop rds = mRdsArray[position];

		int routeColor = Data.getColorFromRouteTag(rds.route.tag);
		routeFavView.setTextColor(mCtx.getResources().getColor(routeColor));

		routeFavView.setText(rds.route.tag.substring(0, 1).toUpperCase(
				Locale.ENGLISH));
		directionFavView.setText(Data.capitalize(rds.direction.title));
		// Drawable drawable = Data.getDrawableForRouteTag(rds.route.tag);
		// vi.setBackgroundDrawable(drawable);

		return vi;
	}

	@Override
	public int getCount() {
		if (mRdsArray.length != 0)
			return mRdsArray.length;
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
