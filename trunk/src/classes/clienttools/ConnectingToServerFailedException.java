
package classes.clienttools;


/**
    Thrown when trying to connect to a server fails.
    @author Belicza Andras
*/
public class ConnectingToServerFailedException extends Exception {
    
    /**
        Creates a new ConnectingToServerFailedException.
        @param message details of the exception
    */
    public ConnectingToServerFailedException( final String message ) {
        super( message );
    }
}
