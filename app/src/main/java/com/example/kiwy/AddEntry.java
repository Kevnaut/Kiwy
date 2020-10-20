package com.example.kiwy;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class AddEntry extends AppCompatActivity {
    String btName;
    String btAddress;
    Button btAddDevice;
    Button btChangeName;
    Button btnRefresh;
    Intent intent;

    TextView tDevice;
    TextView tAddress;
    EditText inName;
    TextView txSignal;
    TextView txSignalNum;

    private BluetoothDevice device;
    private BluetoothAdapter adapter;
    private KiwyBroadcastReceiver receiver;

    private int deviceRssi;

    public static final String FILE_NAME = "SavedDevices.csv";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);
        intent = getIntent();

        // Set the buttons
        tDevice = (TextView) findViewById(R.id.tDevice);
        tAddress = (TextView) findViewById(R.id.tAddress);
        btAddDevice = (Button) findViewById(R.id.btAddDevice);
        btChangeName = (Button) findViewById(R.id.btChangeName);
        inName = (EditText) findViewById(R.id.inName);
        txSignal = (TextView) findViewById(R.id.txSignal);
        txSignalNum = (TextView) findViewById(R.id.txSignalNum);
        btnRefresh = (Button) findViewById(R.id.btnRefresh);

        // Set up selected Bluetooth device
        device = intent.getExtras().getParcelable("btDevice");
        btName = device.getName();
        btAddress = device.getAddress();
        deviceRssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);

        // Set up adapter and reciver to get signal strenth
        adapter = BluetoothAdapter.getDefaultAdapter();
        receiver = new KiwyBroadcastReceiver(device);


        // Set text for selected Bluetooth device
        tDevice.setText(btName);
        tAddress.setText(btAddress);
        txSignalNum.setText("Refresh");

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

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(adapter.isDiscovering()) {
                    adapter.cancelDiscovery();
                }
                if(!adapter.isDiscovering()) {
                    adapter.startDiscovery();
                    IntentFilter discoverIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(receiver,discoverIntent);

                    txSignalNum.setText( deviceRssi + " dBm");
                }
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    public void save() {
        Intent intent = new Intent(this, LocateItem.class);
        intent.putExtra("btDeviceName", btName);
        intent.putExtra("btDeviceAddress", btAddress);
        startActivity(intent);

    }


    /*
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

   */
}