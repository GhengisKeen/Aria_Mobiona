package com.deus_tech.ariasdk;

// Interface For Indicating Aria Connection Status for Updating UI
public interface AriaConnectionListener{


    void onDiscoveryStarted();

    void onDiscoveryFinished(boolean _found);

    void onConnected();

    void onReady();

    void onDisconnected();


}//AriaConnectionListener
