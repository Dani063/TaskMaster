package Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.example.taskmaster.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Objects.Subtarea;

public class SubtareaAdapter extends RecyclerView.Adapter<SubtareaAdapter.SubtareaViewHolder> {

    private List<Subtarea> subtareaList;

    public SubtareaAdapter(List<Subtarea> subtareaList) {
        this.subtareaList = subtareaList;
    }

    @NonNull
    @Override
    public SubtareaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subtarea, parent, false);
        return new SubtareaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubtareaViewHolder holder, int position) {
        Subtarea subtarea = subtareaList.get(position);
        holder.subtareaTextView.setText(subtarea.getNombre());
        holder.bind(subtarea);
    }

    @Override
    public int getItemCount() {
        return subtareaList.size();
    }

    static class SubtareaViewHolder extends RecyclerView.ViewHolder {
        TextView subtareaTextView;
        CheckBox subtareaCheckBox;

        SubtareaViewHolder(View itemView) {
            super(itemView);
            subtareaTextView = itemView.findViewById(R.id.tituloItemS);
            subtareaCheckBox = itemView.findViewById(R.id.checkboxEstadoS);
        }

        void bind(Subtarea subtarea) {
            subtareaTextView.setText(subtarea.getNombre());
            DatabaseReference databaseReferenceAux = FirebaseDatabase.getInstance().getReference("Subtareas").child(subtarea.getSid());

            // Obtener el estado de la subtarea desde la base de datos y mostrarlo mediante la checkbox
            databaseReferenceAux.child("estado").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String estado = dataSnapshot.getValue(String.class);
                        subtareaCheckBox.setChecked("Finalizado".equals(estado));
                        System.out.println(estado);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.out.println(""+error.getMessage());
                }
            });
            // Actualizar el estado de la subtarea en la base de datos al cambiar el estado de la checkbox
            
            subtareaCheckBox.setOnCheckedChangeListener((buttonView, isChecked) ->{
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Subtareas").child(subtarea.getSid());
                if (isChecked) {
                    databaseReference.child("estado").setValue("Finalizado");
                } else {
                    databaseReference.child("estado").setValue("No finalizado");
                }
            });
        }
    }
}
