package com.jepaynedev.townmap;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by James Payne on 7/14/2016.
 * jepayne1138@gmail.com
 */
public class CameraFollowLocationListener implements
        LocationListener,
        GoogleMap.OnCameraChangeListener,
        GoogleMap.OnMyLocationButtonClickListener {

    private final String LOG_TAG = this.getClass().getSimpleName().substring(0, 23);
    private final GoogleMap mMap;
    private boolean followLocation = true;  // Camera should follow current location

    // https://code.google.com/p/gmaps-api-issues/issues/detail?id=4636#c21
    private boolean nextCameraChangeIsManual = false;

    public CameraFollowLocationListener(GoogleMap mMap) {
        this.mMap = mMap;
        // Set the location button listener (used to resume following)
        this.mMap.setOnMyLocationButtonClickListener(this);
        // Set the camera change listener (used to disable following)
        this.mMap.setOnCameraChangeListener(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(LOG_TAG, "onLocationChanged():  followLocation = " + followLocation);
        if (followLocation) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
            this.mMap.animateCamera(cameraUpdate, new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    nextCameraChangeIsManual = false;
                }

                @Override
                public void onCancel() {
                    nextCameraChangeIsManual = true;
                }});
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        followLocation = true;
        return true;
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (nextCameraChangeIsManual) {
            // User caused onCameraChange
            followLocation = false;
        } else {
            // The next map move will be caused by user, unless we do another move programmatically
            nextCameraChangeIsManual = true;
        }
    }
}
