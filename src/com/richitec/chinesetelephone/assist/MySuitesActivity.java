package com.richitec.chinesetelephone.assist;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.constant.SystemConstants;
import com.richitec.chinesetelephone.constant.TelUser;
import com.richitec.commontoolkit.activityextension.NavigationActivity;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.HttpUtils;
import com.richitec.commontoolkit.utils.MyToast;
import com.richitec.commontoolkit.utils.HttpUtils.HttpRequestType;
import com.richitec.commontoolkit.utils.HttpUtils.HttpResponseResult;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;
import com.richitec.commontoolkit.utils.HttpUtils.PostRequestFormat;

public class MySuitesActivity extends NavigationActivity {
	private MySuitesListAdapter listAdapter;
	private ProgressDialog progressDlg;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_suites_layout);

		setTitle(R.string.my_suites_item);

		listAdapter = new MySuitesListAdapter(this);
		ExpandableListView listView = (ExpandableListView) findViewById(R.id.my_suites_listview);
		listView.setAdapter(listAdapter);

		refreshSuites();
	}
	
	private void refreshSuites() {
		progressDlg = ProgressDialog.show(MySuitesActivity.this, null, getString(R.string.getting_suite));
		HashMap<String, String> params = new HashMap<String, String>();
		UserBean user = UserManager.getInstance().getUser();
		params.put("countryCode", (String) user.getValue(TelUser.countryCode.name()));
		HttpUtils.postSignatureRequest(getString(R.string.server_url) + getString(R.string.getSuites_url), PostRequestFormat.URLENCODED, params, null, HttpRequestType.ASYNCHRONOUS, onFinishedGetSuites);
	}
	
	private OnHttpRequestListener onFinishedGetSuites = new OnHttpRequestListener() {
		
		@Override
		public void onFinished(HttpResponseResult responseResult) {
			dismissDlg();
			try {
				JSONObject data = new JSONObject(responseResult.getResponseText());
				listAdapter.setSuites(data);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		
		@Override
		public void onFailed(HttpResponseResult responseResult) {
			Log.d(SystemConstants.TAG, "onFinishedGetSuites status: " + responseResult.getStatusCode() + " text: " + responseResult.getResponseText());
			dismissDlg();
			MyToast.show(MySuitesActivity.this, R.string.get_suite_failed, Toast.LENGTH_SHORT);
			
		}
	};
	
	private void dismissDlg() {
		if (progressDlg != null) {
			progressDlg.dismiss();
		}
	}
}
