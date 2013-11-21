package com.turnerapac.adultswimau.apps.generic;

import android.content.Context;

/*
 * This class returns corresponding image url for the  device being used
 */
public class MVMediaImageHelper {

	public static final String IMAGE_MDPI = "146x82.jpg";

	public static final String IMAGE_HDPI = "219x123.jpg";

	public static final String IMAGE_XHDPI = "452x254.jpg";

	public static String getImageUrl(Context context, String imagePath) {
		if (AppHelper.isTablet(context))
			return imagePath + IMAGE_XHDPI;
		else
			return imagePath + IMAGE_HDPI;
	}
}
