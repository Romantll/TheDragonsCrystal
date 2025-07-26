package com.kirbits.thedragonscrystal.models;

import java.util.List;

public class FlowNode {
    private int id;
    private int x;
    private int y;
    private boolean visited;
    private boolean ending;
    private String label;
    private List<Integer> children; // For connecting to other nodes

    // Full constructor (with children & label)
    public FlowNode(int id, int x, int y, boolean visited, boolean ending, String label, List<Integer> children) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.visited = visited;
        this.ending = ending;
        this.label = label;
        this.children = children;
    }

    public static List<FlowNode> buildFromSave(SaveData saveData) {
        List<FlowNode> nodes = new java.util.ArrayList<>();
        java.util.Set<Integer> visited = new java.util.HashSet<>(saveData.getVisitedPageIds());
        java.util.Set<String> endings = saveData.getUnlockedEndings();

        // Example: Generate 30 nodes. Replace with dynamic if needed.
        for (int i = 0; i < 30; i++) {
            boolean isVisited = visited.contains(i);
            boolean isEnding = endings.contains("Ending: " + i);
            nodes.add(new FlowNode(i, i % 5, i / 5, isVisited, isEnding)); // X/Y for layout
        }
        return nodes;
    }


    // Overloaded constructor without label (defaults label to id)
    public FlowNode(int id, int x, int y, boolean visited, boolean ending, List<Integer> children) {
        this(id, x, y, visited, ending, String.valueOf(id), children);
    }

    // Overloaded constructor without children (defaults to null)
    public FlowNode(int id, int x, int y, boolean visited, boolean ending) {
        this(id, x, y, visited, ending, String.valueOf(id), null);
    }

    // Getters
    public int getId() { return id; }
    public int getX() { return x; }
    public int getY() { return y; }
    public boolean isVisited() { return visited; }
    public boolean isEnding() { return ending; }
    public String getLabel() { return label; }
    public List<Integer> getChildren() { return children; }

    // Setters 
    public void setLabel(String label) { this.label = label; }
    public void setChildren(List<Integer> children) { this.children = children; }
}
