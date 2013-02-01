package com.richitec.chinesetelephone.sip;

import com.richitec.commontoolkit.utils.DataStorageUtils;

public class SipCallModeSetting {

	// key of store sip call mode select pattern
	private static final String SIPCALLMODE_SELECTPATTERN_KEY = "sipCallMode_selectPattern_storingKey";

	// get sip call mode select pattern
	public static SipCallModeSelectPattern getSipCallModeSelectPattern() {
		return null;
	}

	// set sip call mode select pattern
	public static void setSipCallModeSelectPattern(
			SipCallModeSelectPattern sipCallModeSelectPattern) {
		DataStorageUtils.putObject(SIPCALLMODE_SELECTPATTERN_KEY,
				sipCallModeSelectPattern.name());
	}

	// inner class
	// sip call mode
	public enum SipCallMode {

		// direct dial and callback
		DIRECT_CALL, CALLBACK

	}

	// sip call mode select pattern
	public enum SipCallModeSelectPattern {

		// direct dial, callback, manual and auto
		DIRECT_CALL, CALLBACK, MANUAL, AUTO

	}

}
