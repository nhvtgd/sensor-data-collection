package com.getpebble.example.logging;

import static android.widget.Toast.makeText;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.PebbleKit.PebbleDataLogReceiver;
import com.getpebble.android.kit.PebbleKit.PebbleDataReceiver;
import com.getpebble.android.kit.util.PebbleDictionary;
import com.google.common.primitives.UnsignedInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Sample code demonstrating how Android applications can receive data logs from Pebble.
 */
public class ExampleDataLoggingActivity extends Activity {
    private static final UUID ACTIVITY_RECOGNITION = UUID.fromString("2834fa63-f127-494d-a231-c933bf0721d2");
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");

    private final StringBuilder mDisplayText = new StringBuilder();
    private int dataPoint = 0;
    long second = 0;
    
    ArrayAdapter<String> activityListAdapter;
    
    ArrayAdapter<String> accelerometerListAdapter;
    
    // Activity List
    ListView activityListView;
    
    // Accelerometer list
    ListView accelerometerListView;
    
    EditText search;
    
    long last_timestamp = 0;
    
    private PebbleKit.PebbleDataLogReceiver mDataLogReceiver = null;
    
    private PebbleDataReceiver mActivityReceiver = null;
    
    /**
	 * The list that will store the filter item when user start typing
	 */
	ArrayList<String> filterList = new ArrayList<String>();
	
	/**
	 * The list that will store the resulting items
	 */
	List<String> activityList = new ArrayList<String>();
	
	List<String> accelerometerList = new ArrayList<String>();
	
	Activity act;
	
	static final int NO_SAVE_ACTIVITY = 2;
	
	static final int SAVE_ACTIVITY = 3;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);
        act = this;
         activityList = Arrays.asList("Aerobics", "American Football", "Badminton", 
        		 "Ballet", "Bandy", "Baseball", "Basketball", "Beach Volleyball", 
        		 "Body Pump", "Bowling", "Boxing", "Circuit Training", "Cleaning", 
        		 "Climbing", "Cricket", "Cross country skiing", "Curling", "Cycling", 
        		 "Dancing", "Disk Ultimate", "Downhill Skiing", "Elliptical Training", 
        		 "Fencing", "Floorball", "Golfing", "Gym Training", "Gymnastics", 
        		 "Handball", "Hockey", "Indoor Cycling", "Kayaking", "Kettlebell", 
        		 "Kite Surfing", "Lacrosse", "Marshall Arts", "Paddling", "Paintball", 
        		 "Parkour", "Petanque", "Pilates", "Polo", "Racquetball", "Riding", 
        		 "Roller Blading", "Roller Skiing", "Roller Skating", "Rowing", "Rugby", 
        		 "Running", "Running on Treadmill", "Scuba Diving", "Shoveling", "Shoveling Snow", 
        		 "Skateboarding", "Snowboarding", "Snow Shoeing", "Soccer", "Spinning", "Squash", 
        		 "Stair Climbing", "Stretching", "Surfing", "Swimming", "Table Tennis", "Tennis", 
        		 "Volleyball", "Walking", "Walking on Treadmill", "Water Polo", "Weight Training", 
        		 "Wheelchair", "Wind Surfing", "Wrestling", "Yoga", "Zumba");
         
         activityListView = (ListView) findViewById(R.id.activity_list);
         activityListAdapter = new ArrayAdapter<String>(this,
        		android.R.layout.simple_list_item_1, activityList);
         activityListView.setAdapter(activityListAdapter);
         activityListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final String selectedActivity = (String) activityListView.getItemAtPosition(position);
				final PebbleDictionary dict = new PebbleDictionary();
				
				// create a diablo to ask if you want to save the activity
				new AlertDialog.Builder(act)
			    .setTitle("Save Activity")
			    .setMessage("Do you want to save this activity on your watch?")
			    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
			        	dict.addString(SAVE_ACTIVITY, selectedActivity);
			        	PebbleKit.sendDataToPebble(act, ACTIVITY_RECOGNITION, dict);				
			        }
			     })
			    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
			            dict.addString(NO_SAVE_ACTIVITY, selectedActivity);
			            PebbleKit.sendDataToPebble(act, ACTIVITY_RECOGNITION, dict);				
			        }
			     })
			    .setIcon(android.R.drawable.ic_dialog_alert)
			     .show();

				
			}
		});	
         
         accelerometerListView = (ListView) findViewById(R.id.accelerometer_list);
         accelerometerListAdapter = new ArrayAdapter<String>(this,
        		android.R.layout.simple_list_item_1, accelerometerList);
         accelerometerListView.setAdapter(accelerometerListAdapter);
        
         search = (EditText) findViewById(R.id.search_bar_listView);
         search.addTextChangedListener(new TextChangeRecorder());
         search.setSingleLine();
        DATE_FORMAT.setTimeZone(TimeZone.getDefault());
        
        
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mDataLogReceiver != null) {
            unregisterReceiver(mDataLogReceiver);
            mDataLogReceiver = null;
        }
        if (mActivityReceiver != null) {
        	unregisterReceiver(mActivityReceiver);
        	mActivityReceiver = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        

        // To receive data logs, Android applications must register a "DataLogReceiver" to receive data.
        //
        // In this example, we're implementing a handler to receive unsigned integer data that was logged by a
        // corresponding watch-app. In the watch-app, three separate logs were created, one per animal. Each log was
        // tagged with a key indicating the animal to which the data corresponds. So, the tag will be used here to
        // look up the animal name when data is received.
        //
        // The data being received contains the seconds since the epoch (a timestamp) of when an ocean faring animal
        // was sighted. The "timestamp" indicates when the log was first created, and will not be used in this example.

        final Handler handler = new Handler();
        mDataLogReceiver = new PebbleDataLogReceiver(ACTIVITY_RECOGNITION) {
            @Override
            public void receiveData(Context context, UUID logUuid, UnsignedInteger timestamp, UnsignedInteger tag, byte[] data) {
            	for (AccelData reading : AccelData.fromDataArray(data)) {
            		if (second == 0 ) {
            		    accelerometerList.add(String.format("Starting recording at %d", timestamp.longValue()));
            		    handler.post(new Runnable() {
            		    	@Override
            		    	public void run() {
            		    		updateUi();
            		    	}
            		    });
            		}
            		accelerometerList.add(reading.toString());
            		
        		    second = timestamp.longValue();
            		dataPoint++;
            		
            		last_timestamp = timestamp.longValue();

            	}
            }
            
            @Override
            public void onFinishSession(Context context, UUID logUuid, UnsignedInteger timestamp, UnsignedInteger tag) {
            	super.onFinishSession(context, logUuid, timestamp, tag);
            	second = timestamp.longValue() - second;
            	accelerometerList.add(String.format("Recording end at %d collect %d points", timestamp.longValue(), dataPoint));
            	handler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateUi();
                    }
                });
            }
            
        };
        
       this.mActivityReceiver = new PebbleDataReceiver(ACTIVITY_RECOGNITION) {
			
			@Override
			public void receiveData(Context context, int transactionId,
					PebbleDictionary data) {
				
				
			}
		};
		
		PebbleKit.registerReceivedDataHandler(this, mActivityReceiver);

        PebbleKit.registerDataLogReceiver(this, mDataLogReceiver);

        PebbleKit.requestDataLogsForApp(this, ACTIVITY_RECOGNITION);
    }

    private void updateUi() {
        accelerometerListAdapter.notifyDataSetChanged();
        Toast.makeText(this, String.format("Receive %d in %d seconds", dataPoint, second), Toast.LENGTH_LONG).show();
    }

    private String getUintAsTimestamp(UnsignedInteger uint) {
        return DATE_FORMAT.format(new Date(uint.longValue() * 1000L)).toString();
    }
    
    private class TextChangeRecorder implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			filterList.clear();

			for (int i = 0; i < activityList.size(); i++) {
				String searchText = search.getText().toString();
				if (isSubString(searchText, activityList.get(i)))
					filterList.add(activityList.get(i));

			}

			activityListView.setAdapter(new ArrayAdapter<String>(act, android.R.layout.simple_list_item_1,filterList));

		}
	}
    
    /**
     * helper to determine whether text2 is a substring of text1*/
    public boolean isSubString(String text1, String text2) {
		if (text1 == null)
			return true;
		if (text2 == null || text2.length() < text1.length())
			return false;
		int len1 = text1.length();
		for (int i = 0; i < text2.length(); i++) {
			if (i + len1 <= text2.length()) {
				if (text2.substring(i, i + len1).equalsIgnoreCase((text1)))
					return true;
			}
		}
		return false;
	}
}
