
package classes.clienttools;

import java.io.*;


/**
    This class holds the changeable server options. When these options may have changed, clients download an instance of this class to get the new options.
    @author Belicza Andras
*/
public class ChangeableServerOptions implements Serializable {

    /** Value of the serverOptions.gameType. */
    public int gameType;
    
}
