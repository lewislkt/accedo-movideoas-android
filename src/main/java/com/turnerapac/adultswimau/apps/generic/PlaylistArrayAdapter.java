package com.turnerapac.adultswimau.apps.generic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.turnerapac.adultswimau.apps.generic.R;
import com.turnerapac.adultswimau.apps.generic.model.MVPlaylistMedia;

/*
 * List Adapter For PlayList.
 */
public class PlaylistArrayAdapter extends ArrayAdapter<MVPlaylistMedia> {

	private TextView menuItemText;
	private TextView menuSubItemText;
	private List<MVPlaylistMedia> menu_item = new ArrayList<MVPlaylistMedia>();

	public PlaylistArrayAdapter(Context context, int textViewResourceId,
			ArrayList<MVPlaylistMedia> menuItems) {
		super(context, textViewResourceId, menuItems);
		this.menu_item = menuItems;
	}

	@Override
	public int getCount() {
		return this.menu_item.size();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.playlist_listitem, parent, false);
		}
		MVPlaylistMedia item = getItem(position);
		menuItemText = (TextView) row.findViewById(R.id.menu_item);
		menuItemText.setText(item.getTitle());
		menuSubItemText = (TextView) row.findViewById(R.id.menu_item_sub);
		menuSubItemText.setText(item.getDescription());
		return row;
	}

}