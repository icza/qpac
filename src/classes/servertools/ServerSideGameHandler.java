
package classes.servertools;

import classes.*;
import classes.utilities.*;
import classes.servertools.gamecore.*;
import classes.options.*;
import java.util.Vector;     // Only the vector, because Map exists in here too
import java.rmi.*;


/**
    The server side game handler class.
    @author Belicza Andras
*/
public class ServerSideGameHandler extends ControllableThread implements OptionsChangeListener {

    /** We trims vector capacities if they are unused for this count of cycles. */
    private static final int TRIM_UNUSED_VECTOR_CAPACITIES_FOR_CYCLES_COUNT = 70;

    /** Reference to the server. */
    private final Server          server;
    /** Reference to the server options. */
    private final ServerOptions   serverOptions;
    /** Rerefence to the players. */
    private final Player[]        players;
    /** The map. */
    private final Map             map;
    /** Handler of the core of the game. */
    private final GameCoreHandler gameCoreHandler;
    /** Elapsed time since game started (ms). */
    private long                  elapsedTime = 0l;
    /** Indicating that the game is paused. */
    private boolean               gamePaused  = false;
    /** Indicating that the game is over (reached at least one of the limits). */
    private boolean               gameOver    = false;
    /** Reference to the bullets. */
    private final Vector          bullets;
    /** Reference to the xplosions. */
    private final Vector          explosions;
    /** Reference to the decorations. */
    private final Vector          decorations;
    /** Reference to the polyline shots. */
    private final Vector          polylineShots;
    /** Array of the vectors (bullets, explosions, decorations, polylineShots). */
    private final Vector[]        vectors;
    /** Tells which cycle used the vectors large capacity laslty at. */
    private final int[]           lastUsedLargeVectorCapacityAtCycles;

    /**
        Creates a new ServerSideGameHandler.
        @param server reference to the server
        @param serverOptions reference to the server options
        @param players reference to the players
    */
    public ServerSideGameHandler( final Server server, final ServerOptions serverOptions, final Player[] players ) {
        super( serverOptions.periodTime );
        this.server        = server;
        this.serverOptions = serverOptions;
        this.players       = players;
        map                = new Map( serverOptions );
        gameCoreHandler    = new GameCoreHandler( this.server, this.serverOptions, players, map );
        bullets            = gameCoreHandler.getBullets();
        decorations        = gameCoreHandler.getDecorations();
        explosions         = gameCoreHandler.getExplosions();
        polylineShots      = gameCoreHandler.getPolylineShots();
        vectors            = new Vector[] { bullets, decorations, explosions, polylineShots };
        lastUsedLargeVectorCapacityAtCycles = new int[ vectors.length ];
    }

    /**
        This method will be executed first before the cycles.
    */
    protected void firstOperate() {
        serverOptions.addOptionsChangeListener( this   );
        serverOptions.addOptionsChangeListener( server );
        optionsChanged();         // We want all setting to be done what are needed after options changed.
        server.optionsChanged();  // Mostly this is not required, because the constructor of server calls this, but in theory options may have been changed between the time of that and the actual time.
    }

    /**
        The cyclycal repeatable server works.
    */
    protected void operate() {
        checkPlayers();
        if ( !gameOver && !gamePaused ) {
            elapsedTime += serverOptions.periodTime;
            checkLimits();
            checkVectorSizes();
            gameCoreHandler.calculateNextMoment();
        }
    }

    /**
        Checks the players who are still inside, and the requests for pausing/resuming the game.
    */
    private void checkPlayers() {
        for ( int playerIndex = 0; playerIndex < players.length; playerIndex++ )
            if ( players[ playerIndex ] != null )
                if ( players[ playerIndex ].closed() ) {
                    try {
                        server.broadcastServerMessage( players[ playerIndex ].getPlayerState().name + ( players[ playerIndex ].isKicked() ? " has been kicked (reason: " + players[ playerIndex ].getKickMessage() + ")." : " has left the game." ) );
                    }
                    catch ( RemoteException re ) {
                        Logging.logError( re );
                    }
                    players[ playerIndex ]  = null;
                }
                else {
                    final Player player = players[ playerIndex ];
                    player.increaseIdleTime( serverOptions.periodTime );
                    if ( player.getAndClearRequestedToPauseOrResume() )
                        if ( gamePaused ) {
                            gamePaused = false;
                            server.broadcastServerMessage( player.getPlayerState().name + " resumed the game." );
                        }
                        else
                            if ( player.getRemainingPausesCount() > 0 ) {
                                gamePaused = true;
                                player.decreaseRemainingPausesCount();
                                server.broadcastServerMessage( player.getPlayerState().name + " paused the game (" + player.getRemainingPausesCount() + " time(s) remaining)." );
                            }
                }
    }

    /**
        Checks if one of the limits has been reached.
    */
    private void checkLimits() {
        if ( serverOptions.isKillLimit )
            if ( determineMaxKill() >= serverOptions.killLimit ) {
                gameOver = true;
                server.broadcastServerMessage( "Kill limit reached, game over!" );
            }
        if ( serverOptions.isTimeLimit )
            if ( elapsedTime >= serverOptions.timeLimit * ( 60l * 1000l ) ) {
                gameOver = true;
                server.broadcastServerMessage( "Time limit reached, game over!" );
            }
    }
    
    private int determineMaxKill() {
        int maxKill = 0;
        switch ( serverOptions.gameType ) {
            case ServerOptions.GAME_TYPE_FREE_FOR_ALL :
                for ( int playerIndex = players.length - 1; playerIndex >= 0; playerIndex-- )
                    if ( players[ playerIndex ] != null )
                        maxKill = Math.max( maxKill, players[ playerIndex ].getPlayerState().killCounter );
                break;
            case ServerOptions.GAME_TYPE_TEAM_MELEE :
                final int[] groupKills = new int[ PlayerState.getMaxGroupIdentifier() ];
                for ( int playerIndex = players.length - 1; playerIndex >= 0; playerIndex-- )
                    if ( players[ playerIndex ] != null ) {
                        final PlayerState playerState = players[ playerIndex ].getPlayerState();
                        maxKill = Math.max( maxKill, groupKills[ playerState.getGroupIdentifier() ] += playerState.killCounter );
                    }
                break;
        }
        return maxKill;
    }

    /**
        Checks the sizes of the vectors, and if they are under the half size 
        of their capacities for a long time, trims their capacities to make downloadings faster.
    */
    private void checkVectorSizes() {
        final int cycleCounter = getCycleCounter();
        for ( int vectorIndex = 0; vectorIndex < vectors.length; vectorIndex++ ) {
            if ( vectors[ vectorIndex ].size() > vectors[ vectorIndex ].capacity() >> 1 )
                lastUsedLargeVectorCapacityAtCycles[ vectorIndex ] = cycleCounter;
            if ( lastUsedLargeVectorCapacityAtCycles[ vectorIndex ] > TRIM_UNUSED_VECTOR_CAPACITIES_FOR_CYCLES_COUNT )
                vectors[ vectorIndex ].trimToSize();
        }
    }

    /**
        This method will be executed lastly after the cycles.
    */
    protected void lastOperate() {
        serverOptions.removeOptionsChangeListener( server );
        serverOptions.removeOptionsChangeListener( this   );
    }

    /**
        This method will be called when server options has changed (implementing OptinosChangeListener interface).
    */
    public void optionsChanged() {
        setOperatePeriodTime( serverOptions.periodTime );
    }
    
    /**
        Returns the map.
        @return the map
    */
    public Map getMap() {
        return map;
    }
    
    /**
        Returns the bullets.
        @return the bullets
    */
    public Vector getBullets() {
        return bullets;
    }
    
    /**
        Returns the decorations.
        @return the decorations
    */
    public Vector getDecorations() {
        return decorations;
    }
    
    /**
        Returns the explosions.
        @return the explosions
    */
    public Vector getExplosions() {
        return explosions;
    }
    
    /**
        Returns the polyline shots.
        @return the polyline shots
    */
    public Vector getPolylineShots() {
        return polylineShots;
    }

    /**
        Returns the elapsed time of the game.
        @return the elapsed time of the game
    */
    public long getElapsedTime() {
        return elapsedTime;
    }

}
