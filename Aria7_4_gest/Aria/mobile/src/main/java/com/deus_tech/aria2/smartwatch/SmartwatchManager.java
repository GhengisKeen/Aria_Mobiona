package com.deus_tech.aria2.smartwatch;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;


public class SmartwatchManager implements DataApi.DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<DataItemBuffer>, Runnable{


    //paths
    public final static String ROUTER_PATH = "/router";
    public final static String ROUTER_VIEW = "/view";
    public final static int ROUTER_VIEW_INTRO = 1;
    public final static int ROUTER_VIEW_DASHBOARD = 2;
    public final static int ROUTER_VIEW_GOPRO = 3;
    public final static int ROUTER_VIEW_MUSIC = 4;
    public final static int ROUTER_VIEW_START_CALIBRATION = 5;
    public final static int ROUTER_VIEW_CALIBRATION = 6;
    public final static int ROUTER_VIEW_END_CALIBRATION = 7;
    public final static int ROUTER_VIEW_TEST = 8;
    public final static String DASHBOARD_PATH = "/dashboard";
    public final static String DASHBOARD_MENU_ITEM = "menuItem";

    public final static String GOPRO_PATH = "/gopro";
    public final static String GOPRO_STATUS = "status";
    public final static int GOPRO_STATUS_SEARCHING = 1;
    public final static int GOPRO_STATUS_NOT_FOUND = 2;
    public final static int GOPRO_STATUS_CONNECTED = 3;
    public final static int GOPRO_STATUS_READY = 4;

    //path-aria2 messages
    public final static String ARIA_MESSAGE_PATH = "/aria2";
    public final static String ARIA_MESSAGE_HOME = "home";
    public final static String ARIA_MESSAGE_ENTER = "enter";
    public final static String ARIA_MESSAGE_BACK = "back";
    public final static String ARIA_MESSAGE_UP = "up";
    public final static String ARIA_MESSAGE_DOWN = "down";


    private Context context;
    private GoogleApiClient apiClient;
    private ArrayList<SmartwatchListener> listeners;
    private String pathToRead;
    private String messageToSend;
    private boolean isConnected;


    public SmartwatchManager(Context _context){

        context = _context;

        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(context);
        builder.addApi(Wearable.API);
        builder.addConnectionCallbacks(this);
        builder.addOnConnectionFailedListener(this);

        apiClient = builder.build();

        listeners = new ArrayList<SmartwatchListener>();

    }//SmartwatchManager


    public void addListener(SmartwatchListener _listener){

        listeners.add(_listener);

    }//addListener


    public void removeListener(SmartwatchListener _listener){

        listeners.remove(_listener);

    }//removeListener


    public boolean isConnected(){

        return isConnected;

    }//isConnected


    //actions

    public void connect(){

        apiClient.connect();

    }//connect


    public void disconnect(){

        Wearable.DataApi.removeListener(apiClient, this);
        apiClient.disconnect();
        apiClient.unregisterConnectionCallbacks(this);
        apiClient.unregisterConnectionFailedListener(this);

    }//disconnect


    public void writeSharedData(final String _path, final DataMap _map){

        //delete the old one
        Uri uri = new Uri.Builder().scheme(PutDataRequest.WEAR_URI_SCHEME).path(_path).build();
        Wearable.DataApi.deleteDataItems(apiClient, uri).setResultCallback(new ResultCallback<DataApi.DeleteDataItemsResult>(){

            public void onResult(DataApi.DeleteDataItemsResult deleteDataItemsResult){

                //create the new one
                PutDataMapRequest putRequest = PutDataMapRequest.create(_path);
                DataMap map = putRequest.getDataMap();
                map.putAll(_map);
                Wearable.DataApi.putDataItem(apiClient, putRequest.asPutDataRequest());

            }
        });

    }//writeSharedData


    public void readSharedData(String _path){

        pathToRead = _path;

        PendingResult<DataItemBuffer> results = Wearable.DataApi.getDataItems(apiClient);
        results.setResultCallback(this);

    }//readSharedData


    public void sendMessage(String _message){

        messageToSend = _message;
        startThread();

    }//sendMessage


    //events

    public void onConnected(Bundle bundle){

        Wearable.DataApi.addListener(apiClient, this);

        isConnected = true;

        for(int i=0 ; i<listeners.size() ; i++){
            listeners.get(i).onApiConnected();
        }

    }//onConnected


    public void onConnectionSuspended(int _info){

        isConnected = false;

        for(int i=0 ; i<listeners.size() ; i++){
            listeners.get(i).onApiDisconnected();
        }

    }//onConnectionSuspended


    public void onConnectionFailed(ConnectionResult connectionResult){

        isConnected = false;

        for(int i=0 ; i<listeners.size() ; i++){
            listeners.get(i).onApiDisconnected();
        }

    }//onConnectionFailed


    public void onDataChanged(DataEventBuffer _dataEvents){

        for(int y=0 ; y<_dataEvents.getCount() ; y++){

            DataEvent dataEvent = _dataEvents.get(y);
            if(dataEvent.getType() == DataEvent.TYPE_DELETED) continue;

            DataItem data = dataEvent.getDataItem();

            String path = data.getUri().getPath();
            DataMapItem dataMapItem = DataMapItem.fromDataItem(data);
            DataMap dataMap = dataMapItem.getDataMap();

            for(int i=0 ; i<listeners.size() ; i++){
                listeners.get(i).onSharedDataChanged(path, dataMap);
            }

        }

    }//onDataChanged


    public void onResult(DataItemBuffer dataItems){

        boolean found = false;

        for(int y=0 ; y<dataItems.getCount() ; y++){

            DataItem data = dataItems.get(y);
            String path = data.getUri().getPath();

            if(path.equals(pathToRead)){

                DataMapItem dataMapItem = DataMapItem.fromDataItem(data);
                DataMap dataMap = dataMapItem.getDataMap();

                for(int i=0 ; i<listeners.size() ; i++){
                    found = true;
                    listeners.get(i).onSharedDataRead(pathToRead, dataMap);
                }

            }

        }

        if(found == false){
            for(int i = 0; i < listeners.size(); i++){
                listeners.get(i).onSharedDataNotFound(pathToRead);
            }
        }

        dataItems.release();

    }//onResult - callback for Wearable.DataApi.getDataItems


    //thread

    public void run(){

        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(apiClient).await();

        if(messageToSend != null){

            for(Node node : nodes.getNodes()){

                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(apiClient, node.getId(), SmartwatchManager.ARIA_MESSAGE_PATH, messageToSend.getBytes() ).await();

            }

        }

    }//run


    private void startThread(){

        Thread thread = new Thread(this);
        thread.start();

    }//startThread


}//SmartwatchManager