package com.doug.nextbus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

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

public class StopView extends Activity {

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
	static View colorBandRed;
	static View colorBandBlue;
	static View colorBandYellow;
	static View colorBandGreen;

	static ProgressBar routeViewProgressBar;
	static ImageView refreshButton;
	static ImageView starButton;
	static String route;
	static String stopid;
	static String stop;
	static TextView drawerHandleTextView;
	static ListView arrivalList;
	static ArrayList<Drawable> drawableList;
	static ArrayList<String> arrivalsList;
	static String[] arrivals;
	static Drawable[] colorInts;
	static StopView thisActivity;
	static ArrayList<String> arrivalRouteOrder;
	static Resources resources;
	static ImageView homeButton;
	boolean deadCellOnly;

	public void onCreate(Bundle savedInstance) {

		super.onCreate(savedInstance);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.stop_view);
		Bundle extras = getIntent().getExtras();
		route = extras.getString("route");
		stopid = extras.getString("stopid");
		stop = extras.getString("stop");
		deadCellOnly = false;
		routeViewProgressBar = (ProgressBar) this.findViewById(R.id.routeviewprogressbar);

		thisActivity = this;
		resources = getResources();
		refreshButton = (ImageView) this.findViewById(R.id.refreshButton);
		homeButton = (ImageView) this.findViewById(R.id.homeButton);
//		starButton = (ImageView) this.findViewById(R.id.starButton);
		firstArrival = (TextView) this.findViewById(R.id.firstArrival);
		secondArrival = (TextView) this.findViewById(R.id.secondArrival);
		thirdArrival = (TextView) this.findViewById(R.id.thirdArrival);
		fourthArrival = (TextView) this.findViewById(R.id.fourthArrival);
		// routeTextView = (TextView) this.findViewById(R.id.routeTextView);
		stopTextView = (TextView) this.findViewById(R.id.stopTextView);

		colorBar = (View) this.findViewById(R.id.colorbar);
		colorSeperator = (View) this.findViewById(R.id.colorSeperator);
		colorBandRed = (View) this.findViewById(R.id.colorBandRed);
		colorBandBlue = (View) this.findViewById(R.id.colorBandBlue);
		colorBandGreen = (View) this.findViewById(R.id.colorBandGreen);
		colorBandYellow = (View) this.findViewById(R.id.colorBandYellow);

		drawerHandleTextView = (TextView) this.findViewById(R.id.drawerTextView);
		arrivalList = (ListView) this.findViewById(R.id.arrivalList);
		arrivalList.setBackgroundColor(getResources().getColor(R.color.black));

		// routeTextView.setText(route.toUpperCase());
		stopTextView.setText(stop);
//		titleBar = (TextView) this.findViewById(R.id.titleBar);
//		// TODO
//		// Doesn't work because View doesn't get redrawn. Fix later
//		// colorBandRed.getBackground().setAlpha(100);
//		// colorBandBlue.getBackground().setAlpha(100);
//		// colorBandYellow.getBackground().setAlpha(100);
//		// colorBandGreen.getBackground().setAlpha(100);
//
//		titleBar.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//
//				Intent intent = new Intent(getApplicationContext(), RoutePager.class);
//				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//				startActivity(intent);
//			}
//
//		});

		refreshButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				routeViewProgressBar.setVisibility(View.VISIBLE);
				refresh(route, stopid);
			}

		});

//		starButton.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Data.setFavoriteStop(route, stopid, stop);
//				Toast.makeText(thisActivity.getApplicationContext(), "Saved this stop for quick access",
//						Toast.LENGTH_SHORT).show();
//			}
//
//		});
		
		homeButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					homeButton.setBackgroundColor(R.color.black);
					return true;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					finish();
					return true;
				}
				return false;	
			}	
		});

		arrivalDrawer = (SlidingDrawer) this.findViewById(R.id.arrivalsDrawer);
		arrivalDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {

			@Override
			public void onDrawerOpened() {
				drawerHandleTextView.setText(stop);
			}

		});

		arrivalDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {

			@Override
			public void onDrawerClosed() {
				drawerHandleTextView.setText("All Routes for this Stop");
			}

		});

		setViewColor(route);

		firstArrival.setText("");
		secondArrival.setText("");
		thirdArrival.setText("");
		fourthArrival.setText("");

		refresh(route, stopid);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.stock_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.aboutmenusitem:
			Intent aboutActivity = new Intent(getApplicationContext(), AboutView.class);
			startActivity(aboutActivity);
			return true;
		case R.id.preferencesmenuitem:
			Intent preferenceActivity = new Intent(getApplicationContext(), Preferences.class);
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

	public void refresh(String route, String stopid) {
		new loadPredictionData().execute(route, stopid);
	}

	private class loadPredictionData extends AsyncTask<String, Void, TreeMap<Integer, Object>> {

		@Override
		protected TreeMap<Integer, Object> doInBackground(String... values) {

			TreeMap<Integer, Object> predictionTreeMap = APIController.getPrediction(values[0], values[1]);

			return predictionTreeMap;
		}

		public void onPostExecute(TreeMap<Integer, Object> predictionTreeMap) {

			if (predictionTreeMap.firstKey() == Integer.valueOf(-1)
					&& predictionTreeMap.get(Integer.valueOf(-1)).equals("error")) {
				firstArrival.setText("--");
				secondArrival.setText("");
				thirdArrival.setText("");
				fourthArrival.setText("");

			} else {

				routeViewProgressBar.setVisibility(View.INVISIBLE);
				// Split up TreeMap into ArrayList for regular view.

				ArrayList<String> predictionValues = new ArrayList<String>();
				arrivalsList = new ArrayList<String>();
				drawableList = new ArrayList<Drawable>();
				arrivalRouteOrder = new ArrayList<String>();

				int i = 0;
				for (Map.Entry<Integer, Object> entry : predictionTreeMap.entrySet()) {
					Object value = entry.getValue();
					if (value instanceof String) {
						if (i < 4 && route.equals(entry.getValue())) {
							if (entry.getKey() != null) {
								predictionValues.add(entry.getKey().toString());
							} else {
								predictionValues.add("--");
								i = 5;
							}
							i++;
						}
					} else {

						LinkedList<String> valueLL = (LinkedList<String>) value;
						ListIterator<String> itr = valueLL.listIterator();
						while (itr.hasNext()) {
							String next = itr.next();
							if (next.equals(route)) {
								predictionValues.add(entry.getKey().toString());
							}
						}
					}
				}

				if (Integer.parseInt(predictionValues.get(0)) < 0) {
					firstArrival.setText("--");
					secondArrival.setText("");
					thirdArrival.setText("");
					fourthArrival.setText("");
				} else {
					firstArrival.setText(predictionValues.get(0));
					Log.i("INFO", "Prediction values length=" + predictionValues.size());

					if (predictionValues.size() > 1)
						secondArrival.setText(predictionValues.get(1));
					else
						secondArrival.setText("");
					if (predictionValues.size() > 2)
						thirdArrival.setText(predictionValues.get(2));
					else
						thirdArrival.setText("");
					if (predictionValues.size() > 3)
						fourthArrival.setText(predictionValues.get(3));
					else
						fourthArrival.setText("");
				}

				// Split up TreeMap for slidingDrawer view.
				Drawable cellDrawable = null;
				for (Map.Entry<Integer, Object> entry : predictionTreeMap.entrySet()) {
					Integer key = entry.getKey();
					Object route = entry.getValue();
					if (route instanceof String) {
						if (key >= 0) {
							if (key == Integer.valueOf(1)) {
								String arrival = key.toString() + " minute";
								arrivalsList.add(arrival);
							} else {
								String arrival = key.toString() + " minutes";
								arrivalsList.add(arrival);
							}

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
							arrivalRouteOrder.add((String) route);
							drawableList.add(cellDrawable);
						}
					} else {
						if (key != Integer.valueOf(-1)) {
							String minutesString = (key == Integer.valueOf(1)) ? " minute" : " minutes";

							LinkedList<String> routeLL = (LinkedList<String>) route;
							ListIterator<String> itr = routeLL.listIterator();

							while (itr.hasNext()) {
								arrivalsList.add(key.toString() + minutesString);
								String next = itr.next();
								if (next.equals("red")) {
									cellDrawable = getResources().getDrawable(R.drawable.redcell);
								} else if (next.equals("blue")) {
									cellDrawable = getResources().getDrawable(R.drawable.bluecell);
								} else if (next.equals("trolley")) {
									cellDrawable = getResources().getDrawable(R.drawable.yellowcell);
								} else if (next.equals("green")) {
									cellDrawable = getResources().getDrawable(R.drawable.greencell);
								} else if (next.equals("night")) {
									cellDrawable = getResources().getDrawable(R.drawable.nightcell);
								}
								arrivalRouteOrder.add(next);
								drawableList.add(cellDrawable);

							}

						}
					}

				}

			}

			if (arrivalsList.size() == 0) {
				Log.i("INFO", "DEAD CELL ONLY");
				arrivalsList.add("No other arrivals");
				arrivals = Data.convertToStringArray(arrivalsList);
				drawableList.add(getResources().getDrawable(R.drawable.deadcell));
				deadCellOnly = true;

			} else {
				arrivals = Data.convertToStringArray(arrivalsList);
			}

			arrivalList.setAdapter(new RainbowArrayAdapter(thisActivity, R.layout.customarrivallist, arrivals,
					drawableList, deadCellOnly));

			arrivalList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

					if (!arrivals[0].equals("No other arrivals")) {
						Intent intent = new Intent(thisActivity.getApplicationContext(), StopView.class);
						intent.putExtra("stopid", stopid);
						intent.putExtra("route", arrivalRouteOrder.get(position));
						intent.putExtra("stop", stop);
						startActivity(intent);
					}
				}

			});

		}

	}

}