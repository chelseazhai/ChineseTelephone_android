package com.richitec.chinesetelephone.sip.services;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.events.NgnEventArgs;
import org.doubango.ngn.events.NgnRegistrationEventArgs;
import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.utils.NgnConfigurationEntry;
import org.doubango.ngn.utils.NgnUriUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.richitec.chinesetelephone.call.OutgoingCallActivity;
import com.richitec.chinesetelephone.sip.SipCallMode;
import com.richitec.chinesetelephone.sip.SipRegisterBean;
import com.richitec.chinesetelephone.sip.listeners.SipRegistrationStateListener;
import com.richitec.commontoolkit.activityextension.AppLaunchActivity;

public class DoubangoSipServices implements ISipServices {

	private static final String LOG_TAG = "DoubangoSipServices";

	// doubango ngn engine instance
	private final NgnEngine NGN_ENGINE = NgnEngine.getInstance();

	// sip registration state listener
	private SipRegistrationStateListener _mSipRegistrationStateListener;

	// doubango ngn registration state broadcast receiver
	private BroadcastReceiver _mRegistrationStateBroadcastReceiver;

	public DoubangoSipServices() {
		super();

		// init doubango ngn registration state broadcast receiver
		_mRegistrationStateBroadcastReceiver = new RegistrationStateBroadcastReceiver();

		// register sip registration state broadcast receiver
		// new sip register intent filter
		IntentFilter _sipRegisterIntentFilter = new IntentFilter();
		_sipRegisterIntentFilter
				.addAction(NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT);

		AppLaunchActivity.getAppContext().registerReceiver(
				_mRegistrationStateBroadcastReceiver, _sipRegisterIntentFilter);
	}

	@Override
	public void registerSipAccount(SipRegisterBean sipAccount,
			SipRegistrationStateListener sipRegistrationStateListener) {
		// update sip registration state listener
		_mSipRegistrationStateListener = sipRegistrationStateListener;

		// starts ngn engine
		if (!NGN_ENGINE.isStarted()) {
			if (NGN_ENGINE.start()) {
				Log.d(LOG_TAG, "Ngn engine started :)");
			} else {
				Log.e(LOG_TAG, "Failed to start ngn engine :(");
			}
		} else {
			Log.d(LOG_TAG, "Ngn engine had been started");
		}

		// get doubango ngn sip service
		INgnSipService _sipService = NGN_ENGINE.getSipService();

		// register sip account
		if (NGN_ENGINE.isStarted() && !_sipService.isRegistered()) {
			// get doubango ngn config service
			INgnConfigurationService _configurationService = NGN_ENGINE
					.getConfigurationService();

			// set network, use both 3g and wifi
			_configurationService.putBoolean(
					NgnConfigurationEntry.NETWORK_USE_3G, true);
			_configurationService.putBoolean(
					NgnConfigurationEntry.NETWORK_USE_WIFI, true);

			// set credentials
			_configurationService.putString(
					NgnConfigurationEntry.IDENTITY_IMPI,
					sipAccount.getSipUserName());
			_configurationService.putString(
					NgnConfigurationEntry.IDENTITY_IMPU, String.format(
							"sip:%s@%s", sipAccount.getSipUserName(),
							sipAccount.getSipDomain()));
			_configurationService.putString(
					NgnConfigurationEntry.IDENTITY_PASSWORD,
					sipAccount.getSipPwd());
			_configurationService.putString(
					NgnConfigurationEntry.NETWORK_PCSCF_HOST,
					sipAccount.getSipServer());
			_configurationService.putString(
					NgnConfigurationEntry.NETWORK_REALM,
					sipAccount.getSipRealm());
			_configurationService.putInt(
					NgnConfigurationEntry.NETWORK_PCSCF_PORT,
					sipAccount.getSipPort());

			// commit changes
			_configurationService.commit();

			// sip account register
			_sipService.register(AppLaunchActivity.getAppContext());
		}
	}

	@Override
	public void unregisterSipAccount(
			SipRegistrationStateListener sipRegistrationStateListener) {
		// get doubango ngn sip service
		INgnSipService _sipService = NGN_ENGINE.getSipService();

		// unregister sip account
		if (NGN_ENGINE.isStarted() && _sipService.isRegistered()) {
			_sipService.unRegister();
		}
	}

	// inner class
	// doubango ngn registration state broadcast receiver
	class RegistrationStateBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// get the action
			String _action = intent.getAction();

			// check the action for ngn registration Event
			if (NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT
					.equals(_action)) {
				// get ngn registration event arguments
				NgnRegistrationEventArgs _ngnRegistrationEventArgs = intent
						.getParcelableExtra(NgnEventArgs.EXTRA_EMBEDDED);

				// check the arguments
				if (null == _ngnRegistrationEventArgs) {
					Log.e(LOG_TAG,
							"Doubango ngn registration event arguments is null");
				} else {
					// check registration event type
					switch (_ngnRegistrationEventArgs.getEventType()) {
					case REGISTRATION_OK:
						Log.d(LOG_TAG, "You are now registered :)");

						_mSipRegistrationStateListener.onRegisterSuccess();
						break;
					case REGISTRATION_NOK:
						Log.d(LOG_TAG, "Failed to register :(");

						_mSipRegistrationStateListener.onRegisterFailed();
						break;
					case UNREGISTRATION_OK:
						Log.d(LOG_TAG, "You are now unregistered :)");

						_mSipRegistrationStateListener.onUnRegisterSuccess();
						break;
					case UNREGISTRATION_NOK:
						Log.d(LOG_TAG, "Failed to unregister :(");

						_mSipRegistrationStateListener.onUnRegisterFailed();
						break;
					case REGISTRATION_INPROGRESS:
						Log.d(LOG_TAG, "Trying to register...");
						break;
					case UNREGISTRATION_INPROGRESS:
						Log.d(LOG_TAG, "Trying to unregister...");
						break;
					}
				}
			}
		}

	}

	@Override
	public void makeSipVoiceCall(String calleeDisplayName,
			String calleePhoneNumber, SipCallMode callMode) {
		// get doubango ngn sip service
		INgnSipService _sipService = NGN_ENGINE.getSipService();

		// re-register
		if (!_sipService.isRegistered()) {
			_sipService.register(AppLaunchActivity.getAppContext());
		}

		// call phone prefix
		final String CallPhonePrefix = "86";

		// generate sip phone number
		final String _sipPhoneUri = NgnUriUtils
				.makeValidSipUri(String.format(
						"sip:%s@%s",
						calleePhoneNumber,
						NGN_ENGINE.getConfigurationService().getString(
								NgnConfigurationEntry.NETWORK_PCSCF_HOST, "")));

		// check sip phone uri
		if (_sipPhoneUri == null) {
			Log.e(LOG_TAG, "Failed to normalize sip uri '" + calleePhoneNumber
					+ "'");
		} else {
			// define audio session
			NgnAVSession _audioSession = NgnAVSession.createOutgoingSession(
					NGN_ENGINE.getSipService().getSipStack(),
					NgnMediaType.Audio);

			// define the outgoing call intent
			Intent _outgoingCallIntent = new Intent(
					AppLaunchActivity.getAppContext(),
					OutgoingCallActivity.class);

			// set new task flag
			_outgoingCallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			// set outgoing call phone, ownership and audio session id
			_outgoingCallIntent
					.putExtra(OutgoingCallActivity.OUTGOING_CALL_PHONE,
							calleePhoneNumber);

			_outgoingCallIntent.putExtra(
					OutgoingCallActivity.OUTGOING_CALL_OWNERSHIP,
					calleeDisplayName);

			_outgoingCallIntent.putExtra(
					OutgoingCallActivity.OUTGOING_CALL_SIPSESSIONID,
					_audioSession.getId());

			// start outgoing call activity and make an new call
			AppLaunchActivity.getAppContext()
					.startActivity(_outgoingCallIntent);

			boolean _ret = _audioSession.makeCall(_sipPhoneUri);
			Log.d(LOG_TAG, "Doubango make call result = " + _ret);
		}
	}

}
