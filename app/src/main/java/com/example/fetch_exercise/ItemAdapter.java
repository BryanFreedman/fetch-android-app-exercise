package com.example.fetch_exercise;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fetch_exercise.Item;

import java.util.List;

// ItemAdapter acts as a bridge between the data and the RecyclerView,
// converting each Item object into a visual representation
// (via a ViewHolder) for display within the RecyclerView.


public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private List<Item> items;

    public ItemAdapter(List<Item> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item view (item_view.xml)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Bind the item data
        Item item = items.get(position);
        holder.nameTextView.setText(item.getName());
        holder.listIdTextView.setText("List ID: " + item.getListId());
        holder.idTextView.setText("Item ID: " + item.getId());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // ViewHolder for RecyclerView items
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView listIdTextView;
        TextView idTextView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize the text views from item_view.xml
            nameTextView = itemView.findViewById(R.id.nameTextView);
            listIdTextView = itemView.findViewById(R.id.listIdTextView);
            idTextView = itemView.findViewById(R.id.idTextView);
        }
    }
}
