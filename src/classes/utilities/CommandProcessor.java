
package classes.utilities;


/**
    This is a command processor.
    @author Belicza Andras
*/
public class CommandProcessor {

    /** Invalid command (if none of command matches a search). */
    public static final int COMMAND_INVALID = -1;

    /** Reference to the names and aliases of the commands. */
    private String[][] commandNames;
    /** The list of the commands. */
    private String     commandList;

    /**
        Creates a new CommandProcessor.
        @param commandNames names and aliases of the commands
        @param commandInfos a short (1 line) description of the commands
    */
    public CommandProcessor( final String[][] commandNames, final String[] commandInfos ) {
        this.commandNames = commandNames;
        buildCommandList( commandInfos );
    }

    /**
        Builds the list of the commands.
        @param commandInfos a short (1 line) description of the commands
    */
    private void buildCommandList( final String[] commandInfos ) {
        final StringBuffer commandList = new StringBuffer();
        for ( int commandNameIndex = 0; commandNameIndex < commandNames.length; commandNameIndex++ ) {
            final String[] commandAliases = commandNames[ commandNameIndex ];
            commandList.append( "--- " );
            for ( int aliasCounter = 0; aliasCounter < commandAliases.length; aliasCounter++  ) {
                commandList.append( commandAliases[ aliasCounter ] );
                if ( aliasCounter != commandAliases.length - 1 )
                    commandList.append( ", " );
            }
            commandList.append( ": " );
            commandList.append( commandInfos[ commandNameIndex ] );
            if ( commandNameIndex != commandNames.length - 1 )
                commandList.append( '\n' );
        }
        this.commandList = commandList.toString();
    }

    /**
        Searches a command among the command names.
        @param command the searchable command
    */
    public int searchCommand( final String command ) {
        for ( int commandNameIndex = 0; commandNameIndex < commandNames.length; commandNameIndex++ )
            for ( int aliasCounter = commandNames[ commandNameIndex ].length - 1; aliasCounter >= 0; aliasCounter-- )
                if ( command.startsWith( commandNames[ commandNameIndex ][ aliasCounter ] ) )
                    return commandNameIndex;
        return COMMAND_INVALID;
    }

    public String getCommandList() {
        return commandList;
    }

}
