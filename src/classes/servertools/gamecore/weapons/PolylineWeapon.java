
package classes.servertools.gamecore.weapons;

import classes.servertools.*;
import classes.servertools.gamecore.*;
import classes.utilities.*;
import classes.servertools.gamecore.bullets.*;
import java.awt.*;
import classes.*;


/**
    Polyline weapon.
    @author Belicza Andras
*/
abstract class PolylineWeapon extends TwoPhaseWeapon {

    /** One shot can be reloaded in this time. */
    private transient final int reloadingTimeOfAShot;

    /**
        Creates a new PolylineWeapon.
        @param ownerPlayer reference to the owner player
        @param maxReloadingTime maximum value of the reloading time
    */
    protected PolylineWeapon( final Player ownerPlayer, final int maxReloadingTime, final int reloadingTimeOfAShot ) {
        super( ownerPlayer, maxReloadingTime );
        this.reloadingTimeOfAShot = reloadingTimeOfAShot;
    }
    
    /**
        Fires the polyline weapon.
        @param cycleCounter value of cycle counter when this firing happens
        @param gameCoreHandler reference to the game core handler
        @param ownerPlayerState player state of the owner player
        @param angle angle where the owner player aims at
    */
    protected void fire( final int cycleCounter, final GameCoreHandler gameCoreHandler, final PlayerState ownerPlayerState, final double angle ) {
        if ( phase == PHASE_FIRING )
            if ( ( reloadingTime += reloadingTimeOfAShot ) < maxReloadingTime ) {
                final ExtensiveMovingObject tempObject = new ExtensiveMovingObject( 0.0f, 0.0f, 0.0f, 0.0f, 2, 2 );
                tempObject.position.x = ownerPlayerState.position.x;
                tempObject.position.y = ownerPlayerState.position.y;
                ownerPlayer.moveExtensiveMovingObjectOutside( tempObject, angle );
                fire( cycleCounter, gameCoreHandler, (int) tempObject.position.x, (int) tempObject.position.y, angle );
            }
            else
                restoreMaxReloadingTime();
        else
            decreaseReloadingTime();
    }

    /**
        Fires the polyline weapon.
        @param cycleCounter value of cycle counter when this firing happens
        @param gameCoreHandler reference to the game core handler
        @param startX the starter x coordinate of the shot
        @param startY the starter y coordinate of the shot
        @param angle angle where the owner player aims at
    */
    protected abstract void fire( final int cycleCounter, final GameCoreHandler gameCoreHandler, final int startX, final int startY, final double angle );

}
                                 