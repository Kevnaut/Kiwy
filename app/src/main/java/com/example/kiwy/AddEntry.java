package com.example.kiwy;

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


        btName = intent.getStringExtra("btDeviceName");
        btAddress = intent.getStringExtra("btDeviceAddress");

        tDevice.setText(btName);
        tAddress.setText(btAddress);

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
        /*
        FileOutputStream fos = null;

        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(btName.getBytes());
            fos.write(btAddress.getBytes());

            Toast.makeText(this, "Devices saved to " + getFilesDir() + "/" + FILE_NAME, Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
         */
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