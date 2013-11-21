package com.turnerapac.adultswimau.apps.generic;

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
import com.turnerapac.adultswimau.apps.generic.model.MVPlaylistMedia;

/*
 * List Adapter For Extended PlayList.
 */
public class ExtendedPlaylistArrayAdapter extends ArrayAdapter<MVPlaylistMedia> {
	private Context mContext;

	private ArrayList<MVPlaylistMedia> mMediaArray;

	private org.slf4j.Logger mLog;

	public ExtendedPlaylistArrayAdapter(Activity context,
			int textViewResourceId, ArrayList<MVPlaylistMedia> arrContent) {
		super(context, textViewResourceId, arrContent);
		this.mContext = context;
		this.mMediaArray = arrContent;

		mLog = LoggerFactory.getLogger(ExtendedPlaylistArrayAdapter.class);
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		View view = convertView;

		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.extendedplaylist_listitem, null);

		} else {
			view = convertView;
		}
		ImageView imageView = (ImageView) view.findViewById(R.id.image_show);
		try {
			UrlImageViewHelper.setUrlDrawable(
					imageView,
					MVMediaImageHelper.getImageUrl(mContext,
							mMediaArray.get(position).getImagePath()));
		} catch (NullPointerException e) {
			mLog.error("NullPointer exception, image path pull");
		}
		TextView mNameTextView = (TextView) view.findViewById(R.id.textView1);
		mNameTextView.setText(mMediaArray.get(position).getTitle());
		TextView mEpisodeTextView = (TextView) view
				.findViewById(R.id.textView2);
		mEpisodeTextView.setText("Season "
				+ MVTagHelper.getValue(mMediaArray.get(position).getTags(),
						MVTagHelper.NS_SHOW, MVTagHelper.PREDICATE_SEASON)
				+ ", Episode "
				+ MVTagHelper.getValue(mMediaArray.get(position).getTags(),
						MVTagHelper.NS_EPISODE, MVTagHelper.PREDICATE_NUMBER));
		TextView mDurationTextView = (TextView) view
				.findViewById(R.id.txt_duration);
		mDurationTextView.setText(AppHelper.getTime(mMediaArray.get(position)
				.getDuration()));
		TextView mDescriptionTextView = (TextView) view
				.findViewById(R.id.textView3);
		if (mDescriptionTextView != null)
			mDescriptionTextView.setText(mMediaArray.get(position)
					.getDescription());
		ImageView mRatingImageView = (ImageView) view
				.findViewById(R.id.img_rating);
		if (mRatingImageView != null)
			mRatingImageView.setImageResource(MVRatingImageHelper
					.getValue(MVTagHelper.getValue(mMediaArray.get(position)
							.getTags(), MVTagHelper.NS_CLASSIFICATION,
							MVTagHelper.PREDICATE_RATING)));
		final Button mShareButton = (Button) view.findViewById(R.id.btn_share);
		if (mShareButton != null) {
			mShareButton.setTag(position);
			mShareButton.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					String tag = String.valueOf(mShareButton.getTag());
					/** sending the event to Google Analytics */
					String mGALabel = MVTagHelper.getValue(
							mMediaArray.get(Integer.valueOf(tag)).getTags(),
							MVTagHelper.NS_SHOW, MVTagHelper.PREDICATE_NAME)
							+ ""
							+ MVTagHelper.getValue(
									mMediaArray.get(Integer.valueOf(tag))
											.getTags(), MVTagHelper.NS_SHOW,
									MVTagHelper.PREDICATE_SEASON)
							+ ""
							+ MVTagHelper.getValue(
									mMediaArray.get(Integer.valueOf(tag))
											.getTags(), MVTagHelper.NS_EPISODE,
									MVTagHelper.PREDICATE_NUMBER);
					AppHelper.sendGoogleAnyaticsEvent(
							Constants.GOOGLE_ANALYTICS_PLAYLIST_CATEGORY,
							Constants.GOOGLE_ANALYTICS_SHARE_ACTION, mGALabel);
					String mShareText = Constants.SHARING_BASE_URL
							+ MVShowNameHelper.getValue(MVTagHelper.getValue(
									mMediaArray.get(Integer.valueOf(tag))
											.getTags(), MVTagHelper.NS_SHOW,
									MVTagHelper.PREDICATE_NAME))
							+ "/"
							+ mMediaArray.get(Integer.valueOf(tag)).getId()
							+ "/"
							+ MVShowNameHelper.getValue(mMediaArray.get(
									Integer.valueOf(tag)).getTitle()) + " "
							+ Constants.SHARING_VIA;
					Intent i = new Intent(Intent.ACTION_SEND);
					i.setType("text/plain");
					i.putExtra(android.content.Intent.EXTRA_TEXT, mShareText);
					mContext.startActivity(i);
				}
			});
		}
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
