package com.example.taskmaster;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import ActualizarTarea.Actualizar_Tarea;
import Objects.Tarea;
import ViewHolder.TareaViewHolder;

public class Menu_Principal extends AppCompatActivity {

    Button Btn_Ajustes;
    FloatingActionButton AgregaNota;
    TextView NombresPrincipal, /*CorreoPrincipal,*/ FraseAnimoTXT, TareasCompletadasTXT;
    ProgressBar progressBarDatos;
    DatabaseReference Usuarios;
    FirebaseUser firebaseUser;
    RecyclerView ReciclerViewTareasPendientes, ReciclerViewTareasCompletadas;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference BASE_DE_DATOS;
    Dialog dialog;
    int count = 0;

    LinearLayoutManager linearLayoutManager1, linearLayoutManager2; //Modifica forma de listar las notas
    FirebaseRecyclerAdapter<Tarea, TareaViewHolder> AdapterPendientes; //usa un detector de eventos para ver los cambios en la bd de tareas
    FirebaseRecyclerAdapter<Tarea, TareaViewHolder> AdapterCompletadas;
    FirebaseRecyclerOptions<Tarea> options, optionscompletadas;

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
        ReciclerViewTareasPendientes = findViewById(R.id.reciclerViewTareasPendientes);
        ReciclerViewTareasCompletadas = findViewById(R.id.reciclerViewTareasCompletadas);
        //ReciclerViewTareasPendientes.setHasFixedSize(true); //recycler view adaptara su tamaño a los elementos//Al estar la actividad dentro de un scroll view si se activa esta propiedad hace que no se vean las tareas
        ReciclerViewTareasPendientes.setLayoutManager(new LinearLayoutManager(this));
        ReciclerViewTareasCompletadas.setLayoutManager(new LinearLayoutManager(this));

        BASE_DE_DATOS = FirebaseDatabase.getInstance().getReference("Tareas");
        dialog = new Dialog(Menu_Principal.this);

        NombresPrincipal = findViewById(R.id.NombresPrincipal);
        //CorreoPrincipal = findViewById(R.id.CorreoPrincipal);
        progressBarDatos = findViewById(R.id.progressBar);
        AgregaNota = findViewById(R.id.AgregaNota);
        FraseAnimoTXT = findViewById(R.id.FraseAnimoTXT);
        TareasCompletadasTXT = findViewById(R.id.TareasCompletadasTXT);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Usuarios = FirebaseDatabase.getInstance().getReference("Usuarios");
        Btn_Ajustes = findViewById(R.id.Btn_Ajustes);

        //Listar Tareas usuario
        ListarTareasUsuarios();
        EscribirFraseAnimo();

        Btn_Ajustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Menu_Principal.this, Ajustes.class));
            }
        });
        AgregaNota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Menu_Principal.this, CrearTarea.class));
            }
        });
        applyPreferences();
    }
    private void EscribirFraseAnimo(){
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            Query query = BASE_DE_DATOS.orderByChild("uid").equalTo(userId);

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int count = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String estado = snapshot.child("estado").getValue(String.class);
                        if ("No finalizado".equals(estado)) {
                            count++;
                        }
                    }
                    Log.d("TareasSinFinalizar", "Número de tareas sin finalizar: " + count);
                    if (count == 0) {
                        FraseAnimoTXT.setText("No tienes tares pendientes! Para crear una nueva, presiona el botón +");
                    } else if (count == 1) {
                        FraseAnimoTXT.setText("Tienes " + count + " tarea pendiente!");
                    }
                    else {
                        FraseAnimoTXT.setText("Tienes " + count + " tareas pendientes!");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w("TareasSinFinalizar", "Error al leer los datos", databaseError.toException());
                }
            });
        }

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
                    FraseAnimoTXT.setVisibility(View.VISIBLE);
                    //CorreoPrincipal.setVisibility(View.VISIBLE);
                    //Obtener los datos
                    String nombres = "Bienvenido " + snapshot.child("nombre").getValue() + " !";
                    String correo = "" + snapshot.child("correo").getValue();
                    //Set Datos
                    NombresPrincipal.setText(nombres);
                    //CorreoPrincipal.setText(correo);
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
        //consulta
        Query queryPendientes = BASE_DE_DATOS.orderByChild("filtro").equalTo(firebaseUser.getUid()+"/No finalizado");
        options = new FirebaseRecyclerOptions.Builder<Tarea>()
                .setQuery(queryPendientes, Tarea.class).build();
        Log.d("DEBUG", "Options set for FirebaseRecyclerAdapter");
        AdapterPendientes = new FirebaseRecyclerAdapter<Tarea, TareaViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TareaViewHolder tareaViewHolder, int i, @NonNull Tarea tarea) {
                Log.d("DEBUG", "onBindViewHolder: Setting data for " + tarea.getTitulo());
                tareaViewHolder.SetearDatos(getApplicationContext(), tarea.getTitulo(), tarea.getDescripcion(),
                        tarea.getFecha(), tarea.getFechaCreacion(), tarea.getEstado(), tarea.getTid(), tarea.getUid());
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
                        //Toast.makeText(Menu_Principal.this, "on item long click", Toast.LENGTH_SHORT).show();

                        //Declarar las vistas
                        Button O_Editar, O_Eliminar;
                        CheckBox estadoTarea;
                        TextView tituloOpcion, descripcionOpcion, fechaOpcion, estadoOpcion;
                        String titulo, descripcion, fecha, estado, uid, tid, fechaCreacion;

                        //Conexion con el diseño
                        dialog.setContentView(R.layout.opciones);

                        //Inicializar vistas
                        estadoTarea = dialog.findViewById(R.id.checkbox);
                        O_Eliminar = dialog.findViewById(R.id.O_Eliminar);
                        O_Editar = dialog.findViewById((R.id.O_Editar));
                        tituloOpcion = dialog.findViewById(R.id.tituloOpcion);
                        descripcionOpcion = dialog.findViewById(R.id.descripcionOpcion);
                        fechaOpcion = dialog.findViewById(R.id.fechaOpcion);
                        estadoOpcion = dialog.findViewById(R.id.estadoOpcion);

                        //Get Strings
                        uid = getItem(position).getUid();
                        fechaCreacion = getItem(position).getFechaCreacion();
                        tid = getItem(position).getTid();
                        titulo = getItem(position).getTitulo();
                        descripcion = getItem(position).getDescripcion();
                        fecha = getItem(position).getFecha();
                        estado = getItem(position).getEstado();
                        estadoTarea.setChecked("Finalizado".equals(estado));

                        // Recuperar textos
                        tituloOpcion.setText(getItem(position).getTitulo());
                        descripcionOpcion.setText(getItem(position).getDescripcion());
                        fechaOpcion.setText(getItem(position).getFecha());
                        estadoOpcion.setText(getItem(position).getEstado());

                        O_Eliminar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EliminarTarea(tid);
                                dialog.dismiss();
                            }
                        });
                        O_Editar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Toast.makeText(Menu_Principal.this, "Nota actualizada", Toast.LENGTH_SHORT).show();
                                //startActivity(new Intent(Menu_Principal.this, Actualizar_Tarea.class));
                                Intent intent = new Intent(Menu_Principal.this, Actualizar_Tarea.class);
                                intent.putExtra("tid",tid);
                                intent.putExtra("uid",uid);
                                intent.putExtra("titulo", titulo);
                                intent.putExtra("descripcion",descripcion);
                                intent.putExtra("fecha",fecha);
                                intent.putExtra("fechaCreacion",fechaCreacion);
                                intent.putExtra("estado",estado);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });
                return tareaViewHolder;
            }
        };
        Query queryCompletadas = BASE_DE_DATOS.orderByChild("filtro").equalTo(firebaseUser.getUid()+"/Finalizado");
        optionscompletadas = new FirebaseRecyclerOptions.Builder<Tarea>()
                .setQuery(queryCompletadas, Tarea.class).build();
        AdapterCompletadas = new FirebaseRecyclerAdapter<Tarea, TareaViewHolder>(optionscompletadas) {
            @Override
            protected void onBindViewHolder(@NonNull TareaViewHolder tareaViewHolder, int i, @NonNull Tarea tarea) {
                Log.d("DEBUG", "onBindViewHolderC: Setting data for " + tarea.getTitulo());
                tareaViewHolder.SetearDatos(getApplicationContext(), tarea.getTitulo(), tarea.getDescripcion(),
                        tarea.getFecha(), tarea.getFechaCreacion(), tarea.getEstado(), tarea.getTid(), tarea.getUid());
            }

            @NonNull
            @Override
            public TareaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                Log.d("DEBUG", "onCreateViewHolderC: Creating view holder");
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tarea, parent, false);
                TareaViewHolder tareaViewHolder = new TareaViewHolder(view);
                tareaViewHolder.setOnClickListener(new TareaViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(Menu_Principal.this, "on item click", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        //Toast.makeText(Menu_Principal.this, "on item long click", Toast.LENGTH_SHORT).show();

                        //Declarar las vistas
                        Button O_Editar, O_Eliminar;
                        CheckBox estadoTarea;
                        TextView tituloOpcion, descripcionOpcion, fechaOpcion, estadoOpcion;
                        String titulo, descripcion, fecha, estado, uid, tid, fechaCreacion;

                        //Conexion con el diseño
                        dialog.setContentView(R.layout.opciones);

                        //Inicializar vistas
                        estadoTarea = dialog.findViewById(R.id.checkbox);
                        O_Eliminar = dialog.findViewById(R.id.O_Eliminar);
                        O_Editar = dialog.findViewById((R.id.O_Editar));
                        tituloOpcion = dialog.findViewById(R.id.tituloOpcion);
                        descripcionOpcion = dialog.findViewById(R.id.descripcionOpcion);
                        fechaOpcion = dialog.findViewById(R.id.fechaOpcion);
                        estadoOpcion = dialog.findViewById(R.id.estadoOpcion);

                        //Get Strings
                        uid = getItem(position).getUid();
                        fechaCreacion = getItem(position).getFechaCreacion();
                        tid = getItem(position).getTid();
                        titulo = getItem(position).getTitulo();
                        descripcion = getItem(position).getDescripcion();
                        fecha = getItem(position).getFecha();
                        estado = getItem(position).getEstado();
                        estadoTarea.setChecked("Finalizado".equals(estado));

                        // Recuperar textos
                        tituloOpcion.setText(getItem(position).getTitulo());
                        descripcionOpcion.setText(getItem(position).getDescripcion());
                        fechaOpcion.setText(getItem(position).getFecha());
                        estadoOpcion.setText(getItem(position).getEstado());

                        O_Eliminar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EliminarTarea(tid);
                                dialog.dismiss();
                            }
                        });
                        O_Editar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Toast.makeText(Menu_Principal.this, "Nota actualizada", Toast.LENGTH_SHORT).show();
                                //startActivity(new Intent(Menu_Principal.this, Actualizar_Tarea.class));
                                Intent intent = new Intent(Menu_Principal.this, Actualizar_Tarea.class);
                                intent.putExtra("tid",tid);
                                intent.putExtra("uid",uid);
                                intent.putExtra("titulo", titulo);
                                intent.putExtra("descripcion",descripcion);
                                intent.putExtra("fecha",fecha);
                                intent.putExtra("fechaCreacion",fechaCreacion);
                                intent.putExtra("estado",estado);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });
                return tareaViewHolder;
            }
        };
        Log.d("DEBUG", "Adapter created");
        linearLayoutManager1 = new LinearLayoutManager(Menu_Principal.this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager1.setReverseLayout(true); //listar desde la ultima tarea creada a la ultima
        linearLayoutManager1.setStackFromEnd(true); //La lista empieza por arriba
        linearLayoutManager2 = new LinearLayoutManager(Menu_Principal.this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager2.setReverseLayout(true); //listar desde la ultima tarea creada a la ultima
        linearLayoutManager2.setStackFromEnd(true); //La lista empieza por arriba
        ReciclerViewTareasPendientes.setLayoutManager(linearLayoutManager1);
        ReciclerViewTareasPendientes.setAdapter(AdapterPendientes);
        ReciclerViewTareasCompletadas.setLayoutManager(linearLayoutManager2);
        ReciclerViewTareasCompletadas.setAdapter(AdapterCompletadas);
        Log.d("DEBUG", "RecyclerView set up completed");
    }
    private void EliminarTarea(String tid){
        AlertDialog.Builder builder = new AlertDialog.Builder(Menu_Principal.this);
        builder.setTitle("Eliminar la tarea");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Query query =BASE_DE_DATOS.orderByChild("tid").equalTo(tid);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()){
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(Menu_Principal.this, "Tarea eliminada", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Menu_Principal.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Menu_Principal.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
        builder.create().show();
    }
    private void applyPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean mostrarCompletadas = preferences.getBoolean("mostrarCompletadas", true);

        ReciclerViewTareasCompletadas.setVisibility(mostrarCompletadas ? View.VISIBLE : View.GONE);
        TareasCompletadasTXT.setVisibility(mostrarCompletadas ? View.VISIBLE : View.GONE);
    }
    @Override
    protected void onStart() {
        super.onStart();
        ComprobarSesionNull();
        if (AdapterPendientes != null) {
            AdapterPendientes.startListening();
            Log.d("DEBUG", "Adapter started listening");
        }
        if (AdapterCompletadas != null) {
            AdapterCompletadas.startListening();
            Log.d("DEBUG", "Adapter started listening");
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (AdapterPendientes != null) {
            AdapterPendientes.stopListening();
            Log.d("DEBUG", "Adapter stopped listening");
        }
        if (AdapterCompletadas != null) {
            AdapterCompletadas.stopListening();
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
}