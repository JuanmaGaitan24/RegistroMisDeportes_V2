package com.example.registromisdeportes_v2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DeportesActivity extends AppCompatActivity {

    public static final String NUMERO = "NUMERO";
    private static final int COD_CAMBIO = 81;
    private static final int COD_OPCIONES = 501;
    ListView listaDeportes;
    Button btnAgregarDeporte, btnModificarDeporte, btnBorrarDeporte, btnActividades, btnOpciones;
    TextView txtNombreDeporte, txtDescripcionDeporte;

    ArrayList<Deporte> deporte;
    Deporte[] ArrayDeportes;

    int id_dep = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deportes);

        ManejadorBD manejadorBD = new ManejadorBD(this);
        listaDeportes = findViewById(R.id.ListViewDeportes);
        btnAgregarDeporte = findViewById(R.id.buttonAgregar);
        btnModificarDeporte = findViewById(R.id.buttonModificarDeporte);
        btnBorrarDeporte = findViewById(R.id.buttonBorrarDeporte);
        btnActividades = findViewById(R.id.buttonActividades);
        btnOpciones = findViewById(R.id.ButtonConfiguracion);
        txtNombreDeporte = findViewById(R.id.editTextNombreDeporte);
        txtDescripcionDeporte = findViewById(R.id.editTextDescripcion);

        ListarDeportes(manejadorBD, listaDeportes);

        btnAgregarDeporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nom_dep = String.valueOf(txtNombreDeporte.getText());
                String des_dep = String.valueOf(txtDescripcionDeporte.getText());

                if ("".equals(nom_dep) || "".equals(des_dep)){
                    Toast.makeText(DeportesActivity.this, R.string.CamposVacios, Toast.LENGTH_LONG).show();
                } else {
                    manejadorBD.InsertarDeporte(nom_dep, des_dep);
                    ListarDeportes(manejadorBD, listaDeportes);
                }

            }
        });

        listaDeportes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                id_dep = Integer.parseInt(deporte.get(i).getId());
                txtNombreDeporte.setText(deporte.get(i).getNombre());
                txtDescripcionDeporte.setText(deporte.get(i).getDescripcion());

            }
        });

        btnModificarDeporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nom_dep = String.valueOf(txtNombreDeporte.getText());
                String des_dep = String.valueOf(txtDescripcionDeporte.getText());

                if ("".equals(nom_dep) || "".equals(des_dep) || id_dep == 0){
                    Toast.makeText(DeportesActivity.this, R.string.ErrorModEli, Toast.LENGTH_LONG).show();
                } else {

                    boolean res = manejadorBD.ActualizarDeporte(id_dep, nom_dep, des_dep);

                    if (res){
                        Toast.makeText(DeportesActivity.this, R.string.ModExisto, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DeportesActivity.this, R.string.ModError, Toast.LENGTH_SHORT).show();
                    }

                    ListarDeportes(manejadorBD, listaDeportes);
                    id_dep = 0;
                    txtNombreDeporte.setText("");
                    txtDescripcionDeporte.setText("");
                }

            }
        });

        btnBorrarDeporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nom_dep = String.valueOf(txtNombreDeporte.getText());
                String des_dep = String.valueOf(txtDescripcionDeporte.getText());

                if ("".equals(nom_dep) || "".equals(des_dep) || id_dep == 0){
                    Toast.makeText(DeportesActivity.this, R.string.ErrorModEli, Toast.LENGTH_LONG).show();
                } else {

                    boolean res = manejadorBD.BorrarDeporte(id_dep);

                    if (res){
                        Toast.makeText(DeportesActivity.this, R.string.ModExisto, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DeportesActivity.this, R.string.ModError, Toast.LENGTH_SHORT).show();
                    }

                    ListarDeportes(manejadorBD, listaDeportes);
                    id_dep = 0;
                    txtNombreDeporte.setText("");
                    txtDescripcionDeporte.setText("");
                }
            }
        });

        btnActividades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Intent intent = new Intent(DeportesActivity.this, ListarActividadesActivity.class);
                    startActivityForResult(intent, COD_CAMBIO);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        btnOpciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(DeportesActivity.this, OpcionesActivity.class);
                    startActivityForResult(intent, COD_OPCIONES);
                }catch (Exception e){
                    e.printStackTrace();
                }
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
                list.add(fila);*/
                deporte.add(deporte1);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == COD_CAMBIO){

        } else {
            //Toast.makeText(this, R.string.Error, Toast.LENGTH_SHORT).show();
        }

    }

    public static class Deporte{
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