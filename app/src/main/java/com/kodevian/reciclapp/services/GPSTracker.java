package com.kodevian.reciclapp.services;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.kodevian.reciclapp.R;
import com.kodevian.reciclapp.util.UtilityClass;

/**
 * Created by junior on 12/05/16.
 */
public class GPSTracker extends Service implements LocationListener {

    private final Context mContext;

    private static final int PERMISSION_REQUEST_CODE = 1;
    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    boolean canGetLocation = false;

    Location currentLocation; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    public AlertDialog.Builder alertDialog;
    // Declaring a Location Manager
    protected LocationManager locationManager;


    public GPSTracker(Context context) {
        this.mContext = context;


        getLocation();

    }


    public Location getLocation() {

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;

        } else {
            try {
                locationManager = (LocationManager) mContext
                        .getSystemService(LOCATION_SERVICE);

                // getting GPS status
                isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);
                // getting network status
                isNetworkEnabled = locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                if (!isGPSEnabled && !isNetworkEnabled) {
                    this.canGetLocation = false;
                } else {
                    this.canGetLocation = true;
                    if (isNetworkEnabled) {


                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("Network", "Network");
                        if (locationManager != null) {
                            currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (currentLocation != null) {
                                latitude = currentLocation.getLatitude();
                                longitude = currentLocation.getLongitude();
                            }
                        }
                    }
                    // if GPS Enabled get lat/long using GPS Services
                    if (isGPSEnabled) {
                        if (currentLocation == null) {

                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            Log.d("GPS Enabled", "GPS Enabled");
                            if (locationManager != null) {
                                currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (currentLocation != null) {
                                    latitude = currentLocation.getLatitude();
                                    longitude = currentLocation.getLongitude();
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return currentLocation;

        }


    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {

            return true;

        } else {

            return false;

        }
    }


    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */
    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

            }
            locationManager.removeUpdates(GPSTracker.this);
        }
    }



    /**
     * Function to get latitude
     * */
    public double getLatitude()
    {
        if(currentLocation != null)
        {
            latitude = currentLocation.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude()
    {
        if(currentLocation != null)
        {
            longitude = currentLocation.getLongitude();
        }

        // return longitude
        return longitude;
    }
    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    public void showSettingsAlert() {
         alertDialog = new AlertDialog.Builder(mContext);
        // Setting Dialog Title
        alertDialog.setTitle("Configurar ubicación");
        // Setting Dialog Message
        alertDialog.setIcon(R.drawable.ic_action_location_green);
        alertDialog.setMessage("Ubicación no está habilitada ¿Deseas habilitarla?");
        // On pressing Settings button
        alertDialog.setPositiveButton("Configuraciones", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location currentLocation) {
// TODO Auto-generated method stub
        this.currentLocation = currentLocation;
        getLatitude();
        getLongitude();

    }

    @Override
    public void onProviderDisabled(String provider) {
// TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
// TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
// TODO Auto-generated method stub
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}