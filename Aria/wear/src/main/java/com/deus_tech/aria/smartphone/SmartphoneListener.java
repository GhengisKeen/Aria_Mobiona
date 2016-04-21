package com.deus_tech.aria.smartphone;


import com.google.android.gms.wearable.DataMap;

public interface SmartphoneListener{


    void onApiConnected();

    void onApiDisconnected();

    void onSharedDataRead(String _path, DataMap _dataMap);

    void onSharedDataNotFound(String _path);

    void onSharedDataChanged(String _path, DataMap _dataMap);

    void onGestureReceived(String _gesture);


}//SmartphoneListener
