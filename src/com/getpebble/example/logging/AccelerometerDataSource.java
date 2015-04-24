package com.getpebble.example.logging;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AccelerometerDataSource {
	// Database fields
	  private SQLiteDatabase database;
	  private MySQLiteHelper dbHelper;
	  private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
			  					MySQLiteHelper.COLUMN_TIMESTAMP,
			  					MySQLiteHelper.COLUMN_ACTIVITY,
			  					MySQLiteHelper.COLUMN_X,
			  					MySQLiteHelper.COLUMN_Y,
			  					MySQLiteHelper.COLUMN_Z,
			  					MySQLiteHelper.COLUMN_SUBMITTED};

	  public AccelerometerDataSource(Context context) {
	    dbHelper = new MySQLiteHelper(context);
	  }

	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }

	  public void close() {
	    dbHelper.close();
	  }

	  public void createAccelerometer(String timestamp, String activity, int x, int y, int z) {
	    ContentValues values = new ContentValues();
	    values.put(MySQLiteHelper.COLUMN_TIMESTAMP, timestamp);
	    values.put(MySQLiteHelper.COLUMN_ACTIVITY, activity);
	    values.put(MySQLiteHelper.COLUMN_X, x);
	    values.put(MySQLiteHelper.COLUMN_Y, y);
	    values.put(MySQLiteHelper.COLUMN_Z, z);
	    values.put(MySQLiteHelper.COLUMN_SUBMITTED, 0);
	    
	    long insertId = database.insert(MySQLiteHelper.TABLE_ACCELEROMETER, null,
	        values);
	  }
	  
	  public void updateAccelerometer(AccelDataModel model) {
		  ContentValues value = createContentValueFromAccelData(model);
		  
		  database.update(MySQLiteHelper.TABLE_ACCELEROMETER, value, MySQLiteHelper.COLUMN_ID + " = " + model.getDeviceID(), null);
	  }
	  
	  private ContentValues createContentValueFromAccelData(AccelDataModel model) {
		  ContentValues value = new ContentValues();
		  value.put(MySQLiteHelper.COLUMN_ID, model.getDeviceID());
		  value.put(MySQLiteHelper.COLUMN_TIMESTAMP, model.getTimestamp());
		  value.put(MySQLiteHelper.COLUMN_ACTIVITY, model.getActivity());
		  value.put(MySQLiteHelper.COLUMN_X, model.getX());
		  value.put(MySQLiteHelper.COLUMN_Y, model.getY());
		  value.put(MySQLiteHelper.COLUMN_Z, model.getZ());
		  value.put(MySQLiteHelper.COLUMN_SUBMITTED, model.isSubmitted());
		  return value;
	  }

	  public void deleteProcessedData() {
	    database.delete(MySQLiteHelper.TABLE_ACCELEROMETER, MySQLiteHelper.COLUMN_SUBMITTED
	        + " = " + "1", null);
	  }

	  public List<AccelDataModel> getUnsubmittedComments() {
	    List<AccelDataModel> accels = new ArrayList<AccelDataModel>();
	    
	    Cursor cursor = database.query(MySQLiteHelper.TABLE_ACCELEROMETER,
	        allColumns, MySQLiteHelper.COLUMN_SUBMITTED + " = " + "0", null, null, null, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      AccelDataModel accel = cursorToComment(cursor);
	      accels.add(accel);
	      cursor.moveToNext();
	    }
	    // make sure to close the cursor
	    cursor.close();
	    return accels;
	  }

	  private AccelDataModel cursorToComment(Cursor cursor) {
	    AccelDataModel accel = new AccelDataModel(
	    		cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getInt(4), cursor.getInt(5));
	    return accel;
	  }
	  
	  public void batchInsert(List<AccelDataModel> accelData) {
		  database.beginTransaction();
		  try{
			  for (AccelDataModel data: accelData){
				  ContentValues contentValues = createContentValueFromAccelData(data);
				  
				  database.insert(MySQLiteHelper.TABLE_ACCELEROMETER,null,contentValues);
			  }
			  // Transaction is successful and all the records have been inserted
			  database.setTransactionSuccessful();
		  }catch(Exception e){
			  Log.e("Error in transaction",e.toString());
		  }finally{
			  //End the transaction
			  database.endTransaction();
		  }
	  }
}
