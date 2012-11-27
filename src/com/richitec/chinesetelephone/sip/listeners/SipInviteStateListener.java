package com.richitec.chinesetelephone.sip.listeners;

public interface SipInviteStateListener {

	// invite initializing
	public void onCallInitializing();

	// invite early media
	public void onCallEarlyMedia();

	// invite remote ringing
	public void onCallRemoteRinging();

	// invite speaking
	public void onCallSpeaking();

	// invite failed
	public void onCallFailed();

	// invite terminating
	public void onCallTerminating();

	// invite terminated
	public void onCallTerminated();

}
