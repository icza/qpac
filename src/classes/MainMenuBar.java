
package classes;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;


/**
    The main menu bar of the program.
    @author Belicza Andras
*/
class MainMenuBar extends JMenuBar implements ActionListener {

    /** Name of the menu options file. */
    private final String MENU_OPTIONS_FILE_NAME = "menustates.dat";

    /** Indicating menu item separator instead of next menu item. */
    private static final JMenuItem SEPARATOR_MENU_ITEM = null;
    
    /** Index of the Game menu. */
    private static final int GAME_MENU_INDEX                               = 0;
    /** Index of Create menu item. */
    private static final int CREATE_MENU_ITEM_INDEX                        = 1;
    /** Index of Create menu item. */
    private static final int JOIN_MENU_ITEM_INDEX                          = 2;
    /** Index of Create menu item. */
    private static final int CLOSE_MENU_ITEM_INDEX                         = 3;
    /** Index of Exit menu item. */
    private static final int EXIT_MENU_ITEM_INDEX                          = 5;

    /** Index of the Settings menu. */
    public  static final int SETTINGS_MENU_INDEX                           = 1;
    /** Index of the Client options menu item. */
    private static final int CLIENT_OPTIONS_MENU_ITEM_INDEX                = 1;
    /** Index of the Server options menu item. */
    private static final int SERVER_OPTIONS_MENU_ITEM_INDEX                = 2;
    /** Index of the Save options on exit menu item. */
    public  static final int SAVE_OPTIONS_ON_EXIT_MENU_ITEM_INDEX          = 4;
    
    /** Index of the Windows menu. */
    public  static final int WINDOWS_MENU_INDEX                            = 2;
    /** Index of the Message console menu item. */
    public  static final int MESSAGE_CONSOLE_MENU_ITEM_INDEX               = 1;
    /** Index of the Map window menu item. */
    public  static final int MAP_WINDOW_MENU_ITEM_INDEX                    = 2;
    /** Index of the Players window menu item. */
    public  static final int PLAYERS_WINDOW_MENU_ITEM_INDEX                = 3;
    /** Index of Clear messages menu item. */
    private static final int CLEAR_MESSAGES_MENU_ITEM_INDEX                = 5;
    /** Index of Line wrap in message console menu item. */
    public  static final int LINE_WRAP_IN_MESSAGE_CONSOLE_MENU_ITEM_INDEX  = 6;
    /** Index of Pack windows menu item. */
    private static final int PACK_WINDOWS_MENU_ITEM_INDEX                  = 8;
    /** Index of the Restore default positions menu item. */
    private static final int RESTORE_DEFAULT_POSITIONS_MENU_ITEM_INDEX     = 9;
    /** Index of the Save window positions on exit menu item name. */
    public  static final int SAVE_WINDOW_POSITIONS_ON_EXIT_MENU_ITEM_INDEX = 10;

    /** Index of the Windows menu. */
    private static final int HELP_MENU_INDEX                               = 3;
    /** Index of the Control keys menu item. */
    private static final int CONTROL_KEYS_MENU_ITEM_INDEX                  = 1;
    /** Index of the Tips menu item. */
    private static final int TIPS_MENU_ITEM_INDEX                          = 2;
    /** Index of the faq menu item. */
    private static final int FAQ_MENU_ITEM_INDEX                           = 3;
    /** Index of the show my host name and ip menu item. */
    private static final int SHOW_MY_HOST_NAME_AND_IP_MENU_ITEM_INDEX      = 5;
    /** Index of the About menu item. */
    private static final int ABOUT_MENU_ITEM_INDEX                         = 7;

    /** The menus and the menu items. */
    private static final JMenuItem[][] MENUS_AND_MENU_ITEMS                   = { { new JMenu( "Game" ), new JMenuItem( "Create" ), new JMenuItem( "Join" ), new JMenuItem( "Close" ), SEPARATOR_MENU_ITEM, new JMenuItem( "Exit" ) }, { new JMenu( "Settings" ), new JMenuItem( "Client options" ), new JMenuItem( "Server options" ), SEPARATOR_MENU_ITEM, new JCheckBoxMenuItem( "Save options on exit", true ) }, { new JMenu( "Windows" ), new JCheckBoxMenuItem( "1 Message console", true ), new JCheckBoxMenuItem( "2 Map window", true ), new JCheckBoxMenuItem( "3 Players window", true ), SEPARATOR_MENU_ITEM, new JMenuItem( "Clear messages" ), new JCheckBoxMenuItem( "Line wrap in message console", true ), SEPARATOR_MENU_ITEM, new JMenuItem( "Pack windows" ), new JMenuItem( "Restore default positions" ), new JCheckBoxMenuItem( "Save window positions on exit", true ) }, { new JMenu( "Help" ), new JMenuItem( "Control keys" ), new JMenuItem( "Tips" ), new JMenuItem( "Faq" ), SEPARATOR_MENU_ITEM, new JMenuItem( "Show my host name and ip" ), SEPARATOR_MENU_ITEM, new JMenuItem( "About" ) } };
    /** Character positions in the name of the menus and menu items which will be the mnemonic key to acces the menu. */
    private static final int      [][] MENU_ITEM_MNEMONIC_CHARACTER_POSITIONS = { {             0      ,                 0        ,                 0      ,                  1      , -1                 ,                  1      }, {             0          ,                 0                ,                 0                , -1                 ,                                          17           }, {             0         ,                         0                        ,                          0                    ,                         0                        , -1                 ,                  0                ,                         0                                   , -1                 ,                 0              ,                 0                           ,                                                   26           }, {             0      ,                 0              ,                 0      ,                 0     , -1                 ,                 0                          , -1                 ,                 0        } };
    /** Not connected game status. */
    public  static final int           GAME_STATUS_NOT_CONNECTED              = 0;
    /** Connected game status. */
    public  static final int           GAME_STATUS_CONNECTED                  = 1;
    /** Game status where disabled all game activity for some kind of error in. */
    public  static final int           GAME_STATUS_DISABLED_ALL_GAME_ACTIVITY = 2;

    /** Reference to the main frame. */
    private final MainFrame mainFrame;

    /**
        Creates a new MainMenuBar.
        @param mainFrame reference to the main frame
    */
    MainMenuBar( final MainFrame mainFrame ) {
        this.mainFrame = mainFrame;
        buildGUI();
    }

    /**
        Builds the graphical user interface of the menubar.
    */
    private void buildGUI() {
        for ( int menuIndex = 0; menuIndex < MENUS_AND_MENU_ITEMS.length; menuIndex++ ) {
            final JMenu menu = (JMenu) MENUS_AND_MENU_ITEMS[ menuIndex ][ 0 ];
            menu.setMnemonic( menu.getText().charAt( MENU_ITEM_MNEMONIC_CHARACTER_POSITIONS[ menuIndex ][ 0 ] ) );
            for ( int menuItemIndex = 1; menuItemIndex < MENUS_AND_MENU_ITEMS[ menuIndex ].length; menuItemIndex++ ) {
                final JMenuItem menuItem = MENUS_AND_MENU_ITEMS[ menuIndex ][ menuItemIndex ];
                if ( menuItem == SEPARATOR_MENU_ITEM )
                    menu.addSeparator();
                else {
                    menuItem.addActionListener( this );
                    menuItem.setMnemonic( menuItem.getText().charAt( MENU_ITEM_MNEMONIC_CHARACTER_POSITIONS[ menuIndex ][ menuItemIndex ] ) );
                    menu.add( menuItem );
                }
            }
            add( menu );
        }
    }

    /**
        Handling action events of the menu items.
        @param ae details of the action event
    */
    public void actionPerformed( final ActionEvent ae ) {
        final JMenuItem source = (JMenuItem) ae.getSource();

        if ( source == MENUS_AND_MENU_ITEMS[ GAME_MENU_INDEX ][ CREATE_MENU_ITEM_INDEX ] )
            mainFrame.createGame();
        else if ( source == MENUS_AND_MENU_ITEMS[ GAME_MENU_INDEX ][ JOIN_MENU_ITEM_INDEX ] )
            mainFrame.joinGame();
        else if ( source == MENUS_AND_MENU_ITEMS[ GAME_MENU_INDEX ][ CLOSE_MENU_ITEM_INDEX ] )
            mainFrame.closeGame();
        else if ( source == MENUS_AND_MENU_ITEMS[ GAME_MENU_INDEX ][ EXIT_MENU_ITEM_INDEX ] )
            mainFrame.exitGame();

        else if ( source == MENUS_AND_MENU_ITEMS[ SETTINGS_MENU_INDEX ][ CLIENT_OPTIONS_MENU_ITEM_INDEX ] )
            mainFrame.showClientOptionsDialog();
        else if ( source == MENUS_AND_MENU_ITEMS[ SETTINGS_MENU_INDEX ][ SERVER_OPTIONS_MENU_ITEM_INDEX ] )
            mainFrame.showServerOptionsDialog();

        else if ( source == MENUS_AND_MENU_ITEMS[ WINDOWS_MENU_INDEX ][ MESSAGE_CONSOLE_MENU_ITEM_INDEX ] )
            mainFrame.checkWindowVisibility( MainFrame.MESSAGE_CONSOLE_INDEX );
        else if ( source == MENUS_AND_MENU_ITEMS[ WINDOWS_MENU_INDEX ][ MAP_WINDOW_MENU_ITEM_INDEX ] )
            mainFrame.checkWindowVisibility( MainFrame.MAP_WINDOW_INDEX );
        else if ( source == MENUS_AND_MENU_ITEMS[ WINDOWS_MENU_INDEX ][ PLAYERS_WINDOW_MENU_ITEM_INDEX ] )
            mainFrame.checkWindowVisibility( MainFrame.PLAYERS_WINDOW_INDEX );

        else if ( source == MENUS_AND_MENU_ITEMS[ WINDOWS_MENU_INDEX ][ CLEAR_MESSAGES_MENU_ITEM_INDEX ] )
            mainFrame.clearMessageConsole();
        else if ( source == MENUS_AND_MENU_ITEMS[ WINDOWS_MENU_INDEX ][ LINE_WRAP_IN_MESSAGE_CONSOLE_MENU_ITEM_INDEX ] )
            mainFrame.checkMessageConsoleLineWrap();
        else if ( source == MENUS_AND_MENU_ITEMS[ WINDOWS_MENU_INDEX ][ PACK_WINDOWS_MENU_ITEM_INDEX ] )
            mainFrame.packWindows();
        else if ( source == MENUS_AND_MENU_ITEMS[ WINDOWS_MENU_INDEX ][ RESTORE_DEFAULT_POSITIONS_MENU_ITEM_INDEX ] )
            mainFrame.restoreDefaultWindowPositions();
            
        else if ( source == MENUS_AND_MENU_ITEMS[ HELP_MENU_INDEX ][ CONTROL_KEYS_MENU_ITEM_INDEX ] )
            mainFrame.showControlKeysHelp();
        else if ( source == MENUS_AND_MENU_ITEMS[ HELP_MENU_INDEX ][ TIPS_MENU_ITEM_INDEX ] )
            mainFrame.showTips();
        else if ( source == MENUS_AND_MENU_ITEMS[ HELP_MENU_INDEX ][ FAQ_MENU_ITEM_INDEX ] )
            mainFrame.showFaq();
        else if ( source == MENUS_AND_MENU_ITEMS[ HELP_MENU_INDEX ][ SHOW_MY_HOST_NAME_AND_IP_MENU_ITEM_INDEX ] )
            mainFrame.showHostNameAndIp();
        else if ( source == MENUS_AND_MENU_ITEMS[ HELP_MENU_INDEX ][ ABOUT_MENU_ITEM_INDEX ] )
            mainFrame.showAboutInformations();
    }
    
    /**
        Sets the game status.
        @param gameStatus the new game status
    */
    public void setGameStatus( final int gameStatus ) {
        boolean connected;
        switch ( gameStatus ) {
            default:
            case GAME_STATUS_NOT_CONNECTED :
                connected = false;
                break;
            case GAME_STATUS_CONNECTED :
                connected = true;
                break;
        }
        MENUS_AND_MENU_ITEMS[ GAME_MENU_INDEX ][ CREATE_MENU_ITEM_INDEX ].setEnabled( !connected && gameStatus != GAME_STATUS_DISABLED_ALL_GAME_ACTIVITY );
        MENUS_AND_MENU_ITEMS[ GAME_MENU_INDEX ][ JOIN_MENU_ITEM_INDEX   ].setEnabled( !connected && gameStatus != GAME_STATUS_DISABLED_ALL_GAME_ACTIVITY );
        MENUS_AND_MENU_ITEMS[ GAME_MENU_INDEX ][ CLOSE_MENU_ITEM_INDEX  ].setEnabled(  connected && gameStatus != GAME_STATUS_DISABLED_ALL_GAME_ACTIVITY );
    }
    
    /**
        Returns the state of a check box menu item.
        @param menuIndex index of menu where the requested menu item is
        @param menuItemIndex index of the requested menu item
        @return state of the specified check box menu item
    */
    public boolean getCheckBoxMenuItemState( final int menuIndex, final int menuItemIndex ) {
        return ( (JCheckBoxMenuItem) MENUS_AND_MENU_ITEMS[ menuIndex ][ menuItemIndex ] ).getState();
    }

    /**
        Clears the specified check box menu item (by setting its state to false).
        @param menuIndex index of menu where the requested menu item is
        @param menuItemIndex index of the requested menu item
    */
    public void clearCheckBoxMenuItem( final int menuIndex, final int menuItemIndex ) {
        ( (JCheckBoxMenuItem) MENUS_AND_MENU_ITEMS[ menuIndex ][ menuItemIndex ] ).setState( false );
    }

    /**
        Saves the states of the menus (the checkbox menu items').
        @return true if saving was successful; false otherwise
    */
    public boolean saveMenuStates() {
        try {
            final ObjectOutputStream file = new ObjectOutputStream( new FileOutputStream( GeneralConsts.OPTIONS_DIRECTORY + MENU_OPTIONS_FILE_NAME ) );
            for ( int menuIndex = 0; menuIndex < MENUS_AND_MENU_ITEMS.length; menuIndex++ )
                for ( int menuItemIndex = 0; menuItemIndex < MENUS_AND_MENU_ITEMS[ menuIndex ].length; menuItemIndex++ )
                    if ( MENUS_AND_MENU_ITEMS[ menuIndex ][ menuItemIndex ] instanceof JCheckBoxMenuItem )
                        file.writeObject( new Boolean( ( (JCheckBoxMenuItem) MENUS_AND_MENU_ITEMS[ menuIndex ][ menuItemIndex ] ).getState() ) );
            file.close();
        }
        catch ( Exception e ) {
            return false;
        }
        return true;
    }

    /**
        Loads the states of the menus (the checkbox menu items').
        @return true if loading was successful; false otherwise
    */
    public boolean loadMenuStates() {
        try {
            final ObjectInputStream file = new ObjectInputStream( new FileInputStream( GeneralConsts.OPTIONS_DIRECTORY + MENU_OPTIONS_FILE_NAME ) );
            for ( int menuIndex = 0; menuIndex < MENUS_AND_MENU_ITEMS.length; menuIndex++ )
                for ( int menuItemIndex = 0; menuItemIndex < MENUS_AND_MENU_ITEMS[ menuIndex ].length; menuItemIndex++ )
                    if ( MENUS_AND_MENU_ITEMS[ menuIndex ][ menuItemIndex ] instanceof JCheckBoxMenuItem )
                        ( (JCheckBoxMenuItem) MENUS_AND_MENU_ITEMS[ menuIndex ][ menuItemIndex ] ).setState( ( (Boolean) file.readObject() ).booleanValue() );  
            file.close();
        }
        catch ( Exception e ) {
            return false;
        }
        return true;
    }

}
