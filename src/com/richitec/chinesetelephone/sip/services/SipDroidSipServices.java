package com.richitec.chinesetelephone.sip.services;

import com.richitec.chinesetelephone.sip.SipRegisterBean;
import com.richitec.chinesetelephone.sip.listeners.SipRegistrationStateListener;

public class SipDroidSipServices extends BaseSipServices implements
		ISipServices {

	@Override
	public void registerSipAccount(SipRegisterBean sipAccount,
			SipRegistrationStateListener sipRegistrationStateListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterSipAccount(
			SipRegistrationStateListener sipRegistrationStateListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean makeDirectDialSipVoiceCall(String calleeName,
			String calleePhone) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hangupSipVoiceCall() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void muteSipVoiceCall() {
		// TODO Auto-generated method stub

	}

	@Override
	public void unmuteSipVoiceCall() {
		// TODO Auto-generated method stub

	}

	@Override
	public void sentDTMF(String dtmfCode) {
		// TODO Auto-generated method stub

	}

}
