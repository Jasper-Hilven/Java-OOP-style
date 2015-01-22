package project.squares.borders;
import project.misc.Direction;
import project.squares.Square;
import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Model;
import be.kuleuven.cs.som.annotate.Raw;
 
/**
 * A border with one or two neighbours and an isolating property.
 * 
 * @author	Stef Noten & Jasper Hilven
 * @version	2.3
 * @Invar	Each border must have proper neighbours.
 * 			| hasProperNeighbours()
 * @Invar	...
 * 			| canHaveAsIsolating(isIsolating())
 */
public abstract class Border {

	/**
	 * Create a new border.
	 * 
	 * @param	square1
	 * 				The first square to connect the border with. 
	 * @param	square2
	 * 				The second square to connect the border with.
	 * @post	...
	 * 			| new.canHaveAsIsolating(isIsolating())
	 * @post	The state of the new border is uninitialised.
	 * 			| ...
	 */
	@Raw @Model
	protected Border() {
		this.state = BorderState.UNINITIALISED;
		setIsolating(canHaveAsIsolating(true));
	}

	/**
	 * Construct this border with given squares as its neighbours.
	 * 
	 * @param	square1
	 *          	The first square to set as neighbour.
	 * @param	square1Direction
	 * 				The direction of this border relative to square1.
	 * @param	square2
	 *          	The second square to set as neighbour.
	 * @post	The state of this border will be the initialised state.
	 * 			| new.getState() == BorderState.INITIALISED
	 * @post 	...
	 * 			| new.hasProperNeighbours()
	 * @post	The first neighbour of this border will be square 1, the second square 2.
	 * 			| ...
	 * @post	...
	 * 			| square1.isMerged() && ((square2 == null) || square2.isMerged())
	 * @throws	IllegalStateException
	 *          	The border's state is not the uninitialised state.
	 *          	| ...
	 * @throws	IllegalArgumentException
	 *          	...
	 *          	| !canBuild(square1, square1Direction, square2)
	 */
	public void build(@Raw Square square1, Direction square1Direction, @Raw Square square2) 
		throws IllegalArgumentException, IllegalStateException
	{
		if (!canBuild(square1, square1Direction, square2))
			throw new IllegalArgumentException();
		
		Border border1 = square1.getBorderAt(square1Direction);
		border1.terminate(false);
		if (square2 != null) {
			Border border2 = square2.getBorderAt(square1Direction.getOppositeDirection());
			if (border1 != border2)
				border2.terminate(false);
		}
		
		setState(BorderState.INITIALISED);
		setNeighbours(square1, square1Direction, square2);
		
		square1.setBorderAt(getNeighbour1Direction(), this);
		if (square2 != null)
			square2.setBorderAt(getNeighbour2Direction(), this);
		
		if (!isIsolating())
			square1.merge();
	}
	
	/**
	 * Construct this border with a given square as its neighbour.
	 * 
	 * @param	square
	 *          	The square to set as neighbour.
	 * @param	squareDirection
	 * 				The direction of this border relative to the given square.
	 * @effect	...
	 * 			| build(square, squareDirection, null)
	 */
	public void build(@Raw Square square, Direction squareDirection) 
		throws IllegalArgumentException, IllegalStateException
	{
		build(square, squareDirection, null);
	}
	
	/**
	 * Check whether it is possible to construct this border with given squares as its neighbours.
	 * 
	 * @param	square1
	 *          	The first square to set as neighbour.
	 * @param	square1Direction
	 * 				The direction of this border relative to square1.
	 * @param	square2
	 *          	The second square to set as neighbour.
	 * @return	...
	 * 			| if (!areValidNeighbours(square1, square1Direction, square2, BorderState.INITIALISED) || !square1.canHaveAsBorderAt(square1Direction, this))
	 * 			|	then result == false
	 * @return	...
	 * 			| else if ((square2 != null) && !square2.canHaveAsBorderAt(square1Direction.getOppositeDirection(), this))
	 * 			| 	then result == false
	 * @return	If the first square has a neighbour then it may not be an other square than square 2 and the border may not be a wall.
	 * 			| else if ((square1.getNeighbour(square1Direction) != null) && 
	 * 			| 	((square1.getNeighbour(square1Direction) != square2) || (this instanceof Wall)))
	 * 			| 	then result == false
	 * @return 	...
	 * 			| else if ((square2 != null) && (square2.getNeighbour(square1Direction.getOppositeDirection()) != null) && 
	 * 			|   (square2.getNeighbour(square1Direction.getOppositeDirection()) != square1))
	 * 			|	then result == false	
	 * @return 	True otherwise
	 * 			| else
	 * 			|	then result == true
	 * @throws	IllegalStateException
	 *          	The border's state is not the uninitialised state.
	 *          	| ...
	 */
	public boolean canBuild(@Raw Square square1, Direction square1Direction, @Raw Square square2)
		throws IllegalStateException 
	{
		if (getState() != BorderState.UNINITIALISED)
			throw new IllegalStateException();
		if (!areValidNeighbours(square1, square1Direction, square2, BorderState.INITIALISED) || !square1.canHaveAsBorderAt(square1Direction, this))
			return false;
		if ((square2 != null) && !square2.canHaveAsBorderAt(square1Direction.getOppositeDirection(), this))
			return false;
		
		Square neighbourOfSquare1 = square1.getNeighbour(square1Direction);
		if ((neighbourOfSquare1 != null) && ((neighbourOfSquare1 != square2) || (this instanceof Wall)))
			return false;
		if (square2 != null) {
			Square neighbourOfSquare2 = square2.getNeighbour(square1Direction.getOppositeDirection());
			if ((neighbourOfSquare2 != null) && (neighbourOfSquare2 != square1))
				return false;
		}
		return true;
	}
	
	/**
	 * Return the first neighbour square of this border.
	 */
	@Basic
	public Square getNeighbour1() {
		return neighbour1;
	}
	
	/**
	 * Variable registering the first neighbour of this border.
	 */
	private Square neighbour1;
	
	/**
	 * Return the direction of the first neighbour, that is the direction from the neighbour to the border.
	 *
	 * @return	The direction in which this border can be found in the first neighbour if the first neighbour is effective.
	 * 			| ...
	 */
	@Basic
	public Direction getNeighbour1Direction() {
		return neighbour1Direction;
	}
	
	/**
	 * Variable registering the direction of this border relative to the first neighbour.
	 */
	private Direction neighbour1Direction;
	
	/**
	 * Return the second neighbour square of this border.
	 */
	@Basic
	public Square getNeighbour2() {
		return neighbour2;
	}
	/**
	 * Variable registering the second neighbour of this border.
	 */
	private Square neighbour2;
	
	/**
	 * Return the direction of the second neighbour, that is, the direction from the neighbour to the border.
	 *
	 * @return	Null if the second neighbour of this border is null, otherwise, the opposite direction of the 
	 * 			direction of the first neighbour.
	 * 			| ...
	 */
	public Direction getNeighbour2Direction() {
		return (getNeighbour2() == null) ? null : getNeighbour1Direction().getOppositeDirection();
	}
	
	/**
	 * Check whether this border has a given square as its neighbour.
	 * 
	 * @param 	square
	 * 				The square to check.
	 * @return	True if and only if one of the neighbours of this border is the given square.
	 * 			| ...
	 */
	public boolean hasNeighbour(Square square) {
		return (getNeighbour1() == square) || (getNeighbour2() == square);
	}
	
	/**
	 * Check whether the border has consistent neighbours.
	 * 
	 * @return 	...
	 * 			| if (!canHaveAsNeighbours(getNeighbour1(), getNeighbour1Direction(), getNeighbour2()))
	 * 			|	then result == false
	 * @return	...
	 * 			| else if (getState() == BorderState.INITIALISED)
	 * 			|	then result == 
	 * 			|		(getNeighbour1().getBorderAt(getNeighbour1Direction()) == this) &&
	 *			| 		( (getNeighbour2() == null) || 
	 *			|			(getNeighbour2().getBorderAt(getNeighbour2Direction()) == this) )
	 */
	public boolean hasProperNeighbours() {
		if (!canHaveAsNeighbours(getNeighbour1(), getNeighbour1Direction(), getNeighbour2()))
			return false;
		if (getState() == BorderState.INITIALISED) {
			boolean properFirst = (getNeighbour1().getBorderAt(getNeighbour1Direction()) == this); 
			boolean properSecond = 
				(getNeighbour2() == null) || 
				(getNeighbour2().getBorderAt(getNeighbour2Direction()) == this);
			
			return properFirst && properSecond;
		}
		return true;
	}

	/**
	 * Set the given squares as neighbours of this border.
	 * 
	 * @param	square1
	 * 				The first square to set as neighbour.
	 * @param	square1Direction
	 * 				The direction in which the border is oriented relative to the square1.
	 * @param	square2
	 * 				The second square to set as neighbour.
	 * @post	... 
	 * 			| new.getNeighbour1() == square1
	 * @post	...
	 * 			| new.getNeighbour1Direction() == neighbour1Direction
	 * @post	... 
	 * 			| new.getNeighbour2() == square2
	 * @post	...
	 * 			| new.getNeighbour2Direction() == neighbour1Direction.getOppositeDirection()
	 * @throws	IllegalArgumentException
	 * 				...
	 * 				| !canHaveAsNeighbours(square1, square2)
	 */
	@Model @Raw
	private void setNeighbours(Square square1, Direction square1Direction, Square square2) 
		throws IllegalArgumentException
	{
		if (!canHaveAsNeighbours(square1, square1Direction, square2))
			throw new IllegalArgumentException();
		this.neighbour1 = square1;
		this.neighbour2 = square2;
		this.neighbour1Direction = square1Direction;
	}
	
	/**
	 * Return whether this border can have the given neighbours as its neighbours in a given state.
	 * 
	 * @param	neighbour1
	 * 				The first square to check as neighbour.
	 * @param	neighbour2
	 * 				The second square to check as neighbour.
	 * @param	state
	 * 				The state for which to check the validity of the neighbours.
	 * @return 	...
	 * 			| if (state == BorderState.INITIALISED)
	 * 			| 	result == (neighbour1 != null) && (square1Direction != null)
	 * 			| else
	 * 			|	result == (neighbour1 == null) && (square1Direction == null) && (neighbour2 == null)
	 */
	@Raw
	public static boolean areValidNeighbours(Square neighbour1, Direction square1Direction, Square neighbour2, BorderState state) {
		if (state == BorderState.INITIALISED)
			return (neighbour1 != null) && (square1Direction != null);
		// TERMINATED or UNINITIALISED
		return (neighbour1 == null) && (square1Direction == null) && (neighbour2 == null);
	}
	
	/**
	 * Return whether this border can have the given neighbours as its neighbours.
	 * 
	 * @param	neighbour1
	 * 				The first square to check as neighbour.
	 * @param	neighbour2
	 * 				The second square to check as neighbour.
	 * @result	...
	 * 			| result == areValidNeighbours(neighbour1, square1Direction, neighbour2, getState())
	 */
	public boolean canHaveAsNeighbours(Square neighbour1, Direction square1Direction, Square neighbour2) {
		return areValidNeighbours(neighbour1, square1Direction, neighbour2, getState());
	}
		
	/**
	 * Return whether a border is isolating.
	 */
	@Basic
	public boolean isIsolating() {
		return isolating;
	}

	/**
	 * Set whether the border is isolating to the given flag.
	 * 
	 * @param	flag
	 *          	Boolean value that indicates whether the border should be
	 *            	isolating or not.
	 * @post	...
	 *			| if (canHaveAsIsolating(flag))
	 *			| 	new.isIsolating() == flag
	 * @post	...
	 * 			| if (!flag)
	 * 			| 	getNeighbour1().isMerged() && ((getNeighbour2() == null) || getNeighbour2().isMerged())
	 */
	@Raw
	protected void setIsolating(boolean flag) {
		if (canHaveAsIsolating(flag)) {
			this.isolating = flag;
			if (!flag && (getState() == BorderState.INITIALISED))
				getNeighbour1().merge();
		}
	}

	/**
	 * Check whether this border can have a given value for its isolating
	 * property.
	 * 
	 * @param	value
	 *          	The value to check the validity for.
	 * @return	True if and only if the given value is a valid value for the
	 *        	isolating property of this border.
	 *        	| ...
	 */
	@Raw
	public abstract boolean canHaveAsIsolating(boolean value);

	/**
	 * A variable determining whether this border is isolating.
	 */
	private boolean isolating;

	/**
	 * Return the current state of the border.
	 */
	@Basic
	public BorderState getState() {
		return this.state;
	}
	
	/**
	 * Sets the state of the current border.
	 * 
	 * @param	state
	 *				The state to set the border to.
	 * @post	...
	 * 			| new.getState() == state	
	 * @Throws	IllegalStateException
	 * 				...
	 * 				| !canHaveAsState(state)
	 */
	private void setState(BorderState state) 
		throws IllegalStateException 
	{
		if (!canHaveAsState(state))
			throw new IllegalStateException();
		this.state = state;
	}
 
	/**
	 * Check whether this square can have the given state as state.
	 * 
	 * @param	state
	 * 				The state to check if this square can have this given state..
	 * @return	...
	 * 			| result == 
	 * 			|	(getState() == state) || 
	 * 			|	((getState() == BorderState.UNINITIALISED) && (state == BorderState.INITIALISED)) ||
	 * 			| 	(state == BorderState.TERMINATED)
	 */
	public boolean canHaveAsState(BorderState state){
		return 
			(getState() == state) || 
			((getState() == BorderState.UNINITIALISED) && (state == BorderState.INITIALISED)) ||
			(state == BorderState.TERMINATED);
	}
	
	/**
	 * A variable representing the state of this border.
	 */
	private BorderState state;

	/**
	 * Terminate this border by replacing itself in its neighbours with null or an open border 
	 * for respectively a terminated corresponding neighbour or an unterminated one. 
	 * 
	 * @post	...
	 * 			| new.getState() == BorderState.TERMINATED
	 * @post	The neighbours of this border are ineffective.
	 * 			| ...
	 * @post	If a former neighbour is terminated, then the corresponding border will be set ineffective, 
	 * 			otherwise it is set to an open border with no second neighbour.
	 * 			| ...
	 * @throws	IllegalStateException
	 * 				...
	 * 				| !canTerminate()
	 */
	public void terminate()
		throws IllegalStateException
	{
		if (!canTerminate())
			throw new IllegalStateException();
		terminate(true);
	}
	
	/**
	 * Terminate this border.
	 * 
	 * @param 	cleanupBindingsFromSquare
	 * 				A parameter indicating whether the bidirectional binding has to be cleaned up in both 
	 * 				directions. If it is false, the binding in the square class will be left untouched.
	 * @post	...
	 * 			| new.getState() == BorderState.TERMINATED
	 * @post	The neighbours of this border are ineffective.
	 * 			| ...
	 * @post	The respective borders of the former neighbours are set to an open border with no second neighbour.
	 * 			| ...
	 * @throws	IllegalStateException
	 * 				...
	 * 				| !canHaveAsState(BorderState.TERMINATED)
	 */
	private void terminate(boolean cleanupBindingsFromSquare) throws IllegalStateException {
		BorderState formerState = getState();
		if (formerState != BorderState.TERMINATED) {
			if (!canHaveAsState(BorderState.TERMINATED))
				throw new IllegalStateException();
			
			setState(BorderState.TERMINATED);
			
			if (formerState == BorderState.UNINITIALISED)
				return;
			
			Square formerNeighbour1 = getNeighbour1(), formerNeighbour2 = getNeighbour2();
			Direction formerNeighbour1Direction = getNeighbour1Direction();
			setNeighbours(null, null, null);
			
			if (cleanupBindingsFromSquare)
				new OpenBorder().build(formerNeighbour1, formerNeighbour1Direction, formerNeighbour2);
		}
	}

	/**
	 *  Check whether it is possible to terminate a border.
	 * 
	 * @return	...
	 * 			| if (!canHaveAsState(BorderState.TERMINATED))
	 * 			|	then result == false
	 * @return	...
	 * 			| else if (getState() == BorderState.UNINITIALISED)
	 * 			|	then result == true
	 * @return	...
	 * 			| else if (!getNeighbour1().canHaveAsBorderAt(getNeighbour1Direction(), new OpenBorder()))
	 * 			| 	then result == false
	 * @return 	...
	 * 			| else if ((getNeighbour2() != null) && !getNeighbour2().canHaveAsBorderAt(getNeighbour2Direction(), new OpenBorder()))
	 * 			|	then result == false
	 * @return 	True otherwise
	 * 			| else
	 * 			|	then result == true
	 */
	public boolean canTerminate() {
		if (!canHaveAsState(BorderState.TERMINATED))
			return false;
		if (getState() == BorderState.UNINITIALISED)
			return true;
		Border controlBorder = new OpenBorder();
		if (!getNeighbour1().canHaveAsBorderAt(getNeighbour1Direction(), controlBorder))
			return false;
		if ((getNeighbour2() != null) && !getNeighbour2().canHaveAsBorderAt(getNeighbour2Direction(), controlBorder))
			return false;
		return true;
	}
		
	/**
	 * Return a copy of this border, that is, an uninitialised border that has no neighbours.
	 * 
	 * @return	...
	 * 			| result.isUninitialisedCopyOf(this)
	 */
	public abstract Border getUninitialisedCopy();
	
	/**
	 * Check whether this border is an uninitialised copy of the given original border.
	 * 
	 * @param 	originalBorder
	 * 				The original border to check.
	 * @return	False if this border is not in the uninitialised state.
	 * 			| ...
	 * @return	False if the original border is this border.	
	 *			| ...
	 * @return	False if this border is of another type than the given original border.
	 * 			| ...
	 */
	public boolean isUninitialisedCopyOf(Border originalBorder) {
		if (getState() != BorderState.UNINITIALISED)
			return false;
		if (originalBorder == this)
			return false;
		return (getClass() == originalBorder.getClass());
	}
	
	/**
	 * Return the dominant border of this border and the other border.
	 * 
	 * @param	otherBorder
	 * 				The other border to compare.
	 * @return	The most dominant border.
	 *			| ...
	 */
	public abstract Border getDominantBorder(Border otherBorder);
	
	/**
	 * Release this border from a given square, give its neighbour a copy of this border and terminate this border.
	 * 
	 * @param	releasingSquare
	 * 				The square to release this border from.
	 * @post	...
	 * 			| new.getState() == BorderState.TERMINATED
	 * @post	The new neighbours of this border and the directions of the neighbours are ineffective.
	 * 			| ...
	 * @post	An uninitialised copy of this border is built on the first square in the former direction of this border.
	 * 			| ...
	 * @post	An uninitialised copy of this border is built on the second square in the former direction of this border.
	 * 			| ...
	 * @throws	IllegalStateException
	 *				...
	 *				| !canReleaseBorder()
	 */
	public void splitBorder() throws IllegalStateException {
		if (!canSplitBorder())
			throw new IllegalStateException();
		
		Square neighbour1 = getNeighbour1(), neighbour2 = getNeighbour2();
		Direction neighbour1Direction = getNeighbour1Direction(), neighbour2Direction = getNeighbour2Direction();
		
		setState(BorderState.TERMINATED);
		setNeighbours(null, null, null);
		
		getUninitialisedCopy().build(neighbour1, neighbour1Direction);
		if (neighbour2 != null)
			getUninitialisedCopy().build(neighbour2, neighbour2Direction);
	}
	 
	/**
	 * Return whether it is possible to split this border.
	 * 
	 * @return	True if and only if the state of this border is the initialised state and one of the neighbours is 
	 * 			in the state of disconnecting.	
	 * 			| result == 
	 * 			|	(getState() == BorderState.INITIALISED) &&
	 * 			|	(getNeighbour1().isDisconnecting() || ((getNeighbour2() != null) && getNeighbour2().isDisconnecting()))
	 */
	public boolean canSplitBorder() {
		return (getState() == BorderState.INITIALISED) &&
			(getNeighbour1().isDisconnecting() || ((getNeighbour2() != null) && getNeighbour2().isDisconnecting()));
	}
}
