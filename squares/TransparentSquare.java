/**
 * 
 */
package project.squares;

import java.util.Map;

import project.misc.Direction;
import project.squares.borders.Border;
import be.kuleuven.cs.som.annotate.Raw;

/**
 * An interface for a transparent square, that is, a square that does not have any full walls (a wall without a door).
 * 
 * @author 	Stef Noten & Jasper Hilven
 * @version	3.0
 *
 */
public interface TransparentSquare extends Square {
	
	/**
	 * Check whether this square can have the given borders if this square is not terminated.
	 * 
	 * @return	A transparent square can not have walls without a door.
	 * 			| if (for some border in mapBorders.values()
	 * 			|		(border instanceof Wall) && !((Wall)border).hasDoor() )
	 * 			|	then result == false
	 * @return	A transparent square must have at least one door and a maximum of two doors, in which case
	 * 			both doors must stand in opposite directions.
	 * 			| ...
	 */
	@Raw @Override
	public boolean canHaveAsBordersIfNotTerminated(Map<Direction, Border> mapBorders);
	
	/**
	 * Return whether an avatar can enter this square.
	 * 
	 * @result	True, an avatar can always enter a transparent square.
	 * 			| result == true 
	 */
	@Override
	public boolean canEnter();
}
