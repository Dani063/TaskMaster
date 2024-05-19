package com.example.taskmaster;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class CrearTarea extends AppCompatActivity {

    TextView Uid_Usuario, Correo_usuario, Fecha_hora_actual, Fecha, Estado;
    Button Btn_Calendario, Guardar;
    EditText Titulo, Descripcion;
    String titulo = "", descripcion = "", fecha="", estado="", uid="", tid="", fechacreacion="";
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    int dia, mes, anio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crear_tarea);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        InicializarVariables();
        ObtenerDatos();

        Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidarDatos();
            }
        });

        Btn_Calendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendario = Calendar.getInstance();
                dia = calendario.get(Calendar.DAY_OF_MONTH);
                mes = calendario.get(Calendar.MONTH);
                anio = calendario.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(CrearTarea.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String diaF, mesF;

                        //Obtener dia
                        if (dayOfMonth<10){
                            diaF= "0"+String.valueOf(dayOfMonth);
                        }
                        //Antes: 9/10/2002 - Ahora 09/10/2002
                        else {
                            diaF = String.valueOf(dayOfMonth);
                        }
                        //Obtener el mes
                        int Mes = month + 1;
                        if (Mes<10){
                            mesF = "0"+String.valueOf(month);
                        }
                        //Antes: 12/8/2002 - Ahora 12/08/2002
                        else {
                            mesF = String.valueOf(month);
                        }

                        //Set fecha en textview
                        Fecha.setText(diaF + "/" + mesF + "/" + year);
                    }
                }
                ,anio,mes,dia);
                datePickerDialog.show();
            }
        });

    }

    private void InicializarVariables() {
        //Text view
        Uid_Usuario = findViewById(R.id.Uid_Usuario);
        Correo_usuario = findViewById(R.id.Correo_usuario);
        Fecha_hora_actual = findViewById(R.id.Fecha_hora_Actual);
        Fecha = findViewById(R.id.Fecha);
        Estado = findViewById(R.id.Estado);
        Estado.setText("No finalizado");
        //Buttons
        Btn_Calendario = findViewById(R.id.Btn_Calendario);
        Guardar = findViewById(R.id.Guardar);
        //Edit Text
        Titulo = findViewById(R.id.Titulo);
        Descripcion = findViewById(R.id.Descripcion);
        //Otras
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

    }
    private void ObtenerDatos() {

        Uid_Usuario.setText(firebaseAuth.getUid());
        Correo_usuario.setText(firebaseAuth.getCurrentUser().getEmail());
        Fecha_hora_actual.setText(GetFechaHora());

    }

    private String GetFechaHora() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    private void ValidarDatos() {

        titulo = Titulo.getText().toString();
        descripcion = Descripcion.getText().toString();
        fecha = Fecha.getText().toString();
        estado = Estado.getText().toString();
        uid = Uid_Usuario.getText().toString();
        fechacreacion = Fecha_hora_actual.getText().toString();


        if (titulo.isEmpty()){
            Toast.makeText(this, "Escribe un titulo", Toast.LENGTH_SHORT).show();
        }
        else {
            CrearNuevaTarea();
        }
    }

    private void CrearNuevaTarea() {
        progressDialog.setMessage("Creando Tarea...");
        progressDialog.show();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tareas").child(uid);
        tid = databaseReference.push().getKey();

        if (descripcion.isEmpty()){
            descripcion = "Vacio";
            Descripcion.setText(descripcion);
        }

        HashMap<String,String> Datos = new HashMap<>();
        Datos.put("Uid", uid);
        Datos.put("Tid", tid);
        Datos.put("titulo", titulo);
        Datos.put("descripcion", descripcion);
        Datos.put("fecha", fecha);
        Datos.put("fechaCreacion", fechacreacion);
        Datos.put("estado", estado);

        //crear tarea en firebase


        databaseReference.child(tid).setValue(Datos).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                Toast.makeText(CrearTarea.this,"Nueva Tarea!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CrearTarea.this,Menu_Principal.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(CrearTarea.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}