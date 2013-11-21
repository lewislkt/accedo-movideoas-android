package com.turnerapac.adultswimau.apps.generic.globalsearch;

import java.util.ArrayList;

import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.turnerapac.adultswimau.apps.generic.R;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.turnerapac.adultswimau.apps.generic.AppHelper;
import com.turnerapac.adultswimau.apps.generic.Constants;
import com.turnerapac.adultswimau.apps.generic.MVMediaImageHelper;
import com.turnerapac.adultswimau.apps.generic.MVRatingImageHelper;
import com.turnerapac.adultswimau.apps.generic.MVShowNameHelper;
import com.turnerapac.adultswimau.apps.generic.MVTagHelper;
import com.turnerapac.adultswimau.apps.generic.model.MVPlaylistMedia;

public class SearchTabListAdapter extends ArrayAdapter<MVPlaylistMedia> {
	private Context mContext;
	private ArrayList<MVPlaylistMedia> mArraySearchListTab;
	private org.slf4j.Logger mLog;

	public SearchTabListAdapter(Activity mContext, int textViewResourceId,
			ArrayList<MVPlaylistMedia> mArraySearchList) {
		super(mContext, textViewResourceId, mArraySearchList);
		this.mContext = mContext;
		this.mArraySearchListTab = mArraySearchList;
		mLog = LoggerFactory.getLogger(SearchTabListAdapter.class);
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		View view = convertView;

		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.search_list_item, null);

		} else {
			view = convertView;
		}
		ImageView imageView = (ImageView) view
				.findViewById(R.id.search_tab_image_show);
		try {
			UrlImageViewHelper.setUrlDrawable(imageView, MVMediaImageHelper
					.getImageUrl(mContext, mArraySearchListTab.get(position)
							.getImagePath()));
		} catch (NullPointerException e) {
			mLog.error("NullPointer exception, image path null");
		}
		TextView txtname = (TextView) view
				.findViewById(R.id.search_tab_name_txt);
		txtname.setText(mArraySearchListTab.get(position).getTitle());
		TextView txtepisode = (TextView) view
				.findViewById(R.id.search_tab_destxt);
		txtepisode.setText("Season "
				+ MVTagHelper.getValue(mArraySearchListTab.get(position)
						.getTags(), MVTagHelper.NS_SHOW,
						MVTagHelper.PREDICATE_SEASON)
				+ ", Episode "
				+ MVTagHelper.getValue(mArraySearchListTab.get(position)
						.getTags(), MVTagHelper.NS_EPISODE,
						MVTagHelper.PREDICATE_NUMBER));
		TextView txtduration = (TextView) view
				.findViewById(R.id.search_tab_txt_duration);
		txtduration.setText(AppHelper.getTime(mArraySearchListTab.get(position)
				.getDuration()));
		TextView txtdescription = (TextView) view
				.findViewById(R.id.search_tab_infotxt);
		txtdescription.setText(mArraySearchListTab.get(position)
				.getDescription());
		ImageView ratingimg = (ImageView) view
				.findViewById(R.id.search_tab_rating);
		ratingimg.setImageResource(MVRatingImageHelper.getValue(MVTagHelper
				.getValue(mArraySearchListTab.get(position).getTags(),
						MVTagHelper.NS_CLASSIFICATION,
						MVTagHelper.PREDICATE_RATING)));

		final Button mShareButton = (Button) view
				.findViewById(R.id.search_tab_share_button);
		mShareButton.setTag(position);
		mShareButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String tag = String.valueOf(mShareButton.getTag());
				String mShareText = Constants.SHARING_BASE_URL
						+ MVShowNameHelper.getValue(MVTagHelper.getValue(
								mArraySearchListTab.get(Integer.valueOf(tag))
										.getTags(), MVTagHelper.NS_SHOW,
								MVTagHelper.PREDICATE_NAME))
						+ "/"
						+ mArraySearchListTab.get(Integer.valueOf(tag)).getId()
						+ "/"
						+ MVShowNameHelper.getValue(mArraySearchListTab.get(
								Integer.valueOf(tag)).getTitle()) + " "
						+ Constants.SHARING_VIA;
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("text/plain");
				i.putExtra(Intent.EXTRA_SUBJECT, "[adult swim] "
						+ mArraySearchListTab.get(Integer.valueOf(tag))
								.getTitle());
				i.putExtra(android.content.Intent.EXTRA_TEXT, mShareText);
				mContext.startActivity(i);
			}
		});
		return view;
	}

	@Override
	public int getCount() {
		return mArraySearchListTab.size();
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
}
