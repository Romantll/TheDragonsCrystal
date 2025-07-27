package com.kirbits.thedragonscrystal.models;

import java.util.List;

public class FlowNode {
    private int id;
    private int x;
    private int y;
    private boolean visited;
    private boolean ending;
    private boolean death;
    private String label;
    private List<Integer> children;

    // Full constructor
    public FlowNode(int id, int x, int y, boolean visited, boolean ending, String label, List<Integer> children) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.visited = visited;
        this.ending = ending;
        this.label = label;
        this.children = children;
        this.death = false; // default
    }

    // Overloaded constructor
    public FlowNode(int id, int x, int y, boolean visited, boolean ending) {
        this(id, x, y, visited, ending, String.valueOf(id), null);
    }

    // Getters
    public int getId() { return id; }
    public int getX() { return x; }
    public int getY() { return y; }
    public boolean isVisited() { return visited; }
    public boolean isEnding() { return ending; }
    public boolean isDeath() { return death; }
    public String getLabel() { return label; }
    public List<Integer> getChildren() { return children; }

    // Setters
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setLabel(String label) { this.label = label; }
    public void setChildren(List<Integer> children) { this.children = children; }
    public void setDeath(boolean death) { this.death = death; }
}
