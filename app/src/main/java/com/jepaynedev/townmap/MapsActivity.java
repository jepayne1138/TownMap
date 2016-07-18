package com.jepaynedev.townmap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        SignInDialogFragment.SignInDialogListener {

    private final String LOG_TAG = this.getClass().getSimpleName();
    private final int PERMISSIONS_REQUEST_FINE_LOCATION = 0;
    private final int LOCATION_REQUEST_INTERVAL = 5000;  // In milliseconds
    private final float DEFAULT_TRACKING_ZOOM_LEVEL = 15;
    private Toolbar toolbar;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationListener followListener;
    private boolean signedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Set up Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
            buildGoogleApiClient();
            mGoogleApiClient.connect();
        }

        if (mMap == null) {
            FragmentManager fragMan = getSupportFragmentManager();
            SupportMapFragment mapFragment =
                    (SupportMapFragment) fragMan.findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            if (followListener != null) {
                // If the camera following listener was set, remove it from the location provider
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, followListener);
            }
        }
        mGoogleApiClient.stopAutoManage(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpMap();
    }

    private void setUpMap() {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(false);

        // Check if we current have FINE_LOCATION permissions and request them if not
        checkLocationPermission();
    }

    private boolean checkLocationPermission() {
        Log.d(LOG_TAG, "checkLocationPermissions called");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG, "FINE_LOCATION permission not granted!");
            Log.d(LOG_TAG, "Requesting FINE_LOCATION permission...");
            // Location permission was not granted, request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_FINE_LOCATION);
            return false;
        }
        Log.d(LOG_TAG, "FINE_LOCATION permission granted!");
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_FINE_LOCATION: {
                Log.d(LOG_TAG, "PERMISSIONS_REQUEST_FINE_LOCATION");
                // If request was cancelled, the result array is empty
                if (grantResults.length > 0 && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted!
                    // TODO: Figure out if I can avoid checking once again.
                    // Check for permission yet again to satisfy AndroidStudio
                    Log.d(LOG_TAG, "FINE_LOCATION permission was granted");
                    if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        Log.d(LOG_TAG, "Second check passed, setting map location enabled");
                        mMap.setMyLocationEnabled(true);
                        // If the ApiClient, LocationRequest and FollowListener have all been
                        // initialized, we are able to set location updates to follow the user
                        if (mGoogleApiClient != null && mLocationRequest != null
                                && followListener != null) {
                            Log.d(LOG_TAG, "FusedLocationApi requestLocationUpdates");
                            LocationServices.FusedLocationApi.requestLocationUpdates(
                                    mGoogleApiClient, mLocationRequest, followListener);
                        }
                    }
                } else {
                    Log.d(LOG_TAG, "FINE_LOCATION permissions denied! Disabling location features.");
                    // Permission was denied!
                    mMap.setMyLocationEnabled(false);
                    // If the ApiClient is connected and the camera following listener was set,
                    // remove it from the location provider
                    if (mGoogleApiClient != null && followListener != null) {
                        // If the camera following listener was set, stop listening
                        LocationServices.FusedLocationApi.removeLocationUpdates(
                                mGoogleApiClient, followListener);
                    }

                }
                return;
            }
        }
    }

    // Implements GoogleApiClient.ConnectionCallbacks
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // When we connect, start a listener to get real-time location updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(LOCATION_REQUEST_INTERVAL);

        // Create the listener that will track the user on the map
        followListener = new CameraFollowLocationListener(mMap);

        // Get the initial position
        if (checkLocationPermission()) {
            // If was have location permission, set the zoom level on the map to something
            // appropriate for tracking th user. With location permission, we will follow the user
            // with the CameraFollowLocationListener, so the camera will move to their location on
            // the first location update, but the default zoom (in case of no location permissions)
            // is set too far out.
            mMap.moveCamera(CameraUpdateFactory.zoomTo(DEFAULT_TRACKING_ZOOM_LEVEL));
        }

        if (checkLocationPermission()) {
            mMap.setMyLocationEnabled(true);
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, followListener);
        }
    }

    // Implements GoogleApiClient.ConnectionCallbacks
    @Override
    public void onConnectionSuspended(int i) {
    }

    // GoogleApiClient.OnConnectionFailedListener
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        if (signedIn) {
            menu.findItem((R.id.action_sign_in)).setVisible(false);
            menu.findItem((R.id.action_sign_out)).setVisible(true);
        } else {
            menu.findItem((R.id.action_sign_in)).setVisible(true);
            menu.findItem((R.id.action_sign_out)).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // Settings option was chosen in the toolbar
                return true;
            case R.id.action_sign_in:
                // Sign In option was chosen in the toolbar
                showSignInDialog();
                return true;
            case R.id.action_sign_out:
                // Sign Out option was chosen in the toolbar
                // TODO: Implement sign out handler

                // Rebuild the menu with flag indicating we're signed out to hide the sign out
                // option and re-show the sign in option
                signedIn = false;
                invalidateOptionsMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSignInDialog() {
        android.support.v4.app.DialogFragment signInDialog = new SignInDialogFragment();
        signInDialog.show(
                getSupportFragmentManager(), String.valueOf(R.string.fragment_tag_sign_in_dialog));
    }

    @Override
    public void onGoogleSignInClick(android.support.v4.app.DialogFragment dialog) {
        Log.d(LOG_TAG, "onGoogleSignInClick");
        // TODO: Implement handler on the condition that sign in was successful
        // May need to check for success first, not sure if we can make is so this is only called
        // upon success

        // Hide the sign in option from the overflow Toolbar menu and replace with sign out
        // TODO:  Make sure this method is only being called on success only
        signedIn = true;
        invalidateOptionsMenu();
    }

    @Override
    public void onDialogNegativeClick(android.support.v4.app.DialogFragment dialog) {
        // Sign in dialog was canceled, we remain signed out
    }
}
