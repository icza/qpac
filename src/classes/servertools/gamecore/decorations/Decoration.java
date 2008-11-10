
package classes.servertools.gamecore.decorations;

import classes.servertools.gamecore.*;
import java.awt.*;
import java.util.*;


/**
    Common part of the decorations. They has no extension: decorations don't collide with other objects.
    @author Belicza Andras
*/
public abstract class Decoration extends MovingObject {

    /** Initial value of cycle counter. */
    protected transient final int initialCycleCounter;

    /**
        Creates a new Decoration.
        @param mass the physical mass of the decoration
        @param shapeResistanceRate the rate of the shape resistance in air and water
        @param capacity the physical capacity of the decoration
        @param antiGravitationRate the anti gravitation rate
        @param initialCycleCounter value of cycle counter when this decoration was created
        @param position initial position of the bullet
        @param direction angle of direction this bullet was shot in
        @param velocity initial velocity of the bullet
    */
    Decoration( final float mass, final float shapeResistanceRate, final float capacity, final float antiGravitationRate, final int initialCycleCounter, final FloatVector position, final double direction, final float velocity ) {
        super( mass, shapeResistanceRate, capacity, antiGravitationRate );
        this.initialCycleCounter = initialCycleCounter;
        this.position.x          = position.x;
        this.position.y          = position.y;
        this.velocity.x          =  velocity * (float) Math.cos( direction );
        this.velocity.y          = -velocity * (float) Math.sin( direction );
    }

    /**
        Returns false always, because not all the decorations may be point decortaions.
        @return false always
    */
    public boolean isPointDecoration() {
        return false;
    }

    /**
        Draws the decoration.
        @param graphicsContext graphics context in wich to draw
        @throws NotOverridedException if this method has not been overrided
    */
    public void draw( final Graphics graphicsContext ) throws NotOverridedException {
        throw new NotOverridedException();
    }
    
    /**
        Draws the point decoration.
        @param graphicsContext graphics context in wich to draw
        @param doubleSized tells whether we have to draw the point decoration in double size
        @throws NonPointDecorationException if this is not a point decoration
    */
    public void drawPointDecoration( final Graphics graphicsContext, final boolean doubleSized ) throws NonPointDecorationException {
        throw new NonPointDecorationException();
    }
    
    /**
        Checks the cycle if we reached the end cycle, and then executes a self-destruction.
        Returns always false, because some decorations don't use this feature.
        @param cycleCounter actual value of cycle counter
        @param decorations vector of decorations
        @param decorationIndex our index in the decorations
        @return false always
    */
    public boolean reachedEndCycle( final int cycleCounter, final Vector decorations, final int decorationIndex ) {
        return false;
    }
    
}
