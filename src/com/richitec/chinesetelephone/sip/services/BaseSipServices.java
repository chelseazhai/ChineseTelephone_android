package com.richitec.chinesetelephone.sip.services;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.provider.CallLog;

import com.richitec.chinesetelephone.call.OutgoingCallActivity;
import com.richitec.chinesetelephone.sip.SipCallMode;
import com.richitec.chinesetelephone.sip.listeners.SipInviteStateListener;
import com.richitec.commontoolkit.activityextension.AppLaunchActivity;
import com.richitec.commontoolkit.calllog.CallLogManager;

public abstract class BaseSipServices implements ISipServices {

	// sip voice call log id
	private Long _mSipVoiceCallLogId;

	// sip invite listener
	private SipInviteStateListener _mSipInviteStateListener;

	// make direct dial sip voice call
	public abstract boolean makeDirectDialSipVoiceCall(String calleeName,
			String calleePhone);

	// make callback sip voice call
	public abstract boolean makeCallbackSipVoiceCall(String calleeName,
			String calleePhone);

	// hangup current sip voice call
	public abstract boolean hangupSipVoiceCall();

	@Override
	public void makeSipVoiceCall(String calleeName, String calleePhone,
			SipCallMode callMode) {
		// before make sip voice call
		beforeMakeSipVoiceCall(calleeName, calleePhone);

		// check call mode and get make sip voice call result
		boolean _makeSipVoiceCallResult;

		switch (callMode) {
		case CALLBACK:
			// make callback sip voice call
			_makeSipVoiceCallResult = makeCallbackSipVoiceCall(calleeName,
					calleePhone);

			break;

		case DIRECT_CALL:
		default:
			// make direct dial sip voice call
			_makeSipVoiceCallResult = makeDirectDialSipVoiceCall(calleeName,
					calleePhone);

			break;
		}

		// after make sip voice call
		afterMakeSipVoiceCall(_makeSipVoiceCallResult);
	}

	@Override
	public boolean hangupSipVoiceCall(Long callDuration) {
		// hangup current sip voice call and get its result
		boolean _hangupCurrentSipVoiceCallResult = hangupSipVoiceCall();

		// after hangup current sip voice call
		afterHangupSipVoiceCall(callDuration);

		return _hangupCurrentSipVoiceCallResult;
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
	public void setSipVoiceCallUsingLoudspeaker() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSipVoiceCallUsingEarphone() {
		// TODO Auto-generated method stub

	}

	public SipInviteStateListener getSipInviteStateListener() {
		return _mSipInviteStateListener;
	}

	public void setSipInviteStateListener(
			SipInviteStateListener sipInviteStateListener) {
		this._mSipInviteStateListener = sipInviteStateListener;
	}

	// before make sip voice call
	private void beforeMakeSipVoiceCall(String calleeName, String calleePhone) {
		// start outgoing call activity and set parameters
		// define the outgoing call intent
		Intent _outgoingCallIntent = new Intent(
				AppLaunchActivity.getAppContext(), OutgoingCallActivity.class);

		// set it as an new task
		_outgoingCallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		// set outgoing call callee phone and callee name
		_outgoingCallIntent.putExtra(OutgoingCallActivity.OUTGOING_CALL_PHONE,
				calleePhone);
		_outgoingCallIntent.putExtra(
				OutgoingCallActivity.OUTGOING_CALL_OWNERSHIP, calleeName);

		// init sip services
		OutgoingCallActivity.initSipServices(this);

		// start outgoing call activity
		AppLaunchActivity.getAppContext().startActivity(_outgoingCallIntent);

		// insert sip voice call log
		_mSipVoiceCallLogId = CallLogManager.insertCallLog(calleeName,
				calleePhone);
	}

	// after make sip voice call
	private void afterMakeSipVoiceCall(boolean makeCallResult) {
		// check make call result and update call failed call log
		if (!makeCallResult) {
			// sip voice call failed
			getSipInviteStateListener().onCallFailed();

			// generate for updating call log values
			Map<String, String> _updateValues = new HashMap<String, String>();
			_updateValues.put(CallLog.Calls.DURATION, "-1");

			// update sip voice call log
			CallLogManager.updateCallLog(_mSipVoiceCallLogId, _updateValues);
		}
	}

	// after hangup current sip voice call
	private void afterHangupSipVoiceCall(Long sipVoiceCallDuration) {
		// generate for updating call log values
		Map<String, String> _updateValues = new HashMap<String, String>();
		_updateValues.put(CallLog.Calls.DURATION,
				sipVoiceCallDuration.toString());

		// sip voice call log: sip voice call duration
		CallLogManager.updateCallLog(_mSipVoiceCallLogId, _updateValues);
	}

}
