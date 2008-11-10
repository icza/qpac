
package classes.utilities;


/**
    Mathematical utility functions.
    @author Belicza Andras
*/
public class MathUtils {
    
    /** The half of the mathematical pi. */
    public static final double HALF_PI           = Math.PI / 2.0;
    /** One and a half of the mathematical pi. */
    public static final double ONE_AND_A_HALF_PI = Math.PI * 1.5;
    /** The double of the mathematical pi. */
    public static final double TWO_PI            = Math.PI * 2.0;

    /**
        Tests whether a double number is between two other double number.
        @param x double number to be tested
        @param x1 the smaller double comparable number
        @param x2 the greater double comparable number
        @return true if x >= x1 and x <= x2; false otherwise
    */
    public static boolean between( final double x, final double x1, final double x2 ) {
        return x >= x1 && x <= x2;
    }

    /**
        Tests whether an integer number is between two other integer number.
        @param x integer number to be tested
        @param x1 the smaller integer comparable number
        @param x2 the greater integer comparable number
        @return true if x >= x1 and x <= x2; false otherwise
    */
    public static boolean between( final int x, final int x1, final int x2 ) {
        return x >= x1 && x <= x2;
    }

    /**
        Normalizes an angle: puts it in the range: [ 0.0;TWO_PI )
        @return the normalized angle
    */
    public static double normalizeAngle( double angle ) {
        return angle - Math.floor( angle / TWO_PI ) * TWO_PI;
    }
    
}
