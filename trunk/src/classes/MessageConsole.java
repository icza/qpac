
package classes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/**
    The message console. A window where all message will be displayed and where we can send new messages from too.
    @author Belicza Andras
*/
class MessageConsole extends AbstractDialog implements ActionListener {

    /** Default size of this window. */
    private static final Dimension DEFAULT_SIZE = new Dimension( 300, 250 );
    /** An empty string to be set for message after sending its content and when messages cleared. */
    public  static final String    EMPTY_LINE   = new String();
    /** Initial text of the message console. */
    private static final String    INITIAL_TEXT = "Type " + MainFrame.HELP_COMMAND_NAME + " to list the available commands.\n\n";

    /** Component for viewing the messages. */
    private final JTextArea  messages = new JTextArea( INITIAL_TEXT );
    /** Component for entering and sending new message. */
    private final JTextField message  = new JTextField();

    /**
        Creates a new MessageConsole.
        @param mainFrame reference to the main frame
        @param referenceWindow reference of the window what to locate this dialog window
    */
    MessageConsole( final MainFrame mainFrame, final Window referenceWindow ) {
        super( "Messages", mainFrame, referenceWindow, MainFrame.MESSAGE_CONSOLE_INDEX );
        buildGUI();
    }
    
    /**
        Builds the graphical user interface of the message console.
    */
    private void buildGUI() {
        messages.setEditable( false );
        getContentPane().add( new JScrollPane( messages ), BorderLayout.CENTER );
        message.addActionListener( this );
        getContentPane().add( message , BorderLayout.SOUTH  );
        final KeyListener keyListener = new KeyAdapter() {
            public void keyTyped( final KeyEvent ke ) {
                if ( ke.getKeyChar() == KeyEvent.VK_ESCAPE )
                    mainFrame.transferFocusToGameSceen();
            }
        };
        message .addKeyListener( keyListener );
        messages.addKeyListener( keyListener );
    }
    
    /**
        Returns the dialog window locating policy: LOCATING_POLICY_ON_RIGHT_SIDE.
        @return the dialog window locating policy
    */
    protected int getLocatingPolicy() {
        return LOCATING_POLICY_ON_RIGHT_SIDE;
    }
    
    /**
        Returns the default size of the message console.
        @return the default size of the message console
    */
    protected Dimension getDefaultSize() {
        return DEFAULT_SIZE;
    }

    /**
        Adds a new message to the messages.
        @param message the new message
    */
    public void addMessage( final String message ) {
        messages.append( message );
        messages.append( "\n" );
        messages.setCaretPosition( messages.getDocument().getLength() );
    }

    /**
        To handling action event (pressing enter) on the message text field.
        @param ae details of the action event
    */
    public void actionPerformed( final ActionEvent ae ) {
        mainFrame.processMessage( message.getText() );
        message.setText( EMPTY_LINE );
    }
    
    /**
        Clears the messages text area, removes all messages.
    */
    public void clearMessages() {
        messages.setText( EMPTY_LINE );
    }

    /**
        Sets the line-wrapping policy of the messages text area.
        @param lineWrap line-wrap policy
    */
    public void setMessagesLineWrap( final boolean lineWrap ) {
        messages.setLineWrap( lineWrap );
    }

    /**
        A request to get the input focus.
    */
    public void gainInputFocus() {
        if ( isVisible() )
            message.requestFocus();
    }

}
