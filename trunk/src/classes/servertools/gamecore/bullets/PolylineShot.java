
package classes.servertools.gamecore.bullets;

import classes.servertools.*;
import classes.servertools.gamecore.*;
import java.awt.*;
import java.io.*;
import java.util.Vector;            // Just Vector, because reference to Map would be ambigous.


/**
    A polyline shot.
    @author Belicza Andras
*/
public abstract class PolylineShot implements Serializable {

    /** Reference to the player who shot this polyline shot. */
    protected transient final     Player shooterPlayer;
    /** X coordinates of the points of this polyline shot. */
    protected final int[]         xPoints;
    /** Y coordinates of the points of this polyline shot. */
    protected final int[]         yPoints;
    /** Color of this polyline shot. */
    private final Color           color;
    /** Initial value of cycle counter. */
    protected transient final int initialCycleCounter;

    /**
        Creates a new PolylineShot.
        @param initialCycleCounter value of cycle counter when this polyline shot was shot
        @param points vector of points
        @param color color of the polyline shot
        @param shooterPlayer reference to the player who shot this polyline shot
    */
    protected PolylineShot( final int initialCycleCounter, final Vector points, final Color color, final Player shooterPlayer ) {
        this.initialCycleCounter = initialCycleCounter;
        this.color               = color;
        this.shooterPlayer       = shooterPlayer;
        xPoints = new int[ points.size()  ];
        yPoints = new int[ xPoints.length ];
        Point point;
        for ( int pointIndex = 0; pointIndex < xPoints.length; pointIndex++ ) {
            point = (Point) points.elementAt( pointIndex );
            xPoints[ pointIndex ] = point.x;
            yPoints[ pointIndex ] = point.y;
        }
    }

    /**
        Draws the polyline shot.
        @param graphicsContext graphics context in wich to draw
    */
    public void draw( final Graphics graphicsContext ) {
        graphicsContext.setColor( color );
        graphicsContext.drawPolyline( xPoints, yPoints, xPoints.length );
    }
    
    /**
        Tests whether this laser hits the specified player.
        @param player player who to test
        @return true, if this laser hits the specified player; false otherwise
    */
    public boolean hitsPlayer( final Player player ) {
        return false;
    }
    
    /**
        Checks the cycle whether we reached the end cycle, and that case we execute a self-destruction.
        @param cycleCounter actual value of cycle counter
        @param polylineShots reference the the polyline shots
        @param ourPolylineShotIndex our index in polyline shots vector
        @return true if we reached end cycle; false otherwise
    */
    public boolean reachedEndCycle( final int cycleCounter, final Vector polylineShots, final int ourPolylineShotIndex ) {
        if ( initialCycleCounter != cycleCounter ) {
            polylineShots.removeElementAt( ourPolylineShotIndex );
            return true;
        }
        return false;
    }

    /**
        Damages a player. This is an empty implementation.
        @param player player to be damaged
        @param gameCoreHandler reference to the game core handler
    */
    public void damagePlayer( final Player player, final GameCoreHandler gameCoreHandler ) {
    }

    /**
        Damages all walls hit by this polyline shot. This is an empty implementation.
        @param map reference to the map
        @param gameCoreHandler reference to the game core handler
    */
    public void damageWalls( final Map map, final GameCoreHandler gameCoreHandler ) {
    }
    
}
