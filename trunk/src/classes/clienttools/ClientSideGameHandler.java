
package classes.clienttools;

import classes.utilities.*;
import classes.options.*;
import classes.servertools.*;
import java.rmi.*;
import java.awt.*;
import classes.*;
import javax.swing.*;
import classes.servertools.gamecore.*;
import classes.servertools.gamecore.bullets.*;
import classes.servertools.gamecore.decorations.*;
import java.util.Vector;     // Only the vector, because Map exists in here too


/**
    The client side game handler class.
    @author Belicza Andras
*/
public class ClientSideGameHandler extends ControllableThread implements OptionsChangeListener, ViewDrawer {

    /** Color of the texts of the names of the players. */
    private static final Color   PLAYER_NAME_TEXT_COLOR                  = Color.white;
    /** Color of the guns. */
    private static final Color   GUN_COLOR                               = Color.black;
    /** Fore color of the hit point stripe. */
    private static final Color   HIT_POINT_STRIPE_FORE_COLOR             = new Color( 250,  80,  80 );
    /** Back color of the hit point stripe. */
    private static final Color   HIT_POINT_STRIPE_BACK_COLOR             = new Color( 150,  50,  50 );
    /** Fore color of the weapon reloading time stripe. */
    private static final Color   WEAPON_RELOADING_TIME_STRIPE_FORE_COLOR = new Color( 250, 250, 250 );
    /** Back color of the weapon reloading time stripe. */
    private static final Color   WEAPON_RELOADING_TIME_STRIPE_BACK_COLOR = new Color( 125, 125, 125 );
    /** Fore color of the oxygen bottle level stripe. */
    private static final Color   OXYGEN_BOTTLE_LEVEL_STRIPE_FORE_COLOR   = new Color(  40, 250,  40 );
    /** Back color of the hit oxygen bottle level stripe. */
    private static final Color   OXYGEN_BOTTLE_LEVEL_STRIPE_BACK_COLOR   = new Color(  10, 140,  10 );
    /** Width of the stripes. */
    private static final int     STRIPE_WIDTH                            = 3;
    /** Stroke of the stripes. */
    private static final Stroke  STRIPE_STROKE                           = new BasicStroke( STRIPE_WIDTH );
    
    /** Length of the gun in forward direction. */
    private static final float  GUN_LENGTH_FORWARD     = 10.0f;
    /** Length of the gun in backward direction. */
    private static final float  GUN_LENGTH_BACKWARD    =  2.0f;
    /** Width of gun in pixel. */
    private static final float  GUN_WIDTH              =  4.0f;
    /** Length of the stripes (hit points, weapon reloading time, etc.).*/
    private static final float  STRIPE_LENGTH          = 25.0f;
    /** Stroke to draw the gun with. */
    private static final Stroke GUN_STROKE             = new BasicStroke( GUN_WIDTH );

    /** Reference to the client options. */
    private final ClientOptions        clientOptions;
    /** Reference to the remote player. */
    private final RemotePlayer         remotePlayer;
    /** Reference to the main frame. */
    private final MainFrame            mainFrame;
    /** Reference to the main frame. */
    private final GameSceen            gameSceen;
    /** Reference to the Remote Qpac Server. */
    private final RemoteQServer        remoteQServer;
    /** Datas of the map. */
    private byte[]                     mapDatas;
    /** Width of the terrain. */
    private int                        terrainWidth;
    /** Height of the terrain. */
    private int                        terrainHeight;
    /** Number of bits needed to shift a map height position to get the map data index. */
    private int                        mapHeightShift;
    /** Reference to the wall images. */
    private final Image[]              wallImages;
    /** Reference to the worm images. */
    private final Image[][][]          wormImages;
    /** Own index in playerStates array. */
    private int                        ownIndex;
    /** Informations needed for drawing. */
    private InformationsForDrawing     informationsForDrawing;
    /** X graphics context translation coordinate when drawing view. */
    private int                        contextTranslationX;
    /** Y graphics context translation coordinate when drawing view. */
    private int                        contextTranslationY;
    /** X coordinate of the visible part of the left side of the terrain. */
    private int                        visibleTerrainWindowX1;
    /** Y coordinate of the visible part of the upper side of the terrain. */
    private int                        visibleTerrainWindowY1;
    /** X coordinate of the visible part of the right side of the terrain. */
    private int                        visibleTerrainWindowX2;
    /** Y coordinate of the visible part of the lower side of the terrain. */
    private int                        visibleTerrainWindowY2;
    /** Listener of all keyboard and mouse events. */
    private final InputDevicesListener inputDevicesListener;
    /** The map component where map will be drawn. */
    private MapComponent               mapComponent;
    /** Counter of downloading map datas. */
    private int                        mapDatasDownloadingCycleCounter = 0;
    /** Table of the players and kills, deaths. */
    private PlayerTable                playerTable;
    /** Tells whether we're in a refresh map component cycle (we have to refresh the map component).*/
    private boolean                    refreshMapComponentCycle;
    /** The unchangeable server options. */
    private UnchangeableServerOptions  unchangeableServerOptions;
    /** Value of the Counter of the changes of changeable server options at last download (tells which 'version' we have). */
    private int                        changeableServerOptionsChangeCounterAtLastDownload = -1;  // We want to download this at the first time
    /** One instance of the changeable server options. */
    private ChangeableServerOptions    changeableServerOptions;
    /** The decorations. */
    private Vector                     decorations;
    /** Indicates that view drawer is drawing at the moment, so next downloading musn't be started (It would 'overwrite' datas what are under drawing: it's a good chance for exceptions). */
    private volatile boolean           drawingAtTheMoment     = false;  // First download can be started immediatelly
    /** Indicates that the informations needed for drawing is being downloading, so drawing now musn't be started. */
    private volatile boolean           downloadingAtTheMoment = true;   // We not draw until the first datas haven't arrived
    /** Color of the wall rubbles. */
    private final Color                wallRubblesColor;

    /**
        Creates a new ClientSideGameHandler.
        @param mainFrame reference to the main frame
        @param gameSceen reference to the game sceen
        @param clientOptions reference to the client options
        @param remotePlayer reference to the remote player
        @param wallImages reference to the wall images
        @param wormImages reference to the worm images
        @param brickWallRGBRepresenter rgb representer of the brick walls
    */
    public ClientSideGameHandler( final MainFrame mainFrame, final GameSceen gameSceen, final ClientOptions clientOptions, final RemoteQServer remoteQServer, final RemotePlayer remotePlayer, final Image[] wallImages, final Image[][][] wormImages, final int brickWallRGBRepresenter ) {
        super( clientOptions.periodTime );
        this.mainFrame       = mainFrame;
        this.gameSceen       = gameSceen;
        this.clientOptions   = clientOptions;
        this.remoteQServer   = remoteQServer;
        this.remotePlayer    = remotePlayer;
        this.wallImages      = wallImages;
        this.wormImages      = wormImages;
        inputDevicesListener = new InputDevicesListener( this.remotePlayer, this );
        wallRubblesColor     = new Color( brickWallRGBRepresenter );
    }

    /**
        This method will be executed first before the cycles.
    */
    protected void firstOperate() {
        if ( clientOptions.clearMessageConsoleAtNewGame )
            mainFrame.clearMessageConsole();
        clientOptions.addOptionsChangeListener( this );
        gameSceen.setViewDrawer( this );
        gameSceen.setCursor( new Cursor( Cursor.CROSSHAIR_CURSOR ) );
        try {
            ownIndex       = remotePlayer.getOwnIndex();
            unchangeableServerOptions = remoteQServer.getUnchangeableServerOptions();
            if ( clientOptions.executeGetServerOptionsCommandAtNewGame )
                mainFrame.processMessage( Client.COMMAND_NAMES[ Client.COMMAND_GET_SERVER_OPTIONS ][ 0 ] );
            terrainWidth   = unchangeableServerOptions.mapWidth  << GeneralConsts.WALL_WIDTH_SHIFT;
            terrainHeight  = unchangeableServerOptions.mapHeight << GeneralConsts.WALL_HEIGHT_SHIFT;
            mapHeightShift = Map.calculateHeightShift( unchangeableServerOptions.mapWidth );
            mapComponent   = new MapComponent( mainFrame, unchangeableServerOptions.mapWidth, unchangeableServerOptions.mapHeight, clientOptions );
            mainFrame.setMapComponent( mapComponent );
            playerTable    = new PlayerTable();
            mainFrame.setPlayerTableComponent( playerTable );
            gameSceen.addKeyListener        ( inputDevicesListener );
            gameSceen.addMouseListener      ( inputDevicesListener );
            gameSceen.addMouseMotionListener( inputDevicesListener );
            gameSceen.addMouseWheelListener ( inputDevicesListener );
            gameSceen.requestFocus();
            optionsChanged();          // We want all setting to be done what are needed after options changed.
        }
        catch ( RemoteException re ) {
            remoteExceptionOccured();
        }
    }

    /**
        The cyclycal repeatable client works.
    */
    protected void operate() {
        try {
            while ( drawingAtTheMoment )  // Painting is always done by another thread. It can be it doesn't finished the last request. We have to wait it, and musn't bother it's datas.
                sleep( 1 );
        }
        catch ( InterruptedException ie ) {
            Logging.logError( ie );
        }
        try {
            if ( remotePlayer.isClosed() ) {
                if ( remotePlayer.isKicked() )
                    JOptionPane.showMessageDialog( mainFrame, new String[] { "The operator has kicked you, closing connection!", "Reason: " + remotePlayer.getKickMessage() }, "Warning", JOptionPane.WARNING_MESSAGE );
                else
                    JOptionPane.showMessageDialog( mainFrame, "Server has been shut down, closing connection!", "Warning", JOptionPane.ERROR_MESSAGE );
                requestToCancel();
            } else {
                remotePlayer.clearIdleTime();
                downloadingAtTheMoment = true;
                getInformationsForDrawing();
                downloadingAtTheMoment = false;
                gameSceen.repaint();
                if ( refreshMapComponentCycle ) {
                    mapComponent.refresh( mapDatas, informationsForDrawing.playerStates );
                    refreshMapComponentCycle = false;
                }
                if ( getCycleCounter() % clientOptions.refreshPlayersWindowAtCyclesCount == 0 )
                    playerTable.refresh( informationsForDrawing.playerStates );
                inputDevicesListener.updateMousePosition(); // Maybe terrain was scrolled, other point got under the mouse cursor
                checkForNewMessages();
            }
        }
        catch ( RemoteException re ) {
            remoteExceptionOccured();
        }
    }

    /**
        This method will be executed lastly after the cycles.
    */
    protected void lastOperate() {
        gameSceen.removeMouseWheelListener ( inputDevicesListener );
        gameSceen.removeMouseMotionListener( inputDevicesListener );
        gameSceen.removeMouseListener      ( inputDevicesListener );
        gameSceen.removeKeyListener        ( inputDevicesListener );
        mainFrame.setPlayerTableComponent( null );
        mainFrame.setMapComponent( null );
        gameSceen.setCursor( Cursor.getDefaultCursor() );
        gameSceen.setViewDrawer( null );
        clientOptions.removeOptionsChangeListener( this );
    }

    /**
        Things to do when RemoteException occurs.
    */
    private void remoteExceptionOccured() {
        JOptionPane.showMessageDialog( mainFrame, "Communication error, closing connection!", "Error", JOptionPane.ERROR_MESSAGE );
        requestToCancel();
    }

    /**
        Get the informations from server needed to draw.
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote mehtod invocation
    */
    private void getInformationsForDrawing() throws RemoteException {
        informationsForDrawing = remoteQServer.getInformationsForDrawing();
        if ( informationsForDrawing.changeableServerOptionsChangeCounter != changeableServerOptionsChangeCounterAtLastDownload ) {
            changeableServerOptions = remoteQServer.getChangeableServerOptions();
            changeableServerOptionsChangeCounterAtLastDownload = informationsForDrawing.changeableServerOptionsChangeCounter;
            changeableServerOptionsChanged();
        }
        decorations = clientOptions.doNotDownloadDecorations ? null : remoteQServer.getDecorations();
        if ( getCycleCounter() % clientOptions.downloadMapDatasAtCyclesCount == 0 ) {
            mapDatas = remoteQServer.getMapDatas();
            if ( mapDatasDownloadingCycleCounter++ % clientOptions.refreshMapComponentAtMapDatasDownloadingCyclesCount == 0 )
                refreshMapComponentCycle = true;
        }
    }

    /**
        This method will be called when changeable server options has changed.
    */
    private void changeableServerOptionsChanged() {
        playerTable.refresh( informationsForDrawing.playerStates, changeableServerOptions.gameType );
    }

    /**
        This method will be called when client options has changed (implementing OptionsChangeListener interface).
    */
    public void optionsChanged() {
        setOperatePeriodTime( clientOptions.periodTime );
        try {
            remotePlayer.setWormColorIndex( clientOptions.wormColorIndex );
            remotePlayer.setName( clientOptions.playerName     );
            playerTable.setFitTableToWindow( clientOptions.fitPlayerTableToPlayersWindow );
        }
        catch ( RemoteException re ) {
            remoteExceptionOccured();
        }
    }
    
//--------------------------------------------------------------------------------------------------------------------
    /**
        Draws the view of a component (implementing ViewDrawer interface).
        @param graphicsContext the graphics context in wich to draw
        @param width the width of the visible part of graphics context
        @param height the width of the visible part of graphics context
    */
    public void drawView( final Graphics graphicsContext, final int width, final int height ) {
        if ( downloadingAtTheMoment )  // We ignore os repaint if haven't got the informations for drawing.
            return;
        drawingAtTheMoment = true;
        try {
            prepareContextForDrawing( graphicsContext, width, height );
            drawTerrain    ( graphicsContext );
            drawRopes      ( graphicsContext );
            drawDecorations( graphicsContext );
            drawPlayers    ( graphicsContext );
            drawShots      ( graphicsContext );
            drawExplosions ( graphicsContext );
        }
        catch ( NullPointerException ne ) {         // Can be called by OS, when we don't have the informations for drawing
        }
        drawingAtTheMoment = false;
    }

    /**
        Prepares the graphics context for drawing.
        @param graphicsContext the graphics context wich to prepare
        @param width the width of the visible part of graphics context
        @param height the width of the visible part of graphics context
    */
    private void prepareContextForDrawing( final Graphics graphicsContext, final int width, final int height ) {
        int translateX   =     0, translateY    =      0;
        int drawingWidth = width, drawingHeight = height;
        if ( terrainHeight <= height ) {
            drawingHeight = terrainHeight;
            translateY    = ( height - drawingHeight ) >> 1;
            graphicsContext.clearRect( translateX, 0, drawingWidth, translateY );
            graphicsContext.clearRect( translateX, translateY + drawingHeight, drawingWidth, height - drawingHeight - translateY );
        }
        if ( terrainWidth <= width ) {
            drawingWidth = terrainWidth;
            translateX   = ( width - drawingWidth ) >> 1;
            graphicsContext.clearRect( 0, translateY, translateX, drawingHeight );
            graphicsContext.clearRect( translateX + drawingWidth, translateY, width - drawingWidth - translateX, drawingHeight );
        }
        if ( translateX == 0 )
            translateX = -Math.min( terrainWidth  - drawingWidth , Math.max( 0, (int) informationsForDrawing.playerStates[ ownIndex ].position.x - ( drawingWidth  >> 1 ) ) );
        if ( translateY == 0 )
            translateY = -Math.min( terrainHeight - drawingHeight, Math.max( 0, (int) informationsForDrawing.playerStates[ ownIndex ].position.y - ( drawingHeight >> 1 ) ) );
        visibleTerrainWindowX1 = Math.max( 0, -translateX );
        visibleTerrainWindowY1 = Math.max( 0, -translateY );
        visibleTerrainWindowX2 = visibleTerrainWindowX1 + drawingWidth  - 1;
        visibleTerrainWindowY2 = visibleTerrainWindowY1 + drawingHeight - 1;
        graphicsContext.translate( translateX, translateY );
        contextTranslationX = translateX;
        contextTranslationY = translateY;
    }
    
    /**
        Draws the terrain.
        @param graphicsContext the graphics context in wich to draw
    */
    private void drawTerrain( final Graphics graphicsContext ) {
        final int startX      = visibleTerrainWindowX1 - visibleTerrainWindowX1 % GeneralConsts.WALL_WIDTH;
        final int startIndexX = startX >> GeneralConsts.WALL_WIDTH_SHIFT;

        int index, x, y = visibleTerrainWindowY1 - visibleTerrainWindowY1 % GeneralConsts.WALL_HEIGHT;
        for ( int indexY = y / GeneralConsts.WALL_HEIGHT; y <= visibleTerrainWindowY2; indexY++, y += GeneralConsts.WALL_HEIGHT )
            for ( x = startX, index = ( indexY << mapHeightShift ) + startIndexX; x <= visibleTerrainWindowX2; index++, x += GeneralConsts.WALL_WIDTH )
                graphicsContext.drawImage( wallImages[ mapDatas[ index ] ], x, y, null );
    }

    /**
        Draws the ropes of the players.
        @param graphicsContext the graphics context in wich to draw
    */
    private void drawRopes( final Graphics graphicsContext ) {
        for ( int playerStateIndex = 0; playerStateIndex < informationsForDrawing.playerStates.length; playerStateIndex++ ) {
            final PlayerState playerState = informationsForDrawing.playerStates[ playerStateIndex ];
            if ( playerState != null && playerState.rope != null )
                playerState.rope.draw( graphicsContext );
        }
    }

    /**
        Draws the players.
        Players are drawn here. Not with Player.draw() or PlayerState.draw().
        Reason: every clients can have their own graphics files and looks, so its not the player object business
        to draw the actual look of the player.
        @param graphicsContext the graphics context in wich to draw
    */
    private void drawPlayers( final Graphics graphicsContext ) {
        for ( int playerStateIndex = 0; playerStateIndex < informationsForDrawing.playerStates.length; playerStateIndex++ ) {
            final PlayerState playerState = informationsForDrawing.playerStates[ playerStateIndex ];
            if ( playerState != null ) {
                if ( playerState.hitsWindow( visibleTerrainWindowX1, visibleTerrainWindowY1, visibleTerrainWindowX2, visibleTerrainWindowY2 ) ) {
                    final int positionX = (int) playerState.position.x;
                    final int positionY = (int) playerState.position.y;
                    final int phase     = Math.min( Math.max( playerState.phase, 0 ), GeneralConsts.WORM_PHASES_COUNT[ playerState.direction ] - 1 ); // If it's under calculation...
                    graphicsContext.drawImage( wormImages[ playerState.wormColorIndex ][ playerState.direction ][ phase ], positionX - playerState.dimensionToLeft, positionY - playerState.dimensionToUp, null );
                    final boolean showPlayerName = clientOptions.showPlayerNames && playerStateIndex != ownIndex;
                    if ( clientOptions.showSelectedWeapons || showPlayerName ) {
                        String textToShow = null;
                        if ( showPlayerName && clientOptions.showSelectedWeapons )
                            textToShow = playerState.name + ':' + ( playerState.weaponIndex + 1 );
                        else if ( !showPlayerName && clientOptions.showSelectedWeapons )
                            textToShow = Integer.toString( playerState.weaponIndex + 1 );
                        else if ( showPlayerName && !clientOptions.showSelectedWeapons )
                            textToShow = playerState.name;
                        graphicsContext.setColor( PLAYER_NAME_TEXT_COLOR );
                        graphicsContext.drawString( textToShow, positionX - ( graphicsContext.getFontMetrics().stringWidth( textToShow ) >> 1 ), positionY - playerState.dimensionToUp );
                    }
                    drawPlayerGun( graphicsContext, playerStateIndex );
                    drawPlayerCondition( graphicsContext, playerState );
                }
            }
        }
    }

    /**
        Draws a gun of a player
        @param graphicsContext the graphics context in wich to draw
        @param playerStateIndex index of the state of player whose gun to be drawn
    */
    private void drawPlayerGun( final Graphics graphicsContext, final int playerStateIndex ) {
        final FloatVector position = informationsForDrawing.playerStates[ playerStateIndex ].position;
        final double      angle    = position.angleTo( playerStateIndex == ownIndex ? inputDevicesListener.getMousePosition() : informationsForDrawing.playerStates[ playerStateIndex ].mousePosition );
        final float       angleSin = (float) Math.sin( angle );
        final float       angleCos = (float) Math.cos( angle );
        graphicsContext.setColor( GUN_COLOR );
        final Graphics2D graphicsContext2D = (Graphics2D) graphicsContext;
        final Stroke     oldStroke         = graphicsContext2D.getStroke();
        graphicsContext2D.setStroke( GUN_STROKE );
        graphicsContext2D.drawLine( (int) ( position.x - angleCos * GUN_LENGTH_BACKWARD ), (int) ( position.y + angleSin * GUN_LENGTH_BACKWARD ), (int) ( position.x + angleCos * GUN_LENGTH_FORWARD ), (int) ( position.y - angleSin * GUN_LENGTH_FORWARD ) );
        graphicsContext2D.setStroke( oldStroke );
    }

    /**
        Draws the condition, the state of a player: hit points, reloading time, air reserves.
        @param graphicsContext the graphics context in wich to draw
        @param playerState player state whoose condition to be drawn
    */
    private void drawPlayerCondition( final Graphics graphicsContext, final PlayerState playerState ) {
        final int positionX1 = (int) playerState.position.x - ( (int) STRIPE_LENGTH >> 1 );
        final int positionX2 = positionX1 + (int) STRIPE_LENGTH;
        int       positionY  = (int) playerState.position.y + playerState.dimensionToDown + 4;
        int       temp;

        final Graphics2D graphicsContext2D = (Graphics2D) graphicsContext;
        final Stroke     oldStroke         = graphicsContext2D.getStroke();
        graphicsContext2D.setStroke( STRIPE_STROKE );
        if ( clientOptions.showWormHitPoints ) {
            graphicsContext2D.setColor( HIT_POINT_STRIPE_BACK_COLOR );
            graphicsContext2D.drawLine( positionX1 + ( temp = (int) ( STRIPE_LENGTH * playerState.hitPoint / GeneralConsts.MAX_PLAYER_HIT_POINT ) ), positionY, positionX2, positionY );
            graphicsContext2D.setColor( HIT_POINT_STRIPE_FORE_COLOR );
            graphicsContext2D.drawLine( positionX1, positionY, positionX1 + temp, positionY );
            positionY += STRIPE_WIDTH;
        }
        if ( clientOptions.showWormWeaponReloadingTimes ) {
            graphicsContext2D.setColor( WEAPON_RELOADING_TIME_STRIPE_BACK_COLOR );
            graphicsContext2D.drawLine( positionX2 - ( temp = (int) ( STRIPE_LENGTH * playerState.weapon.getReloadingTime() / playerState.weapon.getMaxReloadingTime() ) ), positionY, positionX2, positionY );
            graphicsContext2D.setColor( WEAPON_RELOADING_TIME_STRIPE_FORE_COLOR );
            graphicsContext2D.drawLine( positionX1, positionY, positionX2 - temp, positionY );
            positionY += STRIPE_WIDTH;
        }
        if ( clientOptions.showWormOxygenBottleLevels ) {
            graphicsContext2D.setColor( OXYGEN_BOTTLE_LEVEL_STRIPE_BACK_COLOR );
            graphicsContext2D.drawLine( positionX1 + ( temp = (int) ( STRIPE_LENGTH * playerState.oxygenBottleLevel / GeneralConsts.MAX_OXYGEN_BOTTLE_LEVEL ) ), positionY, positionX2, positionY );
            graphicsContext2D.setColor( OXYGEN_BOTTLE_LEVEL_STRIPE_FORE_COLOR );
            graphicsContext2D.drawLine( positionX1, positionY, positionX1 + temp, positionY );
            positionY += STRIPE_WIDTH;
        }
        graphicsContext2D.setStroke( oldStroke );
    }

    /**
        Draws the shots.
        @param graphicsContext the graphics context in wich to draw
    */
    private void drawShots( final Graphics graphicsContext ) {
        final Vector bullets = informationsForDrawing.bullets;
        Bullet bullet;
        for ( int bulletCounter = bullets.size() - 1; bulletCounter >= 0; bulletCounter-- )
            if ( ( bullet = ( (Bullet) bullets.elementAt( bulletCounter ) ) ).hitsWindow( visibleTerrainWindowX1, visibleTerrainWindowY1, visibleTerrainWindowX2, visibleTerrainWindowY2 ) )
                bullet.draw( graphicsContext );
        final Vector polylineShots = informationsForDrawing.polylineShots;
        for ( int polylineShotCounter = polylineShots.size() - 1; polylineShotCounter >= 0; polylineShotCounter-- )
            ( (PolylineShot) polylineShots.elementAt( polylineShotCounter ) ).draw( graphicsContext );
    }

    /**
        Draws the decorations
        @param graphicsContext the graphics context in wich to draw
    */
    private void drawDecorations( final Graphics graphicsContext ) {
        if ( decorations != null ) {
            final boolean doubleSizedPointDecorations = clientOptions.showRubblesAndBloodInDoubleSize;
            Decoration      decoration;
            PointDecoration pointDecoration;
            for ( int decorationIndex = decorations.size() - 1; decorationIndex >= 0; decorationIndex-- )
                if ( ( decoration = (Decoration) decorations.elementAt( decorationIndex ) ).hitsWindow( visibleTerrainWindowX1, visibleTerrainWindowY1, visibleTerrainWindowX2, visibleTerrainWindowY2 ) )
                    try {
                        if ( decoration.isPointDecoration() ) {
                            if ( ( pointDecoration = (PointDecoration) decoration ).isRubble() )
                                pointDecoration.setColor( wallRubblesColor );
                            decoration.drawPointDecoration( graphicsContext, doubleSizedPointDecorations );
                        }
                        else
                            decoration.draw( graphicsContext );
                    }
                    catch ( Exception e ) {   // This should never happen
                        Logging.logError( e );
                    }
        }
    }

    /**
        Draws the explosions.
        @param graphicsContext the graphics context in wich to draw        
    */
    private void drawExplosions( final Graphics graphicsContext ) {
        final Vector explosions = informationsForDrawing.explosions;
        for ( int explosionIndex = explosions.size() - 1; explosionIndex >= 0; explosionIndex-- ) {
            final Explosion explosion = (Explosion) explosions.elementAt( explosionIndex );
            if ( explosion.hitsWindow( visibleTerrainWindowX1, visibleTerrainWindowY1, visibleTerrainWindowX2, visibleTerrainWindowY2 ) )
                explosion.draw( graphicsContext );
        }
    }

//--------------------------------------------------------------------------------------------------------------------
    /**
        Returns the x graphics context translation coordinate.
        @return x translation coordinate
    */
    public int getContextTranslationX() {
        return contextTranslationX;
    }

    /**
        Returns the y graphics context translation coordinate.
        @return y translation coordinate
    */
    public int getContextTranslationY() {
        return contextTranslationY;
    }

    /**
        Checks if any we have got any new messages.
        @throws RemoteException thrown by java rmi mechanism if error occurs during remote mehtod invocation
    */
    public void checkForNewMessages() throws RemoteException {
        if ( remotePlayer.hasNewMessage() ) {
            final Vector messages = remotePlayer.getMessages();
            for ( int messageCounter = 0; messageCounter < messages.size(); messageCounter++ )
                mainFrame.addMessage( (String) messages.elementAt( messageCounter ) );
            remotePlayer.clearMessages();
        }
    }
    
    /**
        Returns the string representation of unchangeable server options.
        @return the string representation of unchangeable server options
    */
    public String getUnchangeableServerOptions() {
        return unchangeableServerOptions == null ? "" : unchangeableServerOptions.toString();
    }

    /**
        Increases the dimension of the game sceen.
    */
    public void increaseGameSceenDimension() {
        gameSceen.increaseDimension();
        mainFrame.checkBounds();
    }

    /**
        Decreases the dimension of the game sceen.
    */
    public void decreaseGameSceenDimension() {
        gameSceen.decreaseDimension();
        mainFrame.checkBounds();
    }

    /**
        Increases the map zooming factor.
    */
    public void increaseMapZoomingFactor() {
        if ( clientOptions.mapZoomingFactor < ClientOptions.MAX_MAP_ZOOMING_FACTOR ) {
            clientOptions.mapZoomingFactor++;
            clientOptions.fireOptionsChanged();
            mainFrame.updateMapWindowScrollPane();
        }
    }

    /**
        Decreases the map zooming factor.
    */
    public void decreaseMapZoomingFactor() {
        if ( clientOptions.mapZoomingFactor > ClientOptions.MIN_MAP_ZOOMING_FACTOR ) {
            clientOptions.mapZoomingFactor--;
            clientOptions.fireOptionsChanged();
            mainFrame.updateMapWindowScrollPane();
        }
    }

}
