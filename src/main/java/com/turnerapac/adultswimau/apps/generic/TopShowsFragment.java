package com.turnerapac.adultswimau.apps.generic;

import java.util.ArrayList;

import org.slf4j.LoggerFactory;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.turnerapac.adultswimau.apps.generic.R;
import com.google.ads.AdRequest;
import com.google.ads.doubleclick.DfpAdView;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.turnerapac.adultswimau.apps.generic.model.MVPlaylistChild;
import com.turnerapac.adultswimau.apps.generic.model.MVPlaylistChildList;
import com.turnerapac.adultswimau.apps.generic.service.MVPlaylistChildRequest;
import com.turnerapac.adultswimau.apps.generic.service.ServiceRequestListener;

public class TopShowsFragment extends BaseFragment {

	private GridView mTopShowsGridView;
	org.slf4j.Logger mLog;
	private ArrayList<MVPlaylistChild> mPlayListArray;
	private ProgressDialog mProgressDialog;
	private DfpAdView mAdView;
	private View mSelectedView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_topshows, container,
				false);
		// Get Banner Ad
		mAdView = AppHelper.getAdView(getActivity());
		RelativeLayout layout = (RelativeLayout) view
				.findViewById(R.id.topshow_addlayout);
		layout.bringToFront();
		layout.addView(mAdView);
		// Load banner Ad
		mAdView.loadAd(new AdRequest());

		return view;
	}

	@Override
	public void onDestroy() {
		// Destroy Ad view
		if (mAdView != null) {
			mAdView.destroy();
		}
		super.onDestroy();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mLog = LoggerFactory.getLogger(TopShowsFragment.class);

		mProgressDialog = new ProgressDialog(getActivity());
		mProgressDialog.setMessage("Loading Top Shows...");
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setCancelable(true);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.show();

		mTopShowsGridView = (GridView) view.findViewById(R.id.topshow_list);

		mLog.info("Fetching Top Shows");
		/**
		 * Top Shows API call
		 */
		spiceManager.getFromCacheAndLoadFromNetworkIfExpired(
				new MVPlaylistChildRequest(AppHelper.getToken(getActivity()),
						Constants.PLAYLIST_ID_TOPSHOWS),
				Constants.CACHE_KEY_TOPSHOWS_PLAYLIST, 10000,
				new MVPlaylistChildRequestListner(getActivity()));
		mTopShowsGridView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				mSelectedView = arg1;
				// Change color of selected view
				arg1.setAlpha((float) 0.5);
				// Navigate to Shows page
				MVPlaylistChild playlistchild = (MVPlaylistChild) arg0
						.getItemAtPosition(arg2);
				Intent intent = new Intent(getActivity(), ShowsActivity.class);
				intent.putExtra("PlayListChild", playlistchild);
				intent.putExtra("Category",
						Constants.GOOGLE_ANALYTICS_TOPSHOWS_CATEGORY);
				startActivity(intent);

			}
		});

	}

	@Override
	public void onResume() {
		super.onResume();
		// Change back to original color of selected view
		if (mSelectedView != null)
			mSelectedView.setAlpha(1);

	}

	private class MVPlaylistChildRequestListner extends
			ServiceRequestListener<MVPlaylistChildList> {

		public MVPlaylistChildRequestListner(Context context) {
			super(context);

		}

		public void onRequestSuccess(MVPlaylistChildList playlist) {
			mLog.info("Child Playlist Retrived");
			mProgressDialog.dismiss();
			if (playlist == null || playlist.getPlaylist() == null
					|| playlist.getPlaylist().isEmpty()) {
				// Show alert if no response
				AppHelper.getAlert(
						getActivity(),
						getResources().getString(R.string.alert_title_error),
						getResources().getString(
								R.string.media_is_currently_not_available))
						.show();
				return;
			}
			mPlayListArray = new ArrayList<MVPlaylistChild>();
			mPlayListArray.addAll(playlist.getPlaylist());
			TopShowListAdapter toplistadapter = new TopShowListAdapter(
					getActivity(), 0, mPlayListArray);
			mTopShowsGridView.setAdapter(toplistadapter);
		}

		@Override
		public void onRequestFailure(String message) {
			mLog.error("Child Playlist Fetching Failed", message);
			mProgressDialog.dismiss();
		}

		@Override
		public void onRetryRequest() {
			/**
			 * Retry Top Shows API call if request failed due to token
			 * expiration
			 */
			spiceManager.getFromCacheAndLoadFromNetworkIfExpired(
					new MVPlaylistChildRequest(AppHelper
							.getToken(getActivity()),
							Constants.PLAYLIST_ID_TOPSHOWS),
					Constants.CACHE_KEY_TOPSHOWS_PLAYLIST,
					DurationInMillis.ONE_MINUTE * 10,
					new MVPlaylistChildRequestListner(getActivity()));

		}
	}

	@Override
	public void onDestroyView() {
		mProgressDialog.dismiss();
		super.onDestroyView();
	}

	@Override
	public void onStart() {
		super.onStart();

		// Send a screen view when the Activity is displayed to the user.
		AdultSwim.getGaTracker().set(Fields.SCREEN_NAME, "TopshowsPage");
		AdultSwim.getGaTracker().send(MapBuilder.createAppView().build());
	}
}
