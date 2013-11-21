package com.turnerapac.adultswimau.apps.generic;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.util.DisplayMetrics;

import com.turnerapac.adultswimau.apps.generic.R;
import com.google.ads.doubleclick.DfpAdView;
import com.google.analytics.tracking.android.MapBuilder;

/*
 * Helper class for application.
 */
public class AppHelper {

	public static boolean isTablet(Context context) {
		// Compute screen size
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		float screenWidth = dm.widthPixels / dm.xdpi;
		float screenHeight = dm.heightPixels / dm.ydpi;
		double size = Math.sqrt(Math.pow(screenWidth, 2)
				+ Math.pow(screenHeight, 2));
		// Tablet devices should have a screen size greater than 6 inches
		return size >= 6;

	}

	public static String getToken(Context context) {
		return context.getSharedPreferences(Constants.PREFS_NAME, 0).getString(
				Constants.MOVIDEO_TOKEN, "");

	}

	public static boolean is_Xlarge(Context context) {
		// Compute screen size
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		float scaleFactor = dm.density;
		float screenWidth = dm.widthPixels / scaleFactor;
		float screenHeight = dm.heightPixels / scaleFactor;
		float smallestWidth = Math.min(screenWidth, screenHeight);
		if (smallestWidth >= 720)// If 10" tab
			return true;
		else
			return false;

	}

	/*
	 * Convert second to minute
	 */
	public static String getTime(String time) {
		try {
			float timeInSeconds = Float.parseFloat(time);

			int hours = (int) timeInSeconds / 3600;
			int minutes = (int) (timeInSeconds % 3600) / 60;
			int seconds = (int) timeInSeconds % 60;

			if (hours > 0)
				return twoDigitString(hours) + " : " + twoDigitString(minutes)
						+ " : " + twoDigitString(seconds);
			else
				return twoDigitString(minutes) + " : "
						+ twoDigitString(seconds);
		} catch (NumberFormatException ee) {
			return time;
		}
	}

	private static String twoDigitString(int number) {

		if (number == 0) {
			return "00";
		}

		if (number / 10 == 0) {
			return "0" + number;
		}

		return String.valueOf(number);
	}

	/*
	 * Get AdView based on Device.
	 */
	public static DfpAdView getAdView(Activity context) {
		DfpAdView mAdView = null;
		if (isTablet(context)) {
			if (!is_Xlarge(context)
					&& context.getResources().getBoolean(R.bool.portrait))
				mAdView = new DfpAdView(context, Constants.AD_SIZE_PHONE,
						Constants.AD_UNIT_ID_PHONE);
			else
				mAdView = new DfpAdView(context, Constants.AD_SIZE_TABLET,
						Constants.AD_UNIT_ID_TABLET);
		} else
			mAdView = new DfpAdView(context, Constants.AD_SIZE_PHONE,
					Constants.AD_UNIT_ID_PHONE);
		return mAdView;
	}

	/*
	 * Get AdView based on Device for promoted.
	 */
	public static DfpAdView getCarouselAdView(Activity context) {
		DfpAdView mAdView = null;
		if (isTablet(context)) {
			if (context.getResources().getBoolean(R.bool.portrait)) {
				if (is_Xlarge(context))
					mAdView = new DfpAdView(context,
							Constants.CAROUSEL_AD_SIZE_TABLET_PORTRAIT,
							Constants.AD_UNIT_ID_TABLET);
				else
					mAdView = new DfpAdView(context,
							Constants.CAROUSEL_AD_SIZE_PHONE,
							Constants.AD_UNIT_ID_PHONE);

			} else {
				mAdView = new DfpAdView(context,
						Constants.CAROUSEL_AD_SIZE_TABLET_LANDSCAPE,
						Constants.AD_UNIT_ID_TABLET);
			}
		} else
			mAdView = new DfpAdView(context, Constants.CAROUSEL_AD_SIZE_PHONE,
					Constants.AD_UNIT_ID_PHONE);
		return mAdView;
	}

	public static Builder getAlert(Context context, String title, String message) {
		return new AlertDialog.Builder(context).setTitle(title)
				.setMessage(message)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				});

	}

	/*
	 * GA Custom Events API
	 */
	public static void sendGoogleAnyaticsEvent(String category, String action,
			String label) {
		AdultSwim.getGaTracker().send(
				MapBuilder.createEvent(category, action, label, null).build());

	}
}
