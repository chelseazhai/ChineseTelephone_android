package com.richitec.chinesetelephone.tab7tabcontent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.richitec.chinesetelephone.R;
import com.richitec.commontoolkit.activityextension.AppLaunchActivity;
import com.richitec.commontoolkit.activityextension.NavigationActivity;
import com.richitec.commontoolkit.addressbook.AddressBookManager;
import com.richitec.commontoolkit.addressbook.ContactBean;
import com.richitec.commontoolkit.customadapter.CommonListAdapter;
import com.richitec.commontoolkit.customcomponent.CommonPopupWindow;
import com.richitec.commontoolkit.customcomponent.ListViewQuickAlphabetBar;
import com.richitec.commontoolkit.customcomponent.ListViewQuickAlphabetBar.OnTouchListener;
import com.richitec.commontoolkit.utils.StringUtils;
import com.rictitec.chinesetelephone.utils.SipUtils.SipCallMode;

public class ContactListTabContentActivity extends NavigationActivity {

	private static final String LOG_TAG = "ContactListTabContentActivity";

	// address book contacts list view
	private ListView _mABContactsListView;

	// all address book name phonetic sorted contacts detail info list
	private static List<ContactBean> _mAllNamePhoneticSortedContactsInfoArray;

	// present contacts in address book detail info list
	private List<ContactBean> _mPresentContactsInABInfoArray;

	// contact search status
	private ContactSearchStatus _mContactSearchStatus = ContactSearchStatus.NONESEARCH;

	// define contact phone dial mode select popup window
	private final ContactPhoneDialModeSelectPopupWindows _mContactPhoneDialModeSelectPopupWindow = new ContactPhoneDialModeSelectPopupWindows(
			R.layout.contact_phone_dialmode_select_popupwindow_layout,
			LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

	// define contact phone numbers select popup window
	private final ContactPhoneNumbersSelectPopupWindow _mContactPhoneNumbersSelectPopupWindow = new ContactPhoneNumbersSelectPopupWindow(
			R.layout.contact_phonenumbers_select_popupwindow_layout,
			LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

	// init all name phonetic sorted contacts info array
	public static void initNamePhoneticSortedContactsInfoArray() {
		_mAllNamePhoneticSortedContactsInfoArray = AddressBookManager
				.getInstance().getAllNamePhoneticSortedContactsInfoArray();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.contact_list_tab_content_activity_layout);

		// set title
		setTitle(R.string.contact_list_tab7nav_title);

		// init present contacts in address book detail info array
		_mPresentContactsInABInfoArray = _mAllNamePhoneticSortedContactsInfoArray;

		// init contacts in address book list view
		_mABContactsListView = (ListView) findViewById(R.id.contactInAB_listView);

		// set contacts in address book listView adapter
		_mABContactsListView
				.setAdapter(generateInABContactAdapter(_mPresentContactsInABInfoArray));
		// init address book contacts listView quick alphabet bar and add on
		// touch listener
		new ListViewQuickAlphabetBar(_mABContactsListView)
				.setOnTouchListener(new ContactsInABListViewQuickAlphabetBarOnTouchListener());

		// set contacts in address book listView on item click listener
		_mABContactsListView
				.setOnItemClickListener(new ContactsInABListViewOnItemClickListener());

		// bind contact search editText text watcher
		((EditText) findViewById(R.id.contact_search_editText))
				.addTextChangedListener(new ContactSearchEditTextTextWatcher());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(
				R.menu.contact_list_tab_content_activity_layout, menu);
		return true;
	}

	// generate in address book contact adapter
	private ListAdapter generateInABContactAdapter(
			List<ContactBean> presentContactsInAB) {
		// in address book contacts adapter data keys
		final String PRESENT_CONTACT_PHOTO = "present_contact_photo";
		final String PRESENT_CONTACT_NAME = "present_contact_name";
		final String PRESENT_CONTACT_PHONES = "present_contact_phones";

		// set address book contacts list view present data list
		List<Map<String, ?>> _addressBookContactsPresentDataList = new ArrayList<Map<String, ?>>();

		for (ContactBean _contact : presentContactsInAB) {
			// generate data
			Map<String, Object> _dataMap = new HashMap<String, Object>();

			// get contact name and phone matching indexes
			@SuppressWarnings("unchecked")
			SparseArray<Integer> _nameMatchingIndexes = (SparseArray<Integer>) _contact
					.getExtension().get(
							AddressBookManager.NAME_MATCHING_INDEXES);
			@SuppressWarnings("unchecked")
			List<List<Integer>> _phoneMatchingIndexes = (List<List<Integer>>) _contact
					.getExtension().get(
							AddressBookManager.PHONENUMBER_MATCHING_INDEXES);

			// set data
			// define contact photo bitmap
			Bitmap _contactPhotoBitmap = ((BitmapDrawable) getResources()
					.getDrawable(R.drawable.img_default_avatar)).getBitmap();

			// check contact photo data
			if (null != _contact.getPhoto()) {
				try {
					// get photo data stream
					InputStream _photoDataStream = new ByteArrayInputStream(
							_contact.getPhoto());

					// check photo data stream
					if (null != _photoDataStream) {
						_contactPhotoBitmap = BitmapFactory
								.decodeStream(_photoDataStream);

						// close photo data stream
						_photoDataStream.close();
					}
				} catch (IOException e) {
					e.printStackTrace();

					Log.e(LOG_TAG,
							"Get contact photo data stream error, error message = "
									+ e.getMessage());
				}
			}

			// set photo
			_dataMap.put(PRESENT_CONTACT_PHOTO, _contactPhotoBitmap);

			// check contact search status
			if (ContactSearchStatus.SEARCHBYNAME == _mContactSearchStatus
					|| ContactSearchStatus.SEARCHBYCHINESENAME == _mContactSearchStatus) {
				// get display name
				SpannableString _displayName = new SpannableString(
						_contact.getDisplayName());

				// set attributed
				for (int i = 0; i < _nameMatchingIndexes.size(); i++) {
					// get key and value
					Integer _nameCharMatchedPos = getRealPositionInContactDisplayName(
							_contact.getDisplayName(),
							_nameMatchingIndexes.keyAt(i));
					Integer _nameCharMatchedLength = _nameMatchingIndexes
							.get(_nameMatchingIndexes.keyAt(i));

					_displayName
							.setSpan(
									new ForegroundColorSpan(Color.BLUE),
									_nameCharMatchedPos,
									AddressBookManager.NAME_CHARACTER_FUZZYMATCHED_LENGTH == _nameCharMatchedLength ? _nameCharMatchedPos + 1
											: _nameCharMatchedPos
													+ _nameCharMatchedLength,
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}

				_dataMap.put(PRESENT_CONTACT_NAME, _displayName);
			} else {
				_dataMap.put(PRESENT_CONTACT_NAME, _contact.getDisplayName());
			}
			if (ContactSearchStatus.SEARCHBYPHONE == _mContactSearchStatus) {
				// get format phone number string
				SpannableString _formatPhoneNumberString = new SpannableString(
						_contact.getFormatPhoneNumbers());

				// get format phone number string separator "\n" positions
				List<Integer> _sepPositions = StringUtils.subStringPositions(
						_contact.getFormatPhoneNumbers(), "\n");

				// set attributed
				for (int i = 0; i < _phoneMatchingIndexes.size(); i++) {
					// check the phone matched
					if (0 != _phoneMatchingIndexes.get(i).size()) {
						// get begin and end position
						int _beginPos = _phoneMatchingIndexes.get(i).get(0);
						int _endPos = _phoneMatchingIndexes.get(i).get(
								_phoneMatchingIndexes.get(i).size() - 1) + 1;

						// check matched phone
						if (1 <= i) {
							_beginPos += _sepPositions.get(i - 1) + 1;
							_endPos += _sepPositions.get(i - 1) + 1;
						}

						_formatPhoneNumberString.setSpan(
								new ForegroundColorSpan(Color.BLUE), _beginPos,
								_endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
				}

				_dataMap.put(PRESENT_CONTACT_PHONES, _formatPhoneNumberString);
			} else {
				_dataMap.put(PRESENT_CONTACT_PHONES,
						_contact.getFormatPhoneNumbers());
			}

			// put alphabet index
			_dataMap.put(CommonListAdapter.ALPHABET_INDEX,
					_contact.getNamePhoneticsString());

			// add data to list
			_addressBookContactsPresentDataList.add(_dataMap);
		}

		// get address book contacts listView adapter
		AddressBookContactAdapter _addressBookContactsListViewAdapter = (AddressBookContactAdapter) ((ListView) findViewById(R.id.contactInAB_listView))
				.getAdapter();

		return null == _addressBookContactsListViewAdapter ? new AddressBookContactAdapter(
				this, _addressBookContactsPresentDataList,
				R.layout.addressbook_contact_layout, new String[] {
						PRESENT_CONTACT_PHOTO, PRESENT_CONTACT_NAME,
						PRESENT_CONTACT_PHONES }, new int[] {
						R.id.addressBook_contact_avatar_imageView,
						R.id.adressBook_contact_displayName_textView,
						R.id.addressBook_contact_phoneNumber_textView })
				: _addressBookContactsListViewAdapter
						.setData(_addressBookContactsPresentDataList);
	}

	// get real position in contact display name with original position
	private Integer getRealPositionInContactDisplayName(String displayName,
			Integer origPosition) {
		int _realPos = 0;

		int _tmpPos = 0;
		boolean _prefixHasChar = false;

		for (int i = 0; i < displayName.length(); i++) {
			if (String.valueOf(displayName.charAt(i))
					.matches("[\u4e00-\u9fa5]")) {
				if (_prefixHasChar) {
					_prefixHasChar = false;

					_tmpPos += 1;
				}

				if (_tmpPos == origPosition) {
					_realPos = i;

					break;
				}

				_tmpPos += 1;
			} else if (' ' == displayName.charAt(i)) {
				if (_prefixHasChar) {
					_prefixHasChar = false;

					_tmpPos += 1;
				}
			} else {
				if (_tmpPos == origPosition) {
					_realPos = i;

					break;
				}

				_prefixHasChar = true;
			}
		}

		return _realPos;
	}

	// make sip voice call
	private void makeSipVoiceCall(String calleeDisplayName,
			String calleePhoneNumber, SipCallMode callMode) {
		//
		Log.d(LOG_TAG, "makeSipVoiceCall - callee display name = "
				+ calleeDisplayName + " , phone number = " + calleePhoneNumber
				+ " and call mode = " + callMode);
	}

	// inner class
	// contact search status
	enum ContactSearchStatus {
		NONESEARCH, SEARCHBYNAME, SEARCHBYCHINESENAME, SEARCHBYPHONE
	}

	// contacts in address book listView quick alphabet bar on touch listener
	class ContactsInABListViewQuickAlphabetBarOnTouchListener extends
			OnTouchListener {

		@Override
		protected boolean onTouch(RelativeLayout alphabetRelativeLayout,
				ListView dependentListView, MotionEvent event,
				Character alphabeticalCharacter) {
			// get scroll position
			if (dependentListView.getAdapter() instanceof CommonListAdapter) {
				// get dependent listView adapter
				CommonListAdapter _commonListAdapter = (CommonListAdapter) dependentListView
						.getAdapter();

				for (int i = 0; i < _commonListAdapter.getCount(); i++) {
					// get alphabet index
					@SuppressWarnings("unchecked")
					String _alphabetIndex = (String) ((Map<String, ?>) _commonListAdapter
							.getItem(i)).get(CommonListAdapter.ALPHABET_INDEX);

					// check alphabet index
					if (null == _alphabetIndex
							|| _alphabetIndex.startsWith(String.valueOf(
									alphabeticalCharacter).toLowerCase())) {
						// set selection
						dependentListView.setSelection(i);

						break;
					}
				}
			} else {
				Log.e(LOG_TAG, "Dependent listView adapter = "
						+ dependentListView.getAdapter() + " and class name = "
						+ dependentListView.getAdapter().getClass().getName());
			}

			return true;
		}

	}

	// contacts in address book listView on item click listener
	class ContactsInABListViewOnItemClickListener implements
			OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// get the click item view data: contact object
			ContactBean _clickItemViewData = _mPresentContactsInABInfoArray
					.get((int) id);

			// check the click item view data
			if (null == _clickItemViewData.getPhoneNumbers()) {
				// show contact has no phone number alert dialog
				new AlertDialog.Builder(ContactListTabContentActivity.this)
						.setTitle(R.string.contact_hasNoPhone_alertDialog_title)
						.setMessage(_clickItemViewData.getDisplayName())
						.setPositiveButton(
								R.string.contact_hasNoPhone_alertDialog_reselectBtn_title,
								null).show();
			} else {
				// switch (_clickItemViewData.getPhoneNumbers().size()) {
				// case 1:
				// Log.d(LOG_TAG, "markContactSelected - selected phone = "
				// + _clickItemViewData.getPhoneNumbers().get(0)
				// + " and selected position = " + id);
				//
				// break;
				//
				// default:
				// // set contact phone numbers for selecting
				// _mContactPhoneNumbersSelectPopupWindow
				// .setContactPhones4Selecting(
				// _clickItemViewData.getDisplayName(),
				// _clickItemViewData.getPhoneNumbers(),
				// position);
				//
				// // show contact phone numbers select popup window
				// _mContactPhoneNumbersSelectPopupWindow.showAtLocation(
				// parent, Gravity.CENTER, 0, 0);
				//
				// break;
				// }

				// set callee contact info
				_mContactPhoneDialModeSelectPopupWindow.setCalleeContactInfo(
						_clickItemViewData, position);

				// show contact phone dial mode select pupupWindow
				_mContactPhoneDialModeSelectPopupWindow.showAtLocation(parent,
						Gravity.CENTER, 0, 0);
			}
		}

	}

	// contact search editText text watcher
	class ContactSearchEditTextTextWatcher implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			// set contact search status
			if (null == s || 0 == s.length()) {
				_mContactSearchStatus = ContactSearchStatus.NONESEARCH;
			} else if (s.toString().matches("^[0-9]*$")) {
				_mContactSearchStatus = ContactSearchStatus.SEARCHBYPHONE;
			} else if (s.toString().matches(".*[\u4e00-\u9fa5].*")) {
				_mContactSearchStatus = ContactSearchStatus.SEARCHBYCHINESENAME;
			} else {
				_mContactSearchStatus = ContactSearchStatus.SEARCHBYNAME;
			}

			// update present contacts in address book detail info list
			switch (_mContactSearchStatus) {
			case SEARCHBYNAME:
				_mPresentContactsInABInfoArray = AddressBookManager
						.getInstance().getContactsByName(s.toString());
				break;

			case SEARCHBYCHINESENAME:
				_mPresentContactsInABInfoArray = AddressBookManager
						.getInstance().getContactsByChineseName(s.toString());
				break;

			case SEARCHBYPHONE:
				_mPresentContactsInABInfoArray = AddressBookManager
						.getInstance().getContactsByPhone(s.toString());
				break;

			case NONESEARCH:
			default:
				_mPresentContactsInABInfoArray = _mAllNamePhoneticSortedContactsInfoArray;
				break;
			}

			// update contacts in address book listView adapter
			_mABContactsListView
					.setAdapter(generateInABContactAdapter(_mPresentContactsInABInfoArray));
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

	}

	// contact phone dial mode select popup window
	class ContactPhoneDialModeSelectPopupWindows extends CommonPopupWindow {

		// select contact info
		private ContactBean _mSelectContact;

		public ContactPhoneDialModeSelectPopupWindows(int resource, int width,
				int height, boolean focusable, boolean isBindDefListener) {
			super(resource, width, height, focusable, isBindDefListener);
		}

		public ContactPhoneDialModeSelectPopupWindows(int resource, int width,
				int height) {
			super(resource, width, height);
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

		// set callee contact info
		public void setCalleeContactInfo(ContactBean contact, int position) {
			// update select contact
			_mSelectContact = contact;

			// set contact phone dial mode select title textView text
			((TextView) getContentView().findViewById(
					R.id.contactPhone_dialMode_select_titleTextView))
					.setText(AppLaunchActivity
							.getAppContext()
							.getResources()
							.getString(
									R.string.contactPhone_dialMode_selectPopupWindow_titleTextView_text)
							.replace("***", contact.getDisplayName()));

			// update contact phone dial mode select direct dial and callback
			// button title
			// define tip text of contact phone for selecting
			String _contactPhone4selecting;

			// check contact phone number count and update contact phone for
			// selecting
			if (1 == contact.getPhoneNumbers().size()) {
				_contactPhone4selecting = "("
						+ contact.getPhoneNumbers().get(0) + ")";
			} else {
				_contactPhone4selecting = "("
						+ contact.getPhoneNumbers().size()
						+ " "
						+ getResources()
								.getString(
										R.string.contactPhone_dialMode_selectPopupWindow_contactPhones_4selecting)
						+ ")";
			}

			// reset direct dial and callback button text
			((Button) getContentView().findViewById(
					R.id.contactPhone_dialMode_select_directdialBtn))
					.setText(getResources()
							.getString(
									R.string.contactPhone_dialMode_selectPopupWindow_directdialBtn_title)
							+ _contactPhone4selecting);
			((Button) getContentView().findViewById(
					R.id.contactPhone_dialMode_select_callbackBtn))
					.setText(getResources()
							.getString(
									R.string.contactPhone_dialMode_selectPopupWindow_callbackBtn_title)
							+ _contactPhone4selecting);
		}

		// check contact for dial mode select
		private void checkContact4DialModeSelect(ContactBean contact,
				SipCallMode dialMode) {
			// dismiss contact phone dial mode select popup window
			dismiss();

			if (1 == contact.getPhoneNumbers().size()) {
				// make sip voice call
				makeSipVoiceCall(contact.getDisplayName(), contact
						.getPhoneNumbers().get(0), dialMode);
			} else {
				// set contact phone numbers for selecting
				_mContactPhoneNumbersSelectPopupWindow
						.setContactPhones4Selecting(
								_mSelectContact.getDisplayName(),
								_mSelectContact.getPhoneNumbers(), dialMode);

				// show contact phone numbers select popup window
				_mContactPhoneNumbersSelectPopupWindow.showAtLocation(
						_mABContactsListView, Gravity.CENTER, 0, 0);
			}
		}

		// inner class
		// contact phone dial mode select direct dial button on click listener
		class ContactPhoneDialModeSelectDirectDialBtnOnClickListener implements
				OnClickListener {

			@Override
			public void onClick(View v) {
				// check contact for dial mode select
				checkContact4DialModeSelect(_mSelectContact,
						SipCallMode.DIRECT_CALL);
			}

		}

		// contact phone dial mode select callback button on click listener
		class ContactPhoneDialModeSelectCallbackBtnOnClickListener implements
				OnClickListener {

			@Override
			public void onClick(View v) {
				// check contact for dial mode select
				checkContact4DialModeSelect(_mSelectContact,
						SipCallMode.CALLBACK);
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

	// contact phone numbers select popup window
	class ContactPhoneNumbersSelectPopupWindow extends CommonPopupWindow {

		// contact display name
		private String _mContactDisplayName;

		// dial contact phone mode
		private SipCallMode _mDialContactPhoneMode;

		public ContactPhoneNumbersSelectPopupWindow(int resource, int width,
				int height, boolean focusable, boolean isBindDefListener) {
			super(resource, width, height, focusable, isBindDefListener);
		}

		public ContactPhoneNumbersSelectPopupWindow(int resource, int width,
				int height) {
			super(resource, width, height);
		}

		@Override
		protected void bindPopupWindowComponentsListener() {

			// get contact phones select phone button parent linearLayout
			LinearLayout _phoneBtnParentLinearLayout = (LinearLayout) getContentView()
					.findViewById(
							R.id.contactPhones_select_phoneBtn_linearLayout);

			// bind contact phone select phone button click listener
			for (int i = 0; i < _phoneBtnParentLinearLayout.getChildCount(); i++) {
				((Button) _phoneBtnParentLinearLayout.getChildAt(i))
						.setOnClickListener(new ContactPhoneSelectPhoneBtnOnClickListener());
			}

			// bind contact phone select phone listView item click listener
			((ListView) getContentView().findViewById(
					R.id.contactPhones_select_phonesListView))
					.setOnItemClickListener(new ContactPhoneSelectPhoneListViewOnItemClickListener());

			// bind contact phone select cancel button click listener
			((Button) getContentView().findViewById(
					R.id.contactPhones_select_cancelBtn))
					.setOnClickListener(new ContactPhoneSelectCancelBtnOnClickListener());
		}

		@Override
		protected void resetPopupWindow() {
			// hide contact phones select phone list view
			((ListView) getContentView().findViewById(
					R.id.contactPhones_select_phonesListView))
					.setVisibility(View.GONE);

			// get contact phones select phone button parent linearLayout and
			// hide it
			LinearLayout _phoneBtnParentLinearLayout = (LinearLayout) getContentView()
					.findViewById(
							R.id.contactPhones_select_phoneBtn_linearLayout);
			_phoneBtnParentLinearLayout.setVisibility(View.GONE);

			// process phone button
			for (int i = 0; i < _phoneBtnParentLinearLayout.getChildCount(); i++) {
				// hide contact phones select phone button
				((Button) _phoneBtnParentLinearLayout.getChildAt(i))
						.setVisibility(View.GONE);
			}
		}

		// set contact phone number for selecting
		public void setContactPhones4Selecting(String displayName,
				List<String> phoneNumbers, SipCallMode dialContactPhoneMode) {
			// update select contact display name and dial its phone mode
			_mContactDisplayName = displayName;
			_mDialContactPhoneMode = dialContactPhoneMode;

			// set contact phones select title textView text
			((TextView) getContentView().findViewById(
					R.id.contactPhones_select_titleTextView))
					.setText((getResources()
							.getString(
									R.string.contactPhones_selectPopupWindow_titleTextView_text)
							+ " " + (SipCallMode.DIRECT_CALL == dialContactPhoneMode ? getResources()
							.getString(
									R.string.contactPhone_dialMode_selectPopupWindow_directdialBtn_title)
							: getResources()
									.getString(
											R.string.contactPhone_dialMode_selectPopupWindow_callbackBtn_title))
							.toLowerCase()).replace("***", displayName));

			// check phone numbers for selecting
			if (2 <= phoneNumbers.size() && phoneNumbers.size() <= 3) {
				// get contact phones select phone button parent linearLayout
				// and show it
				LinearLayout _phoneBtnParentLinearLayout = (LinearLayout) getContentView()
						.findViewById(
								R.id.contactPhones_select_phoneBtn_linearLayout);
				_phoneBtnParentLinearLayout.setVisibility(View.VISIBLE);

				// process phone button
				for (int i = 0; i < phoneNumbers.size(); i++) {
					// get contact phones select phone button
					Button _phoneBtn = (Button) _phoneBtnParentLinearLayout
							.getChildAt(i);

					// set button text and show it
					_phoneBtn.setText(phoneNumbers.get(i));
					_phoneBtn.setVisibility(View.VISIBLE);
				}
			} else {
				// get contact phones select phone list view
				ListView _phoneListView = (ListView) getContentView()
						.findViewById(R.id.contactPhones_select_phonesListView);

				// set phone list view adapter
				_phoneListView
						.setAdapter(new ArrayAdapter<String>(
								AppLaunchActivity.getAppContext(),
								R.layout.contact_phonenumbers_select_phoneslist_item_layout,
								phoneNumbers));

				// show phone list view
				_phoneListView.setVisibility(View.VISIBLE);
			}
		}

		// inner class
		// contact phone select phone button on click listener
		class ContactPhoneSelectPhoneBtnOnClickListener implements
				OnClickListener {

			@Override
			public void onClick(View v) {
				// get phone button text
				String _selectedPhone = (String) ((Button) v).getText();

				// dismiss contact phone select popup window
				dismiss();

				// make sip voice call
				makeSipVoiceCall(_mContactDisplayName, _selectedPhone,
						_mDialContactPhoneMode);
			}

		}

		// contact phone select phone listView on item click listener
		class ContactPhoneSelectPhoneListViewOnItemClickListener implements
				OnItemClickListener {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// get phone listView item data
				String _selectedPhone = (String) ((TextView) view).getText();

				// dismiss contact phone select popup window
				dismiss();

				// make sip voice call
				makeSipVoiceCall(_mContactDisplayName, _selectedPhone,
						_mDialContactPhoneMode);
			}

		}

		// contact phone select cancel button on click listener
		class ContactPhoneSelectCancelBtnOnClickListener implements
				OnClickListener {

			@Override
			public void onClick(View v) {
				// dismiss contact phone select popup window
				dismiss();
			}

		}

	}

}
