package com.example.kiwy;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Intent;
import android.content.IntentFilter;

import androidx.appcompat.app.AppCompatActivity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class LocateItem extends AppCompatActivity {

    private static String deviceNameHeader = "Searching for ";
    private TextView tDeviceNameBox;
    private TextView tDistance;
    private Button btFound;
    private ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    private DeviceListAdapter mDeviceListAdapter;
    private int deviceRssi;

    //BEGIN Super dumb hacky way to repeatedly get RSSI
    private final BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            //Toast.makeText(getApplicationContext()," onReceive: ACTION FOUND ", Toast.LENGTH_SHORT).show();
            if(action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                mBTDevices.add(device);
                System.out.println( "TEST1: onRecive: " + device.getName() + " : " + device.getAddress());
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);

                // if the found device is the same as the one found
                if (device.getAddress().toString() == intent.getStringExtra("btDeviceAddress")){

                    deviceRssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                    //Toast.makeText(getApplicationContext(),"  RSSI: " + deviceRssi + "dBm", Toast.LENGTH_SHORT).show();

                }

                deviceRssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                //Toast.makeText(getApplicationContext(),"  RSSI: " + deviceRssi + "dBm", Toast.LENGTH_SHORT).show();

            }
        }
    };
    //END Super dumb hacky way to repeatedly get RSSI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate_item);
        Intent intent = getIntent();

        tDeviceNameBox = (TextView) findViewById(R.id.tDeviceNameBox);
        tDistance = (TextView) findViewById(R.id.tDistance);
        btFound = (Button) findViewById(R.id.btFound);
        //btDeviceName = (TextBox)

        tDeviceNameBox.setText(deviceNameHeader + intent.getStringExtra("btDeviceName"));
        setDistanceReadout(5, "feet");
        btFound.setOnClickListener(new View.OnClickListener(){ //go back to main menu
            @Override
             public void onClick(View view){
                returnToMainMenu();
            }
        });
    }

    // accepts distance in terms of feet
    // if unit is not type feet it will convert it the the appropriate type
    public void setDistanceReadout(double distance, String Units){

        String UnitAbbreviation;
        double calculatedDistance = distance;

        if(Units == "feet"){
            UnitAbbreviation = " ft.";
        } else if (Units == "meters" ) {
            //convert to meters with only 1 decimal place
            calculatedDistance = BigDecimal.valueOf(distance/3.28)
                    .setScale(1, RoundingMode.HALF_UP)
                    .doubleValue();
            UnitAbbreviation = " M";
        } else {
            UnitAbbreviation = " unknown units";
        }

        tDistance.setText(String.valueOf(calculatedDistance) + UnitAbbreviation);

    }

    //TODO
    //returns the direction in degrees
    /*
    private int findDirection(){
        int ret;

        return ret;
    }
    */

    public void returnToMainMenu(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}