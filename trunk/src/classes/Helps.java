
package classes;

import java.awt.*;
import javax.swing.*;


/**
    Contains help informations about the game, and makes them visible if requested.
    @author Belicza Andras
*/
class Helps {

    /**
        Shows the help about control keys.
        @param mainFrame reference to the main frame
    */
    public static void showControlKeysHelp( final Frame mainFrame ) {
        final JTextArea keys = new JTextArea();
        keys.append( "Left - move left\n" );
        keys.append( "Right - move right\n" );
        keys.append( "Up - jump/move up\n" );
        keys.append( "Down - move down\n" );
        keys.append( "\n" );
        keys.append( "Mouse left button - fire\n" );
        keys.append( "Mouse right button - use rope\n" );
        keys.append( "Mouse wheel - change weapon\n" );
        keys.append( "\n" );
        keys.append( "1 - select rifle\n" );
        keys.append( "2 - select shotgun\n" );
        keys.append( "3 - select splinter grenade launcher\n" );
        keys.append( "4 - select explosive grenade launcher\n" );
        keys.append( "5 - select rocket launcher\n" );
        keys.append( "6 - select laser cannon\n" );
        keys.append( "7 - select thunderbolt\n" );
        keys.append( "\n" );
        keys.append( "+ - increase sight\n" );
        keys.append( "- - decrease sight\n" );
        keys.append( "* - increase map zooming factor\n" );
        keys.append( "/ - decrease map zooming factor\n" );
        keys.append( "Pause - pause/resume game\n" );
        showTextAreaHelp( keys, mainFrame, "Control keys" );
    }

    /**
        Shows tips about the game.
        @param mainFrame reference to the main frame
    */
    public static void showTips( final Frame mainFrame ) {
        int tipCounter = 1;
        final JTextArea tips = new JTextArea();
        tips.append( tipCounter++ + ". Press Enter in the main window if you want to send message.\n\n" );
        tips.append( tipCounter++ + ". Press Escape in the message console, if you want to return to the game.\n\n" );
        tips.append( tipCounter++ + ". Game will run faster, if you set max player number exactly to how many of you would like to play.\n\n" );
        showTextAreaHelp( tips, mainFrame, "Tips" );
    }

    /**
        Shows the faq of the game.
        @param mainFrame reference to the main frame
    */
    public static void showFaq( final Frame mainFrame ) {
        int faqCounter = 1;
        final JTextArea faqs = new JTextArea();
        faqs.append( "\n" );
        faqs.append( "Frequently asked questions\n" );
        faqs.append( "\n" );
        faqs.append( "Content:\n" );
        faqs.append( faqCounter++ + ". What can I do if I have a slow computer?\n" );
        faqs.append( faqCounter++ + ". What can I do if I have a slow connection?\n" );
        faqs.append( faqCounter++ + ". What are the rules of creating new graphics?\n" );
        faqs.append( "\n" );
        faqCounter = 1;
        faqs.append( "-------------------------------------------------------------------\n" );
        faqs.append( faqCounter++ + ". What can I do if I have a slow computer?\n" );
        faqs.append( "-decrease the size of the game sight, the size of the main window\n" );
        faqs.append( "-decrease the game sceen refreshing time\n" );
        faqs.append( "-increase download map datas at sceen refresh cycles count\n" );
        faqs.append( "-increase refresh map at map downloading cycles count\n" );
        faqs.append( "-increase refresh players window at sceen refresh cycles count\n" );
        faqs.append( "-tell the server to decrease the amount of blood\n" );
        faqs.append( "-tell the server to decrease the amount of wall rubbles\n" );
        faqs.append( "-do not show rubbles and blood in double size\n" );
        faqs.append( "-do not download decorations\n" );
        faqs.append( "-decrease map zooming factor\n" );
        faqs.append( "-play with less player\n" );
        faqs.append( "-play on smaller map\n" );
        faqs.append( "-if you are the server, decrease the server period time\n" );
        faqs.append( "-close map window\n" );
        faqs.append( "-close players window\n" );
        faqs.append( "-do not show name of players, hit point of the worms, reloading times of the weapons, level of oxygen bottles\n" );
        faqs.append( "-turn off double buffering graphics technique\n" );
        faqs.append( "-close other applications\n" );
        faqs.append( "-turn off your computer\n" );
        faqs.append( "\n" );
        faqs.append( "-------------------------------------------------------------------\n" );
        faqs.append( faqCounter++ + ". What can I do if I have a slow connection?\n" );
        faqs.append( "-decrease the game sceen refreshing time\n" );
        faqs.append( "-increase download map datas at sceen refresh cycles count\n" );
        faqs.append( "-tell the server to decrease the amount of blood\n" );
        faqs.append( "-tell the server to decrease the amount of wall rubbles\n" );
        faqs.append( "-do not download decorations\n" );
        faqs.append( "-play with less player\n" );
        faqs.append( "-play on smaller map\n" );
        faqs.append( "-turn off other down/uploadings\n" );
        faqs.append( "-go offline\n" );
        faqs.append( "\n" );
        faqs.append( "-------------------------------------------------------------------\n" );
        faqs.append( faqCounter++ + ". What are the rules of creating new graphics?\n" );
        faqs.append( "-search other Qpac graphic files on Qpac home page or on the internet and replace 'worm.gif' and 'walls.gif' in 'datas' directory with them\n" );
        faqs.append( "-go to the 'datas' directory, and edit/repaint the 'worm.gif' and 'walls.gif' as you would like it\n" );
        faqs.append( "The rules of interpreting 'worm.gif':\n" );
        faqs.append( "\t-the white color (r:255,g:255,b:255) will be painted to the color of worm\n" );
        faqs.append( "\t-the black color (r:0,g:0,b:0) will be non-opaque\n" );
        faqs.append( "\t-other colors will remain unchanged\n" );
        faqs.append( "The rules of interpreting 'walls.gif':\n" );
        faqs.append( "\t-the color of the point in the second line and second column will determine the color of wall on the map\n" );
        faqs.append( "\t-the color of wall rubbles is determined the same way" );
        faqs.setTabSize( 4 );
        showTextAreaHelp( faqs, mainFrame, "Faq" );
    }

    /**
        Shows main infromations about the game.
        @param mainFrame reference to the main frame
    */
    public static void showAboutInformations( final Frame mainFrame ) {
        final JTextArea  about = new JTextArea();
        about.append( "\n" );
        about.append( GeneralConsts.APPLICATION_NAME + ' ' + GeneralConsts.APPLICATION_VERSION + '\n' );
        about.append( "\n" );
        about.append( "Home page: http://icza.sch.bme.hu\n" );
        about.append( "\n" );
        about.append( "Written by Belicza András\n" );
        about.append( "Email: icza@freemail.hu\n" );
        about.append( "\n" );
        about.append( "A hundred percent Java game\n" );
        about.append( "Tested and developed on J2SDK 1.4.2.\n" );
        about.append( "\n" );
        about.append( "This program is freeware!\n" );
        about.append( "The source code is now public!\n" );
        about.append( "If you have any comments, please visit the home page!\n" );
        about.append( "If you have any questions, bug reports, job offers or money to support me, please contact me!\n" );
        about.append( "If you have new Qpac graphics files, upload them at the home page.\n" );
        about.append( "\n" );
        about.append( "2003\n" );
        about.append( "Hungary\n" );
        showTextAreaHelp( about, mainFrame, "About" );
    }

    /**
        Shows help informaions contained by a text area.
        @param helpTextArea the text area containing the help informations
        @param mainFrame reference to the main frame
        @param title title of the help dialog
    */
    private static void showTextAreaHelp( final JTextArea helpTextArea, final Frame mainFrame, final String title ) {
        helpTextArea.setCaretPosition( 0 );
        helpTextArea.setEditable( false );
        helpTextArea.setLineWrap( true );
        helpTextArea.setWrapStyleWord( true );
        helpTextArea.setRows( 15 );
        helpTextArea.setColumns( 40 );
        JOptionPane.showMessageDialog( mainFrame, new JScrollPane( helpTextArea ), title, JOptionPane.PLAIN_MESSAGE );
    }

}
