
package classes.servertools.gamecore.weapons;


/**
    Describes a laser direction.
    @author Belicza Andras
*/
class LaserDirection {

    /** X coordinate of the laser. */
    public float  x;
    /** Y coordinate of the laser. */
    public float  y;
    /** Angle of the direction of the laser. */
    public double angle;
    
    /**
        Creates a new LaserDirection.
        @param x x coordinate of the laser
        @param y y coordinate of the laser
        @param angle angle of the direction of the laser
    */
    protected LaserDirection( final float x, final float y, final double angle ) {
        this.x     = x;
        this.y     = y;
        this.angle = angle;
    }

}
