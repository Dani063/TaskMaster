package ActualizarTarea;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.taskmaster.R;

public class Actualizar_Tarea extends AppCompatActivity {

    TextView Uid_Usuario_A, Correo_usuario_A, Fecha_hora_Actual_A, Fecha_A, Tid_A, Estado_A;
    EditText Titulo_A, Descripcion_A;
    Button Btn_Calendario_A, Guardar_A;

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
    }

    public void InicializarVistas(){
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

    private void RecuperarDatos(){
        Bundle intent = getIntent().getExtras();

        tid = intent.getString("tid");
        uid_usuario = intent.getString("uid");
        fecha_hora_actual = intent.getString("fechaCreacion");
        fecha = intent.getString("fecha");
        titulo = intent.getString("titulo");
        descripcion = intent.getString("descripcion");
        estado = intent.getString("estado");
    }

    private void SetearDatos(){
        Uid_Usuario_A.setText(uid_usuario);
        Fecha_A.setText(fecha);
        Fecha_hora_Actual_A.setText(fecha_hora_actual);
        Titulo_A.setText(titulo);
        Descripcion_A.setText(descripcion);
        Tid_A.setText(tid);
        Estado_A.setText(estado);
    }

}
