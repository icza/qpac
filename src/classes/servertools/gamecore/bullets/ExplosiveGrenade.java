
package classes.servertools.gamecore.bullets;

import classes.servertools.gamecore.*;
import classes.servertools.*;
import java.util.*;


/**
    Explosive grenade. If explodes, an explosion will be created.
    @author Belicza Andras
*/
public class ExplosiveGrenade extends Grenade {

    /** Range of the explosion of the grenade. */
    private static final int EXPLOSION_RANGE  = 100;
    /** Damage of the explosions of the explosive grenades. */
    public static final int  EXPLOSION_DAMAGE = 25;

    /**
        Creates a new ExplosiveGrenade.
        @param initialCycleCounter value of cycle counter when this explosive grenade was launched
        @param position initial position of this explosive grenade
        @param direction angle of direction this explosive grenade was launched in
        @param shooterPlayer reference to the player who launched this explosive grenade
    */
    public ExplosiveGrenade( final int initialCycleCounter, final FloatVector position, final double direction, final Player shooterPlayer ) {
        super( initialCycleCounter, position, direction, shooterPlayer );
    }
    
    /**
        This method explodes the explosive grenade.
        @param bullets reference to the bullets
        @param ourBulletIndex our index in bullets vector
        @param explosions reference to the explosions
    */
    public void explode( final Vector bullets, final int ourBulletIndex, final Vector explosions ) {
        bullets.removeElementAt( ourBulletIndex );
        explosions.add( new Explosion( (int) position.x, (int) position.y, EXPLOSION_RANGE, EXPLOSION_DAMAGE, shooterPlayer ) );
    }
    
}
