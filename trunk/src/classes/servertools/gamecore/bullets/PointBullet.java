
package classes.servertools.gamecore.bullets;

import classes.servertools.gamecore.*;
import classes.servertools.*;
import classes.*;
import java.awt.*;


/**
    Point bullet.
    @author Belicza Andras
*/
public class PointBullet extends Bullet {

    /** Physical mass of a point bullet (kg). */
    private static float MASS                   = 0.01f;
    /** Shape resistance rate of a point bullet. */
    private static float SHAPE_RESISTANCE_RATE  = 0.00015f;
    /** Physical capacity of a point bullet (m^3). */
    private static float CAPACITY               = 0.0000003f;
    /** Anti gravitation rate of a point bullet. */
    private static float ANTI_GRAVITATION_RATE  = 0.97f;
    /** Width of a point bullet. */
    private static int   WIDTH                  = 4;
    /** Height of a point bullet. */
    private static int   HEIGHT                 = WIDTH;
    /** Velocity of a point bullet. */
    private static float VELOCITY               = 8.0f;
    /** Color of a point bullet. */
    private static Color COLOR                  = Color.yellow;

    /** Damage of the point bullet. */
    private transient final int damage;
    /** Tells whether the bullet is double sized. */
    private final boolean       doubleSized;
    
    /**
        Creates a new PointBullet.
        @param position initial position of the point bullet
        @param direction angle of direction this point bullet was shot in
        @param damage damage of this point bullet
        @param doubleSized tells whether this bullet is double sized
        @param shooterPlayer reference to the player who fired this point bullet
    */
    public PointBullet( final FloatVector position, final double direction, final int damage, final boolean doubleSized, final Player shooterPlayer ) {
        super( MASS, SHAPE_RESISTANCE_RATE, CAPACITY, ANTI_GRAVITATION_RATE, WIDTH, HEIGHT, -1, position, direction, VELOCITY, shooterPlayer );
        this.damage      = damage;
        this.doubleSized = doubleSized;
    }

    /**
        Called when this bullet collides with something.
        @param gameCoreHandler reference to the game core handler
        @param hitsMapTowardAxes contains which axes this bullet hits the map toward
        @param hitWallIndices contains the indices of the wall hit by this bullet
        @param hitsPlayerTowardAxes contains which axes this bullet hits one of the players toward
        @param hitPlayer player hit by this bullet
        @param ownBulletIndex own index in bullets vector
    */
    public void collides( final GameCoreHandler gameCoreHandler, final int hitsMapTowardAxes, final Point hitWallIndices, final int hitsPlayerTowardAxes, final Player hitPlayer, final int ownBulletIndex ) {
        if ( hitsMapTowardAxes != MovingObjectConsts.STEPPING_AXIS_NONE ) {
            gameCoreHandler.damageWall( hitWallIndices.x, hitWallIndices.y, damage );
            gameCoreHandler.getBullets().removeElementAt( ownBulletIndex );
        }
        else
            if ( hitsPlayerTowardAxes != MovingObjectConsts.STEPPING_AXIS_NONE ) {
                gameCoreHandler.damagePlayer( hitPlayer, damage, shooterPlayer );
                gameCoreHandler.getBullets().removeElementAt( ownBulletIndex );
            }
    }
    
    /**
        Draws the bullet.
        @param graphicsContext graphics context in wich to draw
    */
    public void draw( final Graphics graphicsContext ) {
        graphicsContext.setColor( COLOR );
        final int positionX = (int) position.x, positionY = (int) position.y;
        graphicsContext.drawLine( positionX, positionY, positionX + ( doubleSized ? 1 : 0 ), positionY );
        if ( doubleSized )
            graphicsContext.drawLine( positionX, positionY + 1, positionX + 1, positionY + 1 );
    }
    
}
