package com.richitec.chinesetelephone.account;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.utils.AppDataSaveRestoreUtil;
import com.richitec.chinesetelephone.utils.CountryCodeManager;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.HttpUtils;
import com.richitec.commontoolkit.utils.HttpUtils.HttpRequestType;
import com.richitec.commontoolkit.utils.HttpUtils.HttpResponseResult;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;
import com.richitec.commontoolkit.utils.HttpUtils.PostRequestFormat;
import com.richitec.commontoolkit.utils.MyToast;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AccountForgetPSWActivity extends Activity {
	private AlertDialog chooseCountryDialog;
	private int lastSelectCountryCode = 0;
	private ProgressDialog progressDlg;
	private CountryCodeManager countryCodeManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_forget_psw);

		countryCodeManager = CountryCodeManager.getInstance();

	}

	public void chooseCountry(View v) {
		AlertDialog.Builder chooseCountryDialogBuilder = new AlertDialog.Builder(
				this);
		chooseCountryDialogBuilder.setTitle(R.string.countrycode_list);
		chooseCountryDialogBuilder.setSingleChoiceItems(
				countryCodeManager.getCountryNameList(), lastSelectCountryCode,
				new chooseCountryListener());
		chooseCountryDialogBuilder.setNegativeButton(R.string.cancel, null);
		chooseCountryDialog = chooseCountryDialogBuilder.create();
		chooseCountryDialog.show();
	}

	class chooseCountryListener implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			lastSelectCountryCode = which;
			((Button) (AccountForgetPSWActivity.this
					.findViewById(R.id.getpsw_choose_country_btn)))
					.setText(countryCodeManager.getCountryName(which));
			chooseCountryDialog.dismiss();
		}

	}

	public void onCancelBtnClick(View v) {
		hideSoftKeyboard();
		finish();
	}

	private void hideSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public void onGetPSWAction(View v) {
		hideSoftKeyboard();

		EditText phoneEdit = (EditText) findViewById(R.id.get_phone_editText);
		String countryCode = countryCodeManager
				.getCountryCode(((Button) findViewById(R.id.getpsw_choose_country_btn))
						.getText().toString().trim());

		if (countryCode == null) {
			MyToast.show(this, R.string.pls_select_country, Toast.LENGTH_SHORT);
			return;
		}

		String phone = phoneEdit.getEditableText().toString().trim();

		if (phone == null || phone.equals("")) {
			MyToast.show(this, R.string.number_cannot_be_null,
					Toast.LENGTH_SHORT);
			return;
		}

		if (!phone.matches("(^[0-9]*)")) {
			MyToast.show(this, R.string.phone_wrong_format, Toast.LENGTH_SHORT);
			return;
		}

		// Log.d(SystemConstants.TAG, phone+":"+countryCode);

		progressDlg = ProgressDialog.show(this, null,
				getString(R.string.sending_pwd_reset_mail));

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("username", phone);
		params.put("countryCode", countryCode);

		HttpUtils.postRequest(getString(R.string.server_url)
				+ getString(R.string.sendResetPwdEmail_url),
				PostRequestFormat.URLENCODED, params, null,
				HttpRequestType.ASYNCHRONOUS, onFinishSendResetPwdMail);
	}

	private void dismiss() {
		if (progressDlg != null)
			progressDlg.dismiss();
	}

	private OnHttpRequestListener onFinishSendResetPwdMail = new OnHttpRequestListener() {

		@Override
		public void onFinished(HttpResponseResult responseResult) {
			// TODO Auto-generated method stub
			dismiss();
			try {
				JSONObject data = new JSONObject(
						responseResult.getResponseText());
				String result = data.getString("result");
				if ("send_ok".equals(result)) {
					String email = data.getString("email");
					new AlertDialog.Builder(AccountForgetPSWActivity.this)
							.setTitle(R.string.alert_title)
							.setMessage(
									String.format(
											getString(R.string.pwd_reset_mail_send_ok),
											email))
							.setPositiveButton(R.string.Ensure, null).show();
				} else if ("send_failed".equals(result)) {
					MyToast.show(AccountForgetPSWActivity.this,
							R.string.pwd_reset_mail_send_failed, Toast.LENGTH_SHORT);
				} else if ("email_not_set".equals(result)) {
					MyToast.show(AccountForgetPSWActivity.this,
							R.string.you_havnt_bind_email, Toast.LENGTH_SHORT);
				} else if ("email_unverify".equals(result)) {
					String email = data.getString("email");
					new AlertDialog.Builder(AccountForgetPSWActivity.this)
					.setTitle(R.string.alert_title)
					.setMessage(
							String.format(
									getString(R.string.you_havnt_verify_email),
									email))
					.setPositiveButton(R.string.Ensure, null).show();
				} else if ("user_not_found".equals(result)) {
					MyToast.show(AccountForgetPSWActivity.this,
							R.string.user_not_found, Toast.LENGTH_SHORT);
				}

			} catch (JSONException e) {
				e.printStackTrace();
				MyToast.show(AccountForgetPSWActivity.this,
						R.string.pwd_reset_mail_send_failed, Toast.LENGTH_SHORT);
			}
		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			dismiss();
			if (responseResult.getStatusCode() == -1) {
				MyToast.show(AccountForgetPSWActivity.this,
						R.string.cannot_connet_server, Toast.LENGTH_SHORT);
			} else {
				MyToast.show(AccountForgetPSWActivity.this,
						R.string.pwd_reset_mail_send_failed, Toast.LENGTH_SHORT);
			}
		}

	};

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
