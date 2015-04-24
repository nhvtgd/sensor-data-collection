package com.getpebble.example.logging.login;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.getpebble.example.logging.ActivitySettingScreen;
import com.getpebble.example.logging.R;

public class SignInScreen extends Activity {
	private SharedPreferences settings;
	private SharedPreferences.Editor prefEditor;
	public static String SETTING = "SETTING";
	public static String USERNAME = "username";
	public static String EMAIL = "email";
	public static String PASSWORD = "password";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_in_page);
				
		Button register = (Button) findViewById(R.id.RegisterBttn_LogInPage);
		register.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), RegisterPage.class);
				startActivity(intent);
			}
		});
		
		// Log in
		Button logIn = (Button) findViewById(R.id.LogInBttn_LogInPage);
		
		// Log in with different account
		Button logInDiff = (Button) findViewById(R.id.DifferentSignInBttn_LogInPage);
		
		settings = getSharedPreferences(SETTING, 0);
		String username = settings.getString(USERNAME, null);
		if (username != null) {
			logIn.setText("Sign in as " + username);
			logIn.setTextSize(getResources().getDimension(R.dimen.textsize));
			
			logInDiff.setVisibility(View.VISIBLE);
			
			logIn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(v.getContext(), ActivitySettingScreen.class);
					startActivity(intent);
				}
			});
			
			logInDiff.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(v.getContext(), SignIn.class);
					startActivity(intent);
				}
			});
			
		} else {
			logIn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(v.getContext(), SignIn.class);
					startActivity(intent);
				}
			});
			
			logInDiff.setVisibility(View.INVISIBLE);
		}
		
		
	}
}
