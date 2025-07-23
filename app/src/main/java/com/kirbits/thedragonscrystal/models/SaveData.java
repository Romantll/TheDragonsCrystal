package com.kirbits.thedragonscrystal.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SaveData {
    private int currentPageId;
    private List<Integer> visitedPageIds;
    private Set<String> unlockedEndings;
    private boolean isDead;

    public SaveData() {
        this.currentPageId = 0;
        this.visitedPageIds = new ArrayList<>();
        this.unlockedEndings = new HashSet<>();
        this.isDead = false;
    }

    public int getCurrentPageId(){
        return currentPageId;
    }

    public void setCurrentPageId(int currentPageId){
        this.currentPageId = currentPageId;
    }

    public List<Integer> getVisitedPageIds(){
        return visitedPageIds;
    }

    public void setVisitedPageIds(List<Integer> visitedPageIds) {
        this.visitedPageIds = visitedPageIds;
    }

    public void addVisitedPageId(int id){
        visitedPageIds.add(id);
    }

    public Set<String> getUnlockedEndings() {
        return unlockedEndings;
    }

    public void unlockEnding(String endingId) {
        unlockedEndings.add(endingId);
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    public void setUnlockedEndings(Set<String> unlockedEndings) {
        this.unlockedEndings = unlockedEndings;
    }
}
