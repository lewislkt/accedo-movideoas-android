package com.turnerapac.adultswimau.apps.generic;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.turnerapac.adultswimau.apps.generic.R;
import com.accedo.fontface.CustomFonts;
import com.turnerapac.adultswimau.apps.generic.model.MVPlaylistChild;

/*
 * List Adapter For All Shows.
 */
public class AllShowListAdapter extends ArrayAdapter<MVPlaylistChild> {
	private ArrayList<MVPlaylistChild> mShowsArray;

	private Context mContext;

	public AllShowListAdapter(Activity context, int textViewResourceId,
			ArrayList<MVPlaylistChild> mShows) {
		super(context, textViewResourceId, mShows);
		this.mContext = context;
		this.mContext = context;
		this.mShowsArray = mShows;
		new CustomFonts(context);
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		View mView = convertView;

		if (mView == null) {
			LayoutInflater mInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mView = mInflater.inflate(R.layout.allshow_listitem, null);

		} else {
			mView = convertView;
		}
		TextView mTextView = (TextView) mView
				.findViewById(R.id.allshow_nametxt);
		mTextView.setText(mShowsArray.get(position).getTitle());
		return mView;
	}

	@Override
	public int getCount() {
		return mShowsArray.size();
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

}
