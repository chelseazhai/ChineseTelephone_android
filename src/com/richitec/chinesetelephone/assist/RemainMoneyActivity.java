package com.richitec.chinesetelephone.assist;

import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.customcomponent.TelephoneBarButtonItem;
import com.richitec.chinesetelephone.customcomponent.TelephoneNavigationActivity;
import com.richitec.commontoolkit.customcomponent.BarButtonItem.BarButtonItemStyle;
import com.richitec.commontoolkit.user.UserManager;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class RemainMoneyActivity extends TelephoneNavigationActivity {
	public static String BALANCE = "balance";
	private double balance;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remain_money_layout);
        
        this.setRightBarButtonItem(new TelephoneBarButtonItem(
        		this,BarButtonItemStyle.LEFT_BACK,R.string.charge_title_popwin,chargeBtnListener));
        
        setTitle(R.string.get_remain_money_title);
        
        balance = getIntent().getDoubleExtra(BALANCE, 0.0);
        //MyToast.show(this, "balance:"+balance, Toast.LENGTH_SHORT);
        
        String username = UserManager.getInstance().getUser().getName();
        
        double callTime = balance*10;
        
        double backcallTime = balance*6;
        
        ((TextView)findViewById(R.id.uername)).setText(username);
        ((TextView)findViewById(R.id.remain_money)).setText(String.valueOf(balance)+getString(R.string.yuan));
        ((TextView)findViewById(R.id.direct_call)).setText(String.valueOf(callTime)+getString(R.string.minute));
        ((TextView)findViewById(R.id.back_call)).setText(String.valueOf(backcallTime)+getString(R.string.minute));
    }
    
    private OnClickListener chargeBtnListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(RemainMoneyActivity.this,AccountChargeActivity.class);
			intent.putExtra(BALANCE, balance);
			startActivity(intent);
		}
    	
    };
}
