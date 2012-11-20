package com.richitec.chinesetelephone.bean;

public class ProductBean {
	private String subject;
	private String body;
	private String price;
	private final String notify_url = "http://122.96.24.173/angola/alipayComplete";
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
}
