
package classes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/**
    Abstract dialog class for the tool/extra windows.
    @author Belicza Andras
*/
abstract class AbstractDialog extends JDialog {

    /** Constant value for locating the dialog window on the right side of the reference window. */
    protected final int LOCATING_POLICY_ON_RIGHT_SIDE = 0;
    /** Constant value for locating the dialog window under the reference window. */
    protected final int LOCATING_POLICY_UNDER         = 1;

    /** Reference to the main frame. */
    protected final MainFrame mainFrame;
    /** Reference of the reference window what locate this window to */
    private   final Window    referenceWindow;
    /** Index of this window at main frame. */
    private   final int       windowIndex;
    /** Scroll pane of the dialog window. */
    private JScrollPane       scrollPane;

    
    /**
        Creates a new AbstractDialog.
        @param mainFrame reference to the main frame
        @param title title of the dialog window
        @param referenceWindow reference of the window what locate this dialog window to 
        @param windowIndex index of this window at main frame
    */
    AbstractDialog( final String title, final MainFrame mainFrame, final Window referenceWindow, final int windowIndex ) {
        super( mainFrame, title );
        this.mainFrame       = mainFrame;
        this.referenceWindow = referenceWindow;
        this.windowIndex     = windowIndex;
        addWindowListener( new WindowAdapter() {
            public void windowClosing( final WindowEvent we ) {
                mainFrame.clearWindowCheckBoxMenuItem( windowIndex );
            }
        } );
        restoreDefaultBounds();
    }
    
    /**
        Sets the scrollable component of the dialog window.
        @param scrollableComponent component to be set and make it scrollable; if null, an empty scrollpane will be set
    */
    public void setScrollableComponent( final JComponent scrollableComponent ) {
        getContentPane().removeAll();
        scrollPane = scrollableComponent == null ? new JScrollPane() : new JScrollPane( scrollableComponent );
        getContentPane().add( scrollPane, BorderLayout.CENTER );
        validate();
        if ( scrollableComponent == null )
            repaint();
    }
    
    /**
        Resizes and repaints: revalidate followed by repaint.
    */
    public void resizeAndRepaint() {
        scrollPane.revalidate();
        scrollPane.repaint();
    }

    /**
        Alings this dialog to the reference window.
    */
    public void alignDialog() {
        final Rectangle referenceWindowBounds = referenceWindow.getBounds();
        switch ( getLocatingPolicy() ) {
            case LOCATING_POLICY_ON_RIGHT_SIDE :
                setLocation( referenceWindowBounds.x + referenceWindowBounds.width, referenceWindowBounds.y );
                break;
            case LOCATING_POLICY_UNDER :
                setLocation( referenceWindowBounds.x, referenceWindowBounds.y + referenceWindowBounds.height );
                break;
        }
    }

    /**
        Restores the default bounds of the dialog window.
    */
    public void restoreDefaultBounds() {
        alignDialog();
        setSize( getDefaultSize() );
        validate();
    }
    
    /**
        Returns the dialog window locating policy: LOCATING_POLICY_ON_RIGHT_SIDE or LOCATING_POLICY_UNDER.
        @return the dialog window locating policy
    */
    protected abstract int getLocatingPolicy();

    /**
        Returns the default size of the dialog window.
        @return the default size of the dialog window
    */
    protected abstract Dimension getDefaultSize();
    
}
