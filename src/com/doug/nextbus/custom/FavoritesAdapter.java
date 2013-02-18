package com.doug.nextbus.custom;

import android.content.Context;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.doug.nextbus.R;
import com.doug.nextbus.backend.Data;

public class FavoritesAdapter extends BaseAdapter {
	Context ctx;

	public FavoritesAdapter(Context ctx) {
		this.ctx = ctx;
	}

	@Override
	public int getCount() {
		return Data.getFavoritesSize();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View vi = convertView;
		if (convertView == null)
			vi = View.inflate(ctx, R.layout.favorite_row, null);

		String routeTag = Data.getFavorite(position).routeTag;

		vi.setBackgroundDrawable(Data.getDrawableForRouteTag(routeTag));

		TextView routeFavView = (TextView) vi.findViewById(R.id.routeFavView);
		TextView directionFavView = (TextView) vi
				.findViewById(R.id.directionFavView);
		TextView stopFavView = (TextView) vi.findViewById(R.id.stopFavView);

		routeFavView
				.setText(Data.capitalize(Data.getFavorite(position).routeTag));
		directionFavView
				.setText(Data.capitalize(Data.getFavorite(position).directionTitle));
		stopFavView
				.setText(Data.capitalize(Data.getFavorite(position).stopTitle));

		if (PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(
				"showActiveRoutes", false)
				&& !Data.isRouteActive(routeTag)) {
			routeFavView.setTextColor(ctx.getResources()
					.getColor(R.color.fade2));
			directionFavView.setTextColor(ctx.getResources().getColor(
					R.color.fade2));
			stopFavView
					.setTextColor(ctx.getResources().getColor(R.color.fade2));
		}

		return vi;
	}
}
