package com.slobodastudio.smspanic.activities;

/** The class describes all simple dialogs in app. It's base on alertdialog.xml */
import com.slobodastudio.smspanic.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SimpleAlertDialogs extends Dialog {

	// listener for single dialog button "Ok". It's close the dialog
	private class OKListener implements android.view.View.OnClickListener {

		@Override
		public void onClick(View v) {

			dismiss();
		}
	}

	TextView dialogMessage; // text field for dialog message
	private String message = null;
	private int messageId = 0;

	/** Needs to set: dialog.setMessage(id);
	 * 
	 * @param context */
	public SimpleAlertDialogs(Context context) {

		super(context);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.alertdialog);
		Button buttonOK = (Button) findViewById(R.id.dialog_btn_ok);
		ImageView icon = (ImageView) findViewById(R.id.im_alert);
		buttonOK.setOnClickListener(new OKListener());
		dialogMessage = (TextView) findViewById(R.id.dialogtext);
		if (messageId != 0) {
			setTitle(R.string.dialog_alert_title);
			dialogMessage.setText(messageId);
		} else if (message != null) {
			setTitle(R.string.dialog_cell_title);
			icon.setImageResource(android.R.drawable.ic_dialog_info);
			dialogMessage.setText(message);
		} else {
			dialogMessage.setText(R.string.dialog_message_error);
		}
	}

	// setter for message
	public void setMessage(int id) {

		messageId = id;
	}

	public void setMessage(String text) {

		message = text;
	}
}
