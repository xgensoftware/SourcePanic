package com.slobodastudio.smspanic.database;

import android.net.Uri;

public class SmsColumns {

	public static final String ADDRESS = "address";
	public static final String BODY = "body";
	public static final String DATE = "date";
	public static final String NAME = "name";
	public static final String SMS_ID = "_id";
	public static final String TYPE = "type";
	public static final Uri SMS_CONTENT_URI = Uri.parse("content://sms");

	/** A private Constructor prevents class from instantiating. */
	private SmsColumns() throws UnsupportedOperationException {

		throw new UnsupportedOperationException("Class is prevented from instantiation");
	}
}
