
package classes.servertools.gamecore.weapons;

import classes.servertools.*;
import classes.servertools.gamecore.*;
import java.util.*;
import classes.servertools.gamecore.bullets.*;


/**
    Rocket launcher.
    @author Belicza Andras
*/
public class RocketLauncher extends Weapon {

    /** Max reloading time of a rocket launcher. */
    private static final int MAX_RELOADING_TIME = 120;
    
    /**
        Creates a new RocketLaucher.
        @param ownerPlayer reference to the owner player
    */
    public RocketLauncher( final Player ownerPlayer ) {
        super( ownerPlayer, MAX_RELOADING_TIME );
    }
    
    /**
        Fires the rocket launcher.
        @param cycleCounter value of cycle counter when this firing happens
        @param gameCoreHandler reference to the game core handler
        @param ownerPlayerState player state of the owner player
        @param angle angle where the owner player aims at
    */
    protected void fire( final int cycleCounter, final GameCoreHandler gameCoreHandler, final PlayerState ownerPlayerState, final double angle ) {
        if ( reloadingTime == 0 ) {
            final Rocket rocket = new Rocket( ownerPlayerState.position, angle, ownerPlayer );
            ownerPlayer.moveExtensiveMovingObjectOutside( rocket, angle );
            gameCoreHandler.getBullets().add( rocket );
            restoreMaxReloadingTime();
        }
        else
            decreaseReloadingTime();
    }
    
}
