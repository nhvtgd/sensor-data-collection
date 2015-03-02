package com.getpebble.example.logging;

public class AccelDataModel {
	private int x;
	private int y;
	private int z;
	private String activity;
	private String timestamp;
	private boolean isSubmitted;
	private long id;
	
	public AccelDataModel(String timestamp, String activity, int x, int y, int z) {
		this.setX(x);
		this.setY(y);
		this.setZ(z);
		this.setActivity(activity);
		this.setTimestamp(timestamp);
		this.setSubmitted(false);
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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
