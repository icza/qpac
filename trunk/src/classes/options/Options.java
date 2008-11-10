
package classes.options;

import java.io.*;
import classes.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/**
    The basics of a saveable/loadable options class with OptionsChangeListener.
    @author Belicza Andras
*/
abstract class Options implements Serializable, ActionListener {

    /** Vector of listeners needed to be informed when options change. */
    transient private Vector        optionsChangeListeners;
    /** Reference to the options dialog. */
    transient protected JDialog     optionsDialog;

    /**
        Creates a new Options.
        @param mainFrame reference to the main frame
    */
    protected Options( final Frame mainFrame ) {
        initializeNewOptions( mainFrame );
        restoreDefaultsToComponents();
        synchronizeOptionsToComponents();
    }

    /**
        Initializes the new Options object.
        @param mainFrame reference to the main frame
    */
    private void initializeNewOptions( final Frame mainFrame ) {
        optionsChangeListeners = new Vector();
        createSettingsComponents();
        optionsDialog = new OptionsDialog( mainFrame, getSettingsDialogTitle(), createSettingsTabbedPane(), this );
    }

    /**
        Creates the settings components of the options.
    */
    protected abstract void createSettingsComponents();

    /**
        Creates and returns a panel with components to view/change the values of the options.
        @return a settings tabbed pane
    */
    protected abstract JTabbedPane createSettingsTabbedPane();

    /**
        Returns the file name where options will be saved to/loaded from.
        @return file name of the options
    */
    protected abstract String getOptionsFileName();

    /**
        Saves the options.
        @return true, if saving was sucessful; false otherwise
    */
    public boolean saveOptions() {
        try {
            ObjectOutputStream file = new ObjectOutputStream( new FileOutputStream( GeneralConsts.OPTIONS_DIRECTORY + getOptionsFileName() ) );
            file.writeObject( this );
            file.close();
        }
        catch ( Exception e ) {
            return false;
        }
        return true;
    }
    
    /**
        Loads the options.
        @param optionsFileName file name to load options from
        @param mainFrame reference to the main frame
        @return the loaded options, or null, if error occured
    */
    protected static Options loadOptions( final String optionsFileName, final Frame mainFrame ) {
        try {
            ObjectInputStream file = new ObjectInputStream( new FileInputStream( GeneralConsts.OPTIONS_DIRECTORY + optionsFileName ) );
            final Options options  = (Options) file.readObject();
            file.close();
            options.initializeNewOptions( mainFrame );
            return options;
        }
        catch ( Exception e ) {
            return null;
        }
    }
    
    /**
        Adds a new OptionsChangeListener.
        @param optionsChangeListener OptionsChangeListener to be added
    */
    public void addOptionsChangeListener( final OptionsChangeListener optionsChangeListener ) {
        optionsChangeListeners.add( optionsChangeListener );
    }
    
    /**
        Removes an OptionsChangeListener.
        @param optionsChangeListener OptionsChangeListener to be removed
    */
    public void removeOptionsChangeListener( final OptionsChangeListener optionsChangeListener ) {
        optionsChangeListeners.remove( optionsChangeListener );
    }
    
    /**
        This method will call the optionsChanged() method of all registered options change listener.
    */
    public void fireOptionsChanged() {
        for ( int optionsChangeListenersCounter = 0; optionsChangeListenersCounter < optionsChangeListeners.size(); optionsChangeListenersCounter++ )
            ( (OptionsChangeListener) optionsChangeListeners.elementAt( optionsChangeListenersCounter ) ).optionsChanged();
    }

    /**
        Returns the title of the settings dialog.
        @return title of the settings dialog
    */
    protected abstract String getSettingsDialogTitle();

    /**
        Shows the settings dialog where can be viewed/changed all options.
    */
    public void showSettingsDialog() {
        synchronizeComponentsToOptions();
        optionsDialog.show();
    }
    
    /**
        Synchronizes the options to the actual values of the option-components.
    */
    protected abstract void synchronizeOptionsToComponents();

    /**
        Synchronizes the option-components to the options.
    */
    protected abstract void synchronizeComponentsToOptions();

    /**
        Restores the default values to the option-components.
    */
    protected abstract void restoreDefaultsToComponents();

    /**
        Handling button actions of Options dialog (implementing ActionListener interface).
        @param ae details of the action event
    */
    public void actionPerformed( final ActionEvent ae ) {
        final String actionCommand = ae.getActionCommand();
        if ( actionCommand.equals( "Ok" ) ) {
            synchronizeOptionsToComponents();
            optionsDialog.dispose();
            fireOptionsChanged();
        } else if ( actionCommand.equals( "Save options" ) ) {
            synchronizeOptionsToComponents();
            fireOptionsChanged();
            if ( saveOptions() )
                JOptionPane.showMessageDialog( optionsDialog, "Options saved successfully!", "", JOptionPane.INFORMATION_MESSAGE );
            else
                JOptionPane.showMessageDialog( optionsDialog, "Can't save options!", "Error", JOptionPane.ERROR_MESSAGE );
        } else if ( actionCommand.equals( "Restore defaults" ) )
            restoreDefaultsToComponents();
    }
    
}
