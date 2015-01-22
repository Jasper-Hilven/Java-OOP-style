package project.dungeons;
import project.misc.Direction;
import be.kuleuven.cs.som.annotate.*;

/**
 * A position with an x, y and z coordinate. 
 * 
 * @Invar	The coordinates are all positive.
 * 			| ...
 * @author 	Stef Noten & Jasper Hilven
 * @version	3.0
 */
@Value
public class Position {
	/**
	 * Create a new position with given x, y and z coordinates.
	 * 
	 * @param 	x
	 * 				The positive x coordinate of this position.
	 * @param 	y
	 * 				The positive y coordinate of this position.
	 * @param 	z
	 * 				The positive y coordinate of this position.
	 * @post	...
	 * 			| new.getX() == x
	 * @post	...
	 * 			| new.getY() == y
	 * @post	...
	 * 			| new.getZ() == z	
	 * @throws	IllegalArgumentException
	 * 				One of the given coordinates is negative.
	 * 				| ...
	 */
	public Position(long x, long y, long z) throws IllegalArgumentException {
		if ((x < 0) || (y < 0) || (z < 0))
			throw new IllegalArgumentException();
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Create a new position with zero as its x, y and z.
	 * @effect	...
	 * 			| this(0, 0, 0)
	 */
	public Position() {
		this(0, 0, 0);
	}

	/**
	 * Get the x coordinate of this position.
	 */
	@Basic @Immutable
	public long getX() {
		return this.x;
	}

	/**
	 * Get the y coordinate of this position.
	 */
	@Basic @Immutable
	public long getY() {
		return this.y;
	}
	
	/**
	 * Get the z coordinate of this position.
	 */
	@Basic @Immutable
	public long getZ() {
		return this.z;
	}

	/**
	 * Variables representing the x, y and z coordinate.
	 */
	long x, y, z;
	
	/**
	 * Check whether this position is identical to a given other position, that is,
	 * if it has the same coordinates.
	 * 
	 * @param 	other
	 * 				The other position to check the coordinates of.
	 * @return	...
	 * 			| result == 
	 * 			| 	(this.getX() == other.getX()) &&
	 * 			|	(this.getY() == other.getY()) &&
	 * 			|	(this.getZ() == other.getZ())
	 * @throws	NullPointerException
	 * 				The given position is not effective.
	 * 				| other == null 
	 */
	public boolean isIdenticalTo(Position other) throws NullPointerException {
		if (other == null)
			new NullPointerException();
		return 
			(this.getX() == other.getX()) &&
			(this.getY() == other.getY()) &&
		 	(this.getZ() == other.getZ());
	}

	/**
	 * Return whether this position is adjacent to another given position, that is,
	 * if two coordinates are the same and the third differs by one.
	 *  
	 * @param 	other
	 * 				The position to check the adjacency for.
	 * @return	True if the positions differ by one in exactly one direction and
	 * 			the coordinates in the two other directions have the same value.
	 * 			| ...
	 * @throws	NullPointerException
	 * 				The given position is not effective.
	 * 				| other == null		
	 */
	public boolean isAdjacentTo(Position other) throws NullPointerException {
		if (other == null)
			throw new NullPointerException();
		
		long dX = Math.max(this.getX(), other.getX()) - Math.min(this.getX(), other.getX());
		long dY = Math.max(this.getY(), other.getY()) - Math.min(this.getY(), other.getY());
		long dZ = Math.max(this.getZ(), other.getZ()) - Math.min(this.getZ(), other.getZ());
		
		if (dX != 0)
			return (dX == 1) && (dY == 0) && (dZ == 0);
		if (dY != 0)
			return (dY == 1) && (dX == 0) && (dZ == 0);
		if (dZ != 0)
			return (dZ == 1) && (dX == 0) && (dY == 0);
		return false;
	}
	
	/**
	 * Get a position representing the axis for the given direction.
	 * 
	 * @param 	direction
	 * 				The direction to get the axis for.
	 * @return	A unit position in the x direction for the ceiling and floor directions,
	 * 			in the y direction for the north and south directions and in the 
	 * 			z direction for the east and west directions.
	 * 			| if ((direction == Direction.CEILING) || (direction == Direction.FLOOR))
	 * 			|	then result.isIdenticalTo(new Position(0, 0, 1)) 
	 * 			| else if ((direction == Direction.NORTH) || (direction == Direction.SOUTH))
	 * 			|	then result.isIdenticalTo(new Position(0, 1, 0))
	 * 			| else
	 * 			|	then result.isIdenticalTo(new Position(1, 0, 0))
	 * @throws	NullPointerException
	 * 				The given position is not valid.
	 * 				| ...
	 */
	public static Position getAxis(Direction direction)
		throws NullPointerException
	{
		if (direction == null)
			throw new NullPointerException();
		
		switch (direction) {
		case CEILING:
		case FLOOR:
			return new Position(0, 0, 1);
		case NORTH:
		case SOUTH:
			return new Position(0, 1, 0);
		case EAST:
		case WEST:
			return new Position(1, 0, 0);
		default:
			assert(false);
			return null;
		}
	}
	
	/**
	 * Get the sign for a given direction, that is, 1 if the direction is positive, 
	 * -1 if the direction is negative.
	 * 
	 * @param 	direction
	 * 				The direction to get the sign for.
	 * @return	1 for the ceiling, north and east directions, -1 for the floor, south and west directions.
	 * 			| if ((direction == Direction.CEILING) || 
	 * 			|	(direction == Direction.NORTH) ||
	 * 			|	(direction == Direction.EAST))
	 * 			|	then result == 1
	 * 			| else
	 * 			|	then result == -1
	 * @throws	NullPointerException
	 * 				The given position is not valid.
	 * 				| ...
	 */
	public static int getSign(Direction direction) 
		throws NullPointerException
	{
		if (direction == null) 
			throw new NullPointerException();
		
		switch (direction) {
		case CEILING:
		case NORTH:
		case EAST:
			return 1;
		case FLOOR:
		case SOUTH:
		case WEST:
			return -1;
		default:
			assert(false);
			return 0;
		}
	}
	
	/**
	 * Return the position next to this position in the given direction.
	 * 
	 * @param	direction
	 * 				The direction in which the wanted position is situated.
	 * @return	...
	 * 			| result.equals(this.offset(
	 * 			|		getAxis(direction).getX() * getSign(direction), 
	 * 			|		getAxis(direction).getY() * getSign(direction), 
	 * 			|		getAxis(direction).getZ() * getSign(direction)))
	 * @throws	NullPointerException
	 * 				...
	 * 				| direction == null
	 * @throws	IllegalArgumentException
	 * 				The position next to this position in the given direction is not a valid position.
	 * 				| ... 
	 */
	public Position getAdjacentPosition(Direction direction)
		throws NullPointerException, IllegalArgumentException
	{
		if (direction == null) 
			throw new NullPointerException();
		
		Position offset = getAxis(direction);
		int sign = getSign(direction);
		return offset(offset.getX() * sign, offset.getY() * sign, offset.getZ() * sign);
	}
	
	/**
	 * Return the direction of the given adjacent position relative to this position.
	 * 
	 * @param	other
	 * 				The other position to compare with this position.
	 * @return	The direction of the given adjacent position relative to this position.
	 * 			| ...
	 * @throws	NullPointerException
	 * 				...
	 * 				| other == null
	 * @throws	IllegalStateException
	 * 				...
	 * 				| !isAdjacentTo(other)
	 */
	public Direction getAdjacentCoordinateDirection(Position other)
		throws NullPointerException, IllegalStateException
	{
		if (!isAdjacentTo(other))
			throw new IllegalStateException();
		
		if (this.getX() != other.getX())
			return (other.getX() > this.getX()) ? Direction.EAST : Direction.WEST;
		if (this.getY() != other.getY())
			return (other.getY() > this.getY()) ? Direction.NORTH : Direction.SOUTH;
		//if (this.getZ() != other.getZ())
		return (other.getZ() > this.getZ()) ? Direction.CEILING : Direction.FLOOR;
	}
	
	/**
	 * Return the difference of this position and the given position.
	 *
	 * @param	otherPosition
	 * 				The position to get the difference relative to this position from.
	 * @return 	A position containing the current position, subtracted by the given other position.
	 * 			| result.equals(new Position(getX() - otherPosition.getX(),
	 * 						        		getY() - otherPosition.getY(),
	 * 										getZ() - otherPosition.getZ()))
	 * @throws	NullPointerException
	 * 				The given position is null.
	 * 				| otherPosition == null
	 * @throws	IllegalArgumentException
	 * 				The calculated position is invalid.
	 * 				| ...
	 */
	public Position subtract(Position otherPosition) throws NullPointerException, IllegalArgumentException {
		return new Position(
				getX() - otherPosition.getX(),
				getY() - otherPosition.getY(),
				getZ() - otherPosition.getZ());
	}
	
	/**
	 * Return the current position, with a given x, y and z offset.
	 *   
	 * @param 	offsetX
	 * 				The x offset.
	 * @param 	offsetY
	 * 				The y offset.
	 * @param 	offsetZ
	 * 				The z offset.
	 * @return	A new position with coordinates that are the sum of the coordinates of 
	 * 			this position and the given offset values.
	 * 			| ...
	 * @throws	IllegalArgumentException
	 * 				The given offset coordinates do not result in a valid position.
	 * 				| ...
	 */
	public Position offset(long offsetX, long offsetY, long offsetZ) 
		throws IllegalArgumentException 
	{
		long x = getX() + offsetX;
		long y = getY() + offsetY;
		long z = getZ() + offsetZ;
		// Checks already for negative values, but ALSO for overflows,
		// since Long.MAX_VALUE + Long.MAX_VALUE < 0
		return new Position(x, y, z);
	}

	/**
	 * Return the current position, with a given position as offset.
	 *   
	 * @param 	offsetPosition
	 * 				The position by which to offset.
	 * @return	A new position with coordinates that are the sum of the coordinates of 
	 * 			this position and the given offset position.
	 * 			| ...
	 * @throws	IllegalArgumentException
	 * 				The offset by the given position does not result in a valid position.
	 * 				| ...
	 * @throws 	NullPointerException
	 * 				The given offset position is not effective.
	 *				| offsetPosition == null 		
	 */
	public Position offset(Position offsetPosition) 
		throws IllegalArgumentException, NullPointerException
	{
		if(offsetPosition == null)
			throw new NullPointerException();
		return offset(offsetPosition.x, offsetPosition.y, offsetPosition.z);
	}
	
	/**
	 * Return the current position, multiplied by a given factor.
	 * 
	 * @param 	factor
	 * 				A factor to multiply this position with.
	 * @return	A new position that is the product of this each coordinate of this 
	 * 			position and the given factor.
	 * 			| result.isIdenticalTo(new Position(getX() * factor, getY() * factor, getZ() * factor))
	 * @throws	IllegalArgumentException
	 * 				The given factor is negative or the multiplication would overflow.
	 * 				| ...
	 */
	public Position multiply(long factor) {
		if (factor < 0)
			throw new IllegalArgumentException();
		long tmp = Long.MAX_VALUE / factor;
		if ((tmp < getX()) || (tmp < getY()) || (tmp < getZ()))
			throw new IllegalArgumentException();
				
		return new Position(getX() * factor, getY() * factor, getZ() * factor);
	}
	
	/**
	 * Return whether this position lays between the given start position (inclusive) and the given stop position (inclusive).
	 * 
	 * @param	startPosition
	 * 				The start position.
	 * @param	stopPosition
	 * 				The stop position.
	 * @return	...
	 * 			| if(getX() > stopPosition.getX() || getX() < startPosition.getX())
	 * 			| 	then result == false
	 * @return	...
	 * 			| else if(getY() > stopPosition.getY() || getY() < startPosition.getY())
	 * 			| 	then result == false
	 * @return	...
	 * 			| else if(getZ() > stopPosition.getZ() || getZ() < startPosition.getZ())
	 * 			| 	then result == false
	 * @return	...
	 * 			| else 
	 * 			| 	then result == true
	 * @throws	NullPointerException
	 * 				One of the given positions is ineffective.
	 * 				| ...
	 * @throws	IllegalArgumentException
	 * 				For some coordinate, the stop position is less than the start position.
	 * 				| ...		
	 */
	public boolean isBetween(Position startPosition, Position stopPosition) throws NullPointerException, IllegalArgumentException{
		if(startPosition == null || stopPosition == null)
			throw new NullPointerException();
		if((startPosition.getX() > stopPosition.getX()) ||
			(startPosition.getY() > stopPosition.getY()) || 
			(startPosition.getZ() > stopPosition.getZ()))
			throw new IllegalArgumentException();
		if(getX() > stopPosition.getX() || getX() < startPosition.getX())
			return false;
		if(getY() > stopPosition.getY() || getY() < startPosition.getY())
			return false;
		if(getZ() > stopPosition.getZ() || getZ() < startPosition.getZ())
			return false;
		return true;

	}
	
	/**
	 * Return true if this position is equal to the other object.
	 * 
	 * @param	other
	 * 				The other object to compare with.
	 * @return	False if the given object is not a position.
	 * 			| ...
	 * @return	Otherwise, true if and only if the given object is a position that is identical to this position.
	 * 			| ...
	 */
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Position))
			return false;
		return isIdenticalTo((Position)other);
	}
	
	/**
	 * Get a hash code for the this position.
	 */
	@Override
	public int hashCode() {
		return new Long(getX()).hashCode() + new Long(getY()).hashCode() + new Long(getZ()).hashCode(); 
	}
	
	/**
	 * Return a string representation of the object. 
	 */
	@Override
	public String toString() {
		return "(" + getX() + "," + getY() + "," + getZ() + ")"; 
	}
}
