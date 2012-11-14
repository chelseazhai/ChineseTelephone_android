package com.richitec.chinesetelephone.customcomponent;

import android.graphics.drawable.Drawable;

import com.richitec.chinesetelephone.R;
import com.richitec.commontoolkit.activityextension.NavigationActivity;


public class TelephoneNavigationActivity extends NavigationActivity {

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);

		// set navigation bar background drawable
		setNavBarBackgroundResource(R.drawable.navigate);
	}

	@Override
	protected Drawable backBarBtnItemNormalDrawable() {
		return getResources().getDrawable(
				R.drawable.navi_btn_normal);
	}

	@Override
	protected Drawable backBarBtnItemPressedDrawable() {
		return getResources().getDrawable(
				R.drawable.navi_btn_press);
	}

}
