package com.kirbits.thedragonscrystal.models;

public class Page {
    public int id;
    public String text;
    public String choice_1_text;
    public int choice_1_target;
    public String choice_2_text;
    public int choice_2_target;
    public String background;

    public Page(int id, String text, String choice_1_text, int choice_1_target, String choice_2_text, int choice_2_target, String background){

        this.id = id;
        this.text = text;
        this.choice_1_text = choice_1_text;
        this.choice_1_target = choice_1_target;
        this.choice_2_text = choice_2_text;
        this.choice_2_target = choice_2_target;
        this.background = background;

    }

    //Getters
    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getChoice1Text() {
        return choice_1_text;
    }

    public int getChoice1Target() {
        return choice_1_target;
    }

    public String getChoice2Text() {
        return choice_2_text;
    }

    public int getChoice2Target() {
        return choice_2_target;
    }

    public String getBackground(){
        return background;
    }
}
