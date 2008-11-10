
package classes.servertools.gamecore.decorations;

import classes.servertools.gamecore.*;
import java.awt.*;


/**
    This is a point-look decoration.
    @author Belicza Andras
*/
public class PointDecoration extends Decoration {

    /** Physical mass of a point decoration (kg). */
    private static float MASS                   = 0.5f;
    /** Shape resistance rate of a point decoration. */
    private static float SHAPE_RESISTANCE_RATE  = 0.3f;
    /** Physical capacity of a point decoration (m^3). */
    private static float CAPACITY               = 0.00001f;
    /** Anti gravitation rate of a point decoration. */
    private static float ANTI_GRAVITATION_RATE  = 0.0f;
    /** Velocity of a point decoration. */
    private static float VELOCITY               = 10.0f;
    
    /** The color of the point decoration. */
    private Color         color;
    /** Tells whether the point decoration is rubble. */
    private final boolean rubble;

    /**
        Creates a new PointDecoration.
        @param initialCycleCounter value of cycle counter when this decoration was created
        @param position initial position of this point decoration
        @param direction angle of velocity of this point decoration
        @param rubble tells whether this point decoration is rubble
        @param color color of this point decoration
    */
    public PointDecoration( final int initialCycleCounter, final FloatVector position, final double direction, final boolean rubble, final Color color ) {
        super( MASS, SHAPE_RESISTANCE_RATE, CAPACITY, ANTI_GRAVITATION_RATE, initialCycleCounter, position, direction, VELOCITY );
        this.rubble = rubble;
        this.color  = color;
    }

    /**
        Creates a new PointDecoration.
        @param initialCycleCounter value of cycle counter when this decoration was created
        @param position initial position of this point decoration
        @param direction angle of velocity of this point decoration
        @param rubble tells whether this point decoration is rubble
    */
    public PointDecoration( final int initialCycleCounter, final FloatVector position, final double direction, final boolean rubble ) {
        this( initialCycleCounter, position, direction, rubble, null );
    }

    /**
        Returns true.
        @return true
    */
    public boolean isPointDecoration() {
        return true;
    }
    
    /**
        Returns true if this point decoration is rubble, false otherwise.
        @return true if this point decoration is rubble; false otherwise
    */
    public boolean isRubble() {
        return rubble;
    }

    /**
        Sets the color of the point decoration.
        @param color the new color of the point decoration
    */
    public void setColor( final Color color ) {
        this.color = color;
    }

    /**
        Draws the point decoration.
        @param graphicsContext graphics context in wich to draw
        @param doubleSized tells whether we have to draw the point decoration in double size
    */
    public void drawPointDecoration( final Graphics graphicsContext, final boolean doubleSized ) {
        graphicsContext.setColor( color );
        final int positionX = (int) position.x, positionY = (int) position.y;
        graphicsContext.drawLine( positionX, positionY , positionX + ( doubleSized ? 1 : 0 ), positionY );
        if ( doubleSized )
            graphicsContext.drawLine( positionX, positionY + 1, positionX + 1, positionY + 1 );
    }
    
}
