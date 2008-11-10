
package classes.servertools.gamecore;

import java.awt.*;
import java.io.*;


/**
    Basics of a moving object.
    @author Belicza Andras
*/
public class MovingObject implements Serializable {

    /** Actual position of the object (m x m or pixel x pixel). */
    public              final FloatVector position = new FloatVector();
    /** Velocity of the object (m/s x m/s). */
    protected           final FloatVector velocity = new FloatVector();      // Needed for drawing rockets...
    /** The physical force affecting to the object (N x N). */
    protected transient final FloatVector force    = new FloatVector(); 
    /** The physical mass of the object (kg). */
    protected transient final float       mass;
    /** Rate of the shape resistance in air and water. */
    protected transient final float       shapeResistanceRate;
    /** The physical capacity of the object (m^3).*/
    protected transient final float       capacity;
    /** Type of position in the map (POSITION_TYPE_ON_GROUND, POSITION_TYPE_IN_AIR or POSITION_TYPE_IN_WATER). */
    protected transient int               positionType;
    /** Remaining gravitation rate is 1.0 - antiGravitation rate. If we want this object to fall down "slower" than other objects, we can give a bigger antiGravitationRate than 0.0 . */
    protected transient final float       remainingGravitationRate;
    
    /**
        Creates a new MovingObject.
        @param mass the physical mass of the object
        @param shapeResistanceRate the rate of the shape resistance in air and water
        @param capacity the physical capacity of the object
        @param antiGravitationRate the anti gravitation rate
    */
    public MovingObject( final float mass, final float shapeResistanceRate, final float capacity, final float antiGravitationRate ) {
        this.mass                     = mass;
        this.shapeResistanceRate      = shapeResistanceRate;
        this.capacity                 = capacity;
        positionType                  = MovingObjectConsts.POSITION_TYPE_IN_AIR;
        this.remainingGravitationRate = 1.0f - antiGravitationRate;
    }
    
    /**
        Steps the object in with actual velocity considering the force and mass.
        @param axis in wich direction of axis to step
        @param direction forward or backward
    */
    public void step( final int axis, final int direction ) {
        if ( direction == MovingObjectConsts.STEPPING_DIRECTION_FORWARD ) {
            if ( ( axis & MovingObjectConsts.STEPPING_AXIS_X ) != MovingObjectConsts.STEPPING_AXIS_NONE ) {
                position.x += velocity.x;
                velocity.x += force.x / mass;
            }
            if ( ( axis & MovingObjectConsts.STEPPING_AXIS_Y ) != MovingObjectConsts.STEPPING_AXIS_NONE ) {
                position.y += velocity.y;
                velocity.y += force.y / mass;
            }
        }
        if ( direction == MovingObjectConsts.STEPPING_DIRECTION_BACKWARD ) {
            if ( ( axis & MovingObjectConsts.STEPPING_AXIS_X ) != MovingObjectConsts.STEPPING_AXIS_NONE ) {
                velocity.x -= force.x / mass;
                position.x -= velocity.x;
            }
            if ( ( axis & MovingObjectConsts.STEPPING_AXIS_Y ) != MovingObjectConsts.STEPPING_AXIS_NONE ) {
                velocity.y -= force.y / mass;
                position.y -= velocity.y;
            }
        }
    }

    /**
        Tests whether this moving object hits the given window.
        @param windowX1 the x coordinate of the left side of the window
        @param windowY1 the y coordinate of the upper side of the window
        @param windowX2 the x coordinate of the right side of the window
        @param windowY2 the y coordinate of the lower side of the window
        @return true if this moving object hits the window; false otherwise
    */
    public boolean hitsWindow( final int windowX1, final int windowY1, final int windowX2, final int windowY2 ) {
        return windowX1 <= position.x && position.x <= windowX2 && windowY1 <= position.y && position.y <= windowY2 ;
    }
    
}
