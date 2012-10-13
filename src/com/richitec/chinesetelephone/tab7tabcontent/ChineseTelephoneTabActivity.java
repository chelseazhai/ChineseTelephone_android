package com.richitec.chinesetelephone.tab7tabcontent;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;

import com.richitec.chinesetelephone.R;
import com.richitec.commontoolkit.utils.CommonUtils;

public class ChineseTelephoneTabActivity extends TabActivity {

	// tab ids array
	private final String[] TABIDS = new String[] {
			"call record history list tab", "dial tab", "contact list tab",
			"more tab" };

	// tab images array
	private final int[][] TABIMGS = new int[][] {
			{ R.drawable.img_tab_callrecord_unselected,
					R.drawable.img_tab_callrecord_selected },
			{ R.drawable.img_tab_dial_unselected,
					R.drawable.img_tab_dial_selected },
			{ R.drawable.img_tab_contactlist_unselected,
					R.drawable.img_tab_contactlist_selected },
			{ R.drawable.img_tab_more_unselected,
					R.drawable.img_tab_more_selected } };

	// current tab index, default is contact list tab
	private int _mCurrentTabIndex = 2;

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
				.newTabSpec(TABIDS[0])
				.setIndicator(
						getResources().getString(
								R.string.call_record_history_list_tab_title),
						getResources().getDrawable(TABIMGS[0][0]))
				.setContent(
						new Intent().setClass(this,
								CallRecordHistoryListTabContentActivity.class));
		_tabHost.addTab(_callRecordHistoryListTabSpec);

		// dial
		TabSpec _dialTabSpec = _tabHost
				.newTabSpec(TABIDS[1])
				.setIndicator(
						getResources().getString(R.string.dial_tab_title),
						getResources().getDrawable(TABIMGS[1][0]))
				.setContent(
						new Intent().setClass(this,
								DialTabContentActivity.class));
		_tabHost.addTab(_dialTabSpec);

		// contact list
		TabSpec _contactListTabSpec = _tabHost
				.newTabSpec(TABIDS[2])
				.setIndicator(
						getResources().getString(
								R.string.contact_list_tab7nav_title),
						getResources().getDrawable(TABIMGS[2][0]))
				.setContent(
						new Intent().setClass(this,
								ContactListTabContentActivity.class));
		_tabHost.addTab(_contactListTabSpec);

		// more
		TabSpec _moreTabSpec = _tabHost
				.newTabSpec(TABIDS[3])
				.setIndicator(
						getResources().getString(R.string.more_tab7nav_title),
						getResources().getDrawable(TABIMGS[3][0]))
				.setContent(
						new Intent().setClass(this,
								MoreTabContentActivity.class));
		_tabHost.addTab(_moreTabSpec);

		// set current tab and tab image
		_tabHost.setCurrentTab(_mCurrentTabIndex);
		((ImageView) _tabHost.getTabWidget().getChildAt(_mCurrentTabIndex)
				.findViewById(android.R.id.icon))
				.setImageResource(TABIMGS[_mCurrentTabIndex][1]);

		// set Chinese telephone on tab changed listener
		_tabHost.setOnTabChangedListener(new ChineseTelephoneOnTabChangeListener());
	}

	// inner class
	// Chinese telephone on tab change listener
	class ChineseTelephoneOnTabChangeListener implements OnTabChangeListener {

		@Override
		public void onTabChanged(String tabId) {
			// get last selected tab index
			int _lastSelectedTabIndex = _mCurrentTabIndex;

			// set current tab index
			_mCurrentTabIndex = CommonUtils.array2List(TABIDS).indexOf(tabId);

			// get tab widget
			TabWidget _tabWidget = getTabHost().getTabWidget();

			// reset last selected and current tab image
			((ImageView) _tabWidget.getChildAt(_lastSelectedTabIndex)
					.findViewById(android.R.id.icon))
					.setImageResource(TABIMGS[_lastSelectedTabIndex][0]);
			((ImageView) _tabWidget.getChildAt(_mCurrentTabIndex).findViewById(
					android.R.id.icon))
					.setImageResource(TABIMGS[_mCurrentTabIndex][1]);
		}

	}

}
