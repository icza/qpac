
package classes;


/**
    This interface contains common constants for serveral classes.
    @author Belicza Andras
*/
public interface GeneralConsts {

    /** Name of the application. */
    String APPLICATION_NAME    = "Qpac";
    /** Version (and sub-version) of the application. */
    String APPLICATION_VERSION = "1.0 untested";
    /** Name of directory contains persistent datas of the application. */
    String DATAS_DIRECTORY     = "datas/";
    /** Name of directory contains the persistent option datas of application. */
    String OPTIONS_DIRECTORY   = DATAS_DIRECTORY;

    /** Port of the remote object registry. */
    int REGISTRY_PORT = 21716;
    
    /** The name of server information object in the remote object registry. */
    String REMOTE_Q_SERVER_OBJECT_REGISTRY_NAME = "RemoteQServer";
    
    /** Width of the walls. */
    int   WALL_WIDTH                  = 32;
    /** Height of the walls. */
    int   WALL_HEIGHT                 = 32;
    /** Number of bits needed to shift an x pixel position to get the map index. */
    int   WALL_WIDTH_SHIFT            =  5;
    /** Number of bits needed to shift a y pixel position to get the map index. */
    int   WALL_HEIGHT_SHIFT           =  5;
    /** Width of the worm. */
    int   WORM_WIDTH                  = 21;
    /** Height of the worm. */
    int   WORM_HEIGHT                 = 26;
    /** Constant value for the left direction on the ground. */
    int   WORM_DIRECTION_GROUND_RIGHT = 0;
    /** Constant value for the right direction on the ground. */
    int   WORM_DIRECTION_GROUND_LEFT  = 1;
    /** Constant value for the left direction in the air (and in water). */
    int   WORM_DIRECTION_AIR_RIGHT    = 2;
    /** Constant value for the right direction in the air (and in water). */
    int   WORM_DIRECTION_AIR_LEFT     = 3;
    /** Count of the directions. */
    int   WORM_DIRECTIONS_COUNT       = 4;
    /** Count of phases in the different directions. */
    int[] WORM_PHASES_COUNT           = { 3, 3, 4, 4 };
    /** The value of the standing phase index in the different directions. */
    int[] WORM_STANDING_PHASES        = { 1, 1, 1, 2 };
    /** Maximum (initial) hit points of the players. */
    int   MAX_PLAYER_HIT_POINT        =  200;
    /** Maximum level of the oxygen bottle (the full status). */
    int   MAX_OXYGEN_BOTTLE_LEVEL     = 1000;

}
