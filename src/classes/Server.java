
package classes;

import classes.utilities.*;
import java.rmi.registry.*;
import java.rmi.*;
import classes.servertools.*;
import java.rmi.server.*;
import classes.options.*;
import classes.clienttools.*;
import classes.servertools.gamecore.*;
import java.util.Vector;            // Just Vector, because reference to Map would be ambigous.
import java.util.StringTokenizer;   // Just StringTokenizer, because reference to Map would be ambigous.


/**
    This class provides the server part of the game.
    @author Belicza Andras
*/
public class Server extends GameHandlingServiceProvider implements RemoteQServer, Timeable, OptionsChangeListener {
   
    /** Maximum idle time before we close a player (ms). */
    public  static final long   MAX_PLAYER_IDLE_TIME               = 5000l;
    /** Period time of checking for dead players (ms). */
    private static final long   PLAYERS_CHECKING_TIMER_PERIOD_TIME = 5000l;
    /** Time to wait after closing players and before completely shut down (so clients can notice that we shut down and it's not a communication error). */
    private static final long   WAITING_TIME_BEFORE_SHUT_DOWN      =  250l;
    /** String prefix will appear in the head of all server message. */
    public  static final String SERVER_MESSAGE_PREFIX              = "<server>: ";

    /** Name of the commands. */
    private static final String[][] COMMAND_NAMES = { { "/kickplayer", "/kp" }   , { "/listplayers", "/lp" }  , { "/sendmessage", "/sm" }  };
    /** Sort (1 line) description of the commands. */
    private static final String[]   COMMAND_INFOS = { "kick a player out of game", "list the players by index", "send message as operator" };
    /** Kick player command. */
    private static final int COMMAND_KICK_PLAYER  = 0;
    /** List players command. */
    private static final int COMMAND_LIST_PLAYERS = 1;
    /** Send message command. */
    private static final int COMMAND_SEND_MESSAGE = 2;
    

    /** Reference to the remote object registry that we created. */
    private static Registry remoteObjectRegistry;

    /** Reference to the main frame. */
    private final MainFrame                 mainFrame;
    /** Reference to the server options. */
    private final ServerOptions             serverOptions;
    /** Array of the players. */
    private final Player[]                  players;
    /** Timing to close dead players. */
    private final Timer                     playersCheckingTimer;
    /** Reference to the map datas. */
    private final byte[]                    mapDatas;
    /** Informations needed be sent on clients requests. */
    private final InformationsForDrawing    informationsForDrawing;
    /** The unchangeable server options. */
    private final UnchangeableServerOptions unchangeableServerOptions = new UnchangeableServerOptions();
    /** The changeable server options. */
    private final ChangeableServerOptions   changeableServerOptions   = new ChangeableServerOptions();
    /** Reference to the decorations. */
    private final Vector                    decorations;
    /** The server command processor. */
    private final CommandProcessor commandProcessor = new CommandProcessor( COMMAND_NAMES, COMMAND_INFOS );

    /**
        Creates a new Server.
        @param mainFrame reference to the main frame
        @param serverOptions reference to the server options
        @throws RemoteObjectRegistryCreationFailedException when creating remote object registry fails
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote mehtod invocation
    */
    public Server( final MainFrame mainFrame, final ServerOptions serverOptions ) throws RemoteObjectRegistryCreationFailedException, RemoteException {
        this.mainFrame     = mainFrame;
        this.serverOptions = serverOptions;
        createRemoteObjectRegistry();
        /* Exporting of object must be after calling createRemoteObjectRegistry(), because if another Qpac registry exists on same host, exportObject() will throw ExportException. */
        UnicastRemoteObject.exportObject( this, GeneralConsts.REGISTRY_PORT );
        players                = new Player[ this.serverOptions.maxNumberOfPlayers ];
        final ServerSideGameHandler serverSideGameHandler = new ServerSideGameHandler( this, this.serverOptions, players );
        setGameHandlingServiceProvider( serverSideGameHandler );
        mapDatas               = serverSideGameHandler.getMap().getDatas();
        informationsForDrawing = new InformationsForDrawing( new PlayerState[ players.length ], serverSideGameHandler.getBullets(), serverSideGameHandler.getExplosions(), serverSideGameHandler.getPolylineShots() );
        decorations = serverSideGameHandler.getDecorations();
        playersCheckingTimer = new Timer( PLAYERS_CHECKING_TIMER_PERIOD_TIME, this );
        playersCheckingTimer.start();
        unchangeableServerOptions.mapWidth           = serverSideGameHandler.getMap().getWidth();
        unchangeableServerOptions.mapHeight          = serverSideGameHandler.getMap().getHeight();
        unchangeableServerOptions.maxNumberOfPlayers = players.length;
        optionsChanged();           // Fill up changeableOptions.
    }
    
    /**
        Creates the remote object registry.
        @throws RemoteObjectRegistryCreationFailedException if creating remote object registry fails
    */
    private void createRemoteObjectRegistry() throws RemoteObjectRegistryCreationFailedException {
        try {
            if ( remoteObjectRegistry == null )
                remoteObjectRegistry = LocateRegistry.createRegistry( GeneralConsts.REGISTRY_PORT );
            remoteObjectRegistry.rebind( GeneralConsts.REMOTE_Q_SERVER_OBJECT_REGISTRY_NAME, this );
        }
        catch ( RemoteException re ) {
            throw new RemoteObjectRegistryCreationFailedException( GeneralConsts.REGISTRY_PORT );
        }
    }

    /**
        Returns the name of the server program (implementing RemoteQServer interface).
        @return the name of the server
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    public String getServerApplicationName() throws RemoteException {
        return GeneralConsts.APPLICATION_NAME;
    }
    
    /**
        Returns the version of the server program (implementing RemoteQServer interface).
        @return the version of the server
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    public String getServerApplicationVersion() throws RemoteException {
        return GeneralConsts.APPLICATION_VERSION;
    }
    
    /**
        Tries to get a player if passwords match and returns it, so a client can enter into the game.
        @param playerName name of player who wants to join
        @param password game password
        @return a RemotePlayerRequestResult the result of the request
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    public RemotePlayerRequestResult getRemotePlayer( final String playerName, final String password ) throws RemoteException {
        final boolean passwordsMatch = serverOptions.password.equals( "" ) ? true : serverOptions.password.equals( password );
        if ( !passwordsMatch )
            return new RemotePlayerRequestResult( false, null );
        for ( int playerIndex = 0; playerIndex < players.length; playerIndex++ )
            if ( players[ playerIndex ] == null ) {
                broadcastServerMessage( playerName + " has joined the game." );
                players[ playerIndex ] = new Player( playerIndex, informationsForDrawing, players, ( (ServerSideGameHandler) gameHandler ).getMap() );
                players[ playerIndex ].recieveMessage( serverOptions.welcomeMessage );
                return new RemotePlayerRequestResult( true, players[ playerIndex ] );
            }
        return new RemotePlayerRequestResult( true, null );
    }
    
    /**
        Returns the datas of the map (implementing RemoteQServer interface).
        @return the datas of the map
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    public byte[] getMapDatas() throws RemoteException {
        return mapDatas;
    }

    /**
        Returns the unchangeable server options.
        @return the unchangeable server options
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    public UnchangeableServerOptions getUnchangeableServerOptions() throws RemoteException {
        return unchangeableServerOptions;
    }

    /**
        Returns the informations needed for drawing (implementing RemoteQServer interface).
        @return the informations needed for drawing
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    public InformationsForDrawing getInformationsForDrawing() throws RemoteException {
        return informationsForDrawing;
    }

    /**
        Returns the decorations (implementing RemoteQServer interface).
        @return the decorations
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    public Vector getDecorations() throws RemoteException {
        return decorations;
    }
    
    /**
        Returns the changeable server options.
        @return the changeable server options
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    public ChangeableServerOptions getChangeableServerOptions() throws RemoteException {
        return changeableServerOptions;
    }

    /**
        Broadcasts a message to all players (implementing RemoteQServer interface).
        @param message the broadcastable message
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    public void broadcastMessage( final String message ) throws RemoteException {
        for ( int playerIndex = 0; playerIndex < players.length; playerIndex++ )
            if ( players[ playerIndex ] != null )
                players[ playerIndex ].recieveMessage( message );
    }
    
    /**
        Returns the string representation of values of server options that are changeable, effects to the game and clients does not requiring them (implementing RemoteQServer interface).
        @return the string representation of values of server options
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    public String getServerOptions() throws RemoteException {
        return serverOptions.toString();
    }

    /**
        Broadcasts a(n informative) server message.
        @param message the broadcastable message
    */
    public void broadcastServerMessage( final String message ) {
        try {
            broadcastMessage( SERVER_MESSAGE_PREFIX + message );
        }
        catch ( RemoteException re ) {
            Logging.logError( re );
        }
    }
    
    /**
        Sends a message to a player.
        @param message message to be sent
        @param playerName name of player to deliver the message to
        @return the result of message sending
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    public String sendMessageToPlayer( final String message, final String playerName ) throws RemoteException {
        boolean foundMatchingPlayerName = false;
        for ( int playerIndex = 0; playerIndex < players.length; playerIndex++ )
            if ( players[ playerIndex ] != null && players[ playerIndex ].getPlayerState().name.equals( playerName ) ) {
                foundMatchingPlayerName = true;
                players[ playerIndex ].recieveMessage( message );
            }
        return foundMatchingPlayerName ? message : SERVER_MESSAGE_PREFIX + "wrong player name!";
    }
    
    /**
        Sends a message to a team.
        @param message message to be sent
        @param teamName name of team whose members to deliver the message to
        @return the result of message sending
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    public String sendMessageToTeam( final String message, final String teamName ) throws RemoteException {
        boolean foundMatchingTeamName = false;
        for ( int playerIndex = 0; playerIndex < players.length; playerIndex++ )
            if ( players[ playerIndex ] != null && players[ playerIndex ].getPlayerState().getGroupName( players[ playerIndex ].getPlayerState().getGroupIdentifier() ).equalsIgnoreCase( teamName ) ) {
                foundMatchingTeamName = true;
                players[ playerIndex ].recieveMessage( message );
            }
        return  foundMatchingTeamName ? message : SERVER_MESSAGE_PREFIX + "nobody is in that team!";
    }

    /**
        Returns the elapsed time since game started.
        @return the elapsed time since game started
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    public long getElapsedGameTime() throws RemoteException {
        return ( (ServerSideGameHandler) gameHandler ).getElapsedTime();
    }

    /**
        Here we shut down the server.
    */
    public void close() {
        super.close();
        playersCheckingTimer.requestToCancel();
        for ( int playerIndex = 0; playerIndex < players.length; playerIndex++ )
            if ( players[ playerIndex ] != null && !players[ playerIndex ].closed() )
                try {
                    players[ playerIndex ].close();
                }
                catch ( RemoteException re ) {
                    Logging.logError( re );
                }
        try {
            final String[] registryEntries = remoteObjectRegistry.list();
            for ( int registryEntryIndex = 0; registryEntryIndex < registryEntries.length; registryEntryIndex++ )
                remoteObjectRegistry.unbind( registryEntries[ registryEntryIndex ] );
            Thread.sleep( WAITING_TIME_BEFORE_SHUT_DOWN );
        }
        catch ( Exception e ) {
            Logging.logError( e );
        }
    }

    /**
        To indicate time-up event (implementing Timeable interface).
    */
    public void timeIsUp() {
        for ( int playerIndex = 0; playerIndex < players.length; playerIndex++ )
            if ( players[ playerIndex ] != null && !players[ playerIndex ].closed() )
                if ( players[ playerIndex ].getIdleTime() > MAX_PLAYER_IDLE_TIME )
                    try {
                        final String playerName = players[ playerIndex ].getPlayerState().name;
                        players[ playerIndex ].close();
                        players[ playerIndex ] = null;
                        broadcastServerMessage( "Connection to " + playerName + " lost, player has been removed." );
                    }
                    catch ( RemoteException re ) {
                        Logging.logError( re );
                    }
    }
    
    /**
        This method will be called when server options has changed.
    */
    public void optionsChanged() {
        changeableServerOptions.gameType = serverOptions.gameType;
        informationsForDrawing.changeableServerOptionsChangeCounter++;
    }

    /**
        Kicks a player out of game.
        @param playerIndex index of player to be kicked
        @param kickMessage reason of kicking
        @return the result of kicking
    */
    private String kickPlayer( final int playerIndex, final String kickMessage ) {
        if ( playerIndex < 0 || playerIndex > players.length - 1 || players[ playerIndex ] == null )
            return "Invalid player index!";
        players[ playerIndex ].kick( kickMessage );
        return "Player has been kicked.";
    }

    /**
        Processes a command.
        @param command command to be processed
        @return true if command processing was successful, false if invalid command was given
    */
    public boolean processCommand( final String command ) {
        StringTokenizer stringTokenizer;
        switch ( commandProcessor.searchCommand( command ) ) {

            case COMMAND_KICK_PLAYER :
                stringTokenizer = new StringTokenizer( command, "|" );
                if ( stringTokenizer.countTokens() < 2 ) {
                    mainFrame.addMessage( "Syntax: " + COMMAND_NAMES[ COMMAND_KICK_PLAYER ][ 0 ] + "|player index[|reason of kicking]" );
                    break;
                }
                stringTokenizer.nextToken();    // The command
                try {
                    final int playerIndex = Integer.parseInt( stringTokenizer.nextToken() );
                    mainFrame.addMessage( kickPlayer( playerIndex, stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : null ) );
                }
                catch ( NumberFormatException ne ) {
                    mainFrame.addMessage( "Player must be identified by number!" );
                }
                break;

            case COMMAND_LIST_PLAYERS :
                final StringBuffer playersList = new StringBuffer();
                for ( int playerIndex = 0; playerIndex < players.length; playerIndex++ )
                    if ( players[ playerIndex ] != null ) {
                        playersList.append( playerIndex );
                        playersList.append( " - " );
                        playersList.append( players[ playerIndex ].getPlayerState().name );
                        if ( playerIndex != players.length - 1 )
                            playersList.append( '\n' );
                    }
                mainFrame.addMessage( "Players list:\n" + playersList );
                break;

            case COMMAND_SEND_MESSAGE :
                stringTokenizer = new StringTokenizer( command, "|" );
                if ( stringTokenizer.countTokens() < 2 ) {
                    mainFrame.addMessage( "Syntax: " + COMMAND_NAMES[ COMMAND_SEND_MESSAGE ][ 0 ] + "|message" );
                    break;
                }
                stringTokenizer.nextToken();    // The command
                try {
                    broadcastMessage( "<operator>: " + stringTokenizer.nextToken() );
                    mainFrame.addMessage( "Message has been sent." );
                }
                catch ( RemoteException re ) {
                    Logging.logError( re );
                }
                break;

            case CommandProcessor.COMMAND_INVALID :
                return false;
//              break;             // F*cking java compiler does not allow this line!!

        }
        return true;
    }

    /**
        Returns the list of the server commands.
        @return the list of the server commands
    */
    public String getCommandList() {
        return commandProcessor.getCommandList();
    }

}
