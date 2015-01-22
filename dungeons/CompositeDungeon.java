package project.dungeons;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import project.squares.Square;
import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Raw;

/**
 * A composite dungeon, that is, a dungeon that can contain other dungeons.
 * 
 * @author 	Stef Noten & Jasper Hilven
 * @version	1.0
 * @Invar	The collection of sub dungeons of this dungeon is effective, if and only if this dungeon is not terminated.
 * 			| (getSubDungeons() == null) == isTerminated()
 * @Invar	Each sub dungeon of this dungeon is a proper sub dungeon.
 * 			| if (!isTerminated())
 * 			| 	then for each position in getSubDungeons().keySet()
 * 			| 			hasProperSubDungeonAt(position)
 */
public class CompositeDungeon<SquareT extends Square> extends Dungeon<SquareT> {
	
	/**
	 * Initialise a composite dungeon, that is, a dungeon that can contain other dungeons.
	 *
	 * @param	maximumPosition
	 * 				The maximum position of this composite dungeon.
	 * @Post	The collection of sub dungeons of this dungeon is effective.
	 * 			| ...
	 * @Post	There are no sub dungeons.
	 * 			| new.getNbSubDungeons() == 0
	 * @Post	The maximum position of this new composite dungeon is equal to the given maximum position.
	 * 			|...
	 */
	public CompositeDungeon(Position maximumPosition) {
		super(maximumPosition);
		this.subDungeons = new HashMap<Position, Dungeon<SquareT>>();
	}
	
	/**
	 * Initialise a composite dungeon, that is, a dungeon that can contain other dungeons.
	 *
	 * @Effect	...
	 * 			| this(new Position(100,100,100));
	 */
	@Raw
	public CompositeDungeon() {
		this(new Position(100,100,100));
	}
	
	/**
	 * Add a square to the list of squares.
	 */
	@Override
	protected void putSquareAt(Position position, SquareT square) throws IllegalStateException, NullPointerException {
		if(isTerminated())
			throw new IllegalStateException();
		if ((position == null) || (square == null))
			throw new NullPointerException();
		getSubDungeonAt(position).putSquareAt(position.subtract(getPositionOfSubDungeon(getSubDungeonAt(position))), square);
	}
	
	/**
	 * Check whether a square can be added to the dungeon at a given position.
	 *
	 * @Return	Otherwise, true if and only if this composite dungeon has a singular dungeon at the given position.
	 *			| else 
	 *			| 	then result == hasSingularDungeon(position)
	 */
	@Override
	public boolean canSetSquareAt(Position position, SquareT square) 
		throws NullPointerException
	{
		if (!super.canSetSquareAt(position, square))
			return false;
		return hasSingularDungeonAt(position);
	}
	
	/**
	 * Remove a square at the given position.
	 * 
	 * @Effect	...
	 * 			| if (hasSquareAt(position))
	 *			|	getSubDungeonAt(position).removeSquareAt(position.subtract(getPositionOfSubDungeon(getSubDungeonAt(position))));
	 */
	@Override
	public void removeSquareAt(Position position) 
		throws NullPointerException, IllegalStateException
	{
		if (hasSquareAt(position))
			getSubDungeonAt(position).removeSquareAt(position.subtract(getPositionOfSubDungeon(getSubDungeonAt(position))));
	}

	/**
	 * Return whether this dungeon has a square at this position. 
	 */
	@Override
	public boolean hasSquareAt(Position position) 
		throws NullPointerException, IllegalStateException
	{
		if(!hasSubDungeonAt(position))
			return false;
		Dungeon<SquareT> subDungeon = getSubDungeonAt(position);
		return subDungeon.hasSquareAt(position.subtract(getPositionOfSubDungeon(subDungeon)));
	}
	
	/**
	 * Return the square at the given position. 
	 */
	@Override
	public SquareT getSquareAt(Position position)
		throws IllegalArgumentException, NullPointerException, IllegalStateException
	{
		if(!hasSquareAt(position))
			throw new IllegalArgumentException();
		Dungeon<SquareT> subDungeon = getSubDungeonAt(position);
		return subDungeon.getSquareAt(position.subtract(getPositionOfSubDungeon(subDungeon)));
	}
	
	/**
	 * Return the number of squares that this dungeon has.
	 */
	@Override
	public int getNbSquares() throws IllegalStateException {
		if(isTerminated())
			throw new IllegalStateException();
		int result = 0;
		for (Dungeon<SquareT> dungeon : subDungeons.values())
			result += dungeon.getNbSquares();
		return result;
	}

	/**
	 * Return the map of squares that this dungeon has, each square with its position relative to this dungeon.
	 */
	@Override
	public Map<Position, SquareT> getSquares() {
		if (isTerminated())
			return null;
		Map<Position, SquareT> retMap = new HashMap<Position, SquareT>();
		for (Dungeon<SquareT> subDungeon : getSubDungeons().values()) {
			for (Position position : subDungeon.getSquares().keySet())
				retMap.put(getPositionOfSubDungeon(subDungeon).offset(position), subDungeon.getSquareAt(position));
		}
		return retMap;
	}
	
	/**
	 * Return the number of direct sub dungeons that this dungeon has.
	 * 
	 * @result	...
	 * 			| result == getSubDungeons().size()
	 * @throws	IllegalStateException
	 * 				This dungeon is terminated.
	 * 				| isTerminated()
	 */
	public int getNbSubDungeons() throws IllegalStateException{
		if(isTerminated())
			throw new IllegalStateException();
		return this.subDungeons.size();
	}
	
	/**
	 * Return the map of direct dungeons that this dungeon has, each dungeon with its position relative to this dungeon.
	 */
	@Basic
	public Map<Position, Dungeon<SquareT>> getSubDungeons(){
		return (this.subDungeons == null) ? null : new HashMap<Position, Dungeon<SquareT>>(this.subDungeons);
	}
	
	/**
	 * Return the dungeon that lays at the given position.
	 * 
	 * @param	position
	 * 				The position at which the wanted dungeon should be returned.
	 * @Return	...
	 * 			| if (getSubDungeons().containsKey(position))
	 * 			|	then result == getSubDungeons().get(position)
	 * @Return	Else if the given position lays in the bounds of one of the sub dungeons of this composite dungeon, 
	 * 			then that dungeon is returned.
	 * 			| ...
	 * @Return	Else, null.
	 * 			| else
	 * 			|	then result == null
	 * @throws	NullPointerException
	 * 				The given position is not effective.
	 * 				| ...
	 * @throws	IllegalStateException
	 * 				This dungeon is terminated.
	 * 				| isTerminated()
	 */
	public Dungeon<SquareT> getSubDungeonAt(Position position) throws NullPointerException, IllegalStateException {
		if(isTerminated())
			throw new IllegalStateException();
		if(position == null)
			throw new NullPointerException();
		
		if (subDungeons.containsKey(position))
			return subDungeons.get(position);
		
		for (Position subPosition : getSubDungeons().keySet()) 
		{
			Dungeon<SquareT> subDungeon = getSubDungeons().get(subPosition);
			if (position.isBetween(subPosition, subDungeon.getMaximumPosition().offset(subPosition)))
				return subDungeon;
		}
		return null;
	}
	
	/**
	 * Check whether this dungeon has a dungeon at a given position.
	 *
	 * @param	position
	 * 				The position to check if this dungeon has a sub dungeon on it.
	 * @Return	...
	 * 			| result == (getSubDungeonAt(position) != null)
	 * @throws 	NullPointerException
	 * 				The given position is ineffective.
	 * 				| position == null
	 * @throws	IllegalStateException
	 * 				This dungeon is terminated.
	 * 				| isTerminated()
	 */
	public boolean hasSubDungeonAt(Position position) throws NullPointerException, IllegalStateException {
		if(position == null)
			throw new NullPointerException();
		return getSubDungeonAt(position) != null;
	}
	
	/**
	 * Check whether this dungeon has the given dungeon as sub dungeon.
	 * 
	 * @param	subDungeon
	 * 				The dungeon to check whether it is in this dungeon.
	 * @param	firstLevel
	 * 				True if only the direct sub dungeons have to be searched through, false otherwise.
	 * @Return	If only the first level has to be searched through, the result is true if and only if this
	 * 			dungeon directly contains the given sub dungeon.
	 * 			| ...
	 * @Return	Otherwise, the result is true if and only if this dungeon or one of its sub dungeons contains
	 * 			the given sub dungeon.
	 * 			| ...
	 * @throws	NullPointerException
	 * 				The given sub dungeon is ineffective.
	 * 				| ...
	 * @throws	IllegalStateException
	 * 				This dungeon is terminated.
	 * 				| isTerminated()
	 */
	public boolean hasSubDungeon(Dungeon<SquareT> subDungeon, boolean firstLevel) 
		throws NullPointerException,IllegalStateException 
	{
		if(isTerminated())
			throw new IllegalStateException();
		if(subDungeon == null)
			throw new NullPointerException();
		if (subDungeons.containsValue(subDungeon)) 
			return true;
		if (firstLevel)
			return false;
		for (Dungeon<SquareT> child : getSubDungeons().values()) {
			if ((child instanceof CompositeDungeon<?>) &&
					((CompositeDungeon<SquareT>)child).hasSubDungeon(subDungeon, false))
				return true;
		}
		return false;
	}
	
	/**
	 * Check whether this dungeon has the given sub dungeon as direct sub dungeon.
	 * 
	 * @param	subDungeon
	 * 				The dungeon to check whether it is directly in this dungeon.
	 * @Return	True if and only if the given dungeon is a direct sub dungeon of this dungeon.
	 * 			| result == hasSubDungeon(subDungeon, true)
	 * @throws	NullPointerException
	 * 				The given sub dungeon is ineffective.
	 * 				| ...
	 * @throws	IllegalStateException
	 * 				This dungeon is terminated.
	 * 				| isTerminated()
	 */
	public boolean hasSubDungeon(Dungeon<SquareT> subDungeon) throws NullPointerException,IllegalStateException {
		return hasSubDungeon(subDungeon, true);
	}
	
	/**
	 * Return the position of the given sub dungeon relative to this dungeon.
	 * 
	 * @param	subDungeon
	 * 				The sub dungeon of this square of which the position is wanted.
	 * @Return 	...
	 * 			| getSubDungeons().get(result) == subDungeon
	 * @throws	NullPointerException
	 * 				The given sub dungeon is ineffective.
	 * 				| ...
	 * @throws	IllegalArgumentException
	 * 				...
	 * 				| !hasSubDungeon(subDungeon)
	 * @throws	IllegalStateException
	 * 				This dungeon is terminated.
	 * 				| isTerminated()
	 */
	public Position getPositionOfSubDungeon(Dungeon<SquareT> subDungeon) 
		throws NullPointerException, IllegalArgumentException, IllegalStateException
	{
		if(!hasSubDungeon(subDungeon))
			throw new IllegalArgumentException();
		for (Position position : getSubDungeons().keySet()) {
			if (getSubDungeons().get(position) == subDungeon)	
				return position;
		}
		assert false;
		return null;
	}

	/**
	 * Check whether this dungeon can set the given dungeon at the given position.
	 *
	 * @param	subDungeon
	 * 				The sub dungeon to set at the given position.
	 * @param	position
	 * 				The position at which the given sub dungeon should be set.
	 * @Post	...
	 * 			| new.getSubDungeonAt(position) == subDungeon
	 * @throws	IllegalArgumentException
	 * 				The given combination of sub dungeon and position is not valid, or the given dungeon already has a parent or is not empty.
	 * 				| !canHaveAsSubDungeonAt(subDungeon, position) || 
	 * 				| (subDungeon.getParentDungeon() != null) ||
	 * 				| (subDungeon.getNbSquares() != 0)
	 * @throws 	NullPointerException
	 * 				The given sub dungeon or the given position is not effective.
	 * 				| (subDungeon == null || position == null) 
	 * @throws	IllegalStateException
	 * 				This dungeon is terminated.
	 * 				| isTerminated()
	 */
	public void setSubDungeonAt(Dungeon<SquareT> subDungeon, Position position)
		throws IllegalArgumentException, NullPointerException, IllegalStateException
	{
		if(!canHaveAsSubDungeonAt(subDungeon, position) || (subDungeon.getParentDungeon() == this) ||
				(subDungeon.getNbSquares() != 0))
			throw new IllegalArgumentException();
		this.subDungeons.put(position, subDungeon);
		subDungeon.setParentDungeon(this);
	}
	
	/**
	 * Check whether this dungeon can set the given dungeon at the given position.
	 * 
	 * @param	subDungeon
	 * 				The sub dungeon to set at the given position.
	 * @param	position
	 * 				The position at which the subDungeon should be set.
	 * 
	 * @Return 	...
	 * 			| if (!position.offset(subDungeon.getMaximumPosition()).isBetween(new Position(), getMaximumPosition()))
	 * 			|	then result == false
	 * @Return 	...
	 * 			| else if ((subDungeon.getParentDungeon() != null) && (subDungeon.getParentDungeon() != this))
	 * 			|	then result == false
	 * @Return	...
	 * 			| else if (for some position in getSubDungeons().keySet()
	 * 			|				(!otherPosition.equals(position) && getSubDungeonAt(otherPosition).overlaps(position, subDungeon.getMaximumPosition())) ||
	 * 			|				(otherPosition.equals(position) && (getSubDungeonAt(otherPosition) != subDungeon)) )
	 * 			|	then result == false
	 * @Return	This dungeon can not contain its own at any level.
	 * 			| else if ((subDungeon == this) || 
	 * 			|	  ((subDungeon instanceof CompositeDungeon<?>) && ((CompositeDungeon<SquareT>)subDungeon).hasSubDungeon(this, false)) )
	 * 			|	then result == false
	 * @Return	...
	 * 			| else
	 * 			|	then result == true
	 * @throws 	NullPointerException
	 * 				The given subDungeon or the given position is not effective.
	 * 				| (subDungeon == null || position == null) 
	 * @throws	IllegalStateException
	 * 				This dungeon or the given sub dungeon is terminated.
	 * 				| this.isTerminated() || subDungeon.isTerminated()
	 *
	 */
	public boolean canHaveAsSubDungeonAt(Dungeon<SquareT> subDungeon, Position position) throws IllegalStateException
	{
		if(this.isTerminated() || subDungeon.isTerminated())
			throw new IllegalStateException();
		if (subDungeon == null || position == null)
			throw new NullPointerException();
		
		// This check also covers the case when the position exceeds the maximum position. 
		if (!position.offset(subDungeon.getMaximumPosition()).isBetween(new Position(), getMaximumPosition()))
			return false;
		
		if ((subDungeon.getParentDungeon() != null) && (subDungeon.getParentDungeon() != this))
			return false;
		for (Position otherPosition : getSubDungeons().keySet()) {
			if (!otherPosition.equals(position)) {
				if (getSubDungeonAt(otherPosition).overlaps(position, subDungeon.getMaximumPosition()))
					  return false;
			} else {
				if (getSubDungeonAt(otherPosition) != subDungeon)
					return false;
			}
		}
		if (subDungeon == this)
			return false;
		if ((subDungeon instanceof CompositeDungeon<?>) && ((CompositeDungeon<SquareT>)subDungeon).hasSubDungeon(this, false))
			return false;
		return true;
	}
	
	/**
	 * Check whether this composite dungeon has a proper sub dungeon at the given position.
	 * 
	 * @param 	position
	 * 				The position of the sub dungeon.
	 * @Return	...
	 * 			| result == 
	 * 			|		hasSubDungeonAt(position) && 
	 * 			|		canHaveAsSubDungeonAt(getSubDungeonAt(position), position) &&
	 * 			|		(getSubDungeonAt(position).getParentDungeon() == this)			
	 * @throws 	NullPointerException
	 * 				The given position is not effective.
	 * 				| position == null
	* @throws	IllegalStateException
	 * 				This dungeon is terminated.
	 * 				| this.isTerminated()
	 */
	public boolean hasProperSubDungeonAt(Position position) throws NullPointerException, IllegalStateException {
		if (!hasSubDungeonAt(position))
			return false;
		if (!canHaveAsSubDungeonAt(getSubDungeonAt(position), position))
			return false;
		
		return getSubDungeonAt(position).getParentDungeon() == this;
	}
	
	/**
	 * Remove a sub dungeon at the given position.
	 * 
	 * @param	position
	 * 				The position at which the sub dungeon is to be removed.
	 * @Post 	There is no sub dungeon at the given position.
	 * 			| !hasSubDungeonAt(position)
	 * @throws	NullPointerException
     * 				The given position is not effective.
	 *				| position == null
	 * @throws	IllegalArgumentException
	 * 				The given position is not valid to remove a dungeon at.
	 * 				| !canRemoveSubDungeonAt(position)
	 * @throws	IllegalStateException
	 * 				This dungeon is terminated.
	 * 				| isTerminated()
	 */
	public void removeSubDungeonAt(Position position) 
		throws NullPointerException, IllegalArgumentException, IllegalStateException 
	{
		if(!canRemoveSubDungeonAt(position))
			throw new IllegalArgumentException();
		Position startPosition = getPositionOfSubDungeon(getSubDungeonAt(position));
		Dungeon<SquareT> oldDungeon = getSubDungeonAt(startPosition); 
		subDungeons.remove(startPosition);
		oldDungeon.setParentDungeon(null);
	}
	
	/**
	 * Check whether it is possible to remove the given sub dungeon at the given position.
	 *  
	 * @param	position
	 * 				The position at which the sub dungeon is to be removed.
	 * @Return	False if this dungeon has no dungeon at the given position.
	 * 			| if (!hasSubDungeonAt(position))
	 * 			| 	then result == false
	 * @Return	Else, false if the sub dungeon at the given position has still any squares left.
	 * 			| else if (getSubDungeonAt(position).getNbSquares() != 0)
	 * 			|	then result == false
	 * @Return 	Else, return true.
	 * 			| else
	 * 			|	then result == true
	 * @throws	NullPointerException
	 * 				The given position is not valid.
	 * 				| position == null
	 * @throws	IllegalStateException
	 * 				This dungeon is terminated.
	 * 				| isTerminated()
	 */
	public boolean canRemoveSubDungeonAt(Position position) throws NullPointerException,IllegalStateException {
		if(isTerminated())
			throw new IllegalStateException();
		if(!hasSubDungeonAt(position))
			return false;
		if (getSubDungeonAt(position).getNbSquares() != 0)
			return false;
		return true;
	}
	
	/**
	 * A variable registering all the sub dungeons of this dungeon by a unique position.
	 */
	private HashMap<Position, Dungeon<SquareT>> subDungeons;
	
	/**
	 * Return a set of all shafts and levels that this composite dungeon has, direct or indirect.
	 * 
	 * @Return	The result is a set of all levels and shafts that are direct or indirect sub dungeons of this dungeon.
	 * 			| ...	
	 * @throws	IllegalStateException
	 * 				This dungeon is terminated.
	 * 				| isTerminated()
	 */
	public Set<SingularDungeon<SquareT>> getAllSingularDungeons() throws IllegalStateException{
		if(isTerminated())
			throw new IllegalStateException();
		
		Set<SingularDungeon<SquareT>> retSet = new HashSet<SingularDungeon<SquareT>>();
		for (Dungeon<SquareT> dungeon : getSubDungeons().values()) {
			if((dungeon instanceof Level<?>) || (dungeon instanceof Shaft<?>))
				retSet.add((SingularDungeon<SquareT>)dungeon);
			if(dungeon instanceof CompositeDungeon<?>)
				retSet.addAll(((CompositeDungeon<SquareT>)dungeon).getAllSingularDungeons());
		}
		return retSet;
	}

	/**
	 * Return whether this dungeon has a singular dungeon at the given position.
	 * 
	 * @Return	Else false if this composite dungeon has no singular dungeon at the given position.
	 * 			|...
	 * @Return 	Else the result is true if and only if the sub dungeon at the given position has a singular dungeon 
	 * 			at the same position expressed in the sub dungeons coordinates.
	 * 			|... 
	 */
	protected boolean hasSingularDungeonAt(Position position) throws NullPointerException {
		if(!super.hasSingularDungeonAt(position))
			return false;
		if(!hasSubDungeonAt(position))
			return false;
		return getSubDungeonAt(position).hasSingularDungeonAt(position.subtract(getPositionOfSubDungeon(getSubDungeonAt(position))));
	}
	
	/**
	 * Get an iterator that iterates over all the squares of this dungeon that satisfy a given predicate.
	 */
	@Override
	public Iterator<SquareT> iterator(final SquarePredicate<? super SquareT> predicate) throws IllegalStateException {
		if(isTerminated())
			throw new IllegalStateException();
		return new Iterator<SquareT>() {

			@Override
			public boolean hasNext() {
				if ((squareIterator == null) || !squareIterator.hasNext()) {
					if (!subdungeonIterator.hasNext())
						return false;
					Dungeon<SquareT> subdungeon = subdungeonIterator.next();
					squareIterator = (predicate == null) ? subdungeon.iterator() : subdungeon.iterator(predicate);
					return hasNext();
				}
				return true;
			}
			
			@Override
			public SquareT next() throws NoSuchElementException {
				if (!hasNext())
					throw new NoSuchElementException();
				return this.squareIterator.next();
			}

			@Override
			public void remove() throws UnsupportedOperationException {
				throw new UnsupportedOperationException();
			}
			
			private Iterator<? extends SquareT> squareIterator = null;
			private Iterator<? extends Dungeon<SquareT>> subdungeonIterator = CompositeDungeon.this.subDungeons.values().iterator();
		};		
	}

	/**
	 * Terminate this dungeon.
	 * 
	 * @Post	Each sub dungeon is terminated.
	 * 			| for each dungeon in getSubDungeons()
	 * 			|	(new dungeon).isTerminated()
	 * @Post	The map with sub dungeons is no longer effective.
	 * 			| new.getSubDungeons() == null
	 */
	@Override
	public void terminate() throws IllegalStateException {
		Map<Position, Dungeon<SquareT>> subDungeons = new HashMap<Position, Dungeon<SquareT>>(this.subDungeons);
		for (Dungeon<SquareT> subDungeon : subDungeons.values())
			subDungeon.terminate();
		super.terminate();
		this.subDungeons = null;
	}
}