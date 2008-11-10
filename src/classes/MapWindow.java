
package classes;

import javax.swing.*;
import java.awt.*;
import classes.clienttools.*;


/**
    The map window.
    @author Belicza Andras
*/
class MapWindow extends AbstractDialog {

    /** Default size of this window. */
    private static final Dimension DEFAULT_SIZE = new Dimension( 200, 200 );

    /**
        Creates a new MapWindow.
        @param mainFrame reference to the main frame
        @param referenceWindow reference of the window what to locate this dialog window
    */
    MapWindow( final MainFrame mainFrame, final Window referenceWindow ) {
        super( "Map", mainFrame, referenceWindow, MainFrame.MAP_WINDOW_INDEX );
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
        Returns the default size of the map window.
        @return the default size of the map window
    */
    protected Dimension getDefaultSize() {
        return DEFAULT_SIZE;
    }
    
}
