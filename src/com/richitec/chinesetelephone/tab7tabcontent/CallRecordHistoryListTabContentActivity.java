package com.richitec.chinesetelephone.tab7tabcontent;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.call.ContactPhoneDialModeSelectpopupWindow;
import com.richitec.commontoolkit.activityextension.NavigationActivity;
import com.richitec.commontoolkit.calllog.CallLogBean;
import com.richitec.commontoolkit.calllog.CallLogBean.CallType;
import com.richitec.commontoolkit.calllog.CallLogManager;
import com.richitec.commontoolkit.utils.CommonUtils;

public class CallRecordHistoryListTabContentActivity extends NavigationActivity {

	private static final String LOG_TAG = "CallRecordHistoryListTabContentActivity";

	// call record detail image button keys
	public static final String CALL_RECORD_IMAGEBUTTON_TAG = "call_record_imageButton_tag";
	public static final String CALL_RECORD_IMAGEBUTTON_ONCLICKLISTENER = "call_record_imageButton_onClickListener";

	// call log list
	private final List<CallLogBean> _mCallLogList = new ArrayList<CallLogBean>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.call_record_history_list_tab_content_activity_layout);

		// set title
		setTitle(R.string.call_record_history_list_nav_title);

		// get call record history listView
		ListView _callRecordHistoryListView = (ListView) findViewById(R.id.callRecordHistoryList_listView);

		// set call record history listView on item click listener
		_callRecordHistoryListView
				.setOnItemClickListener(new CallRecordHistoryListViewOnItemClickListener());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(
				R.menu.call_record_history_list_tab_content_activity_layout,
				menu);
		return true;
	}

	@Override
	protected void onResume() {
		// clear call log list
		_mCallLogList.clear();

		// update call record history listView adapter
		((ListView) findViewById(R.id.callRecordHistoryList_listView))
				.setAdapter(generateCallRecordHistoryListItemAdapter());

		super.onResume();
	}

	// generate call record history list item adapter
	private ListAdapter generateCallRecordHistoryListItemAdapter() {
		// call record history list item adapter data keys
		final String CALL_RECORD_CALLTYPE = "call_record_callType";
		final String CALL_RECORD_DISPLAYNAME = "call_record_displayName";
		final String CALL_RECORD_PHONE = "call_record_phone";
		final String CALL_RECORD_INITIATETIME = "call_record_initiateTime";
		final String CALL_RECORD_DETAIL = "call_record_detailInfo";

		// set call record history list view data list
		List<Map<String, ?>> _callRecordHistoryDataList = new ArrayList<Map<String, ?>>();

		// get all call log and all added to call log list
		_mCallLogList.addAll(CallLogManager.getAllCallLogs());

		for (int i = 0; i < _mCallLogList.size(); i++) {
			// generate data
			Map<String, Object> _dataMap = new HashMap<String, Object>();

			// get call log bean
			CallLogBean _callLogBean = _mCallLogList.get(i);

			// get call type, callee display name and phone
			CallType _callType = _callLogBean.getCallType();
			String _calleeName = _callLogBean.getCalleeName();
			String _calleePhone = _callLogBean.getCalleePhone();

			// put value
			_dataMap.put(CALL_RECORD_CALLTYPE, _callType);
			_dataMap.put(CALL_RECORD_DISPLAYNAME,
					CallType.MISSED == _callType ? new SpannableString(
							_calleeName) : _calleeName);
			_dataMap.put(CALL_RECORD_PHONE,
					CallType.MISSED == _callType ? new SpannableString(
							_calleePhone) : _calleePhone);
			_dataMap.put(CALL_RECORD_INITIATETIME,
					formatCallRecordInitiateTime(_callLogBean.getCallDate()));

			// call record detail value map
			Map<String, Object> _callRecordDetailValueMap = new HashMap<String, Object>();
			_callRecordDetailValueMap.put(CALL_RECORD_IMAGEBUTTON_TAG, i);
			_callRecordDetailValueMap
					.put(CALL_RECORD_IMAGEBUTTON_ONCLICKLISTENER,
							new CallRecordHistoryListViewItemDetailImgBtnOnClickListener());
			_dataMap.put(CALL_RECORD_DETAIL, _callRecordDetailValueMap);

			// add data to list
			_callRecordHistoryDataList.add(_dataMap);
		}

		return new CallRecordHistoryListItemAdapter(this,
				_callRecordHistoryDataList,
				R.layout.call_record_historylist_item, new String[] {
						CALL_RECORD_CALLTYPE, CALL_RECORD_DISPLAYNAME,
						CALL_RECORD_PHONE, CALL_RECORD_INITIATETIME,
						CALL_RECORD_DETAIL }, new int[] {
						R.id.record_callType_imageView,
						R.id.record_displayName_textView,
						R.id.record_phone_textView,
						R.id.record_initiateTime_textView,
						R.id.recordDetail_imageBtn });
	}

	// format call record initiate time
	private String formatCallRecordInitiateTime(Long callDate) {
		// define return string builder
		StringBuilder _ret = new StringBuilder();

		// call record initiate time day and time format, format unix timeStamp
		final DateFormat _callRecordInitiateTimeDayFormat = new SimpleDateFormat(
				"yy-MM-dd", Locale.getDefault());
		final DateFormat _callRecordInitiateTimeTimeFormat = new SimpleDateFormat(
				"HH:mm:ss", Locale.getDefault());

		// format day and time
		_ret.append(_callRecordInitiateTimeDayFormat.format(callDate))
				.append("\n")
				.append(_callRecordInitiateTimeTimeFormat.format(callDate));

		return _ret.toString();
	}

	// inner class
	// call record history listView on item click listener
	class CallRecordHistoryListViewOnItemClickListener implements
			OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// get the click item view data: call log object
			CallLogBean _clickItemViewData = _mCallLogList.get((int) id);

			// define contact phone dial mode select popup window
			ContactPhoneDialModeSelectpopupWindow _contactPhoneDialModeSelectPopupWindow = new ContactPhoneDialModeSelectpopupWindow(
					R.layout.contact_phone_dialmode_select_popupwindow_layout,
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

			// get callee phone and name
			String _calleePhone = _clickItemViewData.getCalleePhone();
			String _calleeName = _clickItemViewData.getCalleeName();

			// check callee phone
			if (null == _calleePhone
					|| _calleePhone.trim().equalsIgnoreCase("")) {
				// show unknown callee phoneo alert dialog
				new AlertDialog.Builder(
						CallRecordHistoryListTabContentActivity.this)
						.setTitle(_calleeName)
						.setMessage(
								R.string.unknownCalleePhone_alertDialog_message)
						.setPositiveButton(
								R.string.unknownCalleePhone_alertDialog_reselectBtn_title,
								null).show();
			} else {
				// set callee contact info
				// generate callee phones
				@SuppressWarnings("unchecked")
				List<String> _calleePhones = (List<String>) CommonUtils
						.array2List(new String[] { _clickItemViewData
								.getCalleePhone() });

				// set callee contact info
				_contactPhoneDialModeSelectPopupWindow.setCalleeContactInfo(
						_calleeName, _calleePhones);

				// show contact phone dial mode select pupupWindow
				_contactPhoneDialModeSelectPopupWindow.showAtLocation(parent,
						Gravity.CENTER, 0, 0);
			}
		}

	}

	// call record history listView item detail image button on click listener
	class CallRecordHistoryListViewItemDetailImgBtnOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			Log.d(LOG_TAG, "view = " + v + " and position = " + v.getTag());

			// generate parameter
			Map<String, Serializable> _parameter = new HashMap<String, Serializable>();

			// put call log bean to parameter
			_parameter.put(CallRecordDetailInfoActivity.CALL_LOG_PARAM_KEY,
					_mCallLogList.get((Integer) v.getTag()));

			// go to the call record detail info activity
			pushActivity(CallRecordDetailInfoActivity.class, _parameter);
		}

	}

}
