package com.doug.nextbus.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
		// TODO Auto-generated method stub
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
			vi = View
					.inflate(ctx, com.doug.nextbus.R.layout.favorite_row, null);

		String routeTag = Data.getFavorite(position).routeTag;

		int bg = R.drawable.redcell;
		if (routeTag.equals("red"))
			bg = R.drawable.redcell;
		else if (routeTag.equals("blue"))
			bg = R.drawable.bluecell;
		else if (routeTag.equals("green"))
			bg = R.drawable.greencell;
		else if (routeTag.equals("yellow"))
			bg = R.drawable.yellowcell;
		else if (routeTag.equals("emory"))
			bg = R.drawable.pinkcell;
		else if (routeTag.equals("night"))
			bg = R.drawable.nightcell;

		vi.setBackgroundDrawable(ctx.getResources().getDrawable(bg));

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
		return vi;
	}

}
