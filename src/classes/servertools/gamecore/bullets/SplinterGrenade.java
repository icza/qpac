
package classes.servertools.gamecore.bullets;

import classes.servertools.gamecore.*;
import classes.servertools.*;
import java.util.*;
import classes.utilities.*;


/**
    Splinter grenade. If explodes, splinters (rifle bullets) will be created.
    @author Belicza Andras
*/
public class SplinterGrenade extends Grenade {

    /** Number of splinters have to create when this slinter grenade explodes. */
    private static final int SPLINTERS_COUNT = 50;
    /** Damage of a splinter. */
    private static final int SPLINTER_DAMAGE = 25;

    /**
        Creates a new SplinterGrenade.
        @param initialCycleCounter value of cycle counter when this splinter grenade was launched
        @param position initial position of this splinter grenade
        @param direction angle of direction this splinter grenade was launched in
        @param shooterPlayer reference to the player who launched this splinter grenade
    */
    public SplinterGrenade( final int initialCycleCounter, final FloatVector position, final double direction, final Player shooterPlayer ) {
        super( initialCycleCounter, position, direction, shooterPlayer );
    }
    
    /**
        This method explodes the splinter grenade.
        @param bullets reference to the bullets
        @param ourBulletIndex our index in bullets vector
        @param explosions reference to the explosions
    */
    public void explode( final Vector bullets, final int ourBulletIndex, final Vector explosions ) {
        bullets.removeElementAt( ourBulletIndex );
        for ( int splinterCounter = 0; splinterCounter < SPLINTERS_COUNT; splinterCounter++ )
            bullets.add( new PointBullet( position, Math.random() * MathUtils.TWO_PI, SPLINTER_DAMAGE, false, shooterPlayer ) );
    }
    
}
