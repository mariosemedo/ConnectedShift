/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.List;
import java.util.Objects;

import static android.R.attr.value;


public class MainActivity extends AppCompatActivity {

  public void login(View view){

    EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
    EditText storeEditText = (EditText) findViewById(R.id.storeEditText);
    EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);

    //ParseUser user = new ParseUser();

    if(usernameEditText.getText().toString().matches("") || passwordEditText.getText().toString().matches("")){

      Toast.makeText(this, "Username and password are required", Toast.LENGTH_SHORT).show();
    }
    else {

      Log.i("here","button");

      ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
        @Override
        public void done(ParseUser user, ParseException e) {

          if(user != null) {
            Log.i("Login", "Successful " + user.getUsername());

            //Query job role

            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("username", user.getUsername());

            query.setLimit(1);

            query.findInBackground(new FindCallback<ParseUser>(){
              @Override
              public void done(List<ParseUser> objects, ParseException e) {

                if(e == null){

                  Log.i("findInBackground", "Retrieved " + objects.size() + " objects");

                  if(objects.size() > 0){

                    for(ParseObject object: objects){

                      String jobRole = object.get("jobRole").toString();
                        try {
                            changeActivity(jobRole);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                      //Log.i("findInBackgroundResult", object.get("jobRole").toString());
                    }
                  }
                }

              }
            });
          }
          else {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
          }

        }
      });

    }
  }

    public void changeActivity(String jobRole) throws ParseException {

    switch (jobRole){
      case "driver": {
        Intent driverIntent = new Intent(MainActivity.this, BluetoothActivity.class);
        //Intent driverIntent = new Intent(MainActivity.this, DriverHomeActivity.class);
        driverIntent.putExtra("username", ParseUser.getCurrentUser().getUsername()); //Optional parameters
        driverIntent.putExtra("name", ParseUser.getCurrentUser().getString("name"));
          ParseUser.getCurrentUser().put("connected", true);
          ParseUser.getCurrentUser().save();
        MainActivity.this.startActivity(driverIntent);
        break;
    }
      case "manager":
      {
        Intent managerIntent = new Intent(MainActivity.this, ManagerHomeActivity.class);
        //Intent managerIntent = new Intent(MainActivity.this, test_activity.class);
        managerIntent.putExtra("username", ParseUser.getCurrentUser().getUsername()); //Optional parameters
        managerIntent.putExtra("name", ParseUser.getCurrentUser().getString("name"));
          ParseUser.getCurrentUser().put("connected", true);
          ParseUser.getCurrentUser().save();
        MainActivity.this.startActivity(managerIntent);
        break;
      }

      default:{
        Toast.makeText(MainActivity.this, "Job Role not found. Please contact support", Toast.LENGTH_LONG).show();
      }
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getSupportActionBar().hide();
    setContentView(R.layout.activity_main);



    //ParseUser.getCurrentUser().put("connected", false);
    //ParseUser.logOut();
    //ParseUser.enableAutomaticUser();
    //ParseAnalytics.trackAppOpenedInBackground(getIntent());

  }
}
