package com.richitec.chinesetelephone.account;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.account.AccountSettingActivity.ChooseCountryListener;
import com.richitec.chinesetelephone.constant.SystemConstants;
import com.richitec.chinesetelephone.util.CountryCodeManager;
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
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AccountForgetPSWActivity extends Activity {
	private AlertDialog chooseCountryDialog;
	private int lastSelectCountryCode=0;
	private ProgressDialog progressDlg;
	private CountryCodeManager countryCodeManager;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_forget_psw);
        
        countryCodeManager = CountryCodeManager.getInstance();
        
        ((Button)findViewById(R.id.getpsw_choose_country_btn))
        		.setText(countryCodeManager.getCountryName(0));
    }  
    
    public void chooseCountry(View v){
    	AlertDialog.Builder chooseCountryDialogBuilder = new AlertDialog.Builder(this);
    	chooseCountryDialogBuilder.setTitle(R.string.countrycode_list);
    	chooseCountryDialogBuilder.setSingleChoiceItems(
    			countryCodeManager.getCountryNameList(), lastSelectCountryCode, new chooseCountryListener());
    	chooseCountryDialogBuilder.setNegativeButton(R.string.cancel, null);
    	chooseCountryDialog= chooseCountryDialogBuilder.create();
    	chooseCountryDialog.show();
    }
    
    class chooseCountryListener implements DialogInterface.OnClickListener{

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			lastSelectCountryCode = which;
			((Button)(AccountForgetPSWActivity.this.findViewById(R.id.getpsw_choose_country_btn)))
					.setText(countryCodeManager.getCountryName(which));
			chooseCountryDialog.dismiss();
		}
    	
    }
    
    public void onCancelBtnClick(View v){
    	InputMethodManager imm = (InputMethodManager)getSystemService(
		 		Context.INPUT_METHOD_SERVICE); 
    	imm.hideSoftInputFromWindow(((EditText) findViewById(R.id.get_phone_editText))
		 		.getWindowToken(),0);
    	finish();
    }
    
    public void onGetPSWAction(View v){
    	EditText phoneEdit = (EditText) findViewById(R.id.get_phone_editText);
    	String countryCode = countryCodeManager.
    					getCountryCode(((Button)findViewById(R.id.getpsw_choose_country_btn))
    								.getText().toString().trim());
    	String phone = phoneEdit.getEditableText().toString().trim();
    	
    	if(!phone.matches("(^[0-9]*)")){
    		MyToast.show(this, R.string.phone_wrong_format, Toast.LENGTH_LONG);
    	}
    	
    	Log.d(SystemConstants.TAG, phone+":"+countryCode);
    	
    	progressDlg = ProgressDialog.show(this, null,
				getString(R.string.sending_request));
    	
    	HashMap<String,String> params = new HashMap<String,String>();
    	params.put("username", phone);
    	params.put("countryCode", countryCode);
    	
    	HttpUtils.postRequest(getString(R.string.server_url)+getString(R.string.getpsw_url), 
    					PostRequestFormat.URLENCODED, params, null,
    					HttpRequestType.ASYNCHRONOUS, onFinishGetPSW);
    }
    
    private void dismiss(){
    	if(progressDlg!=null)
    		progressDlg.dismiss();
    }
    
    private OnHttpRequestListener onFinishGetPSW = new OnHttpRequestListener(){

		@Override
		public void onFinished(HttpResponseResult responseResult) {
			// TODO Auto-generated method stub
			int result = responseResult.getStatusCode();
			dismiss();
			if(result == 200||result==201){
				new AlertDialog.Builder(AccountForgetPSWActivity.this)
					.setTitle(R.string.alert_title)
					.setMessage(R.string.get_psw_finish)
					.setPositiveButton(R.string.ok, 
							new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									InputMethodManager imm = (InputMethodManager)getSystemService(
											 		Context.INPUT_METHOD_SERVICE); 
							        imm.hideSoftInputFromWindow(((EditText) findViewById(R.id.get_phone_editText))
							        		 		.getWindowToken(),0);
							        
							        UserManager.getInstance().getUser().setPassword("");
							        UserManager.getInstance().getUser().setUserKey("");
							        Intent intent = new Intent(AccountForgetPSWActivity.this, AccountSettingActivity.class);
									intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(intent);
									dialog.dismiss();
									finish();
								}
							}
							).show();
				
			}
		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			// TODO Auto-generated method stub
			//Log.d(SystemConstants.TAG, responseResult.getStatusCode()+"");
			dismiss();
			MyToast.show(AccountForgetPSWActivity.this,
					R.string.phone_number_not_exist, Toast.LENGTH_SHORT);
		}
    	
    };
}
