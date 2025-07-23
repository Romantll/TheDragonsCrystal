package com.kirbits.thedragonscrystal.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kirbits.thedragonscrystal.R;
import com.kirbits.thedragonscrystal.models.Node;

import java.util.List;

public class NodeAdapter extends RecyclerView.Adapter<NodeAdapter.NodeViewHolder> {

    private final List<Node> nodeList;

    public NodeAdapter(List<Node> nodeList) {
        this.nodeList = nodeList;
    }

    @NonNull
    @Override
    public NodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_node, parent, false);
        return new NodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NodeViewHolder holder, int position) {
        Node node = nodeList.get(position);

        // Display the node ID or "?" if unvisited
        if (node.isVisited()) {
            holder.nodeText.setText(String.valueOf(node.getId()));
        } else {
            holder.nodeText.setText("?");
        }

        // Set color based on type
        if (node.isEnding()) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FFD700")); // Gold for endings
        } else if (node.isVisited()) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#8A2BE2")); // Purple for visited
        } else {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#808080")); // Gray for unvisited
        }
    }

    @Override
    public int getItemCount() {
        return nodeList.size();
    }

    static class NodeViewHolder extends RecyclerView.ViewHolder {
        TextView nodeText;
        CardView cardView;

        public NodeViewHolder(@NonNull View itemView) {
            super(itemView);
            nodeText = itemView.findViewById(R.id.node_text);
            cardView = itemView.findViewById(R.id.node_card);
        }
    }
}
