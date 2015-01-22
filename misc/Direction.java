package project.misc;

/**
 * A enumeration representing different directions.
 * 
 * @author 	Stef Noten & Jasper Hilven
 * @version	1.0
 *
 */
public enum Direction {
	/**
	 * The north direction.
	 */
	NORTH,
	/**
	 * The east direction.
	 */
	EAST,
	/**
	 * The south direction.
	 */
	SOUTH,
	/**
	 * The west direction.
	 */
	WEST,
	/**
	 * The downward direction.
	 */
	FLOOR,
	/**
	 * The upward direction.
	 */
	CEILING;

	/**
	 * Return the opposite direction.
	 *
	 * @return	West becomes east, south becomes north, ceiling becomes floor and vice versa.
	 * 			| ...
	 */
	public Direction getOppositeDirection() {
		
		switch (this) {
		case WEST: return EAST;
		case EAST: return WEST;
		case SOUTH: return NORTH;
		case NORTH: return SOUTH;
		case FLOOR: return CEILING;
		case CEILING: return FLOOR;
		default:
			assert false;
			return null;
		}
	}
 }
