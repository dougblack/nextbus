package com.doug.nextbus.activities;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.content.Context;
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

	private Context ctx;
	private SharedPreferences prefs;
	private String[] currentRoutes;
	private RoutePagerAdapter pagerAdapter;
	public static final String[] allRoutes;

	static {
		allRoutes = new String[] { "red", "blue", "trolley", "green", "night",
				"emory" };
	}

	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.route_picker);

		// Used for waking up the server, done first
		new WakeupAsyncTask().execute();

		this.ctx = this;
		Data.setConfigData(ctx);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);

		// Updating available routes depending on preference
		updateCurrentRoutes();

		// Setup Pager and Adapter
		pagerAdapter = new RoutePagerAdapter(currentRoutes, ctx);
		pager.setAdapter(pagerAdapter);

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
		boolean onlyActiveRoutes = prefs.getBoolean("showActiveRoutes", true);
		if (onlyActiveRoutes) {
			currentRoutes = APIController.getActiveRoutesList(ctx);
		} else {
			currentRoutes = allRoutes;
		}
	}

	/** Updates text color depending on the position of view page */
	private void setViewColor(int position) {
		int color = R.color.orange; // default color
		if (currentRoutes.length > 0) { // if there are active routes
			color = Data.getColorFromRouteTag(currentRoutes[position]);
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
			Intent aboutActivity = new Intent(ctx, CreditsActivity.class);
			startActivity(aboutActivity);
			return true;
		case R.id.preferencesmenuitem:
			Intent preferenceActivity = new Intent(ctx,
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

			pagerAdapter.updateCurrentRoutes(this.currentRoutes);
			pager.setCurrentItem(0);
			pagerAdapter.notifyDataSetChanged();
			setViewColor(0);
		}

	}
}
