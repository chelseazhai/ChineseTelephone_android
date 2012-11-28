package com.richitec.chinesetelephone.assist;

import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.account.AccountSettingActivity;
import com.richitec.chinesetelephone.bean.DialPreferenceBean;
import com.richitec.chinesetelephone.bean.TelUserBean;
import com.richitec.chinesetelephone.constant.DialPreference;
import com.richitec.chinesetelephone.constant.LaunchSetting;
import com.richitec.chinesetelephone.constant.TelUser;
import com.richitec.chinesetelephone.sip.SipUtils;
import com.richitec.chinesetelephone.utils.AppUpdateManager;
import com.richitec.chinesetelephone.utils.CountryCodeManager;
import com.richitec.chinesetelephone.utils.DialPreferenceManager;
import com.richitec.commontoolkit.activityextension.NavigationActivity;
import com.richitec.commontoolkit.customcomponent.CommonPopupWindow;
import com.richitec.commontoolkit.user.User;
import com.richitec.commontoolkit.user.UserBean;
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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends NavigationActivity {
	private AlertDialog chooseCountryDialog ;
	private CountryCodeManager countryCodeManager = CountryCodeManager.getInstance();
	public static String TITLE_NAME = "titlename";
	private String inviteLink;
	
	private AlertDialog.Builder builder;
	private AlertDialog dialog;
	private ProgressDialog progressDialog;
	private RadioGroup dialGroup;
	private RadioGroup answerGroup;
	private RadioGroup launchGroup;
	private RadioGroup loginGroup;
	
	private PopupWindow popWin;
	
	private final ModifyPSWPopupWindow modifyPSWPopupWindow = new ModifyPSWPopupWindow(
			R.layout.modify_psw_popupwindow_layout,
			LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT
			);	
	private final GetPSWPopupWindow getPSWPopupWindow = new GetPSWPopupWindow(
			R.layout.get_psw_popupwindow_layout,
			LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT
			);	
	private final SetAreaCodePopupWindow setAreaCodePopupWindow = new SetAreaCodePopupWindow(
			R.layout.set_areacode_popupwindow_layout,
			LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT
			);
	private final SetDialPreferencePopupWindow setDialPreferencePopupWin = new SetDialPreferencePopupWindow(
			R.layout.dial_preference_popupwindow_layout,
			LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT
			);
	private final LaunchSetCodePopupWindow launchSetCodePopupWindow = new LaunchSetCodePopupWindow(
			R.layout.setup_preference_popupwindow_layout,
			LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT
			);
	private final SetAuthNumberPopupWindow setAuthNumberPopupWindow = new SetAuthNumberPopupWindow(
			R.layout.set_auth_number_popupwindow_layout,
			LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	//Log.e("SettingActivity", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        
        LinearLayout inviteFriend = (LinearLayout) findViewById(R.id.account_invite_btn);        
        inviteFriend.setOnClickListener(inviteFriendListener);
        
        LinearLayout changeAccount = (LinearLayout) findViewById(R.id.account_setting_btn);        
        changeAccount.setOnClickListener(changeAccountListener);
        
        LinearLayout modifyPSW = (LinearLayout)findViewById(R.id.account_changePSW_btn);
        modifyPSW.setOnClickListener(modifyPSWListener);
        
        LinearLayout getPSW = (LinearLayout)findViewById(R.id.account_getPSW_btn);
        getPSW.setOnClickListener(getPswListener);
        
        dialGroup = (RadioGroup)setDialPreferencePopupWin.getContentView().
        		findViewById(R.id.dial_radio_group);
        answerGroup = (RadioGroup)setDialPreferencePopupWin.getContentView().
        		findViewById(R.id.answer_radio_group);
        
        launchGroup = (RadioGroup)launchSetCodePopupWindow.getContentView().
        		findViewById(R.id.auto_launch_radio_group);
        loginGroup = (RadioGroup)launchSetCodePopupWindow.getContentView().
        		findViewById(R.id.auto_login_group);
        
        builder = new AlertDialog.Builder(SettingActivity.this)
		.setTitle(R.string.alert_title)
		.setMessage(R.string.get_psw_finish)
		.setPositiveButton(R.string.ok, 
				new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						UserManager.getInstance().getUser().setPassword("");
						Intent intent = new Intent(SettingActivity.this, AccountSettingActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						finish();
					}
				}
				);
        
        setTitle(R.string.menu_settings);
    } 
    
    public void getAuthNumber(View v){
    	progressDialog = ProgressDialog.show(this, null,
				getString(R.string.sending_request), true);
    	TelUserBean userBean = (TelUserBean) UserManager.getInstance().getUser();
    	String username = userBean.getName();
    	String countrycode = userBean.getCountryCode();
		HashMap<String,String> params = new HashMap<String,String>();
		params.put("username", username);
		params.put("countryCode", countrycode);
		HttpUtils.postSignatureRequest(getString(R.string.server_url)+getString(R.string.getBindPhone), 
				PostRequestFormat.URLENCODED, params,
				null, HttpRequestType.ASYNCHRONOUS, onFinishedGetBindPhone);
    }
    
    private OnHttpRequestListener onFinishedGetBindPhone = new OnHttpRequestListener() {

		@Override
		public void onFinished(HttpResponseResult responseResult) {

			try {
				JSONObject data = new JSONObject(responseResult.getResponseText());
				String phone = data.getString("bindphone");
				String countrycode = data.getString("bindphone_country_code");
				//Log.d("!!!!!!", phone+":"+countrycode);
				View contentView = LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.get_bind_phone_popupwindow_layout, null); 
				
				popWin = new PopupWindow(contentView,
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT
						);
				popWin.showAtLocation(findViewById(R.id.setting_main_layout), Gravity.CENTER, 0, 0);
				
				((TextView)(popWin.getContentView().
						findViewById(R.id.get_bind_country_editText))).setText(countrycode);
				((TextView)(popWin.getContentView().
						findViewById(R.id.get_bind_number_editText))).setText(phone);
				((Button)(popWin.getContentView().findViewById(R.id.get_bind_confirmBtn))).
						setOnClickListener(
								new OnClickListener(){
									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub
										popWin.dismiss();
									}									
								}
						);
					
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
			dismiss();
		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			dismiss();
			MyToast.show(SettingActivity.this, R.string.server_error, Toast.LENGTH_SHORT);			
		}
	};
    
    public void setAuthNumber(View v){
    	setAuthNumberPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }
    
    public void exitProgram(View v){
    	new AlertDialog.Builder(SettingActivity.this)
		.setTitle(R.string.alert_title)
		.setMessage(R.string.exit_program_messge)
		.setPositiveButton(R.string.ok, 
				new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						DataStorageUtils.putObject(User.password.name(), "");
			    		DataStorageUtils.putObject(User.password.name(), "");
						DataStorageUtils.putObject(TelUser.vosphone.name(), "");
						DataStorageUtils.putObject(TelUser.vosphone_pwd.name(), "");
						DataStorageUtils.putObject(User.userkey.name(), "");
						SipUtils.unregisterSipAccount(null);
						System.exit(0);
					}
				}
				)
		.setNegativeButton(R.string.cancel, null).show();
    }
    
    public void checkUpdateVersion(View v){
    	AppUpdateManager updateManager = new AppUpdateManager(this);
    	updateManager.checkVersion(true);
    }
    
    @Override
    public void onBackPressed(){
    	this.getParent().onBackPressed();
    }
    
    public void setAreaCode(View v){
    	setAreaCodePopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }
    
    public void setSetupPreference(View v){
    	launchSetCodePopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }
    
    public void setDialPreference(View v){
    	setDialPreferencePopupWin.showAtLocation(v, Gravity.CENTER, 0, 0);
    }
    
    public void getRemainMoney(View v){
    	Intent intent = new Intent(SettingActivity.this,RemainMoneyActivity.class);
    	intent.putExtra("nav_back_btn_default_title",getString(R.string.setting));
    	startActivity(intent);  	
    }
    
    public void inviteFriend(View v){
    	progressDialog = ProgressDialog.show(this, null,
				getString(R.string.sending_request), true);
    	TelUserBean userBean = (TelUserBean) UserManager.getInstance().getUser();
    	String username = userBean.getName();
    	String countrycode = userBean.getCountryCode();
		HashMap<String,String> params = new HashMap<String,String>();
		params.put("username", username);
		params.put("countryCode", countrycode);
		HttpUtils.postSignatureRequest(getString(R.string.server_url)+getString(R.string.getInviteLink), 
				PostRequestFormat.URLENCODED, params,
				null, HttpRequestType.ASYNCHRONOUS, onFinishedGetInviteLink);
    }
    
    private OnHttpRequestListener onFinishedGetInviteLink = new OnHttpRequestListener() {

		@Override
		public void onFinished(HttpResponseResult responseResult) {

			inviteLink = responseResult.getResponseText();			
			dismiss();
			/*Intent intent = new Intent();
	        intent.setAction(Intent.ACTION_PICK);
	        intent.setData(ContactsContract.Contacts.CONTENT_URI);
	        startActivityForResult(intent, 0);*/
			HashMap<String,Object> params = new HashMap<String,Object>();
			params.put("inviteLink", inviteLink);
			SettingActivity.this.pushActivity(ContactLisInviteFriendActivity.class,params);
		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			dismiss();

			MyToast.show(SettingActivity.this, R.string.server_error, Toast.LENGTH_SHORT);
			
		}
	};
	
	/*protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 0) {

            if (resultCode == RESULT_OK) {

                if (data == null) {
                    return;
                }    
                
                Uri result = data.getData();
                final String[] phones = getPhoneNumbers(result);
                
                if(phones.length>0){
                	if(phones.length==1){
                		Uri uri = Uri.parse("smsto:" + phones[0]);
            			Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
            			String inviteMessage = getString(R.string.invite_message).replace("***", inviteLink);
            			intent.putExtra("sms_body", inviteMessage);
            			startActivity(intent);
                	}
                	else{
                		AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        				builder.setTitle(getString(R.string.choose_sms_number));
        				builder.setItems(phones,
        						new DialogInterface.OnClickListener(){

        							@Override
        							public void onClick(DialogInterface dialog,
        									int which) {
        								// TODO Auto-generated method stub
        								Uri uri = Uri.parse("smsto:" + phones[0]);
        		            			Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        		            			String inviteMessage = getString(R.string.invite_message).replace("***", inviteLink);
        		            			intent.putExtra("sms_body", inviteMessage);
        		            			startActivity(intent);
        							}      					
        						}
        						)
        				.setNegativeButton(getString(R.string.cancel), null);	
        				builder.show();
                	}
                }
                else{
                	MyToast.show(this, R.string.no_sms_number, Toast.LENGTH_SHORT);
                }
            }
        }
    }*/
	
	/*private String[] getPhoneNumbers(Uri contactData){
		String contactId = contactData.getLastPathSegment();
		Cursor cursor = managedQuery(contactData, null, null, null, null);
		cursor.moveToFirst();
		
		int phoneColumn = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER); 
		int phoneNum = cursor.getInt(phoneColumn);
		
		if(phoneNum>0){
			final String[] _projection = new String[] {Phone.NUMBER };
			final String _selection = Data.MIMETYPE + "=? and "+Phone.CONTACT_ID+"=?";
			final String[] _selectionArgs = new String[] { Phone.CONTENT_ITEM_TYPE,contactId };
			List<String> phones = new ArrayList<String>();
			
			Cursor phoneCursor = this.getContentResolver().query(Data.CONTENT_URI, _projection, _selection, _selectionArgs, null);
			if(phoneCursor!=null){
				while(phoneCursor.moveToNext()){
					String phone = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.NUMBER));
					//find the old record and update it
					if(phone!=null&&!phone.trim().equals(""))
						phones.add(phone);
				}
			}
			return phones.toArray(new String[]{});
		}
		else{
			return new String[]{};
		}
	}*/
    
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
    
    private OnClickListener inviteFriendListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			inviteFriend(null);
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
			//Log.d("update", "update!");
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
				String newpsw = ((EditText)modifyPSWPopupWindow.getContentView().
						findViewById(R.id.new_psw_editText)).getEditableText().toString().trim();

				UserManager.getInstance().getUser().setUserKey(userkey);
				UserManager.getInstance().getUser().setPassword(StringUtils.md5(newpsw));
				updatePreference(userkey,newpsw);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dismiss();
			if(modifyPSWPopupWindow!=null)
				modifyPSWPopupWindow.dismiss();
			MyToast.show(SettingActivity.this, R.string.change_psw_success, Toast.LENGTH_SHORT);
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
	
	private void setAreaCode(String areacode){
		TelUserBean telUserBean = (TelUserBean) UserManager.getInstance().getUser();
		telUserBean.setAreaCode(areacode);
		DataStorageUtils.putObject(TelUser.areaCode.name(), areacode);
	}
	
	private OnHttpRequestListener onFinishGetPSW = new OnHttpRequestListener(){

			@Override
			public void onFinished(HttpResponseResult responseResult) {
				// TODO Auto-generated method stub
				dismiss();
				int result = responseResult.getStatusCode();
				
				if(result == 200||result==201){
					builder.setMessage(R.string.get_psw_finish);
					dialog = builder.create();
					dialog.show();
					if(getPSWPopupWindow!=null)
						getPSWPopupWindow.dismiss();					
				}
			}

			@Override
			public void onFailed(HttpResponseResult responseResult) {
				// TODO Auto-generated method stub
				//Log.d(SystemConstants.TAG, responseResult.getStatusCode()+"");
				dismiss();
				MyToast.show(SettingActivity.this,
						R.string.phone_number_not_exist, Toast.LENGTH_SHORT);
			}
	    	
	    };
    
    private void saveSetupSetting(){
    	int launchPatternId = this.launchGroup.getCheckedRadioButtonId();
    	
    	if(launchPatternId==R.id.auto_launch_rbtn){
    		DataStorageUtils.putObject(LaunchSetting.autoLaunch.name(), "autoLaunch");
    	}
    	else if(launchPatternId==R.id.manual_launch_rbtn){
    		DataStorageUtils.putObject(LaunchSetting.autoLaunch.name(), "");
    	}
    	
    	int loginPatternId = this.loginGroup.getCheckedRadioButtonId();
    	TelUserBean userBean = (TelUserBean) UserManager.getInstance().getUser();
    	
    	if(loginPatternId==R.id.auto_login_rbtn){
    		if(!userBean.isRememberPwd()){
    			userBean.setRememberPwd(true);
    			DataStorageUtils.putObject(User.password.name(), userBean.getPassword());
    			DataStorageUtils.putObject(TelUser.vosphone.name(), userBean.getVosphone());
    			DataStorageUtils.putObject(TelUser.vosphone_pwd.name(), userBean.getVosphone_pwd());
    			DataStorageUtils.putObject(User.userkey.name(), userBean.getUserKey());
    		}
    	}
    	else if(loginPatternId==R.id.manual_login_rbtn){
    		userBean.setRememberPwd(false);
    		DataStorageUtils.putObject(User.password.name(), "");
			DataStorageUtils.putObject(TelUser.vosphone.name(), "");
			DataStorageUtils.putObject(TelUser.vosphone_pwd.name(), "");
			DataStorageUtils.putObject(User.userkey.name(), "");
    	}
    }
    
    //拨打设置
    private void saveDialPreference(){
    	int dialPatternId = this.dialGroup.getCheckedRadioButtonId();
    	int answerPatternId = this.answerGroup.getCheckedRadioButtonId();
    	
    	String dialPattern = getDialPattern(dialPatternId);
    	String answerPattern = getAnswerPattern(answerPatternId);
    	
    	DialPreferenceBean dialBean = DialPreferenceManager.getInstance().getDialPreferenceBean();
    	dialBean.setAnswerPattern(answerPattern);
    	dialBean.setDialPattern(dialPattern);
    	
    	DataStorageUtils.putObject(DialPreference.DialSetting.dialPattern.name(), dialPattern);
    	DataStorageUtils.putObject(DialPreference.DialSetting.answerPattern.name(), answerPattern);
    }
    
    private String getDialPattern(int id){
    	String result = "";
    	switch(id){
    	case R.id.radio_direct_btn:
    		result = DialPreference.DIRECT_DIAL;
    		break;
    	case R.id.radio_back_btn:
    		result = DialPreference.BACK_DIAL;
    		break;
    	case R.id.radio_manual_btn:
    		result = DialPreference.MANUAL_DIAL;
    		break;
    	}
    	return result;
    }
    
    private String getAnswerPattern(int id){
    	String result = "";
    	switch(id){
    	case R.id.answer_auto_btn:
    		result = DialPreference.AUTO_ANSWER;
    		break;
    	case R.id.answer_manual_btn:
    		result = DialPreference.MANUAL_ANSWER;
    		break;
    	}
    	return result;
    }
    
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

	class LaunchSetCodePopupWindow extends CommonPopupWindow {
		
		public LaunchSetCodePopupWindow(int resource, int width,
				int height, boolean focusable, boolean isBindDefListener) {
			super(resource, width, height, focusable, isBindDefListener);
		}
		
		public LaunchSetCodePopupWindow(int resource, int width,
				int height) {
			super(resource, width, height);
			UserBean user = UserManager.getInstance().getUser();
			if(user.isRememberPwd()){
				((RadioButton)getContentView().findViewById(R.id.auto_login_rbtn)).setChecked(true);
			}
			else
				((RadioButton)getContentView().findViewById(R.id.manual_login_rbtn)).setChecked(true);
			
			String auto = DataStorageUtils.getString(LaunchSetting.autoLaunch.name());
			if(auto!=null&&auto.equals("autoLaunch")){
				((RadioButton)getContentView().findViewById(R.id.auto_launch_rbtn)).setChecked(true);
			}
			else
				((RadioButton)getContentView().findViewById(R.id.manual_launch_rbtn)).setChecked(true);
		}
		
		@Override
		protected void bindPopupWindowComponentsListener() {
		
			// bind contact phone select cancel button click listener
			((Button) getContentView().findViewById(R.id.setup_confirmBtn))
					.setOnClickListener(new LaunchSetConfirmBtnOnClickListener());
			((Button)getContentView().findViewById(R.id.setup_cancelBtn)).setOnClickListener(
					new LaunchSetCancelBtnOnClickListener());
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

	class SetAreaCodePopupWindow extends CommonPopupWindow {
		
		public SetAreaCodePopupWindow(int resource, int width,
				int height, boolean focusable, boolean isBindDefListener) {
			super(resource, width, height, focusable, isBindDefListener);
		}
		
		public SetAreaCodePopupWindow(int resource, int width,
				int height) {
			super(resource, width, height);
			TelUserBean telUser = (TelUserBean) UserManager.getInstance().getUser();
			String areacode = telUser.getAreaCode();
			if(areacode!=null&&!areacode.equals(""))
				((EditText)getContentView().findViewById(R.id.set_areacode_editText)).setText(areacode);
		}
		
		@Override
		protected void bindPopupWindowComponentsListener() {
		
			// bind contact phone select cancel button click listener
			((Button) getContentView().findViewById(R.id.set_areacode_confirmBtn))
					.setOnClickListener(new SetAreaCodeConfirmBtnOnClickListener());
			((Button)getContentView().findViewById(R.id.set_areacode_cancelBtn)).setOnClickListener(
					new SetAreaCodeCancelBtnOnClickListener());
		}
		
		@Override
		protected void resetPopupWindow() {
			// hide contact phones select phone list view
			((EditText)getContentView().findViewById(R.id.set_areacode_editText)).setText("");
		}
		
		// inner class
		// contact phone select phone button on click listener
		class SetAreaCodeConfirmBtnOnClickListener implements OnClickListener {
		
			@Override
			public void onClick(View v) {		
				// dismiss contact phone select popup window		
				String areacode = ((EditText)getContentView().
							findViewById(R.id.set_areacode_editText)).getEditableText().toString().trim();
				
				if(!areacode.matches("(^[0-9]*)")){
					MyToast.show(SettingActivity.this, R.string.invalid_areacode, Toast.LENGTH_SHORT);
					return;
				}
				if(areacode==null||areacode.equals("")){
					MyToast.show(SettingActivity.this, R.string.null_areacode, Toast.LENGTH_SHORT);
					return;
				}
				InputMethodManager imm = (InputMethodManager)getSystemService(
						Context.INPUT_METHOD_SERVICE); 
				imm.hideSoftInputFromWindow(((EditText) getContentView().findViewById(R.id.set_areacode_editText))
						.getWindowToken(),0);
				setAreaCode(areacode);
				dismiss();
			}
		
		}
		
		// contact phone select cancel button on click listener
		class SetAreaCodeCancelBtnOnClickListener implements OnClickListener {
		
			@Override
			public void onClick(View v) {
				// dismiss contact phone select popup window
				InputMethodManager imm = (InputMethodManager)getSystemService(
				 		Context.INPUT_METHOD_SERVICE); 
				imm.hideSoftInputFromWindow(((EditText) getContentView()
						.findViewById(R.id.set_areacode_editText))
	    		 		.getWindowToken(),0);
				dismiss();
			}
		
		}
	
	}

	class SetDialPreferencePopupWindow extends CommonPopupWindow {
		
		public SetDialPreferencePopupWindow(int resource, int width,
				int height, boolean focusable, boolean isBindDefListener) {
			super(resource, width, height, focusable, isBindDefListener);
		}
		
		public SetDialPreferencePopupWindow(int resource, int width,
				int height) {
			super(resource, width, height);
			DialPreferenceBean dialBean = DialPreferenceManager.getInstance().getDialPreferenceBean();
			String dialPattern = dialBean.getDialPattern();
			String answerPattern = dialBean.getAnswerPattern();
			
			//Log.d("Setting Dial Preference", dialPattern+":"+answerPattern);
			
			if(dialPattern!=null){
				if(dialPattern.equals(DialPreference.DIRECT_DIAL)){
					((RadioButton)getContentView().findViewById(R.id.radio_direct_btn)).setChecked(true);
				}
				else if(dialPattern.equals(DialPreference.BACK_DIAL)){
					((RadioButton)getContentView().findViewById(R.id.radio_back_btn)).setChecked(true);
				}
				else if(dialPattern.equals(DialPreference.MANUAL_DIAL)){
					((RadioButton)getContentView().findViewById(R.id.radio_manual_btn)).setChecked(true);
				}
			}
			if(answerPattern!=null){
				if(answerPattern.equals(DialPreference.AUTO_ANSWER)){
					((RadioButton)getContentView().findViewById(R.id.answer_auto_btn)).setChecked(true);
				}
				else if(answerPattern.equals(DialPreference.MANUAL_ANSWER)){
					((RadioButton)getContentView().findViewById(R.id.answer_manual_btn)).setChecked(true);
				}
			}
		}
		
		@Override
		protected void bindPopupWindowComponentsListener() {
		
			// bind contact phone select cancel button click listener
			((Button) getContentView().findViewById(R.id.dial_preference_confirmBtn))
					.setOnClickListener(new SetAreaCodeConfirmBtnOnClickListener());
			((Button)getContentView().findViewById(R.id.dial_preference_cancelBtn)).setOnClickListener(
					new SetAreaCodeCancelBtnOnClickListener());
		}
		
		@Override
		protected void resetPopupWindow() {
			// hide contact phones select phone list view
		}
		
		// inner class
		// contact phone select phone button on click listener
		class SetAreaCodeConfirmBtnOnClickListener implements OnClickListener {
		
			@Override
			public void onClick(View v) {		
				// dismiss contact phone select popup window		
				saveDialPreference();
				dismiss();
			}
		
		}
		
		// contact phone select cancel button on click listener
		class SetAreaCodeCancelBtnOnClickListener implements OnClickListener {		
			@Override
			public void onClick(View v) {
				// dismiss contact phone select popup window
				dismiss();
			}	
		}
    }
	
	class SetAuthNumberPopupWindow extends CommonPopupWindow {
		private int lastSelectCountryCode = 0;
		
		
		public SetAuthNumberPopupWindow(int resource, int width,
				int height, boolean focusable, boolean isBindDefListener) {
			super(resource, width, height, focusable, isBindDefListener);
		}
		
		public SetAuthNumberPopupWindow(int resource, int width,
				int height) {
			super(resource, width, height);
			((Button)(this.getContentView().findViewById(R.id.setAuth_choose_country_btn)))
    		.setText(countryCodeManager.getCountryName(0));
			//Log.d("Setting Dial Preference", dialPattern+":"+answerPattern);
		}
		
		@Override
		protected void bindPopupWindowComponentsListener() {
			// bind contact phone select cancel button click listener
			((Button) getContentView().findViewById(R.id.set_auth_confirmBtn))
					.setOnClickListener(new SetAuthCodeConfirmBtnOnClickListener());
			((Button)getContentView().findViewById(R.id.set_auth_cancelBtn))
					.setOnClickListener(new SetAuthCodeCancelBtnOnClickListener());
			((Button)(this.getContentView().findViewById(R.id.setAuth_choose_country_btn)))
					.setOnClickListener(new OnClickListener(){
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
			((EditText)(SetAuthNumberPopupWindow.this.getContentView()
					.findViewById(R.id.set_auth_number_editText))).setText("");
		}
		
		public void chooseCountry(View v){
	    	AlertDialog.Builder chooseCountryDialogBuilder = new AlertDialog.Builder(SettingActivity.this);
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
				((Button)(SetAuthNumberPopupWindow.this.getContentView()
						.findViewById(R.id.setAuth_choose_country_btn)))
						.setText(countryCodeManager.getCountryName(which));
				chooseCountryDialog.dismiss();
			}    	
	    }
		
		// inner class
		// contact phone select phone button on click listener
		class SetAuthCodeConfirmBtnOnClickListener implements OnClickListener {
		
			@Override
			public void onClick(View v) {		
				// dismiss contact phone select popup window		
				//saveDialPreference();
				String phone = ((EditText)(SetAuthNumberPopupWindow.this.getContentView()
								.findViewById(R.id.set_auth_number_editText))).getText().toString().trim();
				String countrycode = countryCodeManager.getCountryCode(
		    			((Button)SetAuthNumberPopupWindow.this.getContentView().
		    					findViewById(R.id.setAuth_choose_country_btn))
		    				.getText().toString().trim());
				if(phone!=null&&!phone.equals("")){
					setAuthNumber(phone,countrycode);
					dismiss();
				}	
				else{
					MyToast.show(SettingActivity.this, R.string.phone_not_null, Toast.LENGTH_SHORT);
				}
			}
		}
		
		// contact phone select cancel button on click listener
		class SetAuthCodeCancelBtnOnClickListener implements OnClickListener {
			@Override
			public void onClick(View v) {
				// dismiss contact phone select popup window
				dismiss();
			}
		}
    }
	
	public void setAuthNumber(String phone,String country){
		Log.d("$$$$$", phone+":"+country);
		progressDialog = ProgressDialog.show(this, null,
				getString(R.string.sending_request), true);
    	TelUserBean userBean = (TelUserBean) UserManager.getInstance().getUser();
    	String username = userBean.getName();
    	String oldCountryCode = userBean.getCountryCode();
		HashMap<String,String> params = new HashMap<String,String>();
		params.put("username", username);
		params.put("countryCode", oldCountryCode);
		params.put("bindphone_country_code", country);
		params.put("bindphone", phone);
		HttpUtils.postSignatureRequest(getString(R.string.server_url)+getString(R.string.setBindPhone), 
				PostRequestFormat.URLENCODED, params,
				null, HttpRequestType.ASYNCHRONOUS, onFinishedSetBindPhone);
	}
	
	private OnHttpRequestListener onFinishedSetBindPhone = new OnHttpRequestListener() {

		@Override
		public void onFinished(HttpResponseResult responseResult) {
			MyToast.show(SettingActivity.this, R.string.set_bind_number_success, Toast.LENGTH_SHORT);			
			dismiss();
		}
		@Override
		public void onFailed(HttpResponseResult responseResult) {
			dismiss();
			MyToast.show(SettingActivity.this, R.string.server_error, Toast.LENGTH_SHORT);			
		}
	};
}
