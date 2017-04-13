package com.slobodastudio.smspanic.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.slobodastudio.smspanic.services.AccelerometerListenerService;

public class BlackActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		if ((intent.hasExtra(AccelerometerListenerService.KEY_TO_ACTIVITY))
				&& (intent.getExtras().getBoolean(AccelerometerListenerService.KEY_TO_ACTIVITY))) {
			finish();
		}
	};

	@Override
	protected void onPause() {

		super.onPause();
	}
}
