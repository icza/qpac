
package classes.servertools.gamecore.bullets;

import classes.servertools.gamecore.*;
import classes.servertools.*;
import java.util.*;
import java.awt.*;


/**
    Common part of the bullets.
    @author Belicza Andras
*/
public abstract class Bullet extends ExtensiveMovingObject {

    /** Reference to the player who fired this bullet. */
    protected transient final Player shooterPlayer;
    /** Initial value of cycle counter. */
    protected transient final int    initialCycleCounter;

    /**
        Creates a new Bullet.
        @param mass the physical mass of the bullet
        @param shapeResistanceRate the rate of the shape resistance in air and water
        @param capacity the physical capacity of the bullet
        @param antiGravitationRate the anti gravitation rate
        @param width width of the bullet
        @param height height of the bullet
        @param initialCycleCounter value of cycle counter when this bullets was shot
        @param position initial position of the bullet
        @param direction angle of direction this bullet was shot in
        @param velocity initial velocity of the bullet
        @param shooterPlayer reference to the player who fired this bullet
    */
    protected Bullet( final float mass, final float shapeResistanceRate, final float capacity, final float antiGravitationRate, final int width, final int height, final int initialCycleCounter, final FloatVector position, final double direction, final float velocity, final Player shooterPlayer ) {
        super( mass, shapeResistanceRate, capacity, antiGravitationRate, width, height );
        this.initialCycleCounter = initialCycleCounter;
        this.position.x          = position.x;
        this.position.y          = position.y;
        this.velocity.x          =  velocity * (float) Math.cos( direction );
        this.velocity.y          = -velocity * (float) Math.sin( direction );
        this.shooterPlayer       = shooterPlayer;
    }
    
    /**
        Called when this bullet collides with something.
        @param gameCoreHandler reference to the game core handler
        @param hitsMapTowardAxes contains which axes this bullet hits the map toward
        @param hitWallIndices contains the indices of the wall hit by this bullet
        @param hitsPlayerTowardAxes contains which axes this bullet hits the one of the palyers toward
        @param player player hit by this bullet
        @param ourBulletIndex our index in bullets vector
    */
    public abstract void collides( final GameCoreHandler gameCoreHandler, final int hitsMapTowardAxes, final Point hitWallIndices, final int hitsPlayerTowardAxes, final Player player, final int ourBulletIndex );
    
    /**
        Draws the bullet.
        @param graphicsContext graphics context in wich to draw
    */
    public abstract void draw( final Graphics graphicsContext );
    
    /**
        Checks the cycle whether we reached the end cycle, and that case we execute a self-destruction.
        Returns always false, because some bullet doesn't use this feature.
        @param cycleCounter actual value of cycle counter
        @param gameCoreHandler reference to the game core handler
        @param ourBulletIndex our index in bullets vector
        @return false always
    */
    public boolean reachedEndCycle( final int cycleCounter, final GameCoreHandler gameCoreHandler, final int ourBulletIndex ) {
        return false;
    }

    /**
        Returns false, a single bullet is not explodable.
        @return false
    */
    public boolean isExplodable() {
        return false;
    }

    /**
        This method should explodes the bullet: an empty implementation of this feature.
        @param bullets reference to the bullets
        @param ourBulletIndex our index in bullets vector
        @param explosions reference to the explosions
    */
    public void explode( final Vector bullets, final int ourBulletIndex, final Vector explosions ) {
    }
    
}
