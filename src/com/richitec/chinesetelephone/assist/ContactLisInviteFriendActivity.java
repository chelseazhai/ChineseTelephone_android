package com.richitec.chinesetelephone.assist;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
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
import android.widget.Toast;

import com.richitec.chinesetelephone.R;
import com.richitec.commontoolkit.activityextension.AppLaunchActivity;
import com.richitec.commontoolkit.activityextension.NavigationActivity;
import com.richitec.commontoolkit.addressbook.AddressBookManager;
import com.richitec.commontoolkit.addressbook.ContactBean;
import com.richitec.commontoolkit.customadapter.CommonListAdapter;
import com.richitec.commontoolkit.customcomponent.CommonPopupWindow;
import com.richitec.commontoolkit.customcomponent.ListViewQuickAlphabetBar;
import com.richitec.commontoolkit.customcomponent.ListViewQuickAlphabetBar.OnTouchListener;
import com.richitec.commontoolkit.utils.MyToast;
import com.richitec.commontoolkit.utils.StringUtils;

public class ContactLisInviteFriendActivity extends NavigationActivity {

	private static final String LOG_TAG = "ContactListInviteFriendActivity";
	
	private final String PRESENT_CONTACT_PHONES = "present_contact_phones";
	private final String PREVIOUS_PHONES_STYLE = "previous_phones_style";
	private final String CONTACT_IS_SELECTED = "contact_is_selected";
	private final String SELECTED_PHONE = "selected_phone";
	private int selectedPosition;
	
	private String inviteLink;

	// address book contacts list view
	private ListView _mABContactsListView;

	// all address book name phonetic sorted contacts detail info list
	private static List<ContactBean> _mAllNamePhoneticSortedContactsInfoArray = AddressBookManager
			.getInstance().getAllNamePhoneticSortedContactsInfoArray();

	// present contacts in address book detail info list
	private List<ContactBean> _mPresentContactsInABInfoArray;
	
	//the friends to send invite
	private List<ContactBean> _mInviteFriendsInfo;

	// contact search status
	private ContactSearchStatus _mContactSearchStatus = ContactSearchStatus.NONESEARCH;

	// define contact phone numbers select popup window
	private final ContactPhoneNumbersSelectPopupWindow _mContactPhoneNumbersSelectPopupWindow = new ContactPhoneNumbersSelectPopupWindow(
			R.layout.contact_phonenumbers_select_popupwindow_layout,
			LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.contact_list_invite_friend_activity_layout);
		
		inviteLink = getIntent().getStringExtra("inviteLink");
		
		Log.d("inviteLink", inviteLink);

		// set title
		setTitle(R.string.sms_invite_title);
		
		_mInviteFriendsInfo = new ArrayList<ContactBean>();

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

	// generate in address book contact adapter
	private ListAdapter generateInABContactAdapter(
			List<ContactBean> presentContactsInAB) {
		// in address book contacts adapter data keys
		final String PRESENT_CONTACT_PHOTO = "present_contact_photo";
		final String PRESENT_CONTACT_NAME = "present_contact_name";

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
				
				if(_contact.getExtension().get(SELECTED_PHONE)==null||
						((String)_contact.getExtension().get(SELECTED_PHONE)).equals("")){
					_dataMap.put(PRESENT_CONTACT_PHONES, _formatPhoneNumberString);
				}
				else{
					_contact.getExtension().put(PREVIOUS_PHONES_STYLE, _formatPhoneNumberString);
					String selectedPhone = (String) _contact.getExtension().get(SELECTED_PHONE);
					String allPhones = _contact.getFormatPhoneNumbers();
					int begin = allPhones.indexOf(selectedPhone);
					int end = begin + selectedPhone.length();
					SpannableString _newformatPhoneNumberString = new SpannableString(
							_formatPhoneNumberString);
					_newformatPhoneNumberString.setSpan(new ForegroundColorSpan(Color.RED), begin,
								end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					_dataMap.put(PRESENT_CONTACT_PHONES, _newformatPhoneNumberString);
				}
			} else {
				if(_contact.getExtension().get(SELECTED_PHONE)==null||
						((String)_contact.getExtension().get(SELECTED_PHONE)).equals("")){
					_dataMap.put(PRESENT_CONTACT_PHONES,
							_contact.getFormatPhoneNumbers());
				}
				else{
					_contact.getExtension().put(PREVIOUS_PHONES_STYLE, _contact.getFormatPhoneNumbers());
					String selectedPhone = (String) _contact.getExtension().get(SELECTED_PHONE);
					String allPhones = _contact.getFormatPhoneNumbers();
					int begin = allPhones.indexOf(selectedPhone);
					int end = begin + selectedPhone.length();
					SpannableString _newformatPhoneNumberString = new SpannableString(
							_contact.getFormatPhoneNumbers());
					_newformatPhoneNumberString.setSpan(new ForegroundColorSpan(Color.RED), begin,
								end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					_dataMap.put(PRESENT_CONTACT_PHONES, _newformatPhoneNumberString);
				}
			}

			// put alphabet index
			_dataMap.put(CommonListAdapter.ALPHABET_INDEX,
					_contact.getNamePhoneticsString());
			
			Boolean _isSelected = (Boolean) _contact.getExtension().get(
					CONTACT_IS_SELECTED);
			if (null == _isSelected) {
				_contact.getExtension().put(CONTACT_IS_SELECTED, false);
			}
			_dataMap.put(CONTACT_IS_SELECTED,
					_contact.getExtension().get(CONTACT_IS_SELECTED));

			// add data to list
			_addressBookContactsPresentDataList.add(_dataMap);
		}

		// get address book contacts listView adapter
		InviteFriendContactAdapter _addressBookContactsListViewAdapter = 
				(InviteFriendContactAdapter) ((ListView) findViewById(R.id.contactInAB_listView))
				.getAdapter();

		return null == _addressBookContactsListViewAdapter ? new InviteFriendContactAdapter(
				this, _addressBookContactsPresentDataList,
				R.layout.invite_friend_contact_layout, new String[] {
						PRESENT_CONTACT_PHOTO, PRESENT_CONTACT_NAME,
						PRESENT_CONTACT_PHONES,CONTACT_IS_SELECTED }, new int[] {
						R.id.addressBook_contact_avatar_imageView,
						R.id.adressBook_contact_displayName_textView,
						R.id.addressBook_contact_phoneNumber_textView ,
						R.id.addressBook_contact_checkbox
						})
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
				new AlertDialog.Builder(ContactLisInviteFriendActivity.this)
						.setTitle(R.string.contact_hasNoPhone_alertDialog_title)
						.setMessage(_clickItemViewData.getDisplayName())
						.setPositiveButton(
								R.string.contact_hasNoPhone_alertDialog_reselectBtn_title,
								null).show();
			} else {
				boolean isSelected =  (Boolean) _clickItemViewData.getExtension().get(CONTACT_IS_SELECTED);
				if(!isSelected){
					 switch (_clickItemViewData.getPhoneNumbers().size()) {
					 case 1:			 
						 _mInviteFriendsInfo.add(_clickItemViewData);
						 _clickItemViewData.getExtension().put(SELECTED_PHONE, _clickItemViewData.getPhoneNumbers().get(0));
						 _clickItemViewData.getExtension().put(CONTACT_IS_SELECTED, true);
						 @SuppressWarnings("unchecked")
						Map<String, Object> map = (Map<String, Object>) _mABContactsListView.getAdapter().getItem(position);
						 map.put(CONTACT_IS_SELECTED, true);
						 //暂存之前显示号码的方式，为取消时恢复号码之前显示
						 Object phonesObj = map.get(PRESENT_CONTACT_PHONES);					 
						 _clickItemViewData.getExtension().put(PREVIOUS_PHONES_STYLE, phonesObj);
						 //将选择号码显示为红色
						 SpannableString _formatPhoneNumberString = new SpannableString(
								 _clickItemViewData.getFormatPhoneNumbers());
						 				 
						 _formatPhoneNumberString.setSpan(
									new ForegroundColorSpan(Color.RED), 0,
									_clickItemViewData.getPhoneNumbers().get(0).length(),
								    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						 map.put(PRESENT_CONTACT_PHONES, _formatPhoneNumberString);
						 
						 ((InviteFriendContactAdapter)_mABContactsListView.getAdapter()).notifyDataSetChanged();
						 break;
					
					default:
						_mContactPhoneNumbersSelectPopupWindow
						.setContactPhones4Selecting(
								_clickItemViewData.getDisplayName(),
								_clickItemViewData.getPhoneNumbers(),
								position);

						// show contact phone numbers select popup window
						_mContactPhoneNumbersSelectPopupWindow.showAtLocation(
						parent, Gravity.CENTER, 0, 0);
						break;
					 }
				}
				 else{
					 _mInviteFriendsInfo.remove(_clickItemViewData);
					 _clickItemViewData.getExtension().put(CONTACT_IS_SELECTED, false);
					 _clickItemViewData.getExtension().put(SELECTED_PHONE, "");

					 Object phoneObj = _clickItemViewData.getExtension().get(PREVIOUS_PHONES_STYLE);
					 _clickItemViewData.getExtension().put(PREVIOUS_PHONES_STYLE, "");
					
					 @SuppressWarnings("unchecked")
					Map<String, Object> map = (Map<String, Object>) _mABContactsListView.getAdapter().getItem(position);
					 if(phoneObj!=null){
						 map.put(PRESENT_CONTACT_PHONES, phoneObj);
					 }
					 map.put(CONTACT_IS_SELECTED, false);
					 ((InviteFriendContactAdapter)_mABContactsListView.getAdapter()).notifyDataSetChanged();
				 }
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

	// contact phone numbers select popup window
	class ContactPhoneNumbersSelectPopupWindow extends CommonPopupWindow {

		// dial contact phone mode

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
				List<String> phoneNumbers,int position) {
			// update select contact display name and dial its phone mode
			selectedPosition = position;

			// set contact phones select title textView text
			((TextView) getContentView().findViewById(
					R.id.contactPhones_select_titleTextView))
					.setText(AppLaunchActivity
							.getAppContext()
							.getResources()
							.getString(
									R.string.select_phone_to_invite)
							.replace("***", displayName));

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
				choosePhone2Invite(_selectedPhone)	;	
			}

		}
		
		private void choosePhone2Invite(String _selectedPhone){
			 ContactBean _clickItemViewData = _mPresentContactsInABInfoArray
					.get(selectedPosition);
			 _mInviteFriendsInfo.add(_clickItemViewData);
			 _clickItemViewData.getExtension().put(SELECTED_PHONE, _selectedPhone);
			 _clickItemViewData.getExtension().put(CONTACT_IS_SELECTED, true);
			 
			 @SuppressWarnings("unchecked")
			 Map<String, Object> map = (Map<String, Object>) _mABContactsListView
					 .getAdapter().getItem(selectedPosition);
			 map.put(CONTACT_IS_SELECTED, true);
			 
			//暂存之前显示号码的方式，为取消时恢复号码之前显示
			 Object phonesObj = map.get(PRESENT_CONTACT_PHONES);					 
			 _clickItemViewData.getExtension().put(PREVIOUS_PHONES_STYLE, phonesObj);
			 //将选择号码显示为红色
			 SpannableString _formatPhoneNumberString = null;
			 if(phonesObj instanceof String){
				 _formatPhoneNumberString = new SpannableString(
						 (String)phonesObj);
			 }
			 else if(phonesObj instanceof SpannableString){
				 _formatPhoneNumberString = new SpannableString(
						 (SpannableString)phonesObj);
			 }
			 
			 String allPhones = _clickItemViewData.getFormatPhoneNumbers();
			 int begin = allPhones.indexOf(_selectedPhone);
			 int end = begin + _selectedPhone.length();
			 				 
			 _formatPhoneNumberString.setSpan(
						new ForegroundColorSpan(Color.RED), begin, end,
					    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			 map.put(PRESENT_CONTACT_PHONES, _formatPhoneNumberString);
			 
			 ((InviteFriendContactAdapter)_mABContactsListView
					 .getAdapter()).notifyDataSetChanged();
			// dismiss contact phone select popup window
			dismiss();
		}

		// contact phone select phone listView on item click listener
		class ContactPhoneSelectPhoneListViewOnItemClickListener implements
				OnItemClickListener {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// get phone listView item data
				String _selectedPhone = (String) ((TextView) view).getText();
				choosePhone2Invite(_selectedPhone)	;		
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
	
	public void onConfirm(View v){
		if(_mInviteFriendsInfo.size()>0){
			StringBuffer toNumbers = new StringBuffer();
			for(ContactBean b :_mInviteFriendsInfo){
				toNumbers.append((String) b.getExtension()
						.get(SELECTED_PHONE)).append(";");
			}	
			Uri uri = Uri.parse("smsto:" + toNumbers.toString());
			Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
			String inviteMessage = getString(R.string.invite_message)
					.replace("***", inviteLink);
			intent.putExtra("sms_body", inviteMessage);
			startActivity(intent);
		}
		else{
			MyToast.show(ContactLisInviteFriendActivity.this, 
					R.string.pls_choose_invite_people, Toast.LENGTH_SHORT);
		}
	}
	
	public void onCancel(View v){
		finish();
	}
	@Override
	public void onDestroy(){
		resetContact();
		super.onDestroy();
	}
	
	private void resetContact(){
		for(ContactBean b :_mInviteFriendsInfo){
			 b.getExtension().put(CONTACT_IS_SELECTED, false);
			 b.getExtension().put(SELECTED_PHONE, "");
		}
		_mInviteFriendsInfo.clear();
	}
}
