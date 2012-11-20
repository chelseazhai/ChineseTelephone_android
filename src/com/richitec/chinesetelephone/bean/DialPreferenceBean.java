package com.richitec.chinesetelephone.bean;

public class DialPreferenceBean {
	private String dialPattern = "manual_dial";
	private String answerPattern = "auto_answer";
	public String getDialPattern() {
		return dialPattern;
	}
	public void setDialPattern(String dialPattern) {
		this.dialPattern = dialPattern;
	}
	public String getAnswerPattern() {
		return answerPattern;
	}
	public void setAnswerPattern(String answerPatter) {
		this.answerPattern = answerPatter;
	}
}
