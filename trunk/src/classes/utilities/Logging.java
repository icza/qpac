
package classes.utilities;

import java.util.*;


/**
    This class logs errors.
    @author Belicza Andras
*/
public class Logging {

    /**
        Logging unexpected exceptions.
        @param exception the unexpected exception
    */
    public static void logError( final Exception exception ) {
        System.err.println( "\nUnexpected exception has occured. Time: " + new Date() );
        exception.printStackTrace( System.err );
    }
    
}
