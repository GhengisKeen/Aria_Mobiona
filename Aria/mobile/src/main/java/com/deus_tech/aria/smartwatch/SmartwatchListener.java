package com.deus_tech.aria.smartwatch;


import com.google.android.gms.wearable.DataMap;

public interface SmartwatchListener{


    void onApiConnected();

    void onApiDisconnected();

    void onSharedDataRead(String _path, DataMap _dataMap);

    void onSharedDataNotFound(String _path);

    void onSharedDataChanged(String _path, DataMap _dataMap);


}//SmartwatchListener
