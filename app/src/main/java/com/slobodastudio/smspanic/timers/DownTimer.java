package com.slobodastudio.smspanic.timers;

import java.util.Timer;

import android.os.Handler;
import android.util.Log;

public class DownTimer {

	private Handler mHandler;
	private Timer mTimer;
	private long mDelay;
	private boolean isRun = false;
	public static final String DEBUG_TAG = DownTimer.class.getSimpleName();

	public DownTimer(Handler handler) {

		mHandler = handler;
	}

	public void startFor(long delay) {

		if (!isRun) {
			isRun = true;
			mTimer = new Timer();
			InitializatorTimerTask countdownTimerTask = new InitializatorTimerTask(mHandler);
			mTimer.schedule(countdownTimerTask, delay);
		}
	}

	public boolean isRunning() {

		return isRun;
	}

	public void start() {

		mTimer = new Timer();
		InitializatorTimerTask countdownTimerTask = new InitializatorTimerTask(mHandler);
		mTimer.schedule(countdownTimerTask, 1000, mDelay);
		isRun = true;
		Log.v(DEBUG_TAG, mHandler.getClass().getSimpleName() + " " + "started");
	}

	public void stop() {

		if (isRun) {
			mTimer.cancel();
			mTimer = null;
			Log.v(DEBUG_TAG, mHandler.getClass().getSimpleName() + " " + "stopped");
		}
		isRun = false;
	}

	public void setDelay(long delay) {

		mDelay = delay;
	}
}
