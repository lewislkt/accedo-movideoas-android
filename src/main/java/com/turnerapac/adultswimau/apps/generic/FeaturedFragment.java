package com.turnerapac.adultswimau.apps.generic;

import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.LoggerFactory;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import com.turnerapac.adultswimau.apps.generic.R;
import com.accedo.fontface.CustomFonts;
import com.google.ads.AdRequest;
import com.google.ads.doubleclick.DfpAdView;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.infinateviewpager.InfiniteCirclePageIndicator;
import com.infinateviewpager.InfinitePagerAdapter;
import com.infinateviewpager.InfiniteViewPager;
import com.jess.ui.TwoWayAbsListView;
import com.jess.ui.TwoWayAbsListView.OnScrollListener;
import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayAdapterView.OnItemClickListener;
import com.jess.ui.TwoWayGridView;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.turnerapac.adultswimau.apps.generic.customviews.ResizableImageView;
import com.turnerapac.adultswimau.apps.generic.model.MVPlaylistChild;
import com.turnerapac.adultswimau.apps.generic.model.MVPlaylistChildList;
import com.turnerapac.adultswimau.apps.generic.model.MVPlaylistMedia;
import com.turnerapac.adultswimau.apps.generic.model.MVPlaylistMediaList;
import com.turnerapac.adultswimau.apps.generic.service.MVPlaylistChildRequest;
import com.turnerapac.adultswimau.apps.generic.service.MVPlaylistMediaRequest;
import com.turnerapac.adultswimau.apps.generic.service.ServiceRequestListener;

public class FeaturedFragment extends BaseFragment {

	private static final String SCREEN_LABEL = "Featured Screen";

	private InfiniteViewPager mPager;

	private static final long ANIM_GALLERY_DELAY = 10000;

	private org.slf4j.Logger mLog;

	private ArrayList<MVPlaylistMedia> mFeaturedMediaArray;

	private ArrayList<MVPlaylistChild> mPlayListArray;

	private LinearLayout mLayoutPlaylist;

	private ProgressDialog mProgressDialog;

	private ProgressBar mProgress;

	private InfiniteCirclePageIndicator mCirclepageIndicator;

	private String mGALabel;

	private InfinitePagerAdapter mPagerAdapter;

	private Handler mHandelr = new Handler();

	private View mSelectedView;

	private CustomFonts mCustomFonts;

	private boolean mApiCallInProgress = false;

	private ScrollView mScrollView;

	private HashMap<String, String> mPageMap = new HashMap<String, String>();

	private Runnable mAnimateGalleryView = new Runnable() {
		public void run() {
			if (mFeaturedMediaArray == null || mFeaturedMediaArray.isEmpty()) {
				return;
			} else {

				// Move carousel banners from one to another
				mPager.setCurrentItem(mPager.getCurrentItem() + 1, true);
				mHandelr.postDelayed(this, ANIM_GALLERY_DELAY);
			}

		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		mCustomFonts = new CustomFonts(getActivity());

		View view = inflater.inflate(R.layout.fragment_featured, container,
				false);
		// Fields set on a tracker persist for all hits, until they are
		// overridden or cleared by assignment to null.
		AdultSwim.getGaTracker().set(Fields.SCREEN_NAME, SCREEN_LABEL);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mProgressDialog = new ProgressDialog(getActivity());
		mProgressDialog.setMessage(getResources().getString(
				R.string.loading_playlist));
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setCancelable(true);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.show();
		mScrollView = (ScrollView) view.findViewById(R.id.myscroll);
		mScrollView.requestChildFocus(mLayoutPlaylist, null);
		mPager = (InfiniteViewPager) view.findViewById(R.id.viewPager);
		mCirclepageIndicator = (InfiniteCirclePageIndicator) view
				.findViewById(R.id.indicator);
		mCirclepageIndicator.setFillColor(Color.RED);
		mLayoutPlaylist = (LinearLayout) view.findViewById(R.id.lyt_playlist);
		mLog = LoggerFactory.getLogger(FeaturedFragment.class);

		mPager.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent arg1) {
				// allows smooth scrolling of carousel banners
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});
		mCirclepageIndicator
				.setOnPageChangeListener(new OnPageChangeListener() {

					public void onPageSelected(int position) {

					}

					public void onPageScrolled(int arg0, float arg1, int arg2) {
						// allows smooth scrolling of carousel banners
						mPager.getParent().requestDisallowInterceptTouchEvent(
								true);
					}

					public void onPageScrollStateChanged(int state) {
						if (state == ViewPager.SCROLL_STATE_IDLE) {
						}
					}
				});

		/** Fetching Playlists for Featured Page */
		mLog.info("Fetching Child Playlist");
		spiceManager.getFromCacheAndLoadFromNetworkIfExpired(
				new MVPlaylistChildRequest(AppHelper.getToken(getActivity()),
						Constants.PLAYLIST_ID_FEATURED),
				Constants.CACHE_KEY_FEATURED_PLAYLIST,
				DurationInMillis.ONE_MINUTE * 10,
				new MVPlaylistChildRequestListner(getActivity()));
		/** Fetching Promoted Media */
		mLog.info("Fetching Promoted Media");
		spiceManager.getFromCacheAndLoadFromNetworkIfExpired(
				new MVPlaylistMediaRequest(AppHelper.getToken(getActivity()),
						Constants.PLAYLIST_ID_PROMOTED),
				Constants.CACHE_KEY_PROMOTED_MEDIA,
				DurationInMillis.ONE_MINUTE * 10,
				new MVPlaylistFeaturedMediaRequestListener(getActivity()));

	}

	public class ImageAdapter extends PagerAdapter {

		private ArrayList<MVPlaylistMedia> arrContent;

		public ImageAdapter(Context context, int textViewResourceId,
				ArrayList<MVPlaylistMedia> objects) {
			this.arrContent = objects;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {

			((ViewPager) container).removeView((View) object);
		}

		@Override
		public void finishUpdate(View container) {
		}

		@Override
		public int getCount() {
			return arrContent.size();
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			if (position == 1) {
				// Load banner ad in second position
				DfpAdView mAdView = AppHelper.getCarouselAdView(getActivity());
				mAdView.loadAd(new AdRequest());
				((ViewPager) view).addView(mAdView, 0);
				return mAdView;
			}
			final View imageLayout = getActivity().getLayoutInflater().inflate(
					R.layout.featured_topview_item, view, false);
			imageLayout.setTag(position);
			imageLayout.setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {

					String tag = String.valueOf(imageLayout.getTag());
					// Get the selected item
					MVPlaylistMedia media = mFeaturedMediaArray.get(Integer
							.valueOf(tag));
					/*
					 * Append ids of all medias starting from the selected one
					 */
					String ids = null;
					int pos = mFeaturedMediaArray.indexOf(media);
					for (int count = pos; count < mFeaturedMediaArray.size(); count++) {
						if (ids == null)
							ids = mFeaturedMediaArray.get(count).getId();
						else
							ids = ids + ","
									+ mFeaturedMediaArray.get(count).getId();
					}
					// Navigate to Episode page
					Intent intent = new Intent(getActivity(),
							EpisodeActivity.class);
					intent.putExtra("MediaObj", media);
					intent.putExtra("MediaIds", ids);
					intent.putExtra("Category",
							Constants.GOOGLE_ANALYTICS_FEATURED_CATEGORY);
					startActivity(intent);
				}
			});
			ResizableImageView imageView = (ResizableImageView) imageLayout
					.findViewById(R.id.image);
			TextView mTiltle = (TextView) imageLayout
					.findViewById(R.id.txt_title);
			mTiltle.setText(arrContent.get(position).getTitle());
			mTiltle.setTypeface(mCustomFonts.getHeadingfont());
			TextView mName = (TextView) imageLayout.findViewById(R.id.txt_name);
			mName.setTypeface(mCustomFonts.getHeadingfont());
			mName.setText(MVTagHelper
					.getValue(arrContent.get(position).getTags(),
							MVTagHelper.NS_SHOW, MVTagHelper.PREDICATE_NAME));
			try {
				String url = null;
				if (AppHelper.isTablet(getActivity()))
					url = Constants.CAROUSEL_IMAGE_BASE_URL_TABLET
							+ arrContent.get(position).getId()
							+ Constants.CAROUSEL_IMAGE_URL_EXTENSION_TABLET;
				else
					url = Constants.CAROUSEL_IMAGE_BASE_URL_PHONE
							+ arrContent.get(position).getId()
							+ Constants.CAROUSEL_IMAGE_URL_EXTENSION_PHONE;
				UrlImageViewHelper.setUrlDrawable(imageView, url);
			} catch (NullPointerException e) {
				mLog.error("NullPointer exception, image path pull");
			}

			((ViewPager) view).addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View container) {
		}

	}

	@Override
	public void onStart() {
		super.onStart();

		// Send a screen view when the Activity is displayed to the user.
		AdultSwim.getGaTracker().set(Fields.SCREEN_NAME, "Featuredpage");
		AdultSwim.getGaTracker().send(MapBuilder.createAppView().build());
	}

	@Override
	public void onPause() {
		// Stop carousel animation
		if (mHandelr != null) {
			mHandelr.removeCallbacks(mAnimateGalleryView);
		}
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		// Start carousel animation
		mHandelr.postDelayed(mAnimateGalleryView, ANIM_GALLERY_DELAY);
		/*
		 * Reset the alpha if any view is in selected state on coming back to
		 * this activity
		 */
		if (mSelectedView != null)
			mSelectedView.setAlpha(1);
	}

	private class MVPlaylistFeaturedMediaRequestListener extends
			ServiceRequestListener<MVPlaylistMediaList> {

		public MVPlaylistFeaturedMediaRequestListener(Context context) {
			super(context);
		}

		public void onRequestSuccess(MVPlaylistMediaList media) {
			mLog.info("SPICE", "Retrieved featured media");
			mProgressDialog.dismiss();
			if (media == null || media.getMedia() == null
					|| media.getMedia().isEmpty())
				return;
			mFeaturedMediaArray = new ArrayList<MVPlaylistMedia>();
			mFeaturedMediaArray.add(media.getMedia().get(0));
			mFeaturedMediaArray.add(new MVPlaylistMedia());
			for (int count = 1; count < media.getMedia().size(); count++)
				mFeaturedMediaArray.add(media.getMedia().get(count));
			mPagerAdapter = new InfinitePagerAdapter(new ImageAdapter(
					getActivity(), 0, mFeaturedMediaArray));
			mPagerAdapter.setOneItemMode();
			mPager.setAdapter(mPagerAdapter);
			mPager.setOffscreenPageLimit(mFeaturedMediaArray.size());
			mCirclepageIndicator.setSnap(true);
			mCirclepageIndicator.setViewPager(mPager);
			mCirclepageIndicator.setCurrentItem(2);

		}

		@Override
		public void onRequestFailure(String message) {
			mLog.info("Fetching promoted media failed", message);
			mProgressDialog.dismiss();

		}

		@Override
		public void onRetryRequest() {
			/*
			 * Recall api if request failed due to token expiration
			 */
			spiceManager.getFromCacheAndLoadFromNetworkIfExpired(
					new MVPlaylistMediaRequest(AppHelper
							.getToken(getActivity()),
							Constants.PLAYLIST_ID_PROMOTED),
					Constants.CACHE_KEY_PROMOTED_MEDIA,
					DurationInMillis.ONE_MINUTE * 10,
					new MVPlaylistFeaturedMediaRequestListener(getActivity()));
		}
	}

	private class MVPlaylistMediaRequestListener extends
			ServiceRequestListener<MVPlaylistMediaList> {
		private String id;
		private int mPage;

		public MVPlaylistMediaRequestListener(Context context,
				String playlist_id) {
			super(context);
			this.id = playlist_id;
			// Get the current page
			mPage = Integer.valueOf(mPageMap.get(id));
		}

		public void onRequestSuccess(MVPlaylistMediaList media) {
			Log.i("MOVIDEO", media.toString());
			if (mProgress != null)
				mProgress.setVisibility(View.INVISIBLE);
			mApiCallInProgress = false;
			if (media == null || media.getMedia() == null
					|| media.getMedia().isEmpty()) {
				if (mPage > 0)
					// If request was to load more items
					AppHelper.getAlert(getActivity(), "",
							getResources().getString(R.string.no_more_videos))
							.show();
				return;
			}
			ArrayList<MVPlaylistMedia> arrMedia = (ArrayList<MVPlaylistMedia>) media
					.getMedia();
			final View view = mLayoutPlaylist.findViewWithTag(id);
			final ProgressBar mProgressBarRight = (ProgressBar) view
					.findViewById(R.id.progressBar1);
			final TwoWayGridView lv_playlist_media = (TwoWayGridView) view
					.findViewById(R.id.lv_playlist);

			lv_playlist_media.setOnScrollListener(new OnScrollListener() {

				public void onScrollStateChanged(TwoWayAbsListView mView,
						int scrollState) {

					int threshold = 1;
					int count = mView.getCount();
					if (scrollState == SCROLL_STATE_IDLE) {
						/**
						 * Load more items
						 */

						int mPage = 0;
						if (mView.getLastVisiblePosition() >= count - threshold) {
							// Get the current page
							mPage = Integer.valueOf(mPageMap.get(id));
							if (mApiCallInProgress)
								return;
							mApiCallInProgress = true;
							++mPage;
							// Put incremented page number in hash map
							mPageMap.put(id, String.valueOf(mPage));
							mProgress = mProgressBarRight;
							mProgress.setVisibility(View.VISIBLE);
							/*
							 * Api call to load more items
							 */
							spiceManager.getFromCacheAndLoadFromNetworkIfExpired(
									new MVPlaylistMediaRequest(AppHelper
											.getToken(getActivity()), id, mPage),
									Constants.CACHE_KEY_FEATURED_PLAYLIST_MEDIA
											+ mPage + id,
									DurationInMillis.ONE_MINUTE * 10,
									new MVPlaylistMediaRequestListener(
											getActivity(), id));
						}
					}
				}

				public void onScroll(TwoWayAbsListView view,
						int firstVisibleItem, int visibleItemCount,
						int totalItemCount) {

				}
			});
			/**
			 * If more items are loaded reset listview tag by adding additional
			 * content.
			 */
			if (mPage > 0) {
				// If request was to load more items
				Object obj = lv_playlist_media.getTag();
				@SuppressWarnings("unchecked")
				ArrayList<MVPlaylistMedia> mMediaArray = (ArrayList<MVPlaylistMedia>) obj;
				mMediaArray.addAll(arrMedia);
				lv_playlist_media.setTag(mMediaArray);
			} else
				lv_playlist_media.setTag(arrMedia);
			lv_playlist_media.setSelector(R.drawable.list_item_bg);
			lv_playlist_media.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(TwoWayAdapterView<?> parent, View view,
						int position, long id) {

					mSelectedView = view;
					// Change the color of selected view
					view.setAlpha((float) 0.5);
					// Get selected item
					MVPlaylistMedia media = (MVPlaylistMedia) parent
							.getItemAtPosition(position);
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
					// Send click event to google analytics
					AppHelper.sendGoogleAnyaticsEvent(
							Constants.GOOGLE_ANALYTICS_FEATURED_CATEGORY,
							Constants.GOOGLE_ANALYTICS_CLICK_ACTION, mGALabel);

					// Get the arraylist object
					Object obj = lv_playlist_media.getTag();
					@SuppressWarnings("unchecked")
					ArrayList<MVPlaylistMedia> mMediaArray = (ArrayList<MVPlaylistMedia>) obj;
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
					// Navigate to Episode page
					Intent intent = new Intent(getActivity(),
							EpisodeActivity.class);
					intent.putExtra("MediaObj", media);
					intent.putExtra("MediaIds", ids);
					intent.putExtra("Category",
							Constants.GOOGLE_ANALYTICS_FEATURED_CATEGORY);
					startActivity(intent);

				}

			});

			if (mPage > 0) {
				/*
				 * If request was to load more items
				 */
				ShowsArrayAdapter adapter = (ShowsArrayAdapter) lv_playlist_media
						.getAdapter();
				adapter.addAll(arrMedia);
				adapter.notifyDataSetChanged();
				return;
			}
			ShowsArrayAdapter adapter = new ShowsArrayAdapter(getActivity(), 0,
					arrMedia);
			lv_playlist_media.setAdapter(adapter);

		}

		@Override
		public void onRequestFailure(String message) {
			mLog.info("SPICE", message);
			if (mProgress != null)
				mProgress.setVisibility(View.INVISIBLE);
		}

		@Override
		public void onRetryRequest() {
			/*
			 * Recall the api if request failed due to token expiration
			 */
			spiceManager.getFromCacheAndLoadFromNetworkIfExpired(
					new MVPlaylistMediaRequest(AppHelper
							.getToken(getActivity()), id, mPage),
					Constants.CACHE_KEY_FEATURED_PLAYLIST_MEDIA + mPage + id,
					DurationInMillis.ONE_MINUTE * 10,
					new MVPlaylistMediaRequestListener(getActivity(), id));

		}
	}

	private class MVPlaylistChildRequestListner extends
			ServiceRequestListener<MVPlaylistChildList> {

		public MVPlaylistChildRequestListner(Context context) {
			super(context);

		}

		public void onRequestSuccess(MVPlaylistChildList playlist) {
			mLog.info("Child Playlist Retrived");

			if (playlist == null || playlist.getPlaylist() == null
					|| playlist.getPlaylist().isEmpty())
				return;
			mPlayListArray = new ArrayList<MVPlaylistChild>();
			mPlayListArray.addAll(playlist.getPlaylist());
			PopulatePlaylist();

		}

		@Override
		public void onRequestFailure(String message) {
			mLog.error("Child Playlist Fetching Failed", message);
		}

		@Override
		public void onRetryRequest() {
			/*
			 * Recall the api if request failed due to token expiration
			 */
			spiceManager.getFromCacheAndLoadFromNetworkIfExpired(
					new MVPlaylistChildRequest(AppHelper
							.getToken(getActivity()),
							Constants.PLAYLIST_ID_FEATURED),
					Constants.CACHE_KEY_FEATURED_PLAYLIST,
					DurationInMillis.ONE_MINUTE * 10,
					new MVPlaylistChildRequestListner(getActivity()));
		}
	}

	private void PopulatePlaylist() {

		/**
		 * Remove all previously added views before adding new views
		 */
		if (mLayoutPlaylist.getChildCount() != 0)
			mLayoutPlaylist.removeAllViews();

		for (MVPlaylistChild playlist : mPlayListArray) {
			View view = getActivity().getLayoutInflater().inflate(
					R.layout.featured_content_item, mLayoutPlaylist, false);
			view.setTag(playlist.getId());
			TextView txt_playlist_name = (TextView) view
					.findViewById(R.id.txt_header);
			txt_playlist_name.setText(playlist.getTitle());
			txt_playlist_name.setTypeface(mCustomFonts.getHeadingfont());
			txt_playlist_name.setTag(playlist);
			txt_playlist_name.setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {

					MVPlaylistChild playlistchild = (MVPlaylistChild) arg0
							.getTag();
					/** sending the play list name event to google analytics */
					AppHelper.sendGoogleAnyaticsEvent(
							Constants.GOOGLE_ANALYTICS_FEATURED_CATEGORY,
							Constants.GOOGLE_ANALYTICS_CLICK_ACTION,
							playlistchild.getTitle());
					// Navigate to extended play list page
					Intent intent = new Intent(getActivity(),
							ExtendedPlaylistActivity.class);
					intent.putExtra("PlayListChild", playlistchild);
					startActivity(intent);

				}
			});
			// Add each play list view to the layout
			mLayoutPlaylist.addView(view);
			// Add play list id and initial page number to hash map
			mPageMap.put(playlist.getId(), String.valueOf(0));
			/*
			 * Api call to fetch play list media
			 */
			spiceManager.getFromCacheAndLoadFromNetworkIfExpired(
					new MVPlaylistMediaRequest(AppHelper
							.getToken(getActivity()), playlist.getId(), 0),
					Constants.CACHE_KEY_FEATURED_PLAYLIST_MEDIA + "0"
							+ playlist.getId(),
					DurationInMillis.ONE_MINUTE * 10,
					new MVPlaylistMediaRequestListener(getActivity(), playlist
							.getId()));
		}

	}

	@Override
	public void onDestroyView() {
		mProgressDialog.dismiss();
		super.onDestroyView();
	}

}
