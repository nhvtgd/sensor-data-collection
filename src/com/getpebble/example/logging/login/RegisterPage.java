package com.getpebble.example.logging.login;

import java.util.Map;

import org.apache.thrift.transport.TTransportException;
import org.json.JSONArray;

import com.getpebble.example.logging.ActivitySettingScreen;
import com.getpebble.example.logging.AlertDialogManager;
import com.getpebble.example.logging.ConnectionDetector;
import com.getpebble.example.logging.R;
import com.getpebble.example.logging.SubmitUsernameToDataHub;
import com.getpebble.example.logging.R.animator;
import com.getpebble.example.logging.R.id;
import com.getpebble.example.logging.R.layout;
import com.getpebble.example.logging.SubmitAccountCreationToDataHub;
import com.superurop.activitydetection.datahub.java.AccountCreationClient;
import com.superurop.activitydetection.datahub.java.AccountService;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ParseException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterPage extends Activity {
	private boolean registered;
	private SharedPreferences settings;
	private SharedPreferences.Editor prefEditor;
	private String email, username, password;
	private Button confirm;
	private long timeClick = System.currentTimeMillis();
	public static final String NETWORK_ERROR_TITLE = "NO CONNECTION";
	public static final String NETWORK_ERROR_MESSAGE = "Please check your connection and try again";
	public static int MINIMUM_LENGTH_PASSWORD = 4;
	TextView errorMessage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_page);
		// Make animation
		makeStartAnimation();

		// Initial Error Message text
		errorMessage = (TextView) findViewById(R.id.MessageError_Register);
		// Confirm Button
		confirm = (Button) findViewById(R.id.ConfirmButton_Register);
		confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Limit one click each second
				if (System.currentTimeMillis() - timeClick < 1000) return;
				else timeClick = System.currentTimeMillis();
				// Clear error message
				errorMessage.setText("");
				// User Name
				username = ((EditText) findViewById(R.id.UserName_Register)).getText().toString();
				// Email Address
				email = ((EditText) findViewById(R.id.EmailAddress_Register)).getText().toString();
				// Password
				password = ((EditText) findViewById(R.id.Password_Register)).getText().toString();
				// Confirm Password
				String confirmPassword = ((EditText) findViewById(R.id.ConfirmPassword_Register)).getText().toString();
				// Update
				if (checkValidInput(username, password, confirmPassword)) {
					SubmitAccountCreationToDataHub acc_creation = new SubmitAccountCreationToDataHub(v.getContext());
					acc_creation.execute(new String[] {username, password, email});
					
					SubmitUsernameToDataHub uname_submission = new SubmitUsernameToDataHub(username,email);
					uname_submission.execute();
				}
			}
		});
	}
	
	private boolean checkValidInput(String username, String password, String confirmPassword) {
		ConnectionDetector connection = new ConnectionDetector(this);
		if (!connection.isConnectingToInternet()) {
			new AlertDialogManager().showAlertDialog(this, NETWORK_ERROR_TITLE,
					NETWORK_ERROR_MESSAGE, false, null);
			return false;
		}
		String text = "";
		if (!isValidUserName(username)) {
			text = "Your user name cannot be empty";
			errorMessage.setText(text);
			Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
			return false;
		} else if (!isMatchPasswords(password, confirmPassword)) {
			text = "The password and confirmation password do not match";
			errorMessage.setText(text);
			Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
			return false;
		} else if (!isValidPassword(password)) {
			text = "Your password should have at least "+MINIMUM_LENGTH_PASSWORD+" characters";
			errorMessage.setText(text);
			Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	/**
	 * Require password has at least a minimum number of character
	 * 
	 * @param password
	 * @return
	 */
	public boolean isValidPassword(String password) {
		if (password.length() < MINIMUM_LENGTH_PASSWORD)
			return false;
		else
			return true;
	}
	
	/**
	 * Check new password matching
	 * 
	 * @param password
	 * @param confirmPassword
	 * @return
	 */
	public boolean isMatchPasswords(String password, String confirmPassword) {
		if (password.equals(confirmPassword))
			return true;
		else
			return false;
	}
	
	/**
	 * Set user name cannot be an empty string
	 * 
	 * @param user_name
	 * @return
	 */
	public boolean isValidUserName(String user_name) {
		if (user_name.length() == 0)
			return false;
		else
			return true;
	}
	
	/**
	 * Make animation move from right to left
	 */
	private void makeStartAnimation() {
		ViewGroup frame = (ViewGroup) findViewById(R.id.Frame_Register);
		Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.animator.slide_to_left_log_in_page);
		for (int i=0; i<frame.getChildCount(); i++) {
			View child = frame.getChildAt(i);
			child.setAnimation(animation);
		}
		animation.start();
	}

}
