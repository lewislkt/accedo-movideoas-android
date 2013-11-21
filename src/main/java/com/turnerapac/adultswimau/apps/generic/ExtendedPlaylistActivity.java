package com.turnerapac.adultswimau.apps.generic;

import java.io.Serializable;
import java.util.ArrayList;

import org.slf4j.LoggerFactory;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.turnerapac.adultswimau.apps.generic.R;
import com.accedo.fontface.CustomFonts;
import com.google.ads.AdRequest;
import com.google.ads.doubleclick.DfpAdView;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.turnerapac.adultswimau.apps.generic.model.MVPlaylistChild;
import com.turnerapac.adultswimau.apps.generic.model.MVPlaylistMedia;
import com.turnerapac.adultswimau.apps.generic.model.MVPlaylistMediaList;
import com.turnerapac.adultswimau.apps.generic.service.MVPlaylistMediaRequest;
import com.turnerapac.adultswimau.apps.generic.service.ServiceRequestListener;

public class ExtendedPlaylistActivity extends BaseActivity implements
		OnScrollListener {

	private TextView mNameTextView;

	org.slf4j.Logger mLog;

	private CustomFonts customFonts;

	private ProgressDialog mProgressDialog;

	private DfpAdView mAdView;

	private ListView mListViewPlaylists;

	private MVPlaylistChild mPlaylistChild;

	private ArrayList<MVPlaylistMedia> mMediaArray;

	private String mGALabel;

	private View mSelectedView;

	private boolean mApiCallInProgress = false;

	private int mPageCount = 0;

	private ProgressBar mProgress;

	private ExtendedPlaylistArrayAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// If not tablet, set screen orientation to portrait
		if (AppHelper.isTablet(this))
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
		setContentView(R.layout.activity_extended_playlist);
		customFonts = new CustomFonts(this);
		// Get banner ad
		mAdView = AppHelper.getAdView(this);
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.addlayout);
		layout.addView(mAdView);
		// Load banner ad
		mAdView.loadAd(new AdRequest());

		mLog = LoggerFactory.getLogger(ExtendedPlaylistActivity.class);

		/**
		 * Actionbar settings
		 */
		ActionBar actionBar = getActionBar();
		actionBar
				.setLogo(AppHelper.isTablet(this) ? R.drawable.tab_logo_selector
						: R.drawable.logo_selector);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(0);

		mNameTextView = (TextView) findViewById(R.id.txt_playlist);
		mNameTextView.setTypeface(customFonts.getHeadingfont());
		mListViewPlaylists = (ListView) findViewById(R.id.lv_playlist);
		mListViewPlaylists.setOnScrollListener(this);
		Serializable data = getIntent().getSerializableExtra("PlayListChild");
		mPlaylistChild = (MVPlaylistChild) data;
		mNameTextView.setText(mPlaylistChild.getTitle());
		mProgress = (ProgressBar) findViewById(R.id.progressBar1);

		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("Loading...");
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setCancelable(true);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.show();

		mLog.info("Playlist medias");
		/**
		 * API call
		 */
		spiceManager.getFromCacheAndLoadFromNetworkIfExpired(
				new MVPlaylistMediaRequest(AppHelper.getToken(this),
						mPlaylistChild.getId()),
				Constants.CACHE_KEY_FEATURED_PLAYLIST_MEDIA + mPageCount
						+ mPlaylistChild.getId(),
				DurationInMillis.ONE_MINUTE * 10,
				new MVPlaylistMediaRequestListener(this));

		mListViewPlaylists.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				mSelectedView = arg1;
				// Add clicked effect to selected view
				arg1.setAlpha((float) 0.5);

				MVPlaylistMedia media = (MVPlaylistMedia) arg0
						.getItemAtPosition(arg2);

				/**
				 * sending the google analytics play list
				 */
				mGALabel = MVTagHelper.getValue(media.getTags(),
						MVTagHelper.NS_SHOW, MVTagHelper.PREDICATE_NAME)
						+ ""
						+ MVTagHelper.getValue(media.getTags(),
								MVTagHelper.NS_SHOW,
								MVTagHelper.PREDICATE_SEASON)
						+ ""
						+ MVTagHelper.getValue(media.getTags(),
								MVTagHelper.NS_EPISODE,
								MVTagHelper.PREDICATE_NUMBER);
				AppHelper.sendGoogleAnyaticsEvent(
						Constants.GOOGLE_ANALYTICS_PLAYLIST_CATEGORY,
						Constants.GOOGLE_ANALYTICS_CLICK_ACTION, mGALabel);
				/*
				 * Append ids of all medias starting from the selected one
				 */
				String ids = null;
				int pos = mMediaArray.indexOf(media);
				for (int count = pos; count < mMediaArray.size(); count++) {
					if (ids == null)
						ids = ids + mMediaArray.get(count).getId();
					else
						ids = ids + "," + mMediaArray.get(count).getId();
				}
				// Navigate to Episode Activity
				Intent intent = new Intent(ExtendedPlaylistActivity.this,
						EpisodeActivity.class);
				intent.putExtra("MediaObj", media);
				intent.putExtra("MediaIds", ids);
				intent.putExtra("MediaIds", ids);
				intent.putExtra("Category",
						Constants.GOOGLE_ANALYTICS_PLAYLIST_CATEGORY);
				startActivity(intent);

			}
		});
	}

	private class MVPlaylistMediaRequestListener extends
			ServiceRequestListener<MVPlaylistMediaList> {

		public MVPlaylistMediaRequestListener(Context context) {
			super(context);
		}

		public void onRequestSuccess(MVPlaylistMediaList media) {
			mLog.info("SPICE", "Succesfully retrieved response");
			mProgress.setVisibility(View.INVISIBLE);
			mApiCallInProgress = false;
			mProgressDialog.dismiss();
			if (media == null || media.getMedia() == null
					|| media.getMedia().isEmpty()) {
				if (mPageCount > 0)
					// If it was an api call to load more items
					AppHelper.getAlert(ExtendedPlaylistActivity.this, "",
							getResources().getString(R.string.no_more_videos))
							.show();
				else
					AppHelper.getAlert(
							ExtendedPlaylistActivity.this,
							getResources()
									.getString(R.string.alert_title_error),
							getResources().getString(
									R.string.media_is_currently_not_available))
							.show();
				return;
			}
			if (mPageCount > 0) {
				// If it was an api call to load more items
				adapter.addAll(media.getMedia());
				adapter.notifyDataSetChanged();
			} else {
				mMediaArray = (ArrayList<MVPlaylistMedia>) media.getMedia();

				adapter = new ExtendedPlaylistArrayAdapter(
						ExtendedPlaylistActivity.this, 0, mMediaArray);
				mListViewPlaylists.setAdapter(adapter);
			}

		}

		@Override
		public void onRequestFailure(String message) {
			mLog.info("SPICE", message);
			mProgress.setVisibility(View.INVISIBLE);
			mProgressDialog.dismiss();

		}

		@Override
		public void onRetryRequest() {
			/*
			 * Recall the api if request failed due to token expiration
			 */
			spiceManager.getFromCacheAndLoadFromNetworkIfExpired(
					new MVPlaylistMediaRequest(AppHelper
							.getToken(ExtendedPlaylistActivity.this),
							mPlaylistChild.getId(), mPageCount),
					Constants.CACHE_KEY_FEATURED_PLAYLIST_MEDIA + mPageCount
							+ mPlaylistChild.getId(),
					DurationInMillis.ONE_MINUTE * 10,
					new MVPlaylistMediaRequestListener(
							ExtendedPlaylistActivity.this));

		}
	}

	@Override
	public void onDestroy() {
		// Destroy the ad view
		if (mAdView != null) {
			mAdView.destroy();
		}
		super.onDestroy();
	}

	@Override
	public void onStart() {
		super.onStart();

		// Send a screen view when the Activity is displayed to the user.
		AdultSwim.getGaTracker()
				.set(Fields.SCREEN_NAME, "ExtendedPlaylistPage");

		AdultSwim.getGaTracker().send(MapBuilder.createAppView().build());
	}

	@Override
	public void onResume() {
		super.onResume();
		/*
		 * Reset the alpha if any view is in selected state on coming back to
		 * this activity
		 */
		if (mSelectedView != null)
			mSelectedView.setAlpha(1);
	}

	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		int threshold = 1;
		int count = view.getCount();
		if (scrollState == SCROLL_STATE_IDLE) {
			/**
			 * Load more items
			 */

			if (view.getLastVisiblePosition() >= count - threshold) {

				if (mApiCallInProgress)// If already an api call is in progress
					return;
				mApiCallInProgress = true;
				++mPageCount;
				mProgress.setVisibility(View.VISIBLE);
				spiceManager.getFromCacheAndLoadFromNetworkIfExpired(
						new MVPlaylistMediaRequest(AppHelper
								.getToken(ExtendedPlaylistActivity.this),
								mPlaylistChild.getId(), mPageCount),
						Constants.CACHE_KEY_FEATURED_PLAYLIST_MEDIA
								+ mPageCount + mPlaylistChild.getId(),
						DurationInMillis.ONE_MINUTE * 10,
						new MVPlaylistMediaRequestListener(
								ExtendedPlaylistActivity.this));

			}
		}

	}
}
