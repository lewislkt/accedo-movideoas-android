package com.turnerapac.adultswimau.apps.generic.globalsearch;

import java.util.ArrayList;

import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.turnerapac.adultswimau.apps.generic.R;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.turnerapac.adultswimau.apps.generic.AppHelper;
import com.turnerapac.adultswimau.apps.generic.MVMediaImageHelper;
import com.turnerapac.adultswimau.apps.generic.MVTagHelper;
import com.turnerapac.adultswimau.apps.generic.model.MVPlaylistMedia;

public class SearchListAdapter extends ArrayAdapter<MVPlaylistMedia> {
	private Context mContext;
	private ArrayList<MVPlaylistMedia> mArraySearchList;
	private org.slf4j.Logger mLog;

	public SearchListAdapter(Activity mContext, int textViewResourceId,
			ArrayList<MVPlaylistMedia> mArraySearchList) {
		super(mContext, textViewResourceId, mArraySearchList);
		this.mContext = mContext;
		this.mArraySearchList = mArraySearchList;
		mLog = LoggerFactory.getLogger(SearchListAdapter.class);
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		View view = convertView;

		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.shows_listitem, null);

		} else {
			view = convertView;
		}
		ImageView imageView = (ImageView) view.findViewById(R.id.image_show);
		try {
			UrlImageViewHelper.setUrlDrawable(imageView, MVMediaImageHelper
					.getImageUrl(mContext, mArraySearchList.get(position)
							.getImagePath()));
		} catch (NullPointerException e) {
			mLog.error("NullPointer exception, image path null");
		}
		TextView txtname = (TextView) view.findViewById(R.id.textView1);
		txtname.setText(mArraySearchList.get(position).getTitle());
		TextView txtepisode = (TextView) view.findViewById(R.id.textView2);
		txtepisode.setText("Season "
				+ MVTagHelper.getValue(
						mArraySearchList.get(position).getTags(),
						MVTagHelper.NS_SHOW, MVTagHelper.PREDICATE_SEASON)
				+ ", Episode "
				+ MVTagHelper.getValue(
						mArraySearchList.get(position).getTags(),
						MVTagHelper.NS_EPISODE, MVTagHelper.PREDICATE_NUMBER));
		TextView txtduration = (TextView) view.findViewById(R.id.txt_duration);
		txtduration.setText(AppHelper.getTime(mArraySearchList.get(position)
				.getDuration()));

		return view;
	}

	@Override
	public int getCount() {
		return mArraySearchList.size();
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
}
