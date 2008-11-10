
package classes.servertools.gamecore;

import java.io.*;
import classes.utilities.*;


/**
    A 2 dimension float precision vector.
    @author Belicza Andras
*/
public class FloatVector implements Serializable {

    /** x coordinate of the vector. */
    public float x;
    /** y coordinate of the vector. */
    public float y;

    /**
        Creates a new FloatVector.
    */
    public FloatVector() {
    }

    /**
        Creates a new FloatVector.
        @param x x coordinate of the vector
        @param y y coordinate of the vector
    */
    public FloatVector( final float x, final float y ) {
        this.x = x;
        this.y = y;
    }

    /**
        Calculates the length of the vector.
        @return the length of the vector
    */
    public float length() {
        return (float) Math.sqrt( x * x + y * y );
    }
    
    /**
        Calculates the distance between the end points of 2 vector
        @param vector2 the other vector
        @return the distance of the end points of the 2 vector (the difference)
    */
    public float distanceTo( final FloatVector vector2 ) {
        return (float) Math.sqrt( ( vector2.x - x ) * ( vector2.x  - x ) + ( vector2.y - y ) * ( vector2.y - y ) );
    }

    /**
        Calculates the angle of the vector.
        @return the angle of the vector
    */
    public double angle() {
        return Math.atan2( x, y ) - MathUtils.HALF_PI;
    }
    
    /**
        Calculates the angle of 2 vector.
        @param vector2 angle with this vector
        @return the angle of the 2 vector
    */
    public double angleTo( final FloatVector vector2 ) {
        return Math.atan2( vector2.x - x, vector2.y - y ) - MathUtils.HALF_PI;
    }
    
}
