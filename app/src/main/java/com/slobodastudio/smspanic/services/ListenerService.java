package com.slobodastudio.smspanic.services;

import java.util.Observable;
import java.util.Observer;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.slobodastudio.smspanic.MessageValue;
import com.slobodastudio.smspanic.activities.MediaCaptureActivity;
import com.slobodastudio.smspanic.broadcasts.SmsSenderBroadcast;
import com.slobodastudio.smspanic.sensors.SensorProvider;
import com.slobodastudio.smspanic.timers.DownTimer;

public class ListenerService extends Service implements Observer {

	private static final String TAG = ListenerService.class.getSimpleName();
	private SensorProvider accelerometerProvider;
	private SensorProvider gyroscopeProvider;
	private DownTimer accelerometerTimer;
	private DownTimer gyroscopeTimer;
	private int accelerometerOnes = 0;
	public static final String SERVICE_MODE_TRANSFER_KEY = "sensorModeTransferKey";
	public static final int SENSOR_LISTENER_MODE = 101;
	public static final int MEDIA_CAPTURE_MODE = 100;
	private int launchMode;
	private int idPanic;

	@Override
	public IBinder onBind(Intent arg0) {

		return null;
	}

	@Override
	public void onCreate() {

		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {

		super.onStart(intent, startId);
		Log.v(TAG, "service started");
		launchMode = intent.getIntExtra(SERVICE_MODE_TRANSFER_KEY, 0);
		if (launchMode == 0) {
			onDestroy();
		}
		switch (launchMode) {
			case 0:
				onDestroy();
				break;
			case SENSOR_LISTENER_MODE:
				startSensorService();
				break;
			case MEDIA_CAPTURE_MODE:
				startMediaCaptureService(intent);
				break;
		}
	}

	private void startMediaCaptureService(Intent intent) {

		MessageValue mv = (MessageValue) intent
				.getSerializableExtra(SmsSenderBroadcast.EXTRA_MESSAGE_VALUE_ID);
		if (mv == null) {
			onDestroy();
		}
		idPanic = mv.getId();
		String emails = null;
		if (mv.isEmailEnable()) {
			emails = mv.getEmails();
		}
		int recorderId = mv.getMediaRecorder();
		int videoRecordType = mv.getRecordingType();
		Log.v(TAG, recorderId + " recorder id");
		switch (recorderId) {
			case MessageValue.MEDIA_RECORDER_CAMERA:
				startRecord(MediaCaptureActivity.MODE_VIDEO_RECORDING, mv.getMediaSendFrequency() * 1000, mv
						.getMediaSendOnes(), emails, videoRecordType);
				break;
			case MessageValue.MEDIA_RECORDER_MIC:
				startRecord(MediaCaptureActivity.MODE_VOICE_RECORDING, mv.getMediaSendFrequency() * 1000, mv
						.getMediaSendOnes(), emails, videoRecordType);
				break;
		}
	}

	private void startRecord(int captureMode, int duration, int ones, String emails, int videoType) {

		Intent captActivity = new Intent(getApplicationContext(), MediaCaptureActivity.class);
		captActivity.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_TASK);
		// captActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		captActivity.putExtra(MediaCaptureActivity.RECORD_VIDEO_TYPE, videoType);
		captActivity.putExtra(MediaCaptureActivity.MODE_TRANSFER_KEY, captureMode);
		captActivity.putExtra(MediaCaptureActivity.ONES_TRANSFER_KEY, ones);
		captActivity.putExtra(MediaCaptureActivity.FILE_SIZE_TRANSFER_KEY, (long) 30000);
		captActivity.putExtra(MediaCaptureActivity.DURATION_TRANSFER_KEY, duration);
		captActivity.putExtra(MediaCaptureActivity.EMAIL_TRANSFER_KEY, emails);
		captActivity.putExtra(SmsSenderBroadcast.EXTRA_MESSAGE_VALUE_ID, idPanic);
		startActivity(captActivity);
		stopSelf();
		Log.v(TAG, "service was started " + ones + " <-ones and duration ->" + duration);
	}

	private void startSensorService() {

		accelerometerProvider = new SensorProvider(this, SensorManager.SENSOR_DELAY_FASTEST);
		gyroscopeProvider = new SensorProvider(this, SensorManager.SENSOR_DELAY_UI);
		accelerometerProvider.addObserver(this);
		try {
			accelerometerProvider.start(Sensor.TYPE_ACCELEROMETER);
			// gyroscopeProvider.start(Sensor.TYPE_GYROSCOPE);
		} catch (UnsupportedSensorException e) {
			// TODO: send notification for user
			e.printStackTrace();
		}
		// accelerometerTimer = new DownTimer(new AccelerometerHandler());
		// gyroscopeTimer = new DownTimer(new GyroscopeHandler());
	}

	@Override
	public void update(Observable observable, Object data) {

		SensorEvent event = (SensorEvent) data;
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			handleAccelerometerData(event);
			Log.v(TAG, "accelerometer updated");
		} else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
			handleGyroscopeData(event);
			Log.v(TAG, "gyroscope updated");
		}
	}

	private void handleAccelerometerData(SensorEvent event) {

		if (accelerometerTimer != null) {
			if (accelerometerTimer.isRunning()) {
				accelerometerOnes++;
				return;
			} else {
				accelerometerTimer.startFor(3000);
				accelerometerOnes++;
			}
		} else {
			accelerometerTimer = new DownTimer(new AccelerometerHandler());
			accelerometerTimer.startFor(3000);
			accelerometerOnes++;
		}
	}

	private void handleGyroscopeData(SensorEvent event) {

		if (gyroscopeTimer != null) {
			if (gyroscopeTimer.isRunning()) {
				return;
			} else {
				gyroscopeTimer.startFor(3000);
			}
		} else {
			gyroscopeTimer = new DownTimer(new AccelerometerHandler());
			gyroscopeTimer.startFor(3000);
		}
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
		Log.v(TAG, "service destroyed");
		if (launchMode == SENSOR_LISTENER_MODE) {
			accelerometerProvider.deleteObserver(this);
			accelerometerProvider.stop();
			gyroscopeProvider.deleteObserver(this);
			gyroscopeProvider.stop();
		}
	}

	class AccelerometerHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			Toast.makeText(ListenerService.this, "accel ones " + accelerometerOnes, Toast.LENGTH_SHORT)
					.show();
			accelerometerOnes = 0;
			accelerometerTimer.stop();
			accelerometerTimer = null;
		}
	}

	class GyroscopeHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			Toast.makeText(ListenerService.this, "accel ones " + accelerometerOnes, Toast.LENGTH_SHORT)
					.show();
			gyroscopeTimer.stop();
			gyroscopeTimer = null;
		}
	}
}
