package com.richitec.chinesetelephone.assist;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.constant.SuiteConstant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MySuitesListAdapter extends BaseExpandableListAdapter {
	private LayoutInflater inflater;
	private Context context;
	private static int GroupCount = 2;
	private JSONObject suites;

	public MySuitesListAdapter(Context context) {
		this.context = context;
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
				e.printStackTrace();
			}
		} else if (groupPosition == 1) {
			try {
				JSONArray array = suites.getJSONArray(SuiteConstant.all_suites
						.name());
				obj = array.get(childPosition);
			} catch (JSONException e) {
				e.printStackTrace();
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
			convertView = getMySuiteItemView(childPosition, convertView);
		} else if (groupPosition == 1) {
			convertView = getAllSuiteItemView(childPosition, convertView);
		}
		return convertView;
	}

	private View getMySuiteItemView(int childPosition, View convertView) {

		return convertView;
	}

	private View getAllSuiteItemView(int childPosition, View convertView) {
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		int count = 0;
		if (groupPosition == 0) {
			try {
				JSONArray array = suites.getJSONArray(SuiteConstant.my_suites
						.name());
				count = array.length();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (groupPosition == 1) {
			try {
				JSONArray array = suites.getJSONArray(SuiteConstant.all_suites
						.name());
				count = array.length();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return count;
	}

	@Override
	public Object getGroup(int groupPosition) {
		String ret = "";
		if (groupPosition == 0) {
			ret = context.getString(R.string.my_suite);
		} else if (groupPosition == 1) {
			ret = context.getString(R.string.all_suite);
		}
		return ret;
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
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
		if (convertView == null) {
			viewHolder = new HeaderViewHolder();
			convertView = inflater.inflate(R.layout.suite_header_item_layout,
					null);
			viewHolder.header = (TextView) convertView
					.findViewById(R.id.suite_header);
			viewHolder.expandIcon = (ImageView) convertView
					.findViewById(R.id.expand_flag_icon);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (HeaderViewHolder) convertView.getTag();
		}

		if (groupPosition == 0) {
			viewHolder.header.setText(R.string.my_suite);
		} else if (groupPosition == 1) {
			viewHolder.header.setText(R.string.all_suite);
		}

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
	}

	final class AllSuiteItemViewHolder {
		public TextView descTV;
		public TextView rentFeeTV;
	}

	final class HeaderViewHolder {
		public TextView header;
		public ImageView expandIcon;
	}

}
