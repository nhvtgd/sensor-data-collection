package com.getpebble.example.logging;

import java.util.Date;
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
	String repo_base;
	boolean hasStopSignal;
	boolean hasStartSignal;
	
	public SubmitAccelDataToDataHub(Activity activity, String repo_base, boolean hasStopSignal, boolean hasStartSignal) {
		this.act = activity;
		this.repo_base = repo_base;
		this.hasStopSignal = hasStopSignal;
		this.hasStartSignal = hasStartSignal;
	}

	@Override
	protected Boolean doInBackground(AccelDataModel... params) {
		try {
			DataHubClient client = new DataHubClient(repo_base);
			JSONArray jsArray = new JSONArray();
			for (int i = 0 ; i < params.length; i ++) {
				AccelDataModel accelData = params[i];
				JSONObject jsobject = new JSONObject();
				if (hasStartSignal && i == 0) {
					jsobject.put("START", accelData.getTimestamp());
				}
				jsobject.put("DeviceID", accelData.getDeviceID());
				jsobject.put("Timestamp", accelData.getTimestamp());
				jsobject.put("Activity", accelData.getActivity());
				jsobject.put("X", accelData.getX());
				jsobject.put("Y", accelData.getY());
				jsobject.put("Z", accelData.getZ());
				jsobject.put("DHand", accelData.isDominantHand() ? 1 : 0);
				jsobject.put("Intensity", accelData.getIntensityLevel());
				jsArray.put(jsobject);
			}
			if (hasStopSignal) { // get the last data point
				if (params.length > 0) {
					((JSONObject) jsArray.get(jsArray.length()-1)).put("STOP", params[params.length-1].getTimestamp());
				}
			}
			if (jsArray.length() > 0 ) {
				client.pushData(jsArray.toString());
			}
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
