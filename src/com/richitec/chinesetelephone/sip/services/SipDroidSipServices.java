package com.richitec.chinesetelephone.sip.services;

import org.sipdroid.sipua.SipdroidEngine;
import org.sipdroid.sipua.ui.Receiver;
import org.sipdroid.sipua.ui.Settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.richitec.chinesetelephone.sip.SipRegisterBean;
import com.richitec.chinesetelephone.sip.listeners.SipInviteStateListener;
import com.richitec.chinesetelephone.sip.listeners.SipRegistrationStateListener;
import com.richitec.commontoolkit.activityextension.AppLaunchActivity;

public class SipDroidSipServices extends BaseSipServices implements
		ISipServices {

	private static final String LOG_TAG = "SipDroidSipServices";

	// application context
	private static final Context APP_CONTEXT = AppLaunchActivity
			.getAppContext();

	// sip registration state listener
	private SipRegistrationStateListener _mSipRegistrationStateListener;

	// sipdroid registration state broadcast receiver
	private BroadcastReceiver _mRegistrationStateBroadcastReceiver;

	// sipdroid sip register intent filter
	private final IntentFilter SIPEGISTER_INTENTFILTER = new IntentFilter();

	// sipdroid audio/video session state broadcast receiver
	private BroadcastReceiver _mAVSessionStateBroadcastReceiver;

	// sipdroid sip invite intent filter
	private final IntentFilter SIPINVITE_INTENTFILTER = new IntentFilter();

	public SipDroidSipServices() {
		super();

		// add sip register intent filter action
		SIPEGISTER_INTENTFILTER
				.addAction(SipdroidEngine.ACTION_REGISTRATION_EVENT);

		// add sip invite intent filter action
		SIPINVITE_INTENTFILTER.addAction(SipdroidEngine.ACTION_INVITE_EVENT);
	}

	@Override
	public void registerSipAccount(SipRegisterBean sipAccount,
			SipRegistrationStateListener sipRegistrationStateListener) {
		// update sip registration state listener
		_mSipRegistrationStateListener = sipRegistrationStateListener;

		// register
		// init sip registration state broadcast receiver
		_mRegistrationStateBroadcastReceiver = new RegistrationStateBroadcastReceiver();

		// register sip registration state broadcast receiver
		APP_CONTEXT.registerReceiver(_mRegistrationStateBroadcastReceiver,
				SIPEGISTER_INTENTFILTER);

		// update configuration
		Editor _edit = PreferenceManager.getDefaultSharedPreferences(
				APP_CONTEXT).edit();

		// set network, use both 3g and wifi
		_edit.putBoolean(Settings.PREF_WLAN, true);
		_edit.putBoolean(Settings.PREF_3G, true);
		_edit.putBoolean(Settings.PREF_EDGE, true);

		// set credentials
		_edit.putString(Settings.PREF_USERNAME, sipAccount.getSipUserName());
		_edit.putString(Settings.PREF_PASSWORD, sipAccount.getSipPwd());
		_edit.putString(Settings.PREF_SERVER, sipAccount.getSipServer());
		_edit.putString(Settings.PREF_DOMAIN, sipAccount.getSipServer());
		_edit.putString(Settings.PREF_DNS, sipAccount.getSipServer());
		_edit.putString(Settings.PREF_PORT, sipAccount.getSipPort().toString());

		// commit changes
		_edit.commit();

		// sip account register
		Receiver.engine(APP_CONTEXT).registerMore();
	}

	@Override
	public void unregisterSipAccount(
			SipRegistrationStateListener sipRegistrationStateListener) {
		Receiver.engine(APP_CONTEXT).unregister();

		// release sipdroid registration state broadcast receiver
		if (null != _mRegistrationStateBroadcastReceiver) {
			APP_CONTEXT
					.unregisterReceiver(_mRegistrationStateBroadcastReceiver);

			_mRegistrationStateBroadcastReceiver = null;
		}
	}

	@Override
	public boolean makeDirectDialSipVoiceCall(String calleeName,
			String calleePhone) {
		// define return result
		boolean _ret = false;

		// invite
		// init sip call audio/video session state receiver
		_mAVSessionStateBroadcastReceiver = new AVSessionStateBroadcastReceiver();

		// register sipdroid audio/video session state receiver
		APP_CONTEXT.registerReceiver(_mAVSessionStateBroadcastReceiver,
				SIPINVITE_INTENTFILTER);

		// sipdroid make an new sip voice call
		_ret = Receiver.engine(APP_CONTEXT).call(calleePhone, true);

		return _ret;

	}

	@Override
	public boolean hangupSipVoiceCall() {
		// define return result
		boolean _ret = false;

		// sipdroid hangup current sip voice call
		Receiver.engine(APP_CONTEXT).rejectcall();
		_ret = true;

		return _ret;
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

	// inner class
	// sipdroid registration state broadcast receiver
	class RegistrationStateBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// check the action for sipdroid registration Event
			if (SipdroidEngine.ACTION_REGISTRATION_EVENT.equals(intent
					.getAction())) {
				// get sipdroid registration event arguments
				String _registrationEventArgs = intent
						.getStringExtra(SipdroidEngine.EXTRA_EMBEDDED);

				// check the arguments
				if (null == _registrationEventArgs) {
					Log.e(LOG_TAG,
							"Sipdroid registration event arguments is null");
				} else {
					// check registration event type
					if (_registrationEventArgs.equalsIgnoreCase("succeed")) {
						Log.d(LOG_TAG, "You are now registered :)");

						_mSipRegistrationStateListener.onRegisterSuccess();
					} else if (_registrationEventArgs
							.equalsIgnoreCase("failed")) {
						Log.d(LOG_TAG, "Failed to register :(");

						_mSipRegistrationStateListener.onRegisterFailed();
					}
				}
			}
		}

	}

	// sipdroid audio/video session state broadcast receiver
	class AVSessionStateBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// check the action for sipdroid invite event
			if (SipdroidEngine.ACTION_INVITE_EVENT.equals(intent.getAction())) {
				// get sipdroid invite event arguments
				String _inviteEventArgs = intent
						.getStringExtra(SipdroidEngine.EXTRA_EMBEDDED);

				// check the arguments
				if (null == _inviteEventArgs) {
					Log.e(LOG_TAG, "Sipdroid invite event arguments is null");
				} else {
					// get sip invite listener
					SipInviteStateListener _sipInviteStateListener = getSipInviteStateListener();

					// check sipdroid invite event type
					if (_inviteEventArgs.equalsIgnoreCase("terminated")) {
						_sipInviteStateListener.onCallTerminated();
					} else if (_inviteEventArgs.equalsIgnoreCase("incall")) {
						_sipInviteStateListener.onCallSpeaking();
					} else {
						Log.d(LOG_TAG, "Sipdroid other invite state = "
								+ _inviteEventArgs);
					}
				}
			}
		}

	}

}