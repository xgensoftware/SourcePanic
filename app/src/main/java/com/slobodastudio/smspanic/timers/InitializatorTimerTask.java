package com.slobodastudio.smspanic.timers;

import android.os.Handler;

import java.util.TimerTask;

public class InitializatorTimerTask extends TimerTask {

	private final Handler mHandler;

	public InitializatorTimerTask(Handler handler) {

		mHandler = handler;
	}

	@Override
	public void run() {

		mHandler.sendEmptyMessage(0);
	}
}