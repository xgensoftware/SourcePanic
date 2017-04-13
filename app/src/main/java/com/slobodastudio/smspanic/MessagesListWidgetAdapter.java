package com.slobodastudio.smspanic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MessagesListWidgetAdapter extends BaseAdapter {

	private final Context context;
	private ArrayList<MessageValue> data = new ArrayList<MessageValue>();

	// new comment in test purposes
	public MessagesListWidgetAdapter(Context context, ArrayList<MessageValue> arr) {

		if (arr != null) {
			data = arr;
		}
		this.context = context;
	}

	@Override
	public int getCount() {

		return data.size();
	}

	@Override
	public Object getItem(int position) {

		return data.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View linearLayout, ViewGroup parent) {

		LayoutInflater inflater = LayoutInflater.from(context);
		if (linearLayout == null) {
			linearLayout = inflater.inflate(R.layout.ac_configurewidget_list_item, parent, false);
		}
		TextView nameOfMessage = (TextView) linearLayout.findViewById(R.id.ac_configure_widget_list_item);
		String name = data.get(position).getName();
		nameOfMessage.setText(name);
		return linearLayout;
	}
}
