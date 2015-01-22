package project.temperature;
 import be.kuleuven.cs.som.annotate.*;

/**
 * A class for signaling temperatures that are out of range, with a minimum temperature, a maximum
 * temperature and a temperature.
 * 
 * @version 1.2
 * @author Stef Noten & Jasper Hilven
 */
public class TemperatureOutOfRangeException extends RuntimeException {
	/**
	 * Initialise this new temperature out of range exception. 
	 * 
	 * @param 	temperature
	 * 				The temperature that caused this out of range exception.
	 * @param 	minTemperature
	 * 				The minimum temperature of the range.
	 * @param 	maxTemperature
	 * 				The maximum temperature of the range.
	 * 
	 * @post 	The temperature of this new temperature out of range exception is equal to the given
	 * 			temperature.
	 * 			| new.getTemperature() == temperature
	 * @post 	The minimum temperature of this new temperature out of range exception is equal to the
	 *       	given minimum temperature. 
	 *			| new.getMinTemperature() == minTemperature
	 * @post 	The maximum temperature of this new temperature out of range exception is equal to the
	 *       	given maximum temperature. 
	 *			| new.getMaxTemperature() == maxTemperature
	 *
	 * @throws 	NullPointerException
	 *				The minimum temperature, maximum temperature or the temperature is not effective.
	 *				| (temperature ==  null) || (minTemperature == null) || (maxTemperature == null)
	 * @throws 	IllegalArgumentException
	 *				The given minimum temperature is greater than the given maximum temperature when 
	 *				expressed in the same temperature unit or the given temperature lies between the given
	 *				minimum and maximum temperatures, that is, it is not out of range. 
	 *				| (minTemperature.compareTo(maxTemperature) > 0) || 
	 *				| ( (temperature.compareTo(minTemperature) >= 0) && (temperature.compareTo(maxTemperature) <= 0))
	 */
	public TemperatureOutOfRangeException(Temperature temperature, Temperature minTemperature, Temperature maxTemperature)
		throws NullPointerException, IllegalArgumentException 
	{
		if ((temperature == null) || (minTemperature == null) || (maxTemperature == null))
			throw new NullPointerException();
		if ((minTemperature.compareTo(maxTemperature) > 0) ||
			((temperature.compareTo(minTemperature) >= 0) && (temperature.compareTo(maxTemperature) <= 0)))
			throw new IllegalArgumentException();
		
		this.temperature = temperature;
		this.minTemperature = minTemperature;
		this.maxTemperature = maxTemperature;
	}

	/**
	 * Return the temperature for this temperature out of range exception.
	 */
	@Basic @Immutable
	public Temperature getTemperature() {
		return temperature;
	}
	
	/**
	 * Variable registering the temperature involved in this temperature out of range exception.
	 */
	private final Temperature temperature;

	
	/**
	 * Return the minimum temperature for this temperature out of range exception.
	 */
	@Basic @Immutable
	public Temperature getMinTemperature() {
		return minTemperature;
	}

	/**
	 * Variable registering the minimum temperature involved in this temperature out of range exception.
	 */
	private final Temperature minTemperature;

	/**
	 * Return the maximum temperature for this temperature out of range exception.
	 */
	@Basic @Immutable
	public Temperature getMaxTemperature() {
		return maxTemperature;
	}

	/**
	 * Variable registering the maximum temperature involved in this temperature out of range exception.
	 */
	private final Temperature maxTemperature;

	/**
	 * Version number of this class.
	 */
	private static final long serialVersionUID = -8108404214167677090L;

}
