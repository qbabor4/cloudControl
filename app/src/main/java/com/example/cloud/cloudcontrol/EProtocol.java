package com.example.cloud.cloudcontrol;

/**
 * Created by Jakub on 09-Apr-18.
 */

public enum EProtocol {
    START('<'),
    END('>'),
    SEPARATOR('#'),
    COLOR("col"),
    RAINBOW("rbw");

    private char protocolChar;
    private String protocolStr;

    EProtocol(char protocolChar){
        this.protocolChar = protocolChar;
    }

    EProtocol(String protocolStr){
        this.protocolStr = protocolStr;
    }

    public char getChar(){
        return protocolChar;
    }

    public String getString(){
        return protocolStr;
    }
}
