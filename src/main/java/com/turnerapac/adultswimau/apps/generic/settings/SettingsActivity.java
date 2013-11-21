package com.turnerapac.adultswimau.apps.generic.settings;

import android.app.ActionBar;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;
import com.turnerapac.adultswimau.apps.generic.R;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.turnerapac.adultswimau.apps.generic.AdultSwim;
import com.turnerapac.adultswimau.apps.generic.AppHelper;
import com.turnerapac.adultswimau.apps.generic.BaseActivity;

public class SettingsActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// If not tablet, set screen orientation to portrait
		if (AppHelper.isTablet(this))
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
		setContentView(R.layout.activity_settings);
		/**
		 * Actionbar settings
		 */
		ActionBar actionBar = getActionBar();
		actionBar.setLogo(R.drawable.logo_selector);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(0);

		TextView mAppVersion = (TextView) findViewById(R.id.settings_appversion_txt);
		/**
		 * Get App Version
		 */
		PackageManager manager = this.getPackageManager();
		PackageInfo info = null;
		try {
			info = manager.getPackageInfo(this.getPackageName(), 0);
		} catch (NameNotFoundException e) {

			e.printStackTrace();
		}
		if (info != null)
			mAppVersion.setText(info.versionName);
	}

	@Override
	public void onStart() {
		super.onStart();

		AdultSwim.getGaTracker().set(Fields.SCREEN_NAME, "settingsPage");

		AdultSwim.getGaTracker().send(MapBuilder.createAppView().build());
	}
}