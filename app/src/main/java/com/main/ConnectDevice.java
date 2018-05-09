package com.main;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by mariooliveira on 13/02/2018.
 */

class ConnectDevice extends Thread {
    public BluetoothSocket bluetoothSocket = null;
    public BluetoothAdapter bluetoothAdapter = BluetoothActivity.BA;
    ConnectThread connectedThread;
    Handler handler;
    static Handler mhandler;

    public ConnectDevice(Handler handler) {
        this.handler = handler;
        //this.mhandler = handler[1];
        connect();
    }

    void connect(){

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(BluetoothActivity.device[0]);
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


        try {
            try {
                if (bluetoothSocket.isConnected())
                    bluetoothSocket.close();
            }catch (NullPointerException e) {}

            bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();

            //connection = true;
            connectedThread = new ConnectThread(bluetoothSocket, handler);
            connectedThread.start();
            Log.i("Connextionn", "here");

        } catch (IOException e) {
            e.printStackTrace();
            DriverHomeActivity.connection = false;
        }

    }

    void setHandler(Handler h){

        handler = h;
    }


    public void disconnect(){

        connectedThread.interrupt();

        Log.i("ConnectDeviceFinish", (String.valueOf(bluetoothSocket.isConnected())  + " " + String.valueOf(connectedThread.isInterrupted())));

        //ConnectDevice.this.interrupt();

    }

}