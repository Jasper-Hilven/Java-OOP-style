package project.squares;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import project.misc.Direction;
import project.squares.borders.Border;
import project.squares.borders.BorderState;
import project.squares.borders.OpenBorder;
import project.squares.borders.Wall;
import project.temperature.Temperature;
import project.temperature.TemperatureOutOfRangeException;
import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Immutable;
import be.kuleuven.cs.som.annotate.Model;
import be.kuleuven.cs.som.annotate.Raw;

/**
 * An implementation of a square, the smallest unit of location with a temperature, a minimum and a maximum temperature, 
 * a humidity, a slippery material property and borders in different directions.
 * 
 * @version 3.0
 * @author 	Stef Noten & Jasper Hilven
 */
public class SquareImpl implements Square {
	
	/**
	 * Initialise a new square, with given temperature and humidity.
	 * 
	 * @param	temperature 
	 * 				The temperature to initialise this square with.
	 * @param	humidity 
	 * 				The humidity of the square, expressed as a percentage.
	 * @param	hasSlipperyMaterial
	 * 				A boolean determining whether this square is made of slippery material.
	 * @param	wallDirections
	 * 				The directions in which to give the new square a wall.
	 * @Pre		The given initial humidity must be a valid humidity.
	 * 			| isValidHumidity(humidity)
	 * @Post	The minimum temperature for this square is -200°C.
	 * 			| new.getMinTemperature().getValueInCelcius() == -200
	 * @Post	The maximum temperature for this square is 5000°C.
	 * 			| new.getMaxTemperature().getValueInCelcius() == 5000
	 * @Post	The temperature for this square is equal to the given temperature.
	 * 			| new.getTemperature() == temperature
	 * @Post	The humidity for this square is equal to the given humidity, rounded by two decimals.
	 * 			| new.getHumidity().compareTo(humidity.setScale(2, RoundingMode.HALF_UP)) == 0
	 * @Post	The material of this square is slippery if and only if the given slippery parameter is true.
	 * 			| new.hasSlipperyMaterial() == hasSlipperyMaterial
	 * @Post	The walls of this new square are determined by the getWallsAt method, to which the given wall directions
	 * 			are passed.
	 * 			| for each direction in Direction.values()
	 * 			|	getWallsAt(wallDirections).get(direction).isUninitialisedCopyOf(new.getBorderAt(direction))
	 * @Throws	NullPointerException
	 * 				The given temperature or humidity is not effective.
	 * 				| (temperature == null) || (humidity == null)
	 * @Throws 	TemperatureOutOfRangeException(temperature, getMinTemperature(), getMaxTemperature())
	 * 				The temperature does not lie between the minimum and maximum temperature.
	 * 				| !matchesTemperatureBoundaries(temperature, new Temperature(-200), new Temperature(5000))
	 * @Throws	IllegalArgumentException
	 * 				!canHaveAsBorders(getWallsAt(wallDirections))
	 */
	@Raw
	public SquareImpl(Temperature temperature, BigDecimal humidity, Boolean hasSlipperyMaterial, Direction... wallDirections)
		throws NullPointerException, TemperatureOutOfRangeException, IllegalArgumentException
	{
		this.minTemperature = new Temperature(-200);
		this.maxTemperature = new Temperature(5000);
		setTemperature(temperature);
		
		setHumidity(humidity);
		this.slipperyMaterial = hasSlipperyMaterial;
		initialiseBorders(getWallsAt(wallDirections));
		
		this.isMerging = false;
		this.isDisconnecting = false;
		this.isConnecting = false;
	}
	
	/**
	 * Initialise a new square with given directions in which to build walls.
	 * 
	 * @effect	This new square is initialised in the same way a new square would be initialised
	 * 			with the more extended constructor involving 25°C as its temperature, 50% as its humidity,
	 * 			no slippery material, and the given directions as the directions in which to initialise a wall.
	 * 			| this(new Temperature(25), new BigDecimal(50), false, wallDirections)
	 */
	@Raw
	public SquareImpl(Direction... wallDirections) {
		this(new Temperature(25), new BigDecimal(50), false, wallDirections);
	}	
	
	/**
	 * Initialise a new square with default properties.
	 * 
	 * @effect	This new square is initialised in the same way a new square would be initialised
	 * 			with the more extended constructor involving the floor direction as the direction
	 * 			in which to initialise a wall.
	 * 			| this(Direction.FLOOR)
	 */
	@Raw
	public SquareImpl() {
		this(Direction.FLOOR);
	}	
	
	/**
	 * Return the temperature of this square.
	 */
	@Override
	public Temperature getTemperature() {
		return this.temperature;
	}
	
	/**
	 * Set the temperature of this square.
	 */
	@Override
	public void changeTemperature(Temperature temperature) 
		throws NullPointerException, TemperatureOutOfRangeException
	{
		setTemperature(temperature);
		merge();
	}
	
	/**
	 * Check whether it is possible to change the temperature of this square.
	 */
	@Override
	public boolean canChangeTemperature() {
		return true;
	}
	
	/**
	 * Set the temperature of this square.
	 * 
	 * @param 	temperature
	 *				The new temperature of this square.
	 * @post	The temperature of this square is equal to the given temperature.
	 * 			| new.getTemperature() == temperature
	 * @throws 	NullPointerException
	 * 				The given temperature is not effective.
	 * 				| temperature == null
	 * @throws 	TemperatureOutOfRangeException(temperature, getMinTemperature(), getMaxTemperature())
	 *				The given temperature is not valid for this square.
	 *				| !canHaveAsTemperature(temperature)
	 */
	@Model @Raw
	private void setTemperature(Temperature temperature) 
		throws NullPointerException, TemperatureOutOfRangeException
	{
		if (!canHaveAsTemperature(temperature))
			throw new TemperatureOutOfRangeException(temperature, getMinTemperature(), getMaxTemperature());
		this.temperature = temperature;
	}
	
	/**
	 * Return whether a given temperature is valid for this square.
	 */
	@Raw @Override
	public boolean canHaveAsTemperature(Temperature temperature) 
		throws NullPointerException
	{
		return matchesTemperatureBoundaries(temperature, getMinTemperature(), getMaxTemperature());
	}
	
	/**
	 * Return whether a given temperature is valid for a given minimum and maximum temperature. 
	 * 
	 * @param 	temperature
	 *         		The temperature to check.
	 * @param 	minTemperature
	 * 				The minimum temperature.
	 * @param 	maxTemperature
	 * 				The maximum temperature.
	 * @return 	True if the given maximum temperature is greater than or equal to the given minimum temperature 
 * 				and the given temperature lies between them. 
	 * 			| result == ((temperature.compareTo(getMinTemperature()) >= 0) &&
	 *			| 			(temperature.compareTo(getMaxTemperature()) <= 0))
	 * @throws 	NullPointerException
	 * 				The given temperature, the minimum or the maximum temperature is not effective.
	 * 				| (temperature == null) || (minTemperature == null) || (maxTemperature == null)
	 */
	public static boolean matchesTemperatureBoundaries(Temperature temperature, Temperature minTemperature, Temperature maxTemperature)
		throws NullPointerException
	{
		if ((temperature == null) || (minTemperature == null) || (maxTemperature == null)) 
			throw new NullPointerException();
		return ((temperature.compareTo(minTemperature) >= 0) && 
				(temperature.compareTo(maxTemperature) <= 0));
	}
	
	/**
	 * A variable registering the temperature for this square.
	 */
	private Temperature temperature;
	
	/**
	 * Return the minimum temperature of this square.
	 */
	@Basic @Override
	public Temperature getMinTemperature() {
		return this.minTemperature;
	}
	
	/**
	 * Set the minimum temperature of this square.
	 */
	@Override
	public void setMinTemperature(Temperature minTemperature) 
		throws TemperatureOutOfRangeException, NullPointerException, IllegalArgumentException
	{	
		if (!matchesTemperatureBoundaries(getTemperature(), minTemperature, getMaxTemperature()))
			throw new TemperatureOutOfRangeException(getTemperature(), minTemperature, getMaxTemperature());
		this.minTemperature = minTemperature;
	}
	
	/**
	 * A variable registering the minimum temperature of this square.
	 */
	private Temperature minTemperature;
	
	/**
	 * Return the maximum temperature of this square.
	 */
	@Basic @Override
	public Temperature getMaxTemperature() {
		return this.maxTemperature;
	}
	
	/**
	 * Set the maximum temperature of this square.
	 */
	@Override
	public void setMaxTemperature(Temperature maxTemperature) 
		throws TemperatureOutOfRangeException, NullPointerException, IllegalArgumentException
	{	
		if (!matchesTemperatureBoundaries(getTemperature(), getMinTemperature(), maxTemperature))
			throw new TemperatureOutOfRangeException(getTemperature(), getMinTemperature(), maxTemperature);
		this.maxTemperature = maxTemperature;
	}
	
	/**
	 * A variable registering the maximum temperature of this square.
	 */
	private Temperature maxTemperature;
	
	/**
	 * Return the cold damage caused by the coldness of this square.
	 */
	@Override
	public int getColdDamage() {
		double tempInCelcius = getTemperature().getValueInCelcius();
		if (tempInCelcius >= -5)
			return 0;
		// The following statements can never give problems
		// because tempInCelcius is restricted to [-273.15, -5]
		double damage = -tempInCelcius / 10 - 0.5;
		double damageRounded = (double)Math.floor(damage);
		return (int)damageRounded;
	}
	
	/**
	 *  Return the heat damage caused by the heat of this square.
	 */
	@Override
	public int getHeatDamage() {
		if (getTemperature().compareTo(getHeatDamageBoundaryTemperature()) <= 0)
			return 0;
		
		// The following statements can never give problems
		// because getTemperature() is bigger than getHeatDamageBoundaryTemperature()
		double damage = (getTemperature().getValueInCelcius() - 
				getHeatDamageBoundaryTemperature().getValueInCelcius()) * getHeatDamagePerDegreeCelcius();
		return (int)Math.min((double)Integer.MAX_VALUE, Math.floor(damage));
	}
	
	/**
	 * Return the boundary temperature from which heat damage starts to occur.
	 */
	@Basic
	public static Temperature getHeatDamageBoundaryTemperature() {
		return heatDamageBoundaryTemperature;
	}
	
	/**
	 * Set the boundary temperature from which heat damage starts to occur.
	 * 
	 * @param 	temperature
	 * 				The temperature from which heat damage needs to start to occur.
	 * @post	The heat damage boundary temperature is equal to the given temperature.
	 * 			| new.getHeatDamageBoundaryTemperature() == temperature
	 * @throws	NullPointerException
	 * 				The given temperature is not effective.
	 * 				| temperature == null
	 */
	@Basic
	public static void setHeatDamageBoundaryTemperature(Temperature temperature)
		throws NullPointerException
	{
		if (temperature == null)
			throw new NullPointerException();
		heatDamageBoundaryTemperature = temperature;
	}
	
	/**
	 * A variable registering the boundary temperature from which heat damage starts to occur.
	 */
	private static Temperature heatDamageBoundaryTemperature = new Temperature(35);
	
	/**
	 * Return the heat damage per degree Celcius.
	 */
	@Basic
	public static double getHeatDamagePerDegreeCelcius() {
		return heatDamagePerDegreeCelcius;
	}
	
	/**
	 * Set the heat damage per degree Celcius.
	 * 
	 * @param 	value
	 * 				The value to set the heat damage per degree Celcius to.
	 * @post	The heat damage per degree Celcius is equal to the given value.
	 * 			| new.getHeatDamagePerDegreeCelcius() == value
	 * @throws 	IllegalArgumentException
	 * 				The given value is not a valid heat damage per degree Celcius.
	 * 				| !isValidHeatDamagePerDegreeCelcius(value)
	 */
	@Basic
	public static void setHeatDamagePerDegreeCelcius(double value)
		throws IllegalArgumentException
	{
		if (!isValidHeatDamagePerDegreeCelcius(value))
			throw new IllegalArgumentException();
		heatDamagePerDegreeCelcius = value;
	}
	
	/**
	 * Return whether a given value is a valid heat damage per degree Celcius.
	 *
	 * @param 	value
	 * 				The value to check the validity for.
	 * @return	True if the value is not negative, false otherwise.
	 * 				| result == (value >= 0)
	 */
	public static boolean isValidHeatDamagePerDegreeCelcius(double value) {
		return (value >= 0);
	}
	
	/**
	 * A variable registering the additional heat damage per degree Celcius 
	 * above the heat damage boundary temperature.
	 */
	private static double heatDamagePerDegreeCelcius = 1d/15d;
	
	/**
	 * Return the points of damage caused by rust to metal objects. 
	 */
	@Override
	public int getRustDamage() {
		BigDecimal humidity = getHumidity();
		BigDecimal damageBoundary = new BigDecimal(30);		
		if (humidity.compareTo(damageBoundary) <= 0)
			return 0;
		
		BigDecimal percentPerDamagePoint = new BigDecimal(7);
		BigDecimal damageBigDecimal = humidity.subtract(damageBoundary)
			.divide(percentPerDamagePoint, 0, BigDecimal.ROUND_DOWN);
		return damageBigDecimal.intValue();
	}
	
	/**
	 * Return the humidity of this square.
	 */
	@Basic @Override
	public BigDecimal getHumidity() {
		return humidity;
	}

	/**
	 * Set the humidity of the square to the given humidity.
	 */
	@Raw @Override
	public void changeHumidity(BigDecimal humidity){
		setHumidity(humidity);
		merge();
	}
	
	/**
	 * Set the humidity of the square to the given humidity.
	 * 
	 * @param	humidity
	 * 				The humidity to assign, expressed as a percentage.
	 * @pre 	The humidity to assign is a valid humidity.
	 * 			| isValidHumidity(humidity)	
	 * @post 	The new humidity of the square equals the given humidity, rounded to two decimals.
	 *			| new.getHumidity().compareTo(humidity.setScale(2, RoundingMode.HALF_UP)) == 0
	 */
	@Model @Raw
	private void setHumidity(BigDecimal humidity){
		assert isValidHumidity(humidity);
		this.humidity = humidity.setScale(2, RoundingMode.HALF_UP);
	}
	
	/**
	 * Check whether the humidity is a valid humidity, that is, the humidity is effective and lies between 0 and 100.
	 * 
	 * @param 	humidity 
	 * 				The humidity expressed as a percentage.
	 * @return	True if the humidity is effective and lies between 0 and 100, false otherwise.
	 *			| result == (humidity != null) && ((humidity.compareTo(BigDecimal.ZERO) >= 0) && 
	 *			|			(humidity.compareTo(new BigDecimal(100)) <= 0))
	 */		
	public static boolean isValidHumidity(BigDecimal humidity){
		if (humidity == null)
			return false;
		return (humidity.compareTo(BigDecimal.ZERO) >= 0) && (humidity.compareTo(new BigDecimal(100)) <= 0); 
	}
	
	/**
	 * A variable registering the humidity of this square.
	 */
	private BigDecimal humidity;

	/**
	 * Return whether this square is slippery.
	 */
	@Override
	public boolean isSlippery() {
		boolean waterSlippery = 
			((getHumidity().compareTo(new BigDecimal(100)) == 0) && (getTemperature().getValueInCelcius() >= 0));
		boolean iceSlippery =
			((getHumidity().compareTo(new BigDecimal(10)) > 0) && (getTemperature().getValueInCelcius() < 0));
		return waterSlippery || iceSlippery || hasSlipperyMaterial();
	}
	
	/**
	 * Return whether this square is made of slippery material.
	 */
	@Basic @Immutable @Override
	public boolean hasSlipperyMaterial() {
		return slipperyMaterial;
	}
	
	/**
	 * A variable registering whether the material of this square is slippery.
	 */
	private boolean slipperyMaterial;
	
 	/**
 	 * Get a value indicating the inhabitability of this square.
 	 */
	@Override
	public double getInhabitability(){
		// The following statements can never overflow or give an error,
		// because humidity satisfies the class invariant 
		double heatInhabitability  = - Math.pow(getHeatDamage(),1.5)/Math.sqrt(101-getHumidity().doubleValue());
		double coldInhabitability = - Math.sqrt(getColdDamage());
		return (double)(heatInhabitability + coldInhabitability);
	}
	
	/**
	 * Return the heat capacity of the material of a square.
	 * This value is used to calculate the temperature of two merged squares.
	 */
	@Basic
	public static double getHeatCapacity() {
		return heatCapacity;
	}

	/**
	 * Set the heat capacity to a given value.
	 * 
	 * @param 	value
	 * 				The value to set.
	 * @throws 	IllegalArgumentException
	 * 				The given value is not a valid heat capacity.
	 * 				| !isValidHeatCapacity(value)
	 * @post	The new heat capacity will equal the given value.
	 * 			| (new Square).getHeatCapacity() == value 
	 */
	public static void setHeatCapacity(double value) throws IllegalArgumentException {
		if (!isValidHeatCapacity(value))
			throw new IllegalArgumentException();
		heatCapacity = value;
	}
	
	/**
	 * Check whether a given value is a valid heat capacity.
	 * 
	 * @param 	value
	 * 				The value to check.
	 * @return	True if the value lies between 0.1 and 0.4, false otherwise.
	 * 			| result == (value >= 0.1) && (value <= 0.4)
	 */
	public static boolean isValidHeatCapacity(double value) {
		return (value >= 0.1) && (value <= 0.4);
	}
	
	/**
	 * A variable registering the heat capacity of the material of a square.
	 */
	private static double heatCapacity = 0.2;
	
	/**
	 * Return the border of this square in the given direction.
	 */
	@Basic @Override
	public Border getBorderAt(Direction direction) throws NullPointerException {
		if (direction == null)
			throw new NullPointerException();
		return getBorders().get(direction);
	}
	
	/**
	 * Return the borders of this square.
	 */
	@Basic @Override
	public Map<Direction, Border> getBorders() {
		return new HashMap<Direction, Border>(borders);
	}

	/**
	 * Set a border of a square in a given direction to a given border.
	 */
	@Raw @Override
	public void setBorderAt(Direction direction, @Raw Border border)
		throws IllegalArgumentException
	{
		if (!canHaveAsBorderAt(direction, border))
			throw new IllegalArgumentException();
		assert ((border.getState() == BorderState.INITIALISED) && border.hasNeighbour(this));
		assert (!hasBorderAt(direction) || (getBorderAt(direction).getState() == BorderState.TERMINATED));
		
		borders.remove(direction);
		borders.put(direction, border);
		
	}
	
	/**
	 * Connect this square with the given neighbours.
	 */
	public void connect(Map<Direction, Square> neighbours) throws NullPointerException {
		if (!canConnect(neighbours))
			throw new IllegalArgumentException();
		this.isConnecting = true;
		Map<Direction, Border> dominantBorders = getDominantBorders(neighbours);
		for (Direction direction : Direction.values()) {
			if (neighbours.containsKey(direction))
				dominantBorders.get(direction).build(this, direction, neighbours.get(direction));
		}		
		this.isConnecting = false;
	}

	/**
	 * Check whether this square can connect with the given neighbours.
	 */
	public boolean canConnect(Map<Direction, Square> neighbours) throws NullPointerException {
		if (hasNeighbours())
			return false;
		Map<Direction, Border> dominantBorders = getDominantBorders(neighbours);
		if (!this.canHaveAsBorders(dominantBorders))
			return false;
		for (Direction direction : neighbours.keySet()) {
			if (!neighbours.get(direction).canHaveAsBorderAt(direction.getOppositeDirection(), dominantBorders.get(direction)))
				return false;
		}
		return true;
	}

	/**
	 * Get whether this square is in the state of connecting with neighbouring squares.
	 */
	@Basic
	public boolean isConnecting() {
		return isConnecting;
	}
	
	/**
	 * A variable determining whether this square is in the state of connecting.
	 */
	private boolean isConnecting;

	/**
	 * Get a map that contains an uninitialised dominant border for the current borders and the borders of the given neighbours.
	 */
	public Map<Direction, Border> getDominantBorders(Map<Direction, Square> neighbours) throws NullPointerException {
		if ((neighbours == null) || neighbours.containsKey(null) || neighbours.containsValue(null))
			throw new NullPointerException();
		HashMap<Direction, Border> result = new HashMap<Direction, Border>();
		for (Direction direction : Direction.values()) {
			if (!neighbours.containsKey(direction))
				result.put(direction, this.getBorderAt(direction).getUninitialisedCopy());
			else {
				Border otherBorder = neighbours.get(direction).getBorderAt(direction.getOppositeDirection());
				result.put(direction, otherBorder.getDominantBorder(this.getBorderAt(direction)).getUninitialisedCopy());
			}
		}
		return result;
	}
	
	/**
	 * Initialise the borders of this square.
	 * 
	 * @param	mapBorders
	 * 				The borders to initialise the square with.
	 * @Pre		All the given borders must be uninitialised.
	 * 			| for each direction in Direction.values()
	 * 			|	mapBorders.get(direction).getState() == BorderState.UNINITIALISED
	 * @Post	The new square has the given borders as borders.
	 * 			| for each direction in Direction.values()
	 * 			| 	new.getBorderAt(direction) == mapBorders.get(direction)
	 * @throws	IllegalArgumentException
	 *				This square cannot have the given borders as borders.
	 *				| !canHaveAsBorders(mapBorders)
	 */
	@Raw
	private void initialiseBorders(Map<Direction, Border> mapBorders) 
		throws IllegalArgumentException
	{
		if (!canHaveAsBorders(mapBorders))
			throw new IllegalArgumentException();
		this.borders = new HashMap<Direction, Border>(mapBorders);
		for (Direction direction : Direction.values()) {
			assert(mapBorders.get(direction).getState() == BorderState.UNINITIALISED);
		}
		
		for (Direction direction : Direction.values()) {
			this.borders.put(direction, new OpenBorder());
			mapBorders.get(direction).build(this, direction, null);
		}
	}
	
	/**
	 * Checks whether a square has a border in the given direction.
	 */
	public boolean hasBorderAt(Direction direction) throws NullPointerException {
		return (getBorderAt(direction) != null);
	}
		
	/**
	 * Check whether this square can have a given border at a given direction.
	 */
	@Override
	public boolean canHaveAsBorderAt(Direction direction, Border border) {
		if (isConnecting())
			return true; 
		return canHaveAsBorders(getBordersReplacedBy(direction, border));
	}

	/**
	 * Get a map containing the borders of this square, in which the border in the given direction is 
	 * replaced by the given border.
	 */
	@Override
	public Map<Direction, Border> getBordersReplacedBy(Direction direction, Border border) {
		HashMap <Direction,Border> borderMap = new HashMap<Direction, Border>();
		borderMap.putAll(getBorders());
		borderMap.remove(direction);
		borderMap.put(direction, border);
		return borderMap;
	}
	
	/**
	 * Check whether this square can have the given borders as its borders.
	 */
	@Raw @Override
	public boolean canHaveAsBorders(Map<Direction, Border> mapBorders) {
		if ((mapBorders == null) || mapBorders.containsKey(null))
			return false;
		 for (Border border : mapBorders.values()) {
			if ((border != null) && (border.getState() == BorderState.TERMINATED))
				return false;
		}
		return canHaveAsBordersIfNotTerminated(mapBorders);
	}

	/**
	 * Check whether this square can have the given borders if this square is not terminated.
	 */
	@Raw @Override
	public boolean canHaveAsBordersIfNotTerminated(Map<Direction, Border> mapBorders) {
		if (mapBorders == null)
			return false;
		
		int wallCount = 0;
		int doorCount = 0;		
		for (Direction direction : Direction.values()) {
			Border border = mapBorders.get(direction); // check voor teruggeven null als key not found!
			if ((border == null) || (border.getState() == BorderState.TERMINATED)) 
				return false;
			if (border instanceof Wall) {
				wallCount++;
				if (((Wall)border).hasDoor()){
					doorCount++;
					if (direction == Direction.FLOOR)
						return false;
				}
			}
		}
		return wallCount >= 1 && doorCount <= 3;
	}

	/**
	 * Check whether this square has proper borders.
	 */
	@Override
	public boolean hasProperBorders() {
		if (!canHaveAsBorders(getBorders()))
			return false;
		for (Direction direction : Direction.values()) {
			if((!hasBorderAt(direction) || !getBorderAt(direction).hasNeighbour(this) || 
					(getBorderAt(direction).getState() != BorderState.INITIALISED)))
				return false;
		}
		return true;
	}

	/**
	 * A variable registering the borders of this square. 
	 */
	private HashMap<Direction, Border> borders;
	
	/**
	 * Get a map with walls for each given direction and with open borders for each not given direction.
	 *  
	 * @param	wallDirections
	 * 				A non-fixed amount of directions for which the returning map must have a wall at.
	 * @return	The returning map does not have ineffective borders in any direction. 
	 * 			| for each direction in Direction.values()
	 * 			| 	result.get(direction) != null
	 * @return	The resulting map will have a wall in each given direction.
	 * 			| for each direction in Direction.values()
	 * 			|	if (wallDirections.contains(direction)
	 * 			|		result.get(direction) instanceof Wall
	 * @return	The resulting map will have an open border in each direction that was not specified.
	 *			| for each direction in Direction.values()
	 *			|	if (!wallDirections.contains(direction)
	 *			|		then result.get(direction) instanceof OpenBorder
	 */
	public HashMap<Direction, Border> getWallsAt(Direction... wallDirections) {
		HashMap<Direction, Border> retMap = new HashMap<Direction, Border>();
		for (Direction direction : Direction.values()) {
			boolean containsDirection = false;
			for (int i = 0; i < wallDirections.length; i++) {
				if (wallDirections[i] == direction) {
					containsDirection = true;
				}
			}
			if (containsDirection)
				retMap.put(direction, new Wall(false, false));
			else
				retMap.put(direction, new OpenBorder());

		}
		return retMap;
	}	
	
	
	/**
	 * Check whether this square has one or more neighbours.
	 */
	@Override
	public boolean hasNeighbours() {
		for (Direction direction : Direction.values()) {
			if (getNeighbour(direction) != null)
				return true;
		}
		return false;
	}
	
	
	/**
	 * Return a neighbour square in a given direction.			
	 */
	@Override
	public Square getNeighbour(Direction direction) {
		Border border = getBorderAt(direction);
		return (border.getNeighbour1() == this) ? border.getNeighbour2() : border.getNeighbour1();
	}
	
	/**  
	 * Return a set of all squares that are in the space of this square, that is, 
	 * all squares separated by a non-isolating border.
	 */
	@Override
	 public Set<Square> getSquaresInSpace() {
		 Set<Square> result = new HashSet<Square>();
		 result.add(this);
		 isInSpace(null, result);
		 return result;
	 }

	/**
	 * Check whether a given square belongs to the space of this square.
	 * The time complexity of this method is linear: the method will go through each square until 
	 * the right square is found (this is linear). Checking if the square is found is done in 
	 * constant time because of the use of a constant search. In total, the method thus is linear.
	 */
	 @Override
	 public boolean isInSpace(Square otherSquare) {
		 return isInSpace(otherSquare, new HashSet<Square>());
	 }
	 
	 /**
	  * Check if a given square belongs to the space of this square and add all the squares that are in the space of 
	  * this square found during the search to a given set (if they haven't already been added) by recursively 
	  * checking all the neighbours of this square, the neighbours of the neighbours, and so on.
	  */
	 public boolean isInSpace(Square otherSquare, Set<Square> checkedSquares) {
		 if (this == otherSquare) 
			 return true;
		 checkedSquares.add(this);
		 Square neighbour;
		 for (Direction direction : Direction.values()) {
			 if (!getBorderAt(direction).isIsolating()) {
				 neighbour = getNeighbour(direction);
				 if ((neighbour != null) && !checkedSquares.contains(neighbour)) {
					if (neighbour.isInSpace(otherSquare, checkedSquares))
						 return true;
				 }
			 }
		 }
		 return false;
	 }
	 
	/**
	 * Get the checked squares after a call to isInSpace with a given square and a given set of checked squares.
	 */
	public Set<Square> getNewCheckedSpaceSquares(Square otherSquare, Set<Square> checkedSquares) {
		if (this == otherSquare)
			return checkedSquares;
		checkedSquares.add(this);
		for (Direction direction : Direction.values()) {
			if (!getBorderAt(direction).isIsolating() && (getNeighbour(direction) != null))
				checkedSquares = getNewCheckedSpaceSquares(otherSquare, checkedSquares);
		}
		return checkedSquares;
	}

	/**
	 * Merge this square with the squares in its space, that is, all the squares that are separated by 
	 * a non-isolating border.
	 */
	@Override @Raw
	public void merge() {
		Set<Square> squaresInSpace = getSquaresInSpace();
		Temperature newTemperature = calculateMergedTemperature(squaresInSpace);
		for (Square square : squaresInSpace) {
			if(!square.canHaveAsTemperature(newTemperature))
				throw new IllegalStateException();
		}
		
		BigDecimal newHumidity = calculateMergedHumidity(squaresInSpace);
		
		this.isMerging = true;
		for (Square square : squaresInSpace)
			square.merge(newTemperature, newHumidity, this);
		this.isMerging = false;
	}
	
	/**
	 * Update a square to the merged conditions.
	 */
	public void merge(Temperature mergedTemperature, BigDecimal mergedHumidity, @Raw Square mergeOrigin) 
		throws TemperatureOutOfRangeException, NullPointerException
	{
		assert isInSpace(mergeOrigin) && mergeOrigin.isMerging();
		
		if ((mergedTemperature == null) || (mergedHumidity == null))
			throw new NullPointerException();
		if (!canHaveAsTemperature(mergedTemperature)) 
			throw new TemperatureOutOfRangeException(mergedTemperature, getMinTemperature(), getMaxTemperature());
		setTemperature(mergedTemperature);
		setHumidity(mergedHumidity);
	}
	
	/**
	 * Check whether all the squares in the space of this square are merged, that is, if they all have the same temperature and humidity.
	 */
	@Override
	public boolean isMerged() {
		for (Square square : getSquaresInSpace()) {
			if ((this.getTemperature().compareTo(square.getTemperature()) != 0) || 
	  			(this.getHumidity().compareTo(square.getHumidity()) != 0))
				return false;
		}
		return true;
	}
	
	/**
	 * Check whether this square is in the state of merging.
	 */
	@Override
	public boolean isMerging() {
		return this.isMerging;
	}
	
	/**
	 * A variable registering whether this square is in a state of merging.
	 */
	private boolean isMerging;
	
	/**
	 * Calculate the humidity to set to merged squares.
	 * 
	 * @param 	other
	 * 				The other square that this square is being merged with.
	 * @return	The calculated mean humidity, that is, the humidity that all squares should have after merging. 
	 * 			| result.compareTo(MEAN(Square square in squaresToMerge : square.getHumidity())) == 0
	 */
	@Model
	protected BigDecimal calculateMergedHumidity(Set<Square> squaresToMerge) {
		BigDecimal totalHumidity = BigDecimal.ZERO;
		for (Square square : squaresToMerge)
			totalHumidity = totalHumidity.add(square.getHumidity());
		return totalHumidity.multiply(new BigDecimal(1.0 / squaresToMerge.size()));
	}
	
	/**
	 * Calculate the temperature to set to merged squares.
	 * 
	 * @param 	other
	 * 				The other square that this square is merged with.
	 * @Pre		The given set is effective.
	 * 			| squaresToMerge != null
	 * @Pre		The given set has at least one square.
	 * 			| squaresToMerge.size() >= 1
	 * @return	The calculated mean temperature, that is, the temperature that all squares should have after merging. 
	 * 			| result.getValueInCelcius() == MEAN(Square square in squaresToMerge : square.getTemperature().getValueInCelcius())
	 */
	@Model
	protected Temperature calculateMergedTemperature(Set<Square> squaresToMerge) {
		assert (squaresToMerge != null) && (squaresToMerge.size() >= 1);
		double factor = 1.0 / squaresToMerge.size();
		double result = 0;
		for (Square square : squaresToMerge)
			result += square.getTemperature().getValueInCelcius() * factor;
		return new Temperature(result);
	}
	
	/**
	 * Return whether an avatar can enter this square.
	 */
	public boolean canEnter() {
		return true;
	}
	
	/**
	 * Return all the accessible direct neighbours of this square.
	 */
	public Set<Square> getAccessibleNeighbours() {
		Set<Square> result = new HashSet<Square>();
		Square neighbour;
		for (Direction direction : Direction.values()) {
			neighbour = getNeighbour(direction);
			if ((neighbour != null) && !getBorderAt(direction).isIsolating())
				result.add(neighbour);
		}
		return result;
	}
	
	/**
	 * Check whether it is possible for an avatar to go from this square to the given other square.
	 */
	public boolean canNavigateTo(Square otherSquare){
		assert (otherSquare != null);
		return canNavigateTo(otherSquare, new HashSet<Square>());
	}
	
	/**
	 * Check whether it is possible for an avatar to go from this square to the given other square, 
	 * with a given set of squares that represents all the squares that have already been determined reachable.
	 */
	@Override
	public boolean canNavigateTo(Square otherSquare, Set<Square> checkedSquares) {
		assert ((otherSquare != null) && (checkedSquares != null));
		if (this == otherSquare) 
			 return true;
		 checkedSquares.add(this);
		 for (Square neighbour : getAccessibleNeighbours()) {
			 if (!checkedSquares.contains(neighbour)) {
				 if (neighbour.canNavigateTo(otherSquare, checkedSquares))
					 return true;
			 }
		 }
		 return false;
	}
	
	/**
	 * Get the checked squares after a call to canNavigateTo with a given navigation target and a given set of checked squares.
	 */
	@Override 
	public Set<Square> getNewCheckedNavigationSquares(Square navigationTarget, Set<Square> checkedSquares) {
		assert ((navigationTarget != null) && (checkedSquares != null));
		if (this == navigationTarget)
			return checkedSquares;
		checkedSquares.add(this);
		for (Square neighbour : getAccessibleNeighbours()) {
			if (!checkedSquares.contains(neighbour))
				checkedSquares = getNewCheckedNavigationSquares(navigationTarget, checkedSquares);
		}
		return checkedSquares;
	}
	
	/**
	 * Disconnect this square from its neighbours and split every border so that
	 * this square and every former neighbour end up with neighbourless copies of it.
	 */
	@Override
	public void disconnect() {
		this.isDisconnecting = true;
		for (Direction direction : Direction.values())
			getBorderAt(direction).splitBorder();
		this.isDisconnecting = false;
	}
	
	/**
	 * Get a value determining whether this square is in the state of disconnecting.
	 */
	@Override
	public boolean isDisconnecting() {
		return this.isDisconnecting;
	}
	
	/**
	 * A variable determining whether this square is in the state of disconnecting.
	 */
	private boolean isDisconnecting;
}

