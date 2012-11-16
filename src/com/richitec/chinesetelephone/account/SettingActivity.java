package com.richitec.chinesetelephone.account;

import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.assist.RemainMoneyActivity;
import com.richitec.chinesetelephone.bean.TelUserBean;
import com.richitec.chinesetelephone.constant.SystemConstants;
import com.richitec.commontoolkit.activityextension.AppLaunchActivity;
import com.richitec.commontoolkit.activityextension.NavigationActivity;
import com.richitec.commontoolkit.customcomponent.CommonPopupWindow;
import com.richitec.commontoolkit.user.User;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.DataStorageUtils;
import com.richitec.commontoolkit.utils.HttpUtils;
import com.richitec.commontoolkit.utils.MyToast;
import com.richitec.commontoolkit.utils.StringUtils;
import com.richitec.commontoolkit.utils.HttpUtils.HttpRequestType;
import com.richitec.commontoolkit.utils.HttpUtils.HttpResponseResult;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;
import com.richitec.commontoolkit.utils.HttpUtils.PostRequestFormat;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class SettingActivity extends Activity {
	
	public static String TITLE_NAME = "titlename";
	
	private AlertDialog.Builder builder;
	private AlertDialog dialog;
	private ProgressDialog progressDialog;
	
	private final ModifyPSWPopupWindow modifyPSWPopupWindow = new ModifyPSWPopupWindow(
			R.layout.modify_psw_popupwindow_layout,
			LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT
			);
	
	private final GetPSWPopupWindow getPSWPopupWindow = new GetPSWPopupWindow(
			R.layout.get_psw_popupwindow_layout,
			LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT
			);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        
        LinearLayout changeAccount = (LinearLayout) findViewById(R.id.account_setting_btn);        
        changeAccount.setOnClickListener(changeAccountListener);
        
        LinearLayout modifyPSW = (LinearLayout)findViewById(R.id.account_changePSW_btn);
        modifyPSW.setOnClickListener(modifyPSWListener);
        
        LinearLayout getPSW = (LinearLayout)findViewById(R.id.account_getPSW_btn);
        getPSW.setOnClickListener(getPswListener);
        
        builder = new AlertDialog.Builder(SettingActivity.this)
		.setTitle(R.string.alert_title)
		.setMessage(R.string.modify_success)
		.setPositiveButton(R.string.ok, 
				new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						Intent intent = new Intent(SettingActivity.this, AccountSettingActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						finish();
					}
				}
				);
        
    }  
    
    public void getRemainMoney(View v){
    	TelUserBean userBean = (TelUserBean) UserManager.getInstance().getUser();
    	String username = userBean.getName();
    	String countryCode = userBean.getCountryCode();
    	
    	progressDialog = ProgressDialog.show(this, null,
				getString(R.string.sending_request), true);
    	
    	HashMap<String,String> params = new HashMap<String,String>();
    	params.put("username", username);
    	params.put("countryCode", countryCode);
    	
    	HttpUtils.postSignatureRequest(getString(R.string.server_url)+getString(R.string.account_balance_url), 
				PostRequestFormat.URLENCODED, params,
				null, HttpRequestType.ASYNCHRONOUS, onFinishedGetBalance);
    	
    }
    
    private OnHttpRequestListener onFinishedGetBalance = new OnHttpRequestListener() {

		@Override
		public void onFinished(HttpResponseResult responseResult) {
			dismiss();
			JSONObject data;
			try {
				data = new JSONObject(
						responseResult.getResponseText());
				double result = data.getDouble("balance");
				Intent intent = new Intent(SettingActivity.this,RemainMoneyActivity.class);
		    	intent.putExtra("nav_back_btn_default_title",getString(R.string.setting));
		    	intent.putExtra(RemainMoneyActivity.BALANCE, result);
		    	startActivity(intent);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			dismiss();
			MyToast.show(SettingActivity.this, R.string.get_balance_error, Toast.LENGTH_SHORT);
		}
	};
    
    private OnClickListener getPswListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			getPSWPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
		}
    	
    };
    
    private OnClickListener changeAccountListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(SettingActivity.this,AccountSettingActivity.class);
			intent.putExtra(TITLE_NAME, getString(R.string.change_account_title));
			intent.putExtra("firstLogin", false);
			startActivity(intent);
		}
    	
    };
    
    private OnClickListener modifyPSWListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			modifyPSWPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
		}
    	
    };
    
    private void dismiss(){
    	if(progressDialog!=null)
    		progressDialog.dismiss();
    }
    
    private void updatePreference(String key,String psw){
		if(UserManager.getInstance().getUser().isRememberPwd()){
			Log.d("update", "update!");
			DataStorageUtils
			.putObject(User.password.name(), psw);
			DataStorageUtils.putObject(User.userkey.name(), key);
		}
	}

	private void modifyPSW(String oldpsw,String newpsw , String confirm){
    	if(oldpsw==null||oldpsw.equals("")){
    		MyToast.show(this, R.string.old_psw_not_null, Toast.LENGTH_SHORT);
    		return;
    	}
    	
    	if(newpsw==null||confirm==null||newpsw.equals("")||confirm.equals("")){
    		MyToast.show(this, R.string.new_confirm_not_null, Toast.LENGTH_SHORT);
    		return;
    	}
    	
    	TelUserBean userBean = (TelUserBean) UserManager.getInstance().getUser();	
    	String oldmd5 = StringUtils.md5(oldpsw);
    	String countrycode = userBean.getCountryCode();
    	String username = userBean.getName();
    	if(!newpsw.equals(confirm)){
    		MyToast.show(this, R.string.new_confirm_not_equal, Toast.LENGTH_SHORT);
    		return;
    	}
    	
    	progressDialog = ProgressDialog.show(this, null,
				getString(R.string.sending_request), true);
    	//Log.d(SystemConstants.TAG, oldmd5+":"+countrycode+":"+username+":"+newpsw+":"+confirm);
		HashMap<String,String> params = new HashMap<String,String>();
		params.put("username", username);
		params.put("countryCode", countrycode);
		params.put("oldPwd", oldmd5);
		params.put("newPwd", newpsw);
		params.put("newPwdConfirm", confirm);
		Log.d("modify", "modify");
		HttpUtils.postSignatureRequest(getString(R.string.server_url)+getString(R.string.modify_psw_url), 
				PostRequestFormat.URLENCODED, params,
				null, HttpRequestType.ASYNCHRONOUS, onFinishedModifyPsw);
    	

    }
    
    private OnHttpRequestListener onFinishedModifyPsw = new OnHttpRequestListener() {

		@Override
		public void onFinished(HttpResponseResult responseResult) {
			try {
				JSONObject data = new JSONObject(
						responseResult.getResponseText());
				String userkey = data.getString("userkey");
				Log.d("userkey", userkey);
				String newpsw = ((EditText)modifyPSWPopupWindow.getContentView().
						findViewById(R.id.new_psw_editText)).getEditableText().toString().trim();
				Log.d("psw", newpsw);
				UserManager.getInstance().getUser().setUserKey(userkey);
				UserManager.getInstance().getUser().setPassword(StringUtils.md5(newpsw));
				updatePreference(userkey,newpsw);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dismiss();
			modifyPSWPopupWindow.dismiss();
		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			dismiss();
			int code = responseResult.getStatusCode();
			if(code==401){
				MyToast.show(SettingActivity.this, R.string.auth_not_pass, Toast.LENGTH_SHORT);
			}
			else if(code==400){
				MyToast.show(SettingActivity.this, R.string.new_psw_error, Toast.LENGTH_SHORT);
			}
			else if(code==500){
				MyToast.show(SettingActivity.this, R.string.server_error, Toast.LENGTH_SHORT);
			}
		}
	};
	
	private void getPSW(String phone){
		String countryCode = ((TelUserBean)UserManager.getInstance().getUser()).getCountryCode();
		
		progressDialog = ProgressDialog.show(this, null,
				getString(R.string.sending_request), true);
		
		HashMap<String,String> params = new HashMap<String,String>();
		params.put("username", phone);
    	params.put("countryCode", countryCode);
    	HttpUtils.postRequest(getString(R.string.server_url)+getString(R.string.getpsw_url), 
				PostRequestFormat.URLENCODED, params, null,
				HttpRequestType.ASYNCHRONOUS, onFinishGetPSW);
	}
	
	 private OnHttpRequestListener onFinishGetPSW = new OnHttpRequestListener(){

			@Override
			public void onFinished(HttpResponseResult responseResult) {
				// TODO Auto-generated method stub
				int result = responseResult.getStatusCode();
				
				if(result == 200||result==201){
					builder.setMessage(R.string.get_psw_finish);
					dialog = builder.create();
					dialog.show();
				}
			}

			@Override
			public void onFailed(HttpResponseResult responseResult) {
				// TODO Auto-generated method stub
				//Log.d(SystemConstants.TAG, responseResult.getStatusCode()+"");
				MyToast.show(SettingActivity.this,
						R.string.phone_number_not_exist, Toast.LENGTH_SHORT);
			}
	    	
	    };
    
    class ModifyPSWPopupWindow extends CommonPopupWindow {
		
		public ModifyPSWPopupWindow(int resource, int width,
				int height, boolean focusable, boolean isBindDefListener) {
			super(resource, width, height, focusable, isBindDefListener);
		}
		
		public ModifyPSWPopupWindow(int resource, int width,
				int height) {
			super(resource, width, height);
		}
		
		@Override
		protected void bindPopupWindowComponentsListener() {
		
			// bind contact phone select cancel button click listener
			((Button) getContentView().findViewById(R.id.modify_psw_confirmBtn))
					.setOnClickListener(new ModifyPSWConfirmBtnOnClickListener());
			((Button)getContentView().findViewById(R.id.modify_psw_cancelBtn)).setOnClickListener(
					new ModifyPSWCancelBtnOnClickListener());
		}
		
		@Override
		protected void resetPopupWindow() {
			// hide contact phones select phone list view
			((EditText)getContentView().findViewById(R.id.old_psw_editText)).setText("");
			((EditText)getContentView().findViewById(R.id.new_psw_editText)).setText("");
			((EditText)getContentView().findViewById(R.id.confirm_psw_editText)).setText("");
		}
		
		// inner class
		// contact phone select phone button on click listener
		class ModifyPSWConfirmBtnOnClickListener implements OnClickListener {
		
			@Override
			public void onClick(View v) {		
				// dismiss contact phone select popup window		
				String oldpsw = ((EditText)getContentView().
							findViewById(R.id.old_psw_editText)).getEditableText().toString().trim();
				String newpsw = ((EditText)getContentView().
							findViewById(R.id.new_psw_editText)).getEditableText().toString().trim();
				String confirm = ((EditText)getContentView().
							findViewById(R.id.confirm_psw_editText)).getEditableText().toString().trim();
				
				InputMethodManager imm = (InputMethodManager)getSystemService(
						Context.INPUT_METHOD_SERVICE); 
				imm.hideSoftInputFromWindow(((EditText) getContentView().findViewById(R.id.old_psw_editText))
						.getWindowToken(),0);
				modifyPSW(oldpsw,newpsw,confirm);
			}
		
		}
		
		// contact phone select cancel button on click listener
		class ModifyPSWCancelBtnOnClickListener implements OnClickListener {
		
			@Override
			public void onClick(View v) {
				// dismiss contact phone select popup window
				InputMethodManager imm = (InputMethodManager)getSystemService(
				 		Context.INPUT_METHOD_SERVICE); 
				imm.hideSoftInputFromWindow(((EditText) getContentView().findViewById(R.id.old_psw_editText))
        		 		.getWindowToken(),0);
				dismiss();
			}
		
		}

    }
    
    class GetPSWPopupWindow extends CommonPopupWindow {
		
		public GetPSWPopupWindow(int resource, int width,
				int height, boolean focusable, boolean isBindDefListener) {
			super(resource, width, height, focusable, isBindDefListener);
		}
		
		public GetPSWPopupWindow(int resource, int width,
				int height) {
			super(resource, width, height);
		}
		
		@Override
		protected void bindPopupWindowComponentsListener() {
		
			// bind contact phone select cancel button click listener
			((Button) getContentView().findViewById(R.id.get_psw_confirmBtn))
					.setOnClickListener(new GetPSWConfirmBtnOnClickListener());
			((Button)getContentView().findViewById(R.id.get_psw_cancelBtn)).setOnClickListener(
					new GetPSWCancelBtnOnClickListener());
		}
		
		@Override
		protected void resetPopupWindow() {
			// hide contact phones select phone list view
			((EditText)getContentView().findViewById(R.id.get_psw_editText)).setText("");
		}
		
		// inner class
		// contact phone select phone button on click listener
		class GetPSWConfirmBtnOnClickListener implements OnClickListener {
		
			@Override
			public void onClick(View v) {		
				// dismiss contact phone select popup window		
				String phone = ((EditText)getContentView().
							findViewById(R.id.get_psw_editText)).getEditableText().toString().trim();
				
				if(!phone.matches("(^[0-9]*)")){
					MyToast.show(SettingActivity.this, R.string.phone_wrong_format, Toast.LENGTH_SHORT);
					return;
				}
				if(phone==null||phone.equals("")){
					MyToast.show(SettingActivity.this, R.string.number_cannot_be_null, Toast.LENGTH_SHORT);
					return;
				}
				InputMethodManager imm = (InputMethodManager)getSystemService(
						Context.INPUT_METHOD_SERVICE); 
				imm.hideSoftInputFromWindow(((EditText) getContentView().findViewById(R.id.get_psw_editText))
						.getWindowToken(),0);
				getPSW(phone);
			}
		
		}
		
		// contact phone select cancel button on click listener
		class GetPSWCancelBtnOnClickListener implements OnClickListener {
		
			@Override
			public void onClick(View v) {
				// dismiss contact phone select popup window
				InputMethodManager imm = (InputMethodManager)getSystemService(
				 		Context.INPUT_METHOD_SERVICE); 
				imm.hideSoftInputFromWindow(((EditText) getContentView().findViewById(R.id.get_psw_editText))
        		 		.getWindowToken(),0);
				dismiss();
			}
		
		}

    }
}
