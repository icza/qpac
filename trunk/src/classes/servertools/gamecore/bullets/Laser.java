
package classes.servertools.gamecore.bullets;

import classes.servertools.*;
import classes.servertools.gamecore.*;
import java.awt.*;
import classes.utilities.*;
import java.util.Vector;            // Just Vector, because reference to Map would be ambigous.
import classes.*;


/**
    Laser.
    @author Belicza Andras
*/
public class Laser extends PolylineShot {

    /** Color of the laser. */
    private static final Color COLOR  = new Color( 250, 160, 160 );
    /** Damage of the laser. */
    private static final int   DAMAGE = 2;
    
    /**
        Creates a new Laser.
        @param initialCycleCounter value of cycle counter when this laser was shot
        @param points vector of points
        @param shooterPlayer reference to the player who shot this laser
    */
    public Laser( final int initialCycleCounter, final Vector points, final Player shooterPlayer ) {
        super( initialCycleCounter, points, COLOR, shooterPlayer );
    }

    /**
        Tests whether this laser crosses a horizontal line.
        @param x1 the smaller x coordinate of the horizontal line
        @param x2 the greater x coordinate of the horizontal line
        @param y y coordinate of the horizontal line
        @param checkFirstLine tells whether we have to check the first line too
        @return true if this laser crosses the specified horizontal line; false otherwise
    */
    private boolean crossesHorizontalLine( final int x1, final int x2, final int y, final boolean checkFirstLine ) {
        for ( int pointIndex = xPoints.length - 2; pointIndex >= ( checkFirstLine ? 0 : 1 ); pointIndex-- ) {
            final int laserY1 = yPoints[ pointIndex ];
            final int laserY2 = yPoints[ pointIndex + 1 ];
            if ( laserY1 == laserY2 )
                continue;
            if ( MathUtils.between( y, laserY1 <= laserY2 ? laserY1 : laserY2, laserY1 <= laserY2 ? laserY2 : laserY1 ) &&
                 MathUtils.between( xPoints[ pointIndex ] + ( y - laserY1 ) * ( xPoints[ pointIndex + 1 ] - xPoints[ pointIndex ] ) / ( laserY2 -laserY1 ), x1, x2 ) )
                return true;
        }
        return false;
    }

    /**
        Tests whether this laser crosses a vertival line.
        @param y1 the smaller y coordinate of the vertival line
        @param y2 the greater y coordinate of the vertival line
        @param x x coordinate of the vertival line
        @param checkFirstLine tells whether we have to check the first line too
        @return true if this laser crosses the specified vertical line; false otherwise
    */
    private boolean crossesVerticalLine( final int y1, final int y2, final int x, final boolean checkFirstLine ) {
        for ( int pointIndex = xPoints.length - 2; pointIndex >= ( checkFirstLine ? 0 : 1 ); pointIndex-- ) {
            final int laserX1 = xPoints[ pointIndex ];
            final int laserX2 = xPoints[ pointIndex + 1 ];
            if ( laserX1 == laserX2 )
                continue;
            if ( MathUtils.between( x, laserX1 <= laserX2 ? laserX1 : laserX2, laserX1 <= laserX2 ? laserX2 : laserX1 ) &&
                 MathUtils.between( yPoints[ pointIndex ] + ( x - laserX1 ) * ( yPoints[ pointIndex + 1 ] - yPoints[ pointIndex ] ) / ( laserX2 - laserX1 ), y1, y2 ) )
                return true;
        }
        return false;
    }

    /**
        Tests whether this laser hits the specified player.
        @param player player who to test
        @return true, if this laser hits the specified player; false otherwise
    */
    public boolean hitsPlayer( final Player player ) {
        final PlayerState playerState = player.getPlayerState();
        final int x1 = (int) playerState.position.x - playerState.dimensionToLeft;
        final int x2 = (int) playerState.position.x + playerState.dimensionToRight;
        final int y1 = (int) playerState.position.y - playerState.dimensionToUp;
        final int y2 = (int) playerState.position.y + playerState.dimensionToDown;
        return crossesVerticalLine( y1, y2, x1, player != shooterPlayer ) || crossesHorizontalLine( x1, x2, y1, player != shooterPlayer ) ||
               crossesVerticalLine( y1, y2, x2, player != shooterPlayer ) || crossesHorizontalLine( x1, x2, y2, player != shooterPlayer )    ;
    }

    /**
        Damages a player.
        @param player player to be damaged
        @param gameCoreHandler reference to the game core handler
    */
    public void damagePlayer( final Player player, final GameCoreHandler gameCoreHandler ) {
        gameCoreHandler.damagePlayer( player, DAMAGE, shooterPlayer );
    }
    
    /**
        Damages all walls hit by this polyline shot.
        @param map reference to the map (not used in this implementation)
        @param gameCoreHandler reference to the game core handler
    */
    public void damageWalls( final Map map, final GameCoreHandler gameCoreHandler ) {
        gameCoreHandler.damageWall( xPoints[ xPoints.length - 1 ] >> GeneralConsts.WALL_WIDTH_SHIFT, yPoints[ yPoints.length - 1 ] >> GeneralConsts.WALL_HEIGHT_SHIFT, DAMAGE );
    }
    
}
