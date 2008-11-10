
package classes.clienttools;

import classes.utilities.*;
import classes.servertools.gamecore.*;


/**
    Properties of a group of player states.
    @author Belicza Andras
*/
class PlayerStatesGroup implements HasOrderingIntKeys {
    
    /** Worm color index of the player states group. This is the group main ID. */
    private final int groupIdentifier;
    /** Count of player states in group. */
    private int       playerStatesCount = 0;
    /** Total kills of states of players of group. */
    private int       totalKills        = 0;
    /** Total deaths of states of players of group. */
    private int       totalDeaths       = 0;

    /**
        Creates a new PlayerStatesGroup.
        @param groupIdentifier group identifier
    */
    public PlayerStatesGroup( final int groupIdentifier ) {
        this.groupIdentifier = groupIdentifier;
    }

    /**
        Includes a player state to counting total properties.
    */
    public void includePlayerState( final PlayerState playerState ) {
        playerStatesCount++;
        totalKills  += playerState.killCounter;
        totalDeaths += playerState.deathCounter;
    }

    /**
        Returns the group identifier.
        @return the group identifier
    */
    public int getGroupIdentifier() {
        return groupIdentifier;
    }

    /**
        Returns the number of player states.
        @return the number of player states
    */
    public int getPlayerStatesCount() {
        return playerStatesCount;
    }

    /**
        Returns the total kills.
        @return the total kills
    */
    public int getTotalKills() {
        return totalKills;
    }

    /**
        Returns the total deaths.
        @return the total deaths
    */
    public int getTotalDeaths() {
        return totalDeaths;
    }

    /**
        Returns the name of the group.
        @return the name of the group
    */
    public String getGroupName() {
        return PlayerState.getGroupName( groupIdentifier );
    }

    /** 
        Returns total kills as the ordering int key (implmementing HasOrderingIntKeys interface).
        @return total kills as the ordering int key
    */
    public int getOrderingIntKey() {
        return totalKills;
    }
    
    /** 
        Returns -1 * total deaths as the secondary ordering int key (implmementing HasOrderingIntKeys interface).
        @return -1 * total deaths as the secondary ordering int key
    */
    public int getSecondaryOrderingIntKey() {
        return -totalDeaths;
    }
    
}
