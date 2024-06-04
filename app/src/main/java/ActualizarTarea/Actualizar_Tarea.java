package ActualizarTarea;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

import com.example.taskmaster.CrearTarea;
import com.example.taskmaster.Menu_Principal;
import com.example.taskmaster.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import Objects.Tarea;

public class Actualizar_Tarea extends AppCompatActivity {

    TextView Uid_Usuario_A, Correo_usuario_A, Fecha_hora_Actual_A, Fecha_A, Tid_A, Estado_A;
    EditText Titulo_A, Descripcion_A;
    Button Btn_Calendario_A, Guardar_A;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    String tid, uid_usuario, correo_usuario, fecha_hora_actual, fecha, titulo, descripcion, estado, filtro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Set the layout for the activity
        setContentView(R.layout.activity_actualizar_tarea);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        InicializarVistas();
        RecuperarDatos();
        SetearDatos();

        Btn_Calendario_A.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendario = Calendar.getInstance();
                int dia, mes, anio;
                dia = calendario.get(Calendar.DAY_OF_MONTH);
                mes = calendario.get(Calendar.MONTH);
                anio = calendario.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Actualizar_Tarea.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String diaF, mesF;

                        //Obtener dia
                        if (dayOfMonth < 10) {
                            diaF = "0" + String.valueOf(dayOfMonth);
                        }
                        //Antes: 9/10/2002 - Ahora 09/10/2002
                        else {
                            diaF = String.valueOf(dayOfMonth);
                        }
                        //Obtener el mes
                        int Mes = month + 1;
                        if (Mes < 10) {
                            mesF = "0" + String.valueOf(month);
                        }
                        //Antes: 12/8/2002 - Ahora 12/08/2002
                        else {
                            mesF = String.valueOf(month);
                        }

                        //Set fecha en textview
                        Fecha_A.setText(diaF + "/" + mesF + "/" + year);
                    }
                }
                        , anio, mes, dia);
                datePickerDialog.show();
            }
        });
        Guardar_A.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidarDatos();
            }
        });
    }

    private void ValidarDatos() {
        titulo = Titulo_A.getText().toString();
        descripcion = Descripcion_A.getText().toString();
        fecha = Fecha_A.getText().toString();
        estado = Estado_A.getText().toString();
        uid_usuario = Uid_Usuario_A.getText().toString();
        fecha_hora_actual = Fecha_hora_Actual_A.getText().toString();
        filtro = uid_usuario + "/" + estado;


        if (titulo.isEmpty()) {
            Toast.makeText(this, "Escribe un titulo", Toast.LENGTH_SHORT).show();
        } else {
            ActualizarTarea();
        }
    }

    public void InicializarVistas() {
        Uid_Usuario_A = findViewById(R.id.Uid_Usuario_A);
        Correo_usuario_A = findViewById(R.id.Correo_usuario_A);
        Fecha_A = findViewById(R.id.Fecha_A);
        Fecha_hora_Actual_A = findViewById(R.id.Fecha_hora_Actual_A);
        Titulo_A = findViewById(R.id.Titulo_A);
        Descripcion_A = findViewById(R.id.Descripcion_A);
        Btn_Calendario_A = findViewById(R.id.Btn_Calendario_A);
        Guardar_A = findViewById(R.id.Guardar_A);
        Tid_A = findViewById(R.id.Tid_A);
        Estado_A = findViewById(R.id.Estado_A);
        progressDialog = new ProgressDialog(this);
    }

    private void RecuperarDatos() {
        firebaseAuth = FirebaseAuth.getInstance();
        Bundle intent = getIntent().getExtras();

        tid = intent.getString("tid");
        uid_usuario = intent.getString("uid");
        fecha_hora_actual = intent.getString("fechaCreacion");
        fecha = intent.getString("fecha");
        titulo = intent.getString("titulo");
        descripcion = intent.getString("descripcion");
        estado = intent.getString("estado");
        correo_usuario = firebaseAuth.getCurrentUser().getEmail();
    }

    private void SetearDatos() {
        Uid_Usuario_A.setText(uid_usuario);
        Fecha_A.setText(fecha);
        Fecha_hora_Actual_A.setText(fecha_hora_actual);
        Titulo_A.setText(titulo);
        Descripcion_A.setText(descripcion);
        Tid_A.setText(tid);
        Estado_A.setText(estado);
        Correo_usuario_A.setText(correo_usuario);
    }
    private void ActualizarTarea() {
        progressDialog.setMessage("Actualizando Tarea...");
        progressDialog.show();
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference();


        if (descripcion.isEmpty()){
            descripcion = "Vacio";
            Descripcion_A.setText(descripcion);
        }
        if (fecha.isEmpty()){
            fecha = "Vacio";
            Fecha_A.setText(fecha);
        }

        if (!uid_usuario.isEmpty() && !tid.equals("")&& !fecha_hora_actual.isEmpty() && !fecha.isEmpty() &&
                !titulo.isEmpty() && !descripcion.isEmpty() && !estado.isEmpty()){
            //crear objeto tarea
            Tarea tarea = new Tarea(titulo,descripcion,fecha,fecha_hora_actual,estado,tid,uid_usuario,filtro);
            //crear tarea en firebase
            databaseReference.child("Tareas").child(tid).setValue(tarea).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    progressDialog.dismiss();
                    Toast.makeText(Actualizar_Tarea.this,"Tarea actualizada!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Actualizar_Tarea.this, Menu_Principal.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(Actualizar_Tarea.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
