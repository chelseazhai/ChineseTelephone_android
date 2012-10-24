package com.richitec.chinesetelephone.call;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.richitec.chinesetelephone.R;

public class OutgoingCallActivity extends Activity {

	private static final String LOG_TAG = "OutgoingCallActivity";

	// outgoing call activity onCreate param key
	public static final String OUTGOING_CALL_PHONE = "outgoing_call_phone";
	public static final String OUTGOING_CALL_OWNERSHIP = "outgoing_call_ownership";

	// outgoing call phone number
	private String _mCallPhone;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.outgoing_call_activity_layout);

		// get the intent parameter data
		Bundle _data = getIntent().getExtras();

		// check the data bundle and get call phone
		if (null != _data) {
			// init outgoing call phone
			if (null != _data.getString(OUTGOING_CALL_PHONE)) {
				_mCallPhone = _data.getString(OUTGOING_CALL_PHONE);
			}

			// set caller textView text
			((TextView) findViewById(R.id.callee_textView))
					.setText(null != _data.getString(OUTGOING_CALL_OWNERSHIP) ? _data
							.getString(OUTGOING_CALL_OWNERSHIP) : _mCallPhone);
		}

		// set wallpaper as outgoing call background
		((ImageView) findViewById(R.id.outgoingcall_background_imageView))
				.setImageDrawable(getWallpaper());

		// get call controller gridView
		GridView _callControllerGridView = (GridView) findViewById(R.id.callController_gridView);

		// set call controller gridView adapter
		_callControllerGridView.setAdapter(generateCallControllerAdapter());

		// set call controller gridView on item click listener
		_callControllerGridView
				.setOnItemClickListener(new CallControllerGridViewOnItemClickListener());

		// bind hangup outgoing call button on click listener
		((ImageButton) findViewById(R.id.hangup_button))
				.setOnClickListener(new HangupOutgoingCallBtnOnClickListener());

		// bind hide keyboard button on click listener
		((ImageButton) findViewById(R.id.hideKeyboard_button))
				.setOnClickListener(new HideKeyboardBtnOnClickListener());
	}

	// generate call controller adapter
	private ListAdapter generateCallControllerAdapter() {
		// call controller item adapter data key
		final String CALL_CONTROLLER_ITEM_ICON = "call_controller_item_icon";
		final String CALL_CONTROLLER_ITEM_LABEL = "call_controller_item_label";

		// define call controller gridView content
		final int[][] _callControllerGridViewContentArray = {
				{ android.R.drawable.ic_lock_lock,
						R.string.callController_contactItem_text },
				{ android.R.drawable.ic_lock_power_off,
						R.string.callController_handfreeItem_text },
				{ android.R.drawable.ic_lock_silent_mode,
						R.string.callController_muteItem_text },
				{ android.R.drawable.ic_lock_silent_mode_off,
						R.string.callController_keyboardItem_text } };

		// set call controller data list
		List<Map<String, ?>> _callControllerDataList = new ArrayList<Map<String, ?>>();

		for (int i = 0; i < _callControllerGridViewContentArray.length; i++) {
			// generate data
			Map<String, Object> _dataMap = new HashMap<String, Object>();

			// put value
			_dataMap.put(CALL_CONTROLLER_ITEM_ICON,
					_callControllerGridViewContentArray[i][0]);
			_dataMap.put(CALL_CONTROLLER_ITEM_LABEL,
					_callControllerGridViewContentArray[i][1]);

			// add data to list
			_callControllerDataList.add(_dataMap);
		}

		return new OutgoingCallControllerAdapter(this, _callControllerDataList,
				R.layout.call_controller_item,
				new String[] { CALL_CONTROLLER_ITEM_ICON,
						CALL_CONTROLLER_ITEM_LABEL }, new int[] {
						R.id.callController_item_iconImgView,
						R.id.callController_item_labelTextView });
	}

	// inner class
	// call controller gridView on item click listener
	class CallControllerGridViewOnItemClickListener implements
			OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Log.d(LOG_TAG,
					"call controller gridView on item click listener, parent = "
							+ parent + ", view = " + view + ", position = "
							+ position + " and id = " + id);

			//
			// ??
		}

	}

	// hangup outgoing call button on click listener
	class HangupOutgoingCallBtnOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// finish outgoing call activity
			finish();

			//
			// ??
		}

	}

	// hide keyboard button on click listener
	class HideKeyboardBtnOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Log.d(LOG_TAG, "hide keyboard and view = " + v);

			//
			// ??
		}

	}

}
