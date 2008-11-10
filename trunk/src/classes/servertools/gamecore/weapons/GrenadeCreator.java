
package classes.servertools.gamecore.weapons;

import classes.servertools.*;
import classes.servertools.gamecore.*;
import classes.servertools.gamecore.bullets.*;


/**
    Interface for the grenade launcher weapon: at firing an instance of object implementing this interface will create
    grenade to be shot.
    @author Belicza Andras
*/
interface GrenadeCreator {

    /**
        Creates a new Grenade.
        @param initialCycleCounter value of cycle counter when this grenade was launched
        @param position initial position of the grenade
        @param direction angle of direction this grenade was launched in
        @param shooterPlayer reference to the player who launched this grenade
    */
    Grenade createGrenade( final int initialCycleCounter, final FloatVector position, final double direction, final Player shooterPlayer );

}
