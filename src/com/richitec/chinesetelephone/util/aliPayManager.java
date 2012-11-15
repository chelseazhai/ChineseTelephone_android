package com.richitec.chinesetelephone.util;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.richitec.chinesetelephone.alipay.AlixId;
import com.richitec.chinesetelephone.alipay.MobileSecurePayer;
import com.richitec.chinesetelephone.alipay.PartnerConfig;
import com.richitec.chinesetelephone.alipay.Rsa;
import com.richitec.chinesetelephone.bean.ProductBean;
import com.richitec.chinesetelephone.constant.AliPay;
import com.richitec.commontoolkit.user.UserManager;


public class AliPayManager {
	private Handler handler;
	private Activity activity;
	
	public AliPayManager(Handler h,Activity a){
		handler = h;
		activity = a;
	}
	
	
	public String getOrderInfo(ProductBean p) {
		String strOrderInfo = "partner=" + "\"" + PartnerConfig.PARTNER + "\"";
		strOrderInfo += "&";
		strOrderInfo += "seller=" + "\"" + PartnerConfig.SELLER + "\"";
		strOrderInfo += "&";
		strOrderInfo += "out_trade_no=" + "\"" + getOutTradeNo(AliPay.aliPayType) + "\"";
		strOrderInfo += "&";
		strOrderInfo += "subject=" + "\"" + p.getSubject() + "\"";
		strOrderInfo += "&";
		strOrderInfo += "body=" + "\"" + p.getBody() + "\"";
		strOrderInfo += "&";
		strOrderInfo += "total_fee=" + "\"" + p.getPrice() + "\"";
		strOrderInfo += "&";
		strOrderInfo += "notify_url=" + "\""
				+ "http://notify.java.jpxx.org/index.jsp" + "\"";
		
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
	
	public  boolean pay(ProductBean p){
		String orderInfo = getOrderInfo(p);
		String signType = getSignType();
		//String strsign = sign(orderInfo);
		String strsign = orderInfo;
		Log.v("sign:", strsign);
		// 对签名进行编码
		strsign = URLEncoder.encode(strsign);
		// 组装好参数
		String info = orderInfo + "&sign=" + "\"" + strsign + "\"" + "&"
				+ signType;
		Log.v("orderInfo:", info);
		
		// 调用pay方法进行支付
		/*MobileSecurePayer msp = new MobileSecurePayer();
		boolean bRet = msp.pay(info, handler, AlixId.RQF_PAY, activity);*/
		Thread t = null;
		try{
			t = new Thread(){
				@Override
				public void run(){
					Message msg = new Message();
					msg.what = 12;
					msg.obj = "haha";
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					handler.sendMessage(msg);
				}
			};
			return true;
		}
		finally{
			t.start();
		}
		
	}
	
}
