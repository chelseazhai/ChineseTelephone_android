package com.richitec.chinesetelephone.alipay;

import com.richitec.commontoolkit.utils.HexUtils;

/**
 * RC4
 * 
 * @author Wang Xing
 * 
 */
public class MyRC4 {

	private int x = 0, y = 0;
	private byte S[];

	public MyRC4() {
		S = new byte[256];
	}

	/** The key-scheduling algorithm (KSA) - initialize S-BOX */
	public void initKey(byte[] key) {
		for (int i = 0; i < 256; i++) {
			S[i] = (byte) i;
		}

		for (int i = 0, j = 0; i < 256; i++) {
			j = (int) ((j + key[i % key.length] + S[i]) & 0xff);
			swap(S, i, j);
		}

	}

	/** The pseudo-random generation algorithm (PRGA) */
	private byte rc4Output() {
		x = (int) ((x + 1) & 0xff);
		y = (int) ((y + S[x]) & 0xff);

		swap(S, x, y);
		return S[(int) ((S[x] + S[y]) & 0xff)];
	}

	/**
	 * do encryption
	 * 
	 * @param msg
	 * @return
	 */
	public byte[] crypt(byte[] msg) {
		x = y = 0;
		byte[] output = new byte[msg.length];
		for (int i = 0; i < msg.length; i++) {
			output[i] = (byte) (msg[i] ^ rc4Output());
		}
		return output;
	}

	private void swap(byte[] s, int i, int j) {
		byte tmp = s[i];
		s[i] = s[j];
		s[j] = tmp;
	}


	public static byte[] rc4(byte[] msg, String key) {
		MyRC4 rc4 = new MyRC4();

		rc4.initKey(key.getBytes());
		byte[] result = rc4.crypt(msg);
		return result;
	}

	public static byte[] encrypt(String msg, String key) {
		byte[] cryptedMsg = rc4(msg.getBytes(), key);
		return cryptedMsg;
	}

	/**
	 * decrypt
	 * @param cryptedMsg
	 * @param key
	 * @return
	 */
	public static String decrypt(byte[] cryptedMsg, String key) {
		byte[] decryptedMsg = rc4(cryptedMsg, key);
		String msg = new String(decryptedMsg);
		return msg;
	}

	/**
	 * encrypt a plain message
	 * @param msg - plain message
	 * @param key - key
	 * @return encrypted message
	 */
	public static String encryptPro(String msg, String key) {
		byte[] cryptedMsg = encrypt(msg, key);
		
		String hexText = HexUtils.convert(cryptedMsg);
		
		return hexText;
	}
	
	/**
	 * decrypt an encrypted message
	 * @param cryptedMsg - crypted message
	 * @param key - key
	 * @return plain text
	 */
	public static String decryptPro(String cryptedMsg, String key) {
		byte[] cryptMsg = HexUtils.convert(cryptedMsg);
		return decrypt(cryptMsg, key);
	}
	

	public static void main(String[] args) {
		String text = "hello";
		String key = "111";
		
		String encryptedMsg = MyRC4.encryptPro(text, key);
		System.out.println("encryptedMsg: " + encryptedMsg);
		
		String plainText = MyRC4.decryptPro(encryptedMsg, key);
		System.out.println("plainText: " + plainText);

	}

}
