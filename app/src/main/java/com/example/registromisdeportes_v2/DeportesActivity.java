package com.example.registromisdeportes_v2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DeportesActivity extends AppCompatActivity {

    public static final String NUMERO = "NUMERO";
    ListView listaDeportes;
    Button btnAgregarDeporte;

    ArrayList<Deporte> deporte;
    Deporte[] ArrayDeportes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deportes);

        ManejadorBD manejadorBD = new ManejadorBD(this);
        listaDeportes = findViewById(R.id.ListViewDeportes);
        btnAgregarDeporte = findViewById(R.id.buttonAgregar);

        ListarDeportes(manejadorBD, listaDeportes);

        btnAgregarDeporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



            }
        });

    }

    private void ListarDeportes(ManejadorBD manejadorBD, ListView listaDeportes) {
        Cursor cursor = manejadorBD.ListarDeportes();
        deporte = new ArrayList<>();
        ArrayAdapter <String> arrayAdapter;
        List<String> list = new ArrayList<>();
        int cont = 0;

        if (cursor != null && cursor.getCount() > 0){
            ArrayDeportes = new Deporte[cursor.getCount()];
            while (cursor.moveToNext()){
                Deporte deporte1 = new Deporte(cursor.getString(0), cursor.getString(1), cursor.getString(2));
                ArrayDeportes[cont] = deporte1;
                cont++;
                /*String fila = deporte1.toString();
                list.add(fila);
                deporte.add(deporte1);*/
            }

            /*arrayAdapter = new ArrayAdapter<>(getBaseContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, list);
            listaDeportes.setAdapter(arrayAdapter);*/

            AdaptadorParaDeportes adaptadorParaDeportes = new AdaptadorParaDeportes(DeportesActivity.this, R.layout.lista, ArrayDeportes);
            listaDeportes.setAdapter(adaptadorParaDeportes);
        }
    }

    private class AdaptadorParaDeportes extends ArrayAdapter<Deporte>{

        public AdaptadorParaDeportes(@NonNull Context context, int resource, @NonNull Deporte[] objects){
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return rellenarFila(position, convertView, parent);
        }

        private View rellenarFila(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View mifila = inflater.inflate(R.layout.lista, parent, false);

            TextView id_dep = mifila.findViewById(R.id.textViewIdDeporte);
            TextView nom_dep = mifila.findViewById(R.id.textViewNombreDeporte);
            TextView des_dep = mifila.findViewById(R.id.textViewDescripcionDeporte);

            id_dep.setText(ArrayDeportes[position].getId());
            nom_dep.setText(ArrayDeportes[position].getNombre());
            des_dep.setText(ArrayDeportes[position].getDescripcion());

            return mifila;
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