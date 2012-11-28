package com.richitec.chinesetelephone.call;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.richitec.commontoolkit.customadapter.CommonListAdapter;

public class OutgoingCallKeyboardAdapter extends CommonListAdapter {

	private static final String LOG_TAG = "OutgoingCallKeyboardAdapter";

	public OutgoingCallKeyboardAdapter(Context context,
			List<Map<String, ?>> data, int itemsLayoutResId, String[] dataKeys,
			int[] itemsComponentResIds) {
		super(context, data, itemsLayoutResId, dataKeys, itemsComponentResIds);
	}

	@Override
	protected void bindView(View view, Map<String, ?> dataMap, String dataKey) {
		// get item data object
		Object _itemData = dataMap.get(dataKey);

		// check view type
		// image button
		if (view instanceof ImageButton) {
			try {
				// define item data map and convert item data to map
				@SuppressWarnings("unchecked")
				Map<String, Object> _itemDataMap = (Map<String, Object>) _itemData;

				// set image button attributes
				((ImageButton) view).setTag(_itemDataMap
						.get(OutgoingCallActivity.KEYBOARD_BUTTON_CODE));
				((ImageButton) view).setImageResource((Integer) _itemDataMap
						.get(OutgoingCallActivity.KEYBOARD_BUTTON_IMAGE));
				((ImageButton) view)
						.setBackgroundResource((Integer) _itemDataMap
								.get(OutgoingCallActivity.KEYBOARD_BUTTON_BGRESOURCE));
				((ImageButton) view)
						.setOnClickListener((OnClickListener) _itemDataMap
								.get(OutgoingCallActivity.KEYBOARD_BUTTON_ONCLICKLISTENER));
			} catch (Exception e) {
				e.printStackTrace();

				Log.e(LOG_TAG, "Convert item data to map error, item data = "
						+ _itemData);
			}
		}
	}

}
