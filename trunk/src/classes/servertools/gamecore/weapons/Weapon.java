
package classes.servertools.gamecore.weapons;

import java.io.*;
import classes.servertools.*;
import classes.servertools.gamecore.*;


/**
    A single weapon.
    @author Belicza Andras
*/
public abstract class Weapon implements Serializable {

    /** Reference to the owner player. */
    protected transient Player ownerPlayer;
    /** Maximum value of the reloading time. */
    protected final int        maxReloadingTime;
    /** Actual value of the reloading time. */
    protected int              reloadingTime;
    
    /**
        Creates a new Weapon.
        @param ownerPlayer reference to the owner player
        @param maxReloadingTime maximum value of the reloading time
    */
    protected Weapon( final Player ownerPlayer, final int maxReloadingTime ) {
        this.ownerPlayer      = ownerPlayer;
        this.maxReloadingTime = maxReloadingTime;
        reloadNow();
    }
    
    /**
        Sets the reloading time to its maximum (after a shot).
    */
    protected void restoreMaxReloadingTime() {
        reloadingTime = maxReloadingTime;
    }
    
    /**
        Decreases the reloading time.
    */
    public void decreaseReloadingTime() {
        if ( reloadingTime > 0 )
            reloadingTime--;
    }
    
    /**
        Reloads now the weapon.
    */
    public void reloadNow() {
        reloadingTime = 0;
    }
    
    /**
        Returns the maximum value of reloading time.
        @return the maximum value of reloading time
    */
    public int getMaxReloadingTime() {
        return maxReloadingTime;
    }

    /**
        Returns the reloading time.
        @return the reloading time
    */
    public int getReloadingTime() {
        return reloadingTime;
    }

    /**
        Fires the weapon.
        @param cycleCounter value of cycle counter when this firing happens
        @param gameCoreHandler reference to the game core handler
    */
    public void fire( final int cycleCounter, final GameCoreHandler gameCoreHandler ) {
        final PlayerState ownerPlayerState = ownerPlayer.getPlayerState();
        final double      angle            = ownerPlayerState.position.angleTo( ownerPlayerState.mousePosition );
        fire( cycleCounter, gameCoreHandler, ownerPlayerState, angle );
    }
    
    /**
        Fires the weapon.
        @param cycleCounter value of cycle counter when this firing happens
        @param gameCoreHandler reference to the game core handler
        @param ownerPlayerState player state of the owner player
        @param angle angle where the owner player aims at
    */
    protected abstract void fire( final int cycleCounter, final GameCoreHandler gameCoreHandler, final PlayerState ownerPlayerState, final double angle );

}
