package com.getpebble.example.logging;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.example.logging.login.SignInScreen;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;

public class TermAndCondition extends Activity{
	SharedPreferences settings;
	SharedPreferences.Editor prefEditor;
	
	CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.term_and_condition);
        
		settings = getSharedPreferences(SignInScreen.SETTING, 0);
		if (settings.getBoolean("Accept", false)) {
			Intent intent = new Intent(this, SignInScreen.class);
			startActivity(intent);
		}
		
		prefEditor = settings.edit();
		
        checkBox = (CheckBox) findViewById(R.id.agreeBox);
        
        Button submission = (Button) findViewById(R.id.agreeButton);
        submission.setOnClickListener(new OnClickListener() {
        	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (checkBox.isChecked()) {
					prefEditor.putBoolean("Accept", true);
					prefEditor.commit();
					Intent intent = new Intent(v.getContext(), SignInScreen.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				} else {
					new AlertDialog.Builder(v.getContext())
				    .setTitle("Aggree to Term and Conditions")
				    .setMessage("You haven't agree to term and condition yet")
				    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) { 
				        	
				        }
				     })
				     .setIcon(android.R.drawable.ic_dialog_alert)
				     .show();
				}
			}
		});
    }
	
}
