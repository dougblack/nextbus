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
import com.doug.nextbus.backend.DataResult.Route;

/* The adapter for the swiping between route pages for the RoutePickerActivity */
public class RoutePagerAdapter extends PagerAdapter {

	String[] currentRoutes;
	Context cxt;

	public RoutePagerAdapter(String[] currentRoutes, Context cxt) {
		super();
		this.currentRoutes = currentRoutes;
		this.cxt = cxt;
	}

	public void destroyItem(View container, int position, Object view) {
		if (view instanceof ListView) {
			((ViewPager) container).removeView((ListView) view);
		} else {
			((ViewPager) container).removeView((TextView) view);
		}

	}

	public int getCount() {
		if (currentRoutes.length > 0) {
			return currentRoutes.length;
		} else {
			return 1;
		}
	}

	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	/*
	 * For each page, make a list view of available stops. Or, if there are
	 * directions, make a list view of available directions. Then launch the
	 * correct activity based on which item in the list view is selected.
	 */
	public Object instantiateItem(View container, int position) {

		if (currentRoutes.length > 0) {
			final int listPosition = position;
			final String currRouteStr = currentRoutes[listPosition];
			final Route currRoute = Data.getRoute(currRouteStr);

			final boolean thisRouteHasDirection = currRoute.direction.size() != 1;
			String[] itemListTemp = new String[] {};
			if (thisRouteHasDirection) {
				// If route has directions, populate list with them
				itemListTemp = Data.getDirList(currRouteStr);
			} else {
				// Route doesn't have direction so list view contains stops
				itemListTemp = Data.getStopTitlesForRouteAndDir(currRouteStr,
						currRoute.getDefaultDirection().title);
			}

			final String[] itemList = itemListTemp;

			ListView stopList = new ListView(cxt);
			stopList.setAdapter(new ArrayAdapter<String>(cxt,
					android.R.layout.simple_list_item_1, itemList));
			stopList.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					if (thisRouteHasDirection) {
						/*
						 * Route has direction so items have to point to
						 * StopListActivity with correct extras.
						 */
						Intent intent = new Intent(cxt.getApplicationContext(),
								StopListActivity.class);
						intent.putExtra("route", currRouteStr);
						intent.putExtra("direction", itemList[position]);
						cxt.startActivity(intent);
					} else {
						/*
						 * Route doesn't have direction so items have to point
						 * to StopViewActivity with correct extras.
						 */
						Intent intent = new Intent(cxt.getApplicationContext(),
								StopViewActivity.class);
						intent.putExtra("stopTag",
								currRoute.stop.get(position).tag);
						intent.putExtra("direction",
								currRoute.getDefaultDirection().title);
						intent.putExtra("directionTag",
								currRoute.getDefaultDirection().tag);
						intent.putExtra("route", currRouteStr);
						intent.putExtra("stop", itemList[position]);
						cxt.startActivity(intent);
					}
				}

			});

			((ViewPager) container).addView(stopList, 0);

			return stopList;
		} else {
			TextView noRoutes = new TextView(cxt);
			noRoutes.setText("No active routes");
			noRoutes.setGravity(Gravity.CENTER);
			noRoutes.setTextSize(40);
			noRoutes.setTypeface(null, 1);
			noRoutes.setTextColor(cxt.getResources().getColor(R.color.white));
			((ViewPager) container).addView(noRoutes, 0);
			return noRoutes;
		}
	}

	public boolean isViewFromObject(View view, Object object) {
		if (object instanceof ListView) {
			return view == ((ListView) object);
		}
		return view == ((TextView) object);
	}

	public String getPageTitle(int position) {
		if (currentRoutes.length > 0) {
			return Data.capitalize(currentRoutes[position]);
		} else {
			return "No routes";
		}
	}

	public void updateCurrentRoutes(String[] currentRoutes) {
		this.currentRoutes = currentRoutes;
	}

}
