package com.example.registromisdeportes_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class OpcionesActivity extends AppCompatActivity {

    private static final String NOMBRE_FICHERO = "DATOS";
    private static final String SONIDO = "SONIDO";

    Button btnSonido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opciones);

        btnSonido = findViewById(R.id.buttonSonido);

        SharedPreferences MisCredenciales = getSharedPreferences(NOMBRE_FICHERO, MODE_PRIVATE);
        boolean sonido = MisCredenciales.getBoolean(SONIDO, true);

        btnSonido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean sonido = MisCredenciales.getBoolean(SONIDO, true);
                SharedPreferences.Editor editor = MisCredenciales.edit();
                if (sonido){
                    Toast.makeText(OpcionesActivity.this, R.string.OffSonido, Toast.LENGTH_SHORT).show();
                    editor.putBoolean(SONIDO, false);
                } else {
                    Toast.makeText(OpcionesActivity.this, R.string.OnSonido, Toast.LENGTH_SHORT).show();
                    editor.putBoolean(SONIDO, true);
                }
                editor.apply();
            }
        });

    }
}