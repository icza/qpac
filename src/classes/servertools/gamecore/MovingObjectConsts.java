
package classes.servertools.gamecore;


/**
    Constants of the moving objects. We separate this because moving objects are downloaded and created on all client's computer at all time.
    So we don't want these constant values to be downloaded (created) all the times.
    @author Belicza Andras
*/
public interface MovingObjectConsts {

    /** Constant value for stepping in none of directions of x and y axes. */
    int STEPPING_AXIS_NONE          = 0x00;
    /** Constant value for stepping in direction of x axis. */
    int STEPPING_AXIS_X             = 0x01;
    /** Constant value for stepping in direction of y axis. */
    int STEPPING_AXIS_Y             = 0x02;
    /** Constant value for stepping in direction both of x and y axes. */
    int STEPPING_AXIS_BOTH          = STEPPING_AXIS_X | STEPPING_AXIS_Y;
    /** The primary stepping axis: if we step, we step first along this axis. */
    int STEPPING_AXIS_PRIMARY       = STEPPING_AXIS_X;
    /** The secondary stepping axis: if we step, we step by second along this axis. */
    int STEPPING_AXIS_SECONDARY     = STEPPING_AXIS_X;
    /** Constant value for stepping forward. */
    int STEPPING_DIRECTION_FORWARD  = 0;
    /** Constant value for stepping backward. */
    int STEPPING_DIRECTION_BACKWARD = 1;
    /** Constant value for indicating beeing on the ground. */
    int POSITION_TYPE_ON_GROUND     = 0;
    /** Constant value for indicating beeing in the air. */
    int POSITION_TYPE_IN_AIR        = 1;
    /** Constant value for indicating beeing in the water. */
    int POSITION_TYPE_IN_WATER      = 2;

}
