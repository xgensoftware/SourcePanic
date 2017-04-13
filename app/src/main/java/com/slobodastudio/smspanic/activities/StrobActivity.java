/** The StrobActivity.java...
 * 
 * Made by sloboda-studio.com in 2012
 * 
 * @author Alexandr Sergienko */
package com.slobodastudio.smspanic.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.slobodastudio.smspanic.BuildConfig;
import com.slobodastudio.smspanic.R;
import com.slobodastudio.smspanic.strob.Strob;

/** @author AlexandrSergienko */
public class StrobActivity extends Activity {

	private static final String TAG = StrobActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Activity Created!!");
		}
		setContentView(R.layout.ac_strob);
	}

	@Override
	public void onResume() {

		super.onResume();
		Strob strob = (Strob) findViewById(R.id.surfPreviewStrob);
		strob.SOS();
		StrobActivity.this.finish();
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "onDestroy called!!");
		}
	}
}
