package com.richitec.chinesetelephone.tab7tabcontent;

import android.os.Bundle;
import android.view.Menu;

import com.richitec.chinesetelephone.R;
import com.richitec.commontoolkit.activityextension.NavigationActivity;

public class MoreTabContentActivity extends NavigationActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.more_tab_content_activity_layout);

		// set title
		setTitle(R.string.more_tab7nav_title);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater()
				.inflate(R.menu.more_tab_content_activity_menu, menu);
		return true;
	}

}
