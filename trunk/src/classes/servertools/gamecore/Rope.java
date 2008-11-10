
package classes.servertools.gamecore;

import java.awt.*;


/**
    Rope of the worms.
    @author Belicza Andras
*/
public class Rope extends ExtensiveMovingObject {

    /** Start position of the rope (actually this is a reference to the owner worm's position).*/
    public final FloatVector startPosition;
    /** Indicating if the rope has an end point, so the rope is ready to do force transfers. */
    public boolean           hasEndPoint;
    /** If the end point of the rope is attached to an other moving object, otherwise null. */
    public MovingObject      endPointMovingObject;
    /** If the end point moving object of the rope is a player, this is its index, otherwise GameCoreHandler.INVALID_PLAYER_INDEX. (Because if player has been disconnected, we have to remove this rope.) */
    public transient int     endPointPlayerIndex;
    /** True if we're climbinb up at the moment. */
    public boolean           climbingUp;
    /** True if we're climbinb down at the moment. */
    public boolean           climbingDown;

    /**
        Creates a new Rope.
        @param startPosition start position of the rope
    */
    public Rope( final FloatVector startPosition ) {
        super( RopeConsts.MASS, RopeConsts.SHAPE_RESISTANCE_RATE, RopeConsts.CAPACITY, RopeConsts.ANTI_GRAVITATION_RATE, RopeConsts.WIDTH, RopeConsts.HEIGHT );
        this.startPosition  = startPosition;
        position.x          = this.startPosition.x;
        position.y          = this.startPosition.y;
        hasEndPoint         = false;
        endPointPlayerIndex = GameCoreHandler.INVALID_PLAYER_INDEX;
    }
    
    /**
        Returns the actual postion of the end of the rope.
        @return position of the end of the rope
    */
    public FloatVector getEndPosition() {
        return endPointMovingObject == null ? position : endPointMovingObject.position;
    }

    /**
        Draws the explosion.
        @param graphicsContext graphics context in wich to draw
    */
    public void draw( final Graphics graphicsContext ) {
        final int ropeEndPositionX = (int) getEndPosition().x;
        final int ropeEndPositionY = (int) getEndPosition().y;
        graphicsContext.setColor( RopeConsts.ROPE_COLOR );
        graphicsContext.drawLine( (int) startPosition.x, (int) startPosition.y, ropeEndPositionX, ropeEndPositionY );
        graphicsContext.drawLine( ropeEndPositionX - dimensionToLeft, ropeEndPositionY - dimensionToUp, ropeEndPositionX + dimensionToRight, ropeEndPositionY + dimensionToDown );
        graphicsContext.drawLine( ropeEndPositionX + dimensionToLeft, ropeEndPositionY - dimensionToUp, ropeEndPositionX - dimensionToRight, ropeEndPositionY + dimensionToDown );
    }
    
}
