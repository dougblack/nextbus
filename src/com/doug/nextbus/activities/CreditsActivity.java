package com.doug.nextbus.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.doug.nextbus.R;
import com.doug.nextbus.RoboSherlock.RoboSherlockActivity;

/*
 * The credits activity. Shows simple contact links. 
 */
public class CreditsActivity extends RoboSherlockActivity {

	static ListView contactList;
	static String[] contactItemStrings = { "@dougblackgt", "doug@dougblack.io",
			"dougblack.io", "Google+",
			"All data copyright Georgia Tech Campus 2011" };

	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.credits);

		contactList = (ListView) findViewById(R.id.contactList);

		contactList.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, contactItemStrings));

		contactList.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				/* Handles contact-link clicks. */
				switch (position) {

				/* TWITTER */
				case 0:
					Intent openBrowserToTwitterProfile = new Intent(
							Intent.ACTION_VIEW, Uri
									.parse("http://twitter.com/DougBlackGT"));
					startActivity(openBrowserToTwitterProfile);
					break;

				/* EMAIL */
				case 1:
					Intent emailIntent = new Intent(
							android.content.Intent.ACTION_SEND);
					emailIntent.setType("plain/text");
					emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
							new String[] { "doug@dougblack.io" });
					emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
							"Feedback on GT NextBus");
					startActivity(emailIntent);
					break;
				/* WEBSITE */
				case 2:
					Intent openBrowserToSite = new Intent(Intent.ACTION_VIEW,
							Uri.parse("http://www.dougblack.io"));
					startActivity(openBrowserToSite);
					break;

				/* GOOGLE+ */
				case 3:
					Intent openBrowserToGooglePlus = new Intent(
							Intent.ACTION_VIEW,
							Uri.parse("https://profiles.google.com/u/0/101188344020658014431"));
					startActivity(openBrowserToGooglePlus);
					break;
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportMenuInflater().inflate(R.menu.about_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.aboutmenusitem:
			Intent aboutActivity = new Intent(this, CreditsActivity.class);
			startActivity(aboutActivity);
			return true;
		case R.id.preferencesmenuitem:
			Intent preferenceIntent = new Intent(this,
					PreferencesActivity.class);
			startActivity(preferenceIntent);
			return true;
		case R.id.favoritesitem:
			Intent favoriteIntent = new Intent(getApplicationContext(),
					FavoritesActivity.class);
			startActivity(favoriteIntent);
			return true;
		case R.id.mapsitem:
			Intent mapIntent = new Intent(getApplicationContext(),
					MapViewActivity.class);
			startActivity(mapIntent);
			return true;
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
