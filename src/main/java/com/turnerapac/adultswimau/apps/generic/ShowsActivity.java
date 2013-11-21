package com.turnerapac.adultswimau.apps.generic;

import java.io.Serializable;
import java.util.ArrayList;

import org.slf4j.LoggerFactory;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.turnerapac.adultswimau.apps.generic.R;
import com.accedo.fontface.CustomFonts;
import com.google.ads.AdRequest;
import com.google.ads.doubleclick.DfpAdView;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.jess.ui.TwoWayAbsListView;
import com.jess.ui.TwoWayAbsListView.OnScrollListener;
import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayAdapterView.OnItemClickListener;
import com.jess.ui.TwoWayGridView;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.turnerapac.adultswimau.apps.generic.model.MVMediaCount;
import com.turnerapac.adultswimau.apps.generic.model.MVMediaSearch;
import com.turnerapac.adultswimau.apps.generic.model.MVPlaylistChild;
import com.turnerapac.adultswimau.apps.generic.model.MVPlaylistMedia;
import com.turnerapac.adultswimau.apps.generic.model.MVSearchCriteria;
import com.turnerapac.adultswimau.apps.generic.service.MVMediaSearchCountRequest;
import com.turnerapac.adultswimau.apps.generic.service.MVMediaSearchRequest;
import com.turnerapac.adultswimau.apps.generic.service.ServiceRequestListener;

public class ShowsActivity extends BaseActivity implements OnClickListener,
		OnScrollListener {

	private Spinner mSpinnerTab, mSpinnerPhone;
	private ImageView mShowImageView;
	private TwoWayGridView mEpisodeGridView;
	private MVPlaylistChild mPlaylistChild;
	org.slf4j.Logger mLog;
	private ArrayList<MVPlaylistMedia> mMediaArray;
	private TextView mNameTextView;
	private Button mAllVideosButton, mEpisodesButton, mClipsButton;
	private int mPageCount = 0;
	private String mKeyword, mSearchCriteria;
	private DfpAdView mAdView;
	private int mTotalPages = 0;
	private CustomFonts customFonts;
	private EpisodeArrayAdapter mAdapter;
	private View mSelectedView;

	private ProgressBar mProgressBar, mLoader;
	private boolean mApiCallInProgress = false;
	private String mCategory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// If not tablet, set screen orientation to portrait
		if (AppHelper.isTablet(this))
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
		mLog = LoggerFactory.getLogger(ShowsActivity.class);
		setContentView(R.layout.activity_shows);
		// Get Banner Ad
		mAdView = AppHelper.getAdView(this);
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.shows_addlayout);
		layout.addView(mAdView);
		// Load Banner Ad
		mAdView.loadAd(new AdRequest());
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

		mProgressBar = (ProgressBar) findViewById(R.id.shows_progress);
		mLoader = (ProgressBar) findViewById(R.id.loader);
		customFonts = new CustomFonts(this);
		mSpinnerTab = (Spinner) findViewById(R.id.sp_playlist);
		mSpinnerPhone = (Spinner) findViewById(R.id.sp_playlist_phone);
		mShowImageView = (ImageView) findViewById(R.id.img_show);
		mEpisodeGridView = (TwoWayGridView) findViewById(R.id.list_shows);
		mEpisodeGridView.setSelector(R.drawable.list_item_bg);
		mEpisodeGridView.setOnScrollListener(this);
		mNameTextView = (TextView) findViewById(R.id.txt_name);
		mAllVideosButton = (Button) findViewById(R.id.btn_allvideos);
		if (mAllVideosButton != null) {
			mAllVideosButton.setTextColor(Color.WHITE);
			mAllVideosButton.setOnClickListener(this);
		}
		mEpisodesButton = (Button) findViewById(R.id.btn_episodes);
		if (mEpisodesButton != null)
			mEpisodesButton.setOnClickListener(this);
		mClipsButton = (Button) findViewById(R.id.btn_clips);
		if (mClipsButton != null)
			mClipsButton.setOnClickListener(this);

		Serializable data = getIntent().getSerializableExtra("PlayListChild");
		mPlaylistChild = (MVPlaylistChild) data;
		mKeyword = String.format("[\"show:name=%s\"]",
				mPlaylistChild.getTitle());
		mCategory = getIntent().getStringExtra("Category");
		if (mNameTextView != null) {
			mNameTextView.setTypeface(customFonts.getHeadingfont());
			mNameTextView.setText(mPlaylistChild.getTitle());
		}
		try {
			String url = null;
			if (AppHelper.isTablet(this))
				url = Constants.BANNER_IMAGE_BASE_URL_TABLET
						+ MVTagHelper.getValue(mPlaylistChild.getTags(),
								MVTagHelper.NS_PLAYLIST,
								MVTagHelper.PREDICATE_PATH)
						+ Constants.BANNER_IMAGE_URL_EXTENSION_TABLET;
			else
				url = Constants.BANNER_IMAGE_BASE_URL_PHONE
						+ MVTagHelper.getValue(mPlaylistChild.getTags(),
								MVTagHelper.NS_PLAYLIST,
								MVTagHelper.PREDICATE_PATH)
						+ Constants.BANNER_IMAGE_URL_EXTENSION_PHONE;
			UrlImageViewHelper.setUrlDrawable(mShowImageView, url);
		} catch (NullPointerException e) {
			mLog.error("NullPointer exception, image path pull");
		}

		if (AppHelper.isTablet(this)) {
			// Bind spinner data for tablet
			ArrayAdapter<CharSequence> adapter = ArrayAdapter
					.createFromResource(this, R.array.playlists_array_tab,
							R.layout.spinner_item);

			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mSpinnerTab.setAdapter(adapter);
			mSpinnerTab.setOnItemSelectedListener(new OnItemSelectedListener() {

				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					switch (arg2) {
					case 0:
						mProgressBar.setVisibility(View.VISIBLE);
						mPageCount = 0;

						mSearchCriteria = MVSearchCriteria.EPISODE_NUMBER;
						mKeyword = String.format("[\"show:name=%s\"]",
								mPlaylistChild.getTitle());
						FetchMediaPlaylist(mKeyword, mSearchCriteria);
						break;
					case 1:
						mProgressBar.setVisibility(View.VISIBLE);
						mPageCount = 0;

						mSearchCriteria = MVSearchCriteria.TITLE;
						FetchMediaPlaylist(mKeyword, mSearchCriteria);
						break;
					case 2:
						mProgressBar.setVisibility(View.VISIBLE);
						mPageCount = 0;

						mSearchCriteria = MVSearchCriteria.DESCRIPTION;
						FetchMediaPlaylist(mKeyword, mSearchCriteria);
						break;
					case 3:
						mProgressBar.setVisibility(View.VISIBLE);
						mPageCount = 0;

						mSearchCriteria = MVSearchCriteria.MOST_RECENT;
						FetchMediaPlaylist(mKeyword, mSearchCriteria);
						break;
					case 4:
						mProgressBar.setVisibility(View.VISIBLE);
						mPageCount = 0;

						mSearchCriteria = MVSearchCriteria.MOST_PLAYED_TODAY;
						FetchMediaPlaylist(mKeyword, mSearchCriteria);
						break;
					case 5:
						mProgressBar.setVisibility(View.VISIBLE);
						mPageCount = 0;

						mSearchCriteria = MVSearchCriteria.MOST_PLAYED_THIS_WEEK;
						FetchMediaPlaylist(mKeyword, mSearchCriteria);
						break;
					case 6:
						mProgressBar.setVisibility(View.VISIBLE);
						mPageCount = 0;

						mSearchCriteria = MVSearchCriteria.MOST_PLAYED_THIS_MONTH;
						FetchMediaPlaylist(mKeyword, mSearchCriteria);
						break;
					}

				}

				public void onNothingSelected(AdapterView<?> arg0) {

				}
			});
		} else {
			// Bind spinner data for phone
			ArrayAdapter<CharSequence> adapter = ArrayAdapter
					.createFromResource(this, R.array.playlists_array_phone,
							R.layout.spinner_item);

			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mSpinnerPhone.setAdapter(adapter);
			mSpinnerPhone
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							switch (arg2) {
							case 0:
								// All Videos Selected
								mProgressBar.setVisibility(View.VISIBLE);
								mPageCount = 0;

								mKeyword = String.format("[\"show:name=%s\"]",
										mPlaylistChild.getTitle());
								mSearchCriteria = MVSearchCriteria.EPISODE_NUMBER;
								FetchMediaPlaylist(mKeyword, mSearchCriteria);
								AppHelper
										.sendGoogleAnyaticsEvent(
												mCategory,
												Constants.GOOGLE_ANALYTICS_CLICK_ACTION,
												"All Videos");
								break;
							case 1:
								// Episodes Selected
								mProgressBar.setVisibility(View.VISIBLE);
								mPageCount = 0;
								mSearchCriteria = MVSearchCriteria.EPISODE_NUMBER;
								mKeyword = String
										.format("[\"show:name=%s\",\"content:type=%s\"]",
												mPlaylistChild.getTitle(),
												MVSearchCriteria.CONTENT_TYPE_EPISODE);
								FetchMediaPlaylist(mKeyword, mSearchCriteria);
								AppHelper
										.sendGoogleAnyaticsEvent(
												mCategory,
												Constants.GOOGLE_ANALYTICS_CLICK_ACTION,
												"Episodes");
								break;
							case 2:
								// Clips Selected
								mProgressBar.setVisibility(View.VISIBLE);
								mPageCount = 0;

								mSearchCriteria = MVSearchCriteria.EPISODE_NUMBER;
								mKeyword = String
										.format("[\"show:name=%s\",\"content:type=%s\"]",
												mPlaylistChild.getTitle(),
												MVSearchCriteria.CONTENT_TYPE_CLIP);
								FetchMediaPlaylist(mKeyword, mSearchCriteria);
								AppHelper
										.sendGoogleAnyaticsEvent(
												mCategory,
												Constants.GOOGLE_ANALYTICS_CLICK_ACTION,
												"Clips");
								break;

							}

						}

						public void onNothingSelected(AdapterView<?> arg0) {

						}
					});

		}
		mEpisodeGridView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(TwoWayAdapterView<?> parent, View view,
					int position, long id) {

				mSelectedView = view;
				// Change color of selected view
				view.setAlpha((float) 0.5);

				MVPlaylistMedia media = (MVPlaylistMedia) parent
						.getItemAtPosition(position);
				/*
				 * Append ids of all medias starting from the selected one
				 */
				String ids = null;
				int pos = mMediaArray.indexOf(media);
				for (int count = pos; count < mMediaArray.size(); count++) {
					if (ids == null)
						ids = mMediaArray.get(count).getId();
					else
						ids = ids + "," + mMediaArray.get(count).getId();
				}
				// Navigate to shows page
				Intent intent = new Intent(ShowsActivity.this,
						EpisodeActivity.class);
				intent.putExtra("Category", mCategory);
				intent.putExtra("MediaObj", media);
				intent.putExtra("MediaIds", ids);
				startActivity(intent);

			}
		});

		/**
		 * Fetch counts
		 */
		if (AppHelper.isTablet(this)) {
			String mClipTag = String.format(
					"[\"show:name=%s\",\"content:type=%s\"]",
					mPlaylistChild.getTitle(),
					MVSearchCriteria.CONTENT_TYPE_CLIP);
			mLog.info("Fetching clip count");
			spiceManager.getFromCacheAndLoadFromNetworkIfExpired(
					new MVMediaSearchCountRequest(AppHelper.getToken(this),
							mClipTag, MVSearchCriteria.MEDIA_COUNT, 0),
					Constants.CACHE_KEY_CLIP_COUNT + mPlaylistChild.getId(),
					DurationInMillis.ONE_MINUTE * 10,
					new MVSearchCountRequestListner(this,
							MVSearchCriteria.CONTENT_TYPE_CLIP));
			String mEpisodeTag = String.format(
					"[\"show:name=%s\",\"content:type=%s\"]",
					mPlaylistChild.getTitle(),
					MVSearchCriteria.CONTENT_TYPE_EPISODE);
			mLog.info("Fetching episode count");
			spiceManager.getFromCacheAndLoadFromNetworkIfExpired(
					new MVMediaSearchCountRequest(AppHelper.getToken(this),
							mEpisodeTag, MVSearchCriteria.MEDIA_COUNT, 0),
					Constants.CACHE_KEY_EPISODE_COUNT + mPlaylistChild.getId(),
					DurationInMillis.ONE_MINUTE * 10,
					new MVSearchCountRequestListner(this,
							MVSearchCriteria.CONTENT_TYPE_EPISODE));

			String mAllVideosTag = String.format("[\"show:name=%s\"]",
					mPlaylistChild.getTitle());
			mLog.info("Fetching allvideos count");
			spiceManager.getFromCacheAndLoadFromNetworkIfExpired(
					new MVMediaSearchCountRequest(AppHelper.getToken(this),
							mAllVideosTag, MVSearchCriteria.MEDIA_COUNT, 0),
					Constants.CACHE_KEY_ALL_VIDEOS_COUNT
							+ mPlaylistChild.getId(),
					DurationInMillis.ONE_MINUTE * 10,
					new MVSearchCountRequestListner(this, "All Videos"));
		}
	}

	private void FetchMediaPlaylist(String tag, String searchCriteria) {

		if (mPageCount != 0)
			// Remove cached data before calling api for loading more items
			spiceManager.removeDataFromCache(MVMediaSearch.class,
					Constants.CACHE_KEY_TOPSHOWS_PLAYLIST_MEDIA + mKeyword
							+ mSearchCriteria);
		mLog.info("Fetching playlist");
		spiceManager.getFromCacheAndLoadFromNetworkIfExpired(
				new MVMediaSearchRequest(AppHelper.getToken(this), mKeyword,
						searchCriteria, mPageCount),
				Constants.CACHE_KEY_TOPSHOWS_PLAYLIST_MEDIA + mKeyword
						+ mSearchCriteria + mPageCount,
				DurationInMillis.ONE_MINUTE * 10, new MVSearchRequestListner(
						this));
	}

	private class MVSearchRequestListner extends
			ServiceRequestListener<MVMediaSearch> {

		public MVSearchRequestListner(Context context) {
			super(context);
		}

		public void onRequestSuccess(MVMediaSearch searchlist) {

			mProgressBar.setVisibility(View.INVISIBLE);
			mLoader.setVisibility(View.INVISIBLE);
			mLog.info("Playlist retrieved");

			if (searchlist == null || searchlist.getMedia() == null
					|| searchlist.getMedia().isEmpty()) {
				// If response is null or empty show alert
				AppHelper.getAlert(
						ShowsActivity.this,
						getResources().getString(R.string.alert_title_error),
						getResources().getString(
								R.string.media_is_currently_not_available))
						.show();
				mMediaArray = new ArrayList<MVPlaylistMedia>();
				mAdapter = new EpisodeArrayAdapter(ShowsActivity.this, 0,
						mMediaArray, mCategory);
				mEpisodeGridView.setAdapter(mAdapter);
				return;
			}

			mTotalPages = searchlist.getTotalPages();
			mApiCallInProgress = false;
			if (mPageCount != 0) {
				/*
				 * If request was to load more items, add response to existing
				 * list
				 */
				mAdapter.addAll(searchlist.getMedia());
				mAdapter.notifyDataSetChanged();
				return;
			}
			mMediaArray = (ArrayList<MVPlaylistMedia>) searchlist.getMedia();
			mAdapter = new EpisodeArrayAdapter(ShowsActivity.this, 0,
					mMediaArray, mCategory);
			mEpisodeGridView.setAdapter(mAdapter);
		}

		@Override
		public void onRequestFailure(String message) {

			mProgressBar.setVisibility(View.INVISIBLE);
			mLoader.setVisibility(View.INVISIBLE);
			mLog.info("Spice :" + message);

		}

		@Override
		public void onRetryRequest() {
			/*
			 * Recall the api if request failed due to token expiration
			 */
			spiceManager.getFromCacheAndLoadFromNetworkIfExpired(
					new MVMediaSearchRequest(AppHelper
							.getToken(ShowsActivity.this), mKeyword,
							mSearchCriteria, mPageCount),
					Constants.CACHE_KEY_TOPSHOWS_PLAYLIST_MEDIA + mKeyword
							+ mSearchCriteria + mPageCount,
					DurationInMillis.ONE_MINUTE * 10,
					new MVSearchRequestListner(ShowsActivity.this));

		}
	}

	private class MVSearchCountRequestListner extends
			ServiceRequestListener<MVMediaCount> {

		private String contentType;

		public MVSearchCountRequestListner(Context context, String contentType) {
			super(context);
			this.contentType = contentType;
		}

		public void onRequestSuccess(MVMediaCount searchlistCount) {
			mLog.info("Spice:" + contentType + " retrieved successfully");
			if (searchlistCount == null)
				return;
			if (contentType.equals(MVSearchCriteria.CONTENT_TYPE_EPISODE))
				mEpisodesButton.setText(getResources().getString(
						R.string.episodes)
						+ "[" + searchlistCount.getTotalItems() + "]");
			else if (contentType.equals(MVSearchCriteria.CONTENT_TYPE_CLIP))
				mClipsButton.setText(getResources().getString(R.string.clips)
						+ "[" + searchlistCount.getTotalItems() + "]");

			else if (contentType.equals("All Videos"))
				mAllVideosButton.setText(getResources().getString(
						R.string.all_videos)
						+ "[" + searchlistCount.getTotalItems() + "]");

		}

		@Override
		public void onRequestFailure(String message) {
			mLog.info("Spice:" + contentType + " error " + message);
		}

		@Override
		public void onRetryRequest() {
			/*
			 * Recall the api if request failed due to token expiration
			 */
			String mTag = "";
			if (contentType.equals(MVSearchCriteria.CONTENT_TYPE_EPISODE))
				mTag = String.format("[\"show:name=%s\",\"content:type=%s\"]",
						mPlaylistChild.getTitle(),
						MVSearchCriteria.CONTENT_TYPE_EPISODE);
			else if (contentType.equals(MVSearchCriteria.CONTENT_TYPE_CLIP))
				mTag = String.format("[\"show:name=%s\",\"content:type=%s\"]",
						mPlaylistChild.getTitle(),
						MVSearchCriteria.CONTENT_TYPE_CLIP);
			else
				mTag = String.format("[\"show:name=%s\"]",
						mPlaylistChild.getTitle());
			spiceManager.getFromCacheAndLoadFromNetworkIfExpired(
					new MVMediaSearchCountRequest(AppHelper
							.getToken(ShowsActivity.this), mTag,
							MVSearchCriteria.MEDIA_COUNT, 0),
					Constants.CACHE_KEY_CLIP_COUNT + mPlaylistChild.getId(),
					DurationInMillis.ONE_MINUTE * 10,
					new MVSearchCountRequestListner(ShowsActivity.this,
							contentType));
		}
	}

	public void onClick(View v) {
		/*
		 * Handle button clicks
		 */
		switch (v.getId()) {
		case R.id.btn_allvideos:
			mProgressBar.setVisibility(View.VISIBLE);
			mPageCount = 0;

			mKeyword = String.format("[\"show:name=%s\"]",
					mPlaylistChild.getTitle());
			FetchMediaPlaylist(mKeyword, mSearchCriteria);
			mAllVideosButton.setTextColor(Color.WHITE);
			mEpisodesButton.setTextColor(getResources().getColor(
					R.color.app_color));
			mClipsButton.setTextColor(getResources()
					.getColor(R.color.app_color));
			AppHelper.sendGoogleAnyaticsEvent(mCategory,
					Constants.GOOGLE_ANALYTICS_CLICK_ACTION, "All Videos");

			break;
		case R.id.btn_episodes:
			mProgressBar.setVisibility(View.VISIBLE);
			mPageCount = 0;

			mKeyword = String.format("[\"show:name=%s\",\"content:type=%s\"]",
					mPlaylistChild.getTitle(),
					MVSearchCriteria.CONTENT_TYPE_EPISODE);
			FetchMediaPlaylist(mKeyword, mSearchCriteria);
			mAllVideosButton.setTextColor(getResources().getColor(
					R.color.app_color));
			mEpisodesButton.setTextColor(Color.WHITE);
			mClipsButton.setTextColor(getResources()
					.getColor(R.color.app_color));
			AppHelper.sendGoogleAnyaticsEvent(mCategory,
					Constants.GOOGLE_ANALYTICS_CLICK_ACTION, "Episodes");
			break;
		case R.id.btn_clips:
			mProgressBar.setVisibility(View.VISIBLE);
			mPageCount = 0;

			mKeyword = String.format("[\"show:name=%s\",\"content:type=%s\"]",
					mPlaylistChild.getTitle(),
					MVSearchCriteria.CONTENT_TYPE_CLIP);
			FetchMediaPlaylist(mKeyword, mSearchCriteria);
			mAllVideosButton.setTextColor(getResources().getColor(
					R.color.app_color));
			mEpisodesButton.setTextColor(getResources().getColor(
					R.color.app_color));
			mClipsButton.setTextColor(Color.WHITE);
			AppHelper.sendGoogleAnyaticsEvent(mCategory,
					Constants.GOOGLE_ANALYTICS_CLICK_ACTION, "Clips");
			break;
		}

	}

	public void onScrollStateChanged(TwoWayAbsListView view, int scrollState) {

		int threshold = 1;
		int count = view.getCount();
		if (scrollState == SCROLL_STATE_IDLE) {
			/**
			 * Load more items
			 */

			if (view.getLastVisiblePosition() >= count - threshold) {

				if (mPageCount == mTotalPages - 1) {
					// If reached last page
					Toast.makeText(ShowsActivity.this,
							getResources().getString(R.string.no_more_videos),
							Toast.LENGTH_LONG).show();
					return;
				}
				if (mApiCallInProgress)// If api call is already in progress
					return;
				mApiCallInProgress = true;
				++mPageCount;
				mLoader.setVisibility(View.VISIBLE);
				FetchMediaPlaylist(mKeyword, mSearchCriteria);
			}
		}
	}

	public void onScroll(TwoWayAbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

	}

	@Override
	public void onDestroy() {
		// Destroy ad view
		if (mAdView != null) {
			mAdView.destroy();
		}
		super.onDestroy();
	}

	@Override
	public void onStart() {
		super.onStart();

		// Send a screen view when the Activity is displayed to the user.
		AdultSwim.getGaTracker().set(Fields.SCREEN_NAME, "ShowsPage");

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

}
