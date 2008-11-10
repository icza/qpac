
package classes.servertools.gamecore.weapons;

import classes.servertools.*;
import classes.servertools.gamecore.*;
import classes.servertools.gamecore.bullets.*;


/**
    Rifle weapon.
    @author Belicza Andras
*/
public class Rifle extends Weapon {

    /** Max reloading time of a rifle. */
    private static final int MAX_RELOADING_TIME  = 7;
    /** Damage of a rifle bullet. */
    private static final int RIFLE_BULLET_DAMAGE = 11;
    
    /**
        Creates a new Rifle.
        @param ownerPlayer reference to the owner player
    */
    public Rifle( final Player ownerPlayer ) {
        super( ownerPlayer, MAX_RELOADING_TIME );
    }
    
    /**
        Fires the rifle.
        @param cycleCounter value of cycle counter when this firing happens
        @param gameCoreHandler reference to the game core handler
        @param ownerPlayerState player state of the owner player
        @param angle angle where the owner player aims at
    */
    protected void fire( final int cycleCounter, final GameCoreHandler gameCoreHandler, final PlayerState ownerPlayerState, final double angle ) {
        if ( reloadingTime == 0 ) {
            final PointBullet rifleBullet = new PointBullet( ownerPlayerState.position, angle, RIFLE_BULLET_DAMAGE, true, ownerPlayer );
            ownerPlayer.moveExtensiveMovingObjectOutside( rifleBullet, angle );
            gameCoreHandler.getBullets().add( rifleBullet );
            restoreMaxReloadingTime();
        }
        else
            decreaseReloadingTime();
    }
    
}
