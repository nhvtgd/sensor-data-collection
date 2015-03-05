package com.getpebble.example.logging;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class ActivitySettingScreen extends Activity{
	Spinner dominant_hand_spinner;
	Spinner intensity_level_spinner;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		dominant_hand_spinner = (Spinner) findViewById(R.id.dominant_hand_spinner);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> dominant_hand_adapter = ArrayAdapter.createFromResource(this,
		        R.array.dominant_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		dominant_hand_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		dominant_hand_spinner.setAdapter(dominant_hand_adapter);
		
		intensity_level_spinner = (Spinner) findViewById(R.id.intensity_level_spinner);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> intensity_level_adapter = ArrayAdapter.createFromResource(this,
		        R.array.intensity_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		intensity_level_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		intensity_level_spinner.setAdapter(intensity_level_adapter);
		
		Button submitButton = (Button) findViewById(R.id.next_bttn_activity_setting);
		submitButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String dominant_hand_selection = dominant_hand_spinner.getSelectedItem().toString();
				String intensity_selection = intensity_level_spinner.getSelectedItem().toString();
				
				Intent intent = new Intent(v.getContext(),AccelDataLoggingActivity.class);
				intent.putExtra("dominant_hand", dominant_hand_selection);
				intent.putExtra("intensity_level", intensity_selection);
				v.getContext().startActivity(intent);
			}
		});
		
	}
}
