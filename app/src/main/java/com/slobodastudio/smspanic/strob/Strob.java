package com.slobodastudio.smspanic.strob;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.slobodastudio.smspanic.BuildConfig;

public class Strob extends SurfaceView implements SurfaceHolder.Callback {

	private static final String TAG = Strob.class.getSimpleName();
	private SurfaceHolder m_Holder;
	private Camera m_Camera;
	private boolean isFlash = false, run = false;

	public Strob(Context context, AttributeSet attrs) {

		super(context, attrs);
		m_Holder = getHolder();
		m_Holder.addCallback(this);
		m_Holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
			isFlash = true;
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

	}

	public void flashSmall() {

		if (BuildConfig.DEBUG) {
			Log.d(TAG + ":flashSmall()", "isFlash  " + isFlash);
		}
		SurfaceHolder holder = super.getHolder();
		if (isFlash) {
			run = true;
			try {
				m_Camera = Camera.open();
			} catch (RuntimeException e) {
				Log.e(TAG, "Could not initialize the Camera", e);
				return;
			}
			Parameters p = m_Camera.getParameters();
			p.setFlashMode(Parameters.FLASH_MODE_TORCH);
			m_Camera.setParameters(p);
			try {
				m_Camera.setPreviewDisplay(holder);
			} catch (IOException e) {
				e.printStackTrace();
				m_Camera.release();
				m_Camera = null;
			}
			surfaceDestroyed(holder);
		}
	}

	public void flashLong() {

		if (BuildConfig.DEBUG) {
			Log.d(TAG + ":flashLong()", "isFlash  " + isFlash);
		}
		SurfaceHolder holder = super.getHolder();
		if (isFlash) {
			run = true;
			try {
				m_Camera = Camera.open();
				Log.v(TAG, "Camera open");
			} catch (RuntimeException e) {
				Log.e(TAG, "Could not initialize the Camera", e);
				return;
			}
			Parameters p = m_Camera.getParameters();
			p.setFlashMode(Parameters.FLASH_MODE_TORCH);
			m_Camera.setParameters(p);
			try {
				m_Camera.setPreviewDisplay(holder);
			} catch (IOException e) {
				e.printStackTrace();
				m_Camera.release();
				m_Camera = null;
			}
			try {
				TimeUnit.MILLISECONDS.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			surfaceDestroyed(holder);
		}
	}

	public void SOS() {

		Thread th = new Thread(new Runnable() {

			@Override
			public void run() {

				flashSmall();
				flashSmall();
				flashSmall();
				try {
					TimeUnit.MILLISECONDS.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				flashLong();
				flashLong();
				flashLong();
				try {
					TimeUnit.MILLISECONDS.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				flashSmall();
				flashSmall();
				flashSmall();
				return;
			}
		});
		th.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

		if (isFlash && run) {
			run = false;
			if (m_Camera != null) {
				m_Camera.release();
				m_Camera = null;
			}
			if (BuildConfig.DEBUG) {
				Log.v(TAG, "surfaceDestroyed");
			}
		}
	}
}
