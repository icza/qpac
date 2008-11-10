
package classes;

import javax.swing.*;
import java.awt.*;


/**
    Window for monitoring the list of players and their results.
    @author Belicza Andras
*/
class PlayersWindow extends AbstractDialog {

    /** Default size of this window. */
    private static final Dimension DEFAULT_SIZE = new Dimension( 300, 200 );

    /**
        Creates a new PlayersWindow.
        @param mainFrame reference to the main frame
        @param referenceWindow reference of the window what to locate this dialog window
    */
    PlayersWindow( final MainFrame mainFrame, final Window referenceWindow ) {
        super( "Players", mainFrame, referenceWindow, MainFrame.PLAYERS_WINDOW_INDEX );
        setScrollableComponent( null );
    }
    
    /**
        Returns the dialog window locating policy: LOCATING_POLICY_UNDER.
        @return the dialog window locating policy
    */
    protected int getLocatingPolicy() {
        return LOCATING_POLICY_UNDER;
    }
    
    /**
        Returns the default size of the players window.
        @return the default size of the players window
    */
    protected Dimension getDefaultSize() {
        return DEFAULT_SIZE;
    }

}
