package com.richitec.chinesetelephone.sip;

import java.io.Serializable;

public class SipRegisterBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7727041894097559324L;

	// sip server
	private String sipServer;
	// sip user name
	private String sipUserName;
	// sip password
	private String sipPwd;
	// sip domain
	private String sipDomain;
	// sip realm
	private String sipRealm;
	// sip port
	private Integer sipPort = 5060;

	public String getSipServer() {
		return sipServer;
	}

	public void setSipServer(String sipServer) {
		this.sipServer = sipServer;
	}

	public String getSipUserName() {
		return sipUserName;
	}

	public void setSipUserName(String sipUserName) {
		this.sipUserName = sipUserName;
	}

	public String getSipPwd() {
		return sipPwd;
	}

	public void setSipPwd(String sipPwd) {
		this.sipPwd = sipPwd;
	}

	public String getSipDomain() {
		return sipDomain;
	}

	public void setSipDomain(String sipDomain) {
		this.sipDomain = sipDomain;
	}

	public String getSipRealm() {
		return sipRealm;
	}

	public void setSipRealm(String sipRealm) {
		this.sipRealm = sipRealm;
	}

	public Integer getSipPort() {
		return sipPort;
	}

	public void setSipPort(Integer sipPort) {
		this.sipPort = sipPort;
	}

	@Override
	public String toString() {
		// init sip register description
		StringBuilder _sipRegisterDescription = new StringBuilder();

		// append sip register server, sip account name, password, sip register
		// domain, realm and port
		_sipRegisterDescription.append("Sip register server: ")
				.append(sipServer).append(", ");
		_sipRegisterDescription.append("sip account name: ")
				.append(sipUserName).append(", ");
		_sipRegisterDescription.append("password: ").append(sipPwd)
				.append(", ");
		_sipRegisterDescription.append("sip register domain: ")
				.append(sipDomain).append(", ");
		_sipRegisterDescription.append("realm: ").append(sipRealm).append(", ");
		_sipRegisterDescription.append("port: ").append(sipPort).append("\n");

		return _sipRegisterDescription.toString();
	}

}
