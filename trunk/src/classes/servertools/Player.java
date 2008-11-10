
package classes.servertools;

import java.rmi.*;
import java.rmi.server.*;
import classes.*;
import classes.servertools.gamecore.*;
import classes.clienttools.*;
import java.awt.event.*;
import classes.utilities.*;
import java.util.Vector;             // Only the Vector because reference to Map would be ambiguous.
import classes.servertools.gamecore.weapons.*;


/**
    Remote player which will be controlled by a remote client.
    @author Belicza Andras
*/
public class Player extends UnicastRemoteObject implements RemotePlayer {

    /** Constant value for the mass of the worm (kg). */
    private static final float WORM_MASS                  = 1.0f;
    /** Constant value for the rate of the air resistance of the worm. */
    private static final float WORM_SHAPE_RESISTANCE_RATE = 0.4f;
    /** Constant value for the capacity of the worm (m^3). */
    private static final float WORM_CAPACITY              = 0.00093f;
    
    /** Maximum value of remaining pauses count. */
    private static final int MAX_REMAINING_PAUSES_COUNT = 3;

    /** Constant for key up index. */
    public  static final int KEY_INDEX_UP       = 0;
    /** Constant for down key index. */
    public  static final int KEY_INDEX_DOWN     = 1;
    /** Constant for left key index. */
    public  static final int KEY_INDEX_LEFT     = 2;
    /** Constant for right key index. */
    public  static final int KEY_INDEX_RIGHT    = 3;
    /** Constant for fire key index. */
    public  static final int KEY_INDEX_FIRE     = 4;
    /** Constant for rope key index. */
    public  static final int KEY_INDEX_ROPE     = 5;
    /** Count of control keys. */
    private static final int CONTROL_KEYS_COUNT = 6;
    
    /** Angles from the center of a worm to the for corner of its image. */
    private static final double[] WORM_IMAGE_CORNER_ANGLES     = new double[ 4 ];
    /** Right up corner of worm image. */
    private static final int      WORM_IMAGE_RIGHT_UP_CORNER   = 0;
    /** Left up corner of worm image. */
    private static final int      WORM_IMAGE_LEFT_UP_CORNER    = 1;
    /** Left down corner of worm image. */
    private static final int      WORM_IMAGE_LEFT_DOWN_CORNER  = 2;
    /** Right down corner of worm image. */
    private static final int      WORM_IMAGE_RIGHT_DOWN_CORNER = 3;

    /** Maximum number of failures of position generation trial. After this we clear a stone wall! */
    private static final int      MAX_POSITION_GENERATION_TRIAL_FAILURE_COUNT = 2000;

    /** Tells whether the client or server closed the player. */
    private volatile boolean             closed = false;
    /** Time of beeing in idle state. */
    private volatile long                idleTime;
    /** Sate of the player. */
    private       PlayerState            playerState;
    /** Own index in players array. */
    private final int                    ownIndex;
    /** Reference to informations for drawing. */
    private final InformationsForDrawing informationsForDrawing;
    /** Reference to the players (needed for only generation random position). */
    private final Player[]               players;
    /** Reference to the map (needed for only generation random position).*/
    private final Map                    map;
    /** States of the control keys. */
    private final boolean[]              controlKeyStates     = new boolean[ CONTROL_KEYS_COUNT ];
    /** Weapons of the player. */
    private final Weapon[]               weapons;
    /** Vector of the messages between the players (and the server). */
    private final Vector                 messages             = new Vector();
    /** Number of times this player can pause the game. */
    private int                          remainingPausesCount = MAX_REMAINING_PAUSES_COUNT;
    /** Tells whether this player wants to pause (if it wasn't paused) or resume (if it was paused) the game. */
    private boolean                      requestedToPauseOrResume;
    /** Tells whether the player was kicked by the server (operator). */
    private boolean                      kicked;
    /** If player was kicked, this may hold a reason of kicking. */
    private String                       kickMessage;

    /**
        The static initializer. We fill up the worm image corner angles array.
    */
    static {
        final ExtensiveMovingObject testWorm = new ExtensiveMovingObject( 0.0f, 0.0f, 0.0f, 0.0f, GeneralConsts.WORM_WIDTH, GeneralConsts.WORM_HEIGHT );
        testWorm.position.y = testWorm.position.x = 0.0f;
        WORM_IMAGE_CORNER_ANGLES[ WORM_IMAGE_RIGHT_UP_CORNER   ] = MathUtils.normalizeAngle( testWorm.position.angleTo( new FloatVector(  testWorm.dimensionToRight, -testWorm.dimensionToUp   ) ) );
        WORM_IMAGE_CORNER_ANGLES[ WORM_IMAGE_LEFT_UP_CORNER    ] = MathUtils.normalizeAngle( testWorm.position.angleTo( new FloatVector( -testWorm.dimensionToLeft , -testWorm.dimensionToUp   ) ) );
        WORM_IMAGE_CORNER_ANGLES[ WORM_IMAGE_LEFT_DOWN_CORNER  ] = MathUtils.normalizeAngle( testWorm.position.angleTo( new FloatVector( -testWorm.dimensionToLeft ,  testWorm.dimensionToDown ) ) );
        WORM_IMAGE_CORNER_ANGLES[ WORM_IMAGE_RIGHT_DOWN_CORNER ] = MathUtils.normalizeAngle( testWorm.position.angleTo( new FloatVector(  testWorm.dimensionToRight,  testWorm.dimensionToDown ) ) );
    }

    /**
        Creates a new Player.
        @param ownIndex own index in players array
        @param informationsForDrawing reference to the informations needed for drawing
        @param players reference to the players
        @param map reference to the map
        @throws RemoteException if error occurs during remote method invocation
    */
    public Player( final int ownIndex, final InformationsForDrawing informationsForDrawing, final Player[] players, final Map map ) throws RemoteException {
        super( GeneralConsts.REGISTRY_PORT );
        this.ownIndex               = ownIndex;
        this.informationsForDrawing = informationsForDrawing;
        this.players                = players;
        this.map                    = map;
        weapons                     = new Weapon[] { new Rifle( this ), new Shotgun( this ), new GrenadeLauncher( this, new SplinterGrenadeFactory() ), new GrenadeLauncher( this, new ExplosiveGrenadeFactory() ), new RocketLauncher( this ), new LaserCannon( this, this.map ), new Thunderbolt( this ) };
        clearRequestedToPauseOrResume();
        reborn();
    }

    /**
        After player has been killed, this method brings him back to life.
    */
    public void reborn() {
        if ( playerState != null )    // We have to clear ropes connected to this player first (but not at the first time)!
            for ( int playerIndex = 0; playerIndex< players.length; playerIndex++ )
                if ( playerIndex != ownIndex && players[ playerIndex ] != null ) {
                    final PlayerState playerState = players[ playerIndex ].playerState;
                    if ( playerState.rope != null && playerState.rope.hasEndPoint && playerState.rope.endPointMovingObject == this.playerState )
                        playerState.rope = null;
                }
        // Then we regenerate the state of player.
        final PlayerState newPlayerState = new PlayerState( WORM_MASS, WORM_SHAPE_RESISTANCE_RATE, WORM_CAPACITY );
        if ( playerState!= null ) {
            newPlayerState.wormColorIndex = playerState.wormColorIndex;
            newPlayerState.name           = playerState.name;
            newPlayerState.killCounter    = playerState.killCounter;
            newPlayerState.deathCounter   = playerState.deathCounter;
        }
        playerState = newPlayerState;
        for ( int weaponIndex = 0; weaponIndex < weapons.length; weaponIndex++ )
            weapons[ weaponIndex ].reloadNow();
        try {
            selectWeapon( 0 );
        }
        catch ( RemoteException re ) {
            Logging.logError( re );
        }
        generateRandomPosition();
        informationsForDrawing.playerStates[ ownIndex ] = playerState;
    }

    /**
        Generates random position to the player where no other player stays, and clears the map around the position.
    */
    private void generateRandomPosition() {
        int     indexX, indexY;
        boolean collidesWithPlayers;
        playerState.position.x = Integer.MIN_VALUE;    // While we trying...
        playerState.position.y = Integer.MIN_VALUE;
        final ExtensiveMovingObject testObject = new ExtensiveMovingObject( 0.0f, 0.0f, 0.0f, 0.0f, GeneralConsts.WORM_WIDTH, GeneralConsts.WORM_HEIGHT );
        int positionGenerationTrialFailureCounter = 0;
        do {
            do {
                indexX = 1 + (int) ( Math.random() * ( map.getWidth () - 1 ) );
                indexY = 1 + (int) ( Math.random() * ( map.getHeight() - 1 ) );
                if ( positionGenerationTrialFailureCounter++ == MAX_POSITION_GENERATION_TRIAL_FAILURE_COUNT )
                    if ( map.getWall( indexX, indexY ) == Map.WALL_STONE )
                        map.setWall( indexX, indexY, Map.WALL_BRICK );   // Not to empty: it can be under water!!
            } while( map.getWall( indexX, indexY ) == Map.WALL_STONE );  // Stone can't be cleared: it can be part of swimming pool boundaries. (But if there isn't other places where to start, we clear it!)
            testObject.position.x = ( indexX << GeneralConsts.WALL_WIDTH_SHIFT  ) + GeneralConsts.WALL_WIDTH  / 2;
            testObject.position.y = ( indexY << GeneralConsts.WALL_HEIGHT_SHIFT ) + GeneralConsts.WALL_HEIGHT / 2;
            collidesWithPlayers = false;
            for ( int playerIndex = 0; playerIndex < players.length && !collidesWithPlayers; playerIndex++ )
                if ( playerIndex != ownIndex && players[ playerIndex ] != null && testObject.hitsExtensiveObject( players[ playerIndex ].playerState ) )
                    collidesWithPlayers = true;
            try {
                Thread.sleep( 0, 100 );
            }
            catch( InterruptedException ie ) {
            }
        } while ( collidesWithPlayers );
        playerState.position.x = testObject.position.x;
        playerState.position.y = testObject.position.y;
        map.clearMapForEnteringPlayer( indexX, indexY );
    }

    /**
        Corrigates the position of an extensive moving object in the given direction, so after it won't hit this player.
        @param extensiveMovingObject extensive moving object whose position to be corrigated
        @param angle direction in where to move the extensive moving object
    */
    public void moveExtensiveMovingObjectOutside( final ExtensiveMovingObject extensiveMovingObject, double angle ) {
        final float angleSin = (float) Math.sin( angle );
        final float angleCos = (float) Math.cos( angle );
        angle = MathUtils.normalizeAngle( angle );
        if ( MathUtils.between( angle, WORM_IMAGE_CORNER_ANGLES[ WORM_IMAGE_RIGHT_UP_CORNER  ], WORM_IMAGE_CORNER_ANGLES[ WORM_IMAGE_LEFT_UP_CORNER    ] ) || 
             MathUtils.between( angle, WORM_IMAGE_CORNER_ANGLES[ WORM_IMAGE_LEFT_DOWN_CORNER ], WORM_IMAGE_CORNER_ANGLES[ WORM_IMAGE_RIGHT_DOWN_CORNER ] )    ) {
            float deltaY = angleSin > 0.0f ? playerState.dimensionToUp + extensiveMovingObject.dimensionToDown + 1 : -playerState.dimensionToDown - extensiveMovingObject.dimensionToUp - 1;
            extensiveMovingObject.position.x +=  deltaY * angleCos / angleSin;
            extensiveMovingObject.position.y += -deltaY;
        }
        else {
            final float deltaX = angleCos > 0.0f ? playerState.dimensionToRight + extensiveMovingObject.dimensionToLeft : -playerState.dimensionToLeft - extensiveMovingObject.dimensionToRight;
            extensiveMovingObject.position.x +=  deltaX;
            extensiveMovingObject.position.y += -deltaX * angleSin / angleCos;
        }
    }

    /**
        Closes the player (implementing RemotePlayer interface).
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    public void close() throws RemoteException {
        closed = true;
        informationsForDrawing.playerStates[ ownIndex ] = null;
    }
    
    /**
        Returns whether client closed the player.
        @return true if the player is closed; false otherwise
    */
    public boolean closed() {
        return closed;
    }
    
    /**
        Returns whether client closed the player (can be called by remote)(implementing RemotePlayer interface).
        @return true if the player is closed; false otherwise
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    public boolean isClosed() throws RemoteException {
        return closed;
    }
    
    /**
        Returns the idle time.
        @return the idle time
    */
    public long getIdleTime() {
        return idleTime;
    }
    
    /**
        Increases the idle time.
        @param time the quantity time of increasing
    */
    public void increaseIdleTime( final long time ) {
        idleTime += time;
    }
    
    /**
        Clears the players idle time.
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    public void clearIdleTime() throws RemoteException {
        if ( closed )
            throw new RemoteException();
        else
            idleTime = 0l;
    }
    
    /**
        Returns own index in players array (implementing RemotePlayer interface).
        @return own index in players array
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    public int getOwnIndex() throws RemoteException {
        return ownIndex;
    }
    
    /**
        Sets the color of player's worm (implementing RemotePlayer interface).
        @param wormColorIndex color of the worm
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    public void setWormColorIndex( final int wormColorIndex ) throws RemoteException {
        playerState.wormColorIndex = wormColorIndex;
    }

    /**
        Sets the player's name (implementing RemotePlayer interface).
        @param name the name of the player
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    public void setName( final String name ) throws RemoteException {
        playerState.name = name;
    }
    
    /**
        Returns the player state.
        @return the player state
    */
    public PlayerState getPlayerState() {
        return playerState;
    }

    /**
        Returns the sates of the control keys.
        @return states of the control keys
    */
    public boolean[] getControlKeyStates() {
        return controlKeyStates;
    }
    
    /**
        Sets the state of a control key (implementing RemotePlayer interface).
        @param keyIndex the index of the key
        @param state state of the key, true if pressed, false if released
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    public void setControlKeyState( final int keyIndex, final boolean state ) throws RemoteException {
        controlKeyStates[ keyIndex ] = state;
    }

    /**
        Sets the position of the mouse (implementing RemotePlayer interface).
        @param x x position of the mouse
        @param y y position of the mouse
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    public void setMousePosition( final float x, final float y ) throws RemoteException {
        playerState.mousePosition.x = x;
        playerState.mousePosition.y = y;
    }
    
    /**
        Selects a weapon (implementing RemotePlayer interface).
        @param weaponIndex index of the weapon to be selected
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    public void selectWeapon( final int weaponIndex ) throws RemoteException {
        if ( weaponIndex < weapons.length ) {
            playerState.weaponIndex = weaponIndex;
            playerState.weapon      = weapons[ playerState.weaponIndex ];
        }
    }

    /**
        Rotates the selected weapon with the specified number (implementing RemotePlayer interface).
        @param number number to rotate selected weapon by
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    public void rotateWeaponBy( final int number ) throws RemoteException {
        final int newWeaponIndex = ( playerState.weaponIndex + number ) % weapons.length;
        selectWeapon( newWeaponIndex >= 0 ? newWeaponIndex : newWeaponIndex + weapons.length );
    }

    /**
        Recieves a message. This method will be called, when new message arrives from one player or from the server.
        @param message the message
    */
    public void recieveMessage( final String message ) {
        messages.add( message );
    }

    /**
        Checks if has at least one new message (implementing RemotePlayer interface).
        @return true if has new message(s); false otherwise
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    public boolean hasNewMessage() throws RemoteException {
        return !messages.isEmpty();
    }

    /**
        Returns the messages (implementing RemoteException interface).
        @return the messages
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    public Vector getMessages() throws RemoteException {
        return messages;
    }

    /**
        Clears the vector of messages (implementing RemoteException interface).
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    public void clearMessages() throws RemoteException {
        messages.removeAllElements();
    }

    /**
        Requests a pause if game wasn't paused, or resume if game was paused (implementing RemotePlayer interface).
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote method invocation
    */
    public void requestForPauseOrResume() throws RemoteException {
        requestedToPauseOrResume = true;
    }

    /**
        Clears the value of requested to pause or resume.
    */
    private void clearRequestedToPauseOrResume() {
        requestedToPauseOrResume = false;
    }
    
    /**
        Returns and after that clears the value of requested to pause or resume.
        @return the value of requested to pause or resume
    */
    public boolean getAndClearRequestedToPauseOrResume() {
        final boolean requestedToPauseOrResume = this.requestedToPauseOrResume;
        clearRequestedToPauseOrResume();
        return requestedToPauseOrResume;
    }
    
    /**
        Returns the value of remaining pauses count.
        @return the value of remaining pauses count
    */
    public int getRemainingPausesCount() {
        return remainingPausesCount;
    }

    /**
        Decreases the value of remaining pauses count.
    */
    public void decreaseRemainingPausesCount() {
        remainingPausesCount--;
    }

    /**
        Kicks the player.
        @param kickMessage reason of kicking
    */
    public void kick( final String kickMessage ) {
        this.kickMessage = kickMessage == null ? "<not given>" : kickMessage;
        kicked           = true;
        try {
            close();
        }
        catch ( RemoteException re ) {
            Logging.logError( re );
        }
    }

    /**
        Returns true, if player was kicked.
        @return true, if player was kicked; false otherwise
    */
    public boolean isKicked() throws RemoteException {
        return kicked;
    }
    
    /**
        Returns the kick message.
        @return the kick message
    */
    public String getKickMessage() throws RemoteException {
        return kickMessage;
    }

}
