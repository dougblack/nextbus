package com.doug.nextbus.activities;

import roboguice.inject.InjectView;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.doug.nextbus.R;
import com.doug.nextbus.RoboSherlock.RoboSherlockActivity;
import com.doug.nextbus.backend.BundleKeys;
import com.doug.nextbus.backend.Data;
import com.doug.nextbus.backend.MenuFunctions;
import com.doug.nextbus.backend.RouteDataGSON.Route;
import com.doug.nextbus.backend.RouteDataGSON.Route.Direction;
import com.doug.nextbus.backend.RouteDataGSON.Route.Stop;

/* This activity shows a list of stops. */
public class StopListActivity extends RoboSherlockActivity {

	@InjectView(R.id.stopListView) private ListView stopListView;
	@InjectView(R.id.directionTextView) private TextView directionTextView;
	@InjectView(R.id.colorbar) private View colorBar;

	public static Context mCtx;

	public static Intent createIntent(Context ctx, String routeTag,
			String directionTitle) {
		Intent intent = new Intent(ctx, StopListActivity.class);
		intent.putExtra(BundleKeys.ROUTE_TAG_KEY, routeTag);
		intent.putExtra(BundleKeys.DIRECTION_TITLE_KEY, directionTitle);
		return intent;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stop_list);

		mCtx = getApplicationContext();

		Bundle extras = getIntent().getExtras();

		if (extras == null) {
			return;
		}

		/* Pull route and direction for extras */
		final String routeTag = extras.getString(BundleKeys.ROUTE_TAG_KEY);
		final String directionTitle = extras
				.getString(BundleKeys.DIRECTION_TITLE_KEY);

		int color = Data.getColorFromRouteTag(routeTag);
		colorBar.setBackgroundColor(getResources().getColor(color));
		directionTextView.setTextColor(getResources().getColor(color));

		directionTextView.setText(Data.capitalize(directionTitle));

		final Route route = Data.getRouteData().get(routeTag);
		final String[] stopTitles = route.getStopTitles(directionTitle);

		stopListView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, stopTitles));

		stopListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Direction direction = route.getDirection(directionTitle);
				Stop stop = route.getStop(directionTitle, position);

				Intent intent = StopViewActivity.createIntent(
						getApplicationContext(), routeTag, direction, stop);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		int[] disabledItems = {};
		return MenuFunctions.onCreateOptionsMenu(this, menu, R.menu.stock_menu,
				disabledItems);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return MenuFunctions.onOptionsItemSelected(this, item);
	}

}
