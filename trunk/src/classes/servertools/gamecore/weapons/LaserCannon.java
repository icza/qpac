
package classes.servertools.gamecore.weapons;

import classes.servertools.*;
import classes.servertools.gamecore.*;
import classes.utilities.*;
import java.util.Vector;            // Just Vector, because reference to Map would be ambigous.
import classes.servertools.gamecore.bullets.*;
import java.awt.*;
import classes.*;


/**
    Laser cannon.
    @author Belicza Andras
*/
public class LaserCannon extends PolylineWeapon {

    /** Max reloading time of a laser cannon. */
    private static final int   MAX_RELOADING_TIME            = 180;
    /** One shot can be reloaded in this time. */
    private static final int   RELOADING_TIME_OF_A_SHOT      =   4;
    /** We searching breaking point in map in every stepping this far. */
    private static final float LASER_STEPPING_DISTANCE       = 4.0f;
    /** The light breaking rate when it gets from air to water. */
    private static final float AIR_WATER_LIGHT_BREAKING_RATE = 4.0f / 3.0f;
    
    /** Reference to the map. */
    private transient final Map map;
    
    /**
        Creates a new LaserCannon.
        @param ownerPlayer reference to the owner player
        @param map reference to the map
    */
    public LaserCannon( final Player ownerPlayer, final Map map ) {
        super( ownerPlayer, MAX_RELOADING_TIME, RELOADING_TIME_OF_A_SHOT );
        this.map = map;
    }
    
    /**
        Fires the laser cannon.
        @param cycleCounter value of cycle counter when this firing happens
        @param gameCoreHandler reference to the game core handler
        @param startX the starter x coordinate of the shot
        @param startY the starter y coordinate of the shot
        @param angle angle where the owner player aims at
    */
    protected void fire( final int cycleCounter, final GameCoreHandler gameCoreHandler, final int startX, final int startY, final double angle ) {
        byte                 hitWall;
        final Vector         points         = new Vector( 3 );
        final LaserDirection laserDirection = new LaserDirection( startX, startY, angle );
        points.add( new Point( (int) laserDirection.x, (int) laserDirection.y ) );
        do {
            calculateNextLaserBreakingPoint( laserDirection );
            final Point hitPoint = new Point( (int) laserDirection.x, (int) laserDirection.y );
            points.add( hitPoint );
            hitWall = map.getWall( hitPoint.x >> GeneralConsts.WALL_WIDTH_SHIFT, hitPoint.y >> GeneralConsts.WALL_HEIGHT_SHIFT );
        } while ( hitWall != Map.WALL_STONE && hitWall != Map.WALL_BRICK );
        gameCoreHandler.getPolylineShots().add( new Laser( cycleCounter, points, ownerPlayer ) );
    }
    
    /**
        This class will "listen" the reflection of the laser.
    */
    private static class ReflectionListener {

        /** Holds the information of the existence of reflection. */
        private boolean wasReflection;
        
        /**
            Sets the whether there was reflection.
            @param wasReflection contains the information of the existence of reflection
        */
        public void setWasReflected( final boolean wasReflection ) {
            this.wasReflection = wasReflection;
        }

        /**
            Tells whether there was reflection.
            @return true if there was reflection; false otherwise
        */
        public boolean wasReflection() {
            return wasReflection;
        }

    }

    /**
        Calculates the next laser break point.
        @param laserDirection object what describes the direction of the laser
    */
    private void calculateNextLaserBreakingPoint( final LaserDirection laserDirection ) {
        final boolean isStartWallEmpty = map.getWall( (int) laserDirection.x >> GeneralConsts.WALL_WIDTH_SHIFT, (int) laserDirection.y >> GeneralConsts.WALL_HEIGHT_SHIFT ) == Map.WALL_EMPTY ? true : false;
        final float   dx =  (float) Math.cos( laserDirection.angle ) * LASER_STEPPING_DISTANCE;
        final float   dy = -(float) Math.sin( laserDirection.angle ) * LASER_STEPPING_DISTANCE;
        byte          hitWall;
        while ( true ) {
            hitWall = map.getWall( (int) laserDirection.x >> GeneralConsts.WALL_WIDTH_SHIFT, (int) laserDirection.y >> GeneralConsts.WALL_HEIGHT_SHIFT );
            if ( hitWall == Map.WALL_STONE || hitWall == Map.WALL_BRICK || isStartWallEmpty && hitWall != Map.WALL_EMPTY || !isStartWallEmpty && hitWall == Map.WALL_EMPTY )
                break;
            laserDirection.x += dx;
            laserDirection.y += dy;
        }
        if ( hitWall != Map.WALL_STONE && hitWall != Map.WALL_BRICK ) {  // Breaking or reflecting laser
            final int actualXIndex = (int) laserDirection.x >> GeneralConsts.WALL_WIDTH_SHIFT;
            final int actualYIndex = (int) laserDirection.y >> GeneralConsts.WALL_HEIGHT_SHIFT;
            final int lastXIndex   = (int) ( laserDirection.x - dx ) >> GeneralConsts.WALL_WIDTH_SHIFT;
            final int lastYIndex   = (int) ( laserDirection.y - dy ) >> GeneralConsts.WALL_HEIGHT_SHIFT;
            final int hitAtSide    = lastYIndex < actualYIndex ? SideConsts.SIDE_UP : ( lastYIndex > actualYIndex ? SideConsts.SIDE_DOWN : ( lastXIndex < actualXIndex ? SideConsts.SIDE_LEFT : SideConsts.SIDE_RIGHT ) );
            final ReflectionListener reflectionListener = new ReflectionListener();
            laserDirection.angle   = calculateBreakingAngle( laserDirection.angle, hitAtSide, isStartWallEmpty ? AIR_WATER_LIGHT_BREAKING_RATE : 1.0 / AIR_WATER_LIGHT_BREAKING_RATE, reflectionListener );
            if ( reflectionListener.wasReflection() ) {  // We musn't step out of water (and else we have to stay in the next medium)
                laserDirection.x -= dx;
                laserDirection.y -= dy;
            }
        }
    }

    /**
        Calculates the breaking angle of the laser.
        @param angle angle of the laser
        @param hitAtSide tells wich side hits the laser at
        @param lightBreakingRate the light breaking rate
        @param reflectionListener reflection listener who "listens" whether the laser will be reflected
        @return the angle of laser after the hit
    */
    private double calculateBreakingAngle( double angle, final int hitAtSide, final double lightBreakingRate, final ReflectionListener reflectionListener ) {
        double rotatingAngle = 0.0;
        switch ( hitAtSide ) {
            case SideConsts.SIDE_UP    : rotatingAngle = 0.0                         ; break;
            case SideConsts.SIDE_LEFT  : rotatingAngle = -MathUtils.HALF_PI          ; break;
            case SideConsts.SIDE_DOWN  : rotatingAngle = -Math.PI                    ; break;
            case SideConsts.SIDE_RIGHT : rotatingAngle = -MathUtils.ONE_AND_A_HALF_PI; break;
        }
        angle = MathUtils.normalizeAngle( angle + rotatingAngle );
        final double alpha    = angle < MathUtils.ONE_AND_A_HALF_PI ? MathUtils.ONE_AND_A_HALF_PI - angle : angle - MathUtils.ONE_AND_A_HALF_PI;
        final double sinAlpha = Math.sin( alpha );
        if ( sinAlpha > lightBreakingRate ) {
            reflectionListener.setWasReflected( true );
            return MathUtils.HALF_PI + ( angle < MathUtils.ONE_AND_A_HALF_PI ? alpha : -alpha ) - rotatingAngle; // Reflecting
        }
        else {
            reflectionListener.setWasReflected( false );
            final double beta  = Math.asin( sinAlpha / lightBreakingRate );  // Breaking: sin(a)/sin(b)=n
            return MathUtils.ONE_AND_A_HALF_PI  + ( angle < MathUtils.ONE_AND_A_HALF_PI ? -beta : beta ) - rotatingAngle;
        }
    }

}
                                 