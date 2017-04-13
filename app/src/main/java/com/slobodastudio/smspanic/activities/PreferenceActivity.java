package com.slobodastudio.smspanic.activities;

import com.slobodastudio.smspanic.R;
import com.slobodastudio.smspanic.utils.ComponentsUtils;
import com.slobodastudio.smspanic.utils.MessageController;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class PreferenceActivity extends android.preference.PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	private static final String ACCOUNT_TYPE = "com.google";
	private final String PREFKEY_STROB_ONES = "pref_ones_minutes_key";
	private final String PREFKEY_STROB_MINUTES = "pref_strob_minutes_key";

	@Override
	public void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		setContentView(R.layout.preferences_layout);
		setTheme(R.style.Theme_WhiteText);
		getListView().setBackgroundColor(getResources().getColor(android.R.color.transparent));
		getListView().setCacheColorHint(getResources().getColor(android.R.color.transparent));
		LinearLayout rl = (LinearLayout) findViewById(R.id.llPref);
		ComponentsUtils.setViewsSettings(rl);
		rl.findViewById(R.id.settingsImage).setClickable(false);
		View parent = (View) getListView().getParent();
		parent.setBackgroundResource(R.drawable.background_blue);
		// checkUserMailAddress();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener((OnSharedPreferenceChangeListener) this);
	}

	@Override
	protected void onStart() {

		super.onStart();
		// checkUserMailAddress();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

		if ((PREFKEY_STROB_ONES.equals(key)) || (PREFKEY_STROB_MINUTES.equals(key))) {
			updateDB();
		}
	}

	private void updateDB() {

		MessageController.updateAllMessages(PreferenceActivity.this);
	}

	private void checkUserMailAddress() {

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		String currentAccount = settings.getString(getString(R.string.mail_default_account), "");
		String defaultAccount = getString(R.string.mail_default_account);
		String userMainAccount = getUserMailAddress();
		if (currentAccount.length() == 0 || defaultAccount.equals(currentAccount.trim())) {
			if (userMainAccount.length() > 0) {
				setGooglePreferences(userMainAccount);
			}
		}
	}

	private String getUserMailAddress() {

		Account[] accounts = AccountManager.get(getApplicationContext()).getAccounts();
		for (Account account : accounts) {
			if (account.type.equals(ACCOUNT_TYPE)) {
				return account.name;
			}
		}
		return "";
	}

	private void setGooglePreferences(String mailAddress) {

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(getString(R.string.pref_email_address_key), mailAddress);
		editor.putString(getString(R.string.pref_email_password_key), "");
		editor.putString(getString(R.string.pref_email_smtp_server_key),
				getString(R.string.mail_gmail_smtp_server));
		editor.commit();
	}
	
	private void setRotationPreference(){
		Spinner sp = new Spinner(getApplicationContext());
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		
		SharedPreferences.Editor editor = settings.edit();
		
		editor.putString(getString(R.string.pref_email_address_key), "");
		editor.putString(getString(R.string.pref_email_password_key), "");
		editor.putString(getString(R.string.pref_email_smtp_server_key),
				getString(R.string.mail_gmail_smtp_server));
		editor.commit();
	}
}