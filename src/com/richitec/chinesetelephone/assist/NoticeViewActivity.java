package com.richitec.chinesetelephone.assist;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.richitec.chinesetelephone.R;
import com.richitec.commontoolkit.activityextension.NavigationActivity;

public class NoticeViewActivity extends NavigationActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_notice_view_layout);

		// set title text
		setTitle(R.string.view_notice);
	}
	
	
	class NoticeListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		
		private List<Map<String, Object>> dataList;
		public NoticeListAdapter(Context context) {
			inflater = LayoutInflater.from(context);
			dataList = new ArrayList<Map<String,Object>>();
		}
		
		@Override
		public int getCount() {
			return dataList.size();
		}

		@Override
		public Object getItem(int position) {
			return dataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}
		
		final class ViewHolder {
			
		}
	}
}
