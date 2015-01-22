package project.dungeons;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import project.squares.Square;
import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Raw;

/**
 * A singular dungeon, that is, a dungeon that is not composed of other dungeons.
 * 
 * @author 	Stef Noten & Jasper Hilven
 * @version	3.0
 */
public abstract class SingularDungeon<SquareT extends Square> extends Dungeon<SquareT> {
	
	/**
	 * Initialise a new singular dungeon with a given maximum position.
	 *
	 * @Post	...
	 * 			| new.getNbSquares() == 0
	 * @effect	...
	 * 			| super(maximumPosition)
	 */
	@Raw
	public SingularDungeon(Position maximumPosition) {
		super(maximumPosition);
		this.squares = new HashMap<Position, SquareT>();
	}
	
	/**
	 * Add a square to the list of squares.
	 */
	@Override
	protected void putSquareAt(Position position, SquareT square) throws IllegalStateException, NullPointerException {
		if (isTerminated())
			throw new IllegalStateException();
		if ((position == null) || (square == null))
			throw new NullPointerException();
		this.squares.put(position, square);
	}

	/**
	 * Return the number of squares that this dungeon has.
	 */
	@Override
	public int getNbSquares() throws IllegalStateException {
		if (isTerminated())
			throw new IllegalStateException();
		return this.squares.size();
	}
	
	/**
	 * Return the map of squares that this dungeon has, each square with his key position.
	 */
	@Basic @Override
	public Map<Position, SquareT> getSquares() {
		return (this.squares == null) ? null : new HashMap<Position, SquareT>(this.squares);
	}
	
	/**
	 * Return the square at the given position.
	 */
	@Override
	public SquareT getSquareAt(Position position) throws IllegalStateException , NullPointerException, IllegalArgumentException
	{
		if (!hasSquareAt(position))
			throw new IllegalArgumentException();
		return this.squares.get(position);
	}
	
	/**
	 * Remove a square at the given position.
	 */
	@Override
	public void removeSquareAt(Position position) 
		throws NullPointerException, IllegalStateException
	{
		if (hasSquareAt(position)) {
			getSquareAt(position).disconnect();
			this.squares.remove(position);
		}
	}

	/**
	 * Return whether this dungeon has a square at this position.
	 */
	@Override
	public boolean hasSquareAt(Position position) 
		throws NullPointerException, IllegalStateException
	{
		if (isTerminated())
			throw new IllegalStateException();
		if (position == null)
			throw new NullPointerException();
		return this.squares.containsKey(position);
	}

	/**
	 * Return whether this dungeon has a singular dungeon at the given position.
	 * 
	 * @return	Else, return true.
	 * 			|...
	 */
	protected boolean hasSingularDungeonAt(Position position) throws NullPointerException {
		if(!super.hasSingularDungeonAt(position))
			return false;
		return true;
	}
	
	/**
	 * A variable registering all the squares of this dungeon by a unique position.
	 */
	private HashMap<Position, SquareT> squares;
	
	/**
	 * Get an iterator that iterates over all the squares of this dungeon that satisfy a given predicate.
	 */
	@Override
	public Iterator<SquareT> iterator(final SquarePredicate<? super SquareT> predicate) throws IllegalStateException {
		if (isTerminated())
			throw new IllegalStateException();
		return new Iterator<SquareT>() {
			@Override
			public boolean hasNext() {
				if (currentSquare != null)
					return true;
				if (squareIterator.hasNext()) {
					SquareT nextSquare = squareIterator.next();
					if ((predicate == null) || predicate.satisfies(nextSquare, SingularDungeon.this)) {
						this.currentSquare = nextSquare;
						return true;
					}
					return hasNext();
				}
				return false;
			}
			
			@Override
			public SquareT next() throws NoSuchElementException {
				if (!hasNext())
					throw new NoSuchElementException();
				SquareT result = currentSquare;
				currentSquare = null;
				return result;
			}

			@Override
			public void remove() throws UnsupportedOperationException {
				throw new UnsupportedOperationException();
			}

			private SquareT currentSquare = null;
			private Iterator<? extends SquareT> squareIterator = squares.values().iterator();
		};	
	}
	
	/**
	 * Terminate this dungeon.
	 */
	public void terminate() {
		Map<Position, SquareT> squares = new HashMap<Position, SquareT>(this.squares);
		for (Position position : squares.keySet())
			removeSquareAt(position);
		super.terminate();
		this.squares = null;
	}
}
