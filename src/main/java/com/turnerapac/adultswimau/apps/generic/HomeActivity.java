package com.turnerapac.adultswimau.apps.generic;

import com.turnerapac.adultswimau.apps.generic.R;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

public class HomeActivity extends BaseActivity {

	public static final String PLAYLIST_ID = "PLAYLIST_ID";

	public static final String MEDIA_ID = "MEDIA_ID";

	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	org.slf4j.Logger log;

	private ActionBar actionBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// If not tablet, set screen orientation to portrait
		if (AppHelper.isTablet(this))
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
		/**
		 * Adding navigation tabs
		 */
		actionBar = getActionBar();
		actionBar
				.setLogo(AppHelper.isTablet(this) ? R.drawable.tab_logo_selector
						: R.drawable.logo_selector);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		AddTabs();

	}

	private void AddTabs() {
		Tab tab = actionBar
				.newTab()
				.setText(R.string.action_navigation_featured)
				.setTabListener(
						new TabListener<FeaturedFragment>(this,
								getResources().getString(
										R.string.action_navigation_featured),
								FeaturedFragment.class)

				);

		actionBar.addTab(tab);

		tab = actionBar
				.newTab()
				.setText(R.string.action_navigation_top_shows)
				.setTabListener(
						new TabListener<TopShowsFragment>(this,
								getResources().getString(
										R.string.action_navigation_top_shows),
								TopShowsFragment.class));
		actionBar.addTab(tab);

		tab = actionBar
				.newTab()
				.setText(R.string.action_navigation_all_shows)
				.setTabListener(
						new TabListener<AllShowsFragment>(this,
								getResources().getString(
										R.string.action_navigation_all_shows),
								AllShowsFragment.class));
		actionBar.addTab(tab);

		tab = actionBar
				.newTab()
				.setText(R.string.action_navigation_playlists)
				.setTabListener(
						new TabListener<PlayListsFragment>(this,
								getResources().getString(
										R.string.action_navigation_playlists),
								PlayListsFragment.class));
		actionBar.addTab(tab);

	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current tab position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current tab position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}

	public static class TabListener<T extends Fragment> implements
			ActionBar.TabListener {
		private Fragment mFragment;

		private final Activity mActivity;

		private final String mTag;

		private final Class<T> mClass;

		/**
		 * Constructor used each time a new tab is created.
		 * 
		 * @param activity
		 *            The host Activity, used to instantiate the fragment
		 * @param tag
		 *            The identifier tag for the fragment
		 * @param clz
		 *            The fragment's Class, used to instantiate the fragment
		 */
		public TabListener(Activity activity, String tag, Class<T> clz) {
			mActivity = activity;
			mTag = tag;
			mClass = clz;
		}

		public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
			if (arg0.getPosition() == 0) {
				AppHelper.sendGoogleAnyaticsEvent(
						Constants.GOOGLE_ANALYTICS_MENU_CATEGORY,
						Constants.GOOGLE_ANALYTICS_CLICK_ACTION,
						Constants.GOOGLE_ANALYTICS_FEATURED_VALUES);
			} else if (arg0.getPosition() == 1) {
				AppHelper.sendGoogleAnyaticsEvent(
						Constants.GOOGLE_ANALYTICS_MENU_CATEGORY,
						Constants.GOOGLE_ANALYTICS_CLICK_ACTION,
						Constants.GOOGLE_ANALYTICS_TOPSHOWS_VALUES);
			} else if (arg0.getPosition() == 2) {
				AppHelper.sendGoogleAnyaticsEvent(
						Constants.GOOGLE_ANALYTICS_MENU_CATEGORY,
						Constants.GOOGLE_ANALYTICS_CLICK_ACTION,
						Constants.GOOGLE_ANALYTICS_ALLSHOWS_VALUES);
			} else {
				AppHelper.sendGoogleAnyaticsEvent(
						Constants.GOOGLE_ANALYTICS_MENU_CATEGORY,
						Constants.GOOGLE_ANALYTICS_CLICK_ACTION,
						Constants.GOOGLE_ANALYTICS_PLAYLIST_VALUES);
			}

		}

		/* The following are each of the ActionBar.TabListener callbacks */
		public void onTabSelected(Tab arg0, FragmentTransaction ft) {
			if (mFragment == null) {
				// If not, instantiate and add it to the activity
				/** sending the events to the google analytics */
				if (arg0.getPosition() == 0) {
					AppHelper.sendGoogleAnyaticsEvent(
							Constants.GOOGLE_ANALYTICS_MENU_CATEGORY,
							Constants.GOOGLE_ANALYTICS_CLICK_ACTION,
							Constants.GOOGLE_ANALYTICS_FEATURED_VALUES);
				} else if (arg0.getPosition() == 1) {
					AppHelper.sendGoogleAnyaticsEvent(
							Constants.GOOGLE_ANALYTICS_MENU_CATEGORY,
							Constants.GOOGLE_ANALYTICS_CLICK_ACTION,
							Constants.GOOGLE_ANALYTICS_TOPSHOWS_VALUES);
				} else if (arg0.getPosition() == 2) {
					AppHelper.sendGoogleAnyaticsEvent(
							Constants.GOOGLE_ANALYTICS_MENU_CATEGORY,
							Constants.GOOGLE_ANALYTICS_CLICK_ACTION,
							Constants.GOOGLE_ANALYTICS_ALLSHOWS_VALUES);
				} else {
					AppHelper.sendGoogleAnyaticsEvent(
							Constants.GOOGLE_ANALYTICS_MENU_CATEGORY,
							Constants.GOOGLE_ANALYTICS_CLICK_ACTION,
							Constants.GOOGLE_ANALYTICS_PLAYLIST_VALUES);
				}
				mFragment = Fragment.instantiate(mActivity, mClass.getName());
				ft.replace(android.R.id.content, mFragment, mTag);
			} else {
				// If it exists, simply attach it in order to show it
				ft.attach(mFragment);
			}
		}

		public void onTabUnselected(Tab arg0, FragmentTransaction ft) {
			if (mFragment != null) {
				// Detach the fragment, because another one is being attached
				ft.detach(mFragment);
			}

		}
	}

}
