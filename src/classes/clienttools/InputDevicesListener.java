
package classes.clienttools;

import java.awt.event.*;
import classes.servertools.*;
import java.rmi.*;
import classes.servertools.gamecore.*;
import java.awt.*;


/**
    Listens and process input device events: keyboard and mouse.
    @author Belicza Andras
*/
class InputDevicesListener implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

    /** Array of control keys. */
    public static final int[] CONTROL_KEYS = { KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT };

    /** Reference to the remote player. */
    private final RemotePlayer          remotePlayer;
    /** Reference to the client side game handler. */
    private final ClientSideGameHandler clientSideGameHandler;
    /** Point of the mouse (in the game sceen component). */
    private final Point                 mousePoint    = new Point();
    /** Position of the mouse in the terrain. */
    private final FloatVector           mousePosition = new FloatVector();

    /**
        Creates a new InputDevicesListener.
        @param remotePlayer reference to the remote player
        @param clientSideGameHandler reference to the client side game handler
    */
    InputDevicesListener( final RemotePlayer remotePlayer, final ClientSideGameHandler clientSideGameHandler ) {
        this.remotePlayer          = remotePlayer;
        this.clientSideGameHandler = clientSideGameHandler;
    }
    
    /**
        To handle keypressed events (implementing KeyListener interface).
        @param ke details of the key event
    */
    public void keyPressed( final KeyEvent ke ) {
        final int keyCode = ke.getKeyCode();
        try {
            for ( int controlKeyIndex = 0; controlKeyIndex < CONTROL_KEYS.length; controlKeyIndex++ )
                if ( keyCode == CONTROL_KEYS[ controlKeyIndex ] ) {
                    remotePlayer.setControlKeyState( controlKeyIndex, true );
                    break;
                }
            if ( keyCode == KeyEvent.VK_PAUSE )
                remotePlayer.requestForPauseOrResume();
        }
        catch ( RemoteException re ) {
        }
    }

    /**
        To handle keyreleased events (implementing KeyListener interface).
        @param ke details of the key event
    */
    public void keyReleased( final KeyEvent ke ) {
        final int keyCode = ke.getKeyCode();
        try {
            for ( int controlKeyIndex = 0; controlKeyIndex <CONTROL_KEYS.length; controlKeyIndex++ )
                if ( keyCode == CONTROL_KEYS[ controlKeyIndex ] ) {
                    remotePlayer.setControlKeyState( controlKeyIndex, false );
                    break;
                }
        }
        catch ( RemoteException re ) {
        }
    }

    /**
        To handle keytyped events (implementing KeyListener interface).
        @param ke details of the key event
    */
    public void keyTyped( final KeyEvent ke ) {
        final int keyChar = ke.getKeyChar();
        try {
            if ( keyChar >= KeyEvent.VK_1 && keyChar <= KeyEvent.VK_9 ) {
                remotePlayer.selectWeapon( keyChar - KeyEvent.VK_1 );
                return;
            }
            switch( keyChar ) {
                case '+':
                    clientSideGameHandler.increaseGameSceenDimension();
                    break;
                case '-':
                    clientSideGameHandler.decreaseGameSceenDimension();
                    break;
                case '*':
                    clientSideGameHandler.increaseMapZoomingFactor();
                    break;
                case '/':
                    clientSideGameHandler.decreaseMapZoomingFactor();
                    break;
            }
        }
        catch ( RemoteException re ) {
        }
    }

    /**
        To handle mouseclicked events (implementing MouseListener interface).
        @param me details of the mouse event
    */
    public void mouseClicked( final MouseEvent me ) {
    }

    /**
        To handle mouseentered events (implementing MouseListener interface).
        @param me details of the mouse event
    */
    public void mouseEntered( final MouseEvent me ) {
    }

    /**
        To handle mouseexited events (implementing MouseListener interface).
        @param me details of the mouse event
    */
    public void mouseExited( final MouseEvent me ) {
    }

    /**
        To handle mousePressed events (implementing MouseListener interface).
        @param me details of the mouse event
    */
    public void mousePressed( final MouseEvent me ) {
        try {
            if ( me.getButton() == MouseEvent.BUTTON1 )       // left button
                remotePlayer.setControlKeyState( Player.KEY_INDEX_FIRE, true );
            else if ( me.getButton() == MouseEvent.BUTTON3 )  // right button
                remotePlayer.setControlKeyState( Player.KEY_INDEX_ROPE, true );
        }
        catch ( RemoteException re ) {
        }
    }

    /**
        To handle mousereleased events (implementing MouseListener interface).
        @param me details of the mouse event
    */
    public void mouseReleased( final MouseEvent me ) {
        try {
            if ( me.getButton() == MouseEvent.BUTTON1 )       // left button
                remotePlayer.setControlKeyState( Player.KEY_INDEX_FIRE, false );
            else if ( me.getButton() == MouseEvent.BUTTON3 )  // right button
                remotePlayer.setControlKeyState( Player.KEY_INDEX_ROPE, false );
        }
        catch ( RemoteException re ) {
        }
    }

    /**
        To handle mousedragged events (implementing MouseMotionListener interface).
        @param me details of the mouse event
    */
    public void mouseDragged( final MouseEvent me ) {
        mousePoint.setLocation( me.getPoint() );
        updateMousePosition();
    }

    /**
        To handle mousemoved events (implementing MouseMotionListener interface).
        @param me details of the mouse event
    */
    public void mouseMoved( final MouseEvent me ) {
        mousePoint.setLocation( me.getPoint() );
        updateMousePosition();
    }

    /**
        To handle mousewheelmoved events ( implementing MouseWheelListener interface).
        @param me details of the mouse wheel moved event
    */
    public void mouseWheelMoved( final MouseWheelEvent me ) {
        try {
            remotePlayer.rotateWeaponBy( me.getWheelRotation() );
        }
        catch ( RemoteException re ) {
        }
    }

    /**
        Updates the position of the mouse at the server.
    */
    public void updateMousePosition() {
        try {
            mousePosition.x = mousePoint.x - clientSideGameHandler.getContextTranslationX();
            mousePosition.y = mousePoint.y - clientSideGameHandler.getContextTranslationY();
            remotePlayer.setMousePosition( mousePosition.x, mousePosition.y );
        }
        catch ( RemoteException re ) {
        }
    }
    
    /**
        Returns the position of the mouse.
        @return the position of the mouse
    */
    public FloatVector getMousePosition() {
        return mousePosition;
    }

}
