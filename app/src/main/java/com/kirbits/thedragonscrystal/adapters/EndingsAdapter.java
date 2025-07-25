package com.kirbits.thedragonscrystal.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kirbits.thedragonscrystal.R;

import java.util.List;

public class EndingsAdapter extends RecyclerView.Adapter<EndingsAdapter.EndingViewHolder> {

    private final List<String> allEndings;
    private final List<String> unlockedEndings;

    public EndingsAdapter(List<String> allEndings, List<String> unlockedEndings) {
        this.allEndings = allEndings;
        this.unlockedEndings = unlockedEndings;
    }

    @NonNull
    @Override
    public EndingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for each ending card
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ending, parent, false);
        return new EndingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EndingViewHolder holder, int position) {
        String endingName = allEndings.get(position);

        // Show ending name if unlocked, otherwise "???"
        if (unlockedEndings.contains(endingName)) {
            holder.endingText.setText(endingName);
            holder.cardView.setCardBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.purple_500)); // Unlocked = purple
        } else {
            holder.endingText.setText("???");
            holder.cardView.setCardBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.darker_gray)); // Locked = gray
        }
    }

    @Override
    public int getItemCount() {
        return allEndings.size();
    }

    static class EndingViewHolder extends RecyclerView.ViewHolder {
        TextView endingText;
        CardView cardView;

        public EndingViewHolder(@NonNull View itemView) {
            super(itemView);
            endingText = itemView.findViewById(R.id.ending_text);
            cardView = itemView.findViewById(R.id.ending_card);
        }
    }
}
