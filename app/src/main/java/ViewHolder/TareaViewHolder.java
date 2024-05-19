package ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmaster.R;

import Objects.Tarea;

public class TareaViewHolder extends RecyclerView.ViewHolder {
    /*View mView;
    private TareaViewHolder.ClickListener mClickListener;


    public interface ClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public void setOnClickListener(TareaViewHolder.ClickListener clickListener){
        mClickListener = clickListener;
    }*/

    TextView tituloTextView;
    TextView descripcionTextView;
    TextView fechaTextView;
    TextView fechaCreacionTextView;
    TextView estadoTextView;

    public TareaViewHolder(@NonNull View itemView) {
        super(itemView);
        /*mView = itemView;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        });*/
        tituloTextView = itemView.findViewById(R.id.tituloTextView);
        descripcionTextView = itemView.findViewById(R.id.descripcionTextView);
        fechaTextView = itemView.findViewById(R.id.fechaTextView);
        fechaCreacionTextView = itemView.findViewById(R.id.fecha_hora_ActualTextView);
        estadoTextView = itemView.findViewById(R.id.estadoTextView);
    }

    //public void SetearDatos()

    public void bind(Tarea tarea) {
        tituloTextView.setText(tarea.getTitulo());
        descripcionTextView.setText(tarea.getDescripcion());
        fechaTextView.setText(tarea.getFecha());
        fechaCreacionTextView.setText(tarea.getFechaCreacion());
        estadoTextView.setText(tarea.getEstado());
    }
}