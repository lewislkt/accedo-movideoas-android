package com.turnerapac.adultswimau.apps.generic;

import java.util.ArrayList;
import java.util.List;

import movideo.android.Movideo;
import movideo.android.enums.EventType;
import movideo.android.event.Event;
import movideo.android.event.IEventListener;
import movideo.android.player.MovideoPlayer;
import movideo.android.player.control.images.ImageStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

import com.testflightapp.lib.TestFlight;

public class PlayerActivity extends Activity {

	private MovideoPlayer movideoPlayer;

	private Movideo movideoApp;

	private Logger mLog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			mLog = LoggerFactory.getLogger(PlayerActivity.class);
			String apiKey;
			String appAlias;
			if (AppHelper.isTablet(this)) {
				apiKey = Constants.MOVIDEO_APP_KEY;
				appAlias = Constants.MOVIDEO_APP_ALIAS_TABLET;
			}
			else {
				apiKey = Constants.MOVIDEO_APP_KEY;
				appAlias = Constants.MOVIDEO_APP_ALIAS_PHONE;
			}
			movideoApp = new Movideo(this, apiKey, appAlias);

			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			getWindow()
					.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
			//this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			/** set MO video player as the content view of this activity */
			setContentView(movideoApp.getMovideoPlayerLayout());

			movideoPlayer = movideoApp.getMovideoPlayer();
			movideoPlayer.setPlayerController(new AdultSwimPlayerController(this, new ImageStore(),
					new movideo.android.util.Logger()));
			movideoPlayer.enableRepeatMode(false);
			movideoPlayer.enableShuffleMode(false);
			movideoPlayer.setMaxBitrate(Constants.HIGH_BIT_RATE); // 1 Mbps
			movideoPlayer.setAutoPlay(true);
			Intent intent = getIntent();

			String playlist = intent.getStringExtra(HomeActivity.PLAYLIST_ID);
			if (playlist != null) {
				long playlistId = Long.parseLong(playlist);
				addPlaylist(playlistId);
			}
			else {
				String mediaIds = intent.getStringExtra(HomeActivity.MEDIA_ID);
				addMedia(mediaIds);
			}

			/**
			 * add event listener to exit the activity when the player finishes the play back
			 */
			final Handler handler = new Handler();
			movideoPlayer.addListener(EventType.MEDIA_QUEUE_FINISHED, new IEventListener() {
				public void onEventFired(Event arg0) {
					handler.post(new Runnable() {
						public void run() {
							PlayerActivity.this.finish();
						}
					});
				}
			});
			/*
			 * event listener for player error
			 */
			movideoPlayer.addListener(EventType.ERROR, new IEventListener() {

				public void onEventFired(Event arg0) {
					mLog.error("Playback Error", arg0.toString());
					TestFlight.log("Player Error : " + arg0.toString());

				}
			});

		}
		catch (Exception e) {
			TestFlight.log("Player Error : " + e.getMessage());
			mLog.error("Playback Error", e.getMessage(), e);
		}
	}

	private void addMedia(String mediaIds) {
		String[] mediaIdArr = mediaIds.split(",");
		if (mediaIdArr != null && mediaIdArr.length > 0) {
			List<Long> media = new ArrayList<Long>(mediaIdArr.length);
			for (String str : mediaIdArr) {
				try {
					media.add(Long.parseLong(str.trim()));
				}
				catch (NumberFormatException e) {
				}
			}

			if (!media.isEmpty()) {
				movideoPlayer.clearQueue();
				movideoPlayer.addToQueue(media.toArray(new Long[] {}));
			}
		}
	}

	private void addPlaylist(long playlistId) {
		try {
			movideoPlayer.clearQueue();
			movideoPlayer.addPlaylist(playlistId);
		}
		catch (movideo.android.exception.MovideoPlayerException e) {
			mLog.error("Add to playlist Failed", e.getMessage(), e);
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (movideoPlayer != null) {
			movideoPlayer.restart();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (movideoPlayer != null) {
			movideoPlayer.resume();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (movideoPlayer != null) {
			movideoPlayer.pause();
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		movideoApp.destroy();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		//this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

}
