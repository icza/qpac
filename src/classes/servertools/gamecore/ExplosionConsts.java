
package classes.servertools.gamecore;

import java.awt.*;


/**
    Constants of the explosions. We separate this because explosions are downloaded and created on all client's computer at all time.
    So we don't want these constant values to be downloaded (created) all the times.
    @author Belicza Andras
*/
interface ExplosionConsts {

    /** Value of range of explosion to decrease at next cycle. */
    int     DELTA_RANGE            = 5;
    /** Color of the zones of the explosions. */
    Color[] ZONE_COLORS            = new Color[ 8 ];
    /** Differences between the ranges of the explosion zones. */
    int     ZONE_RANGE_DIFFERENCES = 15;

}
