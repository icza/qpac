
package classes.servertools.gamecore.weapons;

import classes.servertools.*;
import classes.servertools.gamecore.*;
import classes.servertools.gamecore.bullets.*;


/**
    This is a splinter grenade factory. Creates splinter grenades.
    @author Belicza Andras
*/
public class SplinterGrenadeFactory implements GrenadeCreator {

    /**
        Creates a new SplinterGrenade (implementing GrenadeCreator interface).
        @param initialCycleCounter value of cycle counter when this splinter grenade was launched
        @param position initial position of this splinter grenade
        @param direction angle of direction this splinter grenade was launched in
        @param shooterPlayer reference to the player who launched this splinter grenade
    */
    public Grenade createGrenade( final int initialCycleCounter, final FloatVector position, final double direction, final Player shooterPlayer ) {
        return new SplinterGrenade( initialCycleCounter, position, direction, shooterPlayer );
    }

}
