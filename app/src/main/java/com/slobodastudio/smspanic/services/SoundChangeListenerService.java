package com.slobodastudio.smspanic.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

import com.slobodastudio.smspanic.BuildConfig;
import com.slobodastudio.smspanic.broadcasts.SmsSenderBroadcast;

public class SoundChangeListenerService extends Service {

	private static final String TAG = SoundChangeListenerService.class.getSimpleName();
	PowerManager.WakeLock wl;
	PowerManager pm;
	boolean wakeUpFlag = false;
	public int Presses = 0;
	private TelephonyManager mTelephonyManager;
	private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {

			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
				case TelephonyManager.CALL_STATE_OFFHOOK:
					break;
				case TelephonyManager.CALL_STATE_RINGING:
					break;
				case TelephonyManager.CALL_STATE_IDLE:
					break;
			}
		}
	};
	public BroadcastReceiver MediaButton_Receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			KeyEvent event = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
			if (event == null) {
				return;
			}
			int action = event.getAction();
			if (action == KeyEvent.KEYCODE_VOLUME_DOWN) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "!!!!!! VOLUME_DOWN !!!!!!");
				}
			}
		};
	};

	@Override
	public IBinder onBind(Intent arg0) {

		return null;
	}

	private ButtonsPressed rec;

	@Override
	public void onCreate() {

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "onCreate called!!");
		}
		super.onCreate();
		mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
		rec = new ButtonsPressed();
		registerReceiver(rec, new IntentFilter("android.media.VOLUME_CHANGED_ACTION"));
		// KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
		// KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
		// lock.disableKeyguard();
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
		mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
		if (rec != null) {
			unregisterReceiver(rec);
		}
		wl.release();
		// KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
		// KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
		// lock.reenableKeyguard();
	}

	@Override
	public void onStart(Intent intent, int startId) {

		super.onStart(intent, startId);
		// wl.acquire();
	}

	private class ButtonsPressed extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "onReceive called!!");
			}
			sendMessage();
			// Intent intent2 = new Intent(context, SosButtonActivity.class);
			// intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// context.startActivity(intent2);
		}
	}

	private void sendMessage() {

		Log.v(TAG, "send message to broadcast");
		Intent intent = new Intent();
		intent.setAction(SmsSenderBroadcast.ACTION);
		intent.putExtra(ListenerService.SERVICE_MODE_TRANSFER_KEY, ListenerService.MEDIA_CAPTURE_MODE);
		intent.putExtra(SmsSenderBroadcast.EXTRA_MESSAGE_VALUE_ID, 1);
		sendBroadcast(intent);
	}
}
