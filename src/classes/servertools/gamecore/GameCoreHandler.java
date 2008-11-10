
package classes.servertools.gamecore;

import classes.servertools.*;
import java.util.*;
import classes.*;
import classes.servertools.gamecore.bullets.*;
import classes.servertools.gamecore.decorations.*;
import java.awt.*;
import classes.options.*;
import classes.utilities.*;
import classes.servertools.gamecore.weapons.*;


/**
    This is the engine of the game, handler of the core of the game.
    (Next moment calculating, interpreting control key states...)
    @author Belicza Andras
*/
public class GameCoreHandler {

    /** The measure of gravitation acceleration (m/s^2). */
    private static final float  GRAVITATION                       = 0.5f;
    /** If Rate = 0.5 and velcity = 5.34 m/s, then shapeResistanceForce = 1 N. (kg/m) */
    private static final float  AIR_SHAPE_RESISTANCE_FORCE_UNIT   = 0.03f;
    /** If Rate = 0.5 and velcity = 3.16 m/s, then shapeResistanceForce = 1 N. (kg/m) */
    private static final float  WATER_SHAPE_RESISTANCE_FORCE_UNIT = 0.2f;
    /** Maximum value of shape resistance force (N). */
    private static final float  MAX_SHAPE_RESISTANCE_FORCE        = 2.0f;
    /** Force to move worm when left or right key is pressed (N). */
    private static final float  HORIZONTAL_MOVING_FORCE           = 0.01f;
    /** Physical density of the water (kg/m^3). */
    private static final float  WATER_DENSITY                     = 1000.0f;
    /** The physical friction rate of the ground (mû). */
    private static final float  GROUND_FRICTION_RATE              = 0.3f;
    /** Initial velocity of the rope (end part). (m/s)*/
    private static final float  ROPE_VELOCITY                     = 12.0f;
    /** Climbing velocity of a worm on the rope (m/s). */
    private static final float  CLIMBING_VELOCITY                 = 2.5f;

    /** Jumping velocity (m/s). */
    private static final float  JUMPING_VELOCITY                  = 8.5f;
    /** Walking velocity of a worm on the ground (m/s). */
    private static final float  WALKING_VELOCITY                  = 1.3f;
    /** Worm acceleration on ground when left or right key is pressed (m/s^2).*/
    private static final float  GROUND_ACCELERATION               = 0.25f;
    /** Flying velocity of a worm in the air (m/s). */
    private static final float  FLYING_VELOCITY                   = 1.2f;
    /** Worm acceleration in the air when left or right key is pressed (m/s^2).*/
    private static final float  AIR_ACCELERATION                  = 0.1f;
    /** Swimming velocity of a worm in the water (m/s). */
    private static final float  SWIMMING_VELOCITY                 = 0.8f;
    /** Worm acceleration in the water when left or right key is pressed (m/s^2).*/
    private static final float  WATER_ACCELERATION                = 0.15f;
    /** Invalid player index value to indicate none of players is selected for an action. */
    public  static final int    INVALID_PLAYER_INDEX              = -1;
    /** Maximum (initial) hit points of the walls. */
    private static final int    MAX_WALL_HIT_POINT                = 200;
    /** Amount of wall rubbles when a wall has been fired out (number of point decoraions). */
    private static final int    AMOUNT_OF_WALL_RUBBLES_OF_A_WALL  = 30;
    /** Total amount of blood of a player: a player will send out this amount of blood from born to death (number of point decorations). */
    private static final int    TOTAL_AMOUNT_OF_BLOOD_OF_A_PLAYER = 200;

    /** Reference to the server. */
    private final Server        server;
    /** Reference to the serverOptions. */
    private final ServerOptions serverOptions;
    /** Reference to the players. */
    private final Player[]      players;
    /** Reference to the map. */
    private final Map           map;
    /** Bullets. */
    private final Vector        bullets       = new Vector();
    /** Explosions. */
    private final Vector        explosions    = new Vector();
    /** Terrain decorations. */
    private final Vector        decorations   = new Vector();
    /** Polyline shots: lasers and lightnings. */
    private final Vector        polylineShots = new Vector();
    /** The wind. */
    private final FloatVector   windVelocity  = new FloatVector();
    /** Hit points of the walls. */
    private final int[][]       wallHitPoints;
    /** We counts the cycles to ourselves, because cycles during game pauses does not count.*/
    private int                 cycleCounter = -1;

    /**
        Creates a new GameCoreHandler.
        @param server reference to the server
        @param serverOptions reference to the server options
        @param players reference to the players
        @param map reference to the map
    */
    public GameCoreHandler( final Server server, final ServerOptions serverOptions, final Player[] players, final Map map ) {
        this.server        = server;
        this.serverOptions = serverOptions;
        this.players       = players;
        this.map           = map;
        wallHitPoints      = new int[ this.map.getHeight() ][ map.getWidth() ];
        for ( int y = 0; y < wallHitPoints.length; y++ )
            for ( int x = wallHitPoints[ y ].length - 1; x >= 0; x-- )
                wallHitPoints[ y ][ x ] = MAX_WALL_HIT_POINT;
    }

    /**
        Calculates the next moment of the game.
    */
    public void calculateNextMoment() {
        cycleCounter++;
        calculatePlayersForces();
        stepBullets      ();
        stepDecorations  ();
        stepRopes        ();
        stepPlayers      ();
        stepExplosions   ();
        stepPolylineShots();
    }

//--------------------------------------------------------------------------------------------------------------------
    /**
        Calculates the forces affects to the players.
    */
    private void calculatePlayersForces() {
        for ( int playerIndex = 0; playerIndex < players.length; playerIndex++ ) {
            final Player player = players[ playerIndex ];
            if ( player != null ) {
                final PlayerState playerState  = player.getPlayerState();
                calculateForce( playerState );
                if ( playerState.positionType == MovingObjectConsts.POSITION_TYPE_ON_GROUND )
                    playerState.setGroundDirection();
                else
                    playerState.setAirDirection();
                handlePlayerKeys( player );
            }
        }
    }

    /**
        Checks the states of the control keys and does what needed.
        @param player player to be handled
    */
    private void handlePlayerKeys( final Player player ) {
        final PlayerState playerState      = player.getPlayerState();
        final boolean[]   controlKeyStates = player.getControlKeyStates();
        if ( !( controlKeyStates[ Player.KEY_INDEX_LEFT  ] || controlKeyStates[ Player.KEY_INDEX_RIGHT ] ) )
            playerState.setStandingPhase();
        handlePlayerKeysCommonPart( player, controlKeyStates );
        switch ( playerState.positionType ) {
            case MovingObjectConsts.POSITION_TYPE_ON_GROUND :
                handlePlayerKeysOnGround( playerState, controlKeyStates );
                break;
            case MovingObjectConsts.POSITION_TYPE_IN_AIR :
                handlePlayerKeysInAir( playerState, controlKeyStates );
                break;
            case MovingObjectConsts.POSITION_TYPE_IN_WATER :
                handlePlayerKeysInWater( playerState, controlKeyStates );
                break;
        }
    }

    /**
        Checks and handles the states of the control keys on the ground.
        @param player player whose keys are under handling
        @param controlKeyStates states of the control keys
    */
    private void handlePlayerKeysCommonPart( final Player player, final boolean[] controlKeyStates ) {
        final PlayerState playerState = player.getPlayerState();
        if ( controlKeyStates[ Player.KEY_INDEX_ROPE ] )
            if ( playerState.rope == null ) {
                final double angle = playerState.position.angleTo( playerState.mousePosition );
                playerState.rope = new Rope( playerState.position );
                playerState.rope.velocity.x =  ROPE_VELOCITY * (float) Math.cos( angle );
                playerState.rope.velocity.y = -ROPE_VELOCITY * (float) Math.sin( angle );
            }
            else
                if ( playerState.rope.hasEndPoint ) {
                    playerState.rope.climbingUp   = controlKeyStates[ Player.KEY_INDEX_UP   ];
                    playerState.rope.climbingDown = controlKeyStates[ Player.KEY_INDEX_DOWN ];
                }
                else
                    ;
        else
            if ( playerState.rope != null )
                playerState.rope = null;
        if ( controlKeyStates[ Player.KEY_INDEX_FIRE ] )
            playerState.weapon.fire( cycleCounter, this );
        else
            playerState.weapon.decreaseReloadingTime();
    }

    /**
        Checks and handles the states of the control keys on the ground.
        @param playerState state of player
        @param controlKeyStates states of the control keys
    */
    private void handlePlayerKeysOnGround( final PlayerState playerState, final boolean[] controlKeyStates ) {
        if ( controlKeyStates[ Player.KEY_INDEX_LEFT  ] ) {
            playerState.direction = GeneralConsts.WORM_DIRECTION_GROUND_LEFT;
            playerState.checkPhase( cycleCounter );
            if ( playerState.velocity.x > -WALKING_VELOCITY )
                playerState.velocity.x -= GROUND_ACCELERATION;
        }
        if ( controlKeyStates[ Player.KEY_INDEX_RIGHT ] ) {
            playerState.direction = GeneralConsts.WORM_DIRECTION_GROUND_RIGHT;
            playerState.checkPhase( cycleCounter );
            if ( playerState.velocity.x <  WALKING_VELOCITY )
                playerState.velocity.x += GROUND_ACCELERATION;
        }
        if ( !( playerState.rope != null && playerState.rope.hasEndPoint ) ) {
            if ( controlKeyStates[ Player.KEY_INDEX_UP ] ) {
                playerState.setAirDirection();
                playerState.setStandingPhase();
                playerState.velocity.y = -JUMPING_VELOCITY;
            } 
        }
    }

    /**
        Checks and handles the states of the control keys on the ground.
        @param playerState state of player
        @param controlKeyStates states of the control keys
    */
    private void handlePlayerKeysInAir( final PlayerState playerState, final boolean[] controlKeyStates ) {
        if ( controlKeyStates[ Player.KEY_INDEX_LEFT  ] ) {
            playerState.direction = GeneralConsts.WORM_DIRECTION_AIR_LEFT;
            playerState.setLastPhase();
            if ( playerState.velocity.x > -FLYING_VELOCITY )
                playerState.velocity.x -= AIR_ACCELERATION;
        }
        if ( controlKeyStates[ Player.KEY_INDEX_RIGHT ] ) {
            playerState.direction = GeneralConsts.WORM_DIRECTION_AIR_RIGHT;
            playerState.setLastPhase();
            if ( playerState.velocity.x <  FLYING_VELOCITY )
                playerState.velocity.x += AIR_ACCELERATION;
        }
    }

    /**
        Checks and handles the states of the control keys on the ground.
        @param playerState state of player
        @param controlKeyStates states of the control keys
    */
    private void handlePlayerKeysInWater( final PlayerState playerState, final boolean[] controlKeyStates ) {
        if ( controlKeyStates[ Player.KEY_INDEX_LEFT  ] ) {
            playerState.direction = GeneralConsts.WORM_DIRECTION_AIR_LEFT;
            playerState.checkPhase( cycleCounter );
            if ( playerState.velocity.x > -SWIMMING_VELOCITY )
                playerState.velocity.x -= WATER_ACCELERATION;
        }
        if ( controlKeyStates[ Player.KEY_INDEX_RIGHT ] ) {
            playerState.direction = GeneralConsts.WORM_DIRECTION_AIR_RIGHT;
            playerState.checkPhase( cycleCounter );
            if ( playerState.velocity.x <  SWIMMING_VELOCITY )
                playerState.velocity.x += WATER_ACCELERATION;
        }
        if ( !( playerState.rope != null && playerState.rope.hasEndPoint ) ) {
            if ( controlKeyStates[ Player.KEY_INDEX_UP ] ) {
                playerState.setAirDirection();
                if ( playerState.velocity.y > -SWIMMING_VELOCITY )
                    playerState.velocity.y -= WATER_ACCELERATION;
            }
            if ( controlKeyStates[ Player.KEY_INDEX_DOWN ] ) {
                playerState.setAirDirection();
                if ( playerState.velocity.y < SWIMMING_VELOCITY )
                    playerState.velocity.y += WATER_ACCELERATION;
            }
        }
    }

//--------------------------------------------------------------------------------------------------------------------
    /**
        Steps the bullets.
    */
    private void stepBullets() {
        int         hitsMapTowardAxes, hitsPlayerTowardAxes;
        int         hitPlayerIndexTowardXAxis, hitPlayerIndexTowardYAxis;
        final Point hitWallIndicesTowardXAxis = new Point(), hitWallIndicesTowardYAxis = new Point();
        Player      hitPlayer;
        Point       hitWallIndices;
        for ( int bulletIndex = bullets.size() - 1; bulletIndex >= 0; bulletIndex-- ) { // Downward direction is necessary because bullets can remove themselves
            final Bullet bullet = (Bullet) bullets.elementAt( bulletIndex );
            if ( bullet.reachedEndCycle( cycleCounter, this, bulletIndex ) )
                continue;
            calculateForce( bullet );
            hitsPlayerTowardAxes = hitsMapTowardAxes = MovingObjectConsts.STEPPING_AXIS_NONE;

            bullet.step( MovingObjectConsts.STEPPING_AXIS_X, MovingObjectConsts.STEPPING_DIRECTION_FORWARD );
            if ( map.hitsMap( bullet, hitWallIndicesTowardXAxis ) )
                hitsMapTowardAxes |= MovingObjectConsts.STEPPING_AXIS_X;
            if ( ( hitPlayerIndexTowardXAxis = hitsPlayer( bullet ) ) != INVALID_PLAYER_INDEX )
                hitsPlayerTowardAxes |= MovingObjectConsts.STEPPING_AXIS_X;
            if ( ( ( hitsMapTowardAxes | hitsPlayerTowardAxes ) & MovingObjectConsts.STEPPING_AXIS_X ) != MovingObjectConsts.STEPPING_AXIS_NONE )
                bullet.step( MovingObjectConsts.STEPPING_AXIS_X, MovingObjectConsts.STEPPING_DIRECTION_BACKWARD );

            bullet.step( MovingObjectConsts.STEPPING_AXIS_Y, MovingObjectConsts.STEPPING_DIRECTION_FORWARD );
            if ( map.hitsMap( bullet, hitWallIndicesTowardYAxis ) )
                hitsMapTowardAxes |= MovingObjectConsts.STEPPING_AXIS_Y;
            if ( ( hitPlayerIndexTowardYAxis = hitsPlayer( bullet ) ) != INVALID_PLAYER_INDEX )
                hitsPlayerTowardAxes |= MovingObjectConsts.STEPPING_AXIS_Y;
            if ( ( ( hitsMapTowardAxes | hitsPlayerTowardAxes ) & MovingObjectConsts.STEPPING_AXIS_Y ) != MovingObjectConsts.STEPPING_AXIS_NONE )
                bullet.step( MovingObjectConsts.STEPPING_AXIS_Y, MovingObjectConsts.STEPPING_DIRECTION_BACKWARD );

            if ( map.getWall( ( (int) bullet.position.x ) >> GeneralConsts.WALL_WIDTH_SHIFT, ( (int) bullet.position.y ) >> GeneralConsts.WALL_HEIGHT_SHIFT ) == Map.WALL_EMPTY )
                bullet.positionType = MovingObjectConsts.POSITION_TYPE_IN_AIR;
            else
                bullet.positionType = MovingObjectConsts.POSITION_TYPE_IN_WATER;

            if ( ( hitsMapTowardAxes | hitsPlayerTowardAxes ) != MovingObjectConsts.STEPPING_AXIS_NONE ) {
                hitPlayer      = hitPlayerIndexTowardXAxis == INVALID_PLAYER_INDEX ? ( hitPlayerIndexTowardYAxis == INVALID_PLAYER_INDEX ? null : players[ hitPlayerIndexTowardYAxis ] ) : players[ hitPlayerIndexTowardXAxis ];
                hitWallIndices = ( hitsMapTowardAxes & MovingObjectConsts.STEPPING_AXIS_X ) != MovingObjectConsts.STEPPING_AXIS_NONE ? hitWallIndicesTowardXAxis : hitWallIndicesTowardYAxis;
                bullet.collides( this, hitsMapTowardAxes, hitWallIndices, hitsPlayerTowardAxes, hitPlayer, bulletIndex );
            }
        }
    }

//--------------------------------------------------------------------------------------------------------------------
    /**
        Steps the decorations.
    */
    private void stepDecorations() {
        for ( int decorationIndex = decorations.size() - 1; decorationIndex >= 0; decorationIndex-- ) { // Downward direction is necessary because decorations can remove themselves
            final Decoration decoration = (Decoration) decorations.elementAt( decorationIndex );
            if ( decoration.reachedEndCycle( cycleCounter, decorations, decorationIndex ) )
                continue;
            calculateForce( decoration );
            decoration.step( MovingObjectConsts.STEPPING_AXIS_BOTH, MovingObjectConsts.STEPPING_DIRECTION_FORWARD );
            if ( map.hitsMapNonExtensive( decoration ) )
                decorations.removeElementAt( decorationIndex );
            else
                if ( map.getWall( ( (int) decoration.position.x ) >> GeneralConsts.WALL_WIDTH_SHIFT, ( (int) decoration.position.y ) >> GeneralConsts.WALL_HEIGHT_SHIFT ) == Map.WALL_EMPTY )
                    decoration.positionType = MovingObjectConsts.POSITION_TYPE_IN_AIR;
                else
                    decoration.positionType = MovingObjectConsts.POSITION_TYPE_IN_WATER;
        }
    }

//--------------------------------------------------------------------------------------------------------------------
    /**
        Steps the ropes.
    */
    private void stepRopes() {
        for ( int playerIndex = 0; playerIndex < players.length; playerIndex++ ) {
            if ( players[ playerIndex ] != null ) {
                final PlayerState playerState = players[ playerIndex ].getPlayerState();
                final Rope        rope        = playerState.rope;
                if ( rope != null )
                    if ( rope.hasEndPoint ) {
                        boolean ropeHasToBeRemoved = false;
                        if ( rope.endPointMovingObject == null ) {
                            if ( !map.hitsMap( rope, null ) )
                                ropeHasToBeRemoved = true;             // Wall has been fired out...
                        }
                        else
                            if ( rope.endPointPlayerIndex != INVALID_PLAYER_INDEX && players[ rope.endPointPlayerIndex ] == null )
                                ropeHasToBeRemoved = true;             // endPointPlayer has been disconnected
                        if ( ropeHasToBeRemoved )
                            playerState.rope = null;
                        else
                            doRopeEffect( rope, playerState );
                    }
                    else {
                        calculateForce( rope );
                        rope.step(  MovingObjectConsts.STEPPING_AXIS_BOTH, MovingObjectConsts.STEPPING_DIRECTION_FORWARD );
                        if ( map.hitsMap( rope, null ) ) {
                            rope.hasEndPoint = true;
                        }
                        else {
                            final int hitPlayerIndex = hitsPlayer( rope, playerIndex );
                            if ( hitPlayerIndex != INVALID_PLAYER_INDEX ) {
                                rope.endPointPlayerIndex  = hitPlayerIndex;
                                rope.endPointMovingObject = players[ hitPlayerIndex ].getPlayerState();
                                rope.hasEndPoint          = true;
                            }
                        }
                    }
            }
        }
    }

    /**
        Does things to have the feeling that we "flies on a rope".
        @param rope the rope
        @param playerState owner of the rope
    */
    private void doRopeEffect( final Rope rope, final PlayerState playerState ) {
        final double ropeAngle     = rope.startPosition.angleTo( rope.getEndPosition() );
        final float  ropeAngleSin  = (float) Math.sin( ropeAngle );
        final float  ropeAngleCos  = (float) Math.cos( ropeAngle );
        
        final double velocityAngle = playerState.velocity.angle();
        double       projectAngle  = MathUtils.normalizeAngle( velocityAngle - ropeAngle );
        projectAngle = ( projectAngle < Math.PI ? MathUtils.HALF_PI : MathUtils.ONE_AND_A_HALF_PI ) - projectAngle;
        final float   newVelocity  = playerState.velocity.length() * (float) Math.cos( projectAngle ); // New velocity of the player at right angles to the rope (perpendicular)
        playerState.velocity.x =  newVelocity * (float) Math.cos( velocityAngle + projectAngle );
        playerState.velocity.y = -newVelocity * (float) Math.sin( velocityAngle + projectAngle );
        
        float ropeForceX = 0.0f;
        float ropeForceY = 0.0f;
        if ( rope.endPointMovingObject == null ) { // We roped to a wall, it can effect us as lagrge force as we need to move in a circle
            final double forceAngle    = playerState.force.angle();
                         projectAngle  = MathUtils.normalizeAngle( forceAngle - ropeAngle );
            projectAngle = ( projectAngle < Math.PI ? MathUtils.HALF_PI : MathUtils.ONE_AND_A_HALF_PI ) - projectAngle;
            final float   newForce     = playerState.force.length() * (float) Math.cos( projectAngle ); // New force of the player at right angles to the rope (perpendicular)
            ropeForceX =  newForce * (float) Math.cos( forceAngle + projectAngle ) - playerState.force.x;
            ropeForceY = -newForce * (float) Math.sin( forceAngle + projectAngle ) - playerState.force.y;
        }

        if ( rope.climbingUp ) {
            playerState.velocity.x +=  CLIMBING_VELOCITY * ropeAngleCos;
            playerState.velocity.y += -CLIMBING_VELOCITY * ropeAngleSin;
        }
        if ( rope.climbingDown ) {
            playerState.velocity.x -=  CLIMBING_VELOCITY * ropeAngleCos;
            playerState.velocity.y -= -CLIMBING_VELOCITY * ropeAngleSin;
        }

        final float  spinForce     =  playerState.mass * ( playerState.velocity.x * playerState.velocity.x + playerState.velocity.y * playerState.velocity.y ) / playerState.position.distanceTo( rope.getEndPosition() );  // F = m * v^2 / r
        ropeForceX +=  spinForce * ropeAngleCos;
        ropeForceY += -spinForce * ropeAngleSin;
        playerState.force.x += ropeForceX;
        playerState.force.y += ropeForceY;

        if ( rope.endPointMovingObject != null ) {
            rope.endPointMovingObject.force.x -= ropeForceX;
            rope.endPointMovingObject.force.y -= ropeForceY;
        }
    }

//--------------------------------------------------------------------------------------------------------------------
    /**
        Steps the players.
    */
    private void stepPlayers() {
        for ( int playerIndex = 0; playerIndex < players.length; playerIndex++ ) {
            final Player player = players[ playerIndex ];
            if ( player != null ) {
                final PlayerState playerState = player.getPlayerState();
                checkOxygenBottle( player );
                playerState.step( MovingObjectConsts.STEPPING_AXIS_X, MovingObjectConsts.STEPPING_DIRECTION_FORWARD );
                while ( hitsPlayer( playerState, playerIndex ) != INVALID_PLAYER_INDEX || map.hitsMap( playerState, null ) ) {
                    playerState.step( MovingObjectConsts.STEPPING_AXIS_X, MovingObjectConsts.STEPPING_DIRECTION_BACKWARD );
                    playerState.velocity.x /= 2.0f;      // We step less and less...
                    if ( playerState.velocity.x < 0.1f && playerState.velocity.x > -0.1f ) // We have to get out from cycle for sure somehow...
                        playerState.velocity.x = 0.0f;
                    playerState.step( MovingObjectConsts.STEPPING_AXIS_X, MovingObjectConsts.STEPPING_DIRECTION_FORWARD );
                }

                if ( map.getWall( ( (int) playerState.position.x ) >> GeneralConsts.WALL_WIDTH_SHIFT, ( (int) playerState.position.y ) >> GeneralConsts.WALL_HEIGHT_SHIFT ) == Map.WALL_EMPTY )
                    playerState.positionType = MovingObjectConsts.POSITION_TYPE_IN_AIR;
                else
                    playerState.positionType = MovingObjectConsts.POSITION_TYPE_IN_WATER;

                playerState.step( MovingObjectConsts.STEPPING_AXIS_Y, MovingObjectConsts.STEPPING_DIRECTION_FORWARD );
                if ( hitsPlayer( playerState, playerIndex ) != INVALID_PLAYER_INDEX || map.hitsMap( playerState, null ) ) {
                    if ( playerState.velocity.y > 0.0f ) // If the worm falling down, not if he jumps up to the ceiling
                        playerState.positionType = MovingObjectConsts.POSITION_TYPE_ON_GROUND;
                    do {
                        playerState.step( MovingObjectConsts.STEPPING_AXIS_Y, MovingObjectConsts.STEPPING_DIRECTION_BACKWARD );
                        playerState.velocity.y /= 2.0f;  // We step less and less...
                        if ( playerState.velocity.y < 0.1f && playerState.velocity.y > -0.1f ) // We have to get out from cycle for sure somehow...
                            playerState.velocity.y = 0.0f;
                        playerState.step( MovingObjectConsts.STEPPING_AXIS_Y, MovingObjectConsts.STEPPING_DIRECTION_FORWARD );
                    } while ( hitsPlayer( playerState, playerIndex ) != INVALID_PLAYER_INDEX || map.hitsMap( playerState, null ) );
                }
            }
        }
    }
    
    /**
        Check the oxygen bottle of a player.
        @param player player whose oxygen bottle needed to be checked
    */
    private void checkOxygenBottle( final Player player ) {
        final PlayerState playerState = player.getPlayerState();
        final byte wall = map.getWall( (int) playerState.position.x >> GeneralConsts.WALL_WIDTH_SHIFT, (int) playerState.position.y >> GeneralConsts.WALL_HEIGHT_SHIFT );
        if ( playerState.positionType == MovingObjectConsts.POSITION_TYPE_IN_WATER || wall == Map.WALL_WATER || wall == Map.WALL_WATER_SURFACE )
            if ( playerState.oxygenBottleLevel == 0 )
                damagePlayer( player, 1, null );
            else
                playerState.oxygenBottleLevel--;
        else
            if ( playerState.oxygenBottleLevel < GeneralConsts.MAX_OXYGEN_BOTTLE_LEVEL )
                if ( ( playerState.oxygenBottleLevel += 10 ) > GeneralConsts.MAX_OXYGEN_BOTTLE_LEVEL )
                    playerState.oxygenBottleLevel = GeneralConsts.MAX_OXYGEN_BOTTLE_LEVEL;
    }

//--------------------------------------------------------------------------------------------------------------------
    /**
        Steps (calculates next states of) the explosions.
    */
    private void stepExplosions() {
        for ( int explosionIndex = explosions.size() - 1; explosionIndex >= 0; explosionIndex-- ) {
            final Explosion explosion = (Explosion) explosions.elementAt( explosionIndex );
            if ( explosion.decreaseAndGetRange() <= 0 ) {
                explosions.removeElementAt( explosionIndex );
                continue;
            }
            for ( int playerIndex = players.length - 1; playerIndex >= 0; playerIndex-- )
                if ( players[ playerIndex ] != null )
                    if ( explosion.reachesMovingObject( players[ playerIndex ].getPlayerState() ) )
                        explosion.damagePlayer( players[ playerIndex ], this );
            explosion.damageWalls( map, this );
            for ( int bulletIndex = bullets.size() - 1; bulletIndex >= 0; bulletIndex-- ) {
                final Bullet bullet = (Bullet) bullets.elementAt( bulletIndex );
                if ( bullet.isExplodable() && explosion.reachesMovingObject( bullet ) )
                    bullet.explode( bullets, bulletIndex, explosions );
            }
        }
    }

//--------------------------------------------------------------------------------------------------------------------
    /**
        Steps (calculates next states of) the polyline shots.
    */
    private void stepPolylineShots() {
        for ( int polylineShotIndex = polylineShots.size() - 1; polylineShotIndex >= 0; polylineShotIndex-- ) {
            final PolylineShot polylineShot = (PolylineShot) polylineShots.elementAt( polylineShotIndex );
            if ( !polylineShot.reachedEndCycle( cycleCounter, polylineShots, polylineShotIndex ) ) {
                for ( int playerIndex = players.length - 1; playerIndex >= 0; playerIndex-- )
                    if ( players[ playerIndex ] != null && polylineShot.hitsPlayer( players[ playerIndex ] ) )
                        polylineShot.damagePlayer( players[ playerIndex ], this );
                polylineShot.damageWalls( map, this );
            }
        }
    }
    
//--------------------------------------------------------------------------------------------------------------------
//  Utility functions for handling gamecore...
    /**
        Tests whether a moving object hits a player.
        @param extensiveMovingObject object needed to be tested
        @return the index of player which is hit by object, or INVALID_PALYER_INDEX if it hits no players
    */
    private int hitsPlayer( final ExtensiveMovingObject extensiveMovingObject ) {
        for ( int playerIndex = players.length - 1; playerIndex >= 0; playerIndex-- )   // Downward, because this will be called a quite much times
            if ( players[ playerIndex ] != null && extensiveMovingObject.hitsExtensiveObject( players[ playerIndex ].getPlayerState() ) )
                return playerIndex;
        return INVALID_PLAYER_INDEX;
    }

    /**
        Tests whether an extensive moving object hits one of the players except one.
        @param extensiveMovingObject object needed to be tested
        @param exceptPlayerIndex here we can give 1 player by its index that we don't want to examine if hits (if none, use INVALID_PLAYER_INDEX constant)
        @return the index of player which is hit by object, or INVALID_PALYER_INDEX if it hits no players
    */
    private int hitsPlayer( final ExtensiveMovingObject extensiveMovingObject, final int exceptPlayerIndex ) {
        for ( int playerIndex = players.length - 1; playerIndex >= 0; playerIndex-- )   // Downward, because this will be called a quite much times
            if ( playerIndex != exceptPlayerIndex && players[ playerIndex ] != null && extensiveMovingObject.hitsExtensiveObject( players[ playerIndex ].getPlayerState() ) )
                return playerIndex;
        return INVALID_PLAYER_INDEX;
    }

//--------------------------------------------------------------------------------------------------------------------
    /**
        Calculates the resultant force affects the moving object.
        @param movingObject object whose force to be calculated
    */
    private void calculateForce( final MovingObject movingObject ) {
        movingObject.force.y = movingObject.force.x = 0.0f;
        doGravitationFieldEffect( movingObject );
        doShapeResistanceEffect( movingObject );
        if ( movingObject.positionType == MovingObjectConsts.POSITION_TYPE_IN_WATER )
            doBuoyancyEffect( movingObject );
        if ( movingObject.positionType == MovingObjectConsts.POSITION_TYPE_ON_GROUND )
            doFrictionEffect( movingObject );
    }

    /**
        Does the gravitation field effect to a moving object.
        @param movingObject object to do effect on
    */
    private void doGravitationFieldEffect( final MovingObject movingObject ) {
        movingObject.force.y += movingObject.mass * ( movingObject.remainingGravitationRate * GRAVITATION );
    }

    /**
        Does the air resistance effect to a moving object.
        @param movingObject object to do effect on
    */
    private void doShapeResistanceEffect( final MovingObject movingObject ) {
        float velocityX = movingObject.velocity.x; 
        float velocityY = movingObject.velocity.y;
        if ( movingObject.positionType == MovingObjectConsts.POSITION_TYPE_IN_AIR ) {
            velocityX -= windVelocity.x;
            velocityY -= windVelocity.y;
        }
        final float UNITmultipleRate = ( movingObject.positionType == MovingObjectConsts.POSITION_TYPE_IN_WATER ? WATER_SHAPE_RESISTANCE_FORCE_UNIT : AIR_SHAPE_RESISTANCE_FORCE_UNIT ) * movingObject.shapeResistanceRate;
        movingObject.force.x -= ( velocityX > 0.0f ?  1.0f : -1.0f ) * Math.min( UNITmultipleRate * velocityX * velocityX, MAX_SHAPE_RESISTANCE_FORCE );
        movingObject.force.y -= ( velocityY > 0.0f ?  1.0f : -1.0f ) * Math.min( UNITmultipleRate * velocityY * velocityY, MAX_SHAPE_RESISTANCE_FORCE );
    }
    
    /**
        Does the water buoyancy effect to a moving object.
        @param movingObject object to do effect on
    */
    private void doBuoyancyEffect( final MovingObject movingObject ) {
        movingObject.force.y -= WATER_DENSITY * GRAVITATION * movingObject.capacity;
    }
    
    /**
        Does the friction effect between a moving object and the ground.
        @param movingObject object to do effect on
    */
    private void doFrictionEffect( final MovingObject movingObject ) {
        movingObject.force.x -= ( movingObject.velocity.x > 0.0f ? 1.0f : -1.0f ) * GROUND_FRICTION_RATE * movingObject.force.y;
    }

    
//--------------------------------------------------------------------------------------------------------------------
    /**
        Returns the bullets.
        @return the bullets
    */
    public Vector getBullets() {
        return bullets;
    }
    
    /**
        Returns the decorations.
        @return the decorations
    */
    public Vector getDecorations() {
        return decorations;
    }

    /**
        Returns the explosions.
        @return the explosions
    */
    public Vector getExplosions() {
        return explosions;
    }

    /**
        Returns the polyline shots.
        @return the polyline shots
    */
    public Vector getPolylineShots() {
        return polylineShots;
    }

    /**
        Damages a player.
        @param player player to be damaged
        @param damage amout of damage
        @param damagerPlayer player who makes the damage
    */
    public void damagePlayer( final Player player, final int damage, final Player damagerPlayer ) {
        final PlayerState playerState = player.getPlayerState();
        if ( playerState.hitPoint > 0 ) {
            final FloatVector position = new FloatVector();
            final float positionX = playerState.position.x;
            final float positionY = playerState.position.y;
            for ( int bloodCounter = ( ( TOTAL_AMOUNT_OF_BLOOD_OF_A_PLAYER * serverOptions.amountOfBlood * damage ) / GeneralConsts.MAX_PLAYER_HIT_POINT ) / 100; bloodCounter > 0; bloodCounter-- ) {
                position.x = positionX + (float) Math.random() * GeneralConsts.WORM_WIDTH;
                position.y = positionY + (float) Math.random() * GeneralConsts.WORM_HEIGHT;
                decorations.add( new PointDecoration( cycleCounter, position, Math.random() * MathUtils.TWO_PI, false, Color.red ) );
            }
            if ( ( playerState.hitPoint -= damage ) <= 0 ) {
                playerState.deathCounter++;
                if ( damagerPlayer != null )
                    damagerPlayer.getPlayerState().killCounter += player == damagerPlayer ? 0 : ( serverOptions.gameType == ServerOptions.GAME_TYPE_TEAM_MELEE && damagerPlayer.getPlayerState().getGroupIdentifier() == playerState.getGroupIdentifier() ? -1 : 1 );
                player.reborn();
                if ( damagerPlayer == null || damagerPlayer == player )
                    server.broadcastServerMessage( playerState.name + " committed suicide." );
                else
                    server.broadcastServerMessage( damagerPlayer.getPlayerState().name + " killed " + playerState.name + '.' );
            }
        }
    }

    /**
        Damages a wall.
        @param indexX x map index of wall to be damaged
        @param indexY y map index of wall to be damaged
        @param damage amout of damage
    */
    public void damageWall( final int indexX, final int indexY, final int damage ) {
        if ( map.getWall( indexX, indexY ) == map.WALL_BRICK )
            if ( ( wallHitPoints[ indexY ][ indexX ] -= damage ) <= 0 ) {
                final FloatVector position = new FloatVector();
                final float positionX = indexX << GeneralConsts.WALL_WIDTH_SHIFT;
                final float positionY = indexY << GeneralConsts.WALL_HEIGHT_SHIFT;
                for ( int wallRubbleCounter = AMOUNT_OF_WALL_RUBBLES_OF_A_WALL * serverOptions.amountOfWallRubbles / 100; wallRubbleCounter > 0; wallRubbleCounter-- ) {
                    position.x = positionX + (float) Math.random() * GeneralConsts.WALL_WIDTH;
                    position.y = positionY + (float) Math.random() * GeneralConsts.WALL_HEIGHT;
                    decorations.add( new PointDecoration( cycleCounter, position, Math.random() * MathUtils.TWO_PI, true ) );
                }
                map.clearWall( indexX, indexY );
            }
    }

}
