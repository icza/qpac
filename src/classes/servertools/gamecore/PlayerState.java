
package classes.servertools.gamecore;

import classes.*;
import classes.servertools.*;
import classes.utilities.*;
import classes.options.*;
import classes.servertools.gamecore.weapons.*;


/**
    The state of a player. information needed to display it on screen.
    @author Belicza Andras
*/
public class PlayerState extends ExtensiveMovingObject implements HasOrderingIntKeys {
    
    /** Color of player's worm. */
    public int               wormColorIndex;
    /** Direction of the player's worm. */
    public int               direction;
    /** Moving phase of the player's worm. */
    public int               phase;
    /** Difference to next phase. */
    public transient int     nextPhaseDifference = 1;
    /** Name of the player. */
    public String            name;
    /** Position of the mouse cursor of the player. */
    public final FloatVector mousePosition       = new FloatVector();
    /** Rope of the worm. */
    public Rope              rope;
    /** Index of selected weapon of the player. */
    public int               weaponIndex;
    /** Selected weapon of the player. */
    public Weapon            weapon;
    /** Hit point of the player. */
    public int               hitPoint;
    /** Level of the oxygen bottle. */
    public int               oxygenBottleLevel;
    /** Counter of killing other players. */
    public int               killCounter  = 0;
    /** Counter of deaths of the player. */
    public int               deathCounter = 0;

    /**
        Creates a new PlayerState.
        @param wormMass physical mass of the player's worm
        @param wormShapeResistanceRate shape resistance rate of the player's worm
        @param wormCapacity physical capacity of the player's worm
    */
    public PlayerState( final float wormMass, final float wormShapeResistanceRate, final float wormCapacity ) {
        super( wormMass, wormShapeResistanceRate, wormCapacity, 0.0f, GeneralConsts.WORM_WIDTH, GeneralConsts.WORM_HEIGHT );
        direction         = GeneralConsts.WORM_DIRECTION_GROUND_RIGHT;
        setStandingPhase();
        hitPoint          = GeneralConsts.MAX_PLAYER_HIT_POINT;
        oxygenBottleLevel = GeneralConsts.MAX_OXYGEN_BOTTLE_LEVEL;
    }
    
    /**
        Sets the direction to ground keeping the left or right information.
    */
    public void setGroundDirection() {
        if ( direction == GeneralConsts.WORM_DIRECTION_AIR_LEFT )
            direction = GeneralConsts.WORM_DIRECTION_GROUND_LEFT;
        else if ( direction == GeneralConsts.WORM_DIRECTION_AIR_RIGHT )
            direction = GeneralConsts.WORM_DIRECTION_GROUND_RIGHT;
    }

    /**
        Sets the direction to air keeping the left or right information.
    */
    public void setAirDirection() {
        if ( direction == GeneralConsts.WORM_DIRECTION_GROUND_LEFT )
            direction = GeneralConsts.WORM_DIRECTION_AIR_LEFT;
        else if ( direction == GeneralConsts.WORM_DIRECTION_GROUND_RIGHT )
            direction = GeneralConsts.WORM_DIRECTION_AIR_RIGHT;
    }
    
    /**
        Sets the standing phase based on the direction.
    */
    public void setStandingPhase() {
        phase = GeneralConsts.WORM_STANDING_PHASES[ direction ];
    }

    /**
        Sets the last phase what got about worm based on the direction.
    */
    public void setLastPhase() {
        if ( direction == GeneralConsts.WORM_DIRECTION_GROUND_LEFT || direction == GeneralConsts.WORM_DIRECTION_AIR_LEFT )
            phase = 0;
        else
            phase = GeneralConsts.WORM_PHASES_COUNT[ direction ] - 1;
    }

    /**
        Checks and changes the phase if needed.
        @param cycleCounter server cycle counter
    */
    public void checkPhase( final int cycleCounter ) {
        if ( cycleCounter % ( 21 / GeneralConsts.WORM_PHASES_COUNT[ direction ] ) == 0 ) {
            phase += nextPhaseDifference;
            if ( phase <= 0 || phase >= GeneralConsts.WORM_PHASES_COUNT[ direction ] - 1 ) {
                if ( phase > GeneralConsts.WORM_PHASES_COUNT[ direction ] - 1 )
                    phase = GeneralConsts.WORM_PHASES_COUNT[ direction ] - 1;
                if ( phase < 0 )
                    phase = 0;
                nextPhaseDifference *= -1;
            }
        }
    }

    /** 
        Returns kill counter as the ordering int key (implmementing HasOrderingIntKeys interface).
        @return kill counter as the ordering int key
    */
    public int getOrderingIntKey() {
        return killCounter;
    }

    /**
        Returns -1 * death counter as secondary ordering int key (implementing HasOrderingIntKeys interface).
        @return -1 * death counter as secondary ordering int key
    */
    public int getSecondaryOrderingIntKey() {
        return -deathCounter;
    }

    /**
        Returns the group identifier (used to group players in team melee game type).
        @return the group identifier
    */
    public int getGroupIdentifier() {
        return wormColorIndex;
    }
    
    /**
        Returns the maximal value of group identifier.
        @return the maximal value of group identifier
    */
    public static int getMaxGroupIdentifier() {
        return ClientOptions.WORM_COLOR_NAMES.length;
    }
    
    /**
        Returns the name of the group.
        @param groupIdentifier group identfier whose name is being queried
        @return the name of the group
    */
    public static String getGroupName( final int groupIdentifier ) {
        
        return ClientOptions.WORM_COLOR_NAMES[ groupIdentifier ];
    }

}
