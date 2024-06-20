package com.example.taskmaster;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import Objects.Subtarea;
import Objects.Tarea;

public class CrearTarea extends AppCompatActivity {

    LinearLayout subtareasContainer;
    List<EditText> subtareasList = new ArrayList<>();
    TextView Uid_Usuario, Correo_usuario, Fecha_hora_actual, Fecha, Estado;
    Button Btn_Calendario, Guardar;
    EditText Titulo, Descripcion;
    String titulo = "", descripcion = "", fecha="", estado="", uid="", tid="", fechacreacion="", filtro="";
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
        addSubTarea();

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
                        int Mes = month + 1;//se escriben en base 0 asi que hay que sumar 1
                        if (Mes<10){
                            mesF = "0"+String.valueOf(Mes);
                        }
                        //Antes: 12/8/2002 - Ahora 12/08/2002
                        else {
                            mesF = String.valueOf(Mes);
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

    private void addSubTarea() {
        final EditText CajaSubtarea = new EditText(this);
        CajaSubtarea.setHint("Subtarea");
        CajaSubtarea.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        CajaSubtarea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty() && subtareasContainer.indexOfChild(CajaSubtarea) == subtareasContainer.getChildCount() - 1) {
                    addSubTarea();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        subtareasContainer.addView(CajaSubtarea);
        subtareasList.add(CajaSubtarea);
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
        subtareasContainer = findViewById(R.id.subtareasContainer);

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
        filtro = uid + "/" + estado;


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
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        tid = databaseReference.push().getKey();

        if (descripcion.isEmpty()){
            descripcion = "Vacio";
            Descripcion.setText(descripcion);
        }
        if (fecha.isEmpty()){
            fecha = "Vacio";
            Fecha.setText(fecha);
        }

        if (!uid.isEmpty() && !tid.equals("")&& !fechacreacion.isEmpty() && !fecha.isEmpty() &&
                !titulo.isEmpty() && !descripcion.isEmpty() && !estado.isEmpty()) {
            List<Subtarea> subtareasObjList = new ArrayList<>();
            for (EditText subtareaField : subtareasList) {
                String subtareaTexto = subtareaField.getText().toString().trim();
                if (!subtareaTexto.isEmpty()) {
                    String sid = databaseReference.push().getKey();
                    if (sid != null) {
                        Subtarea subtareaObj = new Subtarea(subtareaTexto, tid, sid);
                        subtareasObjList.add(subtareaObj);
                        databaseReference.child("Subtareas").child(sid).setValue(subtareaObj);
                    }
                }
            }
            //crear objeto tarea
            Tarea tarea = new Tarea(titulo, descripcion, fecha, fechacreacion, estado, tid, uid, filtro, subtareasObjList);
            //crear tarea en firebase
            databaseReference.child("Tareas").child(tid).setValue(tarea).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    progressDialog.dismiss();
                    Toast.makeText(CrearTarea.this, "Nueva Tarea!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CrearTarea.this, Menu_Principal.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(CrearTarea.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(CrearTarea.this,Menu_Principal.class));
    }
}