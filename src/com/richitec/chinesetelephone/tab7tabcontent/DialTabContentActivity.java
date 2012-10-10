package com.richitec.chinesetelephone.tab7tabcontent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.richitec.chinesetelephone.R;

public class DialTabContentActivity extends Activity {

	// dial phone textView
	private TextView _mDialPhoneTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.dial_tab_content_activity_layout);

		// init dial phone textView
		_mDialPhoneTextView = (TextView) findViewById(R.id.dial_phone_textView);
		_mDialPhoneTextView.setText("");

		// get dial phone button gridView
		GridView _dialPhoneButtonGridView = ((GridView) findViewById(R.id.dial_phoneBtn_gridView));

		// set dial phone button grid view adapter
		_dialPhoneButtonGridView.setAdapter(generateDialPhoneButtonAdapter());

		// set dial phone button grid view item click and long click listener
		_dialPhoneButtonGridView
				.setOnItemClickListener(new DialBtnGridViewItemOnClickListener());
		_dialPhoneButtonGridView
				.setOnItemLongClickListener(new DialBtnGridViewItemOnLongClickListener());

		// test by ares
		((ImageButton) findViewById(R.id.dial_newContact_functionBtn))
				.setImageResource(R.drawable.img_dial_newcontact_btn);
		((ImageButton) findViewById(R.id.dial_call_functionBtn))
				.setImageResource(R.drawable.img_dial_call_btn);
		((ImageButton) findViewById(R.id.dial_clearDialPhone_functionBtn))
				.setImageResource(R.drawable.img_dial_cleardialphone_btn);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater()
				.inflate(R.menu.dial_tab_content_activity_layout, menu);
		return true;
	}

	// generate dial phone button adapter
	private ListAdapter generateDialPhoneButtonAdapter() {
		// dial phone button adapter data keys
		final String DIAL_PHONEBUTTON_IMAGE = "dial_phone_button_image";

		// define dial phone button gridView content
		int[] _dialPhoneButtonGridViewContentArray = {
				R.drawable.img_dial_1_btn, R.drawable.img_dial_2_btn,
				R.drawable.img_dial_3_btn, R.drawable.img_dial_4_btn,
				R.drawable.img_dial_5_btn, R.drawable.img_dial_6_btn,
				R.drawable.img_dial_7_btn, R.drawable.img_dial_8_btn,
				R.drawable.img_dial_9_btn, R.drawable.img_dial_star_btn,
				R.drawable.img_dial_0_btn, R.drawable.img_dial_pound_btn };

		// set address book contacts list view present data list
		List<Map<String, ?>> _dialPhoneButtonDataList = new ArrayList<Map<String, ?>>();

		for (int i = 0; i < _dialPhoneButtonGridViewContentArray.length; i++) {
			// generate data
			HashMap<String, Object> _dataMap = new HashMap<String, Object>();

			_dataMap.put(
					DIAL_PHONEBUTTON_IMAGE,
					getResources().getDrawable(
							_dialPhoneButtonGridViewContentArray[i]));

			// add data to list
			_dialPhoneButtonDataList.add(_dataMap);
		}

		return new DialPhoneButtonAdapter(this, _dialPhoneButtonDataList,
				R.layout.dial_phone_btn_layout,
				new String[] { DIAL_PHONEBUTTON_IMAGE },
				new int[] { R.id.dialBtn_imageView });
	}

	// inner class
	// dial button gridView item on click listener
	class DialBtnGridViewItemOnClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// define dial phone string builder
			StringBuilder _dialPhoneStringBuilder = new StringBuilder(
					_mDialPhoneTextView.getText());

			// check position
			switch (position) {
			case 9:
				// *
				_dialPhoneStringBuilder.append('*');
				break;

			case 10:
				// 0
				_dialPhoneStringBuilder.append(0);
				break;

			case 11:
				// #
				_dialPhoneStringBuilder.append('#');
				break;

			default:
				// numeric
				_dialPhoneStringBuilder.append(position + 1);
				break;
			}

			// reset dial phone textView text
			_mDialPhoneTextView.setText(_dialPhoneStringBuilder);
		}

	}

	// dial button gridView item on long click listener
	class DialBtnGridViewItemOnLongClickListener implements
			OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			boolean _ret = false;

			// check 0 or +
			if (10 == position) {
				// define dial phone string builder
				StringBuilder _dialPhoneStringBuilder = new StringBuilder(
						_mDialPhoneTextView.getText());

				// +
				_dialPhoneStringBuilder.append('+');

				// reset dial phone textView text
				_mDialPhoneTextView.setText(_dialPhoneStringBuilder);

				_ret = true;
			}

			return _ret;
		}

	}

}
