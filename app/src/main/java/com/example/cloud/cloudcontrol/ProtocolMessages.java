package com.example.cloud.cloudcontrol;

/**
 * Created by Jakub on 09-Apr-18.
 */

public class ProtocolMessages {

    public static String getRainbowFrame(){
        return getMainFrame(EProtocol.RAINBOW.getString() + EProtocol.SEPARATOR.getChar());
    }

    public static String getColorFrame(String color){
        return getMainFrame(EProtocol.COLOR.getString() + EProtocol.SEPARATOR.getChar()+ color );
    }

    private static String getMainFrame(String msg){
        return(EProtocol.START.getChar() + msg  + EProtocol.END.getChar());
    }
}
