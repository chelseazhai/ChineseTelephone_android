package com.richitec.chinesetelephone.tab7tabcontent;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.IInterface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.assist.SettingActivity;
import com.richitec.chinesetelephone.constant.SystemConstants;
import com.richitec.chinesetelephone.constant.TelUser;
import com.richitec.chinesetelephone.sip.SipUtils;
import com.richitec.chinesetelephone.sip.listeners.SipRegistrationStateListener;
import com.richitec.chinesetelephone.sip.listeners.SipRegistrationStateListenerImp;
import com.richitec.chinesetelephone.utils.AppDataSaveRestoreUtil;
import com.richitec.chinesetelephone.utils.AppUpdateManager;
import com.richitec.chinesetelephone.utils.SipRegisterManager;
import com.richitec.commontoolkit.addressbook.AddressBookManager;
import com.richitec.commontoolkit.customcomponent.CTPopupWindow;
import com.richitec.commontoolkit.customcomponent.CTTabSpecIndicator;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.HttpUtils;
import com.richitec.commontoolkit.utils.MyToast;
import com.richitec.commontoolkit.utils.ValidatePattern;
import com.richitec.commontoolkit.utils.HttpUtils.HttpRequestType;
import com.richitec.commontoolkit.utils.HttpUtils.HttpResponseResult;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;
import com.richitec.commontoolkit.utils.HttpUtils.PostRequestFormat;

public class ChineseTelephoneTabActivity extends TabActivity {
	private ProgressDialog progressDlg;

	// tab widget item content array
	private final int[][] TAB_WIDGETITEM_CONTENTS = new int[][] {
			{ R.string.call_record_history_list_tab_title,
					R.drawable.callrecord_tab_icon },
			{ R.string.dial_tab_title, R.drawable.dial_tab_icon },
			{ R.string.contact_list_tab7nav_title,
					R.drawable.contactlist_tab_icon },
			{ R.string.more_tab7nav_title, R.drawable.more_tab_icon } };

	// current tab index, default is contact list tab
	private int _mDefaultTabIndex = 1;

	private SipRegistrationStateListener sipRegistrationStateListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(SystemConstants.TAG, "ChineseTelephoneTabActivity - onCreate");
		super.onCreate(savedInstanceState);

		sipRegistrationStateListener = new SipRegistrationStateListenerImp();

		// set content view
		setContentView(R.layout.chinese_telephone_tab_activity_layout);

		// get tabHost
		TabHost _tabHost = getTabHost();

		// define tabSpec
		TabSpec _tabSpec;

		// set tab indicator and content
		// call record history list
		_tabSpec = _tabHost
				.newTabSpec(
						getResources().getString(TAB_WIDGETITEM_CONTENTS[0][0]))
				.setIndicator(
						new CTTabSpecIndicator(this,
								TAB_WIDGETITEM_CONTENTS[0][0],
								TAB_WIDGETITEM_CONTENTS[0][1]))
				.setContent(
						new Intent().setClass(this,
								CallRecordHistoryListTabContentActivity.class));
		_tabHost.addTab(_tabSpec);

		// dial
		_tabSpec = _tabHost
				.newTabSpec(
						getResources().getString(TAB_WIDGETITEM_CONTENTS[1][0]))
				.setIndicator(
						new CTTabSpecIndicator(this,
								TAB_WIDGETITEM_CONTENTS[1][0],
								TAB_WIDGETITEM_CONTENTS[1][1]))
				.setContent(
						new Intent().setClass(this,
								DialTabContentActivity.class));
		_tabHost.addTab(_tabSpec);

		// contact list
		_tabSpec = _tabHost
				.newTabSpec(
						getResources().getString(TAB_WIDGETITEM_CONTENTS[2][0]))
				.setIndicator(
						new CTTabSpecIndicator(this,
								TAB_WIDGETITEM_CONTENTS[2][0],
								TAB_WIDGETITEM_CONTENTS[2][1]))
				.setContent(
						new Intent().setClass(this,
								ContactListTabContentActivity.class));
		_tabHost.addTab(_tabSpec);

		// more
		_tabSpec = _tabHost
				.newTabSpec(
						getResources().getString(TAB_WIDGETITEM_CONTENTS[3][0]))
				.setIndicator(
						new CTTabSpecIndicator(this,
								TAB_WIDGETITEM_CONTENTS[3][0],
								TAB_WIDGETITEM_CONTENTS[3][1]))
				.setContent(new Intent().setClass(this, SettingActivity.class));
		_tabHost.addTab(_tabSpec);

		// set current tab and tab image
		_tabHost.setCurrentTab(_mDefaultTabIndex);

		AppUpdateManager updateManager = new AppUpdateManager(this);
		updateManager.checkVersion(false);

		Intent intent = getIntent();
		String emailStatus = intent.getStringExtra("email_status");
		String email = intent.getStringExtra("email");
		Double regGivenMoney = intent.getDoubleExtra("reg_given_money", 0.0);
		if (emailStatus != null) {
			if (email == null || email.equals("")) {
				// alert user to set email
				final View dlgView = LayoutInflater.from(this).inflate(
						R.layout.email_set_dlg_layout, null);
				Builder alertBuilder = new AlertDialog.Builder(this).setTitle(
						R.string.email_setting_title).setView(dlgView);
				final AlertDialog alertDlg = alertBuilder.create();
				alertDlg.show();
				TextView descTv = (TextView) alertDlg
						.findViewById(R.id.email_setting_desc_tv);
				if (regGivenMoney > 0) {
					descTv.setText(String.format(
							getString(R.string.email_setting_desc),
							regGivenMoney.floatValue()));
				} else {
					descTv.setText(R.string.wanna_bind_email);
				}
				Button confirmBt = (Button) alertDlg
						.findViewById(R.id.bind_email_confirmBtn);
				confirmBt.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						EditText emailET = (EditText) alertDlg
								.findViewById(R.id.email_setting_input);
						String email = emailET.getText().toString().trim();
						if (!ValidatePattern.isValidEmail(email)) {
							MyToast.show(ChineseTelephoneTabActivity.this,
									R.string.pls_input_valid_email_address,
									Toast.LENGTH_SHORT);
						} else {
							progressDlg = ProgressDialog.show(
									ChineseTelephoneTabActivity.this, null,
									getString(R.string.binding_email_address));

							HashMap<String, String> params = new HashMap<String, String>();
							params.put("email", email);
							UserBean user = UserManager.getInstance().getUser();
							params.put("countryCode", (String) user
									.getValue(TelUser.countryCode.name()));
							HttpUtils.postSignatureRequest(
									getString(R.string.server_url)
											+ getString(R.string.setEmail_url),
									PostRequestFormat.URLENCODED, params, null,
									HttpRequestType.ASYNCHRONOUS,
									onFinishedSetEmail);
						}

					}

					private OnHttpRequestListener onFinishedSetEmail = new OnHttpRequestListener() {

						@Override
						public void onFinished(HttpResponseResult responseResult) {
							dismissProgressDlg();
							try {
								JSONObject data = new JSONObject(responseResult
										.getResponseText());
								String result = data.getString("result");
								if ("money gain mail send ok".equals(result)) {
									new AlertDialog.Builder(
											ChineseTelephoneTabActivity.this)
											.setTitle(R.string.alert_title)
											.setMessage(
													R.string.money_get_mail_send_ok_check_ur_mail)
											.setPositiveButton(R.string.Ensure,
													null).show();

									alertDlg.dismiss();
								} else if ("money gain mail send failed"
										.equals(result)) {
									new AlertDialog.Builder(
											ChineseTelephoneTabActivity.this)
											.setTitle(R.string.alert_title)
											.setMessage(
													R.string.bind_email_success)
											.setPositiveButton(R.string.Ensure,
													null).show();
									alertDlg.dismiss();
								} else if ("address verify mail send ok"
										.equals(result)) {
									new AlertDialog.Builder(
											ChineseTelephoneTabActivity.this)
											.setTitle(R.string.alert_title)
											.setMessage(
													R.string.verify_mail_send_ok)
											.setPositiveButton(R.string.Ensure,
													null).show();
									alertDlg.dismiss();
								} else if ("address verify mail send failed"
										.equals(result)) {
									new AlertDialog.Builder(
											ChineseTelephoneTabActivity.this)
											.setTitle(R.string.alert_title)
											.setMessage(
													R.string.bind_email_success)
											.setPositiveButton(R.string.Ensure,
													null).show();
									alertDlg.dismiss();
								} else if ("email is already binded by others"
										.equals(result)) {
									MyToast.show(
											ChineseTelephoneTabActivity.this,
											R.string.email_already_binded,
											Toast.LENGTH_SHORT);
								} else {
									bindError();
								}
							} catch (JSONException e) {
								e.printStackTrace();
								bindError();
							}

						}

						@Override
						public void onFailed(HttpResponseResult responseResult) {
							dismissProgressDlg();
							bindError();
						}

						private void bindError() {
							MyToast.show(ChineseTelephoneTabActivity.this,
									R.string.bind_email_failed,
									Toast.LENGTH_SHORT);
						}
					};
				});

				Button cancelBt = (Button) alertDlg
						.findViewById(R.id.bind_email_cancelBtn);
				cancelBt.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDlg.dismiss();
					}
				});
			} else {
				if (regGivenMoney > 0) {
					new AlertDialog.Builder(this)
							.setTitle(R.string.alert_title)
							.setMessage(
									String.format(
											getString(R.string.u_havent_got_ur_money_yet),
											regGivenMoney))
							.setPositiveButton(R.string.Ensure, null).show();
				}
			}
		}
	}

	private void dismissProgressDlg() {
		if (progressDlg != null) {
			progressDlg.dismiss();
		}
	}

	@Override
	public void onDestroy() {
		Log.d(SystemConstants.TAG, "ChineseTelephoneTabActivity - onDestroy");
		SipRegistrationStateListenerImp.cancelVOIPOnlineStatus();
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		Log.d(SystemConstants.TAG, "ChineseTelephoneTabActivity - onStop");
		super.onStop();
	}

	@Override
	protected void onResume() {
		Log.d(SystemConstants.TAG, "ChineseTelephoneTabActivity - onResume");
		SipRegisterManager.registSip(sipRegistrationStateListener,
				getString(R.string.vos_server));
		super.onResume();

		// if (espw != null) {
		// espw.showAtLocation(getTabWidget(), Gravity.CENTER, 0, 0);
		// }
	}

	@Override
	protected void onPause() {
		Log.d(SystemConstants.TAG, "ChineseTelephoneTabActivity - onPause");
		super.onPause();
	}

	@Override
	protected void onRestart() {
		Log.d(SystemConstants.TAG, "ChineseTelephoneTabActivity - onRestart");
		super.onRestart();
	}

	@Override
	protected void onStart() {
		Log.d(SystemConstants.TAG, "ChineseTelephoneTabActivity - onStart");
		super.onStart();
	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.alert_title)
				.setMessage(R.string.exit)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								SipUtils.unregisterSipAccount(null);
								SipUtils.destroySipEngine();
								AddressBookManager.getInstance()
										.unRegistContactObserver();
								SipRegistrationStateListenerImp
										.cancelVOIPOnlineStatus();
								UserManager.getInstance().setUser(
										new UserBean());

								System.exit(0);
							}
						}).setNegativeButton(R.string.cancel, null).show();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.d(SystemConstants.TAG,
				"ChineseTelephoneTabActivity - onRestoreInstanceState");
		AppDataSaveRestoreUtil.onRestoreInstanceState(savedInstanceState);

		int currentTabIndex = savedInstanceState.getInt("current_tab");
		Log.d(SystemConstants.TAG, "restore - current tab: " + currentTabIndex);
		if (currentTabIndex != 0) {
			super.onRestoreInstanceState(savedInstanceState);
		} else {
			TabHost tabHost = getTabHost();
			tabHost.setCurrentTab(currentTabIndex);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.d(SystemConstants.TAG,
				"ChineseTelephoneTabActivity - onSaveInstanceState");
		AppDataSaveRestoreUtil.onSaveInstanceState(outState);

		super.onSaveInstanceState(outState);
		TabHost tabHost = getTabHost();
		int currentTabIndex = tabHost.getCurrentTab();
		Log.d(SystemConstants.TAG, "save - current tab: " + currentTabIndex);
		outState.putInt("current_tab", currentTabIndex);
	}

}
