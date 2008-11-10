
package classes.utilities;


/**
    This is an array orderer class.
    @author Belicza Andras
*/
public class Orderer {

    /**
        Orders an array to the same array (by selecting maximum).
        @param array array to be ordered
        @return the (ordered) array
    */
    public static HasOrderingIntKeys[] orderArray( final HasOrderingIntKeys[] array ) {
        int                maximumIntKeyIndex;
        HasOrderingIntKeys maximumElement;
        for ( int index = 0; index < array.length - 1; index++ ) {
            maximumElement = array[ maximumIntKeyIndex = index + 1 ];
            for ( int index2 = index + 2; index2 < array.length; index2++ )
                if ( isGreater( array[ index2 ], maximumElement ) )
                    maximumElement = array[ maximumIntKeyIndex = index2 ];
            if ( isGreater( maximumElement, array[ index ] ) ) {
                final HasOrderingIntKeys objectSwitchesPlaceWithMaximum = array[ index ];
                array[ index              ] = maximumElement;
                array[ maximumIntKeyIndex ] = objectSwitchesPlaceWithMaximum;
            }
        }
        return array;
    }

    /**
        Determines whether an element is greater from an another element.
        If the primary ordering int keys are equals, then the secondary ordering int keys will decide.
        @param element one of the comparable elements
        @param anotherElement another of the comparable elements
        @return true if element is greater than anotherElement; false otherwise
    */
    private static boolean isGreater( final HasOrderingIntKeys element, final HasOrderingIntKeys anotherElement ) {
        return element.getOrderingIntKey() > anotherElement.getOrderingIntKey() || element.getOrderingIntKey() == anotherElement.getOrderingIntKey() && element.getSecondaryOrderingIntKey() > anotherElement.getSecondaryOrderingIntKey();
    }
    
}
