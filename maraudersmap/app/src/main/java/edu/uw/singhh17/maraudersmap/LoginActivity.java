package edu.uw.singhh17.maraudersmap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.Firebase;

public class LoginActivity extends AppCompatActivity {


    private EditText name;
    private Button signIn;
    Firebase ref;

    SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        prefs = getSharedPreferences("Map", MODE_PRIVATE);


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (prefs.getBoolean("firstrun", true)) {


            setContentView(R.layout.activity_login);



            name = (EditText)findViewById(R.id.name);
            signIn = (Button) findViewById(R.id.signIn);

            Firebase.setAndroidContext(this);
            ref = new Firebase("https://map-users.firebaseio.com/users" ).child("UserDetail");


            signIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Creating firebase object

                    //Getting values to store
                    String names = name.getText().toString().trim();

                    //Creating Person object
                    UserDetail user = new UserDetail();

                    //Adding values
                    user.setFullName(names);

                    //Storing values to firebase

                    ref.push().setValue(user);

                    Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                    startActivity(intent);
                }


            });

            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            prefs.edit().putBoolean("firstrun", false).commit();
        } else {
            Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
            startActivity(intent);
        }
    }



}
