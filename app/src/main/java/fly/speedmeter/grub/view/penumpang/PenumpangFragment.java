package fly.speedmeter.grub.view.penumpang;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ornach.nobobutton.NoboButton;

import java.util.Objects;

import fly.speedmeter.grub.R;
import fly.speedmeter.grub.utils.MyLocation;
import fly.speedmeter.grub.utils.PenumpangSharedViewModel;
import fly.speedmeter.grub.utils.SessionManager;
import fly.speedmeter.grub.utils.SpeedSharedViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class PenumpangFragment extends Fragment implements PenumpangView {
    LinearLayout notReady;
    ScrollView readyView;
    FrameLayout buttonPlusAnak,buttonPlusDewasa,buttonPlusTua,buttonMinAnak,buttonMinDewasa,buttonMinTua;
    TextView jumlahPenumpang, jumlahAnak, jumlahDewasa, jumlahTua;
    NoboButton kirimPenumpang;
    int counterAnak, counterNambahAnak, counterKurangAnak, counterDewasa, counterNambahDewasa, counterKurangDewasa,counterTua,counterKurangTua,counterNambahTua, counterTotal;
    PenumpangPresenter presenter;
    SessionManager session;
    private SharedPreferences sharedPreferences;
    private PenumpangSharedViewModel penumpangSharedViewModel;
    MyLocation myLocation = new MyLocation();
    Boolean statusPenumpang;

    public PenumpangFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        session = new SessionManager(getActivity());
        presenter = new PenumpangPresenter(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getActivity()).getApplicationContext());
        penumpangSharedViewModel = ViewModelProviders.of(getActivity()).get(PenumpangSharedViewModel.class);
        View v =  inflater.inflate(R.layout.fragment_penumpang, container, false);
        buttonMinAnak = v.findViewById(R.id.btnMinAnk);
        buttonMinDewasa = v.findViewById(R.id.btnMinDws);
        buttonMinTua = v.findViewById(R.id.btnMinTua);
        buttonPlusAnak = v.findViewById(R.id.btnPlusAnk);
        buttonPlusDewasa = v.findViewById(R.id.btnPlusDws);
        buttonPlusTua = v.findViewById(R.id.btnPlusTua);
        jumlahPenumpang = v.findViewById(R.id.currentPenumpang);
        jumlahAnak = v.findViewById(R.id.sumAnak);
        jumlahDewasa = v.findViewById(R.id.sumDewasa);
        jumlahTua = v.findViewById(R.id.sumTua);
        kirimPenumpang = v.findViewById(R.id.kirimPenumpang);
        notReady = v.findViewById(R.id.notReadyView);
        readyView = v.findViewById(R.id.scrollView);
        notReady.setVisibility(View.GONE);
        counterAnak = 0;
        counterDewasa = 0;
        counterTua = 0;
        myLocation.getLocation(getActivity(),getActivity().getApplicationContext(), locationResult);

        boolean r = myLocation.getLocation(getActivity(), getActivity().getApplicationContext(),
                locationResult);

        SpeedSharedViewModel speedSharedViewModel = ViewModelProviders.of(getActivity()).get(SpeedSharedViewModel.class);
        speedSharedViewModel.getSelected().observe(this, item -> {
            if(item){
                notReady.setVisibility(View.VISIBLE);
                readyView.setVisibility(View.GONE);
                statusPenumpang = false;
                penumpangSharedViewModel.select(statusPenumpang);
            }else{
                notReady.setVisibility(View.GONE);
                readyView.setVisibility(View.VISIBLE);
            }
        });

//kurangi penumpang anak
        buttonMinAnak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counterAnak--;
                if (counterAnak < 0) {
                    jumlahAnak.setText(Integer.toString(0));
                    counterAnak = 0;
                } else {
                    jumlahAnak.setText(Integer.toString(counterAnak));
                    counterKurangAnak++;
                }
                counterTotal = Integer.valueOf(jumlahAnak.getText().toString()) + Integer.valueOf(jumlahDewasa.getText().toString()) + Integer.valueOf(jumlahTua.getText().toString());
                jumlahPenumpang.setText(Integer.toString(counterTotal));

            }
        });

        //kurangi penumpang dewasa
        buttonMinDewasa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counterDewasa--;
                if (counterDewasa < 0) {
                    jumlahDewasa.setText((Integer.toString(0)));
                    counterDewasa = 0;
                } else {
                    jumlahDewasa.setText((Integer.toString(counterDewasa)));
                    counterKurangDewasa++;
                }
                counterTotal = Integer.valueOf(jumlahAnak.getText().toString()) + Integer.valueOf(jumlahDewasa.getText().toString()) + Integer.valueOf(jumlahTua.getText().toString());
                jumlahPenumpang.setText(Integer.toString(counterTotal));

            }
        });
        //kurangi penumpang manula
        buttonMinTua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counterTua--;
                if (counterTua < 0) {
                    jumlahTua.setText((Integer.toString(0)));
                    counterTua = 0;
                } else {
                    jumlahTua.setText((Integer.toString(counterTua)));
                    counterKurangTua++;
                }
                counterTotal = Integer.valueOf(jumlahAnak.getText().toString()) + Integer.valueOf(jumlahDewasa.getText().toString()) + Integer.valueOf(jumlahTua.getText().toString());
                jumlahPenumpang.setText(Integer.toString(counterTotal));

            }
        });

        //tambahi penumpang anak
        buttonPlusAnak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counterAnak++;
                if (counterAnak < 0) {
                    jumlahAnak.setText(Integer.toString(0));
                    counterAnak = 0;
                } else {
                    jumlahAnak.setText(Integer.toString(counterAnak));
                }
                counterTotal = Integer.valueOf(jumlahAnak.getText().toString()) + Integer.valueOf(jumlahDewasa.getText().toString()) + Integer.valueOf(jumlahTua.getText().toString());
                jumlahPenumpang.setText(Integer.toString(counterTotal));
                counterNambahAnak++;
            }
        });

        //tambahi penumpang dewasa
        buttonPlusDewasa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counterDewasa++;
                if (counterDewasa < 0) {
                    jumlahDewasa.setText((Integer.toString(0)));
                    counterDewasa = 0;
                } else {
                    jumlahDewasa.setText((Integer.toString(counterDewasa)));
                }
                counterTotal = Integer.valueOf(jumlahAnak.getText().toString()) + Integer.valueOf(jumlahDewasa.getText().toString()) + Integer.valueOf(jumlahTua.getText().toString());
                jumlahPenumpang.setText(Integer.toString(counterTotal));
                counterNambahDewasa++;
            }
        });
        //tambahi penumpang manula
        buttonPlusTua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counterTua++;
                if (counterTua < 0) {
                    jumlahTua.setText((Integer.toString(0)));
                    counterTua = 0;
                } else {
                    jumlahTua.setText((Integer.toString(counterTua)));
                }
                counterTotal = Integer.valueOf(jumlahAnak.getText().toString()) + Integer.valueOf(jumlahDewasa.getText().toString()) + Integer.valueOf(jumlahTua.getText().toString());
                jumlahPenumpang.setText(Integer.toString(counterTotal));
                counterNambahTua++;
            }
        });
        kirimPenumpang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.kirimPenumpang(
                        session.getKeyToken(),
                        counterNambahAnak,
                        counterNambahDewasa,
                        counterNambahTua,
                        counterKurangAnak,
                        counterKurangDewasa,
                        counterKurangTua,
                        sharedPreferences.getString("lokasi",null),
                        counterTotal,
                        Integer.parseInt(sharedPreferences.getString("plat_bus",null)),
                        Integer.parseInt(sharedPreferences.getString("nama_supir",null))
                        );
                statusPenumpang = true;
                penumpangSharedViewModel.select(statusPenumpang);
            }
        });
       return v;
    }
    @Override
    public void statusSuccess(String message) {
        notifyDialog();
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Context getContext(){
        return Objects.requireNonNull(getActivity()).getApplicationContext();
    }

    @Override
    public void statusError(String message) {
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        presenter.detachView();
    }

    public MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {

        @Override
        public void gotLocation(Location location) {
            // TODO Auto-generated method stub
            double Longitude = location.getLongitude();
            double Latitude = location.getLatitude();
            Log.d("Latitude","titik: "+Latitude);
            Log.d("Longitude","titik: "+Longitude);


            try {
                SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
                prefsEditor.putString("lokasi", Latitude+","+Longitude);
                prefsEditor.apply();
                Log.d("Lokasi",sharedPreferences.getString("lokasi",null));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };
    public void notifyDialog(){

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Data penumpang telah terkirim!");
        // this is set the view from XML inside AlertDialog
        alert.setMessage("Terima Kasih!\nJangan patah semangat yuk kerjanya!");
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
}
