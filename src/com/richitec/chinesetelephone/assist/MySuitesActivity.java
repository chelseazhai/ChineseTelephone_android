package com.richitec.chinesetelephone.assist;

import android.os.Bundle;

import com.richitec.chinesetelephone.R;
import com.richitec.commontoolkit.activityextension.NavigationActivity;

public class MySuitesActivity extends NavigationActivity {
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_my_suites_layout);
	        
	        setTitle(R.string.my_suites_item);
	        
	 }
}
