package com.doug.nextbus.activities;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;

import com.doug.nextbus.R;
import com.doug.nextbus.backend.APIController;
import com.doug.nextbus.backend.Data;
import com.doug.nextbus.custom.RoutePagerAdapter;
import com.doug.nextbus.custom.WakeupAsyncTask;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitlePageIndicator.IndicatorStyle;

/* The top level, main activity. It lets the users switch between routes. */
public class RoutePickerActivity extends RoboActivity implements
		OnSharedPreferenceChangeListener {

	@InjectView(R.id.routepagerviewpager) private ViewPager pager;
	@InjectView(R.id.mapButton) private ImageView mapButton;
	@InjectView(R.id.routes) private TitlePageIndicator titleIndicator;
	@InjectView(R.id.favoriteButtonLaunch) private ImageView favoritesLaunchButton;

	private String[] mCurrentRoutes;
	private SharedPreferences mPrefs;
	private RoutePagerAdapter mPagerAdapter;
	public static final String[] DEFAULT_ALL_ROUTES;

	static {
		DEFAULT_ALL_ROUTES = new String[] { "blue", "red", "trolley", "night",
				"green", "emory" };
	}

	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.route_picker);

		// Used for waking up the server, done first
		new WakeupAsyncTask().execute();

		Data.setConfig(this);

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

		// Button and event for switching to MapView
		mapButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					mapButton.setBackgroundColor(getResources().getColor(
							R.color.black));
					return true;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					mapButton.setBackgroundColor(0);
					Intent mapActivity = new Intent(getApplicationContext(),
							MapViewActivity.class);
					startActivity(mapActivity);
					return true;
				}
				return true;
			}
		});

		favoritesLaunchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						FavoritesActivity.class);
				startActivity(intent);
			}
		});
	}

	/** Updates available routes depending on show active routes preference. */
	private void updateCurrentRoutes() {
		boolean onlyActiveRoutes = mPrefs.getBoolean("showActiveRoutes", true);
		if (onlyActiveRoutes) {
			mCurrentRoutes = APIController.getActiveRoutesList(this);
		} else {
			mCurrentRoutes = DEFAULT_ALL_ROUTES;
		}
	}

	/** Updates text color depending on the position of view page */
	private void setViewColor(int position) {
		int color = R.color.orange; // default color
		if (mCurrentRoutes.length > 0) { // if there are active routes
			color = Data.getColorFromRouteTag(mCurrentRoutes[position]);
		}
		titleIndicator.setSelectedColor(getResources().getColor(color));
		titleIndicator.setFooterColor(getResources().getColor(color));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.stock_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.aboutmenusitem:
			Intent aboutActivity = new Intent(this, CreditsActivity.class);
			startActivity(aboutActivity);
			return true;
		case R.id.preferencesmenuitem:
			Intent preferenceActivity = new Intent(this,
					PreferencesActivity.class);
			startActivity(preferenceActivity);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		if (key.equals("showActiveRoutes")) {
			updateCurrentRoutes();
			mPagerAdapter.updateRoutes(this.mCurrentRoutes);
			// Making sure the right color is chosen for the view
			setViewColor(pager.getCurrentItem());
		}

	}
}
