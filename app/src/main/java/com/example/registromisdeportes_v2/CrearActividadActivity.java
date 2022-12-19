package com.example.registromisdeportes_v2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
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
    private static final int HACIA_LISTA = 28;
    private static final int HACIA_OPCIONES = 29;
    private static final String NOMBRE_FICHERO = "DATOS";
    private static final String SONIDO = "SONIDO";
    private static final String ID_CANAL = "Nombre Canal";
    Button btnAgregarActividad, btnOpciones;
    Spinner ListaDeportes;
    TextView txtMinutos;

    ArrayList<DeportesActivity.Deporte> deporte;
    DeportesActivity.Deporte[] ArrayDeportes;

    LocationManager locationManager;
    Location loc;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_actividad);

        mediaPlayer = MediaPlayer.create(this, R.raw.ready_go);

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
        btnOpciones = findViewById(R.id.buttonConfiguracion);
        ListaDeportes = findViewById(R.id.spinnerDeportes);
        txtMinutos = findViewById(R.id.editTextMinutos);

        RellenarLista(manejadorBD, ListaDeportes);
        
        btnAgregarActividad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String duracion = txtMinutos.getText().toString();
                String nom_dep = ListaDeportes.getSelectedItem().toString();
                int id_dep = 0;
                int minutos = 0;

                if(!"".equals(duracion)){

                    try {
                        Cursor cursor = manejadorBD.ListaNomDeportes(nom_dep);
                        if (cursor.moveToFirst()){
                            id_dep = cursor.getInt(0);
                        }
                        minutos = Integer.valueOf(duracion);
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

                    SharedPreferences MisCredenciales = getSharedPreferences(NOMBRE_FICHERO, MODE_PRIVATE);
                    boolean sonido = MisCredenciales.getBoolean(SONIDO, true);
                    if (sonido){
                        mediaPlayer.start();
                    }
                    int segundos = minutos * 60;
                    MiCuentaAtras miCuentaAtras = new MiCuentaAtras(segundos);
                    miCuentaAtras.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    Intent intent = new Intent(CrearActividadActivity.this, ListarActividadesActivity.class);
                    startActivityForResult(intent, HACIA_LISTA);

                } else {
                    Toast.makeText(CrearActividadActivity.this, R.string.Rellenar, Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnOpciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CrearActividadActivity.this, OpcionesActivity.class);
                startActivityForResult(intent, HACIA_OPCIONES);
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

    class MiCuentaAtras extends AsyncTask<String, String,String>{
        int miTiempo;

        public MiCuentaAtras(int tiempo){
            miTiempo = tiempo;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            miTiempo--;
        }

        @Override
        protected void onPostExecute(String s) {

            lanzarNotificacionConFoto();

        }

        @Override
        protected String doInBackground(String... strings) {
            while (miTiempo > 0){
                publishProgress();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    private void lanzarNotificacionConFoto() {

        String idChannnel = "Canal 1";
        String nombreCanal = "Canal con Foto";

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, ID_CANAL);

        builder.setSmallIcon(R.drawable.launcher_icon).
                setContentTitle("Actividad Finalizada").
                setAutoCancel(false).
                setContentText("");

        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        Bitmap bitmapAlbert = BitmapFactory.decodeResource(getResources(),R.drawable.launcher_icon);
        bigPictureStyle.bigPicture(bitmapAlbert);
        bigPictureStyle.setBigContentTitle("");
        bigPictureStyle.setSummaryText("Felicidades acabaste la actividad creada");

        builder.setStyle(bigPictureStyle);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(idChannnel, nombreCanal, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(R.color.azul_muy_oscuro);
            notificationChannel.enableVibration(true);
            notificationChannel.setShowBadge(true);
            builder.setChannelId(idChannnel);
            notificationManager.createNotificationChannel(notificationChannel);

        } else {
            builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
        }
        notificationManager.notify(4, builder.build());
    }

}