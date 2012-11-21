package com.rictitec.chinesetelephone.utils;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.alipay.AlixId;
import com.richitec.chinesetelephone.alipay.MobileSecurePayer;
import com.richitec.chinesetelephone.alipay.PartnerConfig;
import com.richitec.chinesetelephone.alipay.Rsa;
import com.richitec.chinesetelephone.bean.ProductBean;
import com.richitec.chinesetelephone.bean.TelUserBean;
import com.richitec.chinesetelephone.constant.AliPay;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.HttpUtils;
import com.richitec.commontoolkit.utils.HttpUtils.HttpRequestType;
import com.richitec.commontoolkit.utils.HttpUtils.HttpResponseResult;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;
import com.richitec.commontoolkit.utils.HttpUtils.PostRequestFormat;


public class AliPayManager {
	private String orderInfo;
	private String out_trade_no;
	private Handler handler;
	private Activity activity;
	
	public AliPayManager(Handler h,Activity a){
		handler = h;
		activity = a;
	}
	
	
	public String getOrderInfo(ProductBean p) {
		out_trade_no = getOutTradeNo(AliPay.aliPayType);
		String strOrderInfo = "partner=" + "\"" + PartnerConfig.PARTNER + "\"";
		strOrderInfo += "&";
		strOrderInfo += "seller=" + "\"" + PartnerConfig.SELLER + "\"";
		strOrderInfo += "&";
		strOrderInfo += "out_trade_no=" + "\"" + out_trade_no + "\"";
		strOrderInfo += "&";
		strOrderInfo += "subject=" + "\"" + p.getSubject() + "\"";
		strOrderInfo += "&";
		strOrderInfo += "body=" + "\"" + p.getBody() + "\"";
		strOrderInfo += "&";
		strOrderInfo += "total_fee=" + "\"" + p.getPrice() + "\"";
		strOrderInfo += "&";
		strOrderInfo += "notify_url=" + "\""
				+ URLEncoder.encode(p.getNotify_url()) + "\"";
		
		return strOrderInfo;
	}
	
	public String getOutTradeNo(String type) {
		
		Date currTime = new Date();
		SimpleDateFormat sf = new SimpleDateFormat("_yyyyMMdd_HHmmss_",
				Locale.US);
		String returnStr =  type + sf.format(currTime) + UserManager.getInstance().getUser().getName() + "_";

		java.util.Random r = new java.util.Random();
		
		returnStr = returnStr + r.nextInt();
		
		return returnStr;
	}
	
	public boolean checkInfo() {
		String partner = PartnerConfig.PARTNER;
		String seller = PartnerConfig.SELLER;
		if (partner == null || partner.length() <= 0 || seller == null
				|| seller.length() <= 0)
			return false;

		return true;
	}
	
	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param signType
	 *            签名方式
	 * @param content
	 *            待签名订单信息
	 * @return
	 */
	public String sign(String content) {
		return Rsa.sign(content, PartnerConfig.RSA_PRIVATE);
	}
	
	public String getSignType() {
		String getSignType = "sign_type=" + "\"" + "RSA" + "\"";
		return getSignType;
	}
	
	public boolean pay(ProductBean p){
		TelUserBean userBean = (TelUserBean) UserManager.getInstance().getUser();
		orderInfo = getOrderInfo(p);
		//String signType = getSignType();
		//get sign form server
		HashMap<String,String> params = new HashMap<String,String>();
		params.put("total_fee", p.getPrice());
    	params.put("out_trade_no", out_trade_no);
    	params.put("content", orderInfo);
    	params.put("countryCode", userBean.getCountryCode());
		
    	HttpUtils.postSignatureRequest(activity.getString(R.string.server_url)+activity.getString(R.string.alipay_sign), 
				PostRequestFormat.URLENCODED, params,
				null, HttpRequestType.ASYNCHRONOUS, onAlipaySignFinishListener);	
		
		/*String strsign = sign(orderInfo);
		Log.v("sign:", strsign);
		// 对签名进行编码
		strsign = URLEncoder.encode(strsign);
		// 组装好参数
		String info = orderInfo + "&sign=" + "\"" + strsign + "\"" + "&"
				+ signType;
		Log.v("orderInfo:", info);
		
		// 调用pay方法进行支付
		MobileSecurePayer msp = new MobileSecurePayer();
		boolean bRet = msp.pay(info, handler, AlixId.RQF_PAY, activity);
		return bRet;*/
    	return true;
	}
	
	private OnHttpRequestListener onAlipaySignFinishListener = new OnHttpRequestListener(){

		@Override
		public void onFinished(HttpResponseResult responseResult) {
			// TODO Auto-generated method stub
			String signType = getSignType();
			String strsign = responseResult.getResponseText();
			Log.v("sign:", strsign);
			// 对签名进行编码
			strsign = URLEncoder.encode(strsign);
			// 组装好参数
			String info = orderInfo + "&sign=" + "\"" + strsign + "\"" + "&"
					+ signType;
			Log.v("orderInfo:", info);
			
			// 调用pay方法进行支付
			MobileSecurePayer msp = new MobileSecurePayer();
			msp.pay(info, handler, AlixId.RQF_PAY, activity);
		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			// TODO Auto-generated method stub
			Message msg = new Message();
			msg.what = AlixId.RQF_PAY;
			msg.obj = "服务器签名错误，请重试";
			handler.sendMessage(msg);
		}
		
	};
	
}
