package fly.speedmeter.grub.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import fly.speedmeter.grub.view.start.MainActivity;

public class MyLocation {

    Timer timer1;
    LocationManager lm;
    LocationResult locationResult;
    Context c;
    FragmentActivity f;
    boolean gps_enabled=false;
    boolean network_enabled=false;

    public boolean getLocation(FragmentActivity app, Context context, LocationResult result)
    {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(app, Manifest.permission.ACCESS_FINE_LOCATION)) { // show some info to user why you want this permission
                Toast.makeText(app, "Allow Location Permission to use this functionality.", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(app, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123 /*LOCATION_PERMISSION_REQUEST_CODE*/);
            } else {
                ActivityCompat.requestPermissions(app, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123 /*LOCATION_PERMISSION_REQUEST_CODE*/);

            }
        }
        f= app;
        c = context;
        //I use LocationResult callback class to pass location value from MyLocation to user code.
        locationResult=result;
        if(lm==null)
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        //exceptions will be thrown if provider is not permitted.
        try{gps_enabled=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}
        try{network_enabled=lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}

        //Toast.makeText(context, gps_enabled+" "+network_enabled,     Toast.LENGTH_LONG).show();

        //don't start listeners if no provider is enabled
        if(!gps_enabled && !network_enabled)
            return false;

        if(gps_enabled)
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
        if(network_enabled)
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
        timer1=new Timer();


        timer1.schedule(new GetLastLocation(), 10000);
        //    Toast.makeText(context, " Yaha Tak AAya", Toast.LENGTH_LONG).show();
        return true;
    }

    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            locationResult.gotLocation(location);
            lm.removeUpdates(this);
            lm.removeUpdates(locationListenerNetwork);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            locationResult.gotLocation(location);
            lm.removeUpdates(this);
            lm.removeUpdates(locationListenerGps);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    class GetLastLocation extends TimerTask {
        @Override

        public void run() {
            if (ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(f, Manifest.permission.ACCESS_FINE_LOCATION)) { // show some info to user why you want this permission
                    Toast.makeText(f, "Allow Location Permission to use this functionality.", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(f, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123 /*LOCATION_PERMISSION_REQUEST_CODE*/);
                } else {
                    ActivityCompat.requestPermissions(f, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123 /*LOCATION_PERMISSION_REQUEST_CODE*/);

                }
            }
            //Context context = getClass().getgetApplicationContext();
            Location net_loc=null, gps_loc=null;
            if(gps_enabled)
                gps_loc=lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(network_enabled)
                net_loc=lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            //if there are both values use the latest one
            if(gps_loc!=null && net_loc!=null){
                if(gps_loc.getTime()>net_loc.getTime())
                    locationResult.gotLocation(gps_loc);
                else
                    locationResult.gotLocation(net_loc);
                return;
            }

            if(gps_loc!=null){
                locationResult.gotLocation(gps_loc);
                return;
            }
            if(net_loc!=null){
                locationResult.gotLocation(net_loc);
                return;
            }
            locationResult.gotLocation(null);
        }
    }

    public static abstract class LocationResult{
        public abstract void gotLocation(Location location);
    }
}

