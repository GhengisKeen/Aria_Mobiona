package com.deus_tech.ariasdk.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

/**
 * Created by user on 5/2/2016.
 */
public class LeScanCallback implements BluetoothAdapter.LeScanCallback {
    private BluetoothBroadcastListener listener;
    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if(listener!=null){
                listener.onDeviceFound(device);
            }
    }
    public void setListner(BluetoothBroadcastListener listener){
        this.listener=listener;
    }
}
