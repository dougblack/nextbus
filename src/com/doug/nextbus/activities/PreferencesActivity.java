package com.doug.nextbus.activities;

import com.doug.nextbus.R;
import com.doug.nextbus.R.xml;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/* This activity is useless since no preferences actually work. TODO? */
public class PreferencesActivity extends PreferenceActivity {
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
	
}
