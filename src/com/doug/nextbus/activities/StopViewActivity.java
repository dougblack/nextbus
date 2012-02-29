package com.doug.nextbus.activities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import com.doug.nextbus.R;
import com.doug.nextbus.R.color;
import com.doug.nextbus.R.drawable;
import com.doug.nextbus.R.id;
import com.doug.nextbus.R.layout;
import com.doug.nextbus.R.menu;
import com.doug.nextbus.backend.APIController;
import com.doug.nextbus.backend.Data;
import com.doug.nextbus.custom.RainbowArrayAdapter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

public class StopViewActivity extends Activity {

	static TextView firstArrival;
	static TextView secondArrival;
	static TextView thirdArrival;
	static TextView fourthArrival;
	// static TextView routeTextView;
	static TextView stopTextView;
	static TextView titleBar;
	static SlidingDrawer arrivalDrawer;

	static View colorBar;
	static View colorSeperator;

	static ProgressBar routeViewProgressBar;
	static ImageView refreshButton;
	static ImageView starButton;
	static String route;
	static String stoptag;
	static String stop;
	static TextView drawerHandleTextView;
	static ListView arrivalList;
	static ArrayList<Drawable> drawableList;
	static ArrayList<String> arrivalsList;
	static String[] arrivals;
	static Drawable[] colorInts;
	static StopViewActivity thisActivity;
	static Resources resources;
	static long start;
	boolean deadCellOnly;

	public void onCreate(Bundle savedInstance) {

		super.onCreate(savedInstance);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.stop_view);
		Bundle extras = getIntent().getExtras();
		route = extras.getString("route");
		stoptag = extras.getString("stoptag");
		stop = extras.getString("stop");
		deadCellOnly = false;
		routeViewProgressBar = (ProgressBar) this.findViewById(R.id.routeviewprogressbar);

		thisActivity = this;
		resources = getResources();
		refreshButton = (ImageView) this.findViewById(R.id.refreshButton);
		// starButton = (ImageView) this.findViewById(R.id.starButton);
		firstArrival = (TextView) this.findViewById(R.id.firstArrival);
		secondArrival = (TextView) this.findViewById(R.id.secondArrival);
		thirdArrival = (TextView) this.findViewById(R.id.thirdArrival);
		fourthArrival = (TextView) this.findViewById(R.id.fourthArrival);
		// routeTextView = (TextView) this.findViewById(R.id.routeTextView);
		stopTextView = (TextView) this.findViewById(R.id.stopTextView);

		colorBar = (View) this.findViewById(R.id.colorbar);
		colorSeperator = (View) this.findViewById(R.id.colorSeperator);

		drawerHandleTextView = (TextView) this.findViewById(R.id.drawerTextView);
		arrivalList = (ListView) this.findViewById(R.id.arrivalList);
		arrivalList.setBackgroundColor(getResources().getColor(R.color.black));
		
		arrivalsList = Data.getAllRoutesForStop(stoptag);
		ArrayList<String> arrivalsTextList = new ArrayList<String>();
		for (String route : arrivalsList) {
			arrivalsTextList.add(Data.capitalize(route));
		}
		

		drawableList = new ArrayList<Drawable>();
		if (arrivalsList.size() <= 0) {
			drawableList.add(getResources().getDrawable(R.drawable.deadcell));
			arrivalsTextList.add("No other arrivals");
			deadCellOnly = true;
		} else {
			
			for (String route : arrivalsList) {
				Drawable cellDrawable = null;
				if (route.equals("red")) {
					cellDrawable = getResources().getDrawable(R.drawable.redcell);
					// colorBandRed.getBackground().setAlpha(255);
				} else if (route.equals("blue")) {
					cellDrawable = getResources().getDrawable(R.drawable.bluecell);
					// colorBandBlue.getBackground().setAlpha(255);
				} else if (route.equals("trolley")) {
					cellDrawable = getResources().getDrawable(R.drawable.yellowcell);
					// colorBandYellow.getBackground().setAlpha(255);
				} else if (route.equals("green")) {
					cellDrawable = getResources().getDrawable(R.drawable.greencell);
					// colorBandGreen.getBackground().setAlpha(255);
				} else if (route.equals("night")) {
					cellDrawable = getResources().getDrawable(R.drawable.nightcell);
				}
				
				drawableList.add(cellDrawable);
			}
			
		}
		
		arrivals = Data.convertToStringArray(arrivalsTextList);

		arrivalList.setAdapter(new RainbowArrayAdapter(thisActivity, R.layout.customarrivallist, arrivals,
				drawableList, deadCellOnly));

		arrivalList.setOnItemClickListener(new OnItemClickListener() {

			
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				if (!arrivals[0].equals("No other arrivals")) {
					Intent intent = new Intent(thisActivity.getApplicationContext(), StopViewActivity.class);
					intent.putExtra("stoptag", stoptag);
					intent.putExtra("route", arrivalsList.get(position));
					intent.putExtra("stop", stop);
					startActivity(intent);
				}
			}

		});

		// routeTextView.setText(route.toUpperCase());
		stopTextView.setText(stop);
		// titleBar = (TextView) this.findViewById(R.id.titleBar);
		// // TODO
		// // Doesn't work because View doesn't get redrawn. Fix later
		// // colorBandRed.getBackground().setAlpha(100);
		// // colorBandBlue.getBackground().setAlpha(100);
		// // colorBandYellow.getBackground().setAlpha(100);
		// // colorBandGreen.getBackground().setAlpha(100);
		//
		// titleBar.setOnClickListener(new OnClickListener() {
		//
		// 
		// public void onClick(View v) {
		//
		// Intent intent = new Intent(getApplicationContext(),
		// RoutePager.class);
		// intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		// startActivity(intent);
		// }
		//
		// });

		refreshButton.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				routeViewProgressBar.setVisibility(View.VISIBLE);
				refresh(route, stoptag);
			}

		});

		// starButton.setOnClickListener(new OnClickListener() {
		//
		// 
		// public void onClick(View v) {
		// Data.setFavoriteStop(route, stoptag, stop);
		// Toast.makeText(thisActivity.getApplicationContext(),
		// "Saved this stop for quick access",
		// Toast.LENGTH_SHORT).show();
		// }
		//
		// });

		arrivalDrawer = (SlidingDrawer) this.findViewById(R.id.arrivalsDrawer);
		arrivalDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {

			
			public void onDrawerOpened() {
				drawerHandleTextView.setText(stop);
			}

		});

		arrivalDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {

			
			public void onDrawerClosed() {
				drawerHandleTextView.setText("All Routes for this Stop");
			}

		});

		setViewColor(route);

		firstArrival.setText("");
		secondArrival.setText("");
		thirdArrival.setText("");
		fourthArrival.setText("");

		refresh(route, stoptag);

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

	public void setViewColor(String route) {
		int color = 0;
		if (route.equals("red")) {
			color = R.color.red;
		} else if (route.equals("blue")) {
			color = R.color.blue;
		} else if (route.equals("green")) {
			color = R.color.green;
			// stopTextView.setTextColor(Color.BLACK);
		} else if (route.equals("trolley")) {
			color = R.color.yellow;
			// stopTextView.setTextColor(Color.BLACK);
		} else if (route.equals("night")) {
			color = R.color.night;
		}

		stopTextView.setTextColor(getResources().getColor(color));
		colorBar.setBackgroundColor(getResources().getColor(color));
		colorSeperator.setBackgroundColor(getResources().getColor(color));
	}

	public void refresh(String route, String stoptag) {
		new loadPredictionData().execute(route, stoptag);
	}

	private class loadPredictionData extends AsyncTask<String, Void, ArrayList<Integer>> {

		
		protected ArrayList<Integer> doInBackground(String... values) {
			
			start = System.currentTimeMillis();
			
			ArrayList<Integer> predictions = APIController.getPrediction(values[0], values[1]);

			return predictions;
		}

		public void onPostExecute(ArrayList<Integer> predictions) {
			
			long end = System.currentTimeMillis() - start;
			Log.i("TIME", "Received and processed prediction in: " + end + "ms");
			
			routeViewProgressBar.setVisibility(View.INVISIBLE);
			// Split up TreeMap into ArrayList for regular view.

			
			drawableList = new ArrayList<Drawable>();

			if (predictions.get(0) == Integer.valueOf(-1)) {
				firstArrival.setText("--");
				secondArrival.setText("");
				thirdArrival.setText("");
				fourthArrival.setText("");

			} else {

				firstArrival.setText(predictions.get(0).toString());

				if (predictions.size() > 1)
					secondArrival.setText(predictions.get(1).toString());
				else
					secondArrival.setText("");
				if (predictions.size() > 2)
					thirdArrival.setText(predictions.get(2).toString());
				else
					thirdArrival.setText("");
				if (predictions.size() > 3)
					fourthArrival.setText(predictions.get(3).toString());
				else
					fourthArrival.setText("");

			}



		}

	}

}