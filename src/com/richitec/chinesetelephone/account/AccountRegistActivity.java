package com.richitec.chinesetelephone.account;

import java.util.HashMap;

import org.json.JSONObject;

import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.utils.CountryCodeManager;
import com.richitec.commontoolkit.utils.HttpUtils;
import com.richitec.commontoolkit.utils.MyToast;
import com.richitec.commontoolkit.utils.HttpUtils.HttpRequestType;
import com.richitec.commontoolkit.utils.HttpUtils.HttpResponseResult;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;
import com.richitec.commontoolkit.utils.HttpUtils.PostRequestFormat;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AccountRegistActivity extends Activity {
	private AlertDialog chooseCountryDialog;
	private int lastSelect=0;
	CountryCodeManager countryCodeManager;
	private ProgressDialog progressDlg;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        setContentView(R.layout.account_regist_layout_step1);
        
        countryCodeManager = CountryCodeManager.getInstance();
        
        ((Button)findViewById(R.id.regist_choose_country_btn)).setText(countryCodeManager.getCountryName(0));
    }
    
    public void chooseCountry(View v){
    	AlertDialog.Builder chooseCountryDialogBuilder = new AlertDialog.Builder(this);
    	chooseCountryDialogBuilder.setTitle(R.string.countrycode_list);
    	chooseCountryDialogBuilder.setSingleChoiceItems(countryCodeManager.getCountryNameList(), lastSelect, new chooseCountryListener());
    	chooseCountryDialogBuilder.setNegativeButton(R.string.cancel, null);
    	chooseCountryDialog= chooseCountryDialogBuilder.create();
    	chooseCountryDialog.show();
    }
    
    public void onFinishRegist(View v){
    	EditText pwd1ET = (EditText) findViewById(R.id.regist_psw_edittext);
		EditText pwd2ET = (EditText) findViewById(R.id.verify_psw_edittext);
		String pwd1 = pwd1ET.getText().toString().trim();
		String pwd2 = pwd2ET.getText().toString().trim();
		
		//Log.d("psw", pwd1+":"+pwd2);

		if (pwd1 == null || pwd1.equals("")) {
			MyToast.show(this, R.string.pls_input_pwd, Toast.LENGTH_SHORT);
			return;
		}

		if (pwd2 == null || pwd2.equals("")) {
			MyToast.show(this, R.string.pls_input_confirm_pwd,
					Toast.LENGTH_SHORT);
			return;
		}

		if (!pwd1.equals(pwd2)) {
			MyToast.show(this, R.string.pwd1_is_different_from_pwd2,
					Toast.LENGTH_SHORT);
			return;
		}
		
		progressDlg = ProgressDialog.show(this, null,
				getString(R.string.finishing_register));
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("password", pwd1);
		params.put("password1", pwd2);

		HttpUtils.postRequest(getString(R.string.server_url)
				+ getString(R.string.user_register_url),
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
					new AlertDialog.Builder(AccountRegistActivity.this)
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
				} else if (result.equals("6")) {
					MyToast.show(AccountRegistActivity.this,
							R.string.register_timeout, Toast.LENGTH_SHORT);
					setBody(R.layout.account_regist_layout_step1);
				} else {
					MyToast.show(AccountRegistActivity.this,
							R.string.error_in_regsiter, Toast.LENGTH_SHORT);
				}
			} catch (Exception e) {
				e.printStackTrace();
				MyToast.show(AccountRegistActivity.this,
						R.string.error_in_regsiter, Toast.LENGTH_SHORT);
			}
		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			dismissProgressDlg();
			MyToast.show(AccountRegistActivity.this,
					R.string.error_in_regsiter, Toast.LENGTH_SHORT);
		}
	};
    
    public void onVerifyAuthCodeAction(View v){
    	String authcode = ((EditText)(findViewById(R.id.auth_code_edittext)))
			.getText().toString().trim();
    	if(authcode==null||authcode.equals("")){
    		MyToast.show(AccountRegistActivity.this,
    				R.string.auth_code_cannot_be_null, Toast.LENGTH_SHORT);
			return;		
    	}
    	if(!authcode.matches("(^[0-9]*)")){
			MyToast.show(AccountRegistActivity.this,
    				R.string.authcode_wrong_format, Toast.LENGTH_SHORT);
			return;
		}
    	
    	//Log.d("authcode", authcode);
    
    	progressDlg = ProgressDialog.show(this, null,
				getString(R.string.verifying_auth_code));
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("code", authcode);
		HttpUtils.postRequest(getString(R.string.server_url)
				+ getString(R.string.check_auth_code_url),
				PostRequestFormat.URLENCODED, params, null,
				HttpRequestType.ASYNCHRONOUS, onFinishedVerifyAuthCode);
    	//setBody(R.layout.account_regist_layout_step3);
    }
    
    private OnHttpRequestListener onFinishedVerifyAuthCode = new OnHttpRequestListener() {

		@Override
		public void onFinished(HttpResponseResult responseResult) {
			dismissProgressDlg();

			try {
				JSONObject data = new JSONObject(
						responseResult.getResponseText());
				String result = data.getString("result");

				if (result.equals("0")) {
					// check phone code successfully, jump to step 3 to fill
					// password
					setBody(R.layout.account_regist_layout_step3);
				} else if (result.equals("2")) {
					MyToast.show(AccountRegistActivity.this,
							R.string.wrong_auth_code, Toast.LENGTH_SHORT);
				} else if (result.equals("6")) {
					MyToast.show(AccountRegistActivity.this,
							R.string.auth_code_timeout, Toast.LENGTH_SHORT);
					setBody(R.layout.account_regist_layout_step1);
				}

			} catch (Exception e) {
				e.printStackTrace();
				MyToast.show(AccountRegistActivity.this, R.string.auth_error,
						Toast.LENGTH_SHORT);
			}
		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			dismissProgressDlg();
			MyToast.show(AccountRegistActivity.this, R.string.auth_error,
					Toast.LENGTH_SHORT);
		}
	};

    
    public void onGetAuthCode(View v){
    	String phone = ((EditText)(findViewById(R.id.regist_phone_edittext)))
    						.getText().toString().trim();
    	String countrycode = countryCodeManager
    					.getCountryCode(((Button)findViewById(R.id.regist_choose_country_btn))
    							.getText().toString()).trim();
    	
    	//Log.d("AccountSetting", phone+":"+countrycode);
    	
    	if (phone==null||phone.equals("")) {
    		MyToast.show(AccountRegistActivity.this,
    				R.string.number_cannot_be_null, Toast.LENGTH_SHORT);
			return;
		}		
		if(!phone.matches("(^[0-9]*)")){
			MyToast.show(AccountRegistActivity.this,
    				R.string.phone_wrong_format, Toast.LENGTH_SHORT);
			return;
		}
		progressDlg = ProgressDialog.show(this, null,
				getString(R.string.sending_request));
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("phone", phone);
		params.put("countryCode", countrycode);
		HttpUtils.postRequest(getString(R.string.server_url)
				+ getString(R.string.retrieve_auth_code_url),
				PostRequestFormat.URLENCODED, params, null,
				HttpRequestType.ASYNCHRONOUS, onFinishedGetAuthCode);
		//setBody(R.layout.account_regist_layout_step2);
    }
    
    private OnHttpRequestListener onFinishedGetAuthCode = new OnHttpRequestListener() {

		@Override
		public void onFinished(HttpResponseResult responseResult) {
			dismissProgressDlg();

			try {
				JSONObject data = new JSONObject(
						responseResult.getResponseText());
				String result = data.getString("result");

				if (result.equals("0")) {
					// get phone code successfully, jump to step 2
					setBody(R.layout.account_regist_layout_step2);
				} else if (result.equals("2")) {
					MyToast.show(AccountRegistActivity.this,
							R.string.invalid_phone_number, Toast.LENGTH_SHORT);
				} else if (result.equals("3")) {
					MyToast.show(AccountRegistActivity.this,
							R.string.existed_phone_number, Toast.LENGTH_SHORT);
				} else {
					MyToast.show(AccountRegistActivity.this,
							R.string.error_in_retrieve_auth_code,
							Toast.LENGTH_SHORT);
				}

			} catch (Exception e) {
				e.printStackTrace();
				MyToast.show(AccountRegistActivity.this,
						R.string.error_in_retrieve_auth_code,
						Toast.LENGTH_SHORT);
			}

		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			dismissProgressDlg();
			MyToast.show(AccountRegistActivity.this,
					R.string.error_in_retrieve_auth_code, Toast.LENGTH_SHORT);
		}
	};
	
	private void setBody(int resID) {
		LinearLayout body = (LinearLayout) getBody();
		body.removeAllViewsInLayout();
		LayoutInflater.from(this).inflate(resID, body);
	}
	
	public LinearLayout getBody(){
		return (LinearLayout) findViewById(R.id.body);
	}
	
	private void dismissProgressDlg(){
		if(progressDlg!=null)
			progressDlg.dismiss();
	}
    
    class chooseCountryListener implements DialogInterface.OnClickListener{

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			lastSelect = which;
			((Button)(AccountRegistActivity.this.findViewById(R.id.regist_choose_country_btn)))
					.setText(countryCodeManager.getCountryName(which));
			chooseCountryDialog.dismiss();
		}
    	
    }
    
    class OnChangeEditTextBGListener implements OnFocusChangeListener{

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			if(hasFocus){
				((LinearLayout)v.getParent()).setBackgroundDrawable(
						AccountRegistActivity.this.getResources().getDrawable(R.drawable.textfeild_selected));
			}
			else{
				((LinearLayout)v.getParent()).setBackgroundDrawable(
						AccountRegistActivity.this.getResources().getDrawable(R.drawable.textfeild_nor));
			}
		}
    }
    
    
}
