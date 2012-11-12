package com.richitec.chinesetelephone;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.richitec.chinesetelephone.account.AccountSettingActivity;
import com.richitec.chinesetelephone.account.SettingActivity;
import com.richitec.chinesetelephone.constant.SystemConstants;
import com.richitec.chinesetelephone.constant.TelUser;
import com.richitec.chinesetelephone.tab7tabcontent.ChineseTelephoneTabActivity;
import com.richitec.chinesetelephone.tab7tabcontent.ContactListTabContentActivity;
import com.richitec.chinesetelephone.tab7tabcontent.DialTabContentActivity;
import com.richitec.chinesetelephone.util.TelUserBean;
import com.richitec.commontoolkit.activityextension.AppLaunchActivity;
import com.richitec.commontoolkit.addressbook.AddressBookManager;
import com.richitec.commontoolkit.user.User;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.DataStorageUtils;

public class ChineseTelephoneAppLaunchActivity extends AppLaunchActivity {

	@Override
	public Drawable splashImg() {
		return getResources().getDrawable(R.drawable.ic_splash);
	}

	@Override
	public Intent intentActivity() {
		// go to Chinese telephone main tab activity
		loadAccount();
		UserBean userBean = UserManager.getInstance().getUser();
		if(userBean.getPassword()!=null&&!userBean.getPassword().equals("")
				&&userBean.getUserKey()!=null&&!userBean.getUserKey().equals(""))
			return new Intent(this, SettingActivity.class);
		else{
			return new Intent(this, AccountSettingActivity.class);
		}
	}

	@Override
	public void didFinishLaunching() {
		// traversal address book
		AddressBookManager.getInstance().traversalAddressBook();

		// init all name phonetic sorted contacts info array
		ContactListTabContentActivity.initNamePhoneticSortedContactsInfoArray();

		// init dial phone button dtmf sound pool map
		DialTabContentActivity.initDialPhoneBtnDTMFSoundPoolMap(this);
	
	}
	
	private void loadAccount() {
		String userName = DataStorageUtils.getString(User.username.name());
		String userkey = DataStorageUtils.getString(User.userkey.name());
		String password = DataStorageUtils.getString(User.password.name());
		String countrycode = DataStorageUtils.getString(TelUser.countryCode.name());
		TelUserBean userBean = new TelUserBean();
		userBean.setName(userName);
		userBean.setUserKey(userkey);
		userBean.setPassword(password);
		userBean.setCountryCode(countrycode);
		UserManager.getInstance().setUser(userBean);
		Log.d(SystemConstants.TAG, "load account: " + userBean.toString());
	}

}
