package com.doug.nextbus.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
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

import com.doug.nextbus.R;
import com.doug.nextbus.backend.APIController;
import com.doug.nextbus.backend.Data;
import com.doug.nextbus.custom.RainbowArrayAdapter;

/* This activity displays the predictions for a the current stop */
public class StopViewActivity extends Activity {

	private TextView firstArrival;
	private TextView secondArrival;
	private TextView thirdArrival;
	private TextView fourthArrival;
	private TextView stopTextView;
	private SlidingDrawer arrivalDrawer;
	private ImageView backButton;

	private View colorBar;
	private View colorSeperator;

	private ProgressBar routeViewProgressBar;
	private ImageView refreshButton;
	private String route;
	private String direction;
	private String directionTag;
	private String stop;
	private String stopTag;
	private TextView drawerHandleTextView;
	private ListView arrivalList;
	private ArrayList<Drawable> drawableList;
	private ArrayList<String> arrivalsList;
	private String[] arrivals;
	private long start;
	private boolean deadCellOnly;

	public void onCreate(Bundle savedInstance) {

		super.onCreate(savedInstance);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.stop_view);
		Bundle extras = getIntent().getExtras();
		route = extras.getString("route");
		direction = extras.getString("direction");
		directionTag = extras.getString("directionTag");
		stop = extras.getString("stop");
		stopTag = extras.getString("stopTag");
		deadCellOnly = false;
		routeViewProgressBar = (ProgressBar) this
				.findViewById(R.id.routeviewprogressbar);

		refreshButton = (ImageView) this.findViewById(R.id.refreshButton);
		firstArrival = (TextView) this.findViewById(R.id.firstArrival);
		secondArrival = (TextView) this.findViewById(R.id.secondArrival);
		thirdArrival = (TextView) this.findViewById(R.id.thirdArrival);
		fourthArrival = (TextView) this.findViewById(R.id.fourthArrival);
		stopTextView = (TextView) this.findViewById(R.id.stopTextView);
		backButton = (ImageView) this.findViewById(R.id.backButton);

		colorBar = (View) this.findViewById(R.id.colorbar);
		colorSeperator = (View) this.findViewById(R.id.colorSeperator);

		drawerHandleTextView = (TextView) this
				.findViewById(R.id.drawerTextView);
		arrivalList = (ListView) this.findViewById(R.id.arrivalList);
		arrivalList.setBackgroundColor(getResources().getColor(R.color.black));

		arrivalsList = new ArrayList<String>();
		// Data.getAllRoutesForStop(stoptag, route);
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
					cellDrawable = getResources().getDrawable(
							R.drawable.redcell);
				} else if (route.equals("blue")) {
					cellDrawable = getResources().getDrawable(
							R.drawable.bluecell);
				} else if (route.equals("trolley")) {
					cellDrawable = getResources().getDrawable(
							R.drawable.yellowcell);
				} else if (route.equals("green")) {
					cellDrawable = getResources().getDrawable(
							R.drawable.greencell);
				} else if (route.equals("night")) {
					cellDrawable = getResources().getDrawable(
							R.drawable.nightcell);
				} else if (route.equals("emory")) {
					cellDrawable = getResources().getDrawable(
							R.drawable.pinkcell);
				}
				drawableList.add(cellDrawable);
			}
		}

		arrivals = Data.convertToStringArray(arrivalsTextList);

		arrivalList.setAdapter(new RainbowArrayAdapter(getApplicationContext(),
				R.layout.customarrivallist, arrivals, drawableList,
				deadCellOnly));

		/*
		 * Listener for arrival drawer thing. If a cell is clicked, open the
		 * stop for that route
		 */
		arrivalList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (!arrivals[0].equals("No other arrivals")) {
					Intent intent = new Intent(getApplicationContext(),
							StopViewActivity.class);
					intent.putExtra("stopTag", stopTag);
					intent.putExtra("route", arrivalsList.get(position));
					intent.putExtra("stop", stop);
					startActivity(intent);
				}
			}
		});

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

		stopTextView.setText(stop);

		refreshButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				routeViewProgressBar.setVisibility(View.VISIBLE);
				refresh(route, direction, stopTag);
			}
		});

		arrivalDrawer = (SlidingDrawer) this.findViewById(R.id.arrivalsDrawer);
		arrivalDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
			public void onDrawerOpened() {
				drawerHandleTextView.setText(stop);
			}
		});

		arrivalDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
			public void onDrawerClosed() {
				drawerHandleTextView.setText("OTHER ROUTES");
				drawerHandleTextView.postInvalidate();
			}
		});

		setViewColor(route);

		firstArrival.setText("");
		secondArrival.setText("");
		thirdArrival.setText("");
		fourthArrival.setText("");

		refresh(route, directionTag, stopTag);

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

	public void setViewColor(String route) {
		int color = Data.getColorFromRouteTag(route);

		stopTextView.setTextColor(getResources().getColor(color));
		colorBar.setBackgroundColor(getResources().getColor(color));
		colorSeperator.setBackgroundColor(getResources().getColor(color));
	}

	public void refresh(String route, String direction, String stopTag) {
		if (route.equals("red")
				&& (stopTag.equals("fitten_a") || stopTag.equals("fitten"))) {
			stopTag = "fitten";

		} else if (route.equals("blue")
				&& (stopTag.equals("fitten_a") || stopTag.equals("fitten"))) {
			stopTag = "fitten_a";
		}
		new loadPredictionData().execute(route, direction, stopTag);
	}

	/* Load prediction data asynchronously. */
	private class loadPredictionData extends
			AsyncTask<String, Void, ArrayList<String>> {

		/* Get the data. */
		protected ArrayList<String> doInBackground(String... values) {
			start = System.currentTimeMillis();
			ArrayList<String> predictions = APIController.getPrediction(
					values[0], values[1], values[2]);
			return predictions;
		}

		/* Update the UI */
		public void onPostExecute(ArrayList<String> predictions) {

			long end = System.currentTimeMillis() - start;
			Log.i("TIME", "Received and processed prediction in: " + end + "ms");

			routeViewProgressBar.setVisibility(View.INVISIBLE);
			drawableList = new ArrayList<Drawable>();
			if (predictions.size() == 0 || predictions.get(0).equals("error")) {
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
