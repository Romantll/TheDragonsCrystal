package com.kirbits.thedragonscrystal.models;

import java.util.ArrayList;
import java.util.List;

public class FlowNode {
    private final int id;
    private int x, y;
    private boolean isVisited;
    private boolean isEnding;
    private boolean isDeath;
    private boolean isUnknown;
    private boolean isPlaceholder;
    private boolean isVisible;
    private List<Integer> children;

    public FlowNode(int id, int x, int y, boolean isVisited, boolean isEnding) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.isVisited = isVisited;
        this.isEnding = isEnding;
        this.isDeath = false;
        this.isUnknown = false;
        this.children = new ArrayList<>();
    }

    public int getId() { return id; }
    public int getX() { return x; }
    public int getY() { return y; }
    public boolean isVisited() { return isVisited; }
    public boolean isEnding() { return isEnding; }
    public boolean isDeath() { return isDeath; }
    public boolean isPlaceholder(){ return  isPlaceholder; }
    public boolean isUnknown() { return isUnknown; }
    public boolean isVisible() {return isVisible; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setVisited(boolean visited) { isVisited = visited; }
    public void setEnding(boolean ending) { isEnding = ending; }
    public void setDeath(boolean death) { isDeath = death; }
    public void setUnknown(boolean unknown) { isUnknown = unknown; }
    public void setPlaceholder(boolean placeholder) { this.isPlaceholder = placeholder; }
    public void setVisible(boolean visible) {this.isVisible = visible; }

    public List<Integer> getChildren() { return children; }
    public void setChildren(List<Integer> children) { this.children = children; }
}
