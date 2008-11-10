
package classes.clienttools;

import java.io.*;


/**
    This class holds the unchangeable server options. Clients download once an instance of this class to get these options.
    @author Belicza Andras
*/
public class UnchangeableServerOptions implements Serializable {

    /** Value of the serverOptions.mapWidth at the time when a new game starts. */
    public int mapWidth;
    /** Value of the serverOptions.mapWidth at the time when a new game starts. */
    public int mapHeight;
    /** Value of the serverOptions.maxNumberOfPlayers at the time when a new game starts. */
    public int maxNumberOfPlayers;
    
    /**
        Redefining toString() method. Returns the string representation of unchangeable server options.
        @return the string representation of unchangeable server options
    */
    public String toString() {
        return "map width = " + mapWidth + ", map height = " + mapHeight + ", max number of players = " + maxNumberOfPlayers;
    }
    
}
