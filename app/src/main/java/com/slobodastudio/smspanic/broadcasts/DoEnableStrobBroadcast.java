package com.slobodastudio.smspanic.broadcasts;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.slobodastudio.smspanic.MessageValue;
import com.slobodastudio.smspanic.R;
import com.slobodastudio.smspanic.activities.StrobActivity;
import com.slobodastudio.smspanic.utils.MessageController;

public class DoEnableStrobBroadcast extends BroadcastReceiver {

	private static final String TAG = DoEnableStrobBroadcast.class.getSimpleName();
	public static final String EXTRA_MESSAGE_VALUE_ID = "extraMessageValueId";
	private static PendingIntent pendingIntent;

	@Override
	public void onReceive(Context context, Intent intent) {

		int messageId = intent.getIntExtra(EXTRA_MESSAGE_VALUE_ID, 0);
		MessageValue message = MessageController.getmessageById(context, messageId);
		if (message == null || !message.isChecked() || !message.isStrobEnabled()) {
			return;
		}
		String array1[] = context.getResources().getStringArray(R.array.spRepeateTimesInMinutes);
		int timesInMinutes = Integer.parseInt(array1[message.getTimesInMinutesId()]);
		AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		if (message.getTimes() > 0) {
			Intent acIntent = new Intent(context, StrobActivity.class);
			acIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(acIntent);
			Intent brIntent = new Intent(context, DoEnableStrobBroadcast.class);
			brIntent.putExtra(EXTRA_MESSAGE_VALUE_ID, messageId);
			pendingIntent = PendingIntent.getBroadcast(context, message.getId(), brIntent,
					PendingIntent.FLAG_CANCEL_CURRENT);
			if (message.getTimes() > 1) {
				message.setTimes(message.getTimes() - 1);
				MessageController.updateMessageInDb(context, message);
				mgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000 / timesInMinutes,
						pendingIntent);
			} else {
				NotificationManager nm = (NotificationManager) context
						.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
				nm.cancel(SmsSenderBroadcast.NOTIFY_ID);
			}
		}
	}

	public static PendingIntent getPendingIntent() {

		return pendingIntent;
	}
}
