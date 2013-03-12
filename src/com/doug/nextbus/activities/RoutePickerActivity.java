package com.doug.nextbus.activities;

import roboguice.inject.InjectView;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.doug.nextbus.R;
import com.doug.nextbus.RoboSherlock.RoboSherlockActivity;
import com.doug.nextbus.backend.APIController;
import com.doug.nextbus.backend.Data;
import com.doug.nextbus.backend.MenuClass;
import com.doug.nextbus.custom.RoutePagerAdapter;
import com.doug.nextbus.custom.WakeupAsyncTask;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitlePageIndicator.IndicatorStyle;

/* The top level, main activity. It lets the users switch between routes. */
public class RoutePickerActivity extends RoboSherlockActivity implements
		OnSharedPreferenceChangeListener {

	@InjectView(R.id.routepagerviewpager) private ViewPager pager;
	@InjectView(R.id.routes) private TitlePageIndicator titleIndicator;

	private String[] mCurrentRoutes;
	private SharedPreferences mPrefs;
	private RoutePagerAdapter mPagerAdapter;

	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.route_picker);

		// Used for waking up the server, done first
		new WakeupAsyncTask().execute();

		Data.setConfig(this);

		int titleId = Resources.getSystem().getIdentifier("action_bar_title",
				"id", "android");

		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		mPrefs.registerOnSharedPreferenceChangeListener(this);

		// Updating available routes depending on preference
		updateCurrentRoutes();

		// Setup Pager and Adapter, make sure passing this
		mPagerAdapter = new RoutePagerAdapter(this, mCurrentRoutes);
		pager.setAdapter(mPagerAdapter);

		// Setup ViewGroup Indicator
		titleIndicator.setFooterIndicatorStyle(IndicatorStyle.Underline);
		titleIndicator.setBackgroundColor(getResources().getColor(
				R.color.subtitlecolor));
		titleIndicator.setFooterIndicatorHeight(10);
		titleIndicator.setSelectedBold(false);

		// Sets the size and color of the text
		final float scale = getResources().getDisplayMetrics().density;
		final float textSize = 20.0f;
		float pixels = textSize * scale;
		titleIndicator.setTextSize(pixels);
		titleIndicator.setViewPager(pager);
		setViewColor(0);

		setEventListeners();

	}

	private void setEventListeners() {
		// Listener for page changing. Basically the left and right swiping
		titleIndicator
				.setOnPageChangeListener(new SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						setViewColor(position);
					}
				});
	}

	/** Updates available routes depending on show active routes preference. */
	private void updateCurrentRoutes() {
		boolean onlyActiveRoutes = mPrefs.getBoolean(
				Data.SHOW_ACTIVE_ROUTES_PREF, true);
		if (onlyActiveRoutes) {
			mCurrentRoutes = APIController.getActiveRoutesList(this);
		} else {
			mCurrentRoutes = Data.DEFAULT_ALL_ROUTES;
		}
	}

	/** Updates text color depending on the position of view page */
	private void setViewColor(int position) {
		int color = R.color.blue; // default color
		if (mCurrentRoutes.length > 0) { // if there are active routes
			color = Data.getColorFromRouteTag(mCurrentRoutes[position]);
		}
		titleIndicator.setSelectedColor(getResources().getColor(color));
		titleIndicator.setFooterColor(getResources().getColor(color));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		int[] disabledItems = {};
		MenuClass.onCreateOptionsMenu(this, menu, R.menu.stock_menu,
				disabledItems);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home)
			return true;
		return MenuClass.onOptionsItemSelected(this, item);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		if (key.equals(Data.SHOW_ACTIVE_ROUTES_PREF)) {
			updateCurrentRoutes();
			mPagerAdapter.updateRoutes(this.mCurrentRoutes);
			// Making sure the right color is chosen for the view
			setViewColor(pager.getCurrentItem());
		}

	}

}
