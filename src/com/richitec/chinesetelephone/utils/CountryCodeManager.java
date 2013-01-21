package com.richitec.chinesetelephone.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.constant.Country;
import com.richitec.chinesetelephone.constant.SystemConstants;
import com.richitec.commontoolkit.CommonToolkitApplication;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;

public class CountryCodeManager {
	private List<Map<String, String>> datas;
	private static CountryCodeManager countryCodeManager;

	private CountryCodeManager() {
		datas = new ArrayList<Map<String, String>>();
		getCountryCode();
	}

	public static CountryCodeManager getInstance() {
		if (countryCodeManager == null)
			synchronized (CountryCodeManager.class) {
				if (countryCodeManager == null) {
					countryCodeManager = new CountryCodeManager();
				}
			}
		return countryCodeManager;
	}

	private void getCountryCode() {
		Context context = CommonToolkitApplication.getContext();
		HashMap<String, String> data = new HashMap<String, String>();
		data.put(Country.contryname.name(), context.getString(R.string.china));
		data.put(Country.code.name(), "0086");
		datas.add(data);
		HashMap<String, String> data1 = new HashMap<String, String>();
		data1.put(Country.contryname.name(), context.getString(R.string.angola));
		data1.put(Country.code.name(), "00244");
		datas.add(data1);

	}

	public String[] getCountryNameList() {
		ArrayList<String> names = new ArrayList<String>();
		for (Map<String, String> data : datas) {
			names.add(data.get(Country.contryname.name()));
		}
		String[] result = new String[names.size()];
		return names.toArray(result);
	}

	public String getCountryName(int index) {
		Map<String, String> data = datas.get(index);
		return data.get(Country.contryname.name());
	}

	public String getCountryCode(String countryname) {
		String code = null;
		for (Map<String, String> data : datas) {
			String name = data.get(Country.contryname.name()).trim();
			String c = data.get(Country.code.name());
			if (countryname.equals(name)) {
				code = c;
				break;
			}
		}
		return code;
	}

	public int getCountryIndex(String code) {
		// test
		UserBean telUser = UserManager.getInstance().getUser();
		Log.d(SystemConstants.TAG,
				"getCountryIndex - userbean: " + telUser.toString());
		// end test

		int i = 0;
		for (Map<String, String> data : datas) {
			String c = data.get(Country.code.name());
			Log.d(SystemConstants.TAG,
					"code: " + code + " data: " + data.toString());
			if (c.equals(code)) {
				Log.d(SystemConstants.TAG, "Find:" + i);
				break;
			}
			i++;
		}
		if (i >= datas.size()) {
			i = -1;
		}
		return i;
	}

	public boolean hasCountryCodePrefix(String phoneNumber) {
		Context context = CommonToolkitApplication.getContext();
		String[] codes = context.getResources().getStringArray(
				R.array.country_codes);
		boolean ret = false;
		if (codes != null) {
			for (String code : codes) {
				if (phoneNumber.startsWith(code)) {
					ret = true;
					break;
				}
			}
		}

		return ret;
	}
}
