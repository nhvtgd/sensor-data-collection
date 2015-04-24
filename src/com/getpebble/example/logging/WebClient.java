package com.getpebble.example.logging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebClient extends WebViewClient{
	Context context;
	boolean loadingFinished = true;
	boolean redirect = false;
	public WebClient(Context c) {
		context = c;
	}
	
	
	   @Override
	   public boolean shouldOverrideUrlLoading(WebView view, String url) {
		   if (!url.startsWith("https://getfit.mit.edu/?q=dashboard")) {
			   return false;
		   }
	       if (!loadingFinished) {
	          redirect = true;
	       }

	       loadingFinished = false;
	       view.loadUrl(url);
	       return true;
	   }
	   
	   @Override
	   public void onPageStarted(WebView view, String url, Bitmap noop) {
		    if (url.startsWith("https://getfit.mit.edu/?q=dashboard")) {
		    	loadingFinished = false;
		    }
	        //SHOW LOADING IF IT ISNT ALREADY VISIBLE  
	    }

	   @Override
	   public void onPageFinished(WebView view, String url) {
		   if (!url.startsWith("https://getfit.mit.edu/?q=dashboard")) {
			   return;
		   }
	       if(!redirect){
	          loadingFinished = true;
	       }

	       if(loadingFinished && !redirect){
			   view.setWebChromeClient(new WebChromeClient());
			   view.addJavascriptInterface(new JavaScriptInterface(context), "Android");
			   
			   String jscontent = "";
		        try{
		            InputStream is = context.getAssets().open("token_extraction_android.js"); //am = Activity.getAssets()
		            InputStreamReader isr = new InputStreamReader(is);
		            BufferedReader br = new BufferedReader(isr);

		            String line;
		            while (( line = br.readLine()) != null) {
		                jscontent += line +"\n";
		            }
		            is.close(); 
		        }
		        catch(Exception e){}
		        String script = "javascript:" + jscontent;
		        view.loadUrl(script); 
			   //injectScriptFile(view, "token_extraction_android.js");
			   
	       } else{
	          redirect = false; 
	       }
	    }
	   
	   private void injectScriptFile(WebView view, String scriptFile) {
	          InputStream input;
	          try {
	             input = context.getAssets().open(scriptFile);
	             byte[] buffer = new byte[input.available()];
	             input.read(buffer);
	             input.close();

	             // String-ify the script byte-array using BASE64 encoding !!!
	             String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
	             view.loadUrl("javascript:(function() {" +
	                          "var parent = document.getElementsByTagName('head').item(0);" +
	                          "var script = document.createElement('script');" +
	                          "script.type = 'text/javascript';" +
	             // Tell the browser to BASE64-decode the string into your script !!!
	                          "script.innerHTML = window.atob('" + encoded + "');" +
	                          "parent.appendChild(script)" +
	                          "})()");
	          } catch (IOException e) {
	             // TODO Auto-generated catch block
	             e.printStackTrace();
	          }
	       }
	   
}
