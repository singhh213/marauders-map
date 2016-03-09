package edu.uw.singhh17.maraudersmap;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    private UiSettings mUiSettings;
    private Marker marker;
    private Firebase myFirebaseRef;
    Firebase userRef;
    private ArrayList<Marker> markersArray;
    private IconGenerator iconFactory;
    private android.support.v4.app.FragmentManager fragmentManager;
    private HashMap<String, String> contactsMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Firebase.setAndroidContext(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        fragmentManager = getSupportFragmentManager();
        iconFactory = new IconGenerator(this);
        TelephonyManager tMgr =(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        final String mPhoneNumber = tMgr.getLine1Number(); //"12532043931";
        Log.d("PHONE NUMBER", "onCreate: " + mPhoneNumber);
        myFirebaseRef = new Firebase("https://torrid-heat-6248.firebaseio.com/users");
        //code to add a user
//        Firebase userRef = myFirebaseRef.child("12064030222");
//        userRef.child("fullName").setValue("Friend 1");
//        userRef.child("lat").setValue(48);
//        userRef.child("long").setValue(-119);
//
//        userRef = myFirebaseRef.child("12064077210");
//        userRef.child("fullName").setValue("Friend 2");
//        userRef.child("lat").setValue(45);
//        userRef.child("long").setValue(-119);

        userRef = myFirebaseRef.child(mPhoneNumber);

        markersArray = new ArrayList<Marker>();
        contactsMap = getContactsMap();

        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (Marker x : markersArray) {
                    x.remove();
                }

                for (DataSnapshot postSnapshot: snapshot.getChildren()) {

                    String phoneNumber = postSnapshot.getKey();

                    if (contactsMap.containsKey(phoneNumber) || mPhoneNumber == phoneNumber) {


                        LatLng latLng = new LatLng((double) postSnapshot.child("lat").getValue(), (double) postSnapshot.child("long").getValue());
                        String fullName = (String) postSnapshot.child("fullName").getValue();

                        MarkerOptions markerOptions = new MarkerOptions().
                                icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(fullName))).
                                position(latLng).
                                title(phoneNumber).
                                snippet(fullName).
                                anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
                        Marker newMarker = mMap.addMarker(markerOptions);
                        //Marker newMarker = mMap.addMarker(new MarkerOptions().position(latLng));
                        markersArray.add(newMarker);
                        //Log.d("DATACHANGED", "onDataChange: " + postSnapshot.toString());
                    }
                }

                //zooms in my location
//                CameraUpdate center=
//                        CameraUpdateFactory.newLatLng(new LatLng((double) snapshot.child(mPhoneNumber).child("lat").getValue(),
//                                (double) snapshot.child(mPhoneNumber).child("long").getValue()));
//                CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
//
//                mMap.moveCamera(center);
//                mMap.animateCamera(zoom);

            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
//                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(Marker arg0) {
                Log.d("no", "cliked on marker");

                Intent intent = new Intent(MapsActivity.this, UserInfo.class);
                intent.putExtra("phoneNumber", arg0.getTitle());
                intent.putExtra("fullName", arg0.getSnippet());
                startActivity(intent);
                return true;
            }
        });
        mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
    }

    @Override
    public void onConnected(Bundle bundle) {
        getLocation(null);

        LocationRequest request = new LocationRequest();
        request.setInterval(10000);
        request.setFastestInterval(10000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, this);
    }

    public void getLocation(View v) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (loc != null) {
                double currentLatitude = loc.getLatitude();
                double currentLongitude = loc.getLongitude();
                LatLng latLng = new LatLng(currentLatitude, currentLongitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
                mMap.animateCamera(zoom);
                userRef.child("lat").setValue(currentLatitude);
                userRef.child("long").setValue(currentLongitude);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("LOC CHANGED", "onLocationChanged: location changed");

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        userRef.child("lat").setValue(currentLatitude);
        userRef.child("long").setValue(currentLongitude);
        Log.d("LOC CHANGED", "onLocationChanged: firebase called");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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


    private HashMap<String, String> getContactsMap() {
        HashMap<String, String> myMap = new HashMap<String, String>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        int phoneType     = pCur.getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                        String phoneNo     = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        switch (phoneType)
                        {
                            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                Log.e(name + ": TYPE_MOBILE", " " + phoneNo + " " + phoneType);
                                phoneNo = formatPhoneNumbers(phoneNo);
                                myMap.put(phoneNo, name);
                                break;
                            default:
                                break;
                        }
                    }
                    pCur.close();
                }
            }
            Log.v("MYMAP", myMap.toString());
        }

        return myMap;
    }

    private String formatPhoneNumbers(String number) {
        String formattedNumber = PhoneNumberUtils.stripSeparators(number);
        if (formattedNumber.length() == 10) {
            formattedNumber = "1" + formattedNumber;
        }
        return formattedNumber;
    }

}
