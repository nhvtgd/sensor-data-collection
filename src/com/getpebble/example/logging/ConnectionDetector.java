package com.getpebble.example.logging;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
 
/**
 * This class is called before any activity that requires internet connection
 * such as a search or buy or sell items
 * @author trannguyen
 *
 */
public class ConnectionDetector {
 
	/**
	 * The Context that this class is run on
	 */
    private Context _context;
 
    /**
     * This constructor take the context that this class is run upon
     * to get information about the network
     * @param context the context that is used
     */
    public ConnectionDetector(Context context){
        this._context = context;
    }
 
    /**
     * Checking for all possible internet providers
     * **/
    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
          if (connectivity != null)
          {
              NetworkInfo[] info = connectivity.getAllNetworkInfo();
              if (info != null)
                  for (int i = 0; i < info.length; i++)
                      if (info[i].getState() == NetworkInfo.State.CONNECTED)
                      {
                          return true;
                      }
 
          }
          return false;
    }
}
