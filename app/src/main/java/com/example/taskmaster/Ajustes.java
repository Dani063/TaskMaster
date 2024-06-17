package com.example.taskmaster;

import android.app.AlertDialog;
import android.app.MediaRouteButton;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class Ajustes extends AppCompatActivity {

    Button CerrarSesion;
    CheckBox MostrarTareas;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ajustes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Configurar el OnBackPressedCallback
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(Ajustes.this, Menu_Principal.class));
            }
        };
        firebaseAuth = FirebaseAuth.getInstance();

        CerrarSesion = findViewById(R.id.CerrarSesion);
        MostrarTareas = findViewById(R.id.checkboxVisibilidadTareas);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean mostrarCompletadas = preferences.getBoolean("mostrarCompletadas", true);
        MostrarTareas.setChecked(mostrarCompletadas);

        MostrarTareas.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("mostrarCompletadas", isChecked);
            editor.apply();
        });

        CerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SalirApp();
            }
        });
    }

    private void SalirApp() {
        firebaseAuth.signOut();
        startActivity(new Intent(Ajustes.this,MainActivity.class));
        Toast.makeText(this, "Cerrando sesion", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Ajustes.this, Menu_Principal.class));
    }

}