package com.richitec.chinesetelephone;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.richitec.chinesetelephone.call.SipCallModeSelector;
import com.richitec.chinesetelephone.call.SipCallModeSelector.SipCallModeSelectPattern;
import com.richitec.chinesetelephone.sip.SipRegisterBean;
import com.richitec.chinesetelephone.sip.SipUtils;
import com.richitec.chinesetelephone.sip.listeners.SipRegistrationStateListener;
import com.richitec.chinesetelephone.tab7tabcontent.ChineseTelephoneTabActivity;
import com.richitec.chinesetelephone.tab7tabcontent.ContactListTabContentActivity;
import com.richitec.commontoolkit.activityextension.AppLaunchActivity;
import com.richitec.commontoolkit.addressbook.AddressBookManager;

public class ChineseTelephoneAppLaunchActivity extends AppLaunchActivity {

	@Override
	public Drawable splashImg() {
		return getResources().getDrawable(R.drawable.ic_splash);
	}

	@Override
	public Intent intentActivity() {
		// go to Chinese telephone main tab activity
		return new Intent(this, ChineseTelephoneTabActivity.class);
	}

	@Override
	public void didFinishLaunching() {
		// traversal address book
		AddressBookManager.getInstance().traversalAddressBook();

		// init all name phonetic sorted contacts info array
		ContactListTabContentActivity.initNamePhoneticSortedContactsInfoArray();

		// test by ares
		// set sip call mode select pattern
		SipCallModeSelector
				.setSipCallModeSelectPattern(SipCallModeSelectPattern.AUTO);

		// generate sip register account
		SipRegisterBean _sipAccount = new SipRegisterBean();

		// set test sip account
		_sipAccount.setSipUserName("9001");
		_sipAccount.setSipPwd("9001");
		_sipAccount.setSipServer("103.20.193.172");
		_sipAccount.setSipDomain("richitec.com");
		_sipAccount.setSipRealm("richitec.com");
		_sipAccount.setSipPort(7788);

		// register sip account
		SipUtils.registerSipAccount(_sipAccount,
				new SipRegistrationStateListener() {

					@Override
					public void onUnRegisterSuccess() {
						Log.d("TestRegisterSipAccount", "onUnRegisterSuccess");
					}

					@Override
					public void onUnRegisterFailed() {
						Log.d("TestRegisterSipAccount", "onUnRegisterFailed");
					}

					@Override
					public void onRegisterSuccess() {
						Log.d("TestRegisterSipAccount", "onRegisterSuccess");
					}

					@Override
					public void onRegisterFailed() {
						Log.d("TestRegisterSipAccount", "onRegisterFailed");
					}
				});
	}

}
