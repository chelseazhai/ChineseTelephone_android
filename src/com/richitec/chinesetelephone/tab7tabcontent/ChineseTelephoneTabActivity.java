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
import com.richitec.chinesetelephone.account.AccountSettingActivity;
import com.richitec.chinesetelephone.assist.SettingActivity;
import com.richitec.chinesetelephone.sip.SipUtils;
import com.richitec.chinesetelephone.sip.listeners.SipRegistrationStateListener;
import com.richitec.chinesetelephone.utils.AppUpdateManager;
import com.richitec.chinesetelephone.utils.SipRegisterManager;
import com.richitec.commontoolkit.customcomponent.CommonTabSpecIndicator;

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
	private int _mCurrentTabIndex = 1;
	
	private AlertDialog dialog;
	
	private SipRegistrationStateListener sipRegistrationStateListener = new SipRegistrationStateListener(){

		@Override
		public void onRegisterSuccess() {
			// TODO Auto-generated method stub
			//do nothing
			Log.d("ChineseTelephoneTabActivity", "regist success");
		}

		@Override
		public void onRegisterFailed() {
			// TODO Auto-generated method stub
			sipRegistFail();
		}

		@Override
		public void onUnRegisterSuccess() {
			// TODO Auto-generated method stub
			//sipRegistFail();
			Log.d("ChineseTelephoneTabActivity", "unregist success");
			sipRegistFail();
		}

		@Override
		public void onUnRegisterFailed() {
			// TODO Auto-generated method stub
			Log.d("ChineseTelephoneTabActivity", "unregist fail");
			sipRegistFail();
		}
		
	};
	
	private void sipRegistFail(){
		dialog.show();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		dialog.dismiss();
		dialog=null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Runnable registSipRunnable = new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				//regist sip account
				SipRegisterManager.registSip(sipRegistrationStateListener, getString(R.string.vos_server));
			}
		};
		Thread registSipThread = new Thread(registSipRunnable);
		registSipThread.start();
		
		dialog = new AlertDialog.Builder(ChineseTelephoneTabActivity.this)
		.setTitle(R.string.alert_title)
		.setMessage(R.string.sip_account_regist_fail)
		.setPositiveButton(ChineseTelephoneTabActivity.this.getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(
							DialogInterface dialog, int arg1) {
						Intent intent = new Intent(ChineseTelephoneTabActivity.this, 
								AccountSettingActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						dialog.dismiss();
						ChineseTelephoneTabActivity.this.finish();					
					}
				}).create();
		
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
						new CommonTabSpecIndicator(this,
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
						new CommonTabSpecIndicator(this,
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
						new CommonTabSpecIndicator(this,
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
						new CommonTabSpecIndicator(this,
								TAB_WIDGETITEM_CONTENTS[3][0],
								TAB_WIDGETITEM_CONTENTS[3][1]))
				.setContent(
						new Intent().setClass(this,
								SettingActivity.class));
		_tabHost.addTab(_tabSpec);

		// set current tab and tab image
		_tabHost.setCurrentTab(_mCurrentTabIndex);
		
		/*TabWidget tabWidget = this.getTabWidget();
		int count = tabWidget.getChildCount();
		  for (int i = 0; i < count; i++) {
		   View view = tabWidget.getChildTabViewAt(i);   
		   final TextView tv = (TextView) view.findViewById(android.R.id.title);
		   tv.setTextSize(15);
		  }*/
		
		AppUpdateManager updateManager = new AppUpdateManager(this);
        updateManager.checkVersion(false);	
	}
	
	@Override
	public void onBackPressed(){
		new AlertDialog.Builder(this)
		.setTitle(R.string.alert_title)
		.setMessage(R.string.exit)
		.setPositiveButton(R.string.ok, 
				new DialogInterface.OnClickListener() {			
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						SipUtils.unregisterSipAccount(null);
						SipUtils.destroySipEngine();
						System.exit(0);
					}
				}
				)
		.setNegativeButton(R.string.cancel, null).show();
	}
}
