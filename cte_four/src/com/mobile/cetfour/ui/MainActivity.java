package com.mobile.cetfour.ui;

import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;
import net.youmi.android.banner.AdViewListener;
import net.youmi.android.offers.OffersManager;
import net.youmi.android.spot.SpotManager;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mobile.cetfour.R;
import com.mobile.cetfour.sqlite.AnswerColumns;
import com.mobile.cetfour.sqlite.BaseColumns;
import com.mobile.cetfour.sqlite.CauseInfo;
import com.mobile.cetfour.sqlite.DBManager;
import com.mobile.cetfour.util.CommUtils;
import com.mobile.cetfour.util.LoadingBar;
import com.mobile.cetfour.util.ThreadUtil;
import com.mobile.cetfour.util.TimeUtils;

public class MainActivity extends Activity implements OnClickListener {

	private boolean hasPressedBack;
	private LoadingBar loadingBar;
	private Handler handle = new Handler() {
	public void handleMessage(android.os.Message msg) {
		switch (msg.what) {
		case 1:
			break;

		default:
			break;
		}
	};
};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (TimeUtils.isNetworkAvailable(this)) {
			DBManager.getInstance(this).removeAll(AnswerColumns.TABLE_NAME);
		}
		setContentView(R.layout.activity_main);
		showBanner();
		initUI();
		asynctaskInstance();
	}

	private void initUI() {
		TextView order = (TextView) findViewById(R.id.order);
		TextView simulate = (TextView) findViewById(R.id.simulate);
		TextView recommend = (TextView) findViewById(R.id.recommend);
		LinearLayout favorite = (LinearLayout) findViewById(R.id.favorite);
		LinearLayout wrong = (LinearLayout) findViewById(R.id.wrong);
		LinearLayout history = (LinearLayout) findViewById(R.id.history);
		order.setOnClickListener(this);
		simulate.setOnClickListener(this);
		recommend.setOnClickListener(this);
		favorite.setOnClickListener(this);
		wrong.setOnClickListener(this);
		history.setOnClickListener(this);
		loadingBar = new LoadingBar(this);
	}

	private void asynctaskInstance() {
		if (!CommUtils.isNetworkAvailable(this)) {
			Toast.makeText(this, R.string.network_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		loadingBar.show();
		AsyncHttpClient client = new AsyncHttpClient();
		client.post(Contans.PATH_HOME, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, final byte[] arg2) {
				ThreadUtil.execute(new Runnable() {

					@Override
					public void run() {
						String result = new String(arg2);
						try {
							JSONObject jsonObject = new JSONObject(result);
							int code = jsonObject.getInt("code");
							if (code == 1) {
								String content = jsonObject.getString("content");
								JSONArray array = new JSONArray(content);
								for (int i = 0; i < content.length(); i++) {
									JSONObject object = array.getJSONObject(i);
									String timu_title = new JSONObject(object.getString("timu")).getString("title");
									String timu_one = new JSONObject(object.getString("timu")).getString("one");
									String timu_tow = new JSONObject(object.getString("timu")).getString("tow");
									String timu_three = new JSONObject(object.getString("timu")).getString("three");
									String timu_four = new JSONObject(object.getString("timu")).getString("four");
									String daan_one = new JSONObject(object.getString("daan")).getString("daan_one");
									String daan_tow = new JSONObject(object.getString("daan")).getString("daan_tow");
									String daan_three = new JSONObject(object.getString("daan")).getString("daan_three");
									String daan_four = new JSONObject(object.getString("daan")).getString("daan_four");
									String types = new JSONObject(object.getString("types")).getString("types");
									String detail = new JSONObject(object.getString("detail")).getString("detail");
									int reply = BaseColumns.NULL;
									CauseInfo myData = new CauseInfo(timu_title, timu_one, timu_tow, timu_three, timu_four, daan_one, daan_tow, daan_three, daan_four, detail,
											types, reply);
									DBManager.getInstance(MainActivity.this).insert(AnswerColumns.TABLE_NAME, myData);
								}
							} else {
								Toast.makeText(MainActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
							}
							Message msg = new Message();
							msg.what = 1;
							handle.sendMessage(msg);
							loadingBar.dismiss();
						} catch (JSONException e) {
							loadingBar.dismiss();
						}
					}
				});
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				Toast.makeText(MainActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
				loadingBar.dismiss();
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.recommend:
			// 调用方式一：直接打开全屏积分墙
			 OffersManager.getInstance(this).showOffersWall();
			break;

		case R.id.order:
			startActivity(new Intent(this, OrderActivity.class));
			break;

		case R.id.simulate:
			View layout = getLayoutInflater().inflate(R.layout.enter_simulate, null);
			final Dialog dialog = new Dialog(this);
			dialog.setTitle("温馨提示");
			dialog.show();
			dialog.getWindow().setContentView(layout);
			final EditText et_name = (EditText) layout.findViewById(R.id.et_name);
			TextView confirm = (TextView) layout.findViewById(R.id.confirm);
			TextView cancel = (TextView) layout.findViewById(R.id.cancel);
			confirm.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (TextUtils.isEmpty(et_name.getText().toString().trim())) {
						Toast.makeText(MainActivity.this, "请先输入考试姓名", Toast.LENGTH_SHORT).show();
					} else {
						ExamActivity.intentToExamActivity(MainActivity.this, et_name.getText().toString().trim());
						Toast.makeText(MainActivity.this, "考试开始", Toast.LENGTH_SHORT).show();
					}
					dialog.dismiss();
				}
			});

			cancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			break;

		case R.id.favorite:
			startActivity(new Intent(this, CollectActivity.class));
			break;

		case R.id.wrong:
			startActivity(new Intent(this, ErrorActivity.class));
			break;

		case R.id.history:
			startActivity(new Intent(this, HisResultActivity.class));
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (hasPressedBack) {
				finish();
				return true;
			}
			hasPressedBack = true;
			Toast.makeText(this, "再按一次退出", Toast.LENGTH_LONG).show();

			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					hasPressedBack = false;
				}
			}, 3000);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void showBanner() {

		// 广告条接口调用（适用于应用）
		// 将广告条adView添加到需要展示的layout控件中
		// LinearLayout adLayout = (LinearLayout) findViewById(R.id.adLayout);
		// AdView adView = new AdView(this, AdSize.FIT_SCREEN);
		// adLayout.addView(adView);

		// 广告条接口调用（适用于游戏）

		// 实例化LayoutParams(重要)
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		// 设置广告条的悬浮位置
		layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT; // 这里示例为右下角
		// 实例化广告条
		AdView adView = new AdView(this, AdSize.FIT_SCREEN);
		// 调用Activity的addContentView函数

		// 监听广告条接口
		adView.setAdListener(new AdViewListener() {

			@Override
			public void onSwitchedAd(AdView arg0) {
				Log.i("YoumiAdDemo", "广告条切换");
			}

			@Override
			public void onReceivedAd(AdView arg0) {
				Log.i("YoumiAdDemo", "请求广告成功");

			}

			@Override
			public void onFailedToReceivedAd(AdView arg0) {
				Log.i("YoumiAdDemo", "请求广告失败");
			}
		});
		this.addContentView(adView, layoutParams);
	}

	@Override
	protected void onStop() {
		// 如果不调用此方法，则按home键的时候会出现图标无法显示的情况。
		SpotManager.getInstance(this).onStop();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		SpotManager.getInstance(this).onDestroy();
		super.onDestroy();
	}

}
