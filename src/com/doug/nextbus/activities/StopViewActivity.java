package com.doug.nextbus.activities;

import java.util.ArrayList;

import roboguice.inject.InjectView;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
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

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.doug.nextbus.R;
import com.doug.nextbus.RoboSherlock.RoboSherlockActivity;
import com.doug.nextbus.backend.APIController;
import com.doug.nextbus.backend.Data;
import com.doug.nextbus.backend.Favorite;
import com.doug.nextbus.backend.MenuClass;
import com.doug.nextbus.backend.RouteDataGSON.Route.Direction;
import com.doug.nextbus.backend.RouteDataGSON.Route.Stop;
import com.doug.nextbus.backend.RouteDirectionStop;
import com.doug.nextbus.custom.ArrivalsAdapter;

/* This activity displays the predictions for a the current stop */
public class StopViewActivity extends RoboSherlockActivity implements
		OnSharedPreferenceChangeListener {

	@InjectView(R.id.firstArrival) private TextView firstArrival;
	@InjectView(R.id.secondArrival) private TextView secondArrival;
	@InjectView(R.id.thirdArrival) private TextView thirdArrival;
	@InjectView(R.id.fourthArrival) private TextView fourthArrival;
	@InjectView(R.id.stopTextView) private TextView stopTextView;
	@InjectView(R.id.drawerTextView) private TextView drawerHandleTextView;

	@InjectView(R.id.refreshButton) private ImageView refreshButton;

	@InjectView(R.id.routeviewprogressbar) private ProgressBar routeViewProgressBar;

	@InjectView(R.id.colorbar) private View colorBar;
	@InjectView(R.id.colorSeperator) private View colorSeperator;
	@InjectView(R.id.arrivalsDrawer) private SlidingDrawer arrivalsDrawer;
	@InjectView(R.id.arrivalList) private ListView arrivalList;
	@InjectView(R.id.favoriteButton) private ImageButton favoriteButton;

	@InjectView(R.id.footer_redcell) private View mFooterRedCell;
	@InjectView(R.id.footer_bluecell) private View mFooterBlueCell;
	@InjectView(R.id.footer_yellowcell) private View mFooterYellowCell;
	@InjectView(R.id.footer_greencell) private View mFooterGreenCell;

	private static final String ROUTE_TAG_KEY = "routeTag";
	private static final String DIRECTION_TITLE_KEY = "directionTitle";
	private static final String DIRECTION_TAG_KEY = "directionTag";
	private static final String STOP_TITLE_KEY = "stopTitle";
	private static final String STOP_TAG_KEY = "stopTag";

	private String mRouteTag;
	private String mDirectionTitle;
	private String mDirectionTag;
	private String mStopTitle;
	private String mStopTag;

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
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return intent;
	}

	public static Intent createIntent(Context ctx, Favorite favorite) {
		Intent intent = new Intent(ctx, StopViewActivity.class);
		intent.putExtra(ROUTE_TAG_KEY, favorite.routeTag);
		intent.putExtra(DIRECTION_TITLE_KEY, favorite.directionTitle);
		intent.putExtra(DIRECTION_TAG_KEY, favorite.directionTag);
		intent.putExtra(STOP_TITLE_KEY, favorite.stopTitle);
		intent.putExtra(STOP_TAG_KEY, favorite.stopTag);
		// Closes all instances of the same activity
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return intent;
	}

	@Override
	public void onCreate(Bundle savedInstance) {

		super.onCreate(savedInstance);
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
		setViewColor();

		setupArrivals();

		// Setting text views to default values
		firstArrival.setText("");
		secondArrival.setText("");
		thirdArrival.setText("");
		fourthArrival.setText("");

		Favorite favorite = new Favorite(mRouteTag, mDirectionTag,
				mDirectionTitle, mStopTag, mStopTitle);

		refresh();

		int starImageResource = R.drawable.favorite_toadd;
		if (Data.isFavorite(favorite)) {
			starImageResource = R.drawable.favorite_toremove;
		}
		favoriteButton.setImageResource(starImageResource);

		setEventListeners(favorite);

		// Setting colors, are needed because of view recycling.
		mFooterBlueCell.setBackgroundColor(getResources()
				.getColor(R.color.blue));
		mFooterRedCell.setBackgroundColor(getResources().getColor(R.color.red));
		mFooterYellowCell.setBackgroundColor(getResources().getColor(
				R.color.yellow));
		mFooterGreenCell.setBackgroundColor(getResources().getColor(
				R.color.green));
	}

	private void setEventListeners(Favorite favorite) {

		refreshButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				routeViewProgressBar.setVisibility(View.VISIBLE);
				refresh();
			}
		});

		favoriteButton.setOnClickListener(new CustomOnClickListener(favorite));

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
					return; //
				}
				Intent intent = StopViewActivity
						.createIntent(getApplicationContext(),
								mRdsArray[position].route.tag,
								mRdsArray[position].direction,
								mRdsArray[position].stop);
				startActivity(intent);
			}
		});

	}

	public void setupArrivals() {
		// TODO: check if the preferences is being used
		mRdsArray = Data.getAllRdsWithStopTitle(mStopTitle, mRouteTag,
				mDirectionTag);

		if (mRdsArray.length == 0) {
			arrivalsDrawer.setVisibility(View.INVISIBLE);
			return;
		} else {
			arrivalsDrawer.setVisibility(View.VISIBLE);
		}

		arrivalList.setAdapter(new ArrivalsAdapter(getApplicationContext(),
				mRdsArray));

		arrivalList.setOnItemClickListener(null);

		/* If a cell is clicked, open the stop for that route */
		arrivalList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (!mRdsArray[0].equals("No other arrivals")) {
					RouteDirectionStop rds = mRdsArray[position];
					Intent intent = StopViewActivity.createIntent(
							getApplicationContext(), rds.route.tag,
							rds.direction, rds.stop);
					startActivity(intent);
				}
			}
		});
	}

	/** Gets the latest prediction data */
	private void refresh() {
		new LoadPredictionAsyncTask(this).execute(mRouteTag, mDirectionTag,
				mStopTag);
	}

	/** Set color of text with respect to current routeTag */
	private void setViewColor() {
		int color = Data.getColorFromRouteTag(mRouteTag);
		stopTextView.setTextColor(getResources().getColor(color));
		colorBar.setBackgroundColor(getResources().getColor(color));
		colorSeperator.setBackgroundColor(getResources().getColor(color));
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
						.setImageResource(R.drawable.favorite_toremove);
				Toast.makeText(getApplicationContext(), "Added to Favorites",
						Toast.LENGTH_SHORT).show();
			} else {
				((ImageButton) v).setImageResource(R.drawable.favorite_toadd);
				Toast.makeText(getApplicationContext(),
						"Removed from Favorites", Toast.LENGTH_SHORT).show();
			}
		}

	}

	/** Load prediction data asynchronously. */
	private class LoadPredictionAsyncTask extends
			AsyncTask<String, Void, ArrayList<String>> {
		Context ctx;

		public LoadPredictionAsyncTask(Context ctx) {
			this.ctx = ctx;
		}

		/* Get the data. */
		@Override
		protected ArrayList<String> doInBackground(String... values) {
			ArrayList<String> predictions = APIController.getPrediction(
					values[0], values[1], values[2]);
			return predictions;
		}

		/* Update the UI */
		@Override
		public void onPostExecute(ArrayList<String> predictions) {
			routeViewProgressBar.setVisibility(View.INVISIBLE);
			try {
				if (predictions.size() == 1 && predictions.get(0).equals("-1")) {
					Toast.makeText(ctx, "Error, Server Down?",
							Toast.LENGTH_LONG).show();
					predictions.clear();
				}

				if (predictions.size() == 0
						|| predictions.get(0).equals("error")) {
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
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
