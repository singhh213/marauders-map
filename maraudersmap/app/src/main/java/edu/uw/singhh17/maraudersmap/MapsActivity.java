package edu.uw.singhh17.maraudersmap;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    private UiSettings mUiSettings;
    private Marker marker;
    private Firebase myFirebaseRef;
    Firebase userRef;
    private ArrayList<Marker> markersArray;
    private IconGenerator iconFactory;
    private android.support.v4.app.FragmentManager fragmentManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Firebase.setAndroidContext(this);

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
        final String mPhoneNumber = tMgr.getLine1Number();
        Log.d("PHONE NUMBER", "onCreate: " + mPhoneNumber);
        myFirebaseRef = new Firebase("https://torrid-heat-6248.firebaseio.com/users");
        //code to add a user
//        Firebase userRef = myFirebaseRef.child("12069809800");
//        userRef.child("fullName").setValue("Jamielenn Uemura");

        userRef = myFirebaseRef.child(mPhoneNumber);
        markersArray = new ArrayList<Marker>();


        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (Marker x : markersArray) {
                    x.remove();
                }
//                Log.d("datachanged", "onDataChange: data has been changed");
//                System.out.println("There are " + snapshot.getChildrenCount() + " users");
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    LatLng latLng= new LatLng((double) postSnapshot.child("lat").getValue(), (double) postSnapshot.child("long").getValue());
                    String fullName = (String) postSnapshot.child("fullName").getValue();
                    String phoneNumber = (String) postSnapshot.getKey();
                    MarkerOptions markerOptions = new MarkerOptions().
                            icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(fullName))).
                            position(latLng).
                            title(phoneNumber).
                            snippet(fullName).
                            anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
                    Marker newMarker = mMap.addMarker(markerOptions);
//                    Marker newMarker = mMap.addMarker(new MarkerOptions().position(latLng));
                    markersArray.add(newMarker);
//                    Log.d("DATACHANGED", "onDataChange: " + postSnapshot.toString());
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

//                android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();
//                ft.add(R.id.drawer_layout, new UserDetail());
//                ft.commit();
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
//                marker = mMap.addMarker(new MarkerOptions().position(latLng));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
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

//        if(marker.getPosition() == null) {
//            marker = mMap.addMarker(new MarkerOptions().position(latLng));
//        } else {
////        marker = mMap.addMarker(new MarkerOptions().position(latLng));
//            marker.setPosition(latLng);
//        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        myFirebaseRef.child("message").setValue("Location changed for the " + count);
        //username
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

}
