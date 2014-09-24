package com.tian.weather.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

public class NetworkUtils {
	private static final String TAG = "[CityWeather]NetworkUtils";
	public static final String ACTION_NETWORK_STATE_CHANGED = "com.tian.cityweather.network_state_changed";
    private static NetworkState previousNetworkState = NetworkState.NONE;

    public enum NetworkState {
        SERVERREQUEST, FULL, WIFI, MOBILE, NONE
    }

    public static boolean isGPRSNetwork(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkType() == TelephonyManager.NETWORK_TYPE_GPRS;
    }
    
    private static NetworkState isNetworkStateChanged(Context context, NetworkState state) {
        if (!state.equals(previousNetworkState)) {
            previousNetworkState = state;
            context.sendBroadcast(new Intent(ACTION_NETWORK_STATE_CHANGED));
        }
        return state;
    }
    
    public static NetworkState getActiveNetworkState(Context context){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
    	if( activeNetwork != null ){
    		int type = activeNetwork.getType();
    		if (type == ConnectivityManager.TYPE_WIFI ) return NetworkState.WIFI;
    		else if(type == ConnectivityManager.TYPE_MOBILE ) return NetworkState.MOBILE;
    	}
    	
    	return NetworkState.NONE;
    }
    
    public static NetworkState getNetworkState(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivity == null){
        	return null;
        }
        NetworkInfo wifi = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean wifiEnabled = wifi.isAvailable() && wifi.isConnected();
        boolean mobileEnabled = mobile.isAvailable() && mobile.isConnected();
        
        NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isAvailable() && activeNetwork.isConnectedOrConnecting()) {
            int type = activeNetwork.getType();
            
            if (type == ConnectivityManager.TYPE_WIFI || type == ConnectivityManager.TYPE_MOBILE) {
                if (wifiEnabled) {
                    if (mobileEnabled) {
                        return isNetworkStateChanged(context, NetworkState.FULL);
                    } else {
                        return isNetworkStateChanged(context, NetworkState.WIFI);
                    }
                } else {
                    return isNetworkStateChanged(context, NetworkState.MOBILE);
                }
            }
            return isNetworkStateChanged(context, NetworkState.FULL);
        } else if ( wifiEnabled && mobileEnabled) {
            return isNetworkStateChanged(context, NetworkState.FULL);
        } else if (wifiEnabled) {
            return isNetworkStateChanged(context, NetworkState.WIFI);
        } else if (mobileEnabled) {
            return isNetworkStateChanged(context, NetworkState.MOBILE);
        } else {
            return isNetworkStateChanged(context, NetworkState.NONE);
        }
    }
    
    public static String getDetailedNetState(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        String info = null;
        if (connManager != null) {
            NetworkInfo activeNet = connManager.getActiveNetworkInfo();
            if (activeNet != null) {
                info = activeNet.toString();
            }
        }
        if (info == null) {
            info = "?";
        }
        return info;
    }
    
    public static boolean isAirplaneMode(Context context) {
        int mode = Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0);
        return (mode != 0);
    }
    
    public static String getNetworkInfoStateLabel(NetworkInfo.State state) {
        if (state == NetworkInfo.State.CONNECTED) {
            return "CONNECTED";
        }

        if (state == NetworkInfo.State.CONNECTING) {
            return "CONNECTING";
        }
        
        if (state == NetworkInfo.State.DISCONNECTED) {
            return "DISCONNECTED";
        }
        
        if (state == NetworkInfo.State.DISCONNECTING) {
            return "DISCONNECTING";
        }
        
        if (state == NetworkInfo.State.SUSPENDED) {
            return "SUSPENDED";
        }
        
        return "UNKNOWN";
    }
    
    
    public static String getNetworkHostIp(){
    	try{
    	for (Enumeration<NetworkInterface> en = NetworkInterface
    			.getNetworkInterfaces(); en.hasMoreElements();) { 
    		
            NetworkInterface intf = en.nextElement(); 
            for (Enumeration<InetAddress> ipAddr = intf
            		.getInetAddresses(); ipAddr.hasMoreElements();){ 

                InetAddress inetAddress = ipAddr.nextElement(); 

                if(!inetAddress.isLoopbackAddress()){
                    return inetAddress.getHostAddress(); 
                 }
             }
    		}
    	}catch (SocketException ex) { 
    		Log.e(TAG, "getNetWorkHostIP Failure in socketException");
        }catch (Exception e) { 
        	Log.e(TAG, "getNetWorkHostIP Failure in exception");
        } 
		return null;
    }
    
}
