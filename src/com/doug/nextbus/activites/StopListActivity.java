package com.doug.nextbus.activites;

import com.doug.nextbus.R;
import com.doug.nextbus.R.color;
import com.doug.nextbus.R.id;
import com.doug.nextbus.R.layout;
import com.doug.nextbus.R.menu;
import com.doug.nextbus.backend.Data;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class StopListActivity extends Activity {

	Data data;
	String[] stops;
	String[] stoptags;

	static ListView stopList;
	static TextView routeTitle;
	static TextView directionTextView;
	static String route;
	static String direction;
	static View colorBar;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.stop_list);

		data = new Data();
		stopList = (ListView) findViewById(R.id.stopListView);
		directionTextView = (TextView) findViewById(R.id.directionTextView);
		Bundle extras = getIntent().getExtras();

		colorBar = (View) findViewById(R.id.colorbar);

//		routeTitle.setOnClickListener(new OnClickListener() {
//
//			
//			public void onClick(View v) {
//
//				Intent intent = new Intent(getApplicationContext(), RoutePager.class);
//				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//				startActivity(intent);
//			}
//
//		});

		if (extras != null) {
			route = extras.getString("route");
			direction = extras.getString("direction");
			directionTextView.setText(Data.capitalize(direction));
			Object[] stopInfo = data.getListForRoute(route, direction);
			stops = (String[]) stopInfo[0];
			stoptags = (String[]) stopInfo[1];
			setDirectionTextViewColor(route);
			Log.i("Info", "Showing list for route=" + route + " and direction=" + direction);

			stopList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stops));

			stopList.setOnItemClickListener(new OnItemClickListener() {
				
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Intent intent = new Intent(getApplicationContext(), StopViewActivity.class);
					intent.putExtra("stoptag", ((String[]) stoptags)[position]);
					intent.putExtra("route", route);
					intent.putExtra("stop", ((String[]) stops)[position]);
					startActivity(intent);
				}

			});
		}

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

	public void setDirectionTextViewColor(String route) {

		int color = 0;
		if (route.equals("red")) {
			color = R.color.red;
		} else if (route.equals("blue")) {
			color = R.color.blue;
		} else if (route.equals("green")) {
			color = R.color.green;
			// directionTextView.setTextColor(Color.BLACK);
		} else if (route.equals("trolley")) {
			color = R.color.yellow;
			// directionTextView.setTextColor(Color.BLACK);
		} else if (route.equals("night")) {
			color = R.color.night;
		}

		colorBar.setBackgroundColor(getResources().getColor(color));
		directionTextView.setTextColor(getResources().getColor(color));
	}

}