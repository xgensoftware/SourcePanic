package com.slobodastudio.smspanic.activities;

import com.slobodastudio.smspanic.MessageValue;
import com.slobodastudio.smspanic.MessagesListWidgetAdapter;
import com.slobodastudio.smspanic.R;
import com.slobodastudio.smspanic.broadcasts.SmsSenderBroadcast;
import com.slobodastudio.smspanic.utils.ComponentsUtils;
import com.slobodastudio.smspanic.utils.MessageController;
import com.slobodastudio.smspanic.utils.Utils;
import com.slobodastudio.smspanic.widget.PanicWidgetModel;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;

public class WidgetConfigureActivity extends Activity {

	private static String name;
	private static String TAG = WidgetConfigureActivity.class.getSimpleName();
	private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	private ListView messagesList;

	public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
			PanicWidgetModel widgetModel, String numbers, String text, int messageId, String widgetName,
			int clickOnes) {

		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.smspanic_widget);
		Intent intent = new Intent();
		intent.putExtra(SmsSenderBroadcast.EXTRA_MESSAGE_VALUE_ID, messageId);
		Log.v(TAG, "id=" + widgetModel.iid);
		PendingIntent pi;
		if (clickOnes == 1) {
			// intent.setClass(context, SmsSenderBroadcast.class);
			intent.setAction(SmsSenderBroadcast.ACTION);
			pi = PendingIntent.getBroadcast(context, widgetModel.iid, intent, 0);
		} else {
			intent.setClass(context, SosButtonActivity.class);
			pi = PendingIntent.getActivity(context, widgetModel.iid, intent, 0);
		}
		views.setTextViewText(R.id.widget_text, widgetName);
		views.setOnClickPendingIntent(R.id.PanicButton, pi);
		Log.v("smswidgetprovider", "updated");
		appWidgetManager.updateAppWidget(widgetModel.iid, views);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ac_configure_widget);
		LinearLayout rl = (LinearLayout) findViewById(R.id.llConfigureWidget);
		ComponentsUtils.setViewsSettings(rl);
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		}
		messagesList = (ListView) findViewById(R.id.ac_configurewidget_messageslist);
		messagesList.setBackgroundColor(getResources().getColor(android.R.color.transparent));
		messagesList.setCacheColorHint(getResources().getColor(android.R.color.transparent));
		ArrayList<MessageValue> namesList = MessageController.getMessagesForWidget(this);
		if (namesList == null) {
			Toast.makeText(this, "hasn't messages for widget", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		Log.v(TAG, namesList.size() + " size of list");
		MessagesListWidgetAdapter adapter = new MessagesListWidgetAdapter(this, namesList);
		messagesList.setAdapter(adapter);
		messagesList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				MessageValue checkedValue = (MessageValue) messagesList.getAdapter().getItem(arg2);
				if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
					return;
				}
				int clickOnes = Integer.parseInt(checkedValue.getWidgClickOnes());
				name = checkedValue.getWidgName();
				updateAppWidgetLocal(Utils.getStringFromArrayList(checkedValue.getNumbers()), checkedValue
						.getText(), checkedValue.getId(), checkedValue.getWidgName(), clickOnes);
				Intent resultValue = new Intent();
				resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
				setResult(RESULT_OK, resultValue);
				finish();
			}
		});
	}

	private void updateAppWidgetLocal(String numbers, String text, int messageId, String widgetName,
			int clickOnes) {

		PanicWidgetModel modelOfWidget = new PanicWidgetModel(mAppWidgetId, numbers, text, messageId,
				widgetName, clickOnes);
		updateAppWidget(this, AppWidgetManager.getInstance(this), modelOfWidget, numbers, text, messageId,
				widgetName, clickOnes);
		modelOfWidget.savePreferences(this);
	}
}