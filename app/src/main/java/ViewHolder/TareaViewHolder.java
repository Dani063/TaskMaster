package ViewHolder;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmaster.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Objects.Tarea;

public class TareaViewHolder extends RecyclerView.ViewHolder {
    View mView;
    CheckBox checkboxEstado;
    private ClickListener mClickListener;


    public interface ClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public void setOnClickListener(ClickListener clickListener){
        mClickListener = clickListener;
    }



    public TareaViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;


        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v, getBindingAdapterPosition());
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mClickListener.onItemLongClick(v, getBindingAdapterPosition());
                return false;
            }
        });
    }

    public void SetearDatos(Context context, String titulo, String descripcion, String fecha,
                            String fechaCreacion, String estado, String tid, String uid){
        //Declarar vistas
        TextView tituloItem, descripcionItem, fechaItem, fecha_hora_actualItem, estadoItem,
                Tid_TareaItem, Uid_UsuarioItem;

        // Establecer la conexion con el item
        tituloItem = mView.findViewById(R.id.tituloItem);
        descripcionItem = mView.findViewById(R.id.descripcionItem);
        fechaItem = mView.findViewById(R.id.fechaItem);
        fecha_hora_actualItem = mView.findViewById(R.id.fecha_hora_ActualItem);
        estadoItem = mView.findViewById(R.id.estadoItem);
        Tid_TareaItem = mView.findViewById(R.id.Tid_TareaItem);
        Uid_UsuarioItem = mView.findViewById(R.id.Uid_UsuarioItem);
        checkboxEstado = mView.findViewById(R.id.checkboxEstado);

        //Setear info en el item
        tituloItem.setText(titulo);
        descripcionItem.setText(descripcion);
        fechaItem.setText(fecha);
        fecha_hora_actualItem.setText(fechaCreacion);
        estadoItem.setText(estado);
        Tid_TareaItem.setText(tid);
        Uid_UsuarioItem.setText(uid);
        checkboxEstado.setChecked("Finalizado".equals(estado));
        checkboxEstado.setOnClickListener(v -> {
            boolean isChecked = checkboxEstado.isChecked();
            DatabaseReference databaseReference;
            databaseReference = FirebaseDatabase.getInstance().getReference("Tareas").child(tid);
            if (isChecked) {
                databaseReference.child("estado").setValue("Finalizado");
            } else {
                databaseReference.child("estado").setValue("No finalizado");
            }
        });
    }
}