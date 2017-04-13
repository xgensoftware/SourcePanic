package com.slobodastudio.smspanic.activities;

import com.slobodastudio.smspanic.MessageValue;
import com.slobodastudio.smspanic.R;
import com.slobodastudio.smspanic.broadcasts.SmsSenderBroadcast;
import com.slobodastudio.smspanic.services.AccelerometerListenerService;
import com.slobodastudio.smspanic.utils.ComponentsUtils;
import com.slobodastudio.smspanic.utils.MessageController;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager.WakeLock;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class MessagesListActivity extends Activity implements OnClickListener, OnLongClickListener,
		OnCheckedChangeListener {

	private ViewGroup container;
	private ArrayList<MessageValue> messagesList;
	private final ArrayList<Integer> checkChangedList = new ArrayList<Integer>();
	private static final String TAG = MessagesListActivity.class.getSimpleName();
	private OnClickListener sendListener;
	private OnClickListener listenerAccel;
	private int operationId = -1;
	private int checkedItemToAccel = -1;
	private ResultReceiver resReceiver;
	private Handler handler;
	private WakeLock mTurnBackOn = null;
	private CheckBox activeItem;
	public static final String ID_PANIC_TO_ACCELEROMETER = "panic_id",
			RECEIVER_TO_ACCELEROMETER = "reseiver_to_accelerometer";
	public static final int UNCHECK_ITEM = 47;
	public static final String MY_AD_UNIT_ID = "a1502ac9006992d";
	public static final String EMAIL = "mail@androids.ru";

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ac_messageslist);
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.RelativeLayout1);
		ComponentsUtils.setViewsSettings(rl);
		container = (ViewGroup) findViewById(R.id.ac_messageslist_container);
		Button addNewButton = (Button) findViewById(R.id.ac_messageslist_addnewnutton);
		addNewButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(MessagesListActivity.this, AddMessageActivity.class);
				startActivity(intent);
			}
		});
		sendListener = new OnClickListener() {

			@Override
			public void onClick(View v) {

				View parent = (View) v.getParent();
				int id = parent.getId();
				// for(MessageValue mv: messagesList) {
				// if (mv.getId() == id) {
				Intent intent = new Intent(MessagesListActivity.this, SosButtonActivity.class);
				intent.putExtra(SmsSenderBroadcast.EXTRA_MESSAGE_VALUE_ID, id);
				startActivity(intent);
				// }
				// }
			}
		};
		listenerAccel = new OnClickListener() {

			@Override
			public void onClick(View buttonView) {

				View parent = (View) buttonView.getParent();
				int idPanic = parent.getId();
				CheckBox ch = (CheckBox) buttonView;
				for (MessageValue selectedItem : messagesList) {
					Log.v(TAG, idPanic + " parentId and " + selectedItem.getId());
					if (idPanic == selectedItem.getId()) {
						selectedItem.setCheckedByAcceler(ch.isChecked());
						Intent intentService = new Intent(MessagesListActivity.this,
								AccelerometerListenerService.class);
						if (ch.isChecked()) {
							Log.v(TAG, "start service");
							checkedItemToAccel = idPanic;
							activeItem = ch;
							intentService.putExtra(ID_PANIC_TO_ACCELEROMETER, idPanic);
							intentService.putExtra(RECEIVER_TO_ACCELEROMETER, resReceiver);
							MessagesListActivity.this.startService(intentService);
						} else {
							Log.v(TAG, "stop service");
							checkedItemToAccel = -1;
							activeItem = null;
							AccelerometerListenerService.setIdPanic(0);
							MessagesListActivity.this.stopService(intentService);
						}
					} else {
						selectedItem.setCheckedByAcceler(false);
					}
				}
				for (int i = 0; i < container.getChildCount(); i++) {
					RelativeLayout l = (RelativeLayout) container.getChildAt(i);
					CheckBox ch1 = (CheckBox) l.getChildAt(2);
					if (ch1.isChecked() && (l.getId() == checkedItemToAccel)) {
						((CheckBox) l.getChildAt(3)).setChecked(true);
					} else {
						ch1.setChecked(false);
					}
				}
			}
		};
		resReceiver = new ResultReceiver(handler) {

			protected void onReceiveResult(int resultCode, Bundle data) {

				if (resultCode == UNCHECK_ITEM) {
					handler.sendEmptyMessage(resultCode);
				}
			}
		};
		handler = new Handler() {

			public void handleMessage(android.os.Message msg) {

				super.handleMessage(msg);
				if (msg.getData().isEmpty()) {
					if (activeItem != null) {
						activeItem.setChecked(false);
					}
				}
			};
		};
	}

	@Override
	public void onResume() {

		super.onResume();
		getMessages();
		mTurnBackOn = AccelerometerListenerService.getmTurnBackOn();
		try {
			mTurnBackOn.release();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {

		// removed Ads
		// if (adView != null) {
		// adView.destroy();
		// }
		super.onDestroy();
		// Intent serviceIntent = new Intent(this, ListenerService.class);
		// stopService(serviceIntent);
	}

	@Override
	public void onPause() {

		super.onPause();
		if ((checkChangedList.size() == 0)) {// && (checkedItemToAccel == -1)
			return;
		}
		for (int checkChangedId : checkChangedList) {
			for (MessageValue currentMessage : messagesList) {
				if (currentMessage.getId() == checkChangedId) {
					MessageController.updateMessageInDb(MessagesListActivity.this, currentMessage);
				}
			}
		}
	}

	private void getMessages() {

		messagesList = new ArrayList<MessageValue>();
		container.removeAllViews();
		messagesList = MessageController.getAllMessages(this);
		if (messagesList != null) {
			for (MessageValue item : messagesList) {
				View child = item.getAsView(this, null);
				child.setOnClickListener(this);
				child.setOnLongClickListener(this);
				CheckBox selectedField = (CheckBox) child.findViewById(R.id.list_item_message_check_active);
				selectedField.setChecked(item.isChecked());
				selectedField.setOnCheckedChangeListener(this);
				CheckBox selectedFieldByAccel = (CheckBox) child
						.findViewById(R.id.list_item_message_check_acceler);
				selectedFieldByAccel.setChecked(item.isCheckedByAcceler());
				selectedFieldByAccel.setOnClickListener(listenerAccel);
				ImageView sendIcon = (ImageView) child.findViewById(R.id.list_item_send_icon);
				sendIcon.setOnClickListener(sendListener);
				if ((AccelerometerListenerService.getIdPanic() != 0)
						&& (child.getId() == AccelerometerListenerService.getIdPanic())) {
					activeItem = (CheckBox) child.findViewById(R.id.list_item_message_check_acceler);
					activeItem.setChecked(true);
				}
				container.addView(child);
			}
		}
	}

	@Override
	public void onClick(View parent) {

		Intent intent = new Intent(MessagesListActivity.this, AddMessageActivity.class);
		intent.putExtra(AddMessageActivity.MODIFY_ACTIVITY_MODE_KEY, true);
		for (MessageValue focusedMessageValue : messagesList) {
			int currentItemId = Integer.valueOf(focusedMessageValue.getId());
			if (currentItemId == parent.getId()) {
				intent.putExtra(AddMessageActivity.STATE_ACTIVITY_TRANSFER_KEY, focusedMessageValue);
				startActivity(intent);
			}
		}
	}

	@Override
	public boolean onLongClick(View v) {

		operationId = v.getId();
		showDialog(v.getId());
		return false;
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		final int messageId = id;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.ac_messages_list_choose_action_message);
		// first dialog button
		builder.setPositiveButton(R.string.ac_messages_list_copy_message,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int id) {

						for (MessageValue selectedMessage : messagesList) {
							if (selectedMessage.getId() == operationId) {
								selectedMessage.setName(selectedMessage.getName() + "_copy");
								MessageController.saveMessageInDb(MessagesListActivity.this, selectedMessage);
								Toast.makeText(MessagesListActivity.this,
										R.string.ac_messages_list_warning_message_copied, Toast.LENGTH_SHORT)
										.show();
								operationId = -1;
								onResume();
							}
						}
					}
				});
		// second dialog button
		builder.setNegativeButton(R.string.ac_messages_list_delete_message,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int id) {

						Log.v(TAG, "must delete " + messageId);
						MessageController.deleteMessageFromDb(MessagesListActivity.this, operationId);
						Toast.makeText(MessagesListActivity.this,
								R.string.ac_messages_list_warning_message_deleted, Toast.LENGTH_SHORT).show();
						operationId = -1;
						onResume();
					}
				});
		return builder.create();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		RelativeLayout parent = (RelativeLayout) buttonView.getParent();
		for (MessageValue selectedItem : messagesList) {
			Log.v(TAG, parent.getId() + " parentId and " + selectedItem.getId());
			if (parent.getId() == selectedItem.getId()) {
				selectedItem.setChecked(isChecked);
				if (isChecked == false) {
					((CheckBox) parent.getChildAt(2)).setChecked(false);
				}
				if (!checkChangedList.contains(selectedItem.getId())) {
					checkChangedList.add(selectedItem.getId());
				}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		super.onCreateOptionsMenu(menu);
		String appMenu[] = getResources().getStringArray(R.array.menuCongratulationTypesActivity_buttonMenu);
		menu.add(Menu.NONE, 0, 0, appMenu[0]).setIcon(android.R.drawable.ic_menu_info_details);
		menu.add(Menu.NONE, 2, 2, appMenu[1]).setIcon(android.R.drawable.ic_menu_help);
		menu.add(Menu.NONE, 3, 3, appMenu[2]).setIcon(android.R.drawable.star_big_off);
		menu.add(Menu.NONE, 4, 4, appMenu[3]).setIcon(android.R.drawable.ic_menu_share);
		menu.add(Menu.NONE, 5, 5, appMenu[4]).setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(Menu.NONE, 6, 6, appMenu[5]).setIcon(android.R.drawable.ic_menu_upload);
		menu.add(Menu.NONE, 7, 7, appMenu[6]);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case 0: // info
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri
						.parse("http://www.google.com/gwt/x?wsc=fa&source=wax&u=http://androids.ru/i/ipanic.htm&ei=woVhUei-GcSG_Ab_kYGACg&whp=3Atop"));
				startActivity(browserIntent);
				break;
			case 2:// help
			{
				Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
				String[] recipients = new String[] { EMAIL, "", };
				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name));
				emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
						getString(R.string.text_email_message));
				emailIntent.setType("text/plain");
				startActivity(Intent.createChooser(emailIntent, getString(R.string.text_send_mail)));
			}
				break;
			case 3:// rate
				try {
					String appPackageName = getPackageName();
					Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="
							+ appPackageName));
					marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
							| Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
					startActivity(marketIntent);
				} catch (Exception ex) {
					Log.v(TAG, "Cannot open ActionView to paste rate.", ex);
				}
				break;
			case 4:// share
				share(getString(R.string.shareSubject), getString(R.string.shareText));
				break;
			case 5:// Preferences
				Intent intent = new Intent(this, PreferenceActivity.class);//
				startActivity(intent);
				break;
			case 6:// Upgrade
				Intent upgrade = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.panicrec"));
				upgrade.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				startActivity(upgrade);
				break;
			case 7://Twitter
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/apps_r")));
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void share(String subject, String text) {

		final Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, text + " https://play.google.com/store/apps/details?id=com.smsrec");
		startActivity(Intent.createChooser(intent, getString(R.string.share)));
	}
}
