package com.richitec.chinesetelephone.sip.services;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
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

	// audio manager
	private AudioManager _mAudioManager;

	// current sip voice call is muted flag
	private Boolean _mIsSipVoiceCallMuted;

	// current sip voice call using loudspeaker flag
	private Boolean _mIsSipVoiceCallUsingLoudspeaker;

	public BaseSipServices() {
		super();

		// init audio manager
		_mAudioManager = (AudioManager) AppLaunchActivity.getAppContext()
				.getSystemService(Context.AUDIO_SERVICE);
	}

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
		// check current sip voice call using earphone
		if (!isSipVoiceCallUsingLoudspeaker()) {
			// close speaker
			_mAudioManager.setSpeakerphoneOn(false);
		}

		// hangup current sip voice call and get its result
		boolean _hangupCurrentSipVoiceCallResult = hangupSipVoiceCall();

		// after hangup current sip voice call, update current sip voice call
		// call log
		updateSipVoiceCallLog(callDuration);

		return _hangupCurrentSipVoiceCallResult;
	}

	@Override
	public void setSipVoiceCallUsingLoudspeaker() {
		// update current sip voice call using loudspeaker flag
		_mIsSipVoiceCallUsingLoudspeaker = true;

		// set current sip voice call loudspeaker
		// set mode
		_mAudioManager.setMode(AudioManager.MODE_NORMAL);

		// open speaker
		_mAudioManager.setSpeakerphoneOn(true);
	}

	@Override
	public void setSipVoiceCallUsingEarphone() {
		// update current sip voice call using loudspeaker flag
		_mIsSipVoiceCallUsingLoudspeaker = false;

		// set current sip voice call earphone
		// close speaker
		_mAudioManager.setSpeakerphoneOn(false);
	}

	public SipInviteStateListener getSipInviteStateListener() {
		return _mSipInviteStateListener;
	}

	public void setSipInviteStateListener(
			SipInviteStateListener sipInviteStateListener) {
		this._mSipInviteStateListener = sipInviteStateListener;
	}

	// update current sip voice call muted flag
	public void setSipVoiceCallMuted(Boolean muting) {
		_mIsSipVoiceCallMuted = muting;
	}

	// check current sip voice call is muted
	public Boolean isSipVoiceCallMuted() {
		return null == _mIsSipVoiceCallMuted ? false : _mIsSipVoiceCallMuted;
	}

	// check current sip voice call is using loudspeaker
	public Boolean isSipVoiceCallUsingLoudspeaker() {
		return null == _mIsSipVoiceCallUsingLoudspeaker ? false
				: _mIsSipVoiceCallUsingLoudspeaker;
	}

	// update current sip voice call call log
	public void updateSipVoiceCallLog(Long sipVoiceCallDuration) {
		// generate for updating call log values
		Map<String, String> _updateValues = new HashMap<String, String>();
		_updateValues.put(CallLog.Calls.DURATION,
				sipVoiceCallDuration.toString());

		// sip voice call log: sip voice call duration
		CallLogManager.updateCallLog(_mSipVoiceCallLogId, _updateValues);
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

			// update sip voice call failed call log
			updateSipVoiceCallLog(-1L);
		}
	}

}
