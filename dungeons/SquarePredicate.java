package project.dungeons;

import project.squares.Square;

/**
 * An interface representing a predicate.
 * 
 * @author 	Stef Noten & Jasper Hilven
 * @version	3.0
 *
 */
public interface SquarePredicate<SquareT extends Square> {

	/**
	 * Check whether a given square with a given parent dungeon satisfies this predicate.
	 * 
	 * @param 	square
	 * 				The square to check.
	 * @param 	parentDungeon
	 * 				The dungeon to which the square belongs.
	 * @Pre		The given dungeon contains the given square.
	 * 			| parentDungeon.getSquares().containsValue(square)
	 * @return	A value indicating if the given square satisfies this predicate.
	 */
	public boolean satisfies(SquareT square, Dungeon<? extends SquareT> parentDungeon);
}
