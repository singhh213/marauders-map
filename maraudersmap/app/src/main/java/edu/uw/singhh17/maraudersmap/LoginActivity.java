package edu.uw.singhh17.maraudersmap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
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
            ref = new Firebase("https://torrid-heat-6248.firebaseio.com/users" );

            TelephonyManager tMgr =(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
            final String mPhoneNumber = tMgr.getLine1Number();

            signIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Creating firebase object

                    //Getting values to store
                    String fullName = name.getText().toString().trim();

                    String phone = formatPhoneNumbers(mPhoneNumber);
                    //code to add a user
                    Firebase userRef = ref.child(phone);
                    userRef.child("fullName").setValue(fullName);
                    userRef.child("lat").setValue(38.0);
                    userRef.child("long").setValue(-77.0);

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

    private String formatPhoneNumbers(String number) {
        String formattedNumber = PhoneNumberUtils.stripSeparators(number);
        if (formattedNumber.length() == 10) {
            formattedNumber = "1" + formattedNumber;
        }
        return formattedNumber;
    }
}
