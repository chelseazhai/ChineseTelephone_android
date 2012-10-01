package com.richitec.chinesetelephone;

import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.richitec.chinesetelephone.tab7tabcontent.ChineseTelephoneTabActivity;
import com.richitec.commontoolkit.activityextension.AppLaunchActivity;

public class ChineseTelephoneAppLaunchActivity extends AppLaunchActivity {

	@Override
	public Drawable splashImg() {
		return null;
	}

	@Override
	public Intent intentActivity() {
		// go to Chinese telephone main tab activity
		return new Intent(this, ChineseTelephoneTabActivity.class);
	}

	@Override
	public void didFinishLaunching() {
		// traversal address book
		// AddressBookManager.getInstance().traversalAddressBook();

		// sleep 3 seconds
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
