package com.doug.nextbus.backend;

import android.content.Intent;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.doug.nextbus.R;
import com.doug.nextbus.RoboSherlock.RoboSherlockActivity;
import com.doug.nextbus.activities.CreditsActivity;
import com.doug.nextbus.activities.FavoritesActivity;
import com.doug.nextbus.activities.MapViewActivity;
import com.doug.nextbus.activities.PreferencesActivity;

public class MenuClass {

	public static boolean onCreateOptionsMenu(RoboSherlockActivity ctx,
			Menu menu, int menuRes, int[] disabledItems) {
		ctx.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		ctx.getSupportMenuInflater().inflate(menuRes, menu);
		for (int disabledItem : disabledItems)
			menu.findItem(disabledItem).setVisible(false);
		return true;
	}

	public static boolean onOptionsItemSelected(RoboSherlockActivity ctx,
			MenuItem item) {
		switch (item.getItemId()) {
		case R.id.aboutmenusitem:
			Intent aboutActivity = new Intent(ctx, CreditsActivity.class);
			ctx.startActivity(aboutActivity);
			return true;
		case R.id.preferencesmenuitem:
			Intent preferenceIntent = new Intent(ctx, PreferencesActivity.class);
			ctx.startActivity(preferenceIntent);
			return true;
		case R.id.favoritesitem:
			Intent favoriteIntent = FavoritesActivity.createIntent(ctx);
			ctx.startActivity(favoriteIntent);
			return true;
		case R.id.mapsitem:
			Intent mapIntent = new Intent(ctx.getApplicationContext(),
					MapViewActivity.class);
			ctx.startActivity(mapIntent);
			return true;
		case android.R.id.home:
			ctx.finish();
			return true;
		default:
			return false;
		}
	}
}
