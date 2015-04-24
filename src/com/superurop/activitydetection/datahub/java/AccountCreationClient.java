package com.superurop.activitydetection.datahub.java;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import com.superurop.activitydetection.datahub.java.AccountService.Client;

public class AccountCreationClient {
	Client acctSvc;
	String EMAIL_REGEX = "[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})";
	String USER_NAME_REGEX = ".+username=([_A-Za-z0-9-]+)";
	Pattern email_pattern = Pattern.compile(EMAIL_REGEX);
	Pattern username_pattern = Pattern.compile(USER_NAME_REGEX);
	
	public AccountCreationClient() throws TTransportException {
		TTransport account_transport = new THttpClient("http://datahub.csail.mit.edu/service/account");
		TProtocol account_protocol = new  TBinaryProtocol(account_transport);
		acctSvc = new AccountService.Client(account_protocol);
	}
	
	public Map<String, String> createAccoun(String username, String password, String email) {
		Map<String, String> result = new HashMap<String, String>();
		try {
			boolean success = acctSvc.create_account(username.toLowerCase(), email.toLowerCase(), password.toLowerCase(),
						"test", "activity_collection_pebble", "b895b860-a4db-4e15-ba1b-263d5db0bb27");
		} catch (AccountException e) {
			// TODO Auto-generated catch block
			if (e.getMessage().contains("Duplicate username")) {
				Matcher m = email_pattern.matcher(e.getMessage());
				if (m.find()) {
					result.put("email", m.group(0));
				}
			} else if (e.getMessage().contains("Duplicate email")) {
				Matcher m = username_pattern.matcher(e.getMessage());
				if (m.find()) {
					result.put("username", m.group(1));
				}
			} else {
				result.put("Unknown", e.getMessage());
			}
			e.printStackTrace();
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
