package com.turnerapac.adultswimau.apps.generic;

import android.app.Fragment;

import com.google.analytics.tracking.android.EasyTracker;
import com.octo.android.robospice.SpiceManager;
import com.turnerapac.adultswimau.apps.generic.service.XmlSpiceService;
/*
 * Parent fragment which is extended by all other fragments
 */
public class BaseFragment extends Fragment {

	protected SpiceManager spiceManager = new SpiceManager(XmlSpiceService.class);

	@Override
	public void onStart() {
		super.onStart();
		spiceManager.start(getActivity());
		EasyTracker.getInstance(getActivity()).activityStart(getActivity());

	}

	@Override
	public void onStop() {
		spiceManager.shouldStop();
		super.onStop();
		EasyTracker.getInstance(getActivity()).activityStop(getActivity());
	}
}
