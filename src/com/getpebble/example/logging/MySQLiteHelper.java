package com.getpebble.example.logging;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper extends SQLiteOpenHelper {

	  public static final String TABLE_ACCELEROMETER = "accelerometer";
	  public static final String COLUMN_ID = "_id";
	  public static final String COLUMN_TIMESTAMP = "timestamp";
	  public static final String COLUMN_X = "X";
	  public static final String COLUMN_Y = "Y";
	  public static final String COLUMN_Z = "Z";
	  public static final String COLUMN_ACTIVITY = "Activity";
	  public static final String COLUMN_SUBMITTED = "isSubmitted";
	  private static final String DATABASE_NAME = "accelerometer.db";
	  private static final int DATABASE_VERSION = 1;

	  // Database creation sql statement
	  private static final String DATABASE_CREATE = "create table "
	      + TABLE_ACCELEROMETER + "(" 
		  + COLUMN_ID + " integer primary key autoincrement, "
	      + COLUMN_TIMESTAMP + " text not null, "
	      + COLUMN_ACTIVITY + " text not null, "
	      + COLUMN_X + " int not null, " 
	      + COLUMN_Y + " int not null, "
	      + COLUMN_Z + " int not null, " 
	      + COLUMN_SUBMITTED + " int not null);";

	  public MySQLiteHelper(Context context) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	  }

	  @Override
	  public void onCreate(SQLiteDatabase database) {
	    database.execSQL(DATABASE_CREATE);
	  }

	  @Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		  db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCELEROMETER);
		  onCreate(db);
	  }
	} 