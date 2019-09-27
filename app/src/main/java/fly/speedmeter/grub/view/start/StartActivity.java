package fly.speedmeter.grub.view.start;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.ornach.nobobutton.NoboButton;

import java.util.ArrayList;
import java.util.List;

import fly.speedmeter.grub.R;
import fly.speedmeter.grub.model.Bus;
import fly.speedmeter.grub.model.Supir;
import fly.speedmeter.grub.network.response.BusResponse;
import fly.speedmeter.grub.network.response.SebuahBusResponse;
import fly.speedmeter.grub.network.response.SebuahSupirResponse;
import fly.speedmeter.grub.network.response.SupirResponse;
import fly.speedmeter.grub.network.response.UserResponse;
import fly.speedmeter.grub.utils.SessionManager;
import fly.speedmeter.grub.view.BaseActivity;
import fly.speedmeter.grub.view.login.LoginActivity;
import fly.speedmeter.grub.view.settings.Settings;

public class StartActivity extends BaseActivity implements View.OnClickListener, StartView {
    private TextView judul;
    FloatingActionButton logoutButton;
    private Spinner spinnerViewBus, spinnerViewSupir;
    private NoboButton fab;
    private SharedPreferences pref;
    private String namaSupir, platBus;
    private Integer idSupir, idBus;
    ProgressDialog progressDialog;
    SessionManager session;
    StartPresenter presenter;
    private Toolbar toolbar;
    String myPref_type;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkPermission())
            {
                // Code for above or equal 23 API Oriented Device
                // Your Permission granted already .Do next code
            } else {
                requestPermission(); // Code for permission
            }
        }
        else
        {

            // Code for Below 23 API Oriented Device
            // Do next code
        }
        session = new SessionManager(this);
        this.pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        presenter = new StartPresenter(this);
        presenter.getBus(session.getKeyToken());
        presenter.getSupir(session.getKeyToken());
        presenter.getSpecUser(session.getKeyToken());
        judul = findViewById(R.id.judulStart);
        spinnerViewBus = findViewById(R.id.spinnerBusID);
        spinnerViewSupir =  findViewById(R.id.spinnerSupirId);
        findViewById(R.id.fab).setOnClickListener(this);
        logoutButton = findViewById(R.id.logout_button);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");

        spinnerViewBus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedPlat = parent.getItemAtPosition(position).toString();
                presenter.getPlat(session.getKeyToken(), selectedPlat);
                platBus = selectedPlat;
                Toast.makeText(getApplicationContext(), "Bus dengan plat nomor " + selectedPlat, Toast.LENGTH_SHORT).show();



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerViewSupir.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedNama = parent.getItemAtPosition(position).toString();
                presenter.getSupirName(session.getKeyToken(),selectedNama);
                namaSupir = selectedNama;
                Toast.makeText(getApplicationContext(), "Supir dengan nama " + selectedNama, Toast.LENGTH_SHORT).show();



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        logoutButton.setOnClickListener(v -> {
            presenter.logout(session.getKeyToken(),session.getKeyToken());
            session.logoutUser();
        });
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(StartActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(StartActivity.this, "Penggunaan lokasi diperlukan untuk pemantauan lokasi dan kecepatan. Mohon izinkan aplikasi ini mengakses lokasi anda", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(StartActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(StartActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value-permission-loc", "Izin Lokasi Didapatkan.");
                } else {
                    Log.e("value-permission-loc", "Izin lokasi ditolak.");
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                startJalan();
                break;
        }
    }

    private void startJalan() {
        presenter.kirimStatus(
                session.getKeyToken(),
                1,
                idBus,
                idSupir
        );
        saveBusToPrefs(idBus, platBus);
        saveSupirToPrefs(idSupir, namaSupir);
        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void showProgress() {
        progressDialog.show();
    }

    @Override
    public void hideProgress() {
        progressDialog.dismiss();
    }

    @Override
    public void statusSuccessUser(UserResponse userResponse) {
        judul.setText("Hai "+userResponse.getNamaUser()+"!\nSelamat Bertugas ya!");
    }

    private void saveSupirToPrefs(Integer namaSupir, String namaJelasSupir)
    {
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("nama_supir");
        editor.apply(); // commit changes
        editor.putString("nama_supir", String.valueOf(namaSupir));
        editor.remove("nama_jelas_supir");
        editor.apply(); // commit changes
        editor.putString("nama_jelas_supir", namaJelasSupir);

        // Save the changes in SharedPreferences
        editor.apply(); // commit changes
    }

    // Writing data supir to SharedPreferences
    private void saveBusToPrefs(Integer platBus, String nomorJelasPlat)
    {
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("plat_bus");
        editor.apply(); // commit changes
        editor.putString("plat_bus", String.valueOf(platBus));
        editor.remove("nomor_jelas_plat");
        editor.apply(); // commit changes
        editor.putString("nomor_jelas_plat", nomorJelasPlat);

        // Save the changes in SharedPreferences
        editor.apply(); // commit changes
    }

    @Override
    public void statusSuccessSupir(SupirResponse supirResponse) {
        List<String> listSpinner = new ArrayList<String>();
        for(Supir supir : supirResponse.getData()) {
            String memberListSpinner = supir.getNama_supir() ;
            listSpinner.add(memberListSpinner);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, listSpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerViewSupir.setAdapter(adapter);
    }

    @Override
    public void statusSuccessBus(BusResponse busResponse) {
        List<String> listSpinner = new ArrayList<String>();
        for(Bus bus : busResponse.getData()) {
            String memberListSpinner = bus.getPlat_nomer() ;
            listSpinner.add(memberListSpinner);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, listSpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerViewBus.setAdapter(adapter);
    }

    @Override
    public void statusSuccessGetSupir(SebuahSupirResponse supirResponse) {
       idSupir = supirResponse.getSebuahData().getIdSupir();
        Log.d("ID supir ","terpilih: "+idSupir);
    }

    @Override
    public void statusSuccessGetBus(SebuahBusResponse busResponse) {
        idBus = busResponse.getSebuahData().getIdBus();
        Log.d("ID bus ","terpilih: "+idBus);
    }

    @Override
    public void statusSuccess(String message) {
    }
    @Override
    public void statusError(String message) {
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }


    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
