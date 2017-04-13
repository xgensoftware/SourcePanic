package com.slobodastudio.smspanic;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;

public class TableRadioButtonGroup extends TableLayout implements OnClickListener {

	private static RadioButton activeRadioButton;

	public TableRadioButtonGroup(Context context) {

		super(context);
	}

	public TableRadioButtonGroup(Context context, AttributeSet attrs) {

		super(context, attrs);
	}

	@Override
	public void onClick(View v) {

		final RadioButton rb = (RadioButton) v;
		if (activeRadioButton != null) {
			activeRadioButton.setChecked(false);
		}
		rb.setChecked(true);
		activeRadioButton = rb;
		Log.v("mes", activeRadioButton.getId() + "");
	}

	@Override
	public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {

		super.addView(child, index, params);
		setChildrenOnClickListener((TableRow) child);
	}

	@Override
	public void addView(View child, android.view.ViewGroup.LayoutParams params) {

		super.addView(child, params);
		setChildrenOnClickListener((TableRow) child);
	}

	private void setChildrenOnClickListener(TableRow tr) {

		final int c = tr.getChildCount();
		for (int i = 0; i < c; i++) {
			final View v = tr.getChildAt(i);
			if (v instanceof RadioButton) {
				v.setOnClickListener(this);
			}
		}
	}

	public int getCheckedRadioButtonId() {

		if (activeRadioButton != null) {
			return activeRadioButton.getId();
		}
		return -1;
	}

	public void setCheckedRadioButton(View v) {

		RadioButton rb = (RadioButton) v;
		rb.setChecked(true);
		activeRadioButton = rb;
	}

	@Override
	public void setEnabled(boolean enabled) {

		final int c = this.getChildCount();
		for (int i = 0; i < c; i++) {
			final View v = this.getChildAt(i);
			if (v instanceof TableRow) {
				TableRow tr = (TableRow) v;
				final int cc = this.getChildCount();
				for (int j = 0; j < cc; j++) {
					final View vv = tr.getChildAt(j);
					if (vv instanceof RadioButton) {
						vv.setEnabled(enabled);
					}
				}
			}
		}
		super.setEnabled(enabled);
	}
}
