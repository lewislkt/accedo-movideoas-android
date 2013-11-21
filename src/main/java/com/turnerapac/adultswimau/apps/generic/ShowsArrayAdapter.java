package com.turnerapac.adultswimau.apps.generic;

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
import com.turnerapac.adultswimau.apps.generic.model.MVPlaylistMedia;

public class ShowsArrayAdapter extends ArrayAdapter<MVPlaylistMedia> {
	private Context mContext;
	private ArrayList<MVPlaylistMedia> mMediaArray;
	private org.slf4j.Logger mLog;

	public ShowsArrayAdapter(Activity context, int textViewResourceId,
			ArrayList<MVPlaylistMedia> arrContent) {
		super(context, textViewResourceId, arrContent);
		this.mContext = context;
		this.mMediaArray = arrContent;
		mLog = LoggerFactory.getLogger(ShowsArrayAdapter.class);
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		View view = convertView;

		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.featured_listitem, null);

		} else {
			view = convertView;
		}
		ImageView imageView = (ImageView) view
				.findViewById(R.id.img_content_small_item);
		try {
			UrlImageViewHelper.setUrlDrawable(
					imageView,
					MVMediaImageHelper.getImageUrl(mContext,
							mMediaArray.get(position).getImagePath()));
		} catch (NullPointerException e) {
			mLog.error("NullPointer exception, image path pull");
		}
		TextView mNameTextView = (TextView) view
				.findViewById(R.id.txt_show_name);
		mNameTextView.setText(MVTagHelper.getValue(mMediaArray.get(position)
				.getTags(), MVTagHelper.NS_SHOW, MVTagHelper.PREDICATE_NAME));
		TextView mDurationTextView = (TextView) view
				.findViewById(R.id.txt_duration);
		mDurationTextView.setText(AppHelper.getTime(mMediaArray.get(position)
				.getDuration()));
		TextView mDescriptionTextView = (TextView) view
				.findViewById(R.id.txt_desc);
		mDescriptionTextView.setText(mMediaArray.get(position).getTitle());
		return view;
	}

	@Override
	public int getCount() {
		return mMediaArray.size();
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

}