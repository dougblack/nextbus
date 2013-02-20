package com.doug.nextbus.custom;

import android.content.Context;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.doug.nextbus.R;
import com.doug.nextbus.backend.Data;
import com.doug.nextbus.backend.Favorite;

public class FavoritesAdapter extends BaseAdapter {
	Context ctx;

	public FavoritesAdapter(Context ctx) {
		this.ctx = ctx;
	}

	@Override
	public View getView(int position, View vi, ViewGroup parent) {
		if (vi == null)
			vi = View.inflate(ctx, R.layout.favorite_row, null);

		Favorite favorite = Data.getFavorite(position);

		vi.setBackgroundDrawable(Data.getDrawableForRouteTag(favorite.routeTag));

		TextView routeFavView = (TextView) vi.findViewById(R.id.routeFavView);
		TextView directionFavView = (TextView) vi
				.findViewById(R.id.directionFavView);
		TextView stopFavView = (TextView) vi.findViewById(R.id.stopFavView);

		routeFavView.setText(Data.capitalize(favorite.routeTag));
		directionFavView.setText(favorite.directionTitle);
		stopFavView.setText(favorite.stopTitle);

		boolean showActiveRoutes = PreferenceManager
				.getDefaultSharedPreferences(ctx).getBoolean(
						"showActiveRoutes", false);

		// Gray out routes that are not active if showActiveRoutes is set
		int color = ctx.getResources().getColor(R.color.white);
		// default is white
		if (showActiveRoutes && !Data.isRouteActive(favorite.routeTag)) {
			color = ctx.getResources().getColor(R.color.fade2);
		}

		routeFavView.setTextColor(color);
		directionFavView.setTextColor(color);
		stopFavView.setTextColor(color);

		return vi;
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
}
