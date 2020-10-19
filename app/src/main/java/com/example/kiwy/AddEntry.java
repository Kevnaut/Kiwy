package com.example.kiwy;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AddEntry extends AppCompatActivity {
    String btName;
    String btAddress;
    Button btAddDevice;
    Button btChangeName;
    Intent intent;

    TextView tDevice;
    TextView tAddress;
    EditText inName;
    TextView tvTest;
    TextView txSignal;
    TextView txSignalNum;

    //Testing
    public BluetoothDevice device;
    public BluetoothAdapter BTAdapter;
    private int deviceRssi;

    public static final String FILE_NAME = "SavedDevices.csv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);
        intent = getIntent();
        tDevice = (TextView) findViewById(R.id.tDevice);
        tAddress = (TextView) findViewById(R.id.tAddress);
        btAddDevice = (Button) findViewById(R.id.btAddDevice);
        btChangeName = (Button) findViewById(R.id.btChangeName);
        inName = (EditText) findViewById(R.id.inName);
        txSignal = (TextView) findViewById(R.id.txSignal);
        txSignalNum = (TextView) findViewById(R.id.txSignalNum);

        device = getIntent().getExtras().getParcelable("btDevice");
        //deviceRssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);


        btName = device.getName();
        btAddress = device.getAddress();


        // Initialize a bt adapter so we can get the RSSI
        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        // Discover the devices
        deviceRssi = getRSSI(btName);


        tDevice.setText(btName);
        tAddress.setText(btAddress);
        //txSignalNum.setText("RSSI: " + deviceRssi + " dBm");

        btAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        btChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = inName.getText().toString();
                tDevice.setText(newName);
                btName = newName;
                inName.setText("");
            }
        });
    }

    public void save() {
        Intent intent = new Intent(this, LocateItem.class);
        intent.putExtra("btDeviceName", btName);
        intent.putExtra("btDeviceAddress", btAddress);
        startActivity(intent);

    }


    private int getRSSI(final String device){
        // My idea is if i start discovery here and have a new reciver I can rescan all the available rssi's and do a string comparison to match for the device and send back the desired rssi
        BTAdapter.startDiscovery();

        System.out.println("TEST1: Name: " + device);
         final BroadcastReceiver receiver = new BroadcastReceiver() {
             int rssi = -999;

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // For some reason it is not findnig action found
                if(BluetoothDevice.ACTION_FOUND.equals(action)){
                    String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
                    // Some reason this never gets called
                    System.out.println("TEST2: Name: " + name);
                    if(name.equals(device)){
                        rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);

                    }
                    System.out.println("TEST3: " + rssi + " name: " + name);
                }
            }

        };

        // Device was not found
        return -999;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void load() {
        FileInputStream fis = null;
        try {
            fis = openFileInput(FILE_NAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader ipr = new InputStreamReader(fis, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();


        try (BufferedReader reader = new BufferedReader(ipr)) {
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line).append('\n');
                line = reader.readLine();
                tvTest.setText(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}