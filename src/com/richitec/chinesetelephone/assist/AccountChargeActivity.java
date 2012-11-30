package com.richitec.chinesetelephone.assist;

import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.alipay.AlixId;
import com.richitec.chinesetelephone.alipay.BaseHelper;
import com.richitec.chinesetelephone.alipay.MobileSecurePayHelper;
import com.richitec.chinesetelephone.alipay.MyRC4;
import com.richitec.chinesetelephone.alipay.PartnerConfig;
import com.richitec.chinesetelephone.alipay.ResultChecker;
import com.richitec.chinesetelephone.bean.ProductBean;
import com.richitec.chinesetelephone.bean.TelUserBean;
import com.richitec.chinesetelephone.constant.AliPay;
import com.richitec.chinesetelephone.utils.AliPayManager;
import com.richitec.commontoolkit.activityextension.NavigationActivity;
import com.richitec.commontoolkit.customcomponent.CommonPopupWindow;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.HttpUtils;
import com.richitec.commontoolkit.utils.HttpUtils.HttpResponseResult;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;
import com.richitec.commontoolkit.utils.MyToast;
import com.richitec.commontoolkit.utils.HttpUtils.HttpRequestType;
import com.richitec.commontoolkit.utils.HttpUtils.PostRequestFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AccountChargeActivity extends NavigationActivity {
	private static MobileSecurePayHelper mspHelper= null;
	private LinearLayout mainLayout;
	private LinearLayout contentLayout;
	private ProgressDialog mProgress = null;
	private final String TAG = "AccountChargeActivity";
	
	private ChargeMoneyPopupWindow chargeMoneyPopupWindow = new ChargeMoneyPopupWindow(
			R.layout.charge_money_popupwindow_layout,
			LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT
			);
	
    // close the progress bar
	// 关闭进度框
	private void closeProgress() {
		try {
			if (mProgress != null) {
				mProgress.dismiss();
				mProgress = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void chargeMoney(double money){
		boolean isMobile_spExist = mspHelper.detectMobile_sp();
		if (!isMobile_spExist)
			return;
		
		chargeMoneyPopupWindow.dismiss();
		final double m = money;
		
		AlertDialog.Builder tDialog = new AlertDialog.Builder(this);
		tDialog.setIcon(R.drawable.alipay_install_info);
		tDialog.setTitle(getString(R.string.ensure_charge_title));
		tDialog.setMessage(getString(R.string.charge_alipay_hint).replace("***", money+""));
		tDialog.setNegativeButton(getString(R.string.cancel), null);
		tDialog.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				ProductBean p = new ProductBean();
		    	p.setBody(AliPay.aliPayBody);
		    	p.setSubject(AliPay.aliPaySubject);
		    	p.setPrice(m+"");
				charging(p);
			}			
		});
    	tDialog.show();
	}
	
	private void getRemainMoney(){
    	TelUserBean userBean = (TelUserBean) UserManager.getInstance().getUser();
    	String username = userBean.getName();
    	String countryCode = userBean.getRegistCountryCode();
    	
    	mProgress = ProgressDialog.show(this, null,
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
			closeProgress();
			JSONObject data;
			try {
				data = new JSONObject(
						responseResult.getResponseText());
				double balance = RemainMoneyActivity.formatRemainMoney(data.getDouble("balance")+"");
				String remainBalanceStr = AccountChargeActivity.this.getString(R.string.remain_balance_textfield);
		        remainBalanceStr += balance + getString(R.string.yuan);
		        ((TextView)findViewById(R.id.remain_balance)).setText(remainBalanceStr);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			closeProgress();
			MyToast.show(AccountChargeActivity.this, R.string.get_balance_error, Toast.LENGTH_SHORT);
		}
	};
	
	private void charging(ProductBean p){
    	AliPayManager aliPayManager = new AliPayManager(mHandler,this);
    	if(aliPayManager.checkInfo()){
        	
	    	aliPayManager.pay(p);	    	
			// show the progress bar to indicate that we have started
			// paying.
			// 显示“正在支付”进度条
			closeProgress();
			mProgress = BaseHelper.showProgress(
					this, null, getString(R.string.is_charging),
					false,true);
    	}
    	else{
    		BaseHelper
			.showDialog(
					this,
					"提示",
					"缺少partner或者seller，请在PartnerConfig.java中增加。",
					R.drawable.infoicon);
    		return;
    	}
    }

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_charge_layout);
        
    	mspHelper = new MobileSecurePayHelper(AccountChargeActivity.this);
        
        //MyToast.show(this, "balance:"+balance, Toast.LENGTH_SHORT);
        
        setTitle(R.string.charge_title_popwin);
        getRemainMoney();
    }
    
    public void aliPayBtnAction(View v){
    	boolean isMobile_spExist = mspHelper.detectMobile_sp();
		if (!isMobile_spExist)
			return;
		
		if(PartnerConfig.PARTNER.equals("")){
			
			mProgress = ProgressDialog.show(this, null,
					getString(R.string.sending_request), true);
			
			TelUserBean telUser = (TelUserBean) UserManager.getInstance().getUser();
			HashMap<String,String> params = new HashMap<String,String>();
	    	params.put("countryCode", telUser.getRegistCountryCode());
			
	    	HttpUtils.postSignatureRequest(getString(R.string.server_url)+getString(R.string.get_seller_partner_key), 
					PostRequestFormat.URLENCODED, params,
					null, HttpRequestType.ASYNCHRONOUS, onGetPrivateKeyListener);	
		}
		else{
	    	mainLayout = (LinearLayout) findViewById(R.id.main_charge_layout);
	    	mainLayout.setVisibility(View.GONE);
	    	contentLayout = (LinearLayout) findViewById(R.id.alipay_charge_content);
	    	contentLayout.setVisibility(View.VISIBLE);
		}
    }
    
    private OnHttpRequestListener onGetPrivateKeyListener = new OnHttpRequestListener(){

		@Override
		public void onFinished(HttpResponseResult responseResult) {
			// TODO Auto-generated method stub
			closeProgress();
			TelUserBean telUser = (TelUserBean) UserManager.getInstance().getUser();
			String encryStr = responseResult.getResponseText();

			String decryData = MyRC4.decryptPro(encryStr, telUser.getUserKey());
		
			try {
				JSONObject data = new JSONObject(decryData);
				String partnerId = data.getString("partner_id");
				String sellerId = data.getString("seller");
				//String private_key = data.getString("private_key");
				
				PartnerConfig.PARTNER = partnerId;
				PartnerConfig.SELLER = sellerId;
				//PartnerConfig.RSA_PRIVATE = private_key;
				
				mainLayout = (LinearLayout) findViewById(R.id.main_charge_layout);
		    	mainLayout.setVisibility(View.GONE);
		    	contentLayout = (LinearLayout) findViewById(R.id.alipay_charge_content);
		    	contentLayout.setVisibility(View.VISIBLE);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			// TODO Auto-generated method stub
			closeProgress();
			MyToast.show(AccountChargeActivity.this, "获取秘钥错误，请重试", Toast.LENGTH_SHORT);
		}
    	
    };
    
    public void backMainChargeAction(View v){
    	if(contentLayout!=null)
    		contentLayout.setVisibility(View.GONE);
    	if(mainLayout!=null)
    		mainLayout.setVisibility(View.VISIBLE);
    }
    
    public void charge100Action(View v){
    	boolean isMobile_spExist = mspHelper.detectMobile_sp();
		if (!isMobile_spExist)
			return;
    	
		AlertDialog.Builder tDialog = new AlertDialog.Builder(this);
		tDialog.setIcon(R.drawable.alipay_install_info);
		tDialog.setTitle(getString(R.string.ensure_charge_title));
		tDialog.setMessage(getString(R.string.charge_alipay_hint).replace("***", 100+""));
		tDialog.setNegativeButton(getString(R.string.cancel), null);
		tDialog.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				ProductBean p = new ProductBean();
		    	p.setBody(AliPay.aliPayBody);
		    	p.setSubject(AliPay.aliPaySubject);
		    	p.setPrice(100+"");
				charging(p);
			}			
		});
    	tDialog.show();
    	
    }
    
    public void charge50Action(View v){
    	boolean isMobile_spExist = mspHelper.detectMobile_sp();
		if (!isMobile_spExist)
			return;
		
		AlertDialog.Builder tDialog = new AlertDialog.Builder(this);
		tDialog.setIcon(R.drawable.alipay_install_info);
		tDialog.setTitle(getString(R.string.ensure_charge_title));
		tDialog.setMessage(getString(R.string.charge_alipay_hint).replace("***", 50+""));
		tDialog.setNegativeButton(getString(R.string.cancel), null);
		tDialog.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				ProductBean p = new ProductBean();
		    	p.setBody(AliPay.aliPayBody);
		    	p.setSubject(AliPay.aliPaySubject);
		    	p.setPrice(50+"");
				charging(p);
			}			
		});
    	tDialog.show();
    }
    
    public void charge30Action(View v){
    	boolean isMobile_spExist = mspHelper.detectMobile_sp();
		if (!isMobile_spExist)
			return;
		
		AlertDialog.Builder tDialog = new AlertDialog.Builder(this);
		tDialog.setIcon(R.drawable.alipay_install_info);
		tDialog.setTitle(getString(R.string.ensure_charge_title));
		tDialog.setMessage(getString(R.string.charge_alipay_hint).replace("***", 30+""));
		tDialog.setNegativeButton(getString(R.string.cancel), null);
		tDialog.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				ProductBean p = new ProductBean();
		    	p.setBody(AliPay.aliPayBody);
		    	p.setSubject(AliPay.aliPaySubject);
		    	p.setPrice(30+"");
				charging(p);
			}			
		});
    	tDialog.show();
    }
    
    public void chargeOtherAction(View v){
    	chargeMoneyPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }
	
	class ChargeMoneyPopupWindow extends CommonPopupWindow {
		
		public ChargeMoneyPopupWindow(int resource, int width,
				int height, boolean focusable, boolean isBindDefListener) {
			super(resource, width, height, focusable, isBindDefListener);
		}
		
		public ChargeMoneyPopupWindow(int resource, int width,
				int height) {
			super(resource, width, height);
		}
		
		@Override
		protected void bindPopupWindowComponentsListener() {
		
			// bind contact phone select cancel button click listener
			((Button) getContentView().findViewById(R.id.charge_confirmBtn))
					.setOnClickListener(new ChargeConfirmBtnOnClickListener());
			((Button)getContentView().findViewById(R.id.charge_cancelBtn)).setOnClickListener(
					new ChargeCancelBtnOnClickListener());
		}
		
		@Override
		protected void resetPopupWindow() {
			// hide contact phones select phone list view
			((EditText)getContentView().findViewById(R.id.charge_money_editText)).setText("");
		}
		
		// inner class
		// contact phone select phone button on click listener
		class ChargeConfirmBtnOnClickListener implements OnClickListener {
		
			@Override
			public void onClick(View v) {		
				// dismiss contact phone select popup window		
				String chargeStr = ((EditText)getContentView().
							findViewById(R.id.charge_money_editText)).getEditableText().toString().trim();
				try{
					double charge = Double.parseDouble(chargeStr);
					InputMethodManager imm = (InputMethodManager)getSystemService(
							Context.INPUT_METHOD_SERVICE); 
					imm.hideSoftInputFromWindow(((EditText) getContentView().findViewById(R.id.charge_money_editText))
							.getWindowToken(),0);
					chargeMoney(charge);
				}
				catch(Exception e){
					MyToast.show(AccountChargeActivity.this, 
							R.string.input_valid_charge_money, Toast.LENGTH_SHORT);
					return;
				}
			}
		
		}
		
		// contact phone select cancel button on click listener
		class ChargeCancelBtnOnClickListener implements OnClickListener {
		
			@Override
			public void onClick(View v) {
				// dismiss contact phone select popup window
				InputMethodManager imm = (InputMethodManager)getSystemService(
				 		Context.INPUT_METHOD_SERVICE); 
				imm.hideSoftInputFromWindow(((EditText) getContentView().findViewById(R.id.charge_money_editText))
        		 		.getWindowToken(),0);
				dismiss();
			}
		
		}

    }
    
 // the handler use to receive the pay result.
 	// 这里接收支付结果，支付宝手机端同步通知
 	private Handler mHandler = new Handler() {
 		public void handleMessage(Message msg) {
 			try {
 				String strRet = (String) msg.obj;
 				//Log.e(TAG, strRet);	// strRet范例：resultStatus={9000};memo={};result={partner="2088201564809153"&seller="2088201564809153"&out_trade_no="050917083121576"&subject="123456"&body="2010新款NIKE 耐克902第三代板鞋 耐克男女鞋 386201 白红"&total_fee="0.01"&notify_url="http://notify.java.jpxx.org/index.jsp"&success="true"&sign_type="RSA"&sign="d9pdkfy75G997NiPS1yZoYNCmtRbdOP0usZIMmKCCMVqbSG1P44ohvqMYRztrB6ErgEecIiPj9UldV5nSy9CrBVjV54rBGoT6VSUF/ufjJeCSuL510JwaRpHtRPeURS1LXnSrbwtdkDOktXubQKnIMg2W0PreT1mRXDSaeEECzc="}
 				switch (msg.what) {
 				case AlixId.RQF_PAY: {
 					//
 					closeProgress();

 					BaseHelper.log(TAG, strRet);

 					// 处理交易结果
 					try {
 						// 获取交易状态码，具体状态代码请参看文档
 						String tradeStatus = "resultStatus={";
 						int imemoStart = strRet.indexOf("resultStatus=");
 						imemoStart += tradeStatus.length();
 						int imemoEnd = strRet.indexOf("};memo=");
 						tradeStatus = strRet.substring(imemoStart, imemoEnd);
 						
 						//先验签通知
 						ResultChecker resultChecker = new ResultChecker(strRet);
 						int retVal = resultChecker.checkSign();
 						// 验签失败
 						if (retVal == ResultChecker.RESULT_CHECK_SIGN_FAILED) {
 							BaseHelper.showDialog(
 									AccountChargeActivity.this,
 									"提示",
 									getResources().getString(
 											R.string.check_sign_failed),
 									android.R.drawable.ic_dialog_alert);
 						} else {// 验签成功。验签成功后再判断交易状态码
 							if(tradeStatus.equals("9000")){//判断交易状态码，只有9000表示交易成功
 								getRemainMoney();
 								BaseHelper.showDialog(AccountChargeActivity.this, "提示","支付成功。交易状态码："+tradeStatus, R.drawable.infoicon);
 							}
 							else
 							BaseHelper.showDialog(AccountChargeActivity.this, "提示", "支付失败。交易状态码:"
 									+ tradeStatus, R.drawable.infoicon);
 						}

 					} catch (Exception e) {
 						e.printStackTrace();
 						BaseHelper.showDialog(AccountChargeActivity.this, "提示", strRet,
 								R.drawable.infoicon);
 					}
 				}
 					break;
 				default :
 					closeProgress();
 				}

 				super.handleMessage(msg);
 			} catch (Exception e) {
 				e.printStackTrace();
 			}
 		}
 	};
}
