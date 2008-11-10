
package classes.servertools.gamecore.bullets;

import java.awt.*;
import classes.servertools.*;
import java.util.Vector;            // Just Vector, because reference to Map would be ambigous.
import classes.servertools.gamecore.*;


/**
    Lightning.
    @author Belicza Andras
*/
public class Lightning extends PolylineShot {

    /** Color of the laser. */
    private static final Color COLOR                          = new Color( 250, 250, 160 );
    /** Number of components of the lightning (number of lines whose form the lightning). */
    private static final int   COMPONENTS_COUNT               = 16;
    /** Dispersion of the points of the components. */
    private static final int   MAX_COMPONENT_POINT_DISPERSION = 10;
    /** Range of the explosion of the lightning. */
    private static final int   EXPLOSION_RANGE                = 20;
    /** Damage of the explosion of the lightning. */
    private static final int   EXPLOSION_DAMAGE               = 4;
    /** We draw a filled circle in place of the explosion. This gives its radius. */
    private static final int   RANGE_TO_SHOW                  = 6;

    /** Explosion of the lightning. We don't add this to the vector of explosions, because we want this to disappear with the lightning. */
    private final Explosion explosion;

    /**
        Creates a new Lightning.
        @param cycleCounter value of cycle counter when this lightning was created
        @param startPoint start point of the lightning
        @param endPoint end point of the lightning
        @param shooterPlayer reference to the player who shot this laser
    */
    public Lightning( final int cycleCounter, final Point startPoint, final Point endPoint, final Player shooterPlayer ) {
        super( cycleCounter, generateLightning( startPoint, endPoint ), COLOR, shooterPlayer );
        explosion = new Explosion( xPoints[ xPoints.length - 1 ], yPoints[ yPoints.length - 1 ], EXPLOSION_RANGE, EXPLOSION_DAMAGE, shooterPlayer );
    }

    /**
        Generates and retuns the points of the lightning.
        @param startPoint start point of the lightning
        @param endPoint end point of the lightning
        @return a vector of points of the lightning
    */
    private static Vector generateLightning( final Point startPoint, final Point endPoint ) {
        final Vector points = new Vector();
        points.add( startPoint );
        generateLightning( points, startPoint, endPoint, COMPONENTS_COUNT - 1 );
        points.add( endPoint );
        return points;
    }

    /**
        Generates lightning between two specified points.
        @param points vector of points where put the generated points to
        @param point1 first point of the generable lightning
        @param point2 last point of the generable lightning
        @param generablePointsCount number of points to generate
    */
    private static void generateLightning( final Vector points, final Point point1, final Point point2, final int generablePointsCount ) {
        final Point middlePoint = new Point( ( ( point1.x + point2.x ) >> 1 ) + (int) ( ( Math.random() - 0.5 ) * MAX_COMPONENT_POINT_DISPERSION ), ( ( point1.y + point2.y ) >> 1 ) + (int) ( ( Math.random() - 0.5 ) * MAX_COMPONENT_POINT_DISPERSION ) );
        if ( generablePointsCount >= 3 )
            generateLightning( points, point1, middlePoint, generablePointsCount >> 1 );
        points.add( middlePoint );
        if ( generablePointsCount >= 3 )
            generateLightning( points, middlePoint, point2, generablePointsCount >> 1 );
    }

    /**
        Draws the polyline shot.
        @param graphicsContext graphics context in wich to draw
    */
    public void draw( final Graphics graphicsContext ) {
        super.draw( graphicsContext );
        graphicsContext.fillOval( xPoints[ xPoints.length - 1 ] - RANGE_TO_SHOW, yPoints[ yPoints.length - 1 ] - RANGE_TO_SHOW, RANGE_TO_SHOW << 1, RANGE_TO_SHOW << 1 );
    }

    /**
        Tests whether this laser hits the specified player.
        @param player player who to test
        @return true, if this laser hits the specified player; false otherwise
    */
    public boolean hitsPlayer( final Player player ) {
        return explosion.reachesMovingObject( player.getPlayerState() );
    }
    
    /**
        Damages a player. This is an empty implementation.
        @param player player to be damaged
        @param gameCoreHandler reference to the game core handler
    */
    public void damagePlayer( final Player player, final GameCoreHandler gameCoreHandler ) {
        explosion.damagePlayer( player, gameCoreHandler );
    }

    /**
        Damages all walls hit by this polyline shot. This is an empty implementation.
        @param map reference to the map
        @param gameCoreHandler reference to the game core handler
    */
    public void damageWalls( final Map map, final GameCoreHandler gameCoreHandler ) {
        explosion.damageWalls( map, gameCoreHandler );
    }
    
}
