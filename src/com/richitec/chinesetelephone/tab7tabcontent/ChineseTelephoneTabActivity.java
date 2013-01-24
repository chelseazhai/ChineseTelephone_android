package com.richitec.chinesetelephone.tab7tabcontent;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.assist.SettingActivity;
import com.richitec.chinesetelephone.constant.SystemConstants;
import com.richitec.chinesetelephone.sip.SipUtils;
import com.richitec.chinesetelephone.sip.listeners.SipRegistrationStateListener;
import com.richitec.chinesetelephone.sip.listeners.SipRegistrationStateListenerImp;
import com.richitec.chinesetelephone.utils.AppDataSaveRestoreUtil;
import com.richitec.chinesetelephone.utils.AppUpdateManager;
import com.richitec.chinesetelephone.utils.SipRegisterManager;
import com.richitec.commontoolkit.addressbook.AddressBookManager;
import com.richitec.commontoolkit.customcomponent.CTTabSpecIndicator;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;

public class ChineseTelephoneTabActivity extends TabActivity {

	// tab widget item content array
	private final int[][] TAB_WIDGETITEM_CONTENTS = new int[][] {
			{ R.string.call_record_history_list_tab_title,
					R.drawable.callrecord_tab_icon },
			{ R.string.dial_tab_title, R.drawable.dial_tab_icon },
			{ R.string.contact_list_tab7nav_title,
					R.drawable.contactlist_tab_icon },
			{ R.string.more_tab7nav_title, R.drawable.more_tab_icon } };

	// current tab index, default is contact list tab
	private int _mDefaultTabIndex = 1;

	private SipRegistrationStateListener sipRegistrationStateListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(SystemConstants.TAG, "ChineseTelephoneTabActivity - onCreate");
		super.onCreate(savedInstanceState);

		sipRegistrationStateListener = new SipRegistrationStateListenerImp();

		// set content view
		setContentView(R.layout.chinese_telephone_tab_activity_layout);

		// get tabHost
		TabHost _tabHost = getTabHost();

		// define tabSpec
		TabSpec _tabSpec;

		// set tab indicator and content
		// call record history list
		_tabSpec = _tabHost
				.newTabSpec(
						getResources().getString(TAB_WIDGETITEM_CONTENTS[0][0]))
				.setIndicator(
						new CTTabSpecIndicator(this,
								TAB_WIDGETITEM_CONTENTS[0][0],
								TAB_WIDGETITEM_CONTENTS[0][1]))
				.setContent(
						new Intent().setClass(this,
								CallRecordHistoryListTabContentActivity.class));
		_tabHost.addTab(_tabSpec);

		// dial
		_tabSpec = _tabHost
				.newTabSpec(
						getResources().getString(TAB_WIDGETITEM_CONTENTS[1][0]))
				.setIndicator(
						new CTTabSpecIndicator(this,
								TAB_WIDGETITEM_CONTENTS[1][0],
								TAB_WIDGETITEM_CONTENTS[1][1]))
				.setContent(
						new Intent().setClass(this,
								DialTabContentActivity.class));
		_tabHost.addTab(_tabSpec);

		// contact list
		_tabSpec = _tabHost
				.newTabSpec(
						getResources().getString(TAB_WIDGETITEM_CONTENTS[2][0]))
				.setIndicator(
						new CTTabSpecIndicator(this,
								TAB_WIDGETITEM_CONTENTS[2][0],
								TAB_WIDGETITEM_CONTENTS[2][1]))
				.setContent(
						new Intent().setClass(this,
								ContactListTabContentActivity.class));
		_tabHost.addTab(_tabSpec);

		// more
		_tabSpec = _tabHost
				.newTabSpec(
						getResources().getString(TAB_WIDGETITEM_CONTENTS[3][0]))
				.setIndicator(
						new CTTabSpecIndicator(this,
								TAB_WIDGETITEM_CONTENTS[3][0],
								TAB_WIDGETITEM_CONTENTS[3][1]))
				.setContent(new Intent().setClass(this, SettingActivity.class));
		_tabHost.addTab(_tabSpec);

		// set current tab and tab image
		_tabHost.setCurrentTab(_mDefaultTabIndex);

		AppUpdateManager updateManager = new AppUpdateManager(this);
		updateManager.checkVersion(false);
	}

	@Override
	public void onDestroy() {
		Log.d(SystemConstants.TAG, "ChineseTelephoneTabActivity - onDestroy");
		SipRegistrationStateListenerImp.cancelVOIPOnlineStatus();
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		Log.d(SystemConstants.TAG, "ChineseTelephoneTabActivity - onStop");
		super.onStop();
	}

	@Override
	protected void onResume() {
		Log.d(SystemConstants.TAG, "ChineseTelephoneTabActivity - onResume");
		SipRegisterManager.registSip(sipRegistrationStateListener,
				getString(R.string.vos_server));
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.d(SystemConstants.TAG, "ChineseTelephoneTabActivity - onPause");
		super.onPause();
	}

	@Override
	protected void onRestart() {
		Log.d(SystemConstants.TAG, "ChineseTelephoneTabActivity - onRestart");
		super.onRestart();
	}

	@Override
	protected void onStart() {
		Log.d(SystemConstants.TAG, "ChineseTelephoneTabActivity - onStart");
		super.onStart();
	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.alert_title)
				.setMessage(R.string.exit)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								SipUtils.unregisterSipAccount(null);
								SipUtils.destroySipEngine();
								AddressBookManager.getInstance()
										.unRegistContactObserver();
								SipRegistrationStateListenerImp.cancelVOIPOnlineStatus();
								UserManager.getInstance().setUser(new UserBean());
					
								System.exit(0);
							}
						}).setNegativeButton(R.string.cancel, null).show();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.d(SystemConstants.TAG,
				"ChineseTelephoneTabActivity - onRestoreInstanceState");
		AppDataSaveRestoreUtil.onRestoreInstanceState(savedInstanceState);

		int currentTabIndex = savedInstanceState.getInt("current_tab");
		Log.d(SystemConstants.TAG, "restore - current tab: "
				+ currentTabIndex);
		if (currentTabIndex != 0) {
			super.onRestoreInstanceState(savedInstanceState);
		} else {
			TabHost tabHost = getTabHost();
			tabHost.setCurrentTab(currentTabIndex);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.d(SystemConstants.TAG,
				"ChineseTelephoneTabActivity - onSaveInstanceState");
		AppDataSaveRestoreUtil.onSaveInstanceState(outState);

		super.onSaveInstanceState(outState);
		TabHost tabHost = getTabHost();
		int currentTabIndex = tabHost.getCurrentTab();
		Log.d(SystemConstants.TAG, "save - current tab: " + currentTabIndex);
		outState.putInt("current_tab", currentTabIndex);
	}
}
