package com.richitec.chinesetelephone.tab7tabcontent;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

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
			// set view text
			((TextView) view)
					.setText(null == _itemData ? ""
							: _itemData instanceof SpannableString ? (SpannableString) _itemData
									: _itemData.toString());
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
	}

}
