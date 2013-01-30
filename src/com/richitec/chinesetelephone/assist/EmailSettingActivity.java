package com.richitec.chinesetelephone.assist;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.constant.EmailStatus;
import com.richitec.chinesetelephone.constant.TelUser;
import com.richitec.chinesetelephone.utils.AppDataSaveRestoreUtil;
import com.richitec.commontoolkit.activityextension.NavigationActivity;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.HttpUtils;
import com.richitec.commontoolkit.utils.HttpUtils.HttpRequestType;
import com.richitec.commontoolkit.utils.HttpUtils.HttpResponseResult;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;
import com.richitec.commontoolkit.utils.HttpUtils.PostRequestFormat;
import com.richitec.commontoolkit.utils.MyToast;
import com.richitec.commontoolkit.utils.ValidatePattern;

public class EmailSettingActivity extends NavigationActivity {
	private ProgressDialog progressDlg;
	private EditText emailInputET;
	private TextView emailStatusTV;
	private TextView descTV;
	private Button bindButton;
	private Button modifyButton;
	private Button mailSendButton;
	private Double frozenMoney = 0.0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			AppDataSaveRestoreUtil.onRestoreInstanceState(savedInstanceState);
		}

		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.activity_email_setting_layout);

		// set title text
		setTitle(R.string.email_setting_title);

		emailInputET = (EditText) findViewById(R.id.email_bind_input_et);
		emailStatusTV = (TextView) findViewById(R.id.email_status_tv);
		descTV = (TextView) findViewById(R.id.email_bind_desc_tv);
		bindButton = (Button) findViewById(R.id.email_bind_bt);
		modifyButton = (Button) findViewById(R.id.email_modify_bt);
		mailSendButton = (Button) findViewById(R.id.email_send_bt);
		getAccountInfo();
	}

	private void getAccountInfo() {
		progressDlg = ProgressDialog.show(this, null,
				getString(R.string.getting_binded_email));
		UserBean user = UserManager.getInstance().getUser();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(TelUser.countryCode.name(),
				(String) user.getValue(TelUser.countryCode.name()));
		HttpUtils.getSignatureRequest(getString(R.string.server_url)
				+ getString(R.string.getAccountInfo_url), params, null,
				HttpRequestType.ASYNCHRONOUS, onFinishedGetAccountInfo);
	}

	private OnHttpRequestListener onFinishedGetAccountInfo = new OnHttpRequestListener() {

		@Override
		public void onFinished(HttpResponseResult responseResult) {
			dismissProgressDlg();
			try {
				JSONObject data = new JSONObject(
						responseResult.getResponseText());
				String email = data.getString("email");
				String emailStatus = data.getString("email_status");
				frozenMoney = data.getDouble("frozen_money");

				if (email == null || email.equals("")) {
					emailInputET.setText("");
					enableEmailEditText();
					if (frozenMoney > 0) {
						// alert that user can gain money by binding email
						descTV.setText(String
								.format(getString(R.string.you_havent_binded_email_now_you_can_gain_money),
										frozenMoney));

					} else {
						// alert user to bind email
						descTV.setText(R.string.you_havent_binded_email_wanna_bind);
					}
				} else {
					emailInputET.setText(email);
					disableEmailEditText();
					if (EmailStatus.verified.name().equals(emailStatus)) {
						emailStatusTV.setText(R.string.email_verified);
						emailStatusTV.setTextColor(getResources().getColor(R.color.dodger_blue));
					} else {
						emailStatusTV.setText(R.string.email_unverified);
						emailStatusTV.setTextColor(getResources().getColor(R.color.red));
					}

					if (frozenMoney > 0) {
						descTV.setText(String
								.format(getString(R.string.click_send_money_gain_to_send_mail),
										frozenMoney));
						mailSendButton.setVisibility(View.VISIBLE);
					} else {
						descTV.setText(R.string.email_modify_desc);
						mailSendButton.setVisibility(View.GONE);
					}
				}

			} catch (JSONException e) {
				e.printStackTrace();
				MyToast.show(EmailSettingActivity.this,
						R.string.failed_to_get_email_info, Toast.LENGTH_SHORT);
			}
		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			dismissProgressDlg();
			switch (responseResult.getStatusCode()) {
			case -1:
				MyToast.show(EmailSettingActivity.this,
						R.string.cannot_connet_server, Toast.LENGTH_SHORT);
				break;

			default:
				MyToast.show(EmailSettingActivity.this,
						R.string.failed_to_get_email_info, Toast.LENGTH_SHORT);
				break;
			}
		}
	};

	private void enableEmailEditText() {
		emailInputET.setEnabled(true);
		emailInputET.setFocusable(true);
		emailInputET.setFocusableInTouchMode(true);
		emailInputET.setBackgroundResource(R.drawable.textfield_selector);
		bindButton.setVisibility(View.VISIBLE);
		modifyButton.setVisibility(View.GONE);
		emailStatusTV.setVisibility(View.GONE);
	}

	private void disableEmailEditText() {
		emailInputET.setEnabled(false);
		emailInputET.setFocusable(false);
		emailInputET.setBackgroundResource(R.drawable.bg_edityzm);
		bindButton.setVisibility(View.GONE);
		modifyButton.setVisibility(View.VISIBLE);
		emailStatusTV.setVisibility(View.VISIBLE);
	}

	private void dismissProgressDlg() {
		if (progressDlg != null) {
			progressDlg.dismiss();
		}
	}

	public void onModifyButtonClick(View v) {
		enableEmailEditText();
		mailSendButton.setVisibility(View.GONE);
		if (frozenMoney > 0) {

		} else {
			descTV.setText(R.string.email_modify_desc);
		}
	}

	public void onSaveButtonClick(View v) {
		hideSoftKeyboard();
		String email = emailInputET.getText().toString().trim();
		if (!ValidatePattern.isValidEmail(email)) {
			MyToast.show(EmailSettingActivity.this,
					R.string.pls_input_valid_email_address, Toast.LENGTH_SHORT);
		} else {
			progressDlg = ProgressDialog.show(EmailSettingActivity.this, null,
					getString(R.string.binding_email_address));
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("email", email);
			UserBean user = UserManager.getInstance().getUser();
			params.put("countryCode",
					(String) user.getValue(TelUser.countryCode.name()));
			HttpUtils.postSignatureRequest(getString(R.string.server_url)
					+ getString(R.string.setEmail_url),
					PostRequestFormat.URLENCODED, params, null,
					HttpRequestType.ASYNCHRONOUS, onFinishedSetEmail);
		}
	}

	private OnHttpRequestListener onFinishedSetEmail = new OnHttpRequestListener() {

		@Override
		public void onFinished(HttpResponseResult responseResult) {
			dismissProgressDlg();
			try {
				JSONObject data = new JSONObject(
						responseResult.getResponseText());
				String result = data.getString("result");
				if ("money gain mail send ok".equals(result)) {
					new AlertDialog.Builder(EmailSettingActivity.this)
							.setTitle(R.string.alert_title)
							.setMessage(
									R.string.money_get_mail_send_ok_check_ur_mail)
							.setPositiveButton(R.string.Ensure, dlgOkClick)
							.show();

				} else if ("money gain mail send failed".equals(result)) {
					new AlertDialog.Builder(EmailSettingActivity.this)
							.setTitle(R.string.alert_title)
							.setMessage(R.string.bind_email_success)
							.setPositiveButton(R.string.Ensure, dlgOkClick)
							.show();
				} else if ("address verify mail send ok".equals(result)) {
					new AlertDialog.Builder(EmailSettingActivity.this)
							.setTitle(R.string.alert_title)
							.setMessage(R.string.verify_mail_send_ok)
							.setPositiveButton(R.string.Ensure, dlgOkClick)
							.show();
				} else if ("address verify mail send failed".equals(result)) {
					new AlertDialog.Builder(EmailSettingActivity.this)
							.setTitle(R.string.alert_title)
							.setMessage(R.string.bind_email_success)
							.setPositiveButton(R.string.Ensure, dlgOkClick)
							.show();
				} else if ("email is already binded by others".equals(result)) {
					MyToast.show(EmailSettingActivity.this,
							R.string.email_already_binded, Toast.LENGTH_SHORT);
				} else {
					bindError();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				bindError();
			}

		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			dismissProgressDlg();
			bindError();
		}

		private void bindError() {
			MyToast.show(EmailSettingActivity.this, R.string.bind_email_failed,
					Toast.LENGTH_SHORT);
		}

		private DialogInterface.OnClickListener dlgOkClick = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				getAccountInfo();
			}
		};
	};

	public void onSendMoneyGainMailButtonClick(View v) {
		progressDlg = ProgressDialog.show(EmailSettingActivity.this, null,
				getString(R.string.sending_mail));
		HashMap<String, String> params = new HashMap<String, String>();
		UserBean user = UserManager.getInstance().getUser();
		params.put("countryCode",
				(String) user.getValue(TelUser.countryCode.name()));
		HttpUtils.postSignatureRequest(getString(R.string.server_url)
				+ getString(R.string.sendActivateFrozenMoneyMail_url),
				PostRequestFormat.URLENCODED, params, null,
				HttpRequestType.ASYNCHRONOUS, onFinishedSendMoneyGainMail);
	}

	private OnHttpRequestListener onFinishedSendMoneyGainMail = new OnHttpRequestListener() {

		@Override
		public void onFinished(HttpResponseResult responseResult) {
			dismissProgressDlg();
			new AlertDialog.Builder(EmailSettingActivity.this)
					.setTitle(R.string.alert_title)
					.setMessage(R.string.mail_send_ok)
					.setPositiveButton(R.string.Ensure, null).show();
		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			dismissProgressDlg();

			if (responseResult.getStatusCode() == -1) {
				MyToast.show(EmailSettingActivity.this,
						R.string.cannot_connet_server, Toast.LENGTH_SHORT);
			} else {
				MyToast.show(EmailSettingActivity.this,
						R.string.mail_send_failed, Toast.LENGTH_SHORT);
			}
		}
	};

	private void hideSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.hideSoftInputFromWindow(emailInputET.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		AppDataSaveRestoreUtil.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}
}
