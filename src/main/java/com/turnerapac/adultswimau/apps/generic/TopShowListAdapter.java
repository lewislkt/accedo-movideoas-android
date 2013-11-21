package com.turnerapac.adultswimau.apps.generic;

import java.util.ArrayList;

import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.turnerapac.adultswimau.apps.generic.R;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.turnerapac.adultswimau.apps.generic.customviews.ResizableImageView;
import com.turnerapac.adultswimau.apps.generic.model.MVPlaylistChild;

/*
 * List Adapter For Top Shows.
 */
public class TopShowListAdapter extends ArrayAdapter<MVPlaylistChild> {
	private ArrayList<MVPlaylistChild> mPlayListArray;
	private Context mContext;
	private ViewHolder mHolder;
	public LayoutInflater mInflater;
	private org.slf4j.Logger mLog;

	public TopShowListAdapter(Activity ctx, int textViewResourceId,
			ArrayList<MVPlaylistChild> arrTopShows) {

		super(ctx, textViewResourceId, arrTopShows);
		this.mContext = ctx;
		this.mPlayListArray = arrTopShows;
		this.mInflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLog = LoggerFactory.getLogger(TopShowListAdapter.class);
	}

	public static class ViewHolder {
		ResizableImageView mImageView;
		TextView mTextView;
	}

	@Override
	public int getCount() {
		return mPlayListArray.size();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		if (convertView == null) {
			mHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.topshow_listitem, null);
			mHolder.mTextView = (TextView) convertView
					.findViewById(R.id.topshow_nametxt);
			mHolder.mImageView = (ResizableImageView) convertView
					.findViewById(R.id.topshow_listimg);
			convertView.setTag(mHolder);
		} else
			mHolder = (ViewHolder) convertView.getTag();
		mHolder.mTextView.setText(mPlayListArray.get(position).getTitle());
		try {
			String url = null;
			if (AppHelper.isTablet(mContext))
				url = Constants.BANNER_IMAGE_BASE_URL_TABLET
						+ MVTagHelper.getValue(mPlayListArray.get(position)
								.getTags(), MVTagHelper.NS_PLAYLIST,
								MVTagHelper.PREDICATE_PATH)
						+ Constants.BANNER_IMAGE_URL_EXTENSION_TABLET;
			else
				url = Constants.BANNER_IMAGE_BASE_URL_PHONE
						+ MVTagHelper.getValue(mPlayListArray.get(position)
								.getTags(), MVTagHelper.NS_PLAYLIST,
								MVTagHelper.PREDICATE_PATH)
						+ Constants.BANNER_IMAGE_URL_EXTENSION_PHONE;
			UrlImageViewHelper.setUrlDrawable(mHolder.mImageView, url);
		} catch (NullPointerException e) {
			mLog.error("NullPointer exception, image path pull");
		}
		return convertView;
	}

}
