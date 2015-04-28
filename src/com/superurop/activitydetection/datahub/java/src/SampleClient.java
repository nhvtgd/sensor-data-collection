package com.superurop.activitydetection.datahub.java.src;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.superurop.activitydetection.datahub.java.DataHubClient;
import com.superurop.activitydetection.datahub.java.ResultSet;
import com.superurop.activitydetection.datahub.java.Tuple;

/** 
 * Sample Java Client for DataHub
 * 
 * @author anantb
 * @date 11/07/2013
 * 
 */

public class SampleClient {
  public static void main(String [] args) {
    try {
      DataHubClient client = new DataHubClient("tranprod");
      
      // execute a query
      ResultSet res = client.getClient().execute_sql(client.getConnection(), "select count(*) from motion_activity_view;", null);
      
      
      // print field names
      FileWriter writer = new FileWriter("/Users/trannguyen/sensorData.csv");
      for (int i = 0; i < res.getField_names().size(); i++){
    	  writer.append(res.getField_names().get(i));
    	  if (i == res.getField_names().size() - 1) {
    		  writer.append("\n");
    	  } else {
    		  writer.append(",");
    	  }
        
      }

      System.out.println();

      // print tuples
      for (Tuple t : res.getTuples()) {
        for (int i = 0; i < t.getCells().size(); i++) {
          System.out.println(new String(t.getCells().get(i).array()));
          writer.append(new String(t.getCells().get(i).array()));
          if (i == t.getCells().size()-1) {
        	  writer.append("\n");
          } else {
        	  writer.append(",");
          }
        }
        System.out.println();
      }
      writer.flush();
      writer.close();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
}
