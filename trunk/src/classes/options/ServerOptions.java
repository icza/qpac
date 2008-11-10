
package classes.options;

import javax.swing.*;
import java.awt.*;


/**
    Variable options of the server.
    @author Belicza Andras
*/
public class ServerOptions extends Options {

    /** Name of file to save/load server options. */
    private static final String   SERVER_OPTIONS_FILE_NAME      = "serveroptions.dat";

    /** Constant for free for all game type. */
    public  static final int      GAME_TYPE_FREE_FOR_ALL         = 0;
    /** Constant for team melee game type. */
    public  static final int      GAME_TYPE_TEAM_MELEE           = 1;
    /** Names of the game types for building list of them. */
    private static final String[] GAME_TYPE_NAMES                = { "Free for all", "Team melee" }; 

    /** Minimal value of period time of reexecutioning of operate() method of the server side game handler. */
    private static final long     MIN_PERIOD_TIME                =    5;
    /** Default value of period time of reexecutioning of operate() method of the server side game handler. */
    private static final long     DEFAULT_PERIOD_TIME            =   30;
    /** Maximal value of period time of reexecutioning of operate() method of the server side game handler. */
    private static final long     MAX_PERIOD_TIME                =  100;
    /** Minimal value of maxNumberOfPlayers. */
    private static final int      MIN_MAX_NUMBER_OF_PLAYERS      =    1;
    /** Default value of maxNumberOfPlayers. */
    private static final int      DEFAULT_MAX_NUMBER_OF_PLAYERS  =    3;
    /** Maximal value of maxNumberOfPlayers. */
    private static final int      MAX_MAX_NUMBER_OF_PLAYERS      =   64;
    /**  Minimal value of mapWidth. */
    private static final int      MIN_MAP_WIDTH                  =    8;
    /**  Default value of mapWidth. */
    private static final int      DEFAULT_MAP_WIDTH              =   32;
    /**  Maximal value of mapWidth. */
    private static final int      MAX_MAP_WIDTH                  = 1024;
    /**  Minimal value of mapHeight. */
    private static final int      MIN_MAP_HEIGHT                 =    8;
    /**  Default value of mapHeight. */
    private static final int      DEFAULT_MAP_HEIGHT             =   16;
    /**  Maximal value of mapHeight. */
    private static final int      MAX_MAP_HEIGHT                 = 1024;
    /** Names of the possible map width values. */
    private static final String[] MAP_WIDTH_NAMES;
    /** Default value of the welcome message. */
    private static final String   DEFAULT_WELCOME_MESSAGE        = "\nWelcome!\n";
    /** Default value of game type. */
    private static final int      DEFAULT_GAME_TYPE              = GAME_TYPE_FREE_FOR_ALL;
    /** Default value of is kill limit. */
    private static final boolean  DEFAULT_IS_KILL_LIMIT          = false;
    /** Minimal value of kill limit. */
    private static final int      MIN_KILL_LIMIT                 =    1;
    /** Default value of kill limit. */
    private static final int      DEFAULT_KILL_LIMIT             =   20;
    /** Maximal value of kill limit. */
    private static final int      MAX_KILL_LIMIT                 = 2000;
    /** Default value of is time limit. */
    private static final boolean  DEFAULT_IS_TIME_LIMIT          = false;
    /** Minimal value of time limit (minute). */
    private static final int      MIN_TIME_LIMIT                 =    1;
    /** Default value of time limit (minute). */
    private static final int      DEFAULT_TIME_LIMIT             =   30;
    /** Maximal value of time limit (minute). */
    private static final int      MAX_TIME_LIMIT                 = 2000;
    /** Default value of password. */
    private static final String   DEFAULT_PASSWORD               = "";
    /** Minimal value of amout of wall rubbles. */
    private static final int      MIN_AMOUNT_OF_WALL_RUBBLES     =   0;
    /** Default value of amout of wall rubbles. */
    private static final int      DEFAULT_AMOUNT_OF_WALL_RUBBLES = 100;
    /** Maximal value of amout of wall rubbles. */
    private static final int      MAX_AMOUNT_OF_WALL_RUBBLES     = 800;
    /** Minimal value of amout of blood. */
    private static final int      MIN_AMOUNT_OF_BLOOD            =   0;
    /** Default value of amout of blood. */
    private static final int      DEFAULT_AMOUNT_OF_BLOOD        = 100;
    /** Maximal value of amout of blood. */
    private static final int      MAX_AMOUNT_OF_BLOOD            = 800;
    /** Minimal value of amount of wall. */
    private static final int      MIN_AMOUNT_OF_WALL             =   0;
    /** Default value of amount of wall. */
    private static final int      DEFAULT_AMOUNT_OF_WALL         =  40;
    /** Maximal value of amount of wall. */
    private static final int      MAX_AMOUNT_OF_WALL             = 100;
    /** Minimal value of amount of stone. */
    private static final int      MIN_AMOUNT_OF_STONE            =   0;
    /** Default value of amount of stone. */
    private static final int      DEFAULT_AMOUNT_OF_STONE        =  15;
    /** Maximal value of amount of stone. */
    private static final int      MAX_AMOUNT_OF_STONE            = 100;
    /** Minimal value of amount of water. */
    private static final int      MIN_AMOUNT_OF_WATER            =   0;
    /** Default value of amount of water. */
    private static final int      DEFAULT_AMOUNT_OF_WATER        =  40;
    /** Maximal value of amount of water. */
    private static final int      MAX_AMOUNT_OF_WATER            = 100;

    
    /** Period time of reexecutioning of operate() method of the server side game handler. */
    public long                  periodTime;
    /** Component to view/change the value of period time. */
    private transient JSpinner   periodTimeComponent;
    /** Maximum number of players in a game. */
    public int                   maxNumberOfPlayers;
    /** Component to view/change the value of maximum number of players. */
    private transient JSpinner   maxNumberOfPlayersComponent;
    /** Width of the map. */
    public int                   mapWidth;
    /** Component to view/change the value of width of the map. */
    private transient JComboBox  mapWidthComponent;
    /** Height of the map. */
    public int                   mapHeight;
    /** Component to view/change the value of height of the map. */
    private transient JSpinner   mapHeightComponent;
    /** Welcome message sent for those who join the game*/
    public String                welcomeMessage;
    /** Component to view/change the value of the welcome message. */
    private transient JTextArea  welcomeMessageComponent;
    /** Type of game: GAME_TYPE_FREE_FOR_ALL, or GAME_TYPE_MELEE. */
    public int                   gameType;
    /** Component to view/change the value of game type. */
    private transient JComboBox  gameTypeComponent;
    /** Tells whether there is a kill limit in the game. */
    public boolean               isKillLimit;
    /** Component to view/change the value of "is kill limit". */
    private transient JCheckBox  isKillLimitComponent;
    /** Kill limit. */
    public int                   killLimit;
    /** Component to view/change the value of kill limit. */
    private transient JSpinner   killLimitComponent;
    /** Tells whether there is a time limit in the game. */
    public boolean               isTimeLimit;
    /** Component to view/change the value of "is time limit". */
    private transient JCheckBox  isTimeLimitComponent;
    /** Time limit (minute). */
    public int                   timeLimit;
    /** Component to view/change the value of time limit. */
    private transient JSpinner   timeLimitComponent;
    /** Password of password protected game. */
    public String                password;
    /** Component to view/change the value of the password. */
    private transient JTextField passwordComponent;
    /** Amount of wall rubbles in percent. */
    public int                   amountOfWallRubbles;
    /** Component to view/change the value of amount of wall rubbles. */
    private transient JSpinner   amountOfWallRubblesComponent;
    /** Amount of blood in percent. */
    public int                   amountOfBlood;
    /** Component to view/change the value of amount of blood. */
    private transient JSpinner   amountOfBloodComponent;
    /** Amount of wall in percent when generating map. */
    public int                   amountOfWall;
    /** Component to view/change the value of amount of wall. */
    private transient JSpinner   amountOfWallComponent;
    /** Amount of stone in percent when generating map. */
    public int                   amountOfStone;
    /** Component to view/change the value of amount of stone. */
    private transient JSpinner   amountOfStoneComponent;
    /** Amount of water: percent of water of the map. */
    public int                   amountOfWater;
    /** Component to view/change the amount of water. */
    private transient JSpinner   amountOfWaterComponent;
    
    /**
        The static initializer. We initializing the MAP_WIDTH_NAMES.
    */
    static {
        int mapWidthNamesCount = 0, i = MAX_MAP_WIDTH;
        while ( i >= MIN_MAP_WIDTH ) {
            i >>= 1;
            mapWidthNamesCount++;
        }
        MAP_WIDTH_NAMES = new String[ mapWidthNamesCount ];
        for ( i = MIN_MAP_WIDTH, mapWidthNamesCount = 0; i <= MAX_MAP_WIDTH; i <<= 1, mapWidthNamesCount++ )
            MAP_WIDTH_NAMES[ mapWidthNamesCount ] = Integer.toString( i );
    }

    /**
        Creates a new ServerOptions.
        @param mainFrame reference to the main frame
    */
    public ServerOptions( final Frame mainFrame ) {
        super( mainFrame );
    }

    /**
        Returns the file name where server options will be saved to/loaded from.
        @return file name of the server options
    */
    protected String getOptionsFileName() {
        return SERVER_OPTIONS_FILE_NAME;
    }

    /**
        Loads the options.
        @param mainFrame reference to the main frame
        @return the loaded serverOptions, or null, if error occured
    */
    public static ServerOptions loadOptions( final Frame mainFrame ) {
        return (ServerOptions) loadOptions( SERVER_OPTIONS_FILE_NAME, mainFrame );
    }
    
    /**
        Creates the settings components of the options.
    */
    protected void createSettingsComponents() {
        periodTimeComponent          = new JSpinner( new SpinnerNumberModel( DEFAULT_PERIOD_TIME, MIN_PERIOD_TIME, MAX_PERIOD_TIME, 1 ) );
        maxNumberOfPlayersComponent  = new JSpinner( new SpinnerNumberModel( DEFAULT_MAX_NUMBER_OF_PLAYERS, MIN_MAX_NUMBER_OF_PLAYERS, MAX_MAX_NUMBER_OF_PLAYERS, 1 ) );
        mapWidthComponent            = new JComboBox( MAP_WIDTH_NAMES );
        mapHeightComponent           = new JSpinner( new SpinnerNumberModel( DEFAULT_MAP_HEIGHT, MIN_MAP_HEIGHT, MAX_MAP_HEIGHT, 1 ) );
        welcomeMessageComponent      = new JTextArea( 10, 20 );
        gameTypeComponent            = new JComboBox( GAME_TYPE_NAMES );
        isKillLimitComponent         = new JCheckBox( "Kill limit:" );
        killLimitComponent           = new JSpinner( new SpinnerNumberModel( DEFAULT_KILL_LIMIT, MIN_KILL_LIMIT, MAX_KILL_LIMIT, 1 ) );
        isTimeLimitComponent         = new JCheckBox( "Time limit:" );
        timeLimitComponent           = new JSpinner( new SpinnerNumberModel( DEFAULT_TIME_LIMIT, MIN_TIME_LIMIT, MAX_TIME_LIMIT, 1 ) );
        passwordComponent            = new JTextField( 10 );
        amountOfWallRubblesComponent = new JSpinner( new SpinnerNumberModel( DEFAULT_AMOUNT_OF_WALL_RUBBLES, MIN_AMOUNT_OF_WALL_RUBBLES, MAX_AMOUNT_OF_WALL_RUBBLES, 1 ) );
        amountOfBloodComponent       = new JSpinner( new SpinnerNumberModel( DEFAULT_AMOUNT_OF_BLOOD, MIN_AMOUNT_OF_BLOOD, MAX_AMOUNT_OF_BLOOD, 1 ) );
        amountOfWallComponent        = new JSpinner( new SpinnerNumberModel( DEFAULT_AMOUNT_OF_WALL, MIN_AMOUNT_OF_WALL, MAX_AMOUNT_OF_WALL, 1 ) );
        amountOfStoneComponent       = new JSpinner( new SpinnerNumberModel( DEFAULT_AMOUNT_OF_STONE, MIN_AMOUNT_OF_STONE, MAX_AMOUNT_OF_STONE, 1 ) );
        amountOfWaterComponent       = new JSpinner( new SpinnerNumberModel( DEFAULT_AMOUNT_OF_WATER, MIN_AMOUNT_OF_WATER, MAX_AMOUNT_OF_WATER, 1 ) );
    }

    /**
        Creates and returns a panel with components to change the values of the server options.
        @return a settings tabbed pane
    */
    protected JTabbedPane createSettingsTabbedPane() {
        JTabbedPane settingsTabbedPane = new JTabbedPane();
        JPanel borderPanel, borderPanel2, panel, tabPanel;
        tabPanel = new JPanel();
        borderPanel = new JPanel( new BorderLayout() );
            panel = new JPanel();
                panel.add( new JLabel( "Maximum number of players:" ) );
                panel.add( maxNumberOfPlayersComponent );
            borderPanel.add( panel, BorderLayout.NORTH );
            panel = new JPanel();
                panel.add( new JLabel( "Select the width of the map:" ) );
                panel.add( mapWidthComponent );
            borderPanel.add( panel, BorderLayout.CENTER );
            borderPanel2 = new JPanel( new BorderLayout() );
                panel = new JPanel();
                    panel.add( new JLabel( "Height of the map:" ) );
                    panel.add( mapHeightComponent );
                borderPanel2.add( panel, BorderLayout.NORTH );
                panel = new JPanel();
                    panel.add( new JLabel( "Select game type:" ) );
                    panel.add( gameTypeComponent );
                borderPanel2.add( panel, BorderLayout.CENTER );
                panel = new JPanel();
                    panel.add( new JLabel( "Game password:" ) );
                    panel.add( passwordComponent );
                borderPanel2.add( panel, BorderLayout.SOUTH );
            borderPanel.add( borderPanel2, BorderLayout.SOUTH );
        tabPanel.add( borderPanel );
        settingsTabbedPane.addTab( "General", tabPanel );
        tabPanel = new JPanel();
        borderPanel = new JPanel( new BorderLayout() );
        tabPanel.add( borderPanel );
            panel = new JPanel();
                panel.add( isKillLimitComponent );
                panel.add( killLimitComponent );
            borderPanel.add( panel, BorderLayout.NORTH );
            panel = new JPanel();
                panel.add( isTimeLimitComponent );
                panel.add( timeLimitComponent );
                panel.add( new JLabel( "minute(s)." ) );
            borderPanel.add( panel, BorderLayout.CENTER );
        settingsTabbedPane.addTab( "Limits", tabPanel );
        tabPanel = new JPanel();
        borderPanel = new JPanel( new BorderLayout() );
            panel = new JPanel();
                panel.add( new JLabel( "Enter welcome message:" ) );
            borderPanel.add( panel, BorderLayout.NORTH );
            panel = new JPanel();
                panel.add( new JScrollPane( welcomeMessageComponent ) );
            borderPanel.add( panel, BorderLayout.CENTER );
        tabPanel.add( borderPanel );
        settingsTabbedPane.addTab( "Welcome", tabPanel );
        tabPanel = new JPanel();
        borderPanel = new JPanel( new BorderLayout() );
            panel = new JPanel();
                panel.add( new JLabel( "Amount of wall:" ) );
                panel.add( amountOfWallComponent );
                panel.add( new JLabel( "%." ) );
            borderPanel.add( panel, BorderLayout.NORTH );
            panel = new JPanel();
                panel.add( new JLabel( "Amount of stone:" ) );
                panel.add( amountOfStoneComponent );
                panel.add( new JLabel( "%." ) );
            borderPanel.add( panel, BorderLayout.CENTER );
            panel = new JPanel();
                panel.add( new JLabel( "Amount of water:" ) );
                panel.add( amountOfWaterComponent );
                panel.add( new JLabel( "%." ) );
            borderPanel.add( panel, BorderLayout.SOUTH );
        tabPanel.add( borderPanel );
        settingsTabbedPane.addTab( "Map generating", tabPanel );
        tabPanel = new JPanel();
        borderPanel = new JPanel( new BorderLayout() );
            panel = new JPanel();
                panel.add( new JLabel( "Cycle/period time of the server:" ) );
                panel.add( periodTimeComponent );
                panel.add( new JLabel( "ms." ) );
            borderPanel.add( panel, BorderLayout.NORTH );
            panel = new JPanel();
                panel.add( new JLabel( "Amount of wall rubbles:" ) );
                panel.add( amountOfWallRubblesComponent );
                panel.add( new JLabel( "%." ) );
            borderPanel.add( panel, BorderLayout.CENTER );
            panel = new JPanel();
                panel.add( new JLabel( "Amount of blood:" ) );
                panel.add( amountOfBloodComponent );
                panel.add( new JLabel( "%." ) );
            borderPanel.add( panel, BorderLayout.SOUTH );
        tabPanel.add( borderPanel );
        settingsTabbedPane.addTab( "Extra", tabPanel );
        for ( int tabCounter = settingsTabbedPane.getTabCount() - 1; tabCounter >= 0; tabCounter-- )
            settingsTabbedPane.setMnemonicAt( tabCounter, settingsTabbedPane.getTitleAt( tabCounter ).charAt( 0 ) );
        return settingsTabbedPane;
    }

    /**
        Returns the title of the server settings dialog.
        @return title of the settings dialog
    */
    protected String getSettingsDialogTitle() {
        return "Server options";
    }
    
    /**
        Synchronizes the server options to the actual values of the option-components.
    */
    protected void synchronizeOptionsToComponents() {
        periodTime          = (long) Double.parseDouble( periodTimeComponent.getValue().toString() );
        maxNumberOfPlayers  = (int) Double.parseDouble( maxNumberOfPlayersComponent.getValue().toString() );
        mapWidth            = Integer.parseInt( mapWidthComponent.getSelectedItem().toString() );
        mapHeight           = (int) Double.parseDouble( mapHeightComponent.getValue().toString() );
        welcomeMessage      = welcomeMessageComponent.getText();
        gameType            = gameTypeComponent.getSelectedIndex();
        isKillLimit         = isKillLimitComponent.isSelected();
        killLimit           = (int) Double.parseDouble( killLimitComponent.getValue().toString() );
        isTimeLimit         = isTimeLimitComponent.isSelected();
        timeLimit           = (int) Double.parseDouble( timeLimitComponent.getValue().toString() );
        password            = passwordComponent.getText();
        amountOfWallRubbles = (int) Double.parseDouble( amountOfWallRubblesComponent.getValue().toString() );
        amountOfBlood       = (int) Double.parseDouble( amountOfBloodComponent.getValue().toString() );
        amountOfWall        = (int) Double.parseDouble( amountOfWallComponent.getValue().toString() );
        amountOfStone       = (int) Double.parseDouble( amountOfStoneComponent.getValue().toString() );
        amountOfWater       = (int) Double.parseDouble( amountOfWaterComponent.getValue().toString() );
    }

    /**
        Synchronizes the option-components to the server options.
    */
    protected void synchronizeComponentsToOptions() {
        periodTimeComponent.setValue( new Long( periodTime ) );
        maxNumberOfPlayersComponent.setValue( new Integer( maxNumberOfPlayers ) );
        mapWidthComponent.setSelectedItem( Integer.toString( mapWidth ) );
        mapHeightComponent.setValue( new Integer( mapHeight ) );
        welcomeMessageComponent.setText( welcomeMessage );
        gameTypeComponent.setSelectedIndex( gameType );
        isKillLimitComponent.setSelected( isKillLimit );
        killLimitComponent.setValue( new Integer( killLimit ) );
        isTimeLimitComponent.setSelected( isTimeLimit );
        timeLimitComponent.setValue( new Integer( timeLimit ) );
        passwordComponent.setText( password );
        amountOfWallRubblesComponent.setValue( new Integer( amountOfWallRubbles ) );
        amountOfBloodComponent.setValue( new Integer( amountOfBlood ) );
        amountOfWallComponent.setValue( new Integer( amountOfWall ) );
        amountOfStoneComponent.setValue( new Integer( amountOfStone ) );
        amountOfWaterComponent.setValue( new Integer( amountOfWater ) );
    }

    /**
        Restores the default values to the option-components.
    */
    protected void restoreDefaultsToComponents() {
        periodTimeComponent.setValue( new Long( DEFAULT_PERIOD_TIME ) );
        maxNumberOfPlayersComponent.setValue( new Integer( DEFAULT_MAX_NUMBER_OF_PLAYERS ) );
        mapWidthComponent.setSelectedItem( Integer.toString( DEFAULT_MAP_WIDTH ) );
        mapHeightComponent.setValue( new Integer( DEFAULT_MAP_HEIGHT ) );
        welcomeMessageComponent.setText( DEFAULT_WELCOME_MESSAGE );
        gameTypeComponent.setSelectedIndex( DEFAULT_GAME_TYPE );
        isKillLimitComponent.setSelected( DEFAULT_IS_KILL_LIMIT );
        killLimitComponent.setValue( new Integer( DEFAULT_KILL_LIMIT ) );
        isTimeLimitComponent.setSelected( DEFAULT_IS_TIME_LIMIT );
        timeLimitComponent.setValue( new Integer( DEFAULT_TIME_LIMIT ) );
        passwordComponent.setText( DEFAULT_PASSWORD );
        amountOfWallRubblesComponent.setValue( new Integer( DEFAULT_AMOUNT_OF_WALL_RUBBLES ) );
        amountOfBloodComponent.setValue( new Integer( DEFAULT_AMOUNT_OF_BLOOD ) );
        amountOfWallComponent.setValue( new Integer( DEFAULT_AMOUNT_OF_WALL ) );
        amountOfStoneComponent.setValue( new Integer( DEFAULT_AMOUNT_OF_STONE ) );
        amountOfWaterComponent.setValue( new Integer( DEFAULT_AMOUNT_OF_WATER ) );
    }
    
    /**
        Redefining the toString() method. Returns the string representation of values of options that are changeable, effects to the game and clients does not requiring them.
        (So they are not in the changeable server options.)
        @return the string representation of the server options
    */
    public String toString() {
        final StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( "amount of wall rubbles = " );
        stringBuffer.append( amountOfWallRubbles );
        stringBuffer.append( "%, " );
        stringBuffer.append( "amount of blood = " );
        stringBuffer.append( amountOfBlood );
        stringBuffer.append( "%, " );
        if ( isKillLimit ) {
            stringBuffer.append( "kill limit = " );
            stringBuffer.append( killLimit );
            stringBuffer.append( ", " );
        }
        if ( isTimeLimit ) {
            stringBuffer.append( "time limit = " );
            stringBuffer.append( timeLimit );
            stringBuffer.append( " minute" );
            if ( timeLimit > 1 )
                stringBuffer.append( 's' );
            stringBuffer.append( ", " );
        }
        stringBuffer.append( "period time = " );
        stringBuffer.append( periodTime );
        stringBuffer.append( " ms" );
        return stringBuffer.toString();
    }

}
