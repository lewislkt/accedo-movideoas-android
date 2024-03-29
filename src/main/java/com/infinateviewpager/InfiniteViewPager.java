package com.infinateviewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * A {@link ViewPager} that allows pseudo-infinite paging with a wrap-around
 * effect. Should be used with an {@link InfinitePagerAdapter}.
 * 
 */
public class InfiniteViewPager extends ViewPager {
	
	private boolean infinitePagesEnabled = true;

	public InfiniteViewPager(Context context) {
		super(context);
	}

	public InfiniteViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setAdapter(PagerAdapter adapter) {
		super.setAdapter(adapter);
		// offset first element so that we can scroll to the left
		setCurrentItem(0);
	}

	@Override
	public void setCurrentItem(int item) {
		if (infinitePagesEnabled) {
			// offset the current item to ensure there is space to scroll
			item = getOffsetAmount() + (item % getAdapter().getCount());
		}
		super.setCurrentItem(item);

	}
	
	public void enableInfinitePages(boolean enable) {
		infinitePagesEnabled = enable;
		if (getAdapter() instanceof InfinitePagerAdapter) {
			InfinitePagerAdapter infAdapter = (InfinitePagerAdapter) getAdapter();
			infAdapter.enableInfinitePages(enable);
		}
	}

	private int getOffsetAmount() {
		if (getAdapter() instanceof InfinitePagerAdapter) {
			InfinitePagerAdapter infAdapter = (InfinitePagerAdapter) getAdapter();
			
			return infAdapter.getRealCount() * 100;
		} else {
			return 0;
		}
	}

}
