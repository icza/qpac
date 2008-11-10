
package classes.servertools.gamecore.bullets;

import classes.servertools.gamecore.*;
import classes.servertools.*;
import java.awt.*;
import java.util.*;

/**
    Rocket.
    @author Belicza Andras
*/
public class Rocket extends Bullet {

    /** Physical mass of a rocket (kg). */
    private static float  MASS                                 = 1.0f;
    /** Shape resistance rate of a rocket. */
    private static float  SHAPE_RESISTANCE_RATE                = 0.055f;
    /** Physical capacity of a rocket (m^3). */
    private static float  CAPACITY                             = 0.000014f;
    /** Anti gravitation rate of a rocket. */
    private static float  ANTI_GRAVITATION_RATE                = 0.85f;
    /** Width of a rocket. */
    private static int    WIDTH                                = 26;
    /** Height of a rocket. */
    private static int    HEIGHT                               = WIDTH;
    /** Velocity of a rocket. */
    private static float  VELOCITY                             = 10.0f;
    /** Color of a rocket. */
    private static Color  COLOR                                = new Color( 100, 250, 100 );
    /** Range of the explosion of the rocket. */
    private static int    EXPLOSION_RANGE                      = 90;
    /** Length of a rocket. */
    private static int    LENGTH                               = WIDTH / 2;
    /** Stroke of the rocket (at drawing). */
    private static Stroke STROKE                               = new BasicStroke( 5.0f );
    /** Damage of the explosions of the rockets. */
    public static final int  EXPLOSION_DAMAGE                  = 20;

    
    /**
        Creates a new rocket.
        @param position initial position of the rocket
        @param direction angle of direction this rocket was launched in
        @param shooterPlayer reference to the player who launched this rocket
    */
    public Rocket( final FloatVector position, final double direction, final Player shooterPlayer ) {
        super( MASS, SHAPE_RESISTANCE_RATE, CAPACITY, ANTI_GRAVITATION_RATE, WIDTH, HEIGHT, -1, position, direction, VELOCITY, shooterPlayer );
    }
    
    /**
        Called when this rocket collides with something.
        @param gameCoreHandler reference to the game core handler
        @param hitsMapTowardAxes contains which axes this rocket hits the map toward
        @param hitWallIndices contains the indices of the wall hit by this rocket
        @param hitsPlayerTowardAxes contains which axes this rocket hits one of the players toward
        @param player player hit by this rocket
        @param ourBulletIndex our index in bullets vector
    */
    public void collides( final GameCoreHandler gameCoreHandler, final int hitsMapTowardAxes, final Point hitWallIndices, final int hitsPlayerTowardAxes, final Player player, final int ourBulletIndex ) {
        explode( gameCoreHandler.getBullets(), ourBulletIndex, gameCoreHandler.getExplosions() );
    }
    
    /**
        Draws the rocket.
        @param graphicsContext graphics context in wich to draw
    */
    public void draw( final Graphics graphicsContext ) {
        final double angle    = velocity.angle();
        final float  angleSin = (float) Math.sin( angle );
        final float  angleCos = (float) Math.cos( angle );
        graphicsContext.setColor( COLOR );
        final Graphics2D graphicsContext2D = (Graphics2D) graphicsContext;
        final Stroke     oldStroke         = graphicsContext2D.getStroke();
        graphicsContext2D.setStroke( STROKE );
        graphicsContext2D.drawLine( (int) ( position.x - angleCos * ( LENGTH / 2.0f ) ), (int) ( position.y + angleSin * ( LENGTH / 2.0f ) ), (int) ( position.x + angleCos * ( LENGTH / 2.0f ) ), (int) ( position.y - angleSin * ( LENGTH / 2.0f ) ) );
        graphicsContext2D.setStroke( oldStroke );
    }
    
    /**
        Returns true.
        @return true
    */
    public boolean isExplodable() {
        return true;
    }

    /**
        This method explodes the rocket.
        @param bullets reference to the bullets
        @param ourBulletIndex our index in bullets vector
        @param explosions reference to the explosions
    */
    public void explode( final Vector bullets, final int ourBulletIndex, final Vector explosions ) {
        bullets.removeElementAt( ourBulletIndex );
        explosions.add( new Explosion( (int) position.x, (int) position.y, EXPLOSION_RANGE, EXPLOSION_DAMAGE, shooterPlayer ) );
    }
    
}
