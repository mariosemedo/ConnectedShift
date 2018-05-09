package com.main;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;

public class ManagerHomeActivity extends AppCompatActivity implements GeoTask.Geo{

    ListView driverListView;
    public List<HashMap<String, String>> driversMap = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_home);
        getSupportActionBar().setTitle("Manager Home");


        Intent intent = getIntent();
        String value = intent.getStringExtra("Username"); //if it's a string you stored.


        final ArrayList<String> myDrivers = new ArrayList<String>(asList(""));
        final ArrayList<String> driverUsernames = new ArrayList<String>(asList(""));


        //final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myDrivers);

        //driverListView.setAdapter(arrayAdapter);
        driverListView = (ListView) findViewById(R.id.list_view);
        driverListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Intent driverIntent = new Intent(ManagerHomeActivity.this, MapsActivity.class);
                Collections.sort(driverUsernames);

                driverIntent.putExtra("username", driverUsernames.get(position+1));

                // Log.i("driver", driverUsernames.get(position));
                // Log.i("driver", driverUsernames.toString());
                // Log.i("driver", myDrivers.toString());
                ManagerHomeActivity.this.startActivity(driverIntent);
                Toast.makeText(getApplicationContext(), myDrivers.get(position),Toast.LENGTH_LONG ).show();
            }
        });


        ParseQuery<ParseUser> query = new ParseUser().getQuery();


        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException exception) {
                if(users != null){

                    String [] strings = {"Driving","Slow pace (2 min) ","Stationary (1 min)",""};
                    int e = 0;
                    for(int i = 0; i < users.size(); i++){
                        if (users.get(i).get("jobRole").toString().equals("driver") ) {

                            ParseGeoPoint geoPoint = users.get(i).getParseGeoPoint("location");

                            users.get(i).put("status",getDriverStatus(geoPoint.toString())); // not saving in database

                            HashMap<String, String> hm = new HashMap<>();
                            hm.put("driver_image", Integer.toString(R.drawable.delivery_man));
                            hm.put("driver_name", users.get(i).get("name").toString());
                            hm.put("driver_status", users.get(i).get("status").toString());

                            String string = strings[e];
                            switch (e){
                                case 0:
                                    hm.put("driver_driving", string);
                                    hm.put("driver_slow","");
                                    hm.put("driver_stationary","");
                                    break;
                                case 1:
                                    hm.put("driver_driving", "");
                                    hm.put("driver_slow",string);
                                    hm.put("driver_stationary","");
                                    break;
                                case 2:
                                    hm.put("driver_driving", "");
                                    hm.put("driver_slow","");
                                    hm.put("driver_stationary",string);
                                    break;
                                default:
                                    hm.put("driver_driving", "");
                                    hm.put("driver_slow","");
                                    hm.put("driver_stationary","");
                                    break;
                            }
                            driversMap.add(hm);
                            e++;

                            //arrayAdapter.add(users.get(i).get("name").toString());
                            driverUsernames.add(i, users.get(i).getUsername());
                        }
                    }
                    createListView();
                }
            }
        });



       /* ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
        parseQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException exception) {

                if(users != null){

                    for(int i = 0; i < users.size(); i++){

                        if(users.get(i).get("jobRole").toString().equals("driver")) {

                            ParseGeoPoint geoPoint = users.get(i).getParseGeoPoint("location");



                        }
                    }
                }
            }
        });
*/


        //getDriverLocation();

    }

    public void createListView() {

        String[] from = {"driver_image", "driver_name", "driver_status", "driver_driving", "driver_slow", "driver_stationary"};
        int[] to = {R.id.driver_image, R.id.driver_name, R.id.driver_status, R.id.driving, R.id.slowpace, R.id.stationary};

        SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), driversMap, R.layout.layout_listview, from, to);
        driverListView.setAdapter(simpleAdapter);

    }

    public String getDriverStatus(String location){


        location = location.replaceAll("[^0-9,.]+","");

        if(location.equals("0.000000,0.000000"))
            return "Offline";

        String status = "";

        Log.i("duration", location);

        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins="
                + "51.8791897,0.932096" + "&destinations=" + location
                + "&mode=driving&language=en-EN&avoid=tolls&key=AIzaSyDRmZRpRXLgR4_tOdf_l2_U5LEkfL_0XDQ";

        try {

            String time = new GeoTask(ManagerHomeActivity.this).execute(url).get();
            int hours =  Integer.valueOf(time) / 60 / 60;
            int mins =  Integer.valueOf(time) / 60;
            mins = mins % 60;
            if(hours > 0)
                status = String.valueOf(hours + " hr ");
            status = status + String.valueOf(mins);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return status +  " mins away from store";

    }

    @Override
    public void setDouble(String string) {

        //String res[]=string.split(",");
        //Double min=Double.parseDouble(res[0])/60;
        //int dist=Integer.parseInt(res[1])/1000;
        //Log.i("Duration= ",  (int) (min / 60) + " hr " + (int) (min % 60) + " mins");
        //Log.i("Distance= ",  dist + " kilometers");

    }
}