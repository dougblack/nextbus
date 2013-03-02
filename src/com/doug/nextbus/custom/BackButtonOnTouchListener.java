package com.doug.nextbus.custom;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockActivity;
import com.doug.nextbus.R;
import com.doug.nextbus.RoboSherlock.RoboSherlockActivity;

public class BackButtonOnTouchListener implements OnTouchListener {

	private final SherlockActivity mCtx;
	private final ImageView backButton;

	public BackButtonOnTouchListener(SherlockActivity mCtx, ImageView backButton) {
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
