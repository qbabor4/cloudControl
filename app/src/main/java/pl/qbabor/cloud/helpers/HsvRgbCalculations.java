package pl.qbabor.cloud.helpers;

/**
 * Created by Jakub Borówka on 2017-06-17.
 *
 */

public class HsvRgbCalculations {

    public static double getDistanceFromCenter(int x, int y, double hsvCircleRadius){
        double triangleBase = Math.abs(hsvCircleRadius - x); // hsvCircleRadius gives center x and y
        double triangleHeight = Math.abs(hsvCircleRadius - y);

        return Math.sqrt(triangleBase * triangleBase + triangleHeight * triangleHeight); //triangle diagonal (pitagoras)
    }

    public static double getSaturation(double distanceFromCenter, double hsvCircleRadius){
        return distanceFromCenter / hsvCircleRadius;
    }

    public static int getHue(int x, int y, double hsvCircleRadius){
        double angle = Math.abs (Math.atan2( (y - hsvCircleRadius), ( hsvCircleRadius - x) ) * 180 / 3.14 - 180);
        angle = (angle + 90) % 360;

        return (int)angle;
    }

    private static String decToHex(int decColor) {
        String hexColor = "";
        char hexArray[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        hexColor += hexArray[decColor % 16];
        decColor = decColor / 16;
        hexColor = hexArray[decColor % 16] + hexColor;

        return hexColor;
    }

    //changes rgb values to hex string, with addition of zeros, when there is only 1 char per one of 3 colors f.e. #ffaff (one a, not a0) // zobaczyc czy tak robi faktycznie
    public static String changeRGBColorTOHex(int red, int green, int blue) {
        return HsvRgbCalculations.decToHex(red) + HsvRgbCalculations.decToHex(green) + HsvRgbCalculations.decToHex(blue);
    }

    /**
     * Changes 0-1 double to 0-255 int
     * @param value value from seekbar
     * @return 0-255 as brightness
     */
    public static int getBrightness(double value){
        return (int)(value*255);
    }

    /// Convert HSV to RGB
    /// h is from 0-360
    /// s,v values are 0-1
    /// r,g,b values are 0-255
    public static int[] hsvToRgb(int H, double S, double V) {
        double red, green, blue;

        if (V == 0) {
            red = green = blue = 0;
        }
        else {
            double hf = H / 60.0;
            int i = (int) Math.floor( hf );
            double f = hf - i;
            double pv = V * (1 - S);
            double qv = V * (1 - S * f);
            double tv = V * (1 - S * (1 - f));

            switch (i) {
                // Red is the dominant color
                case 0:
                    red = V;
                    green = tv;
                    blue = pv;
                    break;
                // Green is the dominant color
                case 1:
                    red = qv;
                    green = V;
                    blue = pv;
                    break;
                case 2:
                    red = pv;
                    green = V;
                    blue = tv;
                    break;
                // Blue is the dominant color
                case 3:
                    red = pv;
                    green = qv;
                    blue = V;
                    break;
                case 4:
                    red = tv;
                    green = pv;
                    blue = V;
                    break;
                // Red is the dominant color
                case 5:
                    red = V;
                    green = pv;
                    blue = qv;
                    break;
                // Just in case we overshoot on our math by a little, we put these here. Since its a switch it won't slow us down at all to put these here.
                case 6:
                    red = V;
                    green = tv;
                    blue = pv;
                    break;
                case -1:
                    red = V;
                    green = pv;
                    blue = qv;
                    break;
                // The color is not defined, we should throw an error.
                default:
                    //LFATAL("i Value error in Pixel conversion, Value is %d", i);
                    red = green = blue = V; // Just pretend its black/white
                    break;
            }
        }

        return new int[] {(int)(red * 255.0), (int)(green * 255.0), (int)(blue * 255.0)};
    }
}
