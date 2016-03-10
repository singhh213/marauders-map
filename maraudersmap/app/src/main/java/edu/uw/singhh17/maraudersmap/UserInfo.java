package edu.uw.singhh17.maraudersmap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;

public class UserInfo extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Firebase myFirebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        myFirebaseRef = new Firebase("https://torrid-heat-6248.firebaseio.com/users");

        Bundle extras = getIntent().getExtras();
        String fullName = extras.getString("fullName");
        String phoneNumber = extras.getString("phoneNumber");


        ImageView profilePic = (ImageView)this.findViewById(R.id.profileImg);
        TextView name = (TextView)this.findViewById(R.id.name);
        final TextView phoneNo = (TextView)this.findViewById(R.id.phoneNo);
        ImageButton sendMsgBtn = (ImageButton)this.findViewById(R.id.msgBtn);
        ImageButton callBtn = (ImageButton)this.findViewById(R.id.callBtn);

        name.setText(fullName);
        phoneNo.setText(phoneNumber);

        sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeText(phoneNo.getText().toString());
            }
        });

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeCall(phoneNo.getText().toString());
            }
        });
    }

    public void composeText(String number) {

        startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null)));
    }

    public void makeCall(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+number));
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_calendar) {
            Intent calIntent = new Intent(Intent.ACTION_INSERT);
            calIntent.setData(CalendarContract.Events.CONTENT_URI);
            startActivity(calIntent);



        } else if (id == R.id.nav_logout) {
            TelephonyManager tMgr =(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
            String mPhoneNumber = tMgr.getLine1Number(); //"12532043931";
            mPhoneNumber = formatPhoneNumbers(mPhoneNumber);
            myFirebaseRef.child(mPhoneNumber).removeValue();
            SharedPreferences prefs = getSharedPreferences("Map",
                    MODE_PRIVATE);

            prefs.edit().putBoolean("firstrun", true).commit();
            Intent intent = new Intent(UserInfo.this, LoginActivity.class);
            startActivity(intent);



        } else if (id == R.id.nav_chat) {
            //chat
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setType("vnd.android-dir/mms-sms");
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private String formatPhoneNumbers(String number) {
        String formattedNumber = PhoneNumberUtils.stripSeparators(number);
        if (formattedNumber.length() == 10) {
            formattedNumber = "1" + formattedNumber;
        }
        return formattedNumber;
    }

}
