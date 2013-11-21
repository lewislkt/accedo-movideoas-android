package com.turnerapac.adultswimau.apps.generic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.slf4j.Logger;
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

public class AllShowsFragment extends BaseFragment {

	private ListView mListView;

	private Logger mLog;

	private ProgressDialog mProgressDialog;

	private DfpAdView mAdView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_allshows, container,
				false);
		// Get banner ad
		mAdView = AppHelper.getAdView(getActivity());
		RelativeLayout layout = (RelativeLayout) view
				.findViewById(R.id.allshow_addlayout);
		layout.addView(mAdView);
		// load banner ad
		mAdView.loadAd(new AdRequest());
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mProgressDialog = new ProgressDialog(getActivity());
		mProgressDialog.setMessage(getResources().getString(
				R.string.loading_allshows_playlist));
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setCancelable(true);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.show();

		mListView = (ListView) view.findViewById(R.id.allshow_list);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				// Get clicked item
				MVPlaylistChild playlistchild = (MVPlaylistChild) arg0
						.getItemAtPosition(arg2);

				// Send click event to google analytics
				AppHelper.sendGoogleAnyaticsEvent(
						Constants.GOOGLE_ANALYTICS_ALLSHOWS_CATEGORY,
						Constants.GOOGLE_ANALYTICS_CLICK_ACTION,
						playlistchild.getTitle());

				// Navigate to shows page
				Intent intent = new Intent(getActivity(), ShowsActivity.class);
				intent.putExtra("PlayListChild", playlistchild);
				intent.putExtra("Category",
						Constants.GOOGLE_ANALYTICS_ALLSHOWS_CATEGORY);
				startActivity(intent);

			}
		});

		mLog = LoggerFactory.getLogger(AllShowsFragment.class);
		mLog.info("Fetching All Shows");
		/**
		 * All shows API call
		 */
		spiceManager.getFromCacheAndLoadFromNetworkIfExpired(
				new MVPlaylistChildRequest(AppHelper.getToken(getActivity()),
						Constants.PLAYLIST_ID_ALLSHOWS, 200),
				Constants.CACHE_KEY_ALLSHOWS_PLAYLIST,
				DurationInMillis.ONE_MINUTE * 10,
				new MVPlaylistChildRequestListner(getActivity()));

	}

	private class MVPlaylistChildRequestListner extends
			ServiceRequestListener<MVPlaylistChildList> {

		public MVPlaylistChildRequestListner(Context context) {
			super(context);

		}

		public void onRequestSuccess(MVPlaylistChildList playlist) {

			mLog.info("All Shows Retrived");
			mProgressDialog.dismiss();
			if (playlist == null || playlist.getPlaylist() == null
					|| playlist.getPlaylist().isEmpty()) {
				// Show alert if response is empty
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
			// Sort the array in alphabetical order
			Collections.sort(arrPlayList, new Comparator<MVPlaylistChild>() {
				public int compare(MVPlaylistChild arg0, MVPlaylistChild arg1) {

					String str1 = arg0.getTitle();
					String[] str1_parts = str1.split(" ");
					if (str1_parts.length > 1)
						if (str1_parts[0].equalsIgnoreCase("THE"))
							str1 = str1.substring(str1.indexOf(" ") + 1);

					String str2 = arg1.getTitle();
					String[] str2_parts = str2.split(" ");
					if (str2_parts.length > 1)
						if (str2_parts[0].equalsIgnoreCase("THE"))
							str2 = str2.substring(str2.indexOf(" ") + 1);
					return str1.compareToIgnoreCase(str2);
				}
			});
			// Set the adapter to the list view
			AllShowListAdapter adapter = new AllShowListAdapter(getActivity(),
					0, arrPlayList);
			mListView.setAdapter(adapter);
		}

		@Override
		public void onRequestFailure(String message) {
			mLog.error("All Shows Fetching Failed", message);
			mProgressDialog.dismiss();
		}

		@Override
		public void onRetryRequest() {
			/**
			 * Recall the api if the request failed due to token expiration
			 */
			spiceManager.getFromCacheAndLoadFromNetworkIfExpired(
					new MVPlaylistChildRequest(AppHelper
							.getToken(getActivity()),
							Constants.PLAYLIST_ID_ALLSHOWS, 200),
					Constants.CACHE_KEY_ALLSHOWS_PLAYLIST,
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
		AdultSwim.getGaTracker().set(Fields.SCREEN_NAME, "AllShowspage");
		AdultSwim.getGaTracker().send(MapBuilder.createAppView().build());
	}
}
