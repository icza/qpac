
package classes.utilities;


/**
    An extra featured, controllable thread class.
    @author Belicza Andras
*/
public abstract class ControllableThread extends Thread implements Timeable {

    /** Running state of the thread. */
    private boolean          running = false;
    /** Indicating that there was a request to cancel the thread. */
    private boolean          requestedToCancel = false;
    /** Timer to control the calls of the operate() method. */
    private Timer            timer;
    /** Indicating time-up evetns. */
    private volatile boolean timeIsUp = false;
    /** Cycle counter. */
    private int              cycleCounter;

    /**
        Creates a new ControlledThread.
        @param operatePeriodTime period time in ms to recall method operate() until a request to cancel arrives
    */
    public ControllableThread( final long operatePeriodTime ) {
        timer = new Timer( operatePeriodTime, this );
    }

    /**
        Where the new thread begins...
    */
    public final void run() {
        cycleCounter = 0;
        running      = true;
        firstOperate();
        if ( !requestedToCancel ) {
            timer.start();
            try {
                while ( !requestedToCancel ) {
                    while ( !timeIsUp && !requestedToCancel )
                        sleep( 1 );
                    timeIsUp = false;
                    if ( !requestedToCancel ) {
                        operate();
                        cycleCounter++;
                    }
                }
            }
            catch ( InterruptedException ie ) {
                Logging.logError( ie );
            }
            timer.requestToCancel();
        }
        lastOperate();
        requestToCancel();
        running = false;
    }

    /**
        To indicate time-up event (implementing Timeable interface).
    */
    public void timeIsUp() {
        timeIsUp = true;
    }
    
    /**
        This method will be executed first before the cycles.
    */
    protected void firstOperate() {
    }

    /**
        This method have to do the cyclycal repeatable work.
    */
    protected abstract void operate();

    /**
        This method will be executed lastly after the cycles.
    */
    protected void lastOperate() {
    }

    /**
        Returns the running state of the thread.
        @return the running state of the thread
    */
    public boolean isRunning() {
        return running;
    }

    /**
        Indicating that we wish to end the thread.
    */    
    public void requestToCancel() {
        timer.requestToCancel();
        requestedToCancel = true;
    }

    /**
        Waits until this thread will stop, until the run method will end.
    */
    public void waitUntilRunning() {
        try {
            while ( running )
                sleep( 1 );
        }
        catch ( InterruptedException ie ) {
            Logging.logError( ie );
        }
    }
    
    /**
        Sets the period time of reexecutioning of operate() method.
        @param periodTime the period time in ms
    */
    public void setOperatePeriodTime( final long periodTime ) {
        timer.setPeriodTime( periodTime );
    }

    /**
        Returns the value of cycle counter.
        @return the value of cycle counter
    */
    public int getCycleCounter() {
        return cycleCounter;
    }

}
