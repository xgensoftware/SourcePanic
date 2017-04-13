package com.slobodastudio.smspanic.database;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/** Contract class for interacting with {@link SmsPanicProvider}. Unless otherwise noted, all time-based fields
 * are milliseconds since epoch and can be compared against {@link System#currentTimeMillis()}.
 * <p>
 * The backing {@link android.content.ContentProvider} assumes that {@link Uri} are generated using stronger
 * {@link String} identifiers, instead of {@code int} {@link BaseColumns#_ID} values, which are prone to
 * shuffle during sync. */
public class SmsPanicContract {

	/** Describes location's table. Each location is associated with a specific {@link Logs}. */
	public static class Messages implements MessagesColumns, BaseColumns {

		/** The MIME type of {@link #CONTENT_URI} providing a directory of locations */
		public static final String CONTENT_DIR_TYPE = "vnd.android.cursor.dir/vnd.smspanic.messages";
		/** The MIME type of {@link #CONTENT_URI} providing a single location */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.smspanic.messages";
		/** The content:// style URL for this table */
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MESSAGES).build();
		/** Default "ORDER BY" clause. */
		public static final String DEFAULT_SORT = MessagesColumns.ID + " ASC";

		/** Build {@link Uri} for requested location {@link #_ID}.
		 * 
		 * @param locationId
		 *            unique location identifier
		 * @return a Uri for the given id */
		public static Uri buildStationsUri(long locationId) {

			return ContentUris.withAppendedId(CONTENT_URI, locationId);
		}

		/** Build {@link Uri} for requested location {@link #_ID}.
		 * 
		 * @param messageId
		 *            unique location identifier
		 * @return a Uri for the given id */
		public static Uri buildMessageUri(String messageId) {

			return CONTENT_URI.buildUpon().appendPath(messageId).build();
		}

		/** Read {@link #_ID} from {@link Messages} {@link Uri}.
		 * 
		 * @param uri
		 *            a location uri that contains location id
		 * @return a unique identifier provided by location uri */
		public static String getMessageId(Uri uri) {

			return uri.getPathSegments().get(1);
		}

		/** A private Constructor prevents class from instantiating. */
		private Messages() {

			throw new UnsupportedOperationException("Class is prevented from instantiation");
		}
	}

	interface MessagesColumns {

		String ID = "_id";
		String TEXT = "text";
		String NAME = "name";
		String NUMBERS = "numbers";
		String LAUNCH_MODE_WIDGET = "launch_mode_widget";
		String WIDG_NAME = "widg_name";
		String WIDG_CLICK_ONES = "widg_click_ones";
		String SHAKE_ONES = "shake_ones";
		String IS_HIDE = "is_hide";
		String IS_CHECKED = "is_checked";
		String IS_EMAIL_ENABLE = "is_email_enable";
		String ROTATE_ONES = "rotate_ones";
		String MEDIA_DEVICE_ID = "media_device_code";
		String SEND_MEDIA_ONES = "send_media_ones";
		String VOLUME_CODE = "volume_code";
		String DEST_EMAILS = "dest_emails";
		String SEND_MEDIA_FREQUENCY = "send_media_frequency";
		String TIMES_IN_MINUTES_ID = "times_in_minutes_id";
		String TIMES_IN_MINUTES = "times_in_minutes";
		String TIMES = "times";
		String TIMES_ID = "times_id";
		String IS_STROB_ENABLED = "is_strob_enabled";
		String RECORD_TYPE = "record_type";
		// String IS_CHECKED_TO_ACCEL = "is_checked_to_accel";
	}

	public static final String CONTENT_AUTHORITY = "com.slobodastudio.smspanic";
	static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
	/** A domain name for the {@link SmsPanicProvider} */
	private static final String PATH_MESSAGES = "messages";
	/** Special value for {@link Logs#NAME_MEDIA} indicating that a task finished with a fail. */
	public static final long STATE_FINISHED_FAIL = 3;
	/** Special value for {@link Logs#NAME_MEDIA} indicating that a task finished correctly. */
	public static final long STATE_FINISHED_OK = 2;
	/** Special value for {@link Logs#NAME_MEDIA} indicating that a task is started. */
	public static final long STATE_STARTED = 1;
	/** Special value for {@link Logs#NAME_MEDIA} indicating that a task is not started. */
	public static final long STATE_WAIT = 0;

	/** A private Constructor prevents class from instantiating. */
	private SmsPanicContract() {

		throw new UnsupportedOperationException("Class is prevented from instantiation");
	}
}
