package com.doug.nextbus.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.doug.nextbus.backend.DataResult.Route.Direction;
import com.doug.nextbus.backend.DataResult.Route.Stop;

/* This activity shows a list of stops. */
public class StopListActivity extends Activity {

	private String[] stops;

	private ListView stopList;
	private TextView directionTextView;
	private String route;
	private String direction;
	private View colorBar;
	private ImageView backButton;

	public static Intent createIntent(Context ctx, String route,
			String direction) {
		Intent intent = new Intent(ctx, StopListActivity.class);
		intent.putExtra("route", route);
		intent.putExtra("direction", direction);
		return intent;
	}

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.stop_list);

		stopList = (ListView) findViewById(R.id.stopListView);
		directionTextView = (TextView) findViewById(R.id.directionTextView);
		backButton = (ImageView) findViewById(R.id.directionBackButton);
		Bundle extras = getIntent().getExtras();

		colorBar = (View) findViewById(R.id.colorbar);

		if (extras != null) {
			/* Pull route and direction for extras */
			route = extras.getString("route");
			direction = extras.getString("direction");
			directionTextView.setText(Data.capitalize(direction));
			stops = Data.getStopTitlesFromRouteAndDir(route, direction);

			setDirectionTextViewColor(Data.getColorFromRouteTag(route));
			Log.i("Info", "Showing list for route=" + route + " and direction="
					+ direction);

			stopList.setAdapter(new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, stops));

			/* Handler for a stop cell. */
			stopList.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Direction dir = Data.getDirObjFromRouteAndDir(route,
							direction);
					Stop stop = Data.getStop(route, direction, position);
					Intent intent = StopViewActivity.createIntent(
							getApplicationContext(), route, dir, stop);
					startActivity(intent);
				}

			});
		}

		backButton.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View arg0, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					backButton.setBackgroundColor(getResources().getColor(
							R.color.black));
					return true;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					backButton.setBackgroundColor(0);
					finish();
					return true;
				}
				return true;
			}
		});
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.stock_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		/* Handle item selection */
		switch (item.getItemId()) {
		case R.id.aboutmenusitem:
			Intent aboutActivity = new Intent(getApplicationContext(),
					CreditsActivity.class);
			startActivity(aboutActivity);
			return true;
		case R.id.preferencesmenuitem:
			Intent preferenceActivity = new Intent(getApplicationContext(),
					PreferencesActivity.class);
			startActivity(preferenceActivity);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* Sets color of label at top of view */
	public void setDirectionTextViewColor(int color) {
		colorBar.setBackgroundColor(getResources().getColor(color));
		directionTextView.setTextColor(getResources().getColor(color));
	}

}
