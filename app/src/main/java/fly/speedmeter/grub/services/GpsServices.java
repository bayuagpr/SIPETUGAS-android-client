package fly.speedmeter.grub.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.preference.PreferenceManager;
import android.widget.Toast;

import fly.speedmeter.grub.R;
import fly.speedmeter.grub.view.speed.SpeedFragment;
import fly.speedmeter.grub.view.start.MainActivity;

public class GpsServices extends Service implements LocationListener, GpsStatus.Listener {
    private LocationManager mLocationManager;

    Location lastlocation = new Location("last");
    Data data;

    double currentLon = 0;
    double currentLat = 0;
    double lastLon = 0;
    double lastLat = 0;

    PendingIntent contentIntent;


    @Override
    public void onCreate() {

        Intent notificationIntent = new Intent(this, SpeedFragment.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        contentIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, 0);

        updateNotification(false);

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(new MainActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) { // show some info to user why you want this permission
                Toast.makeText(this, "Allow Location Permission to use this functionality.", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(new MainActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123 /*LOCATION_PERMISSION_REQUEST_CODE*/);
            } else {
                ActivityCompat.requestPermissions(new MainActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123 /*LOCATION_PERMISSION_REQUEST_CODE*/);

            }
        }
        mLocationManager.addGpsStatusListener(this);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);
        Context ctx = getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public void onLocationChanged(Location location) {
        data = SpeedFragment.getData();
        if (data.isRunning()){
            currentLat = location.getLatitude();
            currentLon = location.getLongitude();

            if (data.isFirstTime()){
                lastLat = currentLat;
                lastLon = currentLon;
                data.setFirstTime(false);
            }

            lastlocation.setLatitude(lastLat);
            lastlocation.setLongitude(lastLon);
            double distance = lastlocation.distanceTo(location);

            if (location.getAccuracy() < distance){
                data.addDistance(distance);

                lastLat = currentLat;
                lastLon = currentLon;
            }

            if (location.hasSpeed()) {
                data.setCurSpeed(location.getSpeed() * 3.6);
                if(location.getSpeed() == 0){
                    new isStillStopped().execute();
                }
            }
            data.update();
            updateNotification(true);
        }
    }

    @SuppressLint("StringFormatMatches")
    @NonNull
    @TargetApi(26)
    public void updateNotification(boolean asData){
        String NOTIFICATION_CHANNEL_ID = "fly.speedmeter.grub";
        String channelName = "SIPETUGAS Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_LOW);
        // Sets whether notifications posted to this channel should display notification lights
        chan.enableLights(true);
        // Sets whether notification posted to this channel should vibrate.
        chan.enableVibration(true);
        // Sets the notification light color for notifications posted to this channel
        chan.setLightColor(Color.GREEN);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);
        Notification.Builder builder = new Notification.Builder(getBaseContext(),NOTIFICATION_CHANNEL_ID)
                .setContentTitle(getString(R.string.running))
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(contentIntent);

        if(asData){
            builder.setContentText(String.format(getString(R.string.notification), data.getMaxSpeed(), data.getDistance()));
        }else{
            builder.setContentText(String.format(getString(R.string.notification), '-', '-'));
        }
        Notification notification = builder.build();
        startForeground(R.string.noti_id, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }   
       
    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }
   
    /* Remove the locationlistener updates when Services is stopped */
    @Override
    public void onDestroy() {
        mLocationManager.removeUpdates(this);
        mLocationManager.removeGpsStatusListener(this);
        stopForeground(true);
    }

    @Override
    public void onGpsStatusChanged(int event) {}

    @Override
    public void onProviderDisabled(String provider) {}
   
    @Override
    public void onProviderEnabled(String provider) {}
   
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    class isStillStopped extends AsyncTask<Void, Integer, String> {
        int timer = 0;
        @Override
        protected String doInBackground(Void... unused) {
            try {
                while (data.getCurSpeed() == 0) {
                    Thread.sleep(1000);
                    timer++;
                }
            } catch (InterruptedException t) {
                return ("The sleep operation failed");
            }
            return ("return object when task is finished");
        }

        @Override
        protected void onPostExecute(String message) {
            data.setTimeStopped(timer);
        }
    }
}
