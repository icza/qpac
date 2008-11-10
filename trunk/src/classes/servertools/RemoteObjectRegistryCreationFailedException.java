
package classes.servertools;


/**
    Thrown when server fails to create remote object registry.
    @author Belicza Andras
*/
public class RemoteObjectRegistryCreationFailedException extends Exception {
    
    /**
        Creates a new RemoteObjectRegistryCreationFailedException.
        @param port port where the trying of registry creation failed
    */
    public RemoteObjectRegistryCreationFailedException( final int port ) {
        super( "Failed to create remote object registry on port " + port + "." );
    }
    
}
