package project.temperature;

import be.kuleuven.cs.som.annotate.*;

/**
 * An enumeration that represents temperature units. 
 * 
 * @version 1.4
 * @author Stef Noten & Jasper Hilven
 */
public enum TemperatureUnit {

	CELCIUS {
		/**
		 * Return the symbol of this temperature unit.
		 */
		public String getSymbol() {
			return "°C";
		}
		
		/**
		 * Return the smallest possible temperature for this temperature unit, that
		 * is, 0K expressed in this temperature unit.
		 */
		public double getMinimumTemperature() {
			return ABSOLUTE_ZERO_IN_CELCIUS;
		}
		
		/**
		 * Return the maximum temperature to be used for this temperature unit, that is,
		 * MAXIMUM_VALUE_IN_CELCIUS.
		 */
		public double getMaximumTemperature() {
			return MAXIMUM_VALUE_IN_CELCIUS;
		}

		
		/**
		 * Convert a given value from the Celcius unit to the Celcius unit.
		 * 
		 * @param 	value
		 *            	The value to convert.
		 * @throws 	InvalidTemperatureException
		 *           	The temperature is invalid. 
		 *           	| (!TemperatureUnit.CELCIUS.canHaveAsValue(value))
		 * @result 	The same value 
		 * 			| result == value
		 */
		public double convertFromCelcius(double value)
			throws InvalidTemperatureException
		{
			if (!TemperatureUnit.CELCIUS.canHaveAsValue(value))
				throw new InvalidTemperatureException(value, this);
			return value;
		}

		/**
		 * Convert a given value from the Celcius unit to the Celcius unit.
		 * 
		 * @param 	value
		 *            	The value to convert.
		 * @throws 	InvalidTemperatureException()
		 * 				The temperature is invalid. 
		 *  			| (!canHaveAsValue(value))
		 * @result 	The same value 
		 * 			| result == value
		 */
		public double convertToCelcius(double value)
			throws InvalidTemperatureException
		{
			if (!canHaveAsValue(value))
				throw new InvalidTemperatureException(value, this);
			return value;
		}
	},
	FARENHEIT {
		/**
		 * Return the symbol of this temperature unit.
		 */
		public String getSymbol() {
			return "°F";
		}
		
		/**
		 * Return the smallest possible temperature for this temperature unit, that
		 * is, 0K expressed in this temperature unit.
		 */
		public double getMinimumTemperature() {
			return ABSOLUTE_ZERO_IN_CELCIUS * 1.8 + 32;
		}

		/**
		 * Return the maximum temperature to be used for this temperature unit, that is,
		 * MAXIMUM_VALUE_IN_CELCIUS expressed in this temperature unit.
		 */
		public double getMaximumTemperature() {
			return MAXIMUM_VALUE_IN_CELCIUS * 1.8 + 32;
		}

		/**
		 * Convert a given value from the Celcius unit to the Farenheit unit.
		 * 
		 * @param 	value
		 *            	The value to convert.
		 * @throws 	InvalidTemperatureException
		 *             	The temperature is invalid. 
		 *             	| (!TemperatureUnit.CELCIUS.canHaveAsValue(value))
		 * @result 	The specified value converted to the Farenheit unit. 
		 * 			| result == value * 1.8 + 32  
		 */
		public double convertFromCelcius(double value)
			throws InvalidTemperatureException
		{
			if (!TemperatureUnit.CELCIUS.canHaveAsValue(value))
				throw new InvalidTemperatureException(value, this);
			return value * 1.8 + 32;
		}

		/**
		 * Convert a given value from the Farenheit unit to the Celcius unit.
		 * 
		 * @param 	value
		 *            	The value to convert.
		 * @throws 	InvalidTemperatureException
		 *             	The temperature is invalid. 
		 *             	| (!canHaveAsValue(value))
		 * @result 	The specified value converted to the Celcius unit. 
		 * 			| result == (value - 32) / 1.8
		 */
		public double convertToCelcius(double value)
			throws InvalidTemperatureException
		{
			if (!canHaveAsValue(value))
				throw new InvalidTemperatureException(value, this);
			return (value - 32) / 1.8;
		}
	},
	KELVIN {
		/**
		 * Return the symbol of this temperature unit.
		 */
		public String getSymbol() {
			return "K";
		}
		
		/**
		 * Return the smallest possible temperature for this temperature unit, that
		 * is, 0K expressed in this temperature unit.
		 */
		public double getMinimumTemperature() {
			return 0;
		}
		
		/**
		 * Return the maximum temperature to be used for this temperature unit, that is,
		 * MAXIMUM_VALUE_IN_CELCIUS expressed in this temperature unit.
		 */
		public double getMaximumTemperature() {
			return MAXIMUM_VALUE_IN_CELCIUS - ABSOLUTE_ZERO_IN_CELCIUS;
		}

		/**
		 * Convert a given value from the Celcius unit to the Kelvin unit.
		 * 
		 * @param 	value
		 *            	The value to convert.
		 * @throws 	InvalidTemperatureException
		 *             	The temperature is invalid. 
		 *             	| (!TemperatureUnit.CELCIUS.canHaveAsValue(value))
		 * @result 	The specified value converted to the Kelvin unit. 
		 * 			| result == value - ABSOLUTE_ZERO_IN_CELCIUS
		 */
		public double convertFromCelcius(double value)
			throws InvalidTemperatureException
		{
			if (!TemperatureUnit.CELCIUS.canHaveAsValue(value))
				throw new InvalidTemperatureException(value, this);
			return value - ABSOLUTE_ZERO_IN_CELCIUS;
		}

		/**
		 * Convert a given value from the Kelvin unit to the Celcius unit.
		 * 
		 * @param 	value
		 *            	The value to convert.
		 * @throws 	InvalidTemperatureException
		 *             	The temperature is invalid. 
		 *             	| (!canHaveAsValue(value))
		 * @result 	The specified value converted to the Celcius unit. 
		 * 			| result == value + ABSOLUTE_ZERO_IN_CELCIUS
		 */
		public double convertToCelcius(double value)
			throws InvalidTemperatureException
		{
			if (!canHaveAsValue(value))
				throw new InvalidTemperatureException(value, this);
			return value + ABSOLUTE_ZERO_IN_CELCIUS;
		}
	};

	/**
	 * Return the symbol of this temperature unit.
	 */
	@Basic @Immutable
	public abstract String getSymbol();

	/**
	 * Convert a given value from this temperature unit to the specified
	 * temperature unit.
	 * 
	 * @param 	unit
	 *            	The temperature unit to convert to.
	 * @param 	value
	 *            	The value to convert.
	 * @throws 	InvalidTemperatureException
	 *             	The temperature is invalid for this temperature unit. 
	 *             	| !this.canHaveAsValue(value)
	 * @throws 	NullPointerException
	 *             	The temperature unit is not effective.
	 *             	| unit == null
	 * @result 	The value converted to the specified temperature unit. 
	 * 			| if (this == unit)
	 * 			|	result == value) 
	 * 			| else
	 * 			|	result == unit.convertFromCelcius(this.convertToCelcius(value))
	 */
	public double convertTo(TemperatureUnit unit, double value)
		throws InvalidTemperatureException, NullPointerException
	{
		if (this == unit) {
			if (!canHaveAsValue(value))
				throw new InvalidTemperatureException(value, this);
			return value;
		}
		double valueInCelcius = this.convertToCelcius(value);
		return unit.convertFromCelcius(valueInCelcius);
	}
	
	/**
	 * Convert a given value from the Celcius unit to this temperature unit.
	 * 
	 * @param 	value
	 *            	The value to convert.
	 * @throws 	InvalidTemperatureException
	 *             	The temperature is invalid. 
	 *             	| !TemperatureUnit.CELCIUS.canHaveAsValue(value)
	 */
	public abstract double convertFromCelcius(double value)
			throws InvalidTemperatureException;

	/**
	 * Convert a given value from this temperature unit to the Celcius unit.
	 * 
	 * @param 	value
	 *            	The value to convert.
	 * @throws 	InvalidTemperatureException
	 *             	The temperature is invalid. 
	 *             	| !canHaveAsValue(value)
	 */
	public abstract double convertToCelcius(double value)
			throws InvalidTemperatureException;

	/**
	 * Return the smallest possible temperature for this temperature unit, that
	 * is, 0K expressed in this temperature unit.
	 */
	@Basic @Immutable
	public abstract double getMinimumTemperature();
	
	/**
	 * Return the maximum temperature to be used for this temperature unit, that is,
	 * MAXIMUM_VALUE_IN_CELCIUS expressed in this temperature unit.
	 */
	@Basic @Immutable
	public abstract double getMaximumTemperature();

	/**
	 * Return whether the temperature is valid for this temperature unit.
	 * 
	 * @param 	value
	 *            	The value to check.
	 * @return 	True if the value is not NaN, not smaller than the smallest
	 *         	possible temperature and not greater than the greatest possible
	 *         	temperature, false otherwise. 
	 *         	| result == ((value != Double.NaN) && (value >= getMinimumTemperature()) &&
	 *         	| 			(value <= getMaximumTemperature())) 
	 */
	public boolean canHaveAsValue(double value) {
		return (value != Double.NaN)
				&& (value >= getMinimumTemperature()) && (value <= getMaximumTemperature());
	}

	/**
	 * The absolute zero temperature expressed in the Celcius unit, that is, -273.15°C.
	 */
	public static final double ABSOLUTE_ZERO_IN_CELCIUS = -273.15;

	/**
	 * The maximum temperature to be used in the Celcius unit, that is, 1E200°C.
	 */
	public static final double MAXIMUM_VALUE_IN_CELCIUS = 1E200;
}