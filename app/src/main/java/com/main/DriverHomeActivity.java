package com.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;

import java.text.DecimalFormat;

public class DriverHomeActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;
    static boolean connection = true;
    static ConnectDevice device;
    Message message;
    DashboardActivity dashboardActivity = new DashboardActivity();
    static public Handler handler2 = new Handler();
    static public boolean dashInstantiated = false;
    static Bundle driverAidBundle;
    static Bundle periodicBundle;

    //private Gauge gauge2;
    int i;
    private TextView displayTextView;
    private TextView aidTextView;
    private static boolean tripStart = false;
    private static int currentTrip;
    private static int currentFuelVolume;
    private static int totalSpeed;
    private static int periodicMessageCount = 1;
    private static double totalFuelSpent = 0.0;
    private static double mileage = 0.0;
    private static double previousSpeed = 0.0;
    private static double driverScore = 100;
    private static double currentSpeedMPH = 0.0;


    public void setConnection() {

        if (connection) {
            device = new ConnectDevice(handler);
            device.start();
        }
        if (!connection) {

            Toast.makeText(getApplicationContext(), "Connection failed", Toast.LENGTH_LONG).show();

            try {
                device.bluetoothSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            connection = true;

            Intent intent = new Intent(DriverHomeActivity.this, BluetoothActivity.class);
            startActivity(intent);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_driver_home);
        setConnection(); // Establish bluetooth connection


        Intent intent = getIntent();
        final String username = intent.getStringExtra("Username");
        String name = intent.getStringExtra("name"); //if it's a string you stored.
        //getSupportActionBar().setTitle(name.toUpperCase());


        getSupportActionBar().setTitle("Driver Home");

        if (ParseUser.getCurrentUser().isAuthenticated()) {
            ParseUser.getCurrentUser().put("connected", true);
        }





        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                Log.i("Location", location.toString());

                DecimalFormat format = new DecimalFormat("##.0000");

                double latitude = Double.valueOf(format.format(location.getLatitude()));
                double longitude = Double.valueOf(format.format(location.getLongitude()));

                ParseGeoPoint geoLocation = new ParseGeoPoint(latitude, longitude);

                ParseUser user = ParseUser.getCurrentUser();
                user.put("location", geoLocation);
                user.saveInBackground();

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };

        // If device is running SDK < 23 (Marshmallow)

        if (Build.VERSION.SDK_INT < 23) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        } else {


            //check if dont have permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //ask for it


                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {
                // have permission

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }
        }


        aidTextView = findViewById(R.id.aidTextView);
        //Button button = findViewById(R.id.button);
        //gauge2 = findViewById(R.id.gauge2);



      /*  final TextView duration = (TextView) findViewById(R.id.duration);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                long millis = System.currentTimeMillis() - starttime;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;

                duration.setText(String.format("%d:%02d", minutes, seconds));
            }
        });*/



        //displayTextView.setText(Integer.toString(gauge2.getValue()));

        /*button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new Thread() {
                    public void run() {
                        for (i = 1; i < 101; i++) {
                            try {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        gauge2.setValue(i);

                                        int value = gauge2.getValue();
                                        //tickerView.setText(Integer.toString(gauge2.getValue()));
                                        Log.i("GaugeValue", Integer.toString(gauge2.getValue()));

                                        displayTextView.setText(Integer.toString(value));

                                    }
                                });
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();
            }

        });
*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.top_menu, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        super.onOptionsItemSelected(menuItem);

        switch (menuItem.getItemId()){
            case R.id.portal :{

                Intent intent = new Intent(DriverHomeActivity.this, WebviewActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.help:{

                return true;
            }
            default:
                return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // do this
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

            }
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void openDashboard(View view) {

        Intent intent = new Intent(DriverHomeActivity.this, DashboardActivity.class);
        device.interrupt();
        DriverHomeActivity.this.startActivity(intent);
    }

    private void setAidIndication(final int aid){

        final int instruction = aid;

        Log.i("DriverHome", "Aid: " + String.valueOf(aid));

        runOnUiThread(new Runnable() {
            public void run() {

                switch (instruction) {

                    case 0:
                        aidTextView.setText(getText(R.string.stay));
                        break;
                    case 1:
                        aidTextView.setText(getString(R.string.up));
                        break;
                    case 2:
                        aidTextView.setText(getText(R.string.down));
                        break;
                    case 4:
                        aidTextView.setText(getText(R.string.ease));
                        //driverScore = driverScore - 1;
                        break;
                    default:
                        aidTextView.setText(getText(R.string.stay));
                        break;
                }
            }
        });
    }

    private void setGearNumber(final int gearNumber){

        final TickerView currentGear = findViewById(R.id.gearTickerView);
        currentGear.setCharacterList(TickerUtils.getDefaultNumberList());

        Log.i("DriverHome", "GearNumber: " + String.valueOf(gearNumber));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                currentGear.setTextColor(Color.BLACK);
                switch (gearNumber){
                    case 0: // Neutral
                        currentGear.setTextColor(Color.RED);
                        currentGear.setText("N");
                        break;
                    case 1:
                        currentGear.setText("1");
                        break;
                    case 2:
                        currentGear.setText("2");
                        break;
                    case 3:
                        currentGear.setText("3");
                        break;
                    case 4:
                        currentGear.setText("4");
                        break;
                    case 5:
                        currentGear.setText("5");
                        break;
                    case 6:
                        currentGear.setText("6");
                        break;
                    case 7: // Automatic transmissions
                        currentGear.setText("7");
                        break;
                    case 8:
                        currentGear.setText("8");
                        break;
                    case 9:
                        currentGear.setText("9");
                        break;
                    default:
                        currentGear.setText("N/A");
                        break;
                }
            }
        });
    }

    private double speedConversion(int speed){

        return speed * 0.62;
    }

    private void setVehicleSpeed (int speed){

        final Double speedMPH = speedConversion(speed); // convert from kph to mph
        final TextView currentSpeed =  (TextView) findViewById(R.id.speedTextView);

        Log.i("DriverHome", "VehicleSpeedKPH: " + String.valueOf(speed));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                currentSpeed.setText(String.valueOf(new DecimalFormat("#").format(speedMPH)));

            }
        });

    }

    private void setTripCounter(int tripCounter){

        final TickerView tripCounterView = findViewById(R.id.tripCounterTickerView);
        tripCounterView.setCharacterList(TickerUtils.getDefaultNumberList());
        tripCounterView.setAnimationDuration(1500);

        Log.i("DriverHome", "TripCounter: " + String.valueOf(tripCounter));

        if(tripStart){
            tripCounter = tripCounter - currentTrip;
        }
        else {
            currentTrip = tripCounter;
            tripStart = true;
        }

        final Double distanceTotal = (tripCounter * 0.00390625) / 1609.344; // total distance done by vehicle
        mileage = distanceTotal;

        //final Double distance = distanceTotal;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tripCounterView.setText(new DecimalFormat("#.#").format(distanceTotal) + " miles");

            }
        });
    }

    static int currentStatus = -1;
    private void setVehicleStatus(int status){

        ParseUser user = ParseUser.getCurrentUser();

        Log.i("DriverHome", "VehicleStatus: " + String.valueOf(status));

        if (status != currentStatus) {
            currentStatus = status;
            Log.i("DriverHome", "VehicleStatus updated");
            switch (status) {
                case 0:
                    user.put("vehicleStatus", "Unknown");
                    break;
                case 1:
                    user.put("vehicleStatus", "Stopped");
                    break;
                case 2:
                    user.put("vehicleStatus", "Stationary");
                    break;
                case 3:
                    user.put("vehicleStatus", "Driving");
                    break;
                default:
                    user.put("vehicleStatus", "N/A");
                    break;
            }

            try {
                user.save();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void setUserScore(int time, int speed){

        //Log.i("DriverHome", "Score: " + String.valueOf(speed));

        int second = (time % 3600) % 60;



        double mph = speedConversion(speed);

        if(periodicMessageCount == 1)
            previousSpeed = mph;

        double currentSpeed = Double.valueOf(new DecimalFormat("#").format(mph));
        //Log.i("DriverHomeScore", String.valueOf(mph) +" "+ String.valueOf( speed) +" "+ String.valueOf(previousSpeed) +" "+  String.valueOf(currentSpeed) );
        double speedDifference = currentSpeed - previousSpeed;

        //Log.i("DriverHome", "Driver score: " + String.valueOf(driverScore) + "Speed Diff:" + String.valueOf(speedDifference));
        // Acceleration hard
        if(speedDifference >= 8.5){
            driverScore = driverScore - 1;
        }
        //
        else if(speedDifference <= -6.5){
            driverScore = driverScore - 1;
        }
        previousSpeed = currentSpeed;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView scoreTextView = (TextView) findViewById(R.id.scoreTextView);
                Gauge gauge = (Gauge) findViewById(R.id.gauge2);

                gauge.setValue((int)driverScore);
                scoreTextView.setText(String.valueOf(new DecimalFormat("#").format(driverScore)));
            }
        });

    }


    private void setDuration(int duration){

        final TextView durationTextView = (TextView) findViewById(R.id.duration);

        Log.i("DriverHome", "Duration: " + String.valueOf(duration));

        duration = duration / 1000; // milliseconds to seconds

        int hour = duration / 3600;
        int minute = (duration % 3600) / 60;
        int second = (duration % 3600) % 60;

        final StringBuilder shiftDuration = new StringBuilder();

        if(hour > 0) {
            shiftDuration.append(hour + "h ");
            shiftDuration.append(minute + "min");
        }
        else
            shiftDuration.append(minute + " min");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                setTextView(durationTextView, shiftDuration, R.string.duration);
            }
        });
    }

    private void setAverageSpeed(int speed){

        final TextView avgSpeedTextView  =  (TextView) findViewById(R.id.averageSpeed);

        speed = (int) speedConversion(speed);

        totalSpeed = totalSpeed + speed;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Double avgSpeed = (double) totalSpeed / (double) periodicMessageCount;
                StringBuilder avgSpeedStringBuilder = new StringBuilder(String.valueOf(new DecimalFormat("#.#").format(avgSpeed)));
                avgSpeedStringBuilder.append(" mph");
                setTextView(avgSpeedTextView, avgSpeedStringBuilder, R.string.avgSpeed);

            }
        });

    }

    private void setShiftCost(double fuel){

        final TextView shiftCostTextView = (TextView) findViewById(R.id.cost);

        //double price = getTodayFuelPrice();
        final double price = 1.16;

        // Reset fuel volume
        //if(periodicMessageCount == 1)
            //currentFuelVolume = fuel;

        //fuel = fuel - currentFuelVolume;

        //double convertedFuel = fuel * 0.00001525878 / 4; // 2^-16 1.52587891E-5;

        //Log.i("DriverHome", "Fuel in litres:" + String.valueOf(fuel));

        final double cost = fuel * price;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                StringBuilder stringBuilder = new StringBuilder("£");
                stringBuilder.append(String.valueOf(new DecimalFormat("#.##").format(cost)));

                setTextView(shiftCostTextView, stringBuilder, R.string.cost);
            }
        });
    }

    private void calculateMPG(int totalMileage){

        final TextView mpgTextView = (TextView) findViewById(R.id.averageMPG);

        double mpg = mileage;

        mpg = mpg / (totalFuelSpent * 0.21);


        final double avgMpg = mpg;

        Log.i("DriverHome", "MPG:" + String.valueOf(mpg));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                StringBuilder stringBuilder =
                        new StringBuilder(String.valueOf(new DecimalFormat("#.#").format(avgMpg)));
                setTextView(mpgTextView, stringBuilder, R.string.avgMPG);
            }
        });

    }

    private void setTextView(TextView textView, StringBuilder stringBuilder, int id){

        String textViewFormat = getString(id) + "<br>"
                + "<h1><b>"
                + stringBuilder.toString()
                + "</h1></b>";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(textViewFormat, Html.FROM_HTML_MODE_LEGACY));
        }
        else {
            textView.setText(Html.fromHtml(textViewFormat));
        }
    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message message) {

            if (message.arg1 == 0) {
                driverAidBundle = message.getData();
                Object rpm = driverAidBundle.get("Engine RPM");
                Object gear = driverAidBundle.get("Gear number");
                Object aid = driverAidBundle.get("Aid Indication");

                setAidIndication((int) aid); // TEST REQUIRED
                //setGearNumber((int) gear); // TEST REQUIRED

                if(currentSpeedMPH == 0)
                    setGearNumber((int) 0);
                else if(currentSpeedMPH > 1 && currentSpeedMPH < 7)
                    setGearNumber((int) 1);
                else if(currentSpeedMPH > 7 && currentSpeedMPH < 15)
                    setGearNumber((int) 2);
                else if(currentSpeedMPH > 15 && currentSpeedMPH < 30)
                    setGearNumber((int) 3);
                else if(currentSpeedMPH > 30 && currentSpeedMPH < 40)
                    setGearNumber((int) 4);
                else if(currentSpeedMPH > 40)
                    setGearNumber((int) 5);

                rpm = (int) rpm * 0.25;
                Log.i("rpm", String.valueOf(rpm));
                Log.i("aid", String.valueOf(aid));


            }

            if (message.arg1 == 1){
                periodicBundle = message.getData();

                Object vehicleSpeed = periodicBundle.get("Vehicle Speed");
                Object gear = periodicBundle.get("Current Gear Number");
                Object score = periodicBundle.get("Driver Score");
                Object status = periodicBundle.get("Vehicle Status");
                Object tripCounter = periodicBundle.get("Distance Trip");
                Object duration = periodicBundle.get("Time Stamp");
                Object usedFuel = periodicBundle.get("Fuel Volume Trip");
                Object cycle = periodicBundle.get("Drive Cycle");

                Log.i("cycle", String.valueOf(cycle));

                setVehicleSpeed((int) vehicleSpeed); // TEST REQUIRED
                setVehicleStatus((int) status); // TEST REQUIRED & TIMER NEEDS TO BE ADDED EITHER THIS OR MANAGER HOME ACTIVITY
                setTripCounter((int) tripCounter); // TEST REQUIRED
                setUserScore((int) duration, (int) vehicleSpeed); // TEST REQUIRED
                setDuration((int) duration); // TEST REQUIRED
                setAverageSpeed((int) vehicleSpeed); // TEST REQUIRED
                calculateMPG((int) tripCounter);

                currentSpeedMPH = speedConversion((int)vehicleSpeed);

                Object fuelRate   = periodicBundle.get("Fuel Rate A");
                Object fuelRateB = periodicBundle.get("Fuel Rate B");

                double fuelRateConversion = 256.0 * (int) fuelRate + (int) fuelRateB;
                fuelRateConversion = fuelRateConversion / 20;
                fuelRateConversion = fuelRateConversion * 9.53674316e-7; // seconds
                totalFuelSpent = totalFuelSpent + fuelRateConversion;

                //Log.i("DriverHome", "Fuel" + String.valueOf(totalFuelSpent));
                setShiftCost(totalFuelSpent);
                Log.i("status", String.valueOf(status));
                periodicMessageCount++;
            }

            /*if(dashInstantiated){
                message.setTarget(DashboardActivity.handler);
                message.sendToTarget();


                //handler2.sendMessage(message1);
                //setText(String.valueOf(rpm));

                //displayText.setText(String.valueOf(rpm));
            }*/
        }
    };





}