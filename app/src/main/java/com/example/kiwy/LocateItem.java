package com.example.kiwy;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.IntentFilter;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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
    //options are "ft" and "m" for feet and meters respectively
    private final String units = "ft";


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

        btFound.setOnClickListener(new View.OnClickListener(){ //go back to main menu
            @Override
            public void onClick(View view){
                returnToMainMenu();
            }
        });

        Timer t = new Timer();

        t.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                if (adapter.isDiscovering()) {
                    adapter.cancelDiscovery();
                }
                adapter.startDiscovery();

                deviceRssi =  MainActivity.getCapturedRSSI(btAddress);

                double distance = convertRSSIToDist(Integer.parseInt(deviceRssi));

                //out of range
                if(distance == -1){
                    pushNotification();
                }


                if (units.equals("ft")) {
                    distance = distance*3.28;
                    //1 decimal place
                    distance = Math.round(distance * 10)*1.0/10;
                } else {
                    //2 decimal places
                    distance = Math.round(distance * 100)*1.0/100;
                }



                // Fix out of range range...



                IntentFilter discoverIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(receiver, discoverIntent);

                //Convert to final b/c Java enforces thread safety here.
                final double finalDistance = distance;

                runOnUiThread(new Runnable() {
                    public void run() {
                        tDistance.setText("" + finalDistance + " " + units);
                    }
                });
            }
        }, 0, 3000);

    }

    /*
        Converts a given RSSI value to a distance of the given unit.
        Code adapted from https://gist.github.com/eklimcz/446b56c0cb9cfe61d575
        For a more formal math equation and explanation read
        https://iotandelectronics.wordpress.com/2016/10/07/how-to-calculate-distance-from-the-rssi-value-of-the-ble-beacon/
        rssi: the rssi value of the device
        returns: double value representing the disance in meters. -1 if out of range
     */
    double convertRSSIToDist(int rssi){

        //There may be a way to get this from Gatt, but for now it's
        //hardcoded as a typical bluetooth power level (-59 to -65 is the typical value)
        final int transmitterPowerLevel = -59;

        //Device out of range
        if(rssi == 0 || rssi == Short.MIN_VALUE){
            return -1;
        }

        double ratio = rssi * 1.0/transmitterPowerLevel;

        if(ratio < 1) {
            return Math.pow(ratio,10);
        }

        return (0.89976)*Math.pow(ratio,7.7095) + 0.111;

    }

    public void returnToMainMenu(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void pushNotification() {

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