package com.main;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DashboardActivity extends AppCompatActivity {


    ProgressBar coolantProgressBar;
    ProgressBar batteryProgressBar;
    ProgressBar rpmProgressBar;
    ProgressBar engineLoadProgressBar;
    ProgressBar fuelRateProgressBar;
    ProgressBar co2RateProgressBar;


    TextView coolantTextView;
    TextView batteryTextView;
    TextView rpmTextView;
    TextView engineLoadTextView;
    TextView fuelRateTextView;
    TextView co2RateTextView;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        // Progress bars
        coolantProgressBar =       findViewById(R.id.progressBar);
        batteryProgressBar =       findViewById(R.id.progressBar1);
        rpmProgressBar =           findViewById(R.id.progressBar2);
        engineLoadProgressBar =    findViewById(R.id.progressBar3);
        fuelRateProgressBar =    findViewById(R.id.progressBar4);
        co2RateProgressBar =  findViewById(R.id.progressBar5);

        // Text views
        coolantTextView =          findViewById(R.id.progress_circle_text);
        batteryTextView =          findViewById(R.id.progress_circle_text1);
        rpmTextView =              findViewById(R.id.progress_circle_text2);
        engineLoadTextView =       findViewById(R.id.progress_circle_text3);
        fuelRateTextView =       findViewById(R.id.progress_circle_text4);
        co2RateTextView =     findViewById(R.id.progress_circle_text5);


        final NumberFormat format = new DecimalFormat("#0.0");
        final NumberFormat formatRate = new DecimalFormat("#0.00");


        Runnable updateGauges = new Runnable() {
            @Override
            public void run() {


                runOnUiThread(new Runnable() {
                    public void run() {

                        Bundle driverAidBundle = DriverHomeActivity.driverAidBundle;
                        Bundle periodicBundle  = DriverHomeActivity.periodicBundle;

                        // Retrieve data from bundles
                        Object coolant      = periodicBundle.get("Engine Coolant Temperature");
                        Object battery      = periodicBundle.get("Vbatt");
                        Object rpm          = driverAidBundle.get("Engine RPM");
                        Object engineLoad   = periodicBundle.get("Engine Load");
                        Object fuelRate   = periodicBundle.get("Fuel Rate A");
                        Object fuelRateB = periodicBundle.get("Fuel Rate B");
                        Object co2Rate = periodicBundle.get("CO2 Rate");


                        try{

                            int coolantConversion       = (int) coolant - 40;
                            double batteryConversion    = (int) battery * 0.1;
                            double rpmConversion        = (int) rpm * 0.25;
                            int engineLoadConversion    = (int) engineLoad; // / 255.0 * 100;
                            double fuelRateConversion = 256.0 * (int) fuelRate + (int) fuelRateB;
                            fuelRateConversion = fuelRateConversion / 20;
                            fuelRateConversion = fuelRateConversion * 9.53674316e-7; // seconds
                            fuelRateConversion = fuelRateConversion * 3600;
                            //fuelRateConversion = fuelRateConversion / 3600;

                            Log.i("fuel rate in litres: ", String.valueOf(fuelRateConversion));

                            //double fuelRateConversion = (int) fuelRate * 9.53674316e-7;// 0.06225;
                            double co2RateConversion = (int) co2Rate * 0.0009765625;



                            Log.i("Dash-coolant", String.valueOf(coolantConversion));
                            Log.i("Dash-battery", String.valueOf(batteryConversion));
                            Log.i("Engine load", String.valueOf(engineLoadConversion));



                            //Log.i("rpm-dash", String.valueOf(rpm));
                            StringBuilder output = new StringBuilder();
                            output.append(String.valueOf(coolantConversion));
                            output.append(getString(R.string.temperature));
                            coolantTextView.setText(output.toString());


                            output.setLength(0);
                            output = new StringBuilder();
                            output.append(String.valueOf(format.format(batteryConversion)));
                            output.append(getString(R.string.voltage));
                            batteryTextView.setText(output.toString());


                            rpmTextView.setText(String.valueOf((int) rpmConversion));


                            engineLoadConversion = (int) (rpmConversion - 800) / 100; // DEMO
                            output.setLength(0);
                            output = new StringBuilder();
                            output.append(String.valueOf(engineLoadConversion));
                            output.append(getString(R.string.percentage));
                            engineLoadTextView.setText(output.toString());



                            output.setLength(0);
                            output = new StringBuilder();
                            output.append(String.valueOf(formatRate.format(fuelRateConversion)));
                            output.append("\n"+ getString(R.string.litres));
                            fuelRateTextView.setText(output.toString());

                            output.setLength(0);
                            output = new StringBuilder();
                            output.append(String.valueOf(formatRate.format(co2RateConversion)));
                            output.append("\n"+ getString(R.string.grams));
                            co2RateTextView.setText(output.toString());


                            coolantConversion = coolantConversion * 100 / 199;
                            Log.i("conversion", String.valueOf(coolantConversion));
                            coolantProgressBar.setProgress(coolantConversion);

                            rpmConversion = rpmConversion / 8000 * 100;
                            rpmProgressBar.setProgress((int) rpmConversion);


                            engineLoadProgressBar.setProgress(engineLoadConversion);

                        }
                        catch (Exception e ) {

                            Log.i("Dashboard-Exception", "Conversion exception");
                            e.printStackTrace();
                        }

                    }
                });
            }
        };
        ScheduledExecutorService exe = Executors.newScheduledThreadPool(1);
        exe.scheduleAtFixedRate(updateGauges, 0, 100, TimeUnit.MILLISECONDS);



/*
        Bundle bundle = DriverHomeActivity.bundle;
        Object rpm = bundle.get("Engine RPM");
        double rpmm = (int) rpm * 0.25;
        Log.i("rpm-dash", String.valueOf(rpm));





        rpmProgressBar.setProgress((int)rpmm);
        rpmTextView.setText(String.valueOf(rpmm));*/


       /* DriverHomeActivity.dashInstantiated =true;

        Message message = Message.obtain(handler);
*/

        /*Bundle bundle = message.getData();
        if(message.arg1 == 0){
            Object rpm = bundle.get("Engine RPM");
            Log.i("rpm-dash", String.valueOf(rpm));
        }*/

        /*Message message = DriverHomeActivity.handler2.obtainMessage();
        Bundle bundle = DriverHomeActivity.handler2.obtainMessage().getData();
        if(message.arg1 == 0){
            Object rpm = bundle.get("Engine RPM");
            Log.i("rpm-dash", String.valueOf(rpm));
        }
*/
/*
        Log.i("dash-here","dashboard");
        try {
            Log.i("dash-here2","dashboard");
            DriverHomeActivity.device.disconnect();
            DriverHomeActivity.device.setHandler(handler);
            //DriverHomeActivity.device.connect();

            //device = new ConnectDevice(handler);
            //device.start();

        }
        catch (Exception e){
            Intent i = new Intent(DashboardActivity.this, DriverHomeActivity.class);
            startActivity(i);

            Log.i("Dashboard problem", e.toString());
            e.printStackTrace();
        }*/



      /*  Bundle bundle =  DriverHomeActivity.handler2.obtainMessage().getData();

        Log.i("obd", String.valueOf(bundle.get("Engine RPM")));
*/

        /*new Thread(new Runnable() {
            public void run() {
                while (i < 100) {
                    i += 2;
                    progressHandler.post(new Runnable() {
                        public void run() {

                            myprogressBar.setProgress(i);
                            myprogressBar3.setProgress(i);
                            progressingTextView.setText("" + i + " °C");
                            int rpm = i * 70;
                            progressingTextView3.setText(Integer.toString(rpm));


                        }
                    });
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();*/
    }
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {


        public void handleMessage(Message message) {

            Log.i("dash-here3","dashboard");
            Bundle bundle = message.getData();
            if (message.arg1 == 0) {
                Log.i("dash-here4","dashboard4");
                Object rpm = bundle.get("Engine RPM");
                rpm = (int) rpm * 0.25;
                Log.i("rpm-dash", String.valueOf(rpm));



                //handler2.sendMessage(message1);
                //setText(String.valueOf(rpm));

                //displayText.setText(String.valueOf(rpm));
            }

            if(message.arg2 == 1){
                Object engineLoad = bundle.get("Engine Load");

                Log.i("engineload-dash", String.valueOf(engineLoad));

            }
        }
    };


  /*  @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {


            //Log.i("dash", "dashboard-device " + String.valueOf(device.isInterrupted()));

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
*/
}
