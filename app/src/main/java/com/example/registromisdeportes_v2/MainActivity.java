package com.example.registromisdeportes_v2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class
MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final int VENGO_GALERIA = 101;
    private static final int PIDO_PERMISO_ESCRITURA = 111;
    private static final int VENGO_CAMARA = 76;
    private static final int CAMBIO_PANTALLA = 24;
    private static final int COD_CAMBIO = 13;
    private static int num_intentos = 3;
    private static final String CLAVE = "CLAVE";
    private static final String NOMBRE_FICHERO = "DATOS";
    private static final String PASSWORD_NAME = "MY_PASSWORD";
    private static final String IMAGE_PATH = "IMAGE_PATH";
    private static final String BTN_BLOCK = "BTN_BLOCK";
    private static final String BD_CREATE = "BD_CREATE";

    Button btnSacarFoto, btnCogerFoto, btnAcceder;
    TextView Contrasenna;
    ImageView FotoPerfil;
    File fichero;
    SensorManager sensorManager;
    MediaPlayer mediaPlayer;

    String[] nombreDeportes = new String[]{};
    String[] descripcionDeportes = new String[]{};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nombreDeportes = new String[]{"Natacion", "Atletismo", "Ciclismo"};
        descripcionDeportes = new String[]{
                "La natación es un deporte que consiste en el desplazamiento de una persona en el agua, sin que esta toque el suelo.",
                "El atletismo es un deporte que agrupa numerosas disciplinas. En este conjunto de prácticas se busca superar al adversario en velocidad o en resistencia.",
                "El ciclismo es un deporte en el que se utiliza una bicicleta para recorrer circuitos al aire libre o en pista cubierta y que engloba diferentes especialidades."
        };

        SharedPreferences MisCredenciales = getSharedPreferences(NOMBRE_FICHERO, MODE_PRIVATE);
        PedirPermisosFoto();

        mediaPlayer = MediaPlayer.create(this, R.raw.nuke_alarm);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_NORMAL);

        btnSacarFoto = findViewById(R.id.buttonSacarFoto);
        btnCogerFoto = findViewById(R.id.buttonCogerFoto);
        btnAcceder = findViewById(R.id.buttonAcceder);
        Contrasenna = findViewById(R.id.editTextTextPassword);
        FotoPerfil = findViewById(R.id.imageViewFotoPerfil);

        String original_pass = MisCredenciales.getString(PASSWORD_NAME, "No Contraseña");
        String photo_path = MisCredenciales.getString(IMAGE_PATH, "No Foto");
        boolean btn_block = MisCredenciales.getBoolean(BTN_BLOCK, false);
        boolean bd_creada = MisCredenciales.getBoolean(BD_CREATE, false);

        if (!bd_creada){
            crearBaseDatos();
        }

        if (original_pass == "No Contraseña"){
            btnAcceder.setText("Registrarte");
        } else {
            btnAcceder.setText("Iniciar Sesion");
        }

        if (photo_path == "No Foto"){
            FotoPerfil.setImageURI(Uri.parse(String.valueOf(R.drawable.nophoto)));
        } else {
            FotoPerfil.setImageURI(Uri.parse(photo_path));
        }

        if (btn_block){
            bloquearBoton();
        }

        btnCogerFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {cargarImagen();}
        });

        btnSacarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {hacerFoto();}
        });

        btnAcceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences MisCredenciales = getSharedPreferences(NOMBRE_FICHERO, MODE_PRIVATE);
                String original_pass = MisCredenciales.getString(PASSWORD_NAME, "No Contraseña");
                String photo_path = MisCredenciales.getString(IMAGE_PATH, "No Foto");

                if (num_intentos > 0){

                    if (original_pass == "No Contraseña"){

                        SharedPreferences.Editor editor = MisCredenciales.edit();
                        editor.putString(PASSWORD_NAME, Contrasenna.getText().toString());
                        Toast.makeText(MainActivity.this, "Contraseña guardada con exito", Toast.LENGTH_SHORT).show();
                        editor.apply();

                    } else {
                        if (original_pass.equals(Contrasenna.getText().toString())){

                            if (photo_path != "No Foto"){
                                Intent intent = new Intent(MainActivity.this, DeportesActivity.class);
                                intent.putExtra(CLAVE, original_pass);
                                startActivityForResult(intent, COD_CAMBIO);
                            } else {
                                Toast.makeText(MainActivity.this, R.string.errorFoto, Toast.LENGTH_SHORT).show();
                                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.traslacion);
                                btnAcceder.startAnimation(animation);
                                num_intentos--;
                            }

                        } else {
                            Toast.makeText(MainActivity.this, R.string.errorClave, Toast.LENGTH_SHORT).show();
                            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.traslacion);
                            btnAcceder.startAnimation(animation);
                            num_intentos--;
                        }
                    }

                } else {
                    Toast.makeText(MainActivity.this, "Numero de intentos alcanzado", Toast.LENGTH_SHORT).show();
                    bloquearBoton();
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }

            }
        });

    }

    private void crearBaseDatos() {
        ManejadorBD manejadorBD = new ManejadorBD(this);
        for (int i = 0; i < nombreDeportes.length; i++){
            manejadorBD.InsertarDeporte(nombreDeportes[i], descripcionDeportes[i]);
        }

        SharedPreferences MisCredenciales = getSharedPreferences(NOMBRE_FICHERO, MODE_PRIVATE);
        SharedPreferences.Editor editor = MisCredenciales.edit();
        editor.putBoolean(BD_CREATE, true);
        editor.apply();
    }

    private void PedirPermisosFoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PIDO_PERMISO_ESCRITURA);
            }
        } else {

        }
    }

    private void hacerFoto() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            fichero = crearFicheroFoto();
        } catch (IOException e) {
            e.printStackTrace();
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, "com.example.registromisdeportes_v2.fileprovider", fichero));

        //if (intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, VENGO_CAMARA);
        /*} else {
            Toast.makeText(this, "Necesitas una camara", Toast.LENGTH_SHORT).show();
        }*/

    }

    private File crearFicheroFoto() throws IOException {
        String FechaYHora = new SimpleDateFormat("yyyyMMdd_HH_mm_ss").format(new Date());
        String NombreFichero = "Foto_" + FechaYHora;
        File CarpetaFotos = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        CarpetaFotos.mkdir();
        File Imagen = File.createTempFile(NombreFichero, ".jpg", CarpetaFotos);
        return Imagen;
    }

    private void cargarImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");
        startActivityForResult(Intent.createChooser(intent, "Seleccione Aplicacion"), VENGO_GALERIA);
    }

    private void bloquearBoton() {
        btnAcceder.setEnabled(false);
        btnAcceder.setBackgroundColor(getResources().getColor(R.color.azul_hint));
        btnAcceder.setTextColor(getResources().getColor(R.color.azul_muy_oscuro));
        SharedPreferences MisCredenciales = getSharedPreferences(NOMBRE_FICHERO, MODE_PRIVATE);
        SharedPreferences.Editor editor = MisCredenciales.edit();
        editor.putBoolean(BTN_BLOCK, true);
        editor.apply();
    }

    private void desbloquearBoton() {
        SharedPreferences MisCredenciales = getSharedPreferences(NOMBRE_FICHERO, MODE_PRIVATE);

        btnAcceder.setEnabled(true);
        btnAcceder.setBackgroundColor(getResources().getColor(R.color.azul_muy_oscuro));
        btnAcceder.setTextColor(getResources().getColor(R.color.white));
        SharedPreferences.Editor editor = MisCredenciales.edit();
        editor.putBoolean(BTN_BLOCK, false);
        editor.apply();
        mediaPlayer.stop();
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        num_intentos = 3;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION){
            //Log.d("senson", "" + sensorEvent.values[1]);
            if (sensorEvent.values[1] >= 175 && sensorEvent.values[1] <= 180){
                desbloquearBoton();
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        SharedPreferences MisCredenciales = getSharedPreferences(NOMBRE_FICHERO, MODE_PRIVATE);
        SharedPreferences.Editor editor = MisCredenciales.edit();

        /*if (resultCode == RESULT_OK && requestCode == VENGO_GALERIA){
            Uri ruta = data.getData();
            editor.putString(IMAGE_PATH, ruta.toString());
            editor.apply();
            FotoPerfil.setImageURI(ruta);
        } else if (requestCode == VENGO_CAMARA){
            if (resultCode == RESULT_OK){
                FotoPerfil.setImageBitmap(BitmapFactory.decodeFile(fichero.getAbsolutePath()));
                editor.putString(IMAGE_PATH, fichero.getAbsolutePath());
                editor.apply();
                actualizarGaleria(fichero.getAbsolutePath());
            }
        } else {
            Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
        }*/

        if (resultCode == RESULT_OK){
            switch (requestCode){

                case VENGO_GALERIA:
                        Uri ruta = data.getData();
                        editor.putString(IMAGE_PATH, ruta.toString());
                        editor.apply();
                        FotoPerfil.setImageURI(ruta);
                    break;
                case VENGO_CAMARA:
                        FotoPerfil.setImageBitmap(BitmapFactory.decodeFile(fichero.getAbsolutePath()));
                        editor.putString(IMAGE_PATH, fichero.getAbsolutePath());
                        editor.apply();
                        actualizarGaleria(fichero.getAbsolutePath());
                    break;
                case COD_CAMBIO:

                    break;
                default:
                    Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
                    break;
            }
        } else {
            Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
        }

    }

    private void actualizarGaleria(String absolutePath) {

        MediaScannerConnection.scanFile(this, new String[]{absolutePath}, null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String s, Uri uri) {
                Log.d("ACTUALIZAR", "Se ha actualizado la galeria");
            }
        });

    }
}