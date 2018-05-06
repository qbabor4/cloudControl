package com.example.cloud.protocol;

/**
 * Created by Jakub on 09-Apr-18.
 */

public class ProtocolMessages {

    public static String getRainbowMessage(int brightness){
        return getRainbowModeFrame(EProtocolRainbowMode.RAINBOW, brightness);
    }

    public static String getAllTheSameChangingMessage(int brightness){
        return getRainbowModeFrame(EProtocolRainbowMode.ALL_THE_SAME, brightness);
    }

    private static String getRainbowModeFrame(EProtocolRainbowMode eProtocolRainbowMode, int brightness) {
        return getMainFrame(EProtocol.RAINBOW.getString() + EProtocol.SEPARATOR.getChar() + EProtocol.RAINBOW_MODE.getString() + eProtocolRainbowMode.getMode() + EProtocol.SEPARATOR.getChar() + EProtocol.BRIGHTNESS.getString() + brightness);
    }

    public static String getColorMessage(String color){
        return getMainFrame(EProtocol.COLOR.getString() + EProtocol.SEPARATOR.getChar()+ color );
    }

    private static String getMainFrame(String msg){
        return(EProtocol.START.getChar() + msg  + EProtocol.END.getChar());
    }

}
