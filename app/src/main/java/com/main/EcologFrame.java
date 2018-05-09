package com.main;

import android.os.Bundle;

public class EcologFrame {

    protected static byte [] data;
    private static int h = 5;

    public EcologFrame(){
        data = new byte[1000];
    }

    public void setData(byte[] d){
        data = d;
    }

    public static byte[] getData() {
        return data;
    }

    static int getNumberDataField (int start, int end){

        int size = end-start+1;
        int value = 0;

        for(int i = 0; i < size; i++){

            value += (int) (data[h + start + i]&0xFF) << (8*(size-(i+1)));
        }

        return value;
    }

    protected int getOpcodeR(){

        return data[3]&0xFF;
    }

    public int getLengthR(){
        int len;
        len=data[1];
        len =+ len + data[0] << 8;

        return len;
    }


    public boolean isAuthenticationRequest(){
        if((data[2]==0x52)&&(data[3]==0x01)){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean isPeriodicTrip(){
        if(getOpcodeR()==0x02)
            return true;
        else
            return false;

    }

    public boolean isDriverAid(){
        if(getOpcodeR() == 0x03)
            return true;
        else
            return false;
    }


    public DriverAid driverAid = new DriverAid();
    public static class DriverAid{
        // Page 20 documentation
        Bundle getbundle(){
            Bundle b = new Bundle();
            b.putString("Frame Type", "Driver aid");
            b.putInt("Engine RPM", (int) getNumberDataField(1,2));
            b.putInt("Engine RPM from MAX", (int) getNumberDataField(3,3));
            b.putInt("Vehicle accelaration from MAX", (int) getNumberDataField(4,4));
            b.putInt("Gear number", (int) getNumberDataField(5,5));
            b.putInt("Aid Indication", (int) getNumberDataField(6,6));
            b.putInt("Raw Acceleration", (int) getNumberDataField(7,8));

            return b;
        }
    }

    public Periodic periodic = new Periodic();
    public static class Periodic{
        //  Page 18 documentation
        Bundle getBundle(){
            Bundle b = new Bundle();
            b.putString("Frame Type", "Periodic");
            b.putInt("Time Stamp", (int) getNumberDataField(1,4));
            b.putInt("Inlet Manifold Temperature", (int) getNumberDataField(5,5));
            b.putInt("Engine Coolant Temperature", (int) getNumberDataField(6,6));
            b.putInt("Vehicle Speed", (int) getNumberDataField(7,7));
            b.putInt("Engine Speed", (int) getNumberDataField(8,9));
            b.putInt("Engine Load", (int) getNumberDataField(10,10));
            b.putInt("CO2 Rate", (int) getNumberDataField(11,12));
            b.putInt("Fuel Rate A", (int) getNumberDataField(13,13));
            b.putInt("Fuel Rate B", (int) getNumberDataField(14,14));
            b.putInt("Current Gear Number", (int) getNumberDataField(15,15));
            b.putInt("Fuel Type", (int) getNumberDataField(16,16));
            b.putInt("Vehicle Communication", (int) getNumberDataField(17,17));
            b.putInt("Vbatt", (int) getNumberDataField(18,19));
            b.putInt("Vehicle Status", (int) getNumberDataField(20,20)); // Send to cloud
            b.putInt("Trip Counter", (int) getNumberDataField(21,22));
            b.putInt("CO2 Trip", (int) getNumberDataField(23,26));
            b.putInt("Fuel Volume Trip", (int) getNumberDataField(27,30));
            b.putInt("Distance Trip", (int) getNumberDataField(31,34));
            b.putInt("Wasted Fuel Trip", (int) getNumberDataField(35,36));
            b.putInt("Fuel Total", (int) getNumberDataField(37,40));
            //b.putInt("Driver Percent", (int) getNumberDataField(79,79));
            b.putInt("MIL", (int) getNumberDataField(93,93));
            b.putInt("Driver Score", (int) getNumberDataField(99,99));
            b.putInt("Drive Cycle", (int) getNumberDataField(101,101));

            return b;
        }
    }

}
