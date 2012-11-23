package com.richitec.chinesetelephone.tab7tabcontent;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.richitec.chinesetelephone.R;
import com.richitec.commontoolkit.activityextension.AppLaunchActivity;
import com.richitec.commontoolkit.calllog.CallLogBean.CallType;
import com.richitec.commontoolkit.customadapter.CommonListAdapter;

public class CallRecordHistoryListItemAdapter extends CommonListAdapter {

	private static final String LOG_TAG = "CallRecordHistoryListItemAdapter";

	public CallRecordHistoryListItemAdapter(Context context,
			List<Map<String, ?>> data, int itemsLayoutResId, String[] dataKeys,
			int[] itemsComponentResIds) {
		super(context, data, itemsLayoutResId, dataKeys, itemsComponentResIds);
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
				_viewNewText.setSpan(new ForegroundColorSpan(AppLaunchActivity
						.getAppContext().getResources().getColor(R.color.red)),
						0, _viewNewText.length(),
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
				Map<String, Object> _itemDataMap = (Map<String, Object>) _itemData;

				// set image button attributes
				((ImageButton) view)
						.setTag(_itemDataMap
								.get(CallRecordHistoryListTabContentActivity.CALL_RECORD_IMAGEBUTTON_TAG));
				((ImageButton) view)
						.setOnClickListener((OnClickListener) _itemDataMap
								.get(CallRecordHistoryListTabContentActivity.CALL_RECORD_IMAGEBUTTON_ONCLICKLISTENER));
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

}
