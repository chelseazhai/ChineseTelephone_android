package com.richitec.chinesetelephone.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.richitec.chinesetelephone.constant.NoticeFields;

public class NoticeDBHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "notices_db";
	private static int DB_VERSION = 1;
	
	private static final String TABLE_NAME = "notice";
	public NoticeDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		 db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                 + NoticeFields.id.name() + " INTEGER PRIMARY KEY,"
                 + NoticeFields.content.name() + " TEXT,"
                 + NoticeFields.create_time.name() + " INTEGER"
                 + ");");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
        onCreate(db);
	}

}
