package com.getpebble.example.logging;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class ActivitySettingScreen extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		
		Spinner dominant_hand_spinner = (Spinner) findViewById(R.id.dominant_hand_spinner);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> dominant_hand_adapter = ArrayAdapter.createFromResource(this,
		        R.array.dominant_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		dominant_hand_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		dominant_hand_spinner.setAdapter(dominant_hand_adapter);
		
		Spinner intensity_level_spinner = (Spinner) findViewById(R.id.intensity_level_spinner);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> intensity_level_adapter = ArrayAdapter.createFromResource(this,
		        R.array.intensity_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		intensity_level_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		intensity_level_spinner.setAdapter(intensity_level_adapter);
	}
}
