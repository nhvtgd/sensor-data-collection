package com.getpebble.example.logging;

import android.util.Log;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

public class AccelData {
    private static final String TAG = AccelData.class.getSimpleName();

    final private int x;
    final private int y;
    final private int z;

    final private int activityLabel;

    public AccelData(byte[] data) {
        x = (data[0] & 0xff) | (data[1] << 8);
        y = (data[2] & 0xff) | (data[3] << 8);
        z = (data[4] & 0xff) | (data[5] << 8);
        activityLabel = data[6];
    }
    
    public int getX() {
    	return x;
    }
    
    public int getY() {
    	return y;
    }
    
    public int getZ() {
    	return z;
    }
    
    public int getActivityLabel() {
    	return activityLabel;
    }
    
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("x", x);
            json.put("y", y);
            json.put("z", z);
            json.put("a", activityLabel);
            return json;
        } catch (JSONException e) {
            Log.w(TAG, "problem constructing accel data, skipping " + e);
        }
        return null;
    }

    public static List<AccelData> fromDataArray(byte[] data) {
        List<AccelData> accels = new ArrayList<AccelData>();
        
        for (int i = 0; i < data.length; i += 8) { // one extra padding bytes
            accels.add(new AccelData(Arrays.copyOfRange(data, i, i + 8)));
        }
        return accels;
    }

    public String toString() {
    	return String.format("Activity: %d, x: %d y: %d z: %d", activityLabel, x,y,z); 
    };
}