
package classes.servertools.gamecore;

import classes.options.*;
import classes.*;
import java.awt.*;


/**
    The Map.
    @author Belicza Andras
*/
public class Map {

    /** Constant value for the empty wall. */
    public static final byte WALL_EMPTY         = 0;
    /** Constant value for the brick wall. */
    public static final byte WALL_BRICK         = 1;
    /** Constant value for the stone wall. */
    public static final byte WALL_STONE         = 2;
    /** Constant value for the "water wall". */
    public static final byte WALL_WATER         = 3;
    /** Constant value for the "water surface wall". */
    public static final byte WALL_WATER_SURFACE = 4;
    /** Number of walls. */
    public static final byte WALLS_COUNT        = 5;

    /** Clearable wall indexes relative to the newly placed player index position. Array of [ dx, dy ] arrays. */
    private static final int[][] CLEARABLE_WALL_INDEXES  = { { 0, -1 }, { -1, 0 }, { 0, 0 }, { 1, 0 } }; 
    /** If hitsMap() was called with null parameter at hitWallIndices, we will write to this variable. */
    private static final Point   UNUSED_HIT_WALL_INDICES = new Point();

    /** The datas of the map: walls, stones, water etc. */
    private final byte[] datas;
    /** Width of the map. */
    private final int    width;
    /** Height of the map. */
    private final int    height;
    /** Number of bits needed to shift a height position to get the data index. */
    private final int    heightShift;
    /** Water level at the bottom of the map. */
    private final int    waterLevel;
    
    /**
        Creates a new Map.
        @param serverOptions reference to the server options
    */
    public Map( final ServerOptions serverOptions ) {
        heightShift = calculateHeightShift( serverOptions.mapWidth );
        width       = 1 << heightShift;       // We work with the width of power of 2
        height      = serverOptions.mapHeight;
        datas       = new byte[ width * height ];
        waterLevel  = height - 1 - ( height - 2 ) * serverOptions.amountOfWater / 100;
        generateMap( serverOptions );
    }
    
    /**
        Calculates the number of bits needed to shift a height position to get the data index from the width.
        @param width width of the map
        @return the value of heightShift
    */
    public static int calculateHeightShift( int width ) {
        int heightShift = 0;
        while ( ( width >>= 1 ) > 0 )
            heightShift++;
        return heightShift;
    }
    
    /**
        Generates a random map using parameters in serverOptions.
        @param serverOptions reference to the server options
    */
    private void generateMap( final ServerOptions serverOptions ) {
        final double probabilityOfWall      = serverOptions.amountOfWall  / 100.0;
        final double probabilityOfStoneWall = serverOptions.amountOfStone / 100.0;
        for ( int y = 0; y < height; y++ )
            for ( int x = 0; x < width; x++ )
                if ( x == 0 || x == width - 1 || y == 0 || y == height - 1 )
                    setWall( x, y, WALL_STONE );
                else {
                    final byte nonWall = y < waterLevel ? WALL_EMPTY : ( y == waterLevel ? WALL_WATER_SURFACE : WALL_WATER );
                    setWall( x, y, Math.random() <= probabilityOfWall ? ( Math.random() <= probabilityOfStoneWall ? WALL_STONE : WALL_BRICK ) : nonWall );
                }
    }
    
    /**
        Sets a wall in the map.
        @param x the x position of the wall
        @param y the y position of the wall
        @param wall wall to place to the position
    */
    public void setWall( final int x, final int y, final byte wall ) {
        datas[ ( y << heightShift ) + x ] = wall;
    }
    
    /**
        Returns a wall from the map.
        @param x the x position of the wall
        @param y the y position of the wall
        @return the wall what is in the position
    */
    public byte getWall( final int x, final int y ) {
        try {
            if ( x < 0 || x >= width )
                throw new ArrayIndexOutOfBoundsException();
            return datas[ ( y << heightShift ) + x ];
        }
        catch ( ArrayIndexOutOfBoundsException ae ) {
            return WALL_STONE;          // Like outside map there is stone everywhere...
        }
    }
    
    /**
        Returns the datas of the map.
        @return the datas of the map
    */
    public byte[] getDatas() {
        return datas;
    }
    
    /**
        Returns the width of the map.
        @return width of the map
    */
    public int getWidth() {
        return width;
    }
    
    /**
        Returns the height of the map.
        @return height of the map
    */
    public int getHeight() {
        return height;
    }

    /**
        Clears the map around a newly placed player.
        @param x x index of the position of player
        @param y y index of the position of player
    */
    public void clearMapForEnteringPlayer( final int x, final int y ) {
        for ( int clearableWallCounter = 0; clearableWallCounter < CLEARABLE_WALL_INDEXES.length; clearableWallCounter++ )
            if ( getWall( x + CLEARABLE_WALL_INDEXES[ clearableWallCounter ][ 0 ], y + CLEARABLE_WALL_INDEXES[ clearableWallCounter ][ 1 ] ) == WALL_BRICK )
                clearWall( x + CLEARABLE_WALL_INDEXES[ clearableWallCounter ][ 0 ], y + CLEARABLE_WALL_INDEXES[ clearableWallCounter ][ 1 ] );
    }

    /**
        Clears a brick wall specified by it's position.
        @param x x index of the position of clearable wall
        @param y y index of the position of clearable wall
    */
    public void clearWall( final int x, final int y ) {
        setWall( x, y, y < waterLevel ? WALL_EMPTY : ( y > waterLevel ? WALL_WATER : WALL_WATER_SURFACE ) );
    }
    
    /**
        Tests whether the moving object hits the map.
        @param movingObject object to be tested
        @return true if the moving object hits the map; false otherwise
    */
    public boolean hitsMapNonExtensive( final MovingObject movingObject ) {
        final byte wall = getWall( (int) movingObject.position.x >> GeneralConsts.WALL_WIDTH_SHIFT, (int) movingObject.position.y >> GeneralConsts.WALL_HEIGHT_SHIFT );
        return wall == WALL_STONE || wall == WALL_BRICK;
    }

    /**
        Tests whether the extensive moving object hits the map (any walls in the map).
        @param extensiveMovingObject object to be tested
        @param hitWallIndices if this is not null, and the object hits the map, this will hold the indices of wall collides with the object (outgoin' parameter)
        @return true if the extensive moving object hits the map; false otherwise
    */
    public boolean hitsMap( final ExtensiveMovingObject extensiveMovingObject, Point hitWallIndices ) {
        if ( hitWallIndices == null )
            hitWallIndices = UNUSED_HIT_WALL_INDICES;
        final int positionX = (int) extensiveMovingObject.position.x;
        final int positionY = (int) extensiveMovingObject.position.y;
        byte wall;
        wall = getWall( ( positionX - extensiveMovingObject.dimensionToLeft  ) >> GeneralConsts.WALL_WIDTH_SHIFT, ( positionY - extensiveMovingObject.dimensionToUp   ) >> GeneralConsts.WALL_HEIGHT_SHIFT );
        if ( wall == WALL_STONE || wall == WALL_BRICK ) {
            hitWallIndices.x = ( positionX - extensiveMovingObject.dimensionToLeft ) >> GeneralConsts.WALL_WIDTH_SHIFT;
            hitWallIndices.y = ( positionY - extensiveMovingObject.dimensionToUp   ) >> GeneralConsts.WALL_HEIGHT_SHIFT;
            return true;
        }
        wall = getWall( ( positionX - extensiveMovingObject.dimensionToLeft  ) >> GeneralConsts.WALL_WIDTH_SHIFT, ( positionY + extensiveMovingObject.dimensionToDown ) >> GeneralConsts.WALL_HEIGHT_SHIFT );
        if ( wall == WALL_STONE || wall == WALL_BRICK ) {
            hitWallIndices.x = ( positionX - extensiveMovingObject.dimensionToLeft ) >> GeneralConsts.WALL_WIDTH_SHIFT;
            hitWallIndices.y = ( positionY + extensiveMovingObject.dimensionToDown ) >> GeneralConsts.WALL_HEIGHT_SHIFT;
            return true;
        }
        wall = getWall( ( positionX + extensiveMovingObject.dimensionToRight ) >> GeneralConsts.WALL_WIDTH_SHIFT, ( positionY - extensiveMovingObject.dimensionToUp   ) >> GeneralConsts.WALL_HEIGHT_SHIFT );
        if ( wall == WALL_STONE || wall == WALL_BRICK ) {
            hitWallIndices.x = ( positionX + extensiveMovingObject.dimensionToRight ) >> GeneralConsts.WALL_WIDTH_SHIFT;
            hitWallIndices.y = ( positionY - extensiveMovingObject.dimensionToUp    ) >> GeneralConsts.WALL_HEIGHT_SHIFT;
            return true;
        }
        wall = getWall( ( positionX + extensiveMovingObject.dimensionToRight ) >> GeneralConsts.WALL_WIDTH_SHIFT, ( positionY + extensiveMovingObject.dimensionToDown ) >> GeneralConsts.WALL_HEIGHT_SHIFT );
        if ( wall == WALL_STONE || wall == WALL_BRICK ) {
            hitWallIndices.x = ( positionX + extensiveMovingObject.dimensionToRight ) >> GeneralConsts.WALL_WIDTH_SHIFT;
            hitWallIndices.y = ( positionY + extensiveMovingObject.dimensionToDown  ) >> GeneralConsts.WALL_HEIGHT_SHIFT;
            return true;
        }
        if ( hitsMapNonExtensive( extensiveMovingObject ) ) {
            hitWallIndices.x = positionX >> GeneralConsts.WALL_WIDTH_SHIFT;
            hitWallIndices.y = positionY >> GeneralConsts.WALL_HEIGHT_SHIFT;
            return true;
        }
        return false;
    }

}
