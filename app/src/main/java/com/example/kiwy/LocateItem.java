package com.example.kiwy;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Timer;
import java.util.TimerTask;

public class LocateItem extends AppCompatActivity {

    private static String deviceNameHeader = "Searching for ";
    private TextView tDeviceNameBox;
    private TextView tDistance;
    private Button btFound;
    private String deviceRssi = "0";
    private BluetoothAdapter adapter;
    private KiwyBroadcastReceiver receiver;
    private BluetoothDevice device;
    private String btAddress;
    private int count;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //initialize variables
        setContentView(R.layout.activity_locate_item);
        final Intent intent = getIntent();
        tDeviceNameBox = (TextView) findViewById(R.id.tDeviceNameBox);
        tDistance = (TextView) findViewById(R.id.tDistance);
        tDeviceNameBox.setText(deviceNameHeader + intent.getStringExtra("btDeviceName"));
        btFound = (Button) findViewById(R.id.btFound);


        adapter = BluetoothAdapter.getDefaultAdapter();
        receiver = new KiwyBroadcastReceiver(device);
        btAddress = intent.getStringExtra("btDeviceAddress");

        btFound.setOnClickListener(new View.OnClickListener() { //go back to main menu
            @Override
            public void onClick(View view) {
                returnToMainMenu();
            }
        });

        Timer t = new Timer();

        t.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                //TODO convert rssi to distance
                System.out.println("DEBUG: run");

                if (adapter.isDiscovering()) {
                    adapter.cancelDiscovery();
                }
                adapter.startDiscovery();

                deviceRssi = MainActivity.getCapturedRSSI(btAddress);

                //HEY SETH I HOPE YOU ENJOY FINDING WHY ITS DINGING EVERY THREE SECONDS ;P
                pushNotification();

                IntentFilter discoverIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(receiver, discoverIntent);

                runOnUiThread(new Runnable() {
                    public void run() {
                        tDistance.setText(deviceRssi + " dBm");
                    }
                });

            }
        }, 0, 3000);

    }

    private void updateDistance() {

        //TODO convert rssi to distance

        if (adapter.isDiscovering()) {
            adapter.cancelDiscovery();
        }
        adapter.startDiscovery();

        deviceRssi = MainActivity.getCapturedRSSI(btAddress);

        IntentFilter discoverIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, discoverIntent);

        tDistance.setText(deviceRssi + " dBm");

    }

    // accepts distance in terms of feet
    // if unit is not type feet it will convert it the the appropriate type
    public void setDistanceReadout(double distance, String Units) {

        String UnitAbbreviation;
        double calculatedDistance = distance;

        if (Units == "feet") {
            UnitAbbreviation = " ft.";
        } else if (Units == "meters") {
            //convert to meters with only 1 decimal place
            calculatedDistance = BigDecimal.valueOf(distance / 3.28)
                    .setScale(1, RoundingMode.HALF_UP)
                    .doubleValue();
            UnitAbbreviation = " M";
        } else {
            UnitAbbreviation = " unknown units";
        }

        tDistance.setText(String.valueOf(calculatedDistance) + UnitAbbreviation);

    }


    public void returnToMainMenu() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void pushNotification() {

        //Test push Notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("BtDisconnected", "BtDisconnected", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

            String message = "Device is now out of range";
            NotificationCompat.Builder builder = new NotificationCompat.Builder(LocateItem.this, "BtDisconnected");
            builder.setContentTitle("Warning");
            builder.setSmallIcon(R.drawable.ic_btdisconnected);
            builder.setContentText(message);
            builder.setAutoCancel(true);

            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(LocateItem.this);
            managerCompat.notify(1, builder.build());
        }

    }

}