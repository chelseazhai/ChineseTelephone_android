package com.richitec.chinesetelephone.tab7tabcontent;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.assist.NoticeViewActivity;
import com.richitec.chinesetelephone.assist.SettingActivity;
import com.richitec.chinesetelephone.constant.SystemConstants;
import com.richitec.chinesetelephone.sip.SipUtils;
import com.richitec.chinesetelephone.sip.listeners.SipRegistrationStateListener;
import com.richitec.chinesetelephone.utils.AppDataSaveRestoreUtil;
import com.richitec.chinesetelephone.utils.AppUpdateManager;
import com.richitec.chinesetelephone.utils.SipRegisterManager;
import com.richitec.commontoolkit.CommonToolkitApplication;
import com.richitec.commontoolkit.addressbook.AddressBookManager;
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

	private SipRegistrationStateListener sipRegistrationStateListener;
	
	class SipRegistrationStateListenerImp implements SipRegistrationStateListener {
		public final static int NOTIFY_ID = 2;
		
		private NotificationManager mNotificationManager;
		
		public SipRegistrationStateListenerImp() {
			mNotificationManager = (NotificationManager) CommonToolkitApplication.getContext()
					.getSystemService(Context.NOTIFICATION_SERVICE);
		}
		
		@Override
		public void onRegisterSuccess() {
			Log.d(SystemConstants.TAG, "regist success");
			Context context = CommonToolkitApplication.getContext();
			Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
					android.R.drawable.presence_online);
			NotificationCompat.Builder builder = new NotificationCompat.Builder(
					context).setSmallIcon(android.R.drawable.presence_online).setLargeIcon(icon)
					.setAutoCancel(false).setOngoing(true).setWhen(0)
					.setContentTitle(context.getString(R.string.online));
			
			Intent resultIntent = new Intent(context, ChineseTelephoneTabActivity.class);
			PendingIntent noticePendingIntent = PendingIntent
					.getActivity(ChineseTelephoneTabActivity.this, 0, resultIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(noticePendingIntent);
			
			Notification notif = builder.build();
			mNotificationManager.notify(NOTIFY_ID, notif);
		}

		@Override
		public void onRegisterFailed() {
			Log.d(SystemConstants.TAG, "regist failed");
			Context context = CommonToolkitApplication.getContext();
			Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
					android.R.drawable.presence_offline);
			NotificationCompat.Builder builder = new NotificationCompat.Builder(
					context).setSmallIcon(android.R.drawable.presence_offline).setLargeIcon(icon)
					.setAutoCancel(false).setOngoing(true).setWhen(0)
					.setContentTitle(context.getString(R.string.offline));
			
			Intent resultIntent = new Intent(context, ChineseTelephoneTabActivity.class);
			PendingIntent noticePendingIntent = PendingIntent
					.getActivity(ChineseTelephoneTabActivity.this, 0, resultIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(noticePendingIntent);
			
			Notification notif = builder.build();
			mNotificationManager.notify(NOTIFY_ID, notif);
			
		}

		@Override
		public void onUnRegisterSuccess() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onUnRegisterFailed() {
			// TODO Auto-generated method stub
			
		}
		
	}

	@Override
	public void onDestroy() {
		Log.d(SystemConstants.TAG, "ChineseTelephoneTabActivity - onDestroy");
		super.onDestroy();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(SystemConstants.TAG, "ChineseTelephoneTabActivity - onCreate");
		super.onCreate(savedInstanceState);
		
		sipRegistrationStateListener = new SipRegistrationStateListenerImp();
		
		Runnable registSipRunnable = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				// regist sip account
				SipRegisterManager.registSip(sipRegistrationStateListener,
						getString(R.string.vos_server));
			}
		};
		Thread registSipThread = new Thread(registSipRunnable);
		registSipThread.start();
		
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
				.setContent(new Intent().setClass(this, SettingActivity.class));
		_tabHost.addTab(_tabSpec);

		// set current tab and tab image
		_tabHost.setCurrentTab(_mCurrentTabIndex);

		/*
		 * TabWidget tabWidget = this.getTabWidget(); int count =
		 * tabWidget.getChildCount(); for (int i = 0; i < count; i++) { View
		 * view = tabWidget.getChildTabViewAt(i); final TextView tv = (TextView)
		 * view.findViewById(android.R.id.title); tv.setTextSize(15); }
		 */

		AppUpdateManager updateManager = new AppUpdateManager(this);
		updateManager.checkVersion(false);
	}

	@Override
	protected void onStop() {
		Log.d(SystemConstants.TAG, "ChineseTelephoneTabActivity - onStop");
		super.onStop();
	}

	@Override
	protected void onResume() {
		Log.d(SystemConstants.TAG, "ChineseTelephoneTabActivity - onResume");
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

	// protected void onSaveInstanceState (Bundle outState) {
	// Log.d(SystemConstants.TAG,
	// "ChineseTelephoneTabActivity  - onSaveInstanceState");
	// }

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
								
								NotificationManager nm = (NotificationManager) CommonToolkitApplication.getContext()
										.getSystemService(Context.NOTIFICATION_SERVICE);
								nm.cancel(SipRegistrationStateListenerImp.NOTIFY_ID);
								// System.exit(0);
								finish();
							}
						}).setNegativeButton(R.string.cancel, null).show();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.d(SystemConstants.TAG,
				"ChineseTelephoneTabActivity - onRestoreInstanceState");
		AppDataSaveRestoreUtil.onRestoreInstanceState(savedInstanceState);
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.d(SystemConstants.TAG,
				"ChineseTelephoneTabActivity - onSaveInstanceState");
		AppDataSaveRestoreUtil.onSaveInstanceState(outState);
		// super.onSaveInstanceState(outState);
	}
}
