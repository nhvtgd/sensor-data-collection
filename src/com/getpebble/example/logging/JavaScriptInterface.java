package com.getpebble.example.logging;

import android.content.Context;
import android.webkit.JavascriptInterface;

public class JavaScriptInterface {
	Context mContext;
	
	public JavaScriptInterface(Context c) {
		mContext = c;
	}

	@JavascriptInterface
	public void passJSON(String array, String jsonObj) {
		System.out.println(array);
		System.out.println(jsonObj);
	}
}