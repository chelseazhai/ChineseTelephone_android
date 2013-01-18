package com.richitec.chinesetelephone.assist;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.constant.TelUser;
import com.richitec.chinesetelephone.utils.AppDataSaveRestoreUtil;
import com.richitec.commontoolkit.activityextension.NavigationActivity;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.HttpUtils;
import com.richitec.commontoolkit.utils.HttpUtils.HttpRequestType;
import com.richitec.commontoolkit.utils.HttpUtils.HttpResponseResult;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;
import com.richitec.commontoolkit.utils.HttpUtils.PostRequestFormat;

public class InviteFriendActivity extends NavigationActivity {
	private String inviteLink;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_invite_friend_layout);

		inviteLink = getIntent().getStringExtra("inviteLink");
		Log.d("inviteLink", inviteLink);

		setTitle(R.string.invite_friend_title);

		loadDescription();
		getRegedUserCountViaShare();
	}

	private void loadDescription() {
		UserBean user = UserManager.getInstance().getUser();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("countryCode",
				(String) user.getValue(TelUser.countryCode.name()));
		HttpUtils.postSignatureRequest(getString(R.string.server_url)
				+ getString(R.string.getRegInviteDescription_url),
				PostRequestFormat.URLENCODED, params, null,
				HttpRequestType.ASYNCHRONOUS, onFinishedLoadDesc);
	}

	private OnHttpRequestListener onFinishedLoadDesc = new OnHttpRequestListener() {

		@Override
		public void onFinished(HttpResponseResult responseResult) {
			try {
				JSONObject data = new JSONObject(
						responseResult.getResponseText());
				TextView descTV = (TextView) findViewById(R.id.invite_reg_descirption_tv);
				String desc = data.getString("reg_gift_desc_text");
				descTV.setText(desc);
				descTV.setVisibility(View.VISIBLE);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
		}
	};

	private void getRegedUserCountViaShare() {
		UserBean user = UserManager.getInstance().getUser();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("countryCode",
				(String) user.getValue(TelUser.countryCode.name()));
		HttpUtils.postSignatureRequest(getString(R.string.server_url)
				+ getString(R.string.getRegedUserCountViaShare_url),
				PostRequestFormat.URLENCODED, params, null,
				HttpRequestType.ASYNCHRONOUS, onFinishedGetUserCount);
	}

	private OnHttpRequestListener onFinishedGetUserCount = new OnHttpRequestListener() {

		@Override
		public void onFinished(HttpResponseResult responseResult) {
			TextView userCountTV = (TextView) findViewById(R.id.shared_user_count_tv);
			try {
				JSONObject data = new JSONObject(
						responseResult.getResponseText());
				int count = data.getInt("shared_user_count");
				if (count > 0) {
					userCountTV.setText(String.format(
							getString(R.string.current_user_shared_reg_count),
							count));
				} else {
					userCountTV.setText(R.string.no_user_shared_reg);
				}
				userCountTV.setVisibility(View.VISIBLE);
			} catch (JSONException e) {
				e.printStackTrace();
				userCountTV.setVisibility(View.GONE);
			}

		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			TextView userCountTV = (TextView) findViewById(R.id.shared_user_count_tv);
			userCountTV.setVisibility(View.GONE);
		}
	};

	public void smsInvite(View v) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("inviteLink", inviteLink);
		pushActivity(ContactLisInviteFriendActivity.class, params);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		AppDataSaveRestoreUtil.onRestoreInstanceState(savedInstanceState);
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		AppDataSaveRestoreUtil.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}
}
