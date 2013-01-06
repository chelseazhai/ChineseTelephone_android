package com.richitec.chinesetelephone.tab7tabcontent;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.SpannableString;
import android.view.View;
import android.widget.TextView;

import com.richitec.commontoolkit.customadapter.CommonListAdapter;

public class CallRecordDetailInfoOperationAdapter extends
		CommonListAdapter {

	public CallRecordDetailInfoOperationAdapter(Context context,
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
	}

}
