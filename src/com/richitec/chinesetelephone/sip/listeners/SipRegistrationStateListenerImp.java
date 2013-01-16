package com.richitec.chinesetelephone.sip.listeners;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.constant.SystemConstants;
import com.richitec.chinesetelephone.tab7tabcontent.ChineseTelephoneTabActivity;
import com.richitec.commontoolkit.CommonToolkitApplication;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;

public class SipRegistrationStateListenerImp implements
		SipRegistrationStateListener {
	public final static int VOIP_ONLINE_NOTIFY_ID = 2;

	private NotificationManager mNotificationManager;

	public SipRegistrationStateListenerImp() {
		mNotificationManager = (NotificationManager) CommonToolkitApplication
				.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
	}

	@Override
	public void onRegisterSuccess() {
		Log.d(SystemConstants.TAG, "regist success");
		Context context = CommonToolkitApplication.getContext();

		UserBean user = UserManager.getInstance().getUser();
		sendNotification(
				android.R.drawable.presence_online,
				context.getString(R.string.app_name),
				String.format(context.getString(R.string.online),
						user.getName()));
	}

	@Override
	public void onRegisterFailed() {
		Log.d(SystemConstants.TAG, "regist failed");
		Context context = CommonToolkitApplication.getContext();

		UserBean user = UserManager.getInstance().getUser();
		sendNotification(
				android.R.drawable.presence_offline,
				context.getString(R.string.app_name),
				String.format(context.getString(R.string.offline),
						user.getName()));
	}

	private void sendNotification(int iconResId, String title, String content) {
		Context context = CommonToolkitApplication.getContext();
		Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ic_launcher);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context).setSmallIcon(iconResId).setLargeIcon(icon)
				.setAutoCancel(false).setOngoing(true).setWhen(0)
				.setContentTitle(title).setContentText(content);

		Intent resultIntent = new Intent(context,
				ChineseTelephoneTabActivity.class);
		PendingIntent noticePendingIntent = PendingIntent.getActivity(
				context, 0, resultIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		
		builder.setContentIntent(noticePendingIntent);

		Notification notif = builder.build();
		mNotificationManager.notify(VOIP_ONLINE_NOTIFY_ID, notif);
	}

	@Override
	public void onUnRegisterSuccess() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnRegisterFailed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRegistering() {
		Log.d(SystemConstants.TAG, "registering");
		Context context = CommonToolkitApplication.getContext();

		UserBean user = UserManager.getInstance().getUser();
		sendNotification(
				android.R.drawable.presence_away,
				context.getString(R.string.app_name),
				String.format(context.getString(R.string.registering),
						user.getName()));

	}

	
	public static void cancelVOIPOnlineStatus() {
		NotificationManager nm = (NotificationManager) CommonToolkitApplication
				.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(SipRegistrationStateListenerImp.VOIP_ONLINE_NOTIFY_ID);
	}
}
