package com.richitec.chinesetelephone.tab7tabcontent;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

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
		if (view instanceof ImageView) {
			// define item data drawable
			Drawable _itemDataDrawable;

			try {
				// convert item data to drawable
				_itemDataDrawable = (Drawable) _itemData;

				// set image button image resource
				((ImageView) view).setImageDrawable(_itemDataDrawable);
			} catch (Exception e) {
				e.printStackTrace();

				Log.e(LOG_TAG,
						"Convert item data to drawable error, item data = "
								+ _itemData);
			}
		}
	}

}
