package com.turnerapac.adultswimau.apps.generic;

import java.lang.reflect.Field;

import android.app.Application;
import android.view.ViewConfiguration;

import com.google.analytics.tracking.android.GAServiceManager;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.testflightapp.lib.TestFlight;

public class AdultSwim extends Application {
	private static GoogleAnalytics mGa;

	private static Tracker mTracker;

	@Override
	public void onCreate() {
		super.onCreate();
		// Initialize TestFlight with application token.
		TestFlight.takeOff(this, Constants.TESTFLIGHT_APPTOKEN);

		// Google Analytics initialization
		initializeGa();

		// Force the Application to show the overflow menu

		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception ex) {
			// Ignore
		}
	}

	/**
	 * Method to handle basic Google Analytics initialization. This call will
	 * not block as all Google Analytics work occurs off the main thread.
	 */
	@SuppressWarnings("deprecation")
	private void initializeGa() {
		mGa = GoogleAnalytics.getInstance(this);
		if (!AppHelper.isTablet(AdultSwim.this)) {
			mTracker = mGa.getTracker(Constants.GA_PROPERTY_ID_PHONE);
		} else {
			mTracker = mGa.getTracker(Constants.GA_PROPERTY_TABLET);
		}
		// Set dispatch period.
		GAServiceManager.getInstance().setLocalDispatchPeriod(
				Constants.GA_DISPATCH_PERIOD);

		// Set dryRun flag.
		mGa.setDryRun(Constants.GA_IS_DRY_RUN);

		// Set Logger level.
		mGa.getLogger().setLogLevel(Constants.GA_LOG_VERBOSITY);

	}

	/*
	 * Returns the Google Analytics tracker.
	 */
	public static Tracker getGaTracker() {
		return mTracker;
	}

	/*
	 * Returns the Google Analytics instance.
	 */
	public static GoogleAnalytics getGaInstance() {
		return mGa;
	}

}
