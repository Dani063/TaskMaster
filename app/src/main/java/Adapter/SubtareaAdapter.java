package Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.example.taskmaster.R;
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
    }

    @Override
    public int getItemCount() {
        return subtareaList.size();
    }

    static class SubtareaViewHolder extends RecyclerView.ViewHolder {
        TextView subtareaTextView;

        SubtareaViewHolder(View itemView) {
            super(itemView);
            subtareaTextView = itemView.findViewById(R.id.tituloItemS);
        }
    }
}
