package com.turnerapac.adultswimau.apps.generic;

import java.io.Serializable;
import java.util.ArrayList;

import org.slf4j.LoggerFactory;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.ShareActionProvider;
import android.widget.ShareActionProvider.OnShareTargetSelectedListener;
import android.widget.TextView;
import com.turnerapac.adultswimau.apps.generic.R;
import com.accedo.fontface.CustomFonts;
import com.google.ads.AdRequest;
import com.google.ads.doubleclick.DfpAdView;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayAdapterView.OnItemClickListener;
import com.jess.ui.TwoWayGridView;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.turnerapac.adultswimau.apps.generic.customviews.ResizableImageView;
import com.turnerapac.adultswimau.apps.generic.model.MVMediaSearch;
import com.turnerapac.adultswimau.apps.generic.model.MVPlayListSearch;
import com.turnerapac.adultswimau.apps.generic.model.MVPlaylistMedia;
import com.turnerapac.adultswimau.apps.generic.service.MVPlayListSearchRequest;
import com.turnerapac.adultswimau.apps.generic.service.MVPlaylistRelatedMediaRequest;
import com.turnerapac.adultswimau.apps.generic.service.ServiceRequestListener;

public class EpisodeActivity extends BaseActivity implements OnClickListener {

	private ImageView mShowImageView, mPlayImageView, mRatingImageView;
	private ResizableImageView mEpisodeImageView;
	private TextView mNameTextView, mDurationTextView, mDescriptionTextView,
			mEpisodeTextView, mShowNameTextView, mRelatedTextView;

	private MVPlaylistMedia mMedia;

	org.slf4j.Logger mLog;

	private String mMediaIds = null;

	private Button mShareButton;

	private RelativeLayout mShareLayout;

	private TwoWayGridView mRelatedEventsGridView;

	private String mShareText;

	private ArrayList<MVPlaylistMedia> arrRelatedMedias;

	private DfpAdView mAdView;

	private CustomFonts customFonts;

	private String mCategory;

	private String mGALabel;

	private View mSelectedView;

	private ScrollView mScrollView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// If not tablet, set screen orientation to portrait
		if (AppHelper.isTablet(this))
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
		setContentView(R.layout.activity_episode);

		// Get banner ad
		mAdView = AppHelper.getAdView(this);
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.addlayout);
		layout.addView(mAdView);
		// Load banner ad
		mAdView.loadAd(new AdRequest());

		mLog = LoggerFactory.getLogger(EpisodeActivity.class);

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

		customFonts = new CustomFonts(this);
		mRelatedTextView = (TextView) findViewById(R.id.txt_related);
		mRelatedEventsGridView = (TwoWayGridView) findViewById(R.id.lv_related_events);
		mShowNameTextView = (TextView) findViewById(R.id.txt_showname);
		mShowImageView = (ImageView) findViewById(R.id.img_show);
		mEpisodeImageView = (ResizableImageView) findViewById(R.id.image_episode);
		mNameTextView = (TextView) findViewById(R.id.txt_name);
		mDurationTextView = (TextView) findViewById(R.id.txt_duration);
		mDescriptionTextView = (TextView) findViewById(R.id.txt_description);
		mEpisodeTextView = (TextView) findViewById(R.id.txt_episode);
		mRatingImageView = (ImageView) findViewById(R.id.img_rating);
		mPlayImageView = (ImageView) findViewById(R.id.image_play);
		mPlayImageView.setImageResource(R.drawable.play_button);
		mPlayImageView.setOnClickListener(this);
		mScrollView = (ScrollView) findViewById(R.id.sv_descripton);
		mShareButton = (Button) findViewById(R.id.btn_share);
		mShareButton.setOnClickListener(this);
		UrlImageViewHelper.setUseBitmapScaling(false);
		mShareLayout = (RelativeLayout) findViewById(R.id.lyt_share);
		// Hide share view in phone
		if (!AppHelper.isTablet(this))
			mShareLayout.setVisibility(View.GONE);
		// Allow smooth scroll of textview preventing the parent to scroll
		if (AppHelper.isTablet(this)) {
			mScrollView.setOnTouchListener(new OnTouchListener() {

				public boolean onTouch(View v, MotionEvent event) {
					v.getParent().requestDisallowInterceptTouchEvent(true);
					return false;
				}
			});
		}
		mMediaIds = getIntent().getStringExtra("MediaIds");
		Serializable data = getIntent().getSerializableExtra("MediaObj");
		mMedia = (MVPlaylistMedia) data;
		mCategory = getIntent().getStringExtra("Category");
		mShareText = Constants.SHARING_BASE_URL
				+ MVShowNameHelper.getValue(MVTagHelper.getValue(
						mMedia.getTags(), MVTagHelper.NS_SHOW,
						MVTagHelper.PREDICATE_NAME)) + "/" + mMedia.getId()
				+ "/" + MVShowNameHelper.getValue(mMedia.getTitle()) + " "
				+ Constants.SHARING_VIA;

		if (AppHelper.isTablet(this)) {
			mShowNameTextView.setVisibility(View.VISIBLE);
			mShowNameTextView.setText(MVTagHelper.getValue(mMedia.getTags(),
					MVTagHelper.NS_SHOW, MVTagHelper.PREDICATE_NAME));
			mShowNameTextView.setTypeface(customFonts.getHeadingfont());
			mRelatedTextView.setTypeface(customFonts.getHeadingfont());
			mRelatedTextView.setText("MORE "
					+ MVTagHelper.getValue(mMedia.getTags(),
							MVTagHelper.NS_SHOW, MVTagHelper.PREDICATE_NAME)
					+ " COMING UP...");

		}

		/**
		 * API call to get banner image
		 */

		spiceManager.getFromCacheAndLoadFromNetworkIfExpired(
				new MVPlayListSearchRequest(AppHelper.getToken(this), mMedia
						.getId(), MVTagHelper.getValue(mMedia.getTags(),
						MVTagHelper.NS_SHOW, MVTagHelper.PREDICATE_NAME), 0),
				Constants.CACHE_KEY_TOPSHOWS_PLAYLIST_MEDIA_BANNER
						+ mMedia.getId(), DurationInMillis.ONE_MINUTE * 10,
				new MVSearchRequestListner(this));

		try {
			UrlImageViewHelper.setUrlDrawable(
					mEpisodeImageView,
					MVMediaImageHelper.getImageUrl(EpisodeActivity.this,
							mMedia.getImagePath()));
		} catch (NullPointerException e) {
			mLog.error("NullPointer exception, image path pull");
		}
		mEpisodeTextView.setText("Season "
				+ MVTagHelper.getValue(mMedia.getTags(), MVTagHelper.NS_SHOW,
						MVTagHelper.PREDICATE_SEASON)
				+ ", Episode "
				+ MVTagHelper.getValue(mMedia.getTags(),
						MVTagHelper.NS_EPISODE, MVTagHelper.PREDICATE_NUMBER));
		mRatingImageView.setImageResource(MVRatingImageHelper
				.getValue(MVTagHelper.getValue(mMedia.getTags(),
						MVTagHelper.NS_CLASSIFICATION,
						MVTagHelper.PREDICATE_RATING)));
		mNameTextView.setText(mMedia.getTitle());
		mDescriptionTextView.setText(mMedia.getDescription());
		mDurationTextView.setText(AppHelper.getTime(mMedia.getDuration()));

		if (AppHelper.isTablet(this)) {

			// Api call for related events
			spiceManager
					.getFromCacheAndLoadFromNetworkIfExpired(
							new MVPlaylistRelatedMediaRequest(AppHelper
									.getToken(this), mMedia.getId()),
							Constants.CACHE_KEY_EPISODE_RELATED_EVENTS
									+ mMedia.getId(),
							DurationInMillis.ONE_MINUTE * 10,
							new MVPlaylistMediaRequestListener(this));

		}
		if (mRelatedEventsGridView != null) {
			mRelatedEventsGridView.setSelector(R.drawable.list_item_bg);
			mRelatedEventsGridView
					.setOnItemClickListener(new OnItemClickListener() {

						public void onItemClick(TwoWayAdapterView<?> parent,
								View view, int position, long id) {

							mSelectedView = view;
							// Change color of selected view
							view.setAlpha((float) 0.5);

							MVPlaylistMedia media = (MVPlaylistMedia) parent
									.getItemAtPosition(position);

							/*
							 * Append ids of all medias starting from the
							 * selected one
							 */
							String ids = null;
							int pos = arrRelatedMedias.indexOf(media);
							for (int count = pos; count < arrRelatedMedias
									.size(); count++) {
								if (ids == null)
									ids = arrRelatedMedias.get(count).getId();
								else
									ids = ids
											+ ","
											+ arrRelatedMedias.get(count)
													.getId();
							}
							// Navigate to episode page
							Intent intent = new Intent(EpisodeActivity.this,
									EpisodeActivity.class);
							intent.putExtra("MediaObj", media);
							intent.putExtra("MediaIds", ids);
							startActivity(intent);

						}
					});

		}

	}

	public void onClick(View v) {
		/*
		 * Handle click events
		 */
		switch (v.getId()) {
		case R.id.image_play:
			if (mMediaIds == null)
				mMediaIds = mMedia.getId();
			mPlayImageView.setImageResource(R.drawable.play_button_clicked);
			/** sending the event to Google Analytics when click play button */
			mGALabel = MVTagHelper.getValue(mMedia.getTags(),
					MVTagHelper.NS_SHOW, MVTagHelper.PREDICATE_NAME)
					+ ""
					+ MVTagHelper.getValue(mMedia.getTags(),
							MVTagHelper.NS_SHOW, MVTagHelper.PREDICATE_SEASON)
					+ ""
					+ MVTagHelper.getValue(mMedia.getTags(),
							MVTagHelper.NS_EPISODE,
							MVTagHelper.PREDICATE_NUMBER);

			if (mCategory != null) {
				AppHelper.sendGoogleAnyaticsEvent(mCategory,
						Constants.GOOGLE_ANALYTICS_PLAY_ACTION, mGALabel);
			}
			// Navigate to player activity
			Intent intent = new Intent(getBaseContext(), PlayerActivity.class);
			intent.putExtra(Constants.MEDIA_ID, mMediaIds);
			startActivity(intent);
			break;
		case R.id.btn_share:

			/** sending the event to Google Analytics */
			mGALabel = MVTagHelper.getValue(mMedia.getTags(),
					MVTagHelper.NS_SHOW, MVTagHelper.PREDICATE_NAME)
					+ ""
					+ MVTagHelper.getValue(mMedia.getTags(),
							MVTagHelper.NS_SHOW, MVTagHelper.PREDICATE_SEASON)
					+ ""
					+ MVTagHelper.getValue(mMedia.getTags(),
							MVTagHelper.NS_EPISODE,
							MVTagHelper.PREDICATE_NUMBER);
			if (mCategory != null)
				AppHelper.sendGoogleAnyaticsEvent(mCategory,
						Constants.GOOGLE_ANALYTICS_SHARE_ACTION, mGALabel);
			// Start share intent
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("text/plain");
			i.putExtra(Intent.EXTRA_SUBJECT,
					"[adult swim] " + mMedia.getTitle());
			i.putExtra(android.content.Intent.EXTRA_TEXT, mShareText);
			startActivity(i);
			break;
		}

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (!AppHelper.isTablet(EpisodeActivity.this)) {
			/**
			 * Share Action Provider
			 */
			MenuItem item = menu.findItem(R.id.action_share);
			item.setVisible(true);
			ShareActionProvider mShareActionProvider = (ShareActionProvider) item
					.getActionProvider();
			mShareActionProvider.setShareIntent(getShareIntent());
			mShareActionProvider
					.setOnShareTargetSelectedListener(new OnShareTargetSelectedListener() {

						public boolean onShareTargetSelected(
								ShareActionProvider source, Intent intent) {

							/** sending the event to Google Analytics */
							mGALabel = MVTagHelper.getValue(mMedia.getTags(),
									MVTagHelper.NS_SHOW,
									MVTagHelper.PREDICATE_NAME)
									+ ""
									+ MVTagHelper.getValue(mMedia.getTags(),
											MVTagHelper.NS_SHOW,
											MVTagHelper.PREDICATE_SEASON)
									+ ""
									+ MVTagHelper.getValue(mMedia.getTags(),
											MVTagHelper.NS_EPISODE,
											MVTagHelper.PREDICATE_NUMBER);
							if (mCategory != null)
								AppHelper
										.sendGoogleAnyaticsEvent(
												mCategory,
												Constants.GOOGLE_ANALYTICS_SHARE_ACTION,
												mGALabel);
							return false;
						}
					});

		}
		return super.onPrepareOptionsMenu(menu);
	}

	private class MVPlaylistMediaRequestListener extends
			ServiceRequestListener<MVMediaSearch> {

		public MVPlaylistMediaRequestListener(Context context) {
			super(context);
		}

		public void onRequestSuccess(MVMediaSearch media) {
			Log.i("MOVIDEO", media.toString());
			if (media == null || media.getMedia() == null
					|| media.getMedia().isEmpty())
				return;
			arrRelatedMedias = new ArrayList<MVPlaylistMedia>();
			arrRelatedMedias.addAll(media.getMedia());
			ShowsArrayAdapter adapter = new ShowsArrayAdapter(
					EpisodeActivity.this, 0, arrRelatedMedias);
			mRelatedEventsGridView.setAdapter(adapter);

		}

		@Override
		public void onRequestFailure(String message) {
			mLog.info("Fetching related events failed ", message);

		}

		@Override
		public void onRetryRequest() {
			/*
			 * Recall api if request failed due to token expiration
			 */
			spiceManager
					.getFromCacheAndLoadFromNetworkIfExpired(
							new MVPlaylistRelatedMediaRequest(AppHelper
									.getToken(EpisodeActivity.this), mMedia
									.getId()),
							Constants.CACHE_KEY_EPISODE_RELATED_EVENTS
									+ mMedia.getId(),
							DurationInMillis.ONE_MINUTE * 10,
							new MVPlaylistMediaRequestListener(
									EpisodeActivity.this));
		}
	}

	private Intent getShareIntent() {

		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT,
				"[adult swim] " + mMedia.getTitle());
		intent.putExtra(android.content.Intent.EXTRA_TEXT, mShareText);
		return intent;
	}

	private class MVSearchRequestListner extends
			ServiceRequestListener<MVPlayListSearch> {

		public MVSearchRequestListner(Context context) {
			super(context);
		}

		public void onRequestSuccess(MVPlayListSearch searchlist) {
			if (searchlist == null || searchlist.getPlaylist() == null)
				return;
			/*
			 * Display banner image
			 */
			try {
				String url = null;
				if (AppHelper.isTablet(EpisodeActivity.this))
					url = Constants.BANNER_IMAGE_BASE_URL_TABLET
							+ MVTagHelper.getValue(searchlist.getPlaylist()
									.get(0).getTags(), MVTagHelper.NS_PLAYLIST,
									MVTagHelper.PREDICATE_PATH)
							+ Constants.BANNER_IMAGE_URL_EXTENSION_TABLET;
				else
					url = Constants.BANNER_IMAGE_BASE_URL_PHONE
							+ MVTagHelper.getValue(searchlist.getPlaylist()
									.get(0).getTags(), MVTagHelper.NS_PLAYLIST,
									MVTagHelper.PREDICATE_PATH)
							+ Constants.BANNER_IMAGE_URL_EXTENSION_PHONE;
				UrlImageViewHelper.setUrlDrawable(mShowImageView, url);

			} catch (NullPointerException e) {
				mLog.error("NullPointer exception, image path pull");
			}

		}

		@Override
		public void onRequestFailure(String message) {
		}

		@Override
		public void onRetryRequest() {
			/*
			 * Recall the api if request failed due to token expiration
			 */
			spiceManager.getFromCacheAndLoadFromNetworkIfExpired(
					new MVPlayListSearchRequest(AppHelper
							.getToken(EpisodeActivity.this), mMedia.getId(),
							MVTagHelper.getValue(mMedia.getTags(),
									MVTagHelper.NS_SHOW,
									MVTagHelper.PREDICATE_NAME), 0),
					Constants.CACHE_KEY_TOPSHOWS_PLAYLIST_MEDIA_BANNER
							+ mMedia.getId(), DurationInMillis.ONE_MINUTE * 10,
					new MVSearchRequestListner(EpisodeActivity.this));
		}
	}

	@Override
	public void onStart() {
		super.onStart();

		// Send a screen view when the Activity is displayed to the user.
		AdultSwim.getGaTracker().set(Fields.SCREEN_NAME, "EpisodePage");
		AdultSwim.getGaTracker().send(MapBuilder.createAppView().build());
	}

	@Override
	public void onResume() {
		/*
		 * Reset the alpha if any view is in selected state on coming back to
		 * this activity
		 */
		if (mSelectedView != null)
			mSelectedView.setAlpha(1);
		/*
		 * Reset the play image to unselected image on coming back to this
		 * activity
		 */
		mPlayImageView.setImageResource(R.drawable.play_button);
		super.onResume();
	}
}
