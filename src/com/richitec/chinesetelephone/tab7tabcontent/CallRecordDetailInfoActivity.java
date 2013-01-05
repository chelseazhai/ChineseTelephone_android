package com.richitec.chinesetelephone.tab7tabcontent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.richitec.chinesetelephone.R;
import com.richitec.commontoolkit.activityextension.NavigationActivity;
import com.richitec.commontoolkit.calllog.CallLogBean;
import com.richitec.commontoolkit.calllog.CallLogBean.CallType;

public class CallRecordDetailInfoActivity extends NavigationActivity {

	private static final String LOG_TAG = "CallRecordDetailInfoActivity";

	// call record detail info param key
	public static final String CALL_LOG_PARAM_KEY = "call_log_bean";

	// callee name
	String _mCalleeName;

	// callee phone number
	String _mCalleePhone;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.call_record_detail_info_activity_layout);

		// set title
		setTitle(R.string.call_record_detailinfo_title);

		// get the intent parameter data
		Bundle _data = getIntent().getExtras();

		// check the data bundle
		if (null != _data) {
			// get and check call log bean
			if (null != _data.get(CALL_LOG_PARAM_KEY)) {
				CallLogBean _callLog = (CallLogBean) _data
						.get(CALL_LOG_PARAM_KEY);

				Log.d(LOG_TAG, "Call log bean = " + _callLog);

				// define call record initiate day and time time format
				final DateFormat _callRecordInitiateTimeDayFormat = new SimpleDateFormat(
						"yyyy"
								+ getResources().getString(
										R.string.callRecord_initiateTime_year)
								+ "MM"
								+ getResources().getString(
										R.string.callRecord_initiateTime_month)
								+ "dd"
								+ getResources().getString(
										R.string.callRecord_initiateTime_day),
						Locale.getDefault());
				final DateFormat _callRecordInitiateTimeTimeFormat = new SimpleDateFormat(
						"HH:mm", Locale.getDefault());

				// 60 seconds per minute
				final Integer SECONDS_PER_MINUTE = 60;

				// reset callee name and phone number
				_mCalleeName = _callLog.getCalleeName();
				_mCalleePhone = _callLog.getCalleePhone();

				// get call duration
				Long _callDuration = _callLog.getCallDuration();

				// update call record contact display name, call type, call day,
				// call time and call duration textView text
				((TextView) findViewById(R.id.callRecord_contact_displayName_textView))
						.setText(_mCalleeName);
				((TextView) findViewById(R.id.callRecord_callType_textView))
						.setText(CallType.OUTGOING == _callLog.getCallType() ? R.string.callRecord_outgoingCall_callType
								: R.string.callRecord_incomingCall_callType);
				((TextView) findViewById(R.id.callRecord_day_textView))
						.setText(_callRecordInitiateTimeDayFormat
								.format(_callLog.getCallDate()));
				((TextView) findViewById(R.id.callRecord_time_textView))
						.setText(_callRecordInitiateTimeTimeFormat
								.format(_callLog.getCallDate()));
				((TextView) findViewById(R.id.callRecord_duration_textView))
						.setText(CallType.MISSED == _callLog.getCallType() ? getResources()
								.getString(
										R.string.callRecord_missed_incomingCall)
								: _callDuration < 0 ? getResources()
										.getString(
												R.string.callRecord_failed_outgoingCall)
										: 0 == _callDuration ? getResources()
												.getString(
														R.string.callRecord_cancel_outgoingCall)
												: _callDuration < SECONDS_PER_MINUTE ? _callDuration
														+ " "
														+ getResources()
																.getString(
																		R.string.callRecord_duration_seconds)
														: 0 == _callDuration
																% SECONDS_PER_MINUTE ? _callDuration
																/ SECONDS_PER_MINUTE
																+ " "
																+ getResources()
																		.getString(
																				R.string.callRecord_duration_minutes)
																: (_callDuration
																		/ SECONDS_PER_MINUTE + 1)
																		+ " "
																		+ getResources()
																				.getString(
																						R.string.callRecord_duration_minutes));
			} else {
				Log.e(LOG_TAG, "Get call log error, call log bean is null");
			}
		}
	}

}
