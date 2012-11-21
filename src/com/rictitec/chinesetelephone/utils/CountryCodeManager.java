package com.rictitec.chinesetelephone.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.richitec.chinesetelephone.constant.Country;
import com.richitec.chinesetelephone.constant.SystemConstants;

public class CountryCodeManager {
	private List<Map<String,String>> datas = new ArrayList<Map<String,String>>();
	private static volatile CountryCodeManager countryCodeManager;
	
	private CountryCodeManager(){
		getCountryCode();
	}
	
	public static CountryCodeManager getInstance(){
		if(countryCodeManager == null)
			synchronized(CountryCodeManager.class){
				if(countryCodeManager==null){
					countryCodeManager = new CountryCodeManager();
				}
			}
		return countryCodeManager;
	}
	
	private void getCountryCode(){
		HashMap<String,String> data = new HashMap<String,String>();
		data.put(Country.contryname.name(), "0086(China 中国)");
		data.put(Country.code.name(), "0086");
		datas.add(data);
		HashMap<String,String> data1 = new HashMap<String,String>();
		data1.put(Country.contryname.name(), "00244(Angola 安哥拉)");
		data1.put(Country.code.name(), "00244");
		datas.add(data1);
	}
	
	public String[] getCountryNameList(){
		ArrayList<String> names = new ArrayList<String>();
		for(Map<String,String> data:datas){
			names.add(data.get(Country.contryname.name()));
		}
		String[] result = new String[names.size()];
		return names.toArray(result);
	}
	
	public String getCountryName(int index){
		Map<String,String>data = datas.get(index);
		return data.get(Country.contryname.name());
	}
	
	public String getCountryCode(String countryname){
		String code = null;
		for(Map<String,String>data:datas){
			String name = data.get(Country.contryname.name()).trim();
			String c = data.get(Country.code.name());
			if(countryname.equals(name)){
				code = c;
				break;
			}
		}
		return code;
	}
	
	public int getCountryIndex(String code){
		int i = 0;	
		for(Map<String,String>data:datas){
			String c = data.get(Country.code.name());
			if(code.equals(c)){
				Log.d(SystemConstants.TAG, "Find:"+i);
				break;
			}
			i++;
		}
		return i;
	}
}
