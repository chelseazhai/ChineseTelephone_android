package com.richitec.chinesetelephone.sip;

import android.util.Log;

import com.richitec.chinesetelephone.sip.listeners.SipRegistrationStateListener;
import com.richitec.chinesetelephone.sip.services.DoubangoSipServices;
import com.richitec.chinesetelephone.sip.services.ISipServices;

public class SipUtils {

	// sip services
	private static final ISipServices sipServices = new DoubangoSipServices();

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

	// destroy sip engine
	public static void destroySipEngine() {
		//
	}

	// make sip voice call
	public static void makeSipVoiceCall(String calleeDisplayName,
			String calleePhoneNumber, SipCallMode callMode) {
		Log.d("SipUtils", "makeSipVoiceCall - callee display name = "
				+ calleeDisplayName + " , phone number = " + calleePhoneNumber
				+ " and call mode = " + callMode);

		sipServices.makeSipVoiceCall(calleeDisplayName, calleePhoneNumber,
				callMode);
	}

	// hangup current sip voice call
	public static void hangupSipVoiceCall() {
		//
	}

	// mute current sip voice call
	public static void muteSipVoiceCall() {
		//
	}

	// unmute current sip voice call
	public static void unmuteSipVoiceCall() {
		//
	}

	// set current sip voice call using loudspeaker
	public static void setSipVoiceCallUsingLoudspeaker() {
		//
	}

	// set current sip voice call using earphone
	public static void setSipVoiceCallUsingEarphone() {
		//
	}

}
