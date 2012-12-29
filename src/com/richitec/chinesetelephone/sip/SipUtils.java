package com.richitec.chinesetelephone.sip;

import android.util.Log;

import com.richitec.chinesetelephone.sip.listeners.SipRegistrationStateListener;
import com.richitec.chinesetelephone.sip.services.BaseSipServices;
import com.richitec.chinesetelephone.sip.services.ISipServices;
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
		sipServices
				.registerSipAccount(sipAccount, sipRegistrationStateListener);
	}

	// unregister sip account
	public static void unregisterSipAccount(
			SipRegistrationStateListener sipRegistrationStateListener) {
		sipServices.unregisterSipAccount(sipRegistrationStateListener);
	}

	// make sip voice call
	public static void makeSipVoiceCall(String calleeName, String calleePhone,
			SipCallMode callMode) {
		Log.d("SipUtils", "makeSipVoiceCall - callee name = " + calleeName
				+ " , phone number = " + calleePhone + " and call mode = "
				+ callMode);

		sipServices.makeSipVoiceCall(calleeName, calleePhone, callMode);
	}

	// destroy sip engine
	public static void destroySipEngine() {
		sipServices.destroySipEngine();
	}

}
