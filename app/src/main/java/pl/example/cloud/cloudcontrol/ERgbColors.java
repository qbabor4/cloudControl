package pl.example.cloud.cloudcontrol;

/**
 * Created by Jakub on 04-Apr-18.
 */

public enum ERgbColors {
    BLACK("000000");

    private String color;

    ERgbColors(String color){
        this.color = color;
    }

    public String getColor(){
        return color;
    }
}
