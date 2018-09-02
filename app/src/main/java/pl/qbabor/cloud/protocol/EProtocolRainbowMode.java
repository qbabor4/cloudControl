package pl.qbabor.cloud.protocol;

public enum EProtocolRainbowMode {

    ALL_THE_SAME(0),
    RAINBOW(1);

    private int mode;

    EProtocolRainbowMode(int mode){
        this.mode = mode;
    }

    public int getMode(){
        return mode;
    }
}
