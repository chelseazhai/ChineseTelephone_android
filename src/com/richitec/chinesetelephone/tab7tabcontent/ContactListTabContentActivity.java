package com.richitec.chinesetelephone.tab7tabcontent;

import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.richitec.chinesetelephone.R;
import com.richitec.commontoolkit.activityextension.NavigationActivity;

public class ContactListTabContentActivity extends NavigationActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.contact_list_tab_content_activity_layout);

		// set title
		setTitle(R.string.contact_list_tab7nav_title);

		// test by ares
		// define list view content
		String[] listViewContentArr = { "张三", "李四", "王五", "唐僧", "孙悟空", "猪八戒",
				"沙悟净", "如来佛祖", "观音老母", "玉皇大帝", "王母娘娘", "嫦娥", "吴刚" };

		// packet array to adapter with content
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_checked, listViewContentArr);

		// set list view adapter
		((ListView) findViewById(R.id.contactInAB_listView))
				.setAdapter(arrayAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(
				R.menu.contact_list_tab_content_activity_layout, menu);
		return true;
	}
}
