package com.richitec.chinesetelephone.tab7tabcontent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.richitec.chinesetelephone.R;

public class DialTabContentActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.dial_tab_content_activity_layout);

		// test by ares
		// define grid view content
		String[] gridViewContentArr = { "1", "2", "3", "4", "5", "6", "7", "8",
				"9", "*", "0", "#", "add", "call", "del" };

		// data list
		ArrayList<Map<String, String>> dataList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < gridViewContentArr.length; i++) {
			HashMap<String, String> dataMap = new HashMap<String, String>();

			dataMap.put("dial_btn_title", gridViewContentArr[i]);

			dataList.add(dataMap);
		}

		// simple adapter with content
		SimpleAdapter adapter = new SimpleAdapter(this, dataList,
				R.layout.dial_btn_layout, new String[] { "dial_btn_title" },
				new int[] { R.id.dialBtn_textView });

		// set grid view adapter
		((GridView) findViewById(R.id.dial_btn_gridView)).setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater()
				.inflate(R.menu.dial_tab_content_activity_layout, menu);
		return true;
	}

}
