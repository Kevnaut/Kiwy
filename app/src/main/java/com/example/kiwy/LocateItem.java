package com.example.kiwy;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class LocateItem extends AppCompatActivity {

    private static String deviceNameHeader = "Searching for ";
    private TextView tDeviceNameBox;
    private TextView tDistance;
    private Button btFound;
    private BluetoothDevice device;
    private int deviceRssi;
    private BluetoothGatt gatt;

    ;
    private BluetoothGattCallback callback;
    private int status;
    //    private DeviceListAdapter mDeviceListAdapter;
 //   private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
  //  private KiwyBroadcastReceiver receiver;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //initialize variables
        setContentView(R.layout.activity_locate_item);
        final Intent intent = getIntent();
        device = intent.getParcelableExtra("btDevice");
        tDeviceNameBox = (TextView) findViewById(R.id.tDeviceNameBox);
        tDeviceNameBox.setText(deviceNameHeader + device.getName());
        tDistance = (TextView) findViewById(R.id.tDistance);
        btFound = (Button) findViewById(R.id.btFound);

        callback = new BluetoothGattCallback() {

            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);


                if(status == 0) {
                    gatt.connect();
                    //gatt.disconnect();
                }
                //gatt.discoverServices();
            }

            @Override
            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                super.onReadRemoteRssi(gatt, rssi, status);
                Toast.makeText(getApplicationContext(),"  RSSI attempt 0: " + rssi + "dBm", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
                // Try to send some data to the device
                //characteristic.setValue("test");
                //gatt.writeCharacteristic(characteristic);
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
            }

            @Override
            public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorRead(gatt, descriptor, status);
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorWrite(gatt, descriptor, status);
            }

            @Override
            public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
                super.onReliableWriteCompleted(gatt, status);
            }

            @Override
            public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
                super.onMtuChanged(gatt, mtu, status);
            }

            @Override
            protected Object clone() throws CloneNotSupportedException {
                return super.clone();
            }

            @Override
            public boolean equals(Object o) {
                return super.equals(o);
            }

            @Override
            protected void finalize() throws Throwable {
                super.finalize();
            }

            @Override
            public int hashCode() {
                return super.hashCode();
            }

            @Override
            public String toString() {
                return super.toString();
            }
        };

          connectDevice(device, callback);
        Toast.makeText(getApplicationContext()," Connected to Device ", Toast.LENGTH_SHORT).show();

 //       Toast.makeText(getApplicationContext(),"  RSSI attempt 2: " + deviceRssi + "dBm", Toast.LENGTH_SHORT).show();

       // deviceRssi = getNewRssi(intent);

  //      Toast.makeText(getApplicationContext(),"  RSSI attempt 3: " + deviceRssi + "dBm", Toast.LENGTH_SHORT).show();

        Toast.makeText(getApplicationContext(),"  RSSI attempt 1: " + deviceRssi + "dBm", Toast.LENGTH_SHORT).show();

        //gatt.connect();
       // gatt.discoverServices();
        //Toast.makeText(getApplicationContext(),"  RSSI attempt 2: " + deviceRssi + "dBm", Toast.LENGTH_SHORT).show();

        //gatt.readRemoteRssi();
        //Toast.makeText(getApplicationContext(),"  RSSI attempt 4: " + deviceRssi + "dBm", Toast.LENGTH_SHORT).show();

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

    private void connectDevice(BluetoothDevice givenDevice, BluetoothGattCallback givenCallback){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            givenDevice.connectGatt(getApplicationContext(),true, givenCallback);
        } else {
            Log.e("LocateItem", "Can't connect to device. Android version too old.");
        }

    }

    private int getNewRssi(Intent intent){
        return intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
    }

    public void returnToMainMenu(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}