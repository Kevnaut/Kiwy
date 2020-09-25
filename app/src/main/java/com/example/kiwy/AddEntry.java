package com.example.kiwy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AddEntry extends AppCompatActivity {
    String btName;
    String btAddress;
    Intent intent;

    TextView tDevice;
    TextView tAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);
        intent = getIntent();
        tDevice = (TextView) findViewById(R.id.tDevice);
        tAddress = (TextView) findViewById(R.id.tAddress);

        btName = intent.getStringExtra("btDeviceName");
        btAddress = intent.getStringExtra("btDeviceAddress");

        tDevice.setText(btName);
        tAddress.setText(btAddress);
    }
}