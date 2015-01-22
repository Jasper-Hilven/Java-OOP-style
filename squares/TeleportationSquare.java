package project.squares;

import java.util.List;
import java.util.Set;

/**
 * A square that is enabled for teleportation, it can have one or more teleportation targets.
 * 
 * @author 	Stef Noten & Jasper Hilven
 * @version	3.0
 *
 *
 * @Invar	...
 * 			| getTargetSquares() != null
 * @Invar	...
 * 			| getNbTargetSquares() >= 1
 */
public interface TeleportationSquare extends Square {
	
	/**
	 * Return all the target squares of this teleportation square.
	 */
	public List<Square> getTargetSquares();
	
	/**
	 * Return the number of target squares of this teleportation square.
	 * 
	 * @return	...
	 * 			| result == getTargetSquares().size();
	 */
	public int getNbTargetSquares();
	
	/**
	 * Add a square to the list of target squares of this teleportation square.
	 * 
	 * @param 	square
	 * 				The square to add as a target square.
	 * 			| ...
	 * @Pre		...
	 * 			| (square != null) && !hasTargetSquare(square)
	 * @Post	...
	 * 			| new.hasTargetSquare(square)
	 */
	public void addTargetSquare(Square square);
	
	/**
	 * Remove a square from the list of target squares of this teleportation square.
	 * 
	 * @param 	square
	 * 				The square to remove as a target square.
	 * @Pre		...
	 * 			| hasTargetSquare(square)
	 * @Pre		...
	 * 			| getNbTargetSquares() >= 2
	 * @Post	The target squares of this teleportation square do not contain the given square.
	 * 			| !new.hasTargetSquare(square)
	 */
	public void removeTargetSquare(Square square);
	
	/**
	 * Return whether a given square is member of the target squares of this teleportation square. 
	 * 
	 * @param	square
	 * 				The square to check if it is member of the target squares of this teleportation square.
	 * @return	...
	 * 			| result == getTargetSquares().contains(square)
	 */
	public boolean hasTargetSquare(Square square);
	
	/**
	 * Return a square to teleport to.
	 * 
	 * @return	A square that is member of the target squares of this teleportation square that is used to teleport to.
	 * 			| hasTargetSquare(result)
	 */
	public Square getNewTeleportationTarget();
	
	/**
	 * Return all the accessible neighbours of this teleportation square.
	 * 
	 * ...
	 * @return	The result set contains all the teleportation targets of this teleportation square.
	 * 			| ...
	 */
	public Set<Square> getAccessibleNeighbours();
}
