package com.richitec.chinesetelephone.bean;

public class ProductBean {
	private String subject;
	private String body;
	private String price;
	private int chargeMoneyId;
	private final String notify_url = "http://www.00244dh.com/angola/alipayClientComplete";

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getNotify_url() {
		return notify_url;
	}

	public int getChargeMoneyId() {
		return chargeMoneyId;
	}

	public void setChargeMoneyId(int chargeMoneyId) {
		this.chargeMoneyId = chargeMoneyId;
	}

}
