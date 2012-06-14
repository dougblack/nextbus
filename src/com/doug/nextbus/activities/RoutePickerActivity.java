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
import com.doug.nextbus.backend.APIController;
import com.doug.nextbus.backend.Data;
import com.doug.nextbus.custom.RoutePagerAdapter;
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

		shouldHideRoutes(hideDeadRoutes);

		// Set up ViewGroup
		pagerAdapter = new RoutePagerAdapter(activeRoutesExist, currentRoutes, hasDirections, data, cxt);
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
		} else {
			pager.setCurrentItem(0);
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
			setCurrentRoutes(APIController.getActiveRoutesList(cxt));
		} else {
			Object[] activeRouteList = new Object[4];
			String[] activeRoutes = { "red", "blue", "trolley", "green", "night" };
			boolean[] hasDirections = { false, false, true, true, true };
			int[] colorOrder = { red, blue, yellow, green, night };
			activeRouteList[0] = activeRoutes;
			activeRouteList[1] = colorOrder;
			activeRouteList[2] = hasDirections;
			activeRouteList[3] = true;
			setCurrentRoutes(activeRouteList);
		}

	}

	private void setCurrentRoutes(Object[] activeRoutesList) {
		currentRoutes = (String[]) activeRoutesList[0];
		colorOrder = (int[]) activeRoutesList[1];
		hasDirections = (boolean[]) activeRoutesList[2];
		activeRoutesExist = (Boolean) activeRoutesList[3];
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

	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

		if (key.equals("showActiveRoutes")) {
			hideDeadRoutes = prefs.getBoolean("showActiveRoutes", true);
			if (hideDeadRoutes) {
				setCurrentRoutes(APIController.getActiveRoutesList(cxt));
			} else {
				Object[] activeRouteList = new Object[4];
				String[] activeRoutes = { "red", "blue", "trolley", "green", "night" };
				boolean[] hasDirections = { false, false, true, true, true };
				int[] colorOrder = { red, blue, yellow, green, night };
				activeRouteList[0] = activeRoutes;
				activeRouteList[1] = colorOrder;
				activeRouteList[2] = hasDirections;
				activeRouteList[3] = true;
				setCurrentRoutes(activeRouteList);
				activeRoutesExist = true;
			}
			RoutePagerAdapter pagerAdapter = new RoutePagerAdapter(activeRoutesExist, currentRoutes, hasDirections,
					data, cxt);
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