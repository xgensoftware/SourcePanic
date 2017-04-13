package com.slobodastudio.smspanic.widget;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class PanicWidgetModel extends APrefSmsWidget {

	private static String F_PHONENUM = "Phone number";
	private static String F_TEXT = "Message text";
	private static String F_MESSAGE = "messageId";
	private static String WIDGET_NAME = "widgetName";
	private static String WIDGET_CLICK_ONES = "widgetClickOnes";
	private static String PANIC_WIDGET_PROVIDER_NAME = "com.slobodastudio.smspanic.widget.SmsWidgetProvider";
	private static String tag = "PanicWidgetModel";
	private String phoneNum = "900838328";
	private String Text = "I'm in police.Help";
	private int messageId;
	private String widgetName;

	public String getWidgetName() {

		return widgetName;
	}

	public int getClickOnes() {

		return clickOnes;
	}

	private int clickOnes;

	public PanicWidgetModel(int instanceId) {

		super(instanceId);
	}

	public PanicWidgetModel(int instanceId, String inNumber, String mesText, int messageId,
			String widgetName, int clickOnes) {

		super(instanceId);
		phoneNum = inNumber;
		Text = mesText;
		this.messageId = messageId;
		this.widgetName = widgetName;
		this.clickOnes = clickOnes;
	}

	public static void clearAllPreferences(Context ctx) {

		APrefSmsWidget.clearAllPreferences(ctx, PANIC_WIDGET_PROVIDER_NAME);
	}

	public static PanicWidgetModel retrieveModel(Context ctx, int widgetId) {

		PanicWidgetModel m = new PanicWidgetModel(widgetId);
		boolean found = m.retrievePrefs(ctx);
		return found ? m : null;
	}

	public String getNum() {

		return phoneNum;
	}

	public int getMessageId() {

		return messageId;
	}

	@Override
	public String getPrefName() {

		return PANIC_WIDGET_PROVIDER_NAME;
	}

	@Override
	public Map<String, String> getPrefsToSave() {

		Map<String, String> map = new HashMap<String, String>();
		map.put(F_PHONENUM, phoneNum);
		map.put(F_TEXT, Text);
		map.put(F_MESSAGE, String.valueOf(messageId));
		map.put(WIDGET_CLICK_ONES, String.valueOf(clickOnes));
		map.put(WIDGET_NAME, widgetName);
		return map;
	}

	public String getText() {

		return Text;
	}

	@Override
	public void init() {

	}

	@Override
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

	public void setNum(String innum) {

		phoneNum = innum;
	}

	public void setText(String inText) {

		Text = inText;
	}

	@Override
	public void setValueForPref(String key, String value) {

		if (key.equals(getStoredKeyForFieldName(PanicWidgetModel.F_PHONENUM))) {
			phoneNum = value;
			return;
		}
		if (key.equals(getStoredKeyForFieldName(PanicWidgetModel.F_TEXT))) {
			Text = value;
			return;
		}
		if (key.equals(getStoredKeyForFieldName(PanicWidgetModel.F_MESSAGE))) {
			messageId = Integer.parseInt(value);
			return;
		}
		if (key.equals(getStoredKeyForFieldName(PanicWidgetModel.WIDGET_CLICK_ONES))) {
			clickOnes = Integer.parseInt(value);
			return;
		}
		if (key.equals(getStoredKeyForFieldName(PanicWidgetModel.WIDGET_NAME))) {
			widgetName = value;
			return;
		}
	}

	private void removePref(SharedPreferences.Editor prefs, String key) {

		String newkey = getStoredKeyForFieldName(key);
		prefs.remove(newkey);
	}
}
