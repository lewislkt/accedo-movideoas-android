package com.turnerapac.adultswimau.apps.generic.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import com.turnerapac.adultswimau.apps.generic.R;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpStatusCodes;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.listener.RequestProgress;
import com.octo.android.robospice.request.listener.RequestProgressListener;
import com.turnerapac.adultswimau.apps.generic.AppHelper;
import com.turnerapac.adultswimau.apps.generic.Constants;
import com.turnerapac.adultswimau.apps.generic.model.MVApplication;

public abstract class ServiceRequestListener<T> implements RequestListener<T>, RequestProgressListener {

	private Context mContext;

	private Logger mLog;

	private SpiceManager mSpiceManager = new SpiceManager(XmlSpiceService.class);

	public ServiceRequestListener(Context context) {
		this.mContext = context;
		mLog = LoggerFactory.getLogger(ServiceRequestListener.class);

	}

	public abstract void onRequestFailure(String message);

	public abstract void onRetryRequest();

	public void onRequestProgressUpdate(RequestProgress progress) {

		switch (progress.getStatus()) {
		case READING_FROM_CACHE:
			mLog.debug("READING_FROM_CACHE");
			break;
		case LOADING_FROM_NETWORK:
			mLog.debug("LOADING_FROM_NETWORK");
			break;
		case COMPLETE:
			break;
		case PENDING:
			break;
		case WRITING_TO_CACHE:
			break;
		default:
			break;
		}
	}

	public void onRequestFailure(SpiceException spiceException) {

		mLog.error("Request Failed", spiceException);

		if (spiceException.getMessage().equals(Constants.ROBOSPICE_NO_NETWORK_MESSAGE)) {
			onRequestFailure(spiceException.getMessage());
			showAlert(mContext.getResources().getString(R.string.network_error_title), mContext.getResources()
					.getString(R.string.network_error_message));
		}
		else if (spiceException.getCause() instanceof IOException) {
			IOException ioException = (IOException) spiceException.getCause();
			if (ioException.getMessage().equals(Constants.GOOGLE_HTTP_CLIENT_401_ERROR_MESSAGE))
				getNewToken();
			else {
				onRequestFailure(spiceException.getMessage());
				showAlert(mContext.getResources().getString(R.string.alert_title_error), mContext.getResources()
						.getString(R.string.system_busy_error));
			}
		}
		else if (spiceException.getCause() instanceof HttpResponseException) {
			HttpResponseException exception = (HttpResponseException) spiceException.getCause();
			if (exception.getStatusCode() == (HttpStatusCodes.STATUS_CODE_UNAUTHORIZED))
				getNewToken();
			else if (exception.getStatusCode() == (HttpStatusCodes.STATUS_CODE_SERVICE_UNAVAILABLE)) {
				onRequestFailure(spiceException.getMessage());
				showAlert(mContext.getResources().getString(R.string.alert_title_error), mContext.getResources()
						.getString(R.string.system_busy_error));
			}
			else {
				onRequestFailure(spiceException.getMessage());
				showAlert(mContext.getResources().getString(R.string.alert_title_error), mContext.getResources()
						.getString(R.string.system_busy_error));
			}
		}
		else {
			onRequestFailure(spiceException.getMessage());
			showAlert(mContext.getResources().getString(R.string.alert_title_error),
					mContext.getResources().getString(R.string.system_busy_error));
		}

	}

	private void getNewToken() {
		mSpiceManager.start(mContext);
		mLog.info("Token Expired. Getting New One..");
		if (AppHelper.isTablet(mContext)) {
			mLog.info("Refreshing App Init For Tablet Version");
			mSpiceManager.execute(new MVAppInitRequest(Constants.MOVIDEO_APP_ALIAS_TABLET, Constants.MOVIDEO_APP_KEY),
					"NOCACHE", DurationInMillis.ALWAYS_EXPIRED, new MVAppInitRequestListener());
		}
		else {
			mLog.info("Refreshing App Init For Phone Version");
			mSpiceManager.execute(new MVAppInitRequest(Constants.MOVIDEO_APP_ALIAS_PHONE, Constants.MOVIDEO_APP_KEY),
					"NOCACHE", DurationInMillis.ALWAYS_EXPIRED, new MVAppInitRequestListener());
		}

		// Intent splashIntent = new Intent(mContext, SplashScreenActivity.class);
		// mContext.startActivity(splashIntent);
	}

	private void showAlert(String title, String message) {
		new AlertDialog.Builder(mContext).setTitle(title).setMessage(message)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						onRetryRequest();
					}
				})
				/*
				 * .setPositiveButton("Retry", new DialogInterface.OnClickListener() { public void
				 * onClick(DialogInterface dialog, int which) { onRetryRequest(); } }) .setNegativeButton("No", new
				 * DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int which) { } })
				 */
				.show();
	}

	private void requestFailed(String message) {
		onRequestFailure(message);
	}

	private class MVAppInitRequestListener implements RequestListener<MVApplication> {

		public void onRequestFailure(SpiceException spiceException) {
			mLog.error("App init Failed ", spiceException.getMessage());
			requestFailed(spiceException.getMessage());
			if (spiceException.getMessage().equals(Constants.ROBOSPICE_NO_NETWORK_MESSAGE))
				showAlert(mContext.getResources().getString(R.string.network_error_title), mContext.getResources()
						.getString(R.string.network_error_message));
			else {
				showAlert(mContext.getResources().getString(R.string.alert_title_error), mContext.getResources()
						.getString(R.string.system_busy_error));
			}
			mSpiceManager.shouldStop();
		}

		public void onRequestSuccess(MVApplication app) {
			mLog.info("App init Success");
			if (app != null && app.getToken() != null) {
				SharedPreferences prefs = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
				prefs.edit().putString(Constants.MOVIDEO_TOKEN, app.getToken()).commit();
				onRetryRequest();
			}

		}
	}

}
