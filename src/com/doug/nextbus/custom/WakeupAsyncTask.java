package com.doug.nextbus.custom;

import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;
import android.util.Log;

/**
 * A Heroku will fall asleep, so it needs a request to wake up. This should be
 * done before any requests are made to teh server
 */
public class WakeupAsyncTask extends AsyncTask<Void, Void, Void> {

	@Override
	protected Void doInBackground(Void... params) {
		wakeupRequest();
		return null;
	}

	public static void wakeupRequest() {
		String target = "http://desolate-escarpment-6039.herokuapp.com/metacritic_review/";
		try {
			URL url = new URL(target);
			URLConnection urlConnection = url.openConnection();
			urlConnection.setReadTimeout(1000);
			urlConnection.setConnectTimeout(1000);

			// opening a connection to wake up dyno
			urlConnection.getInputStream();

			return;

		} catch (Exception e) {
			e.printStackTrace();
			Log.d("error", e.getClass().toString());
		}

	}
}
