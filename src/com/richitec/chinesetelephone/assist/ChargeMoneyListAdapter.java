package com.richitec.chinesetelephone.assist;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.constant.ChargeMoneyConstants;

public class ChargeMoneyListAdapter extends BaseAdapter {

	private JSONArray dataArray;
	private LayoutInflater inflater;

	public ChargeMoneyListAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		dataArray = new JSONArray();
	}

	public void setData(JSONArray data) {
		if (data != null) {
			this.dataArray = data;
			notifyDataSetChanged();
		}
	}
	
	@Override
	public int getCount() {
		return dataArray.length();
	}

	@Override
	public Object getItem(int position) {
		Object ret = null;
		try {
			ret = dataArray.get(position);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.alipay_charge_money_listitem, null);
			
			viewHolder.chargeMoneyTV = (TextView) convertView.findViewById(R.id.charge_money_tv);
			viewHolder.descriptionTV = (TextView) convertView.findViewById(R.id.charge_money_desc_tv);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		JSONObject chargeMoneyObject = (JSONObject) getItem(position);
		if (chargeMoneyObject != null) {
			try {
				double chargeMoney = chargeMoneyObject.getDouble(ChargeMoneyConstants.charge_money.name());
				String description = chargeMoneyObject.getString(ChargeMoneyConstants.description.name());
				
				viewHolder.chargeMoneyTV.setText(String.format("%.2f", chargeMoney));
				viewHolder.descriptionTV.setText(description);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return convertView;
	}

	final class ViewHolder {
		TextView chargeMoneyTV;
		TextView descriptionTV;
		
	}
}

