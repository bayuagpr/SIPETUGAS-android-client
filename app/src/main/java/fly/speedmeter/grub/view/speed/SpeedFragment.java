
package fly.speedmeter.grub.view.speed;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.ornach.nobobutton.NoboButton;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fly.speedmeter.grub.services.Data;
import fly.speedmeter.grub.services.GpsServices;
import fly.speedmeter.grub.R;
import fly.speedmeter.grub.utils.PenumpangSharedViewModel;
import fly.speedmeter.grub.utils.SessionManager;
import fly.speedmeter.grub.utils.SpeedSharedViewModel;
import fly.speedmeter.grub.view.start.StartActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class SpeedFragment extends Fragment implements LocationListener, GpsStatus.Listener, SpeedView{

    private SharedPreferences sharedPreferences;
    private LocationManager mLocationManager;
    private static Data data;
    SpeedPresenter presenter;
    SessionManager session;

    private RadioButton radioButton;
    int id, barang_id, nama, harga, jumlah_harga, jumlah_barang;

    private NoboButton fab;
    private NoboButton refresh, darurat;
    private ProgressBarCircularIndeterminate progressBarCircularIndeterminate;
    private TextView satellite;
    private TextView status;
    private TextView accuracy;
    private TextView currentSpeed;
    private TextView maxSpeed;
    private TextView averageSpeed;
    private TextView distance;
    private TextView judulKecepatan;
    private Chronometer time;
    private Data.onGpsServiceUpdate onGpsServiceUpdate;
    final MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
    private boolean firstfix;
    private SpeedSharedViewModel speedSharedViewModel;
    MqttAndroidClient mqttAndroidClient;
    String messageMqtt;
    Boolean statusJalan,statusBerhenti;
    final String serverUri = "ws://broker.shiftr.io:80";

    String clientId = MqttClient.generateClientId();
    final String publishTopic = "lintasdisiplin/sipetugas/";
    final String username = "f4fa07d5";
    final String password = "89d8ea01dd465f2f";

    public SpeedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        session = new SessionManager(getActivity());
        View v = inflater.inflate(R.layout.fragment_speed, container, false);
        data = new Data(onGpsServiceUpdate);
        statusBerhenti = true;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getActivity()).getApplicationContext());
        speedSharedViewModel = ViewModelProviders.of(getActivity()).get(SpeedSharedViewModel.class);
        darurat = v.findViewById(R.id.darurat);
        fab =  v.findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);

        refresh =  v.findViewById(R.id.refresh);
        refresh.setVisibility(View.INVISIBLE);

        session = new SessionManager(getActivity());

        presenter = new SpeedPresenter(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("jalan");
        editor.apply(); // commit changes
        editor.putString("jalan", "belum");

        mqttAndroidClient = new MqttAndroidClient(getActivity().getApplicationContext(), serverUri, clientId);
        final MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(false);
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());
        try {
            IMqttToken token = mqttAndroidClient.connect(mqttConnectOptions);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d("kon", "Terkoneksi");
                    //Toast.makeText(getActivity(), "Mqtt connected", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("diskon", "gagal terkoneksi");
                    Log.i("MQTT", "Client connection failed: " + exception.getMessage());
                    //Toast.makeText(getActivity(), "Mqtt failed to connect:"+exception.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
        onGpsServiceUpdate = new Data.onGpsServiceUpdate() {
            @Override
            public void update() {
                double maxSpeedTemp = data.getMaxSpeed();
                double distanceTemp = data.getDistance();
                double averageTemp;

                if (sharedPreferences.getBoolean("auto_average", false)){
                    averageTemp = data.getAverageSpeedMotion();
                }else{
                    averageTemp = data.getAverageSpeed();
                }

                String speedUnits;
                String distanceUnits;
                if (sharedPreferences.getBoolean("miles_per_hour", false)) {
                    maxSpeedTemp *= 0.62137119;
                    distanceTemp = distanceTemp / 1000.0 * 0.62137119;
                    averageTemp *= 0.62137119;
                    speedUnits = "mi/jam";
                    distanceUnits = "mi";
                } else {
                    speedUnits = "km/jam";
                    if (distanceTemp <= 1000.0) {
                        distanceUnits = "m";
                    } else {
                        distanceTemp /= 1000.0;
                        distanceUnits = "km";
                    }
                }

                SpannableString s = new SpannableString(String.format("%.0f", maxSpeedTemp) + speedUnits);
                s.setSpan(new RelativeSizeSpan(0.5f), s.length() - 6, s.length(), 0);
                maxSpeed.setText(s);

                s = new SpannableString(String.format("%.0f", averageTemp) + speedUnits);
                s.setSpan(new RelativeSizeSpan(0.5f), s.length() - 6, s.length(), 0);
                averageSpeed.setText(s);

                s = new SpannableString(String.format("%.3f", distanceTemp) + distanceUnits);
                s.setSpan(new RelativeSizeSpan(0.5f), s.length() - 2, s.length(), 0);
                distance.setText(s);
            }
        };

        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        satellite = v.findViewById(R.id.satellite);
        status =  v.findViewById(R.id.status);
        accuracy =  v.findViewById(R.id.accuracy);
        maxSpeed =  v.findViewById(R.id.maxSpeed);
        averageSpeed =  v.findViewById(R.id.averageSpeed);
        distance =  v.findViewById(R.id.distance);
        time =  v.findViewById(R.id.time);
        currentSpeed =  v.findViewById(R.id.currentSpeed);
        judulKecepatan = v.findViewById(R.id.judulKecepatan);
        progressBarCircularIndeterminate =  v.findViewById(R.id.progressBarCircularIndeterminate);

        time.setText("00:00:00");
        time.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            boolean isPair = true;
            @Override
            public void onChronometerTick(Chronometer chrono) {
                long time;
                if(data.isRunning()){
                    time= SystemClock.elapsedRealtime() - chrono.getBase();
                    data.setTime(time);
                }else{
                    time = data.getTime();
                }

                int h   = (int)(time /3600000);
                int m = (int)(time  - h*3600000)/60000;
                int s= (int)(time  - h*3600000 - m*60000)/1000 ;
                String hh = h < 10 ? "0"+h: h+"";
                String mm = m < 10 ? "0"+m: m+"";
                String ss = s < 10 ? "0"+s: s+"";
                chrono.setText(hh+":"+mm+":"+ss);

                if (data.isRunning()){
                    chrono.setText(hh+":"+mm+":"+ss);
                } else {
                    if (isPair) {
                        isPair = false;
                        chrono.setText(hh+":"+mm+":"+ss);
                    }else{
                        isPair = true;
                        chrono.setText("");
                    }
                }

            }
        });
        darurat.setOnClickListener(v13 -> notifyDialog());
        fab.setOnClickListener(v12 -> {
            Log.d("Tombol jalan/berhenti", "diklik");
            if(!statusBerhenti){
                if(!data.isRunning()){
                    data.setRunning(true);
                    fab.setText("Berhenti");
                    time.setBase(SystemClock.elapsedRealtime() - data.getTime());
                    time.start();
                    data.setFirstTime(true);
                    getActivity().startService(new Intent(Objects.requireNonNull(getActivity()).getBaseContext(), GpsServices.class));
                    refresh.setVisibility(View.INVISIBLE);
                    statusJalan = true;
                    speedSharedViewModel.select(statusJalan);
                }else{
                    data.setRunning(false);
                    fab.setText("Jalan");
                    status.setText("");
                    getActivity().stopService(new Intent(Objects.requireNonNull(getActivity()).getBaseContext(), GpsServices.class));
                    refresh.setVisibility(View.VISIBLE);
                    statusJalan = false;
                    speedSharedViewModel.select(statusJalan);
                }
            }else{
//                if(data.isRunning()){
//                statusBerhenti = false;
//            }else{
//                    penumpanDialog();
//            }
                penumpanDialog();
            }

        });
        refresh.setOnClickListener(v1 -> finishDialog());
        PenumpangSharedViewModel penumpangSharedViewModel = ViewModelProviders.of(getActivity()).get(PenumpangSharedViewModel.class);
        penumpangSharedViewModel.getSelected().observe(this, item -> {
            if(item){
            statusBerhenti = false;
            }else{
                statusBerhenti = true;
            }
        });
        return v;
    }



    @Override
    public void onResume() {
        super.onResume();
        firstfix = true;
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) { // show some info to user why you want this permission
                Toast.makeText(getActivity(), "Izinkan penggunaan lokasi dari smartphonemu untuk dapat melanjutkan", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123 /*LOCATION_PERMISSION_REQUEST_CODE*/);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123 /*LOCATION_PERMISSION_REQUEST_CODE*/);

            }
        }
        if (!data.isRunning()){
            Gson gson = new Gson();
            String json = sharedPreferences.getString("data", "");
            data = gson.fromJson(json, Data.class);
        }
        if (data == null){
            data = new Data(onGpsServiceUpdate);
        }else{
            data.setOnGpsServiceUpdate(onGpsServiceUpdate);
        }

        if (mLocationManager.getAllProviders().indexOf(LocationManager.GPS_PROVIDER) >= 0) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);
        } else {
            Log.w("MainActivity", "No GPS location provider found. GPS data display will not be available.");
        }

        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGpsDisabledDialog();
        }

        mLocationManager.addGpsStatusListener(this);
    }

    @Override
    public void statusDaruratSuccess(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        daruratDialog();
    }

    @Override
    public void statusSuccess(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void statusError(String message) {
    }
    @Override
    public void onPause() {
        super.onPause();
        mLocationManager.removeUpdates(this);
        mLocationManager.removeGpsStatusListener(this);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(data);
        prefsEditor.putString("data", json);
        prefsEditor.commit();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        getActivity().stopService(new Intent(Objects.requireNonNull(getActivity()).getBaseContext(), GpsServices.class));
        mqttAndroidClient.unregisterResources();
        mqttAndroidClient.close();
        presenter.detachView();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location.hasAccuracy()) {
            String acc = String.format("%.0f", location.getAccuracy()) + "m";
            if (sharedPreferences.getBoolean("miles_per_hour", false)) {
                acc = String.format(Locale.ENGLISH, "%.0f", location.getAccuracy() * 3.28084) + "ft";
            }
            SpannableString s = new SpannableString(acc);
            s.setSpan(new RelativeSizeSpan(0.75f), s.length()-1, s.length(), 0);
            accuracy.setText(s);

            if (firstfix){
                status.setText("");
                fab.setVisibility(View.VISIBLE);
                if (!data.isRunning() && !maxSpeed.getText().equals("")) {
                    refresh.setVisibility(View.VISIBLE);
                }
                firstfix = false;
            }
        }else{
            firstfix = true;
        }

        if (location.hasSpeed()) {
            progressBarCircularIndeterminate.setVisibility(View.GONE);
            String speed = String.format(Locale.ENGLISH, "%.0f", location.getSpeed() * 3.6) + "km/jam";

            if (sharedPreferences.getBoolean("miles_per_hour", false)) { // Convert to MPH
                speed = String.format(Locale.ENGLISH, "%.0f", location.getSpeed() * 3.6 * 0.62137119) + "mi/jam";

            }

            SpannableString s = new SpannableString(speed);
            s.setSpan(new RelativeSizeSpan(0.25f), s.length()-6, s.length(), 0);
            Pattern p = Pattern.compile("\\d+");
            Matcher m = p.matcher(s.toString());
            while(m.find()) {
                messageMqtt = String.valueOf(Double.parseDouble(m.group()));
                if (isAlreadyConnected())
                {
                    publishMessage(messageMqtt,
                            Integer.parseInt(sharedPreferences.getString("nama_supir",null)));
                }
            }
            if (data.isRunning()) {
                judulKecepatan.setText("Kecepatan bus saat ini");
                currentSpeed.setTextSize(60);
                currentSpeed.setText(s);
            } else {
                judulKecepatan.setText("");
                currentSpeed.setTextSize(30);
                currentSpeed.setText("Bus sedang berhenti");
            }
        }




    }

    public void onGpsStatusChanged (int event) {
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()).getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) { // show some info to user why you want this permission
                Toast.makeText(getActivity(), "Izinkan penggunaan lokasi dari smartphonemu untuk dapat melanjutkan.", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123 /*LOCATION_PERMISSION_REQUEST_CODE*/);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123 /*LOCATION_PERMISSION_REQUEST_CODE*/);

            }
        }
        switch (event) {
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                GpsStatus gpsStatus = mLocationManager.getGpsStatus(null);
                int satsInView = 0;
                int satsUsed = 0;
                Iterable<GpsSatellite> sats = gpsStatus.getSatellites();
                for (GpsSatellite sat : sats) {
                    satsInView++;
                    if (sat.usedInFix()) {
                        satsUsed++;
                    }
                }
                satellite.setText(String.valueOf(satsUsed) + "/" + String.valueOf(satsInView));
                if (satsUsed == 0) {
                    fab.setText("Jalan");
                    data.setRunning(false);
                    status.setText("");
                    getActivity().stopService(new Intent(getActivity().getBaseContext(), GpsServices.class));
                    fab.setVisibility(View.INVISIBLE);
                    refresh.setVisibility(View.INVISIBLE);
                    accuracy.setText("");
                    status.setText(getResources().getString(R.string.waiting_for_fix));
                    firstfix = true;
                }
                break;

            case GpsStatus.GPS_EVENT_STOPPED:
                if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    showGpsDisabledDialog();
                }
                break;
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                break;
        }
    }

    public void showGpsDisabledDialog(){
        Dialog dialog = new Dialog(Objects.requireNonNull(getActivity()).getApplicationContext(), getResources().getString(R.string.gps_disabled), getResources().getString(R.string.please_enable_gps));

        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
            }
        });
        dialog.show();
    }

    public void resetData(){
        fab.setText("Jalan");
        refresh.setVisibility(View.INVISIBLE);
        time.stop();
        maxSpeed.setText("");
        averageSpeed.setText("");
        distance.setText("");
        time.setText("00:00:00");
        data = new Data(onGpsServiceUpdate);
    }
    public static Data getData() {
        return data;
    }
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {}

    @Override
    public void onProviderDisabled(String s) {}
    public boolean isAlreadyConnected() {
        if(mqttAndroidClient != null){
            try{
                boolean result = mqttAndroidClient.isConnected();
                if(result){
                    return true;
                }
                else {
                    return false;
                }
            }
            catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }
        else {
            return false;
        }
    }
    public void publishMessage(final String speed, final Integer supir_id){


        if(data.isRunning())
        {
            MqttMessage message = new MqttMessage();
            message.setPayload(speed.getBytes());
            message.setQos(0);
            try {

                mqttAndroidClient.publish(publishTopic+supir_id, message);
                Log.i("keterangan", "Message published");
                Log.d("topic",""+publishTopic+supir_id);
                Log.d("message",""+message);




            } catch (MqttException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else{
            String messageDefault = String.valueOf(Double.parseDouble("0"));
            MqttMessage messageTemp = new MqttMessage();
            messageTemp.setPayload(messageDefault.getBytes());
            messageTemp.setQos(0);
            try {
                mqttAndroidClient.publish(publishTopic+supir_id, messageTemp);
                Log.i("keterangan", "Message published");
                Log.d("topic",""+publishTopic+supir_id);
                Log.d("message",""+messageTemp);




            } catch (MqttException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


    }

    @Override
    public Context getContext(){
        return Objects.requireNonNull(getActivity()).getApplicationContext();
    }

    public void daruratDialog(){

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Kami akan segera menghubungi anda");
        // this is set the view from XML inside AlertDialog
        alert.setMessage("Harap tetap tenang dan jangan panik");
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    public void penumpanDialog(){

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Data penumpang belum dikirim");
        // this is set the view from XML inside AlertDialog
        alert.setMessage("Harap mengirim data penumpang terlebih dahulu " +
                "walaupun tidak terjadi naik dan turun penumpang");
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    public void finishDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Sudah selesaikah perjalanannya?");
        // this is set the view from XML inside AlertDialog
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(true);
        alert.setPositiveButton("Sudah", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    IMqttToken disconToken = mqttAndroidClient.disconnect();
                    //disconToken.waitForCompletion();
                    disconToken.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // we are now successfully disconnected
                            Log.d("diskon", "Success disconnected");
                           // Toast.makeText(getActivity(), "Mqtt disconnected", Toast.LENGTH_SHORT).show();


                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken,
                                              Throwable exception) {
                            Log.d("fail", "failed disconnected");
                           // Toast.makeText(getActivity(), "failed disconnected: "+exception.getMessage(), Toast.LENGTH_SHORT).show();
                            // something went wrong, but probably we are disconnected anyway
                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                presenter.kirimStatus(
                        session.getKeyToken(),
                        0,
                        Integer.parseInt(sharedPreferences.getString("plat_bus",null)),
                        Integer.parseInt(sharedPreferences.getString("nama_supir",null))
                );
                resetData();
                getActivity().stopService(new Intent(Objects.requireNonNull(getActivity()).getBaseContext(), GpsServices.class));
                Intent intent = new Intent(getActivity().getApplicationContext(), StartActivity.class);
                startActivity(intent);
            }
        });
        alert.setNegativeButton("Belum", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    public void notifyDialog(){
        final LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_custom_dialog, null);
        final EditText message = alertLayout.findViewById(R.id.shipper_field);
        final RadioGroup kategori = alertLayout.findViewById(R.id.rbgrup);
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Laporkan keadaan darurat!");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(true);
        alert.setPositiveButton("Kirim", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Integer check = kategori.getCheckedRadioButtonId();
                View v = inflater.inflate(R.layout.layout_custom_dialog,null);
                radioButton = v.findViewById(check);
                String kategori = (String) radioButton.getText();
                String keterangan = message.getText().toString();
                presenter.kirimDarurat(session.getKeyToken(),kategori,keterangan,sharedPreferences.getString("lokasi",null),
                        Integer.parseInt(sharedPreferences.getString("plat_bus",null)),
                        Integer.parseInt(sharedPreferences.getString("nama_supir",null)));
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }
}
