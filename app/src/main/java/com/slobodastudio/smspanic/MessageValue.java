package com.slobodastudio.smspanic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.StringTokenizer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MessageValue implements Serializable {

	public static final int MEDIA_RECORDER_CAMERA = 100;
	public static final int MEDIA_RECORDER_MIC = 101;
	private static final long serialVersionUID = 6195723873939855971L;
	private static final String TAG = MessageValue.class.getSimpleName();
	private boolean checked;
	private boolean checkedByAcceler = false;
	private String emails = null;
	private int id;
	private boolean isEmailEnable = false;
	private boolean isHide = false;
	private boolean isRecordingTypeEnabled = false;
	private boolean launchModeWidget;
	private boolean isStrobEnabled = false;
	private int mediaRecorder = 0;
	private int recordingType = 0;
	private int mediaSendFrequency = 0;
	private int mediaSendOnes = 0;
	private String name;
	private ArrayList<String> numbers;
	private String rotateOnes;
	private String shakeOnes;
	private String text;
	private String widgClickOnes;
	private String widgName;
	private int timesInMinutes;
	private int timesInMinutesId;
	private int times;

	public boolean isEmailEnable() {

		return isEmailEnable;
	}

	public void setEmailEnable(boolean isEmailEnable) {

		this.isEmailEnable = isEmailEnable;
	}

	public int getTimesInMinutesId() {

		return timesInMinutesId;
	}

	public void setTimesInMinutesId(int timesInMinutesId) {

		this.timesInMinutesId = timesInMinutesId;
	}

	public int getTimesId() {

		return timesId;
	}

	public void setTimesId(int timesId) {

		this.timesId = timesId;
	}

	private int timesId;

	public boolean isCheckedByAcceler() {

		return checkedByAcceler;
	}

	public void setCheckedByAcceler(boolean checkedByAcceler) {

		this.checkedByAcceler = checkedByAcceler;
	}

	public boolean isStrobEnabled() {

		return isStrobEnabled;
	}

	public void setStrobEnabled(boolean isStrobEnabled) {

		this.isStrobEnabled = isStrobEnabled;
	}

	public int getTimesInMinutes() {

		return timesInMinutes;
	}

	public void setTimesInMinutes(int timesInMinutes) {

		this.timesInMinutes = timesInMinutes;
	}

	public boolean isRecordingTypeEnabled() {

		return isRecordingTypeEnabled;
	}

	public void setRecordingTypeEnabled(boolean recordingTypeEnabled) {

		isRecordingTypeEnabled = recordingTypeEnabled;
	}

	public int getTimes() {

		return times;
	}

	public void setTimes(int times) {

		this.times = times;
	}

	public MessageValue() {

	}

	public View getAsView(final Context context, ViewGroup parent) {

		LayoutInflater inflater = LayoutInflater.from(context);
		RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.message_list_item, parent);
		TextView nameText = (TextView) layout.findViewById(R.id.list_item_message_name);
		nameText.setText(name);
		CheckBox checkAccel = (CheckBox) layout.findViewById(R.id.list_item_message_check_acceler);
		checkAccel.setChecked(isCheckedByAcceler());
		CheckBox check = (CheckBox) layout.findViewById(R.id.list_item_message_check_active);
		check.setChecked(isChecked());
		layout.setId(id);
		return layout;
	}

	// /** @return the emails */
	public String getEmails() {

		return emails;
	}

	/** @return the id */
	public int getId() {

		return id;
	}

	/** @return the mediaRecorder */
	public int getMediaRecorder() {

		return mediaRecorder;
	}

	/** @return the recordingType */
	public int getRecordingType() {

		return recordingType;
	}

	/** @return the mediaSendFrequency */
	public int getMediaSendFrequency() {

		Log.v(TAG, mediaSendFrequency + " get send freq");
		return mediaSendFrequency;
	}

	/** @return the mediaSendOnes */
	public int getMediaSendOnes() {

		return mediaSendOnes;
	}

	/** @return the name */
	public String getName() {

		return name;
	}

	/** @return the numbers */
	public ArrayList<String> getNumbers() {

		return numbers;
	}

	/** @return the rotateOnes */
	public String getRotateOnes() {

		return rotateOnes;
	}

	/** @return the shakeOnes */
	public String getShakeOnes() {

		return shakeOnes;
	}

	/** @return the text */
	public String getText() {

		return text;
	}

	/** @return the widgClickOnes */
	public String getWidgClickOnes() {

		return widgClickOnes;
	}

	/** @return the widgName */
	public String getWidgName() {

		return widgName;
	}

	/** @return the checked */
	public boolean isChecked() {

		return checked;
	}

	/** @param checked
	 *            the checked to set */
	public void setChecked(boolean checked) {

		this.checked = checked;
	}

	public boolean isHide() {

		return isHide;
	}

	/** @return the launchModeAccelerometer */
	/** @return the launchModeWidget */
	public boolean isLaunchModeWidget() {

		return launchModeWidget;
	}

	/** @param emails
	 *            the emails to set */
	public void setEmails(String emails) {

		this.emails = emails;
	}

	public void setHide(boolean isHide) {

		this.isHide = isHide;
	}

	/** @param id
	 *            the id to set */
	public void setId(int id) {

		this.id = id;
	}

	/** @param launchModeWidget
	 *            the launchModeWidget to set */
	public void setLaunchModeWidget(boolean launchModeWidget) {

		this.launchModeWidget = launchModeWidget;
	}

	/** @param mediaRecorder
	 *            the mediaRecorder to set */
	public void setMediaRecorder(int mediaRecorder) {

		this.mediaRecorder = mediaRecorder;
	}

	/** @param recordingType
	 *            the recordingType to set */
	public void setRecordingType(int recordingType) {

		this.recordingType = recordingType;
	}

	/** @param mediaSendFrequency
	 *            the mediaSendFrequency to set */
	public void setMediaSendFrequency(int mediaSendFrequency) {

		Log.v(TAG, mediaSendFrequency + " set send freq");
		this.mediaSendFrequency = mediaSendFrequency;
	}

	/** @param mediaSendOnes
	 *            the mediaSendOnes to set */
	public void setMediaSendOnes(int mediaSendOnes) {

		this.mediaSendOnes = mediaSendOnes;
	}

	/** @param name
	 *            the name to set */
	public void setName(String name) {

		this.name = name;
	}

	/** @param numbers
	 *            the numbers to set */
	public void setNumbers(ArrayList<String> numbers) {

		this.numbers = numbers;
	}

	/** @param rotateOnes
	 *            the rotateOnes to set */
	public void setRotateOnes(String rotateOnes) {

		this.rotateOnes = rotateOnes;
	}

	/** @param shakeOnes
	 *            the shakeOnes to set */
	public void setShakeOnes(String shakeOnes) {

		this.shakeOnes = shakeOnes;
	}

	/** @param text
	 *            the text to set */
	public void setText(String text) {

		this.text = text;
	}

	/** @param widgClickOnes
	 *            the widgClickOnes to set */
	public void setWidgClickOnes(String widgClickOnes) {

		this.widgClickOnes = widgClickOnes;
	}

	/** @param widgName
	 *            the widgName to set */
	public void setWidgName(String widgName) {

		this.widgName = widgName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return "MessageValue [checked=" + checked + ", emails=" + emails + ", checkedByAcceler ="
				+ checkedByAcceler + ", id=" + id + ", isEmailEnable=" + isEmailEnable + ", isHide=" + isHide
				+ ", isRecordingTypeEnabled=" + isRecordingTypeEnabled + ", recordingType=" + recordingType
				+ ", isStrobEnabled=" + isStrobEnabled + ", launchModeWidget=" + launchModeWidget
				+ ", mediaRecorder=" + mediaRecorder + ", mediaSendFrequency=" + mediaSendFrequency
				+ ", mediaSendOnes=" + mediaSendOnes + ", name=" + name + ", numbers=" + numbers
				+ ", rotateOnes=" + rotateOnes + ", shakeOnes=" + shakeOnes + ", text=" + text
				+ ", widgClickOnes=" + widgClickOnes + ", widgName=" + widgName + ", timesInMinutes="
				+ timesInMinutes + ", timesInMinutesId=" + timesInMinutesId + ", timesId=" + timesId
				+ ", times=" + times + "]";
	}

	public String getNumbersNamesPairs() {

		StringBuilder sb = new StringBuilder();
		for (String number : numbers) {
			{
				sb.append(number);
				sb.append(",");
			}
		}
		return sb.toString();
	}

	public void setNumbersNames(String str) {

		numbers = new ArrayList<String>();
		// names = new ArrayList<String>();
		StringTokenizer outerSt = new StringTokenizer(str, ",");
		while (outerSt.hasMoreTokens()) {
			String pairStr = outerSt.nextToken();
			if (pairStr.trim().length() == 0) {
				continue;
			}
			String[] pair = pairStr.split(":");
			numbers.add(pair[0]);
			// names.add(ContactsUtil.getContactDistplayName(context, pair[0], Phone.NUMBER));
		}
	}
}
