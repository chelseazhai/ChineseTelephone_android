package com.richitec.chinesetelephone.sip.services;

import java.util.HashMap;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.provider.CallLog;
import android.util.Log;

import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.call.OutgoingCallActivity;
import com.richitec.chinesetelephone.constant.TelUser;
import com.richitec.chinesetelephone.sip.SipCallMode;
import com.richitec.chinesetelephone.sip.listeners.SipInviteStateListener;
import com.richitec.chinesetelephone.sip.listeners.SipRegistrationStateListener;
import com.richitec.commontoolkit.activityextension.AppLaunchActivity;
import com.richitec.commontoolkit.addressbook.AddressBookManager;
import com.richitec.commontoolkit.CommonToolkitApplication;
import com.richitec.commontoolkit.calllog.CallLogManager;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.HttpUtils;
import com.richitec.commontoolkit.utils.HttpUtils.HttpRequestType;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;
import com.richitec.commontoolkit.utils.HttpUtils.PostRequestFormat;

public abstract class BaseSipServices implements ISipServices {

	private static final String LOG_TAG = "BaseSipServices";

	// application context
	protected static Context _appContext;

	// sip registration state listener
	protected SipRegistrationStateListener _mSipRegistrationStateListener;

	// sip registration state broadcast receiver
	protected BroadcastReceiver _mRegistrationStateBroadcastReceiver;

	// sip register intent filter
	protected final IntentFilter SIPEGISTER_INTENTFILTER = new IntentFilter();

	// sip audio/video session state broadcast receiver
	protected BroadcastReceiver _mAVSessionStateBroadcastReceiver;

	// sip invite intent filter
	protected final IntentFilter SIPINVITE_INTENTFILTER = new IntentFilter();

	// sip voice call log id
	private Long _mSipVoiceCallLogId;

	// sip invite listener
	private SipInviteStateListener _mSipInviteStateListener;

	private static String[] PhoneNumberFilterPrefix = { "17909", "11808",
			"12593", "17951", "17911", "+" };

	// audio manager
	private AudioManager _mAudioManager;

	// current sip voice call is muted flag
	private Boolean _mIsSipVoiceCallMuted;

	// current sip voice call using loudspeaker flag
	private Boolean _mIsSipVoiceCallUsingLoudspeaker;

	private boolean sipRegisterCalled;
	
	public BaseSipServices() {
		super();

		// init application context
		_appContext = CommonToolkitApplication.getContext();

		// init audio manager
		_mAudioManager = (AudioManager) _appContext
				.getSystemService(Context.AUDIO_SERVICE);
		
		sipRegisterCalled = false;
	}
	
	protected void setSipRegisterCalled(boolean flag) {
		sipRegisterCalled = flag;
	}

	/**
	 * whether the sip service registration method has been called
	 * @return
	 */
	public boolean isSipRegisterCalled() {
		return sipRegisterCalled;
	}
	
	// make direct dial sip voice call
	public abstract boolean makeDirectDialSipVoiceCall(String calleeName,
			String calleePhone);

	// hangup current sip voice call
	public abstract boolean hangupSipVoiceCall();

	@Override
	public void makeSipVoiceCall(final String calleeName,
			final String calleePhone, final SipCallMode callMode) {
		// before make sip voice call
		beforeMakeSipVoiceCall(calleeName, calleePhone, callMode);

		// new handle post delay 0.5 second to execute make sip voice call
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// check call mode and get make sip voice call result
				boolean _makeSipVoiceCallResult = false;
				String checkedCalleePhone = new String(calleePhone);
				checkedCalleePhone = AddressBookManager.filterNumber(
						checkedCalleePhone,
						AddressBookManager.FILTER_ONLY_IP_PREFIX);
				for (String prefix : PhoneNumberFilterPrefix) {
					int index = calleePhone.indexOf(prefix);
					if (index == 0 && prefix.length() < calleePhone.length()) {
						checkedCalleePhone = calleePhone.substring(prefix
								.length());
					}
				}
				if (calleePhone
						.matches("(^[0]\\d{2,3}\\d{7,8})|(^[1][\\d]{10})|(\\d{9})")) {
					UserBean telUser = UserManager.getInstance().getUser();
					checkedCalleePhone = (String) telUser
							.getValue(TelUser.dialCountryCode.name())
							+ calleePhone;
				}

				switch (callMode) {
				case CALLBACK:
					// mark sip voice call is callback sip voice call call log
					updateSipVoiceCallLog(-2L);

					// make callback sip voice call
					_makeSipVoiceCallResult = makeCallbackSipVoiceCall(
							calleeName, checkedCalleePhone);

					break;

				case DIRECT_CALL:
				default:
					// make direct dial sip voice call
					_makeSipVoiceCallResult = makeDirectDialSipVoiceCall(
							calleeName, checkedCalleePhone);

					break;
				}

				// after make sip voice call
				afterMakeSipVoiceCall(_makeSipVoiceCallResult);
			}
		}, 500);
	}

	@Override
	public boolean hangupSipVoiceCall(Long callDuration) {
		// check current sip voice call using earphone
		if (!isSipVoiceCallUsingLoudspeaker()) {
			// close speaker
			if (_mAudioManager != null) {
				_mAudioManager.setSpeakerphoneOn(false);
			}
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
		if (_mAudioManager != null) {
			// set mode
			_mAudioManager.setMode(AudioManager.MODE_NORMAL);

			// open speaker
			_mAudioManager.setSpeakerphoneOn(true);
		}
	}

	@Override
	public void setSipVoiceCallUsingEarphone() {
		// update current sip voice call using loudspeaker flag
		_mIsSipVoiceCallUsingLoudspeaker = false;

		// set current sip voice call earphone
		// close speaker
		if (_mAudioManager != null) {
			_mAudioManager.setSpeakerphoneOn(false);
		}
	}

	public SipInviteStateListener getSipInviteStateListener() {
		return _mSipInviteStateListener;
	}

	public void setSipInviteStateListener(
			SipInviteStateListener sipInviteStateListener) {
		_mSipInviteStateListener = sipInviteStateListener;
	}

	public BroadcastReceiver getAVSessionStateBroadcastReceiver() {
		return _mAVSessionStateBroadcastReceiver;
	}

	public void setAVSessionStateBroadcastReceiver(
			BroadcastReceiver avSessionStateBroadcastReceiver) {
		_mAVSessionStateBroadcastReceiver = avSessionStateBroadcastReceiver;
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
	private void beforeMakeSipVoiceCall(String calleeName, String calleePhone,
			SipCallMode callMode) {
		// insert sip voice call log
		_mSipVoiceCallLogId = CallLogManager.insertCallLog(calleeName,
				calleePhone);

		// start outgoing call activity and set parameters
		// define the outgoing call intent
		Intent _outgoingCallIntent = new Intent(_appContext,
				OutgoingCallActivity.class);

		// set it as an new task
		_outgoingCallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		// set outgoing call mode, callee phone and callee name
		_outgoingCallIntent.putExtra(OutgoingCallActivity.OUTGOING_CALL_MODE,
				callMode);
		_outgoingCallIntent.putExtra(OutgoingCallActivity.OUTGOING_CALL_PHONE,
				calleePhone);
		_outgoingCallIntent.putExtra(
				OutgoingCallActivity.OUTGOING_CALL_OWNERSHIP, calleeName);

		// start outgoing call activity
		_appContext.startActivity(_outgoingCallIntent);
	}

	// after make sip voice call
	private void afterMakeSipVoiceCall(boolean makeCallResult) {
		// check make call result and update call failed call log
		if (!makeCallResult) {
			// get sip invite state listener
			SipInviteStateListener _sipInviteStateListener = getSipInviteStateListener();

			// check sip invite state listener
			if (null != _sipInviteStateListener) {
				// sip voice call failed
				_sipInviteStateListener.onCallFailed();
			} else {
				Log.e(LOG_TAG,
						"Get sip invite state listener error, sip invite listener = "
								+ _sipInviteStateListener);
			}

			// update sip voice call failed call log
			updateSipVoiceCallLog(-1L);
		}
	}

	// make callback sip voice call
	private boolean makeCallbackSipVoiceCall(String calleeName,
			String calleePhone) {
		// define send callback sip voice call http request listener
		OnHttpRequestListener _sendCallbackSipVoiceCallHttpRequestListener = null;

		OutgoingCallActivity oca = null;
		// update send callback sip voice call http request listener
		try {
			oca = (OutgoingCallActivity) getSipInviteStateListener();
			_sendCallbackSipVoiceCallHttpRequestListener = oca
					.getSendCallbackSipVoiceCallHttpRequestListener();
			UserBean user = UserManager.getInstance().getUser();
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("callee", calleePhone);
			params.put("countryCode",
					(String) user.getValue(TelUser.countryCode.name()));
			params.put("vosPhoneNumber",
					(String) user.getValue(TelUser.vosphone.name()));
			params.put("vosPhonePassword",
					(String) user.getValue(TelUser.vosphone_pwd.name()));
			// send callback sip voice call post request
			HttpUtils.postSignatureRequest(oca.getString(R.string.server_url)
					+ oca.getString(R.string.callback_url),
					PostRequestFormat.URLENCODED, params, null,
					HttpRequestType.ASYNCHRONOUS,
					_sendCallbackSipVoiceCallHttpRequestListener);
		} catch (Exception e) {
			Log.e(LOG_TAG,
					"Get send callback sip voice call http request listener error, sip invite state listener = "
							+ getSipInviteStateListener()
							+ " and exception = "
							+ e.getMessage());
		}

		return true;
	}

}
