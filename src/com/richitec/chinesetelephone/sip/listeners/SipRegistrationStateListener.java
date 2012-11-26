package com.richitec.chinesetelephone.sip.listeners;

public abstract class SipRegistrationStateListener {

	// register success
	public abstract void onRegisterSuccess();

	// register failed
	public abstract void onRegisterFailed();

	// unregister success
	public abstract void onUnRegisterSuccess();

	// unregister failed
	public abstract void onUnRegisterFailed();

}
