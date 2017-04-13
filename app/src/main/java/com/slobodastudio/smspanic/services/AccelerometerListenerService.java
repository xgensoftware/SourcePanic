package com.slobodastudio.smspanic.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.CheckBox;

import com.slobodastudio.smspanic.R;
import com.slobodastudio.smspanic.activities.BlackActivity;
import com.slobodastudio.smspanic.activities.MessagesListActivity;
import com.slobodastudio.smspanic.broadcasts.SmsSenderBroadcast;

public class AccelerometerListenerService extends Service implements SensorEventListener {

	private static final String TAG = AccelerometerListenerService.class.getSimpleName();
	private SensorManager mSensorManager;
	private Sensor mAccelerometerSensor;
	private double gravity_const = 9.8;
	private static int idPanic = 0;
	private ResultReceiver receiver;
	private PowerManager mPwrMgr;
	private Handler handler = new Handler();
	private static WakeLock mTurnBackOn = null;

	public static WakeLock getmTurnBackOn() {

		return mTurnBackOn;
	}

	public static void setmTurnBackOn(WakeLock mTurnBackOn) {

		AccelerometerListenerService.mTurnBackOn = mTurnBackOn;
	}

	private WakeLock mWakeLock = null;
	public static final String KEY_TO_ACTIVITY = "finish_ativity", KEY_ID_MESSAGE = "id_message";
	private static CheckBox activeItem;

	public static CheckBox getActiveItem() {

		return activeItem;
	}

	public static void setActiveItem(CheckBox activeItem) {

		AccelerometerListenerService.activeItem = activeItem;
	}

	public static int getIdPanic() {

		return idPanic;
	}

	public static void setIdPanic(int idPanic) {

		AccelerometerListenerService.idPanic = idPanic;
	}

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {

		Log.v(TAG, "start service");
		startTimer();
		idPanic = intent.getExtras().getInt(MessagesListActivity.ID_PANIC_TO_ACCELEROMETER);
		receiver = intent.getExtras().getParcelable(MessagesListActivity.RECEIVER_TO_ACCELEROMETER);
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
		if (sensors.size() > 0) {
			for (Sensor sensor : sensors) {
				if (Sensor.TYPE_ACCELEROMETER == sensor.getType()) {
					mAccelerometerSensor = sensor;
					break;
				}
			}
		}
		mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
		mPwrMgr = (PowerManager) this.getSystemService(POWER_SERVICE);
		mWakeLock = mPwrMgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Accel");
		mWakeLock.acquire();
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		registerReceiver(ScreenReceiver, intentFilter);
		KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
		KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
		lock.disableKeyguard();
		super.onStart(intent, startId);
	}

	public BroadcastReceiver ScreenReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			Log.v(TAG, "onReceive!");
			if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
				Log.v(TAG, "screen off!");
				handler.post(new Runnable() {

					@Override
					public void run() {

						try {
							mTurnBackOn.release();
						} catch (Exception e) {
							e.printStackTrace();
						}
						mTurnBackOn = mPwrMgr.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
								| PowerManager.ACQUIRE_CAUSES_WAKEUP, "AccelOn");
						mTurnBackOn.acquire();
						Intent black = new Intent(AccelerometerListenerService.this, BlackActivity.class);
						black.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						black.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
						startActivity(black);
					}
				});
			}
		}
	};

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		float x = event.values[SensorManager.DATA_X];
		float y = event.values[SensorManager.DATA_Y];
		float z = event.values[SensorManager.DATA_Z];
		double rez = new BigDecimal(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2))
				- gravity_const).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		int sensitivity = Integer.parseInt(settings
				.getString(getString(R.string.pref_accelerometer_key), "1"));
		if (rez > ((11 - sensitivity) * 2)) {
			sendMessage();
			stopSelf();
			Log.v(TAG, "rez = " + rez);
		}
	}

	@Override
	public void onDestroy() {

		Log.v(TAG, "onDestroy");
		mSensorManager.unregisterListener(this);
		idPanic = 0;
		receiver.send(MessagesListActivity.UNCHECK_ITEM, null);
		try {
			mTurnBackOn.release();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Intent black = new Intent(AccelerometerListenerService.this, BlackActivity.class);
		black.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		black.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		black.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
		black.putExtra(KEY_TO_ACTIVITY, true);
		startActivity(black);
		KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
		KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
		lock.reenableKeyguard();
		super.onDestroy();
	}

	private void startTimer() {

		Log.v(TAG, "start timer!");
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		long time = Integer.parseInt(settings.getString(getString(R.string.pref_shake_key), "1")) * 60000;
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {

			@Override
			public void run() {

				setIdPanic(0);
				stopSelf();
			}
		};
		timer.schedule(task, time);
	}

	private void sendMessage() {

		Log.v(TAG, "send message to broadcast");
		Intent intent = new Intent();
		intent.setAction(SmsSenderBroadcast.ACTION);
		intent.putExtra(ListenerService.SERVICE_MODE_TRANSFER_KEY, ListenerService.MEDIA_CAPTURE_MODE);
		intent.putExtra(SmsSenderBroadcast.EXTRA_MESSAGE_VALUE_ID, idPanic);
		sendBroadcast(intent);
	}
}
