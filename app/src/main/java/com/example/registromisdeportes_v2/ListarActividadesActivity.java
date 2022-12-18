package com.example.registromisdeportes_v2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ListarActividadesActivity extends AppCompatActivity {

    private static final int COD_CAMBIO = 75;
    private static final int VER_MAPA = 11;
    Button btnCrearActividad;
    ListView ListaArctividades;
    ArrayList<Actividad> actividad;
    Actividad[] ArrayActividad;

    int cont = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_actividades);

        btnCrearActividad = findViewById(R.id.buttonCrearActividad);
        ListaArctividades = findViewById(R.id.listaActividades);

        ManejadorBD manejadorBD = new ManejadorBD(this);
        ListarActividades(manejadorBD, ListaArctividades);

        btnCrearActividad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ListarActividadesActivity.this, CrearActividadActivity.class);
                startActivityForResult(intent, COD_CAMBIO);

            }
        });
    }

    private void ListarActividades(ManejadorBD manejadorBD, ListView listaArctividades) {

        Cursor cursor = manejadorBD.ListarActividad();
        actividad = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter;
        List<String> list = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0){
            ArrayActividad = new Actividad[cursor.getCount()];
            while (cursor.moveToNext()){
                Actividad actividad1 = new Actividad(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getInt(6), cursor.getInt(7));
                ArrayActividad[cont] = actividad1;
                cont++;
                actividad.add(actividad1);
            }

            AdaptadorParaActividades adaptadorParaActividades = new AdaptadorParaActividades(ListarActividadesActivity.this, R.layout.lista_actividades, ArrayActividad);
            ListaArctividades.setAdapter(adaptadorParaActividades);

        }

    }

    private class AdaptadorParaActividades extends ArrayAdapter<Actividad>{

        public AdaptadorParaActividades(@NonNull Context context, int resource, @NonNull Actividad[] objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return rellenarFila(position, convertView, parent);
        }

        private View rellenarFila(int position, View convertView, ViewGroup parent){

            LayoutInflater inflater = getLayoutInflater();
            View mifila = inflater.inflate(R.layout.lista_actividades, parent, false);

            TextView id_act = mifila.findViewById(R.id.textViewIdActividad);
            TextView id_dep = mifila.findViewById(R.id.textViewNombreActividad);
            TextView fecha = mifila.findViewById(R.id.textViewFecha);
            TextView hora = mifila.findViewById(R.id.textViewHora);
            TextView latitud = mifila.findViewById(R.id.textViewLatitud);
            TextView longitud = mifila.findViewById(R.id.textViewLongitud);
            TextView bateria = mifila.findViewById(R.id.textViewBateria);
            Button btnLocalizar = mifila.findViewById(R.id.buttonLocalizar);

            String lat = ArrayActividad[position].getLatitud();
            String lon = ArrayActividad[position].getLongitud();

            id_act.setText(String.valueOf(ArrayActividad[position].getId()));
            id_dep.setText(String.valueOf(ArrayActividad[position].getId_dep()));
            fecha.setText(ArrayActividad[position].getFecha());
            hora.setText(ArrayActividad[position].getHora());
            latitud.setText(ArrayActividad[position].getLatitud());
            longitud.setText(ArrayActividad[position].getLongitud());
            bateria.setText(ArrayActividad[position].getBateria());

            btnLocalizar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + lat + "," + lon));
                    startActivityForResult(intent, VER_MAPA);
                }
            });

            return mifila;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == COD_CAMBIO){

        } else {
            //Toast.makeText(this, R.string.Error, Toast.LENGTH_SHORT).show();
        }

    }

    public static class Actividad{
        String  fecha, hora, latitud, longitud;
        int id, id_dep, duracion, bateria;

        public Actividad(int id, int id_dep, String fecha, String hora, String latitud, String longitud, int duracion, int bateria) {
            this.id = id;
            this.id_dep = id_dep;
            this.fecha = fecha;
            this.hora = hora;
            this.latitud = latitud;
            this.longitud = longitud;
            this.duracion = duracion;
            this.bateria = bateria;
        }

        @NonNull
        @Override
        public String toString() {

            return "Actividad{" +
                   "id=" + id + '\'' +
                   ", id_dep=" + id_dep + '\'' +
                   ", fecha=" + fecha + '\'' +
                   ", hora=" + hora + '\'' +
                   ", latitud=" + latitud + '\'' +
                   ", longitud=" + longitud + '\'' +
                   ", duracion=" + duracion + '\'' +
                   '}';

        }

        public int getBateria() {
            return bateria;
        }

        public void setBateria(int bateria) {
            this.bateria = bateria;
        }

        public String getFecha() {
            return fecha;
        }

        public void setFecha(String fecha) {
            this.fecha = fecha;
        }

        public String getHora() {
            return hora;
        }

        public void setHora(String hora) {
            this.hora = hora;
        }

        public String getLatitud() {
            return latitud;
        }

        public void setLatitud(String latitud) {
            this.latitud = latitud;
        }

        public String getLongitud() {
            return longitud;
        }

        public void setLongitud(String longitud) {
            this.longitud = longitud;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getId_dep() {
            return id_dep;
        }

        public void setId_dep(int id_dep) {
            this.id_dep = id_dep;
        }

        public int getDuracion() {
            return duracion;
        }

        public void setDuracion(int duracion) {
            this.duracion = duracion;
        }
    }

}