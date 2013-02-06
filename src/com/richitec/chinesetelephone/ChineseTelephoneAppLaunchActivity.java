package com.richitec.chinesetelephone;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.richitec.chinesetelephone.account.AccountSettingActivity;
import com.richitec.chinesetelephone.service.NoticeService;
import com.richitec.chinesetelephone.call.SipCallModeSelector;
import com.richitec.chinesetelephone.call.SipCallModeSelector.SipCallModeSelectPattern;
import com.richitec.chinesetelephone.sip.SipRegisterBean;
import com.richitec.chinesetelephone.sip.SipUtils;
import com.richitec.chinesetelephone.sip.listeners.SipRegistrationStateListener;
import com.richitec.chinesetelephone.tab7tabcontent.ChineseTelephoneTabActivity;
import com.richitec.chinesetelephone.tab7tabcontent.ContactListTabContentActivity;
import com.richitec.chinesetelephone.utils.AppDataSaveRestoreUtil;
import com.richitec.commontoolkit.activityextension.AppLaunchActivity;
import com.richitec.commontoolkit.addressbook.AddressBookManager;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.DeviceUtils;

public class ChineseTelephoneAppLaunchActivity extends AppLaunchActivity {

	@Override
	public Drawable splashImg() {
		return getResources().getDrawable(R.drawable.ic_splash);
	}

	@Override
	public Intent intentActivity() {
		// go to Chinese telephone main tab activity
		AppDataSaveRestoreUtil.loadAccount();

		UserBean userBean = UserManager.getInstance().getUser();
		if (userBean.getPassword() != null
				&& !userBean.getPassword().equals("")
				&& userBean.getUserKey() != null
				&& !userBean.getUserKey().equals("")) {
			return new Intent(this, ChineseTelephoneTabActivity.class);
		} else {
			return new Intent(this, AccountSettingActivity.class);
		}
	}

	@Override
	public void didFinishLaunching() {
		// traversal address book
		AddressBookManager.setFilterMode(AddressBookManager.FILTER_DEFAULT);
		AddressBookManager.getInstance().traversalAddressBook();

		// init all name phonetic sorted contacts info array
		ContactListTabContentActivity.initNamePhoneticSortedContactsInfoArray();
		
	}
	
	@Override
	public void doPostExecute() {
		if (!DeviceUtils.isServiceRunning(this, NoticeService.class)) {
			Intent noticeService = new Intent(this, NoticeService.class);
			startService(noticeService);
		}
		
		AddressBookManager.getInstance().registContactOberver();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		AddressBookManager.getInstance().unRegistContactObserver();
//		System.exit(0);
	}

	@Override
	protected void onRestoreInstanceState (Bundle savedInstanceState) {
		AppDataSaveRestoreUtil.onRestoreInstanceState(savedInstanceState);
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	@Override
	protected void onSaveInstanceState (Bundle outState) {
		AppDataSaveRestoreUtil.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}
	
}
