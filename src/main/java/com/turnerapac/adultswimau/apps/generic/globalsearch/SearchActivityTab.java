package com.turnerapac.adultswimau.apps.generic.globalsearch;

import java.util.ArrayList;
import org.slf4j.LoggerFactory;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.turnerapac.adultswimau.apps.generic.R;
import com.google.ads.AdRequest;
import com.google.ads.doubleclick.DfpAdView;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.turnerapac.adultswimau.apps.generic.AdultSwim;
import com.turnerapac.adultswimau.apps.generic.AppHelper;
import com.turnerapac.adultswimau.apps.generic.BaseActivity;
import com.turnerapac.adultswimau.apps.generic.Constants;
import com.turnerapac.adultswimau.apps.generic.EpisodeActivity;
import com.turnerapac.adultswimau.apps.generic.model.MVMediaCount;
import com.turnerapac.adultswimau.apps.generic.model.MVMediaSearch;
import com.turnerapac.adultswimau.apps.generic.model.MVPlaylistMedia;
import com.turnerapac.adultswimau.apps.generic.model.MVSearchCriteria;
import com.turnerapac.adultswimau.apps.generic.service.MVMediaSearchCountRequest;
import com.turnerapac.adultswimau.apps.generic.service.MVMediaSearchRequest;
import com.turnerapac.adultswimau.apps.generic.service.ServiceRequestListener;

public class SearchActivityTab extends BaseActivity implements OnClickListener,
		OnScrollListener {

	private int mPageCount = 0;
	private ListView mSearchListTab;
	private org.slf4j.Logger mLog;
	private String searchkey;
	private int mDisplayOrientation;
	private ArrayList<MVPlaylistMedia> mAllVideosListTab;
	private String mSearchCriteria;
	private String mTagFormat;
	private int mTotalPages;
	private ProgressDialog mProgressDialog;
	private Button mSearchAllVideosButtonTab, mSearchEpisodesButtonTab,
			mSearchClipsButtonTab;
	private Spinner mSearchSpinnerTab;
	private TextView mSearchAllvideosTab, mSearchEpisodeTab, mSearchClipTab,
			mSearchRelevantTab, mSearchRecentTab, mSearchPopularTab,
			mSearchTrendingTab;
	private DfpAdView mAdView;
	private SearchTabListAdapter adapter;
	private boolean mApiCallInProgress = false;
	private View mSelectedView;
	private ProgressBar mLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_tab);
		mAdView = AppHelper.getAdView(this);
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.tab_search_addlayout);
		layout.addView(mAdView);
		mAdView.loadAd(new AdRequest());
		/** getting Orientation of the screen */
		mDisplayOrientation = getScreenOrientation();
		/** setting the progressdialog */
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("Loading medias ...");
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setCancelable(true);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.show();

		mLoader = (ProgressBar) findViewById(R.id.loader);

		/**
		 * Actionbar settings
		 */
		ActionBar actionBar = getActionBar();
		actionBar.setLogo(AppHelper.isTablet(this) ? R.drawable.tab_logo_selector : R.drawable.logo_selector);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(0);

		/** logger */
		mLog = LoggerFactory.getLogger(SearchActivityTab.class);
		/** getting the search query */
		Intent intent = getIntent();
		searchkey = intent.getStringExtra("searchkey");
		/** pumbing the ids of the UI widgets */
		mSearchListTab = (ListView) findViewById(R.id.search_list_tab);
		mSearchListTab.setOnScrollListener(this);

		mSearchAllVideosButtonTab = (Button) findViewById(R.id.btn_search_allvideos);
		if (mSearchAllVideosButtonTab != null) {
			mSearchAllVideosButtonTab.setTextColor(Color.WHITE);
			mSearchAllVideosButtonTab.setOnClickListener(this);
		}
		mSearchEpisodesButtonTab = (Button) findViewById(R.id.btn_search_episodes);
		if (mSearchEpisodesButtonTab != null) {
			mSearchEpisodesButtonTab.setTextColor(getResources().getColor(
					R.color.app_color));
			mSearchEpisodesButtonTab.setOnClickListener(this);
		}
		mSearchClipsButtonTab = (Button) findViewById(R.id.btn_search_clips);
		if (mSearchClipsButtonTab != null) {
			mSearchClipsButtonTab.setTextColor(getResources().getColor(
					R.color.app_color));
			mSearchClipsButtonTab.setOnClickListener(this);
		}

		mSearchAllvideosTab = (TextView) findViewById(R.id.search_videostab_txt);
		if (mSearchAllvideosTab != null) {
			mSearchAllvideosTab.setTextColor(Color.WHITE);
			mSearchAllvideosTab.setOnClickListener(this);
		}

		mSearchEpisodeTab = (TextView) findViewById(R.id.search_episode_tabtxt);
		mSearchAllvideosTab = (TextView) findViewById(R.id.search_videostab_txt);
		if (mSearchEpisodeTab != null) {
			mSearchEpisodeTab.setTextColor(getResources().getColor(
					R.color.app_color));
			mSearchEpisodeTab.setOnClickListener(this);
		}
		mSearchClipTab = (TextView) findViewById(R.id.search_clip_tabtxt);
		if (mSearchClipTab != null) {
			mSearchClipTab.setTextColor(getResources().getColor(
					R.color.app_color));
			mSearchClipTab.setOnClickListener(this);
		}
		mSearchRelevantTab = (TextView) findViewById(R.id.search_relevant_tabtxt);
		if (mSearchRelevantTab != null) {
			mSearchRelevantTab.setTextColor(getResources().getColor(
					R.color.app_color));
			mSearchRelevantTab.setOnClickListener(this);
		}
		mSearchRecentTab = (TextView) findViewById(R.id.search_recent_tabtxt);
		if (mSearchRecentTab != null) {
			mSearchRecentTab.setTextColor(getResources().getColor(
					R.color.app_color));
			mSearchRecentTab.setOnClickListener(this);
		}
		mSearchPopularTab = (TextView) findViewById(R.id.search_popular_tabtxt);
		if (mSearchPopularTab != null) {
			mSearchPopularTab.setTextColor(getResources().getColor(
					R.color.app_color));
			mSearchPopularTab.setOnClickListener(this);
		}
		mSearchTrendingTab = (TextView) findViewById(R.id.search_trending_tabtxt);
		if (mSearchTrendingTab != null) {
			mSearchTrendingTab.setTextColor(getResources().getColor(
					R.color.app_color));
			mSearchTrendingTab.setOnClickListener(this);
		}
		mSearchSpinnerTab = (Spinner) findViewById(R.id.spinner_search);

		mTagFormat = String.format("[\"%s\"]", searchkey);
		if (mSearchSpinnerTab != null) {
			ArrayAdapter<CharSequence> adapter = ArrayAdapter
					.createFromResource(this,
							R.array.searchlistsitems_arraytab,
							R.layout.spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mSearchSpinnerTab.setAdapter(adapter);
			mSearchSpinnerTab
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							mPageCount = 0;
							switch (arg2) {
							case 0:
								mProgressDialog.show();
								mSearchCriteria = Constants.EMPTY_DATA;
								FetchSearchResult(mTagFormat, mSearchCriteria);
								break;
							case 1:
								mProgressDialog.show();
								mSearchCriteria = MVSearchCriteria.MOST_POPULAR;
								FetchSearchResult(mTagFormat, mSearchCriteria);
								break;
							case 2:
								mProgressDialog.show();
								mSearchCriteria = MVSearchCriteria.MOST_RECENT;
								FetchSearchResult(mTagFormat, mSearchCriteria);
								break;
							case 3:
								mProgressDialog.show();
								mSearchCriteria = MVSearchCriteria.TRENDING;
								FetchSearchResult(mTagFormat, mSearchCriteria);
								break;
							}

						}

						public void onNothingSelected(AdapterView<?> arg0) {

						}
					});
		}
		/** in the case landscape orientation */
		if (mDisplayOrientation == 2) {
			mSearchCriteria = Constants.EMPTY_DATA;
			FetchSearchResult(mTagFormat, mSearchCriteria);
			mSearchAllvideosTab.setTextColor(Color.WHITE);
			mSearchRelevantTab.setTextColor(Color.WHITE);

		}
		mSearchListTab.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View arg1,
					int position, long arg3) {
				mSelectedView = arg1;
				arg1.setAlpha((float) 0.5);

				MVPlaylistMedia media = (MVPlaylistMedia) parent
						.getItemAtPosition(position);
				String ids = null;
				int pos = mAllVideosListTab.indexOf(media);
				for (int count = pos; count < mAllVideosListTab.size(); count++) {
					if (ids == null)
						ids = mAllVideosListTab.get(count).getId();
					else
						ids = ids + "," + mAllVideosListTab.get(count).getId();
				}
				Intent intent = new Intent(SearchActivityTab.this,
						EpisodeActivity.class);
				intent.putExtra("MediaObj", media);
				intent.putExtra("MediaIds", ids);
				startActivity(intent);
			}
		});

		/**
		 * getting the search count
		 * 
		 */
		mLog.info("Fetching Search(clip) count Result");
		String mClipTag = String.format("['%s',\"content:type=%s\"]",
				searchkey, MVSearchCriteria.CONTENT_TYPE_CLIP);
		spiceManager.execute(
				new MVMediaSearchCountRequest(AppHelper.getToken(this),
						mClipTag, MVSearchCriteria.MEDIA_COUNT, 0),
				Constants.CACHE_KEY_CLIP_COUNT, DurationInMillis.ONE_SECOND,
				new MVSearchCountRequestListner(this,
						MVSearchCriteria.CONTENT_TYPE_CLIP));
		String mEpisodeTag = String.format("['%s',\"content:type=%s\"]",
				searchkey, MVSearchCriteria.CONTENT_TYPE_EPISODE);
		mLog.info("Fetching Search(episode) count Result");
		spiceManager.execute(
				new MVMediaSearchCountRequest(AppHelper.getToken(this),
						mEpisodeTag, MVSearchCriteria.MEDIA_COUNT, 0),
				Constants.CACHE_KEY_EPISODE_COUNT, DurationInMillis.ONE_SECOND,
				new MVSearchCountRequestListner(this,
						MVSearchCriteria.CONTENT_TYPE_EPISODE));

		String mAllVideosTag = String.format("['%s']", searchkey);
		mLog.info("Fetching Search(allvideos) count Result");
		spiceManager.execute(
				new MVMediaSearchCountRequest(AppHelper.getToken(this),
						mAllVideosTag, MVSearchCriteria.MEDIA_COUNT, 0),
				Constants.CACHE_KEY_ALL_VIDEOS_COUNT,
				DurationInMillis.ONE_SECOND, new MVSearchCountRequestListner(
						this, "All Videos"));
	}

	public void onClick(View v) {
		mPageCount = 0;
		switch (v.getId()) {
		case R.id.btn_search_allvideos:
			mProgressDialog.show();
			mSearchAllVideosButtonTab.setTextColor(Color.WHITE);
			mSearchClipsButtonTab.setTextColor(getResources().getColor(
					R.color.app_color));
			mSearchEpisodesButtonTab.setTextColor(getResources().getColor(
					R.color.app_color));
			mTagFormat = String.format("['%s']", searchkey);
			FetchSearchResult(mTagFormat, mSearchCriteria);

			break;
		case R.id.btn_search_episodes:
			mProgressDialog.show();
			mSearchEpisodesButtonTab.setTextColor(Color.WHITE);
			mSearchClipsButtonTab.setTextColor(getResources().getColor(
					R.color.app_color));
			mSearchAllVideosButtonTab.setTextColor(getResources().getColor(
					R.color.app_color));
			mTagFormat = String.format("['%s',\"content:type=Episode\"]",
					searchkey);
			FetchSearchResult(mTagFormat, mSearchCriteria);

			break;
		case R.id.btn_search_clips:
			mProgressDialog.show();
			mSearchClipsButtonTab.setTextColor(Color.WHITE);
			mSearchEpisodesButtonTab.setTextColor(getResources().getColor(
					R.color.app_color));
			mSearchAllVideosButtonTab.setTextColor(getResources().getColor(
					R.color.app_color));
			mTagFormat = String.format("['%s',\"content:type=Clip\"]",
					searchkey);
			FetchSearchResult(mTagFormat, mSearchCriteria);
			break;
		case R.id.search_videostab_txt:
			mProgressDialog.show();
			mSearchAllvideosTab.setTextColor(Color.WHITE);
			mSearchEpisodeTab.setTextColor(getResources().getColor(
					R.color.app_color));
			mSearchClipTab.setTextColor(getResources().getColor(
					R.color.app_color));
			mTagFormat = String.format("['%s']", searchkey);
			FetchSearchResult(mTagFormat, mSearchCriteria);
			break;
		case R.id.search_episode_tabtxt:
			mProgressDialog.show();
			mSearchEpisodeTab.setTextColor(Color.WHITE);
			mSearchAllvideosTab.setTextColor(getResources().getColor(
					R.color.app_color));
			mSearchClipTab.setTextColor(getResources().getColor(
					R.color.app_color));
			mTagFormat = String.format("['%s',\"content:type=Episode\"]",
					searchkey);
			FetchSearchResult(mTagFormat, mSearchCriteria);
			break;
		case R.id.search_clip_tabtxt:
			mProgressDialog.show();
			mSearchClipTab.setTextColor(Color.WHITE);
			mSearchEpisodeTab.setTextColor(getResources().getColor(
					R.color.app_color));
			mSearchAllvideosTab.setTextColor(getResources().getColor(
					R.color.app_color));
			mTagFormat = String.format("['%s',\"content:type=Clip\"]",
					searchkey);
			FetchSearchResult(mTagFormat, mSearchCriteria);
			break;
		case R.id.search_relevant_tabtxt:
			mProgressDialog.show();
			mSearchRelevantTab.setTextColor(Color.WHITE);
			mSearchRecentTab.setTextColor(getResources().getColor(
					R.color.app_color));
			mSearchPopularTab.setTextColor(getResources().getColor(
					R.color.app_color));
			mSearchTrendingTab.setTextColor(getResources().getColor(
					R.color.app_color));
			mSearchCriteria = Constants.EMPTY_DATA;
			FetchSearchResult(mTagFormat, mSearchCriteria);
			break;
		case R.id.search_recent_tabtxt:
			mProgressDialog.show();
			mSearchRecentTab.setTextColor(Color.WHITE);
			mSearchRelevantTab.setTextColor(getResources().getColor(
					R.color.app_color));
			mSearchPopularTab.setTextColor(getResources().getColor(
					R.color.app_color));
			mSearchTrendingTab.setTextColor(getResources().getColor(
					R.color.app_color));
			mSearchCriteria = MVSearchCriteria.MOST_RECENT;
			FetchSearchResult(mTagFormat, mSearchCriteria);
			break;
		case R.id.search_popular_tabtxt:
			mProgressDialog.show();
			mSearchPopularTab.setTextColor(Color.WHITE);
			mSearchRelevantTab.setTextColor(getResources().getColor(
					R.color.app_color));
			mSearchTrendingTab.setTextColor(getResources().getColor(
					R.color.app_color));
			mSearchRecentTab.setTextColor(getResources().getColor(
					R.color.app_color));
			mSearchCriteria = MVSearchCriteria.MOST_POPULAR;
			FetchSearchResult(mTagFormat, mSearchCriteria);
			break;
		case R.id.search_trending_tabtxt:
			mProgressDialog.show();
			mSearchTrendingTab.setTextColor(Color.WHITE);
			mSearchRelevantTab.setTextColor(getResources().getColor(
					R.color.app_color));
			mSearchPopularTab.setTextColor(getResources().getColor(
					R.color.app_color));
			mSearchRecentTab.setTextColor(getResources().getColor(
					R.color.app_color));
			mSearchCriteria = MVSearchCriteria.TRENDING;
			FetchSearchResult(mTagFormat, mSearchCriteria);
			break;

		}

	}

	/** search Request Listener class */

	private class MVSearchRequestListner extends
			ServiceRequestListener<MVMediaSearch> {

		public MVSearchRequestListner(Context context) {
			super(context);
		}

		public void onRequestSuccess(MVMediaSearch searchlist) {
			mLog.info("Search Result Retrived");
			mLoader.setVisibility(View.INVISIBLE);
			mProgressDialog.dismiss();
			if (searchlist == null || searchlist.getMedia() == null
					|| searchlist.getMedia().isEmpty()) {
				AppHelper.getAlert(SearchActivityTab.this, "",
						getResources().getString(R.string.no_result)).show();
				mAllVideosListTab = new ArrayList<MVPlaylistMedia>();
				adapter = new SearchTabListAdapter(SearchActivityTab.this, 0,
						mAllVideosListTab);
				mSearchListTab.setAdapter(adapter);
				return;
			}

			mApiCallInProgress = false;
			mTotalPages = searchlist.getTotalPages();
			if (mPageCount != 0) {
				adapter.addAll(searchlist.getMedia());
				adapter.notifyDataSetChanged();
				return;
			}
			/** setting the the search list Adapter showing the searchlist */
			mAllVideosListTab = new ArrayList<MVPlaylistMedia>();
			mAllVideosListTab.addAll(searchlist.getMedia());
			adapter = new SearchTabListAdapter(SearchActivityTab.this, 0,
					mAllVideosListTab);
			mSearchListTab.setAdapter(adapter);

		}

		@Override
		public void onRequestFailure(String message) {
			mLog.info("SPICE", message);
			mLoader.setVisibility(View.INVISIBLE);
			mProgressDialog.dismiss();
		}

		@Override
		public void onRetryRequest() {
			spiceManager.execute(
					new MVMediaSearchRequest(AppHelper
							.getToken(SearchActivityTab.this), mTagFormat,
							mSearchCriteria, mPageCount),
					Constants.CACHE_KEY_SEARCH_KEY,
					DurationInMillis.ALWAYS_EXPIRED,
					new MVSearchRequestListner(SearchActivityTab.this));
		}
	}

	/** web service call for the search */
	private void FetchSearchResult(String tag, String searchCriteria) {
		/** invoking the search call */
		mLog.info("Fetching Search Result");
		spiceManager.execute(
				new MVMediaSearchRequest(AppHelper
						.getToken(SearchActivityTab.this), tag, searchCriteria,
						mPageCount), Constants.CACHE_KEY_SEARCH_KEY,
				DurationInMillis.ALWAYS_EXPIRED, new MVSearchRequestListner(
						SearchActivityTab.this));

	}

	/** getting the screen orientation */
	public int getScreenOrientation() {
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			return 1; // Portrait Mode

		} else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			return 2; // Landscape mode
		}
		return 0;
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

				if (mPageCount == mTotalPages - 1) {
					Toast.makeText(SearchActivityTab.this,
							getResources().getString(R.string.no_more_videos),
							Toast.LENGTH_LONG).show();
					return;
				}
				if (mApiCallInProgress)
					return;
				mApiCallInProgress = true;
				++mPageCount;
				mLoader.setVisibility(View.VISIBLE);
				FetchSearchResult(mTagFormat, mSearchCriteria);

			}
		}

	}

	@Override
	public void onDestroy() {
		if (mAdView != null) {
			mAdView.destroy();
		}
		super.onDestroy();
	}

	/**
	 * web service request class for the search count
	 */
	private class MVSearchCountRequestListner extends
			ServiceRequestListener<MVMediaCount> {

		private String contentType;

		public MVSearchCountRequestListner(Context context, String contentType) {
			super(context);
			this.contentType = contentType;
		}

		public void onRequestSuccess(MVMediaCount searchlistCount) {
			mLog.info("Search Result count Retrived");
			if (searchlistCount == null)
				return;
			if (contentType.equals(MVSearchCriteria.CONTENT_TYPE_EPISODE)) {
				if (mSearchEpisodesButtonTab != null)
					mSearchEpisodesButtonTab.setText(getResources().getString(
							R.string.episodes)
							+ "["
							+ String.valueOf(searchlistCount.getTotalItems())
							+ "]");
				if (mSearchEpisodeTab != null)
					mSearchEpisodeTab.setText(getResources().getString(
							R.string.episodes)
							+ "["
							+ String.valueOf(searchlistCount.getTotalItems())
							+ "]");

			} else if (contentType.equals(MVSearchCriteria.CONTENT_TYPE_CLIP)) {
				if (mSearchClipsButtonTab != null)
					mSearchClipsButtonTab.setText(getResources().getString(
							R.string.clips)
							+ "["
							+ String.valueOf(searchlistCount.getTotalItems())
							+ "]");
				if (mSearchClipTab != null)
					mSearchClipTab.setText(getResources().getString(
							R.string.clips)
							+ "["
							+ String.valueOf(searchlistCount.getTotalItems())
							+ "]");

			}

			else if (contentType.equals("All Videos")) {
				if (mSearchAllVideosButtonTab != null)
					mSearchAllVideosButtonTab.setText(getResources().getString(
							R.string.all_videos)
							+ "["
							+ String.valueOf(searchlistCount.getTotalItems())
							+ "]");
				if (mSearchAllvideosTab != null)
					mSearchAllvideosTab.setText(getResources().getString(
							R.string.all_videos)
							+ "["
							+ String.valueOf(searchlistCount.getTotalItems())
							+ "]");
			}
		}

		@Override
		public void onRequestFailure(String message) {
			mLog.info("SPICE", message);
		}

		@Override
		public void onRetryRequest() {
			String mTag = "";
			if (contentType.equals(MVSearchCriteria.CONTENT_TYPE_EPISODE))
				mTag = String.format("['%s',\"content:type=%s\"]", searchkey,
						MVSearchCriteria.CONTENT_TYPE_EPISODE);
			else if (contentType.equals(MVSearchCriteria.CONTENT_TYPE_CLIP))
				mTag = String.format("['%s',\"content:type=%s\"]", searchkey,
						MVSearchCriteria.CONTENT_TYPE_CLIP);
			else
				mTag = String.format("['%s']", searchkey);

			spiceManager.execute(
					new MVMediaSearchCountRequest(AppHelper
							.getToken(SearchActivityTab.this), mTag,
							MVSearchCriteria.MEDIA_COUNT, 0),
					Constants.CACHE_KEY_CLIP_COUNT,
					DurationInMillis.ONE_SECOND,
					new MVSearchCountRequestListner(SearchActivityTab.this,
							contentType));
		}
	}

	@Override
	public void onStart() {
		super.onStart();

		/** Send a screen view when the Activity is displayed to the user. */
		// AdultSwim.getGaTracker().send(MapBuilder.createAppView().build());
		AdultSwim.getGaTracker().set(Fields.SCREEN_NAME, "searchPage");
		AdultSwim.getGaTracker().send(MapBuilder.createAppView().build());
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		MenuItem searchItem = menu.findItem(R.id.action_search);
		SearchView mSearchView = (SearchView) searchItem.getActionView();
		mSearchView.setIconifiedByDefault(false);
		mSearchView.setQuery(searchkey, false);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (mSelectedView != null)
			mSelectedView.setAlpha(1);
	}
}
