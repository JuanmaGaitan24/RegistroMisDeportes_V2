package com.example.registromisdeportes_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class DeportesActivity extends AppCompatActivity {

    public static final String NUMERO = "NUMERO";
    ListView listaDeportes;
    ArrayList<Deporte> deporte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deportes);

        ManejadorBD manejadorBD = new ManejadorBD(this);
        listaDeportes = findViewById(R.id.ListViewDeportes);

        Cursor cursor = manejadorBD.ListarDeportes();
        deporte = new ArrayList<>();
        ArrayAdapter <String> arrayAdapter;
        List<String> list = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0){
            while (cursor.moveToNext()){
                Deporte deporte1 = new Deporte(cursor.getString(0), cursor.getString(1), cursor.getString(2));
                String fila = deporte1.toString();
                list.add(fila);
                deporte.add(deporte1);
            }

            arrayAdapter = new ArrayAdapter<>(getBaseContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, list);
            listaDeportes.setAdapter(arrayAdapter);

        }
    }

    class Deporte{
        String id, nombre, descripcion;

        public Deporte(String id, String nombre, String descripcion){
            this.id = id;
            this.nombre = nombre;
            this.descripcion = descripcion;
        }

        @Override
        public String toString() {
            return "Deporte{" +
                   "id=" + id + '\'' +
                   ", nombre=" + nombre + '\'' +
                   ", descripcion=" + descripcion + '\'' +
                   '}';
        }

        public String getId() {return id;}

        public void setId(String id) {this.id = id;}

        public String getNombre() {return nombre;}

        public void setNombre(String nombre) {this.nombre = nombre;}

        public String getDescripcion() {return descripcion;}

        public void setDescripcion(String descripcion) {this.descripcion = descripcion;}
    }
}