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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EndingsActivity extends AppCompatActivity {

    private FlowChartView flowChartView;
    private TextView noDataText;

    // Spacing constants for layout
    private static final int X_SPACING = 300;
    private static final int Y_SPACING = 250;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endings);

        flowChartView = findViewById(R.id.flow_chart_view);
        noDataText     = findViewById(R.id.no_data_text);

        // Load correct slot (default to 1 if none provided)
        int slot = getIntent().getIntExtra("slot", 1);
        SaveData saveData = SaveManager.loadGame(this, slot);

        if (saveData == null) {
            noDataText.setVisibility(View.VISIBLE);
            flowChartView.setVisibility(View.GONE);
            Log.d("FlowDebug", "No save data found for slot " + slot);
            return;
        }

        // Debug: Show what data we’re using
        Log.d("FlowDebug", "=== EndingsActivity Data ===");
        Log.d("FlowDebug", "Visited pages: " + saveData.getVisitedPageIds());
        Log.d("FlowDebug", "Global pages: " + saveData.getGloballyUnlockedPages());
        Log.d("FlowDebug", "Unlocked endings: " + saveData.getUnlockedEndings());

        // Build & lay out only the subgraph the player has discovered
        List<FlowNode> nodes = buildFlowNodes(saveData);

        flowChartView.setNodes(nodes);
        flowChartView.invalidate();
    }


    /** Load story.json from assets into a List<Page> */
    private List<Page> loadStoryPages() {
        try {
            InputStream in = getAssets().open("story.json");
            InputStreamReader reader = new InputStreamReader(in);
            return new Gson().fromJson(
                    reader,
                    new TypeToken<List<Page>>(){}.getType()
            );
        } catch (Exception e) {
            Log.e("EndingsActivity", "Error loading story JSON", e);
            return Collections.emptyList();
        }
    }

    /**
     * Build exactly the subgraph the player has discovered so far,
     * plus immediate “?” placeholders for any children of visited pages.
     */
    private List<FlowNode> buildFlowNodes(SaveData saveData) {
        List<Page> allPages = loadStoryPages();
        Map<Integer, Page> pageMap = new HashMap<>();
        for (Page p : allPages) pageMap.put(p.getId(), p);

        // per‑run visited pages
        Set<Integer> visited = new HashSet<>(saveData.getVisitedPageIds());
        // global progression across all runs
        Set<Integer> global = saveData.getGloballyUnlockedPages();
        if (global == null) global = Collections.emptySet();
        // endings you’ve truly unlocked
        Set<String> endings = saveData.getUnlockedEndings();

        // determine which pages to show: always anything global or visited
        Set<Integer> visible = new HashSet<>(global);
        visible.addAll(visited);

        // add direct children for placeholders
        for (int pid : new ArrayList<>(visible)) {
            Page parent = pageMap.get(pid);
            if (parent == null) continue;
            Integer c1 = parent.getChoice1Target();
            Integer c2 = parent.getChoice2Target();
            if (c1 != null) visible.add(c1);
            if (c2 != null) visible.add(c2);
        }

        // build FlowNode objects only for those visible IDs
        Map<Integer,FlowNode> nodeMap = new HashMap<>();
        List<FlowNode> nodes = new ArrayList<>();
        for (int pid : visible) {
            Page page = pageMap.get(pid);
            boolean isEnding  = endings.contains("Ending: " + pid);
            boolean isDeath   = page != null && page.isDeath();
            //show as visited if either visited this run or in global progression
            boolean isVisited = visited.contains(pid) || global.contains(pid);

            FlowNode node = new FlowNode(pid, 0, 0, isVisited, isEnding);
            node.setDeath(isDeath);
            node.setChildren(new ArrayList<>());
            nodeMap.put(pid, node);
            nodes.add(node);
        }

        // wire up edges only to visible children
        for (FlowNode n : nodes) {
            Page p = pageMap.get(n.getId());
            if (p == null) continue;
            Integer t1 = p.getChoice1Target();
            Integer t2 = p.getChoice2Target();
            if (t1 != null && nodeMap.containsKey(t1)) n.getChildren().add(t1);
            if (t2 != null && nodeMap.containsKey(t2)) n.getChildren().add(t2);
        }

        // run your existing layout routine over the resulting subgraph
        Set<Integer> done = new HashSet<>();
        int offset = 0;
        for (Integer root : global) {
            if (!done.contains(root)) {
                offset = assignCoordinates(nodeMap, root, 0, offset, done);
            }
        }

        return nodes;
    }

    /**
     * Recursive tree‐layout: horizontal main path, branches below.
     * Returns the next “y‐offset” after laying out this subtree.
     */
    private int assignCoordinates(Map<Integer, FlowNode> nodeMap,
                                  int nodeId,
                                  int depth,
                                  int offset,
                                  Set<Integer> visited) {
        if (visited.contains(nodeId)) return offset;
        visited.add(nodeId);

        FlowNode node = nodeMap.get(nodeId);
        if (node == null) return offset;

        List<Integer> children = node.getChildren();
        int currentY = offset * Y_SPACING;

        // Death nodes slightly above their branch
        if (node.isDeath()) currentY -= Y_SPACING / 2;

        node.setX(depth * X_SPACING);
        node.setY(currentY);

        if (children.isEmpty()) {
            return offset + 1;
        }

        // ensure stable order
        children.sort(Integer::compareTo);

        // main path inline
        Integer mainChild = children.get(0);
        assignCoordinates(nodeMap, mainChild, depth + 1, offset, visited);

        // branches below
        int branchOffset = offset + 1;
        for (int i = 1; i < children.size(); i++) {
            branchOffset = assignCoordinates(
                    nodeMap,
                    children.get(i),
                    depth + 1,
                    branchOffset,
                    visited
            );
        }

        return branchOffset;
    }
}
