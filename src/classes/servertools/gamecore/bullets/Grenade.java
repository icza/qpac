
package classes.servertools.gamecore.bullets;

import classes.servertools.gamecore.*;
import classes.servertools.*;
import java.awt.*;
import java.util.*;


/**
    Grenade. Abstract because explode() method is not (re)defined.
    @author Belicza Andras
*/
public abstract class Grenade extends Bullet {

    /** Physical mass of a grenade (kg). */
    private static float MASS                                 = 0.5f;
    /** Shape resistance rate of a grenade. */
    private static float SHAPE_RESISTANCE_RATE                = 0.015f;
    /** Physical capacity of a grenade (m^3). */
    private static float CAPACITY                             = 0.00015f;
    /** Anti gravitation rate of a grenade. */
    private static float ANTI_GRAVITATION_RATE                = 0.6f;
    /** Width of a grenade. */
    private static int   WIDTH                                = 6;
    /** Height of a grenade. */
    private static int   HEIGHT                               = WIDTH;
    /** Velocity of a grenade. */
    private static float VELOCITY                             = 7.0f;
    /** Remainig velocity rate at collisions. */
    private static float REMAINING_VELOCITY_RATE_AT_COLLISION = 0.8f;
    /** Color of a grenade. */
    private static Color COLOR                                = Color.yellow;
    /** Time until a fired grenade will explode (cout of cycles). */
    private static int   DETONATION_TIME                      = 80;
    
    /**
        Creates a new Grenade.
        @param initialCycleCounter value of cycle counter when this grenade was launched
        @param position initial position of the grenade
        @param direction angle of direction this grenade was launched in
        @param shooterPlayer reference to the player who launched this grenade
    */
    public Grenade( final int initialCycleCounter, final FloatVector position, final double direction, final Player shooterPlayer ) {
        super( MASS, SHAPE_RESISTANCE_RATE, CAPACITY, ANTI_GRAVITATION_RATE, WIDTH, HEIGHT, initialCycleCounter, position, direction, VELOCITY, shooterPlayer );
    }
    
    /**
        Called when this grenade collides with something.
        @param gameCoreHandler reference to the game core handler
        @param hitsMapTowardAxes contains which axes this grenade hits the map toward
        @param hitWallIndices contains the indices of the wall hit by this grenade
        @param hitsPlayerTowardAxes contains which axes this grenade hits one of the players toward
        @param player player hit by this grenade
        @param ourBulletIndex our index in bullets vector
    */
    public void collides( final GameCoreHandler gameCoreHandler, final int hitsMapTowardAxes, final Point hitWallIndices, final int hitsPlayerTowardAxes, final Player player, final int ourBulletIndex ) {
        if ( ( ( hitsMapTowardAxes | hitsPlayerTowardAxes ) & MovingObjectConsts.STEPPING_AXIS_X ) != MovingObjectConsts.STEPPING_AXIS_NONE )
            velocity.x = -REMAINING_VELOCITY_RATE_AT_COLLISION * velocity.x;
        if ( ( ( hitsMapTowardAxes | hitsPlayerTowardAxes ) & MovingObjectConsts.STEPPING_AXIS_Y ) != MovingObjectConsts.STEPPING_AXIS_NONE )
            velocity.y = -REMAINING_VELOCITY_RATE_AT_COLLISION * velocity.y;
    }
    
    /**
        Draws the grenade.
        @param graphicsContext graphics context in wich to draw
    */
    public void draw( final Graphics graphicsContext ) {
        graphicsContext.setColor( COLOR );
        graphicsContext.fillOval( (int) position.x - dimensionToLeft, (int) position.y - dimensionToUp, dimensionToLeft + dimensionToRight + 1, dimensionToUp + dimensionToDown + 1 );
    }
    
    /**
        Returns true.
        @return true
    */
    public boolean isExplodable() {
        return true;
    }

    /**
        Checks the cycle whether we reached the end cycle, and then executes a self-destruction.
        @param cycleCounter actual value of cycle counter
        @param bulletIndex our index in bullets vector
        @return true if we reached the end cycle, and the grenade just exploded; false otherwise
    */
    public boolean reachedEndCycle( final int cycleCounter, final GameCoreHandler gameCoreHandler, final int bulletIndex ) {
        if ( cycleCounter - initialCycleCounter > DETONATION_TIME ) {
            explode( gameCoreHandler.getBullets(), bulletIndex, gameCoreHandler.getExplosions() );
            return true;
        }
        return false;
    }

}
