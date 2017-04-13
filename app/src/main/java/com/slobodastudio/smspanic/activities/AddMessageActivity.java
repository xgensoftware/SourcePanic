package com.slobodastudio.smspanic.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.slobodastudio.smspanic.MessageValue;
import com.slobodastudio.smspanic.R;
import com.slobodastudio.smspanic.utils.ComponentsUtils;
import com.slobodastudio.smspanic.utils.ContactsUtil;
import com.slobodastudio.smspanic.utils.MessageController;
import com.slobodastudio.smspanic.utils.PreferenceUtil;
import com.slobodastudio.smspanic.utils.Utils;

public class AddMessageActivity extends Activity {

	public static final int PICK_CONTACTS_ID = 100;
	private static final String TAG = AddMessageActivity.class.getSimpleName();
	// public static final int WIDGET_NAME_LENGHT = 12;
	public static final int MESSAGE_NAME_LENGHT = 20;
	private int openMessageId;
	private EditText namePanicField;
	private EditText destinatNumbersField;
	private EditText textMessageField;
	private RadioGroup clickOnesGroup;
	private Button saveExitButton;
	private Button pickContactsButton;
	private RadioButton widgetRadioOneClick;
	private RadioButton widgetRadioTwoClick;
	private CheckBox widgetEnable;
	private CheckBox sendMediaEnable;
	private CheckBox sendEmailEnable;
	private EditText sendMediaOnesField;
	private EditText sendMediaFrequencyField;
	private EditText destEmailField;
	private RadioGroup recordDeviceChooseGroup;
	private RadioGroup recordTypeChooseGroup;
	private RadioButton dictophoneRadioEnable;
	private RadioButton cameraRadioEnable;
	private RadioButton recordingFullScreen;
	private RadioButton recordingNotificationArea;
	public static final String MODIFY_ACTIVITY_MODE_KEY = "openActivityModeKey";
	public static final String STATE_ACTIVITY_TRANSFER_KEY = "stateActivityTransferKey";
	private boolean editmode = false;
	private CheckBox hideFromHistoryEnable;
	private ArrayList<String> numbers = new ArrayList<String>();
	private LinearLayout numbersContainer;
	private OnClickListener deleteNumber;
	private int editingTemplateId;
	private boolean isEditMessageActive;
	private CheckBox cbStrobSos;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ac_add_message);
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.RelativeLayout1);
		ComponentsUtils.setViewsSettings(rl);
		widgetEnable = (CheckBox) findViewById(R.id.ac_addnewmessage_addwidgetcheckbox);
		clickOnesGroup = (RadioGroup) findViewById(R.id.ac_addmessage_radio_ones_click_group);
		namePanicField = (EditText) findViewById(R.id.ac_addmessage_name_panic);
		destinatNumbersField = (EditText) findViewById(R.id.ac_addmessage_numbers_panic);
		textMessageField = (EditText) findViewById(R.id.ac_addmessage_text_panic);
		saveExitButton = (Button) findViewById(R.id.ac_addnewmessage_saveexitbutton);
		widgetRadioOneClick = (RadioButton) clickOnesGroup
				.findViewById(R.id.ac_addmessage_radiooneclick);
		widgetRadioTwoClick = (RadioButton) clickOnesGroup
				.findViewById(R.id.ac_addmessage_radiotwoclick);
		pickContactsButton = (Button) findViewById(R.id.ac_addmessage_phonebook_button);
		hideFromHistoryEnable = (CheckBox) findViewById(R.id.ac_addnewmessage_hidefromhistorycheckbox);
		numbersContainer = (LinearLayout) findViewById(R.id.ac_addmessage_container_phone_numbers);
		cbStrobSos = (CheckBox) findViewById(R.id.cbStrobSos);
		/** additional parameters initialize below */
		sendMediaEnable = (CheckBox) findViewById(R.id.ac_addnewmessage_record_enable);
		sendEmailEnable = (CheckBox) findViewById(R.id.ac_addnewmessage_send_email_enable);
		sendMediaOnesField = (EditText) findViewById(R.id.ac_addnewmessage_send_media_ones);
		sendMediaFrequencyField = (EditText) findViewById(R.id.ac_addnewmessage_send_media_frequency);
		destEmailField = (EditText) findViewById(R.id.ac_addmessage_send_email_field);
		recordDeviceChooseGroup = (RadioGroup) findViewById(R.id.ac_addmessage_radio_record_device_chooser);
		recordTypeChooseGroup = (RadioGroup) findViewById(R.id.ac_addmesage_radio_show_record_chooser);
		dictophoneRadioEnable = (RadioButton) recordDeviceChooseGroup
				.findViewById(R.id.ac_addmessage_radio_dictophone);
		cameraRadioEnable = (RadioButton) recordDeviceChooseGroup
				.findViewById(R.id.ac_addmessage_radio_camera);
		recordingFullScreen = (RadioButton) recordTypeChooseGroup
				.findViewById(R.id.ac_addmessage_radio_full_screen);
		recordingNotificationArea = (RadioButton) recordTypeChooseGroup
				.findViewById(R.id.ac_addmessage_radio_notification_area);
		/*
		 * disable parameters default
		 */
		recordingFullScreen.setEnabled(false);
		recordingNotificationArea.setEnabled(false);
		setAdditionalParametersEnabled(sendMediaEnable.isChecked());
		setWidgetParametersEnable(widgetEnable.isChecked());
		// checkBusyVolumeButtons();
		/*
		 * initialize onActions listeners
		 */
		pickContactsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(AddMessageActivity.this,
						PickContactsActivity.class);
				if (destinatNumbersField.getText().length() > 0) {
					numbers = Utils.getStringsArrayListFromStr(v.getContext(),
							destinatNumbersField.getText().toString().trim());
					intent.putExtra(PickContactsActivity.EXTRA_COMPARING_LIST,
							numbers);
				}
				intent.putExtra(PickContactsActivity.EXTRA_COMPARING_COLUMN,
						ContactsContract.Data.DATA1);
				intent.putExtra(PickContactsActivity.EXTRA_RETURN_COLUMN,
						ContactsContract.Data.DATA1);
				startActivityForResult(intent, PICK_CONTACTS_ID);
			}
		});
		saveExitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (checkFillingFields()) {
					saveValues(v.getContext());
					
					finish();
				}
			}
		});
		sendMediaEnable
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {

						setAdditionalParametersEnabled(sendMediaEnable
								.isChecked());
						if(isChecked){
							dictophoneRadioEnable.setChecked(true);
						}
					}
				});
		dictophoneRadioEnable
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton compoundButton,
							boolean isChecked) {

						setVideoTypeParameters(!isChecked);
					}
				});
		cameraRadioEnable
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton compoundButton,
							boolean isChecked) {

						setVideoTypeParameters(isChecked);
						if(isChecked)
							recordingFullScreen.setChecked(true);
					}
				});
		widgetEnable.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				setWidgetParametersEnable(widgetEnable.isChecked());
			}
		});
		sendEmailEnable
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0,
							boolean isChecked) {

						if (isChecked) {
							String email = PreferenceUtil.getString(
									AddMessageActivity.this,
									getString(R.string.pref_email_address_key));
							StringBuilder sb = new StringBuilder();
							for(char a : email.toCharArray()){
								if(a != ' '){
									sb.append(a);
								}
							}
							
							String pass = PreferenceUtil
									.getString(
											AddMessageActivity.this,
											getString(R.string.pref_email_password_key));
							if (!android.util.Patterns.EMAIL_ADDRESS.matcher(
									sb).matches()
									|| pass.length() < 1) {
								sendEmailEnable.setChecked(false);
								Toast.makeText(AddMessageActivity.this,
										R.string.ac_addmessage_email_failed,
										Toast.LENGTH_LONG).show();
							}

						}
					}
				});
		/*
		 * get intent with launch mode (edit or create)
		 */
		Intent intent = getIntent();
		if (intent.getBooleanExtra(MODIFY_ACTIVITY_MODE_KEY, false)) {
			editmode = true;
			MessageValue currentState = (MessageValue) intent
					.getSerializableExtra(STATE_ACTIVITY_TRANSFER_KEY);
			Log.v(TAG, currentState.getName() + " name");
			openMessageId = currentState.getId();
			fillFields(currentState);
		}
		deleteNumber = new OnClickListener() {

			@Override
			public void onClick(View v) {

				editingTemplateId = v.getId();
				showDialog(v.getId());
			}
		};
		destinatNumbersField
				.setOnFocusChangeListener(new OnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {

						if (!hasFocus) {
							ArrayList<String> tempNumbers = Utils
									.getStringsArrayListFromStr(v.getContext(),
											destinatNumbersField.getText()
													.toString().trim());
							ArrayList<String> newNumbers = new ArrayList<String>();
							for (String currentNumber : tempNumbers) {
								newNumbers.add(currentNumber);
							}
							numbers = newNumbers;
							String f = destinatNumbersField.getText()
									.toString();
							
							
							char l;
							
							if(f.length() > 0)
								l = f.charAt(f.length() - 1);
							else
								l = 0;
							if (f.length() != 0)
								if (l != ',')
									destinatNumbersField
											.setText(destinatNumbersField
													.getText().toString() + ",");
							
							refreshNumbersList();
							
						}
					}
				});

		refreshNumbersList();
	}

	/**
	 * filling values in activity at edit existing messages
	 * 
	 * @param currentState
	 *            - message value for edit
	 */
	private void fillFields(MessageValue currentState) {

		namePanicField.setText(currentState.getName());
		destinatNumbersField.setText(Utils.getStringFromArrayList(currentState
				.getNumbers()));
		numbers = currentState.getNumbers();
		refreshNumbersList();
		textMessageField.setText(currentState.getText());
		isEditMessageActive = currentState.isChecked();
		setlaunchMode(currentState);
		destEmailField.setText(currentState.getEmails());
		if (currentState.getMediaRecorder() != 0) {
			sendMediaEnable.setChecked(true);
			recordTypeChooseGroup.setEnabled(currentState
					.isRecordingTypeEnabled());
			switch (currentState.getMediaRecorder()) {
			case MessageValue.MEDIA_RECORDER_CAMERA:
				dictophoneRadioEnable.setChecked(false);
				cameraRadioEnable.setChecked(true);
				setRecordTypeClick(currentState.getRecordingType());
				break;
			case MessageValue.MEDIA_RECORDER_MIC:
				cameraRadioEnable.setChecked(false);
				dictophoneRadioEnable.setChecked(true);
				dictophoneRadioEnable.invalidate();
				break;
			}
			sendEmailEnable.setChecked(currentState.isEmailEnable());
			destEmailField.setText(currentState.getEmails());
			sendMediaFrequencyField.setText(String.valueOf((currentState
					.getMediaSendFrequency() /*
											 * divide to 1000
											 */)));
			sendMediaOnesField.setText(String.valueOf(currentState
					.getMediaSendOnes()));
		}
		hideFromHistoryEnable.setChecked(currentState.isHide());
		cbStrobSos.setChecked(currentState.isStrobEnabled());
		Log.d(TAG, "message=" + currentState);
	}

	private void setlaunchMode(MessageValue currentState) {

		if (currentState.isLaunchModeWidget()) {
			widgetEnable.setChecked(true);
			setWidgetClickOnes(currentState.getWidgClickOnes());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PICK_CONTACTS_ID && resultCode == RESULT_OK) {
			ArrayList<String> pickedContacts = ((ArrayList<String>) data
					.getSerializableExtra(PickContactsActivity.EXTRA_RETURN_LIST));
			// *******
			numbers = pickedContacts;
			StringBuilder sb = new StringBuilder();
			for (String str : pickedContacts) {
				sb.append(str);
				sb.append(", ");
			}
			destinatNumbersField.setText(sb.toString());
			refreshNumbersList();
		}
	}

	private void refreshNumbersList() {
		numbersContainer.removeAllViews();

		for (int i = 0; i < numbers.size(); i++) {
			LinearLayout temp = getNumberView(i, numbers.get(i),
					ContactsUtil.getContactDistplayName(getBaseContext(),
							numbers.get(i), Phone.NUMBER));
			temp.setOnClickListener(deleteNumber);
			numbersContainer.addView(temp);
		}
	}

	private LinearLayout getNumberView(int viewId, String number, String name) {

		LayoutInflater inflater = LayoutInflater.from(this);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.contact_item, null);
		TextView nameText = (TextView) layout.findViewById(R.id.contact_name);
		TextView numberText = (TextView) layout
				.findViewById(R.id.contact_number);
		numberText.setText(number);
		nameText.setText(name);
		layout.setId(viewId);
		layout.setBackgroundResource(R.drawable.textview_background);
		return layout;
	}

	private void saveValues(Context context) {

		String messageName = namePanicField.getText().toString().trim();
		String destNumbers = destinatNumbersField.getText().toString().trim();
		String textMessage = textMessageField.getText().toString().trim();
		String widgetName = messageName;
		numbers = Utils.getStringsArrayListFromStr(context, destNumbers);
		MessageValue newMessage = new MessageValue();
		getLaunchMode(newMessage);
		newMessage.setName(messageName);
		newMessage.setNumbers(numbers);
		newMessage.setWidgName(widgetName);
		newMessage.setText(textMessage);
		newMessage.setWidgClickOnes(getWidgetClickOnes());
		// ******STROB
		SharedPreferences settingsStrob = PreferenceManager
				.getDefaultSharedPreferences(this);
		int timesOnesInMinutes = Integer.parseInt(settingsStrob.getString(
				getString(R.string.pref_ones_minutes_key), "1"));
		int timesInMinutes = Integer.parseInt(settingsStrob.getString(
				getString(R.string.pref_strob_minutes_key), "1"));
		newMessage.setStrobEnabled(cbStrobSos.isChecked());
		newMessage.setTimesInMinutesId(timesOnesInMinutes);
		newMessage.setTimesId(timesInMinutes);
		// -----end STrob
		if (hideFromHistoryEnable.isChecked()) {
			newMessage.setHide(true);
		}
		/*
		 * save fields for media (camera, mic, email etc)
		 */
		if (sendMediaEnable.isChecked()) {
			if (cameraRadioEnable.isChecked()) {
				newMessage.setRecordingTypeEnabled(true);
				newMessage.setRecordingType(getRecordTypeClick());
			} else {
				newMessage.setRecordingTypeEnabled(false);
			}
			newMessage = saveChooseDevice(newMessage);
			if ((sendMediaOnesField.getText().length() != 0)
					&& (sendMediaFrequencyField.getText().length() != 0)) {
				newMessage = saveMediaFieldsValue(newMessage);
			}
		}
		/*
		 * choose action - save new or update
		 */
		if (editmode) {
			newMessage.setId(openMessageId);
			newMessage.setChecked(isEditMessageActive);
			MessageController.updateMessageInDb(this, newMessage);
		} else {
			newMessage.setChecked(true);
			MessageController.saveMessageInDb(this, newMessage);
		}
	}

	private MessageValue saveChooseDevice(MessageValue mv) {

		int chooseId = recordDeviceChooseGroup.getCheckedRadioButtonId();
		RadioButton checked = (RadioButton) recordDeviceChooseGroup
				.findViewById(chooseId);
		int checkedText = (int) checked.getId();
		if (checkedText == dictophoneRadioEnable.getId()) {
			mv.setMediaRecorder(MessageValue.MEDIA_RECORDER_MIC);
		} else if (checkedText == cameraRadioEnable.getId()) {
			mv.setMediaRecorder(MessageValue.MEDIA_RECORDER_CAMERA);
			chooseId = recordTypeChooseGroup.getCheckedRadioButtonId();
			checked = (RadioButton) recordTypeChooseGroup
					.findViewById(chooseId);
			if (checked.equals(recordingNotificationArea)) {
				// notification area
				mv.setRecordingType(0);
			} else {
				// full screen
				mv.setRecordingType(1);
			}
		} else {
			return null;
		}
		return mv;
	}

	private MessageValue saveMediaFieldsValue(MessageValue mv) {

		if (sendEmailEnable.isChecked()) {
			String destEmails = destEmailField.getText().toString().trim();
			mv.setEmails(destEmails);
			mv.setEmailEnable(sendEmailEnable.isChecked());
		}
		int mediaSentOnes = Integer.parseInt(sendMediaOnesField.getText()
				.toString().trim());
		mv.setMediaSendOnes(mediaSentOnes);
		int sendMediaFrequency = Integer.parseInt(sendMediaFrequencyField
				.getText().toString().trim());
		mv.setMediaSendFrequency(sendMediaFrequency);
		return mv;
	}

	private String getWidgetClickOnes() {

		int checked = clickOnesGroup.getCheckedRadioButtonId();
		try {
			if (checked == widgetRadioOneClick.getId()) {
				return "1";
			} else if (checked == widgetRadioTwoClick.getId()) {
				return "2";
			} else {
				return "0";
			}
		} catch (NullPointerException e) {
			return "0";
		}
	}

	private void setWidgetClickOnes(String onesStr) {

		int ones = Integer.parseInt(onesStr);
		switch (ones) {
		case 1:
			widgetRadioTwoClick.setChecked(false);
			widgetRadioOneClick.setChecked(true);
			break;
		case 2:
			widgetRadioOneClick.setChecked(false);
			widgetRadioTwoClick.setChecked(true);
			break;
		}
	}

	private int getRecordTypeClick() {

		if (recordingFullScreen.isChecked()) {
			// full screen
			return new Integer(1);
		}
		if (recordingNotificationArea.isChecked()) {
			// notification area
			return new Integer(0);
		}
		return new Integer(0);
	}

	private void setRecordTypeClick(int type) {

		switch (type) {
		case 1:
			recordingNotificationArea.setChecked(false);
			recordingFullScreen.setChecked(true);
			break;
		case 0:
			recordingFullScreen.setChecked(false);
			recordingNotificationArea.setChecked(true);
			break;
		}
	}

	private int getLaunchMode(MessageValue newMessage) {

		int launchMode = 0;
		if (widgetEnable.isChecked()) {
			newMessage.setLaunchModeWidget(true);
		}
		return launchMode;
	}

	private boolean checkFillingFields() {

		if (namePanicField.getText().length() == 0) {
			return false;
		}
		String destNumbers = destinatNumbersField.getText().toString().trim();
		ArrayList<String> numbersBuf = Utils.getStringsArrayListFromStr(
				getBaseContext(), destNumbers);
		if (numbersBuf.size() < 1) {
			Toast.makeText(this, "Enter phone number(s)", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (widgetEnable.isChecked() && namePanicField.length() == 0) {
			Toast.makeText(this, "Fill widget name, please", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		if (namePanicField.length() > MESSAGE_NAME_LENGHT) {
			Toast.makeText(
					this,
					"widget name more than " + MESSAGE_NAME_LENGHT
							+ " characters", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (namePanicField.length() > MESSAGE_NAME_LENGHT) {
			Toast.makeText(
					this,
					"message name more than " + MESSAGE_NAME_LENGHT
							+ "characters", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (sendMediaEnable.isChecked()
				&& ((sendMediaOnesField.getText().length() == 0) || (sendMediaFrequencyField
						.getText().length() == 0))) {
			Toast.makeText(this,
					"Fill ones and frequency sending media, please",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		String email = destEmailField.getText().toString().trim();
		if (sendMediaEnable.isChecked() && sendEmailEnable.isChecked()
				&& email.length() == 0) {
			Toast.makeText(this, "Fill valid email, please", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		
		return true;
	}

	@SuppressWarnings("unused")
	private final static boolean isValidEmail(CharSequence target) {

		try {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
					.matches();
		} catch (NullPointerException exception) {
			exception.printStackTrace();
			return false;
		}
	}

	private void setVideoTypeParameters(boolean isCamera) {

		if (isCamera) {
			recordingFullScreen.setEnabled(true);
			recordingNotificationArea.setEnabled(true);
		} else {
			recordTypeChooseGroup.clearCheck();
			dictophoneRadioEnable.setChecked(true);
			recordingFullScreen.setEnabled(false);
			recordingNotificationArea.setEnabled(false);
		}
	}

	private void setAdditionalParametersEnabled(boolean flag) {

		sendEmailEnable.setEnabled(flag);
		sendMediaOnesField.setEnabled(flag);
		sendMediaFrequencyField.setEnabled(flag);
		destEmailField.setEnabled(flag);
		recordDeviceChooseGroup.setEnabled(flag);
		recordTypeChooseGroup.setEnabled(flag);
		dictophoneRadioEnable.setEnabled(flag);
		cameraRadioEnable.setEnabled(flag);
		if (flag && cameraRadioEnable.isChecked()) {
			recordingFullScreen.setEnabled(true);
			recordingNotificationArea.setEnabled(true);
		} else if ((flag && !cameraRadioEnable.isChecked()) || !flag) {
			recordingFullScreen.setEnabled(false);
			recordingNotificationArea.setEnabled(false);
		}
	}

	private void setWidgetParametersEnable(boolean flag) {
		widgetRadioOneClick.setEnabled(flag);
		widgetRadioTwoClick.setEnabled(flag);
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		Log.v(TAG, "contact id " + id);
		AlertDialog.Builder builderRemove = new AlertDialog.Builder(this);
		builderRemove.setMessage(R.string.ac_addmessage_delete_query);
		// first dialog button
		builderRemove.setPositiveButton(R.string.ac_addmessage_yes,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int id) {

						Log.v(TAG, "" + id);
						numbers.remove(editingTemplateId);
						destinatNumbersField.setText(Utils
								.getStringFromArrayList(numbers));
						refreshNumbersList();
						Toast.makeText(AddMessageActivity.this,
								R.string.ac_addmessage_contact_removed,
								Toast.LENGTH_SHORT).show();
					}
				});
		// second dialog button
		builderRemove.setNegativeButton(R.string.ac_addmessage_no,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int id) {

						// do nothing
					}
				});
		return builderRemove.create();
	}
}
