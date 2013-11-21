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
import android.widget.ListView;
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

public class PlayListsFragment extends BaseFragment {

	private ListView mListView;
	org.slf4j.Logger mLog;
	private ProgressDialog mProgressDialog;
	private DfpAdView mAdView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_playlists, container,
				false);
		// Get Banner Ad
		mAdView = AppHelper.getAdView(getActivity());
		RelativeLayout layout = (RelativeLayout) view
				.findViewById(R.id.addlayout);
		layout.addView(mAdView);
		// Load Banner Ad
		mAdView.loadAd(new AdRequest());
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mProgressDialog = new ProgressDialog(getActivity());
		mProgressDialog.setMessage("Loading playlist...");
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setCancelable(true);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.show();

		mListView = (ListView) view.findViewById(R.id.lv_playlists);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				MVPlaylistChild playlistchild = (MVPlaylistChild) arg0
						.getItemAtPosition(arg2);
				/** sending the play list name event to google analytics */
				AppHelper.sendGoogleAnyaticsEvent(
						Constants.GOOGLE_ANALYTICS_PLAYLIST_CATEGORY,
						Constants.GOOGLE_ANALYTICS_CLICK_ACTION,
						playlistchild.getTitle());
				// Navigate to Extended Playlist page
				Intent intent = new Intent(getActivity(),
						ExtendedPlaylistActivity.class);
				intent.putExtra("PlayListChild", playlistchild);
				startActivity(intent);

			}
		});

		mLog = LoggerFactory.getLogger(PlayListsFragment.class);
		mLog.info("Fetching playlist");
		/**
		 * Playlist API call
		 */
		spiceManager.getFromCacheAndLoadFromNetworkIfExpired(
				new MVPlaylistChildRequest(AppHelper.getToken(getActivity()),
						Constants.PLAYLIST_ID_FEATURED),
				Constants.CACHE_KEY_FEATURED_PLAYLIST,
				DurationInMillis.ONE_MINUTE * 10,
				new MVPlaylistChildRequestListner(getActivity()));
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
				AppHelper.getAlert(
						getActivity(),
						getResources().getString(R.string.alert_title_error),
						getResources().getString(
								R.string.media_is_currently_not_available))
						.show();
				return;
			}
			ArrayList<MVPlaylistChild> arrPlayList = new ArrayList<MVPlaylistChild>();
			arrPlayList.addAll(playlist.getPlaylist());
			AllShowListAdapter adapter = new AllShowListAdapter(getActivity(),
					0, arrPlayList);
			mListView.setAdapter(adapter);

		}

		@Override
		public void onRequestFailure(String message) {
			mLog.error("Child Playlist Fetching Failed", message);
			mProgressDialog.dismiss();
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

	@Override
	public void onDestroyView() {
		mProgressDialog.dismiss();
		super.onDestroyView();
	}

	@Override
	public void onStart() {
		super.onStart();

		// Send a screen view when the Activity is displayed to the user.
		AdultSwim.getGaTracker().set(Fields.SCREEN_NAME, "PlaylistPage");

		AdultSwim.getGaTracker().send(MapBuilder.createAppView().build());
	}

}
