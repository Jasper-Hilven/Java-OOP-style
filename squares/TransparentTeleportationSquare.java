package project.squares;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import project.misc.Direction;
import project.squares.borders.Border;
import project.squares.borders.Wall;
import project.temperature.Temperature;
import project.temperature.TemperatureOutOfRangeException;
import be.kuleuven.cs.som.annotate.Raw;
/**
 * A transparent square that is enabled for teleportation, that is, a square that does not have 
 * any walls (without doors) and can have one or more teleportation targets.
 * 
 * @author	Stef Noten & Jasper Hilven
 * @version	3.0
 *
 */
public class TransparentTeleportationSquare extends TeleportationSquareImpl
		implements TransparentSquare {

	/**
	 * Initialise a new transparent teleportation square with given temperature, humidity, slipperiness, teleportation targets 
	 * and directions in which to put a wall with a door.
	 * 
	 * @param	temperature 
	 * 				The temperature to initialise this square with.
	 * @param	humidity 
	 * 				The humidity of the square, expressed as a percentage.
	 * @param	hasSlipperyMaterial
	 * 				A boolean determining whether this square is made of slippery material.
	 * @param	wallDirections
	 * 				The directions in which to give the new square a wall with a door.
	 * @param	teleportationTargets
	 * 				The target squares of this teleportation square.
	 * @Effect	...
	 * 			| super(temperature, humidity, hasSlipperyMaterial, teleportationTargets, wallDirections);
	 */
	public TransparentTeleportationSquare(Temperature temperature, BigDecimal humidity, Boolean hasSlipperyMaterial, List<Square> teleportationTargets, Direction[] wallDirections)
		throws NullPointerException, TemperatureOutOfRangeException, IllegalArgumentException
	{
		super(temperature, humidity, hasSlipperyMaterial, teleportationTargets, wallDirections);
	}
	
	/**
	 * Initialise a new transparent teleportation square, with given teleportation targets and directions in which to put a wall.
	 *
	 * @param	teleportationTargets
	 * 				The target squares of this transparent teleportation square.
	 * @param	wallDirections
	 * 				The directions in which to give new square a wall.
	 * @Effect	...
	 * 			| this(new Temperature(25), new BigDecimal(50), false, teleportationTargets, wallDirections);
	 */
	@Raw
	public TransparentTeleportationSquare(List<Square> teleportationTargets, Direction... wallDirections)
		throws NullPointerException, TemperatureOutOfRangeException, IllegalArgumentException
	{
		this(new Temperature(25), new BigDecimal(50), false, teleportationTargets, wallDirections);
	}

	/**
	 * Check whether this square can have the given borders if this square is not terminated.
	 * 
	 * ...
	 * @return	A transparent teleportation square can not have walls without a door.
	 * 			| else if (for some border in mapBorders.values()
	 * 			|		(border instanceof Wall) && !((Wall)border).hasDoor() )
	 * 			|	then result == false
	 * @return	A transparent teleportation square must have at least one door and a maximum of two doors, in which case
	 * 			both doors must stand in opposite directions.
	 * 			| ...
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
	 * 
	 * ...
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
