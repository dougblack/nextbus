package com.doug.nextbus.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.doug.nextbus.R;

/* This activity is useless since no preferences actually work. TODO? */
public class PreferencesActivity extends PreferenceActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

}
