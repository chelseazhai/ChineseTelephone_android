package com.richitec.chinesetelephone.account;

import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.constant.SystemConstants;
import com.richitec.chinesetelephone.utils.AppDataSaveRestoreUtil;
import com.richitec.chinesetelephone.utils.CountryCodeManager;
import com.richitec.commontoolkit.utils.HttpUtils;
import com.richitec.commontoolkit.utils.HttpUtils.HttpRequestType;
import com.richitec.commontoolkit.utils.HttpUtils.HttpResponseResult;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;
import com.richitec.commontoolkit.utils.HttpUtils.PostRequestFormat;
import com.richitec.commontoolkit.utils.MyToast;

public class AccountDirectRegisterActivity extends Activity {
	private AlertDialog chooseCountryDialog;
	private int lastSelect = 0;
	CountryCodeManager countryCodeManager;
	private ProgressDialog progressDlg;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.account_direct_regist);

		countryCodeManager = CountryCodeManager.getInstance();

		EditText phoneNumberET = (EditText) findViewById(R.id.regist_phone_edittext);
		EditText pwdET = (EditText) findViewById(R.id.regist_pwd_edittext);
		EditText pwd1ET = (EditText) findViewById(R.id.regist_pwd1_edittext);

		OnChangeEditTextBGListener list = new OnChangeEditTextBGListener();
		phoneNumberET.setOnFocusChangeListener(list);
		pwdET.setOnFocusChangeListener(list);
		pwd1ET.setOnFocusChangeListener(list);
	}

	public void chooseCountry(View v) {
		AlertDialog.Builder chooseCountryDialogBuilder = new AlertDialog.Builder(
				this);
		chooseCountryDialogBuilder.setTitle(R.string.countrycode_list);
		chooseCountryDialogBuilder.setSingleChoiceItems(
				countryCodeManager.getCountryNameList(), lastSelect,
				new ChooseCountryListener());
		chooseCountryDialogBuilder.setNegativeButton(R.string.cancel, null);
		chooseCountryDialog = chooseCountryDialogBuilder.create();
		chooseCountryDialog.show();
	}

	private void dismissProgressDlg() {
		if (progressDlg != null)
			progressDlg.dismiss();
	}

	class ChooseCountryListener implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			lastSelect = which;
			((Button) (AccountDirectRegisterActivity.this
					.findViewById(R.id.regist_choose_country_btn)))
					.setText(countryCodeManager.getCountryName(which));
			chooseCountryDialog.dismiss();
		}

	}

	public void onRegisterAction(View v) {
		EditText phoneNumberET = (EditText) findViewById(R.id.regist_phone_edittext);
		EditText pwdET = (EditText) findViewById(R.id.regist_pwd_edittext);
		EditText pwd1ET = (EditText) findViewById(R.id.regist_pwd1_edittext);
		Button countryCodeBt = (Button) findViewById(R.id.regist_choose_country_btn);

		String country = countryCodeBt.getText().toString().trim();
		Log.d(SystemConstants.TAG, "country: " + country);
		String countryCode = countryCodeManager.getCountryCode(country);
		if (countryCode == null) {
			MyToast.show(this, R.string.pls_select_country, Toast.LENGTH_SHORT);
			return;
		}

		String phoneNumber = phoneNumberET.getText().toString().trim();
		String pwd = pwdET.getText().toString().trim();
		String pwd1 = pwd1ET.getText().toString().trim();
		if (phoneNumber == null || phoneNumber.equals("")) {
			MyToast.show(this, R.string.number_cannot_be_null,
					Toast.LENGTH_SHORT);
			return;
		} else if (countryCodeManager.hasCountryCodePrefix(phoneNumber)) {
			MyToast.show(this, R.string.phone_number_cannot_start_with_countrycode,
					Toast.LENGTH_SHORT);
			return;
		}

		if (pwd == null || pwd.equals("")) {
			MyToast.show(this, R.string.pls_input_pwd, Toast.LENGTH_SHORT);
			return;
		}

		if (pwd1 == null || pwd1.equals("")) {
			MyToast.show(this, R.string.pls_input_confirm_pwd,
					Toast.LENGTH_SHORT);
			return;
		}

		if (!pwd.equals(pwd1)) {
			MyToast.show(this, R.string.pwd1_is_different_from_pwd2,
					Toast.LENGTH_SHORT);
			return;
		}

		progressDlg = ProgressDialog.show(this, null,
				getString(R.string.finishing_register));

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("countryCode", countryCode);
		params.put("phoneNumber", phoneNumber);
		params.put("password", pwd);
		HttpUtils.postRequest(getString(R.string.server_url)
				+ getString(R.string.direct_reg_url),
				PostRequestFormat.URLENCODED, params, null,
				HttpRequestType.ASYNCHRONOUS, onFinishedRegister);

	}

	private OnHttpRequestListener onFinishedRegister = new OnHttpRequestListener() {

		@Override
		public void onFinished(HttpResponseResult responseResult) {
			dismissProgressDlg();
			try {
				JSONObject data = new JSONObject(
						responseResult.getResponseText());
				String result = data.getString("result");
				if (result.equals("0")) {
					// register ok, jump to login view
					new AlertDialog.Builder(AccountDirectRegisterActivity.this)
							.setTitle(R.string.alert_title)
							.setMessage(R.string.register_ok)
							.setPositiveButton(R.string.ok,
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											finish();
										}
									}).show();
				} else if (result.equals("1")) {
					MyToast.show(AccountDirectRegisterActivity.this,
							R.string.number_cannot_be_null, Toast.LENGTH_SHORT);
				} else if (result.equals("2")) {
					MyToast.show(AccountDirectRegisterActivity.this,
							R.string.phone_wrong_format, Toast.LENGTH_SHORT);
				} else if (result.equals("3")) {
					MyToast.show(AccountDirectRegisterActivity.this,
							R.string.existed_phone_number, Toast.LENGTH_SHORT);
				} else if (result.equals("4")) {
					MyToast.show(AccountDirectRegisterActivity.this,
							R.string.pls_input_pwd, Toast.LENGTH_SHORT);
				} else {
					MyToast.show(AccountDirectRegisterActivity.this,
							R.string.error_in_regsiter, Toast.LENGTH_SHORT);
				}
			} catch (Exception e) {
				e.printStackTrace();
				MyToast.show(AccountDirectRegisterActivity.this,
						R.string.error_in_regsiter, Toast.LENGTH_SHORT);
			}
		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			dismissProgressDlg();
			MyToast.show(AccountDirectRegisterActivity.this,
					R.string.error_in_regsiter, Toast.LENGTH_SHORT);
		}
	};

	class OnChangeEditTextBGListener implements OnFocusChangeListener {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			if (hasFocus) {
				((LinearLayout) v.getParent())
						.setBackgroundDrawable(AccountDirectRegisterActivity.this
								.getResources().getDrawable(
										R.drawable.bg_edit_s));
			} else {
				((LinearLayout) v.getParent())
						.setBackgroundDrawable(AccountDirectRegisterActivity.this
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
