
package classes.servertools;

import java.io.*;


/**
    The result of a remote player request.
    @author Belicza Andras
*/
public class RemotePlayerRequestResult implements Serializable {

    /** Tells if password was accepted. */
    private boolean      passwordAccepted;
    /** A remote player object. */
    private RemotePlayer remotePlayer;
    
    /**
        Creates a new RemotePlayerRequestResult.
        @param passwordAccepted tells whether password was accepted
        @param remotePlayer reference to the given remotePlayer if password was accepted; null otherwise
    */
    public RemotePlayerRequestResult( final boolean passwordAccepted, final RemotePlayer remotePlayer ) {
        this.passwordAccepted = passwordAccepted;
        this.remotePlayer     = remotePlayer;
    }
    
    /**
        Returns the value of password accepted.
        @return true, if password was accepted; false otherwise
    */
    public boolean passwordAccepted() {
        return passwordAccepted;
    }

    /**
        Returns the reference to the given remote player.
        @return the reference to the given remote player
    */
    public RemotePlayer getRemotePlayer() {
        return remotePlayer;
    }
    
}
