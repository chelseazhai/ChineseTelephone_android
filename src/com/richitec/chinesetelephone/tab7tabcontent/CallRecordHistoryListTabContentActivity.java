package com.richitec.chinesetelephone.tab7tabcontent;

import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.richitec.chinesetelephone.R;
import com.richitec.commontoolkit.activityextension.NavigationActivity;

public class CallRecordHistoryListTabContentActivity extends NavigationActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.call_record_history_list_tab_content_activity_layout);

		// set title
		setTitle(R.string.call_record_history_list_nav_title);

		// test by ares
		// define list view content
		String[] listViewContentArr = { "13770886721", "13456231234",
				"025-33456789", "18678231456" };

		// packet array to adapter with content
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_checked, listViewContentArr);

		// set list view adapter
		((ListView) findViewById(R.id.callRecordHistoryList_listView))
				.setAdapter(arrayAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(
				R.menu.call_record_history_list_tab_content_activity_layout,
				menu);
		return true;
	}

}
