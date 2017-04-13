/*
 * Copyright (C) 2009 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package com.slobodastudio.smspanic.activities;

import com.slobodastudio.smspanic.R;
import com.slobodastudio.smspanic.utils.ComponentsUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public final class PickContactsActivity extends Activity {

	private class ContactCheckedCursorAdapter extends BaseAdapter {

		private final Context mContext;
		private final String mCursorColNames[];
		private final int mIds[];
		private final Cursor mmCursor;
		private float mWeight[] = null;

		@SuppressWarnings("unused")
		public ContactCheckedCursorAdapter(Context context, Cursor cursor, String curNames[], int ids[]) {

			super();
			mmCursor = cursor;
			mContext = context;
			mCursorColNames = curNames;
			mIds = ids;
		}

		public ContactCheckedCursorAdapter(Context context, Cursor cursor, String curNames[], int ids[],
				float weight[]) {

			super();
			mmCursor = cursor;
			mContext = context;
			mCursorColNames = curNames;
			mWeight = weight;
			mIds = ids;
		}

		@Override
		public int getCount() {

			return mmCursor.getCount();
		}

		@Override
		public Object getItem(int position) {

			mmCursor.moveToPosition(position);
			return mmCursor;
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			LinearLayout llRoot = new LinearLayout(mContext);
			llRoot.setOrientation(LinearLayout.HORIZONTAL);
			llRoot.setPadding(5, 3, 5, 3);
			llRoot.setGravity(Gravity.CENTER);
			LinearLayout llRootForTV = null;
			if (mmCursor.moveToPosition(position)) {
				for (int i = 0; i < mCursorColNames.length; i++) {
					if (mIds[i] == android.R.id.text1) {
						llRootForTV = new LinearLayout(mContext);
						llRootForTV.setOrientation(LinearLayout.VERTICAL);
						llRootForTV.setPadding(5, 3, 5, 3);
						llRootForTV.setGravity(Gravity.CENTER);
						if (mWeight != null) {
							llRootForTV.setLayoutParams(new LayoutParams(
									android.view.ViewGroup.LayoutParams.FILL_PARENT,
									android.view.ViewGroup.LayoutParams.WRAP_CONTENT, mWeight[i]));
						} else {
							llRootForTV.setLayoutParams(new LayoutParams(
									android.view.ViewGroup.LayoutParams.FILL_PARENT,
									android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f));
						}
						TextView tvItem = new TextView(mContext);
						tvItem.setId(android.R.id.text1);
						tvItem.setTextSize(18f);
						tvItem.setTextColor(getResources().getColor(android.R.color.white));
						tvItem.setPadding(3, 3, 3, 3);
						tvItem.setLayoutParams(new LayoutParams(
								android.view.ViewGroup.LayoutParams.FILL_PARENT,
								android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
						tvItem.setText(mmCursor.getString(mmCursor.getColumnIndex(mCursorColNames[i])));
						llRootForTV.addView(tvItem);
						llRoot.addView(llRootForTV);
					}
					if (mIds[i] == android.R.id.text2) {
						TextView tvItem = new TextView(mContext);
						tvItem.setId(android.R.id.text2);
						tvItem.setTextSize(14f);
						tvItem.setTextColor(getResources().getColor(android.R.color.white));
						tvItem.setPadding(20, 3, 3, 3);
						tvItem.setText("tel: "
								+ mmCursor.getString(mmCursor.getColumnIndex(mCursorColNames[i])));
						if (llRootForTV == null) {
							llRootForTV = new LinearLayout(mContext);
							llRootForTV.setOrientation(LinearLayout.VERTICAL);
							llRootForTV.setPadding(5, 3, 5, 3);
							llRootForTV.setGravity(Gravity.CENTER);
							if (mWeight != null) {
								llRootForTV.setLayoutParams(new LayoutParams(
										android.view.ViewGroup.LayoutParams.FILL_PARENT,
										android.view.ViewGroup.LayoutParams.WRAP_CONTENT, mWeight[i]));
							} else {
								llRootForTV.setLayoutParams(new LayoutParams(
										android.view.ViewGroup.LayoutParams.FILL_PARENT,
										android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f));
							}
							llRoot.addView(llRootForTV);
						}
						llRootForTV.addView(tvItem);
					}
				}
				final CheckedTextView cbItem = new CheckedTextView(mContext);
				cbItem.setId(android.R.id.checkbox);
				cbItem.setPadding(3, 3, 3, 3);
				cbItem.setHapticFeedbackEnabled(false);
				cbItem.setChecked(mLvContactList.isItemChecked(position));
				refreshState(cbItem);
				cbItem.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						cbItem.setChecked(!cbItem.isChecked());
						mLvContactList.setItemChecked(position, cbItem.isChecked());
						refreshState(cbItem);
						refreshReturnList(position);
					}
				});
				cbItem.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 0.8f));
				llRoot.addView(cbItem);
			}
			return llRoot;
		}
	}

	/** Key. Some type from ContactsContract.Data tables */
	public static final String EXTRA_COMPARING_COLUMN = "comparing_column_type";
	/** Key for comparing list this list will be comparing with all rows, that returned from
	 * ContactsContract.Data tables. You can check column in EXTRA_COMPARING_COLUMN. */
	public static final String EXTRA_COMPARING_LIST = "comparing_list";
	/** Key. Some type, that will be return in activityResult. Default value ContactsContract.Data.CONTACT_ID. */
	public static final String EXTRA_RETURN_COLUMN = "return_column_type";
	public static final String EXTRA_RETURN_LIST = "return_list";
	public static final String EXTRA_RETURN_LIST2 = "return_list2";
	public static final boolean LOGV = true;
	public static final String TAG = PickContactsActivity.class.getSimpleName();
	private String mComparingColumn;
	private ArrayList<String> mComparingList;
	private ArrayList<String> mComparingList2;
	private Cursor mCursor;
	private EditText mEtSearch;
	private ListView mLvContactList;
	private String mReturnColumn;
	private RelativeLayout mRlRoot;

	/** Obtains the contact list for the currently selected account.
	 * 
	 * @return A cursor for for accessing the contact list. */
	private void getContacts(String search) {

		// Run query
		Uri uri = ContactsContract.Data.CONTENT_URI;
		String[] projection;
		if (mComparingColumn == null) {
			projection = new String[] { mReturnColumn, ContactsContract.Data.DISPLAY_NAME, Phone.NUMBER };
			mComparingColumn = Phone.NUMBER;
		} else {
			projection = new String[] { mReturnColumn, ContactsContract.Data.DISPLAY_NAME, Phone.NUMBER,
					mComparingColumn };
		}
		StringBuilder selection = new StringBuilder();
		selection.append(ContactsContract.Data.MIMETYPE);
		selection.append("='");
		selection.append(Phone.CONTENT_ITEM_TYPE);
		selection.append("' AND ( lower(");
		selection.append(ContactsContract.Data.DISPLAY_NAME);
		selection.append(") like '%");
		selection.append(search.toLowerCase());
		selection.append("%' or ");
		selection.append(ContactsContract.Data.DISPLAY_NAME);
		selection.append(" like '%");
		selection.append(search.substring(0, search.length() > 0 ? 1 : 0).toUpperCase()
				+ search.substring(search.length() > 0 ? 1 : 0));
		selection.append("%' OR ");
		selection.append(Phone.NUMBER);
		selection.append(" like '%");
		selection.append(search);
		selection.append("%') ");
		String[] selectionArgs = null;
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
		mCursor = managedQuery(uri, projection, selection.toString(), selectionArgs, sortOrder);
	}

	/** Called when the activity is first created. Responsible for initializing the UI. */
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mRlRoot = new RelativeLayout(this);
		mRlRoot.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT));
		mRlRoot.setGravity(Gravity.RIGHT);
		mEtSearch = new EditText(this);
		mEtSearch.setId(android.R.id.edit);
		mEtSearch.setPadding(3, 3, 3, 4);
		mEtSearch.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS); // Switch off t9
		InputFilter[] filterArray = new InputFilter[1];
		filterArray[0] = new InputFilter() {

			@Override
			public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart,
					int dend) {

				try {
					if (LOGV) {
						Log.v(TAG, "CharSequence " + source + ", int " + start + " int " + end + ", Spanned "
								+ dest + ", int " + dstart + ",	int " + dend + "\n"
								+ (dest.subSequence(0, end == 0 ? dend - 1 : dstart) + "" + source));
					}
					populateContactList((dest.subSequence(0, end == 0 ? dend - 1 : dstart) + "" + source));
				} catch (IndexOutOfBoundsException e) {
					Log.e(TAG, "Error to filtering input text", e);
				}
				return source;
			}
		};
		mEtSearch.setFilters(filterArray);
		setContentView(mRlRoot);
		Intent intent = getIntent();
		mComparingColumn = intent.getStringExtra(EXTRA_COMPARING_COLUMN);
		mReturnColumn = intent.getStringExtra(EXTRA_RETURN_COLUMN);
		mReturnColumn = mReturnColumn == null ? ContactsContract.Data.CONTACT_ID : mReturnColumn;
		mComparingList = intent.getStringArrayListExtra(EXTRA_COMPARING_LIST) == null ? new ArrayList<String>()
				: intent.getStringArrayListExtra(EXTRA_COMPARING_LIST);
		mComparingList2 = (ArrayList<String>) mComparingList.clone();
		mLvContactList = new ListView(this);
		mLvContactList.setId(android.R.id.list);
		mLvContactList.setBackgroundColor(getResources().getColor(android.R.color.transparent));
		mLvContactList.setCacheColorHint(getResources().getColor(android.R.color.transparent));
		Button button = new Button(this);
		button.setId(android.R.id.button1);
		button.setText("Ok");
		// set LayoutParams
		RelativeLayout.LayoutParams btnLayoutParams = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		btnLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		RelativeLayout.LayoutParams etLayoutParams = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		etLayoutParams.addRule(RelativeLayout.ABOVE, button.getId());
		RelativeLayout.LayoutParams headerLayoutParams = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		headerLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		RelativeLayout.LayoutParams lvLayoutParams = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		lvLayoutParams.addRule(RelativeLayout.ABOVE, mEtSearch.getId());
		mEtSearch.setLayoutParams(etLayoutParams);
		button.setLayoutParams(btnLayoutParams);
		mLvContactList.setLayoutParams(lvLayoutParams);
		mRlRoot.addView(button);
		mRlRoot.addView(mEtSearch);
		mRlRoot.addView(mLvContactList);
		mRlRoot.setBackgroundResource(R.drawable.background_blue);
		ComponentsUtils.setViewsSettings(mRlRoot);
		mLvContactList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {

				CheckedTextView cbItem = (CheckedTextView) view.findViewById(android.R.id.checkbox);
				boolean check = mLvContactList.isItemChecked(position);
				cbItem.setChecked(check);
				refreshState(cbItem);
				refreshReturnList(position);
			}
		});
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.putExtra(EXTRA_RETURN_LIST, mComparingList);
				intent.putExtra(EXTRA_RETURN_LIST2, mComparingList2);
				setResult(RESULT_OK, intent);// returnList));
				finish();
			}
		});
		populateContactList("");
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
		mCursor.close();
	}

	/** Populate the contact list based on account currently selected in the account spinner. */
	private void populateContactList(String search) {

		// Build adapter with contact entries
		getContacts(search);
		String projection[];
		projection = new String[] { ContactsContract.Data.DISPLAY_NAME, Phone.NUMBER };
		ContactCheckedCursorAdapter adapter = new ContactCheckedCursorAdapter(this, mCursor, projection,
				new int[] { android.R.id.text1, android.R.id.text2 }, new float[] { 0.2f, 0.8f });
		mLvContactList.setAdapter(adapter);
		mLvContactList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		for (int i = 0; i < mCursor.getCount(); i++) {
			if (mCursor.moveToPosition(i)) {
				boolean checked = mComparingList.contains(mCursor.getString(mCursor
						.getColumnIndex(mComparingColumn)));
				mLvContactList.setItemChecked(i, checked);
			}
		}
	}

	private void refreshReturnList(int position) {

		boolean add = mLvContactList.isItemChecked(position);
		String text = "";
		String name = "";
		if (mCursor.moveToPosition(position)) {
			name = mCursor.getString(mCursor.getColumnIndexOrThrow(ContactsContract.Data.DISPLAY_NAME));
			text = mCursor.getString(mCursor.getColumnIndexOrThrow(mComparingColumn));
		}
		if (add) {
			if (!mComparingList.contains(text)) {
				mComparingList2.add(name);
				mComparingList.add(text);
			}
		} else {
			int indexForRemove = mComparingList.indexOf(text);
			mComparingList.remove(indexForRemove);
			mComparingList2.remove(indexForRemove);
		}
	}

	private void refreshState(CheckedTextView cbItem) {

		if (cbItem.isChecked()) {
			cbItem.setCheckMarkDrawable(R.drawable.btn_check_on);// android.R.drawable.checkbox_on_background);
		} else {
			cbItem.setCheckMarkDrawable(R.drawable.btn_check_off);// android.R.drawable.checkbox_off_background);
		}
		cbItem.postInvalidate();
	}
}