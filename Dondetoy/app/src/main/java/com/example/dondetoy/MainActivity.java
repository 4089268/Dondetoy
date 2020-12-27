package com.example.dondetoy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;

public class MainActivity extends AppCompatActivity {

    public static final int fastUpdateInterval = 5;
    public static final int defaultUpdateIntervar = 30;

    TextView tv_latitud, tv_longitud, tv_altitud, tv_precision,tv_tipoGps;
    Switch sw_ahorroEnergia,sw_activarGps;

    FusedLocationProviderClient fuseLocationProviderC;

    boolean actualizaciones = false;

    //Location Request, archivo con las configuraciones para FudesLocationProviderClient
    LocationRequest locRequest;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_latitud = findViewById(R.id.tv_latitud);
        tv_longitud = findViewById(R.id.tv_longitud);
        tv_altitud = findViewById(R.id.tv_altitud);
        tv_precision = findViewById(R.id.tv_precision);
        tv_tipoGps = findViewById(R.id.tv_tipoGps);
        sw_ahorroEnergia = findViewById(R.id.sw_ahorroEnergia);
        sw_activarGps = findViewById(R.id.sw_activarGps);

        
        //Iniciarlizar configuracion de FuseLocationProvider
        locRequest = new LocationRequest();
        locRequest.setInterval(1000 * defaultUpdateIntervar);
        locRequest.setFastestInterval(1000 * fastUpdateInterval);
        locRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }
}