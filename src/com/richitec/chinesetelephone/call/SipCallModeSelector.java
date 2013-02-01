package com.richitec.chinesetelephone.call;

import android.util.Log;

import com.richitec.commontoolkit.utils.DataStorageUtils;

public class SipCallModeSelector {

	private static final String LOG_TAG = SipCallModeSelector.class
			.getCanonicalName();

	// key of store sip call mode select pattern
	private static final String SIPCALLMODE_SELECTPATTERN_KEY = "sipCallMode_selectPattern_storingKey";

	// get sip call mode select pattern
	public static SipCallModeSelectPattern getSipCallModeSelectPattern() {
		// define sip call mode select pattern, default is manual
		SipCallModeSelectPattern _sipCallModeSelectPattern = SipCallModeSelectPattern.MANUAL;

		// get sip call mode select pattern string from shared preferences
		String _sipCallModeSelectPatternString = DataStorageUtils
				.getString(SIPCALLMODE_SELECTPATTERN_KEY);

		// check sip call mode select pattern string and convert to sip call
		// mode select pattern
		if (null != _sipCallModeSelectPatternString) {
			if (SipCallModeSelectPattern.DIRECT_CALL.name().equalsIgnoreCase(
					_sipCallModeSelectPatternString)) {
				_sipCallModeSelectPattern = SipCallModeSelectPattern.DIRECT_CALL;
			} else if (SipCallModeSelectPattern.CALLBACK.name()
					.equalsIgnoreCase(_sipCallModeSelectPatternString)) {
				_sipCallModeSelectPattern = SipCallModeSelectPattern.CALLBACK;
			} else if (SipCallModeSelectPattern.AUTO.name().equalsIgnoreCase(
					_sipCallModeSelectPatternString)) {
				_sipCallModeSelectPattern = SipCallModeSelectPattern.AUTO;
			} else if (SipCallModeSelectPattern.MANUAL.name().equalsIgnoreCase(
					_sipCallModeSelectPatternString)) {
				_sipCallModeSelectPattern = SipCallModeSelectPattern.MANUAL;
			} else {
				Log.e(LOG_TAG,
						"Convert sip call mode select pattern string to sip call mode select pattern error, call mode select pattern string = "
								+ _sipCallModeSelectPatternString);
			}
		} else {
			Log.w(LOG_TAG,
					"Sip call mode select pattern not existed in shared preferences, please set first");
		}

		return _sipCallModeSelectPattern;
	}

	// set sip call mode select pattern
	public static void setSipCallModeSelectPattern(
			SipCallModeSelectPattern sipCallModeSelectPattern) {
		// store using shared preferences
		DataStorageUtils.putObject(SIPCALLMODE_SELECTPATTERN_KEY,
				sipCallModeSelectPattern.name());
	}

	// inner class
	// sip call mode select pattern
	public enum SipCallModeSelectPattern {

		// direct dial, callback, manual and auto
		DIRECT_CALL, CALLBACK, MANUAL, AUTO

	}

}
