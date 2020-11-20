package com.example.kiwy;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private int rssi;

    BluetoothAdapter mBluetoothAdapter;

    // BTName, BTAddr
    HashMap<String, String> capturedDevices = new HashMap<String, String>();
    // BTAddr, BTRSSI
    static HashMap<String, String> capturedRSSI = new HashMap<String, String>();

    //Button btnEnableDisable_Discoverable;
    Button btnONOFF;
    Button btnDiscoverDevices;
    //Button btnLocateItem;
    Button btnHelp;

    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public DeviceListAdapter mDeviceListAdapter;
    ListView lvNewDevices;

    // Testing Push Notificication
    Button btnTestPush;

    // Create a BroadcastReceiver for ACTION_STATE_CHANGED
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        break;
                    case BluetoothAdapter.STATE_ON:
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        break;
                }
            }
        }
    };

    // Create a BroadcastReceiver for ACTION_SCAN_MODE_CHANGED
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        break;
                }
            }
        }
    };

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            capturedDevices = new HashMap<String, String>();
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                mBTDevices.add(device);
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);

                capturedDevices.put(device.getName(), device.getAddress());
                rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                String s = "" + rssi;
                capturedRSSI.put(device.getAddress(), s);
                lvNewDevices.setAdapter(mDeviceListAdapter);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver1);
        unregisterReceiver(mBroadcastReceiver2);
        unregisterReceiver(mBroadcastReceiver3);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing our Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Initializing the buttons
        btnONOFF = (Button) findViewById(R.id.btnONOFF);
        //btnEnableDisable_Discoverable = (Button) findViewById(R.id.btnEnableDisable_Discoverable);
        btnDiscoverDevices = (Button) findViewById(R.id.btnDiscoverDevices);
        lvNewDevices = (ListView) findViewById(R.id.lvNewDevices);
        mBTDevices = new ArrayList<>();
        btnHelp = (Button) findViewById(R.id.btnHelp);




        if (mBluetoothAdapter.isEnabled() == true) {
            btnONOFF.setText("Turn on Bluetooth");
        } else {
            btnONOFF.setText("Turn off Bluetooth");
        }


        // All onClick actions

        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHelp();
            }
        });

        lvNewDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Make a new activity and send the device that was selected
                BluetoothDevice device = mBTDevices.get(i);

                openAddEntry(device);

            }
        });
        btnONOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mBluetoothAdapter.isEnabled() == true) {
                    btnONOFF.setText("Turn off Bluetooth");
                } else {
                    btnONOFF.setText("Turn on Bluetooth");
                }
                enableDisableBT();

            }
        });

        // For future use if we want to find other people with their phones
        /*
        btnEnableDisable_Discoverable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                //discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                //startActivity(discoverableIntent);

                //IntentFilter intentFilter = new IntentFilter(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
                //registerReceiver(mBroadcastReceiver2, intentFilter);
            }
        });
        */

        btnDiscoverDevices.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                lvNewDevices.setAdapter(null);
                mBTDevices.clear();

                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();

                    checkBTPermissions();

                    mBluetoothAdapter.startDiscovery();
                    IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);

                }
                if (!mBluetoothAdapter.isDiscovering()) {

                    checkBTPermissions();

                    mBluetoothAdapter.startDiscovery();
                    IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
                }
            }
        });


    }

    public void enableDisableBT() {
        if (mBluetoothAdapter == null) {
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkBTPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");

            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        } else {
        }
    }

    public void openAddEntry(BluetoothDevice device) {
        Intent intent = new Intent(this, AddEntry.class);

        // Send bt name, address, current rssi

        intent.putExtra("btDevice", device);
        intent.putExtra("btName", device.getName());
        intent.putExtra("btAddress", device.getAddress());
        String currentRssi = capturedRSSI.get(device.getAddress());
        intent.putExtra("btRSSI", currentRssi);
        startActivity(intent);
    }

    public void openHelp() {
        Intent intent = new Intent(this, Help.class);
        startActivity(intent);
    }
/*
    public void openLocateItem() {
        Intent intent = new Intent(this, LocateItem.class);
        startActivity(intent);
    }
*/
    public static String getCapturedRSSI(String address) {

        return capturedRSSI.get(address);
    }

    public void pushNotification() {

        //Test push Notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("BtDisconnected", "BtDisconnected", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

            String message = "Device is now out of range";
            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "BtDisconnected");
            builder.setContentTitle("Warning");
            builder.setSmallIcon(R.drawable.ic_btdisconnected);
            builder.setContentText(message);
            builder.setAutoCancel(true);

            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
            managerCompat.notify(1, builder.build());
        }

    }
}


