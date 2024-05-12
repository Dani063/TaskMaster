package com.example.taskmaster;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Menu_Principal extends AppCompatActivity {

    Button CerrarSesion, AgregaNota;
    TextView NombresPrincipal, CorreoPrincipal;
    ProgressBar progressBarDatos;
    DatabaseReference Usuarios;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_principal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        NombresPrincipal = findViewById(R.id.NombresPrincipal);
        CorreoPrincipal = findViewById(R.id.CorreoPrincipal);
        progressBarDatos = findViewById(R.id.progressBar);
        AgregaNota = findViewById(R.id.AgregaNota);

        Usuarios = FirebaseDatabase.getInstance().getReference("Usuarios");
        CerrarSesion = findViewById(R.id.CerrarSesion);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        CerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SalirApp();
            }
        });
        AgregaNota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Menu_Principal.this, CrearTarea.class));
            }
        });
    }

    private void CargaDatos(){
        Usuarios.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    //El progreso se oculta
                    progressBarDatos.setVisibility(View.GONE);
                    //Los datos se muestran
                    NombresPrincipal.setVisibility(View.VISIBLE);
                    CorreoPrincipal.setVisibility(View.VISIBLE);
                    //Obtener los datos
                    String nombres = "Bienvenido " + snapshot.child("nombre").getValue() + " !";
                    String correo = ""+snapshot.child("correo").getValue();
                    //Set Datos
                    NombresPrincipal.setText(nombres);
                    CorreoPrincipal.setText(correo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        ComprobarSesionNull();
        super.onStart();
    }

    private void ComprobarSesionNull(){
        if (firebaseUser!=null){
            CargaDatos();
        }else {
            startActivity(new Intent(Menu_Principal.this,MainActivity.class));
            finish();
        }
    }
    private void SalirApp() {
        firebaseAuth.signOut();
        startActivity(new Intent(Menu_Principal.this,MainActivity.class));
        Toast.makeText(this, "Cerrando sesion", Toast.LENGTH_SHORT).show();
    }
}