package com.richitec.chinesetelephone.assist;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.constant.SuiteConstant;
import com.richitec.chinesetelephone.constant.SystemConstants;
import com.richitec.chinesetelephone.constant.TelUser;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.HttpUtils;
import com.richitec.commontoolkit.utils.HttpUtils.HttpRequestType;
import com.richitec.commontoolkit.utils.HttpUtils.HttpResponseResult;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;
import com.richitec.commontoolkit.utils.HttpUtils.PostRequestFormat;
import com.richitec.commontoolkit.utils.MyToast;

public class MySuitesListAdapter extends BaseExpandableListAdapter {
	private LayoutInflater inflater;
	private MySuitesActivity activity;
	private final static int GroupCount = 2;
	private JSONObject suites;
	private ProgressDialog progressDlg;

	public MySuitesListAdapter(MySuitesActivity context) {
		this.activity = context;
		inflater = LayoutInflater.from(context);
		suites = new JSONObject();
	}

	public void setSuites(JSONObject suites) {
		this.suites = suites;
		notifyDataSetChanged();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		Object obj = null;
		if (groupPosition == 0) {
			try {
				JSONArray array = suites.getJSONArray(SuiteConstant.my_suites
						.name());
				obj = array.get(childPosition);
			} catch (JSONException e) {
				Log.d(SystemConstants.TAG, e.getMessage());
			}
		} else if (groupPosition == 1) {
			try {
				JSONArray array = suites.getJSONArray(SuiteConstant.all_suites
						.name());
				obj = array.get(childPosition);
			} catch (JSONException e) {
				Log.d(SystemConstants.TAG, e.getMessage());
			}
		}
		return obj;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		if (groupPosition == 0) {
			if (getChildrenCount(groupPosition) == 1
					&& getChild(groupPosition, childPosition) == null) {
				convertView = inflater.inflate(R.layout.no_suites_now_layout,
						null);
				TextView tv = (TextView) convertView
						.findViewById(R.id.no_suite_now_textview);
				tv.setText(R.string.no_ordered_suites_now);
			} else {
				convertView = getMySuiteItemView(groupPosition, childPosition,
						convertView);
			}
		} else if (groupPosition == 1) {
			if (getChildrenCount(groupPosition) == 1
					&& getChild(groupPosition, childPosition) == null) {
				convertView = inflater.inflate(R.layout.no_suites_now_layout,
						null);
				TextView tv = (TextView) convertView
						.findViewById(R.id.no_suite_now_textview);
				tv.setText(R.string.no_suites_to_order_now);
			} else {
				convertView = getAllSuiteItemView(groupPosition, childPosition,
						convertView);
			}
		} else {
			return new LinearLayout(activity);
		}
		return convertView;
	}

	private View getMySuiteItemView(int groupPosition, int childPosition,
			View convertView) {
		Log.d(SystemConstants.TAG, "getAllSuiteItemView - group pos: "
				+ groupPosition + " child pos: " + childPosition);
		MySuiteItemViewHolder viewHolder = null;
		Object tag = convertView != null ? convertView.getTag() : null;
		if (tag != null) {
			if (tag instanceof MySuiteItemViewHolder) {
				viewHolder = (MySuiteItemViewHolder) tag;
			}
		}
		if (viewHolder == null) {
			viewHolder = new MySuiteItemViewHolder();
			convertView = inflater.inflate(R.layout.my_suite_item_layout, null);
			viewHolder.descTV = (TextView) convertView
					.findViewById(R.id.my_suite_desc);
			viewHolder.rentFeeTV = (TextView) convertView
					.findViewById(R.id.rent_fee);
			viewHolder.availableTimeTV = (TextView) convertView
					.findViewById(R.id.available_time_tv);
			viewHolder.expireTimeTV = (TextView) convertView
					.findViewById(R.id.expire_time_tv);
			viewHolder.unsubscribeBt = (Button) convertView
					.findViewById(R.id.unsubscribe_button);
			convertView.setTag(viewHolder);
		}

		final JSONObject suiteItem = (JSONObject) getChild(groupPosition,
				childPosition);
		if (suiteItem != null) {
			try {
				viewHolder.descTV.setText(suiteItem
						.getString(SuiteConstant.comment.name()));
				viewHolder.rentFeeTV.setText(suiteItem
						.getString(SuiteConstant.rentMoney.name()));
				String availableTime = suiteItem
						.getString(SuiteConstant.availableTime.name());
				viewHolder.availableTimeTV.setText(availableTime);

				String expireTime = suiteItem
						.getString(SuiteConstant.expireTime.name());
				if (expireTime.equals("never")) {
					viewHolder.expireTimeTV.setText(R.string.never_expire);
				} else {
					viewHolder.expireTimeTV.setText(expireTime);
				}

				viewHolder.unsubscribeBt
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								try {
									final String orderId = suiteItem
											.getString(SuiteConstant.orderId
													.name());
									new AlertDialog.Builder(activity)
											.setTitle(R.string.alert_title)
											.setMessage(
													R.string.sure_to_unsubscribe_suite)
											.setPositiveButton(
													R.string.unsubscribe,
													new DialogInterface.OnClickListener() {

														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															progressDlg = ProgressDialog
																	.show(activity,
																			null,
																			activity.getString(R.string.unsubscribing_suite));
															HashMap<String, String> params = new HashMap<String, String>();
															UserBean user = UserManager
																	.getInstance()
																	.getUser();
															params.put(
																	"countryCode",
																	(String) user
																			.getValue(TelUser.countryCode
																					.name()));
															params.put(
																	"orderSuiteId",
																	orderId);
															HttpUtils
																	.postSignatureRequest(
																			activity.getString(R.string.server_url)
																					+ activity
																							.getString(R.string.unsubscribe_suite_url),
																			PostRequestFormat.URLENCODED,
																			params,
																			null,
																			HttpRequestType.ASYNCHRONOUS,
																			onFinishedUnsubscribeSuite);
														}

														private OnHttpRequestListener onFinishedUnsubscribeSuite = new OnHttpRequestListener() {

															@Override
															public void onFinished(
																	HttpResponseResult responseResult) {
																dismissDlg();
																MyToast.show(activity, R.string.unsubscribe_suite_success, Toast.LENGTH_SHORT);
																activity.refreshSuites();
//																new AlertDialog.Builder(
//																		activity)
//																		.setTitle(
//																				R.string.alert_title)
//																		.setMessage(
//																				R.string.unsubscribe_suite_success)
//																		.setPositiveButton(
//																				R.string.ok,
//																				new DialogInterface.OnClickListener() {
//
//																					@Override
//																					public void onClick(
//																							DialogInterface dialog,
//																							int which) {
//																						activity.refreshSuites();
//																					}
//																				})
//																		.show();
															}

															@Override
															public void onFailed(
																	HttpResponseResult responseResult) {
																dismissDlg();

																switch (responseResult
																		.getStatusCode()) {
																case 500:
																	try {
																		JSONObject data = new JSONObject(
																				responseResult
																						.getResponseText());
																		String vosInfo = data
																				.getString("vos_info");
																		MyToast.show(
																				activity,
																				String.format(
																						"%s[%s]",
																						activity.getString(R.string.unsubscribe_suite_failed),
																						vosInfo),
																				Toast.LENGTH_SHORT);
																	} catch (JSONException e) {
																		e.printStackTrace();
																		MyToast.show(
																				activity,
																				R.string.subscribe_suite_failed,
																				Toast.LENGTH_SHORT);
																	}
																	break;
																case -1:
																	MyToast.show(
																			activity,
																			R.string.unsubscribe_suite_failed_check_network,
																			Toast.LENGTH_SHORT);
																	break;
																default:
																	MyToast.show(
																			activity,
																			R.string.unsubscribe_suite_failed,
																			Toast.LENGTH_SHORT);
																	break;

																}
															}
														};
													})
											.setNegativeButton(R.string.Cancel,
													null).show();
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}
						});
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return convertView;
	}

	private View getAllSuiteItemView(int groupPosition, int childPosition,
			View convertView) {
		Log.d(SystemConstants.TAG, "getAllSuiteItemView - group pos: "
				+ groupPosition + " child pos: " + childPosition);
		AllSuiteItemViewHolder viewHolder = null;
		Object tag = convertView != null ? convertView.getTag() : null;
		if (tag != null) {
			if (tag instanceof AllSuiteItemViewHolder) {
				viewHolder = (AllSuiteItemViewHolder) tag;
			}
		}
		if (viewHolder == null) {
			viewHolder = new AllSuiteItemViewHolder();
			convertView = inflater
					.inflate(R.layout.all_suite_item_layout, null);
			viewHolder.descTV = (TextView) convertView
					.findViewById(R.id.suite_desc);
			viewHolder.rentFeeTV = (TextView) convertView
					.findViewById(R.id.rent_fee);
			viewHolder.subscribeBt = (Button) convertView
					.findViewById(R.id.subscribe_button);
			convertView.setTag(viewHolder);
		}

		final JSONObject suiteItem = (JSONObject) getChild(groupPosition,
				childPosition);
		if (suiteItem != null) {
			try {
				viewHolder.descTV.setText(suiteItem
						.getString(SuiteConstant.comment.name()));
				viewHolder.rentFeeTV.setText(suiteItem
						.getString(SuiteConstant.rentMoney.name()));

				viewHolder.subscribeBt
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								try {
									final String suiteId = suiteItem
											.getString(SuiteConstant.suiteId
													.name());

									new AlertDialog.Builder(activity)
											.setTitle(
													R.string.select_time_to_open_suite)
											.setItems(
													R.array.subscribe_suite_menu,
													new DialogInterface.OnClickListener() {

														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															String openTimeType = null;
															switch (which) {
															case 0:
																openTimeType = "at_once";
																break;
															case 1:
																openTimeType = "next_month";
																break;
															default:
																break;
															}

															if (openTimeType != null) {
																progressDlg = ProgressDialog
																		.show(activity,
																				null,
																				activity.getString(R.string.subscribing_suite));
																HashMap<String, String> params = new HashMap<String, String>();
																UserBean user = UserManager
																		.getInstance()
																		.getUser();
																params.put(
																		"countryCode",
																		(String) user
																				.getValue(TelUser.countryCode
																						.name()));
																params.put(
																		"suiteId",
																		suiteId);
																params.put(
																		"open_time_type",
																		openTimeType);
																HttpUtils
																		.postSignatureRequest(
																				activity.getString(R.string.server_url)
																						+ activity
																								.getString(R.string.subscribe_suite_url),
																				PostRequestFormat.URLENCODED,
																				params,
																				null,
																				HttpRequestType.ASYNCHRONOUS,
																				onFinishedSubscribeSuite);
															}

														}

														private OnHttpRequestListener onFinishedSubscribeSuite = new OnHttpRequestListener() {

															@Override
															public void onFinished(
																	HttpResponseResult responseResult) {
																dismissDlg();
																MyToast.show(activity, R.string.subscribe_suite_success, Toast.LENGTH_SHORT);
																activity.refreshSuites();
//																new AlertDialog.Builder(
//																		activity)
//																		.setTitle(
//																				R.string.alert_title)
//																		.setMessage(
//																				R.string.subscribe_suite_success)
//																		.setPositiveButton(
//																				R.string.ok,
//																				new DialogInterface.OnClickListener() {
//
//																					@Override
//																					public void onClick(
//																							DialogInterface dialog,
//																							int which) {
//																						activity.refreshSuites();
//																					}
//																				})
//																		.show();
															}

															@Override
															public void onFailed(
																	HttpResponseResult responseResult) {
																dismissDlg();

																switch (responseResult
																		.getStatusCode()) {
																case 500:
																	try {
																		JSONObject data = new JSONObject(
																				responseResult
																						.getResponseText());
																		String vosInfo = data
																				.getString("vos_info");
																		MyToast.show(
																				activity,
																				String.format(
																						"%s[%s]",
																						activity.getString(R.string.subscribe_suite_failed),
																						vosInfo),
																				Toast.LENGTH_SHORT);
																	} catch (JSONException e) {
																		e.printStackTrace();
																		MyToast.show(
																				activity,
																				R.string.subscribe_suite_failed,
																				Toast.LENGTH_SHORT);
																	}
																	break;
																case -1:
																	MyToast.show(
																			activity,
																			R.string.subscribe_suite_failed_check_network,
																			Toast.LENGTH_SHORT);
																	break;
																default:
																	MyToast.show(
																			activity,
																			R.string.subscribe_suite_failed,
																			Toast.LENGTH_SHORT);
																	break;
																}

															}
														};
													}).show();

								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		int count = 1;
		if (groupPosition == 0) {
			try {
				JSONArray array = suites.getJSONArray(SuiteConstant.my_suites
						.name());
				count = array.length();
			} catch (JSONException e) {
				Log.d(SystemConstants.TAG, e.getMessage());
			}
		} else if (groupPosition == 1) {
			try {
				JSONArray array = suites.getJSONArray(SuiteConstant.all_suites
						.name());
				count = array.length();
			} catch (JSONException e) {
				Log.d(SystemConstants.TAG, e.getMessage());
			}
		}
		if (count == 0) {
			count = 1;
		}
		return count;
	}

	@Override
	public Object getGroup(int groupPosition) {
		String ret = "";
		if (groupPosition == 0) {
			ret = activity.getString(R.string.my_suite);
		} else if (groupPosition == 1) {
			ret = activity.getString(R.string.all_suite);
		}
		return ret;
	}

	@Override
	public int getGroupCount() {
		return GroupCount;
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		
		HeaderViewHolder viewHolder = null;
		viewHolder = (HeaderViewHolder) (convertView != null ? convertView.getTag() : null);
		if (viewHolder != null && !(viewHolder instanceof HeaderViewHolder)) {
			viewHolder = null;
		}
		if (viewHolder == null) {
			viewHolder = new HeaderViewHolder();
			convertView = inflater.inflate(R.layout.suite_header_item_layout,
					null);
			viewHolder.header = (TextView) convertView
					.findViewById(R.id.suite_header);
			viewHolder.expandIcon = (ImageView) convertView
					.findViewById(R.id.expand_flag_icon);
			convertView.setTag(viewHolder);
		}
		
		Log.d(SystemConstants.TAG, "group view holder: " + viewHolder);
		
		if (groupPosition == 0) {
			viewHolder.header.setText(R.string.my_suite);
		} else if (groupPosition == 1) {
			viewHolder.header.setText(R.string.all_suite);
		} else {
			return new LinearLayout(activity);
		}
		
		Log.d(SystemConstants.TAG, "my suite group pos: " + groupPosition + " group count: " + getGroupCount());

		if (isExpanded) {
			viewHolder.expandIcon
					.setImageResource(R.drawable.navigation_expand);
		} else {
			viewHolder.expandIcon
					.setImageResource(R.drawable.navigation_next_item);
		}
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}

	final class MySuiteItemViewHolder {
		public TextView descTV;
		public TextView rentFeeTV;
		public TextView availableTimeTV;
		public TextView expireTimeTV;
		public Button unsubscribeBt;
	}

	final class AllSuiteItemViewHolder {
		public TextView descTV;
		public TextView rentFeeTV;
		public Button subscribeBt;
	}

	final class HeaderViewHolder {
		public TextView header;
		public ImageView expandIcon;
	}

	private void dismissDlg() {
		if (progressDlg != null) {
			progressDlg.dismiss();
		}
	}

}
