package com.kirbits.thedragonscrystal.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Color;


import androidx.appcompat.app.AppCompatActivity;

import com.kirbits.thedragonscrystal.R;
import com.kirbits.thedragonscrystal.models.FlowNode;
import com.kirbits.thedragonscrystal.models.SaveData;
import com.kirbits.thedragonscrystal.utils.SaveManager;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EndingsActivity extends AppCompatActivity {

    private TextView noDataText;
    private ImageView graphImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endings);

        noDataText = findViewById(R.id.no_data_text);
        graphImage = new ImageView(this);
        ((android.widget.FrameLayout) findViewById(R.id.graph_container)).addView(graphImage);

        SaveData saveData = SaveManager.loadGame(this, 1); // Load slot 1 for now

        if (saveData == null) {
            noDataText.setVisibility(View.VISIBLE);
            return;
        }

        List<FlowNode> nodes = FlowNode.buildFromSave(saveData);

        // Build graph
        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();
        Map<Integer, Object> vertexMap = new HashMap<>();

        graph.getModel().beginUpdate();
        try {
            for (FlowNode node : nodes) {
                String color = node.isEnding() ? "#FFD700" : node.isVisited() ? "#8A2BE2" : "#808080";
                Object v = graph.insertVertex(parent, null, node.getLabel(), 0, 0, 120, 60,
                        "fillColor=" + color + ";fontColor=#FFFFFF;strokeColor=#000000;rounded=1");
                vertexMap.put(node.getId(), v);
            }

            for (FlowNode node : nodes) {
                if (node.getChildren() != null) {
                    for (int childId : node.getChildren()) {
                        if (vertexMap.containsKey(childId)) {
                            graph.insertEdge(parent, null, "", vertexMap.get(node.getId()), vertexMap.get(childId));
                        }
                    }
                }
            }
        } finally {
            graph.getModel().endUpdate();
        }

        // Layout
        mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
        layout.setOrientation(1);
        layout.setInterRankCellSpacing(150);
        layout.setIntraCellSpacing(80);
        layout.execute(parent);

        // Render as Bitmap
        Bitmap graphBitmap = mxCellRenderer.createBufferedImage(graph, null, 1, null, true, null);
        graphImage.setImageBitmap(graphBitmap);
    }


}
