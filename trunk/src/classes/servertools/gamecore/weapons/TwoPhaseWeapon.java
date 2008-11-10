
package classes.servertools.gamecore.weapons;

import classes.servertools.*;


/**
    Two phase weapon. The first phase is the firing phase, the second is the reloading phase.
    There is no reloading in the firing phase, and there is no firing in the reloading phase.
    @author Belicza Andras
*/
abstract class TwoPhaseWeapon extends Weapon {

    /** The first, firing phase of the weapon. */
    protected static final int PHASE_FIRING    = 0;
    /** The second, reloading phase of the weapon. */
    protected static final int PHASE_RELOADING = 1;

    /** Phase of the weapon. */
    protected transient int phase;

    /**
        Creates a new TwoPhaseWeapon.
        @param ownerPlayer reference to the owner player
        @param maxReloadingTime maximum value of the reloading time
    */
    public TwoPhaseWeapon( final Player ownerPlayer, final int maxReloadingTime ) {
        super( ownerPlayer, maxReloadingTime );
    }
    
    /**
        Sets the reloading time to its maximum (after several shots), and switches to reloading phase.
    */
    protected void restoreMaxReloadingTime() {
        super.restoreMaxReloadingTime();
        phase = PHASE_RELOADING;
    }
    
    /**
        Decreases the reloading time, and if reloading is done, switches to firing phase.
    */
    public void decreaseReloadingTime() {
        if ( phase == PHASE_RELOADING ) {
            super.decreaseReloadingTime();
            if ( reloadingTime == 0 )
                phase = PHASE_FIRING;
        }
    }
    
    /**
        Reloads now the weapon, and sets the firing phase.
    */
    public void reloadNow() {
        super.reloadNow();
        phase = PHASE_FIRING;
    }
    
}
