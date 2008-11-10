
package classes.utilities;


/**
    A class provides a game handling service inherited from ControllableThread.
    @author Belicza Andras
*/
public class GameHandlingServiceProvider {
    
    /** The game handler object. */
    protected ControllableThread gameHandler;
    
    /**
        Sets the game handler object.
        @param gameHandler the game handler object that provides the service
    */
    public void setGameHandlingServiceProvider( final ControllableThread gameHandler ) {
        this.gameHandler = gameHandler;
    }
    
    /**
        Starts the game handler.
    */
    public void start() {
        gameHandler.start();
    }
    
    /**
        Closes the game handler.
    */
    public void close() {
        gameHandler.requestToCancel();
        gameHandler.waitUntilRunning();
    }

}
