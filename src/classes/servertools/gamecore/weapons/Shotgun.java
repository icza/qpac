
package classes.servertools.gamecore.weapons;

import classes.servertools.*;
import classes.servertools.gamecore.*;
import classes.servertools.gamecore.bullets.*;


/**
    Shotgun.
    @author Belicza Andras
*/
public class Shotgun extends Weapon {

    /** Max reloading time of a shotgun. */
    private static final int    MAX_RELOADING_TIME      = 55;
    /** Number of shots in a shotgun bullet. */
    private static final int    SHOTS_COUNT             = 14;
    /** Maximum scatter angle of the shots of a shotgun bullet. */
    private static final double MAX_SHOTS_SCATTER_ANGLE = Math.PI / 13.0;
    /** Damage of a shot. */
    private static final int    SHOT_DAMAGE             = 8;
    
    /**
        Creates a new Shotgun.
        @param ownerPlayer reference to the owner player
    */
    public Shotgun( final Player ownerPlayer ) {
        super( ownerPlayer, MAX_RELOADING_TIME );
    }
    
    /**
        Fires the shotgun.
        @param cycleCounter value of cycle counter when this firing happens
        @param gameCoreHandler reference to the game core handler
        @param ownerPlayerState player state of the owner player
        @param angle angle where the owner player aims at
    */
    protected void fire( final int cycleCounter, final GameCoreHandler gameCoreHandler, final PlayerState ownerPlayerState, final double angle ) {
        if ( reloadingTime == 0 ) {
            for ( int shotCounter = 0; shotCounter < SHOTS_COUNT; shotCounter++ ) {
                final double      randomAngle = angle + ( Math.random() - 0.5 ) * MAX_SHOTS_SCATTER_ANGLE;
                final PointBullet shot        = new PointBullet( ownerPlayerState.position, randomAngle, SHOT_DAMAGE, false, ownerPlayer );
                ownerPlayer.moveExtensiveMovingObjectOutside( shot, randomAngle );
                gameCoreHandler.getBullets().add( shot );
            }
            restoreMaxReloadingTime();
        }
        else
            decreaseReloadingTime();
    }
    
}
