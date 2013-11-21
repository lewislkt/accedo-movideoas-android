package com.turnerapac.adultswimau.apps.generic;

/*
 * This class is used to create share url. It remove all escape characters,transform to lowercase,
 * and replace spaces with hyphen
 */
public class MVShowNameHelper {

	public static String getValue(String name) {

		String mShowName = name;
		mShowName = mShowName.replaceAll("[|?,.!*<\":>+\\[\\]/']", "");
		mShowName = mShowName.replaceAll(" - ", "-");
		mShowName = mShowName.replaceAll("\\s+", "-");
		mShowName = mShowName.toLowerCase();
		return mShowName;
	}

}
