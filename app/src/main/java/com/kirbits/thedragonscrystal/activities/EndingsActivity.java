package com.kirbits.thedragonscrystal.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kirbits.thedragonscrystal.R;
import com.kirbits.thedragonscrystal.models.FlowNode;
import com.kirbits.thedragonscrystal.models.SaveData;
import com.kirbits.thedragonscrystal.utils.SaveManager;
import com.kirbits.thedragonscrystal.views.FlowChartView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EndingsActivity extends AppCompatActivity {

    private FlowChartView flowChartView;
    private TextView noDataText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endings);

        flowChartView = findViewById(R.id.flow_chart_view);
        noDataText = findViewById(R.id.no_data_text);

        // Load autosave by default (slot 0) or change this to load latest manual slot
        SaveData saveData = SaveManager.loadGame(this, 1);
        android.util.Log.d("FlowChartDebug", "SaveData loaded: " + (saveData != null));
        if (saveData == null) {
            android.util.Log.d("FlowChartDebug", "No save data found, showing no data text");
            // No save data found
            noDataText.setVisibility(View.VISIBLE);
            flowChartView.setNodes(new ArrayList<>());  // <-- prevent null crash
            flowChartView.setVisibility(View.GONE);
            return;
        }


        // Convert SaveData into FlowNodes for the flowchart
        List<FlowNode> nodes = buildFlowNodes(saveData);

        // Pass nodes to custom view for drawing
        flowChartView.setNodes(nodes);
    }

    /**
     * Builds a list of FlowNodes for the flowchart.
     * @param saveData The saved game data.
     * @return List of FlowNodes for rendering.
     */
    private List<FlowNode> buildFlowNodes(SaveData saveData) {
        android.util.Log.d("FlowChartDebug", "Building flow nodes...");
        List<FlowNode> nodes = new ArrayList<>();
        for (FlowNode node : nodes) {
            android.util.Log.d("FlowChartDebug",
                    "Node " + node.getId() + " at (" + node.getX() + "," + node.getY() +
                            ") visited=" + node.isVisited() + " ending=" + node.isEnding());
        }
        Set<Integer> visited = new java.util.HashSet<>(saveData.getVisitedPageIds());
        Set<String> endings = saveData.getUnlockedEndings();

        // Each page will be spaced out on a grid, 3 per row.
        int xSpacing = 300; // horizontal spacing
        int ySpacing = 300; // vertical spacing
        int columns = 3;

        for (int i = 0; i < 30; i++) { // Assume 30 pages max, adjust as needed
            boolean isVisited = visited.contains(i);
            boolean isEnding = endings.contains("Ending: " + i);

            int x = i % columns;  // grid column index
            int y = i / columns;  // grid row index


            FlowNode node = new FlowNode(i, x, y, isVisited, isEnding);
            nodes.add(node);

        }
        android.util.Log.d("FlowChartDebug", "Built " + nodes.size() + " nodes.");
        return nodes;
    }
}
