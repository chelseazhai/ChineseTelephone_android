package com.richitec.chinesetelephone.call;

import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.call.SipCallModeSelector.SipCallModeSelectPattern;
import com.richitec.chinesetelephone.sip.SipUtils;
import com.richitec.chinesetelephone.tab7tabcontent.ContactListTabContentActivity.ContactPhoneNumbersSelectPopupWindow;
import com.richitec.commontoolkit.CTApplication;
import com.richitec.commontoolkit.customcomponent.CTPopupWindow;
import com.richitec.commontoolkit.utils.NetworkInfoUtils;
import com.richitec.commontoolkit.utils.NetworkInfoUtils.NoActiveNetworkException;

public class OutgoingCallGenerator {

	private static final String LOG_TAG = OutgoingCallGenerator.class
			.getCanonicalName();

	// contact phone dial mode select popup window
	private ContactPhoneDialModeSelectPopupWindow _mContactPhoneDialModeSelectPopupWindow;

	// contact phone dial mode select popup window dependent view
	private View _mContactPhoneDialModeSelectPopupWindowDependentView;

	// contact info: display name and phone numbers
	private String _mContactName;
	private List<String> _mContactPhones;

	// for dial
	// dial phone textView and and previous dial phone
	private TextView _mDialPhoneTextView;
	private StringBuffer _mPreviousDialPhone;

	public OutgoingCallGenerator(
			View contactDialModeSelectPopupWindowDependentView) {
		// set contact phone dial mode select popup window dependent view
		_mContactPhoneDialModeSelectPopupWindowDependentView = contactDialModeSelectPopupWindowDependentView;
	}

	// set dial phone textView for clearing dial phone textView text and
	// previous dial phone for saving previous dial phone
	public OutgoingCallGenerator setDialPhoneTextView4ClearingText7PreviousDialPhone4Saving(
			TextView dialPhoneTextView, StringBuffer previousDialPhone) {
		_mDialPhoneTextView = dialPhoneTextView;
		_mPreviousDialPhone = previousDialPhone;

		return this;
	}

	// generate an new outgoing call
	public void generateNewOutgoingCall(String contactName,
			List<String> contactPhones) {
		// set contact info: display name and phone numbers
		_mContactName = contactName;
		_mContactPhones = contactPhones;

		// get and check sip call mode select pattern
		switch (SipCallModeSelector.getSipCallModeSelectPattern()) {
		case DIRECT_CALL:
			// check contact for generate an new outgoing call: direct dial
			checkContact4GenNewOutgongCall(SipCallMode.DIRECT_CALL,
					SipCallModeSelectPattern.DIRECT_CALL);
			break;

		case CALLBACK:
			// check contact for generate an new outgoing call: callback
			checkContact4GenNewOutgongCall(SipCallMode.CALLBACK,
					SipCallModeSelectPattern.CALLBACK);
			break;

		case AUTO:
			try {
				// get and check current active network type
				switch (NetworkInfoUtils.getNetworkType()) {
				case ConnectivityManager.TYPE_WIFI:
					// check contact for generate an new outgoing call: auto
					// direct dial
					checkContact4GenNewOutgongCall(SipCallMode.DIRECT_CALL,
							SipCallModeSelectPattern.AUTO);
					break;

				case ConnectivityManager.TYPE_MOBILE:
				default:
					// check contact for generate an new outgoing call: auto
					// callback
					checkContact4GenNewOutgongCall(SipCallMode.CALLBACK,
							SipCallModeSelectPattern.AUTO);
					break;
				}
			} catch (NoActiveNetworkException e) {
				Log.e(LOG_TAG,
						"Generate an new outgoing call error, because here is no active network currently, exception message = "
								+ e.getMessage());

				Toast.makeText(CTApplication.getContext(),
						R.string.currentActiveNetworkNotAvailable,
						Toast.LENGTH_SHORT).show();

				e.printStackTrace();
			}
			break;

		case MANUAL:
		default:
			// define contact phone dial mode select pupup window show it with
			// animation
			(_mContactPhoneDialModeSelectPopupWindow = new ContactPhoneDialModeSelectPopupWindow(
					R.layout.contact_phone_dialmode_select_popupwindow_layout,
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT))
					.showAtLocationWithAnimation(
							_mContactPhoneDialModeSelectPopupWindowDependentView,
							Gravity.CENTER, 0, 0);
			break;
		}
	}

	// check contact for generate an new outgoing call
	private void checkContact4GenNewOutgongCall(final SipCallMode dialMode,
			SipCallModeSelectPattern dialModeSelectPattern) {
		// check dial mode select pattern
		if (SipCallModeSelectPattern.MANUAL == dialModeSelectPattern) {
			_mContactPhoneDialModeSelectPopupWindow.dismiss();
		}

		// check contact phone size
		if (1 == _mContactPhones.size()) {
			// make sip voice call
			SipUtils.makeSipVoiceCall(_mContactName, _mContactPhones.get(0),
					dialMode);

			// check dial phone textView
			if (null != _mDialPhoneTextView) {
				// save previous dial phone and clear dial phone textView
				// text
				CharSequence _previousDialPhone = _mDialPhoneTextView.getText();
				_mDialPhoneTextView.setText("");
				_mPreviousDialPhone.append(_previousDialPhone);
			} else {
				Log.e(LOG_TAG,
						"Get dial phone textView for clear its text error, dial phone textView is null");
			}
		} else {
			// define contact phone numbers select popup window, set contact
			// phones for select and show it using handle to delay popup window
			// switch duration if needed
			new Handler().postDelayed(
					new Runnable() {

						@Override
						public void run() {
							(new ContactPhoneNumbersSelectPopupWindow(
									R.layout.contact_phonenumbers_select_popupwindow_layout,
									LayoutParams.FILL_PARENT,
									LayoutParams.FILL_PARENT)
									.setContactPhones4Selecting(_mContactName,
											_mContactPhones, dialMode))
									.showAtLocationWithAnimation(
											_mContactPhoneDialModeSelectPopupWindowDependentView,
											Gravity.CENTER, 0, 0);
						}
					},
					SipCallModeSelectPattern.MANUAL == dialModeSelectPattern ? _mContactPhoneDialModeSelectPopupWindow.DISMISS7NEXTSHOWNANIMATION_SWITCHDURATION
							: 0);
		}
	}

	// inner class
	// contact phone dial mode select popup window
	class ContactPhoneDialModeSelectPopupWindow extends CTPopupWindow {

		public ContactPhoneDialModeSelectPopupWindow(int resource, int width,
				int height, boolean focusable, boolean isBindDefListener) {
			super(resource, width, height, focusable, isBindDefListener);

			// init contact phone dial mode select popup window UI
			initContactPhoneDialModeSelectPopupWindowUI();
		}

		public ContactPhoneDialModeSelectPopupWindow(int resource, int width,
				int height) {
			super(resource, width, height);

			// init contact phone dial mode select popup window UI
			initContactPhoneDialModeSelectPopupWindowUI();
		}

		@Override
		protected void bindPopupWindowComponentsListener() {
			// bind contact phone dial mode select direct dial, callback and
			// cancel button click listener
			((Button) getContentView().findViewById(
					R.id.contactPhone_dialMode_select_directdialBtn))
					.setOnClickListener(new ContactPhoneDialModeSelectDirectDialBtnOnClickListener());

			((Button) getContentView().findViewById(
					R.id.contactPhone_dialMode_select_callbackBtn))
					.setOnClickListener(new ContactPhoneDialModeSelectCallbackBtnOnClickListener());

			((Button) getContentView().findViewById(
					R.id.contactPhone_dialMode_select_cancelBtn))
					.setOnClickListener(new ContactPhoneDialModeSelectCancelBtnOnClickListener());
		}

		@Override
		protected void resetPopupWindow() {
			// nothing to do
		}

		// init contact phone dial mode select popup window UI
		private void initContactPhoneDialModeSelectPopupWindowUI() {
			// get application context
			Context _appContext = CTApplication.getContext();

			// set contact phone dial mode select title textView text
			((TextView) getContentView().findViewById(
					R.id.contactPhone_dialMode_select_titleTextView))
					.setText(_appContext
							.getResources()
							.getString(
									R.string.contactPhone_dialMode_selectPopupWindow_titleTextView_text)
							.replace("***", _mContactName));

			// update contact phone dial mode select direct dial and callback
			// button title
			// define tip text of contact phone for selecting
			String _contactPhone4selecting;

			// check contact phone number count and update contact phone for
			// selecting
			if (1 == _mContactPhones.size()) {
				_contactPhone4selecting = "(" + _mContactPhones.get(0) + ")";
			} else {
				_contactPhone4selecting = "("
						+ _mContactPhones.size()
						+ " "
						+ _appContext
								.getResources()
								.getString(
										R.string.contactPhone_dialMode_selectPopupWindow_contactPhones_4select)
						+ ")";
			}

			// reset direct dial and callback button text
			((Button) getContentView().findViewById(
					R.id.contactPhone_dialMode_select_directdialBtn))
					.setText(_appContext
							.getResources()
							.getString(
									R.string.contactPhone_dialMode_selectPopupWindow_directdialBtn_title)
							+ _contactPhone4selecting);
			((Button) getContentView().findViewById(
					R.id.contactPhone_dialMode_select_callbackBtn))
					.setText(_appContext
							.getResources()
							.getString(
									R.string.contactPhone_dialMode_selectPopupWindow_callbackBtn_title)
							+ _contactPhone4selecting);
		}

		// inner class
		// contact phone dial mode select direct dial button on click listener
		class ContactPhoneDialModeSelectDirectDialBtnOnClickListener implements
				OnClickListener {

			@Override
			public void onClick(View v) {
				// check contact for generate an new outgoing call: manual
				// direct dial
				checkContact4GenNewOutgongCall(SipCallMode.DIRECT_CALL,
						SipCallModeSelectPattern.MANUAL);
			}

		}

		// contact phone dial mode select callback button on click listener
		class ContactPhoneDialModeSelectCallbackBtnOnClickListener implements
				OnClickListener {

			@Override
			public void onClick(View v) {
				// check contact for generate an new outgoing call: manual
				// callback
				checkContact4GenNewOutgongCall(SipCallMode.CALLBACK,
						SipCallModeSelectPattern.MANUAL);
			}

		}

		// contact phone dial mode select cancel button on click listener
		class ContactPhoneDialModeSelectCancelBtnOnClickListener implements
				OnClickListener {

			@Override
			public void onClick(View v) {
				// dismiss contact phone dial mode select popup window with
				// animation
				dismissWithAnimation();
			}

		}

	}

}
