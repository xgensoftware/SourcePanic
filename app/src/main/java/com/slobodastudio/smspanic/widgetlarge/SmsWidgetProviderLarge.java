package com.slobodastudio.smspanic.widgetlarge;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.slobodastudio.smspanic.activities.LargeWidgetConfigureActivity;
import com.slobodastudio.smspanic.widget.PanicWidgetModel;

public class SmsWidgetProviderLarge extends AppWidgetProvider {

	public void onDelete(final Context context, final int[] appWidgetIds) {

		final int N = appWidgetIds.length;
		for (int i = 0; i < N; ++i) {
			int WidgetId = appWidgetIds[i];
			PanicWidgetModel pwm = PanicWidgetModel.retrieveModel(context, WidgetId);
			pwm.removePrefs(context);
		}
	}

	public void onDisabled(final Context context, final int[] appWidgetIds) {

		PanicWidgetModel.clearAllPreferences(context);
		PackageManager pm = context.getPackageManager();
		pm.setComponentEnabledSetting(new ComponentName("com.slobodastudio.smspanic",
				".widgetlarge.SmsWidgetProviderLarge"), PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
				PackageManager.DONT_KILL_APP);
	}

	@Override
	public void onEnabled(final Context context) {

		// PanicWidgetModel.clearAllPreferences(context);
		// TODO: what is this? comment it, because it does scary things. Sergii
		PackageManager pm = context.getPackageManager();
		pm.setComponentEnabledSetting(new ComponentName("com.slobodastudio.smspanic",
				".widgetlarge.SmsWidgetProviderLarge"), PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
				PackageManager.DONT_KILL_APP);
	}

	@Override
	public void onReceive(final Context context, final Intent intent) {

		final String action = intent.getAction();
		if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {
			final int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
			if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
				onDeleted(context, new int[] { appWidgetId });
			}
		} else {
			super.onReceive(context, intent);
		}
	}

	@Override
	public void onUpdate(final Context context, final AppWidgetManager appWidgetManager,
			final int[] appWidgetIds) {

		final int N = appWidgetIds.length;
		for (int i = 0; i < N; ++i) {
			int WidgetId = appWidgetIds[i];
			updateAppWidget(context, appWidgetManager, WidgetId);
		}
	}

	private void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager,
			final int appWidgetId) {

		PanicWidgetModelLarge pwm = PanicWidgetModelLarge.retrieveModel(context, appWidgetId);
		if (pwm == null) {
			return;
		}
		LargeWidgetConfigureActivity.updateAppWidget(context, appWidgetManager, pwm, pwm.getNum(), pwm
				.getText(), pwm.getMessageId(), pwm.getWidgetName(), pwm.getClickOnes());
	}
}
