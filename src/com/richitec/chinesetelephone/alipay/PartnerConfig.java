package com.richitec.chinesetelephone.alipay;

public class PartnerConfig {

	// 合作商户ID。用签约支付宝账号登录ms.alipay.com后，在账户信息页面获取。
	public static String PARTNER = "";
	// 商户收款的支付宝账号
	public static String SELLER = "";
	// 商户（RSA）私钥
	public static String RSA_PRIVATE = "";
			// 支付宝（RSA）公钥 用签约支付宝账号登录ms.alipay.com后，在密钥管理页面获取。
	public static String RSA_ALIPAY_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCshUVa4r8YkUloeJJX5j5jTAr/AdXRj9cQg0+mZgD11k8iIbk/TO7MhN/Cbi+wAOKaaP4GPiOmM34aQnG9Xq0bNq7s4h5osFSO5pArJOfBthZiMy2uXlhJg+XPBx+FItKOfCfr/tomhTz2Pc/K4SOIwC4fAqYVNCp2KzfmyV+JoQIDAQAB";
	// 支付宝安全支付服务apk的名称，必须与assets目录下的apk名称一致
	public static String ALIPAY_PLUGIN_NAME = "alipay_plugin_20120428msp.apk";

}
