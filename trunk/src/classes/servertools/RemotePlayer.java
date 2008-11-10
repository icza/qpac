
package classes.servertools;

import java.rmi.*;
import java.io.*;
import java.util.*;


/**
    Interface to control a player from remote.
    @author Belicza Andras
*/
public interface RemotePlayer extends Remote, Serializable {
    
    /**
        Closes the player.
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    void close() throws RemoteException;
    
    /**
        Clears the players idle time.
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    void clearIdleTime() throws RemoteException;

    /**
        Returns whether the player is closed.
        @return true if the player is closed; false otherwise
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    boolean isClosed() throws RemoteException;

    /**
        Returns own index in players array.
        @return own index in players array
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    int getOwnIndex() throws RemoteException;
    
    /**
        Sets the color of player's worm.
        @param wormColorIndex color of the worm
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    void setWormColorIndex( final int wormColorIndex ) throws RemoteException;
    
    /**
        Sets the player's name.
        @param name the name of the player
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    void setName( final String name ) throws RemoteException;

    /**
        Sets the state of a control key.
        @param keyIndex the index of the key
        @param state state of the key, true if pressed, false if released
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    void setControlKeyState( final int keyIndex, final boolean state ) throws RemoteException;
    
    /**
        Sets the position of the mouse.
        @param x x position of the mouse
        @param y y position of the mouse
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    void setMousePosition( final float x, final float y ) throws RemoteException;

    /**
        Selects a weapon.
        @param weaponIndex index of the weapon to be selected
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    void selectWeapon( final int weaponIndex ) throws RemoteException;

    /**
        Rotates the selected weapon with the specified number.
        @param number number to rotate selected weapon by
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    void rotateWeaponBy( final int number ) throws RemoteException;

    /**
        Checks if has at least one new message.
        @return true if has new message(s); false otherwise
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    boolean hasNewMessage() throws RemoteException;

    /**
        Returns the messages.
        @return the messages
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    Vector getMessages() throws RemoteException;

    /**
        Clears the vector of messages.
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    void clearMessages() throws RemoteException;
    
    /**
        Requests a pause if game wasn't paused, or resume if game was paused.
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    void requestForPauseOrResume() throws RemoteException;
    
    /**
        Returns true, if player was kicked.
        @return true, if player was kicked; false otherwise
    */
    boolean isKicked() throws RemoteException;
    
    /**
        Returns the kick message.
        @return the kick message
    */
    String getKickMessage() throws RemoteException;

}
