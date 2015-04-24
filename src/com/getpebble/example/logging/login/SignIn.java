package com.getpebble.example.logging.login;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ParseException;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.getpebble.example.logging.AccelDataLoggingActivity;
import com.getpebble.example.logging.ActivitySettingScreen;
import com.getpebble.example.logging.AlertDialogManager;
import com.getpebble.example.logging.CheckDatahubValidLogin;
import com.getpebble.example.logging.ConnectionDetector;
import com.getpebble.example.logging.R;
import com.getpebble.example.logging.SubmitUsernameToDataHub;
import com.superurop.activitydetection.datahub.java.DataHubClient;


public class SignIn extends Activity {
	private SharedPreferences settings;
	private SharedPreferences.Editor prefEditor;
	private String email, username, password;
	private Button confirm;
	private TextView errorMessage;
	private long timeClick = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_in);
				
		// Animation
		makeAnimation();
		// SharedPreferences
		settings = getSharedPreferences(SignInScreen.SETTING, 0);
		prefEditor = settings.edit();
		errorMessage = (TextView) findViewById(R.id.signInErrorMassage);

		// Confirm Button
		confirm = (Button) findViewById(R.id.signInConfirmButton);
		confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (System.currentTimeMillis() - timeClick < 1000) return;
				else timeClick = System.currentTimeMillis();
				((TextView)findViewById(R.id.signInErrorMassage)).setText("");
				// Email Address
				EditText usernameField = (EditText) findViewById(R.id.signInUsername);
				username = usernameField.getText().toString();
				// Password
				EditText passwordField = (EditText) findViewById(R.id.signInPassword);
				password = passwordField.getText().toString();
				// Update
				checkValidLogIn(username, password);
			}
		});
	}
	
	public void checkValidLogIn(String user_name, String pass) {
		ConnectionDetector connection = new ConnectionDetector(this);
		if (!connection.isConnectingToInternet()) {
			new AlertDialogManager().showAlertDialog(this, RegisterPage.NETWORK_ERROR_TITLE,
					RegisterPage.NETWORK_ERROR_MESSAGE, false, null);
		}
		// instead of using webview to do it, we can try to connect to 
		// datahub with that credential
		// determine valid credential using that username and password.
		CheckDatahubValidLogin loginChecker = new CheckDatahubValidLogin(this, new Intent(this, ActivitySettingScreen.class));
		loginChecker.execute(new String[] {user_name, pass});
		
		// The user may already have a datahub account, so we need to try to submit it, we don't know the email
		// address
		SubmitUsernameToDataHub uname_submission = new SubmitUsernameToDataHub(username, "");
		uname_submission.execute();
	}

	/**
	 * Make animation move from right to left
	 */
	private void makeAnimation() {
		ViewGroup frame = (ViewGroup) findViewById(R.id.LogIn_LinearLayout);
		Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.animator.slide_to_left_log_in_page);
		for (int i=0; i<frame.getChildCount(); i++) {
			View child = frame.getChildAt(i);
			child.setAnimation(animation);
		}
		animation.start();
	}
}
