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
                + NOM_ACTIVIDAD + " TEXT, "
                + TIP_DEPORTE + " TEXT, "
                + DUR_ACTIVIDAD + " TEXT"
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

    public boolean InsertarActividad(String nombre, String deporte, int tiempo){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOM_ACTIVIDAD, nombre);
        contentValues.put(TIP_DEPORTE, deporte);
        contentValues.put(DUR_ACTIVIDAD, tiempo);

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

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
