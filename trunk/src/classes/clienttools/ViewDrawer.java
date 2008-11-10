
package classes.clienttools;

import java.awt.*;


/**
    Defines capabality to draw the view of a component.
    @author Belicza Andras
*/
public interface ViewDrawer {

    /**
        Draws the view of a component.
        @param graphicsContext the graphics context in wich to paint
        @param width the width of the visible part of graphics context
        @param height the width of the visible part of graphics context
    */
    void drawView( final Graphics graphicsContext, final int width, final int height );

}
