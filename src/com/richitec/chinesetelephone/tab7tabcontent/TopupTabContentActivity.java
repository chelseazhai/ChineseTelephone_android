package com.richitec.chinesetelephone.tab7tabcontent;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.richitec.chinesetelephone.R;

public class TopupTabContentActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.topup_tab_content_activity_layout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.topup_tab_content_activity_layout,
				menu);
		return true;
	}

}
