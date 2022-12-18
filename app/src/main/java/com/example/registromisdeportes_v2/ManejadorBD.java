package com.example.registromisdeportes_v2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ManejadorBD extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "misdeportes.db";
    private static final String TAB_DEPORTES = "DEPORTES";
    private static final String TAB_ACTIVIDAD = "ACTIVIDADES";
    private static final String ID_DEPORTE = "ID_DEPORTE";
    private static final String ID_ACTIVIDAD = "ID_ACTIVIDAD";
    private static final String NOM_DEPORTE = "NOM_DEPORTE";
    private static final String NOM_ACTIVIDAD = "NOM_ACTIVIDAD";
    private static final String DES_DEPORTE = "DES_DEPORTE";
    private static final String DUR_ACTIVIDAD = "DUR_ACTIVIDAD";
    private static final String TIP_DEPORTE = "TIP_DEPORTE";
    private static final String ID_DEP = "ID_DEP";
    private static final String FECHA_ACT = "FECHA_ACT";
    private static final String HORA_ACT = "HORA_ACT";
    private static final String LATITUD_ACT = "LATITUD_ACT";
    private static final String LONGITUD_ACT = "LONGITUD_ACT";
    private static final String BATERIA = "BATERIA";

    public ManejadorBD(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    public ManejadorBD(Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE " + TAB_DEPORTES + "(" + ID_DEPORTE + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + NOM_DEPORTE + " TEXT, "
                + DES_DEPORTE + " TEXT"
                + ")");
        sqLiteDatabase.execSQL("CREATE TABLE " + TAB_ACTIVIDAD + "(" + ID_ACTIVIDAD + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ID_DEP + " INTEGER, "
                + FECHA_ACT + " TEXT, "
                + HORA_ACT + " TEXT, "
                + LATITUD_ACT + " TEXT, "
                + LONGITUD_ACT + " TEXT, "
                + DUR_ACTIVIDAD + " INTEGER, "
                + BATERIA + "INTEGER, "
                + " FOREIGN KEY(" + ID_DEP + ") REFERENCES " + TAB_DEPORTES + "(" + ID_DEPORTE + ") "
                + ")");

    }

    public boolean InsertarDeporte(String nombre, String descripcion){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOM_DEPORTE, nombre);
        contentValues.put(DES_DEPORTE, descripcion);

        long resultado = sqLiteDatabase.insert(TAB_DEPORTES, null, contentValues);
        sqLiteDatabase.close();
        return (resultado != -1);
    }

    public boolean ActualizarDeporte(int id, String nom_dep, String des_dep){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOM_DEPORTE, nom_dep);
        contentValues.put(DES_DEPORTE, des_dep);

        long resultado = sqLiteDatabase.update(TAB_DEPORTES, contentValues, ID_DEPORTE + "=?", new String[]{String.valueOf(id)});
        sqLiteDatabase.close();

        return (resultado > 0);
    }

    public boolean BorrarDeporte(int id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        int res = sqLiteDatabase.delete(TAB_DEPORTES, ID_DEPORTE + "=?", new String[]{String.valueOf(id)});
        sqLiteDatabase.close();

        return res > 0;
    }

    public boolean InsertarActividad(int id_dep, String fecha, String hora, String Lat, String Lon, int min, int bateria){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID_DEP, id_dep);
        contentValues.put(FECHA_ACT, fecha);
        contentValues.put(HORA_ACT, hora);
        contentValues.put(LATITUD_ACT, Lat);
        contentValues.put(LONGITUD_ACT, Lon);
        contentValues.put(DUR_ACTIVIDAD, min);
        contentValues.put(BATERIA, bateria);

        long resultado = sqLiteDatabase.insert(TAB_ACTIVIDAD, null, contentValues);
        sqLiteDatabase.close();
        return (resultado != -1);
    }

    public Cursor ListarDeportes(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TAB_DEPORTES, null);
        return cursor;
    }

    public Cursor ListarActividad(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TAB_ACTIVIDAD, null);
        return cursor;
    }

    public Cursor ListaNomDeportes(String nom_dep){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + ID_DEPORTE + " FROM " + TAB_DEPORTES + " WHERE " + NOM_DEPORTE + " = '" + nom_dep + "';", null);
        return cursor;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
