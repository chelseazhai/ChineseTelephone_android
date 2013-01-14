package com.richitec.chinesetelephone.tab7tabcontent;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.call.ContactPhoneDialModeSelectpopupWindow;
import com.richitec.chinesetelephone.constant.SystemConstants;
import com.richitec.commontoolkit.activityextension.NavigationActivity;
import com.richitec.commontoolkit.calllog.CallLogBean;
import com.richitec.commontoolkit.calllog.CallLogManager;
import com.richitec.commontoolkit.utils.CommonUtils;

public class CallRecordHistoryListTabContentActivity extends NavigationActivity {

	// call record history list item adapter data keys
	public static final String CALL_RECORD_CALLTYPE = "call_record_callType";
	public static final String CALL_RECORD_DISPLAYNAME = "call_record_displayName";
	public static final String CALL_RECORD_PHONE = "call_record_phone";
	public static final String CALL_RECORD_INITIATETIME = "call_record_initiateTime";
	public static final String CALL_RECORD_DETAIL = "call_record_detailInfo";

	// call record history listView
	private ListView _mCallRecordHistoryListView;

	// call log need to reload flag
	private boolean _mCallLogNeed2Reload;

	// call log content observer
	private final CallLogContentObserver CALLLOG_CONTENTOBSERVER = new CallLogContentObserver();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(SystemConstants.TAG, "CallRecordHistoryListTabContentActivity - onCreate");
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.call_record_history_list_tab_content_activity_layout);

		// set title
		setTitle(R.string.call_record_history_list_nav_title);

		// get call record history listView
		_mCallRecordHistoryListView = (ListView) findViewById(R.id.callRecordHistoryList_listView);

		// set call record history listView tag
		_mCallRecordHistoryListView.setTag(this);

		// set call record history listView adapter
		_mCallRecordHistoryListView
				.setAdapter(new CallRecordHistoryListItemAdapter(this,
						R.layout.call_record_historylist_item, CallLogManager
								.getAllCallLogQueryCursor(), new String[] {
								CALL_RECORD_CALLTYPE, CALL_RECORD_DISPLAYNAME,
								CALL_RECORD_PHONE, CALL_RECORD_INITIATETIME,
								CALL_RECORD_DETAIL }, new int[] {
								R.id.record_callType_imageView,
								R.id.record_displayName_textView,
								R.id.record_phone_textView,
								R.id.record_initiateTime_textView,
								R.id.recordDetail_imageBtn }));

		// set call record history listView on item click listener
		_mCallRecordHistoryListView
				.setOnItemClickListener(new CallRecordHistoryListViewOnItemClickListener());

		// add call log changed ContentObserver
		getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI,
				false, CALLLOG_CONTENTOBSERVER);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(
				R.menu.call_record_history_list_tab_content_activity_layout,
				menu);
		return true;
	}
	
	@Override
    public void onBackPressed(){
    	this.getParent().onBackPressed();
    }

	protected void onSaveInstanceState (Bundle outState) {
		Log.d(SystemConstants.TAG, "CallRecordHistoryListTabContentActivity  - onSaveInstanceState");
	}
	
	@Override
	protected void onStop() {
		Log.d(SystemConstants.TAG, "CallRecordHistoryListTabContentActivity - onStop");
		super.onStop();
	}
	
	@Override
    protected void onPause() {
		Log.d(SystemConstants.TAG, "CallRecordHistoryListTabContentActivity - onPause");
		super.onPause();
    }
	
	@Override
	protected void onRestart() {
		Log.d(SystemConstants.TAG, "CallRecordHistoryListTabContentActivity - onRestart");
		super.onRestart();
	}

	@Override
	protected void onStart() {
		Log.d(SystemConstants.TAG, "CallRecordHistoryListTabContentActivity - onStart");
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		Log.d(SystemConstants.TAG, "CallRecordHistoryListTabContentActivity - onResume");
		// check call log need to reload flag
		if (_mCallLogNeed2Reload) {
			// reset call log need to reload flag
			_mCallLogNeed2Reload = false;

			// update call record history listView adapter
			_mCallRecordHistoryListView
					.setAdapter(new CallRecordHistoryListItemAdapter(this,
							R.layout.call_record_historylist_item,
							CallLogManager.getAllCallLogQueryCursor(),
							new String[] { CALL_RECORD_CALLTYPE,
									CALL_RECORD_DISPLAYNAME, CALL_RECORD_PHONE,
									CALL_RECORD_INITIATETIME,
									CALL_RECORD_DETAIL }, new int[] {
									R.id.record_callType_imageView,
									R.id.record_displayName_textView,
									R.id.record_phone_textView,
									R.id.record_initiateTime_textView,
									R.id.recordDetail_imageBtn }));
		}

		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// remove call log changed ContentObserver
		getContentResolver().unregisterContentObserver(CALLLOG_CONTENTOBSERVER);

		super.onDestroy();
	}

	// generate call record history listView item detail image button on click
	// listener
	protected OnClickListener generateCallRecordHistoryListViewItemDetailImgBtnOnClickListener() {
		return new CallRecordHistoryListViewItemDetailImgBtnOnClickListener();
	}

	
	
	// inner class
	// call record history listView on item click listener
	class CallRecordHistoryListViewOnItemClickListener implements
			OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// get the click item view data: call log object
			CallLogBean _clickItemViewData = (CallLogBean) ((CallRecordHistoryListItemAdapter) _mCallRecordHistoryListView
					.getAdapter()).getDataList().get(position);

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
								R.string.callRecord_unknownCalleePhone_alertDialog_message)
						.setPositiveButton(
								R.string.callRecord_unknownCalleePhone_alertDialog_reselectBtn_title,
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
			// generate parameter
			Map<String, Serializable> _parameter = new HashMap<String, Serializable>();

			// put call log bean to parameter
			_parameter
					.put(CallRecordDetailInfoActivity.CALL_LOG_PARAM_KEY,
							(CallLogBean) ((CallRecordHistoryListItemAdapter) _mCallRecordHistoryListView
									.getAdapter()).getDataList().get(
									(Integer) v.getTag()));

			// go to the call record detail info activity
			pushActivity(CallRecordDetailInfoActivity.class, _parameter);
		}

	}

	// call log db changed observer
	class CallLogContentObserver extends ContentObserver {

		public CallLogContentObserver() {
			super(new Handler());
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);

			// call log need to reload
			_mCallLogNeed2Reload = true;
		}

	}

}
