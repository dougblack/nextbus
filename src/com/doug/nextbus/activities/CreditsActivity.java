package com.doug.nextbus.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.doug.nextbus.R;

/*
 * The credits activity. Shows simple contact links. 
 */
public class CreditsActivity extends Activity {

	static ListView contactList;
	static String[] contactItemStrings = { "@dougblackgt", "doug@dougblack.io",
			"dougblack.io", "Google+",
			"All data copyright Georgia Tech Campus 2011" };
	static ImageView backButton;

	public void onCreate(Bundle savedInstance) {

		super.onCreate(savedInstance);
		setContentView(R.layout.credits);

		contactList = (ListView) findViewById(R.id.contactList);
		backButton = (ImageView) findViewById(R.id.creditsBackButton);

		contactList.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, contactItemStrings));

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

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.about_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.tohome:
			Intent intent = new Intent(getApplicationContext(),
					RoutePickerActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
