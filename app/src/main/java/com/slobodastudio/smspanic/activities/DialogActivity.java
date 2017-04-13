package com.slobodastudio.smspanic.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.slobodastudio.smspanic.R;
import com.slobodastudio.smspanic.broadcasts.DoEnableStrobBroadcast;
import com.slobodastudio.smspanic.broadcasts.SmsSenderBroadcast;

public class DialogActivity extends Activity {

	private final int DIALOG_CANCEL_STROB = 222;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		showDialog(DIALOG_CANCEL_STROB);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		AlertDialog dialog;
		switch (id) {
			case DIALOG_CANCEL_STROB:
				builder.setTitle(DialogActivity.this.getResources().getString(R.string.app_name));
				builder.setMessage(DialogActivity.this.getResources().getString(R.string.strob_cancel))
						.setCancelable(false).setPositiveButton(
								DialogActivity.this.getResources().getString(R.string.ac_addmessage_yes),
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog, int id) {

										NotificationManager nm = (NotificationManager) DialogActivity.this
												.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
										nm.cancel(SmsSenderBroadcast.NOTIFY_ID);
										AlarmManager am = (AlarmManager) DialogActivity.this
												.getSystemService(Context.ALARM_SERVICE);
										am.cancel(SmsSenderBroadcast.pendingIntent);
										am.cancel(DoEnableStrobBroadcast.getPendingIntent());
										DialogActivity.this.finish();
										dialog.cancel();
									}
								}).setNegativeButton(
								DialogActivity.this.getResources().getString(R.string.ac_addmessage_no),
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog, int id) {

										DialogActivity.this.finish();
										dialog.cancel();
									}
								});
				dialog = builder.create();
				return dialog;
		}
		return super.onCreateDialog(id);
	}

	@Override
	protected void onPause() {

		Intent intent = new Intent(DialogActivity.this, MessagesListActivity.class);
		startActivity(intent);
		super.onPause();
	}
}
