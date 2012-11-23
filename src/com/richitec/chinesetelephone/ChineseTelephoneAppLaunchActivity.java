package com.richitec.chinesetelephone;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import com.richitec.chinesetelephone.account.AccountSettingActivity;
import com.richitec.chinesetelephone.assist.SettingActivity;
import com.richitec.chinesetelephone.bean.DialPreferenceBean;
import com.richitec.chinesetelephone.bean.TelUserBean;
import com.richitec.chinesetelephone.constant.DialPreference;
import com.richitec.chinesetelephone.constant.SystemConstants;
import com.richitec.chinesetelephone.constant.TelUser;
import com.richitec.chinesetelephone.tab7tabcontent.ChineseTelephoneTabActivity;
import com.richitec.chinesetelephone.tab7tabcontent.ContactListTabContentActivity;
import com.richitec.chinesetelephone.tab7tabcontent.DialTabContentActivity;
import com.richitec.commontoolkit.activityextension.AppLaunchActivity;
import com.richitec.commontoolkit.addressbook.AddressBookManager;
import com.richitec.commontoolkit.user.User;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.DataStorageUtils;
import com.rictitec.chinesetelephone.utils.DialPreferenceManager;

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
				&&userBean.getUserKey()!=null&&!userBean.getUserKey().equals("")){
			return new Intent(this, ChineseTelephoneTabActivity.class);
		}
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
		String areacode = DataStorageUtils.getString(TelUser.areaCode.name());
		String vosphone = DataStorageUtils.getString(TelUser.vosphone.name());
		String vosphone_psw = DataStorageUtils.getString(TelUser.vosphone_pwd.name());
		
		TelUserBean userBean = new TelUserBean();
		userBean.setName(userName);
		userBean.setUserKey(userkey);
		userBean.setPassword(password);
		userBean.setCountryCode(countrycode);
		if (password != null && !password.equals("") && userkey != null && !userkey.equals("")) {
			userBean.setRememberPwd(true);
		}
		userBean.setAreaCode(areacode);
		userBean.setVosphone(vosphone);
		userBean.setVosphone_pwd(vosphone_psw);
		UserManager.getInstance().setUser(userBean);
		Log.d(SystemConstants.TAG+" load account: ", userBean.toString());
		//保存拨打设置属性
		DialPreferenceBean dialBean = DialPreferenceManager.getInstance().getDialPreferenceBean();
		String dialPattern = DataStorageUtils.getString(DialPreference.DialSetting.dialPattern.name());
		if(dialPattern!=null)
			dialBean.setDialPattern(dialPattern);
		String answerPattern = DataStorageUtils.getString(DialPreference.DialSetting.answerPattern.name());
		if(answerPattern!=null)
			dialBean.setAnswerPattern(answerPattern);
		
		//Log.d("LoadDialSetting", dialPattern+":"+answerPattern);
	}

}
