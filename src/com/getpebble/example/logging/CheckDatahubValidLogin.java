package com.getpebble.example.logging;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.getpebble.example.logging.login.SignInScreen;
import com.superurop.activitydetection.datahub.java.DataHubClient;

public class CheckDatahubValidLogin extends AsyncTask<String, Void, Boolean>{
	Intent intent;
	Context context;
	String username;
	String password;
	ProgressDialog progressDialog;
	
	public CheckDatahubValidLogin(Context context, Intent intent) {
		this.intent = intent;
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		progressDialog = new ProgressDialog(context);
		progressDialog.setMessage("Logging In");
		progressDialog.show();
	}
	@Override
	protected Boolean doInBackground(String... params) {
		// TODO Auto-generated method stub
		username = params[0];
		password = params[1];
		return DataHubClient.isValidLogin(params[0], params[1]);
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		progressDialog.cancel();
		if (result) {
			context.startActivity(intent);
			SharedPreferences sharedPref = context.getSharedPreferences(SignInScreen.SETTING, 0);
			SharedPreferences.Editor prefEditor = sharedPref.edit();
			prefEditor.putString(SignInScreen.USERNAME, username);
			prefEditor.putString(SignInScreen.PASSWORD, password);
			prefEditor.commit();
		} else {
			new AlertDialogManager().showAlertDialog(context, "Invalid Login", "Please try again", false, null);
		}
	}

}
