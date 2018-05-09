package com.main;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by admin on 22-Jan-18.
 */

public class BluetoothActivity extends AppCompatActivity {

    static String [] device = {""};
    List devices  = new ArrayList<String>();
    ListView pairedDevicesListView;
    public static BluetoothAdapter BA;

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        //setTheme(android.R.style.Theme_Holo);
        setContentView(R.layout.activity_bluetooth);
        getSupportActionBar().setTitle("Connect your shift");

        BA = BluetoothAdapter.getDefaultAdapter();
        pairedDevicesListView = (ListView) findViewById(R.id.pairedDeviceListView);

        if(BA.isEnabled())
            Toast.makeText(getApplicationContext(), "Bluetooth is ON", Toast.LENGTH_LONG).show();
        else
        {
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(i);

            if(BA.isEnabled())
                Toast.makeText(getApplicationContext(),"Bluetooth has been turned on", Toast.LENGTH_LONG).show();
        }

        pairedDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int item, long l) {
                device[0] = (String) devices.get(item);
            }
        });

    }

    public void turnBluetoothOff(View view){

        BA.disable();

        if(BA.isEnabled())
            Toast.makeText(getApplicationContext(), "Bluetooth could not be disabled", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getApplicationContext(),"Bluetooth has been turned off", Toast.LENGTH_LONG).show();
    }

    public void findDiscoverableDevices(View view){
        Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivity(i);
    }

    public void viewPairedDevices(View view){
        Set<BluetoothDevice> pairedDevices = BA.getBondedDevices();



        ArrayList pairedDevicesArrayList = new ArrayList();
        int i = 0;
        for(BluetoothDevice bluetoothDevice : pairedDevices){

            pairedDevicesArrayList.add(bluetoothDevice.getName());
            devices.add(i,bluetoothDevice.getAddress());
            i++;
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, pairedDevicesArrayList);
        pairedDevicesListView.setAdapter(arrayAdapter);
    }

    public void connectDevice(View view){

        if(device[0].equals(""))
            Toast.makeText(this, "Please select a device", Toast.LENGTH_LONG).show();
        else{
            Intent i = getIntent();
            String username = i.getStringExtra("username");
            String name = i.getStringExtra("name");
            Intent intent = new Intent(BluetoothActivity.this, DriverHomeActivity.class);
            intent.putExtra("deviceAddress", device[0] );
            intent.putExtra("username", username);
            intent.putExtra("name",name);
            startActivity(intent);

        }
    }
}
