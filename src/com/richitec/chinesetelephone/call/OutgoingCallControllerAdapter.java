package com.richitec.chinesetelephone.call;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.richitec.commontoolkit.customadapter.CommonListAdapter;

public class OutgoingCallControllerAdapter extends CommonListAdapter {

	private static final String LOG_TAG = "OutgoingCallControllerAdapter";

	public OutgoingCallControllerAdapter(Context context,
			List<Map<String, ?>> data, int itemsLayoutResId, String[] dataKeys,
			int[] itemsComponentResIds) {
		super(context, data, itemsLayoutResId, dataKeys, itemsComponentResIds);
	}

	@Override
	protected void bindView(View view, Map<String, ?> dataMap, String dataKey) {
		// get item data object
		Object _itemData = dataMap.get(dataKey);

		// check view type
		// relativeLayout
		if (view instanceof RelativeLayout) {
			try {
				// define item data map and convert item data to map
				@SuppressWarnings("unchecked")
				Map<String, Object> _itemDataMap = (Map<String, Object>) _itemData;

				// get item data map values
				Integer _itemBackgroundResource = (Integer) _itemDataMap
						.get(OutgoingCallActivity.CALL_CONTROLLER_ITEM_BACKGROUND);
				OnTouchListener _itemOnTouchListener = (OnTouchListener) _itemDataMap
						.get(OutgoingCallActivity.CALL_CONTROLLER_ITEM_ONTOUCHLISTENER);

				// set call controller item background resource and on touch
				// listener
				((RelativeLayout) view)
						.setBackgroundResource(_itemBackgroundResource);
				if (null != _itemOnTouchListener) {
					((RelativeLayout) view)
							.setOnTouchListener(_itemOnTouchListener);
				}
			} catch (Exception e) {
				e.printStackTrace();

				Log.e(LOG_TAG, "Convert item data to map error, item data = "
						+ _itemData);
			}
		}
		// textView
		else if (view instanceof TextView) {
			// set view text
			if (null == _itemData) {
				((TextView) view).setText("");
			} else if (_itemData instanceof Integer) {
				((TextView) view).setText((Integer) _itemData);
			} else {
				((TextView) view).setText(_itemData.toString());
			}
		}
		// imageView
		else if (view instanceof ImageView) {
			try {
				// define item data integer and convert item data to integer
				Integer _itemDataInteger = (Integer) _itemData;

				// set imageView image resource
				((ImageView) view).setImageResource(_itemDataInteger);
			} catch (Exception e) {
				e.printStackTrace();

				Log.e(LOG_TAG,
						"Convert item data to integer error, item data = "
								+ _itemData);
			}
		}
	}

}
