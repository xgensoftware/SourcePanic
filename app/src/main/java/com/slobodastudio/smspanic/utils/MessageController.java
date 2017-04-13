package com.slobodastudio.smspanic.utils;

import java.util.ArrayList;
import java.util.StringTokenizer;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.slobodastudio.smspanic.MessageValue;
import com.slobodastudio.smspanic.R;
import com.slobodastudio.smspanic.database.SmsPanicContract;

public class MessageController {

	private static final String TAG = MessageController.class.getSimpleName();

	public interface LAUNCH_MODES {

		public static final int WIDGET_MODE = 1;
		public static final int ACCELEROMETER_MODE = 2;
		public static final int GYROSCOPE_MODE = 4;
	}

	public static void saveMessageInDb(Context context, MessageValue newMessage) {

		ContentValues cv = getContentValues(newMessage);
		context.getContentResolver().insert(SmsPanicContract.Messages.CONTENT_URI, cv);
	}

	public static void updateMessageInDb(Context context, MessageValue newMessage) {

		ContentValues cv = getContentValues(newMessage);
		Log.v(TAG, newMessage.getName() + " " + newMessage.getId() + " update");
		String whereId = String.valueOf(newMessage.getId()).trim();
		context.getContentResolver().update(SmsPanicContract.Messages.CONTENT_URI, cv,
				SmsPanicContract.Messages.ID + "='" + whereId + "'", null);
	}

	public static void updateAllMessages(Context context) {

		SharedPreferences settingsStrob = PreferenceManager.getDefaultSharedPreferences(context);
		int timesOnesInMinutes = Integer.parseInt(settingsStrob.getString(context
				.getString(R.string.pref_ones_minutes_key), "1"));
		int timesInMinutes = Integer.parseInt(settingsStrob.getString(context
				.getString(R.string.pref_strob_minutes_key), "1"));
		ArrayList<MessageValue> messages = getAllMessages(context);
		if(messages == null)
			return;
		for (MessageValue curMessage : messages) {
			curMessage.setTimesInMinutesId(timesOnesInMinutes);
			curMessage.setTimesId(timesInMinutes);
			updateMessageInDb(context, curMessage);
		}
	}

	public static void deleteMessageFromDb(Context context, int messageId) {

		context.getContentResolver().delete(SmsPanicContract.Messages.CONTENT_URI,
				SmsPanicContract.Messages.ID + "='" + messageId + "'", null);
	}

	public static ArrayList<String> getStringsArrayListFromStr(String numbersStr) {

		ArrayList<String> numbers = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(numbersStr, ",");
		while (st.hasMoreTokens()) {
			numbers.add(st.nextToken().trim());
		}
		return numbers;
	}

	private static ContentValues getContentValues(MessageValue newMessage) {

		ContentValues cv = new ContentValues();
		cv.put(SmsPanicContract.Messages.NAME, newMessage.getName());
		cv.put(SmsPanicContract.Messages.NUMBERS, newMessage.getNumbersNamesPairs());
		cv.put(SmsPanicContract.Messages.TEXT, newMessage.getText());
		cv.put(SmsPanicContract.Messages.WIDG_NAME, newMessage.getWidgName());
		cv.put(SmsPanicContract.Messages.WIDG_CLICK_ONES, newMessage.getWidgClickOnes());
		cv.put(SmsPanicContract.Messages.SHAKE_ONES, newMessage.getShakeOnes());
		cv.put(SmsPanicContract.Messages.ROTATE_ONES, newMessage.getRotateOnes());
		cv.put(SmsPanicContract.Messages.LAUNCH_MODE_WIDGET, String.valueOf(newMessage.isLaunchModeWidget()));
		cv.put(SmsPanicContract.Messages.IS_CHECKED, String.valueOf(newMessage.isChecked()));
		cv.put(SmsPanicContract.Messages.MEDIA_DEVICE_ID, newMessage.getMediaRecorder());
		cv.put(SmsPanicContract.Messages.SEND_MEDIA_FREQUENCY, newMessage.getMediaSendFrequency());
		cv.put(SmsPanicContract.Messages.SEND_MEDIA_ONES, newMessage.getMediaSendOnes());
		cv.put(SmsPanicContract.Messages.DEST_EMAILS, newMessage.getEmails());
		cv.put(SmsPanicContract.Messages.IS_HIDE, String.valueOf(newMessage.isHide()));
		cv.put(SmsPanicContract.Messages.IS_EMAIL_ENABLE, String.valueOf(newMessage.isEmailEnable()));
		cv.put(SmsPanicContract.Messages.IS_STROB_ENABLED, String.valueOf(newMessage.isStrobEnabled()));
		cv.put(SmsPanicContract.Messages.RECORD_TYPE, newMessage.getRecordingType());
		cv.put(SmsPanicContract.Messages.TIMES_IN_MINUTES_ID, newMessage.getTimesInMinutesId());
		cv.put(SmsPanicContract.Messages.TIMES_IN_MINUTES, newMessage.getTimesInMinutes());
		cv.put(SmsPanicContract.Messages.TIMES_ID, newMessage.getTimesId());
		cv.put(SmsPanicContract.Messages.TIMES, newMessage.getTimes());
		// cv.put(SmsPanicContract.Messages.IS_CHECKED_TO_ACCEL,
		// String.valueOf(newMessage.isCheckedByAcceler()));
		return cv;
	}

	public static ArrayList<MessageValue> getMessagesForWidget(Context context) {

		Cursor cur = context.getContentResolver().query(
				SmsPanicContract.Messages.CONTENT_URI,
				null,
				SmsPanicContract.Messages.LAUNCH_MODE_WIDGET + "='true'" + " AND "
						+ SmsPanicContract.Messages.IS_CHECKED + "='" + String.valueOf(true) + "'", null,
				null);
		if (cur.getCount() == 0) {
			cur.close();
			return null;
		}
		ArrayList<MessageValue> listOfWidgetMessages = new ArrayList<MessageValue>();
		listOfWidgetMessages = getMessages(cur);
		cur.close();
		return listOfWidgetMessages;
	}

	public static MessageValue getmessageById(Context context, int id) {

		Cursor cur = context.getContentResolver().query(SmsPanicContract.Messages.CONTENT_URI, null,
				SmsPanicContract.Messages.ID + "='" + String.valueOf(id) + "'", null, null);
		if (cur.getCount() == 0) {
			cur.close();
			return null;
		}
		cur.moveToFirst();
		MessageValue tempMessage = getMessage(cur);
		cur.close();
		return tempMessage;
	}

	public static ArrayList<MessageValue> getAllMessages(Context context) {

		Cursor cur = context.getContentResolver().query(SmsPanicContract.Messages.CONTENT_URI, null, null,
				null, null);
		if (cur.getCount() == 0) {
			cur.close();
			return null;
		}
		Log.v(TAG, cur.getCount() + " cursor count");
		ArrayList<MessageValue> listOfWidgetMessages = new ArrayList<MessageValue>();
		listOfWidgetMessages = getMessages(cur);
		cur.close();
		return listOfWidgetMessages;
	}

	private static ArrayList<MessageValue> getMessages(Cursor cursor) {

		ArrayList<MessageValue> messages = new ArrayList<MessageValue>();
		cursor.moveToFirst();
		do {
			messages.add(getMessage(cursor));
		} while (cursor.moveToNext());
		return messages;
	}

	private static MessageValue getMessage(Cursor cursor) {

		MessageValue tempMessage = new MessageValue();
		int id = Integer.parseInt(cursor
				.getString(cursor.getColumnIndexOrThrow(SmsPanicContract.Messages.ID)));
		tempMessage.setId(id);
		boolean widgetMode = Boolean.valueOf(cursor.getString(cursor
				.getColumnIndexOrThrow(SmsPanicContract.Messages.LAUNCH_MODE_WIDGET)));
		tempMessage.setLaunchModeWidget(widgetMode);
		tempMessage.setName(cursor.getString(cursor.getColumnIndexOrThrow(SmsPanicContract.Messages.NAME)));
		tempMessage.setNumbersNames(cursor.getString(cursor
				.getColumnIndexOrThrow(SmsPanicContract.Messages.NUMBERS)));
		tempMessage.setText(cursor.getString(cursor.getColumnIndexOrThrow(SmsPanicContract.Messages.TEXT)));
		tempMessage.setWidgName(cursor.getString(cursor
				.getColumnIndexOrThrow(SmsPanicContract.Messages.WIDG_NAME)));
		tempMessage.setWidgClickOnes(cursor.getString(cursor
				.getColumnIndexOrThrow(SmsPanicContract.Messages.WIDG_CLICK_ONES)));
		String isChecked = cursor.getString(cursor
				.getColumnIndexOrThrow(SmsPanicContract.Messages.IS_CHECKED));
		tempMessage.setChecked(Boolean.parseBoolean(isChecked));
		// String isCheckedToAccel = cursor.getString(cursor
		// .getColumnIndexOrThrow(SmsPanicContract.Messages.IS_CHECKED_TO_ACCEL));
		// tempMessage.setCheckedByAcceler(Boolean.parseBoolean(isCheckedToAccel));
		String isHide = cursor.getString(cursor.getColumnIndexOrThrow(SmsPanicContract.Messages.IS_HIDE));
		tempMessage.setHide(Boolean.parseBoolean(isHide));
		String isEmailEnable = cursor.getString(cursor
				.getColumnIndexOrThrow(SmsPanicContract.Messages.IS_EMAIL_ENABLE));
		tempMessage.setEmailEnable(Boolean.parseBoolean(isEmailEnable));
		tempMessage.setMediaRecorder(cursor.getInt(cursor
				.getColumnIndexOrThrow(SmsPanicContract.Messages.MEDIA_DEVICE_ID)));
		tempMessage.setEmails(cursor.getString(cursor
				.getColumnIndexOrThrow(SmsPanicContract.Messages.DEST_EMAILS)));
		tempMessage.setMediaSendFrequency(cursor.getInt(cursor
				.getColumnIndexOrThrow(SmsPanicContract.Messages.SEND_MEDIA_FREQUENCY)));
		tempMessage.setMediaSendOnes(cursor.getInt(cursor
				.getColumnIndexOrThrow(SmsPanicContract.Messages.SEND_MEDIA_ONES)));
		String isStrobEnable = cursor.getString(cursor
				.getColumnIndexOrThrow(SmsPanicContract.Messages.IS_STROB_ENABLED));
		tempMessage.setStrobEnabled(Boolean.parseBoolean(isStrobEnable));
		tempMessage.setRecordingType(cursor.getInt(cursor
				.getColumnIndexOrThrow(SmsPanicContract.Messages.RECORD_TYPE)));
		tempMessage.setTimesInMinutesId(cursor.getInt(cursor
				.getColumnIndexOrThrow(SmsPanicContract.Messages.TIMES_IN_MINUTES_ID)));
		tempMessage.setTimesInMinutes(cursor.getInt(cursor
				.getColumnIndexOrThrow(SmsPanicContract.Messages.TIMES_IN_MINUTES)));
		tempMessage.setTimesId(cursor
				.getInt(cursor.getColumnIndexOrThrow(SmsPanicContract.Messages.TIMES_ID)));
		tempMessage.setTimes(cursor.getInt(cursor.getColumnIndexOrThrow(SmsPanicContract.Messages.TIMES)));
		return tempMessage;
	}
	// public static final int[] getBusyVolumeCodes(Context context) {
	//
	// String projection[] = { SmsPanicContract.Messages.VOLUME_CODE };
	// Cursor cur = context.getContentResolver().query(SmsPanicContract.Messages.CONTENT_URI, projection,
	// null, null, null);
	// if (cur.getCount() == 0) {
	// cur.close();
	// return null;
	// }
	// int result[] = new int[cur.getCount()];
	// cur.moveToFirst();
	// int i = 0;
	// do {
	// result[i++] = cur.getInt(cur.getColumnIndexOrThrow(SmsPanicContract.Messages.VOLUME_CODE));
	// } while (cur.moveToNext());
	// cur.close();
	// return result;
	// }
}
