package com.richitec.chinesetelephone.tab7tabcontent;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
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

		// set tab indicator and content
		// call record history list
		TabSpec _callRecordHistoryListTabSpec = _tabHost
				.newTabSpec("call record history list tab spec")
				.setIndicator(
						getResources().getString(
								R.string.call_record_history_list_tab_title))
				.setContent(
						new Intent().setClass(this,
								CallRecordHistoryListTabContentActivity.class));
		_tabHost.addTab(_callRecordHistoryListTabSpec);

		// dial
		TabSpec _dialTabSpec = _tabHost
				.newTabSpec("dial tab spec")
				.setIndicator(getResources().getString(R.string.dial_tab_title))
				.setContent(
						new Intent().setClass(this,
								DialTabContentActivity.class));
		_tabHost.addTab(_dialTabSpec);

		// contact list
		TabSpec _contactListTabSpec = _tabHost
				.newTabSpec("contact list tab spec")
				.setIndicator(
						getResources().getString(
								R.string.contact_list_tab7nav_title))
				.setContent(
						new Intent().setClass(this,
								ContactListTabContentActivity.class));
		_tabHost.addTab(_contactListTabSpec);

		// setting
		TabSpec _settingTabSpec = _tabHost
				.newTabSpec("setting tab spec")
				.setIndicator(
						getResources()
								.getString(R.string.setting_tab7nav_title))
				.setContent(
						new Intent().setClass(this,
								SettingTabContentActivity.class));
		_tabHost.addTab(_settingTabSpec);

		// set current tab
		_tabHost.setCurrentTab(1);

		//
		// _tabHost.getTabWidget().get
	}

}
