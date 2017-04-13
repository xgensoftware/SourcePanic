package com.slobodastudio.smspanic.media;

import com.slobodastudio.smspanic.R;
import com.slobodastudio.smspanic.utils.MessageController;
import com.slobodastudio.smspanic.utils.PreferenceUtil;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class EmailSender implements Runnable {

	private Context mContext;
	private HashSet<String> fileNames;
	private ArrayList<String> emails;

	public EmailSender(Context context, HashSet<String> fileNames, String email) {

		mContext = context;
		emails = MessageController.getStringsArrayListFromStr(email);
		this.fileNames = fileNames;
	}

	public void sendMail(Context context, String fileName) {

		Mail m = new Mail(getAddressFrom(), getPassword());
		m.setSmtpHost(getSmtpAddress());
		m.setSmtpPort(getSmtpPort());
		String[] toArr = emails.toArray(new String[emails.size()]);// { "makarenko04@yandex.ru",
																	// "jekanaloyko@gmail.com" };
		m.setTo(toArr);
		m.setFrom(getAddressFrom());
		m.setSubject("send from SmsPanic application");
		m.setBody("Email body.");
		try {
			m.addAttachment(fileName);
			if (m.send()) {
                Log.d("EmailSender", "Email was sent successfully.");
			} else {
                Log.d("EmailSender", "Email was not sent.");
			}
		} catch (Exception e) {
			Log.e("MailApp", "Could not send email", e);
		}
	}

	private String getAddressFrom() {

		String addressKey = mContext.getString(R.string.pref_email_address_key);
		return PreferenceUtil.getString(mContext, addressKey).trim();
	}

	private String getSmtpPort() {

		String smtpPortKey = mContext.getString(R.string.pref_email_smtp_port_key);
		return PreferenceUtil.getString(mContext, smtpPortKey).trim();
	}

	private String getSmtpAddress() {

		String smtpAddressKey = mContext.getString(R.string.pref_email_smtp_server_key);
		return PreferenceUtil.getString(mContext, smtpAddressKey).trim();
	}

	private String getPassword() {

		String passwordKey = mContext.getString(R.string.pref_email_password_key);
		return PreferenceUtil.getString(mContext, passwordKey).trim();
	}

	@Override
	public void run() {

		Log.v(EmailSender.class.getSimpleName(), "emailsender run");
		Iterator<String> iterator = fileNames.iterator();
		while (iterator.hasNext()) {
			String currentFile = iterator.next();
			File file = new File(currentFile);
			if (file.exists()) {
				sendMail(mContext, currentFile);
				Log.v(EmailSender.class.getSimpleName(), fileNames.size() + " _ " + currentFile + "  "
						+ Thread.currentThread().getId());
				// file.delete();
			}
		}
	}
}
