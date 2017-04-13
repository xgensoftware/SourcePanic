package com.slobodastudio.smspanic.sensors;

import java.io.PrintWriter;
import java.util.Observable;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.slobodastudio.smspanic.services.UnsupportedSensorException;
import com.slobodastudio.smspanic.timers.DownTimer;

/** Class that provides values from selected sensor. For use this you must implement Observer
 * 
 * @author iridium */
public class SensorProvider extends Observable implements SensorEventListener {

	private static final String TAG = SensorProvider.class.getSimpleName();
	private SensorManager sensorManager;
	private Context mContext;
	private Sensor mSensor = null;
	private int updateSensorRate = -1;
	private boolean run = false;
	private DownTimer accelerometerTimer;
	private PrintWriter fileX;
	private PrintWriter fileY;
	private PrintWriter fileZ;

	/** Create instance of SensorProvider, for getting values from sensors Must implements Observer
	 * 
	 * @param context
	 * @param typeOfSensor
	 *            - use static field of class Sensor
	 * @param updateRate
	 *            - use static field of class SensorManager */
	public SensorProvider(Context context, int updateRate) {

		mContext = context;
		updateSensorRate = updateRate;
		sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
	}

	/** Start monitoring sensor values. Result will send to your update(Object obj) method
	 * 
	 * @param typeOfSensor
	 * @return - true if has observers and parameters are valid, false otherwise
	 * @throws UnsupportedSensorException */
	public boolean start(int typeOfSensor) throws UnsupportedSensorException {

		mSensor = sensorManager.getDefaultSensor(typeOfSensor);
		Log.v(TAG, mSensor.getPower() + " power");
		sensorManager.registerListener(this, mSensor, updateSensorRate);
		run = true;
		return true;
	}

	/** stop monitoring sensor */
	public void stop() {

		if (run) {
			sensorManager.unregisterListener(this);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {

	}

	public static volatile float kFilteringFactor = (float) 0.05;
	long shakeTime = -1;
	float rollingZ;
	float rollingY;
	float rollingX;

	@Override
	public void onSensorChanged(SensorEvent event) {

	}

	class AccelerometerHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			sensorManager.unregisterListener(SensorProvider.this);
			accelerometerTimer.stop();
			accelerometerTimer = null;
			fileX.close();
			fileY.close();
			fileZ.close();
		}
	}
}
