package com.getpebble.example.logging;

public class AccelDataModel {
	private int x;
	private int y;
	private int z;
	private String activity;
	private String timestamp;
	private boolean isSubmitted;
	private long id;
	private boolean isDominantHand;
	private int intensityLevel;
	private String deviceID;
	
	public AccelDataModel(String timestamp, String activity, int x, int y, int z) {
		this.setX(x);
		this.setY(y);
		this.setZ(z);
		this.setActivity(activity);
		this.setTimestamp(timestamp);
		this.setSubmitted(false);
		this.setDominantHand(true);
		this.setIntensityLevel(3);
		this.setDeviceID("");
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isSubmitted() {
		return isSubmitted;
	}

	public void setSubmitted(boolean isSubmitted) {
		this.isSubmitted = isSubmitted;
	}
	
	@Override
	public String toString() {
		return String.format("Timestamp %s Activity %s X %d Y %d Z %d", this.timestamp, this.activity, this.x, this.y, this.z);
	}

	public boolean isDominantHand() {
		return isDominantHand;
	}

	public void setDominantHand(boolean isDominantHand) {
		this.isDominantHand = isDominantHand;
	}

	public int getIntensityLevel() {
		return intensityLevel;
	}

	public void setIntensityLevel(int intensityLevel) {
		this.intensityLevel = intensityLevel;
	}

	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
}
