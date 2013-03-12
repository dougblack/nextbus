package com.doug.nextbus.custom;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.doug.nextbus.R;
import com.doug.nextbus.activities.StopListActivity;
import com.doug.nextbus.activities.StopViewActivity;
import com.doug.nextbus.backend.Data;
import com.doug.nextbus.backend.RouteDataGSON.Route;
import com.doug.nextbus.backend.RouteDataGSON.Route.Direction;
import com.doug.nextbus.backend.RouteDataGSON.Route.Stop;

/** The adapter for the swiping between route pages for the RoutePickerActivity */
public class RoutePagerAdapter extends PagerAdapter {

	private final Context mCtx;
	private String[] mRoutes;

	public RoutePagerAdapter(Context ctx, String[] routes) {
		this.mCtx = ctx;
		this.mRoutes = routes;
	}

	/** How to update the routes at runtime, calls notifyDataSetChanged() */
	public void updateRoutes(String[] routes) {
		this.mRoutes = routes;
		notifyDataSetChanged();
	}

	/*
	 * For each page, make a list view of available stops. Or, if there are
	 * directions, make a list view of available directions. Then launch the
	 * correct activity based on which item in the list view is selected.
	 */
	public Object instantiateItem(View container, int position) {

		// If there are not routes, return a cell saying there are no routes
		if (mRoutes.length == 0) {
			TextView noRoutes = new TextView(mCtx);
			noRoutes.setText("No active routes");
			noRoutes.setGravity(Gravity.CENTER);
			noRoutes.setTextSize(40);
			noRoutes.setTypeface(null, 1);
			noRoutes.setTextColor(mCtx.getResources().getColor(R.color.white));
			((ViewPager) container).addView(noRoutes, 0);
			return noRoutes;
		}

		// At this point there are routes, so list view as necessary
		final int listPosition = position;
		final String routeTag = mRoutes[listPosition];
		final Route currentRoute = Data.getRouteWithTag(routeTag);
		final boolean hasMultipleDirections = currentRoute
				.hasMultipleDirections();

		// For populating the list view
		String[] itemListTemp = new String[] {};
		if (hasMultipleDirections) {
			// Populate list with direction titles
			itemListTemp = currentRoute.getDirectionTitles();
		} else {
			// Populate list with stop titles
			itemListTemp = currentRoute.getStopTitles(currentRoute
					.getDefaultDirection().title);
		}

		final String[] itemList = itemListTemp;

		ListView itemListView = new ListView(mCtx);
		itemListView.setAdapter(new ArrayAdapter<String>(mCtx,
				android.R.layout.simple_list_item_1, itemList));
		itemListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (hasMultipleDirections) {
					/*
					 * Route has multiple directions, items start
					 * StopListActivity
					 */
					Intent intent = StopListActivity.createIntent(mCtx,
							routeTag, itemList[position]);
					mCtx.startActivity(intent);
				} else {
					/*
					 * Route only has one direction, items start
					 * StopViewActivity
					 */
					Direction defaultDirection = currentRoute
							.getDefaultDirection();
					Stop stop = currentRoute
							.getStopFromDefaultDirection(position);
					Intent intent = StopViewActivity.createIntent(mCtx,
							routeTag, defaultDirection, stop);
					mCtx.startActivity(intent);
				}
			}

		});

		((ViewPager) container).addView(itemListView, 0);
		return itemListView;

	}

	@Override
	public void destroyItem(View container, int position, Object view) {
		if (view instanceof ListView) {
			((ViewPager) container).removeView((ListView) view);
		} else {
			((ViewPager) container).removeView((TextView) view);
		}

	}

	@Override
	public int getCount() {
		if (mRoutes.length > 0) {
			return mRoutes.length;
		} else {
			return 1; // If no routes, return 1 for deadcell
		}
	}

	/** Necessary code to force the view to update */
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		if (object instanceof ListView) {
			return view == ((ListView) object);
		}
		return view == ((TextView) object);
	}

	@Override
	public String getPageTitle(int position) {
		if (mRoutes.length > 0) {
			return Data.capitalize(mRoutes[position]);
		} else {
			return "No routes"; // When no routes
		}
	}

}
