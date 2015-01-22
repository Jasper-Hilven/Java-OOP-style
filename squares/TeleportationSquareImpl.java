package project.squares;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import project.misc.Direction;
import project.temperature.*;
import be.kuleuven.cs.som.annotate.*;

/**
* A square that is enabled for teleportation, it can have one or more teleportation targets.
* 
* @author 	Stef Noten & Jasper Hilven
* @version	3.0
*
*/
public class TeleportationSquareImpl extends SquareImpl implements TeleportationSquare {

	/**
	 * Initialise a new teleportation square, with given temperature, humidity, slipperiness, teleportation targets
	 * and directions in which to put a wall.
	 * 
	 * @param	temperature 
	 * 				The temperature to initialise this square with.
	 * @param	humidity 
	 * 				The humidity of the square, expressed as a percentage.
	 * @param	hasSlipperyMaterial
	 * 				A boolean determining whether this square is made of slippery material.
	 * @param	teleportationTargets
	 * 				The target squares of this teleportation square.
	 * @param	wallDirections
	 * 				The directions in which to give new square a wall.
	 * @Pre		The given list of teleportation targets does not contain doubles.
	 * 			| ...
	 * @Pre		...
	 * 			| (teleportationTargets != null) && 
	 * 			| (teleportationTargets.size() >= 1) && 
	 * 			| for each square in teleportationTargets
	 * 			|	(square != null)
	 * @Post	...
	 * 			| new.getTargetSquares() != null
	 * @Post	The list of target squares contains all the given teleportation targets exactly once.
	 * 			| new.getTargetSquares().size() == teleportationTargets.size()
	 * 			| for each square in teleportationTargets
	 * 			|	for one addedSquare in new.getTargetSquares()
	 * 			|		square == addedSquare 
	 * @Effect	...
	 * 			| super(temperature, humidity, hasSlipperyMaterial, wallDirections);
	 */
	@Raw
	public TeleportationSquareImpl(Temperature temperature, BigDecimal humidity, Boolean hasSlipperyMaterial, List<Square> teleportationTargets, Direction... wallDirections)
		throws NullPointerException, TemperatureOutOfRangeException, IllegalArgumentException
	{
		super(temperature, humidity, hasSlipperyMaterial, wallDirections);
		
		assert (teleportationTargets != null) && (teleportationTargets.size() >= 1);
		for (Square square : teleportationTargets)
			assert square != null;
		for (int i = 0; i < teleportationTargets.size(); i++) {
			for (int j = i + 1; j < teleportationTargets.size(); j++) {
				assert (teleportationTargets.get(i) != teleportationTargets.get(j));
			}
		}
			
		this.targetSquares = new ArrayList<Square>(teleportationTargets);
	}

	/**
	 * Initialise a new teleportation square, with given teleportation targets and directions in which to put a wall.
	 *
	 * @param	teleportationTargets
	 * 				The target squares of this teleportation square.
	 * @param	wallDirections
	 * 				The directions in which to give new square a wall.
	 * @Effect	...
	 * 			| this(new Temperature(25), new BigDecimal(50), false, teleportationTargets, wallDirections);
	 */
	@Raw
	public TeleportationSquareImpl(List<Square> teleportationTargets, Direction... wallDirections)
		throws NullPointerException, TemperatureOutOfRangeException, IllegalArgumentException
	{
		this(new Temperature(25), new BigDecimal(50), false, teleportationTargets, wallDirections);
	}
	
	/**
	 * Initialise a new teleportation square, with given teleportation targets.
	 *
	 * @param	teleportationTargets
	 * 				The target squares of this teleportation square.
	 * @Effect	...
	 * 			| this(teleportationTargets, Direction.FLOOR);
	 */
	@Raw
	public TeleportationSquareImpl(List<Square> teleportationTargets)
		throws NullPointerException, TemperatureOutOfRangeException, IllegalArgumentException
	{
		this(teleportationTargets, Direction.FLOOR);
	}
	
	/**
	 * Return a copy of all the target squares of this teleportation square.
	 */
	@Basic @Override
	public List<Square> getTargetSquares() {
		return new ArrayList<Square>(this.targetSquares);
	}
	
	/**
	 * Return the number of target squares of this teleportation square.
	 */
	@Override
	public int getNbTargetSquares() {
		return this.targetSquares.size();
	}

	/**
	 * Add a square to the list of target squares of this teleportation square.
	 */
	@Override
	public void addTargetSquare(Square square) {
		assert (square != null) && !hasTargetSquare(square);
		this.targetSquares.add(square);
	}
	
	/**
	 * Remove a square from the list of target squares of this teleportation square.
	 */
	@Override
	public void removeTargetSquare(Square square) {
		assert (hasTargetSquare(square));
		assert (getNbTargetSquares() >= 2);
		this.targetSquares.remove(square);
	}

	/**
	 * Return whether a given square is member of the target squares of this teleportation square. 
	 */
	@Override
	public boolean hasTargetSquare(Square square) {
		return this.targetSquares.contains(square);
	}

	/**
	 * Return a random square to teleport to.
	 */
	@Override
	public Square getNewTeleportationTarget() {
		assert getNbTargetSquares() >= 1;
		return this.targetSquares.get((int)(Math.random() * getNbTargetSquares()));
	}

	/**
	 * A variable representing the list of target squares of this teleportation square.
	 */
	private List<Square> targetSquares;
	
	/**
	 * Return all the accessible neighbours of this teleportation square.
	 */
	@Override
	public Set<Square> getAccessibleNeighbours() {
		Set<Square> result = super.getAccessibleNeighbours();
		for (Square square : this.targetSquares)
			result.add(square);
		return result;
	}
}
