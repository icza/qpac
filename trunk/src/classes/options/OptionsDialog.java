
package classes.options;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/**
    Dialog to show options and make them changable.
    @author Belicza Andras
*/
class OptionsDialog extends JDialog implements ActionListener {
    
    /** The owner frame. */
    final Frame ownerFrame;

    /**
        Creates a new OptionsDialog.
        @param ownerFrame the owner frame
        @param title title of the options dialog
        @param components a panel already containing the components of the options
        @param options reference to the options
    */
    OptionsDialog( final Frame ownerFrame, final String title, final JComponent components, final Options options ) {
        super( ownerFrame, title, true );
        this.ownerFrame = ownerFrame;
        buildGUI( components, options );
        pack();
    }
    
    /**
        Builds the graphical user interface of the dialog.
        @param components reference to the components
        @param options reference to the options
    */
    private void buildGUI( final JComponent components, final Options options ) {
        getContentPane().add( components, BorderLayout.CENTER );
        final JPanel panel = new JPanel();
        JButton button;
            button = new JButton( "Ok" );
            button.addActionListener( options );
            button.setMnemonic( button.getText().charAt( 1 ) );
            panel.add( button );
            button = new JButton( "Cancel" );
            button.addActionListener( this );
            button.setMnemonic( button.getText().charAt( 0 ) );
            panel.add( button );
            button = new JButton( "Save options" );
            button.addActionListener( options );
            button.setMnemonic( button.getText().charAt( 0 ) );
            panel.add( button );
            button = new JButton( "Restore defaults" );
            button.addActionListener( options );
            button.setMnemonic( button.getText().charAt( 0 ) );
            panel.add( button );
        getContentPane().add( panel, BorderLayout.SOUTH );
    }

    /**
        Alignes this dialog the the owner frame.
    */
    private void alignToOwnerFrame() {
        setLocation( Math.max( 0, ownerFrame.getLocation().x + ( ownerFrame.getWidth() >> 1 ) - ( getWidth() >> 1 ) ), Math.max( 0, ownerFrame.getLocation().y + ( ownerFrame.getHeight() >> 1 ) - ( getHeight() >> 1 ) ) );
    }

    /**
        Makes the dialog visible (overriding Component.show() for align dialog first).
    */
    public void show() {
        alignToOwnerFrame();
        super.show();
    }

    /**
        Handling the cancel button (implementing ActionListener interface).
    */
    public void actionPerformed( final ActionEvent ae ) {
        hide();
    }
    
}
