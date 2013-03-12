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
import com.doug.nextbus.backend.Data;
import com.doug.nextbus.backend.MenuClass;
import com.doug.nextbus.backend.DataGSON.Route;
import com.doug.nextbus.backend.DataGSON.Route.Direction;
import com.doug.nextbus.backend.DataGSON.Route.Stop;

/* This activity shows a list of stops. */
public class StopListActivity extends RoboSherlockActivity {

	@InjectView(R.id.stopListView) private ListView stopListView;
	@InjectView(R.id.directionTextView) private TextView directionTextView;
	@InjectView(R.id.colorbar) private View colorBar;

	private static final String ROUTE_TAG_KEY = "routeTag";
	private static final String DIRECTION_TITLE_KEY = "directionTitle";

	public static Intent createIntent(Context ctx, String routeTag,
			String directionTitle) {
		Intent intent = new Intent(ctx, StopListActivity.class);
		intent.putExtra(ROUTE_TAG_KEY, routeTag);
		intent.putExtra(DIRECTION_TITLE_KEY, directionTitle);
		return intent;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stop_list);

		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			/* Pull route and direction for extras */
			final String routeTag = extras.getString(ROUTE_TAG_KEY);
			final String directionTitle = extras.getString(DIRECTION_TITLE_KEY);

			final Route route = Data.getRouteWithTag(routeTag);
			final String[] stopTitles = route.getStopTitles(directionTitle);
			setDirectionTextViewColor(Data.getColorFromRouteTag(routeTag));
			directionTextView.setText(Data.capitalize(directionTitle));

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
	}

	/** Sets color of label at top of view */
	public void setDirectionTextViewColor(int color) {
		colorBar.setBackgroundColor(getResources().getColor(color));
		directionTextView.setTextColor(getResources().getColor(color));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		int[] disabledItems = {};
		return MenuClass.onCreateOptionsMenu(this, menu, R.menu.stock_menu,
				disabledItems);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return MenuClass.onOptionsItemSelected(this, item);
	}

}
