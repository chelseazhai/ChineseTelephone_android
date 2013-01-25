package com.richitec.chinesetelephone.call;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.sip.SipCallMode;
import com.richitec.chinesetelephone.sip.SipUtils;
import com.richitec.commontoolkit.CTApplication;
import com.richitec.commontoolkit.customcomponent.CTPopupWindow;

public class ContactPhoneDialModeSelectpopupWindow extends CTPopupWindow {

	private static final String LOG_TAG = "ContactPhoneDialModeSelectpopupWindow";

	// select contact info: display name and phone numbers
	private String _mSelectContactName;
	private List<String> _mSelectContactPhones;

	// dial phone textView and and previous dial phone
	private TextView _mDialPhoneTextView;
	private StringBuffer _mPreviousDialPhone;

	// contact phone numbers select popup window and its dependent view
	private CTPopupWindow _mContactPhoneNumbersSelectPopupWindow;
	private View _mContactPhoneNumbersSelectPopupWindowDependentView;

	public ContactPhoneDialModeSelectpopupWindow(int resource, int width,
			int height, boolean focusable, boolean isBindDefListener) {
		super(resource, width, height, focusable, isBindDefListener);
	}

	public ContactPhoneDialModeSelectpopupWindow(int resource, int width,
			int height) {
		super(resource, width, height);
	}

	public ContactPhoneDialModeSelectpopupWindow(Context context,
			AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ContactPhoneDialModeSelectpopupWindow(Context context,
			AttributeSet attrs) {
		super(context, attrs);
	}

	public ContactPhoneDialModeSelectpopupWindow(Context context) {
		super(context);
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

	// set dial phone textView and previous dial phone
	public void setDialPhoneTextView(TextView dialPhoneTextView,
			StringBuffer previousDialPhone) {
		_mDialPhoneTextView = dialPhoneTextView;
		_mPreviousDialPhone = previousDialPhone;
	}

	// set contact phone numbers select popup window and its dependent view
	public void setContactPhoneNumbersSelectPopupWindow7ItsDependentView(
			CTPopupWindow contactPhoneNumbersSelectPopupWindow,
			View contactPhoneNumbersSelectPopupWindowDependentView) {
		_mContactPhoneNumbersSelectPopupWindow = contactPhoneNumbersSelectPopupWindow;
		_mContactPhoneNumbersSelectPopupWindowDependentView = contactPhoneNumbersSelectPopupWindowDependentView;
	}

	// set callee contact info
	public void setCalleeContactInfo(String contactName,
			List<String> contactPhones) {
		// get application context
		Context _appContext = CTApplication.getContext();

		// update select contact info
		_mSelectContactName = contactName;
		_mSelectContactPhones = contactPhones;

		// set contact phone dial mode select title textView text
		((TextView) getContentView().findViewById(
				R.id.contactPhone_dialMode_select_titleTextView))
				.setText(_appContext
						.getResources()
						.getString(
								R.string.contactPhone_dialMode_selectPopupWindow_titleTextView_text)
						.replace("***", contactName));

		// update contact phone dial mode select direct dial and callback
		// button title
		// define tip text of contact phone for selecting
		String _contactPhone4selecting;

		// check contact phone number count and update contact phone for
		// selecting
		if (1 == contactPhones.size()) {
			_contactPhone4selecting = "(" + contactPhones.get(0) + ")";
		} else {
			_contactPhone4selecting = "("
					+ contactPhones.size()
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

	// check contact for dial mode select
	private void checkContact4DialModeSelect(SipCallMode dialMode) {
		// dismiss contact phone dial mode select popup window
		dismiss();

		// check select contact phone size
		if (1 == _mSelectContactPhones.size()) {
			// make sip voice call
			SipUtils.makeSipVoiceCall(_mSelectContactName,
					_mSelectContactPhones.get(0), dialMode);

			// check dial phone textView
			if (null != _mDialPhoneTextView) {
				// save previous dial phone and clear dial phone textView text
				CharSequence _previousDialPhone = _mDialPhoneTextView.getText();
				_mDialPhoneTextView.setText("");
				_mPreviousDialPhone.append(_previousDialPhone);
			} else {
				Log.e(LOG_TAG,
						"Get dial phone textView for clear its text error, dial phone textView is null");
			}
		} else {
			// check contact phone numbers select popup window and its dependent
			// view
			if (null != _mContactPhoneNumbersSelectPopupWindow
					&& null != _mContactPhoneNumbersSelectPopupWindowDependentView) {
				try {
					// set contact phone numbers for selecting using java
					// reflection
					_mContactPhoneNumbersSelectPopupWindow
							.getClass()
							.getMethod(
									"setContactPhones4Selecting",
									new Class[] { String.class, List.class,
											SipCallMode.class })
							.invoke(_mContactPhoneNumbersSelectPopupWindow,
									new Object[] { _mSelectContactName,
											_mSelectContactPhones, dialMode });

					// show contact phone numbers select popup window with
					// animation
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							_mContactPhoneNumbersSelectPopupWindow
									.showAtLocationWithAnimation(
											_mContactPhoneNumbersSelectPopupWindowDependentView,
											Gravity.CENTER, 0, 0);
						}
					}, DISMISSANIMATION_DURATION);
				} catch (Exception e) {
					Log.e(LOG_TAG,
							"Contact phone numbers select popup window reflection method:'setContactPhones4Selecting' error and exception message = "
									+ e.getMessage());
				}
			} else {
				Log.e(LOG_TAG,
						"Check contact phone numbers select popup window and its dependent view error, contact phone numbers select popup window = "
								+ _mContactPhoneNumbersSelectPopupWindow
								+ " and its dependent view = "
								+ _mContactPhoneNumbersSelectPopupWindowDependentView);
			}
		}
	}

	// inner class
	// contact phone dial mode select direct dial button on click listener
	class ContactPhoneDialModeSelectDirectDialBtnOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			// check contact for dial mode select: direct dial
			checkContact4DialModeSelect(SipCallMode.DIRECT_CALL);
		}

	}

	// contact phone dial mode select callback button on click listener
	class ContactPhoneDialModeSelectCallbackBtnOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			// check contact for dial mode select: callback
			checkContact4DialModeSelect(SipCallMode.CALLBACK);
		}

	}

	// contact phone dial mode select cancel button on click listener
	class ContactPhoneDialModeSelectCancelBtnOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			// dismiss contact phone dial mode select popup window
			dismiss();
		}

	}

}
