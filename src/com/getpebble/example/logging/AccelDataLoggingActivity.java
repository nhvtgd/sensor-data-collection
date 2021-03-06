package com.getpebble.example.logging;

import static android.widget.Toast.makeText;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.IntentService;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
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
import com.getpebble.example.logging.login.SignInScreen;
import com.superurop.activitydetection.datahub.java.DBException;
import com.superurop.activitydetection.datahub.java.DataHubClient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.thrift.TException;

/**
 * Sample code demonstrating how Android applications can receive data logs from Pebble.
 */
public class AccelDataLoggingActivity extends Activity {
    private static final UUID ACTIVITY_RECOGNITION = UUID.fromString("2834fa63-f127-494d-a231-c933bf0721d2");
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private int STOP_SIGNAL = 0xFFFF;
    private int START_SIGNAL = 0x0000;
    static final String ACTIVITY_LABEL = "Activity"; // parent node
    static final String DURATION = "Duration";
    
    private final StringBuilder mDisplayText = new StringBuilder();
    private int dataPoint = 0;
    long second = 0;
    
    ArrayAdapter<String> activityListAdapter;
    
    LazyAdapter accelerometerListAdapter;
    
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
	
	List<AccelDataModel> accelerometerDataSource = new ArrayList<AccelDataModel>();
	
	Activity act;
	
	static final int NO_SAVE_ACTIVITY = 2;
	
	static final int SAVE_ACTIVITY = 3;
	
	String currentActivity, deviceID, dominant_hand, intensity_level, repo_base = "";
	
	SharedPreferences pref;
	
	boolean receiveStopSignal;
	boolean receiveStartSignal;
	
	long start, stop;
	
	ArrayList<HashMap<String, String>> activity_duration_list = new ArrayList<HashMap<String, String>>();
	
	IntentService backgroundService; 
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);
        
        // in order to arrive to this screen the user must have registered or login
        // and that should be saved in share preference already
        pref = getSharedPreferences(SignInScreen.SETTING, 0);
        repo_base = pref.getString(SignInScreen.USERNAME, null);
        assert repo_base != null;
        
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
        		 "Running", "Running on Treadmill", "Scuba Diving", "Shoveling", "Shoveling Snow", "Sitting",
        		 "Skateboarding", "Snowboarding", "Snow Shoeing", "Soccer", "Spinning", "Squash", 
        		 "Stair Climbing", "Stretching", "Surfing", "Swimming", "Table Tennis", "Tennis", 
        		 "Volleyball", "Walking", "Walking on Treadmill", "Water Polo", "Weight Training", 
        		 "Wheelchair", "Wind Surfing", "Wrestling", "Yoga", "Zumba");
         
         
         boolean connected = PebbleKit.isWatchConnected(getApplicationContext());
         if (!connected){
        	 showDisconnectionAlert();
         }
         
         PebbleKit.registerPebbleDisconnectedReceiver(getApplicationContext(), new BroadcastReceiver() {

        	  @Override
        	  public void onReceive(Context context, Intent intent) {
        		  showDisconnectionAlert();
        	  }

        	});
         
         // send signal to pebble to start the app
         // Launching my app
         PebbleKit.startAppOnPebble(getApplicationContext(), ACTIVITY_RECOGNITION);
         
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

				currentActivity = selectedActivity;
				
				// create a dialog to ask if you want to save the activity
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
         
        // initialize accelerometer list view
        accelerometerListView = (ListView) findViewById(R.id.accelerometer_list);
        accelerometerListAdapter = new LazyAdapter(this, activity_duration_list); 
        		
        accelerometerListView.setAdapter(accelerometerListAdapter);
        
        // Enable search bar
        search = (EditText) findViewById(R.id.search_bar_listView);
        search.addTextChangedListener(new TextChangeRecorder());
        search.setSingleLine();
        DATE_FORMAT.setTimeZone(TimeZone.getDefault());
        
        // initialize device ID
        deviceID = getUserDeviceID();  
        
        // get dominant hand and intensity from previous activity so we 
        // can send at the same time and other data and don't generate more 
        // network call unneccessary
        Intent intent = getIntent();
        dominant_hand = intent.getStringExtra("dominant_hand");
        intensity_level = intent.getStringExtra("intensity_level");
        
        
        // register
        final Handler handler = new Handler();
        mDataLogReceiver = new PebbleDataLogReceiver(ACTIVITY_RECOGNITION) {
            @Override
            public void receiveData(Context context, UUID logUuid, Long timestamp, Long tag, byte[] data) {
            	for (AccelData reading : AccelData.fromDataArray(data)) {
            		            		
            		//accelerometerList.add(reading.toString());
            		String activity = getActivityLabel(reading.getActivityLabel());
            		if (activity == "") { // this is customized activity
            			activity = currentActivity;
            		} else if (currentActivity == null || currentActivity.length() == 0) {
            			currentActivity = activity;
            		}
            		AccelDataModel accelDataSource = new AccelDataModel(String.valueOf(System.currentTimeMillis()), 
            					activity, reading.getX(), reading.getY(), reading.getZ());
            		accelDataSource.setDominantHand(dominant_hand.equals("Yes"));
            		accelDataSource.setIntensityLevel(Integer.parseInt(intensity_level));
            		accelDataSource.setDeviceID(deviceID);
            		
            		accelerometerDataSource.add(accelDataSource);
        		    second = timestamp.longValue();
            		dataPoint++;
            		
            		last_timestamp = timestamp.longValue();
            	}
            }
            
            @Override
            public void onFinishSession(Context context, UUID logUuid, Long timestamp, Long tag) {
            	super.onFinishSession(context, logUuid, timestamp, tag);
            	
            	//accelerometerList.add(String.format("Recording end at %d collect %d points", timestamp.longValue(), dataPoint));
            	
            	
            	handler.post(new Runnable() {
                    @Override
                    public void run() {
                    	updateUi();
                    	SubmitAccelDataToDataHub task = new SubmitAccelDataToDataHub(act, repo_base, receiveStopSignal, receiveStartSignal);
                    	AccelDataModel[] data = new AccelDataModel[accelerometerDataSource.size()];
                    	task.execute(accelerometerDataSource.toArray(data));
                    	
                    	// whenever we submit the data, we need to clear the state
                    	receiveStopSignal = false;
                    	receiveStartSignal = false;
                    }
                });
                
            }
            
        };
        
       this.mActivityReceiver = new PebbleDataReceiver(ACTIVITY_RECOGNITION) {
			
			@Override
			public void receiveData(Context context, int transactionId,
					PebbleDictionary data) {
				if (data.contains(STOP_SIGNAL)) {
					receiveStopSignal = true;
					stop = new Date().getTime();
					HashMap<String, String> act_duration = new HashMap<String,String>();
					String activity = getActivityLabel(data.getInteger(STOP_SIGNAL).intValue());
					if (activity.length() == 0) {
						activity = currentActivity;
					} else if (currentActivity == null || currentActivity.length() == 0) {
						currentActivity = activity;
					}
						
					act_duration.put(ACTIVITY_LABEL, activity); 
					act_duration.put(DURATION, String.valueOf((stop-start)/1000));
					activity_duration_list.add(act_duration);
					
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							updateUi();							
						}
					});
				} else if (data.contains(START_SIGNAL)) {
					start = new Date().getTime();
					receiveStartSignal = true;
					String activity = getActivityLabel(data.getInteger(START_SIGNAL).intValue());
					if (activity.length() == 0) {
						activity = currentActivity;
					} else if (currentActivity == null || currentActivity.length() == 0) {
						currentActivity = activity;
					}
				}				
			}
		};
		
		PebbleKit.registerReceivedDataHandler(this, mActivityReceiver);

        PebbleKit.registerDataLogReceiver(this, mDataLogReceiver);

        PebbleKit.requestDataLogsForApp(this, ACTIVITY_RECOGNITION);
    }
    
    private String getActivityLabel(int label) {
    	String activity = "";
		if (label == 0) {
			activity = "Sitting";
		} else if (label == 1) {
			activity = "Walking";
		
		} else {
			activity = currentActivity;
		}
		return activity;
    }
    
    private void showDisconnectionAlert() {
    	new AlertDialog.Builder(this)
	    .setTitle("Pebble connection")
	    .setMessage("No Pebble Watch detected. No activity will be logged.")
	    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	        	
	        }
	     })
	     .setIcon(android.R.drawable.ic_dialog_alert).create()
	     .show();
    }
    
    public String getUserDeviceID() {
    	final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        return deviceId;
    }

    @Override
    protected void onDestroy() {
    	super.onDestroy();
        if (mDataLogReceiver != null) {
            unregisterReceiver(mDataLogReceiver);
            mDataLogReceiver = null;
        }
        if (mActivityReceiver != null) {
        	unregisterReceiver(mActivityReceiver);
        	mActivityReceiver = null;
        }
        
        // Closing my app
        PebbleKit.closeAppOnPebble(getApplicationContext(), ACTIVITY_RECOGNITION);
    }	
    
    private void updateUi() {
        accelerometerListAdapter.notifyDataSetChanged();
        Toast.makeText(this, String.format("Receive %d in %d seconds", dataPoint, second), Toast.LENGTH_LONG).show();
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
