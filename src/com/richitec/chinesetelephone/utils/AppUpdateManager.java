package com.richitec.chinesetelephone.utils;

import org.json.JSONObject;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.richitec.chinesetelephone.R;
import com.richitec.commontoolkit.utils.HttpUtils;
import com.richitec.commontoolkit.utils.HttpUtils.HttpRequestType;
import com.richitec.commontoolkit.utils.HttpUtils.HttpResponseResult;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;
import com.richitec.commontoolkit.utils.VersionUtils;

public class AppUpdateManager {
	private Context context;
	private boolean isFromSetting;
	
	public AppUpdateManager(Context context) {
		this.context = context;
	}
	
	public void checkVersion(boolean isFromSetting) {
		this.isFromSetting = isFromSetting;
		if (VersionUtils.checkVersion) {
			VersionUtils.localVersion = VersionUtils.currentVersionName(context);
			VersionUtils.updateURL = context.getString(R.string.appvcenter_url) + context.getString(R.string.app_download_url);
			HttpUtils.getRequest(context.getString(R.string.appvcenter_url)
					+ context.getString(R.string.app_version_url), null, null,
					HttpRequestType.ASYNCHRONOUS, onFinishedGetVersion);
		}
		else{
			if(isFromSetting){
				VersionUtils.localVersion = VersionUtils.currentVersionName(context);
				VersionUtils.updateURL = context.getString(R.string.appvcenter_url) + context.getString(R.string.app_download_url);
				HttpUtils.getRequest(context.getString(R.string.appvcenter_url)
						+ context.getString(R.string.app_version_url), null, null,
						HttpRequestType.ASYNCHRONOUS, onFinishedGetVersion);
			}
		}
	}
	
	private void noNewVersionDialog(){
		new AlertDialog.Builder(context)
		.setTitle(R.string.alert_title)
		.setMessage(R.string.no_new_version)
		.setPositiveButton(context.getString(R.string.cancel),
				null)
		.show();
	}

	private OnHttpRequestListener onFinishedGetVersion = new OnHttpRequestListener() {

		@Override
		public void onFinished(HttpResponseResult responseResult) {
			try {
				JSONObject data = new JSONObject(responseResult.getResponseText());
				String comment = data.getString("comment");
				VersionUtils.serverVerion = data.getString("version");
				
				Log.d("check version", "success : " + comment + ":" + VersionUtils.serverVerion);
				
				if (VersionUtils.compareVersion(VersionUtils.serverVerion,
						VersionUtils.localVersion) > 0
						&& VersionUtils.updateURL != null
						&& !VersionUtils.updateURL.equals("")) {
					// prompt update dialog
					String detectNewVersion = context.getString(R.string.detect_new_version);
					detectNewVersion = String.format(detectNewVersion,
							VersionUtils.serverVerion, comment);

					new AlertDialog.Builder(context)
							.setTitle(R.string.alert_title)
							.setMessage(detectNewVersion)
							.setPositiveButton(R.string.upgrade,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface arg0, int arg1) {
											VersionUtils.checkVersion = false;
											context.startActivity(new Intent(
													Intent.ACTION_VIEW,
													Uri.parse(VersionUtils.updateURL)));
										}
									})
							.setNegativeButton(R.string.cancel,
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											VersionUtils.checkVersion = false;
										}
									}).show();
				} else {
					VersionUtils.checkVersion = false;
					if(isFromSetting){
						noNewVersionDialog();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			Log.d("check version", "failed");
		}
	};
}
