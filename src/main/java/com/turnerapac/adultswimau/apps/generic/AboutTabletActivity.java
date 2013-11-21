package com.turnerapac.adultswimau.apps.generic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;

public class AboutTabletActivity extends BaseActivity {

	private Logger mLog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLog = LoggerFactory.getLogger(AboutTabletActivity.class);
		setContentView(R.layout.activity_about);

		/**
		 * Get App Version
		 */
		PackageManager manager = this.getPackageManager();
		PackageInfo info = null;
		try {
			info = manager.getPackageInfo(this.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			mLog.error("Getting Package Name Failed", e);
		}

		TextView mVersionTextView = (TextView) findViewById(R.id.txt_version);
		if (info != null)
			mVersionTextView.setText("Version " + info.versionName);

		LinearLayout mMainLayout = (LinearLayout) findViewById(R.id.lyt_main);
		mMainLayout.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View arg0, MotionEvent arg1) {
				/**
				 * Finish the activity on clicking outside the pop up text area
				 */
				finish();
				return false;
			}
		});
		RelativeLayout mContentLayout = (RelativeLayout) findViewById(R.id.lyt_content);
		mContentLayout.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View arg0, MotionEvent arg1) {
				/**
				 * Prevent finishing the activity on clicking the pop up text
				 * area
				 */
				return true;
			}
		});

	}

	/** Sending the screen view to GA */
	@Override
	public void onStart() {
		super.onStart();

		AdultSwim.getGaTracker().set(Fields.SCREEN_NAME, "AboutPage");
		AdultSwim.getGaTracker().send(MapBuilder.createAppView().build());
	}

}
