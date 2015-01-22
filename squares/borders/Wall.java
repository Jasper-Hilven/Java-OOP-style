package project.squares.borders;

import be.kuleuven.cs.som.annotate.*;

/**
 * A wall, that is, a border that can have a door and can be slippery if it does not have a door.
 * 
 * @author 	Stef Noten & Jasper Hilven
 * @version 2.1
 * @Invar	A wall with a door can never be slippery.
 * 			| !hasDoor() && isSlippery()
 */
public class Wall extends Border {
	
	/**
	 * Initialise a new wall with parameters indicating if it has a door and if it is slippery. 
	 * 
	 * @param	hasDoor
	 *				Determines whether this wall has a door.	
	 * @param	isSlippery
	 * 				Determines whether this wall is slippery.
	 * @post	...
	 * 			| new.hasDoor() == hasDoor 
	 * @post	If the wall has a door, it can not be slippery. If it does not have a door, 
	 * 			the value of the given slippery parameter is set.
	 * 			| new.isSlippery() == isSlippery && !hasDoor
	 */
	@Raw
	public Wall(boolean hasDoor, boolean isSlippery) {
		super();
		this.hasDoor = hasDoor;
		this.slippery = isSlippery && !hasDoor;
	}

	/**
	 * Return whether this wall has a door.
	 */
	@Basic @Immutable
	public boolean hasDoor() {
		return hasDoor;
	}
	 
	/**
	 * Return whether the door is open if this wall has a door.
	 * 
	 * @return	...
	 * 		| if (!hasDoor)
	 * 		| 	result == false
	 * 		| else
	 * 		|  	result == !isIsolating()
	 */
	public boolean isDoorOpen() {
		return hasDoor() && !isIsolating();
	}

	/**
	 * Open the door of a wall, if it has one.
	 *
	 * @Post	If the wall has a door, the door is open.
	 * 			| ...
	 * @Post	If the wall has no door, nothing is changed.
	 * 			| ...
	 */
	public void openDoor() {
		if (hasDoor())
			setIsolating(false);
	}
	
	/**
	 * Close the door of this wall, if it has one.
	 * 
	 * @Post	If the wall has a door, the door is closed.
	 * 			| ...
	 * @Post	If the wall has no door, nothing is changed.
	 * 			| ...
	 */
	public void closeDoor() {
		if (hasDoor())
			setIsolating(true);
	}
	
	/**
	 * A variable determining whether this wall has a door.
	 */
	private boolean hasDoor;

	/**
	 * Check whether this wall can have a given value for its isolating
	 * property.
	 * 
	 * @param	value
	 *          	The value to check the validity for.
	 * @return	...
	 *        	| if (!hasDoor())
	 * 			| 	result == (value == true)
	 * 			| else
	 * 			|	result == true
	 */
	@Override
	public boolean canHaveAsIsolating(boolean value) {
		if (!hasDoor())
			return value == true;
		return true;
	}
	
	/**
	 * Return whether a wall is slippery or not.
	 */
	@Basic @Immutable
	public boolean isSlippery() {
		return slippery;
	}
	
	/**
	 * A variable determining whether this wall is slippery.
	 */
	private boolean slippery;

	/**
	 * Return a copy of this wall, that is, an uninitialised wall that has no 
	 * neighbours and has the same properties as this wall.
	 */
	@Override
	public Wall getUninitialisedCopy() {
		return new Wall(hasDoor(), isSlippery());
	}
	
	/**
	 * Check whether this border is an uninitialised copy of the given original border.
	 * 
	 * ...
	 * @return	False if the slipperiness of this border differs from the slipperiness of the given original border.
	 * 			| else if (isSlippery() != ((Wall)originalBorder).isSlippery())
	 *			|	then result == false
	 * @return	Otherwise, true if and only if the ownership of a door is equivalent with the ownership 
	 * 			of a door of the given original bordeR.
	 * 			| else
	 * 			|	then result == hasDoor() == ((Wall)originalBorder).hasDoor()
	 */
	@Override
	public boolean isUninitialisedCopyOf(Border originalBorder) {
		if (!super.isUninitialisedCopyOf(originalBorder))
			return false;
		Wall originalWall = ((Wall)originalBorder);
		if (isSlippery() != originalWall.isSlippery())
			return false;
		return hasDoor() == originalWall.hasDoor();
	}
	
	/**
	 * Return the dominant border of this border and the other border.
	 * 
	 * @param	otherBorder
	 * 				The other border to compare.
	 * @return	...
	 * 			| if (otherBorder == null)
	 * 			|	then result == this
	 *			| else if(otherBorder.getClass() == OpenBorder.class)
	 *			|	then result == this
	 *			| else if( otherBorder.getClass() == Wall.class)
	 *			|	result == hasDoor ? otherBorder : this
	 *			| else
	 *			|	result == otherBorder.getDominantBorder(this)
	 */
	@Override
	public Border getDominantBorder(Border otherBorder){
		if (otherBorder == null) 
			return this;
		if(otherBorder.getClass() == OpenBorder.class)
			return this;
		if( otherBorder.getClass() == Wall.class)
			return hasDoor ? otherBorder : this;
		return otherBorder.getDominantBorder(this);
	}
}
