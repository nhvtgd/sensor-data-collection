package com.getpebble.example.logging;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class GetFitLoggingActivity extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.getfit_webview);
	    
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
	        WebView.setWebContentsDebuggingEnabled(true);
	    }
	    
	    WebView webView = (WebView) findViewById(R.id.getfit_webview);

	    WebSettings webSettings = webView.getSettings();
	    webSettings.setJavaScriptEnabled(true);
	    webSettings.setAllowUniversalAccessFromFileURLs(true);
	    webView.loadUrl("https://getfit.mit.edu/Shibboleth.sso/" +
	    		"WAYF?target=https%3A%2F%2Fgetfit.mit.edu%2F%3Fq%3Dshib_login%2Fdashboard");
	    webView.setWebViewClient(new WebClient(this));
	    
	}
}
