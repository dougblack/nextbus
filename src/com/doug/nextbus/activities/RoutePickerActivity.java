package com.doug.nextbus.activities;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.doug.nextbus.R;
import com.doug.nextbus.backend.Data;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitlePageIndicator.IndicatorStyle;
import com.viewpagerindicator.TitleProvider;

public class RoutePickerActivity extends Activity implements OnSharedPreferenceChangeListener {

	private Context cxt;

	Data data;
	static boolean[] hasDirections;
	static TitlePageIndicator titleIndicator;
	static int red;
	static int blue;
	static int green;
	static int yellow;
	static int night;
	static String[] currentRoutes;
	static int[] colorOrder;
	static boolean hideDeadRoutes = true;
	static boolean activeRoutesExist = true;
	static RoutePagerAdapter pagerAdapter;
	static ViewPager pager;
	static ImageView mapButton;

	public void onCreate(Bundle savedInstance) {

		super.onCreate(savedInstance);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.route_picker);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);
		hideDeadRoutes = prefs.getBoolean("showActiveRoutes", true);
		cxt = this;
		Data.setConfigData(getApplicationContext());
		data = new Data();
		mapButton = (ImageView) findViewById(R.id.mapButton);

		red = getResources().getColor(R.color.red);
		blue = getResources().getColor(R.color.blue);
		green = getResources().getColor(R.color.green);
		yellow = getResources().getColor(R.color.yellow);
		night = getResources().getColor(R.color.night);
		JSONObject favStop = Data.readStopData();

		shouldHideRoutes(hideDeadRoutes);

		Log.i("INFO", "Current routes: " + Arrays.toString(currentRoutes));

		// Set up ViewGroup
		pagerAdapter = new RoutePagerAdapter();
		pager = (ViewPager) findViewById(R.id.routepagerviewpager);
		pager.setAdapter(pagerAdapter);

		// Set up ViewGroup indicator.
		final TitlePageIndicator titleIndicator = (TitlePageIndicator) findViewById(R.id.routes);
		titleIndicator.setFooterIndicatorStyle(IndicatorStyle.Underline);
		titleIndicator.setBackgroundColor(getResources().getColor(R.color.subtitlecolor));
		titleIndicator.setTopPadding(10);
		titleIndicator.setFooterIndicatorHeight(10);
		titleIndicator.setSelectedBold(false);

		final float scale = getResources().getDisplayMetrics().density;
		float textSize = 20.0f;
		float pixels = textSize * scale;
		titleIndicator.setTextSize(pixels);
		titleIndicator.setViewPager(pager);

		titleIndicator.setOnPageChangeListener(new OnPageChangeListener() {

			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}

			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			public void onPageSelected(int position) {
				if (colorOrder.length > 0) {
					titleIndicator.setSelectedColor(colorOrder[position]);
					titleIndicator.setFooterColor(colorOrder[position]);
				}
			}

		});
		if (activeRoutesExist && currentRoutes.length > 0) {
			pager.setCurrentItem(1);
			// titleIndicator.setFooterColor(colorOrder[titleIndicator.getCurrentItem()]);
			// titleIndicator.setSelectedColor(colorOrder[titleIndicator.getCurrentItem()]);
		} else {
			pager.setCurrentItem(0);
			// titleIndicator.setFooterColor(getResources().getColor(R.color.white));
			// titleIndicator.setSelectedColor(getResources().getColor(R.color.white));
		}

		mapButton.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View arg0, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					mapButton.setBackgroundColor(R.color.black);
					return true;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					mapButton.setBackgroundColor(0);
					Intent mapActivity = new Intent(getApplicationContext(), MapViewActivity.class);
					startActivity(mapActivity);
					return true;
				}
				return true;
			}
		});

	}

	private void shouldHideRoutes(boolean hideDeadRoutes) {

		// Should all routes be shown?
		if (hideDeadRoutes) {
			setCurrentRoutes(getActiveRoutesList());
		} else {
			Object[] activeRouteList = new Object[3];
			String[] activeRoutes = { "red", "blue", "trolley", "green", "night" };
			boolean[] hasDirections = { false, false, true, true, true };
			int[] colorOrder = { red, blue, yellow, green, night };
			activeRouteList[0] = activeRoutes;
			activeRouteList[1] = colorOrder;
			activeRouteList[2] = hasDirections;
			setCurrentRoutes(activeRouteList);
		}

	}

	private void setCurrentRoutes(Object[] activeRoutesList) {
		currentRoutes = (String[]) activeRoutesList[0];
		colorOrder = (int[]) activeRoutesList[1];
		hasDirections = (boolean[]) activeRoutesList[2];
	}

	/**
	 * This method returns the list of active routes by NextBus official
	 * schedule
	 * 
	 * @return active routes
	 */
	public static Object[] getActiveRoutesList() {
		ArrayList<String> activeRoutesList = new ArrayList<String>();
		ArrayList<Integer> activeColorsList = new ArrayList<Integer>();
		ArrayList<Boolean> activeRoutesHasDirectionsList = new ArrayList<Boolean>();

		Time time = new Time();
		time.switchTimezone("EST");

		time.setToNow();
		int hour = time.hour;
		int day = time.weekDay - 1;
		// Log.i("INFO", "Current hour: " + hour);
		// Log.i("INFO", "Current weekday: " + day);
		// Log.i("INFO", "Current minutes: " + time.minute);
		// Log.i("INFO", "DST: " + time.isDst);
		if (day < 5) {
			// Monday - Friday
			if ((hour >= 7) && (hour <= 19)) {
				// 6:45am - 10:45pm
				activeRoutesList.add("red");
				activeColorsList.add(red);
				activeRoutesHasDirectionsList.add(false);
				activeRoutesList.add("blue");
				activeColorsList.add(blue);
				activeRoutesHasDirectionsList.add(false);
			}
			if ((hour >= 7) && (hour <= 18)) {
				// 6:15am - 9:45pm
				activeRoutesList.add("green");
				activeColorsList.add(green);
				activeRoutesHasDirectionsList.add(true);
			}
			if ((hour >= 5) && (hour <= 21)) {
				// 5:15am - 11:00pm
				activeRoutesList.add("trolley");
				activeColorsList.add(yellow);
				activeRoutesHasDirectionsList.add(true);
			}
			if ((day != 4) && (hour >= 21) || (hour <= 2)) {
				// 8:45pm - 3:30am
				activeRoutesList.add("night");
				activeColorsList.add(night);
				activeRoutesHasDirectionsList.add(true);
			}
		} else if (day == 5) {
			// Saturday
			if ((hour >= 10) && (hour <= 17)) {
				// 9:30am - 7:00pm
				activeRoutesList.add("trolley");
				activeColorsList.add(yellow);
				activeRoutesHasDirectionsList.add(true);
			}
		} else if (day == 6) {
			// Sunday
			if ((hour >= 15) && (hour <= 20)) {
				// 2:30pm - 6:30pm
				activeRoutesList.add("trolley");
				activeColorsList.add(yellow);
				activeRoutesHasDirectionsList.add(true);
			}
			if ((hour >= 20 && time.minute >= 45) || (hour <= 3 && time.minute <= 30)) {
				// 8:45pm - 3:30am
				activeRoutesList.add("night");
				activeColorsList.add(night);
				activeRoutesHasDirectionsList.add(true);
			}
		}

		if (activeRoutesList.size() == 0) {
			activeRoutesExist = false;
		}

		Object[] returnData = { Data.convertToStringArray(activeRoutesList), Data.convertIntegers(activeColorsList),
				Data.convertToBooleanArray(activeRoutesHasDirectionsList) };

		return returnData;

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.stock_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.aboutmenusitem:
			Intent aboutActivity = new Intent(getApplicationContext(), CreditsActivity.class);
			startActivity(aboutActivity);
			return true;
		case R.id.preferencesmenuitem:
			Intent preferenceActivity = new Intent(getApplicationContext(), PreferencesActivity.class);
			startActivity(preferenceActivity);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private class RoutePagerAdapter extends PagerAdapter implements TitleProvider {

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

		/**
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
							Intent intent = new Intent(getApplicationContext(), StopListActivity.class);
							intent.putExtra("route", currentRoutes[listPosition]);
							intent.putExtra("direction", itemList[position]);
							startActivity(intent);
						} else {
							// Route doesn't have direction so items have to
							// point to StopViewActivity with
							// correct extras.
							Intent intent = new Intent(getApplicationContext(), StopViewActivity.class);
							intent.putExtra("stoptag", stopTags[position]);
							intent.putExtra("route", currentRoutes[listPosition]);
							intent.putExtra("stop", itemList[position]);
							startActivity(intent);
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
				noRoutes.setTextColor(getResources().getColor(R.color.white));
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

	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

		if (key.equals("showActiveRoutes")) {
			hideDeadRoutes = prefs.getBoolean("showActiveRoutes", true);
			if (hideDeadRoutes) {
				setCurrentRoutes(getActiveRoutesList());
			} else {
				Object[] activeRouteList = new Object[3];
				String[] activeRoutes = { "red", "blue", "trolley", "green", "night" };
				boolean[] hasDirections = { false, false, true, true, true };
				int[] colorOrder = { red, blue, yellow, green, night };
				activeRouteList[0] = activeRoutes;
				activeRouteList[1] = colorOrder;
				activeRouteList[2] = hasDirections;
				setCurrentRoutes(activeRouteList);
				activeRoutesExist = true;
			}
			RoutePagerAdapter pagerAdapter = new RoutePagerAdapter();
			pager.setAdapter(pagerAdapter);
			pager.setCurrentItem(1);
			
		}

	}

	public static ArrayList<String> getActiveRoutes() {
		ArrayList<String> activeRoutes = new ArrayList<String>();
		for (int i = 0; i < currentRoutes.length; i++) {
			activeRoutes.add(currentRoutes[i]);
		}

		return activeRoutes;
	}

}