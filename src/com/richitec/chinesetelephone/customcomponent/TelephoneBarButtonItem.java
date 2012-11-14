package com.richitec.chinesetelephone.customcomponent;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup.LayoutParams;

import com.richitec.commontoolkit.activityextension.R;
import com.richitec.commontoolkit.customcomponent.BarButtonItem;

public class TelephoneBarButtonItem extends BarButtonItem {

	public TelephoneBarButtonItem(Context context,
			BarButtonItemStyle barBtnItemStyle, CharSequence title,
			OnClickListener btnClickListener) {
		super(
				context,
				title,
				barBtnItemStyle,
				context.getResources().getDrawable(R.drawable.navi_btn_normal),
				context.getResources().getDrawable(R.drawable.navi_btn_press), btnClickListener);
		
		//this.setTextColor(context.getResources().getColor(R.color.white));
		this.setTextSize(15);
	}

	public TelephoneBarButtonItem(Context context,
			BarButtonItemStyle barBtnItemStyle, int titleId,
			OnClickListener btnClickListener) {
		this(context, barBtnItemStyle, context.getResources()
				.getString(titleId), btnClickListener);
	}

	public TelephoneBarButtonItem(Context context, CharSequence title,
			BarButtonItemStyle barBtnItemStyle,
			Drawable normalBackgroundDrawable,
			Drawable pressedBackgroundDrawable, OnClickListener btnClickListener) {
		super(context, title, barBtnItemStyle, normalBackgroundDrawable,
				pressedBackgroundDrawable, btnClickListener);
	}

	public TelephoneBarButtonItem(Context context, CharSequence title,
			OnClickListener btnClickListener) {
		super(context, title, btnClickListener);
	}

	public TelephoneBarButtonItem(Context context, int titleId,
			int normalBackgroundResId, int pressedBackgroundResId,
			OnClickListener btnClickListener) {
		super(context, titleId, normalBackgroundResId, pressedBackgroundResId,
				btnClickListener);
	}

	public TelephoneBarButtonItem(Context context, int titleId,
			OnClickListener btnClickListener) {
		super(context, titleId, btnClickListener);
	}

	public TelephoneBarButtonItem(Context context, int resId) {
		super(context, resId);
	}

	public TelephoneBarButtonItem(Context context) {
		super(context);
	}

	@Override
	protected Drawable leftBarBtnItemNormalDrawable() {
		return this.getResources().getDrawable(
				R.drawable.navi_btn_normal);
	}

	@Override
	protected Drawable rightBarBtnItemNormalDrawable() {
		return this.getResources().getDrawable(
				R.drawable.navi_btn_normal);
	}

}
