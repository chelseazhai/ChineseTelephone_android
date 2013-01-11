package com.richitec.chinesetelephone.assist;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
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
	private ListView listView;
	private NoticeListAdapter listAdapter;
	private NoticeDBHelper dbhelper;
	private Cursor cursor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_notice_view_layout);

		// set title text
		setTitle(R.string.view_notice);
		listView = (ListView) findViewById(R.id.notice_list_view);
		dbhelper = new NoticeDBHelper(this);

		refresh();

		listView.setOnItemClickListener(onNoticeItemClick);
		listView.setOnItemLongClickListener(onNoticeLongClick);
	}

	public void refresh() {
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		cursor = dbhelper.getAllNoticesCursor();
		listAdapter = new NoticeListAdapter(this,
				R.layout.notice_list_item_layout, cursor, new String[] {
						NoticeFields.status.name(),
						NoticeFields.create_time.name(),
						NoticeFields.content.name() }, new int[] {
						R.id.notice_flag_icon, R.id.notice_time_tv,
						R.id.notice_content_tv });
		listView.setAdapter(listAdapter);
	}

	private OnItemClickListener onNoticeItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			HashMap<String, Object> notice = (HashMap<String, Object>) listAdapter
					.getDataList().get(position);
			Integer noticeId = (Integer) notice.get(NoticeFields.noticeid
					.name());
			dbhelper.setNoticeAsRead(noticeId);
			notice.put(NoticeFields.status.name(), NoticeStatus.read.name());
			listAdapter.notifyDataSetChanged();
		}
	};

	private OnItemLongClickListener onNoticeLongClick = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			HashMap<String, Object> notice = (HashMap<String, Object>) listAdapter
					.getDataList().get(position);
			final Integer noticeId = (Integer) notice.get(NoticeFields.noticeid
					.name());
			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
					NoticeViewActivity.this).setTitle(R.string.notice_op)
					.setItems(R.array.notice_op_menu,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									switch (which) {
									case 0:
										dbhelper.deleteNotice(noticeId);
										refresh();
										break;

									default:
										break;
									}

								}
							});
			alertBuilder.show();
			return false;
		}
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		cursor.close();
		dbhelper.close();
		super.onDestroy();
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
				map.put(NoticeFields.noticeid.name(), cursor.getInt(cursor
						.getColumnIndex(NoticeFields._id.name())));
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
					iv.setImageResource(R.drawable.notice_read);
				} else {
					iv.setImageResource(R.drawable.notice_unread);
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
