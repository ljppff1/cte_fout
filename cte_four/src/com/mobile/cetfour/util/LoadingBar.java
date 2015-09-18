package com.mobile.cetfour.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.mobile.cetfour.R;

public class LoadingBar extends ProgressDialog {

	private TextView content;

	public LoadingBar(Context context) {
		super(context);
	}

	public LoadingBar(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading_bar);
		content = (TextView) findViewById(R.id.loading_bar_message);
		this.setCancelable(false);
	}

	public void setMessage(String message) {
		if (this.isShowing()) {
			content.setText(message);
		}
	}

	public void setMessage(int message) {
		if (this.isShowing()) {
			content.setText(message);
		}
	}

	public static LoadingBar show(Context ctx) {
		LoadingBar d = new LoadingBar(ctx);
		d.setCancelable(false);
		d.show();
		return d;
	}

	public static LoadingBar show(Context ctx, String message) {
		LoadingBar d = new LoadingBar(ctx);
		d.content.setText(message);
		d.show();
		return d;
	}

	public static LoadingBar show(Context ctx, int message) {
		LoadingBar d = new LoadingBar(ctx);
		d.content.setText(message);
		d.show();
		return d;
	}
	
}
