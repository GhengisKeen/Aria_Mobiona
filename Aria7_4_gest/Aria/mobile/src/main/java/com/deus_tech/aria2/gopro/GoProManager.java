package com.deus_tech.aria2.gopro;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.deus_tech.aria2.R;
import com.deus_tech.aria2.util.Util;

import java.util.ArrayList;
import java.util.List;

public class GoProManager extends BroadcastReceiver implements GoProRequestListener{

    //actions
    public  static int ACTION_NONE = 0;
    public  static int ACTION_TURN_ON = 1;
    public  static int ACTION_TURN_OFF = 2;
    public  static int ACTION_PHOTO_MODE = 3;
    public  static int ACTION_VIDEO_MODE = 4;
    public  static int ACTION_START_CAPTURE = 5;
    public  static int ACTION_END_CAPTURE = 6;
    public  static int ACTION_CHANGE_MODE = 7;
    //status scan
    private  static int SCAN_STATUS_TODO = 1;
    private  static int SCAN_STATUS_DONE = 2;
    //status connection
    private  static int CONNECTION_STATUS_NOT_CONNECTED = 1;
    private  static int CONNECTION_STATUS_CONNECTED = 2;


    private Context context;
    private WifiManager wifiManager;
    private ConnectivityManager connectivityManager;
    private GoProRequest request;
    private int currentAction;

    //connection data
    private String ssid = null;
    private String key = null;
    private int netId = 0;
    private String ip = "10.5.5.9";

    //status
    private int scanStatus;
    private int connectionStatus;

    //listener
    private ArrayList<GoProListener> listeners;



    public GoProManager(Context _context){

        context = _context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        listeners = new ArrayList<GoProListener>();

    }//constructor


    public void addListener(GoProListener _listener){

        listeners.add(_listener);

    }//addListener


    public void removeListener(GoProListener _listener){

        listeners.remove(_listener);

    }//removeListener


    public void connect(){

        ssid = Util.loadStringPreference(context, R.string.gopro_ssid);
        key = Util.loadStringPreference(context, R.string.gopro_password);

        scanStatus = GoProManager.SCAN_STATUS_DONE;
        connectionStatus = GoProManager.CONNECTION_STATUS_NOT_CONNECTED;

        //this calls automatically the onReceive method for wifi state
        initBroadcastReceiver();

        if(wifiManager.isWifiEnabled() == false){

            wifiManager.setWifiEnabled(true);

        }

    }//connect


    public void disconnect(){

        if(connectionStatus == GoProManager.CONNECTION_STATUS_CONNECTED){

            wifiManager.disconnect();

        }

        try{

            context.unregisterReceiver(this);

        }catch(IllegalArgumentException e){}

    }//disconnect


    private void onWifiEnabled(){

        if(isConnected()){

            onConnected();

        }else{

            searchNetwork();

        }

    }//onWifiEnabled


    private void onWifiNotEnabled(){

        onDisconnected();

    }//onWifiNotEnabled


    private void searchNetwork(){

        scanStatus = GoProManager.SCAN_STATUS_TODO;

        wifiManager.startScan();

    }//searchNetwork


    private void onNetworkFound(){

        scanStatus = GoProManager.SCAN_STATUS_DONE;

        updateConfiguration();
        connectToNetwork();

    }//onNetworkFound


    private void onNetworkNotFound(){

        scanStatus = GoProManager.SCAN_STATUS_DONE;

        for(int i=0 ; i<listeners.size() ; i++){
            listeners.get(i).onGoProNotFound();
        }

    }//onNetworkNotFound


    private void updateConfiguration(){

        WifiConfiguration wifiConfig = null;
        int maxPriority = 0;

        //1. search for an existing wifi configuration, and the max priority

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();

        for(int i=0 ; i<list.size() ; i++){

            WifiConfiguration tmp = list.get(i);

            if(tmp.SSID == "\""+ssid+"\""){
                wifiConfig = tmp;
            }

            if(maxPriority < tmp.priority){
                maxPriority = tmp.priority+1;
            }

        }


        if(wifiConfig == null){

            //2-a. if the network dosn't exist, create the configuration

            wifiConfig = new WifiConfiguration();
            wifiConfig.SSID = "\""+ssid+"\"";
            wifiConfig.preSharedKey = "\""+key+"\"";
            wifiConfig.priority = maxPriority;

            netId = wifiManager.addNetwork(wifiConfig);

        }else{

            //2-b. if the network exists, update the configuration

            wifiConfig.preSharedKey = "\""+key+"\"";
            wifiConfig.priority = maxPriority;

            netId = wifiManager.updateNetwork(wifiConfig);

        }

        //3. save the new configuration
        //wifiManager.saveConfiguration();

    }//updateConfiguration


    private void connectToNetwork(){

        connectionStatus = GoProManager.CONNECTION_STATUS_NOT_CONNECTED;

        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();

    }//connectToNetwork


    private void onConnected(){

        connectionStatus = GoProManager.CONNECTION_STATUS_CONNECTED;

        for(int i=0 ; i<listeners.size() ; i++){
            listeners.get(i).onGoProConnected();
        }

    }//onConnected


    private void onDisconnected(){

        connectionStatus = GoProManager.CONNECTION_STATUS_NOT_CONNECTED;

        for(int i=0 ; i<listeners.size() ; i++){
            listeners.get(i).onGoProDisconnected();
        }

    }//onDisconnected


    private boolean isConnected(){

        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if(networkInfo.isConnected()){

            WifiInfo connectionInfo = wifiManager.getConnectionInfo();

            if(connectionInfo != null && connectionInfo.getSSID().equals("\""+ssid+"\"")){

                return true;

            }

        }

        return false;

    }//isConnected


    public void initBroadcastReceiver(){

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);

        context.registerReceiver(this, filter);

    }//initBroadcastReceiver


    public void onReceive(Context _context, Intent _intent){

        String action = _intent.getAction();

        if(action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)){

            int wifiStatus = _intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);

            if(wifiStatus == WifiManager.WIFI_STATE_ENABLED){

                onWifiEnabled();

            }else{

                onWifiNotEnabled();

            }

        }else if(action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){

            if(scanStatus != GoProManager.SCAN_STATUS_TODO) return;

            List<ScanResult> wifiList = wifiManager.getScanResults();

            for(int i = 0; i < wifiList.size(); i++){

                if(wifiList.get(i).SSID.equals(ssid)){

                    onNetworkFound();
                    return;

                }

            }

            onNetworkNotFound();

        }else if(action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){

            NetworkInfo info = _intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            boolean connected = info.isConnected();

            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String currentSsid = wifiInfo.getSSID();

            if(currentSsid.equals("\""+ssid+"\"") == false) return;

            if(connected == true && connectionStatus == GoProManager.CONNECTION_STATUS_NOT_CONNECTED){

                onConnected();

            }else if(connected == false && connectionStatus == GoProManager.CONNECTION_STATUS_CONNECTED){

                onDisconnected();

            }

        }

    }//onReceive


    public void doAction(int _action){

        request = new GoProRequest();
        request.setListener(this);

        String url = "";

        if(_action == GoProManager.ACTION_TURN_ON){

            url = "http://"+ip+"/bacpac/PW?t="+key+"&p=%01";

        }else if(_action == GoProManager.ACTION_TURN_OFF){

            url = "http://"+ip+"/bacpac/PW?t="+key+"&p=%00";

        }else if(_action == GoProManager.ACTION_PHOTO_MODE){

            url = "http://"+ip+"/camera/CM?t="+key+"&p=%01";

        }else if(_action == GoProManager.ACTION_VIDEO_MODE){

            url = "http://"+ip+"/camera/CM?t="+key+"&p=%00";

        }else if(_action == GoProManager.ACTION_START_CAPTURE){

            url = "http://"+ip+"/bacpac/SH?t="+key+"&p=%01";

        }else if(_action == GoProManager.ACTION_END_CAPTURE){

            url = "http://"+ip+"/bacpac/SH?t="+key+"&p=%00";

        }else if(_action == GoProManager.ACTION_CHANGE_MODE){

            url = "http://"+ip+"/bacpac/PW?t="+key+"&p=%02";

        }

        currentAction = _action;

        for(int i=0 ; i<listeners.size() ; i++){
            listeners.get(i).onGoProActionStarted(_action);
        }

        request.execute(url);

    }//doAction


    public void onGoProRequestSucceed(GoProRequest _request){

        for(int i=0 ; i<listeners.size() ; i++){
            listeners.get(i).onGoProActionDone(currentAction);
        }

        currentAction = GoProManager.ACTION_NONE;

    }//onGoProRequestSucceed


    public void onGoProRequestError(GoProRequest _request){

        for(int i=0 ; i<listeners.size() ; i++){
            listeners.get(i).onGoProActionError(currentAction);
        }

        currentAction = GoProManager.ACTION_NONE;

    }//onGoProRequestError


    public int getStatus(){

        return connectionStatus;

    }//getStatus


}//GoProManager