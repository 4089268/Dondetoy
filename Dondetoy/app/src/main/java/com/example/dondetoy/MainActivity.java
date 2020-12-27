package com.example.dondetoy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int fastUpdateInterval = 5;
    public static final int defaultUpdateIntervar = 20;
    private static final int PERMISSIONS_FINE_LOCATION = 99;

    TextView tv_latitud, tv_longitud, tv_altitud, tv_precision,tv_tipoGps,tv_activo,tv_totalCoordenadas;
    Switch sw_ahorroEnergia,sw_activarGps;
    ListView lv_listaCoordenadas;

    FusedLocationProviderClient fuseLocationProviderC;

    boolean actualizaciones = false;

    //Location Request, archivo con las configuraciones para FudesLocationProviderClient
    LocationRequest locRequest;
    LocationCallback locationCallback;
    List<Location> myLocations;
    ListLocAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_latitud = findViewById(R.id.tv_latitud);
        tv_longitud = findViewById(R.id.tv_longitud);
        tv_altitud = findViewById(R.id.tv_altitud);
        tv_precision = findViewById(R.id.tv_precision);
        tv_tipoGps = findViewById(R.id.tv_tipoGps);
        tv_activo = findViewById(R.id.tv_activo);
        tv_totalCoordenadas = findViewById(R.id.tv_totalCoordenadas);
        sw_ahorroEnergia = findViewById(R.id.sw_ahorroEnergia);
        sw_activarGps = findViewById(R.id.sw_activarGps);

        myLocations = new ArrayList<Location>();
        lv_listaCoordenadas = findViewById(R.id.lv_coordenadas);
        myAdapter = new ListLocAdapter(MainActivity.this, myLocations);
        lv_listaCoordenadas.setAdapter(myAdapter);



        //Iniciarlizar configuracion de FuseLocationProvider
        locRequest = new LocationRequest();
        locRequest.setInterval(1000 * defaultUpdateIntervar);
        locRequest.setFastestInterval(1000 * fastUpdateInterval);
        locRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        //Evento que se lanza cada vez que se optiene una coordenada
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                //Si hay locations mostrar en pantalla.
                if(locationResult.getLocations().size() > 0){
                    myLocations.add(locationResult.getLastLocation());
                    String tmps="Lat: " + String.valueOf(locationResult.getLastLocation().getLatitude()) + "  Long: " + String.valueOf(locationResult.getLastLocation().getLongitude());
                    Toast.makeText(MainActivity.this,"Loop",Toast.LENGTH_SHORT).show();
                    UpdateUI(locationResult.getLastLocation());
                }
            }
        };

        //Comprobar permisos de GPS
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){


        }else{
            //Pedir permisos para usar GPS
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }

        //Crear los listener de los eventos de los controles
        sw_activarGps.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(sw_activarGps.isChecked()){
                    StartLocationsUpdates();
                }else{
                    StopLocationsUpdates();
                }
            }
        });
        sw_ahorroEnergia.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if( sw_ahorroEnergia.isChecked()){
                    locRequest.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tv_tipoGps.setText("Usando GPS Sensor");
                }else{
                    locRequest.setPriority( LocationRequest.PRIORITY_LOW_POWER);
                    tv_tipoGps.setText("Usando Torres Celulares o WIFI");
                }
            }
        });

        GetLastLocation();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch ( requestCode){
            case PERMISSIONS_FINE_LOCATION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    GetLastLocation();
                }else{
                    Toast.makeText(this,"Esta Aplicacion necesita permisos de GPS para funcionar",Toast.LENGTH_SHORT).show();
                    //finish();
                }
        }
    }


    private void StartLocationsUpdates(){
        Toast.makeText(this,"Iniciando Servicio",Toast.LENGTH_SHORT).show();

        //Comprobar si hay permisos de LOCALICACION
        if(ActivityCompat.checkSelfPermission(MainActivity.this , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            tv_activo.setText("SERVICIO ACTIVO");
            tv_latitud.setText("");
            tv_longitud.setText("");
            tv_altitud.setText("");
            tv_precision.setText("");

            fuseLocationProviderC.requestLocationUpdates(locRequest,locationCallback,null);
        }else{
            sw_activarGps.setChecked(false);

            //Pedir permisos para usar GPS
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }

    }
    private void StopLocationsUpdates(){
        Toast.makeText(MainActivity.this ,"Deteniendo Servicio",Toast.LENGTH_SHORT).show();
        myLocations.clear();
        tv_activo.setText("SERVICIO DETENIDO");
        tv_latitud.setText("");
        tv_longitud.setText("");
        tv_altitud.setText("");
        tv_precision.setText("");
        tv_totalCoordenadas.setText("Total coordenadas: 0");
        fuseLocationProviderC.removeLocationUpdates(locationCallback);
    }


    private void GetLastLocation() {
        //Obtener la ultima coordenada del GPS
        fuseLocationProviderC = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            //Obtener las coordenadas
            fuseLocationProviderC.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location == null ){
                          Toast.makeText(MainActivity.this ,"GPS Esta descativado",Toast.LENGTH_SHORT).show();
                    }else{
                        UpdateUI(location);
                    }
                }
            });

        }else{
            //Pedir permisos para usar GPS
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
    }
    private void UpdateUI(Location loc) {
        tv_latitud.setText(String.valueOf(loc.getLatitude()) );
        tv_longitud.setText(String.valueOf(loc.getLongitude()) );

        if (loc.hasAltitude() ){
            tv_altitud.setText(String.valueOf(loc.getAltitude()) );
        }else{
            tv_altitud.setText("NO DISPONIBLE");
        }

        if(loc.hasAccuracy()){
            tv_precision.setText(String.valueOf(loc.getAccuracy()));
        }else{
            tv_precision.setText("NO DISPONIBLE");
        }

        // Mostrar total de coordenadas obtenidas
        try {
            tv_totalCoordenadas.setText("Total coordenadas: " + myLocations.size() );
        }catch(Exception e){
            tv_totalCoordenadas.setText("Total coordenadas: 0");
        }


        // Mostrar las coordenadas en la lista
        myAdapter = new ListLocAdapter(MainActivity.this, myLocations);
        lv_listaCoordenadas.setAdapter(myAdapter);

    }
}