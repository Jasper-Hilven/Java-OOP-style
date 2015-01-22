package project.squares;

import java.math.BigDecimal;
import java.util.Map;

import project.misc.*;
import project.squares.borders.*;
import project.temperature.*;
import be.kuleuven.cs.som.annotate.*;

/**
 * A square filled with rock that can not be entered and that can not contain anything.
 * 
 * @author 	Stef Noten & Jasper Hilven
 * @version	3.0
 *
 * @Invar	A rock square does not have slippery material.
 * 			| ...
 * @Invar	...
 * 			| !canChangeTemperature()
 */
public class RockSquare extends SquareImpl {
	
	/**
	 * Initialise a rock square.
	 * 
	 * @effect	This constructor calls its super constructor with zero as its temperature and humidity, 
	 * 			false for it's slippery material property and with all the directions for the directions
	 * 			in which to put walls.
	 * 			| super(new Temperature(0), BigDecimal.ZERO, false, Direction.values());
	 */
	public RockSquare() {
		super(new Temperature(0), BigDecimal.ZERO, false, Direction.values());
	}
	
	/**
	 * Return the temperature of this rock square, that is, the mean of the temperatures
	 * of the neighbouring squares.
	 * 
	 * @result	The mean of the temperatures of the neighbouring squares, excluding possible neighbouring rock squares.
	 * 			If there are no non-rock neighbours, the result is 0°C.
	 * 			| ...
	 */
	@Override
	public Temperature getTemperature() {
		double result = 0;
		int nbNeighbours = 0;
		for (Direction direction : Direction.values()) {
			Square neighbour = getNeighbour(direction);
			if ((neighbour != null) && ! (neighbour instanceof RockSquare))
				nbNeighbours++;
		}
		if (nbNeighbours != 0) { 
			for (Direction direction : Direction.values()) {
				Square neighbour = getNeighbour(direction);
				if ((neighbour != null) && ! (neighbour instanceof RockSquare))
					result += neighbour.getTemperature().getValueInCelcius() / nbNeighbours; 
			}
		}
		return new Temperature(result);
	}

	
	/**
	 * Check whether it is possible to change the temperature of this square.
	 * 
	 * @return	False.
	 * 			| result == false
	 */
	@Override
	public boolean canChangeTemperature() {
		return false;
	}
	
	/**
	 * Check whether this rock square can have the given borders if this square is not terminated.
	 * 
	 * @return	A rock square can only have walls without a door.
	 * 			| else
	 * 			|	result == 
	 * 			|		for each border in mapBorders.values()
	 * 			|			(border instanceof Wall) && !((Wall)border).hasDoor()
	 */
	@Raw @Override
	public boolean canHaveAsBordersIfNotTerminated(Map<Direction, Border> mapBorders) {
		if (!super.canHaveAsBordersIfNotTerminated(mapBorders))
			return false;
		for (Border border : mapBorders.values()) {
			if (!(border instanceof Wall) || ((Wall)border).hasDoor())
				return false;
		}
		return true;
	}
	
	/**
	 * Return whether an avatar can enter this rock square.
	 * 
	 * @result	False, a rock square can never contain anything.
	 * 			| result == false
	 */
	@Override
	public boolean canEnter() {
		return false;
	}
}
