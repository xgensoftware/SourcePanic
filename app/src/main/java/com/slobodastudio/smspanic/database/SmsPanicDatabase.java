package com.slobodastudio.smspanic.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.slobodastudio.smspanic.database.SmsPanicContract.MessagesColumns;

/** Helper for managing {@link SQLiteDatabase} that stores data for {@link SmsPanicProvider}. */
public class SmsPanicDatabase extends SQLiteOpenHelper {

	private interface CreateTable {

		String messages = "CREATE TABLE " + Tables.MESSAGES + " (" + StationsSql.id + StationsSql.text
				+ StationsSql.name + StationsSql.numbers + StationsSql.is_checked + StationsSql.is_hide
				+ StationsSql.widg_name + StationsSql.widg_click_ones + StationsSql.shake_ones
				+ StationsSql.rotate_ones + StationsSql.is_email_enable + StationsSql.media_device_id
				+ StationsSql.send_media_ones + StationsSql.volume_code + StationsSql.dest_emails
				+ StationsSql.send_media_frequency + StationsSql.launch_mode_widget
				+ StationsSql.times_in_minutes_id + StationsSql.times_in_minutes + StationsSql.times_id
				+ StationsSql.times + StationsSql.is_strob_enabled + StationsSql.record_type // +
																								// StationsSql.is_checked_to_accel
				+ ")";
	}

	private interface StationsSql {

		String id = MessagesColumns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ";
		String text = MessagesColumns.TEXT + " TEXT, ";
		String numbers = MessagesColumns.NUMBERS + " TEXT, ";
		String name = MessagesColumns.NAME + " TEXT,";
		String widg_name = MessagesColumns.WIDG_NAME + " TEXT, ";
		String widg_click_ones = MessagesColumns.WIDG_CLICK_ONES + " TEXT, ";
		String shake_ones = MessagesColumns.SHAKE_ONES + " TEXT, ";
		String rotate_ones = MessagesColumns.ROTATE_ONES + " TEXT, ";
		String is_checked = MessagesColumns.IS_CHECKED + " TEXT, ";
		String is_hide = MessagesColumns.IS_HIDE + " TEXT, ";
		String is_email_enable = MessagesColumns.IS_EMAIL_ENABLE + " TEXT, ";
		String media_device_id = MessagesColumns.MEDIA_DEVICE_ID + " INTEGER, ";
		String send_media_ones = MessagesColumns.SEND_MEDIA_ONES + " INTEGER, ";
		String dest_emails = MessagesColumns.DEST_EMAILS + " TEXT, ";
		String volume_code = MessagesColumns.VOLUME_CODE + " INTEGER, ";
		String send_media_frequency = MessagesColumns.SEND_MEDIA_FREQUENCY + " INTEGER, ";
		String launch_mode_widget = MessagesColumns.LAUNCH_MODE_WIDGET + " TEXT, ";
		String times_in_minutes = MessagesColumns.TIMES_IN_MINUTES + " INTEGER, ";
		String times_in_minutes_id = MessagesColumns.TIMES_IN_MINUTES_ID + " INTEGER, ";
		String times_id = MessagesColumns.TIMES_ID + " INTEGER, ";
		String times = MessagesColumns.TIMES + " INTEGER, ";
		String is_strob_enabled = MessagesColumns.IS_STROB_ENABLED + " TEXT, ";
		String record_type = MessagesColumns.RECORD_TYPE + " INTEGER ";
		// String is_checked_to_accel = MessagesColumns.IS_CHECKED_TO_ACCEL + " TEXT ";
	}

	interface Tables {

		String MESSAGES = "messages";
	}

	private static final String DATABASE_NAME = "smspanic.db";
	// NOTE: carefully update onUpgrade() when bumping database versions to make
	// sure user data is saved.
	private static final int DATABASE_VERSION = 1;
	private static final String TAG = SmsPanicDatabase.class.getSimpleName();

	/** @param context
	 *            to use to open or create the database */
	public SmsPanicDatabase(Context context) {

		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL(CreateTable.messages);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {

		super.onOpen(db);
		if (!db.isReadOnly()) {
			// Enable foreign key constraints
			db.execSQL("PRAGMA foreign_keys=ON;");
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		Log.d(TAG, "onUpgrade() from " + oldVersion + " to " + newVersion);
		if (oldVersion != DATABASE_VERSION) {
			Log.w(TAG, "Destroying old data during upgrade");
			db.execSQL("DROP TABLE IF EXISTS " + Tables.MESSAGES);
			onCreate(db);
		}
	}
}
