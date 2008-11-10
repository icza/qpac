
package classes.utilities;


/**
    This is a timer thread. Generates time-up events (method calls) with the frequency calculated from the period time.
    @author Belicza Andras
*/
public class Timer extends Thread {

    /** Period time of time-up events (method calls) in ms. */
    private long     periodTime;
    /** Object to indicate to the time-up events. */
    private Timeable timeable;
    /** Indicating request to cancel timer. */
    private boolean  requestedToCancel = false;
    
    /**
        Creates a new Timer.
        @param periodTime period time of the time-up events (method calls)
        @param timeable   object to indicate to the time-up events
    */
    public Timer( final long periodTime, final Timeable timeable ) {
        this.periodTime = periodTime;
        this.timeable   = timeable;
    }
    
    /**
        Beginning of timing...
    */
    public void run() {
        try {
            while ( !requestedToCancel ) {
                sleep( periodTime );
                timeable.timeIsUp();
            }
        }
        catch ( InterruptedException ie ) {
            Logging.logError( ie );
        }
    }

    /**
        Indicate request to cancel timer.
    */
    public void requestToCancel() {
        requestedToCancel = true;
    }
    
    /**
        Sets the period time of time-up events.
        @param periodTime the period time in ms
    */
    public void setPeriodTime( final long periodTime ) {
        this.periodTime = periodTime;
    }

}
