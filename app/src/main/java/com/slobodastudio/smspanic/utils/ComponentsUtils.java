package com.slobodastudio.smspanic.utils;

import com.slobodastudio.smspanic.R;
import com.slobodastudio.smspanic.activities.MessagesListActivity;
import com.slobodastudio.smspanic.activities.PreferenceActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ComponentsUtils {

	public static void setViewsSettings(View v) {

		Typeface tf = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/comicSans.ttf");
		TextView text = (TextView) v.findViewById(R.id.tvHeader);
		if (text != null) {
			text.setTypeface(tf);
			text.setTextColor(Color.WHITE);
		}
		ImageView share = (ImageView) v.findViewById(R.id.shareImage);
		if (share != null) {
			share.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					share(v.getContext());
				}
			});
		}
		ImageView settings = (ImageView) v.findViewById(R.id.settingsImage);
		if (settings != null) {
			settings.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					startSettings(v.getContext());
				}
			});
		}
		ImageView mail = (ImageView) v.findViewById(R.id.mailImage);
		if (mail != null) {
			mail.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					emailUs(v.getContext());
				}
			});
		}
	}

	public static void share(Context context) {

		final Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		String subject = context.getString(R.string.shareSubject);
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		String text = context.getString(R.string.shareText);
		intent.putExtra(Intent.EXTRA_TEXT, text);
		context.startActivity(Intent.createChooser(intent, context.getString(R.string.share)));
	}

	public static void startSettings(Context context) {

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		intent.setClassName(context, PreferenceActivity.class.getName());
		context.startActivity(intent);
	}

	public static void emailUs(Context context) {

		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		String[] recipients = new String[] { MessagesListActivity.EMAIL, "", };
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, context
				.getString(R.string.text_email_message));
		emailIntent.setType("text/plain");
		context.startActivity(Intent.createChooser(emailIntent, context.getString(R.string.text_send_mail)));
	}

	public static void startInstruction(Context context) {

		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://androids.ru/i/ipanic.htm"));
		context.startActivity(browserIntent);
	}

	public static void otherApp(Context context) {

		Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:Globalapps+R"));
		marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		context.startActivity(marketIntent);
	}

	public static void upgrade(Context context) {

		Intent upgrade = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.smsplan2"));
		upgrade.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		context.startActivity(upgrade);
	}
}
