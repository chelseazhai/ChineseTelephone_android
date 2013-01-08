package com.richitec.chinesetelephone;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.Log;

import com.richitec.chinesetelephone.account.AccountSettingActivity;
import com.richitec.chinesetelephone.bean.DialPreferenceBean;
import com.richitec.chinesetelephone.constant.DialPreference;
import com.richitec.chinesetelephone.constant.SystemConstants;
import com.richitec.chinesetelephone.constant.TelUser;
import com.richitec.chinesetelephone.service.NoticeService;
import com.richitec.chinesetelephone.tab7tabcontent.ChineseTelephoneTabActivity;
import com.richitec.chinesetelephone.tab7tabcontent.ContactListTabContentActivity;
import com.richitec.chinesetelephone.tab7tabcontent.DialTabContentActivity;
import com.richitec.chinesetelephone.utils.DialPreferenceManager;
import com.richitec.commontoolkit.activityextension.AppLaunchActivity;
import com.richitec.commontoolkit.addressbook.AddressBookManager;
import com.richitec.commontoolkit.addressbook.ContactSyncService;
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
		Intent service = new Intent(this, ContactSyncService.class);
		startService(service);

		Intent noticeService = new Intent(this, NoticeService.class);
		startService(noticeService);
		
		// go to Chinese telephone main tab activity
		loadAccount();

		UserBean userBean = UserManager.getInstance().getUser();
		if (userBean.getPassword() != null
				&& !userBean.getPassword().equals("")
				&& userBean.getUserKey() != null
				&& !userBean.getUserKey().equals("")) {
			return new Intent(this, ChineseTelephoneTabActivity.class);
		} else {
			return new Intent(this, AccountSettingActivity.class);
		}
	}

	@Override
	public void didFinishLaunching() {
		// traversal address book
		AddressBookManager.setFilterMode(AddressBookManager.FILTER_DEFAULT);
		AddressBookManager.getInstance().traversalAddressBook();

		// init all name phonetic sorted contacts info array
		ContactListTabContentActivity.initNamePhoneticSortedContactsInfoArray();

	}

	private void loadAccount() {
		String userName = DataStorageUtils.getString(User.username.name());
		String userkey = DataStorageUtils.getString(User.userkey.name());
		String password = DataStorageUtils.getString(User.password.name());
		String countrycode = DataStorageUtils.getString(TelUser.countryCode
				.name());
		String dialcountrycode = DataStorageUtils
				.getString(TelUser.dialCountryCode.name());
		String vosphone = DataStorageUtils.getString(TelUser.vosphone.name());
		String vosphone_psw = DataStorageUtils.getString(TelUser.vosphone_pwd
				.name());
		String bindPhone = DataStorageUtils.getString(TelUser.bindphone.name());
		String bindPhoneCountryCode = DataStorageUtils
				.getString(TelUser.bindphone_country_code.name());

		UserBean user = new UserBean();
		user.setName(userName);
		user.setUserKey(userkey);
		user.setPassword(password);
		user.setValue(TelUser.countryCode.name(), countrycode);
		user.setValue(TelUser.bindphone.name(), bindPhone);
		user.setValue(TelUser.bindphone_country_code.name(), bindPhoneCountryCode);
		
		
		if (dialcountrycode == null || dialcountrycode.trim().equals("")) {
			user.setValue(TelUser.dialCountryCode.name(), countrycode);
		} else {
			user.setValue(TelUser.dialCountryCode.name(), dialcountrycode);
		}
		if (password != null && !password.equals("") && userkey != null
				&& !userkey.equals("")) {
			user.setRememberPwd(true);
		}
		user.setValue(TelUser.vosphone.name(), vosphone);
		user.setValue(TelUser.vosphone_pwd.name(), vosphone_psw);
		UserManager.getInstance().setUser(user);
		Log.d(SystemConstants.TAG + " load account: ", user.toString());
		// 保存拨打设置属性
		DialPreferenceBean dialBean = DialPreferenceManager.getInstance()
				.getDialPreferenceBean();
		String dialPattern = DataStorageUtils
				.getString(DialPreference.DialSetting.dialPattern.name());
		if (dialPattern != null)
			dialBean.setDialPattern(dialPattern);
		String answerPattern = DataStorageUtils
				.getString(DialPreference.DialSetting.answerPattern.name());
		if (answerPattern != null)
			dialBean.setAnswerPattern(answerPattern);

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		System.exit(0);
	}

	
}
