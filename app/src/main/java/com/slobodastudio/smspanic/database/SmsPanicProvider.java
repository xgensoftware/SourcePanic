package com.slobodastudio.smspanic.database;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.slobodastudio.smspanic.database.SmsPanicContract.Messages;
import com.slobodastudio.smspanic.database.SmsPanicContract.MessagesColumns;
import com.slobodastudio.smspanic.database.SmsPanicDatabase.Tables;

/** Provider that stores {@link SmsPanicContract} data. Data is usually inserted by {@link SyncService}, and
 * queried by various {@link Activity} instances. */
public class SmsPanicProvider extends ContentProvider {

	private static final int MESSAGES_DIR = 201;
	private static final int MESSAGES_ITEM = 200;
	private static final boolean LOGV = true;
	private static final UriMatcher sUriMatcher = buildUriMatcher();
	private static final String TAG = "ScheduleProvider";

	/** Build a simple {@link SelectionBuilder} to match the requested {@link Uri}. This is usually enough to
	 * support {@link #insert}, {@link #update}, and {@link #delete} operations. */
	private static SelectionBuilder buildSimpleSelection(Uri uri) {

		final SelectionBuilder builder = new SelectionBuilder();
		final int match = sUriMatcher.match(uri);
		switch (match) {
			case MESSAGES_DIR:
				return builder.table(Tables.MESSAGES);
			case MESSAGES_ITEM:
				final String locationId = Messages.getMessageId(uri);
				return builder.table(Tables.MESSAGES).where(MessagesColumns.ID + "=?", locationId);
			default:
				throw new IllegalArgumentException("Unknown uri: " + uri);
		}
	}

	/** Build and return a {@link UriMatcher} that catches all {@link Uri} variations supported by this
	 * {@link ContentProvider}. */
	private static UriMatcher buildUriMatcher() {

		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = SmsPanicContract.CONTENT_AUTHORITY;
		matcher.addURI(authority, "messages", MESSAGES_DIR);
		matcher.addURI(authority, "messages/*", MESSAGES_ITEM);
		return matcher;
	}

	private SmsPanicDatabase mOpenHelper;

	/** Apply the given set of {@link ContentProviderOperation}, executing inside a {@link SQLiteDatabase}
	 * transaction. All changes will be rolled back if any single one fails. */
	@Override
	public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
			throws OperationApplicationException {

		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			final int numOperations = operations.size();
			final ContentProviderResult[] results = new ContentProviderResult[numOperations];
			for (int i = 0; i < numOperations; i++) {
				results[i] = operations.get(i).apply(this, results, i);
			}
			db.setTransactionSuccessful();
			return results;
		} finally {
			db.endTransaction();
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		if (LOGV) {
			Log.v(TAG, "delete(uri=" + uri + ")");
		}
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final SelectionBuilder builder = buildSimpleSelection(uri);
		int retVal = builder.where(selection, selectionArgs).delete(db);
		getContext().getContentResolver().notifyChange(uri, null);
		return retVal;
	}

	@Override
	public String getType(Uri uri) {

		final int match = sUriMatcher.match(uri);
		switch (match) {
			case MESSAGES_DIR:
				return Messages.CONTENT_DIR_TYPE;
			case MESSAGES_ITEM:
				return Messages.CONTENT_ITEM_TYPE;
			default:
				throw new IllegalArgumentException("Unknown uri: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		if (LOGV) {
			Log.v(TAG, "insert(uri=" + uri + ", values=" + values.toString() + ")");
		}
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		final long insertedId;
		final Uri insertedUri;
		switch (match) {
			case MESSAGES_DIR:
				insertedId = db.insertOrThrow(Tables.MESSAGES, null, values);
				insertedUri = Messages.buildStationsUri(insertedId);
				break;
			default:
				throw new IllegalArgumentException("Unknown uri: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return insertedUri;
	}

	@Override
	public boolean onCreate() {

		final Context context = getContext();
		mOpenHelper = new SmsPanicDatabase(context);
		return true;
	}

	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode) {

		throw new UnsupportedOperationException("With uri: " + uri + ", mode: " + mode);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {

		if (LOGV) {
			Log.v(TAG, "query(uri=" + uri + ", proj=" + Arrays.toString(projection) + ")");
		}
		final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		final SelectionBuilder builder = new SelectionBuilder();
		final int match = sUriMatcher.match(uri);
		switch (match) {
			case MESSAGES_DIR:
			case MESSAGES_ITEM:
				builder.table(Tables.MESSAGES);
				break;
			default:
				throw new IllegalArgumentException("Unknown uri: " + uri);
		}
		builder.where(selection, selectionArgs);
		return builder.query(db, projection, sortOrder);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

		if (LOGV) {
			Log.v(TAG, "update(uri=" + uri + ", values=" + values.toString() + ")");
		}
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final SelectionBuilder builder = buildSimpleSelection(uri);
		int retVal = builder.where(selection, selectionArgs).update(db, values);
		getContext().getContentResolver().notifyChange(uri, null);
		return retVal;
	}
}
