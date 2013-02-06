package com.richitec.chinesetelephone.tab7tabcontent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.call.SipCallMode;
import com.richitec.chinesetelephone.sip.SipUtils;
import com.richitec.chinesetelephone.utils.AppDataSaveRestoreUtil;
import com.richitec.commontoolkit.activityextension.NavigationActivity;
import com.richitec.commontoolkit.addressbook.AddressBookManager;
import com.richitec.commontoolkit.call.CallLogBean;
import com.richitec.commontoolkit.call.CallLogBean.CallType;
import com.richitec.commontoolkit.customadapter.CTListAdapter;
import com.richitec.commontoolkit.utils.CommonUtils;

public class CallRecordDetailInfoActivity extends NavigationActivity {

	private static final String LOG_TAG = CallRecordDetailInfoActivity.class
			.getCanonicalName();

	// call record detail info param key
	public static final String CALL_LOG_PARAM_KEY = "call_log_bean";

	// callee name
	private String _mCalleeName;

	// callee phone number
	private String _mCalleePhone;

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

				// define call record detail info initiate day and time time
				// format
				final DateFormat _callRecordInitiateTimeDayFormat = new SimpleDateFormat(
						"yyyy"
								+ getResources()
										.getString(
												R.string.callRecord_detailInfo_initiateTime_year)
								+ "MM"
								+ getResources()
										.getString(
												R.string.callRecord_detailInfo_initiateTime_month)
								+ "dd"
								+ getResources()
										.getString(
												R.string.callRecord_detailInfo_initiateTime_day),
						Locale.getDefault());
				final DateFormat _callRecordInitiateTimeTimeFormat = new SimpleDateFormat(
						"HH:mm", Locale.getDefault());

				// 60 seconds per minute
				final Integer SECONDS_PER_MINUTE = 60;

				// define callee photo bitmap
				Bitmap _calleePhotoBitmap = ((BitmapDrawable) getResources()
						.getDrawable(R.drawable.img_gray_avatar)).getBitmap();

				// reset callee name and phone number
				_mCalleeName = _callLog.getCalleeName();
				_mCalleePhone = _callLog.getCalleePhone();

				// get callee phone and call duration
				String _calleePhone = _callLog.getCalleePhone();
				Long _callDuration = _callLog.getCallDuration();

				// get address book manager reference
				AddressBookManager _addressBookManager = AddressBookManager
						.getInstance();

				// is callee in address book
				@SuppressWarnings("unchecked")
				Long _calleeContactId = _addressBookManager
						.isContactWithPhoneInAddressBook(_calleePhone);

				// check callee contact id
				if (null != _calleeContactId) {
					// get callee photo, check it and reset callee photo bitmap
					byte[] _calleePhoto = _addressBookManager
							.getContactByAggregatedId(_calleeContactId)
							.getPhoto();
					if (null != _calleePhoto) {
						try {
							// get photo data stream
							InputStream _photoDataStream = new ByteArrayInputStream(
									_calleePhoto);

							// check photo data stream
							if (null != _photoDataStream) {
								_calleePhotoBitmap = BitmapFactory
										.decodeStream(_photoDataStream);

								// close photo data stream
								_photoDataStream.close();
							}
						} catch (IOException e) {
							e.printStackTrace();

							Log.e(LOG_TAG,
									"Get callee photo data stream error, error message = "
											+ e.getMessage());
						}
					}
				}

				// init call record contact photo, display name, call type, call
				// day, call time and call duration textView text
				((ImageView) findViewById(R.id.callRecord_detailInfo_contact_avatar_imageView))
						.setImageBitmap(_calleePhotoBitmap);
				((TextView) findViewById(R.id.callRecord_detailInfo_contact_displayName_textView))
						.setText(_mCalleeName);
				((TextView) findViewById(R.id.callRecord_detailInfo_callType_textView))
						.setText(CallType.OUTGOING == _callLog.getCallType() ? R.string.callRecord_detailInfo_outgoingCall_callType
								: R.string.callRecord_detailInfo_incomingCall_callType);
				((TextView) findViewById(R.id.callRecord_detailInfo_day_textView))
						.setText(_callRecordInitiateTimeDayFormat
								.format(_callLog.getCallDate()));
				((TextView) findViewById(R.id.callRecord_detailInfo_time_textView))
						.setText(_callRecordInitiateTimeTimeFormat
								.format(_callLog.getCallDate()));
				ImageView durationIcon = (ImageView) findViewById(R.id.callRecord_detailInfo_duration_type_icon);
				if (CallType.MISSED == _callLog.getCallType()) {
					durationIcon.setImageResource(android.R.drawable.sym_call_missed);
				} else if (CallType.INCOMING == _callLog.getCallType()) {
					durationIcon.setImageResource(android.R.drawable.sym_call_incoming);
				} else if (CallType.OUTGOING == _callLog.getCallType()) {
					durationIcon.setImageResource(android.R.drawable.sym_call_outgoing);
				} else {
					durationIcon.setImageDrawable(null);
				}
				
				((TextView) findViewById(R.id.callRecord_detailInfo_duration_textView))
						.setText(CallType.MISSED == _callLog.getCallType() ? getResources()
								.getString(
										R.string.callRecord_detailInfo_missed_incomingCall)
								: _callDuration < 0 ? -1 == _callDuration ? getResources()
										.getString(
												R.string.callRecord_detailInfo_failed_outgoingCall)
										: getResources()
												.getString(
														R.string.callRecord_detailInfo_callbackRequest_outgoingCall)
										: 0 == _callDuration ? getResources()
												.getString(
														R.string.callRecord_detailInfo_cancel_outgoingCall)
												: _callDuration < SECONDS_PER_MINUTE ? _callDuration
														+ " "
														+ getResources()
																.getString(
																		R.string.callRecord_detailInfo_duration_seconds)
														: 0 == _callDuration
																% SECONDS_PER_MINUTE ? _callDuration
																/ SECONDS_PER_MINUTE
																+ " "
																+ getResources()
																		.getString(
																				R.string.callRecord_detailInfo_duration_minutes)
																: (_callDuration
																		/ SECONDS_PER_MINUTE + 1)
																		+ " "
																		+ getResources()
																				.getString(
																						R.string.callRecord_detailInfo_duration_minutes));

				// get call record detail info operation listView
				ListView _callRecordDetailInfoOperationListView = (ListView) findViewById(R.id.callRecord_detailInfo_operation_listView);

				// check callee phone
				if (null == _calleePhone
						|| _calleePhone.trim().equalsIgnoreCase("")) {
					Log.d(LOG_TAG, "Call record detail info operation phone = "
							+ _calleePhone);
				} else {
					// set operation listView adapter and on item click listener
					_callRecordDetailInfoOperationListView
							.setAdapter(generateCallRecordDetailInfoOperationAdapter(_calleePhone));

					_callRecordDetailInfoOperationListView
							.setOnItemClickListener(new CallRecordDetailInfoOperationListViewOnItemClickListener());
				}
			} else {
				Log.e(LOG_TAG, "Get call log error, call log bean is null");
			}
		}
	}

	// generate call record detail info operation adapter
	private ListAdapter generateCallRecordDetailInfoOperationAdapter(
			String operationPhone) {
		// call record detail info operation adapter data keys
		final String OPERATION_TIP = "operation_tip";
		final String OPERATION_PHONE = "operation_phone";

		// call record detail info operation tip array
		final int[] OPERATION_TIPS = new int[] {
				R.string.callRecord_detailInfo_operation4directdial,
				R.string.callRecord_detailInfo_operation4callback,
				R.string.callRecord_detailInfo_operation4sms };

		// set call record detail info operation list view data list
		List<Map<String, ?>> _callRecordDetailInfoOperationdataList = new ArrayList<Map<String, ?>>();

		for (int operationTipInteger : OPERATION_TIPS) {
			// generate data
			Map<String, Object> _dataMap = new HashMap<String, Object>();

			// put value
			_dataMap.put(OPERATION_TIP,
					getResources().getString(operationTipInteger));
			_dataMap.put(OPERATION_PHONE, operationPhone);

			// add data to list
			_callRecordDetailInfoOperationdataList.add(_dataMap);
		}

		return new CallRecordDetailInfoOperationAdapter(this,
				_callRecordDetailInfoOperationdataList,
				R.layout.call_record_detail_info_operationlist_item_layout,
				new String[] { OPERATION_TIP, OPERATION_PHONE }, new int[] {
						R.id.callRecord_detailInfo_operationTip_textView,
						R.id.callRecord_detailInfo_operationPhone_textView });
	}

	// inner class
	// call record detail info operation adapter
	class CallRecordDetailInfoOperationAdapter extends CTListAdapter {

		public CallRecordDetailInfoOperationAdapter(Context context,
				List<Map<String, ?>> data, int itemsLayoutResId,
				String[] dataKeys, int[] itemsComponentResIds) {
			super(context, data, itemsLayoutResId, dataKeys,
					itemsComponentResIds);
		}

		@Override
		protected void bindView(View view, Map<String, ?> dataMap,
				String dataKey) {
			// get item data object
			Object _itemData = dataMap.get(dataKey);

			// check view type
			// textView
			if (view instanceof TextView) {
				// set view text
				((TextView) view)
						.setText(null == _itemData ? ""
								: _itemData instanceof SpannableString ? (SpannableString) _itemData
										: _itemData.toString());
			}
		}

	}

	// call record detail info operation listView on item click listener
	class CallRecordDetailInfoOperationListViewOnItemClickListener implements
			OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// check clicked position
			switch (position) {
			case 0:
				// direct call
				SipUtils.makeSipVoiceCall(_mCalleeName, _mCalleePhone,
						SipCallMode.DIRECT_CALL);
				break;

			case 1:
				// call back
				SipUtils.makeSipVoiceCall(_mCalleeName, _mCalleePhone,
						SipCallMode.CALLBACK);
				break;

			case 2:
			default:
				// send short message
				// define send short message intent
				Intent _smsIntent = new Intent(Intent.ACTION_SENDTO,
						Uri.parse("smsto:" + _mCalleePhone));

				// check send short message intent and start the activity
				if (CommonUtils.isIntentAvailable(_smsIntent)) {
					startActivity(_smsIntent);
				}

				break;
			}
		}

	}

	@Override
	protected void onRestoreInstanceState (Bundle savedInstanceState) {
		AppDataSaveRestoreUtil.onRestoreInstanceState(savedInstanceState);
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	@Override
	protected void onSaveInstanceState (Bundle outState) {
		AppDataSaveRestoreUtil.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}
}
