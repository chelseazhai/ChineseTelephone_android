package com.richitec.chinesetelephone.utils;

import android.os.Bundle;
import android.util.Log;

import com.richitec.chinesetelephone.bean.DialPreferenceBean;
import com.richitec.chinesetelephone.constant.DialPreference;
import com.richitec.chinesetelephone.constant.SystemConstants;
import com.richitec.chinesetelephone.constant.TelUser;
import com.richitec.commontoolkit.addressbook.AddressBookManager;
import com.richitec.commontoolkit.user.User;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.DataStorageUtils;

public class AppDataSaveRestoreUtil {
	public static void onSaveInstanceState (Bundle outState) {
		UserBean user = UserManager.getInstance().getUser();
		outState.putString(User.username.name(), user.getName());
	}
	
	
	public static void onRestoreInstanceState (Bundle savedInstanceState) {
		if (!AddressBookManager.getInstance().isInited()) {
			AddressBookManager.getInstance().traversalAddressBook();
			AddressBookManager.getInstance().registContactOberver();
		}
		
		String userName = savedInstanceState.getString(User.username.name());
		
		UserBean user = UserManager.getInstance().getUser();
		if (userName == null || userName.equals("")) {
		} else if (user.getName() == null || user.getName().equals("")) {
			loadAccount();
		}
		
	}
	
	public static void loadAccount() {
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
		Log.d(SystemConstants.TAG, " load account: " + user.toString());
		// 保存拨打设置属性
		DialPreferenceBean dialBean = DialPreferenceManager.getInstance()
				.getDialPreferenceBean();
//		String dialPattern = DataStorageUtils
//				.getString(DialPreference.DialSetting.dialPattern.name());
//		if (dialPattern != null)
//			dialBean.setDialPattern(dialPattern);
		String answerPattern = DataStorageUtils
				.getString(DialPreference.DialSetting.answerPattern.name());
		if (answerPattern != null)
			dialBean.setAnswerPattern(answerPattern);

	}
}
