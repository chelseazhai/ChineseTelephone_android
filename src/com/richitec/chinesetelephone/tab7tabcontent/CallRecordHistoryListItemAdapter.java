package com.richitec.chinesetelephone.tab7tabcontent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.richitec.chinesetelephone.R;
import com.richitec.commontoolkit.CommonToolkitApplication;
import com.richitec.commontoolkit.calllog.CallLogBean;
import com.richitec.commontoolkit.calllog.CallLogBean.CallType;
import com.richitec.commontoolkit.calllog.CallLogManager;
import com.richitec.commontoolkit.customadapter.CommonListCursorAdapter;

public class CallRecordHistoryListItemAdapter extends CommonListCursorAdapter {

	private static final String LOG_TAG = "CallRecordHistoryListItemAdapter";

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
			CallLogBean _callLog = CallLogManager.getCallLogFromCursor(cursor);

			// Log.d(LOG_TAG, "Get call log from cursor, call log bean = "
			// + _callLog);

			// add to data
			data.add(_callLog);
		} else {
			Log.e(LOG_TAG, "Call log query cursor is null");
		}
	}

	@Override
	protected Map<String, ?> recombinationData(String dataKey, Object dataObject) {
		// define data value and map
		Object _dataValue = null;
		Map<String, Object> _dataMap = new HashMap<String, Object>();

		// check data object
		try {
			// define call log bean data and convert data to call log bean
			CallLogBean _dataCallLogBean = (CallLogBean) dataObject;

			// check data key and update data value
			if (CallRecordHistoryListTabContentActivity.CALL_RECORD_CALLTYPE
					.equalsIgnoreCase(dataKey)) {
				// call type
				_dataValue = _dataCallLogBean.getCallType();
			} else if (CallRecordHistoryListTabContentActivity.CALL_RECORD_DISPLAYNAME
					.equalsIgnoreCase(dataKey)) {
				// callee display name
				// get call type and callee name
				CallType _callType = _dataCallLogBean.getCallType();
				String _calleeName = _dataCallLogBean.getCalleeName();

				_dataValue = CallType.MISSED == _callType ? new SpannableString(
						_calleeName) : _calleeName;
			} else if (CallRecordHistoryListTabContentActivity.CALL_RECORD_PHONE
					.equalsIgnoreCase(dataKey)) {
				// callee phone number
				// get call type and callee phone
				CallType _callType = _dataCallLogBean.getCallType();
				String _calleePhone = _dataCallLogBean.getCalleePhone();

				_dataValue = CallType.MISSED == _callType ? new SpannableString(
						_calleePhone) : _calleePhone;
			} else if (CallRecordHistoryListTabContentActivity.CALL_RECORD_INITIATETIME
					.equalsIgnoreCase(dataKey)) {
				// call initiate time
				_dataValue = formatCallRecordInitiateTime(_dataCallLogBean
						.getCallDate());
			} else if (CallRecordHistoryListTabContentActivity.CALL_RECORD_DETAIL
					.equalsIgnoreCase(dataKey)) {
				// call record detail
				// call record detail value map
				SparseArray<OnClickListener> _callRecordDetailValueMap = new SparseArray<View.OnClickListener>();

				// get and check call detail info button on clicked listener
				OnClickListener _callRecordHistoryListViewItemDetailImgBtnOnClickListener = null;
				try {
					_callRecordHistoryListViewItemDetailImgBtnOnClickListener = ((CallRecordHistoryListTabContentActivity) _mContext)
							.generateCallRecordHistoryListViewItemDetailImgBtnOnClickListener();
				} catch (Exception e) {
					e.printStackTrace();

					Log.e(LOG_TAG,
							"Get call detail info button on clicked listener error, exception = "
									+ e.getMessage());
				}

				// append call detail info button on clicked listener
				_callRecordDetailValueMap
						.append(_data.indexOf(_dataCallLogBean),
								_callRecordHistoryListViewItemDetailImgBtnOnClickListener);

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
	protected void bindView(View view, Map<String, ?> dataMap, String dataKey) {
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
				_viewNewText.setSpan(new ForegroundColorSpan(
						CommonToolkitApplication.getContext().getResources()
								.getColor(R.color.red)), 0, _viewNewText
						.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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

				Log.e(LOG_TAG, "Convert item data to map error, item data = "
						+ _itemData);
			}
		}
		// image view
		else if (view instanceof ImageView) {
			try {
				// define item data CallType and convert item data to CallType
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

}
