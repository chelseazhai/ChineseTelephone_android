package com.richitec.chinesetelephone.sip;

import com.richitec.chinesetelephone.sip.listeners.SipRegistrationStateListener;
import com.richitec.chinesetelephone.sip.services.BaseSipServices;
import com.richitec.chinesetelephone.sip.services.ISipServices;
import com.richitec.chinesetelephone.sip.services.SipDroidSipServices;

public class SipUtils {

	// singleton instance
	private static volatile SipUtils _singletonInstance;

	// sip services object
	private ISipServices _mSipServices;

	private SipUtils() {
		// init sip services object
		_mSipServices = new /* DoubangoSipServices() */SipDroidSipServices();
	}

	// get base sip services
	public static BaseSipServices getSipServices() {
		// check instance
		if (null == _singletonInstance) {
			synchronized (SipUtils.class) {
				if (null == _singletonInstance) {
					_singletonInstance = new SipUtils();
				}
			}
		}

		return (BaseSipServices) _singletonInstance._mSipServices;
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
