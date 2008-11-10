
package classes.servertools.gamecore.weapons;

import classes.servertools.*;
import classes.servertools.gamecore.*;
import classes.servertools.gamecore.bullets.*;


/**
    Grenade grenade launcher.
    @author Belicza Andras
*/
public class GrenadeLauncher extends Weapon {

    /** Max reloading time of a grenade launcher. */
    private static final int MAX_RELOADING_TIME = 90;

    /** A grenade factory who generates grenade at firings. */
    private transient final GrenadeCreator grenadeFactory;
    
    /**
        Creates a new GrenadeLaucher.
        @param ownerPlayer reference to the owner player
        @param grenadeFactory a grenade factory
    */
    public GrenadeLauncher( final Player ownerPlayer, final GrenadeCreator grenadeFactory  ) {
        super( ownerPlayer, MAX_RELOADING_TIME );
        this.grenadeFactory = grenadeFactory;
    }
    
    /**
        Fires the grenade launcher.
        @param cycleCounter value of cycle counter when this firing happens
        @param gameCoreHandler reference to the game core handler
        @param ownerPlayerState player state of the owner player
        @param angle angle where the owner player aims at
    */
    protected void fire( final int cycleCounter, final GameCoreHandler gameCoreHandler, final PlayerState ownerPlayerState, final double angle ) {
        if ( reloadingTime == 0 ) {
            final Grenade grenade = grenadeFactory.createGrenade( cycleCounter, ownerPlayerState.position, angle, ownerPlayer );
            ownerPlayer.moveExtensiveMovingObjectOutside( grenade, angle );
            gameCoreHandler.getBullets().add( grenade );
            restoreMaxReloadingTime();
        }
        else
            decreaseReloadingTime();
    }
    
}
