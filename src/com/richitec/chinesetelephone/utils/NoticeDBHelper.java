package com.richitec.chinesetelephone.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.richitec.chinesetelephone.constant.NoticeFields;
import com.richitec.chinesetelephone.constant.NoticeStatus;
import com.richitec.chinesetelephone.constant.SystemConstants;

public class NoticeDBHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "notices_db";
	private static int DB_VERSION = 1;

	private static final String TABLE_NAME = "notice";

	public NoticeDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + NoticeFields.id.name()
				+ " INTEGER PRIMARY KEY," + NoticeFields.content.name()
				+ " TEXT," + NoticeFields.create_time.name() + " INTEGER,"
				+ NoticeFields.status.name() + " TEXT" + ");");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
		onCreate(db);
	}

	public void addNotice(Integer id, String content, Long createTime) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(NoticeFields.id.name(), id);
		values.put(NoticeFields.content.name(), content);
		values.put(NoticeFields.create_time.name(), createTime);
		values.put(NoticeFields.status.name(), NoticeStatus.unread.name());
		long rowId = db.insert(TABLE_NAME, null, values);
		db.close();
	}

	public List<Map<String, Object>> getAllNotices() {
		List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
		SQLiteDatabase db = getReadableDatabase();
		String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + NoticeFields.create_time.name() + " DESC";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()) {
			do {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(NoticeFields.id.name(), cursor.getInt(cursor
						.getColumnIndex(NoticeFields.id.name())));
				map.put(NoticeFields.content.name(), cursor.getString(cursor
						.getColumnIndex(NoticeFields.content.name())));
				map.put(NoticeFields.create_time.name(), cursor.getLong(cursor
						.getColumnIndex(NoticeFields.create_time.name())));
				map.put(NoticeFields.status.name(), cursor.getString(cursor
						.getColumnIndex(NoticeFields.status.name())));

				list.add(map);
			} while (cursor.moveToNext());
		}
		return list;
	}
	
	public Cursor getAllNoticesCursor() {
		SQLiteDatabase db = getReadableDatabase();
		String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + NoticeFields.create_time.name() + " DESC";
		Cursor cursor = db.rawQuery(sql, null);
		return cursor;
	}

	public void setNoticeAsRead(Integer id) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(NoticeFields.status.name(), NoticeStatus.read.name());

		db.update(TABLE_NAME, values, NoticeFields.id.name() + " = ?",
				new String[] { String.valueOf(id) });

		db.close();
	}

	public void deleteNotice(Integer id) {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(TABLE_NAME, NoticeFields.id.name() + " = ?",
				new String[] { String.valueOf(id) });
		db.close();
	}
}
