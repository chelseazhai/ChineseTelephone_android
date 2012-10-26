package com.richitec.chinesetelephone.call;

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

public class SampleDoubangoNGNStack {

	private static final String LOG_TAG = "SampleDoubangoNGNStack";

	// call phone prefix
	private final String CallPhonePrefix = "86";

	// broadcast receive
	private BroadcastReceiver _mSipBroadCastRecv;

	// doubango ngnEngine
	private final NgnEngine _mEngine;

	// doubango ngnConfigService and sipService
	private final INgnConfigurationService _mConfigurationService;
	private final INgnSipService _mSipService;

	// credentials
	private static final String SIP_USERNAME = "8003";
	private static final String SIP_PASSWORD = "622021";
	private static final String SIP_SERVER_HOST = "210.56.60.150";
	private static final String SIP_DOMAIN = "richitec.com";
	private static final String SIP_REALM = "richitec.com";
	private static final int SIP_SERVER_PORT = 5060;

	// singleton instance
	private static volatile SampleDoubangoNGNStack _singletonInstance;

	private SampleDoubangoNGNStack() {
		// init doubango ngnEngine
		_mEngine = NgnEngine.getInstance();

		// get doubango ngnConfigService and sipService
		_mConfigurationService = _mEngine.getConfigurationService();
		_mSipService = _mEngine.getSipService();
	}

	// get sample doubangoNGNStack singleton instance
	public static SampleDoubangoNGNStack getInstance() {
		if (null == _singletonInstance) {
			synchronized (SampleDoubangoNGNStack.class) {
				if (null == _singletonInstance) {
					_singletonInstance = new SampleDoubangoNGNStack();
				}
			}
		}

		return _singletonInstance;
	}

	public BroadcastReceiver getSipBroadCastRecv() {
		return _mSipBroadCastRecv;
	}

	public NgnEngine getNgnEngine() {
		return _mEngine;
	}

	public INgnSipService getSipService() {
		return _mSipService;
	}

	// init account register receiver
	public void initAccountRegisterReceiver(Context context) {
		// Listen for registration events
		_mSipBroadCastRecv = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// get action
				final String _action = intent.getAction();

				// registration Event
				if (NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT
						.equals(_action)) {
					// get ngnEvent arguments
					NgnRegistrationEventArgs _ngnEventArgs = intent
							.getParcelableExtra(NgnEventArgs.EXTRA_EMBEDDED);

					// check arguments
					if (_ngnEventArgs == null) {
						return;
					}
					switch (_ngnEventArgs.getEventType()) {
					case REGISTRATION_NOK:
						Log.d(LOG_TAG, "Failed to register :(");
						break;
					case UNREGISTRATION_OK:
						Log.d(LOG_TAG, "You are now unregistered :)");
						break;
					case REGISTRATION_OK:
						Log.d(LOG_TAG, "You are now registered :)");
						break;
					case REGISTRATION_INPROGRESS:
						Log.d(LOG_TAG, "Trying to register...");
						break;
					case UNREGISTRATION_INPROGRESS:
						Log.d(LOG_TAG, "Trying to unregister...");
						break;
					case UNREGISTRATION_NOK:
						Log.d(LOG_TAG, "Failed to unregister :(");
						break;
					}
				}
			}
		};

		final IntentFilter intentFilter = new IntentFilter();
		intentFilter
				.addAction(NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT);
		context.registerReceiver(_mSipBroadCastRecv, intentFilter);
	}

	// release account register receiver
	public void releaseAccountRegisterReceiver(Context context) {
		context.unregisterReceiver(_mSipBroadCastRecv);

		_mSipBroadCastRecv = null;
	}

	// account register
	public void accountRegister() {
		// Set network
		_mConfigurationService.putBoolean(NgnConfigurationEntry.NETWORK_USE_3G,
				true);
		_mConfigurationService.putBoolean(
				NgnConfigurationEntry.NETWORK_USE_WIFI, true);

		// Set credentials
		_mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPI,
				SIP_USERNAME);
		_mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPU,
				String.format("sip:%s@%s", SIP_USERNAME, SIP_DOMAIN));
		_mConfigurationService.putString(
				NgnConfigurationEntry.IDENTITY_PASSWORD, SIP_PASSWORD);
		_mConfigurationService.putString(
				NgnConfigurationEntry.NETWORK_PCSCF_HOST, SIP_SERVER_HOST);
		_mConfigurationService.putInt(NgnConfigurationEntry.NETWORK_PCSCF_PORT,
				SIP_SERVER_PORT);
		_mConfigurationService.putString(NgnConfigurationEntry.NETWORK_REALM,
				SIP_REALM);

		// VERY IMPORTANT: Commit changes
		_mConfigurationService.commit();
	}

	public boolean makeVoiceCall(Context context, String phoneNumber) {
		boolean _ret = false;

		// generate sip phone number
		final String _sipPhoneUri = NgnUriUtils.makeValidSipUri(String.format(
				"sip:%s@%s", CallPhonePrefix + phoneNumber, SIP_DOMAIN));

		// check sip phone uri
		if (_sipPhoneUri == null) {
			Log.e(LOG_TAG, "Failed to normalize sip uri '" + phoneNumber + "'");
		} else {
			// define audio avSession
			NgnAVSession _audioAVSession = NgnAVSession.createOutgoingSession(
					_mSipService.getSipStack(), NgnMediaType.Audio);

			Intent i = new Intent(context, OutgoingCallActivity.class);

			i.putExtra(OutgoingCallActivity.OUTGOING_CALL_SIPSESSIONID,
					_audioAVSession.getId());
			i.putExtra(OutgoingCallActivity.OUTGOING_CALL_PHONE, phoneNumber);

			context.startActivity(i);

			_ret = _audioAVSession.makeCall(_sipPhoneUri);
		}

		return _ret;
	}

}
