package com.example.taskmaster;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

    LinearLayoutManager linearLayoutManager; //Modifica forma de listar las notas
    FirebaseRecyclerAdapter<Tarea, TareaViewHolder> firebaseRecyclerAdapter; //usa un detector de eventos para ver los cambios en la bd de tareas
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
        //recyclerViewTareas.setHasFixedSize(true); //recycler view adaptara su tamaño a los elementos//Al estar la actividad dentro de un scroll view si se activa esta propiedad hace que no se vean las tareas
        recyclerViewTareas.setLayoutManager(new LinearLayoutManager(this));

        BASE_DE_DATOS = FirebaseDatabase.getInstance().getReference("Tareas");

        NombresPrincipal = findViewById(R.id.NombresPrincipal);
        CorreoPrincipal = findViewById(R.id.CorreoPrincipal);
        progressBarDatos = findViewById(R.id.progressBar);
        AgregaNota = findViewById(R.id.AgregaNota);

        Usuarios = FirebaseDatabase.getInstance().getReference("Usuarios");
        CerrarSesion = findViewById(R.id.CerrarSesion);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //Listar Tareas usuario
        ListarTareasUsuarios();

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
                    // Verificación de datos en BASE_DE_DATOS
                    BASE_DE_DATOS.addValueEventListener(new ValueEventListener() {
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
                .setQuery(BASE_DE_DATOS, Tarea.class).build();
        Log.d("DEBUG", "Options set for FirebaseRecyclerAdapter");
        System.out.println(options.getSnapshots().getClass());

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Tarea, TareaViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TareaViewHolder tareaViewHolder, int i, @NonNull Tarea tarea) {
                Log.d("DEBUG", "onBindViewHolder: Setting data for " + tarea.getTitulo());
                tareaViewHolder.SetearDatos(getApplicationContext(), tarea.getTitulo(), tarea.getDescripcion(),
                        tarea.getFecha(), tarea.getFechaCreacion(), tarea.getEstado(), tarea.gettid(), tarea.getuid());
            }

            @NonNull
            @Override
            public TareaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                Log.d("DEBUG", "onCreateViewHolder: Creating view holder");
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tarea, parent, false);
                TareaViewHolder tareaViewHolder = new TareaViewHolder(view);
                tareaViewHolder.setOnClickListener(new TareaViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(Menu_Principal.this, "on item click", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        Toast.makeText(Menu_Principal.this, "on item long click", Toast.LENGTH_SHORT).show();
                    }
                });
                return tareaViewHolder;
            }
        };
        Log.d("DEBUG", "Adapter created");
        linearLayoutManager = new LinearLayoutManager(Menu_Principal.this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true); //listar desde la ultima tarea creada a la ultima
        linearLayoutManager.setStackFromEnd(true); //La lista empieza por arriba
        recyclerViewTareas.setLayoutManager(linearLayoutManager);
        recyclerViewTareas.setAdapter(firebaseRecyclerAdapter);
        Log.d("DEBUG", "RecyclerView set up completed");
    }
    @Override
    protected void onStart() {
        super.onStart();
        ComprobarSesionNull();
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.startListening();
            Log.d("DEBUG", "Adapter started listening");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.stopListening();
            Log.d("DEBUG", "Adapter stopped listening");
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