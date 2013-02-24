package com.doug.nextbus.custom;

import roboguice.activity.RoboActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.doug.nextbus.R;

public class BackButtonOnTouchListener implements OnTouchListener {

	private final RoboActivity mCtx;
	private final ImageView backButton;

	public BackButtonOnTouchListener(RoboActivity mCtx, ImageView backButton) {
		super();
		this.mCtx = mCtx;
		this.backButton = backButton;
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			backButton.setBackgroundColor(mCtx.getResources().getColor(
					R.color.black));
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			backButton.setBackgroundColor(0);
			mCtx.finish();
		}
		return true;
	}

}
