
package classes.servertools.gamecore;

import java.io.*;
import classes.servertools.*;
import classes.*;
import java.awt.*;


/**
    The explosions of the grenades and rockets.
    @author Belicza Andras
*/
public class Explosion implements Serializable {

    /** X coordinate of the position of the explosion. */
    private final int              positionX;
    /** Y coordinate of the position of the explosion. */
    private final int              positionY;
    /** Range of the explosion. */
    private int                    range;
    /** Square of the range. */
    private transient int          rangeSquare;
    /** Reference to the player whose bullet's explosion is this. */
    private transient final Player ownerPlayer;
    /** Damage of the explosion. */
    private transient final int    damage;

    /**
        The static initializer. We will fill up the zone colors array in the ExplosionConsts interface.
    */
    static {
        for ( int zoneColorIndex = ExplosionConsts.ZONE_COLORS.length - 1; zoneColorIndex >= 0; zoneColorIndex-- )
            ExplosionConsts.ZONE_COLORS[ zoneColorIndex ] = new Color( 240 - ( zoneColorIndex * ExplosionConsts.ZONE_RANGE_DIFFERENCES ) / 2, 160 - ( zoneColorIndex * ExplosionConsts.ZONE_RANGE_DIFFERENCES ) / 2, 0 );
    }
    
    /**
        Creates a new Explosion.
        @param positionX x coordinate of the position of the explosion
        @param positionY y coordinate of the position of the explosion
        @param range range of the explosion
        @param damage damage of the explosion
        @param ownerPlayer reference to the player whose bullet's explosion is this
    */
    public Explosion( final int positionX, final int positionY, final int range, final int damage, final Player ownerPlayer ) {
        this.positionX   = positionX;
        this.positionY   = positionY;
        this.damage      = damage;
        this.ownerPlayer = ownerPlayer;
        setRange( range );
    }
    
    /**
        Sets the range.
        @param range the new range
    */
    private void setRange( final int range ) {
        this.range  = range;
        rangeSquare = range * range;
    }

    /**
        Decreases the range and returns it.
        @return the range after decreasing
    */
    public int decreaseAndGetRange() {
        setRange( range - ExplosionConsts.DELTA_RANGE );
        return range;
    }

    /**
        Returns the range.
        @return the range
    */
    public int getRange() {
        return range;
    }

    /**
        Checks whether this explosion reaches a moving object.
        @param movingObject moving object to be checked
        @return true if the moving object is whithin range; false otherwise
    */
    public boolean reachesMovingObject( final MovingObject movingObject ) {
        final int deltaX = (int) movingObject.position.x - positionX;
        final int deltaY = (int) movingObject.position.y - positionY;
        return deltaX * deltaX + deltaY * deltaY <= rangeSquare ? true : false;
    }

    /**
        Damages a player.
        @param player player to be damaged
        @param gameCoreHandler reference to the game core handler
    */
    public void damagePlayer( final Player player, final GameCoreHandler gameCoreHandler ) {
        gameCoreHandler.damagePlayer( player, damage, ownerPlayer );
    }

    /**
        Damages all walls hit by this polyline shot. This is an empty implementation.
        @param map reference to the map
        @param gameCoreHandler reference to the game core handler
    */
    public void damageWalls( final Map map, final GameCoreHandler gameCoreHandler ) {
        final int minPositionX = ( ( ( positionX - range ) >> GeneralConsts.WALL_WIDTH_SHIFT  ) << GeneralConsts.WALL_WIDTH_SHIFT  ) + ( GeneralConsts.WALL_WIDTH  >> 1 );
        final int minPositionY = ( ( ( positionY - range ) >> GeneralConsts.WALL_HEIGHT_SHIFT ) << GeneralConsts.WALL_HEIGHT_SHIFT ) + ( GeneralConsts.WALL_HEIGHT >> 1 );
        final int maxPositionX = ( ( ( positionX + range ) >> GeneralConsts.WALL_WIDTH_SHIFT  ) << GeneralConsts.WALL_WIDTH_SHIFT  ) + ( GeneralConsts.WALL_WIDTH  >> 1 );
        final int maxPositionY = ( ( ( positionY + range ) >> GeneralConsts.WALL_HEIGHT_SHIFT ) << GeneralConsts.WALL_HEIGHT_SHIFT ) + ( GeneralConsts.WALL_HEIGHT >> 1 );
        for ( int positionY = minPositionY; positionY <= maxPositionY; positionY += GeneralConsts.WALL_HEIGHT )
            for ( int positionX = minPositionX; positionX <= maxPositionX; positionX += GeneralConsts.WALL_WIDTH )
                if ( isPositionWithinRange( positionX, positionY ) )
                    gameCoreHandler.damageWall( positionX >> GeneralConsts.WALL_WIDTH_SHIFT, positionY >> GeneralConsts.WALL_HEIGHT_SHIFT, damage );
    }
    
    /**
        Checks whether the given position is within range of explosion.
        @param positionX x coordinate of position
        @param positionY y coordinate of position
        @return true if the given position is within range; false otherwise
    */
    private boolean isPositionWithinRange( final int positionX, final int positionY ) {
        return ( positionX - this.positionX ) * ( positionX - this.positionX ) + ( positionY - this.positionY ) * ( positionY - this.positionY ) <= rangeSquare ? true : false;
    }

    /**
        Tests whether this explosion hits the given window.
        @param windowX1 the x coordinate of the left side of the window
        @param windowY1 the y coordinate of the upper side of the window
        @param windowX2 the x coordinate of the right side of the window
        @param windowY2 the y coordinate of the lower side of the window
        @return true if this explosion hits the window; false otherwise
    */
    public boolean hitsWindow( final int windowX1, final int windowY1, final int windowX2, final int windowY2 ) {
        return !( windowX2 < positionX - range || positionX + range < windowX1 ||
                  windowY2 < positionY - range || positionY + range < windowY1    );
    }
    
    /**
        Draws the explosion.
        @param graphicsContext graphics context in wich to draw
    */
    public void draw( final Graphics graphicsContext ) {
        int zoneColorIndex = ExplosionConsts.ZONE_COLORS.length - 1;
        graphicsContext.setColor( ExplosionConsts.ZONE_COLORS[ zoneColorIndex-- ] );
        graphicsContext.fillOval( positionX - range, positionY - range, range << 1, range << 1 );
        int i = ExplosionConsts.ZONE_COLORS.length - 1;
        for ( int zoneRange = range - ( range % ExplosionConsts.ZONE_RANGE_DIFFERENCES == 0 ? ExplosionConsts.ZONE_RANGE_DIFFERENCES : range % ExplosionConsts.ZONE_RANGE_DIFFERENCES ); zoneRange > 0; zoneRange -= ExplosionConsts.ZONE_RANGE_DIFFERENCES ) {
            graphicsContext.setColor( ExplosionConsts.ZONE_COLORS[ Math.max( zoneColorIndex--, 0 ) ] );
            graphicsContext.fillOval( positionX - zoneRange, positionY - zoneRange, zoneRange << 1, zoneRange << 1 );
        }
    }
    
}
