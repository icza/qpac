
package classes;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import classes.utilities.*;
import classes.servertools.*;
import classes.clienttools.*;
import classes.options.*;
import classes.servertools.gamecore.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;


/**
    This is the main frame of the Qpac.
    @author Belicza Andras
*/
public class MainFrame extends JFrame {

    /** Name of the window options file. */
    private static final String WINDOW_OPTIONS_FILE_NAME = "windowpositions.dat";
    /** Default location of the main frame. */
    private static final Point  DEFAULT_LOCATION         = new Point( 20, 20 );
    /** Index of the Message console window. */
    public  static final int    MESSAGE_CONSOLE_INDEX    = 0;
    /** Index of the Map window. */
    public  static final int    MAP_WINDOW_INDEX         = 1;
    /** Index of the Players window. */
    public  static final int    PLAYERS_WINDOW_INDEX     = 2;
    /** Count of tool/extra windows. */
    public  static final int    WINDOWS_COUNT            = 3;
    /** Name of the help command. */
    public static  final String HELP_COMMAND_NAME        = "/help";

    /** Name of the commands. */
    private static final String[][] COMMAND_NAMES  = { { HELP_COMMAND_NAME, "/h" } };
    /** Sort (1 line) description of the commands. */
    private static final String[]   COMMAND_INFOS  = { "list available commands"   };
    /** Help command. */
    private static final int        COMMAND_HELP   = 0;


    /** The main menu bar. */
    private final MainMenuBar      mainMenuBar = new MainMenuBar( this );
    /** The Server object. */
    private Server                 server;
    /** The Client object. */
    private Client                 client;
    /** The options of the server. */
    private ServerOptions          serverOptions;
    /** The options of the client. */
    private ClientOptions          clientOptions;
    /** The game sceen. */
    private final GameSceen        gameSceen           = new GameSceen( this );
    /** Images of the walls. */
    private final Image[]          wallImages          = new Image[ Map.WALLS_COUNT ];
    /** RGB (color) representers of the walls.*/
    private final int[]            wallRGBRepresenters = new int[ Map.WALLS_COUNT ];
    /** Images of the worms. */
    private final Image[][][]      wormImages          = new Image[ ClientOptions.WORM_COLORS.length ][ GeneralConsts.WORM_DIRECTIONS_COUNT ][];
    /** Tool/extra windows: message console, map window, players window. */
    private final AbstractDialog[] windows             = new AbstractDialog[ WINDOWS_COUNT ];
    /** Contains the menu item indexes in main menu bar of the windows (who are identified by window indexes). */
    private final int[]            windowIndexToMenuItemIndex = new int[ windows.length ];
    /** The local command processor. */
    private final CommandProcessor commandProcessor    = new CommandProcessor( COMMAND_NAMES, COMMAND_INFOS );
    
    /**
        Creates a new MainFrame.
    */
    public MainFrame() {
        super( GeneralConsts.APPLICATION_NAME );
        determineMenuItemIndexes();
        setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
        addWindowListener( new WindowAdapter() {
            public void windowClosing( final WindowEvent we ) {
                exitGame();
            }
        } );
        buildGUI();
        pack();
        setResizable( false );
        windows[ MESSAGE_CONSOLE_INDEX ] = new MessageConsole( this, this                                    );
        windows[ MAP_WINDOW_INDEX             ] = new MapWindow     ( this, windows[ MESSAGE_CONSOLE_INDEX ] );
        windows[ PLAYERS_WINDOW_INDEX         ] = new PlayersWindow ( this, this                                    );
        windows[ MAP_WINDOW_INDEX     ].setFocusableWindowState( false );
        windows[ PLAYERS_WINDOW_INDEX ].setFocusableWindowState( false );
        loadOptions();
        gameSceen.setClientOptions( clientOptions );
        boolean foundImageFiles = loadWallImages() && loadWormImages();
        mainMenuBar.setGameStatus( foundImageFiles ? MainMenuBar.GAME_STATUS_NOT_CONNECTED : MainMenuBar.GAME_STATUS_DISABLED_ALL_GAME_ACTIVITY );
        show();
        if ( !foundImageFiles )
            JOptionPane.showMessageDialog( this, new String[] { "Wrong or missing image files: 'datas\\worm.gif' and/or 'datas\\walls.gif'!", "Creating and joining game now disabled!" }, "Error", JOptionPane.ERROR_MESSAGE );
    }
    
    /**
        Determines the menu item indexes: fills up the windowToMenuItemIndex array.
    */
    private void determineMenuItemIndexes() {
        for ( int windowIndex = 0; windowIndex < windows.length; windowIndex++ ) {
            switch ( windowIndex ) {
                case MESSAGE_CONSOLE_INDEX : 
                    windowIndexToMenuItemIndex[ windowIndex ] = MainMenuBar.MESSAGE_CONSOLE_MENU_ITEM_INDEX;
                    break;
                case MAP_WINDOW_INDEX :
                    windowIndexToMenuItemIndex[ windowIndex ] = MainMenuBar.MAP_WINDOW_MENU_ITEM_INDEX;
                    break;
                case PLAYERS_WINDOW_INDEX :
                    windowIndexToMenuItemIndex[ windowIndex ] = MainMenuBar.PLAYERS_WINDOW_MENU_ITEM_INDEX;
                    break;
                default :
                    Logging.logError( new Exception( "Not every window has a menu item in the Windows menu!" ) );
                    break;
            }
        }
    }

    /**
        Builds the graphical user interface of the frame.
    */
    private void buildGUI() {
        final JPanel newContentPane = new JPanel( new FlowLayout( FlowLayout.CENTER, 0, 0 ) );
        newContentPane.add( gameSceen );
        setContentPane( newContentPane );
        setJMenuBar( mainMenuBar );
    }
    
    /**
        Makes this window (frame) visible. (overriding Window.show() ).
    */
    public void show() {
        for ( int windowIndex = windows.length - 1; windowIndex >= 0; windowIndex-- ) // Downward direction: it's more aesthetic
            checkWindowVisibility( windowIndex );
        super.show();
    }

    /**
        Sets the new bounds of the main frame.
        @param location new location of the main frame
        @param gameSceenDimension new dimension of the game sceen
    */
    private void setNewBounds( final Point location, final Dimension gameSceenDimension ) {
        gameSceen.setDimension( gameSceenDimension );
        setLocation( location );
        pack();
        repaint();
    }

//------- Menu item commands------------------------------------------------------------------------------------------
    /**
        Creates a new game.
    */
    public void createGame() {
        closeGame();
        try {
            server = new Server( this, serverOptions );
            server.start();
            connectGame( false );
        }
        catch ( RemoteObjectRegistryCreationFailedException re ) {
            closeGame();
            JOptionPane.showMessageDialog( this, new String[] { "Can't create server:", re.getMessage() }, "Error", JOptionPane.ERROR_MESSAGE );
        }
        catch ( java.rmi.RemoteException re ) {
            closeGame();
            Logging.logError( re );
        }
    }
    
    /**
        Joines a game.
    */
    public void joinGame() {
        connectGame( true );
    }

    /**
        Closes actual game.
    */
    public void closeGame() {
        if ( client != null ) {
            client.close();
            client = null;
        }
        if ( server != null ) {
            server.close();
            server = null;
        }
        mainMenuBar.setGameStatus( MainMenuBar.GAME_STATUS_NOT_CONNECTED );
    }

    /**
        Exits from the program after all neccessary work is done.
    */
    public void exitGame() {
        closeGame();
        if ( mainMenuBar.getCheckBoxMenuItemState( MainMenuBar.SETTINGS_MENU_INDEX, MainMenuBar.SAVE_OPTIONS_ON_EXIT_MENU_ITEM_INDEX ) ) {
            serverOptions.saveOptions();
            clientOptions.saveOptions();
        }
        if ( mainMenuBar.getCheckBoxMenuItemState( MainMenuBar.WINDOWS_MENU_INDEX, MainMenuBar.SAVE_WINDOW_POSITIONS_ON_EXIT_MENU_ITEM_INDEX ) )
            saveWindowPositions();
        mainMenuBar.saveMenuStates();
        dispose();
        System.exit( 0 );
    }

    /**
        Shows the client options dialog.
    */
    public void showClientOptionsDialog() {
        clientOptions.showSettingsDialog();
    }
    
    /**
        Shows the server options dialog.
    */
    public void showServerOptionsDialog() {
        serverOptions.showSettingsDialog();
    }
    
    /**
        Checks and sets the visibility of a window.
        @param windowIndex index of window whose visiblity needed to be checked
    */
    public void checkWindowVisibility( final int windowIndex ) {
        windows[ windowIndex ].setVisible( mainMenuBar.getCheckBoxMenuItemState( MainMenuBar.WINDOWS_MENU_INDEX, windowIndexToMenuItemIndex[ windowIndex ] ) );
    }
    
    /**
        Clears the messages in the message console.
    */
    public void clearMessageConsole() {
        ( (MessageConsole) windows[ MESSAGE_CONSOLE_INDEX ] ).clearMessages();
    }
    
    /**
        Checks and sets the line-wrapping policy of the text area of the message console.
    */
    public void checkMessageConsoleLineWrap() {
        ( (MessageConsole) windows[ MESSAGE_CONSOLE_INDEX ] ).setMessagesLineWrap( mainMenuBar.getCheckBoxMenuItemState( MainMenuBar.WINDOWS_MENU_INDEX, MainMenuBar.LINE_WRAP_IN_MESSAGE_CONSOLE_MENU_ITEM_INDEX ) );
    }

    /**
        Restores the default positions of all windows.
    */
    public void restoreDefaultWindowPositions() {
        setNewBounds( DEFAULT_LOCATION, gameSceen.DEFAULT_DIMENSION );
        for ( int windowIndex = 0; windowIndex < windows.length; windowIndex++ ) // Forward direction: locating can be relative to a previous window
            windows[ windowIndex ].restoreDefaultBounds();
        packWindows();
    }

    /**
        Processes a message: sends it to the server (through the client) or process it as a command.
        @param message message to be processed
    */
    public void processMessage( final String message ) {
        if ( message.equals( MessageConsole.EMPTY_LINE ) )
            addMessage( message );                              // An "ENTER"
        else
            if ( message.charAt( 0 ) == '/' ) {                 // This will be a command
                final String command = message.toLowerCase();
                switch ( commandProcessor.searchCommand( command ) ) {
                    case COMMAND_HELP :
                        addMessage( "\nAvailable commands at the moment:\n" + commandProcessor.getCommandList() + ( client == null ? "" : '\n' + client.getCommandList() ) + ( server == null ? "" : '\n' + server.getCommandList() ) );
                        break;
                    case CommandProcessor.COMMAND_INVALID :
                        if ( client == null || !client.processCommand( command ) )
                            if ( server == null )
                                addMessage( "Invalid command: " + message );
                            else
                                if ( !server.processCommand( command ) )
                                    addMessage( "Invalid command: " + message );
                }
            }
            else                                                // This will be a message
                if ( client != null )
                    client.sendMessage( message );
    }

    /**
        Pack windows: sets theirs bounds to fit to each other.
    */
    public void packWindows() {
        windows[ PLAYERS_WINDOW_INDEX  ].setSize( this.getWidth(), windows[ PLAYERS_WINDOW_INDEX ].getHeight() );
        windows[ MESSAGE_CONSOLE_INDEX ].setSize( windows[ MESSAGE_CONSOLE_INDEX ].getWidth(), this.getHeight() + windows[ PLAYERS_WINDOW_INDEX ].getHeight() - windows[ MAP_WINDOW_INDEX ].getHeight() );
        for ( int windowIndex = 0; windowIndex < windows.length; windowIndex++ ) { // Forward direction: locating can be relative to a previous window
            windows[ windowIndex ].alignDialog();
            windows[ windowIndex ].validate();
        }
    }

    /**
        Shows the help about control keys.
    */
    public void showControlKeysHelp() {
        Helps.showControlKeysHelp( this );
    }

    /**
        Shows tips about the game.
    */
    public void showTips() {
        Helps.showTips( this );
    }

    /**
        Shows the faq of the game.
    */
    public void showFaq() {
        Helps.showFaq( this );
    }

    /**
        Shows the host name and the ip address of the computer.
    */
    public void showHostNameAndIp() {
        String localHostName, localIP;
        try {
            final InetAddress localHost = InetAddress.getLocalHost();
            localHostName = localHost.getHostName();
            if ( !localHostName.equals( localHost.getCanonicalHostName() ) )
                localHostName = localHostName + " (" + localHost.getCanonicalHostName() + ')';
            localIP = localHost.getHostAddress();
        }
        catch ( UnknownHostException ue ) {
            localIP = localHostName = "<unknown>";
        }
        JOptionPane.showMessageDialog( this, new String[] { "Host name: " + localHostName, "IP address: " + localIP }, "Host name and IP", JOptionPane.INFORMATION_MESSAGE );
    }

    /**
        Shows main infromations about the game.
    */
    public void showAboutInformations() {
        Helps.showAboutInformations( this );
    }
    
//--------------------------------------------------------------------------------------------------------------------
    /**
        Connects to a game.
        @param isDestinationHostUserDefined tells whether we have to connect to the host given at client options, or to our local server
    */
    private void connectGame( final boolean isDestinationHostUserDefined ) {
        try {
            client = new Client( this, gameSceen, isDestinationHostUserDefined, clientOptions, isDestinationHostUserDefined ? null : serverOptions.password, wallImages, wormImages, wallRGBRepresenters[ Map.WALL_BRICK ] );
            client.start();
            mainMenuBar.setGameStatus( MainMenuBar.GAME_STATUS_CONNECTED );
        }
        catch ( ConnectingToServerFailedException ce ) {
            closeGame();
            JOptionPane.showMessageDialog( this, new String[] { "Can't join to server:", ce.getMessage() }, "Error", JOptionPane.ERROR_MESSAGE );
        }
        catch ( Exception e ) {
            closeGame();
            JOptionPane.showMessageDialog( this, new String[] { "Can't join to server:", e.getMessage() }, "Error", JOptionPane.ERROR_MESSAGE );
        }
    }
    
    /**
        Tries to load all saved options from files. If one of them fails, it with be created with the default values.
    */
    private void loadOptions() {
        if ( !loadWindowPositions() )
            restoreDefaultWindowPositions();
        mainMenuBar.loadMenuStates();
        checkMessageConsoleLineWrap();
        serverOptions = ServerOptions.loadOptions( this );
        if ( serverOptions == null )
            serverOptions = new ServerOptions( this );
        clientOptions = ClientOptions.loadOptions( this );
        if ( clientOptions == null )
            clientOptions = new ClientOptions( this );
    }

    /**
        Saves the positions of the windows (the bounds).
        @return true if saving was successful; false otherwise
    */
    private boolean saveWindowPositions() {
        try {
            final ObjectOutputStream file = new ObjectOutputStream( new FileOutputStream( GeneralConsts.OPTIONS_DIRECTORY + WINDOW_OPTIONS_FILE_NAME ) );
            file.writeObject( this     .getLocation()  );
            file.writeObject( gameSceen.getDimension() );
            for ( int windowIndex = 0; windowIndex <= windows.length; windowIndex++ )
                file.writeObject( windows[ windowIndex ].getBounds() );
            file.close();
        }
        catch ( Exception e ) {
            return false;
        }
        return true;
    }

    /**
        Loads the positions of the windows (the bounds).
        @return true if loading was successful; false otherwise
    */
    private boolean loadWindowPositions() {
        try {
            final ObjectInputStream file = new ObjectInputStream( new FileInputStream( GeneralConsts.OPTIONS_DIRECTORY + WINDOW_OPTIONS_FILE_NAME ) );
            final Point     mainFrameLocation  = (Point)     file.readObject();
            final Dimension gameSceenDimension = (Dimension) file.readObject();
            setNewBounds( mainFrameLocation, gameSceenDimension );
            for ( int windowIndex = 0; windowIndex < windows.length; windowIndex++ )
                windows[ windowIndex ].setBounds( (Rectangle) file.readObject() );
            file.close();
        }
        catch ( Exception e ) {
            return false;
        }
        return true;
    }

    /**
        Loads the images of the walls.
        @return true, if image loading was succesful; false otherwise
    */
    private boolean loadWallImages() {
        final Image wallsImage = new ImageIcon( GeneralConsts.DATAS_DIRECTORY + "walls.gif" ).getImage();
        if ( wallsImage.getWidth( null ) < GeneralConsts.WALL_WIDTH + 2 || wallsImage.getHeight( null ) < ( GeneralConsts.WALL_HEIGHT + 1 ) * Map.WALLS_COUNT + 1 )
            return false;
        final BufferedImage wallsBufferedImage = GameSceen.createCompatibleImage( wallsImage.getWidth( null ), wallsImage.getHeight( null ) );
        wallsBufferedImage.getGraphics().drawImage( wallsImage, 0, 0, null );
        for ( int wallImageIndex = 0; wallImageIndex< wallImages.length; wallImageIndex++ )
            wallImages[ wallImageIndex ] = wallsBufferedImage.getSubimage( 1, wallImageIndex * ( GeneralConsts.WALL_HEIGHT + 1 ) + 1, GeneralConsts.WALL_WIDTH, GeneralConsts.WALL_HEIGHT );

        for ( int wallRGBRepresenterIndex = 0; wallRGBRepresenterIndex < wallRGBRepresenters.length; wallRGBRepresenterIndex++ )
            wallRGBRepresenters[ wallRGBRepresenterIndex ] = ( (BufferedImage) wallImages[ wallRGBRepresenterIndex ] ).getRGB( 1, 1 );
        return true;
    }

    /**
        Loads the images of the worm.
        @return true, if image loading was succesful; false otherwise
    */
    private boolean loadWormImages() {
        final Image loadedWormImages = new ImageIcon( GeneralConsts.DATAS_DIRECTORY + "worm.gif" ).getImage();
        int maxWormPhase = -1;
        for ( int direction = 0; direction < GeneralConsts.WORM_DIRECTIONS_COUNT; direction++ )
            maxWormPhase = Math.max( maxWormPhase, GeneralConsts.WORM_PHASES_COUNT[ direction ] );
        if ( loadedWormImages.getWidth( null ) < ( GeneralConsts.WORM_WIDTH + 1 ) * maxWormPhase + 1 || loadedWormImages.getHeight( null ) < ( GeneralConsts.WORM_HEIGHT + 1 ) * GeneralConsts.WORM_DIRECTIONS_COUNT + 1 )
            return false;
        final BufferedImage bufferedWormImages = GameSceen.createCompatibleImage( loadedWormImages.getWidth( null ), loadedWormImages.getHeight( null ) );
        bufferedWormImages.getGraphics().drawImage( loadedWormImages, 0, 0, null );
        final int           NON_OPAQUE_RGB     = new Color( 0, 0, 0, 0 ).getRGB();
        final int           BLACK_COLOR_RGB    = new Color( 0, 0, 0    ).getRGB();

        for ( int colorIndex = 0; colorIndex < wormImages.length; colorIndex++ ) {
            final int WORM_COLOR_RGB = ClientOptions.WORM_COLORS[ colorIndex ].getRGB();
            for ( int direction = 0; direction < wormImages[ colorIndex ].length; direction++ ) {
                wormImages[ colorIndex ][ direction ] = new Image[ GeneralConsts.WORM_PHASES_COUNT[ direction ] ];
                for ( int phase = 0; phase < wormImages[ colorIndex ][ direction ].length; phase++ ) {
                    final int baseX = phase     * ( GeneralConsts.WORM_WIDTH  + 1 ) + 1;
                    final int baseY = direction * ( GeneralConsts.WORM_HEIGHT + 1 ) + 1;
                    final BufferedImage wormImage = GameSceen.createCompatibleBitmaskTransparencyImage( GeneralConsts.WORM_WIDTH, GeneralConsts.WORM_HEIGHT );
                    for ( int y = 0; y < GeneralConsts.WORM_HEIGHT; y++ )
                        for ( int x = 0; x < GeneralConsts.WORM_WIDTH; x++ )
                            wormImage.setRGB( x, y, bufferedWormImages.getRGB( baseX + x, baseY + y ) == BLACK_COLOR_RGB ? NON_OPAQUE_RGB : WORM_COLOR_RGB );
                    wormImages[ colorIndex ][ direction ][ phase ] = wormImage;
                }
            }
        }
        return true;
    }
    
    /**
        Adds a new message to the message console.
        @param message message to be added
    */
    public void addMessage( final String message ) {
        ( (MessageConsole) windows[ MESSAGE_CONSOLE_INDEX ] ).addMessage( message );
    }
    
    /**
        Clears the check box menu item of a window(by settings its state to false).
        @param windowIndex index of window whose check box menu item needed to be clear
    */
    public void clearWindowCheckBoxMenuItem( final int windowIndex ) {
        mainMenuBar.clearCheckBoxMenuItem( MainMenuBar.WINDOWS_MENU_INDEX, windowIndexToMenuItemIndex[ windowIndex ] );
    }

    /**
        Sets the map component of the map window.
        @param mapComponent map component to be set
    */
    public void setMapComponent( final MapComponent mapComponent ) {
        windows[ MAP_WINDOW_INDEX ].setScrollableComponent( mapComponent );
    }

    /**
        Sets the player table component of the players window.
        @param playerTable table of the players
    */
    public void setPlayerTableComponent( final PlayerTable playerTable ) {
        windows[ PLAYERS_WINDOW_INDEX ].setScrollableComponent( playerTable );
    }
    
    /**
        Returns the RGB representers of the walls.
        @return the RGB representers of the walls
    */
    public int[] getWallRGBRepresenters() {
        return wallRGBRepresenters;
    }
    
    /**
        Updates the scroll pane of the map window (needed when map zooming factor has been changed).
    */
    public void updateMapWindowScrollPane() {
        windows[ MAP_WINDOW_INDEX ].resizeAndRepaint();
    }

    /**
        Checks and resizes the main frame, and moves other windows if needed.
    */
    public void checkBounds() {
        pack();
        repaint();
    }
    
    /**
        Transfers the input focus to the game sceen.
    */
    public void transferFocusToGameSceen() {
        gameSceen.requestFocus();
    }

    /**
        Transfers the input focus to the message console.
    */
    public void transferFocusToMessageConsole() {
        ( (MessageConsole) windows[ MESSAGE_CONSOLE_INDEX ] ).gainInputFocus();
    }

}
