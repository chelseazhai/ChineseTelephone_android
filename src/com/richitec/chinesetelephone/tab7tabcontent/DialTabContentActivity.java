package com.richitec.chinesetelephone.tab7tabcontent;

import java.util.ArrayList;
import java.util.HashMap;
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
import android.widget.SimpleAdapter;
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

		// test by ares
		// define grid view content
		String[] gridViewContentArr = { "1", "2", "3", "4", "5", "6", "7", "8",
				"9", "*", "0", "#" };

		// data list
		ArrayList<Map<String, String>> dataList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < gridViewContentArr.length; i++) {
			HashMap<String, String> dataMap = new HashMap<String, String>();

			dataMap.put("dial_btn_title", gridViewContentArr[i]);

			dataList.add(dataMap);
		}

		// simple adapter with content
		SimpleAdapter adapter = new SimpleAdapter(this, dataList,
				R.layout.dial_btn_layout, new String[] { "dial_btn_title" },
				new int[] { R.id.dialBtn_textView });

		// get dial button gridView
		GridView _dialBtnGridView = ((GridView) findViewById(R.id.dial_btn_gridView));

		// set dial button grid view adapter
		_dialBtnGridView.setAdapter(adapter);

		// set dial button grid view item click and long click listener
		_dialBtnGridView
				.setOnItemClickListener(new DialBtnGridViewItemOnClickListener());
		_dialBtnGridView
				.setOnItemLongClickListener(new DialBtnGridViewItemOnLongClickListener());

		// test by ares
		((ImageButton) findViewById(R.id.dial_newContact_functionBtn))
				.setImageResource(R.drawable.img_dial_star_btn);
		((ImageButton) findViewById(R.id.dial_clearDialPhone_functionBtn))
				.setImageResource(R.drawable.img_dial_star_btn);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater()
				.inflate(R.menu.dial_tab_content_activity_layout, menu);
		return true;
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
