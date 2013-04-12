package com.doug.nextbus.custom;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Locale;

import android.content.Context;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.doug.nextbus.R;
import com.doug.nextbus.backend.Data;
import com.doug.nextbus.backend.Favorite;
import com.doug.nextbus.backend.FavoritesGSON;
import com.google.gson.Gson;

public class FavoritesAdapter extends BaseAdapter {
	private static FavoritesGSON sFavorites;

	private Context mCtx;

	public FavoritesAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	@Override
	public View getView(int position, View vi, ViewGroup parent) {
		if (vi == null) {
			vi = View.inflate(mCtx, R.layout.row_favorite, null);
		}

		TextView routeFavView = (TextView) vi.findViewById(R.id.routeFavView);
		TextView directionFavView = (TextView) vi
				.findViewById(R.id.directionFavView);
		TextView stopFavView = (TextView) vi.findViewById(R.id.stopFavView);

		Favorite favorite = getFavorite(position);
		// vi.setBackgroundDrawable(Data.getDrawableForRouteTag(favorite.routeTag));

		routeFavView.setText(favorite.routeTag.substring(0, 1).toUpperCase(
				Locale.US));
		directionFavView.setText(favorite.directionTitle);
		stopFavView.setText(favorite.stopTitle);

		int routeColor = Data
				.getColorFromRouteTag(getFavorite(position).routeTag);
		routeFavView.setTextColor(mCtx.getResources().getColor(routeColor));

		boolean showActiveRoutes = PreferenceManager
				.getDefaultSharedPreferences(mCtx).getBoolean(
						Data.SHOW_ACTIVE_ROUTES_PREF, false);

		// Gray out routes that are not active if showActiveRoutes is set
		int color = mCtx.getResources().getColor(R.color.white);
		// default is white

		if (showActiveRoutes && !Data.isRouteActive(favorite.routeTag)) {
			color = mCtx.getResources().getColor(R.color.fade3);
		}

		// routeFavView.setTextColor(color);
		directionFavView.setTextColor(color);
		stopFavView.setTextColor(color);

		return vi;
	}

	@Override
	public int getCount() {
		return getFavoritesSize();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public boolean toggleFavorite(Favorite favorite) {
		if (sFavorites == null)
			loadFavoritesData();
		boolean ret = sFavorites.toggleFavorite(favorite);
		saveFavoriteData();
		return ret;

	}

	private void loadFavoritesData() {
		try {
			FileInputStream fis = mCtx.openFileInput("favorites.txt");
			Reader reader = new InputStreamReader(fis);
			sFavorites = new Gson().fromJson(reader, FavoritesGSON.class);
		} catch (Exception e) {
			System.out.println(e);
			sFavorites = new FavoritesGSON();
		}
	}

	private void saveFavoriteData() {
		try {
			sFavorites.sort();
			String toSave = new Gson().toJson(sFavorites);
			FileOutputStream fos = mCtx.openFileOutput("favorites.txt",
					Context.MODE_PRIVATE);
			fos.write(toSave.getBytes());
			fos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getFavoritesSize() {
		if (sFavorites == null)
			loadFavoritesData();
		return sFavorites.getSize();
	}

	public Favorite getFavorite(int index) {
		if (sFavorites == null)
			loadFavoritesData();
		return sFavorites.getFavorite(index);
	}

	public void updateFavorites() {
		loadFavoritesData();
	}
}
