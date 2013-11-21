package com.turnerapac.adultswimau.apps.generic.globalsearch;

import java.util.ArrayList;

import org.slf4j.LoggerFactory;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.view.ViewPager.PageTransformer;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
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
import com.turnerapac.adultswimau.apps.generic.customviews.PagerContainer;
import com.turnerapac.adultswimau.apps.generic.model.MVMediaSearch;
import com.turnerapac.adultswimau.apps.generic.model.MVPlaylistMedia;
import com.turnerapac.adultswimau.apps.generic.model.MVSearchCriteria;
import com.turnerapac.adultswimau.apps.generic.service.MVMediaSearchRequest;
import com.turnerapac.adultswimau.apps.generic.service.ServiceRequestListener;

public class SearchActivity extends BaseActivity implements OnScrollListener {

	private ViewPager mSearchViewPager;
	private PagerContainer mContainer;
	private ListView mSearchList;
	private Spinner mSearchSpinner;
	private String mSearchCriteria;
	private String[] mSearchItem;
	private int mPagerSelectedItem;
	private org.slf4j.Logger mLog;
	public String searchkey;
	private ProgressDialog mProgressDialog;
	private int mPageCount = 0;
	private int mTotalPages;
	private String mTagFormat;
	private ArrayList<MVPlaylistMedia> mAllVideosList;
	private DfpAdView mAdView;
	private SearchListAdapter mPagerAdapter;
	private boolean mApiCallInProgress = false;
	private ProgressBar mLoader;
	private View mSelectedView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		/**
		 * Loading Banner Ad
		 */
		mAdView = AppHelper.getAdView(this);
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.search_addlayout);
		layout.addView(mAdView);
		mAdView.loadAd(new AdRequest());
		/**
		 * Actionbar settings
		 */
		ActionBar actionBar = getActionBar();
		actionBar.setLogo(android.R.color.transparent);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(0);

		mContainer = (PagerContainer) findViewById(R.id.search_pager_container);
		mLoader = (ProgressBar) findViewById(R.id.loader);
		mSearchViewPager = mContainer.getViewPager();
		mSearchList = (ListView) findViewById(R.id.search_list);
		mSearchList.setOnScrollListener(this);
		mSearchSpinner = (Spinner) findViewById(R.id.search_spinner_item);
		mSearchItem = getResources().getStringArray(
				R.array.searchlistsitems_array);
		/** getting the search query */
		Intent intent = getIntent();
		searchkey = intent.getStringExtra("searchkey");
		mSearchCriteria = MVSearchCriteria.MOST_POPULAR;
		mTagFormat = String.format("['%s']", searchkey);
		ViewPagerAdapter adapter = new ViewPagerAdapter();
		mSearchViewPager.setAdapter(adapter);
		// Necessary or the pager will only have one extra page to show
		// make this at least however many pages you can see
		mSearchViewPager.setOffscreenPageLimit(3);
		mSearchViewPager.getCurrentItem();
		// A little space between pages
		mSearchViewPager.setPageMargin(8);
		mSearchViewPager.setCurrentItem(1);
		// If hardware acceleration is enabled, you should also remove
		// clipping on the pager for its children.
		mSearchViewPager.setClipChildren(false);

		/** progress dialog */
		mProgressDialog = new ProgressDialog(SearchActivity.this);
		mProgressDialog.setMessage("Loading....");
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setCancelable(true);
		mProgressDialog.setCanceledOnTouchOutside(false);

		mLog = LoggerFactory.getLogger(SearchActivity.class);
		mSearchViewPager.setPageTransformer(true, new PageTransformer() {

			public void transformPage(View view, float position) {

				if ((position > 0) || (position < 0)) {
					// Fade unselected view
					view.setAlpha((float) 0.5);

				} else if (position == 0) {
					// Selected view
					view.setAlpha(1);

				}

			}
		});

		/** the spinner for the showing the category in the all videos search */
		ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter
				.createFromResource(this, R.array.searchlists_array,
						R.layout.spinner_item);
		adapterSpinner
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSearchSpinner.setAdapter(adapterSpinner);
		mSearchSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
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

		mPagerSelectedItem = mSearchViewPager.getCurrentItem();
		mSearchViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageScrollStateChanged(int state) {
			}

			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
			}

			public void onPageSelected(int position) {
				mPageCount = 0;
				mPagerSelectedItem = position;
				if (mPagerSelectedItem == 0) {

					mProgressDialog.show();
					mTagFormat = String.format("['%s',\"content:type=Clip\"]",
							searchkey);
					FetchSearchResult(mTagFormat, mSearchCriteria);
				} else if (mPagerSelectedItem == 1) {
					mProgressDialog.show();
					mTagFormat = String.format("['%s']", searchkey);
					FetchSearchResult(mTagFormat, mSearchCriteria);
				} else {
					mProgressDialog.show();
					mTagFormat = String.format(
							"['%s',\"content:type=Episode\"]", searchkey);
					FetchSearchResult(mTagFormat, mSearchCriteria);
				}
			}
		});
		/** adding the click to the listView */
		mSearchList.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View arg1,
					int position, long arg3) {
				
				mSelectedView = arg1;
				arg1.setAlpha((float) 0.5);
				
				MVPlaylistMedia media = (MVPlaylistMedia) parent
						.getItemAtPosition(position);
				String ids = null;
				int pos = mAllVideosList.indexOf(media);
				for (int count = pos; count < mAllVideosList.size(); count++) {
					if (ids == null)
						ids = mAllVideosList.get(count).getId();
					else
						ids = ids + "," + mAllVideosList.get(count).getId();
				}
				Intent intent = new Intent(SearchActivity.this,
						EpisodeActivity.class);
				intent.putExtra("MediaObj", media);
				intent.putExtra("MediaIds", ids);
				startActivity(intent);
			}
		});

	}

	/** view pager Adapter class */

	public class ViewPagerAdapter extends PagerAdapter {

		public ViewPagerAdapter() {
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
			return mSearchItem.length;
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			final View imageLayout = getLayoutInflater().inflate(
					R.layout.search_title_item, view, false);
			imageLayout.setTag(position);

			TextView search_txt = (TextView) imageLayout
					.findViewById(R.id.search_item_txt);
			search_txt.setText(mSearchItem[position]);
			search_txt.setGravity(Gravity.CENTER);
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
				AppHelper.getAlert(SearchActivity.this, "",
						getResources().getString(R.string.no_result)).show();
				mAllVideosList = new ArrayList<MVPlaylistMedia>();
				mPagerAdapter = new SearchListAdapter(SearchActivity.this, 0,
						mAllVideosList);
				mSearchList.setAdapter(mPagerAdapter);
				return;
			}
			mApiCallInProgress = false;
			mTotalPages = searchlist.getTotalPages();
			if (mPageCount != 0) {
				mPagerAdapter.addAll(searchlist.getMedia());
				mPagerAdapter.notifyDataSetChanged();
				return;
			}
			/** setting the the search list Adapter showing the search list */
			mAllVideosList = new ArrayList<MVPlaylistMedia>();
			mAllVideosList.addAll(searchlist.getMedia());
			mPagerAdapter = new SearchListAdapter(SearchActivity.this, 0,
					mAllVideosList);
			mSearchList.setAdapter(mPagerAdapter);
		}

		@Override
		public void onRequestFailure(String message) {
			mLog.info("SPICE", message);
			mProgressDialog.dismiss();
			mLoader.setVisibility(View.INVISIBLE);
		}

		@Override
		public void onRetryRequest() {
			spiceManager.execute(
					new MVMediaSearchRequest(AppHelper
							.getToken(SearchActivity.this), mTagFormat, mSearchCriteria,
							mPageCount), Constants.CACHE_KEY_SEARCH_KEY,
					DurationInMillis.ALWAYS_EXPIRED, new MVSearchRequestListner(
							SearchActivity.this));
		}

	}

	/** web service call for the search */

	private void FetchSearchResult(String tag, String searchCriteria) {
		/** invoking the search call */
		mLog.info("Fetching Search Result");
		spiceManager.execute(
				new MVMediaSearchRequest(AppHelper
						.getToken(SearchActivity.this), tag, searchCriteria,
						mPageCount), Constants.CACHE_KEY_SEARCH_KEY,
				DurationInMillis.ALWAYS_EXPIRED, new MVSearchRequestListner(
						SearchActivity.this));

	}

	/** calling the web service when scrolling the list */

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		int threshold = 1;
		int count = view.getCount();
		if (scrollState == SCROLL_STATE_IDLE) {
			/**
			 * Load more items
			 */

			if (view.getLastVisiblePosition() >= count - threshold) {

				if (mPageCount == mTotalPages - 1) {
					Toast.makeText(SearchActivity.this,
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

	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

	}

	@Override
	public void onDestroy() {
		if (mAdView != null) {
			mAdView.destroy();
		}
		super.onDestroy();
	}

	@Override
	public void onStart() {
		super.onStart();

		// Send a screen view when the Activity is displayed to the user.
		AdultSwim.getGaTracker().set(Fields.SCREEN_NAME, "SearchPage");

		AdultSwim.getGaTracker().send(MapBuilder.createAppView().build());
	}

	@Override
	public void onPause() {

		super.onPause();
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
