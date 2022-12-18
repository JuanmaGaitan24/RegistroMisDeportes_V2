package com.example.registromisdeportes_v2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.ArrayList;
import java.util.List;

public class CrearActividadActivity extends AppCompatActivity {

    private static final int PIDO_PERMISO_GPS = 27;
    Button btnAgregarActividad;
    Spinner ListaDeportes;
    TextView txtMinutos;

    ArrayList<DeportesActivity.Deporte> deporte;
    DeportesActivity.Deporte[] ArrayDeportes;

    LocationManager locationManager;
    Location loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_actividad);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PIDO_PERMISO_GPS);
            }
        } else {

        }
        loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        ManejadorBD manejadorBD = new ManejadorBD(this);
        
        btnAgregarActividad = findViewById(R.id.buttonAgregarActividad);
        ListaDeportes = findViewById(R.id.spinnerDeportes);
        txtMinutos = findViewById(R.id.editTextMinutos);

        RellenarLista(manejadorBD, ListaDeportes);
        
        btnAgregarActividad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String duracion = txtMinutos.getText().toString();
                String nom_dep = ListaDeportes.getSelectedItem().toString();
                int id_dep = 0;

                if(!"".equals(duracion)){

                    try {
                        Cursor cursor = manejadorBD.ListaNomDeportes(nom_dep);
                        if (cursor.moveToFirst()){
                            id_dep = cursor.getInt(0);
                        }
                        int minutos = Integer.valueOf(duracion);
                        String fecha = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
                        String hora = new SimpleDateFormat("HH:mm:ss").format(new Date());
                        String Latitud = String.valueOf(loc.getLatitude());
                        String Longitud = String.valueOf(loc.getLongitude());

                        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                        Intent batteryStatus = registerReceiver(null, ifilter);

                        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                        float battery = (level / (float)scale)*100;
                        int bateria = Integer.valueOf((int) battery);

                        manejadorBD.InsertarActividad(id_dep, fecha, hora, Latitud, Longitud, minutos, bateria);
                        Toast.makeText(CrearActividadActivity.this, "Insertado con existo", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(CrearActividadActivity.this, R.string.ErrorCrear, Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(CrearActividadActivity.this, R.string.Rellenar, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void RellenarLista(ManejadorBD manejadorBD, Spinner listaDeportes) {
        
        Cursor cursor = manejadorBD.ListarDeportes();
        deporte = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter;
        List<String> list = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0){
            ArrayDeportes = new DeportesActivity.Deporte[cursor.getCount()];
            while (cursor.moveToNext()){
                DeportesActivity.Deporte deporte1 = new DeportesActivity.Deporte(cursor.getString(0), cursor.getString(1), cursor.getString(2));
                /*ArrayDeportes[cont] = deporte1;
                cont++;*/
                String fila = deporte1.getNombre();
                list.add(fila);
                deporte.add(deporte1);
            }

            arrayAdapter = new ArrayAdapter<>(getBaseContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, list);
            listaDeportes.setAdapter(arrayAdapter);

        }
        
    }
}