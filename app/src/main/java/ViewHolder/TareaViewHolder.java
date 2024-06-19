package ViewHolder;


import android.animation.Animator;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.taskmaster.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import Objects.Subtarea;
import Objects.Tarea;
import Adapter.SubtareaAdapter;

public class TareaViewHolder extends RecyclerView.ViewHolder {
    View mView;
    CheckBox checkboxEstado;
    private ClickListener mClickListener;
    //private boolean isAnimating = false;


    public interface ClickListener{
        public void onItemClick(View view, int position);
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
                databaseReference.child("filtro").setValue(uid+"/Finalizado");
                /*LottieAnimationView animationView;
                animationView = mView.findViewById(R.id.LottieTareaCompletada);
                // Mostrar y reproducir la animación
                animationView.setVisibility(View.VISIBLE);
                animationView.playAnimation();
                isAnimating = true;
                // Ocultar y parar la animación
                animationView.addAnimatorListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(@NonNull Animator animation) {
                        animationView.setVisibility(View.GONE);
                        isAnimating = false;
                    }

                    @Override
                    public void onAnimationCancel(@NonNull Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(@NonNull Animator animation) {
                    }
                });*/
            } else {
                databaseReference.child("estado").setValue("No finalizado");
                databaseReference.child("filtro").setValue(uid+"/No finalizado");
            }
        });
    }
}

class TareaClickListener implements TareaViewHolder.ClickListener {
    private Context context;
    private List<Tarea> tareas;

    public TareaClickListener(Context context, List<Tarea> tareas) {
        this.context = context;
        this.tareas = tareas;
    }

    @Override
    public void onItemClick(View view, int position) {
        // Crear y configurar el diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.subtareas, null);
        builder.setView(dialogView);

        // Inicializar vistas del diálogo
        TextView tituloSubtareas = dialogView.findViewById(R.id.SubtareasTXT);
        RecyclerView recyclerViewSubtareas = dialogView.findViewById(R.id.RecyclerViewSubtareas);

        // Configurar RecyclerView
        recyclerViewSubtareas.setLayoutManager(new LinearLayoutManager(context));
        List<Subtarea> subtareas = tareas.get(position).getSubtareas();
        SubtareaAdapter adapter = new SubtareaAdapter(subtareas);
        recyclerViewSubtareas.setAdapter(adapter);

        // Establecer el título del diálogo
        tituloSubtareas.setText("Subtareas de " + tareas.get(position).getTitulo());

        // Mostrar el diálogo
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }
}