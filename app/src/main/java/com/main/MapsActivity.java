package com.main;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        Intent intent = getIntent();
        final String driver = intent.getStringExtra("username");
        ParseGeoPoint g = new ParseGeoPoint();
        final ArrayList<ParseGeoPoint> geoPointStore = new ArrayList<>(asList(g));

        Runnable mapUpdate = new Runnable() {
            @Override
            public void run() {

                final HashMap<String,ParseGeoPoint> drivers =  new HashMap<>();
                ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
                parseQuery.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> users, ParseException exception) {

                        if (users != null) {
                                String driverSelected = "";
                            for (int i = 0; i < users.size(); i++) {

                                if(users.get(i).get("jobRole").toString().equals("driver")) {

                                    //ParseGeoPoint geoPoint = users.get(i).getParseGeoPoint("location");
                                    if (users.get(i).get("username").toString().equals(driver))
                                        driverSelected = users.get(i).get("name").toString();
                                    //geoPointStore.add(geoPoint);
                                    //displayMap(googleMap, geoPoint, users.get(i).get("name").toString());
                                    drivers.put(users.get(i).get("name").toString(), users.get(i).getParseGeoPoint("location"));

                                    //Log.i("location", geoPoint.toString());
                                    //Log.i("list1", geoPointStore.toString());
                                    //Log.i("location",users.get(i).get("location").toString());
                                }

                            }
                            displayMap(googleMap, drivers, driverSelected);
                        }
                    }
                });
            }
        };

        ScheduledExecutorService exe = Executors.newScheduledThreadPool(1);
        exe.scheduleAtFixedRate(mapUpdate, 0, 1, TimeUnit.SECONDS);
    }


    public void displayMap(GoogleMap googleMap, HashMap<String, ParseGeoPoint> drivers, String driver) {

        mMap = googleMap;
        googleMap.clear();
        // Add a marker in Sydney and move the camera

        //mMap.setMinZoomPreference(15.0f);

        for (Map.Entry<String, ParseGeoPoint> entry: drivers.entrySet()) {

            String name = entry.getKey();
            ParseGeoPoint position = entry.getValue();
            LatLng location = new LatLng(position.getLatitude(), position.getLongitude());

            if(name.equals(driver))
                mMap.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory.fromResource(R.drawable.car2)).title(name));
            else
                mMap.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory.fromResource(R.drawable.car3)).title(name));

            //mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        }



        //Log.i("list1", geoPointStore.toString());

        //LatLng location = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());

        //mMap.addMarker(new MarkerOptions().position(new LatLng(51.888200,0.931383)).icon(BitmapDescriptorFactory.fromResource(R.drawable.car3)).title("Frank Jones"));

        //mMap.addMarker(new MarkerOptions().position(sydney).title("Mario Oliveira"));


    }

}
