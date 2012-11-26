package com.richitec.chinesetelephone.sip.services;

import com.richitec.chinesetelephone.sip.SipCallMode;
import com.richitec.chinesetelephone.sip.SipRegisterBean;
import com.richitec.chinesetelephone.sip.listeners.SipRegistrationStateListener;

public interface ISipServices {

	// register sip account
	public void registerSipAccount(SipRegisterBean sipAccount,
			SipRegistrationStateListener sipRegistrationStateListener);

	// unregister sip account
	public void unregisterSipAccount(
			SipRegistrationStateListener sipRegistrationStateListener);

	// make sip voice call
	public void makeSipVoiceCall(String calleeDisplayName,
			String calleePhoneNumber, SipCallMode callMode);

}
