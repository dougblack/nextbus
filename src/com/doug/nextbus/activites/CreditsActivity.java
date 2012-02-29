package com.doug.nextbus.activites;

import com.doug.nextbus.R;
import com.doug.nextbus.R.id;
import com.doug.nextbus.R.layout;
import com.doug.nextbus.R.menu;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CreditsActivity extends Activity {

	static ListView contactList;
	static String[] contactItemStrings = { "@DougBlackGT", "dblackgt@gmail.com", "itsdoug.com", "Google+", "All data copyright Georgia Tech Campus 2011" };

	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.credits);

		contactList = (ListView) findViewById(R.id.contactList);

		contactList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contactItemStrings));

		contactList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				switch (position) {
				case 0:
					Intent openBrowserToTwitterProfile = new Intent(Intent.ACTION_VIEW, Uri
							.parse("http://twitter.com/DougBlackGT"));
					startActivity(openBrowserToTwitterProfile);
					break;
				case 1:
					Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

					emailIntent.setType("plain/text");
					emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { "dblackgt@gmail.com" });
					emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Feedback on GT NextBus");

					startActivity(emailIntent);
					break;
				case 2:
					Intent openBrowserToItsDoug = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.itsdoug.com"));
					startActivity(openBrowserToItsDoug);
					break;
				case 3:
					Intent openBrowserToGooglePlus = new Intent(Intent.ACTION_VIEW, Uri
							.parse("https://profiles.google.com/u/0/101188344020658014431"));
					startActivity(openBrowserToGooglePlus);
					break;
				}

			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.about_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.tohome:
			Intent intent = new Intent(getApplicationContext(), RoutePickerActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}