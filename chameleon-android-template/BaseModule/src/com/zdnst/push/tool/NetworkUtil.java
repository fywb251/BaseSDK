/**
 * 
 */
package com.zdnst.push.tool;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {

	public  static Boolean isNetworkConnected(Context context) {  
		ConnectivityManager manager = (ConnectivityManager) context    
		              .getApplicationContext().getSystemService(    
		                     Context.CONNECTIVITY_SERVICE);    
		          
		       if (manager == null) {    
		           return false;    
		       }    
		          
		       NetworkInfo networkinfo = manager.getActiveNetworkInfo();    
		          
		       if (networkinfo == null || !networkinfo.isAvailable()) {    
		           return false;    
		       }    
		     
		       return true;    
		    } 

}
