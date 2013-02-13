package com.doug.nextbus.custom;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.doug.nextbus.R;
import com.doug.nextbus.activities.StopListActivity;
import com.doug.nextbus.activities.StopViewActivity;
import com.doug.nextbus.backend.Data;
import com.viewpagerindicator.TitleProvider;

/* The adapter for the swiping between route pages for the RoutePickerActivity */
public class RoutePagerAdapter extends PagerAdapter implements TitleProvider {

	boolean activeRoutesExist;
	String[] currentRoutes;
	boolean[] hasDirections;
	Data data;
	Context cxt;
	
	public RoutePagerAdapter(boolean activeRoutesExist, String[] currentRoutes, boolean[] hasDirections, Data data,
			Context cxt) {
		super();
		this.activeRoutesExist = activeRoutesExist;
		this.currentRoutes = currentRoutes;
		this.hasDirections = hasDirections;
		this.data = data;
		this.cxt = cxt;
		
	}

	public void destroyItem(View container, int position, Object view) {
		if (view instanceof ListView) {
			((ViewPager) container).removeView((ListView) view);
		} else {
			((ViewPager) container).removeView((TextView) view);
		}

	}

	public void finishUpdate(View arg0) {
		// TODO Auto-generated method stub
	}

	public int getCount() {
		if (activeRoutesExist) {
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
		if (activeRoutesExist) {
			final boolean thisRouteHasDirection = hasDirections[position];
			String[] itemListTemp = null;
			String[] stopTagsTemp = null;

			if (thisRouteHasDirection) {
				// If route has direction set list view to contain
				// directions
				itemListTemp = data.getDirectionList(currentRoutes[position]);
			} else {
				// Route doesn't have direction so list view contains stops
				Object[] stopsAndTags = data.getStopList(currentRoutes[position]);
				itemListTemp = (String[]) stopsAndTags[0];
				stopTagsTemp = (String[]) stopsAndTags[1];
			}

			final String[] itemList = itemListTemp;
			final String[] stopTags = stopTagsTemp;

			final int listPosition = position;
			ListView stopList = new ListView(cxt);
			stopList.setAdapter(new ArrayAdapter<String>(cxt, android.R.layout.simple_list_item_1, itemList));
			stopList.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if (thisRouteHasDirection) {
						// Route has direction so items have to point to
						// StopListActivity with correct
						// extras.
						Intent intent = new Intent(cxt.getApplicationContext(), StopListActivity.class);
						intent.putExtra("route", currentRoutes[listPosition]);
						intent.putExtra("direction", itemList[position]);
						cxt.startActivity(intent);
					} else {
						// Route doesn't have direction so items have to
						// point to StopViewActivity with
						// correct extras.
						Intent intent = new Intent(cxt.getApplicationContext(), StopViewActivity.class);
						intent.putExtra("stoptag", stopTags[position]);
						intent.putExtra("route", currentRoutes[listPosition]);
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

	public void restoreState(Parcelable arg0, ClassLoader arg1) {
		// TODO Auto-generated method stub

	}

	public Parcelable saveState() {
		// TODO Auto-generated method stub
		return null;
	}

	public void startUpdate(View arg0) {

		// TODO Auto-generated method stub

	}

	public String getTitle(int position) {
		if (activeRoutesExist) {
			return Data.capitalize(currentRoutes[position]);
		} else {
			return "No routes";
		}
	}

}
