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
        //System.out.println("TEST: initial RSSI " + deviceRssi);

        // Set up adapter and reciver to get signal strenth
        adapter = BluetoothAdapter.getDefaultAdapter();
        receiver = new KiwyBroadcastReceiver(device) ;

        // Set text for selected Bluetooth device
        tDevice.setText(btName);
        tAddress.setText(btAddress);
        txSignalNum.setText("Refresh");

        btAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                System.out.println("Bluetooth bonding with device: ");
                boolean outcome = false;

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    outcome = device.createBond();
                }

                System.out.println("TEST: Bounding outcome : " + outcome);

                // If the outcome is true, we are bounding with this device.
                if (outcome == true) {
                    //this.boundingDevice = device;
                    System.out.println("TEST: bound");

                    deviceRssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);

                    android.widget.Toast.makeText(getApplicationContext()," INSIDE ADD DEVICE RSSI: " + deviceRssi + "dBm", android.widget.Toast.LENGTH_SHORT).show();
                }
                 */

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

                //System.out.println(deviceRssi + " dBm");

                if(adapter.isDiscovering()) {
                    adapter.cancelDiscovery();
                }

                adapter.startDiscovery();
                IntentFilter discoverIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(receiver, discoverIntent);

                txSignalNum.setText(deviceRssi + " dBm");

/*
                if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                    BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    //3 cases:
                    //case1: bonded already
                    if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                        System.out.println("BroadcastReceiver: BOND_BONDED.");
                    }
                    //case2: creating a bone
                    if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                        System.out.println("BroadcastReceiver: BOND_BONDING.");
                    }
                    //case3: breaking a bond
                    if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                        System.out.println("BroadcastReceiver: BOND_NONE.");
                    }
                }
*/

               /*
                String action = intent.getAction();
                if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                    deviceRssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                    android.widget.Toast.makeText(getApplicationContext()," INSIDE ACTION FOUND RSSI: " + deviceRssi + "dBm", android.widget.Toast.LENGTH_SHORT).show();
                }

                android.widget.Toast.makeText(getApplicationContext(),"  RSSI: " + deviceRssi + "dBm", android.widget.Toast.LENGTH_SHORT).show();

                */

            }

        });

    }

    /*

    public boolean pair(BluetoothDevice device) {
        // Stops the discovery and then creates the pairing.
        if (device.isDiscovering()) {
            Log.d(TAG, "Bluetooth cancelling discovery.");
            bluetooth.cancelDiscovery();
        }
        Log.d(TAG, "Bluetooth bonding with device: " + deviceToString(device));
        boolean outcome = device.createBond();
        Log.d(TAG, "Bounding outcome : " + outcome);

        // If the outcome is true, we are bounding with this device.
        if (outcome == true) {
            this.boundingDevice = device;
        }
        return outcome;
    }

    */

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