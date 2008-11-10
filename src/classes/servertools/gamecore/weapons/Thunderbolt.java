
package classes.servertools.gamecore.weapons;

import classes.servertools.*;
import classes.servertools.gamecore.*;
import classes.servertools.gamecore.bullets.*;
import java.util.*;
import java.awt.*;


/**
    Thunderbolt.
    @author Belicza Andras
*/
public class Thunderbolt extends PolylineWeapon {

    /** Max reloading time of a thunderbolt. */
    private static final int   MAX_RELOADING_TIME       = 200;
    /** One shot can be reloaded in this time. */
    private static final int   RELOADING_TIME_OF_A_SHOT =   7;
    /** Maximum range of a thunderbolt. */
    private static final int   MAX_RANGE                = 100;

    /**
        Creates a new Thunderbolt.
        @param ownerPlayer reference to the owner player
    */
    public Thunderbolt( final Player ownerPlayer ) {
        super( ownerPlayer, MAX_RELOADING_TIME, RELOADING_TIME_OF_A_SHOT );
    }
    
    /**
        Fires the thunderbolt.
        @param cycleCounter value of cycle counter when this firing happens
        @param gameCoreHandler reference to the game core handler
        @param startX the starter x coordinate of the shot
        @param startY the starter y coordinate of the shot
        @param angle angle where the owner player aims at
    */
    protected void fire( final int cycleCounter, final GameCoreHandler gameCoreHandler, final int startX, final int startY, final double angle ) {
        final int range = Math.min( MAX_RANGE, (int) new FloatVector( startX, startY ).distanceTo( ownerPlayer.getPlayerState().mousePosition ) );
        gameCoreHandler.getPolylineShots().add( new Lightning( cycleCounter, new Point( startX, startY ), new Point( startX + (int) ( Math.cos( angle ) * range ), startY - (int) ( Math.sin( angle ) * range ) ), ownerPlayer ) );
//        gameCoreHandler.getExplosions().add( new Explosion( startX + (int) ( Math.cos( angle ) * range ), startY - (int) ( Math.sin( angle ) * range ), 20, ownerPlayer ) );
    }
    
}
