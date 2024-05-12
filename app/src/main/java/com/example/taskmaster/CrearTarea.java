package com.example.taskmaster;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CrearTarea extends AppCompatActivity {

    TextView Uid_Usuario, Correo_usuario, Fecha_hora_actual, Fecha, Estado;
    Button Btn_Calendario, Guardar;
    EditText Titulo, Descripcion;
    String titulo = "", descripcion = "", fecha="";
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;

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
        Uid_Usuario = findViewById(R.id.Uid_Usuario);
        Correo_usuario = findViewById(R.id.Correo_usuario);
        Fecha_hora_actual = findViewById(R.id.Fecha_hora_Actual);
        Fecha = findViewById(R.id.Fecha);
        Estado = findViewById(R.id.Estado);

        Btn_Calendario = findViewById(R.id.Btn_Calendario);
        Guardar = findViewById(R.id.Guardar);

        Titulo = findViewById(R.id.Titulo);
        Descripcion = findViewById(R.id.Descripcion);



        Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidarDatos();
            }
        });

    }

    private void ValidarDatos() {

        titulo = Titulo.getText().toString();
        descripcion = Descripcion.getText().toString();
        fecha = Fecha.getText().toString();
        if (descripcion.isEmpty()){
            descripcion = "Vacio";
            Descripcion.setText(descripcion);
        }
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

        String Tid = firebaseAuth.getUid();

        HashMap<String,String> Datos = new HashMap<>();
        Datos.put("Tid", Tid);
        Datos.put("titulo", titulo);
        Datos.put("descripcion", descripcion);
        Datos.put("fecha", fecha);

        //crear tarea en firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tareas");

        databaseReference.child(Tid).setValue(Datos).addOnSuccessListener(new OnSuccessListener<Void>() {
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