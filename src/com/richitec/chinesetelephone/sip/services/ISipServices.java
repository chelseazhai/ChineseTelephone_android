package com.richitec.chinesetelephone.sip.services;

import android.media.AudioManager;

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
	public void makeSipVoiceCall(String calleeName, String calleePhone,
			SipCallMode callMode);

	// hangup current sip voice call
	public boolean hangupSipVoiceCall(Long callDuration);

	// mute current sip voice call
	public void muteSipVoiceCall(AudioManager audioManager);

	// unmute current sip voice call
	public void unmuteSipVoiceCall(AudioManager audioManager);

	// set current sip voice call using loudspeaker
	public void setSipVoiceCallUsingLoudspeaker(AudioManager audioManager);

	// set current sip voice call using earphone
	public void setSipVoiceCallUsingEarphone(AudioManager audioManager);

	// send dtmf
	public void sentDTMF(String dtmfCode);

}
