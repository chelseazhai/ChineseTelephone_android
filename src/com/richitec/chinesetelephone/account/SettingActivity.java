package com.richitec.chinesetelephone.account;

import com.richitec.chinesetelephone.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.support.v4.app.NavUtils;

public class SettingActivity extends Activity {
	
	public static String TITLE_NAME = "titlename";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        
        LinearLayout changeAccount = (LinearLayout) findViewById(R.id.account_setting_btn);        
        changeAccount.setOnClickListener(changeAccountListener);
        
        LinearLayout modifyPSW = (LinearLayout)findViewById(R.id.account_changePSW_btn);
        
    } 
    
    private OnClickListener changeAccountListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(SettingActivity.this,AccountSettingActivity.class);
			intent.putExtra(TITLE_NAME, getString(R.string.change_account_title));
			startActivity(intent);
		}
    	
    };
    
    
}
