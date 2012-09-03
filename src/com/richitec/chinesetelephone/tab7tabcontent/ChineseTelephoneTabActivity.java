package com.richitec.chinesetelephone.tab7tabcontent;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.richitec.chinesetelephone.R;

public class ChineseTelephoneTabActivity extends TabActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.chinese_telephone_tab_activity_layout);

		// get tabHost
		TabHost _tabHost = getTabHost();

		//
		Intent layout1intent = new Intent();
		layout1intent.setClass(this, ContactListTabContentActivity.class);
		TabSpec layout1spec = _tabHost.newTabSpec("layout1");
		layout1spec.setIndicator(
				"layou1",
				getResources().getDrawable(
						android.R.drawable.stat_sys_phone_call));
		layout1spec.setContent(layout1intent);
		_tabHost.addTab(layout1spec);

		Intent layout2intent = new Intent();
		layout2intent.setClass(this, DialTabContentActivity.class);
		TabSpec layout2spec = _tabHost.newTabSpec("layout2");
		layout2spec.setIndicator("layout2");
		layout2spec.setContent(layout2intent);
		_tabHost.addTab(layout2spec);

		Intent layout3intent = new Intent();
		layout3intent.setClass(this, CallRecordHistoryListActivity.class);
		TabSpec layout3spec = _tabHost.newTabSpec("layout3");
		layout3spec.setIndicator("layout3");
		layout3spec.setContent(layout3intent);
		_tabHost.addTab(layout3spec);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.chinese_telephone_tab_activity_layout,
				menu);
		return true;
	}

}
