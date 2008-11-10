
package classes;

import javax.swing.*;
import java.awt.*;
import classes.clienttools.*;
import java.awt.image.*;
import classes.options.*;
import java.awt.event.*;


/**
    A component where the game actions will be displayed, the scene of actions.
    @author Belicza Andras
*/
public class GameSceen extends JComponent implements OptionsChangeListener {

    /** The mimimum dimension of the game sceen. */
    private static final Dimension MIN_DIMENSION           = new Dimension(  50,  50 );
    /** The default dimension of the game sceen. */
    public  static final Dimension DEFAULT_DIMENSION       = new Dimension( 300, 300 );
    /** Changing dimension unit when increasing/decreasing dimension. */
    public  static final Dimension DIMENSION_CHANGING_UNIT = new Dimension(  10,  10 );
    
    /** Reference to the main frame. */
    private MainFrame       mainFrame;
    /** Reference to the client options. */
    private ClientOptions   clientOptions;
    /** The dimension of this component. */
    private final Dimension dimension = new Dimension( DEFAULT_DIMENSION );
    /** The view drawer is responsible to draw the view. */
    private ViewDrawer      viewDrawer;
    
    /**
        Creates a new GameSceen.
        @param mainFrame reference to the mainFrame
    */
    public GameSceen( final MainFrame mainFrame ) {
        this.mainFrame = mainFrame;
        addKeyListener( new KeyAdapter() {
            public void keyTyped( final KeyEvent ke ) {
                if ( ke.getKeyChar() == KeyEvent.VK_ENTER )
                    mainFrame.transferFocusToMessageConsole();
            }
        } );
    }

    /**
        Sets the client options.
        @param clientOptions reference to the client options.
    */
    public void setClientOptions( final ClientOptions clientOptions ) {
        this.clientOptions = clientOptions;
        this.clientOptions.addOptionsChangeListener( this );
        optionsChanged();   // We want things to be done what needed when options changed.
    }

    /**
        Finalizes the game sceen: we have to remove ourself from the options change listeners of client options.
    */
    protected void finalize() {
        clientOptions.removeOptionsChangeListener( this );
    }

    /**
        Sets the dimension of the game sceen.
        @param dimension the new dimension of the game sceen
    */
    public void setDimension( final Dimension dimension ) {
        this.dimension.setSize( dimension );
        checkDimension();
        revalidate();
    }

    /**
        Increases the dimension of the game sceen.
    */
    public void increaseDimension() {
        dimension.width  += DIMENSION_CHANGING_UNIT.width;
        dimension.height += DIMENSION_CHANGING_UNIT.height;
        revalidate();
    }

    /**
        Decreases the dimension of the game sceen.
    */
    public void decreaseDimension() {
        dimension.width  -= DIMENSION_CHANGING_UNIT.width;
        dimension.height -= DIMENSION_CHANGING_UNIT.height;
        checkDimension();
        revalidate();
    }

    /**
        Checks if the dimension is legal, not smaller then MIN_DIMENSION.
        Dimension will be corrected if it's illegal.
    */
    private void checkDimension() {
        if ( dimension.width < MIN_DIMENSION.width )
            dimension.width = MIN_DIMENSION.width;
        if ( dimension.height < MIN_DIMENSION.height )
            dimension.height = MIN_DIMENSION.height;
    }

    /**
        Returns the dimension of the game sceen.
        @return the dimension of the game sceen
    */
    public Dimension getDimension() {
        return dimension;
    }

    /**
        Returns the preferred size of the component.
        @return the preferred size of the component
    */
    public Dimension getPreferredSize() {
        return dimension;
    }

    /**
        Paints the actual view of the component.
        @param graphicsContext the graphics context in wich to paint
    */
    public void paint( final Graphics graphicsContext ) {
        if ( viewDrawer != null )
            viewDrawer.drawView( graphicsContext, dimension.width, dimension.height );
    }

    /**
        Sets the view drawer for this component.
        @param viewDrawer the view drawer what will draw the view of this component
    */    
    public void setViewDrawer( final ViewDrawer viewDrawer ) {
        this.viewDrawer = viewDrawer;
        setOpaque( this.viewDrawer != null );   // If viewDrawer != null, it will clears the whole game sceen.
        if ( this.viewDrawer == null )
            repaint();
    }

    /**
        This method will be called when client options has changed (implementing OptionsChangeListener interface).
    */
    public void optionsChanged() {
        RepaintManager.currentManager( this ).setDoubleBufferingEnabled( !clientOptions.turnOffDoubleBufferingGraphicsTechnique );
    }

    /**
        Creates display compatible buffered image for fast image works.
        @param width width of creatable image
        @param height height of creatable image
        @return a compatible BufferedImage
    */
    public static BufferedImage createCompatibleImage( final int width, final int height ) {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage( width, height );
    }

    /**
        Creates display compatible bitmask transparency buffered image for fast image works.
        @param width width of creatable image
        @param height height of creatable image
        @return a compatible bitmask transparency BufferedImage
    */
    public static BufferedImage createCompatibleBitmaskTransparencyImage( final int width, final int height ) {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage( width, height, Transparency.BITMASK );
    }

}
