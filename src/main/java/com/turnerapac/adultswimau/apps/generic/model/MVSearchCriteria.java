package com.turnerapac.adultswimau.apps.generic.model;

public class MVSearchCriteria {
	public static String DEFAULT = "&orderBy=creationdate&orderDesc=true";
	public static String MOST_POPULAR = "&orderBy=playsweek&orderDesc=true";
	public static String MOST_RECENT = "&orderBy=creationdate&orderDesc=true";
	public static String TRENDING = "&orderBy=playsday&orderDesc=true";
	public static String MEDIA_COUNT = "&orderValue=episode:number&totals=true";
	public static String CONTENT_TYPE_CLIP = "Clip";
	public static String CONTENT_TYPE_EPISODE = "Episode";


	public static String EPISODE_NUMBER = "&orderBy=tag&orderValue=episode:number";
	public static String TITLE = "&orderBy=title&orderDesc=true";
	public static String DESCRIPTION = "&orderBy=description&orderDesc=true";
	public static String MOST_PLAYED_TODAY = "&orderBy=playsday&orderDesc=true";
	public static String MOST_PLAYED_THIS_WEEK = "&orderBy=playsweek&orderDesc=true";
	public static String MOST_PLAYED_THIS_MONTH = "&orderBy=playsmonth&orderDesc=true";

}
