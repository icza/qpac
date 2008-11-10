
package classes.options;

import java.awt.*;
import javax.swing.*;
import classes.*;


/**
    Variable options of the client.
    @author Belicza Andras
*/
public class ClientOptions extends Options {

    /** Name of file to save/load client options. */
    private static final String CLIENT_OPTIONS_FILE_NAME         = "clientoptions.dat";
    /** Forbidden characters what can't be part of names of players (because message console uses them or they are part of the server chat name or they are separator of command arguments). */
    private static final char[] FORBIDDEN_PLAYER_NAME_CHARACTERS = { ':', '<', '>', '|' };
    
    /** Names of the possible worm colors. */
    public  static final String[] WORM_COLOR_NAMES               = { "Red"                 , "Green"               , "White"                   , "Yellow"                , "Teal"                  , "Purple"                 };
    /** Possible colors of the worm. */
    public  static final Color [] WORM_COLORS                    = { new Color( 250, 0, 0 ), new Color( 0, 250, 0 ), new Color( 250, 250, 250 ), new Color( 250, 250, 0 ), new Color( 0, 250, 250 ), new Color( 250, 0, 250 ) };

    /** Minimal value of period time of reexecutioning of operate() method of the client side game handler. */
    private static final long    MIN_PERIOD_TIME                                 =  20;
    /** Default value of period time of reexecutioning of operate() method of the client side game handler. */
    private static final long    DEFAULT_PERIOD_TIME                             =  50;
    /** Maximal value of period time of reexecutioning of operate() method of the client side game handler. */
    private static final long    MAX_PERIOD_TIME                                 = 500;
    /** Default value of the server host. */
    private static final String  DEFAULT_SERVER_HOST                             = "";
    /** Default value of index of the worm's color. */
    private static final int     DEFAULT_WORM_COLOR_INDEX                        = 0;
    /** Default value of the name of the player. */
    private static final String  DEFAULT_PLAYER_NAME                             = "anonymous";
    /** Default value to show the names of the players. */
    private static final boolean DEFAULT_SHOW_PLAYER_NAMES                       = true;
    /** Default value to show the selected weapons. */
    private static final boolean DEFAULT_SHOW_SELECTED_WEAPONS                   = true;
    /** Default value to show the hit points of the worms. */
    private static final boolean DEFAULT_SHOW_WORM_HIT_POINTS                    = true;
    /** Default value to show the reloading times of the weapon of the worms. */
    private static final boolean DEFAULT_SHOW_WORM_WEAPON_RELOADING_TIMES        = true;
    /** Default value to show the levels of the oxygen bottle of the worms. */
    private static final boolean DEFAULT_SHOW_WORM_OXYGEN_BOTTLE_LEVELS          = true;
    /** Minimum value of download map datas at cycles count. */
    private static final int     MIN_DOWNLOAD_MAP_DATAS_AT_CYCLES_COUNT          =  1;
    /** Default value of download map datas at cycles count. */
    private static final int     DEFAULT_DOWNLOAD_MAP_DATAS_AT_CYCLES_COUNT      =  2;
    /** Maximum value of download map datas at cycles count. */
    private static final int     MAX_DOWNLOAD_MAP_DATAS_AT_CYCLES_COUNT          = 20;
    /** Default value of refresh map component at map datas downloading cycles count. */
    private static final int     MIN_REFRESH_MAP_COMPONENT_AT_MAP_DATAS_DOWNLOADING_CYCLES_COUNT     =  1;
    /** Default value of refresh map component at map datas downloading cycles count. */
    private static final int     DEFAULT_REFRESH_MAP_COMPONENT_AT_MAP_DATAS_DOWNLOADING_CYCLES_COUNT =  2;
    /** Default value of refresh map component at map datas downloading cycles count. */
    private static final int     MAX_REFRESH_MAP_COMPONENT_AT_MAP_DATAS_DOWNLOADING_CYCLES_COUNT     = 20;
    /** Default value of map zooming factor. */
    public  static final int     MIN_MAP_ZOOMING_FACTOR                          = 1;
    /** Default value of map zooming factor. */
    private static final int     DEFAULT_MAP_ZOOMING_FACTOR                      = 5;
    /** Default value of map zooming factor. */
    public  static final int     MAX_MAP_ZOOMING_FACTOR                          = GeneralConsts.WALL_HEIGHT / 2;
    /** Default value of clear message console at new game. */
    private static final boolean DEFAULT_CLEAR_MESSAGE_CONSOLE_AT_NEW_GAME       = true;
    /** Default value of execute getserveroptions command at new game. */
    private static final boolean DEFAULT_EXECUTE_GET_SERVER_OPTIONS_COMMAND_AT_NEW_GAME = true;
    /** Minimum value of refresh players window at cycles count. */
    private static final int     MIN_REFRESH_PLAYERS_WINDOW_AT_CYCLES_COUNT      =   1;
    /** Default value of refresh players window at cycles count. */
    private static final int     DEFAULT_REFRESH_PLAYERS_WINDOW_AT_CYCLES_COUNT  =  20;
    /** Max value of refresh players window at cycles count. */
    private static final int     MAX_REFRESH_PLAYERS_WINDOW_AT_CYCLES_COUNT      = 100;
    /** Default value of fit player table to players window. */
    private static final boolean DEFAULT_FIT_PLAYER_TABLE_TO_PLAYERS_WINDOW      = true;
    /** Default value of the password. */
    private static final String  DEFAULT_PASSWORD                                = "";
    /** Default value of turn off double buffering graphics technique. */
    private static final boolean DEFAULT_TURN_OFF_DOUBLE_BUFFERING_GRAPHICS_TECHNIQUE = false;
    /** Default value of do not download decorations. */
    private static final boolean DEFAULT_DO_NOT_DOWNLOAD_DECORATIONS             = false;
    /** Default value of show rubbles and blood in double size. */
    private static final boolean DEFAULT_SHOW_RUBBLES_AND_BLOOD_IN_DOUBLE_SIZE   = true;

    /** Period time of reexecutioning of operate() method of the client side game handler. */
    public long                  periodTime;
    /** Component to view/change the value of period time. */
    private transient JSpinner   periodTimeComponent;
    /** Host name or IP address of the server. */
    public String                serverHost;
    /** Component to view/change the value of server host. */
    private transient JTextField serverHostComponent;
    /** Color of the client's worm. */
    public int                   wormColorIndex;
    /** Component to view/change the value of color index of the worm. */
    private transient JComboBox  wormColorIndexComponent;
    /** Name of the player. */
    public String                playerName;
    /** Component to view/change the value of player name. */
    private transient JTextField playerNameComponent;
    /** Tells whether have to show the names of the players. */
    public boolean               showPlayerNames;
    /** Component to view/change the value to show the names of the players. */
    private transient JCheckBox  showPlayerNamesComponent;
    /** Tells whether have to show the selected weapons of the players. */
    public boolean               showSelectedWeapons;
    /** Component to view/change the value of show selected weapons. */
    private transient JCheckBox  showSelectedWeaponsComponent;
    /** Tells whether have to show the hit points of the worms. */
    public boolean               showWormHitPoints;
    /** Component to view/change the value to show the hit points of the worm. */
    private transient JCheckBox  showWormHitPointsComponent;
    /** Tells whether have to show the reloading times of the weapon of the worms. */
    public boolean               showWormWeaponReloadingTimes;
    /** Component to view/change the value to show the reloading times of the weapon of the worm. */
    private transient JCheckBox  showWormWeaponReloadingTimesComponent;
    /** Tells whether have to show the levels of the oxygen bottle of the worms. */
    public boolean               showWormOxygenBottleLevels;
    /** Component to view/change the value to show the levels of the oxygen bottle of the worm. */
    private transient JCheckBox  showWormOxygenBottleLevelsComponent;
    /** Downloading map datas only at cycles count multiple of this. */
    public int                   downloadMapDatasAtCyclesCount;
    /** Component to view/change the value of download map datas at cycles count. */
    private transient JSpinner   downloadMapDatasAtCyclesCountComponent;
    /** Refresh map component only at map datas downloading cycles count multiple of this. */
    public int                   refreshMapComponentAtMapDatasDownloadingCyclesCount;
    /** Component to view/change the value of refresh map component at map datas downloading cycles count. */
    private transient JSpinner   refreshMapComponentAtMapDatasDownloadingCyclesCountComponent;
    /** Zooming factor of the map (at drawing). */
    public int                   mapZoomingFactor;
    /** Component to view/change the value of map zooming factor. */
    private transient JSpinner   mapZoomingFactorComponent;
    /** Tells whether have to clear message console at new games. */
    public boolean               clearMessageConsoleAtNewGame;
    /** Component to view/change the value of clear message console at new game. */
    private transient JCheckBox  clearMessageConsoleAtNewGameComponent;
    /** Tells whether have to execute getserveroptions command at new game. */
    public boolean               executeGetServerOptionsCommandAtNewGame;
    /** Component to view/change the value of execute getserveroptions command at new game. */
    private transient JCheckBox  executeGetServerOptionsCommandAtNewGameComponent;
    /** Refresh players window only in cycles count multiple of this. */
    public int                   refreshPlayersWindowAtCyclesCount;
    /** Component to view/change the value of refresh players window in cycles count component. */
    private transient JSpinner   refreshPlayersWindowAtCyclesCountComponent;
    /** Tells whether player table have to fit to the players window. */
    public boolean               fitPlayerTableToPlayersWindow;
    /** Component to view/change the value of fit player table to players window. */
    private transient JCheckBox  fitPlayerTableToPlayersWindowComponent;
    /** Game password. */
    public String                password;
    /** Component to view/change the value of password. */
    private transient JTextField passwordComponent;
    /** Tells whether have to turn off double buffering technique. */
    public boolean               turnOffDoubleBufferingGraphicsTechnique;
    /** Component to view/change the value of turn off double buffering technique. */
    private transient JCheckBox  turnOffDoubleBufferingGraphicsTechniqueComponent;
    /** Tells whether don't have to download decorations. */
    public boolean               doNotDownloadDecorations;
    /** Component to view/change the value of do not download decorations. */
    private transient JCheckBox  doNotDownloadDecorationsComponent;
    /** Tells whether have to show rubbles and blood in double size. */
    public boolean               showRubblesAndBloodInDoubleSize;
    /** Component to view/change the value of show rubbles and blood in double size. */
    private transient JCheckBox  showRubblesAndBloodInDoubleSizeComponent;
    
    /**
        Creates a new ClientOptions.
        @param mainFrame reference to the main frame
    */
    public ClientOptions( final Frame mainFrame ) {
        super( mainFrame );
    }

    /**
        Returns the file name where client options will be saved to/loaded from.
        @return file name of the client options
    */
    protected String getOptionsFileName() {
        return CLIENT_OPTIONS_FILE_NAME;
    }

    /**
        Loads the options.
        @param mainFrame reference to the main frame
        @return the loaded serverOptions, or null, if error occured
    */
    public static ClientOptions loadOptions( final Frame mainFrame ) {
        return (ClientOptions) loadOptions( CLIENT_OPTIONS_FILE_NAME, mainFrame );
    }
    
    /**
        Creates the settings components of the options.
    */
    protected void createSettingsComponents() {
        periodTimeComponent                              = new JSpinner( new SpinnerNumberModel( DEFAULT_PERIOD_TIME, MIN_PERIOD_TIME, MAX_PERIOD_TIME, 1 ) );
        serverHostComponent                              = new JTextField( 10 );
        wormColorIndexComponent                          = new JComboBox( WORM_COLOR_NAMES );
        playerNameComponent                              = new JTextField( 10 );
        showPlayerNamesComponent                         = new JCheckBox( "name of players" );
        showSelectedWeaponsComponent                     = new JCheckBox( "selected weapons" );
        showWormHitPointsComponent                       = new JCheckBox( "hit point of the worms" );
        showWormWeaponReloadingTimesComponent            = new JCheckBox( "reloading time of the weapons" );
        showWormOxygenBottleLevelsComponent              = new JCheckBox( "level of the oxygen bottles" );
        downloadMapDatasAtCyclesCountComponent           = new JSpinner( new SpinnerNumberModel( DEFAULT_DOWNLOAD_MAP_DATAS_AT_CYCLES_COUNT, MIN_DOWNLOAD_MAP_DATAS_AT_CYCLES_COUNT, MAX_DOWNLOAD_MAP_DATAS_AT_CYCLES_COUNT, 1 ) );
        refreshMapComponentAtMapDatasDownloadingCyclesCountComponent = new JSpinner( new SpinnerNumberModel( DEFAULT_REFRESH_MAP_COMPONENT_AT_MAP_DATAS_DOWNLOADING_CYCLES_COUNT, MIN_REFRESH_MAP_COMPONENT_AT_MAP_DATAS_DOWNLOADING_CYCLES_COUNT, MAX_REFRESH_MAP_COMPONENT_AT_MAP_DATAS_DOWNLOADING_CYCLES_COUNT, 1 ) );
        mapZoomingFactorComponent                        = new JSpinner( new SpinnerNumberModel( DEFAULT_MAP_ZOOMING_FACTOR, MIN_MAP_ZOOMING_FACTOR, MAX_MAP_ZOOMING_FACTOR, 1 ) );
        clearMessageConsoleAtNewGameComponent            = new JCheckBox( "Clear message console at new game" );
        executeGetServerOptionsCommandAtNewGameComponent = new JCheckBox( "Execute '/getserveroptions' command at new games" );
        refreshPlayersWindowAtCyclesCountComponent       = new JSpinner( new SpinnerNumberModel( DEFAULT_REFRESH_PLAYERS_WINDOW_AT_CYCLES_COUNT, MIN_REFRESH_PLAYERS_WINDOW_AT_CYCLES_COUNT, MAX_REFRESH_PLAYERS_WINDOW_AT_CYCLES_COUNT, 1 ) );
        fitPlayerTableToPlayersWindowComponent           = new JCheckBox( "Fit player table to players window" );
        passwordComponent                                = new JTextField( 10 );
        turnOffDoubleBufferingGraphicsTechniqueComponent = new JCheckBox( "Turn off double buffering graphics technique" );
        doNotDownloadDecorationsComponent                = new JCheckBox( "Do not download decorations" );
        showRubblesAndBloodInDoubleSizeComponent         = new JCheckBox( "Show rubbles and blood in double size" );
    }

    /**
        Creates and returns a panel with components to change the values of the client options.
        @return a settings tabbed pane
    */
    protected JTabbedPane createSettingsTabbedPane() {
        JTabbedPane settingsTabbedPane = new JTabbedPane();
        JPanel borderPanel, borderPanel2, panel, tabPanel;
        tabPanel = new JPanel();
        borderPanel = new JPanel( new BorderLayout() );
            panel = new JPanel();
                panel.add( new JLabel( "Server host name or IP:" ) );
                panel.add( serverHostComponent );
            borderPanel.add( panel, BorderLayout.NORTH );
            panel = new JPanel();
                panel.add( new JLabel( "Player name:" ) );
                panel.add( playerNameComponent );
            borderPanel.add( panel, BorderLayout.CENTER );
            borderPanel2 = new JPanel( new BorderLayout() );
                panel = new JPanel();
                    panel.add( new JLabel( "Password:" ) );
                    panel.add( passwordComponent );
                borderPanel2.add( panel, BorderLayout.NORTH );
                panel = new JPanel();
                    panel.add( new JLabel( "Choose a color for your worm:" ) );
                    panel.add( wormColorIndexComponent );
                borderPanel2.add( panel, BorderLayout.CENTER );
            borderPanel.add( borderPanel2, BorderLayout.SOUTH );
        tabPanel.add( borderPanel );
        settingsTabbedPane.addTab( "General", tabPanel );
        tabPanel = new JPanel();
        borderPanel = new JPanel( new BorderLayout() );
            panel = new JPanel();
                panel.add( showPlayerNamesComponent );
            borderPanel.add( panel, BorderLayout.NORTH );
            panel = new JPanel();
                panel.add( showSelectedWeaponsComponent );
            borderPanel.add( panel, BorderLayout.CENTER );
            borderPanel2 = new JPanel( new BorderLayout() );
                panel = new JPanel();
                    panel.add( showWormHitPointsComponent );
                borderPanel2.add( panel, BorderLayout.NORTH );
                panel = new JPanel();
                    panel.add( showWormWeaponReloadingTimesComponent );
                borderPanel2.add( panel, BorderLayout.CENTER );
                panel = new JPanel();
                    panel.add( showWormOxygenBottleLevelsComponent );
                borderPanel2.add( panel, BorderLayout.SOUTH );
            borderPanel.add( borderPanel2, BorderLayout.SOUTH );
        tabPanel.add( borderPanel );
        settingsTabbedPane.addTab( "What to show", tabPanel );
        tabPanel = new JPanel();
        borderPanel = new JPanel( new BorderLayout() );
            panel = new JPanel();
                panel.add( new JLabel( "Time between scene refreshes:" ) );
                panel.add( periodTimeComponent );
                panel.add( new JLabel( "ms." ) );
            borderPanel.add( panel, BorderLayout.NORTH );
            panel = new JPanel();
                panel.add( new JLabel( "Download map datas in every" ) );
                panel.add( downloadMapDatasAtCyclesCountComponent );
                panel.add( new JLabel( "scene refresh cycle(s)." ) );
            borderPanel.add( panel, BorderLayout.CENTER );
            borderPanel2 = new JPanel( new BorderLayout() );
                panel = new JPanel();
                    panel.add( new JLabel( "Refresh map in every" ) );
                    panel.add( refreshMapComponentAtMapDatasDownloadingCyclesCountComponent );
                    panel.add( new JLabel( "map downloading cycle(s)." ) );
                borderPanel2.add( panel, BorderLayout.NORTH );
                panel = new JPanel();
                    panel.add( new JLabel( "Refresh players window in every" ) );
                    panel.add( refreshPlayersWindowAtCyclesCountComponent );
                    panel.add( new JLabel( "scene refresh cycle(s)." ) );
                borderPanel2.add( panel, BorderLayout.CENTER );
            borderPanel.add( borderPanel2, BorderLayout.SOUTH );
        tabPanel.add( borderPanel );
        settingsTabbedPane.addTab( "Timings", tabPanel );
        tabPanel = new JPanel();
        borderPanel = new JPanel( new BorderLayout() );
            panel = new JPanel();
                panel.add( clearMessageConsoleAtNewGameComponent );
            borderPanel.add( panel, BorderLayout.NORTH );
            panel = new JPanel();
                panel.add( executeGetServerOptionsCommandAtNewGameComponent );
            borderPanel.add( panel, BorderLayout.CENTER );
            borderPanel2 = new JPanel( new BorderLayout() );
                panel = new JPanel();
                    panel.add( new JLabel( "Map zooming factor:" ) );
                    panel.add( mapZoomingFactorComponent );
                borderPanel2.add( panel, BorderLayout.NORTH );
                panel = new JPanel();
                    panel.add( fitPlayerTableToPlayersWindowComponent );
                borderPanel2.add( panel, BorderLayout.CENTER );
            borderPanel.add( borderPanel2, BorderLayout.SOUTH );
        tabPanel.add( borderPanel );
        settingsTabbedPane.addTab( "Others", tabPanel );
        tabPanel = new JPanel();
        borderPanel = new JPanel( new BorderLayout() );
            panel = new JPanel();
                panel.add( turnOffDoubleBufferingGraphicsTechniqueComponent );
            borderPanel.add( panel, BorderLayout.NORTH );
            panel = new JPanel();
                panel.add( doNotDownloadDecorationsComponent );
            borderPanel.add( panel, BorderLayout.CENTER );
            panel = new JPanel();
                panel.add( showRubblesAndBloodInDoubleSizeComponent );
            borderPanel.add( panel, BorderLayout.SOUTH );
        tabPanel.add( borderPanel);
        settingsTabbedPane.addTab( "Extra", tabPanel );
        for ( int tabCounter = settingsTabbedPane.getTabCount() - 1; tabCounter >= 0; tabCounter-- )
            settingsTabbedPane.setMnemonicAt( tabCounter, settingsTabbedPane.getTitleAt( tabCounter ).charAt( 0 ) );
        return settingsTabbedPane;
    }

    /**
        Returns the title of the client settings dialog.
        @return title of the settings dialog
    */
    protected String getSettingsDialogTitle() {
        return "Client options";
    }

    /**
        Synchronizes the client options to the actual values of the option-components.
    */
    protected void synchronizeOptionsToComponents() {
        periodTime                              = (long) Double.parseDouble( periodTimeComponent.getValue().toString() );
        serverHost                              = serverHostComponent.getText();
        wormColorIndex                          = wormColorIndexComponent.getSelectedIndex();
        String correctedPlayerName              = playerNameComponent.getText();
        for ( int forbiddenPlayerNameCharacterIndex = FORBIDDEN_PLAYER_NAME_CHARACTERS.length - 1; forbiddenPlayerNameCharacterIndex >= 0; forbiddenPlayerNameCharacterIndex-- )
            correctedPlayerName = correctedPlayerName.replace( FORBIDDEN_PLAYER_NAME_CHARACTERS[ forbiddenPlayerNameCharacterIndex ], '_' );
        playerName                              = correctedPlayerName;
        playerNameComponent.setText( playerName );
        showPlayerNames                         = showPlayerNamesComponent.isSelected();
        showSelectedWeapons                     = showSelectedWeaponsComponent.isSelected();
        showWormHitPoints                       = showWormHitPointsComponent.isSelected();
        showWormWeaponReloadingTimes            = showWormWeaponReloadingTimesComponent.isSelected();
        showWormOxygenBottleLevels              = showWormOxygenBottleLevelsComponent.isSelected();
        downloadMapDatasAtCyclesCount           = (int) Double.parseDouble( downloadMapDatasAtCyclesCountComponent.getValue().toString() );
        refreshMapComponentAtMapDatasDownloadingCyclesCount = (int) Double.parseDouble( refreshMapComponentAtMapDatasDownloadingCyclesCountComponent.getValue().toString() );
        mapZoomingFactor                        = (int) Double.parseDouble( mapZoomingFactorComponent.getValue().toString() );
        clearMessageConsoleAtNewGame            = clearMessageConsoleAtNewGameComponent.isSelected();
        executeGetServerOptionsCommandAtNewGame = executeGetServerOptionsCommandAtNewGameComponent.isSelected();
        refreshPlayersWindowAtCyclesCount       = (int) Double.parseDouble( refreshPlayersWindowAtCyclesCountComponent.getValue().toString() );
        fitPlayerTableToPlayersWindow           = fitPlayerTableToPlayersWindowComponent.isSelected();
        password                                = passwordComponent.getText();
        turnOffDoubleBufferingGraphicsTechnique = turnOffDoubleBufferingGraphicsTechniqueComponent.isSelected();
        doNotDownloadDecorations                = doNotDownloadDecorationsComponent.isSelected();
        showRubblesAndBloodInDoubleSize         = showRubblesAndBloodInDoubleSizeComponent.isSelected();
    }

    /**
        Synchronizes the option-components to the client options.
    */
    protected void synchronizeComponentsToOptions() {
        periodTimeComponent.setValue( new Long( periodTime ) );
        serverHostComponent.setText( serverHost );
        wormColorIndexComponent.setSelectedIndex( wormColorIndex );
        playerNameComponent.setText( playerName );
        showPlayerNamesComponent.setSelected( showPlayerNames );
        showSelectedWeaponsComponent.setSelected( showSelectedWeapons );
        showWormHitPointsComponent.setSelected( showWormHitPoints );
        showWormWeaponReloadingTimesComponent.setSelected( showWormWeaponReloadingTimes );
        showWormOxygenBottleLevelsComponent.setSelected( showWormOxygenBottleLevels );
        downloadMapDatasAtCyclesCountComponent.setValue( new Integer( downloadMapDatasAtCyclesCount ) );
        refreshMapComponentAtMapDatasDownloadingCyclesCountComponent.setValue( new Integer( refreshMapComponentAtMapDatasDownloadingCyclesCount ) );
        mapZoomingFactorComponent.setValue( new Integer( mapZoomingFactor ) );
        clearMessageConsoleAtNewGameComponent.setSelected( clearMessageConsoleAtNewGame );
        executeGetServerOptionsCommandAtNewGameComponent.setSelected( executeGetServerOptionsCommandAtNewGame );
        refreshPlayersWindowAtCyclesCountComponent.setValue( new Integer( refreshPlayersWindowAtCyclesCount ) );
        fitPlayerTableToPlayersWindowComponent.setSelected( fitPlayerTableToPlayersWindow );
        passwordComponent.setText( password );
        turnOffDoubleBufferingGraphicsTechniqueComponent.setSelected( turnOffDoubleBufferingGraphicsTechnique );
        doNotDownloadDecorationsComponent.setSelected( doNotDownloadDecorations );
        showRubblesAndBloodInDoubleSizeComponent.setSelected( showRubblesAndBloodInDoubleSize );
    }

    /**
        Restores the default values to the option-components.
    */
    protected void restoreDefaultsToComponents() {
        periodTimeComponent.setValue( new Long( DEFAULT_PERIOD_TIME ) );
        serverHostComponent.setText( DEFAULT_SERVER_HOST );
        wormColorIndexComponent.setSelectedIndex( DEFAULT_WORM_COLOR_INDEX );
        playerNameComponent.setText( DEFAULT_PLAYER_NAME );
        showPlayerNamesComponent.setSelected( DEFAULT_SHOW_PLAYER_NAMES );
        showSelectedWeaponsComponent.setSelected( DEFAULT_SHOW_SELECTED_WEAPONS );
        showWormHitPointsComponent.setSelected( DEFAULT_SHOW_WORM_HIT_POINTS );
        showWormWeaponReloadingTimesComponent.setSelected( DEFAULT_SHOW_WORM_WEAPON_RELOADING_TIMES );
        showWormOxygenBottleLevelsComponent.setSelected( DEFAULT_SHOW_WORM_OXYGEN_BOTTLE_LEVELS );
        downloadMapDatasAtCyclesCountComponent.setValue( new Integer( DEFAULT_DOWNLOAD_MAP_DATAS_AT_CYCLES_COUNT ) );
        refreshMapComponentAtMapDatasDownloadingCyclesCountComponent.setValue( new Integer( DEFAULT_REFRESH_MAP_COMPONENT_AT_MAP_DATAS_DOWNLOADING_CYCLES_COUNT ) );
        mapZoomingFactorComponent.setValue( new Integer( DEFAULT_MAP_ZOOMING_FACTOR ) );
        clearMessageConsoleAtNewGameComponent.setSelected( DEFAULT_CLEAR_MESSAGE_CONSOLE_AT_NEW_GAME );
        executeGetServerOptionsCommandAtNewGameComponent.setSelected( DEFAULT_EXECUTE_GET_SERVER_OPTIONS_COMMAND_AT_NEW_GAME );
        refreshPlayersWindowAtCyclesCountComponent.setValue( new Integer( DEFAULT_REFRESH_PLAYERS_WINDOW_AT_CYCLES_COUNT ) );
        fitPlayerTableToPlayersWindowComponent.setSelected( DEFAULT_FIT_PLAYER_TABLE_TO_PLAYERS_WINDOW );
        passwordComponent.setText( DEFAULT_PASSWORD );
        turnOffDoubleBufferingGraphicsTechniqueComponent.setSelected( DEFAULT_TURN_OFF_DOUBLE_BUFFERING_GRAPHICS_TECHNIQUE );
        doNotDownloadDecorationsComponent.setSelected( DEFAULT_DO_NOT_DOWNLOAD_DECORATIONS );
        showRubblesAndBloodInDoubleSizeComponent.setSelected( DEFAULT_SHOW_RUBBLES_AND_BLOOD_IN_DOUBLE_SIZE );
    }

}
