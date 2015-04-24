package com.getpebble.example.logging;

import java.util.Map;

import org.apache.thrift.transport.TTransportException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.getpebble.example.logging.login.SignInScreen;
import com.superurop.activitydetection.datahub.java.AccountCreationClient;

public class SubmitAccountCreationToDataHub extends AsyncTask<String, Void, Map<String,String>> {
	Context act;
	SharedPreferences settings;
	ProgressDialog progressDialog;
	
	public SubmitAccountCreationToDataHub(Context context) {
		this.act = context;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		progressDialog = new ProgressDialog(act);
		progressDialog.setMessage("Registering user");
		progressDialog.show();
	}
	
	@Override
	protected Map<String,String> doInBackground(String... params) {
		String username = params[0];
		String password = params[1];
		String email = params[2];
			
		AccountCreationClient acc_client;
		Map<String, String> acc_creation_result = null;
		try {
			acc_client = new AccountCreationClient();
			acc_creation_result = acc_client.createAccoun(username, password, email);
			if (acc_creation_result.isEmpty()) { // sucessful if hashMap is empty
				settings = act.getSharedPreferences(SignInScreen.SETTING, 0);
				SharedPreferences.Editor prefEditor = settings.edit();
				prefEditor.putString(SignInScreen.USERNAME, username);
				prefEditor.putString(SignInScreen.EMAIL, email);
				prefEditor.putString(SignInScreen.PASSWORD, password);
				prefEditor.commit();
				Intent intent = new Intent(act, ActivitySettingScreen.class);
				act.startActivity(intent);
			}
		} catch (TTransportException e) {
			e.printStackTrace();
		} 
		return acc_creation_result;
	}
	
	@Override
	protected void onPostExecute(Map<String, String> acc_creation_result) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(act.getApplicationContext(), SignInScreen.class);
		if (!acc_creation_result.isEmpty()) {
			if (acc_creation_result.containsKey("email")) {
				new AlertDialogManager().showAlertDialog(act, "Account existed under email " + acc_creation_result.get("email"),
						"Do you want to go to sign in page?",false, intent);
			} else if (acc_creation_result.containsKey("username")) {
				new AlertDialogManager().showAlertDialog(act, "Account existed under username " + acc_creation_result.get("username"),
						"Do you want to go to sign in page?", false, intent);
			} else {
				new AlertDialogManager().showAlertDialog(act.getApplicationContext(), "Unknown error",
						"Please try again later", false, null);
			}
		}
		progressDialog.cancel();
	}
}
