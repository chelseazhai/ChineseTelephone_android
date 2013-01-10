package com.richitec.chinesetelephone.assist;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.constant.NoticeFields;
import com.richitec.chinesetelephone.constant.NoticeStatus;
import com.richitec.chinesetelephone.utils.NoticeDBHelper;
import com.richitec.commontoolkit.activityextension.NavigationActivity;
import com.richitec.commontoolkit.customadapter.CommonListCursorAdapter;

public class NoticeViewActivity extends NavigationActivity {

	private NoticeListAdapter listAdapter;
	private NoticeDBHelper dbhelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_notice_view_layout);

		// set title text
		setTitle(R.string.view_notice);
		dbhelper = new NoticeDBHelper(this);
		Cursor c = dbhelper.getAllNoticesCursor();
		listAdapter = new NoticeListAdapter(this,
				R.layout.notice_list_item_layout, c, new String[] {
						NoticeFields.status.name(),
						NoticeFields.content.name(),
						NoticeFields.create_time.name() }, new int[] {
						R.id.notice_flag_icon, R.id.notice_content_tv,
						R.id.notice_time_tv });

		ListView list = (ListView) findViewById(R.id.notice_list_view);
		list.setAdapter(listAdapter);
	}

	class NoticeListAdapter extends CommonListCursorAdapter {

		public NoticeListAdapter(Context context, int itemsLayoutResId,
				Cursor c, String[] dataKeys, int[] itemsComponentResIds) {
			super(context, itemsLayoutResId, c, dataKeys, itemsComponentResIds);
		}

		@Override
		protected void appendCursorData(List<Object> data, Cursor cursor) {
			if (cursor != null) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(NoticeFields.id.name(), cursor.getInt(cursor
						.getColumnIndex(NoticeFields.id.name())));
				map.put(NoticeFields.content.name(), cursor.getString(cursor
						.getColumnIndex(NoticeFields.content.name())));
				map.put(NoticeFields.create_time.name(), cursor.getLong(cursor
						.getColumnIndex(NoticeFields.create_time.name())));
				map.put(NoticeFields.status.name(), cursor.getString(cursor
						.getColumnIndex(NoticeFields.status.name())));

				data.add(map);
			}

		}

		@Override
		protected Map<String, ?> recombinationData(String dataKey,
				Object dataObject) {
			return (Map<String, Object>) dataObject;
		}

		@Override
		protected void bindView(View view, Map<String, ?> dataMap,
				String dataKey) {
			if (view instanceof ImageView) {
				String status = (String) dataMap.get(dataKey);
				ImageView iv = (ImageView) view;
				if (NoticeStatus.read.name().equals(status)) {
					iv.setImageResource(R.drawable.leaf_gray);
				} else {
					iv.setImageResource(R.drawable.leaf);
				}
			} else if (view instanceof TextView) {
				TextView tv = (TextView) view;
				if (NoticeFields.content.name().equals(dataKey)) {
					String content = (String) dataMap.get(dataKey);
					tv.setText(content);
				} else if (NoticeFields.create_time.name().equals(dataKey)) {
					Long time = (Long) dataMap.get(dataKey);

					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(time * 1000);
					SimpleDateFormat df = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm");
					String createTime = df.format(cal.getTime());

					tv.setText(createTime);
				}
			}

		}

	}
}
