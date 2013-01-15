package com.richitec.chinesetelephone.assist;

import java.math.BigDecimal;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.constant.TelUser;
import com.richitec.chinesetelephone.utils.AppDataSaveRestoreUtil;
import com.richitec.commontoolkit.activityextension.NavigationActivity;
import com.richitec.commontoolkit.customcomponent.BarButtonItem;
import com.richitec.commontoolkit.customcomponent.BarButtonItem.BarButtonItemStyle;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.HttpUtils;
import com.richitec.commontoolkit.utils.HttpUtils.HttpRequestType;
import com.richitec.commontoolkit.utils.HttpUtils.HttpResponseResult;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;
import com.richitec.commontoolkit.utils.HttpUtils.PostRequestFormat;
import com.richitec.commontoolkit.utils.MyToast;

public class RemainMoneyActivity extends NavigationActivity {
	public static String BALANCE = "balance";
	private double balance;
	private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remain_money_layout);

		this.setRightBarButtonItem(new BarButtonItem(this,
				R.string.charge_title_popwin, BarButtonItemStyle.RIGHT_GO,
				chargeBtnListener));

		setTitle(R.string.get_remain_money_title);

		String username = UserManager.getInstance().getUser().getName();
		((TextView) findViewById(R.id.uername)).setText(username);
	}

	@Override
	public void onResume() {
		getRemainMoney();
		super.onResume();
	}

	public static double formatRemainMoney(String money) {
		BigDecimal b = new BigDecimal(money);
		double result = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return result;
	}

	private void getRemainMoney() {
		UserBean userBean = UserManager.getInstance().getUser();
		String username = userBean.getName();
		String countryCode = (String) userBean.getValue(TelUser.countryCode
				.name());

		progressDialog = ProgressDialog.show(this, null,
				getString(R.string.sending_request), true);

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("username", username);
		params.put("countryCode", countryCode);

		HttpUtils.postSignatureRequest(getString(R.string.server_url)
				+ getString(R.string.account_balance_url),
				PostRequestFormat.URLENCODED, params, null,
				HttpRequestType.ASYNCHRONOUS, onFinishedGetBalance);
	}

	private void dismiss() {
		if (progressDialog != null)
			progressDialog.dismiss();
	}

	private OnClickListener chargeBtnListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			RemainMoneyActivity.this.pushActivity(AccountChargeActivity.class);
		}
	};
	private OnHttpRequestListener onFinishedGetBalance = new OnHttpRequestListener() {

		@Override
		public void onFinished(HttpResponseResult responseResult) {
			dismiss();
			JSONObject data;
			try {
				data = new JSONObject(responseResult.getResponseText());
				balance = RemainMoneyActivity.formatRemainMoney(data
						.getDouble("balance") + "");

				((TextView) findViewById(R.id.remain_money)).setText(String
						.valueOf(balance) + getString(R.string.yuan));

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			dismiss();
			MyToast.show(RemainMoneyActivity.this, R.string.get_balance_error,
					Toast.LENGTH_SHORT);
		}
	};
	
	@Override
	protected void onRestoreInstanceState (Bundle savedInstanceState) {
		AppDataSaveRestoreUtil.onRestoreInstanceState(savedInstanceState);
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	@Override
	protected void onSaveInstanceState (Bundle outState) {
		AppDataSaveRestoreUtil.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}
}
