package com.turnerapac.adultswimau.apps.generic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.turnerapac.adultswimau.apps.generic.R;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.turnerapac.adultswimau.apps.generic.model.MVApplication;
import com.turnerapac.adultswimau.apps.generic.service.MVAppInitRequest;
import com.turnerapac.adultswimau.apps.generic.service.XmlSpiceService;

public class SplashScreenActivity extends Activity {

	protected SpiceManager spiceManager = new SpiceManager(
			XmlSpiceService.class);

	private Logger mLog;

	private ImageView mSplashImageView;

	// Splash screen timer
	private static int SPLASH_TIME_OUT = 3000;

	private Handler handler = new Handler();
	private Runnable runnable = new Runnable() {

		public void run() {
			// This method will be executed once the timer is over
			// Start app main activity
			Intent homeIntent = new Intent(getBaseContext(), HomeActivity.class);
			startActivity(homeIntent);
			// close this activity
			finish();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (AppHelper.isTablet(this))
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
		setContentView(R.layout.activity_splash);
		mLog = LoggerFactory.getLogger(SplashScreenActivity.class);
		mSplashImageView = (ImageView) findViewById(R.id.imgSplash);
		if (AppHelper.isTablet(this)) {

			UrlImageViewHelper.setUseBitmapScaling(false);

			if (getResources().getBoolean(R.bool.portrait))
				UrlImageViewHelper.setUrlDrawable(mSplashImageView,
						Constants.SPLASH_SCREEN_IMAGE_URL_TABLET_PORTRAIT,
						R.drawable.splashscreen_tablet_portrait, 0,
						new UrlImageViewCallback() {

							public void onLoaded(ImageView imageView,
									Bitmap loadedBitmap, String url,
									boolean loadedFromCache) {

								requestToken();

							}
						});
			else
				UrlImageViewHelper.setUrlDrawable(mSplashImageView,
						Constants.SPLASH_SCREEN_IMAGE_URL_TABLET_LANDSCAPE,
						R.drawable.splash_screen_land, 0,
						new UrlImageViewCallback() {

							public void onLoaded(ImageView imageView,
									Bitmap loadedBitmap, String url,
									boolean loadedFromCache) {

								requestToken();

							}
						});

		} else
			UrlImageViewHelper.setUrlDrawable(mSplashImageView,
					Constants.SPLASH_SCREEN_IMAGE_URL_PHONE,
					R.drawable.splash_screen, 0, new UrlImageViewCallback() {

						public void onLoaded(ImageView imageView,
								Bitmap loadedBitmap, String url,
								boolean loadedFromCache) {

							requestToken();

						}
					});

	}

	private void requestToken() {
		if (AppHelper.isTablet(this)) {
			mLog.info("Calling App Init For Tablet Version");
			spiceManager.execute(new MVAppInitRequest(
					Constants.MOVIDEO_APP_ALIAS_TABLET,
					Constants.MOVIDEO_APP_KEY), "NOCACHE",
					DurationInMillis.ALWAYS_EXPIRED,
					new MVAppInitRequestListener());
		} else {
			mLog.info("Calling App Init For Phone Version");
			spiceManager.execute(new MVAppInitRequest(
					Constants.MOVIDEO_APP_ALIAS_PHONE,
					Constants.MOVIDEO_APP_KEY), "NOCACHE",
					DurationInMillis.ALWAYS_EXPIRED,
					new MVAppInitRequestListener());
		}
	}

	private class MVAppInitRequestListener implements
			RequestListener<MVApplication> {

		public void onRequestFailure(SpiceException spiceException) {
			mLog.error("App init Failed ", spiceException.getMessage());
			if (spiceException.getMessage().equals("Network is not available"))
				showAlert(getResources()
						.getString(R.string.network_error_title),
						getResources()
								.getString(R.string.network_error_message));
			else {
				showAlert(getResources().getString(R.string.alert_title_error),
						getResources().getString(R.string.system_busy_error));
			}

		}

		public void onRequestSuccess(MVApplication app) {
			mLog.info("App init Success");
			if (app != null && app.getToken() != null) {
				/**
				 * Remove all previously cached data
				 */
				spiceManager.removeAllDataFromCache();
				SharedPreferences prefs = getSharedPreferences(
						Constants.PREFS_NAME, 0);
				prefs.edit().putString(Constants.MOVIDEO_TOKEN, app.getToken())
						.commit();
				/*
				 * Showing splash screen with a timer.
				 */
				handler.postDelayed(runnable, SPLASH_TIME_OUT);
			}

		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		spiceManager.start(this);
	}

	@Override
	protected void onStop() {
		spiceManager.shouldStop();
		// Remove callbacks if it is in progress
		if (runnable != null)
			handler.removeCallbacks(runnable);
		super.onStop();
	}

	private void showAlert(String title, String message) {
		new AlertDialog.Builder(this)
				.setTitle(title)
				.setMessage(message)
				.setPositiveButton("Retry",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								requestToken();
							}
						})
				.setNegativeButton("Exit",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}
						}).show();

	}
}
