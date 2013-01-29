package com.richitec.chinesetelephone.account;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.assist.SettingActivity;
import com.richitec.chinesetelephone.constant.SystemConstants;
import com.richitec.chinesetelephone.constant.TelUser;
import com.richitec.chinesetelephone.sip.SipUtils;
import com.richitec.chinesetelephone.sip.listeners.SipRegistrationStateListenerImp;
import com.richitec.chinesetelephone.tab7tabcontent.ChineseTelephoneTabActivity;
import com.richitec.chinesetelephone.utils.AppDataSaveRestoreUtil;
import com.richitec.chinesetelephone.utils.AppUpdateManager;
import com.richitec.chinesetelephone.utils.CountryCodeManager;
import com.richitec.commontoolkit.user.User;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.DataStorageUtils;
import com.richitec.commontoolkit.utils.HttpUtils;
import com.richitec.commontoolkit.utils.HttpUtils.HttpRequestType;
import com.richitec.commontoolkit.utils.HttpUtils.HttpResponseResult;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;
import com.richitec.commontoolkit.utils.HttpUtils.PostRequestFormat;
import com.richitec.commontoolkit.utils.MyToast;
import com.richitec.commontoolkit.utils.StringUtils;

public class AccountSettingActivity extends Activity {
	private ProgressDialog progressDialog;
	private AlertDialog chooseCountryDialog;
	private int lastSelectCountryCode = 0;
	private CountryCodeManager countryCodeManager;
	private String PWD_MASK = "#@1d~`*)";
	private boolean useSavedPsw;

	private boolean isFirstLogin;

	private TextWatcher onTextChanged = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// ((CheckBox)(findViewById(R.id.account_remember_psw_cbtn))).setChecked(false);
			useSavedPsw = false;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}
	};
	private OnHttpRequestListener onFinishedLogin = new OnHttpRequestListener() {

		@Override
		public void onFinished(HttpResponseResult responseResult) {
			try {

				JSONObject data = new JSONObject(
						responseResult.getResponseText());
				String result = data.getString("result");
				if (result.equals("0")) {
					// login success
					loginSuccess(data);
				} else if (result.equals("1") || result.equals("2")) {
					Log.d("Error Result", result);
					loginFailed();
				} else {
					loginError();
				}

			} catch (Exception e) {
				e.printStackTrace();
				loginError();
			}

		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			loginError();
		}
	};

	private void saveUserAccount() {
		Log.d(SystemConstants.TAG, "save user account");
		UserBean user = UserManager.getInstance().getUser();
		Log.d(SystemConstants.TAG, "user: " + user.toString());
		DataStorageUtils.putObject(User.username.name(), user.getName());
		DataStorageUtils.putObject(TelUser.countryCode.name(),
				user.getValue(TelUser.countryCode.name()));
		DataStorageUtils.putObject(TelUser.dialCountryCode.name(),
				user.getValue(TelUser.dialCountryCode.name()));

		DataStorageUtils.putObject(TelUser.vosphone.name(),
				user.getValue(TelUser.vosphone.name()));
		DataStorageUtils.putObject(TelUser.vosphone_pwd.name(),
				user.getValue(TelUser.vosphone_pwd.name()));
		DataStorageUtils.putObject(User.userkey.name(), user.getUserKey());
		DataStorageUtils.putObject(TelUser.bindphone.name(),
				user.getValue(TelUser.bindphone.name()));
		DataStorageUtils.putObject(TelUser.bindphone_country_code.name(),
				user.getValue(TelUser.bindphone_country_code.name()));
		if (user.isRememberPwd()) {
			DataStorageUtils
					.putObject(User.password.name(), user.getPassword());
		} else {
			DataStorageUtils.putObject(User.password.name(), "");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.account_setting_layout);

		Intent intent = this.getIntent();

		isFirstLogin = intent.getBooleanExtra("firstLogin", true);

		Bundle data = intent.getExtras();
		if (data != null && data.containsKey(SettingActivity.TITLE_NAME)) {
			String title = data.getString(SettingActivity.TITLE_NAME);
			// Log.d(SystemConstants.TAG, "title:"+title);
			((TextView) findViewById(R.id.account_setting_title))
					.setText(title);
		}

		countryCodeManager = CountryCodeManager.getInstance();

		EditText userEditText = (EditText) findViewById(R.id.account_user_edittext);
		EditText pswEditText = (EditText) findViewById(R.id.account_psw_edittext);

		userEditText.addTextChangedListener(onTextChanged);
		pswEditText.addTextChangedListener(onTextChanged);

		userEditText.setOnFocusChangeListener(new OnChangeEditTextBGListener());
		pswEditText.setOnFocusChangeListener(new OnChangeEditTextBGListener());

		initUI();

		AppUpdateManager updateManager = new AppUpdateManager(this);
		updateManager.checkVersion(false);
	}

	private void initUI() {
		UserBean user = UserManager.getInstance().getUser();
		String countryCode = (String) user.getValue(TelUser.countryCode.name());
		if (countryCode == null || countryCode.equals("")) {
			((Button) findViewById(R.id.account_choose_country_btn))
					.setText(R.string.pls_select_country);
		} else {
			lastSelectCountryCode = countryCodeManager
					.getCountryIndex((String) user.getValue(TelUser.countryCode
							.name()));
			((Button) findViewById(R.id.account_choose_country_btn))
					.setText(countryCodeManager
							.getCountryName(lastSelectCountryCode));
		}

		EditText userEditText = (EditText) findViewById(R.id.account_user_edittext);
		EditText pswEditText = (EditText) findViewById(R.id.account_psw_edittext);
		CheckBox remember = (CheckBox) findViewById(R.id.account_remember_psw_cbtn);

		userEditText.setText(user.getName());

		if (user.getPassword() != null && user.getPassword() != "") {
			pswEditText.setText(PWD_MASK);
			useSavedPsw = true;
			remember.setChecked(true);
		} else {
			useSavedPsw = false;
			remember.setChecked(false);
		}

		// 从设置页面切换账号时标明用户是否在登录时勾选记住密码
		if (!user.isRememberPwd()) {
			// Log.d("Account remember", user.isRememberPwd()+"");
			pswEditText.setText("");
			useSavedPsw = false;
			remember.setChecked(false);
		}

	}

	@Override
	public void onBackPressed() {
		if (isFirstLogin) {
			Intent i = new Intent(Intent.ACTION_MAIN);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.addCategory(Intent.CATEGORY_HOME);
			startActivity(i);
		} else {
			finish();
		}
	}

	// country code select dialog
	public void chooseCountry(View v) {
		AlertDialog.Builder chooseCountryDialogBuilder = new AlertDialog.Builder(
				this);
		chooseCountryDialogBuilder.setTitle(R.string.countrycode_list);
		chooseCountryDialogBuilder.setSingleChoiceItems(
				countryCodeManager.getCountryNameList(), lastSelectCountryCode,
				new ChooseCountryListener());
		chooseCountryDialogBuilder.setNegativeButton(R.string.cancel, null);
		chooseCountryDialog = chooseCountryDialogBuilder.create();
		chooseCountryDialog.show();
	}

	public void onRegist(View v) {
		Intent intent = new Intent(this, AccountDirectRegisterActivity.class);
		startActivity(intent);
	}

	public void onLogin(View v) {
		String username = ((EditText) (findViewById(R.id.account_user_edittext)))
				.getText().toString().trim();
		String psw = ((EditText) (findViewById(R.id.account_psw_edittext)))
				.getText().toString().trim();
		String countrycode = countryCodeManager
				.getCountryCode(((Button) findViewById(R.id.account_choose_country_btn))
						.getText().toString().trim());

		boolean isRemember = ((CheckBox) (findViewById(R.id.account_remember_psw_cbtn)))
				.isChecked();

		// Log.d("AccountSetting",
		// username+":"+psw+":"+countrycode+":"+isRemember);

		if (countrycode == null) {
			MyToast.show(this, R.string.pls_select_country, Toast.LENGTH_SHORT);
			return;
		}

		if (username.equals("")) {
			MyToast.show(this, R.string.number_cannot_be_null,
					Toast.LENGTH_LONG);
			return;
		}

		if (psw.equals("")) {
			MyToast.show(this, R.string.psw_cannot_be_null, Toast.LENGTH_LONG);
			return;
		}

		if (!username.matches("(^[0-9]*)")) {
			MyToast.show(this, R.string.phone_wrong_format, Toast.LENGTH_LONG);
			return;
		}

		if (!useSavedPsw) {
			UserBean user = new UserBean();
			user.setName(username);
			user.setPassword(StringUtils.md5(psw));
			UserManager.getInstance().setUser(user);
		}

		progressDialog = ProgressDialog.show(this, null,
				getString(R.string.logining), true);

		UserBean telUserBean = UserManager.getInstance().getUser();
		telUserBean.setRememberPwd(isRemember);
		telUserBean.setValue(TelUser.countryCode.name(), countrycode);
		String dialCountryCode = (String) telUserBean
				.getValue(TelUser.dialCountryCode.name());
		if (dialCountryCode == null || dialCountryCode.trim().equals("")) {
			telUserBean.setValue(TelUser.dialCountryCode.name(), countrycode);
		}

		// unregitst sip account first
		SipUtils.unregisterSipAccount(null);
		loginAccount(telUserBean);
	}

	private void loginAccount(UserBean telUserBean) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("loginName", telUserBean.getName());
		params.put("loginPwd", telUserBean.getPassword());
		params.put("countryCode",
				(String) telUserBean.getValue(TelUser.countryCode.name()));
		params.put("brand", Build.BRAND);
		params.put("model", Build.MODEL);
		params.put("release", Build.VERSION.RELEASE);
		params.put("sdk", Build.VERSION.SDK);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		int nowWidth = dm.widthPixels; // 当前分辨率 宽度
		int nowHeigth = dm.heightPixels; // 当前分辨率高度
		params.put("width", Integer.toString(nowWidth));
		params.put("height", Integer.toString(nowHeigth));

		String loginUrl = getString(R.string.server_url)
				+ getString(R.string.login_url);

		HttpUtils.postRequest(loginUrl, PostRequestFormat.URLENCODED, params,
				null, HttpRequestType.ASYNCHRONOUS, onFinishedLogin);
	}

	public void loginError() {
		closeProgressDialog();
		MyToast.show(this, R.string.login_error, Toast.LENGTH_LONG);
		processError();
	}

	public void loginFailed() {
		closeProgressDialog();
		MyToast.show(this, R.string.login_failed, Toast.LENGTH_LONG);
		processError();
	}

	private void processError() {
		isFirstLogin = true;
		SipUtils.unregisterSipAccount(null);
		SipRegistrationStateListenerImp.cancelVOIPOnlineStatus();
	}

	public void loginSuccess(JSONObject data) {
		closeProgressDialog();
		try {
			String userKey = data.getString("userkey");
			String vosphone = data.getString("vosphone");
			String vosphone_psw = data.getString("vosphone_pwd");
			String bindPhone = data.getString("bindphone");
			String bindPhoneCountryCode = data
					.getString("bindphone_country_code");
			String status = data.getString("status");
			String email = null;
			try {
				email = data.getString("email");
			} catch (JSONException e) {
			}
			Double regGivenMoney = data.getDouble("reg_given_money");
			
			UserBean telUser = UserManager.getInstance().getUser();
			telUser.setUserKey(userKey);
			telUser.setValue(TelUser.vosphone.name(), vosphone);
			telUser.setValue(TelUser.vosphone_pwd.name(), vosphone_psw);
			telUser.setValue(TelUser.bindphone.name(), bindPhone);
			telUser.setValue(TelUser.bindphone_country_code.name(),
					bindPhoneCountryCode);
			saveUserAccount();

			Intent intent = new Intent(AccountSettingActivity.this,
					ChineseTelephoneTabActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("status", status);
			intent.putExtra("email", email);
			intent.putExtra("reg_given_money", regGivenMoney);
			startActivity(intent);
			finish();

		} catch (JSONException e) {
			e.printStackTrace();
			MyToast.show(this, R.string.login_error, Toast.LENGTH_SHORT);
			processError();
		}

	}

	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	public void onCheckBoxTitleClick(View v) {
		boolean isCheck = !((CheckBox) findViewById(R.id.account_remember_psw_cbtn))
				.isChecked();
		((CheckBox) findViewById(R.id.account_remember_psw_cbtn))
				.setChecked(isCheck);
	}

	public void onForgetPSWBtnClick(View v) {
		Intent intent = new Intent(this, AccountForgetPSWActivity.class);
		startActivity(intent);
	}

	class ChooseCountryListener implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			lastSelectCountryCode = which;
			((Button) (AccountSettingActivity.this
					.findViewById(R.id.account_choose_country_btn)))
					.setText(countryCodeManager.getCountryName(which));
			chooseCountryDialog.dismiss();
		}

	}

	class OnChangeEditTextBGListener implements OnFocusChangeListener {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			if (hasFocus) {
				((LinearLayout) v.getParent())
						.setBackgroundDrawable(AccountSettingActivity.this
								.getResources().getDrawable(
										R.drawable.bg_edit_s));
			} else {
				((LinearLayout) v.getParent())
						.setBackgroundDrawable(AccountSettingActivity.this
								.getResources().getDrawable(
										R.drawable.bg_edityzm));
			}
		}

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		AppDataSaveRestoreUtil.onRestoreInstanceState(savedInstanceState);
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		AppDataSaveRestoreUtil.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}
}
