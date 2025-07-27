package com.kirbits.thedragonscrystal.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kirbits.thedragonscrystal.R;
import com.kirbits.thedragonscrystal.models.FlowNode;
import com.kirbits.thedragonscrystal.models.Page;
import com.kirbits.thedragonscrystal.models.SaveData;
import com.kirbits.thedragonscrystal.utils.SaveManager;
import com.kirbits.thedragonscrystal.views.FlowChartView;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EndingsActivity extends AppCompatActivity {

    private FlowChartView flowChartView;
    private TextView noDataText;

    // Spacing for tree layout
    private static final int X_SPACING = 400;  // More horizontal spread
    private static final int Y_SPACING = 300;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endings);

        flowChartView = findViewById(R.id.flow_chart_view);
        noDataText = findViewById(R.id.no_data_text);

        // Load save data (slot 1 for now)
        SaveData saveData = SaveManager.loadGame(this, 1);
        if (saveData == null) {
            noDataText.setVisibility(View.VISIBLE);
            flowChartView.setVisibility(View.GONE);
            return;
        }

        // Build and layout nodes
        List<FlowNode> nodes = buildFlowNodes(saveData);

        // Display in custom view
        flowChartView.setNodes(nodes);
        flowChartView.invalidate();
    }

    /** Load story.json into a List<Page> */
    private List<Page> loadStoryPages() {
        try {
            InputStream inputStream = getAssets().open("story.json");
            InputStreamReader reader = new InputStreamReader(inputStream);
            return new Gson().fromJson(reader, new TypeToken<List<Page>>() {}.getType());
        } catch (Exception e) {
            Log.e("EndingsActivity", "Error loading story JSON", e);
            return new ArrayList<>();
        }
    }

    /** Build FlowNodes and assign tree layout positions */
    private List<FlowNode> buildFlowNodes(SaveData saveData) {
        List<FlowNode> nodes = new ArrayList<>();
        Map<Integer, FlowNode> nodeMap = new HashMap<>();

        List<Page> pages = loadStoryPages();
        Set<Integer> visited = new HashSet<>(saveData.getVisitedPageIds());
        Set<String> endings = saveData.getUnlockedEndings();

        // Step 1: Create FlowNode for every page
        for (Page page : pages) {
            boolean isVisited = visited.contains(page.getId());
            boolean isEnding = endings.contains("Ending: " + page.getId());
            boolean isDeath = page.isDeath();

            FlowNode node = new FlowNode(page.getId(), 0, 0, isVisited, isEnding);
            node.setDeath(isDeath);
            node.setChildren(new ArrayList<>());

            nodes.add(node);
            nodeMap.put(page.getId(), node);
        }

        // Step 2: Add connections (choices)
        for (Page page : pages) {
            FlowNode parentNode = nodeMap.get(page.getId());
            if (parentNode == null) continue;

            if (page.getChoice1Target() != null && nodeMap.containsKey(page.getChoice1Target())) {
                parentNode.getChildren().add(page.getChoice1Target());
            }
            if (page.getChoice2Target() != null && nodeMap.containsKey(page.getChoice2Target())) {
                parentNode.getChildren().add(page.getChoice2Target());
            }
        }

        // Step 3: Assign coordinates
        Set<Integer> visitedNodes = new HashSet<>();
        int offset = 0;
        for (Integer nodeId : nodeMap.keySet()) {
            if (!visitedNodes.contains(nodeId)) {
                offset = assignCoordinates(nodeMap, nodeId, 0, offset, visitedNodes);
            }
        }

        return nodes;
    }

    /** Recursive tree layout with horizontal main path */
    private int assignCoordinates(Map<Integer, FlowNode> nodeMap, int nodeId, int depth, int offset, Set<Integer> visited) {
        if (visited.contains(nodeId)) return offset;
        visited.add(nodeId);

        FlowNode node = nodeMap.get(nodeId);
        if (node == null) return offset;

        List<Integer> children = node.getChildren();
        int currentY = offset * Y_SPACING;

        // Death nodes slightly above their branch
        if (node.isDeath()) currentY -= Y_SPACING / 2;

        // Assign coordinates
        node.setX(depth * X_SPACING);
        node.setY(currentY);

        if (children.isEmpty()) return offset + 1;

        // Sort children to make branching consistent
        children.sort(Integer::compareTo);

        // Main path inline
        Integer mainChild = children.get(0);
        assignCoordinates(nodeMap, mainChild, depth + 1, offset, visited);

        // For branches: pull them closer in X so edges are shorter (less crossing)
        int branchOffset = offset + 1;
        for (int i = 1; i < children.size(); i++) {
            int childDepth = depth + 1;

            // Bring side branches slightly closer than the main path
            int adjustedXSpacing = (int) (X_SPACING * 0.7);  // 70% of normal spacing
            FlowNode childNode = nodeMap.get(children.get(i));
            if (childNode != null) {
                childNode.setX(node.getX() + adjustedXSpacing);
            }

            branchOffset = assignCoordinates(nodeMap, children.get(i), childDepth, branchOffset, visited);
        }

        return branchOffset;
    }





}
