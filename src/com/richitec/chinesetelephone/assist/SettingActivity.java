package com.richitec.chinesetelephone.assist;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.account.AccountForgetPSWActivity;
import com.richitec.chinesetelephone.account.AccountSettingActivity;
import com.richitec.chinesetelephone.bean.DialPreferenceBean;
import com.richitec.chinesetelephone.call.SipCallModeSelector;
import com.richitec.chinesetelephone.call.SipCallModeSelector.SipCallModeSelectPattern;
import com.richitec.chinesetelephone.constant.DialPreference;
import com.richitec.chinesetelephone.constant.LaunchSetting;
import com.richitec.chinesetelephone.constant.SystemConstants;
import com.richitec.chinesetelephone.constant.TelUser;
import com.richitec.chinesetelephone.sip.SipUtils;
import com.richitec.chinesetelephone.sip.listeners.SipRegistrationStateListenerImp;
import com.richitec.chinesetelephone.utils.AppDataSaveRestoreUtil;
import com.richitec.chinesetelephone.utils.AppUpdateManager;
import com.richitec.chinesetelephone.utils.CountryCodeManager;
import com.richitec.chinesetelephone.utils.DialPreferenceManager;
import com.richitec.commontoolkit.activityextension.NavigationActivity;
import com.richitec.commontoolkit.addressbook.AddressBookManager;
import com.richitec.commontoolkit.customcomponent.CTPopupWindow;
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

public class SettingActivity extends NavigationActivity {
	private AlertDialog chooseCountryDialog;
	private CountryCodeManager countryCodeManager;
	public static String TITLE_NAME = "titlename";
	private String inviteLink;

	private AlertDialog.Builder builder;
	private AlertDialog dialog;
	private ProgressDialog progressDialog;
	private RadioGroup dialGroup;
	private RadioGroup answerGroup;
	private RadioGroup launchGroup;
	private RadioGroup loginGroup;

	private ModifyPSWPopupWindow modifyPSWPopupWindow;
	private GetPSWPopupWindow getPSWPopupWindow;
	private SetDialCountryCodePopupWindow setDialCountryCodePopupWindow;
	private SetDialPreferencePopupWindow setDialPreferencePopupWin;
	private LaunchSetCodePopupWindow launchSetCodePopupWindow;
	private SetBindNumberPopupWindow setBindNumberPopupWindow;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(SystemConstants.TAG, "SettingActivity - onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		countryCodeManager = CountryCodeManager.getInstance();

		// modifyPSWPopupWindow = new ModifyPSWPopupWindow(
		// R.layout.modify_psw_popupwindow_layout, LayoutParams.FILL_PARENT,
		// LayoutParams.FILL_PARENT);

		// getPSWPopupWindow = new GetPSWPopupWindow(
		// R.layout.get_psw_popupwindow_layout, LayoutParams.FILL_PARENT,
		// LayoutParams.FILL_PARENT);

		// setDialCountryCodePopupWindow = new SetDialCountryCodePopupWindow(
		// R.layout.set_dialcountrycode_popupwindow_layout,
		// LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

		setDialPreferencePopupWin = new SetDialPreferencePopupWindow(
				R.layout.dial_preference_popupwindow_layout,
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

		launchSetCodePopupWindow = new LaunchSetCodePopupWindow(
				R.layout.setup_preference_popupwindow_layout,
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

		setBindNumberPopupWindow = new SetBindNumberPopupWindow(
				R.layout.set_bind_number_popupwindow_layout,
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

		LinearLayout inviteFriend = (LinearLayout) findViewById(R.id.account_invite_btn);
		inviteFriend.setOnClickListener(inviteFriendListener);

		LinearLayout chargeAccount = (LinearLayout) findViewById(R.id.account_charge_btn);
		chargeAccount.setOnClickListener(chargeAccountListener);

		LinearLayout changeAccount = (LinearLayout) findViewById(R.id.account_setting_btn);
		changeAccount.setOnClickListener(changeAccountListener);

		LinearLayout modifyPSW = (LinearLayout) findViewById(R.id.account_changePSW_btn);
		modifyPSW.setOnClickListener(modifyPSWListener);

		LinearLayout getPSW = (LinearLayout) findViewById(R.id.account_getPSW_btn);
		getPSW.setOnClickListener(getPswListener);

		dialGroup = (RadioGroup) setDialPreferencePopupWin.getContentView()
				.findViewById(R.id.dial_radio_group);
		answerGroup = (RadioGroup) setDialPreferencePopupWin.getContentView()
				.findViewById(R.id.answer_radio_group);

		launchGroup = (RadioGroup) launchSetCodePopupWindow.getContentView()
				.findViewById(R.id.auto_launch_radio_group);
		loginGroup = (RadioGroup) launchSetCodePopupWindow.getContentView()
				.findViewById(R.id.auto_login_group);

		builder = new AlertDialog.Builder(SettingActivity.this)
				.setTitle(R.string.alert_title)
				.setMessage(R.string.get_psw_finish)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
								UserManager.getInstance().getUser()
										.setPassword("");
								Intent intent = new Intent(
										SettingActivity.this,
										AccountSettingActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
										| Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent);
								finish();
							}
						});

		setTitle(R.string.menu_settings);

	}

	@Override
	protected void onStop() {
		Log.d(SystemConstants.TAG, "SettingActivity - onStop");
		super.onStop();
	}

	@Override
	protected void onResume() {
		Log.d(SystemConstants.TAG, "SettingActivity - onResume");
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.d(SystemConstants.TAG, "SettingActivity - onPause");
		super.onPause();
	}

	@Override
	protected void onRestart() {
		Log.d(SystemConstants.TAG, "SettingActivity - onRestart");
		super.onRestart();
	}

	@Override
	protected void onStart() {
		Log.d(SystemConstants.TAG, "SettingActivity - onStart");
		super.onStart();
	}

	public void setAuthNumber(View v) {
		progressDialog = ProgressDialog.show(this, null,
				getString(R.string.sending_request), true);
		UserBean userBean = UserManager.getInstance().getUser();
		String username = userBean.getName();
		String countrycode = (String) userBean.getValue(TelUser.countryCode
				.name());
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("username", username);
		params.put("countryCode", countrycode);
		HttpUtils.postSignatureRequest(getString(R.string.server_url)
				+ getString(R.string.getBindPhone),
				PostRequestFormat.URLENCODED, params, null,
				HttpRequestType.ASYNCHRONOUS, onFinishedGetBindPhone);

	}

	private OnHttpRequestListener onFinishedGetBindPhone = new OnHttpRequestListener() {

		@Override
		public void onFinished(HttpResponseResult responseResult) {
			dismiss();
			Button countryButton = (Button) setBindNumberPopupWindow
					.getContentView().findViewById(
							R.id.setAuth_choose_country_btn);
			EditText phoneET = (EditText) setBindNumberPopupWindow
					.getContentView().findViewById(
							R.id.set_auth_number_editText);

			try {
				JSONObject data = new JSONObject(
						responseResult.getResponseText());
				String phone = data.getString("bindphone");
				String countrycode = data.getString("bindphone_country_code");

				countryButton.setText(countryCodeManager
						.getCountryName(countryCodeManager
								.getCountryIndex(countrycode)));

				phoneET.setText(phone);

			} catch (JSONException e) {
				e.printStackTrace();
				countryButton.setText(countryCodeManager.getCountryName(0));
				phoneET.setText("");
			}

			setBindNumberPopupWindow.showAtLocation(
					findViewById(R.id.setting_main_layout), Gravity.CENTER, 0,
					0);
		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			dismiss();
			MyToast.show(SettingActivity.this, R.string.get_bindphone_failed,
					Toast.LENGTH_SHORT);
			Button countryButton = (Button) setBindNumberPopupWindow
					.getContentView().findViewById(
							R.id.setAuth_choose_country_btn);
			EditText phoneET = (EditText) setBindNumberPopupWindow
					.getContentView().findViewById(
							R.id.set_auth_number_editText);
			countryButton.setText(countryCodeManager.getCountryName(0));
			phoneET.setText("");
			setBindNumberPopupWindow.showAtLocation(
					findViewById(R.id.setting_main_layout), Gravity.CENTER, 0,
					0);
		}
	};

	public void logout(View v) {
		new AlertDialog.Builder(SettingActivity.this)
				.setTitle(R.string.alert_title)
				.setMessage(R.string.logout_messge)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								DataStorageUtils.putObject(
										User.username.name(), "");
								DataStorageUtils.putObject(
										User.password.name(), "");
								DataStorageUtils.putObject(
										TelUser.vosphone.name(), "");
								DataStorageUtils.putObject(
										TelUser.vosphone_pwd.name(), "");
								DataStorageUtils.putObject(User.userkey.name(),
										"");
								DataStorageUtils.putObject(
										TelUser.countryCode.name(), "");
								DataStorageUtils.putObject(
										TelUser.dialCountryCode.name(), "");
								DataStorageUtils.putObject(
										TelUser.bindphone.name(), "");
								DataStorageUtils.putObject(
										TelUser.bindphone_country_code.name(),
										"");

								SipUtils.unregisterSipAccount(null);
								SipUtils.destroySipEngine();
								AddressBookManager.getInstance()
										.unRegistContactObserver();
								SipRegistrationStateListenerImp
										.cancelVOIPOnlineStatus();

								UserManager.getInstance().setUser(
										new UserBean());
								System.exit(0);
							}
						}).setNegativeButton(R.string.cancel, null).show();
	}

	public void checkUpdateVersion(View v) {
		AppUpdateManager updateManager = new AppUpdateManager(this);
		updateManager.checkVersion(true);
	}

	@Override
	public void onBackPressed() {
		this.getParent().onBackPressed();
	}

	public void setDialCountryCode(View v) {
		setDialCountryCodePopupWindow = new SetDialCountryCodePopupWindow(
				R.layout.set_dialcountrycode_popupwindow_layout,
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

		UserBean telUser = UserManager.getInstance().getUser();
		String dialcountrycode = (String) telUser
				.getValue(TelUser.dialCountryCode.name());
		int dialCountryIndex = countryCodeManager
				.getCountryIndex(dialcountrycode);
		((Button) (setDialCountryCodePopupWindow.getContentView()
				.findViewById(R.id.set_dial_country_btn)))
				.setText(countryCodeManager.getCountryName(dialCountryIndex));
		setDialCountryCodePopupWindow
				.setSelectDialCountryCode(dialCountryIndex);
		setDialCountryCodePopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
	}

	public void setSetupPreference(View v) {
		launchSetCodePopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
	}

	public void setDialPreference(View v) {
		setDialPreferencePopupWin.showAtLocation(v, Gravity.CENTER, 0, 0);
	}

	public void getRemainMoney(View v) {
		Intent intent = new Intent(SettingActivity.this,
				AccountInfoActivity.class);
		intent.putExtra("nav_back_btn_default_title",
				getString(R.string.setting));
		startActivity(intent);
	}

	public void inviteFriend(View v) {
		progressDialog = ProgressDialog.show(this, null,
				getString(R.string.sending_request), true);
		UserBean userBean = UserManager.getInstance().getUser();
		String username = userBean.getName();
		String countrycode = (String) userBean.getValue(TelUser.countryCode
				.name());
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("username", username);
		params.put("countryCode", countrycode);
		HttpUtils.postSignatureRequest(getString(R.string.server_url)
				+ getString(R.string.getInviteLink),
				PostRequestFormat.URLENCODED, params, null,
				HttpRequestType.ASYNCHRONOUS, onFinishedGetInviteLink);
	}

	private OnHttpRequestListener onFinishedGetInviteLink = new OnHttpRequestListener() {

		@Override
		public void onFinished(HttpResponseResult responseResult) {

			inviteLink = responseResult.getResponseText();
			dismiss();
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("inviteLink", inviteLink);
			SettingActivity.this.pushActivity(InviteFriendActivity.class,
					params);
		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			dismiss();

			if (responseResult.getStatusCode() == -1) {
				MyToast.show(SettingActivity.this,
						R.string.cannot_connet_server, Toast.LENGTH_SHORT);
			} else {
				MyToast.show(SettingActivity.this, R.string.server_error,
						Toast.LENGTH_SHORT);
			}
		}
	};

	private OnClickListener getPswListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// getPSWPopupWindow = new GetPSWPopupWindow(
			// R.layout.get_psw_popupwindow_layout,
			// LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			// getPSWPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
			pushActivity(AccountForgetPSWActivity.class);
		}

	};
	// change to another account
	private OnClickListener changeAccountListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(SettingActivity.this,
					AccountSettingActivity.class);
			intent.putExtra(TITLE_NAME,
					getString(R.string.change_account_title));
			intent.putExtra("firstLogin", false);
			startActivity(intent);
		}

	};

	private OnClickListener inviteFriendListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			inviteFriend(null);
		}

	};
	// charging money to account
	private OnClickListener chargeAccountListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			SettingActivity.this.pushActivity(AccountChargeActivity.class);
		}

	};

	private OnClickListener modifyPSWListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			modifyPSWPopupWindow = new ModifyPSWPopupWindow(
					R.layout.modify_psw_popupwindow_layout,
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			modifyPSWPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
		}

	};

	private void dismiss() {
		if (progressDialog != null)
			progressDialog.dismiss();
	}

	private void updatePreference(String key, String psw) {
		if (UserManager.getInstance().getUser().isRememberPwd()) {
			// Log.d("update", "update!");
			DataStorageUtils.putObject(User.password.name(), psw);
			DataStorageUtils.putObject(User.userkey.name(), key);
		}
	}

	private void modifyPSW(String oldpsw, String newpsw, String confirm) {
		if (oldpsw == null || oldpsw.equals("")) {
			MyToast.show(this, R.string.old_psw_not_null, Toast.LENGTH_SHORT);
			return;
		}

		if (newpsw == null || confirm == null || newpsw.equals("")
				|| confirm.equals("")) {
			MyToast.show(this, R.string.new_confirm_not_null,
					Toast.LENGTH_SHORT);
			return;
		}

		UserBean userBean = UserManager.getInstance().getUser();
		String oldmd5 = StringUtils.md5(oldpsw);
		String countrycode = (String) userBean.getValue(TelUser.countryCode
				.name());
		String username = userBean.getName();
		if (!newpsw.equals(confirm)) {
			MyToast.show(this, R.string.new_confirm_not_equal,
					Toast.LENGTH_SHORT);
			return;
		}

		progressDialog = ProgressDialog.show(this, null,
				getString(R.string.sending_request), true);
		// Log.d(SystemConstants.TAG,
		// oldmd5+":"+countrycode+":"+username+":"+newpsw+":"+confirm);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("username", username);
		params.put("countryCode", countrycode);
		params.put("oldPwd", oldmd5);
		params.put("newPwd", newpsw);
		params.put("newPwdConfirm", confirm);

		HttpUtils.postSignatureRequest(getString(R.string.server_url)
				+ getString(R.string.modify_psw_url),
				PostRequestFormat.URLENCODED, params, null,
				HttpRequestType.ASYNCHRONOUS, onFinishedModifyPsw);

	}

	private OnHttpRequestListener onFinishedModifyPsw = new OnHttpRequestListener() {

		@Override
		public void onFinished(HttpResponseResult responseResult) {
			dismiss();

			if (modifyPSWPopupWindow == null) {
				return;
			}
			try {
				JSONObject data = new JSONObject(
						responseResult.getResponseText());
				String userkey = data.getString("userkey");
				String newpsw = ((EditText) modifyPSWPopupWindow
						.getContentView().findViewById(R.id.new_psw_editText))
						.getEditableText().toString().trim();

				UserManager.getInstance().getUser().setUserKey(userkey);
				UserManager.getInstance().getUser()
						.setPassword(StringUtils.md5(newpsw));
				updatePreference(userkey, newpsw);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (modifyPSWPopupWindow != null)
				modifyPSWPopupWindow.dismiss();
			MyToast.show(SettingActivity.this, R.string.change_psw_success,
					Toast.LENGTH_SHORT);
		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			dismiss();
			int code = responseResult.getStatusCode();
			if (code == 401) {
				MyToast.show(SettingActivity.this, R.string.auth_not_pass,
						Toast.LENGTH_SHORT);
			} else if (code == 400) {
				MyToast.show(SettingActivity.this, R.string.new_psw_error,
						Toast.LENGTH_SHORT);
			} else if (code == 500) {
				MyToast.show(SettingActivity.this, R.string.server_error,
						Toast.LENGTH_SHORT);
			}
		}
	};

	private void getPSW(String phone) {
		String countryCode = (String) (UserManager.getInstance().getUser())
				.getValue(TelUser.countryCode.name());

		progressDialog = ProgressDialog.show(this, null,
				getString(R.string.sending_request), true);

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("username", phone);
		params.put("countryCode", countryCode);
		HttpUtils.postRequest(getString(R.string.server_url)
				+ getString(R.string.getpsw_url), PostRequestFormat.URLENCODED,
				params, null, HttpRequestType.ASYNCHRONOUS, onFinishGetPSW);
	}

	private void setDialCountryCode(String dialcountryCode) {
		UserBean telUserBean = UserManager.getInstance().getUser();
		telUserBean.setValue(TelUser.dialCountryCode.name(), dialcountryCode);
		DataStorageUtils.putObject(TelUser.dialCountryCode.name(),
				dialcountryCode);
	}

	private OnHttpRequestListener onFinishGetPSW = new OnHttpRequestListener() {

		@Override
		public void onFinished(HttpResponseResult responseResult) {
			// TODO Auto-generated method stub
			dismiss();
			int result = responseResult.getStatusCode();

			if (result == 200 || result == 201) {
				builder.setMessage(R.string.get_psw_finish);
				dialog = builder.create();
				dialog.show();
				if (getPSWPopupWindow != null)
					getPSWPopupWindow.dismiss();
			}
		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			// TODO Auto-generated method stub
			// Log.d(SystemConstants.TAG, responseResult.getStatusCode()+"");
			dismiss();
			MyToast.show(SettingActivity.this, R.string.phone_number_not_exist,
					Toast.LENGTH_SHORT);
		}

	};

	private void saveSetupSetting() {
		int launchPatternId = this.launchGroup.getCheckedRadioButtonId();

		if (launchPatternId == R.id.auto_launch_rbtn) {
			DataStorageUtils.putObject(LaunchSetting.autoLaunch.name(),
					"autoLaunch");
		} else if (launchPatternId == R.id.manual_launch_rbtn) {
			DataStorageUtils.putObject(LaunchSetting.autoLaunch.name(), "");
		}

		int loginPatternId = this.loginGroup.getCheckedRadioButtonId();
		UserBean userBean = UserManager.getInstance().getUser();

		if (loginPatternId == R.id.auto_login_rbtn) {
			if (!userBean.isRememberPwd()) {
				userBean.setRememberPwd(true);
				DataStorageUtils.putObject(User.password.name(),
						userBean.getPassword());
				DataStorageUtils.putObject(TelUser.vosphone.name(),
						userBean.getValue(TelUser.vosphone.name()));
				DataStorageUtils.putObject(TelUser.vosphone_pwd.name(),
						userBean.getValue(TelUser.vosphone_pwd.name()));
				DataStorageUtils.putObject(User.userkey.name(),
						userBean.getUserKey());
			}
		} else if (loginPatternId == R.id.manual_login_rbtn) {
			userBean.setRememberPwd(false);
			DataStorageUtils.putObject(User.password.name(), "");
			DataStorageUtils.putObject(TelUser.vosphone.name(), "");
			DataStorageUtils.putObject(TelUser.vosphone_pwd.name(), "");
			DataStorageUtils.putObject(User.userkey.name(), "");
		}
	}

	// 拨打设置
	private void saveDialPreference() {
		int dialPatternId = this.dialGroup.getCheckedRadioButtonId();
		int answerPatternId = this.answerGroup.getCheckedRadioButtonId();

		SipCallModeSelectPattern dialPattern = getDialPattern(dialPatternId);
		SipCallModeSelector.setSipCallModeSelectPattern(dialPattern);

		String answerPattern = getAnswerPattern(answerPatternId);
		
		DialPreferenceBean dialBean = DialPreferenceManager.getInstance()
				.getDialPreferenceBean();
		dialBean.setAnswerPattern(answerPattern);
		
		DataStorageUtils.putObject(
				DialPreference.DialSetting.answerPattern.name(), answerPattern);
	}

	private SipCallModeSelectPattern getDialPattern(int id) {
		SipCallModeSelectPattern result = SipCallModeSelectPattern.MANUAL;
		switch (id) {
		case R.id.radio_direct_btn:
			result = SipCallModeSelectPattern.DIRECT_CALL;
			break;
		case R.id.radio_back_btn:
			result = SipCallModeSelectPattern.CALLBACK;
			break;
		case R.id.radio_manual_btn:
			result = SipCallModeSelectPattern.MANUAL;
			break;
		case R.id.radio_auto_select_call_mode_btn:
			result = SipCallModeSelectPattern.AUTO;
		}
		return result;
	}

	private String getAnswerPattern(int id) {
		String result = "";
		switch (id) {
		case R.id.answer_auto_btn:
			result = DialPreference.AUTO_ANSWER;
			break;
		case R.id.answer_manual_btn:
			result = DialPreference.MANUAL_ANSWER;
			break;
		}
		return result;
	}

	class ModifyPSWPopupWindow extends CTPopupWindow {

		public ModifyPSWPopupWindow(int resource, int width, int height,
				boolean focusable, boolean isBindDefListener) {
			super(resource, width, height, focusable, isBindDefListener);
		}

		public ModifyPSWPopupWindow(int resource, int width, int height) {
			super(resource, width, height);
		}

		@Override
		protected void bindPopupWindowComponentsListener() {

			// bind contact phone select cancel button click listener
			((Button) getContentView().findViewById(R.id.modify_psw_confirmBtn))
					.setOnClickListener(new ModifyPSWConfirmBtnOnClickListener());
			((Button) getContentView().findViewById(R.id.modify_psw_cancelBtn))
					.setOnClickListener(new ModifyPSWCancelBtnOnClickListener());
		}

		@Override
		protected void resetPopupWindow() {
			// hide contact phones select phone list view
			((EditText) getContentView().findViewById(R.id.old_psw_editText))
					.setText("");
			((EditText) getContentView().findViewById(R.id.new_psw_editText))
					.setText("");
			((EditText) getContentView()
					.findViewById(R.id.confirm_psw_editText)).setText("");
		}

		// inner class
		// contact phone select phone button on click listener
		class ModifyPSWConfirmBtnOnClickListener implements OnClickListener {

			@Override
			public void onClick(View v) {
				// dismiss contact phone select popup window
				String oldpsw = ((EditText) getContentView().findViewById(
						R.id.old_psw_editText)).getEditableText().toString()
						.trim();
				String newpsw = ((EditText) getContentView().findViewById(
						R.id.new_psw_editText)).getEditableText().toString()
						.trim();
				String confirm = ((EditText) getContentView().findViewById(
						R.id.confirm_psw_editText)).getEditableText()
						.toString().trim();

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(((EditText) getContentView()
						.findViewById(R.id.old_psw_editText)).getWindowToken(),
						0);
				modifyPSW(oldpsw, newpsw, confirm);
			}

		}

		// contact phone select cancel button on click listener
		class ModifyPSWCancelBtnOnClickListener implements OnClickListener {

			@Override
			public void onClick(View v) {
				// dismiss contact phone select popup window
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(((EditText) getContentView()
						.findViewById(R.id.old_psw_editText)).getWindowToken(),
						0);
				dismiss();
			}

		}

	}

	class GetPSWPopupWindow extends CTPopupWindow {

		public GetPSWPopupWindow(int resource, int width, int height,
				boolean focusable, boolean isBindDefListener) {
			super(resource, width, height, focusable, isBindDefListener);
		}

		public GetPSWPopupWindow(int resource, int width, int height) {
			super(resource, width, height);
		}

		@Override
		protected void bindPopupWindowComponentsListener() {

			// bind contact phone select cancel button click listener
			((Button) getContentView().findViewById(R.id.get_psw_confirmBtn))
					.setOnClickListener(new GetPSWConfirmBtnOnClickListener());
			((Button) getContentView().findViewById(R.id.get_psw_cancelBtn))
					.setOnClickListener(new GetPSWCancelBtnOnClickListener());
		}

		@Override
		protected void resetPopupWindow() {
			// hide contact phones select phone list view
			((EditText) getContentView().findViewById(R.id.get_psw_editText))
					.setText("");
		}

		// inner class
		// contact phone select phone button on click listener
		class GetPSWConfirmBtnOnClickListener implements OnClickListener {

			@Override
			public void onClick(View v) {
				// dismiss contact phone select popup window
				String phone = ((EditText) getContentView().findViewById(
						R.id.get_psw_editText)).getEditableText().toString()
						.trim();

				if (!phone.matches("(^[0-9]*)")) {
					MyToast.show(SettingActivity.this,
							R.string.phone_wrong_format, Toast.LENGTH_SHORT);
					return;
				}
				if (phone == null || phone.equals("")) {
					MyToast.show(SettingActivity.this,
							R.string.number_cannot_be_null, Toast.LENGTH_SHORT);
					return;
				}
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(((EditText) getContentView()
						.findViewById(R.id.get_psw_editText)).getWindowToken(),
						0);
				getPSW(phone);
			}

		}

		// contact phone select cancel button on click listener
		class GetPSWCancelBtnOnClickListener implements OnClickListener {

			@Override
			public void onClick(View v) {
				// dismiss contact phone select popup window
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(((EditText) getContentView()
						.findViewById(R.id.get_psw_editText)).getWindowToken(),
						0);
				dismiss();
			}

		}

	}

	class LaunchSetCodePopupWindow extends CTPopupWindow {

		public LaunchSetCodePopupWindow(int resource, int width, int height,
				boolean focusable, boolean isBindDefListener) {
			super(resource, width, height, focusable, isBindDefListener);
		}

		public LaunchSetCodePopupWindow(int resource, int width, int height) {
			super(resource, width, height);
			UserBean user = UserManager.getInstance().getUser();
			if (user.isRememberPwd()) {
				((RadioButton) getContentView().findViewById(
						R.id.auto_login_rbtn)).setChecked(true);
			} else
				((RadioButton) getContentView().findViewById(
						R.id.manual_login_rbtn)).setChecked(true);

			String auto = DataStorageUtils.getString(LaunchSetting.autoLaunch
					.name());
			if (auto != null && auto.equals("autoLaunch")) {
				((RadioButton) getContentView().findViewById(
						R.id.auto_launch_rbtn)).setChecked(true);
			} else
				((RadioButton) getContentView().findViewById(
						R.id.manual_launch_rbtn)).setChecked(true);
		}

		@Override
		protected void bindPopupWindowComponentsListener() {

			// bind contact phone select cancel button click listener
			((Button) getContentView().findViewById(R.id.setup_confirmBtn))
					.setOnClickListener(new LaunchSetConfirmBtnOnClickListener());
			((Button) getContentView().findViewById(R.id.setup_cancelBtn))
					.setOnClickListener(new LaunchSetCancelBtnOnClickListener());
		}

		@Override
		protected void resetPopupWindow() {
			// hide contact phones select phone list view
		}

		// inner class
		// contact phone select phone button on click listener
		class LaunchSetConfirmBtnOnClickListener implements OnClickListener {

			@Override
			public void onClick(View v) {
				// dismiss contact phone select popup window
				saveSetupSetting();
				dismiss();
			}

		}

		// contact phone select cancel button on click listener
		class LaunchSetCancelBtnOnClickListener implements OnClickListener {

			@Override
			public void onClick(View v) {
				// dismiss contact phone select popup window
				dismiss();
			}

		}

	}

	class SetDialCountryCodePopupWindow extends CTPopupWindow {
		private int lastDialCountryCodeSelect = 0;

		public SetDialCountryCodePopupWindow(int resource, int width,
				int height, boolean focusable, boolean isBindDefListener) {
			super(resource, width, height, focusable, isBindDefListener);
		}

		public SetDialCountryCodePopupWindow(int resource, int width, int height) {
			super(resource, width, height);
			UserBean telUser = UserManager.getInstance().getUser();
			Log.d(SystemConstants.TAG,
					"SetDialCountryCodePopupWindow - userbean: "
							+ telUser.toString());

			String dialcountrycode = (String) telUser
					.getValue(TelUser.dialCountryCode.name());
			int dialCountryIndex = countryCodeManager
					.getCountryIndex(dialcountrycode);
			lastDialCountryCodeSelect = dialCountryIndex;
			((Button) (this.getContentView()
					.findViewById(R.id.set_dial_country_btn)))
					.setText(countryCodeManager
							.getCountryName(dialCountryIndex));
		}

		@Override
		protected void bindPopupWindowComponentsListener() {

			// bind contact phone select cancel button click listener
			((Button) getContentView().findViewById(
					R.id.set_countrycode_confirmBtn))
					.setOnClickListener(new SetDialCountryCodeConfirmBtnOnClickListener());
			((Button) getContentView().findViewById(
					R.id.set_countrycode_cancelBtn))
					.setOnClickListener(new SetDialCountryCodeCancelBtnOnClickListener());
			((Button) (this.getContentView()
					.findViewById(R.id.set_dial_country_btn)))
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							chooseCountry(v);
						}
					});
		}

		public void chooseCountry(View v) {
			AlertDialog.Builder chooseCountryDialogBuilder = new AlertDialog.Builder(
					SettingActivity.this);
			chooseCountryDialogBuilder.setTitle(R.string.countrycode_list);
			chooseCountryDialogBuilder.setSingleChoiceItems(
					countryCodeManager.getCountryNameList(),
					lastDialCountryCodeSelect, new chooseCountryListener());
			chooseCountryDialogBuilder.setNegativeButton(R.string.cancel, null);
			chooseCountryDialog = chooseCountryDialogBuilder.create();
			chooseCountryDialog.show();
		}

		class chooseCountryListener implements DialogInterface.OnClickListener {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				lastDialCountryCodeSelect = which;
				((Button) (SetDialCountryCodePopupWindow.this.getContentView()
						.findViewById(R.id.set_dial_country_btn)))
						.setText(countryCodeManager.getCountryName(which));
				chooseCountryDialog.dismiss();
			}
		}

		@Override
		protected void resetPopupWindow() {
			// hide contact phones select phone list view
		}

		public void setSelectDialCountryCode(int s) {
			lastDialCountryCodeSelect = s;
		}

		// inner class
		// contact phone select phone button on click listener
		class SetDialCountryCodeConfirmBtnOnClickListener implements
				OnClickListener {

			@Override
			public void onClick(View v) {
				// dismiss contact phone select popup window
				String dialcountrycode = countryCodeManager
						.getCountryCode(((Button) SetDialCountryCodePopupWindow.this
								.getContentView().findViewById(
										R.id.set_dial_country_btn)).getText()
								.toString().trim());

				Log.d("dialcountrycode", dialcountrycode);
				setDialCountryCode(dialcountrycode);
				dismiss();
			}

		}

		// contact phone select cancel button on click listener
		class SetDialCountryCodeCancelBtnOnClickListener implements
				OnClickListener {

			@Override
			public void onClick(View v) {
				// dismiss contact phone select popup window
				dismiss();
			}

		}

	}

	class SetDialPreferencePopupWindow extends CTPopupWindow {

		public SetDialPreferencePopupWindow(int resource, int width,
				int height, boolean focusable, boolean isBindDefListener) {
			super(resource, width, height, focusable, isBindDefListener);
		}

		public SetDialPreferencePopupWindow(int resource, int width, int height) {
			super(resource, width, height);
			// DialPreferenceBean dialBean = DialPreferenceManager.getInstance()
			// .getDialPreferenceBean();
			// String dialPattern = dialBean.getDialPattern();
			// String answerPattern = dialBean.getAnswerPattern();

			// Log.d("Setting Dial Preference", dialPattern+":"+answerPattern);

			initUI();

			// if (answerPattern != null) {
			// if (answerPattern.equals(DialPreference.AUTO_ANSWER)) {
			// ((RadioButton) getContentView().findViewById(
			// R.id.answer_auto_btn)).setChecked(true);
			// } else if (answerPattern.equals(DialPreference.MANUAL_ANSWER)) {
			// ((RadioButton) getContentView().findViewById(
			// R.id.answer_manual_btn)).setChecked(true);
			// }
			// }
		}
		
		private void initUI() {
			SipCallModeSelectPattern callMode = SipCallModeSelector
					.getSipCallModeSelectPattern();

			switch (callMode) {
			case DIRECT_CALL:
				((RadioButton) getContentView().findViewById(
						R.id.radio_direct_btn)).setChecked(true);
				break;
			case CALLBACK:
				((RadioButton) getContentView().findViewById(
						R.id.radio_back_btn)).setChecked(true);
				break;

			case AUTO:
				((RadioButton) getContentView().findViewById(
						R.id.radio_auto_select_call_mode_btn)).setChecked(true);
				break;

			case MANUAL:
			default:
				((RadioButton) getContentView().findViewById(
						R.id.radio_manual_btn)).setChecked(true);
				break;
			}

		}

		@Override
		protected void bindPopupWindowComponentsListener() {

			// bind contact phone select cancel button click listener
			((Button) getContentView().findViewById(
					R.id.dial_preference_confirmBtn))
					.setOnClickListener(new SetConfirmBtnOnClickListener());

			((Button) getContentView().findViewById(
					R.id.dial_preference_cancelBtn))
					.setOnClickListener(new SetCancelBtnOnClickListener());
		}

		@Override
		protected void resetPopupWindow() {
			initUI();
		}

		// inner class
		// contact phone select phone button on click listener
		class SetConfirmBtnOnClickListener implements OnClickListener {

			@Override
			public void onClick(View v) {
				// dismiss contact phone select popup window
				saveDialPreference();
				dismiss();
			}

		}

		// contact phone select cancel button on click listener
		class SetCancelBtnOnClickListener implements OnClickListener {
			@Override
			public void onClick(View v) {
				// dismiss contact phone select popup window
				dismiss();
			}
		}
	}

	class SetBindNumberPopupWindow extends CTPopupWindow {
		private int lastSelectCountryCode = 0;

		public SetBindNumberPopupWindow(int resource, int width, int height,
				boolean focusable, boolean isBindDefListener) {
			super(resource, width, height, focusable, isBindDefListener);
		}

		public SetBindNumberPopupWindow(int resource, int width, int height) {
			super(resource, width, height);
			((Button) (this.getContentView()
					.findViewById(R.id.setAuth_choose_country_btn)))
					.setText(countryCodeManager.getCountryName(0));
			// Log.d("Setting Dial Preference", dialPattern+":"+answerPattern);
		}

		@Override
		protected void bindPopupWindowComponentsListener() {
			// bind contact phone select cancel button click listener
			((Button) getContentView().findViewById(R.id.set_auth_confirmBtn))
					.setOnClickListener(new SetBindNumberConfirmBtnOnClickListener());
			((Button) getContentView().findViewById(R.id.set_auth_cancelBtn))
					.setOnClickListener(new SetBindNumberCancelBtnOnClickListener());
			((Button) (this.getContentView()
					.findViewById(R.id.setAuth_choose_country_btn)))
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							chooseCountry(v);
						}
					});
		}

		@Override
		protected void resetPopupWindow() {
			// hide contact phones select phone list view
			// ((EditText)(SetBindNumberPopupWindow.this.getContentView()
			// .findViewById(R.id.set_auth_number_editText))).setText("");
		}

		public void chooseCountry(View v) {
			AlertDialog.Builder chooseCountryDialogBuilder = new AlertDialog.Builder(
					SettingActivity.this);
			chooseCountryDialogBuilder.setTitle(R.string.countrycode_list);
			chooseCountryDialogBuilder.setSingleChoiceItems(
					countryCodeManager.getCountryNameList(),
					lastSelectCountryCode, new chooseCountryListener());
			chooseCountryDialogBuilder.setNegativeButton(R.string.cancel, null);
			chooseCountryDialog = chooseCountryDialogBuilder.create();
			chooseCountryDialog.show();
		}

		class chooseCountryListener implements DialogInterface.OnClickListener {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				lastSelectCountryCode = which;
				((Button) (SetBindNumberPopupWindow.this.getContentView()
						.findViewById(R.id.setAuth_choose_country_btn)))
						.setText(countryCodeManager.getCountryName(which));
				chooseCountryDialog.dismiss();
			}
		}

		// inner class
		// contact phone select phone button on click listener
		class SetBindNumberConfirmBtnOnClickListener implements OnClickListener {

			@Override
			public void onClick(View v) {
				// dismiss contact phone select popup window
				// saveDialPreference();
				String phone = ((EditText) (SetBindNumberPopupWindow.this
						.getContentView()
						.findViewById(R.id.set_auth_number_editText)))
						.getText().toString().trim();
				String countrycode = countryCodeManager
						.getCountryCode(((Button) SetBindNumberPopupWindow.this
								.getContentView().findViewById(
										R.id.setAuth_choose_country_btn))
								.getText().toString().trim());
				if (phone != null && !phone.equals("")) {
					setBindNumber(phone, countrycode);
					dismiss();
				} else {
					MyToast.show(SettingActivity.this, R.string.phone_not_null,
							Toast.LENGTH_SHORT);
				}
			}
		}

		// contact phone select cancel button on click listener
		class SetBindNumberCancelBtnOnClickListener implements OnClickListener {
			@Override
			public void onClick(View v) {
				// dismiss contact phone select popup window
				dismiss();
			}
		}
	}

	private void setBindNumber(String phone, String country) {
		progressDialog = ProgressDialog.show(this, null,
				getString(R.string.sending_request), true);
		UserBean userBean = UserManager.getInstance().getUser();
		String username = userBean.getName();
		String countryCode = (String) userBean.getValue(TelUser.countryCode
				.name());
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("username", username);
		params.put("countryCode", countryCode);
		params.put("bindphone_country_code", country);
		params.put("bindphone", phone);
		HttpUtils.postSignatureRequest(getString(R.string.server_url)
				+ getString(R.string.setBindPhone),
				PostRequestFormat.URLENCODED, params, null,
				HttpRequestType.ASYNCHRONOUS, onFinishedSetBindPhone);
	}

	private OnHttpRequestListener onFinishedSetBindPhone = new OnHttpRequestListener() {

		@Override
		public void onFinished(HttpResponseResult responseResult) {
			MyToast.show(SettingActivity.this,
					R.string.set_bind_number_success, Toast.LENGTH_SHORT);
			dismiss();
		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			dismiss();
			MyToast.show(SettingActivity.this, R.string.server_error,
					Toast.LENGTH_SHORT);
		}
	};

	public void onClickMySuiteAction(View v) {
		pushActivity(MySuitesActivity.class);
	}

	public void onClickAboutAction(View v) {
		pushActivity(AboutActivity.class);
	}

	public void onClickViewNoticeAction(View v) {
		pushActivity(NoticeViewActivity.class);
	}

	public void onClickFeeQueryAction(View v) {
		pushActivity(FeeActivity.class);
	}

	public void onEmailSettingButtonClick(View v) {
		pushActivity(EmailSettingActivity.class);
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
