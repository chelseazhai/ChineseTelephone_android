package com.richitec.chinesetelephone.account;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;
import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.assist.SettingActivity;
import com.richitec.chinesetelephone.bean.TelUserBean;
import com.richitec.chinesetelephone.constant.SystemConstants;
import com.richitec.chinesetelephone.constant.TelUser;
import com.richitec.chinesetelephone.sip.SipUtils;
import com.richitec.chinesetelephone.tab7tabcontent.ChineseTelephoneTabActivity;
import com.richitec.chinesetelephone.utils.AppUpdateManager;
import com.richitec.chinesetelephone.utils.CountryCodeManager;
import com.richitec.commontoolkit.user.User;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.DataStorageUtils;
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

public class AccountSettingActivity extends Activity {
	private ProgressDialog progressDialog;
	private AlertDialog chooseCountryDialog;
	private int lastSelectCountryCode=0;
	private CountryCodeManager countryCodeManager;
	private String PWD_MASK = "#@1d~`*)";
	private boolean useSavedPsw ;
	
	private boolean isFirstLogin;
	
    private TextWatcher onTextChanged = new TextWatcher() {
	
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			//((CheckBox)(findViewById(R.id.account_remember_psw_cbtn))).setChecked(false);
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
		TelUserBean user = (TelUserBean) UserManager.getInstance().getUser();
		Log.d(SystemConstants.TAG, "user: " + user.toString());
		DataStorageUtils.putObject(User.username.name(), user.getName());
		DataStorageUtils.putObject(TelUser.countryCode.name(), user.getCountryCode());
		
		if (user.isRememberPwd()) {
			DataStorageUtils.putObject(TelUser.vosphone.name(), user.getVosphone());
			DataStorageUtils.putObject(TelUser.vosphone_pwd.name(), user.getVosphone_pwd());
			DataStorageUtils
					.putObject(User.password.name(), user.getPassword());
			DataStorageUtils.putObject(User.userkey.name(), user.getUserKey());
		} else {
			DataStorageUtils.putObject(User.password.name(), "");
			DataStorageUtils.putObject(TelUser.vosphone.name(), "");
			DataStorageUtils.putObject(TelUser.vosphone_pwd.name(), "");
			DataStorageUtils.putObject(User.userkey.name(), "");
			//user.setPassword("");
		}
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        setContentView(R.layout.account_setting_layout);
        
        Intent intent = this.getIntent();
        
        isFirstLogin = intent.getBooleanExtra("firstLogin", true);
        
        Bundle data = intent.getExtras();
        if(data!=null&&data.containsKey(SettingActivity.TITLE_NAME)){
        	String title = data.getString(SettingActivity.TITLE_NAME);
        	//Log.d(SystemConstants.TAG, "title:"+title);
        	((TextView)findViewById(R.id.account_setting_title)).setText(title);
        }
        
        countryCodeManager = CountryCodeManager.getInstance();
        TelUserBean user = (TelUserBean) UserManager.getInstance().getUser();
        
        if(user.getCountryCode()==null||user.getCountryCode().equals("")){
	        ((Button)findViewById(R.id.account_choose_country_btn))
	        		.setText(countryCodeManager.getCountryName(0));
        }
        else{
        	lastSelectCountryCode = countryCodeManager.getCountryIndex(user.getCountryCode());
        	((Button)findViewById(R.id.account_choose_country_btn))
    				.setText(countryCodeManager.getCountryName(lastSelectCountryCode));
        }
        
        EditText userEditText = (EditText) findViewById(R.id.account_user_edittext);
        EditText pswEditText = (EditText)findViewById(R.id.account_psw_edittext);
        CheckBox remember = (CheckBox)findViewById(R.id.account_remember_psw_cbtn);
        
        userEditText.addTextChangedListener(onTextChanged);
        pswEditText.addTextChangedListener(onTextChanged);
              
        userEditText.setOnFocusChangeListener(new OnChangeEditTextBGListener());       
        pswEditText.setOnFocusChangeListener(new OnChangeEditTextBGListener());
        
        userEditText.setText(user.getName());
        
        if(user.getPassword()!=null&&user.getPassword()!=""){
        	pswEditText.setText(PWD_MASK);
        	useSavedPsw = true;
        	remember.setChecked(true);
        }
        else{
        	useSavedPsw = false;
        	remember.setChecked(false);
        }
        
        //从设置页面切换账号时标明用户是否在登录时勾选记住密码
        if(!user.isRememberPwd()){
        	//Log.d("Account remember", user.isRememberPwd()+"");
        	pswEditText.setText("");
        	useSavedPsw = false;
        	remember.setChecked(false);
        }
        
        AppUpdateManager updateManager = new AppUpdateManager(this);
        updateManager.checkVersion(false);
    }
	
	@Override
	public void onBackPressed(){
		if(isFirstLogin){
			Intent i= new Intent(Intent.ACTION_MAIN); 
		    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		    i.addCategory(Intent.CATEGORY_HOME); 
		    startActivity(i);
		}
		else{
			finish();
		}
	}
    
	//country code select dialog
    public void chooseCountry(View v){
    	AlertDialog.Builder chooseCountryDialogBuilder = new AlertDialog.Builder(this);
    	chooseCountryDialogBuilder.setTitle(R.string.countrycode_list);
    	chooseCountryDialogBuilder.setSingleChoiceItems(
    			countryCodeManager.getCountryNameList(), lastSelectCountryCode, new ChooseCountryListener());
    	chooseCountryDialogBuilder.setNegativeButton(R.string.cancel, null);
    	chooseCountryDialog= chooseCountryDialogBuilder.create();
    	chooseCountryDialog.show();
    }
    
    public void onRegist(View v){
    	Intent intent = new Intent(this,AccountRegistActivity.class);
    	startActivity(intent);
    }
    
    public void onLogin(View v){
    	String username = ((EditText)(findViewById(R.id.account_user_edittext)))
    							.getText().toString().trim();
    	String psw = ((EditText)(findViewById(R.id.account_psw_edittext)))
    						.getText().toString().trim();
    	String countrycode = countryCodeManager.getCountryCode(
    			((Button)findViewById(R.id.account_choose_country_btn))
    				.getText().toString().trim());
    	boolean isRemember = ((CheckBox)(findViewById(R.id.account_remember_psw_cbtn))).isChecked();
    	
    	//Log.d("AccountSetting", username+":"+psw+":"+countrycode+":"+isRemember);
    	
    	if (username.equals("")) {
    		MyToast.show(this, R.string.number_cannot_be_null, Toast.LENGTH_LONG);
			return;
		} 

		if (psw.equals("")) {
			MyToast.show(this, R.string.psw_cannot_be_null, Toast.LENGTH_LONG);
			return;
		}
		
		if(!username.matches("(^[0-9]*)")){
			MyToast.show(this, R.string.phone_wrong_format, Toast.LENGTH_LONG);
			return;
		}
		
		if(!useSavedPsw){
			//Log.d("SETUser", "set user");
			UserManager.getInstance().setUser(username, psw);
		}
		
		progressDialog = ProgressDialog.show(this, null,
				getString(R.string.logining), true);
		
		TelUserBean telUserBean = (TelUserBean) UserManager.getInstance().getUser();
		telUserBean.setRememberPwd(isRemember);
		telUserBean.setCountryCode(countrycode);
		
		//unregitst sip account first
		SipUtils.unregisterSipAccount(null);
		loginAccount(telUserBean);
    }
    
    private void loginAccount(TelUserBean telUserBean){
    	HashMap<String,String> params = new HashMap<String,String>();
		params.put("loginName", telUserBean.getName());
		params.put("loginPwd", telUserBean.getPassword());
		params.put("countryCode", telUserBean.getCountryCode());
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
		
		String loginUrl = getString(R.string.server_url)+getString(R.string.login_url);
		
		HttpUtils.postRequest(loginUrl, PostRequestFormat.URLENCODED, params,
				null, HttpRequestType.ASYNCHRONOUS, onFinishedLogin);
    }
    
    public void loginError() {
		closeProgressDialog();
		MyToast.show(this, R.string.login_error, Toast.LENGTH_LONG);
	}

	public void loginFailed() {
		closeProgressDialog();
		MyToast.show(this, R.string.login_failed, Toast.LENGTH_LONG);
	}

	public void loginSuccess(JSONObject data) {
		try {
			String userKey = data.getString("userkey");
			UserManager.getInstance().setUserKey(userKey);
			String vosphone = data.getString("vosphone");
			String vosphone_psw = data.getString("vosphone_pwd");
			
			TelUserBean telUser = (TelUserBean)UserManager.getInstance().getUser();
			telUser.setVosphone(vosphone);
			telUser.setVosphone_pwd(vosphone_psw);
			
			saveUserAccount();
			
			closeProgressDialog();
			Intent intent = new Intent(AccountSettingActivity.this, 
					ChineseTelephoneTabActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
			startActivity(intent);
			finish();	

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	
	private void closeProgressDialog(){
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
	
	public void onCheckBoxTitleClick(View v){
    	boolean isCheck = !((CheckBox)findViewById(R.id.account_remember_psw_cbtn)).isChecked();
    	((CheckBox)findViewById(R.id.account_remember_psw_cbtn)).setChecked(isCheck);
    }
    
    public void onForgetPSWBtnClick(View v){
    	Intent intent = new Intent(this,AccountForgetPSWActivity.class);
    	startActivity(intent);
    }
    
    class ChooseCountryListener implements DialogInterface.OnClickListener{
	
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			lastSelectCountryCode = which;
			((Button)(AccountSettingActivity.this.findViewById(R.id.account_choose_country_btn)))
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
						AccountSettingActivity.this.getResources().getDrawable(R.drawable.textfeild_selected));
			}
			else{
				((LinearLayout)v.getParent()).setBackgroundDrawable(
						AccountSettingActivity.this.getResources().getDrawable(R.drawable.textfeild_nor));
			}
		}
    	
    }
}
