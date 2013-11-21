package com.turnerapac.adultswimau.apps.generic;

import com.google.ads.AdSize;
import com.google.analytics.tracking.android.Logger.LogLevel;

public class Constants {

	// TestFlight Application Token
	public static String TESTFLIGHT_APPTOKEN = "63d4353c-1ccd-4d35-b704-96c071261613";

	// MO-video Application Details
	public static String MOVIDEO_APP_ALIAS_PHONE = "adultswim-universal-androidphone";

	public static String MOVIDEO_APP_ALIAS_TABLET = "adultswim-universal-androidtablet";

	public static String MOVIDEO_APP_KEY = "movideoAdultSwimAsia";

	// Playlist ids
	public static String PLAYLIST_ID_FEATURED = "55267";

	public static String PLAYLIST_ID_PROMOTED = "55290";

	public static String PLAYLIST_ID_TOPSHOWS = "55274";

	public static String PLAYLIST_ID_ALLSHOWS = "55284";

	// AD Details
	public static String AD_UNIT_ID_PHONE = "/3626/adultswim_MobileApps/AndroidPhone";

	public static String AD_UNIT_ID_TABLET = "/3626/adultswim_MobileApps/AndroidTablet";

	public static AdSize[] AD_SIZE_PHONE = { AdSize.BANNER, new AdSize(320, 48) };

	public static AdSize[] AD_SIZE_TABLET = { AdSize.BANNER,
			new AdSize(728, 90) };

	public static AdSize[] CAROUSEL_AD_SIZE_PHONE = { AdSize.BANNER,
			new AdSize(320, 250) };

	public static AdSize[] CAROUSEL_AD_SIZE_TABLET_LANDSCAPE = { AdSize.BANNER,
			new AdSize(960, 250) };

	public static AdSize[] CAROUSEL_AD_SIZE_TABLET_PORTRAIT = { AdSize.BANNER,
			new AdSize(728, 90) };

	public static String PREFS_NAME = "adultswimpoc";

	public static String MOVIDEO_TOKEN = "MOVIDEO_TOKEN";

	public static String BASE_URL = "http://api.movideo.com/rest";

	public static String EMPTY_DATA = "";

	// Cache Keys
	public static String CACHE_KEY_FEATURED_PLAYLIST_MEDIA = "CACHE_KEY_FEATURED_PLAYLIST_MEDIA";

	public static String CACHE_KEY_FEATURED_PLAYLIST = "CACHE_KEY_FEATURED_PLAYLIST";

	public static String CACHE_KEY_PROMOTED_MEDIA = "CACHE_KEY_PROMOTED_MEDIA";

	public static String CACHE_KEY_PLAYLIST_MEDIA = "CACHE_KEY_PLAYLIST_MEDIA";

	public static String CACHE_KEY_TOPSHOWS_PLAYLIST = "CACHE_KEY_TOPSHOWS_PLAYLIST";

	public static String CACHE_KEY_TOPSHOWS_PLAYLIST_MEDIA = "CACHE_KEY_TOPSHOWS_PLAYLIST_MEDIA";

	public static String CACHE_KEY_TOPSHOWS_PLAYLIST_MEDIA_BANNER = "CACHE_KEY_TOPSHOWS_PLAYLIST_MEDIA_BANNER";

	public static String CACHE_KEY_ALLSHOWS_PLAYLIST = "CACHE_KEY_ALLSHOWS_PLAYLIST";

	public static String CACHE_KEY_SEARCH_KEY = "CACHE_KEY_SEARCH";

	public static String CACHE_KEY_EPISODE_COUNT = "CACHE_KEY_EPISODE_COUNT";

	public static String CACHE_KEY_CLIP_COUNT = "CACHE_KEY_CLIP_COUNT";

	public static String CACHE_KEY_ALL_VIDEOS_COUNT = "CACHE_KEY_ALL_VIDEOS_COUNT";

	public static String CACHE_KEY_EPISODE_RELATED_EVENTS = "CACHE_KEY_EPISODE_RELATED_EVENTS";

	public static int SEARCH_PAGE_SIZE = 10;

	public static String MEDIA_ID = "MEDIA_ID";

	// Banner and Carousel Urls
	public static String CAROUSEL_IMAGE_BASE_URL_PHONE = "http://www.adultswim.com.au/assets/images/carousel/";

	public static String CAROUSEL_IMAGE_BASE_URL_TABLET = "http://www.adultswim.com.au/assets/images/carousel/";

	public static String BANNER_IMAGE_BASE_URL_TABLET = "http://www.adultswim.com.au/assets/images/show-images/";

	public static String BANNER_IMAGE_BASE_URL_PHONE = "http://www.adultswim.com.au/assets/images/show-images/";

	public static String CAROUSEL_IMAGE_URL_EXTENSION_PHONE = "-phone.jpg";

	public static String CAROUSEL_IMAGE_URL_EXTENSION_TABLET = "-tablet.jpg";

	public static String BANNER_IMAGE_URL_EXTENSION_PHONE = "/show-header-xxs.jpg";

	public static String BANNER_IMAGE_URL_EXTENSION_TABLET = "/show-header-md.jpg";

	// Splash screen Urls
	public static String SPLASH_SCREEN_IMAGE_URL_PHONE = "http://adultswim.com.au/assets/splash/android_splash_360x640.png";

	public static String SPLASH_SCREEN_IMAGE_URL_TABLET_PORTRAIT = "http://adultswim.com.au/assets/splash/android_splash_800x1280.png";

	public static String SPLASH_SCREEN_IMAGE_URL_TABLET_LANDSCAPE = "http://adultswim.com.au/assets/splash/android_splash_1280x800.png";

	// Sharing Details
	public static String SHARING_BASE_URL = "http://www.adultswim.com.au/";

	public static String SHARING_VIA = "via @adultswim";

	// Fonts Uri
	public static String headingFontFaceUri = "fonts/BlocExtCond.otf";

	public static String bodyFontFaceUri = "fonts/HelveticaNeue.ttf";

	/*
	 * Google Analytics configuration values.
	 */
	// Placeholder property ID.
	public static final String GA_PROPERTY_ID_PHONE = "UA-43201154-4";

	public static final String GA_PROPERTY_TABLET = "UA-43201154-5";

	// Dispatch period in seconds.
	public static final int GA_DISPATCH_PERIOD = 30;

	// Prevent hits from being sent to reports, i.e. during testing.
	public static final boolean GA_IS_DRY_RUN = false;

	// GA Logger Level.
	public static final LogLevel GA_LOG_VERBOSITY = LogLevel.INFO;

	// GA Events
	public static String GOOGLE_ANALYTICS_PLAYER_STARTCATGORY = "MediaPlayer Started";

	public static String GOOGLE_ANALYTICS_PLAYER_START_ACTION = "Start playing";

	public static String GOOGLE_ANALYTICS_PLAYER_START_LABEL = "Start playing";

	public static String GOOGLE_ANALYTICS_PLAYER_STOP_CATGORY = "MediaPlayer Stoped";

	public static String GOOGLE_ANALYTICS_PLAYER_STOP_ACTION = "MediaPlayer istopped";

	public static String GOOGLE_ANALYTICS_PLAYER_STOP_LABEL = "MediaPlayer Stoped";

	public static String GOOGLE_ANALYTICS_COMPLETE_FIRST_QUARTILE_CATGORY = "First quartile";

	public static String GOOGLE_ANALYTICS_COMPLETE_FIRST_QUARTILE_ACTION = "Video completed first_quartile";

	public static String GOOGLE_ANALYTICS_COMPLETE_FIRST_QUARTILE_LABEL = "Video completed first_quartile";

	public static String GOOGLE_ANALYTICS_COMPLETE_MID_POINT_CATGORY = "Half quartile";

	public static String GOOGLE_ANALYTICS_COMPLETE_MID_POINT_ACTION = "Video completed half_quartile";

	public static String GOOGLE_ANALYTICS_COMPLETE_MID_POINT_QUARTILE_LABEL = "completed half_quartile";

	public static String GOOGLE_ANALYTICS_COMPLETE_THIRD_QUARTILE_CATGORY = "Third quartile";

	public static String GOOGLE_ANALYTICS_COMPLETE_THIRD_QUARTILE_ACTION = "Video third_quartile ";

	public static String GOOGLE_ANALYTICS_COMPLETE__THIRD_QUARTILE_LABEL = "completed third_quartile";

	public static String GOOGLE_ANALYTICS_PLAYER_PAUSED_CATGORY = "MediaPlayer Pause";

	public static String GOOGLE_ANALYTICS_PLAYER_PAUSED_ACTION = "Video is Paused ";

	public static String GOOGLE_ANALYTICS_PLAYER_PAUSED_LABEL = "video is paused";

	public static String GOOGLE_ANALYTICS_PLAYER_RESUMED_CATGORY = "MediaPlayer Resume";

	public static String GOOGLE_ANALYTICS_PLAYER_RESUMED_ACTION = "Video player is Resumed ";

	public static String GOOGLE_ANALYTICS_PLAYER_RESUMED_LABEL = "video is Resumed";

	public static String GOOGLE_ANALYTICS_APP_START_CATEGORY = "Application started";

	public static String GOOGLE_ANALYTICS_APP_START_ACTION = "Application Started";

	public static String GOOGLE_ANALYTICS_APP_START_LABEL = "Adult_Swim_start";

	public static String GOOGLE_ANALYTICS_APP_STOP_CATEGORY = "Application Stoped";

	public static String GOOGLE_ANALYTICS_APP_STOP_ACTION = "Application Stoped";

	public static String GOOGLE_ANALYTICS_APP_STOP_LABEL = "Adult_Swim_stoped";

	public static String GOOGLE_ANALYTICS_APP_PAUSE_CATEGORY = "Application Paused";

	public static String GOOGLE_ANALYTICS_APP_PAUSE_ACTION = "Application Paused";

	public static String GOOGLE_ANALYTICS_APP_PAUSE_LABEL = "Adult_Swim_Paused";

	public static String GOOGLE_ANALYTICS_APP_RESUME_CATEGORY = "Application Resumed";

	public static String GOOGLE_ANALYTICS_APP_RESUME_ACTION = "Application Resumed";

	public static String GOOGLE_ANALYTICS_APP_RESUME_LABEL = "Adult_Swim_Paused";

	public static String GOOGLE_ANALYTICS_MENU_CATEGORY = "Menu";

	public static String GOOGLE_ANALYTICS_FEATURED_CATEGORY = "Featured";

	public static String GOOGLE_ANALYTICS_TOPSHOWS_CATEGORY = "TopShows";

	public static String GOOGLE_ANALYTICS_ALLSHOWS_CATEGORY = "AllShows";

	public static String GOOGLE_ANALYTICS_PLAYLIST_CATEGORY = "Playlist";

	public static String GOOGLE_ANALYTICS_CLICK_ACTION = "Click";

	public static String GOOGLE_ANALYTICS_SHARE_ACTION = "Share";

	public static String GOOGLE_ANALYTICS_PLAY_ACTION = "Play";

	public static String GOOGLE_ANALYTICS_FEATURED_VALUES = "Featured";

	public static String GOOGLE_ANALYTICS_TOPSHOWS_VALUES = "Top Shows";

	public static String GOOGLE_ANALYTICS_ALLSHOWS_VALUES = "All Shows";

	public static String GOOGLE_ANALYTICS_PLAYLIST_VALUES = "Playlist";

	public static int HIGH_BIT_RATE = 1000000;

	public static String GOOGLE_HTTP_CLIENT_401_ERROR_MESSAGE = "No authentication challenges found";

	public static String ROBOSPICE_NO_NETWORK_MESSAGE = "Network is not available";

}
