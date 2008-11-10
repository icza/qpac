
package classes.clienttools;

import classes.servertools.gamecore.*;
import java.io.*;
import java.util.*;


/**
    Informations needed a client for drawing.
    @author Belicza Andras
*/
public class InformationsForDrawing implements Serializable {
    
    /** States of the players. */
    public final PlayerState[] playerStates;
    /** Bullets. */
    public final Vector        bullets;
    /** Explosions. */
    public final Vector        explosions;
    /** Polyline shots. */
    public final Vector        polylineShots;
    /** Counter of the changes of the changeable server options. */
    public int                 changeableServerOptionsChangeCounter = 0;
    
    /**
        Creates a new InformationsForDrawing.
        @param playerStates states of the players
        @param bullets bullets
        @param explosions explosions
        @param polylineShots polyline shots
    */
    public InformationsForDrawing( final PlayerState[] playerStates, final Vector bullets, final Vector explosions, final Vector polylineShots ) {
        this.playerStates  = playerStates;
        this.bullets       = bullets;
        this.explosions    = explosions;
        this.polylineShots = polylineShots;
    }

}
