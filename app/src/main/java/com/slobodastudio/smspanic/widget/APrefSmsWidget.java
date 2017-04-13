package com.slobodastudio.smspanic.widget;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

public abstract class APrefSmsWidget implements ISmsPanicWidgetContract {

	private static String tag = "AWidgetModel";
	public int iid;

	public APrefSmsWidget(int instanceId) {

		iid = instanceId;
	}

	public static void clearAllPreferences(Context context, String prefname) {

		SharedPreferences prefs = context.getSharedPreferences(prefname, 0);
		SharedPreferences.Editor prefsEdit = prefs.edit();
		prefsEdit.clear();
		prefsEdit.commit();
	}

	@Override
	public abstract String getPrefName();

	@Override
	public Map<String, String> getPrefsToSave() {

		return null;
	}

	@Override
	public abstract void init();

	public void removePrefs(Context context) {

		Map<String, String> keyValuePairs = getPrefsToSave();
		if (keyValuePairs == null) {
			return;
		}
		SharedPreferences.Editor prefs = context.getSharedPreferences(getPrefName(), 0).edit();
		for (String key : keyValuePairs.keySet()) {
			removePref(prefs, key);
		}
		prefs.commit();
	}

	public boolean retrievePrefs(Context ctx) {

		SharedPreferences prefs = ctx.getSharedPreferences(getPrefName(), 0);
		Map<String, ?> keyValuePairs = prefs.getAll();
		boolean prefFound = false;
		for (String key : keyValuePairs.keySet()) {
			if (isItMyPref(key) == true) {
				String value = (String) keyValuePairs.get(key);
				setValueForPref(key, value);
				prefFound = true;
			}
		}
		return prefFound;
	}

	public void savePreferences(Context context) {

		Map<String, String> keyValuePairs = getPrefsToSave();
		if (keyValuePairs == null) {
			return;
		}
		SharedPreferences.Editor prefs = context.getSharedPreferences(getPrefName(), 0).edit();
		for (String key : keyValuePairs.keySet()) {
			String value = keyValuePairs.get(key);
			savePref(prefs, key, value);
		}
		prefs.commit();
	}

	@Override
	public void setValueForPref(String key, String value) {

		return;
	}

	private boolean isItMyPref(String keyname) {

		if (keyname.indexOf("_" + iid) > 0) {
			return true;
		}
		return false;
	}

	private void removePref(SharedPreferences.Editor prefs, String key) {

		String newkey = getStoredKeyForFieldName(key);
		prefs.remove(newkey);
	}

	private void savePref(SharedPreferences.Editor prefs, String key, String value) {

		String newkey = getStoredKeyForFieldName(key);
		prefs.putString(newkey, value);
	}

	protected String getStoredKeyForFieldName(String fieldName) {

		return fieldName + "_" + iid;
	}
}
