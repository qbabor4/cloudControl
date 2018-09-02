package pl.qbabor.cloud.protocol;

/**
 * Created by Jakub on 09-Apr-18.
 * generyka zrobić
 */

public enum EProtocol {

    /* Protocol chars */
    START('<'),
    END('>'),
    SEPARATOR('#'),

    /* Protocol strings */
    COLOR("col"),
    RAINBOW("rbw"),
    BRIGHTNESS("bgh"),
    RAINBOW_MODE("mod");

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
