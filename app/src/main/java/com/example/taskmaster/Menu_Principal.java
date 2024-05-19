package com.example.taskmaster;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Objects.Tarea;
import ViewHolder.TareaViewHolder;

public class Menu_Principal extends AppCompatActivity {

    Button CerrarSesion, AgregaNota;
    TextView NombresPrincipal, CorreoPrincipal;
    ProgressBar progressBarDatos;
    DatabaseReference Usuarios;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    RecyclerView recyclerViewTareas;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference BASE_DE_DATOS;

    LinearLayoutManager linearLayoutManager;
    FirebaseRecyclerAdapter<Tarea, TareaViewHolder> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Tarea> options;

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
        recyclerViewTareas = findViewById(R.id.reciclerViewTareas);
        recyclerViewTareas.setHasFixedSize(true);
        recyclerViewTareas.setLayoutManager(new LinearLayoutManager(this));

        firebaseDatabase = FirebaseDatabase.getInstance();
        BASE_DE_DATOS = FirebaseDatabase.getInstance().getReference("Tareas");

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

    private void CargaDatos() {
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
                    String correo = "" + snapshot.child("correo").getValue();
                    //Set Datos
                    NombresPrincipal.setText(nombres);
                    CorreoPrincipal.setText(correo);
                    //Listar Tareas usuario
                    ListarTareasUsuarios();
                    // Verificación de datos en BASE_DE_DATOS
                    BASE_DE_DATOS.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot tareaSnapshot : dataSnapshot.getChildren()) {
                                    Tarea tarea = tareaSnapshot.getValue(Tarea.class);
                                    System.out.println("Tarea: " + tarea.getTitulo());
                                }
                            } else {
                                System.out.println("No hay tareas para este usuario.");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            System.err.println("Error al leer tareas: " + databaseError.getMessage());
                        }
                    });
                }
            }
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println("Error al leer datos del usuario: " + error.getMessage());
            }
        });
    }
    private void ListarTareasUsuarios(){

        options = new FirebaseRecyclerOptions.Builder<Tarea>()
                .setQuery(BASE_DE_DATOS.child(firebaseUser.getUid()), Tarea.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Tarea, TareaViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull TareaViewHolder tareaViewHolder, int i, @NonNull Tarea tarea) {
                System.out.println("Tareaaaa: " + tarea.getTitulo()); // Verificar datos aquí
                tareaViewHolder.bind(tarea);
            }

            @NonNull
            @Override
            public TareaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tarea, parent, false);
                return new TareaViewHolder(view);
            }
        };
        recyclerViewTareas.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }
    @Override
    protected void onStart() {
        super.onStart();
        ComprobarSesionNull();
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.stopListening();
        }
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