package com.slobodastudio.smspanic.broadcasts;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;

import com.slobodastudio.smspanic.MessageValue;
import com.slobodastudio.smspanic.R;
import com.slobodastudio.smspanic.activities.DialogActivity;
import com.slobodastudio.smspanic.database.SmsColumns;
import com.slobodastudio.smspanic.services.ListenerService;
import com.slobodastudio.smspanic.timers.DownTimer;
import com.slobodastudio.smspanic.utils.LocationProvider;
import com.slobodastudio.smspanic.utils.MessageController;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class SmsSenderBroadcast extends BroadcastReceiver implements Observer {

	public static final String EXTRA_MESSAGE_VALUE_ID = "extraMessageValueId";
	private Context context;
	private LocationProvider locationProvider;
	private static final boolean LOGV = true;
	private boolean wasSent = false;
	private DownTimer timer;
	private MessageValue message;
	public static final int LOCATION_WAITING_INTERVAL = 15000;
	public static final int NOTIFY_ID = 253;
	public static PendingIntent pendingIntent;
	public static final String GMAPS_LINK_HEADER = "https://maps.google.com/maps?q=";
	public static final String GMAPS_LINK_TRAILER = "&num=1&t=m&z=10";
	public static final String ACTION = "com.slobodastudio.smspanic.smssenderbroadcast";
	private static final String TAG = SmsSenderBroadcast.class.getSimpleName();

	public void sendSms(Context context, String number, String message)
			throws IllegalArgumentException {

		SmsManager sms = SmsManager.getDefault();
		sms.sendMultipartTextMessage(number, null, sms.divideMessage(message), null, null);
		if (!this.message.isHide()) {
			saveMessage(context, number, message);
		}
		if (LOGV) {
			Log.v(TAG, "message was sent " + number + " text " + message);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals(ACTION)) {
			this.context = context;
			int messageId = intent.getIntExtra(EXTRA_MESSAGE_VALUE_ID, 0);
			message = MessageController.getmessageById(context, messageId);
			if (message == null || !message.isChecked()) {
				return;
			}
			// ---------Strob
			String array1[] = context.getResources()
					.getStringArray(R.array.spRepeateTimesInMinutes);
			int timesInMinutes = Integer.parseInt(array1[message.getTimesInMinutesId()]);
			String array2[] = context.getResources().getStringArray(R.array.spRepeateTimes);
			int times = Integer.parseInt(array2[message.getTimesId()]);
			if (message.isStrobEnabled()) {
				sendNotification();
				message.setTimes(times * timesInMinutes);
				MessageController.updateMessageInDb(context, message);
				AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
				Intent brIntent = new Intent(context, DoEnableStrobBroadcast.class);
				brIntent.putExtra(EXTRA_MESSAGE_VALUE_ID, messageId);
				pendingIntent = PendingIntent.getBroadcast(context, message.getId(), brIntent,
						PendingIntent.FLAG_CANCEL_CURRENT);
				long allTime = (message.getMediaSendOnes() * message.getMediaSendFrequency() * 1000) + 5000;
				Log.v(TAG, " allTime = " + allTime);
				mgr.set(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis() + allTime),
						pendingIntent);
			}
			// ------end strob
			timer = new DownTimer(new TimerEndHandler());
			locationProvider = LocationProvider.getInstance();
			locationProvider.setParams(context);
			locationProvider.addObserver(this);
			locationProvider.startUpdates();
			timer.startFor(LOCATION_WAITING_INTERVAL);
			Thread thr = new Thread() {

				@Override
				public void run() {

					super.run();
					startServiceIfNeed();
				}
			};
			thr.start();
			Log.v(TAG, "onreceive");
		}
	}

	private void saveMessage(Context context, String number, String body) {

		if (LOGV) {
			Log.v(TAG, "saveMessage (in history) called!");
		}
		ContentValues values = new ContentValues();
		values.put(SmsColumns.ADDRESS, number);
		values.put(SmsColumns.BODY, body);
		values.put(SmsColumns.DATE, System.currentTimeMillis());
		values.put(SmsColumns.TYPE, 2);
		context.getContentResolver().insert(SmsColumns.SMS_CONTENT_URI, values);
	}

	private void send(Context context, String number, String text) {

		try {
			sendSms(context, number, text);
		} catch (IllegalArgumentException e) {
			Log.e(TAG, "Argument exception", e);
		}
		if (LOGV) {
			Log.v(TAG, "sms sent");
		}
	}

	@Override
	public void update(Observable observable, Object location) {

		Log.v(TAG, "update called");
		Location currentLocation = (Location) location;
		timer.stop();
		timer = null;
		if (LOGV) {
			Log.v(TAG, "onReceive called! AIRPLANE_MODE_ON="
					+ (Settings.System.getInt(context.getContentResolver(),
							Settings.System.AIRPLANE_MODE_ON, 0) != 0));
		}
		if (wasSent) {
			return;
		}
		if ((Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON,
				0) == 0)) {
			String messageBody = getMessageBody(currentLocation);
			ArrayList<String> numbers = message.getNumbers();
			if (numbers != null) {
				for (String num : numbers) {
					Log.v(TAG, num);
					send(context, num, messageBody);
				}
			} else {
				Log.e(TAG, "numbers is null");
			}
			wasSent = true;
			locationProvider.deleteObserver(this);
			locationProvider.stopUpdates();
		}
	}

	class TimerEndHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			Log.v(TAG, "handler called");
			if ((Settings.System.getInt(context.getContentResolver(),
					Settings.System.AIRPLANE_MODE_ON, 0) == 0)) {
				String messageBody = getMessageBody(null);
				ArrayList<String> numbers = message.getNumbers();
				if (wasSent) {
					return;
				}
				if (numbers != null) {
					for (String num : numbers) {
						Log.v(TAG, num);
						send(context, num, messageBody);
					}
				} else {
					Log.e(TAG, "numbers is null");
				}
				wasSent = true;
				timer.stop();
				timer = null;
				locationProvider.deleteObserver(SmsSenderBroadcast.this);
				locationProvider.stopUpdates();
			}
		}
	}

	private String getMessageBody(Location location) {

		StringBuilder sb = new StringBuilder();
		sb.append(message.getText());
		sb.append("\n");
		if (location == null) {
			Location lastLoc = locationProvider.getLastLocation();
			if (lastLoc == null) {
				sb.append("cannot get location");
			} else {
				Log.v(TAG, "last known" + lastLoc.getLatitude());
				sb.append("latitude: ");
				sb.append(lastLoc.getLatitude());
				sb.append("\nlongitude: ");
				sb.append(lastLoc.getLongitude());
				sb.append("\nSee on google maps:\n");
				sb.append(GMAPS_LINK_HEADER);
				sb.append(lastLoc.getLatitude());
				sb.append(",");
				sb.append(lastLoc.getLongitude());
				sb.append(GMAPS_LINK_TRAILER);
			}
		} else {
			sb.append("latitude: ");
			sb.append(location.getLatitude());
			sb.append("\nlongitude: ");
			sb.append(location.getLongitude());
			sb.append("\nSee on google maps:\n");
			sb.append(GMAPS_LINK_HEADER);
			sb.append(location.getLatitude());
			sb.append(",");
			sb.append(location.getLongitude());
			sb.append(GMAPS_LINK_TRAILER);
		}
		sb.append("\n");
		return sb.toString();
	}

	private void startServiceIfNeed() {

		if (message.getMediaRecorder() != 0) {
			Log.v(TAG, "starting service " + message.toString());
			Intent intent = new Intent(context, ListenerService.class);
			intent.putExtra(ListenerService.SERVICE_MODE_TRANSFER_KEY,
					ListenerService.MEDIA_CAPTURE_MODE);
			intent.putExtra(SmsSenderBroadcast.EXTRA_MESSAGE_VALUE_ID, message);
			context.startService(intent);
		}
	}

	private void sendNotification() {

		int icon = R.drawable.ic_launcher;
		CharSequence tickerText = context.getString(R.string.app_name);
		Notification notification = new Notification(icon, tickerText, 0);
		CharSequence contentTitle = context.getString(R.string.app_name);
		CharSequence contentText = context.getResources().getString(R.string.strob_cancel);
		Intent notificationIntent = new Intent(context, DialogActivity.class);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		NotificationManager nm = (NotificationManager) context
				.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		// notification.flags |= Notification.FLAG_AUTO_CANCEL;
		nm.notify(NOTIFY_ID, notification);
	}
}
