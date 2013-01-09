package com.richitec.chinesetelephone.service;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.assist.AboutActivity;
import com.richitec.chinesetelephone.constant.SystemConstants;
import com.richitec.chinesetelephone.utils.NoticeDBHelper;
import com.richitec.commontoolkit.utils.DataStorageUtils;
import com.richitec.commontoolkit.utils.HttpUtils;
import com.richitec.commontoolkit.utils.HttpUtils.HttpRequestType;
import com.richitec.commontoolkit.utils.HttpUtils.HttpResponseResult;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;

public class NoticeService extends Service {

	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;
	private Timer timer;
	private NoticeGetTimerTaks task;

	private static final long interval = 10 * 1000;

	// Handler that receives messages from the thread
	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				timer.schedule(task, 0, interval);
			}

		}
	}

	@Override
	public void onCreate() {
		// Start up the thread running the service. Note that we create a
		// separate thread because the service normally runs in the process's
		// main thread, which we don't want to block. We also make it
		// background priority so CPU-intensive work will not disrupt our UI.
		HandlerThread thread = new HandlerThread("ServiceStartArguments",
				Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		// Get the HandlerThread's Looper and use it for our Handler
		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);

		if (timer == null) {
			timer = new Timer();
		}
		if (task == null) {
			task = new NoticeGetTimerTaks(this);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mServiceHandler.sendEmptyMessage(0);

		// If we get killed, after returning from here, restart
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// We don't provide binding, so return null
		return null;
	}

	@Override
	public void onDestroy() {
		// Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
		task.cancel();
	}

	class NoticeGetTimerTaks extends TimerTask {
		private static final String MAX_NOTICE_ID = "max_notice_id";

		private static final int NOTIFY_ID = 1;
		private Context context;
		private NotificationManager mNotificationManager;

		private NoticeDBHelper dbHelper;

		public NoticeGetTimerTaks(Context context) {
			this.context = context;
			mNotificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			dbHelper = new NoticeDBHelper(context);
		}

		@Override
		public void run() {
			Log.d(SystemConstants.TAG, "get new notice");

			String maxId = DataStorageUtils.getString(MAX_NOTICE_ID);
			if (maxId == null || maxId.equals("")) {
				maxId = "-1";
			}
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("maxId", maxId);
			HttpUtils.getRequest(context.getString(R.string.server_url)
					+ context.getString(R.string.getNewNotice_url), params,
					null, HttpRequestType.ASYNCHRONOUS, onFinishedGetNotice);
		}

		private OnHttpRequestListener onFinishedGetNotice = new OnHttpRequestListener() {

			@Override
			public void onFinished(HttpResponseResult responseResult) {
				try {
					JSONArray notices = new JSONArray(
							responseResult.getResponseText());
					if (notices != null && notices.length() > 0)
						saveAndAlertNotices(notices);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailed(HttpResponseResult responseResult) {

			}
		};

		private void saveAndAlertNotices(JSONArray notices)
				throws JSONException {
			int maxId = 0;
			String contentToDisplay = "";
			long timeToDisplay = 0;
			for (int i = 0; i < notices.length(); i++) {
				JSONObject notice = notices.getJSONObject(i);
				int id = notice.getInt("id");
				String content = notice.getString("content");
				long time = notice.getLong("create_time");
				if (id > maxId) {
					maxId = id;
					contentToDisplay = content;
					timeToDisplay = time;
				}

				// save notice to database
				dbHelper.addNotice(id, content, time);
			}

			DataStorageUtils.putObject(MAX_NOTICE_ID, String.valueOf(maxId));

			// show notice in status bar
			Log.d(SystemConstants.TAG, "notify: " + contentToDisplay);
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					context).setSmallIcon(R.drawable.ic_launcher)
					.setContentTitle(contentToDisplay)
					.setNumber(notices.length());

			Intent resultIntent = new Intent(context, AboutActivity.class);
			PendingIntent noticePendingIntent = PendingIntent
					.getActivity(context, 0, resultIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setContentIntent(noticePendingIntent);
			mBuilder.setAutoCancel(true);

			Notification notif = mBuilder.build();
			notif.defaults = Notification.DEFAULT_ALL;
			mNotificationManager.notify(NOTIFY_ID, notif);
		}
	}
}
