
package classes.servertools.gamecore;

import java.awt.*;


/**
    Constants of the rope. We separate this because ropes (with playerStates) are downloaded and created on all client's computer at all time.
    So we don't want these constant values to be downloaded (created) all the times.
    @author Belicza Andras
*/
interface RopeConsts {

    /** Mass of the rope (end part). (kg) */
    float MASS                  = 1.0f;
    /** Shape resistance rate of the rope (end part). */
    float SHAPE_RESISTANCE_RATE = 0.01f;
    /** Physical capacity of the rope (end part). (m^3) */
    float CAPACITY              = 0.02f;
    /** Width of the rope (end part). (pixel)*/
    int   WIDTH                 = 6;
    /** Height of the rope (end part). (pixel)*/
    int   HEIGHT                = WIDTH;
    /** Anti gravitation rate of the rope (end part). */
    float ANTI_GRAVITATION_RATE = 0.95f;
    /** Color of the ropes. */
    Color ROPE_COLOR            = Color.white;

}
