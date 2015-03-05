package com.superurop.activitydetection.datahub.java;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;

public class DataHubClient {
	DataHub.Client client;
	Connection con;
	final static String SQL_INSERT_FORMAT = "INSERT INTO accelerometer (data) VALUES ('%s')";
	
	public DataHubClient() throws DBException, TException {
		TTransport transport = new THttpClient("http://datahub.csail.mit.edu/service");
		TProtocol protocol = new  TBinaryProtocol(transport);
		client = new DataHub.Client(protocol);
		
		// open connection
		ConnectionParams con_params = new ConnectionParams();
		con_params.setUser("trannguyen");
		con_params.setPassword("trannguyen");
		con = client.open_connection(con_params);
		
	}
	
	public void pushData(String deviceID, String timeStamp, String activity, int x, int y, int z) throws DBException, TException {
		String sqlStatement = String.format(SQL_INSERT_FORMAT, deviceID, timeStamp, activity, x, y,z);
		
		// 	execute a query
		ResultSet res = client.execute_sql(con, sqlStatement, null);
	}
	
	public void pushData(String blob) throws DBException, TException {
		String sqlStatement = String.format(SQL_INSERT_FORMAT, blob);
		
		// 	execute a query
		ResultSet res = client.execute_sql(con, sqlStatement, null);
	}
	
	public Connection getConnection(){
		return con;
	}
	
	public DataHub.Client getClient() {
		return client;
	}
	
	public static void main(String[] args) throws DBException, TException {
		DataHubClient client = new DataHubClient();
		client.pushData("tranDevice", String.valueOf(System.currentTimeMillis()), "Running", 10, 20, 100);
	}
}
