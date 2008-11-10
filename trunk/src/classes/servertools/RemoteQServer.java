
package classes.servertools;

import java.rmi.*;
import classes.servertools.*;
import classes.clienttools.*;
import java.util.*;


/**
    Interface of the remote Server.
    (RemoteServer name is already in use...)
    @author Belicza Andras
*/
public interface RemoteQServer extends Remote {
    
    /**
        Returns the name of the server program.
        @return the name of the server
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    String getServerApplicationName() throws RemoteException;
    
    /**
        Returns the version of the server program.
        @return the version of the server
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    String getServerApplicationVersion() throws RemoteException;
    
    /**
        Tries to get a player if passwords match and returns it, so a client can enter into the game.
        @param playerName name of player who wants to join
        @param password game password
        @return a RemotePlayerRequestResult the result of the request
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    RemotePlayerRequestResult getRemotePlayer( final String playerName, final String password ) throws RemoteException;
    
    /**
        Returns the datas of the map. This is separated from getInformationsForDrawing, because there is an option that map datas are not downloaded in every cycles.
        @return the datas of the map
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    byte[] getMapDatas() throws RemoteException;
    
    /**
        Returns the unchangeable server options.
        @return the unchangeable server options
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    UnchangeableServerOptions getUnchangeableServerOptions() throws RemoteException;

    /**
        Returns the informations needed for drawing.
        @return the informations needed for drawing
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    InformationsForDrawing getInformationsForDrawing() throws RemoteException;
    
    /**
        Returns the decorations.
        @return the decorations
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    Vector getDecorations() throws RemoteException;
    
    /**
        Returns the changeable server options.
        @return the changeable server options
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    ChangeableServerOptions getChangeableServerOptions() throws RemoteException;

    /**
        Returns the string representation of values of server options that are changeable, effects to the game and clients does not requiring them.
        @return the string representation of values of server options
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    String getServerOptions() throws RemoteException;

    /**
        Broadcasts a message to all players.
        @param message the broadcastable message
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    void broadcastMessage( final String message ) throws RemoteException;

    /**
        Sends a message to a player.
        @param message message to be sent
        @param playerName name of player to deliver the message to
        @return the result of message sending
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    String sendMessageToPlayer( final String message, final String playerName ) throws RemoteException;

    /**
        Sends a message to a team.
        @param message message to be sent
        @param teamName name of team whose members to deliver the message to
        @return the result of message sending
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    String sendMessageToTeam( final String message, final String teamName ) throws RemoteException;
    
    /**
        Returns the elapsed time since game started.
        @return the elapsed time since game started
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    long getElapsedGameTime() throws RemoteException;

}
