package com.slobodastudio.smspanic.widget;

import java.util.Map;

public interface ISmsPanicWidgetContract {

	public String getPrefName();

	public Map<String, String> getPrefsToSave();

	public void init();

	public void setValueForPref(String key, String value);
}
