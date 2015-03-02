package com.getpebble.example.logging;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.superurop.activitydetection.datahub.java.DataHub;
import com.superurop.activitydetection.datahub.java.DataHubClient;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class SubmitAccelDataToDataHub extends AsyncTask<AccelDataModel, Void, Boolean> {
	
	private Exception exception;
	Activity act;
	
	public SubmitAccelDataToDataHub(Activity activity) {
		this.act = activity;
	}

	@Override
	protected Boolean doInBackground(AccelDataModel... params) {
		try {
			DataHubClient client = new DataHubClient();
			JSONArray jsArray = new JSONArray();
			for (AccelDataModel accelData : params) {
				JSONObject jsobject = new JSONObject();
				jsobject.put("DeviceID", accelData.getId());
				jsobject.put("Timestamp", accelData.getTimestamp());
				jsobject.put("Activity", accelData.getActivity());
				jsobject.put("X", accelData.getX());
				jsobject.put("Y", accelData.getY());
				jsobject.put("Z", accelData.getZ());
				jsArray.put(jsobject);
			}
			client.pushData(jsArray.toString());
		} catch (Exception e) {
			Log.e("[SUBMIT DATA", "Fail to submit data to datahub");
			return false;
		}
		return true;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		String r = result ? "Successful" : "Failure";
		Toast.makeText(act, "Submit data to server is " + r, Toast.LENGTH_LONG).show();
	}
	
}
