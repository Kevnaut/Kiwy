package com.example.kiwy;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.HashMap;
import java.util.Map;

public class KiwyBroadcastReceiver extends BroadcastReceiver {
    private BluetoothDevice device;
    private Map<String,String> deviceList;

    public KiwyBroadcastReceiver(BluetoothDevice device) {
        this.device = device;
        deviceList = new HashMap<>();
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if(action.equals(BluetoothDevice.ACTION_FOUND)) {
            BluetoothDevice d = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE) + " dBm";
            deviceList.put(d.getAddress(),rssi);

        }
    }

    public Map<String,String> getDeviceList() {
       return deviceList;
    }
}
