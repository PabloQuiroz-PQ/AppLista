package com.ppql.applista;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public TextView textViewItem;
    public ListView listaTareas;
    //public String tareas [] = {};
    public String posicion = "-1";

    public ArrayList<String> tareas;

    ConexionSQLiteHelper conexionSQLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listaTareas = (ListView)findViewById(R.id.listViewTareas);
        textViewItem = (TextView) findViewById(R.id.textViewItem);

        tareas = new ArrayList<String>();
        //tareas.add("Prueba 1");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_tarea, tareas);
        listaTareas.setAdapter(adapter);

        listaTareas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                posicion = "";
                posicion = "" + position;
            }
        });

        conexionSQLiteHelper = new ConexionSQLiteHelper(this,"listTarea",null, 3);
    }

    public void eliminarItem(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Eliminar tarea");
        builder.setMessage("¿Desea eliminar la tarea?").setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int j = Integer.parseInt(posicion);
                if(j >= 0){
                    tareas.remove(j);
                    eliminarTareas();
                    for(int a = 0; a < tareas.size(); a++){
                        agregarTarea(tareas.get(a), i +"");
                    }
                    consultarTodo();
                    actualizarArray();
                    posicion = "-1";
                } else{
                    Toast.makeText(getApplicationContext(), "Por favor seleccione una tarea", Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
                actualizarArray();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();

    }

    public void actualizarArray(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_tarea, tareas);
        listaTareas.setAdapter(adapter);
    }

    public void agregarItem(View view){
        LayoutInflater inflater = getLayoutInflater();

        View dialoglayout = inflater.inflate(R.layout.dialog_agregar, null);
        final EditText txtTarea = (EditText) dialoglayout.findViewById(R.id.txtTarea);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        tareas.add(txtTarea.getText().toString());

                        eliminarTareas();
                        for(int i = 0; i < tareas.size(); i++){
                            agregarTarea(tareas.get(i), i +"");
                        }
                        consultarTodo();
                        actualizarArray();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });;
        builder.setView(dialoglayout);
        builder.show();
    }

    public void editarItem(View view){
        LayoutInflater inflater = getLayoutInflater();

        View dialoglayout = inflater.inflate(R.layout.dialog_editar, null);
        final EditText txtTarea = (EditText) dialoglayout.findViewById(R.id.txtTarea);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        int j = Integer.parseInt(posicion);
                        if(j >= 0){
                            tareas.set(j, txtTarea.getText().toString());
                            eliminarTareas();
                            for(int i = 0; i < tareas.size(); i++){
                                agregarTarea(tareas.get(i), i +"");
                            }
                            consultarTodo();
                            actualizarArray();

                            posicion = "-1";
                        } else {
                            Toast.makeText(getApplicationContext(), "Por favor seleccione una tarea", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                        actualizarArray();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplicationContext(), "Por favor seleccione una tarea", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });;
        builder.setView(dialoglayout);
        builder.show();
    }

    public void consultarTodo(){
        SQLiteDatabase db = conexionSQLiteHelper.getWritableDatabase();
        try {
            String sql = "SELECT id, tarea FROM tarea";
            Cursor c = db.rawQuery(sql, null);
            if (c.getCount() == 0) {
                return;
            }
            StringBuffer buffer = new StringBuffer();
            tareas.clear();
            while (c.moveToNext()) {
                tareas.add(c.getString(1));
            }
        } catch (Exception e){
            e.printStackTrace();
        }finally {

            db.close();
        }
    }

    public void agregarTarea(String tarea, String posicion){
        SQLiteDatabase db = conexionSQLiteHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("tarea", tarea);
            values.put("posicion", posicion);
            Long idTarea = db.insert("tareas", "tarea", values);
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();

        }finally {
            db.endTransaction();
            db.close();
        }
    }

    public void eliminarTareas(){
        SQLiteDatabase db = conexionSQLiteHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete("tareas", "", null);
            db.setTransactionSuccessful();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.endTransaction();
            db.close();
        }
    }

    public void modificarTarea(String tarea, String posicion){
        SQLiteDatabase db = conexionSQLiteHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("tarea", tarea);
            String[] args = new String[]{posicion};
            int idTarea = db.update("tareas",values, "posicion=?", args);
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.endTransaction();
            db.close();
        }
    }


}