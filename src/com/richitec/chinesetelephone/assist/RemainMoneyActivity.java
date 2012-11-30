package com.richitec.chinesetelephone.assist;

import java.math.BigDecimal;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.bean.TelUserBean;
import com.richitec.commontoolkit.activityextension.NavigationActivity;
import com.richitec.commontoolkit.customcomponent.BarButtonItem;
import com.richitec.commontoolkit.customcomponent.BarButtonItem.BarButtonItemStyle;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.HttpUtils;
import com.richitec.commontoolkit.utils.MyToast;
import com.richitec.commontoolkit.utils.HttpUtils.HttpRequestType;
import com.richitec.commontoolkit.utils.HttpUtils.HttpResponseResult;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;
import com.richitec.commontoolkit.utils.HttpUtils.PostRequestFormat;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class RemainMoneyActivity extends NavigationActivity {
	public static String BALANCE = "balance";
	private double balance;
	private ProgressDialog progressDialog;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remain_money_layout);
        
        this.setRightBarButtonItem(new BarButtonItem(
        		this,BarButtonItemStyle.RIGHT_GO,R.string.charge_title_popwin,chargeBtnListener));
        
        setTitle(R.string.get_remain_money_title);

        //MyToast.show(this, "balance:"+balance, Toast.LENGTH_SHORT);
        
        String username = UserManager.getInstance().getUser().getName();      
        ((TextView)findViewById(R.id.uername)).setText(username);    
    }
    
    @Override
	public void onResume(){
		getRemainMoney();
		super.onResume();
	} 
    
    public static double formatRemainMoney(String money){
    	BigDecimal b = new BigDecimal(money); 
    	double result = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    	return result;
    }

	private void getRemainMoney(){
    	TelUserBean userBean = (TelUserBean) UserManager.getInstance().getUser();
    	String username = userBean.getName();
    	String countryCode = userBean.getRegistCountryCode();
    	
    	progressDialog = ProgressDialog.show(this, null,
				getString(R.string.sending_request), true);
    	
    	HashMap<String,String> params = new HashMap<String,String>();
    	params.put("username", username);
    	params.put("countryCode", countryCode);
    	
    	HttpUtils.postSignatureRequest(getString(R.string.server_url)+getString(R.string.account_balance_url), 
				PostRequestFormat.URLENCODED, params,
				null, HttpRequestType.ASYNCHRONOUS, onFinishedGetBalance);
    }
    
    private void dismiss(){
    	if(progressDialog!=null)
    		progressDialog.dismiss();
    }
    
    private OnClickListener chargeBtnListener = new OnClickListener(){
	
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			/*Intent intent = new Intent(RemainMoneyActivity.this,AccountChargeActivity.class);
			intent.putExtra("nav_back_btn_default_title",getString(R.string.back));
			startActivity(intent);*/
			RemainMoneyActivity.this.pushActivity(AccountChargeActivity.class);
			//RemainMoneyActivity.this.pushActivity(activityClass)
		}
	};
	private OnHttpRequestListener onFinishedGetBalance = new OnHttpRequestListener() {

		@Override
		public void onFinished(HttpResponseResult responseResult) {
			dismiss();
			JSONObject data;
			try {
				data = new JSONObject(
						responseResult.getResponseText());
				balance = RemainMoneyActivity.formatRemainMoney(data.getDouble("balance")+"");
				
				int callTime = (int) (balance*10/2);		        
		        int backcallTime = (int) (balance*6);
		        
		        ((TextView)findViewById(R.id.remain_money)).setText(String.valueOf(balance)+getString(R.string.yuan));
		        ((TextView)findViewById(R.id.direct_call)).setText(String.valueOf(callTime)+getString(R.string.minute));
		        ((TextView)findViewById(R.id.back_call)).setText(String.valueOf(backcallTime)+getString(R.string.minute));
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			dismiss();
			MyToast.show(RemainMoneyActivity.this, R.string.get_balance_error, Toast.LENGTH_SHORT);
		}
	};
}
