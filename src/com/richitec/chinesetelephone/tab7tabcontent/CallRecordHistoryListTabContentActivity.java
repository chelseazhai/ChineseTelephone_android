package com.richitec.chinesetelephone.tab7tabcontent;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.call.ContactPhoneDialModeSelectpopupWindow;
import com.richitec.commontoolkit.CTApplication;
import com.richitec.commontoolkit.activityextension.NavigationActivity;
import com.richitec.commontoolkit.call.CallLogBean;
import com.richitec.commontoolkit.call.CallLogBean.CallType;
import com.richitec.commontoolkit.call.CallLogManager;
import com.richitec.commontoolkit.customadapter.CTListCursorAdapter;
import com.richitec.commontoolkit.utils.CommonUtils;

public class CallRecordHistoryListTabContentActivity extends NavigationActivity {

	// call record history listView
	private ListView _mCallRecordHistoryListView;

	// need to saved call log data list
	private List<Object> _mNeed2SavedCallLogDataList;

	// call log need to reload flag
	private boolean _mCallLogNeed2Reload;

	// call log content observer
	private final CallLogContentObserver CALLLOG_CONTENTOBSERVER = new CallLogContentObserver();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.call_record_history_list_tab_content_activity_layout);

		// set title
		setTitle(R.string.call_record_history_list_nav_title);

		// get call record history listView
		_mCallRecordHistoryListView = (ListView) findViewById(R.id.callRecordHistoryList_listView);

		// set call record history listView adapter
		_mCallRecordHistoryListView
				.setAdapter(new CallRecordHistoryListItemAdapter(
						this,
						R.layout.call_record_historylist_item,
						CallLogManager.getAllCallLogQueryCursor(),
						new String[] {
								CallRecordHistoryListItemAdapter.CALL_RECORD_CALLTYPE,
								CallRecordHistoryListItemAdapter.CALL_RECORD_DISPLAYNAME,
								CallRecordHistoryListItemAdapter.CALL_RECORD_PHONE,
								CallRecordHistoryListItemAdapter.CALL_RECORD_INITIATETIME,
								CallRecordHistoryListItemAdapter.CALL_RECORD_DETAIL },
						new int[] { R.id.record_callType_imageView,
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
	protected void onResume() {
		// check call log need to reload flag
		if (_mCallLogNeed2Reload) {
			// reset call log need to reload flag
			_mCallLogNeed2Reload = false;

			// update call record history listView adapter
			_mCallRecordHistoryListView
					.setAdapter(new CallRecordHistoryListItemAdapter(
							this,
							R.layout.call_record_historylist_item,
							CallLogManager.getAllCallLogQueryCursor(),
							new String[] {
									CallRecordHistoryListItemAdapter.CALL_RECORD_CALLTYPE,
									CallRecordHistoryListItemAdapter.CALL_RECORD_DISPLAYNAME,
									CallRecordHistoryListItemAdapter.CALL_RECORD_PHONE,
									CallRecordHistoryListItemAdapter.CALL_RECORD_INITIATETIME,
									CallRecordHistoryListItemAdapter.CALL_RECORD_DETAIL },
							new int[] { R.id.record_callType_imageView,
									R.id.record_displayName_textView,
									R.id.record_phone_textView,
									R.id.record_initiateTime_textView,
									R.id.recordDetail_imageBtn }));
		} else {
			// check and set call record history list item adapter data list
			if (null != _mNeed2SavedCallLogDataList) {
				// define call record history listView adapter
				CallRecordHistoryListItemAdapter _callRecordHistoryListItemAdapter = null;

				// set call record history listView adapter data list
				try {
					// get call record history listView adapter
					_callRecordHistoryListItemAdapter = (CallRecordHistoryListItemAdapter) _mCallRecordHistoryListView
							.getAdapter();

					_callRecordHistoryListItemAdapter
							.setData(_mNeed2SavedCallLogDataList);
				} catch (Exception e) {
					//
				}
			}
		}

		super.onResume();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// get restore call log data list
		@SuppressWarnings("unchecked")
		List<Object> _callLogDataList = (List<Object>) savedInstanceState
				.getSerializable("@@");

		// check call log data list
		if (null != _callLogDataList) {
			// restore need to saved call log data list
			_mNeed2SavedCallLogDataList = _callLogDataList;
		}

		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// define call record history listView adapter
		CallRecordHistoryListItemAdapter _callRecordHistoryListItemAdapter = null;

		// save call log data list
		try {
			// get call record history listView adapter
			_callRecordHistoryListItemAdapter = (CallRecordHistoryListItemAdapter) _mCallRecordHistoryListView
					.getAdapter();

			// get call log data list
			List<Object> _callLogDataList = _callRecordHistoryListItemAdapter
					.getDataList();

			// check call log data list and saved
			if (null != _callLogDataList) {
				outState.putSerializable("@@", (Serializable) _callLogDataList);

				// save UI data
				super.onSaveInstanceState(outState);
			}
		} catch (Exception e) {
			//
		}
	}

	@Override
	protected void onDestroy() {
		// remove call log changed ContentObserver
		getContentResolver().unregisterContentObserver(CALLLOG_CONTENTOBSERVER);

		super.onDestroy();
	}

	// inner class
	// call record history list item adapter
	class CallRecordHistoryListItemAdapter extends CTListCursorAdapter {

		private static final String LOG_TAG = "CallRecordHistoryListItemAdapter";

		// call record history list item adapter data keys
		private static final String CALL_RECORD_CALLTYPE = "call_record_callType";
		private static final String CALL_RECORD_DISPLAYNAME = "call_record_displayName";
		private static final String CALL_RECORD_PHONE = "call_record_phone";
		private static final String CALL_RECORD_INITIATETIME = "call_record_initiateTime";
		private static final String CALL_RECORD_DETAIL = "call_record_detailInfo";

		public CallRecordHistoryListItemAdapter(Context context,
				int itemsLayoutResId, Cursor c, String[] dataKeys,
				int[] itemsComponentResIds) {
			super(context, itemsLayoutResId, c, dataKeys, itemsComponentResIds);
		}

		@Override
		protected void appendCursorData(List<Object> data, Cursor cursor) {
			// check call log query cursor
			if (null != cursor) {
				// get call log object
				CallLogBean _callLog = CallLogManager
						.getCallLogFromCursor(cursor);

				// Log.d(LOG_TAG, "Get call log from cursor, call log bean = "
				// + _callLog);

				// add to data
				data.add(_callLog);
			} else {
				Log.e(LOG_TAG, "Call log query cursor is null");
			}
		}

		@Override
		protected Map<String, ?> recombinationData(String dataKey,
				Object dataObject) {
			// define data value and map
			Object _dataValue = null;
			Map<String, Object> _dataMap = new HashMap<String, Object>();

			// check data object
			try {
				// define call log bean data and convert data to call log bean
				CallLogBean _dataCallLogBean = (CallLogBean) dataObject;

				// check data key and update data value
				if (CALL_RECORD_CALLTYPE.equalsIgnoreCase(dataKey)) {
					// call type
					_dataValue = _dataCallLogBean.getCallType();
				} else if (CALL_RECORD_DISPLAYNAME.equalsIgnoreCase(dataKey)) {
					// callee display name
					// get call type and callee name
					CallType _callType = _dataCallLogBean.getCallType();
					String _calleeName = _dataCallLogBean.getCalleeName();

					_dataValue = CallType.MISSED == _callType ? new SpannableString(
							_calleeName) : _calleeName;
				} else if (CALL_RECORD_PHONE.equalsIgnoreCase(dataKey)) {
					// callee phone number
					// get call type and callee phone
					CallType _callType = _dataCallLogBean.getCallType();
					String _calleePhone = _dataCallLogBean.getCalleePhone();

					_dataValue = CallType.MISSED == _callType ? new SpannableString(
							_calleePhone) : _calleePhone;
				} else if (CALL_RECORD_INITIATETIME.equalsIgnoreCase(dataKey)) {
					// call initiate time
					_dataValue = formatCallRecordInitiateTime(_dataCallLogBean
							.getCallDate());
				} else if (CALL_RECORD_DETAIL.equalsIgnoreCase(dataKey)) {
					// call record detail
					// call record detail value map
					SparseArray<OnClickListener> _callRecordDetailValueMap = new SparseArray<View.OnClickListener>();

					// append call detail info button on clicked listener
					_callRecordDetailValueMap
							.append(_mData.indexOf(_dataCallLogBean),
									new CallRecordHistoryListViewItemDetailImgBtnOnClickListener());

					_dataValue = _callRecordDetailValueMap;
				} else {
					Log.e(LOG_TAG, "Recombination data error, data key = "
							+ dataKey + " and data object = " + dataObject);
				}
			} catch (Exception e) {
				e.printStackTrace();

				Log.e(LOG_TAG,
						"Convert data to call log bean object error, data = "
								+ dataObject);
			}

			// put data value to map and return
			_dataMap.put(dataKey, _dataValue);
			return _dataMap;
		}

		@Override
		protected void bindView(View view, Map<String, ?> dataMap,
				String dataKey) {
			// get item data object
			Object _itemData = dataMap.get(dataKey);

			// check view type
			// textView
			if (view instanceof TextView) {
				// generate view text
				SpannableString _viewNewText = new SpannableString(
						null == _itemData ? "" : _itemData.toString());

				// check data class name
				if (_itemData instanceof SpannableString) {
					_viewNewText.setSpan(
							new ForegroundColorSpan(CTApplication.getContext()
									.getResources().getColor(R.color.red)), 0,
							_viewNewText.length(),
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}

				// set view text
				((TextView) view).setText(_viewNewText);
			}
			// image button
			else if (view instanceof ImageButton) {
				try {
					// define item data map and convert item data to map
					@SuppressWarnings("unchecked")
					SparseArray<OnClickListener> _itemDataMap = (SparseArray<OnClickListener>) _itemData;

					// set image button attributes
					((ImageButton) view).setTag(_itemDataMap.keyAt(0));
					((ImageButton) view).setOnClickListener(_itemDataMap
							.get(_itemDataMap.keyAt(0)));
				} catch (Exception e) {
					e.printStackTrace();

					Log.e(LOG_TAG,
							"Convert item data to map error, item data = "
									+ _itemData);
				}
			}
			// image view
			else if (view instanceof ImageView) {
				try {
					// define item data CallType and convert item data to
					// CallType
					CallType _itemCallType = (CallType) _itemData;

					// set image view resource
					switch (_itemCallType) {
					case INCOMING:
						((ImageView) view)
								.setImageResource(android.R.drawable.sym_call_incoming);
						break;

					case OUTGOING:
						((ImageView) view)
								.setImageResource(android.R.drawable.sym_call_outgoing);
						break;

					case MISSED:
					default:
						((ImageView) view)
								.setImageResource(android.R.drawable.sym_call_missed);
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();

					Log.e(LOG_TAG,
							"Convert item data to CallType error, item data = "
									+ _itemData);
				}
			}
		}

		// format call record initiate time
		private String formatCallRecordInitiateTime(Long callDate) {
			// define return string builder
			StringBuilder _ret = new StringBuilder();

			// call record initiate time day and time format, format timeStamp
			final DateFormat _callRecordInitiateTimeDayFormat = new SimpleDateFormat(
					"yy-MM-dd", Locale.getDefault());
			final DateFormat _callRecordInitiateTimeTimeFormat = new SimpleDateFormat(
					"HH:mm", Locale.getDefault());

			// miliSceonds of day
			Long MILLISECONDS_PER_DAY = 24 * 60 * 60 * 1000L;

			// get current system time
			Long _currentSystemTime = System.currentTimeMillis();

			// compare current system time and call date
			if (_currentSystemTime - callDate >= 0) {
				// get today zero o'clock calendar instance
				Calendar _todayZeroCalendarInstance = Calendar
						.getInstance(Locale.getDefault());
				_todayZeroCalendarInstance.set(Calendar.AM_PM, 0);
				_todayZeroCalendarInstance.set(Calendar.HOUR, 0);
				_todayZeroCalendarInstance.set(Calendar.MINUTE, 0);
				_todayZeroCalendarInstance.set(Calendar.SECOND, 0);
				_todayZeroCalendarInstance.set(Calendar.MILLISECOND, 0);

				// get call date calendar instance
				Calendar _callDateCalendarInstance = Calendar
						.getInstance(Locale.getDefault());
				_callDateCalendarInstance.setTimeInMillis(callDate);

				// format day and time
				if (_callDateCalendarInstance
						.before(_todayZeroCalendarInstance)) {
					// get today zero o'clock and call date time different
					Long _today7callDateCalendarTimeDifferent = _todayZeroCalendarInstance
							.getTimeInMillis()
							- _callDateCalendarInstance.getTimeInMillis();

					// get application context
					Context _appContext = CTApplication.getContext();

					// check time different
					if (_today7callDateCalendarTimeDifferent <= MILLISECONDS_PER_DAY) {
						_ret.append(_appContext.getResources().getString(
								R.string.yesterdayCallRecord_callDate));
					} else {
						// get first day zero o'clock of week calendar instance
						Calendar _firstDayOfWeekZeroCalendarInstance = Calendar
								.getInstance(Locale.getDefault());
						_firstDayOfWeekZeroCalendarInstance
								.setTimeInMillis(_todayZeroCalendarInstance
										.getTimeInMillis());
						_firstDayOfWeekZeroCalendarInstance.set(
								Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

						if (_callDateCalendarInstance
								.before(_firstDayOfWeekZeroCalendarInstance)) {
							_ret.append(_callRecordInitiateTimeDayFormat
									.format(callDate));
						} else {
							_ret.append(_appContext
									.getResources()
									.getStringArray(
											R.array.callRecord_callDate_daysOfWeek)[_callDateCalendarInstance
									.get(Calendar.DAY_OF_WEEK) - 1]);
						}
					}
				} else {
					_ret.append(_callRecordInitiateTimeTimeFormat
							.format(callDate));
				}
			} else {
				Log.e(LOG_TAG,
						"Format call record initiate time error, call date greater than current system time");
			}

			return _ret.toString();
		}

	}

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

				// show contact phone dial mode select pupupWindow with
				// animation
				_contactPhoneDialModeSelectPopupWindow
						.showAtLocationWithAnimation(parent, Gravity.CENTER, 0,
								0);
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
