package com.richitec.chinesetelephone.tab7tabcontent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Intents;
import android.provider.ContactsContract.RawContacts;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.call.OutgoingCallGenerator;
import com.richitec.commontoolkit.activityextension.NavigationActivity;
import com.richitec.commontoolkit.addressbook.AddressBookManager;
import com.richitec.commontoolkit.customadapter.CTListAdapter;
import com.richitec.commontoolkit.customcomponent.CTPopupWindow;
import com.richitec.commontoolkit.utils.CommonUtils;
import com.richitec.commontoolkit.utils.DisplayScreenUtils;
import com.richitec.commontoolkit.utils.ToneGeneratorUtils;
import com.richitec.internationalcode.AreaAbbreviation;

public class DialTabContentActivity extends NavigationActivity {

	private static final String LOG_TAG = DialTabContentActivity.class
			.getCanonicalName();

	// dial phone textView and previous dial phone
	private TextView _mDialPhoneTextView;
	private StringBuffer _mPreviousDialPhone;

	// contact pick activity request code
	private static final int PICK_CONTACT = 0;

	// insert phone to contact mode select popup window
	private InsertPhone2ContactModeSelectPopupWindow _mInsertPhone2ContactModeSelectPopupWindow;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.dial_tab_content_activity_layout);

		// init previous dial phone
		_mPreviousDialPhone = new StringBuffer();

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
		// get add new or update contact dial function button
		ImageButton _addNew6updateContactFunBtn = (ImageButton) findViewById(R.id.dial_new6updateContact_functionBtn);

		// set its image resource
		_addNew6updateContactFunBtn
				.setImageResource(R.drawable.img_dial_newcontact_btn);

		// set its click listener
		_addNew6updateContactFunBtn
				.setOnClickListener(new AddNew6updateContactDialFunBtnOnClickListener());

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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// get user input phone number
		String _userInputPhone = _mDialPhoneTextView.getText().toString();

		// check request code
		switch (requestCode) {
		case PICK_CONTACT:
			// check result code
			switch (resultCode) {
			case RESULT_CANCELED:
				Log.d(LOG_TAG, "Pick contact canceled");
				break;

			case RESULT_OK:
			default:
				// check data
				if (null != data) {
					// get pick contact data
					Uri _pickContactData = data.getData();

					// get pick contact id
					String _pickContactId = _pickContactData
							.getLastPathSegment();

					// check pick contact id
					if (null != _pickContactId) {
						// define contact edit intent
						Intent _contactEditIntent = new Intent(
								Intent.ACTION_EDIT, Uri.withAppendedPath(
										Contacts.CONTENT_URI, _pickContactId));

						// put extra
						_contactEditIntent.putExtra(Intents.Insert.PHONE,
								_userInputPhone);
						_contactEditIntent.putExtra(Intents.Insert.PHONE_TYPE,
								Phone.TYPE_MOBILE);

						// check contact exit intent and start the activity
						if (CommonUtils.isIntentAvailable(_contactEditIntent)) {
							startActivity(_contactEditIntent);
						}
					} else {
						Log.e(LOG_TAG, "Picked contact id is null");
					}
				} else {
					Log.e(LOG_TAG, "On activity result intent data = " + data);
				}
				break;
			}
			break;

		default:
			// nothing to do
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
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
			_valueMap.put(DialPhoneButtonAdapter.DIAL_PHONE_BUTTON_CODE, i);
			_valueMap.put(DialPhoneButtonAdapter.DIAL_PHONE_BUTTON_IMAGE,
					_dialPhoneButtonGridViewImgResourceContentArray[i]);
			_valueMap.put(
					DialPhoneButtonAdapter.DIAL_PHONE_BUTTON_ONCLICKLISTENER,
					new DialPhoneBtnOnClickListener());
			_valueMap
					.put(DialPhoneButtonAdapter.DIAL_PHONE_BUTTON_ONLONGCLICKLISTENER,
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

	// insert phone to new contact
	private void insertPhone2NewContact(String insertPhone) {
		Log.d(LOG_TAG, "Generate new contact and add phone = " + insertPhone
				+ " to it");

		// define contact insert intent
		Intent _contactInsertIntent = new Intent(Intent.ACTION_INSERT);

		// put type and extra
		_contactInsertIntent.setType(Contacts.CONTENT_TYPE);
		_contactInsertIntent.setType(RawContacts.CONTENT_TYPE);
		_contactInsertIntent.putExtra(Intents.Insert.PHONE, insertPhone);
		_contactInsertIntent.putExtra(Intents.Insert.PHONE_TYPE,
				Phone.TYPE_MOBILE);

		// check contact insert intent and start the activity
		if (CommonUtils.isIntentAvailable(_contactInsertIntent)) {
			startActivity(_contactInsertIntent);
		}
	}

	// inner class
	// dial phone button adapter
	class DialPhoneButtonAdapter extends CTListAdapter {

		private static final String LOG_TAG = "DialPhoneButtonAdapter";

		// dial phone button gridView keys
		private static final String DIAL_PHONE_BUTTON_CODE = "dial_phone_button_code";
		private static final String DIAL_PHONE_BUTTON_IMAGE = "dial_phone_button_image";
		private static final String DIAL_PHONE_BUTTON_ONCLICKLISTENER = "dial_phone_button_onClickListener";
		private static final String DIAL_PHONE_BUTTON_ONLONGCLICKLISTENER = "dial_phone_button_onLongClickListener";

		public DialPhoneButtonAdapter(Context context,
				List<Map<String, ?>> data, int itemsLayoutResId,
				String[] dataKeys, int[] itemsComponentResIds) {
			super(context, data, itemsLayoutResId, dataKeys,
					itemsComponentResIds);
		}

		@Override
		protected void bindView(View view, Map<String, ?> dataMap,
				String dataKey) {
			// get item data object
			Object _itemData = dataMap.get(dataKey);

			// check view type
			// image button
			if (view instanceof ImageButton) {
				try {
					// define item data map and convert item data to map
					@SuppressWarnings("unchecked")
					Map<String, Object> _itemDataMap = (Map<String, Object>) _itemData;

					// set image button attributes
					((ImageButton) view).setTag(_itemDataMap
							.get(DIAL_PHONE_BUTTON_CODE));
					((ImageButton) view)
							.setImageResource((Integer) _itemDataMap
									.get(DIAL_PHONE_BUTTON_IMAGE));
					((ImageButton) view)
							.setOnClickListener((OnClickListener) _itemDataMap
									.get(DIAL_PHONE_BUTTON_ONCLICKLISTENER));
					((ImageButton) view)
							.setOnLongClickListener((OnLongClickListener) _itemDataMap
									.get(DIAL_PHONE_BUTTON_ONLONGCLICKLISTENER));
				} catch (Exception e) {
					e.printStackTrace();

					Log.e(LOG_TAG,
							"Convert item data to map error, item data = "
									+ _itemData);
				}
			}
		}

	}

	// dial phone textView text watcher
	class DialPhoneTextViewTextWatcher implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			// update previous dial phone
			_mPreviousDialPhone.setLength(0);
			_mPreviousDialPhone.append(s);

			// measure text
			// define textView text bounds and font size
			Rect _textBounds = new Rect();
			Integer _textFontSize;

			// set its default font size
			_mDialPhoneTextView.setTextSize(DisplayScreenUtils
					.pix2sp(getResources().getDimension(
							R.dimen.dialPhone_textView_textMaxFontSize)));

			do {
				// get text bounds
				_mDialPhoneTextView.getPaint().getTextBounds(s.toString(), 0,
						s.toString().length(), _textBounds);

				// get text font size
				_textFontSize = DisplayScreenUtils.pix2sp(_mDialPhoneTextView
						.getTextSize());

				// check bounds
				if (_textBounds.right + /* padding left and right */2 * 16 > _mDialPhoneTextView
						.getRight()) {
					// reset dial phone textView text font size
					_mDialPhoneTextView.setTextSize(_textFontSize - 1);
				} else {
					break;
				}
			} while (_textFontSize > DisplayScreenUtils.pix2sp(getResources()
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

	// add new or update contact dial function button on click listener
	class AddNew6updateContactDialFunBtnOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			// get insert phone number
			String _insertPhone = _mDialPhoneTextView.getText().toString();

			// check insert phone number
			if (null != _insertPhone && !"".equalsIgnoreCase(_insertPhone)) {
				// check address book contacts size
				if (0 == AddressBookManager.getInstance()
						.getAllContactsInfoArray().size()) {
					// no contact in address book, generate new contact and add
					// phone to it
					insertPhone2NewContact(_insertPhone);
				} else {
					// define insert phone to contact mode select popup window
					_mInsertPhone2ContactModeSelectPopupWindow = new InsertPhone2ContactModeSelectPopupWindow(
							R.layout.insert_phone2contact_mode_select_popupwindow_layout,
							LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

					// set phone number for insert to contact
					_mInsertPhone2ContactModeSelectPopupWindow
							.setPhone4Insert2Contact(_insertPhone);

					// show insert phone to contact mode select popup window
					// with animation
					_mInsertPhone2ContactModeSelectPopupWindow
							.showAtLocationWithAnimation(v, Gravity.CENTER, 0,
									0);
				}
			} else {
				// add new contact
				ContactListTabContentActivity
						.addNewContact(DialTabContentActivity.this);
			}
		}
	}

	// insert phone to contact mode select popup window
	class InsertPhone2ContactModeSelectPopupWindow extends CTPopupWindow {

		// insert phone number
		private String _mInsertPhone;

		public InsertPhone2ContactModeSelectPopupWindow(int resource,
				int width, int height, boolean focusable,
				boolean isBindDefListener) {
			super(resource, width, height, focusable, isBindDefListener);
		}

		public InsertPhone2ContactModeSelectPopupWindow(int resource,
				int width, int height) {
			super(resource, width, height);
		}

		@Override
		protected void bindPopupWindowComponentsListener() {
			// bind insert phone to contact mode select insert to new contact,
			// existed contact and cancel button click listener
			((Button) getContentView().findViewById(
					R.id.insertPhone2NewContact_button))
					.setOnClickListener(new InsertPhone2ContactModeSelectInsert2NewContactBtnOnClickListener());

			((Button) getContentView().findViewById(
					R.id.insertPhone2ExistedContact_button))
					.setOnClickListener(new InsertPhone2ContactModeSelectInsert2ExistedContactBtnOnClickListener());

			((Button) getContentView().findViewById(
					R.id.insertPhone2Contact_cancelBtn))
					.setOnClickListener(new InsertPhone2ContactModeSelectCancelBtnOnClickListener());
		}

		@Override
		protected void resetPopupWindow() {
			// nothing to do
		}

		// set phone number for inserting to contact
		public void setPhone4Insert2Contact(String insertPhone) {
			// update phone number for inserting
			_mInsertPhone = insertPhone;

			// set insert phone to contact mode select title textView text
			((TextView) getContentView().findViewById(
					R.id.insertPhone2ContactMode_select_titleTextView))
					.setText(getResources()
							.getString(
									R.string.insertPhone2Contact_modeSelectPopupWindow_titleTextView_text)
							.replace("***", insertPhone));
		}

		// inner class
		// insert phone to contact mode select insert to new contact button on
		// click listener
		class InsertPhone2ContactModeSelectInsert2NewContactBtnOnClickListener
				implements OnClickListener {

			@Override
			public void onClick(View v) {
				// dismiss insert phone to contact mode select popup window
				dismiss();

				// insert phone to new contact
				insertPhone2NewContact(_mInsertPhone);
			}

		}

		// insert phone to contact mode select insert to existed contact button
		// on click listener
		class InsertPhone2ContactModeSelectInsert2ExistedContactBtnOnClickListener
				implements OnClickListener {

			@Override
			public void onClick(View v) {
				// dismiss insert phone to contact mode select popup window
				dismiss();

				Log.d(LOG_TAG, "Add phone = " + _mInsertPhone
						+ " to existed contact");

				// define contact pick intent
				Intent _contactPickIntent = new Intent(Intent.ACTION_PICK);

				// put type
				_contactPickIntent.setType(Contacts.CONTENT_TYPE);

				// check contact pick intent and start the activity for result
				if (CommonUtils.isIntentAvailable(_contactPickIntent)) {
					startActivityForResult(_contactPickIntent, PICK_CONTACT);
				}
			}

		}

		// insert phone to contact mode select cancel button on click listener
		class InsertPhone2ContactModeSelectCancelBtnOnClickListener implements
				OnClickListener {

			@Override
			public void onClick(View v) {
				// dismiss insert phone to contact mode select popup window with
				// animation
				dismissWithAnimation();
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

				// generate an new outgoing call
				new OutgoingCallGenerator(v).setDialPhoneTextView4Dial(
						_mDialPhoneTextView, _mPreviousDialPhone)
						.preGenNewOutgoingCall(_calleeName, _calleePhones);
			} else if (null != _mPreviousDialPhone
					&& !"".equalsIgnoreCase(_mPreviousDialPhone.toString())) {
				// set dial phone textView text using previous dial phone
				_mDialPhoneTextView.setText(_mPreviousDialPhone);
			} else {
				Log.e(LOG_TAG,
						"Both the dial phone number and previous dial phone are null");
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
