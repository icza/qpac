
package classes.clienttools;

import javax.swing.*;
import java.awt.*;
import classes.options.*;
import classes.servertools.gamecore.*;
import java.awt.image.*;
import classes.*;


/**
    Component of the map window: the map will be drawn on this component.
    @author Belicza Andras
*/
public class MapComponent extends JComponent implements OptionsChangeListener {

    /** Width of the map. */
    private final int           mapWidth;
    /** Height of the map. */
    private final int           mapHeight;
    /** Reference to the client options. */
    private final ClientOptions clientOptions;
    /** Preferred size of the component. */
    private final Dimension     preferredSize = new Dimension();
    /** Reference to the map datas. */
    private byte[]              mapDatas;
    /** Reference to the states of players (for drawing). */
    private PlayerState[]       playerStates;
    /** RGB (color) representers of the walls. */
    private final int[]         wallRGBRepresenters;
    /** Buffer where to draw map first. */
    private final BufferedImage buffer;
    /** Reference to the main frame. */
    private final MainFrame mainFrame;
    
    /**
        Creates a new MapComponent.
        @param mainFrame reference to the main frame
        @param mapWidth width of the map
        @param mapHeight height of the map
        @param clientOptions reference to the clientOptions
    */
    MapComponent( final MainFrame mainFrame, final int mapWidth, final int mapHeight, final ClientOptions clientOptions ) {
        this.mainFrame      = mainFrame;
        this.mapWidth       = mapWidth;
        this.mapHeight      = mapHeight;
        this.clientOptions  = clientOptions;
        buffer              = GameSceen.createCompatibleImage( this.mapWidth, this.mapHeight );
        wallRGBRepresenters = this.mainFrame.getWallRGBRepresenters();
        this.clientOptions.addOptionsChangeListener( this );
    }

    /**
        Returns the preferred size of the component.
        @return the preferred size of the component
    */
    public Dimension getPreferredSize() {
        preferredSize.width  = mapWidth  * clientOptions.mapZoomingFactor;
        preferredSize.height = mapHeight * clientOptions.mapZoomingFactor;
        return preferredSize;
    }

    /**
        This method will be called when options has changed (implementing OptionsChangeListener interface).
    */
    public void optionsChanged() {
        mainFrame.updateMapWindowScrollPane();
    }

    /**
        Refreshes the map component (repaints its actual view).
        @param mapDatas reference to the map datas
        @param playerStates reference to the states of the players
    */
    public void refresh( final byte[] mapDatas, final PlayerState[] playerStates ) {
        this.mapDatas     = mapDatas;
        this.playerStates = playerStates;
        repaint();
    }

    /**
        Paints the actual view of the component.
        @param graphicsContext graphics context in wich to paint
    */
    public void paint( final Graphics graphicsContext ) {
        if ( mapDatas != null ) {
            int mapDataCounter = mapWidth * mapHeight - 1;  // This is equal to mapDatas.length - 1
            for ( int y = mapHeight - 1; y >= 0; y-- )                       // Downward direction is important for speed
                for ( int x = mapWidth - 1; x >= 0; x-- )  // Downward direction is important for speed
                    buffer.setRGB( x, y, wallRGBRepresenters[ mapDatas[ mapDataCounter-- ] ] );
            if ( playerStates != null )
                for ( int playerStateIndex = 0; playerStateIndex < playerStates.length; playerStateIndex++ ) {
                    final PlayerState playerState = playerStates[ playerStateIndex ];
                    if ( playerState != null )
                        buffer.setRGB( ( (int) playerState.position.x ) >> GeneralConsts.WALL_WIDTH_SHIFT, ( (int) playerState.position.y ) >> GeneralConsts.WALL_HEIGHT_SHIFT, ClientOptions.WORM_COLORS[ playerState.wormColorIndex ].getRGB() );
                }
            graphicsContext.drawImage( buffer, 0, 0, buffer.getWidth() * clientOptions.mapZoomingFactor, buffer.getHeight() * clientOptions.mapZoomingFactor, null );
        }
    }
    
}
