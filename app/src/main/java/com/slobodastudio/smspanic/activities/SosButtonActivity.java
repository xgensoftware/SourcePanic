package com.slobodastudio.smspanic.activities;

import com.slobodastudio.smspanic.R;
import com.slobodastudio.smspanic.broadcasts.SmsSenderBroadcast;
import com.slobodastudio.smspanic.services.ListenerService;
import com.slobodastudio.smspanic.utils.ComponentsUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class SosButtonActivity extends Activity {

	private int messageId;
	// private Intent intent;
	private static final String TAG = SosButtonActivity.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ac_sos_button);
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.RelativeLayout1);
		ComponentsUtils.setViewsSettings(rl);
		Intent intent = getIntent();
		messageId = intent.getIntExtra(SmsSenderBroadcast.EXTRA_MESSAGE_VALUE_ID, 0);
		ImageButton sosButton = (ImageButton) findViewById(R.id.ac_sos_button);
		sosButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				sendMessage();
			}
		});
	}

	private void sendMessage() {

		Log.v(TAG, "send message to broadcast");
		Intent intent = new Intent();
		intent.setAction(SmsSenderBroadcast.ACTION);
		intent.putExtra(ListenerService.SERVICE_MODE_TRANSFER_KEY, ListenerService.MEDIA_CAPTURE_MODE);
		intent.putExtra(SmsSenderBroadcast.EXTRA_MESSAGE_VALUE_ID, messageId);
		sendBroadcast(intent);
		finish();
	}
}
