package com.mobile.cetfour.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class CommUtils {

	public static String etToString(EditText et) {
		return et.getText().toString().trim();
	}

	public static String tvToString(TextView tv) {
		return tv.getText().toString().trim();
	}
	
	public static int etToInt(EditText et) {
		if (TextUtils.isEmpty(et.getText().toString().trim())) {
			return 0;
		} else {
			return Integer.valueOf(et.getText().toString().trim());
		}
	}
	
	/**
	 * 网络是否可用
	 */
	public synchronized static boolean isNetworkAvailable(Context context) {
		boolean result = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = null;
		if (null != connectivityManager) {
			networkInfo = connectivityManager.getActiveNetworkInfo();
			if (null != networkInfo && networkInfo.isAvailable() && networkInfo.isConnected()) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * 隐藏软键盘
	 * 
	 * @param activity
	 * @param view
	 */
	public static void hideSoftInput(Context activity, View view) {
		try {
			if (view != null) {
				InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
				if(imm.isActive()) {
					imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
     * 验证手机号码格式
     * 
     * 移动号码段: 139、138、137、136、135、134、150、151、152、157、158、159、182、183、187、188、147
     * 4G号码段  : 178
     * 联通号码段: 130、131、132、136、185、186、145
     * 4G号码段  : 176
     * 电信号码段: 133、153、180、189
     * 4G号码段  : 177
     * 
     * @param phoneNum
     * @return
     */
	public static boolean checkMobilePhoneNum(String phoneNum) {
		if (TextUtils.isEmpty(phoneNum)) {
			return false;
		}
		String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0|2|3,5-9])|(17[6|7|8]))\\d{8}$";
		try {
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(phoneNum);
			return m.matches();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
}
