package com.richitec.chinesetelephone.sip;

import android.util.Log;

import com.richitec.chinesetelephone.constant.SystemConstants;
import com.richitec.chinesetelephone.sip.listeners.SipRegistrationStateListener;
import com.richitec.chinesetelephone.sip.services.BaseSipServices;
import com.richitec.chinesetelephone.sip.services.ISipServices;
import com.richitec.commontoolkit.addressbook.AddressBookManager;
import com.richitec.chinesetelephone.sip.services.SipDroidSipServices;

public class SipUtils {

	// sip services
	private static final ISipServices sipServices = new /* DoubangoSipServices() */SipDroidSipServices();

	// get base sip services
	public static BaseSipServices getSipServices() {
		return (BaseSipServices) sipServices;
	}

	// register sip account
	public static void registerSipAccount(SipRegisterBean sipAccount,
			SipRegistrationStateListener sipRegistrationStateListener) {
		Log.d(SystemConstants.TAG, "SipUtils - registerSipAccount");
		sipServices
				.registerSipAccount(sipAccount, sipRegistrationStateListener);
	}

	// unregister sip account
	public static void unregisterSipAccount(
			SipRegistrationStateListener sipRegistrationStateListener) {
		sipServices.unregisterSipAccount(sipRegistrationStateListener);
	}

	// destroy sip engine
	public static void destroySipEngine() {
		//
	}

	// make sip voice call
	public static void makeSipVoiceCall(String calleeName, String calleePhone,
			SipCallMode callMode) {
		Log.d("SipUtils", "makeSipVoiceCall - callee name = " + calleeName
				+ " , phone number = " + calleePhone + " and call mode = "
				+ callMode);
		
		sipServices.makeSipVoiceCall(calleeName, calleePhone, callMode);
	}

}
