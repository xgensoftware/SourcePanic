package com.slobodastudio.smspanic.activities;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.CameraProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.slobodastudio.smspanic.R;
import com.slobodastudio.smspanic.SmsPanicApplication;
import com.slobodastudio.smspanic.broadcasts.SmsSenderBroadcast;
import com.slobodastudio.smspanic.dao.EmailMessageRecord;
import com.slobodastudio.smspanic.media.EmailSender;
import com.slobodastudio.smspanic.utils.ComponentsUtils;
import com.slobodastudio.smspanic.utils.DownloadThreadManager;
import com.slobodastudio.smspanic.utils.PreferenceUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class MediaCaptureActivity extends Activity implements OnInfoListener,
		SurfaceHolder.Callback, OnErrorListener {

	interface ViewIds {

		public static final int CENTER_TEXT_VIEW_ID = 999;
		public static final int ACTIVITY_HEADER_TEXT_ID = 100;
		public static final int PROGRESS_BAR_ID = 101;
		public static final int VIDEO_VIEW_ID = 102;
		public static final int VOICE_RECORD_TEXT_ID = 103;
		public static final int VIDEO_RECORD_TEXT_ID = 104;
		public static final int VOICE_VIEW_ID = 105;
	}

	public static final String TAG = MediaCaptureActivity.class.getSimpleName();
	private static final String FILE_NAME_HEADER = Environment
			.getExternalStorageDirectory() + "/";
	public static final String MODE_TRANSFER_KEY = "modeTransferKey";
	public static final String FILE_NAME_TRANSFER_KEY = "fileNameTransferKey";
	public static final String FILE_SIZE_TRANSFER_KEY = "fileSizeTransferKey";
	public static final String DURATION_TRANSFER_KEY = "durationTransferKey";
	public static final String EMAIL_TRANSFER_KEY = "emailTransferKey";
	public static final String ONES_TRANSFER_KEY = "onesTransferKey";
	public static final String FILES_DIR = "sms_panic_dir/";
	public static final String RECORD_VIDEO_TYPE = "recordVideoType";
	public static final int MODE_VOICE_RECORDING = 200;
	public static final int MODE_VIDEO_RECORDING = 201;
	private static final String HEADER_TEXT = "recording";
	private static final String VOICE_RECORD_TEXT = "voice";
	private static final String VIDEO_RECORD_TEXT = "video";
	public static final int ORIENTATION_PORTRAIT = 1;
	public static final int ORIENTATION_UNSCPECIFIED = -1;
	private static final String VOICE_CENTER_TEXT = "no image";
	private static final int NOTIFICATION_ID = 1;
	private MediaRecorder mRecorder = null;
	private RelativeLayout rootRelLayout;
	private TextView activityHeaderText;
	private ProgressBar mProgressBar;
	private VideoView mVideoView;
	private TextView voiceRecordHint;
	private TextView videoRecordHint;
	private TextView voiceRecordView;
	private SurfaceHolder mHolder = null;
	private Camera mCamera = null;
	private String outputFileName;
	private int requestedMode = 0;
	private int duration = 0;
	private int ones = 0;
	private int messageId;
	private final HashSet<String> fileNames = new HashSet<String>();
	private String emails = null;
	private int currentCameraId;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		initializeViews();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(rootRelLayout);
		ComponentsUtils.setViewsSettings(rootRelLayout);
		Intent requestIntent = getIntent();
		if (requestIntent != null) {
			requestedMode = requestIntent.getIntExtra(MODE_TRANSFER_KEY, 0);
			duration = requestIntent.getIntExtra(DURATION_TRANSFER_KEY, 0);
			ones = requestIntent.getIntExtra(ONES_TRANSFER_KEY, 0);
			emails = requestIntent.getStringExtra(EMAIL_TRANSFER_KEY);
			messageId = requestIntent.getIntExtra(
					SmsSenderBroadcast.EXTRA_MESSAGE_VALUE_ID, 0);
			Log.v(TAG, ones + " =ones " + duration + " =duration");
			switch (requestedMode) {
			case MODE_VOICE_RECORDING:
				startVoiceRecording();
				mVideoView.setVisibility(View.GONE);
				voiceRecordView.setVisibility(View.VISIBLE);
				break;
			case MODE_VIDEO_RECORDING:
				int recordType = requestIntent
						.getIntExtra(RECORD_VIDEO_TYPE, 0);
				if (recordType == new Integer(0)) {
					// notification area video mode
					RelativeLayout.LayoutParams notificationLayout = new RelativeLayout.LayoutParams(
							1, 1);
					notificationLayout.addRule(RelativeLayout.ALIGN_LEFT,
							RelativeLayout.TRUE);
					notificationLayout.addRule(RelativeLayout.ALIGN_TOP,
							RelativeLayout.TRUE);
					mVideoView.setLayoutParams(notificationLayout);
					mVideoView.invalidate();
				} else {
					// full screen mode
					RelativeLayout.LayoutParams fullScreenLayout = new RelativeLayout.LayoutParams(
							new LayoutParams(
									android.view.ViewGroup.LayoutParams.FILL_PARENT,
									android.view.ViewGroup.LayoutParams.FILL_PARENT));
					mVideoView.setLayoutParams(fullScreenLayout);
					mVideoView.invalidate();
				}
				mVideoView.setVisibility(View.VISIBLE);
				voiceRecordView.setVisibility(View.GONE);
				initCamera();
				break;
			}
		} else {
			Log.v(TAG, "finish()");
		}
		super.onCreate(savedInstanceState);
		AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mgr.setStreamMute(AudioManager.STREAM_SYSTEM, true);
		statusBarOn();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
	}

	private void statusBarOn() {

		Notification notification = new Notification(R.drawable.ic_widget,
				getString(R.string.statuBarRecord), System.currentTimeMillis());
		PendingIntent pendingIntent = PendingIntent.getActivity(this,
				NOTIFICATION_ID, new Intent(), PendingIntent.FLAG_ONE_SHOT);
		notification.setLatestEventInfo(this,
				getString(R.string.statuBarRecord), "", pendingIntent);
		NotificationManager notificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(NOTIFICATION_ID, notification);
	}

	private void statusBarOff() {

		NotificationManager notificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(NOTIFICATION_ID);
	}

	@Override
	public void onResume() {

		super.onResume();
		if (requestedMode == MODE_VIDEO_RECORDING) {
			try {
				mCamera.reconnect();
			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void initializeViews() {

		getWindow().getAttributes().alpha = 0;
		rootRelLayout = new RelativeLayout(this);
		rootRelLayout.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT));
		rootRelLayout.setGravity(Gravity.TOP);
		mVideoView = new VideoView(this);
		mVideoView.setId(ViewIds.VIDEO_VIEW_ID);
		mVideoView.setPadding(10, 10, 10, 10);
		mVideoView.setVisibility(View.VISIBLE);
		voiceRecordView = new TextView(this);
		voiceRecordView.setId(ViewIds.VOICE_VIEW_ID);
		voiceRecordView.setPadding(10, 10, 10, 10);
		voiceRecordView.setTextSize(50);
		voiceRecordView.setText(VOICE_CENTER_TEXT);
		voiceRecordView.setGravity(Gravity.CENTER_HORIZONTAL);
		voiceRecordView.setVisibility(View.GONE);
		RelativeLayout.LayoutParams mVideoViewLayoutParams = new RelativeLayout.LayoutParams(
				1, 1);
		mVideoViewLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		mVideoViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		rootRelLayout.addView(mVideoView);
		rootRelLayout.addView(voiceRecordView);
	}

	private void startVoiceRecording() {

		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
		outputFileName = createFilesDir(ones) + "_" + getDataTime() + ".amr";
		mRecorder.setOutputFile(outputFileName);
		fileNames.add(outputFileName);
		mRecorder.setMaxDuration(duration);
		mRecorder.setOnInfoListener(this);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		try {
			mRecorder.prepare();
			Log.v(TAG, "voice record start");
		} catch (IOException e) {
			Log.e(TAG, "prepare() failed");
			e.printStackTrace();
		}
		mRecorder.start();
	}

	private void stopVoiceRecording() {

		mRecorder.stop();
		mRecorder.reset();
	}

	@Override
	public void onInfo(MediaRecorder mr, int what, int extra) {

		switch (what) {
		case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
			ones--;
			if (ones > 0) {
				Log.v(TAG, "reinitializing");
				switch (requestedMode) {
				case MODE_VIDEO_RECORDING:
					reinitializeVideoRecorder();
					break;
				case MODE_VOICE_RECORDING:
					reinitializeVoiceRecorder();
				}
			} else {
				finish();
				Log.e(TAG, "record stopped normal");
			}
			break;
		case MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED:
			Log.e(TAG, "onInfo invoked "
					+ MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED);
			break;

		case MediaRecorder.MEDIA_RECORDER_INFO_UNKNOWN:
			Log.e(TAG, "onInfo invoked "
					+ MediaRecorder.MEDIA_RECORDER_INFO_UNKNOWN);
			break;

		}
		Log.e(TAG, "onInfo invoked");
	}

	private void reinitializeVoiceRecorder() {

		stopVoiceRecording();
		mRecorder = null;
		startVoiceRecording();
	}

	private void reinitializeVideoRecorder() {

		if (mRecorder != null) {
			mRecorder.stop();
			mRecorder.reset();
			mRecorder.release();
			mRecorder = null;
		}
		if (Build.VERSION.SDK_INT > 10) {
			mCamera.stopPreview();
			mCamera.unlock();
		}
		initRecorder();
		mRecorder.setOnInfoListener(this);
		mRecorder.setOnErrorListener(this);
		try {
			mRecorder.start();
		} catch (IllegalStateException e) {
			Log.e(TAG, "error to start record", e);
		}
	}

	@Override
	public void onError(MediaRecorder arg0, int arg1, int arg2) {

		Log.v(TAG, "error occurred");
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

		Log.v(TAG, "surface changed");
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		switch (requestedMode) {
		case MODE_VIDEO_RECORDING:
			try {
				mCamera.setPreviewDisplay(mHolder);
				mCamera.startPreview();
				Log.e(TAG, "surface created");
			} catch (IOException e) {
				Log.v(TAG, "Could not start the preview");
				e.printStackTrace();
			}
			mCamera.stopPreview();
			mCamera.unlock();
			initRecorder();
			startVideoRecording();
			break;
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

		Log.v(TAG, "surface destroyed");
	}

	private boolean initCamera() {

		try {
			Log.v(TAG, "init camera");
			int numberOfCameras = Camera.getNumberOfCameras();

			mCamera = Camera.open();
			if (mCamera == null)
				for (int i = 0; i < numberOfCameras; i++) {
					mCamera = Camera.open(i);
					if (mCamera != null) {
						currentCameraId = i;
						break;
					}
				}

			Camera.Parameters params = mCamera.getParameters();
			mCamera.setParameters(params);
			Camera.Parameters p = mCamera.getParameters();
			Size mPreviewSize;
			final List<Size> listSize = p.getSupportedPreviewSizes();
			String angle = PreferenceUtil.getString(getApplicationContext(),
					"pref_video_angle_key", "0");
			int ang = Integer.parseInt(angle);

			if ("ME371MG".equals(Build.DEVICE) && "asus".equals(Build.BRAND)) {
				mPreviewSize = listSize.get(1);
				ang = 180;
			} else {
				mPreviewSize = listSize.get(0);
			}

			Log.v(TAG, "use: width = " + mPreviewSize.width + " height = "
					+ mPreviewSize.height);
			p.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
			p.setPreviewFormat(PixelFormat.YCbCr_420_SP);

			mCamera.setParameters(p);
			mCamera.setDisplayOrientation(ang);
			mCamera.lock();
			mHolder = mVideoView.getHolder();
			mHolder.addCallback(this);
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		} catch (RuntimeException re) {
			Log.e(TAG, "Could not initialize the Camera", re);
			return false;
		}
		return true;
	}

	@SuppressWarnings("unused")
	private void releaseRecorder() {

		if (mRecorder != null) {
			mRecorder.release();
			mRecorder = null;
		}
	}

	@Override
	protected void onPause() {

		super.onPause();
	}

	@SuppressWarnings("unused")
	private void releaseCamera() {

		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera = null;
		}
	}

	private void initRecorder() {
		setRequestedOrientation(ORIENTATION_PORTRAIT);
		if (mRecorder != null) {
			mRecorder.stop();
			mRecorder.reset();
			mRecorder.release();
			mRecorder = null;
		}
		outputFileName = createFilesDir(ones) + "_" + getDataTime() + ".mp4";
		fileNames.add(outputFileName);
		File outFile = new File(outputFileName);
		if (outFile.exists()) {
			outFile.delete();
		}
		try {
			mRecorder = new MediaRecorder();
			mRecorder.setCamera(mCamera);

			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

			int angle = Integer.parseInt(PreferenceUtil.getString(
					getApplicationContext(), "pref_video_angle_key", "0"));

			if ("ME371MG".equals(Build.DEVICE) && "asus".equals(Build.BRAND)) {
				angle = 180;
			}

			mRecorder.setOrientationHint(angle);

			setCamcorderQuality();

			if (duration != 0) {
				mRecorder.setMaxDuration(duration);
			}
			mRecorder.setPreviewDisplay(mHolder.getSurface());
			mRecorder.setOutputFile(outputFileName);
			mRecorder.prepare();
			Log.v(TAG, "MediaRecorder initialized");
		} catch (Exception e) {
			try {
				Log.e(TAG, "MediaRecorder initialized", e);
				// mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H263);
				mRecorder.prepare();
			} catch (Exception e1) {
				Log.e(TAG, "MediaRecorder failed to initialize");
				e.printStackTrace();
			}
		}
	}

	private void setCamcorderQuality() {
		CamcorderProfile hQuality = CamcorderProfile.get(currentCameraId,
				CamcorderProfile.QUALITY_HIGH);
		CamcorderProfile lQuality = CamcorderProfile.get(currentCameraId,
				CamcorderProfile.QUALITY_LOW);

		int quality = Integer.parseInt(PreferenceUtil.getString(
				getApplicationContext(), "pref_video_quality_key"));
		switch (quality) {
		case 1:
			mRecorder.setOutputFormat(hQuality.fileFormat);
			mRecorder.setAudioEncoder(lQuality.audioCodec);
			mRecorder.setVideoEncoder(lQuality.videoCodec);
			mRecorder.setVideoSize(hQuality.videoFrameWidth,
					hQuality.videoFrameHeight);
			mRecorder.setVideoEncodingBitRate(lQuality.videoBitRate);
			mRecorder.setVideoFrameRate(5);
			break;
		case 2:
			mRecorder.setProfile(hQuality);
			break;
		default:
			mRecorder.setProfile(lQuality);
			break;
		}

	}

	private void startVideoRecording() {

		setRequestedOrientation(ORIENTATION_PORTRAIT);
		mRecorder.setOnInfoListener(this);
		mRecorder.setOnErrorListener(this);
		try {
			mRecorder.start();
		} catch (RuntimeException e) {
			Toast.makeText(getApplicationContext(), "Camera error",
					Toast.LENGTH_LONG).show();
			// e.printStackTrace();
			setResult(1);
			finish();

		}
	}

	private void stopVideoRecording() {

		// setRequestedOrientation(ORIENTATION_UNSCPECIFIED);
		if (mRecorder != null) {
			mRecorder.setOnErrorListener(this);
			mRecorder.setOnInfoListener(this);
			try {
				mRecorder.stop();
			} catch (IllegalStateException e) {
				Log.e(TAG,
						"Got IllegalStateException in stopRecording. This can happen if the recorder has already stopped.");
			}
		}
		setRequestedOrientation(ORIENTATION_UNSCPECIFIED);
	}

	@Override
	public void onDestroy() {

		// sendFiles();
		super.onDestroy();
		Log.v(TAG, "onDestroy invoked");
		switch (requestedMode) {
		case MODE_VOICE_RECORDING:
			stopVoiceRecording();
			// releaseRecorder();
			// releaseCamera();
			break;
		case MODE_VIDEO_RECORDING:
			stopVideoRecording();
			try {
				mCamera.release();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		// Intent brIntent = new Intent(this, DoEnableStrobBroadcast.class);
		// brIntent.putExtra(SmsSenderBroadcast.EXTRA_MESSAGE_VALUE_ID,
		// messageId);
		// this.sendBroadcast(brIntent);
		Log.v(TAG, "onDestroy invoked send broadkast " + messageId);
		AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mgr.setStreamMute(AudioManager.STREAM_SYSTEM, false);
		statusBarOff();
	}

	private void sendFiles() {

		if (emails == null) {
			Log.d(TAG, "emails are empty or null");
			return;
		}
		Log.e(TAG, fileNames.size() + " size of file list");
		if (emails.length() != 0) {
			Iterator<String> iter = fileNames.iterator();
			while (iter.hasNext()) {
				Log.v(TAG, iter.next());
			}
			/*
			 * for (String fm : fileNames) { EmailMessageRecord mr = new
			 * EmailMessageRecord(); mr.setAdresses(emails.toString());
			 * mr.setAttachment(fm); mr.setBody("BODY");
			 * mr.setSubject("sms-panic"); mr.setTime(new Date());
			 * SmsPanicApplication a = (SmsPanicApplication)getApplication();
			 * a.addEmail(mr); }
			 */

			EmailSender task = new EmailSender(getApplicationContext(),
					fileNames, emails);
			DownloadThreadManager worker = DownloadThreadManager.getInstance();
			worker.sendDownloadTask(task);

		}
		// finish();
	}

	@Override
	protected void onStop() {

		super.onStop();
		Log.v(TAG, "onstop");
		sendFiles();
	}

	private String createFilesDir(int ones) {

		String dirName = FILE_NAME_HEADER + FILES_DIR;
		File dir = new File(dirName);
		dir.mkdirs();
		return dirName + String.valueOf(ones);
	}

	private String getDataTime() {

		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");//
		long time = System.currentTimeMillis();// -
												// Calendar.getInstance().getTimeZone().getRawOffset();
		String dateTimeText = date.format(time);
		return dateTimeText;
	}
}