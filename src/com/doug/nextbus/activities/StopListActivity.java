package com.doug.nextbus.activities;

import android.app.Activity;
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
import com.doug.nextbus.backend.DataResult.Route.PathStop;

/* This activity shows a list of stops. */
public class StopListActivity extends Activity {

	String[] stops;
	String[] stopTags;

	private ListView stopList;
	private TextView directionTextView;
	private String route;
	private String direction;
	private View colorBar;
	private ImageView backButton;

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
			stops = Data.getStopTitlesForRouteAndDir(route, direction);

			setDirectionTextViewColor(route);
			Log.i("Info", "Showing list for route=" + route + " and direction="
					+ direction);

			stopList.setAdapter(new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, stops));

			/* Handler for a stop cell. */
			stopList.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					PathStop pStop = Data.getPathStopForDirandIndex(route,
							direction, position);
					Intent intent = new Intent(getApplicationContext(),
							StopViewActivity.class);
					intent.putExtra("direction", direction);
					intent.putExtra("directionTag",
							Data.getDirectionTag(route, direction));

					intent.putExtra("stopTag", pStop.tag);
					intent.putExtra("route", route);
					intent.putExtra("stop", Data
							.getStopTitleFromRouteAndStopTag(route, pStop.tag));

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
	public void setDirectionTextViewColor(String route) {

		int color = 0;
		if (route.equals("red")) {
			color = R.color.red;
		} else if (route.equals("blue")) {
			color = R.color.blue;
		} else if (route.equals("green")) {
			color = R.color.green;
		} else if (route.equals("trolley")) {
			color = R.color.yellow;
		} else if (route.equals("night")) {
			color = R.color.night;
		} else if (route.equals("emory")) {
			color = R.color.pink;
		}

		colorBar.setBackgroundColor(getResources().getColor(color));
		directionTextView.setTextColor(getResources().getColor(color));
	}

}
