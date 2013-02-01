package com.richitec.chinesetelephone.sip;

import com.richitec.chinesetelephone.call.SipCallMode;
import com.richitec.chinesetelephone.sip.listeners.SipRegistrationStateListener;
import com.richitec.chinesetelephone.sip.services.BaseSipServices;
import com.richitec.chinesetelephone.sip.services.ISipServices;
import com.richitec.chinesetelephone.sip.services.SipDroidSipServices;

public class SipUtils {

	// sip services object, singleton instance
	private static volatile ISipServices _sipServices;

	// get base sip services
	public static BaseSipServices getSipServices() {
		// check sip services instance
		if (null == _sipServices) {
			synchronized (SipUtils.class) {
				if (null == _sipServices) {
					// init sip services object
					_sipServices = new /* DoubangoSipServices() */SipDroidSipServices();
				}
			}
		}

		return (BaseSipServices) _sipServices;
	}

	// register sip account
	public static void registerSipAccount(SipRegisterBean sipAccount,
			SipRegistrationStateListener sipRegistrationStateListener) {
		getSipServices().registerSipAccount(sipAccount,
				sipRegistrationStateListener);
	}

	// unregister sip account
	public static void unregisterSipAccount(
			SipRegistrationStateListener sipRegistrationStateListener) {
		getSipServices().unregisterSipAccount(sipRegistrationStateListener);
	}

	// make sip voice call
	public static void makeSipVoiceCall(String calleeName, String calleePhone,
			SipCallMode callMode) {
		getSipServices().makeSipVoiceCall(calleeName, calleePhone, callMode);
	}

	// destroy sip engine
	public static void destroySipEngine() {
		getSipServices().destroySipEngine();
	}

}
