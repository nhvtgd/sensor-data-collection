package com.superurop.activitydetection.datahub.java.src;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;

import com.superurop.activitydetection.datahub.java.Connection;
import com.superurop.activitydetection.datahub.java.ConnectionParams;
import com.superurop.activitydetection.datahub.java.DataHub;
import com.superurop.activitydetection.datahub.java.DataHubClient;
import com.superurop.activitydetection.datahub.java.ResultSet;
import com.superurop.activitydetection.datahub.java.Tuple;

import java.nio.ByteBuffer;

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
      DataHubClient client = new DataHubClient();
      client.pushData("tranDevice", String.valueOf(System.currentTimeMillis()), "Running", 10, 20, 100);
      
      // execute a query
      ResultSet res = client.getClient().execute_sql(client.getConnection(), "select * from accelerometer;", null);
      
      // print field names
      for (String field_name : res.getField_names()) {
        System.out.print(field_name + "\t");
      }

      System.out.println();

      // print tuples
      for (Tuple t : res.getTuples()) {
        for (ByteBuffer cell : t.getCells()) {
          System.out.print(new String(cell.array()) + "\t");
        }
        System.out.println();
      }	  
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
}
