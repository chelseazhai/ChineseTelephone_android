package com.richitec.chinesetelephone.tab7tabcontent;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageButton;

import com.richitec.commontoolkit.customadapter.CommonListAdapter;

public class DialPhoneButtonAdapter extends CommonListAdapter {

	private static final String LOG_TAG = "DialPhoneButtonAdapter";

	public DialPhoneButtonAdapter(Context context, List<Map<String, ?>> data,
			int itemsLayoutResId, String[] dataKeys, int[] itemsComponentResIds) {
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
						.get(DialTabContentActivity.DIAL_PHONE_BUTTON_CODE));
				((ImageButton) view).setImageResource((Integer) _itemDataMap
						.get(DialTabContentActivity.DIAL_PHONE_BUTTON_IMAGE));
				((ImageButton) view)
						.setOnClickListener((OnClickListener) _itemDataMap
								.get(DialTabContentActivity.DIAL_PHONE_BUTTON_ONCLICKLISTENER));
				((ImageButton) view)
						.setOnLongClickListener((OnLongClickListener) _itemDataMap
								.get(DialTabContentActivity.DIAL_PHONE_BUTTON_ONLONGCLICKLISTENER));
			} catch (Exception e) {
				e.printStackTrace();

				Log.e(LOG_TAG, "Convert item data to map error, item data = "
						+ _itemData);
			}
		}
	}

}
