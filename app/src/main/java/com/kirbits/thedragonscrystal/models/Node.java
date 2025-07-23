package com.kirbits.thedragonscrystal.models;

public class Node {
    private int id;
    private boolean isEnding;
    private boolean isVisited;

    public Node(int id, boolean isEnding, boolean isVisited) {
        this.id = id;
        this.isEnding = isEnding;
        this.isVisited = isVisited;
    }

    public int getId() { return id; }
    public boolean isEnding() { return isEnding; }
    public boolean isVisited() { return isVisited; }
}
