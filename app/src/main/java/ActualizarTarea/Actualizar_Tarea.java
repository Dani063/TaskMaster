package ActualizarTarea;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.taskmaster.CrearTarea;
import com.example.taskmaster.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

public class Actualizar_Tarea extends AppCompatActivity {

    TextView Uid_Usuario_A, Correo_usuario_A, Fecha_hora_Actual_A, Fecha_A, Tid_A, Estado_A;
    EditText Titulo_A, Descripcion_A;
    Button Btn_Calendario_A, Guardar_A;
    FirebaseAuth firebaseAuth;

    String tid, uid_usuario, correo_usuario, fecha_hora_actual, fecha, titulo, descripcion, estado;

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


        if (titulo.isEmpty()) {
            Toast.makeText(this, "Escribe un titulo", Toast.LENGTH_SHORT).show();
        } else {
            ActulizaTarea();
        }
    }

    private void ActulizaTarea() {
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
    }

    private void RecuperarDatos() {
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
}
