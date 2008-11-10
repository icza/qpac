
package classes;

import java.rmi.*;
import classes.servertools.*;
import classes.clienttools.*;
import classes.utilities.*;
import classes.options.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;


/**
    This class provides/controlls the client part of the game.
    @author Belicza Andras
*/
public class Client extends GameHandlingServiceProvider {

    /** Name of the commands. */
    public  static final String[][] COMMAND_NAMES = { { "/getserveroptions", "/gso" }            , { "/messageplayer", "/mp" }, { "/messageteam", "/mt" }, { "/elapsedtime", "/et" } };
    /** Sort (1 line) description of the commands. */
    private static final String[]   COMMAND_INFOS = { "get the global server options of the game", "send message to a player" , "send message to a team" , " show elapsed time"      };
    /** Get server options command. */
    public  static final int COMMAND_GET_SERVER_OPTIONS = 0;
    /** Message player command. */
    private static final int COMMAND_MESSAGE_PLAYER     = 1;
    /** Message team command. */
    private static final int COMMAND_MESSAGE_TEAM       = 2;
    /** Elapsed time command. */
    private static final int COMMAND_ELAPSED_TIME       = 3;

    /** Reference to the main frame. */
    private final MainFrame        mainFrame;
    /** Reference to a remote player object. */
    private RemotePlayer           remotePlayer;
    /** Remote Qpac Server. */
    private RemoteQServer          remoteQServer;
    /** Reference to the clientOptions. */
    private final ClientOptions    clientOptions;
    /** The client command processor. */
    private final CommandProcessor commandProcessor = new CommandProcessor( COMMAND_NAMES, COMMAND_INFOS );

    /**
        Creates a new Client.
        @param mainFrame reference to the main frame
        @param gameSceen reference to the game sceen
        @param isDestinationHostUserDefined tells whether we have to connect to the host given at client options, or to our local server
        @param clientOptions the client options
        @param localServerPassword if we join to our server, this holds the password of local server; null otherwise
        @param wallImages reference to the wall images
        @param wormImages reference to the worm images
        @throws Exception if error occurs during connecting to server
        @param brickWallRGBRepresenter rgb representer of the brick walls
    */
    Client( final MainFrame mainFrame, final GameSceen gameSceen, final boolean isDestinationHostUserDefined, final ClientOptions clientOptions, final String localServerPassword, final Image[] wallImages, final Image[][][] wormImages, final int brickWallRGBRepresenter ) throws Exception {
        this.mainFrame = mainFrame;
        this.clientOptions = clientOptions;
        connectToServer( isDestinationHostUserDefined, this.clientOptions, localServerPassword );
        setGameHandlingServiceProvider( new ClientSideGameHandler( this.mainFrame, gameSceen, this.clientOptions, remoteQServer, remotePlayer, wallImages, wormImages, brickWallRGBRepresenter ) );
    }
    
    /**
        Connects to the server, and gets a remote client stub.
        @param isDestinationHostUserDefined tells whether we have to connect to the host given at client options, or to our local server
        @param clientOptions the client options
        @param localServerPassword if we join to our server, this holds the password of local server; null otherwise
        @throws Exception if error occurs during the connection
    */
    private void connectToServer( final boolean isDestinationHostUserDefined, final ClientOptions clientOptions, final String localServerPassword ) throws Exception {
        String destinationHost = isDestinationHostUserDefined ? clientOptions.serverHost : "localhost";
        destinationHost = "rmi://" + destinationHost + ':' + GeneralConsts.REGISTRY_PORT + '/';

        try {
            remoteQServer = (RemoteQServer) Naming.lookup( destinationHost + GeneralConsts.REMOTE_Q_SERVER_OBJECT_REGISTRY_NAME );
        }
        catch ( NotBoundException ne ) {
            throw new ConnectingToServerFailedException( "Not running game actually on destination host." );
        }
        catch ( RemoteException re ) {
            throw new ConnectingToServerFailedException( "Not running server on destination host." );
        }

        if ( !remoteQServer.getServerApplicationName().equals( GeneralConsts.APPLICATION_NAME ) )
            throw new ConnectingToServerFailedException( "Server is not " + GeneralConsts.APPLICATION_NAME + " server." );
        if ( !remoteQServer.getServerApplicationVersion().equals( GeneralConsts.APPLICATION_VERSION ) )
            throw new ConnectingToServerFailedException( "Incompatible server version: " + remoteQServer.getServerApplicationVersion() );
        final RemotePlayerRequestResult remotePlayerRequestResult = remoteQServer.getRemotePlayer( clientOptions.playerName, isDestinationHostUserDefined ? clientOptions.password : localServerPassword );
        if ( !remotePlayerRequestResult.passwordAccepted() )
            throw new ConnectingToServerFailedException( "Passwords do not match!" );
        if ( ( remotePlayer = remotePlayerRequestResult.getRemotePlayer() ) == null )
            throw new ConnectingToServerFailedException( "Server is full!" );
    }

    /**
        Here we close the client.
    */
    public void close() {
        super.close();
        try {
            remotePlayer.close();
        }
        catch ( RemoteException re ) {
        }
    }
    
    /**
        Sends a message to the server.
        @param message message to be sent
    */
    public void sendMessage( final String message ) {
        if ( remoteQServer != null && remotePlayer != null )
            try {
                remoteQServer.broadcastMessage( clientOptions.playerName + ": " + message );
            }
            catch ( RemoteException re ) {
            }
    }

    /**
        Processes a command.
        @param command command to be processed
        @return true if command processing was successful, false if invalid command was given
    */
    public boolean processCommand( final String command ) {
        StringTokenizer stringTokenizer;
        String          message;
        try {
            switch ( commandProcessor.searchCommand( command ) ) {

                case COMMAND_GET_SERVER_OPTIONS :
                    mainFrame.addMessage( "Global server options:\n" + ( (ClientSideGameHandler) gameHandler ).getUnchangeableServerOptions() + ", " + remoteQServer.getServerOptions() );
                    break;

                case COMMAND_MESSAGE_PLAYER :
                    stringTokenizer = new StringTokenizer( command, "|" );
                    if ( stringTokenizer.countTokens() < 3 ) {
                        mainFrame.addMessage( "Syntax: " + COMMAND_NAMES[ COMMAND_MESSAGE_PLAYER ][ 0 ] + "|player name|message" );
                        break;
                    }
                    stringTokenizer.nextToken();    // The command
                    final String playerName = stringTokenizer.nextToken();
                    message = clientOptions.playerName + " to " + playerName + ": " + stringTokenizer.nextToken();
                    mainFrame.addMessage( remoteQServer.sendMessageToPlayer( message, playerName ) );
                    break;

                case COMMAND_MESSAGE_TEAM :
                    stringTokenizer = new StringTokenizer( command, "|" );
                    if ( stringTokenizer.countTokens() < 3 ) {
                        mainFrame.addMessage( "Syntax: " + COMMAND_NAMES[ COMMAND_MESSAGE_TEAM ][ 0 ] + "|team name|message" );
                        break;
                    }
                    stringTokenizer.nextToken();    // The command
                    final String teamName = stringTokenizer.nextToken();
                    message = clientOptions.playerName + " to " + teamName + " team: " + stringTokenizer.nextToken();
                    mainFrame.addMessage( remoteQServer.sendMessageToTeam( message, teamName ) );
                    break;

                case COMMAND_ELAPSED_TIME :
                    final long elapsedTimeSeconds = remoteQServer.getElapsedGameTime() / 1000;
                    mainFrame.addMessage( "Elapsed time: " + elapsedTimeSeconds / 60 / 60 % 60 + ':' + elapsedTimeSeconds / 60 % 60 + ':' + elapsedTimeSeconds % 60 );
                    break;

                case CommandProcessor.COMMAND_INVALID :
                    return false;
//                  break;             // F*cking java compiler does not allow this line!!

            }
        }
        catch ( RemoteException re ) {
        }
        return true;
    }

    /**
        Returns the list of the client commands.
        @return the list of the client commands
    */
    public String getCommandList() {
        return commandProcessor.getCommandList();
    }

}
