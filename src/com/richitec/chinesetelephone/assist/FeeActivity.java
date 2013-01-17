package com.richitec.chinesetelephone.assist;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.richitec.chinesetelephone.R;
import com.richitec.chinesetelephone.utils.AppDataSaveRestoreUtil;
import com.richitec.commontoolkit.activityextension.NavigationActivity;

public class FeeActivity extends NavigationActivity {
	private int maxNumber;
	private LinearLayout loadingLayout;
	private ProgressBar progressBar;
	private TextView loadingTV;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.fee_activity_layout);

		// set title text
		setTitle(R.string.fee_query_item);
		maxNumber = Integer
				.parseInt(getString(R.string.fee_loading_progressBar_max));

		loadingLayout = (LinearLayout) findViewById(R.id.fee_loading_linearLayout);
		progressBar = (ProgressBar) findViewById(R.id.fee_loading_progressBar);
		loadingTV = (TextView) findViewById(R.id.fee_loading_textView);
		// get support webView
		WebView feeWebView = (WebView) findViewById(R.id.fee_webView);

		// load support url
		feeWebView.clearCache(true);
		feeWebView.loadUrl(getString(R.string.fee_query_url));

		// add web chrome client for loading progress changed
		feeWebView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);

				// set support loading progressBar progress
				progressBar.setProgress(newProgress);

				// set support loading textView text
				loadingTV
						.setText(getString(R.string.fee_loading_textView_textHeader)
								+ newProgress + "%");

				// check support page loading completed
				if (maxNumber == newProgress) {
					// support loading completed, remove support loading
					// linearLayout
					loadingLayout.setVisibility(View.GONE);
				}
			}

		});
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		AppDataSaveRestoreUtil.onRestoreInstanceState(savedInstanceState);
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		AppDataSaveRestoreUtil.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}
}
