package project.dungeons;

import be.kuleuven.cs.som.annotate.Raw;
import project.squares.Square;

/**
 * A level, that is, a singular dungeon in which all squares have the same z coordinate.
 * 
 * @author 	Stef Noten & Jasper Hilven
 * @version	3.0
 * @Invar	All the squares in a level have a z coordinate of value zero.
 * 			| for each position in getSquares().keySet()
 * 			|	(position.getZ() == 0)
 * @Invar	The z coordinate of the maximum position of a level is zero.
 * 			| getMaximumPosition().getZ() == 0 
 */
public class Level<SquareT extends Square> extends SingularDungeon<SquareT>  {
	
	/**
	 * Initialise a new level with given maximum coordinates.
	 * 
	 * @param	maximumX
	 * 				The value to set the x component of the maximum position of this level to.
	 * @param	maximumY
	 * 				The value to set the y component of the maximum position of this level to.
	 * @post	The x and y components of the maximum position will be equal to the given values.
	 * 			| new.getMaximumPosition().isIdenticalTo(new Position(maximumX, maximumY, 0))
	 */
	@Raw
	public Level(long maximumX, long maximumY) {
		super(new Position(maximumX, maximumY, 0));
	}
	
	/**
	 * Initialise a default new level.
	 *
	 * @effect	...
	 * 			| this(100, 100);
	 */
	@Raw
	public Level() {
		this(100, 100);
	}

	/**
	 * Check whether a position is a valid maximum position.
	 * 
	 * @param	position
	 * 				The position to check the validity for.
	 * @return	False if the given position has a z coordinate different from zero.
	 * 			| else if (position.getZ() != 0)
	 * 			|	then result == false
	 */
	@Override
	public boolean canHaveAsMaximumPosition(Position position) {
		return super.canHaveAsMaximumPosition(position) && (position.getZ() == 0);
	}
}
