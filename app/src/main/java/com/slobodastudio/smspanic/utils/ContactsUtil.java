package com.slobodastudio.smspanic.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;

public class ContactsUtil {

	/** @param context
	 * @param value
	 *            like phone number or id in contacts DB.
	 * @param comparingColumn
	 *            like ContactsContract.Data.DISPLAY_NAME or Phone.NUMBER
	 * @return */
	public static String getContactDistplayName(Context context, String value, String comparingColumn) {

		// Run query
		Uri uri = ContactsContract.Data.CONTENT_URI;
		String[] projection = { ContactsContract.Data.DISPLAY_NAME };
		StringBuilder selection = new StringBuilder();
		selection.append(ContactsContract.Data.MIMETYPE);
		selection.append("='");
		selection.append(Phone.CONTENT_ITEM_TYPE);
		selection.append("' AND ( lower(");
		selection.append(comparingColumn);
		selection.append(") like '%");
		selection.append(value.toLowerCase().trim());
		selection.append("%') ");
		String[] selectionArgs = null;
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
		Cursor cursor = context.getContentResolver().query(uri, projection, selection.toString(),
				selectionArgs, sortOrder);
		if (cursor.moveToFirst()) {
			return cursor.getString(0);
		}
		return "";
	}
}
