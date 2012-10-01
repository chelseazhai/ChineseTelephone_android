package com.richitec.chinesetelephone.tab7tabcontent;

import android.os.Bundle;
import android.view.Menu;

import com.richitec.chinesetelephone.R;
import com.richitec.commontoolkit.activityextension.NavigationActivity;

public class SettingTabContentActivity extends NavigationActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.setting_tab_content_activity_layout);

		// set title
		setTitle(R.string.setting_tab7nav_title);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.setting_tab_content_activity_layout,
				menu);
		return true;
	}

}
