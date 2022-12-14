package com.han.a175_kg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import butterknife.ButterKnife;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class DeviceList extends AppCompatActivity {

    // Widgets
    Button btnPaired;
    ListView devicelist;

    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    public static String EXTRA_ADDRESS = "device_address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        ButterKnife.bind(this);
        // Initialize widgets
        btnPaired = (Button) findViewById(R.id.paired_dev_btn);
        devicelist = (ListView) findViewById(R.id.paired_dev_listview);

        // Initialize bluetooth adapter
        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        if (myBluetooth == null) { // This device has not bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Adapter Not Available", Toast.LENGTH_LONG).show();
            finish();
        } else if (!myBluetooth.isEnabled()) { // This device has bluetooth adapter but turned off
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivityForResult(turnBTon, 1); // Intent to turn on bluetooth adapter
        }
        Log.e("HAN", "onCreate");
        btnPaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairedDevicesList();
            }
        });

    }

    // List with paired bluetooth devices
    private void pairedDevicesList() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

        }
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();

        if (pairedDevices.size() > 0) // ArrayList with name and MAC address of paired devices
            for(BluetoothDevice bt : pairedDevices)
                list.add(bt.getName() + "\n" + bt.getAddress());
        else
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();


        // Display paired devices in the listview
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        devicelist.setAdapter(adapter);
        devicelist.setOnItemClickListener(myListClickListener);

    }

    // When a paired device is clicked
    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3) {
            // MAC address are last 17 characters of the textview clicked
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            //Intent i = new Intent(DeviceList.this, Actions.class);
            Intent i = new Intent(DeviceList.this, MainActivity.class);
            // Send to next activity the MAC address of the chosen device
            i.putExtra(EXTRA_ADDRESS, address);
            startActivity(i);
        }
    };

}