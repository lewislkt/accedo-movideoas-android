package com.turnerapac.adultswimau.apps.generic;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Point;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.octo.android.robospice.SpiceManager;
import com.turnerapac.adultswimau.apps.generic.service.XmlSpiceService;
import com.turnerapac.adultswimau.apps.generic.settings.SettingsActivity;

/*
 * Parent activity which is extended by all other activities
 */
public class BaseActivity extends Activity implements OnQueryTextListener {

	protected SpiceManager spiceManager = new SpiceManager(
			XmlSpiceService.class);

	private Dialog msettingsDialog;

	private int mActionBarHeight;

	private int mDisplayWidth;

	private int mDisplayHeight;

	private MenuItem mMenuItemSettings;

	private TextView mAppVersionTxt;

	private SearchView mSearchView;

	private PackageManager manager;

	private PackageInfo info;

	@Override
	protected void onStart() {
		super.onStart();
		spiceManager.start(this);

		EasyTracker.getInstance(this).activityStart(this);
		TypedValue tv = new TypedValue();
		// getting the height of the Action bar
		if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
			mActionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
					getResources().getDisplayMetrics());
		}
		/** Adjust settings dialog height to screen height-actionbar height */
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		mDisplayWidth = size.x;
		mDisplayHeight = size.y;
		msettingsDialog = new Dialog(this, R.style.FullHeightDialog);

		// Get App Version

		manager = this.getPackageManager();
		info = null;
		try {
			info = manager.getPackageInfo(this.getPackageName(), 0);
		} catch (NameNotFoundException e) {

			e.printStackTrace();
		}

	}

	@Override
	protected void onStop() {
		spiceManager.shouldStop();
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
		/** Google analytics event for application stop */
		/**
		 * sending the the media player event to google analytics
		 */
		AdultSwim.getGaTracker().send(
				MapBuilder.createEvent(
						Constants.GOOGLE_ANALYTICS_APP_STOP_CATEGORY,
						Constants.GOOGLE_ANALYTICS_APP_STOP_ACTION,
						Constants.GOOGLE_ANALYTICS_APP_START_LABEL, null)
						.build());

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(
				com.turnerapac.adultswimau.apps.generic.R.menu.main, menu);
		/**
		 * Customizing search view
		 */
		MenuItem searchItem = menu.findItem(R.id.action_search);
		mSearchView = (SearchView) searchItem.getActionView();
		mSearchView.setOnQueryTextListener(this);
		// listener to know if search view is expanded or collapsed
		mSearchView.addOnLayoutChangeListener(_searchExpandHandler);
		int searchPlateId = mSearchView.getContext().getResources()
				.getIdentifier("android:id/search_plate", null, null);
		mSearchView.findViewById(searchPlateId).setBackgroundResource(
				R.drawable.search_custom_edit_text);
		return true;
	}

	/** adding the action in the sub menu item */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.submenu_settings:

			if (!AppHelper.isTablet(this)) {
				Intent i = new Intent(this, SettingsActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			} else {

				settingsTabPopupDialog();
				mMenuItemSettings = item;
				/** sending the screen view of settings page in tab */
				AdultSwim.getGaTracker()
						.set(Fields.SCREEN_NAME, "settingsPage");

				AdultSwim.getGaTracker().send(
						MapBuilder.createAppView().build());
			}
			return true;
		case R.id.submenu_about:
			Intent intent = null;
			if (AppHelper.isTablet(this))
				intent = new Intent(this, AboutTabletActivity.class);
			else
				intent = new Intent(this, AboutActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;

		case android.R.id.home:
			onBackPressed();

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/** setting a dialog pop up for the Tablet settings page */
	@SuppressLint("CutPasteId")
	public void settingsTabPopupDialog() {
		LayoutInflater inflater = LayoutInflater.from(getBaseContext());
		View contentView = inflater.inflate(R.layout.activity_settings_tab,
				null);
		int popupwidth;
		if (mDisplayWidth < mDisplayHeight) {
			popupwidth = (int) (0.55 * (mDisplayWidth));
		} else {
			popupwidth = (int) (0.40 * (mDisplayWidth));
		}
		msettingsDialog.setContentView(contentView);
		WindowManager.LayoutParams wmlp = msettingsDialog.getWindow()
				.getAttributes();
		wmlp.gravity = Gravity.RIGHT | Gravity.TOP;
		msettingsDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,
				mDisplayHeight);
		msettingsDialog.setCancelable(false);
		wmlp.x = 0;
		wmlp.y = mActionBarHeight;
		RelativeLayout lyt_popup = (RelativeLayout) msettingsDialog
				.findViewById(R.id.settings_layaout_tab);
		RelativeLayout setting_layout = (RelativeLayout) msettingsDialog
				.findViewById(R.id.settings_sub_layout);
		ViewGroup.LayoutParams mSettingsWidth = setting_layout
				.getLayoutParams();
		mSettingsWidth.width = popupwidth;
		RelativeLayout header_popup = (RelativeLayout) msettingsDialog
				.findViewById(R.id.headerlayout);
		ViewGroup.LayoutParams mheaderWidth = header_popup.getLayoutParams();
		mheaderWidth.height = mActionBarHeight;
		lyt_popup.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				mMenuItemSettings.setActionView(null);
				msettingsDialog.dismiss();
				return false;
			}
		});
		RelativeLayout lyt_settings = (RelativeLayout) msettingsDialog
				.findViewById(R.id.settings_sub_layout);
		lyt_settings.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		/** setting the app version */
		mAppVersionTxt = (TextView) msettingsDialog
				.findViewById(R.id.settings_version_apptxt);
		if (info != null)
			mAppVersionTxt.setText(info.versionName);

		msettingsDialog.show();

	}

	public boolean onQueryTextChange(String search_txt) {
		return false;
	}

	/** getting the search query */
	public boolean onQueryTextSubmit(String search_txt) {
		String searchkey = search_txt;
		// Collapse action view before navigating to search page
		mSearchView.onActionViewCollapsed();
		if (!AppHelper.isTablet(this)) {
			Intent i = new Intent(
					BaseActivity.this,
					com.turnerapac.adultswimau.apps.generic.globalsearch.SearchActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			i.putExtra("searchkey", searchkey);
			startActivity(i);
		} else {
			Intent i = new Intent(
					BaseActivity.this,
					com.turnerapac.adultswimau.apps.generic.globalsearch.SearchActivityTab.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			i.putExtra("searchkey", searchkey);
			startActivity(i);
		}
		return true;
	}

	private final OnLayoutChangeListener _searchExpandHandler = new OnLayoutChangeListener() {
		public void onLayoutChange(View v, int left, int top, int right,
				int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
			if (AppHelper.isTablet(BaseActivity.this))
				return;
			SearchView searchView = (SearchView) v;
			// Hide the action bar logo in search page
			if (!searchView.isIconified())
				getActionBar().setLogo(android.R.color.transparent);
			else
				getActionBar().setLogo(R.drawable.logo_selector);
		}
	};

}
