package com.richitec.chinesetelephone.autolaunch;

import com.richitec.chinesetelephone.ChineseTelephoneAppLaunchActivity;
import com.richitec.chinesetelephone.constant.LaunchSetting;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
//自动开机启动
public class BootCompletedReceiver extends BroadcastReceiver {

	private  String ACTION = "android.intent.action.BOOT_COMPLETED";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.d("autoLaunch", "out");
		if(intent.getAction().equals(ACTION)&&isAutoLaunch(context)){
			Log.d("autoLaunch", "in");
			Intent start = new Intent(context,ChineseTelephoneAppLaunchActivity.class);
			start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			context.startActivity(start);
		}
	}
	
	private boolean isAutoLaunch(Context context){
		SharedPreferences _sharedPreferences = context.getSharedPreferences(
						"sharedPreferencesDataStorage"
						, Activity.MODE_PRIVATE);
		//String autoLaunch = DataStorageUtils.getString(LaunchSetting.autoLaunch.name());
		if(_sharedPreferences.contains(LaunchSetting.autoLaunch.name())){
			Log.d("contain", "contain");
			String _ret = _sharedPreferences.getString(LaunchSetting.autoLaunch.name(), null);
			Log.d("ret", _ret);
			if(_ret!=null&&_ret.equals("autoLaunch")){
				return true;
			}
		}
		Log.d("notcontain", "notcontain");
		return false;
	}

}
