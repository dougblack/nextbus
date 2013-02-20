package com.doug.nextbus.activities;

import java.util.ArrayList;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

import com.doug.nextbus.R;
import com.doug.nextbus.backend.APIController;
import com.doug.nextbus.backend.Data;
import com.doug.nextbus.backend.Favorite;
import com.doug.nextbus.backend.JSONDataResult.Route.Direction;
import com.doug.nextbus.backend.JSONDataResult.Route.Stop;
import com.doug.nextbus.backend.RouteDirectionStop;
import com.doug.nextbus.custom.ArrivalAdapter;

/* This activity displays the predictions for a the current stop */
public class StopViewActivity extends RoboActivity implements
		OnSharedPreferenceChangeListener {

	@InjectView(R.id.firstArrival) private TextView firstArrival;
	@InjectView(R.id.secondArrival) private TextView secondArrival;
	@InjectView(R.id.thirdArrival) private TextView thirdArrival;
	@InjectView(R.id.fourthArrival) private TextView fourthArrival;
	@InjectView(R.id.stopTextView) private TextView stopTextView;
	@InjectView(R.id.drawerTextView) private TextView drawerHandleTextView;
	@InjectView(R.id.titleText) private TextView titleTextView;

	@InjectView(R.id.backButton) private ImageView backButton;
	@InjectView(R.id.refreshButton) private ImageView refreshButton;
	@InjectView(R.id.favoriteButton) private ImageView favoriteButton;
	@InjectView(R.id.routeviewprogressbar) private ProgressBar routeViewProgressBar;

	@InjectView(R.id.colorbar) private View colorBar;
	@InjectView(R.id.colorSeperator) private View colorSeperator;
	@InjectView(R.id.arrivalsDrawer) private SlidingDrawer arrivalsDrawer;
	@InjectView(R.id.arrivalList) private ListView arrivalList;

	final private static String ROUTE_TAG_KEY = "routeTag";
	final private static String DIRECTION_TITLE_KEY = "directionTitle";
	final private static String DIRECTION_TAG_KEY = "directionTag";
	final private static String STOP_TITLE_KEY = "stopTitle";
	final private static String STOP_TAG_KEY = "stopTag";
	final private static String RETURN_TO_FAVORITES = "returnToFavorites";

	private String mRouteTag;
	private String mDirectionTitle;
	private String mDirectionTag;
	private String mStopTitle;
	private String mStopTag;

	private String[] mArrivalsArray;
	private RouteDirectionStop[] mRdsArray;

	public static Intent createIntent(Context ctx, String routeTag,
			Direction direction, Stop stop) {
		Intent intent = new Intent(ctx, StopViewActivity.class);
		intent.putExtra(ROUTE_TAG_KEY, routeTag);
		intent.putExtra(DIRECTION_TITLE_KEY, direction.title);
		intent.putExtra(DIRECTION_TAG_KEY, direction.tag);
		intent.putExtra(STOP_TITLE_KEY, stop.title);
		intent.putExtra(STOP_TAG_KEY, stop.tag);
		// Closes all instances of the same activity
		// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return intent;
	}

	public static Intent createIntent(Context ctx, Favorite favorite) {

		Intent intent = new Intent(ctx, StopViewActivity.class);
		intent.putExtra(ROUTE_TAG_KEY, favorite.routeTag);
		intent.putExtra(DIRECTION_TITLE_KEY, favorite.directionTitle);
		intent.putExtra(DIRECTION_TAG_KEY, favorite.directionTag);
		intent.putExtra(STOP_TITLE_KEY, favorite.stopTitle);
		intent.putExtra(STOP_TAG_KEY, favorite.stopTag);
		intent.putExtra(RETURN_TO_FAVORITES, true);
		// Closes all instances of the same activity
		// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return intent;
	}

	@Override
	public void onCreate(Bundle savedInstance) {

		super.onCreate(savedInstance);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.stop_view);

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);

		// Extras
		Bundle extras = getIntent().getExtras();
		mRouteTag = extras.getString(ROUTE_TAG_KEY);
		mDirectionTitle = extras.getString(DIRECTION_TITLE_KEY);
		mDirectionTag = extras.getString(DIRECTION_TAG_KEY);
		mStopTitle = extras.getString(STOP_TITLE_KEY);
		mStopTag = extras.getString(STOP_TAG_KEY);

		stopTextView.setText(mStopTitle);

		if (extras.getBoolean(RETURN_TO_FAVORITES))
			titleTextView.setText("FAVORITES");

		setupArrivals();

		setViewColor(mRouteTag);

		// Setting text views to default values
		firstArrival.setText("");
		secondArrival.setText("");
		thirdArrival.setText("");
		fourthArrival.setText("");

		Favorite favorite = new Favorite(mRouteTag, mDirectionTag,
				mDirectionTitle, mStopTag, mStopTitle);

		int star = R.drawable.rate_star_big_off_holo_light;
		if (Data.isFavorite(favorite)) {
			star = R.drawable.rate_star_big_on_holo_light;
		}
		favoriteButton.setImageResource(star);

		refresh(mRouteTag, mDirectionTag, mStopTag);

		setEventListeners(favorite);
	}

	private void setEventListeners(Favorite favorite) {
		backButton.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View arg0, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					backButton.setBackgroundColor(getResources().getColor(
							R.color.black));
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					backButton.setBackgroundColor(0);
					finish();
				}
				return true;
			}
		});

		favoriteButton.setOnClickListener(new CustomOnClickListener(favorite));

		refreshButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				routeViewProgressBar.setVisibility(View.VISIBLE);
				refresh(mRouteTag, mDirectionTag, mStopTag);
			}
		});

		arrivalsDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
			public void onDrawerOpened() {
				drawerHandleTextView.setText(mStopTitle);
			}
		});

		arrivalsDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
			public void onDrawerClosed() {
				drawerHandleTextView.setText("OTHER ROUTES");
			}
		});

		arrivalList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mRdsArray.length == 0) {
					return;
				}
				Intent intent = StopListActivity.createIntent(
						getApplicationContext(), mRdsArray[position].route.tag,
						mRdsArray[position].direction.title);
				startActivity(intent);

			}

		});

	}

	/** Gets Rds with stopTitle, exclude Rds with routeTag and directionTag */
	public void setupArrivals() {
		mRdsArray = Data.getAllRdsWithStopTitle(mStopTitle, mRouteTag,
				mDirectionTag);

		arrivalList.setAdapter(new ArrivalAdapter(getApplicationContext(),
				mRdsArray));

		arrivalList.setOnItemClickListener(null);
		/*
		 * Listener for arrival drawer thing. If a cell is clicked, open the
		 * stop for that route
		 */
		arrivalList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (!mArrivalsArray[0].equals("No other arrivals")) {
					RouteDirectionStop rds = mRdsArray[position];
					Intent intent = StopViewActivity.createIntent(
							getApplicationContext(), rds.route.tag,
							rds.direction, rds.stop);
					startActivity(intent);
				}
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.stock_menu, menu);
		return true;
	}

	/** Gets the latest prediction data */
	private void refresh(String route, String direction, String stopTag) {
		new LoadPredictionAsyncTask(this).execute(route, direction, stopTag);
	}

	/** Set color of text with respect to route */
	private void setViewColor(String route) {
		int color = Data.getColorFromRouteTag(route);
		stopTextView.setTextColor(getResources().getColor(color));
		colorBar.setBackgroundColor(getResources().getColor(color));
		colorSeperator.setBackgroundColor(getResources().getColor(color));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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

	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

		if (key.equals(Data.SHOW_ACTIVE_ROUTES_PREF)) {
			this.setupArrivals();
		}

	}

	/** Custom OnClickListener so the favorite class could be passed to it. */
	private class CustomOnClickListener implements OnClickListener {

		private Favorite favorite;

		public CustomOnClickListener(Favorite favorite) {
			this.favorite = favorite;
		}

		@Override
		public void onClick(View v) {
			boolean ret = Data.toggleFavorite(favorite);
			if (ret) {
				((ImageButton) v)
						.setImageResource(R.drawable.rate_star_big_on_holo_light);
				Toast.makeText(getApplicationContext(), "Added to Favorites",
						Toast.LENGTH_SHORT).show();
			} else {
				((ImageButton) v)
						.setImageResource(R.drawable.rate_star_big_off_holo_light);
				Toast.makeText(getApplicationContext(),
						"Removed from Favorites", Toast.LENGTH_SHORT).show();
			}
		}

	}

	/* *Load prediction data asynchronously. */
	private class LoadPredictionAsyncTask extends
			AsyncTask<String, Void, ArrayList<String>> {
		Context ctx;

		public LoadPredictionAsyncTask(Context ctx) {
			this.ctx = ctx;
		}

		/* Get the data. */
		protected ArrayList<String> doInBackground(String... values) {
			ArrayList<String> predictions = APIController.getPrediction(
					values[0], values[1], values[2]);
			return predictions;
		}

		/* Update the UI */
		public void onPostExecute(ArrayList<String> predictions) {
			routeViewProgressBar.setVisibility(View.INVISIBLE);

			if (predictions.size() == 1 && predictions.get(0).equals("-1")) {
				Toast.makeText(ctx, "Error, Server Down?", Toast.LENGTH_LONG)
						.show();
				predictions.clear();
			}

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
