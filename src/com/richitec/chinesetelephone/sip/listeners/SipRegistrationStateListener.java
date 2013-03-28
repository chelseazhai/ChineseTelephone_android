package com.richitec.chinesetelephone.sip.listeners;

public interface SipRegistrationStateListener {

	// registering
	public void onRegistering();

	// register success
	public void onRegisterSuccess();

	// register failed
	public void onRegisterFailed();

	// unregister success
	public void onUnRegisterSuccess();

	// unregister failed
	public void onUnRegisterFailed();

}
