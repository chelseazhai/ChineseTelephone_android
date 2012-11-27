package com.rictitec.chinesetelephone.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.account.AccountSettingActivity;
import com.richitec.chinesetelephone.bean.TelUserBean;
import com.richitec.chinesetelephone.sip.SipRegisterBean;
import com.richitec.chinesetelephone.sip.SipUtils;
import com.richitec.chinesetelephone.sip.listeners.SipRegistrationStateListener;
import com.richitec.chinesetelephone.tab7tabcontent.ChineseTelephoneTabActivity;
import com.richitec.commontoolkit.user.UserManager;

public class SipRegisterManager {
	
	private Context context;
	private AlertDialog dialog;
	
	public SipRegisterManager(Context c){
		context = c;
		
		dialog = new AlertDialog.Builder(context)
		.setTitle(R.string.alert_title)
		.setMessage("网络电话登录失败，请重新登录！")
		.setPositiveButton(context.getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(
							DialogInterface arg0, int arg1) {
						Intent intent = new Intent(context, 
								AccountSettingActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
						((Activity)context).startActivity(intent);
						arg0.dismiss();
						((Activity)context).finish();					
					}
				}).create();
	}
	
	public static void registSip(SipRegistrationStateListener sipRegistStateListener){
		
		TelUserBean userBean = (TelUserBean) UserManager.getInstance().getUser();	
		String sipName = userBean.getVosphone();
		String sipPsw = userBean.getVosphone_pwd();
		
		if(sipName!=null&&!sipName.equals("")&&sipPsw!=null&&!sipPsw.equals("")){
			// test by ares
			// generate sip register account
			SipRegisterBean _sipAccount = new SipRegisterBean();
	
			// set test sip account
			_sipAccount.setSipUserName(sipName);
			_sipAccount.setSipPwd(sipPsw);
			_sipAccount.setSipServer("112.132.217.13");
			_sipAccount.setSipDomain("richitec.com");
			_sipAccount.setSipRealm("richitec.com");
		
			SipUtils.registerSipAccount(_sipAccount,sipRegistStateListener);
			//SipUtils.registerSipAccount(_sipAccount,sipRegistrationStateListener);
		}
	}

	private SipRegistrationStateListener sipRegistrationStateListener = new SipRegistrationStateListener(){

		@Override
		public void onRegisterSuccess() {
			// TODO Auto-generated method stub
			//do nothing
			Log.d("ChineseTelephoneTabActivity", "regist success");
		}

		@Override
		public void onRegisterFailed() {
			// TODO Auto-generated method stub
			sipRegistFail();
		}

		@Override
		public void onUnRegisterSuccess() {
			// TODO Auto-generated method stub
			//sipRegistFail();
			Log.d("ChineseTelephoneTabActivity", "unregist success");
		}

		@Override
		public void onUnRegisterFailed() {
			// TODO Auto-generated method stub
			Log.d("ChineseTelephoneTabActivity", "unregist fail");
		}
		
	};
	
	private void sipRegistFail(){
		dialog.show();
	}
}
