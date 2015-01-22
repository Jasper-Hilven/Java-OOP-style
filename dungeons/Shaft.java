package project.dungeons;
import java.util.Map;
import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Raw;
import project.misc.Direction;
import project.squares.RockSquare;
import project.squares.Square;
import project.squares.borders.Border;
import project.squares.borders.Wall;

/**
 * A shaft, that is, a singular dungeon that can not contain rock squares and 
 * that has squares that have positions that only vary in one coordinate.
 * 
 * @author 	Stef Noten & Jasper Hilven
 * @version	3.0
 * @Invar	A shaft can not contain rock squares.
 * 			| for each square in getSquares().values()
 * 			|	! (square instanceof RockSquare)
 * @Invar	The maximum position of a shaft has two coordinates of value zero.
 * 			| for some factor in long
 * 			|	getMaximumPosition().isIdenticalTo(
 * 			|		Position.getAxis(getShaftDirection()).multiply(factor))
 * @Invar	A shaft can not have a wall with a door between to adjacent squares.
 * 			| hasInternalDoors() == false
 */
public class Shaft<SquareT extends Square> extends SingularDungeon<SquareT> {
	
	/**
	 * Initialise a new shaft with a given direction and a given maximum size.
	 *
	 * @Pre 	The given direction must be a positive direction.
	 * 			| Position.getSign(direction) > 0
	 * @Post	The maximum position of the new shaft has a coordinate equal to the given maximum size
	 * 			for the axis corresponding to the given direction, zero for the other coordinates.
	 * 			| new.getMaximumPosition().isIdenticalTo(Position.getAxis(direction).multiply(maximumSize))
	 * @Post	The shaft direction of the new shaft is equal to the given direction.
	 * 			| new.getShaftDirection(direction) == direction
	 */
	@Raw
	public Shaft(Direction direction, long maximumSize) {
		super(Position.getAxis(direction).multiply(maximumSize));
		assert(Position.getSign(direction) > 0);
		this.shaftDirection = direction;
	}
	
	/**
	 * Initialise a new shaft with a given direction.
	 * 
	 * @effect	...
	 * 			| this(direction, 100)
	 */
	@Raw
	public Shaft(Direction direction) {
		this(direction, 100);
	}
	
	/**
	 * Get the direction of this shaft .
	 * 
	 * @return	The direction for which the corresponding axis has a non-zero value in the maximum position of this shaft. 
	 */
	@Basic
	public Direction getShaftDirection() {
		return this.shaftDirection;
	}

	/**
	 * A variable representing the direction of this shaft.
	 */
	private Direction shaftDirection;
	
	/**
	 * Check whether a position is a valid maximum position.
	 * 
	 * @param	position
	 * 				The position to check the validity for.
	 * @return	False if the given position has a coordinate that differs from zero in a direction that differs from the shaft direction.
	 * 			| else if (! (for some factor in long
	 * 			|				position.equals(Position.getAxis(getShaftDirection()).multiply(factor))))
	 * 			|	then result == false
	 */
	@Override
	public boolean canHaveAsMaximumPosition(Position position) {
		if (!super.canHaveAsMaximumPosition(position))
			return false;
		if (position.equals(new Position()))
			return true;
		Position axis = Position.getAxis(getShaftDirection());
		return 
			(axis.getX() == 0) == (position.getX() == 0) &&
			(axis.getY() == 0) == (position.getY() == 0) &&
			(axis.getZ() == 0) == (position.getZ() == 0);
	}
	
	/**
	 * Check whether a square can be added to this shaft at a given position.
	 * 
	 * @return	A shaft can not contain rock squares.
	 * 			| else if (square instanceof RockSquare)
	 * 			|	then result == false
	 * @return	A shaft can not have a wall with a door between two adjacent squares.
	 * 			| else if (for some direction in {getShaftDirection() union getShaftDirection().getOppositeDirection() }
	 * 			|				hasSquareAt(position.getAdjacentPosition(direction)) &&
	 * 			|				(square.getDominantBordersAt(getNeighboursAt(position)).get(direction) instanceof Wall) &&
	 * 			|				((Wall)square.getDominantBordersAt(getNeighboursAt(position)).get(direction)).hasDoor())
	 * 			|	then result == false
	 * @return	Else, true.
	 * 			| else
	 * 			|	then result == true
	 */
	@Override
	public boolean canSetSquareAt(Position position, SquareT square) 
		throws NullPointerException, IllegalStateException
	{
		if (!super.canSetSquareAt(position, square))
			return false;
		if (square instanceof RockSquare)
			return false;

		Map<Direction, Border> dominantBorders = square.getDominantBorders(getNeighboursAt(position));
		Border border1 = dominantBorders.get(getShaftDirection());
		Border border2 = dominantBorders.get(getShaftDirection().getOppositeDirection());
		if (hasSquareAt(position.getAdjacentPosition(getShaftDirection()))) {
			if ((border1 instanceof Wall) && ((Wall)border1).hasDoor())
				return false;
		}
		if (hasSquareAt(position.getAdjacentPosition(getShaftDirection().getOppositeDirection()))) {
			if ((border2 instanceof Wall) && ((Wall)border2).hasDoor())
				return false;
		}
		return true;
	}

	/**
	 * Check whether a shaft has no squares that are connected with walls with doors between them.
	 * 
	 * @return	False if there are some squares in this shaft that are connected with walls with doors between them, true otherwise.
	 * 			| ...
	 * @throws	IllegalStateException
	 * 				This shaft is terminated.
	 * 				| isTerminated() == true
	 */
	public boolean hasInternalDoors() throws IllegalStateException {
		if (isTerminated())
			throw new IllegalStateException();
		for (Square square : getSquares().values()) {
			if (square.hasBorderAt(shaftDirection) && (square.getBorderAt(shaftDirection) instanceof Wall) && 
					((Wall)square.getBorderAt(shaftDirection)).hasDoor() && getSquares().containsValue(square.getNeighbour(shaftDirection)))
				return true;
		}
		return false;
	}
	/**
	 * Terminate this shaft.
	 *	
	 * @post	The shaft direction of this shaft is not effective.
	 *			getShaftDirection() == null
	 */
	@Override
	public void terminate() {
		super.terminate();
		shaftDirection = null;
	}
}
