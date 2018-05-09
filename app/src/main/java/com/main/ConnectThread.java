package com.main;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ContentHandlerFactory;
import java.util.ArrayList;

/**
 * Created by mariooliveira on 09/02/2018.
 */

public class ConnectThread extends Thread {


    private MAP27 protocol = new MAP27();
    private String message;
    private InputStream inputStream;
    private InputStreamReader inputStreamReader;
    private Handler nhandler;
    private Handler mhandler;
    private ArrayList<Handler> handlers = new ArrayList<>();

    ConnectThread(BluetoothSocket bluetoothSocket, Handler handler){

        nhandler = handler;
        //mhandler = handler[1];

        handlers.add(nhandler);
        handlers.add(mhandler);

        try{
            inputStream = bluetoothSocket.getInputStream();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        inputStreamReader = new InputStreamReader(inputStream);
    }


    // gather incoming messages and read the messages
    public void run(){
        Looper.prepare(); // Initialize the current thread as a looper
        byte[] buffer = new byte[1024];
        int bytes;
        while(true){
            try{
                bytes = inputStream.read(buffer);
                bufferRead(buffer, bytes);
            }
            catch (Exception e){
                e.printStackTrace();
                break;
            }
        }
    }

    // convert receiving messages to data
    private void read(EcologFrame ecologFrame, Handler handler){
        Message message = Message.obtain();

        if(ecologFrame.isDriverAid()){
            Bundle driverAid = ecologFrame.driverAid.getbundle();
            message.arg1 = 0;
            message.setData(driverAid);
            nhandler.sendMessage(message);
            //mhandler.sendMessage(message);
        }

        if(ecologFrame.isPeriodicTrip()){
            Bundle periodic = ecologFrame.periodic.getBundle();
            message.arg1 = 1;
            message.setData(periodic);
            nhandler.sendMessage(message);
            //mhandler.sendMessage(message);
        }
    }

    // get input state type of received messages
    private void bufferRead(byte[] buffer, int bytes){

            for(int i = 0; i < bytes; i++) {
                protocol.input(buffer[i]);
                if (protocol.isFullFrame()) {
                    EcologFrame ecologFrame = new EcologFrame();
                    ecologFrame.setData(protocol.getData());
                    read(ecologFrame, nhandler);
                    protocol.reset();

            }
        }
    }
}
