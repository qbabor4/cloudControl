package com.example.cloud.cloudcontrol;

/**
 * Created by Jakub on 04-Apr-18.
 */

public enum Colors {
    BLACK("000000");

    private String color;

    Colors(String color){
        this.color = color;
    }

    public String getColor(){
        return color;
    }
}
