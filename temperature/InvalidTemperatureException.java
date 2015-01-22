package project.temperature;
import be.kuleuven.cs.som.annotate.*;

/**
 * A class for signaling impossible temperatures with a given value and a temperature unit.
 * 
 * @version 1.0
 * @author Stef Noten & Jasper Hilven
 * 
 */
public class InvalidTemperatureException extends RuntimeException {

	/**
	 * Initialize this new invalid temperature exception with a given value and
	 * temperature unit.
	 * 
	 * @param 	value
	 *            	The value that caused this invalid temperature exception.
	 * @param 	unit
	 *            	The temperature unit for which the value caused this invalid
	 *            	temperature exception.
	 * @post 	The value of this new invalid temperature exception is equal to the
	 *       	given value. 
	 *       	| new.getValue() == value
	 * @post 	The temperature unit of this new invalid temperature exception is
	 *       	equal to the given temperature unit. 
	 *       	| new.getTemperatureUnit() == unit
	 */
	public InvalidTemperatureException(double value, TemperatureUnit unit) {
		this.value = value;
		this.unit = unit;
	}

	/**
	 * Return the temperature unit for this invalid temperature exception.
	 */
	@Basic @Immutable
	public TemperatureUnit getTemperatureUnit() {
		return unit;
	}

	/**
	 * Variable registering the temperature unit involved in this invalid
	 * temperature exception.
	 */
	private final TemperatureUnit unit;

	/**
	 * Return the value of the temperature for this invalid temperature
	 * exception.
	 */
	@Basic @Immutable
	public final double getValue() {
		return value;

	}

	/**
	 * Variable registering the value involved in this invalid temperature
	 * exception.
	 */
	private final double value;

	/**
	 * Version number of this class.
	 */
	private static final long serialVersionUID = 1365446685366158164L;
}
