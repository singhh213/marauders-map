package edu.uw.singhh17.maraudersmap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.Firebase;

public class LoginActivity extends AppCompatActivity {


    private EditText name;
    private Button signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        name = (EditText)findViewById(R.id.name);
        signIn = (Button) findViewById(R.id.signIn);

        Firebase.setAndroidContext(this);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating firebase object
                Firebase ref = new Firebase("https://map-users.firebaseio.com/users" );

                //Getting values to store
                String names = name.getText().toString().trim();

                //Creating Person object
                UserDetail user = new UserDetail();

                //Adding values
                user.setFullName(names);

                //Storing values to firebase
                ref.child("UserDetail").setValue(user);

                Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                startActivity(intent);
            }


        });
    }




}
