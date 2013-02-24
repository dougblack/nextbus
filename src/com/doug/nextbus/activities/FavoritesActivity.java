package com.doug.nextbus.activities;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.doug.nextbus.R;
import com.doug.nextbus.backend.Data;
import com.doug.nextbus.backend.Favorite;
import com.doug.nextbus.custom.BackButtonOnTouchListener;
import com.doug.nextbus.custom.FavoritesAdapter;

public class FavoritesActivity extends RoboActivity {

	@InjectView(R.id.stopListView) private ListView stopListView;
	@InjectView(R.id.directionTextView) private TextView directionTextView;
	@InjectView(R.id.colorbar) private View colorBar;
	@InjectView(R.id.directionBackButton) private ImageView backButton;
	@InjectView(R.id.titleText) private TextView titleText;

	FavoritesAdapter mFavoritesAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stop_list);

		titleText.setText("FAVORITES");
		// directionTextView.setText("Favorites");

		((LinearLayout) directionTextView.getParent())
				.removeView(directionTextView);

		mFavoritesAdapter = new FavoritesAdapter(getApplicationContext());

		/*
		 * Making the colorbar smaller height, cleaner look. Maybe make new
		 * layout file?
		 */
		colorBar.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, 3));

		stopListView.setAdapter(mFavoritesAdapter);
		setEventListeners();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// In case any preferences were changed.
		mFavoritesAdapter.notifyDataSetChanged();
	}

	private void setEventListeners() {
		stopListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Favorite favorite = Data.getFavorite(position);
				startActivity(StopViewActivity.createIntent(
						getApplicationContext(), favorite));
			}
		});

		backButton.setOnTouchListener(new BackButtonOnTouchListener(this,
				backButton));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.stock_menu, menu);
		return true;
	}

}
