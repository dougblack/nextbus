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
import com.doug.nextbus.backend.JSONDataResult.Route;
import com.doug.nextbus.backend.JSONDataResult.Route.Direction;
import com.doug.nextbus.backend.JSONDataResult.Route.Stop;

/** The adapter for the swiping between route pages for the RoutePickerActivity */
public class RoutePagerAdapter extends PagerAdapter {

	private String[] routes;
	private final Context ctx;

	public RoutePagerAdapter(String[] routes, Context ctx) {
		this.routes = routes;
		this.ctx = ctx;
	}

	/** How to update the routes at runtime, calls notifyDataSetChanged() */
	public void updateRoutes(String[] routes) {
		this.routes = routes;
		notifyDataSetChanged();
	}

	/*
	 * For each page, make a list view of available stops. Or, if there are
	 * directions, make a list view of available directions. Then launch the
	 * correct activity based on which item in the list view is selected.
	 */
	public Object instantiateItem(View container, int position) {

		// If there are not routes, return a cell saying there are no routes
		if (routes.length == 0) {
			TextView noRoutes = new TextView(ctx);
			noRoutes.setText("No active routes");
			noRoutes.setGravity(Gravity.CENTER);
			noRoutes.setTextSize(40);
			noRoutes.setTypeface(null, 1);
			noRoutes.setTextColor(ctx.getResources().getColor(R.color.white));
			((ViewPager) container).addView(noRoutes, 0);
			return noRoutes;
		}

		// At this point there are routes, so list view as necessary
		final int listPosition = position;
		final String routeTag = routes[listPosition];
		final Route currentRoute = Data.getRouteWithTag(routeTag);
		final boolean hasMultipleDirections = currentRoute.hasManyDirections();

		// For populating the list view
		String[] itemListTemp = new String[] {};
		if (hasMultipleDirections) {
			// Populate list with direction titles
			itemListTemp = currentRoute.getDirectionTitles();
		} else {
			// Populate with stop titles
			itemListTemp = currentRoute.getStopTitles(currentRoute
					.getDefaultDirection().title);
		}

		final String[] itemList = itemListTemp;

		ListView itemListView = new ListView(ctx);

		itemListView.setAdapter(new ArrayAdapter<String>(ctx,
				android.R.layout.simple_list_item_1, itemList));
		itemListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (hasMultipleDirections) {
					/*
					 * Route has direction so items have to point to
					 * StopListActivity with correct extras.
					 */
					Intent intent = StopListActivity.createIntent(ctx,
							routeTag, itemList[position]);
					ctx.startActivity(intent);
				} else {
					/*
					 * Route doesn't have direction so items have to point to
					 * StopViewActivity with correct extras.
					 */
					Direction defaultDirection = currentRoute
							.getDefaultDirection();
					Stop stop = currentRoute
							.getStopFromDefaultDirection(position);
					Intent intent = StopViewActivity.createIntent(ctx,
							routeTag, defaultDirection, stop);

					ctx.startActivity(intent);
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
		if (routes.length > 0) {
			return routes.length;
		} else {
			return 1;
		}
	}

	/* Necessary code to force the view to update */
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
		if (routes.length > 0) {
			return Data.capitalize(routes[position]);
		} else {
			return "No routes";
		}
	}

}
