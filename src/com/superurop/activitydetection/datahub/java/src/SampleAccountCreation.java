package com.superurop.activitydetection.datahub.java.src;

import java.nio.ByteBuffer;
import java.util.Map;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import com.superurop.activitydetection.datahub.java.AccountCreationClient;
import com.superurop.activitydetection.datahub.java.AccountException;
import com.superurop.activitydetection.datahub.java.AccountService;
import com.superurop.activitydetection.datahub.java.AccountService.Client;
import com.superurop.activitydetection.datahub.java.Connection;
import com.superurop.activitydetection.datahub.java.ConnectionParams;
import com.superurop.activitydetection.datahub.java.DataHub;
import com.superurop.activitydetection.datahub.java.ResultSet;
import com.superurop.activitydetection.datahub.java.Tuple;

public class SampleAccountCreation {
	public static void main(String[] args) throws AccountException, TException {
		TTransport datahub_transport = new THttpClient("http://datahub.csail.mit.edu/service");
		TProtocol datahub_protocol = new  TBinaryProtocol(datahub_transport);
		DataHub.Client datahub_client = new DataHub.Client(datahub_protocol);
		
		AccountCreationClient acc_client = new AccountCreationClient(); 
		Map<String, String> acc_creation_result = acc_client.createAccoun("trannguyen_abc", "test", "whathurtsthemost1989@gmail.com");
		
		ConnectionParams params = new ConnectionParams();
		params.setApp_id("activity_collection_pebble");
		params.setApp_token("b895b860-a4db-4e15-ba1b-263d5db0bb27");
		params.setRepo_base("trannguyen_test");
		Connection conn = datahub_client.open_connection(params);
		ResultSet result = datahub_client.list_repos(conn);
		
		// print field names
		for (String field_name : result.getField_names()) {
			System.out.print(field_name + "\t");
	    }	

		   // print tuples
	    for (Tuple t : result.getTuples()) {
	    	for (ByteBuffer cell : t.getCells()) {
	          System.out.print(new String(cell.array()) + "\t");
	    	}
	        System.out.println();
	    }	  
		
	}
}
