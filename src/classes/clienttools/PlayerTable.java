
package classes.clienttools;

import javax.swing.*;
import classes.options.*;
import classes.servertools.gamecore.*;
import javax.swing.table.*;
import classes.utilities.*;


/**
    Table of the players in killing order (and in team melee game type it is team grouped).
    @author Belicza Andras
*/
public class PlayerTable extends JTable {

    /** Names of the columns in the different game types. */
    private static final String[][] COLUMN_NAMES                 = { { "Player", "Kills", "Deaths" }, { "Team", "Player", "Kills", "Deaths" } };
    /** Columns index in free for all game type. */
    private static final int        FREE_FOR_ALL_GAME_TYPE_INDEX = 0;
    /** Columns index in team melee game type. */
    private static final int        TEAM_MELEE_GAME_TYPE_INDEX   = 1;

    /** Data model of the table. */
    private final DefaultTableModel tableModel = new DefaultTableModel();
    /** Datas of the table. */
    private Object[][]              table;
    /** Type of the game. */
    private int                     gameType;
    
    /**
        Creates a new PlayerTable.
    */
    PlayerTable() {
        setModel( tableModel );
        getTableHeader().setReorderingAllowed( false );
        setAutoCreateColumnsFromModel( false );
    }

    /**
        Sets a property of fitting table to window.
        @param fitTableToWindow the value of fitting table to window
    */
    public void setFitTableToWindow( final boolean fitTableToWindow ) {
        setAutoResizeMode( fitTableToWindow ? AUTO_RESIZE_NEXT_COLUMN : AUTO_RESIZE_OFF );
    }

    /**
        Refreshes the player table.
        @param playerStates states of the players
        @param gameType type of the game
    */
    public void refresh( final PlayerState[] playerStates, int gameType ) {
        this.gameType = gameType;
        refresh( playerStates );
        createDefaultColumnsFromModel();
    }
    
    /**
        Refreshes the player table.
        @param playerStates states of the players
    */
    public void refresh( final PlayerState[] playerStates ) {
        switch ( gameType ) {
            case ServerOptions.GAME_TYPE_FREE_FOR_ALL :
                buildFreeForAllGameTypeTable( orderPlayerStates( (PlayerState[]) playerStates.clone() ) );
                tableModel.setDataVector( table, COLUMN_NAMES[ FREE_FOR_ALL_GAME_TYPE_INDEX ] );
                break;
            case ServerOptions.GAME_TYPE_TEAM_MELEE :
                buildTeamMeleeGameTypeTable( orderPlayerStates( (PlayerState[]) playerStates.clone() ) );
                tableModel.setDataVector( table, COLUMN_NAMES[ TEAM_MELEE_GAME_TYPE_INDEX ] );
                break;
        }
    }
    
    /**
        Orders the states of players by killCounter attribute.
        @param playerStates reference to the states of the players
        @return ordered array of the states of the players without null elements
    */
    private PlayerState[] orderPlayerStates( final PlayerState[] playerStates ) {
        final PlayerState[] orderedPlayerStates = new PlayerState[ nonNullElementsCount( playerStates ) ];
        int orderedPlayerStateIndex = 0;
        for ( int playerStateIndex = 0; playerStateIndex < playerStates.length; playerStateIndex++ )
            if ( playerStates[ playerStateIndex ] != null )
                orderedPlayerStates[ orderedPlayerStateIndex++ ] = playerStates[ playerStateIndex ];

        return (PlayerState[]) Orderer.orderArray( (HasOrderingIntKeys[]) orderedPlayerStates );
    }

    /**
        Returns the number of non null elements.
        @param array array to be measured
        @return the number of non null elements
    */
    private int nonNullElementsCount( final Object[] array ) {
        int nonNullElementsCount = 0;
        for ( int arrayIndex = array.length - 1; arrayIndex >= 0; arrayIndex-- )
            if ( array[ arrayIndex ] != null )
                nonNullElementsCount++;
        return nonNullElementsCount;
    }

    /**
        Builds the players table in free for all game type.
        @param orderedPlayerStates ordered states of players who have to be placed into the table
    */
    private void buildFreeForAllGameTypeTable( final PlayerState[] orderedPlayerStates ) {
        int orderedPlayerStateIndex = 0;
        table = new Object[ orderedPlayerStates.length ][ COLUMN_NAMES[ FREE_FOR_ALL_GAME_TYPE_INDEX ].length ];
        for ( int rowIndex = 0; rowIndex < table.length; rowIndex++ ) {
            final Object[] row = table[ rowIndex ];
            final PlayerState playerState = orderedPlayerStates[ orderedPlayerStateIndex++ ];
            int columnIndex = 0;
            row[ columnIndex++ ] = playerState.name;
            row[ columnIndex++ ] = new Integer( playerState.killCounter );
            row[ columnIndex++ ] = new Integer( playerState.deathCounter );
        }
    }

    /**
        Builds the players table in team melee game type.
        @param orderedPlayerStates ordered states of players who have to be placed into the table
    */
    private void buildTeamMeleeGameTypeTable( final PlayerState[] orderedPlayerStates ) {
        final PlayerStatesGroup[] playerStatesGroups       = grouporderPlayerStates( orderedPlayerStates );
        final PlayerState[]       grouporderedPlayerStates = orderedPlayerStates;
        int grouporderedPlayerStateIndex = 0, playerStatesGroupIndex = -1, groupPlayerStateCounter = 0;
        table = new Object[ grouporderedPlayerStates.length + playerStatesGroups.length ][ COLUMN_NAMES[ TEAM_MELEE_GAME_TYPE_INDEX ].length ];
        for ( int rowIndex = 0; rowIndex < table.length; rowIndex++ ) {
            final Object[] row = table[ rowIndex ];
            if ( groupPlayerStateCounter-- == 0 ) {
                final PlayerStatesGroup playerStatesGroup = playerStatesGroups[ ++playerStatesGroupIndex ];
                groupPlayerStateCounter = playerStatesGroup.getPlayerStatesCount();
                int columnIndex = 0;
                row[ columnIndex++ ] = playerStatesGroup.getGroupName();
                row[ columnIndex++ ] = null;
                row[ columnIndex++ ] = new Integer( playerStatesGroup.getTotalKills () );
                row[ columnIndex++ ] = new Integer( playerStatesGroup.getTotalDeaths() );
            }
            else {
                final PlayerState playerState = grouporderedPlayerStates[ grouporderedPlayerStateIndex++ ];
                int columnIndex = 0;
                row[ columnIndex++ ] = null;              // The group column (color)
                row[ columnIndex++ ] = playerState.name;
                row[ columnIndex++ ] = new Integer( playerState.killCounter  );
                row[ columnIndex++ ] = new Integer( playerState.deathCounter );
            }
        }
    }

    /**
        Groups the states of players, and returns the ordered array groups
        @param orderedPlayerStates ordered stattes of players who have to be grouped
        @return ordered array of player states group holding group properties
    */
    private PlayerStatesGroup[] grouporderPlayerStates( final PlayerState[] orderedPlayerStates ) {
        // First we create groups
        final PlayerStatesGroup[] playerStatesGroups = new PlayerStatesGroup[ groupsCount( orderedPlayerStates ) ];
        for ( int orderedPlayerStateIndex = orderedPlayerStates.length - 1; orderedPlayerStateIndex >= 0; orderedPlayerStateIndex-- ) {
            final PlayerState playerState = orderedPlayerStates[ orderedPlayerStateIndex ];
            int playerStatesGroupIndex = 0;
            while ( playerStatesGroups[ playerStatesGroupIndex ] != null && playerStatesGroups[ playerStatesGroupIndex ].getGroupIdentifier() != playerState.getGroupIdentifier() )
                playerStatesGroupIndex++;
            if ( playerStatesGroups[ playerStatesGroupIndex ] == null )
                playerStatesGroups[ playerStatesGroupIndex ] = new PlayerStatesGroup( playerState.getGroupIdentifier() );
            playerStatesGroups[ playerStatesGroupIndex ].includePlayerState( playerState );
        }
        // Now order them
        Orderer.orderArray( (HasOrderingIntKeys[]) playerStatesGroups );
        // And lastly grouping players
        int orderedPlayerStateIndex = 0, orderedPlayerStateIndex2;
        for ( int playerStatesGroupIndex = 0; playerStatesGroupIndex < playerStatesGroups.length - 1; playerStatesGroupIndex++ ) { // The last group will be at end of list (it's its good position)
            final PlayerStatesGroup playerStatesGroup = playerStatesGroups[ playerStatesGroupIndex ];
            final int groupIdentifier = playerStatesGroup.getGroupIdentifier();
            for ( int groupPlayerStateCounter = playerStatesGroup.getPlayerStatesCount() - 1; groupPlayerStateCounter >= 0; groupPlayerStateCounter-- ) {
                if ( orderedPlayerStates[ orderedPlayerStateIndex ].getGroupIdentifier() != groupIdentifier ) {
                    orderedPlayerStateIndex2 = orderedPlayerStateIndex;
                    while ( orderedPlayerStates[ ++orderedPlayerStateIndex2 ].getGroupIdentifier() != groupIdentifier )
                        ;
                    final PlayerState switchingTempPlayerState = orderedPlayerStates[ orderedPlayerStateIndex ];
                    orderedPlayerStates[ orderedPlayerStateIndex  ] = orderedPlayerStates[ orderedPlayerStateIndex2 ];
                    orderedPlayerStates[ orderedPlayerStateIndex2 ] = switchingTempPlayerState;
                }
                orderedPlayerStateIndex++;
            }
        }

        return playerStatesGroups;
    }
    
    /**
        Returns the number of groups these states of players will be scattered to.
        @param playerStates states of players to be measured
        @return the number of groups
    */
    private int groupsCount( final PlayerState[] playerStates ) {
        final boolean[] isGroupNonEmpty = new boolean[ PlayerState.getMaxGroupIdentifier() ];
        for ( int playerStateIndex = playerStates.length - 1; playerStateIndex >= 0; playerStateIndex-- )
            isGroupNonEmpty[ playerStates[ playerStateIndex ].getGroupIdentifier() ] = true;
        int groupsCount = 0;
        for ( int isGroupNonEmptyIndex = isGroupNonEmpty.length - 1; isGroupNonEmptyIndex >= 0; isGroupNonEmptyIndex-- )
            if ( isGroupNonEmpty[ isGroupNonEmptyIndex ] )
                groupsCount++;
        return groupsCount;
    }


    /**
        Returns false regardless of parameter values.
        @param row the row whose value to be queried
        @param column the column whose value to be queried
        @return false
    */
    public boolean isCellEditable( final int row, final int column ) {
        return false;
    }

}
