package project.squares.borders;

/**
 * An open border, that is, a border that is not isolated.
 * 
 * @Invar	...
 * 			| !isIsolated()
 * @author 	Stef Noten & Jasper Hilven
 * @version	1.0
 *
 */
public class OpenBorder extends Border {

	/**
	 * Initialise a new open border.
	*/
	public OpenBorder() {
		super();
	}
	
	/**
	 * Check whether this open border can have a given value for its isolating property.
	 * 
	 * @return	...
	 *        	| result == (value == false)
	 */
	@Override
	public boolean canHaveAsIsolating(boolean value) {
		return (value == false);
	}

	/**
	 * Return a copy of this border, that is, an uninitialised open border that has no neighbours.
	 */
	@Override
	public OpenBorder getUninitialisedCopy() {
		return new OpenBorder();
	}
	
	/**
	 * Check whether this border is an uninitialised copy of the given original border.
	 * 
	 * ...
	 * @return	True otherwise.
	 * 			| else
	 *			|	result == true
	 */
	@Override
	public boolean isUninitialisedCopyOf(Border originalBorder) {
		return super.isUninitialisedCopyOf(originalBorder);
	}
	
	/**
	 * Return the dominant border of this border and a given other border.
	 * 
	 * @param	otherBorder
	 * 				The other border to compare.
	 * @return	...
	 * 			| if (otherBorder == null)
	 * 			|	then result == this
	 *			| else if(otherBorder.getClass() == OpenBorder.class)
	 *			|	then result == this
	 *			| else
	 *			|	then result == otherBorder.getDominantBorder(this)
	 */
	@Override
	public Border getDominantBorder(Border otherBorder) throws NullPointerException{
		if (otherBorder == null) 
			return this;
		if(otherBorder.getClass() == OpenBorder.class)
			return this;
		return otherBorder.getDominantBorder(this);
	}
}
