package com.richitec.chinesetelephone.tab7tabcontent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.call.ContactPhoneDialModeSelectpopupWindow;
import com.richitec.commontoolkit.activityextension.NavigationActivity;
import com.richitec.commontoolkit.addressbook.AddressBookManager;
import com.richitec.commontoolkit.utils.CommonUtils;
import com.richitec.commontoolkit.utils.DpPixUtils;
import com.richitec.commontoolkit.utils.ToneGeneratorUtils;
import com.richitec.internationalcode.AreaAbbreviation;

public class DialTabContentActivity extends NavigationActivity {

	private static final String LOG_TAG = "DialTabContentActivity";

	// dial phone button gridView keys
	public static final String DIAL_PHONE_BUTTON_CODE = "dial_phone_button_code";
	public static final String DIAL_PHONE_BUTTON_IMAGE = "dial_phone_button_image";
	public static final String DIAL_PHONE_BUTTON_ONCLICKLISTENER = "dial_phone_button_onClickListener";
	public static final String DIAL_PHONE_BUTTON_ONLONGCLICKLISTENER = "dial_phone_button_onLongClickListener";

	// dial phone textView
	private TextView _mDialPhoneTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.dial_tab_content_activity_layout);

		// init dial phone textView
		// set dial phone textView
		_mDialPhoneTextView = (TextView) findViewById(R.id.dial_phone_textView);

		// set its default text
		_mDialPhoneTextView.setText("");

		// set its text watcher
		_mDialPhoneTextView
				.addTextChangedListener(new DialPhoneTextViewTextWatcher());

		// set dial phone button grid view adapter
		((GridView) findViewById(R.id.dial_phoneBtn_gridView))
				.setAdapter(generateDialPhoneButtonAdapter());

		// init dial function button and set click and long click listener
		// get add new contact dial function button
		ImageButton _addNewContactFunBtn = (ImageButton) findViewById(R.id.dial_newContact_functionBtn);

		// set its image resource
		_addNewContactFunBtn
				.setImageResource(R.drawable.img_dial_newcontact_btn);

		// set its click listener
		_addNewContactFunBtn
				.setOnClickListener(new AddNewContactDialFunBtnOnClickListener());

		// set dial call button click listener
		((ImageButton) findViewById(R.id.dial_call_functionBtn))
				.setOnClickListener(new CallDialFunBtnOnClickListener());

		// get clear dial phone function button
		ImageButton _clearDialPhoneFunBtn = (ImageButton) findViewById(R.id.dial_clearDialPhone_functionBtn);

		// set its image resource
		_clearDialPhoneFunBtn
				.setImageResource(R.drawable.img_dial_cleardialphone_btn);

		// set its click and long click listener
		_clearDialPhoneFunBtn
				.setOnClickListener(new ClearDialPhoneDialFunBtnOnClickListener());
		_clearDialPhoneFunBtn
				.setOnLongClickListener(new ClearDialPhoneDialFunBtnOnLongClickListener());
	}

	@Override
	protected boolean hideNavigationBarWhenOnCreated() {
		return true;
	}

	// generate dial phone button adapter
	private ListAdapter generateDialPhoneButtonAdapter() {
		// dial phone button adapter data key
		final String DIAL_PHONE_BUTTON = "dial_phone_button";

		// define dial phone button gridView image resource content
		final int[] _dialPhoneButtonGridViewImgResourceContentArray = {
				R.drawable.img_dial_1_btn, R.drawable.img_dial_2_btn,
				R.drawable.img_dial_3_btn, R.drawable.img_dial_4_btn,
				R.drawable.img_dial_5_btn, R.drawable.img_dial_6_btn,
				R.drawable.img_dial_7_btn, R.drawable.img_dial_8_btn,
				R.drawable.img_dial_9_btn, R.drawable.img_dial_star_btn,
				R.drawable.img_dial_0_btn, R.drawable.img_dial_pound_btn };

		// set dial phone button data list
		List<Map<String, ?>> _dialPhoneButtonDataList = new ArrayList<Map<String, ?>>();

		for (int i = 0; i < _dialPhoneButtonGridViewImgResourceContentArray.length; i++) {
			// generate data
			Map<String, Object> _dataMap = new HashMap<String, Object>();

			// value map
			Map<String, Object> _valueMap = new HashMap<String, Object>();
			_valueMap.put(DIAL_PHONE_BUTTON_CODE, i);
			_valueMap.put(DIAL_PHONE_BUTTON_IMAGE,
					_dialPhoneButtonGridViewImgResourceContentArray[i]);
			_valueMap.put(DIAL_PHONE_BUTTON_ONCLICKLISTENER,
					new DialPhoneBtnOnClickListener());
			_valueMap.put(DIAL_PHONE_BUTTON_ONLONGCLICKLISTENER,
					new DialPhoneBtnOnLongClickListener());

			// put value
			_dataMap.put(DIAL_PHONE_BUTTON, _valueMap);

			// add data to list
			_dialPhoneButtonDataList.add(_dataMap);
		}

		return new DialPhoneButtonAdapter(this, _dialPhoneButtonDataList,
				R.layout.dial_phone_btn_layout,
				new String[] { DIAL_PHONE_BUTTON },
				new int[] { R.id.dialBtn_imageBtn });
	}

	// inner class
	// dial phone textView text watcher
	class DialPhoneTextViewTextWatcher implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			// measure text
			// define textView text bounds and font size
			Rect _textBounds = new Rect();
			Integer _textFontSize;

			// set its default font size
			_mDialPhoneTextView.setTextSize(DpPixUtils.pix2dp(getResources()
					.getDimension(R.dimen.dialPhone_textView_textMaxFontSize)));

			do {
				// get text bounds
				_mDialPhoneTextView.getPaint().getTextBounds(s.toString(), 0,
						s.toString().length(), _textBounds);

				// get text font size
				_textFontSize = DpPixUtils.pix2dp(_mDialPhoneTextView
						.getTextSize());

				// check bounds
				if (_textBounds.right + /* padding left and right */2 * 16 > _mDialPhoneTextView
						.getRight()) {
					// reset dial phone textView text font size
					_mDialPhoneTextView.setTextSize(_textFontSize - 1);
				} else {
					break;
				}
			} while (_textFontSize > DpPixUtils.pix2dp(getResources()
					.getDimension(R.dimen.dialPhone_textView_textMinFontSize)));

			// get the dial phone ownership
			// get dial phone ownership textView
			TextView _dialPhoneOwnershipTextView = (TextView) findViewById(R.id.dial_phone_ownership_textView);

			// get address book manager reference
			AddressBookManager _addressBookManager = AddressBookManager
					.getInstance();

			// get dial phone has ownership
			@SuppressWarnings("unchecked")
			Long _dialPhoneOwnershipId = _addressBookManager
					.isContactWithPhoneInAddressBook(s.toString(), null,
							(List<AreaAbbreviation>) CommonUtils
									.array2List(new AreaAbbreviation[] {
											AreaAbbreviation.CN,
											AreaAbbreviation.AO }));

			// check dial phone has ownership
			if (null != _dialPhoneOwnershipId) {
				// set dial phone ownership textView text and show it
				_dialPhoneOwnershipTextView.setText(_addressBookManager
						.getContactByAggregatedId(_dialPhoneOwnershipId)
						.getDisplayName());

				_dialPhoneOwnershipTextView.setVisibility(View.VISIBLE);
			} else {
				// hide dial phone ownership textView
				_dialPhoneOwnershipTextView.setVisibility(View.GONE);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

	}

	// dial phone button on click listener
	class DialPhoneBtnOnClickListener implements OnClickListener {

		// define dial phone button value data
		private final String[] _dialPhoneButtonValueData = new String[] { "1",
				"2", "3", "4", "5", "6", "7", "8", "9", "*", "0", "#" };

		@Override
		public void onClick(View v) {
			// define dial phone string builder
			StringBuilder _dialPhoneStringBuilder = new StringBuilder(
					_mDialPhoneTextView.getText());

			// dial phone
			_dialPhoneStringBuilder
					.append(_dialPhoneButtonValueData[(Integer) v.getTag()]);

			// reset dial phone textView text
			_mDialPhoneTextView.setText(_dialPhoneStringBuilder);

			// play dial phone button dtmf sound
			ToneGeneratorUtils.getInstance()
					.playDTMFSound((Integer) v.getTag());
		}

	}

	// dial phone button on long click listener
	class DialPhoneBtnOnLongClickListener implements OnLongClickListener {

		@Override
		public boolean onLongClick(View v) {
			boolean _ret = false;

			// check +
			if (10 == (Integer) v.getTag()) {
				// define dial phone string builder
				StringBuilder _dialPhoneStringBuilder = new StringBuilder(
						_mDialPhoneTextView.getText());

				// +
				_dialPhoneStringBuilder.append('+');

				// reset dial phone textView text
				_mDialPhoneTextView.setText(_dialPhoneStringBuilder);

				// play dial phone button dtmf sound
				ToneGeneratorUtils.getInstance().playDTMFSound(
						(Integer) v.getTag());

				_ret = true;
			}

			return _ret;
		}

	}

	// add new contact dial function button on click listener
	class AddNewContactDialFunBtnOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// get dial phone text
			String _dialPhoneString = _mDialPhoneTextView.getText().toString();

			// check dial phone string
			if (null != _dialPhoneString
					&& !"".equalsIgnoreCase(_dialPhoneString)) {
				Log.d(LOG_TAG, "add phone = " + _dialPhoneString
						+ " to new or existed contact");

				// define contact insert intent
				Intent _contactInsertIntent = new Intent(Intent.ACTION_INSERT);

				// put type and extra
				_contactInsertIntent.setType(Contacts.CONTENT_TYPE);
				_contactInsertIntent.putExtra(Phone.NUMBER, _dialPhoneString);
				_contactInsertIntent.putExtra(Phone.TYPE, Phone.TYPE_MOBILE);

				// check contact insert intent and start the activity
				if (CommonUtils.isIntentAvailable(_contactInsertIntent)) {
					startActivity(_contactInsertIntent);
				}
			}
		}

	}

	// call dial function button on click listener
	class CallDialFunBtnOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// get dial phone text
			String _dialPhoneString = _mDialPhoneTextView.getText().toString();

			// check dial phone string
			if (null != _dialPhoneString
					&& !"".equalsIgnoreCase(_dialPhoneString)) {
				// make voice call
				// makeVoiceCall(_dialPhoneString);

				// define contact phone dial mode select popup window
				ContactPhoneDialModeSelectpopupWindow _contactPhoneDialModeSelectPopupWindow = new ContactPhoneDialModeSelectpopupWindow(
						R.layout.contact_phone_dialmode_select_popupwindow_layout,
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

				// set callee contact info
				// generate callee display name and phones
				// get dial phone ownership textView
				TextView _dialPhoneOwnershipTextView = (TextView) findViewById(R.id.dial_phone_ownership_textView);

				String _calleeName = View.VISIBLE == _dialPhoneOwnershipTextView
						.getVisibility() ? _dialPhoneOwnershipTextView
						.getText().toString() : getResources().getString(
						R.string.dial_phone_callee_unknown);

				@SuppressWarnings("unchecked")
				List<String> _calleePhones = (List<String>) CommonUtils
						.array2List(new String[] { _dialPhoneString });

				// set callee contact info
				_contactPhoneDialModeSelectPopupWindow.setCalleeContactInfo(
						_calleeName, _calleePhones);

				// show contact phone dial mode select pupupWindow
				_contactPhoneDialModeSelectPopupWindow.showAtLocation(v,
						Gravity.CENTER, 0, 0);
			} else {
				Log.e(LOG_TAG, "The dial phone number is null");
			}
		}

	}

	// clear dial phone dial function button on click listener
	class ClearDialPhoneDialFunBtnOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// get dial phone text
			String _dialPhoneString = _mDialPhoneTextView.getText().toString();

			// check dial phone string
			if (null != _dialPhoneString && _dialPhoneString.length() > 0) {
				// reset dial phone textView text
				_mDialPhoneTextView.setText(_dialPhoneString.substring(0,
						_dialPhoneString.length() - 1));
			}
		}

	}

	// clear dial phone dial function button on long click listener
	class ClearDialPhoneDialFunBtnOnLongClickListener implements
			OnLongClickListener {

		@Override
		public boolean onLongClick(View view) {
			// get dial phone text
			String _dialPhoneString = _mDialPhoneTextView.getText().toString();

			// check dial phone string
			if (null != _dialPhoneString && _dialPhoneString.length() > 0) {
				// clear dial phone textView text
				_mDialPhoneTextView.setText("");
			}

			return true;
		}

	}

}
