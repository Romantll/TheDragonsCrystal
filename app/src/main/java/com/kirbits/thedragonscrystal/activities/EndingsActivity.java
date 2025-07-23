package com.kirbits.thedragonscrystal.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kirbits.thedragonscrystal.R;
import com.kirbits.thedragonscrystal.models.SaveData;
import com.kirbits.thedragonscrystal.models.Node;
import com.kirbits.thedragonscrystal.adapters.NodeAdapter;
import com.kirbits.thedragonscrystal.utils.SaveManager;

import java.util.ArrayList;
import java.util.List;

public class EndingsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NodeAdapter adapter;
    private TextView noDataText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endings);

        recyclerView = findViewById(R.id.recycler_view_nodes);
        noDataText = findViewById(R.id.no_data_text);

        // Load the latest save (slot 1 by default for now)
        SaveData saveData = SaveManager.loadGame(this, 1);

        if (saveData == null) {
            // No save data exists
            noDataText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            return;
        }

        // Build a list of nodes based on visited pages and endings
        List<Node> nodes = buildNodeList(saveData);

        // Set up RecyclerView
        adapter = new NodeAdapter(nodes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }

    /**
     * Converts SaveData into a list of Nodes for the flowchart.
     */
    private List<Node> buildNodeList(SaveData saveData) {
        List<Node> nodes = new ArrayList<>();
        for (int pageId : saveData.getVisitedPageIds()) {
            boolean isEnding = saveData.getUnlockedEndings().contains("Ending: " + pageId);
            nodes.add(new Node(pageId, isEnding, true));  // visited page
        }

        // Add a placeholder for unexplored nodes (example: "?")
        for (int i = nodes.size(); i < 30; i++) { // 30 is arbitrary; you can base it on total pages
            nodes.add(new Node(i, false, false)); // unexplored
        }

        return nodes;
    }
}
