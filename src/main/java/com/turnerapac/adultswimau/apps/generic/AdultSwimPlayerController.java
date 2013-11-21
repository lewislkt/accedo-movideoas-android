package com.turnerapac.adultswimau.apps.generic;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import movideo.android.enums.EventType;
import movideo.android.event.Event;
import movideo.android.event.IEventListener;
import movideo.android.model.IMediaFile;
import movideo.android.player.control.IMovideoPlayerControl;
import movideo.android.player.control.IPlayerController;
import movideo.android.player.control.images.ImageStore;
import movideo.android.util.Logger;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.turnerapac.adultswimau.apps.generic.R;
import com.turnerapac.adultswimau.apps.generic.R.color;
import com.google.inject.Inject;

@SuppressLint("ViewConstructor")
public class AdultSwimPlayerController extends FrameLayout implements
		IPlayerController {

	private static final int PROGRESSBAR_INSET_DIP = 5;
	private static final String LOG_CODE = "MovideoPlayerController";
	private static final int sDefaultTimeout = 4000;
	private static final int FADE_OUT = 1;
	private static final int SHOW_PROGRESS = 2;
	private static final int TRANSPARENT = 0x00000000;
	private static final String BITRATE_UNIT_SYMBOL = " kbps";

	private boolean enableShuffle;

	private boolean enableRepeat;

	private IMovideoPlayerControl player;
	private Logger logger;
	private ImageStore imageStore;
	private Context context;
	private View anchor;
	private LinearLayout rootView;
	private SeekBar progress;
	private TextView endTime, currentTime;
	private boolean showing;
	private boolean dragging;
	private StringBuilder formatBuilder;
	private Formatter formatter;
	private ImageButton pauseButton;
	private ImageButton nextButton;
	private ImageButton prevButton;
	private ImageButton bitrateButton;
	private ImageButton volumeButton;
	private ImageButton loopButton;
	private ImageButton shuffleButton;
	private BitrateSelector bitrateSelector;
	private List<IMediaFile> mediaFiles;

	private float screenDensity;

	private Handler handler = new MessageHandler(this);

	private IEventListener playerEventListener = new IEventListener() {

		public void onEventFired(Event event) {
			if (event.getEvent() == EventType.MEDIA_PLAY_STARTED) {
				updateNextPrevButtons();
			}
		}
	};

	private View.OnClickListener pauseListener = new View.OnClickListener() {
		public void onClick(View v) {
			logger.debug(LOG_CODE, "click event : play button");
			doPauseResume();
			show(sDefaultTimeout);
		}
	};

	private View.OnTouchListener playButtonTouchListener = new OnTouchListener() {

		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				logger.debug(LOG_CODE, "touch event : down : play button");
				if (player.isPlaying()) {
					pauseButton.setImageResource(R.drawable.play_active_icon);
				} else {
					pauseButton.setImageResource(R.drawable.pause_active_icon);
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				logger.debug(LOG_CODE, "touch event : up : play button");
				if (player.isPlaying()) {
					pauseButton.setImageResource(R.drawable.play_icon);
				} else {
					pauseButton.setImageResource(R.drawable.pause_icon);
				}
			}
			return false;
		}
	};

	private View.OnTouchListener nextButtonTouchListener = new OnTouchListener() {

		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				nextButton.setImageResource(R.drawable.fastforward_active_icon);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				nextButton.setImageResource(R.drawable.fastforward_icon);
			}
			return false;
		}
	};

	private View.OnTouchListener prevButtonTouchListener = new OnTouchListener() {

		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				prevButton.setImageResource(R.drawable.rewind_active_icon);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				prevButton.setImageResource(R.drawable.rewind_icon);
			}
			return false;
		}
	};

	private View.OnClickListener nextListener = new View.OnClickListener() {

		public void onClick(View v) {
			player.next();
			updateNextPrevButtons();
			show(sDefaultTimeout);
		}
	};

	private View.OnClickListener prevListener = new View.OnClickListener() {

		public void onClick(View v) {
			player.previous();
			updateNextPrevButtons();
			show(sDefaultTimeout);
		}
	};

	private View.OnClickListener bitrateListener = new OnClickListener() {
		public void onClick(View v) {
			bitrateSelector.show();
		}
	};

	private View.OnTouchListener bitrateButtonTouchListener = new OnTouchListener() {

		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				bitrateButton.setImageResource(R.drawable.settings_active_icon);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				bitrateButton.setImageResource(R.drawable.settings_con);
			}
			return false;
		}
	};

	private View.OnClickListener volumeButtonClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			if (player.isMuted()) {
				player.unmute();
				// change icon if unmute was success
				if (!player.isMuted()) {
					volumeButton.setImageResource(R.drawable.volume_icon);
				}
			} else {
				player.mute();
				// change icon if mute was success
				if (player.isMuted()) {
					volumeButton.setImageResource(R.drawable.mute_icon);
				}
			}
		}
	};

	private View.OnTouchListener volumeButtonTouchListener = new View.OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (player.isMuted()) {
					volumeButton.setImageResource(R.drawable.mute_active_icon);
				} else {
					volumeButton
							.setImageResource(R.drawable.volume_active_icon);
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				if (player.isMuted()) {
					volumeButton.setImageResource(R.drawable.mute_icon);
				} else {
					volumeButton.setImageResource(R.drawable.volume_icon);
				}
			}
			return false;
		}
	};

	private View.OnClickListener loopButtonClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			if (player.isRepeat()) {
				player.setRepeat(false);
				loopButton.setImageResource(R.drawable.loop_icon);
			} else {
				player.setRepeat(true);
				loopButton.setImageResource(R.drawable.loop_active_icon);
			}
		}
	};

	OnClickListener shuffleButtonClickListener = new OnClickListener() {

		public void onClick(View v) {
			player.shuffle();
		}
	};

	OnTouchListener shuffleButtonTouchListener = new OnTouchListener() {

		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				shuffleButton.setImageResource(R.drawable.shuffle_active_icon);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				shuffleButton.setImageResource(R.drawable.shuffle_icon);
			}

			return false;
		}
	};

	@Inject
	public AdultSwimPlayerController(Context context, ImageStore imageStore,
			Logger logger) {
		super(context);
		this.context = context;
		this.imageStore = imageStore;
		this.logger = logger;
	}

	@Override
	public void onFinishInflate() {
		initControllerView();
	}

	public void setMediaPlayer(IMovideoPlayerControl player) {
		this.player = player;
		updatePausePlay();

		player.addListener(EventType.MEDIA_PLAY_STARTED, playerEventListener);
	}

	/**
	 * Set the view that acts as the anchor for the control view. This can for
	 * example be a VideoView, or your Activity's main view.
	 * 
	 * @param view
	 *            The view to which to anchor the controller when it is visible.
	 */
	public void setAnchorView(View view) {
		anchor = view;

		removeAllViews();
		makeControllerView();

		logger.debug(LOG_CODE, "Setting Achor View ");
	}

	/**
	 * Create the view that holds the widgets that control playback. Derived
	 * classes can override this to create their own.
	 * 
	 * @return The controller view.
	 * @hide This doesn't work as advertised
	 */
	protected void makeControllerView() {
		screenDensity = context.getResources().getDisplayMetrics().density;
		rootView = new LinearLayout(context);
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		rootView.setBackgroundResource(R.drawable.player_bar);
		rootView.setOrientation(LinearLayout.HORIZONTAL);

		LinearLayout.LayoutParams lp;
		LinearLayout controlLayout = new LinearLayout(context);
		lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.CENTER_VERTICAL;
		controlLayout.setLayoutParams(lp);
		controlLayout.setBackgroundColor(Color.TRANSPARENT);
		controlLayout.setOrientation(LinearLayout.HORIZONTAL);
		rootView.addView(controlLayout);

		prevButton = new ImageButton(context);
		prevButton.setImageResource(R.drawable.rewind_icon);
		setViewBackground(prevButton, null);
		Point size = getMaxSize(
				imageStore.getImage(ImageStore.MEDIA_PREV_ACTIVE_IMAGE),
				imageStore.getImage(ImageStore.MEDIA_PREV_PRESSED_IMAGE));
		lp = new LinearLayout.LayoutParams(size.x, size.y);
		lp.gravity = Gravity.CENTER_VERTICAL;
		prevButton.setLayoutParams(lp);
		controlLayout.addView(prevButton);

		pauseButton = new ImageButton(context);
		pauseButton.setImageResource(R.drawable.play_icon);
		setViewBackground(pauseButton, null);
		size = getMaxSize(imageStore.getImage(ImageStore.MEDIA_PLAY_IMAGE),
				imageStore.getImage(ImageStore.MEDIA_PLAY_PRESSED_IMAGE));
		lp = new LinearLayout.LayoutParams(size.x, size.y);
		lp.gravity = Gravity.CENTER_VERTICAL;
		pauseButton.setLayoutParams(lp);
		controlLayout.addView(pauseButton);

		nextButton = new ImageButton(context);
		nextButton.setImageResource(R.drawable.fastforward_icon);
		setViewBackground(nextButton, null);
		size = getMaxSize(
				imageStore.getImage(ImageStore.MEDIA_NEXT_ACTIVE_IMAGE),
				imageStore.getImage(ImageStore.MEDIA_NEXT_PRESSED_IMAGE));
		lp = new LinearLayout.LayoutParams(size.x, size.y);
		lp.gravity = Gravity.CENTER_VERTICAL;
		nextButton.setLayoutParams(lp);
		controlLayout.addView(nextButton);

		LinearLayout progressBarLayout = new LinearLayout(context);
		lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
		lp.gravity = Gravity.CENTER_VERTICAL;
		progressBarLayout.setLayoutParams(lp);
		progressBarLayout.setOrientation(LinearLayout.HORIZONTAL);
		progressBarLayout.setBackgroundColor(Color.TRANSPARENT);
		rootView.addView(progressBarLayout);

		currentTime = new TextView(context);
		currentTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		currentTime.setTypeface(currentTime.getTypeface(), Typeface.BOLD);
		currentTime.setTextColor(Color.WHITE);
		currentTime.setPadding(1, 0, 1, 0);
		currentTime.setGravity(Gravity.CENTER_VERTICAL);
		lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.CENTER_VERTICAL;
		currentTime.setLayoutParams(lp);
		progressBarLayout.addView(currentTime);

		progress = new SeekBar(context, null,
				android.R.attr.progressBarStyleHorizontal);
		setSeekBarColor(progress, Color.WHITE, Color.DKGRAY,
				color.player_bitrate_color);

		Bitmap thumbImage = BitmapFactory.decodeResource(getResources(),
				R.drawable.scroll_handle_icon_resized_temp);
		logger.debug(LOG_CODE, "Thumb Size {0}x{1}", thumbImage.getWidth(),
				thumbImage.getHeight());
		logger.debug(LOG_CODE, "Screen Density {0}", context.getResources()
				.getDisplayMetrics().density);
		Drawable thumb = new BitmapDrawable(context.getResources(), thumbImage);
		Bitmap thumbActiveImage = BitmapFactory.decodeResource(getResources(),
				R.drawable.scroll_handle_active_icon);
		Drawable thumbActive = new BitmapDrawable(context.getResources(),
				thumbActiveImage);
		size = getMaxSize(thumbImage, thumbActiveImage);
		StateListDrawable thumbStates = new StateListDrawable();
		thumbStates.addState(new int[] { android.R.attr.state_pressed },
				thumbActive);
		thumbStates.addState(new int[] {}, thumb);
		progress.setThumb(thumbStates);
		progress.setPadding(size.x / 2, 0, size.x / 2, 0);
		lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
		lp.gravity = Gravity.CENTER_VERTICAL;
		progress.setLayoutParams(lp);
		progressBarLayout.addView(progress);

		endTime = new TextView(context);
		endTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		endTime.setTypeface(currentTime.getTypeface(), Typeface.BOLD);
		endTime.setTextColor(Color.WHITE);
		endTime.setPadding(1, 0, 1, 0);
		endTime.setGravity(Gravity.CENTER_HORIZONTAL);
		lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.CENTER_VERTICAL;
		endTime.setLayoutParams(lp);
		progressBarLayout.addView(endTime);

		bitrateButton = new ImageButton(context);
		bitrateButton.setImageResource(R.drawable.settings_con);
		setViewBackground(bitrateButton, null);
		size = getMaxSize(imageStore.getImage(ImageStore.BITRATE_IMAGE),
				imageStore.getImage(ImageStore.BITRATE_PRESSED_IMAGE));
		lp = new LinearLayout.LayoutParams(size.x, size.y);
		lp.gravity = Gravity.CENTER_VERTICAL;
		bitrateButton.setLayoutParams(lp);
		rootView.addView(bitrateButton);

		volumeButton = new ImageButton(context);
		volumeButton.setImageResource(R.drawable.volume_icon);
		setViewBackground(volumeButton, null);
		size = getMaxSize(imageStore.getImage(ImageStore.VOLUME_IMAGE),
				imageStore.getImage(ImageStore.VOLUME_PRESSED_IMAGE));
		lp = new LinearLayout.LayoutParams(size.x, size.y);
		lp.gravity = Gravity.CENTER_VERTICAL;
		volumeButton.setLayoutParams(lp);
		rootView.addView(volumeButton);

		loopButton = new ImageButton(context);
		loopButton.setImageResource(R.drawable.loop_icon);
		setViewBackground(loopButton, null);
		size = getMaxSize(imageStore.getImage(ImageStore.LOOP_IMAGE),
				imageStore.getImage(ImageStore.LOOP_PRESSED_IMAGE));
		lp = new LinearLayout.LayoutParams(size.x, size.y);
		lp.gravity = Gravity.CENTER_VERTICAL;
		loopButton.setLayoutParams(lp);
		rootView.addView(loopButton);

		enableRepeatButton(enableRepeat);

		shuffleButton = new ImageButton(context);
		shuffleButton.setImageResource(R.drawable.shuffle_icon);
		setViewBackground(shuffleButton, null);
		size = getMaxSize(imageStore.getImage(ImageStore.SHUFFLE_IMAGE),
				imageStore.getImage(ImageStore.SHUFFLE_PRESSED_IMAGE));
		lp = new LinearLayout.LayoutParams(size.x, size.y);
		lp.gravity = Gravity.CENTER_VERTICAL;
		shuffleButton.setLayoutParams(lp);
		rootView.addView(shuffleButton);

		enableShuffleButton(enableShuffle);

		initControllerView();
	}

	private void enableShuffleButton(boolean enable) {
		if (enable) {
			shuffleButton.setVisibility(View.VISIBLE);
		} else {
			shuffleButton.setVisibility(View.GONE);
		}
	}

	private void enableRepeatButton(boolean enable) {
		if (enable) {
			loopButton.setVisibility(View.VISIBLE);
		} else {
			loopButton.setVisibility(View.GONE);
		}
	}

	/**
	 * Set the background of a view to a given Drawable, or remove the
	 * background if its null.
	 * 
	 * @param view
	 * @param drawable
	 */
	private void setViewBackground(View view, Drawable drawable) {
		view.setBackgroundDrawable(drawable);

	}

	/**
	 * set colors for playback progress bar
	 * 
	 * @param seekBar
	 * @param progressColor
	 * @param secondaryProgressColor
	 * @param progressBackgroundColor
	 */
	private void setSeekBarColor(SeekBar seekBar, int progressColor,
			int secondaryProgressColor, int progressBackgroundColor) {
		int insetHeight = (int) (PROGRESSBAR_INSET_DIP * screenDensity);
		LayerDrawable ld = (LayerDrawable) seekBar.getProgressDrawable();
		Drawable progressColorDrawable = new InsetDrawable(new ColorDrawable(
				progressColor), 0, insetHeight, 0, insetHeight);
		ClipDrawable progressDrawable = new ClipDrawable(progressColorDrawable,
				Gravity.LEFT, ClipDrawable.HORIZONTAL);
		ld.setDrawableByLayerId(android.R.id.progress, progressDrawable);

		Drawable secProgressColorDrawable = new InsetDrawable(
				new ColorDrawable(secondaryProgressColor), 0, insetHeight, 0,
				insetHeight);
		ClipDrawable secProgressDrawable = new ClipDrawable(
				secProgressColorDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
		ld.setDrawableByLayerId(android.R.id.secondaryProgress,
				secProgressDrawable);

		Drawable backgroundDrawable = new InsetDrawable(new ColorDrawable(
				progressBackgroundColor), 0, insetHeight, 0, insetHeight);
		ld.setDrawableByLayerId(android.R.id.background, backgroundDrawable);
	}

	/**
	 * registers button event listeners
	 */
	private void initControllerView() {
		if (pauseButton != null) {
			pauseButton.requestFocus();
			pauseButton.setOnClickListener(pauseListener);
			pauseButton.setOnTouchListener(playButtonTouchListener);
		}

		if (bitrateButton != null) {
			bitrateButton.setOnClickListener(bitrateListener);
			bitrateButton.setOnTouchListener(bitrateButtonTouchListener);
			bitrateSelector = new BitrateSelector(bitrateButton, rootView);
		}

		if (nextButton != null) {
			nextButton.setOnClickListener(nextListener);
			nextButton.setOnTouchListener(nextButtonTouchListener);
		}

		if (prevButton != null) {
			prevButton.setOnClickListener(prevListener);
			prevButton.setOnTouchListener(prevButtonTouchListener);
		}

		if (progress != null) {
			progress.setOnSeekBarChangeListener(mSeekListener);
			progress.setMax(1000);
		}

		if (volumeButton != null) {
			volumeButton.setOnClickListener(volumeButtonClickListener);
			volumeButton.setOnTouchListener(volumeButtonTouchListener);
		}

		if (loopButton != null) {
			loopButton.setOnClickListener(loopButtonClickListener);
		}

		if (shuffleButton != null) {
			shuffleButton.setOnClickListener(shuffleButtonClickListener);
			shuffleButton.setOnTouchListener(shuffleButtonTouchListener);
		}

		formatBuilder = new StringBuilder();
		formatter = new Formatter(formatBuilder, Locale.getDefault());
	}

	/**
	 * Show the controller on screen. It will go away automatically after 3
	 * seconds of inactivity.
	 */
	public void show() {
		show(sDefaultTimeout);
	}

	/**
	 * Show the controller on screen. It will go away automatically after
	 * 'timeout' milliseconds of inactivity.
	 * 
	 * @param timeout
	 *            The timeout in milliseconds. Use 0 to show the controller
	 *            until hide() is called.
	 */
	public void show(int timeout) {
		if (!showing && anchor != null && anchor.getParent() != null) {
			setProgress();
			if (pauseButton != null) {
				pauseButton.requestFocus();
			}

			ViewGroup anchorParent = (ViewGroup) anchor.getParent();
			RelativeLayout.LayoutParams tlp2 = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			tlp2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

			anchorParent.addView(rootView, tlp2);

			showing = true;
		}
		initButtons();
		updatePausePlay();

		// cause the progress bar to be updated even if mShowing
		// was already true. This happens, for example, if we're
		// paused with the progress bar showing the user hits play.
		handler.sendEmptyMessage(SHOW_PROGRESS);

		Message msg = handler.obtainMessage(FADE_OUT);
		if (timeout != 0) {
			handler.removeMessages(FADE_OUT);
			handler.sendMessageDelayed(msg, timeout);
		}
	}

	/**
	 * initialise button status
	 */
	private void initButtons() {
		List<? extends IMediaFile> availableMediaFiles = player
				.getAvailableMediaFiles();
		if (availableMediaFiles != null && availableMediaFiles.size() > 1) {
			bitrateButton.setVisibility(View.VISIBLE);
		} else {
			bitrateButton.setVisibility(View.GONE);
		}
	}

	public void enableRepeatMode(boolean enable) {
		enableRepeat = enable;
		enableRepeatButton(enableRepeat);
	}

	public void enableShuffleMode(boolean enable) {
		enableShuffle = enable;
		enableShuffleButton(enableShuffle);
	}

	/**
	 * checks whether the controller is visible the screen.
	 */
	public boolean isShowing() {
		return showing;
	}

	/**
	 * Remove the controller from the screen.
	 */
	public void hide() {
		if (anchor == null || anchor.getParent() == null) {
			return;
		}

		try {
			((ViewGroup) anchor.getParent()).removeView(rootView);
			handler.removeMessages(SHOW_PROGRESS);
		} catch (IllegalArgumentException ex) {
			logger.warn(LOG_CODE, "already removed");
		}

		bitrateSelector.hide();
		showing = false;
	}

	/**
	 * format time in millis to mm:ss or hh:mm:ss
	 * 
	 * @param timeMs
	 * @return
	 */
	private String stringForTime(int timeMs) {
		int totalSeconds = timeMs / 1000;

		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;

		formatBuilder.setLength(0);
		if (hours > 0) {
			return formatter.format("%d:%02d:%02d", hours, minutes, seconds)
					.toString();
		} else {
			return formatter.format("%02d:%02d", minutes, seconds).toString();
		}
	}

	/**
	 * returns the maximum size of set of images
	 * 
	 * @param images
	 * @return
	 */
	private Point getMaxSize(Bitmap... images) {
		Point maxPoint = new Point(0, 0);
		for (Bitmap image : images) {
			if (image.getWidth() * image.getHeight() > (maxPoint.x * maxPoint.y)) {
				maxPoint.x = image.getWidth();
				maxPoint.y = image.getHeight();
			}
		}

		return maxPoint;
	}

	/**
	 * sets progress of playback progress bar
	 * 
	 * @return
	 */
	private int setProgress() {
		if (player == null || dragging) {
			return 0;
		}

		int position = player.getCurrentPosition();
		int duration = player.getDuration();
		if (progress != null) {
			if (duration > 0) {
				// use long to avoid overflow
				long pos = 1000L * position / duration;
				progress.setProgress((int) pos);
			}
			int percent = player.getBufferPercentage();
			progress.setSecondaryProgress(percent * 10);
		}

		if (endTime != null)
			endTime.setText(stringForTime(duration));
		if (currentTime != null)
			currentTime.setText(stringForTime(position));

		return position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		show(sDefaultTimeout);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onTrackballEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTrackballEvent(MotionEvent ev) {
		show(sDefaultTimeout);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.ViewGroup#dispatchKeyEvent(android.view.KeyEvent)
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (player == null) {
			return true;
		}

		int keyCode = event.getKeyCode();
		final boolean uniqueDown = event.getRepeatCount() == 0
				&& event.getAction() == KeyEvent.ACTION_DOWN;
		if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
				|| keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
				|| keyCode == KeyEvent.KEYCODE_SPACE) {
			if (uniqueDown) {
				doPauseResume();
				show(sDefaultTimeout);
				if (pauseButton != null) {
					pauseButton.requestFocus();
				}
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
			if (uniqueDown && !player.isPlaying()) {
				player.resume();
				updatePausePlay();
				show(sDefaultTimeout);
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
				|| keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
			if (uniqueDown && player.isPlaying()) {
				player.pause();
				updatePausePlay();
				show(sDefaultTimeout);
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
				|| keyCode == KeyEvent.KEYCODE_VOLUME_UP
				|| keyCode == KeyEvent.KEYCODE_VOLUME_MUTE) {
			// don't show the controls for volume adjustment
			return super.dispatchKeyEvent(event);
		} else if (keyCode == KeyEvent.KEYCODE_BACK
				|| keyCode == KeyEvent.KEYCODE_MENU) {
			if (uniqueDown) {
				hide();
			}
			return true;
		}

		show(sDefaultTimeout);
		return super.dispatchKeyEvent(event);
	}

	/**
	 * updates the status of pause/play button
	 */
	private void updatePausePlay() {
		if (rootView == null || pauseButton == null || player == null) {
			return;
		}

		if (player.isPlaying()) {
			pauseButton.setImageResource(R.drawable.pause_icon);
		} else {
			pauseButton.setImageResource(R.drawable.play_icon);
		}
	}

	/**
	 * updates the status of next/previous buttons
	 */
	private void updateNextPrevButtons() {
		if (rootView == null || pauseButton == null || player == null) {
			return;
		}

		if (player.hasNext()) {
			nextButton.setImageResource(R.drawable.fastforward_icon);
			nextButton.setEnabled(true);
			setImageAlpha(nextButton, 0xff);
		} else {
			nextButton.setImageResource(R.drawable.fastforward_icon);
			nextButton.setEnabled(false);
			setImageAlpha(nextButton, 0x30);
		}

		if (player.hasPrevious()) {
			prevButton.setImageResource(R.drawable.rewind_icon);
			prevButton.setEnabled(true);
			setImageAlpha(prevButton, 0xff);
		} else {
			prevButton.setImageResource(R.drawable.rewind_icon);
			prevButton.setEnabled(false);
			setImageAlpha(prevButton, 0x30);
		}
	}

	/**
	 * sets the transparency level of image buttons
	 * 
	 * @param button
	 * @param alpha
	 */
	private void setImageAlpha(ImageButton button, int alpha) {
		button.setAlpha(alpha);
	}

	/**
	 * toggle play/pause status
	 */
	private void doPauseResume() {
		if (player == null) {
			return;
		}

		if (player.isPlaying()) {
			player.pause();
		} else {
			player.resume();
		}
		updatePausePlay();
	}

	// There are two scenarios that can trigger the seekbar listener to trigger:
	//
	// The first is the user using the touchpad to adjust the posititon of the
	// seekbar's thumb. In this case onStartTrackingTouch is called followed by
	// a number of onProgressChanged notifications, concluded by
	// onStopTrackingTouch.
	// We're setting the field "mDragging" to true for the duration of the
	// dragging
	// session to avoid jumps in the position in case of ongoing playback.
	//
	// The second scenario involves the user operating the scroll ball, in this
	// case there WON'T BE onStartTrackingTouch/onStopTrackingTouch
	// notifications,
	// we will simply apply the updated position without suspending regular
	// updates.
	private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
		public void onStartTrackingTouch(SeekBar bar) {
			show(3600000);

			dragging = true;

			// By removing these pending progress messages we make sure
			// that a) we won't update the progress while the user adjusts
			// the seekbar and b) once the user is done dragging the thumb
			// we will post one of these messages to the queue again and
			// this ensures that there will be exactly one message queued up.
			handler.removeMessages(SHOW_PROGRESS);
		}

		public void onProgressChanged(SeekBar bar, int progress,
				boolean fromuser) {
			if (player == null) {
				return;
			}

			if (!fromuser) {
				// We're not interested in programmatically generated changes to
				// the progress bar's position.
				return;
			}

			long duration = player.getDuration();
			long newposition = (duration * progress) / 1000L;
			player.seekTo((int) newposition);
			if (currentTime != null)
				currentTime.setText(stringForTime((int) newposition));
		}

		public void onStopTrackingTouch(SeekBar bar) {
			dragging = false;
			setProgress();
			updatePausePlay();
			show(sDefaultTimeout);

			// Ensure that progress is properly updated in the future,
			// the call to show() does not guarantee this because it is a
			// no-op if we are already showing.
			handler.sendEmptyMessage(SHOW_PROGRESS);
		}
	};

	/**
	 * enable/disable view
	 */
	@Override
	public void setEnabled(boolean enabled) {
		if (pauseButton != null) {
			pauseButton.setEnabled(enabled);
		}
		if (nextButton != null) {
			nextButton.setEnabled(enabled);
		}
		if (prevButton != null) {
			prevButton.setEnabled(enabled);
		}
		if (progress != null) {
			progress.setEnabled(enabled);
		}
		super.setEnabled(enabled);
	}

	private static class MessageHandler extends Handler {
		private final WeakReference<AdultSwimPlayerController> mView;

		MessageHandler(AdultSwimPlayerController view) {
			mView = new WeakReference<AdultSwimPlayerController>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			AdultSwimPlayerController view = mView.get();
			if (view == null || view.player == null) {
				return;
			}

			int pos;
			switch (msg.what) {
			case FADE_OUT:
				view.hide();
				break;
			case SHOW_PROGRESS:
				pos = view.setProgress();
				if (!view.dragging && view.showing && view.player.isPlaying()) {
					msg = obtainMessage(SHOW_PROGRESS);
					sendMessageDelayed(msg, 1000 - (pos % 1000));
				}
				break;
			}
		}
	}

	/**
	 * Contoller to view/select bit rates for current playing media
	 * 
	 * @author rranawaka
	 */
	private class BitrateSelector {

		private View root;
		private View anchor;
		private PopupWindow popup;
		private boolean showing;
		private ViewGroup controllerLayout;

		private static final int TEXT_VIEW_ID_START = 26000;

		private BitrateSelector(View anchor, ViewGroup controllerLayout) {
			super();
			this.anchor = anchor;
			this.controllerLayout = controllerLayout;
			popup = new PopupWindow(context);
			popup.setOutsideTouchable(true);
			popup.setTouchable(true);
			popup.setBackgroundDrawable(new BitmapDrawable(context
					.getResources()));

			popup.setTouchInterceptor(new OnTouchListener() {

				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
						popup.dismiss();
						showing = false;
					}

					return false;
				}

			});
		}

		private void initControllerView() {
			LinearLayout.LayoutParams lp;
			LinearLayout rootLayout = new LinearLayout(context);
			lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			rootLayout.setLayoutParams(lp);
			rootLayout.setBackgroundColor(TRANSPARENT);
			rootLayout.setPadding(0, 0, 0, 0);
			rootLayout.setOrientation(LinearLayout.VERTICAL);

			TextView textView;

			mediaFiles = new ArrayList<IMediaFile>();
			mediaFiles.addAll(player.getAvailableMediaFiles());
			Collections.reverse(mediaFiles); // sort in decending order

			TableLayout tableLayout = new TableLayout(context);
			lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			tableLayout.setLayoutParams(lp);
			tableLayout.setBackgroundColor(TRANSPARENT);
			tableLayout.setPadding(0, 0, 0, 0);

			for (int i = 0; i < mediaFiles.size(); i++) {
				String bitrate = mediaFiles.get(i).getQualityDesc();

				if (bitrate != null && bitrate.length() > 0) {
					TableRow tableRow = new TableRow(context);
					tableRow.setBackgroundColor(TRANSPARENT);
					lp = new TableRow.LayoutParams(
							TableRow.LayoutParams.WRAP_CONTENT,
							TableRow.LayoutParams.WRAP_CONTENT);
					tableRow.setLayoutParams(lp);
					tableRow.setPadding(0, 0, 0, 0);
					final SpannableStringBuilder sb = new SpannableStringBuilder(
							bitrate + BITRATE_UNIT_SYMBOL);
					final ForegroundColorSpan fcs1 = new ForegroundColorSpan(
							Color.WHITE);
					final ForegroundColorSpan fcs2 = new ForegroundColorSpan(
							Color.WHITE);
					final AbsoluteSizeSpan acs = new AbsoluteSizeSpan(32);
					final StyleSpan scs = new StyleSpan(Typeface.BOLD);
					sb.setSpan(scs, 0, sb.length(),
							Spannable.SPAN_INCLUSIVE_INCLUSIVE);
					sb.setSpan(acs, 0, sb.length(),
							Spannable.SPAN_INCLUSIVE_INCLUSIVE);
					sb.setSpan(fcs1, 0, bitrate.length(),
							Spannable.SPAN_INCLUSIVE_INCLUSIVE);
					sb.setSpan(fcs2, bitrate.length(), sb.length(),
							Spannable.SPAN_INCLUSIVE_INCLUSIVE);
					textView = new TextView(context);
					textView.setBackgroundColor(color.player_bitrate_color);
					textView.setText(sb);
					textView.setId(TEXT_VIEW_ID_START + i);
					int paddingX = (int) (12 * screenDensity);
					int paddingY = (int) (4 * screenDensity);
					textView.setPadding(paddingX, paddingY, paddingX, paddingY);
					textView.setOnClickListener(textClickListener);
					textView.setOnTouchListener(textTouchListener);
					lp = new TableRow.LayoutParams(
							TableRow.LayoutParams.WRAP_CONTENT,
							TableRow.LayoutParams.WRAP_CONTENT);
					textView.setLayoutParams(lp);

					if (player.getCurrentMediaFile() == mediaFiles.get(i)) {
						textView.setBackgroundColor(Color.DKGRAY);
					}
					tableRow.addView(textView);
					lp = new TableLayout.LayoutParams(
							TableLayout.LayoutParams.WRAP_CONTENT,
							TableLayout.LayoutParams.WRAP_CONTENT);
					tableLayout.addView(tableRow, lp);
				} else {
					mediaFiles.remove(i);
					i--;
				}
			}

			rootLayout.addView(tableLayout);

			if (!mediaFiles.isEmpty()) {
				ImageView imageView = new ImageView(context);
				imageView.setImageResource(R.drawable.pointer_temp);
				lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				lp.gravity = Gravity.CENTER_HORIZONTAL;
				imageView.setLayoutParams(lp);
				root = rootLayout;
			}
		}

		private void show() {
			initControllerView();
			if (root != null) {
				Rect anchorLocation = locateView(anchor);
				Rect controllerLocation = locateView(controllerLayout);

				root.measure(View.MeasureSpec.UNSPECIFIED,
						View.MeasureSpec.UNSPECIFIED);

				int height = root.getMeasuredHeight();
				int width = root.getMeasuredWidth();
				int paddingBottom = (int) (2 * screenDensity);

				int x = ((anchorLocation.left + anchorLocation.right) / 2)
						- (width / 2);
				int y = controllerLocation.top - height - paddingBottom;
				popup.setContentView(root);
				popup.showAtLocation(anchor, Gravity.NO_GRAVITY, x, y);
				popup.update(x, y, width, height);
				showing = true;
			}
		}

		private void hide() {
			if (showing) {
				popup.dismiss();
				showing = false;
			}
		}

		private Rect locateView(View v) {
			int[] loc_int = new int[2];
			if (v == null)
				return null;
			try {
				v.getLocationOnScreen(loc_int);
			} catch (NullPointerException npe) {
				// Happens when the view doesn't exist on screen anymore.
				return null;
			}
			Rect location = new Rect();
			location.left = loc_int[0];
			location.top = loc_int[1];
			location.right = location.left + v.getWidth();
			location.bottom = location.top + v.getHeight();
			return location;
		}

		private OnClickListener textClickListener = new OnClickListener() {

			public void onClick(View v) {
				int id = v.getId();
				int index = id - TEXT_VIEW_ID_START;

				if (index >= 0 && index < mediaFiles.size()) {
					IMediaFile mediaFile = mediaFiles.get(index);
					player.selectMediaFile(mediaFile);
					hide();
				}

			}
		};

		private OnTouchListener textTouchListener = new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					v.setBackgroundColor(Color.DKGRAY);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					v.setBackgroundColor(Color.TRANSPARENT);
				}
				return false;
			}
		};

	}

}