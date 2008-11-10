
package classes.servertools.gamecore;


/**
    Moving object with extension (dimension).
    @author Belicza Andras
*/
public class ExtensiveMovingObject extends MovingObject {

    /** Dimension to left. */
    public final int dimensionToLeft;
    /** Dimension to up. */
    public final int dimensionToUp;
    /** Dimension to right. */
    public final int dimensionToRight;
    /** Dimension to down. */
    public final int dimensionToDown;
    
    /**
        Creates a new ExtensiveMovingObject.
        @param mass the physical mass of the object
        @param shapeResistanceRate the rate of the shape resistance in air and water
        @param capacity the physical capacity of the object
        @param antiGravitationRate the anti gravitation rate
        @param width width of the object
        @param height height of the object
    */
    public ExtensiveMovingObject( final float mass, final float shapeResistanceRate, final float capacity, final float antiGravitationRate, final int width, final int height ) {
        super( mass, shapeResistanceRate, capacity, antiGravitationRate );
        dimensionToLeft  = width  >> 1;
        dimensionToUp    = height >> 1;
        dimensionToRight = width  - dimensionToLeft - 1;
        dimensionToDown  = height - dimensionToUp   - 1;
    }
    
    /**
        Tests whether this extensive moving object hits the given window.
        @param windowX1 the x coordinate of the left side of the window
        @param windowY1 the y coordinate of the upper side of the window
        @param windowX2 the x coordinate of the right side of the window
        @param windowY2 the y coordinate of the lower side of the window
        @return true if this extensive moving object hits the window; false otherwise
    */
    public boolean hitsWindow( final int windowX1, final int windowY1, final int windowX2, final int windowY2 ) {
        return !( windowX2 < position.x - dimensionToLeft || position.x + dimensionToRight < windowX1 ||
                  windowY2 < position.y - dimensionToUp   || position.y + dimensionToDown  < windowY1    );
    }
    
    /**
        Tests whether this extensive moving object hits another one.
        @param extensiveMovingObject the other object needed to test
        @return true if the 2 extensive moving objects hit each other; false otherwise
    */
    public boolean hitsExtensiveObject( final ExtensiveMovingObject extensiveMovingObject ) {
        return !( extensiveMovingObject.position.x + extensiveMovingObject.dimensionToRight < position.x - dimensionToLeft ||
                  position.x + dimensionToRight < extensiveMovingObject.position.x - extensiveMovingObject.dimensionToLeft ||
                  extensiveMovingObject.position.y + extensiveMovingObject.dimensionToDown < position.y - dimensionToUp    ||
                  position.y + dimensionToDown  < extensiveMovingObject.position.y - extensiveMovingObject.dimensionToUp );
    }
    
}
