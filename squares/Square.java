package project.squares;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import project.misc.*;
import project.squares.borders.*;
import project.temperature.*;
import be.kuleuven.cs.som.annotate.*;

/**
 * A square, the smallest unit of location with a temperature, a minimum and a maximum temperature, 
 * a humidity, a slippery material property and borders in different directions.
 *
 * @Invar	The maximum temperature is greater than or equal to the minimum temperature 
 * 			and the temperature lies between them.
 * 	    	| matchesTemperatureBoundaries(getTemperature(), getMinTemperature(), getMaxTemperature())
 * @Invar	The humidity is a valid humidity.
 * 			| isValidHumidity(getHumidity())
 * @Invar	The heat damage boundary temperature is effective.
 * 			| getHeatDamageBoundaryTemperature() != null
 * @Invar	The heat damage per degree Celcius is valid.
 * 			| isValidHeatDamagePerDegreeCelcius(getHeatDamagePerDegreeCelcius())
 * @Invar	The heat capacity is valid.
 * 			| isValidHeatCapacity(getHeatCapacity())
 * @Invar	A square must have proper borders attached to it.
 * 			| hasProperBorders()
 * @Invar	All squares in the space of this square are merged.
 * 			| isMerged()
 * @Invar	A square must never be in a state of merging.
 * 			| !isMerging()
 * @Invar	A square must never be in a state of disconnecting.
 * 			| !isDisconnecting()
 * @Invar	A square must never be in a state of connecting.
 * 			| !isConnecting()
 * 
 * @version 3.0
 * @author 	Stef Noten & Jasper Hilven
 */
public interface Square {

	/**
	 * Return the temperature of this square.
	 */
	public Temperature getTemperature();

	/**
	 * Set the temperature of this square.
	 * 
	 * @param 	temperature
	 *				The new temperature of this square.
	 * @post	If the temperature of this square can be changed, the new temperature will be the given 
	 * 			temperature and the new square will be in a merged state. 
	 * 			| if (canChangeTemperature())
	 * 			|	new.getTemperature().getValueInCelcius() == 
	 * 			|		(getTemperature().getValueInCelcius() + 
	 * 			|		(getTemperature().getValueInCelcius() - temperature.getValueInCelcius()) / getSquaresInSpace().size()) 
	 */
	@Raw
	public void changeTemperature(Temperature temperature)
			throws NullPointerException, TemperatureOutOfRangeException;

	/**
	 * Check whether it is possible to change the temperature of this square.
	 */
	@Basic @Immutable
	public boolean canChangeTemperature();
	
	
	/**
	 * Return whether a given temperature is valid for this square.
	 * 
	 * @param 	temperature
	 *				The temperature to check.
	 * @return	The value of matchesTemperatureBoundaries with the given temperature as temperature, 
	 * 			the minimum temperature of this square as minimum temperature and the maximum 
	 * 			temperature of this square as maximum temperature.
	 * 			| result == matchesTemperatureBoundaries(temperature, getMinTemperature(), getMaxTemperature())
	 */
	@Raw
	public boolean canHaveAsTemperature(Temperature temperature)
			throws NullPointerException;

	/**
	 * Return the minimum temperature of this square.
	 */
	@Basic
	public Temperature getMinTemperature();

	/**
	 * Set the minimum temperature of this square.
	 *
	 * @param	minTemperature
	 * 				The minimum temperature to set.
	 * @post	The minimum temperature of this square is equal to the given minimum temperature.
	 * 			| new.getMinTemperature() == minTemperature
	 * @throws	IllegalArgumentException
	 * 				The given minimum temperature is greater than the maximum temperature of this square.
	 * 				| minTemperature.compareTo(getMaximumTemperature()) > 0
	 * @throws 	TemperatureOutOfRangeException(getTemperature(), minTemperature, getMaxTemperature())
	 * 				The given minimum temperature is not valid because the current temperature would not 
	 * 				lie within its boundaries.
	 * 				| (minTemperature.compareTo(getMaximumTemperature()) <= 0) &&
	 * 				| !matchesTemperatureBoundaries(getTemperature(), minTemperature, getMaxTemperature())
	 * @throws 	NullPointerException
	 * 				The given value is not effective.
	 * 				| minTemperature == null
	 */
	public void setMinTemperature(Temperature minTemperature)
			throws TemperatureOutOfRangeException, NullPointerException,
			IllegalArgumentException;

	/**
	 * Return the maximum temperature of this square.
	 */
	@Basic
	public Temperature getMaxTemperature();

	/**
	 * Set the maximum temperature of this square.
	 * 
	 * @param	maxTemperature
	 * 				The maximum temperature to set.
	 * @post	The maximum temperature of this square is equal to the given maximum temperature.
	 * 			| new.getMaxTemperature() == maxTemperature
	 * @throws	IllegalArgumentException
	 * 				The given maximum temperature is smaller than the minimum temperature of this square.
	 * 				| maxTemperature.compareTo(getMinimumTemperature()) < 0
	 * @throws 	TemperatureOutOfRangeException(getTemperature(), getMinTemperature(), maxTemperature)
	 * 				The maximum temperature is not valid because the current temperature would not lie within its boundaries.
	 * 				| (maxTemperature.compareTo(getMinimumTemperature()) >= 0) &&
	 * 				| !matchesTemperatureBoundaries(getTemperature(), getMinTemperature(), maxTemperature)
	 * @throws 	NullPointerException
	 * 				The given value is not effective.
	 * 				| maxTemperature == null
	 */
	public void setMaxTemperature(Temperature maxTemperature)
			throws TemperatureOutOfRangeException, NullPointerException,
			IllegalArgumentException;

	/**
	 * Return the cold damage caused by the coldness of this square.
	 * 	
	 * @return	Zero if the temperature is not beneath -5°C, 
	 * 			otherwise one point per 10°C beneath -5°C.
	 * 			| if (getTemperature().compareTo(new Temperature(-5)) >= 0)
	 * 			|	result == 0
	 * 			| else
	 * 			|	result == (int)Math.floor(-getTemperature().getValueInCelcius() / 10 - 0.5)
	 */
	public int getColdDamage();

	/**
	 * Return the heat damage caused by the heat of this square.
	 * 
	 * @return	Zero if the temperature is beneath the heat damage boundary temperature. Otherwise 
	 * 			the heat damage per degree Celcius of this square multiplied by the count of 
	 * 			degrees Celcius above the heat damage boundary temperature, rounded down. When this
	 * 			value is bigger than Integer.MAX_VALUE, Integer.MAX_VALUE is returned.
	 * 			| if (getTemperature().compareTo(getHeatDamageBoundaryTemperature()) <= 0)
	 * 			|	result == 0
	 * 			| else
	 * 			|	result == (int)Math.min((double)Integer.MAX_VALUE,
	 * 			|		Math.floor((getTemperature().getValueInCelcius() - 
	 *			|			getHeatDamageBoundaryTemperature().getValueInCelcius()) * getHeatDamagePerDegreeCelcius())
	 */
	public int getHeatDamage();

	/**
	 * Return the points of damage caused by rust to metal objects. 
	 * 
	 * @return	One point per 7 percent above 30 percent of humidity, rounded down. 
	 * 			Zero if the percentage of humidity is not greater than 30 percent.
	 * 			| if (getHumidity().compareTo(new BigDecimal(30)) <= 0)
	 * 			|	result == 0
	 * 			| else 
	 * 			| 	result == humidity.subtract(new BigDecimal(30)).divide(new BigDecimal(7), 0, BigDecimal.ROUND_DOWN).intValue())
	 */
	public int getRustDamage();

	/**
	 * Return the humidity of this square.
	 */
	@Basic
	public BigDecimal getHumidity();

	/**
	 * Set the humidity of the square to the given humidity.
	 * 
	 * @param	humidity
	 * 				The humidity to assign, expressed as a percentage.
	 * @effect	Sets the humidity to the given humidity and merges afterwards.
	 *			| setHumidity(humidity);
	 *			| merge();
	 */
	@Raw
	public void changeHumidity(BigDecimal humidity);

	/**
	 * Return whether this square is slippery.
	 * 
	 * @return	True if this square is water slippery, that is, it has a humidity of 100% and a temperature not beneath 0°C.
	 * 			| if ((getHumidity().compareTo(new BigDecimal(100)) == 0) && (getTemperature().getValueInCelcius() >= 0))
	 * 			|	result == true
	 * 		 	True if this square is ice slippery, that is, it has a humidity great than 10% and a strict negative temperature.
	 * 			| if ((getHumidity().compareTo(new BigDecimal(10)) > 0) && (getTemperature().getValueInCelcius() < 0))
	 * 			|	result == true
	 * 		 	True if this square is made of slippery material.
	 * 			| if (hasSlipperyMaterial())
	 * 			|	result == true
	 * 			False otherwise
	 * 			| else
	 * 			|	result == false
	 */
	public boolean isSlippery();

	/**
	 * Return whether this square is made of slippery material.
	 */
	@Basic
	@Immutable
	public boolean hasSlipperyMaterial();

	/**
	 * Get a value indicating the inhabitability of this square.
	 * 
	 * @return	A double that represents the inhabitability of this square. That is minus the square root of the cubed heat damage
	 * 			divided by the square root of 101 minus the humidity, minus the square root of the cold damage.
	 * 			| result == - Math.pow(getHeatDamage(),1.5)/Math.sqrt(101-getHumidity().doubleValue()) - Math.sqrt(getColdDamage()) 
	 */
	public double getInhabitability();

	/**
	 * Return the border of this square in the given direction.
	 * 
	 * @param 	direction
	 * 				The direction in which the border is.
	 * @return	The border at the given direction.
	 * 			| result == getBorders().get(direction)
	 * @throws	NullPointerException
	 * 				The given direction is not effective. 			
	 *				| direction == null
	 */
	@Basic
	public Border getBorderAt(Direction direction) throws NullPointerException;

	/**
	 * Return the borders of this square.
	 */
	@Basic
	public Map<Direction, Border> getBorders();

	/**
	 * Set a border of a square in a given direction to a given border.
	 * 
	 * @param	direction
	 * 				The direction in which the border is set.
	 * @param	border
	 * 				The border to set.
	 * @Pre		The given border must be initialised and have this square as neighbour.	
	 * 			| (border.getState() == BorderState.INITIALISED) && border.hasNeighbour(this)
	 * @Pre		If this square has a border in the given direction, then that border must be terminated.
	 * 			| if (hasBorderAt(direction))
	 * 			| 	getBorderAt(direction).getState() == BorderState.TERMINATED
	 * @post	This square will have the given border in the given direction.
	 * 			| new.getBorderAt(direction) == border 
	 * @throws	IllegalArgumentException
	 * 				The square cannot have this border as border.
	 * 				| !canHaveAsBorderAt(direction, border)
	 */
	@Raw
	public void setBorderAt(Direction direction, @Raw Border border)
			throws IllegalArgumentException;
	
	/**
	 * Connect this square with the given neighbours.
	 * 
	 * @param 	neighbours
	 * 				A map of directions with the corresponding neighbours to connect to.
	 * @post	This square is connected to each of the given neighbours in the corresponding directions, 
	 * 			joined by a corresponding dominant border.
	 * 			| for each direction in Direction
	 * 			|	getDominantBorders(neighbours).get(direction).isUninitialisedCopyOf(new.getBorderAt(direction)) &&
	 * 			|	( !neighbours.containsKey(direction) || (new.getNeighbour(direction) == neighbours.get(direction)) )
	 * @throws	IllegalArgumentException
	 * 				This square can not be connected with the given neighbours.
	 * 				| !canConnect(neighbours)
	 * @throws	NullPointerException
	 * 				The given map of neighbours is not effective or the given map contains a null square or a null direction.
	 * 				| (neighbours == null) || neighbours.containsKey(null) || neighbours.containsValue(null)
	 */
	public void connect(Map<Direction, Square> neighbours) throws NullPointerException;

	/**
	 * Check whether this square can connect with the given neighbours.
	 * 
	 * @param 	neighbours
	 * 				A map of directions with the corresponding neighbours to connect to.
	 * @return	True if and only if this square does not have any neighbours yet and if the dominant borders 
	 * 			for the given neighbours are valid for this square and all the given neighbours.
	 * 			| result == 
	 * 			|	!hasNeighbours() &&
	 * 			|	square.canHaveAsBorders(getDominantBorders(neighbours) &&
	 * 			|	for each direction in Direction
	 * 			|		!neighbours.containsKey(direction) || 
	 * 			|		neighbours.get(direction).canHaveAsBorderAt(direction.getOppositeDirection(), getDominantBorders(neighbours).get(direction))
	 * @throws	NullPointerException
	 * 				The given map of neighbours is not effective or the given map contains a null square or a null direction.
	 * 				| (neighbours == null) || neighbours.containsKey(null) || neighbours.containsValue(null)
	 */
	public boolean canConnect(Map<Direction, Square> neighbours) throws NullPointerException;

	/**
	 * Get whether this square is in the state of connecting with neighbouring squares.
	 */
	@Basic
	public boolean isConnecting();

	/**
	 * Get a map that contains an uninitialised dominant border for the current borders and the borders of the given neighbours.
	 * 
	 * @param 	neighbours
	 * 				A map of directions with the corresponding neighbours to get the corresponding dominant borders for.
	 * @return 	A map that contains the dominant borders of this square for each direction if this square would be 
	 * 			connected to the given neighbours. The dominant border in a given direction is the most dominant 
	 * 			border of this square at the given direction and the neighbour square's border at the opposite direction.
	 * 			| result != null && 
	 * 			| result.size() == Direction.values().size() && 
	 * 			| for each direction in Direction
	 * 			|	if (neighbours.containsKey(direction))
	 * 			|		then result.get(direction).isUninitialisedCopyOf(
	 * 			|				neighbours.get(direction).getBorderAt(direction.getOppositeDirection())
	 * 			|					.getDominantBorder(getBorderAt(direction)))
	 * 			|	else
	 * 			|  		then result.get(direction).isUninitialisedCopyOf(getBorderAt(direction)) 
	 * @throws	NullPointerException
	 * 				The given map of neighbours is not effective or the given map contains a null square or a null direction.
	 * 				| (neighbours == null) || neighbours.containsKey(null) || neighbours.containsValue(null)
	 */
	public Map<Direction, Border> getDominantBorders(Map<Direction, Square> neighbours) throws NullPointerException;

	/**
	 * Check whether this square can have a given border at a given direction.
	 *  
	 * @param	direction
	 * 				The direction in which to check if the square can have the given border as border.
	 * @param	border
	 * 				The border to check if it can be a border at the given direction for this square.
	 * @return	True if this border is in the state of connecting.
	 * 			| if (isConnecting())
	 * 			|	then result == true
	 * @return	Else, true if this square can have a given border at a given direction.
	 * 			| else
	 * 			| 	then result == canHaveAsBorders(getBordersReplacedBy(direction, border))
	 */
	@Raw
	public boolean canHaveAsBorderAt(Direction direction, Border border);
	
	/**
	 * Get a map containing the borders of this square, in which the border in the given direction is 
	 * replaced by the given border.
	 * 
	 * @param 	direction
	 * 				The direction in which to replace the original border.
	 * @param 	border
	 * 				The border to replace the original border with in the resulting map.
	 * @return	...
	 * 			| for each direction2 in Direction
	 * 			|	result.get(direction2) == (direction2 == direction) ? border : getBorders().get(direction2) 
	 */
	public Map<Direction, Border> getBordersReplacedBy(Direction direction, Border border);

	/**
	 * Check whether this square can have the given borders as its borders.
	 * 
	 * @param	mapBorders
	 * 				The map of borders to check.
	 * @return	False if the given map of borders is ineffective or if the given map of borders contains an ineffective key.
	 * 			| if ((mapBorders == null) || mapBorders.containsKey(null))
	 * 			|	then result == false
	 * @return	False if the given map of borders contains a terminated border.
	 * 			| else if ((border != null) && (border.getState() == BorderState.TERMINATED))
	 * 			| 	then result == false
	 * @return	Otherwise return whether the border can have this map of borders as this square is not terminated.
	 * 			| else 
	 * 			|	then result == canHaveAsBordersIfNotTerminated(mapBorders)
	 * */
	@Raw
	public boolean canHaveAsBorders(Map<Direction, Border> mapBorders);

	/**
	 * Check whether this square can have the given borders if this square is not terminated.
	 * 
	 * @param	mapBorders
	 * 				the map with borders to check if this square can have this map as neighbours.
	 * @return	False if the given map is not effective.
	 * 			| if (mapBorders == null)
	 * 			|	then result == false
	 * @return	False if for at least one direction the map has a null as border or a terminated border.
	 * 			| else if( for some Direction direction in Direction.values()
	 * 			|		( mapBorders.get(direction) == null) || ( mapBorders.get(direction).getState() == BorderState.TERMINATED))
	 * 			|	then result == false
	 * @return  False if there is no wall (with or without door) in the given map of borders.
	 * 			| else if(!for some Border border in mapBorders.values()	
	 * 			|			(border instanceof Wall))
	 * 			| 	then result == false
	 * @return	False if there is a wall with a door in the floor.
	 * 			| else if( (mapBorders.get(Direction.FLOOR) instanceof Wall) && ((Wall)mapBorders.get(Direction.FLOOR)).hasDoor())
	 * 			|	then result == false
	 * @return	False if there the map has strict more than 3 walls with door.
	 * 			| else if (COUNT((mapBorders.get(direction) instanceof Wall) && mapBorders.get(direction).hasDoor()) : direction in Direction.values()) > 3)
	 * 			|	then result == false
	 */
	@Raw
	public boolean canHaveAsBordersIfNotTerminated(Map<Direction, Border> mapBorders);

	/**
	 * Check whether this square has proper borders.
	 * 
	 * @return	False if this square cannot have its current borders as borders.
	 * 			| if (!canHaveAsBorders(getBorders()))
	 * 			|	result == false
	 * @return	False if this square does not have a border in each direction, if it has a border that is not initialised 
	 * 			or if it has a border that doesn't have this square as neighbour.
	 * 			| if (for some direction in Direction.values() 
	 * 			|		!hasBorderAt(direction) || !getBorderAt(direction).hasNeighbour(this) || 
	 * 			|		(getBorderAt(direction).getState() != BorderState.INITIALISED))
	 * 			|			then result == false
	 * @return	True otherwise.
	 * 			| else
	 * 			| 	then result == true
	 */
	public boolean hasProperBorders();

	/**
	 * Checks whether a square has a border in the given direction.
	 * 
	 * @param	direction
	 * 				The direction in which to check if there is a border.
	 * @return	True if the border in the given direction is effective, false otherwise.
	 * 			| result == getBorderAt(direction) != null
	 * @throws	NullPointerException
	 * 				The given direction is not effective.
	 * 				| direction == null
	 */
	public boolean hasBorderAt(Direction direction) throws NullPointerException;
	
	/**
	 * Check whether this square has one or more neighbours.
	 * 
	 * @return	True if and only if this square has one or more neighbours.
	 * 			| result == 
	 * 			|	for some direction in Direction.values()
	 * 			|		getNeighbour(direction) != null 
	 */
	public boolean hasNeighbours();

	/**
	 * Return a neighbour square in a given direction.
	 * 
	 * @param	direction
	 * 				The direction in which the other square is situated.
	 * @return 	The neighbour of the border in the given direction.
	 * 			| 	then result == (getBorderAt(direction).getNeighbour1() == this) ? 
	 * 			|		getBorderAt(direction).getNeighbour2() : 
	 * 			|		getBorderAt(direction).getNeighbour1()
	 */
	public Square getNeighbour(Direction direction);

	/**
	 * Return a set of all squares that are in the space of this square, that is, 
	 * all squares separated by a non-isolating border.
	 * 
	 * @return	Each square that is connected to this square.
	 * 			| for each square in Square
	 * 			| 	result.contains(square) == isInSpace(square)
	 */
	public Set<Square> getSquaresInSpace();

	/**
	 * Check whether a given square belongs to the space of this square.
	 * The time complexity of this method is linear: the method will go through each square until 
	 * the right square is found (this is linear). Checking if the square is found is done in 
	 * constant time because of the use of a constant search. In total, the method thus is linear.
	 * 
	 * @param 	otherSquare
	 * 			A square to check if it belongs to the space of this square.
	 * @return	A boolean determining whether the given square belongs to the space of this square.
	 * 			| if (this == otherSquare)
	 * 			|	then result == true
	 * 			| else
	 * 			|	result == 
	 * 			|		for some direction in Direction
	 * 			|			!getBorder(direction).isIsolating() && (getNeighbourAt(direction).canNavigateTo(otherSquare)
	 */
	public boolean isInSpace(Square otherSquare);

	/**
	 * Check if a given square belongs to the space of this square and add all the squares that are in the space of 
	 * this square found during the search to a given set (if they haven't already been added) by recursively 
	 * checking all the neighbours of this square, the neighbours of the neighbours, and so on.
	 * 
	 * @param 	checkedSquares
	 * 			The set of squares in the space that have already been checked.
	 * @Pre		Each square in the set of checked squares is in the space of this square.
	 * 			| for each square in checkedSquares
	 * 			|	isInSpace(square)
	 * @Post	The list of checked squares is appended with the squares that are newly determined to be in the space of this square.
	 * 			| (new checkedSquares) == getNewCheckedSpaceSquares(otherSquare, checkedSquares)
	 * @return	True if this square is the other square or if the other square belongs to the space of the neighbours of
	 *			| if (this == otherSquare)
	 * 			|	then result == true
	 * 			| else
	 * 			|	result == 
	 * 			|		for some direction in Direction
	 * 			|			!getBorder(direction).isIsolating() && (getNeighbourAt(direction).canNavigateTo(otherSquare)
	 * 
	 */
	public boolean isInSpace(Square otherSquare, Set<Square> checkedSquares);
	
	/**
	 * Get the checked squares after a call to isInSpace with a given square and a given set of checked squares.
	 * 
	 * @param 	otherSquare
	 * 				The square that is to be determined if it is in the space of this square.
	 * @param 	checkedSquares
	 * 				The squares that have already been checked.
	 * @return	If this square is the given other square, the given set of checked squares is returned.
	 * 			| if (this == navigationTarget)
	 * 			|	then result == checkedSquares
	 * @return	Otherwise, the resulting set is the given set of checked squares, appended with this square, with all
	 * 			the new checked squares of the neighbours of this square that are separated by a non-isolating border. 
	 * 			| else 
	 * 			|	then 
	 * 			|		(result == checkedSquares) &&
	 * 			| 		(result.contains(this) && 
	 * 			| 		for each direction in Direction
	 * 			|			getBorderAt(direction).isIsolating() || (getNeighbour(direction) == null) ||
	 * 			|			result.containsAll(neighbour.getNewCheckedSpaceSquares(otherSquare, checkedSquares union this))
	 */
	public Set<Square> getNewCheckedSpaceSquares(Square otherSquare, Set<Square> checkedSquares);
	
	/**
	 * Merge this square with the squares in its space, that is, all the squares that are separated by 
	 * a non-isolating border.
	 *  
	 * @post	The squares in the space of this square are merged.
	 * 			| isMerged()
	 * @throws	TemperatureOutOfRangeException
	 * 				The calculated merged temperature is invalid for at least one of the squares in the space of this square.
	 * 				| for some Square square in getSquaresInSpace()
	 * 				| 	!square.canHaveAsTemperature(calculateMergedTemperature(getSquaresInSpace()))
	 * @effect	This method has the same effect as calling merge on all the squares in the space of this square, with
	 * 			the merged temperature calculated by the calculateMergedTemperature method and the merged humidity 
	 * 			calculated by the calculateMergedHumidity method.
	 * 			| Set<Square> squares = getSquaresInSpace();
	 * 			| Temperature mergedTemperature = calculateMergedTemperature(squares);
	 * 			| BigDecimal mergedHumidity = calculateMergedHumidity(squares); 
	 * 			| for (Square square : squares)
	 * 			| 	square.merge(mergedTemperature, mergedHumidity);
	 */
	@Raw
	public void merge();

	/**
	 * Update a square to the merged conditions.
	 * 
	 * @param	mergedTemperature
	 * 				The temperature to which this square's temperature is to be set.
	 * @param	mergedHumidity
	 * 				The humidity to which this square's humidity is to be set.
	 * @Pre		The origin of this call must be a merge operation.
	 * 			| isInSpace(mergeOrigin) && mergeOrigin.isMerging()
	 * @post	The new temperature is equal to the given temperature.
	 * 			| new.getTemperature() == mergedTemperature
	 * @post	The new humidity is equal to the given humidity, rounded by two decimals.
	 * 			| new.getHumidity() == mergedHumidity.setScale(2, RoundMode.HALF_UP)
	 * @throws	TemperatureOutOfRangeException
	 * 				The given temperature is not a valid temperature for this square.
	 * 				| !canHaveAsTemperature(mergedTemperature)
	 * @throws	NullPointerException
	 * 				The given humidity or the given temperature is not effective.
	 * 				| (mergedTemperature == null) || (mergedHumidity == null)  
	 */
	@Raw
	public void merge(Temperature mergedTemperature, BigDecimal mergedHumidity, @Raw Square mergeOrigin) 
	 	throws TemperatureOutOfRangeException, NullPointerException;

	/**
	 * Check whether all the squares in the space of this square are merged, that is, if they all have the same temperature and humidity.
	 * 
	 * @return	True if and only if all the squares in the same space have the same temperature and humidity.
	 * 			| result == 
	 * 			| 	for each Square square in getSquaresInSpace()
	 * 			|		(this.getTemperature().compareTo(square.getTemperature()) == 0) && 
	 * 			|		(this.getHumidity().compareTo(square.getHumidity()) == 0)
	 */
	public boolean isMerged();
	
	/**
	 * Check whether this square is in the state of merging.
	 */
	@Basic @Raw
	public boolean isMerging();
	
	/**
	 * Return whether an avatar can enter this square.
	 */
	public boolean canEnter();
	
	/**
	 * Return all the accessible direct neighbours of this square.
	 * 
	 * @return	The result set contains the neighbours of this square in each direction that are 
	 * 			separated by a non-isolating borders.
	 * 			| for each direction in Direction.values()
	 * 			|	if ((getNeighbour(direction) != null) && !getBorderAt(direction).isIsolating())
	 * 			|		then result.contains(getNeighbour(direction)) == true
	 */
	public Set<Square> getAccessibleNeighbours();
	
	/**
	 * Check whether it is possible for an avatar to go from this square to the given other square.
	 * 
	 * @param	otherSquare
	 * 				The other square to navigate to.
	 * @Pre		The given other square is effective.
	 * 			| otherSquare != null
	 * @return	True if and only if a path can be found starting from this square to the given square.
	 * 			| if (this == otherSquare)
	 * 			|	result == true
	 * 			| else
	 * 			|	result == 
	 * 			|		for some neighbour in getAccessibleNeighbours()
	 * 			|			neighbour.canNavigateTo(otherSquare)
	 */
	public boolean canNavigateTo(Square otherSquare);
	
	/**
	 * Check whether it is possible for an avatar to go from this square to the given other square, 
	 * with a given set of squares that represents all the squares that have already been determined reachable.
	 *
	 * @param	otherSquare
	 * 				The other square to navigate to.
	 * @param	checkedSquares
	 * 				The squares that have already been determined reachable.
	 * @Pre		The given other square and the given set of checked squares are effective.
	 * 			| (otherSquare != null) && (checkedSquares != null)
	 * @Pre		Each square in the set of checked squares can be navigated to from this square.
	 * 			| for each square in checkedSquares
	 * 			|	canNavigateTo(square)
	 * @Post	The list of checked squares is appended with the newly reachable determined squares.
	 * 			| (new checkedSquares) == getNewCheckedNavigationSquares(otherSquare, checkedSquares)
	 * @return	True if and only if a path can be found starting from this square to the given square.
	 * 			| if (this == otherSquare)
	 * 			|	result == true
	 * 			| else
	 * 			|	result == 
	 * 			|		for some neighbour in getAccessibleNeighbours()
	 * 			|			neighbour.canNavigateTo(otherSquare)
	 */
	public boolean canNavigateTo(Square otherSquare, Set<Square> checkedSquares);
	
	/**
	 * Get the checked squares after a call to canNavigateTo with a given navigation target and a given set of checked squares.
	 * 
	 * @param 	navigationTarget
	 * 				The square that is to be navigated to.
	 * @param 	checkedSquares
	 * 				The squares that have already been checked.
	 * @Pre		The given navigation target and the given set of checked squares are effective.
	 * 			| (navigationTarget != null) && (checkedSquares != null)
	 * @Pre		Each square in the set of checked squares can be navigated to from this square.
	 * 			| for each square in checkedSquares
	 * 			|	canNavigateTo(square)
	 * @return	If this square is the given navigation target, the given set of checked squares is returned.
	 * 			| if (this == navigationTarget)
	 * 			|	then result == checkedSquares
	 * @return	Otherwise, the resulting set is the given set of checked squares, appended with this square, with all
	 * 			the new checked squares of the accessible neighbours of this square. 
	 * 			| else 
	 * 			|	then 
	 * 			|		(result == checkedSquares) &&
	 * 			| 		(result.contains(this) && 
	 * 			| 		for each neighbour in getAccessibleNeighbours()
	 * 			|			result.containsAll(neighbour.getNewCheckedNavigationSquares(navigationTarget, checkedSquares union this))
	 */
	public Set<Square> getNewCheckedNavigationSquares(Square navigationTarget, Set<Square> checkedSquares);
		
	/**
	 * Disconnect this square from its neighbours and split every border so that
	 * this square and every former neighbour end up with neighbourless copies of it.
	 * 
	 * @effect	For each direction, the border at that direction is split.
	 * 			| for (Direction direction : Direction.values())
	 * 			|	getBorderAt(direction).splitBorder();
	 */
	public void disconnect();
	
	/**
	 * Get a value determining whether this square is in the state of disconnecting.
	 */
	@Basic @Raw
	public boolean isDisconnecting();
}