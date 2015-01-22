package project.dungeons;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import project.misc.Direction;
import project.squares.Square;
import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Raw;

/**
 * A dungeon, that is, a collection of squares.
 * 
 * @author 	Stef Noten & Jasper Hilven
 * @version	3.2
 * @Invar	The collection of squares of this dungeon is effective, if and only if this dungeon is not terminated.
 * 			| (getSquares() == null) == isTerminated()
 * @Invar	The maximum position of this dungeon is effective, if and only if this dungeon is not terminated.
 * 			| (getMaximumPosition() == null) == isTerminated()
 * @Invar	Not more than 20 percent of the squares of this dungeon has slippery material, if this dungeon is not terminated.
 * 			| if (!isTerminated)
 * 			| 	COUNT(square.hasSlipperyMaterial() : square in getSquares().values()) * 5 <= getNbSquares()
 * @Invar	This dungeon can never have a square with position (x,x,x) or a position of which
 * 			one of the coordinates exceeds the corresponding coordinate of the maximum position.
 * 			| if (!isTerminated())
 * 			| 	for each x in long 
 * 			|		!hasSquareAt(new Position(x,x,x))
 * 			| 	&& for each position in Position
 * 			|		!hasSquareAt(position) || 
 * 			|		((pos.getX() <= getMaximumPosition().getX()) && 
 * 			|			(pos.getY() <= getMaximumPosition().getY()) && 
 * 			|			(pos.getZ() <= getMaximumPosition().getZ()))
 * @Invar	This dungeon has a proper parent dungeon.
 *			| hasProperParentDungeon()
 * @Invar 	Adjacent squares act as neighbours.
 * 			| areAdjacentSquaresConnected()
 */
public abstract class Dungeon<SquareT extends Square> implements Iterable<SquareT> {
	
	/**
	 * Initialise a new dungeon with a given maximum position.
	 *
	 * @param	maximumPosition
	 * 				The position that will be the maximum position of this dungeon, that is, the maximum position that a square can have
	 * 				when added to this dungeon.
	 * @post	...
	 *			| new.getMaximumPosition() == maximumPosition
	 * @post	...
	 * 			| new.isTerminated() == false
	 */
	@Raw
	protected Dungeon(Position maximumPosition) {
		this.maximumPosition = maximumPosition;
		this.terminated = false;
	}
	
	/**
	 * Initialise a new dungeon.
	 * 
	 * @effect	...
	 * 			| this(new Position(100,100,100));
	 */
	@Raw
	protected Dungeon() {
		this(new Position(100,100,100));
	}

	/**
	 * Add a square to this dungeon at a given position and merge its borders 
	 * with the respective borders of its new neighbours (if present).

	 * @param	position
	 * 				The position to set the given square to in this dungeon.
	 * @param	square
	 * 				The square to set.
	 * @post	...
	 * 			| new.getSquareAt(position) == square
	 * @effect	...
	 * 			| if (this != getRootDungeon())
	 * 			|	getRootDungeon().setSquareAt(getAbsolutePosition(position), square);
	 * 			| square.connect(getNeighboursAt(position));
	 * 			| putSquareAt(position, square)
	 * @throws 	NullPointerException
	 * 				The given position or square is not effective.
	 * 				| (position == null) || (square == null)
	 * @throws	IllegalArgumentException
	 * 				...
	 * 				| !getRootDungeon().canSetSquareAt(position, square)
	 */
	public void setSquareAt(Position position, SquareT square)
		throws NullPointerException, IllegalArgumentException,IllegalStateException
	{
		if (this != getRootDungeon())
			getRootDungeon().setSquareAt(getAbsolutePosition(position), square);
		if (!canSetSquareAt(position, square))
			throw new IllegalArgumentException();
		square.connect(getNeighboursAt(position));
		putSquareAt(position, square);
	}
	
	/**
	 * Add a square to the list of squares.
	 *
	 * @param 	position
	 * 				The position to add the square at.
	 * @param 	square
	 * 				The square to add.
	 * @post	...
	 * 			| new.getSquareAt(position) == square
	 * @throws	NullPointerException
	 * 				The given position or the given square is not effective.
	 * 				| (position == null) || (square == null)
	 * @throws 	IllegalStateException
	 *				...
	 *				| isTerminated()
	 */
	protected abstract void putSquareAt(Position position, SquareT square) throws IllegalStateException, NullPointerException;
	
	/**
	 * Check whether a square can be added to the dungeon at a given position.
	 *  
	 * @param	position
	 * 				The position to set the given square in this dungeon.
	 * @param	square
	 * 				The square to set.
	 * @result	...
	 * 			| if(isTerminated())
	 * 			|	result == false
	 * @result	The root dungeon controls the addition logic of a square.
	 * 			| else if (getRootDungeon() != this)
	 * 			| 	then result == getRootDungeon().canSetSquareAt(position, square)
	 * @result	...
	 * 			| else if (getNbSquares() == Integer.MAX_VALUE)
	 *			|	then result == false
	 * @result	...
	 * 			| else if (hasSquareAt(position))
	 * 			|	then result == false
	 * @result	...
	 * 			| else if ((position.getX() > getMaximumPosition().getX()) || (position.getY() > getMaximumPosition().getY()) ||
	 *			|	  (position.getZ() > getMaximumPosition().getZ()))
	 *			| 		then result == false
	 * @result	...
	 * 			| else if ((position.getX() == position.getY()) && (position.getX() == position.getZ()))
	 * 			|	then result == false
	 * @result	...
	 * 			| else if (getSquares().containsValue(square) || square.hasNeighbours())
	 * 			|	then result == false
	 * @result	...
	 * 			| else if (!canSetSquareForSlipperiness(square))
	 *			| 	then result == false
	 * @result	...
	 * 			| else if (!square.canConnect(getNeighboursAt(position)))
	 * 			|	then result == false
	 * @throws	NullPointerException
	 * 				The given position or square is not effective.
	 * 				| (position == null) || (square == null)
	 */
	public boolean canSetSquareAt(Position position, SquareT square)	
		throws NullPointerException 
	{
		if (isTerminated())
			return false;
		if (getRootDungeon() != this)
			return getRootDungeon().canSetSquareAt(getAbsolutePosition(position), square);
		if (getNbSquares() == Integer.MAX_VALUE)
			return false;
		if (hasSquareAt(position))
			return false;
		if ((position.getX() > getMaximumPosition().getX()) || (position.getY() > getMaximumPosition().getY()) ||
				(position.getZ() > getMaximumPosition().getZ()))
			return false;
		if ((position.getX() == position.getY()) && (position.getY() == position.getZ()))
			return false;
		if (getSquares().containsValue(square) || square.hasNeighbours())
			return false; // This square was already added to this or another Dungeon.
		if (!canSetSquareForSlipperiness(square))//alleen checken voor root dungeon
			return false;
		if (!square.canConnect(getNeighboursAt(position)))
			return false;
		return true;
	}
	
	/**
	 * Get a map of the neighbours with corresponding directions at a specific position in this dungeon.
	 *
	 * @param 	position
	 * 				The position to return the neighbours of.
	 * @return	If this dungeon is not the root dungeon, the method is passed through to the root dungeon.
	 * 			| if (this != getRootDungeon())
	 * 			|	then result == getRootDungeon().getNeighboursAt(position)
	 * @return	...
	 * 			| else
	 * 			|	then for each direction in Direction
	 * 			|		hasSquareAt(position.getAdjacentPosition(direction)) ?
	 * 			|			result.get(direction) == getSquareAt(position.getAdjacentPosition(direction)) :
	 * 			|			!result.containsKey(direction)
	 * @throws	NullPointerException
	 * 				The given position is not effective.
	 * 				| position == null
	 * @throws 	IllegalStateException
	 *				This dungeon is terminated.
	 *				| isTerminated()
	 */
	public Map<Direction, Square> getNeighboursAt(Position position) 
		throws NullPointerException, IllegalStateException 
	{
		if (position == null)
			throw new NullPointerException();
		if (getRootDungeon() != this)
			return getRootDungeon().getNeighboursAt(getAbsolutePosition(position));
		Map<Direction, Square> neighbours = new HashMap<Direction, Square>();
		for (Direction direction : Direction.values()) {
			try {
				Position pos = position.getAdjacentPosition(direction);
				if (hasSquareAt(pos))
					neighbours.put(direction, getSquareAt(pos));
			} catch (IllegalArgumentException ex) {
				// The adjacent position is not a valid position.
			}
		}
		return neighbours;
	}
	
	/**
	 * Check whether the given square can be added based on the criteria of slipperiness.
	 *
	 * @param	newSquare
	 * 				The square to check the validity for based on slipperiness.  
	 * @return	True if not more than 20% of the squares will have a slippery material after 
	 * 			adding the given square, false otherwise. If the given square does not have
	 * 			a slippery material, it can always be added for this criteria.
	 * 			| if (newSquare.hasSlipperyMaterial())
	 * 			|	result == (COUNT(square.hasSlipperyMaterial() : square in getSquares().values()) + 1) * 5 <= (getNbSquares() + 1)
	 * 			| else
	 * 			|	result == true
	 * @throws 	IllegalStateException
	 *				...
	 *				| isTerminated() == true 
	 * @throws 	NullPointerException
	 * 				The given square is not effective.
	 * 				| newSquare == null
	 */
	public boolean canSetSquareForSlipperiness(SquareT newSquare) throws IllegalStateException, NullPointerException {
		if (isTerminated())
			throw new IllegalStateException();
		if (newSquare == null)
			throw new NullPointerException();
		if (!newSquare.hasSlipperyMaterial())
			return true;
		int maxAllowedSlipperySquares = (int)(getNbSquares() / 5.0 + 0.2);//rounded downwards, so no problem.
		int totalSlipperySquares = 1;
		for (SquareT square : getSquares().values()) {
			if (square.hasSlipperyMaterial()) {
				totalSlipperySquares++;
				if (totalSlipperySquares > maxAllowedSlipperySquares)
					return false;
			}
		}
		return true;
	}

	/**
	 * Return the number of squares that this dungeon has.
	 * 
	 * @result	...
	 * 			| result == getSquares().size()
	 *
	 * @throws IllegalStateException
	 *				...
	 *				| isTerminated()
	 */
	public abstract int getNbSquares() throws IllegalStateException;
	
	/**
	 * Return the map of squares that this dungeon has, each square with his key position.
	 */
	public abstract Map<Position, SquareT> getSquares();
	
	/**
	 * Return the square at the given position.
	 *
	 * @param	position
	 * 				The position at which the square to return is located.
	 * @return	The square at the given position.
	 * 			| result == getSquares().get(position)
	 * @throws 	NullPointerException
	 * 				The given position is not effective.
	 * 				| position == null
	 * @throws 	IllegalArgumentException
	 * 				...
	 * 				| !hasSquareAt(position)
	 * @throws 	IllegalStateException
	 *				...
	 *				| isTerminated()
	 */
	public abstract SquareT getSquareAt(Position position)
		throws IllegalArgumentException, NullPointerException, IllegalStateException;
	
	/**
	 * Remove a square at the given position.
	 *
	 * @param	position
	 * 				The position to remove a square at.
	 * @post	...
	 * 			| !new.hasSquareAt(position)
	 * @post	...
	 * 			| if (hasSquareAt(position))
	 * 			| 	!(new getSquareAt(position)).hasNeighbours()
	 * @throws	NullPointerException
	 * 				...
	 * 				| position == null
	 * @throws IllegalStateException
	 *				...
	 *				| isTerminated()
	 */
	public abstract void removeSquareAt(Position position) 
		throws NullPointerException, IllegalStateException;
	
	/**
	 * Return whether this dungeon has a square at this position.
	 * 
	 * @param 	position
	 * 				The position at which to check the presence of a square.
	 * @return	True if and only if this dungeon has a square at the given position.
	 * 			| result == getSquares().containsKey(position)
	 * @throws 	NullPointerException
	 * 				The given position is not effective.
	 * 				| position == null
	 * @throws IllegalStateException
	 *				...
	 *				| isTerminated()
	 */
	public abstract boolean hasSquareAt(Position position) 
		throws NullPointerException, IllegalStateException;
	
	/**
	 * Check whether all adjacent squares in this dungeon have their neighbouring borders in common.
	 * 
	 * @return	If this dungeon is not the root dungeon, the method is passed through to the root dungeon.
	 * 			| if (getRootDungeon() != this)
	 * 			|	then result == getRootDungeon().areAdjacentSquaresConnected()
	 * @return	True if all adjacent squares in this dungeon have their neighbouring borders in common, false otherwise.
	 * 			| result == 
	 * 			|	for each Position pos1, pos2 in getSquares().keySet()
	 * 			|		!pos1.isAdjacentTo(pos2) ||
	 * 			|		getSquareAt(pos1).getNeighbour(pos1.getAdjacentCoordinateDirection(pos2)) == getSquareAt(pos2)
	 * @throws IllegalStateException
	 *				...
	 *				| isTerminated()
	 */
	public boolean areAdjacentSquaresConnected() throws IllegalStateException {
		if (getRootDungeon() != this)
			return getRootDungeon().areAdjacentSquaresConnected();
		
		for (Position position : getSquares().keySet()) {
			SquareT square = getSquareAt(position);
			for (Direction direction : Direction.values()) {
				try {
					Position neighbourPosition = position.getAdjacentPosition(direction);
					if (hasSquareAt(neighbourPosition)) {
						if (square.getNeighbour(direction) != getSquareAt(neighbourPosition))
							return false;
					}
				}
				catch(IllegalArgumentException e){
					//Illegal position.
				}
			}
		}
		return true;
	}
	
	/**
	 * Return the maximum position of this dungeon, that is, a position giving the 
	 * maximum coordinates a square can have.
	 */
	@Basic
	public Position getMaximumPosition() {
		return maximumPosition;
	}

	/**
	 * Set the maximum position to the given position.
	 * 
	 * @param	position
	 * 				The position to set the maximum position to.
	 * @post	The new maximum position is equal to the given value.
	 * 			| new.getMaximumPosition() == position
	 * @throws	IllegalArgumentException
	 * 				...
	 *				| !canHaveAsMaximumPosition(position)
	 * @throws IllegalStateException
	 *				...
	 *				| isTerminated()
	 */
	public void setMaximumPosition(Position position) 
		throws IllegalArgumentException, IllegalStateException
	{
		if (!canHaveAsMaximumPosition(position)) 
			throw new IllegalArgumentException();
		this.maximumPosition = position;
	}

	/**
	 * Check whether a position is a valid maximum position.
	 * 
	 * @param	maximumPosition
	 * 				The position to check the validity for.
	 * @return	False if the given value is not effective and this dungeon is not terminated, true if the given value is ineffective and this dungeon is terminated.
	 * 			| if (position == null)
	 * 			|	then result == isTerminated()
	 * @return	False if one or more coordinate values are smaller than those of the current maximum position.
	 * 			| else if ((position.getX() < getMaximumPosition().getX()) ||
	 * 			|	  (position.getY() < getMaximumPosition().getY()) ||
	 * 			|	  (position.getZ() < getMaximumPosition().getZ()))
	 * 			|	then result == false
	 * @return	False if this dungeon has a parent dungeon and the new maximum position would be greater than 
	 * 			that of the parent dungeon.
	 * 			| else if ((getParentDungeon() != null) && 
	 * 			|			(!getParentDungeon().getPositionOfSubDungeon(this).offset(position).isBetween(new Position(), getParentDungeon().getMaximumPosition())))
	 * 			|	then result == false
	 * @return	False if this dungeon has a parent dungeon and if one of its sub dungeons would overlap with the given new maximum position.
	 * 			| else if ((getParentDungeon() != null) && 
	 * 			|		for some position in parent.getSubDungeons().keySet()
	 * 			|			(getSubDungeonAt(position) != this) && getSubDungeonAt(position).overlaps(otherPosition, maximumPosition))
	 * 			|	then result == false
	 * @return 	True otherwise.
	 * 			| else
	 * 			|	then result == true
	 * @throws IllegalStateException
	 *				...
	 *				| isTerminated() && (maximumPosition != null)
	 */
	public boolean canHaveAsMaximumPosition(Position maximumPosition) throws IllegalStateException {
		if (maximumPosition == null)
			return isTerminated();
		if (isTerminated())
			throw new IllegalStateException();
		if (maximumPosition.getX() < getMaximumPosition().getX())
			return false;
		if (maximumPosition.getY() < getMaximumPosition().getY())
			return false;
		if (maximumPosition.getZ() < getMaximumPosition().getZ())
			return false;
		CompositeDungeon<SquareT> parent = getParentDungeon();
		if (parent != null) {
			if (!parent.getPositionOfSubDungeon(this).offset(maximumPosition).isBetween(new Position(), parent.getMaximumPosition()))
				return false;
			for (Position otherPosition : parent.getSubDungeons().keySet()) {
				Dungeon<SquareT> dungeon = parent.getSubDungeonAt(otherPosition);
				if ((dungeon != this) && dungeon.overlaps(otherPosition, maximumPosition))
					return false;
			}
		}
		return true;
	}

	/**
	 * A variable registering the maximum position of this dungeon.
	 */
	private Position maximumPosition;
	
	/**
	 * Check whether the given range overlaps with this dungeon. For this dungeon, the origin used is the position of 
	 * this dungeon in its parent dungeon, or the zero position if it has no parent dungeon.
	 * 
	 * @param 	otherOrigin
	 * 				The origin of the other dungeon.
	 * @param	otherMaximumPosition
	 *				The maximum position of the other dungeon.
	 * @return	True if for each axis the ranges of this and the given other dungeon overlap, that is, the range from the respective 
	 * 			origin to the respective maximum position offset by that origin, false otherwise.
	 * 			| ...
	 * @throws	NullPointerException
	 * 				One of the given parameters is not effective.
	 * 				| (otherOrigin == null || otherMaximumPosition == null)				
	 * @throws IllegalStateException
	 *				...
	 *				| isTerminated()
	 *
	 */
	public boolean overlaps(Position otherOrigin, Position otherMaximumPosition) throws NullPointerException, IllegalStateException
	{
		if(isTerminated())
			throw new IllegalStateException();
		Position origin;
		if (getParentDungeon() != null)
			origin = getParentDungeon().getPositionOfSubDungeon(this);
		else
			origin = new Position();
		
		if (otherOrigin == null || otherMaximumPosition == null)
			throw new NullPointerException();
		return origin.isBetween(otherOrigin, otherOrigin.offset(otherMaximumPosition)) || otherOrigin.isBetween(origin, origin.offset(getMaximumPosition()));
	}
	
	/**
	 * Return the parent dungeon of this dungeon, null if there is no parent.
	 */
	@Basic
	public CompositeDungeon<SquareT> getParentDungeon(){
		return this.parentDungeon;
	}
	/**
	 * Return the root dungeon of this dungeon, that is, itself if it has no parent dungeon, otherwise the root dungeon of this parent.
	 *
	 * @return	This dungeon if this dungeon has no parent dungeon.
	 * 			| if(getParentDungeon == null)
	 * 			|	then result == this
	 * 			| else 
	 * 			|	then result == getParentDungeon.getRootDungeon()
	 * @throws IllegalStateException
	 *				...
	 *				| isTerminated() == true 
	 */
	public Dungeon<SquareT> getRootDungeon() throws IllegalStateException {
		if (isTerminated())
			throw new IllegalStateException();
		if (getParentDungeon() != null)
			return getParentDungeon().getRootDungeon();
		return this;
	}
	
	/**
	 * Return whether this dungeon has a proper parent dungeon.
	 * 
	 * @return	True if the parent dungeon of this dungeon is not effective.
	 * 			| if (getParentDungeon() == null)
	 * 			|	then result == true
	 * @return 	Otherwise, false if this dungeon is terminated
	 * 			| else if(isTerminated())
	 * 			| 	then result == false
	 * @return	Otherwise, false if the parent dungeon does not have this dungeon as subdungeon.
	 * 			| else if (!getParentDungeon().hasSubDungeon(this))
	 * 			|	then result == false
	 * @return 	Otherwise, true if and only if this dungeon is a proper subdungeon of the parent dungeon.
	 * 			| else
	 * 			|	then result == getParentDungeon().hasProperSubDungeonAt(getParentDungeon().getPositionOfSubDungeon(this))
	 */
	public boolean hasProperParentDungeon() {
		if (getParentDungeon() == null) 
			return true;
		if (isTerminated())
			return false;
		if (!getParentDungeon().hasSubDungeon(this))
			return false;
		return getParentDungeon().hasProperSubDungeonAt(getParentDungeon().getPositionOfSubDungeon(this));
	}
	
	/**
	 * Set the parent dungeon of this dungeon.
	 *
	 * @param 	parentDungeon
	 *				The parent dungeon of this square to set.
	 * @Pre		...
	 * 			| if (parentDungeon == null) 
	 * 			| 	then ((getParentDungeon() == null) || !getParentDungeon().hasSubDungeon(this))
	 * 			| else
	 *			| 	then (!isTerminated() && parentDungeon.hasSubDungeon(this) && (getParentDungeon() == null))
	 * @Post	The given parent dungeon is set as parent dungeon.
	 * 			| new.getParentDungeon() == parentDungeon
	 */
	@Raw
	public void setParentDungeon(@Raw CompositeDungeon<SquareT> parentDungeon){
		if (parentDungeon == null) 
			 assert ((getParentDungeon() == null) || !getParentDungeon().hasSubDungeon(this));
		else
			 assert (!isTerminated() && parentDungeon.hasSubDungeon(this) && (getParentDungeon() == null));
		
		this.parentDungeon = parentDungeon;
	}
	
	/**
	 * A variable registering the parent dungeon of this dungeon.
	 */
	private CompositeDungeon<SquareT> parentDungeon;
	
	/**
	 * Return whether this dungeon has a singular dungeon at the given position.
	 * 
	 * @return	False if the position is greater then its maximum position.
	 * 			|...
	 * @throws	NullPointerException
	 *				The given position is ineffective.
	 *				| position == null
	 */
	protected boolean hasSingularDungeonAt(Position position) throws NullPointerException {
		if (position == null)
			throw new NullPointerException();
		if (!position.isBetween(new Position(), getMaximumPosition()))
			return false;
		return true;
	}
	
	
	/**
	 * Return the equivalent position of the given position in this dungeon's root dungeon.
	 * 
	 * @param	position
	 *	 			The position of which the equivalent position in this dungeons root dungeon should be calculated.
	 * @return	If there is a parent dungeon, then the resulting position is equal to the absolute position of the given position 
	 * 			in this dungeon's parent dungeon, offset with the position of this dungeon in its parent dungeon.
	 * 			| if (getParentDungeon() != null)
	 * 			| 	then result.equals(getParentDungeon().getAbsolutePosition(position).offset(getParentDungeon().getPositionOfSubDungeon(this)))
	 * @return 	Else return this position.
	 * 			| else
	 * 			| 	then result == position
	 * @throws	NullPointerException
	 * 				The given position is not effective.
	 * 				| position == null
	 */
	public Position getAbsolutePosition(Position position) {
		if (position == null)
			throw new NullPointerException();
		if (getParentDungeon() == null)
			return position;
		return getParentDungeon().getAbsolutePosition(position).offset(getParentDungeon().getPositionOfSubDungeon(this));
	}

	/**
	 * Get an iterator that iterates over all the squares of this dungeon.
	 * 
	 * @throws IllegalStateException
	 *				...
	 *				| isTerminated()
	 */
	public Iterator<SquareT> iterator() throws IllegalStateException {
		return iterator(null); 
	}
	
	/**
	 * Get an iterator that iterates over all the squares of this dungeon that satisfy a given predicate.
	 * 
	 * @param 	predicate
	 * 				A predicate that the squares returned by the iterator have to satisfy. If the predicate 
	 * 				is not effective, no condition is tested, that is, the resulting iterator iterates over all squares. 				
	 * @throws 	IllegalStateException
	 *				...
	 *				| isTerminated()
	 */
	public abstract Iterator<SquareT> iterator(SquarePredicate<? super SquareT> predicate) throws IllegalStateException;
	
	/**
	 * Return a set of all the squares in this dungeon that satisfy a given predicate.
	 * 
	 * @param 	predicate
	 * 				A predicate that the squares in the resulting set have to satisfy.
	 * @Pre		The given predicate is effective.
	 * 			| predicate != null
	 * @return	All the squares in this dungeon that satisfy the given predicate, are member of the resulting set.
	 * 			| for each square in getSquares()
	 * 			|	!predicate.satisfies(square, this) || result.contains(square)
	 * @return	No null element, no squares that are not part of this dungeon and no squares that do not satisfy 
	 * 			the given predicate are member of the resulting set.
	 * 			| for each square in {Square union null}
	 * 			|	if ((square == null) || !getSquares().containsValue(square) || !predicate.satisfies(square, this))
	 * 			|		then !result.contains(square)
	 * @throws IllegalStateException
	 *				...
	 *				| isTerminated()
	 */
	public Set<SquareT> getAllSquaresSatisfying(SquarePredicate<? super SquareT> predicate) throws IllegalStateException {
		if (isTerminated())
			throw new IllegalStateException();
		Set<SquareT> result = new HashSet<SquareT>();
		Iterator<SquareT> iterator = iterator(predicate);
		while (iterator.hasNext())
			result.add(iterator.next());
		return result;
	}

	/**
	 * Terminate this dungeon.
	 *
	 * @post	This dungeon is terminated.
	 * 			| new.isTerminated()
	 * @post	This dungeon is removed from its parent dungeon, if it has one.
	 * 			| if (getParentDungeon() != null)
	 * 			|	then ! (new getParentDungeon()).hasSubDungeon(this)
	 * @post	Each square in this dungeon is removed and disconnected.
	 * 			| for each square in getSquares()
	 * 			|	! (new square).hasNeighbours()
	 * @post	The map of squares of this dungeon is not effective. 
	 * 			| new.getSquares() == null
	 * @post	The maximum position is not effective.
	 * 			| new.getMaximumPosition() == null
	 * @throws IllegalStateException
	 *				This dungeon is already terminated.
	 *				| isTerminated() 
	 */
	public void terminate() {
		if (isTerminated())
			throw new IllegalStateException();
		for (Position position : getSquares().keySet())
			removeSquareAt(position);
		if(getParentDungeon() != null)
			getParentDungeon().removeSubDungeonAt(getParentDungeon().getPositionOfSubDungeon(this));
		this.maximumPosition = null;
		this.terminated = true;
	}

	/**
	 * Check whether this square is terminated.
	 */
	@Basic
	public boolean isTerminated(){
		return terminated;
	}
	
	/**
	 * Variable registering whether this square is terminated.
	 */
	private boolean terminated;
}
