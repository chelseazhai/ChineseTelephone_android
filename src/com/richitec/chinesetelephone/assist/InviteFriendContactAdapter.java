package com.richitec.chinesetelephone.assist;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.richitec.commontoolkit.customadapter.CTListAdapter;

public class InviteFriendContactAdapter extends CTListAdapter {

	private static final String LOG_TAG = "AddressBookContactAdapter";

	public InviteFriendContactAdapter(Context context,
			List<Map<String, ?>> data, int itemsLayoutResId, String[] dataKeys,
			int[] itemsComponentResIds) {
		super(context, data, itemsLayoutResId, dataKeys, itemsComponentResIds);
	}

	@Override
	protected void bindView(View view, Map<String, ?> dataMap, String dataKey) {
		// get item data object
		Object _itemData = dataMap.get(dataKey);
		// check view type
		if(view instanceof CheckBox){
			
			boolean selected = (Boolean) _itemData;
			if(selected){
				((CheckBox)view).setChecked(true);				
			}
			else{
				((CheckBox)view).setChecked(false);
			}
		}
		// textView
		else if (view instanceof TextView) {
			// set view text
			((TextView) view)
					.setText(null == _itemData ? ""
							: _itemData instanceof SpannableString ? (SpannableString) _itemData
									: _itemData.toString());
		}
		// imageView
		else if (view instanceof ImageView) {
			try {
				// define item data bitmap and convert item data to bitmap
				Bitmap _itemDataBitmap = (Bitmap) _itemData;

				// set imageView image
				((ImageView) view).setImageBitmap(_itemDataBitmap);
			} catch (Exception e) {
				e.printStackTrace();

				Log.e(LOG_TAG,
						"Convert item data to bitmap error, item data = "
								+ _itemData);
			}
		}
	}

}
