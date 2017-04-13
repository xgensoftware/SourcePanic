/*
 * Copyright 2012 sloboda-studio.com
 */
package com.slobodastudio.smspanic.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/** The Helper Class for SharedPreference. Helps with save/read operations. */
public class PreferenceUtil {

	/** Retrieve a boolean value from a preference.
	 * 
	 * @param context
	 *            The {@link Context} to get {@link SharedPreferences}.
	 * @param preferenceKey
	 *            The name of the preference to retrieve.
	 * @param defaultValue
	 *            Value to return if this preference does not exist.
	 * @return Returns the preference value if it exists, or defaultValue. Throws ClassCastException if there
	 *         is a preference with this name that is not a boolean.
	 * @throws ClassCastException */
	public static boolean getBoolean(Context context, String preferenceKey, boolean defaultValue)
			throws ClassCastException {

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getBoolean(preferenceKey, defaultValue);
	}

	public static int getInt(Context context, String preferenceKey, int defaultValue) {

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getInt(preferenceKey, defaultValue);
	}

	/** Retrieve a long value from a preference.
	 * 
	 * @param context
	 *            The {@link Context} to get {@link SharedPreferences}.
	 * @param preferenceKey
	 *            The name of the preference to retrieve.
	 * @param defaultValue
	 *            Value to return if this preference does not exist.
	 * @return Returns the preference value if it exists, or defaultValue. Throws ClassCastException if there
	 *         is a preference with this name that is not a boolean.
	 * @throws ClassCastException */
	public static long getLong(Context context, String preferenceKey, long defaultValue)
			throws ClassCastException {

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getLong(preferenceKey, defaultValue);
	}

	/** Retrieve a String value from a preference.
	 * 
	 * @param context
	 *            The {@link Context} to get {@link SharedPreferences}.
	 * @param preferenceKey
	 *            The name of the preference to retrieve.
	 * @return Returns the preference value if it exists, or throw IllegalArgumentException. Throws
	 *         ClassCastException if there is a preference with this name that is not a String.
	 * @throws IllegalArgumentException
	 * @throws ClassCastException */
	public static String getString(Context context, String preferenceKey) throws IllegalArgumentException,
			ClassCastException {

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		// if (!sp.contains(preferenceKey)) {
		// throw new IllegalArgumentException("Preference value need to be saved before read it.");
		// } else {
		return sp.getString(preferenceKey, "");
		// }
	}

	/** Retrieve a String value from a preference.
	 * 
	 * @param context
	 *            The {@link Context} to get {@link SharedPreferences}.
	 * @param preferenceKey
	 *            The name of the preference to retrieve.
	 * @param defaultValue
	 *            Value to return if this preference does not exist.
	 * @return Returns the preference value if it exists, or defaultValue. Throws ClassCastException if there
	 *         is a preference with this name that is not a String.
	 * @throws ClassCastException */
	public static String getString(Context context, String preferenceKey, String defaultValue)
			throws ClassCastException {

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getString(preferenceKey, defaultValue);
	}

	/** Set a boolean value in the {@link SharedPreferences}.
	 * 
	 * @param context
	 *            The {@link Context} to get {@link SharedPreferences}.
	 * @param preferenceKey
	 *            The name of the preference to modify.
	 * @param value
	 *            The new value for the preference.
	 * @return Returns true if the new values were successfully written to persistent storage. */
	public static boolean putBoolean(Context context, String preferenceKey, boolean value) {

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editorSp = sp.edit();
		editorSp.putBoolean(preferenceKey, value);
		return editorSp.commit();
	}

	/** Set a int value in the {@link SharedPreferences}.
	 * 
	 * @param context
	 *            The {@link Context} to get {@link SharedPreferences}.
	 * @param preferenceKey
	 *            The name of the preference to modify.
	 * @param value
	 *            The new value for the preference.
	 * @return Returns true if the new values were successfully written to persistent storage. */
	public static boolean putInt(Context context, String preferenceKey, int value) {

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editorSp = sp.edit();
		editorSp.putInt(preferenceKey, value);
		return editorSp.commit();
	}

	/** Set a long value in the {@link SharedPreferences}.
	 * 
	 * @param context
	 *            The {@link Context} to get {@link SharedPreferences}.
	 * @param preferenceKey
	 *            The name of the preference to modify.
	 * @param value
	 *            The new value for the preference.
	 * @return Returns true if the new values were successfully written to persistent storage. */
	public static boolean putLong(Context context, String preferenceKey, long value) {

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editorSp = sp.edit();
		editorSp.putLong(preferenceKey, value);
		return editorSp.commit();
	}

	/** Set a String value in the {@link SharedPreferences}.
	 * 
	 * @param context
	 *            The {@link Context} to get {@link SharedPreferences}.
	 * @param preferenceKey
	 *            The name of the preference to modify.
	 * @param value
	 *            The new value for the preference.
	 * @return Returns true if the new values were successfully written to persistent storage. */
	public static boolean putString(Context context, String preferenceKey, String value) {

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editorSp = sp.edit();
		editorSp.putString(preferenceKey, value);
		return editorSp.commit();
	}

	/** A private Constructor prevents class from instantiating. */
	private PreferenceUtil() {

		throw new UnsupportedOperationException("Class is prevented from instantiation");
	}
}
