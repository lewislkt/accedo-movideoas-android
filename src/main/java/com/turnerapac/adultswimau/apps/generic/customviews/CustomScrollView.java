package com.turnerapac.adultswimau.apps.generic.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

import com.jess.ui.TwoWayGridView;
/**
* CustomScrollView: Prevent auto focusing the twowaygridview on loading content
*/
public class CustomScrollView extends ScrollView {

	public CustomScrollView(Context context) {
		super(context);
	}

	public CustomScrollView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
	}

	public CustomScrollView(Context context, AttributeSet attributeSet,
			int defStyle) {
		super(context, attributeSet, defStyle);
	};
	@Override 
	public void requestChildFocus(View child, View focused) { 
	    if (focused instanceof TwoWayGridView ) 
	       return;
	    super.requestChildFocus(child, focused);
	}
	
}
