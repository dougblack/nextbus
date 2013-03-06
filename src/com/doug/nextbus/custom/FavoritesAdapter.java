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
	private Context mCtx;

	public FavoritesAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	@Override
	public View getView(int position, View vi, ViewGroup parent) {
		if (vi == null) {
			vi = View.inflate(mCtx, R.layout.favorite_row, null);
		}

		TextView routeFavView = (TextView) vi.findViewById(R.id.routeFavView);
		TextView directionFavView = (TextView) vi
				.findViewById(R.id.directionFavView);
		TextView stopFavView = (TextView) vi.findViewById(R.id.stopFavView);

		Favorite favorite = Data.getFavorite(position);
		//		vi.setBackgroundDrawable(Data.getDrawableForRouteTag(favorite.routeTag));

		routeFavView.setText(favorite.routeTag.substring(0, 1).toUpperCase());
		directionFavView.setText(favorite.directionTitle);
		stopFavView.setText(favorite.stopTitle);

		int routeColor = Data
				.getColorFromRouteTag(Data.getFavorite(position).routeTag);
		routeFavView.setTextColor(mCtx.getResources().getColor(routeColor));

		boolean showActiveRoutes = PreferenceManager
				.getDefaultSharedPreferences(mCtx).getBoolean(
						Data.SHOW_ACTIVE_ROUTES_PREF, false);

		// Gray out routes that are not active if showActiveRoutes is set
		int color = mCtx.getResources().getColor(R.color.white);
		// default is white

		if (showActiveRoutes && !Data.isRouteActive(favorite.routeTag)) {
			color = mCtx.getResources().getColor(R.color.fade2);
		}

		// routeFavView.setTextColor(color);
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
