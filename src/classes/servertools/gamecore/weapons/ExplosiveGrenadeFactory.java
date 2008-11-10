
package classes.servertools.gamecore.weapons;

import classes.servertools.*;
import classes.servertools.gamecore.*;
import classes.servertools.gamecore.bullets.*;


/**
    This is an explosive grenade factory. Creates explosive grenades.
    @author Belicza Andras
*/
public class ExplosiveGrenadeFactory implements GrenadeCreator {

    /**
        Creates a new ExplosiveGrenade (implementing GrenadeCreator interface).
        @param initialCycleCounter value of cycle counter when this explosive grenade was launched
        @param position initial position of this explosive grenade
        @param direction angle of direction this explosive grenade was launched in
        @param shooterPlayer reference to the player who launched this explosive grenade
    */
    public Grenade createGrenade( final int initialCycleCounter, final FloatVector position, final double direction, final Player shooterPlayer ) {
        return new ExplosiveGrenade( initialCycleCounter, position, direction, shooterPlayer );
    }

}
