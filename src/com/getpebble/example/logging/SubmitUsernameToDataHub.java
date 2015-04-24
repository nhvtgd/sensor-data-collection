package com.getpebble.example.logging;

import android.os.AsyncTask;

import com.superurop.activitydetection.datahub.java.DataHubClient;

public class SubmitUsernameToDataHub extends AsyncTask<Void, Void, Void> {
	String repo_base;
	String email;
	
	public SubmitUsernameToDataHub(String repo_base, String email) {
		this.repo_base = repo_base;
		this.email = email;
	}

	@Override
	protected Void doInBackground(Void... params) {
		DataHubClient.submitUsernameToDB(repo_base, email);
		return null;
	}
	
}
