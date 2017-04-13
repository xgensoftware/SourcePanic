package com.slobodastudio.smspanic.services;

import android.util.Log;

public class UnsupportedSensorException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String TAG = UnsupportedSensorException.class.getSimpleName();

	public UnsupportedSensorException() {

		Log.e(TAG, "Sensor is unavailable");
	}
}
