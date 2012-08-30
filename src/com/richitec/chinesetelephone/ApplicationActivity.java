package com.richitec.chinesetelephone;

import android.app.Activity;
import android.os.Bundle;

public class ApplicationActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// loading splash image
		setContentView(R.layout.application_activity_layout);
	}

}
