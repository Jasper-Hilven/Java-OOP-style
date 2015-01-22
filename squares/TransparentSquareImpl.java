package project.squares;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import project.misc.Direction;
import project.squares.borders.*;
import project.temperature.Temperature;
import project.temperature.TemperatureOutOfRangeException;
import be.kuleuven.cs.som.annotate.*;

/**
 * A transparent square, that is, a square that does not have any full walls (a wall without a door).
 * 
 * @author	Stef Noten & Jasper Hilven
 * @version	3.0
 *
 */
public class TransparentSquareImpl extends SquareImpl implements TransparentSquare {
	
	/**
	 * Initialise a new transparent square, with given temperature, humidity, slipperiness and directions in which to put a wall.
	 * 
	 * @param	temperature 
	 * 				The temperature to initialise this square with.
	 * @param	humidity 
	 * 				The humidity of the square, expressed as a percentage.
	 * @param	hasSlipperyMaterial
	 * 				A boolean determining whether this square is made of slippery material.
	 * @param	wallDirections
	 * 				The directions in which to give the new square a wall with a door.
	 * @Effect 	...
	 * 			| super(temperature, humidity, hasSlipperyMaterial, wallDirections);
	 */
	@Raw
	public TransparentSquareImpl(Temperature temperature, BigDecimal humidity, Boolean hasSlipperyMaterial, Direction... wallDirections)
		throws NullPointerException, TemperatureOutOfRangeException, IllegalArgumentException
	{
		super(temperature, humidity, hasSlipperyMaterial, wallDirections);
	}
	
	/**
	 * Initialise a new transparent square with the given directions in which to put a wall with a door.
	 * 
	 * @Effect 	...
	 * 			| super(wallDirections);
	 */
	@Raw
	public TransparentSquareImpl(Direction... wallDirections)
		throws NullPointerException, TemperatureOutOfRangeException, IllegalArgumentException
	{
		super(wallDirections);
	}
	
	/**
	 * Initialise a new transparent square.
	 * 
	 * @Effect 	...
	 * 			| super(Direction.NORTH);
	 */
	@Raw
	public TransparentSquareImpl()
		throws NullPointerException, TemperatureOutOfRangeException, IllegalArgumentException
	{
		super(Direction.NORTH);
	}
	
	/**
	 * Check whether this square can have the given borders if this square is not terminated.
	 */
	@Raw @Override
	public boolean canHaveAsBordersIfNotTerminated(Map<Direction, Border> mapBorders) {
		if (!super.canHaveAsBordersIfNotTerminated(mapBorders))
			return false;

		Direction[] doorDirections = new Direction[2];
		int doors = 0;
		for (Direction direction : Direction.values()) {
			Border border = mapBorders.get(direction);
			if (border instanceof Wall) {
				if (!((Wall)border).hasDoor())
					return false;
				if (++doors > 2)
					return false;
				doorDirections[doors-1] = direction;
			}
		}
		if (doors == 2)
			return doorDirections[0].getOppositeDirection() == doorDirections[1];
		return true;
	}
	
	/**
	 * Return a map with walls for each given direction and with open borders for each not given direction.
	 *  
	 * @param	wallDirections
	 * 				A non-fixed amount of directions for which the returning map must have a wall with a door at.
	 * @return	The resulting map will have a wall with a door in each given direction.
	 * 			| for each direction in Direction.values()
	 * 			|	if (wallDirections.contains(direction)
	 * 			|		((Wall)result.get(direction)).hasDoor()
	 */
	@Override
	public HashMap<Direction, Border> getWallsAt(Direction... wallDirections) {
		HashMap<Direction, Border> retMap = super.getWallsAt(wallDirections);
		for (Direction direction : wallDirections) {
			retMap.put(direction, new Wall(true, false));
		}
		return retMap;
	}
	
	/**
	 * Return whether an avatar can enter this square.
	 */
	@Override
	public boolean canEnter() {
		return true;
	}
}
