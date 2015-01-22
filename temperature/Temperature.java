package project.temperature;
import be.kuleuven.cs.som.annotate.*;

/**
 * A temperature with a value that can be expressed in different temperature units.
 * 
 * @Invar 	The Celcius value of this temperature is a valid value for the Celcius unit.
 * 			| TemperatureUnit.CELCIUS.canHaveAsValue(getValueInCelcius()) 
 * 
 * @author 	Stef Noten & Jasper Hilven
 * @version	1.3
 */
@Value
public class Temperature {
	
	/**
	 * Initialise a new temperature, with given value and temperature unit.
	 * 
	 * @param	value 
	 * 				The value of the temperature.
	 * @param	unit 
	 * 				The temperature unit of the temperature.
	 * @throws 	NullPointerException
	 * 				The temperature unit is not effective.
	 * 				| unit == null
	 * @throws 	InvalidTemperatureException(value, unit)
	 * 				The value is invalid for the given temperature unit.
	 * 				| !unit.canHaveAsValue(value)
	 */
	public Temperature(double value, TemperatureUnit unit) 
		throws NullPointerException, InvalidTemperatureException
	{
		if (unit == null)
			throw new NullPointerException();
		this.valueInCelcius = unit.convertToCelcius(value);
	}

	/**
	 * Initialise a new temperature, with given value in the Celcius temperature unit.
	 * 
	 * @param	value 
	 * 				The value of the temperature.
	 * @effect	This constructor has the same effect as calling the more extended constructor with
	 * 			the given value as its value and the Celcius temperature unit as its unit.
	 * 			| this(value, TemperatureUnit.CELCIUS)
	 */
	public Temperature(double value)
		throws InvalidTemperatureException 
	{
		this(value, TemperatureUnit.CELCIUS);
	}
	
	/**
	 * Return the value of this temperature in the Celcius unit. 
	 */
	@Basic @Immutable
	public double getValueInCelcius() {
		return this.valueInCelcius;
	}
	
	/**
	 * Return the value of this temperature in the given temperature unit.
	 * 
	 * @result	The stored Celcius value of this temperature converted to the given temperature unit.
	 * 			| result == unit.convertFromCelcius(getValueInCelcius())
	 */
	public double getValue(TemperatureUnit unit) {
		return unit.convertFromCelcius(getValueInCelcius());
	}
	
	/**
	 * A variable representing the value of this temperature, represented in the Celcius unit.
	 */
	private final double valueInCelcius;
	
	/**
	 * Compare this temperature with a specified temperature.
	 * 
	 * @param 	temperature 	
	 * 				The temperature to compare this temperature with.
	 * @return 	An integer indicating the relation of this temperature and the given temperature.
	 * 			Zero when equal, positive when the former is greater, negative when the former is smaller. 
	 * 			| (result == Double.compare(this.getValueInCelcius(), temperature.getValueInCelcius())) 
	 * @throws 	NullPointerException
	 *  			The specified temperature is not effective.
	 * 				| temperature == null
	 */
	public int compareTo(Temperature temperature) 
		throws NullPointerException
	{
		if (temperature == null)
			throw new NullPointerException();
		return Double.compare(this.getValueInCelcius(), temperature.getValueInCelcius());
	}
	
	/**
	 * Return a string representation of the object. 
	 */
	@Override
	public String toString() {
		return Double.toString(getValueInCelcius()) + TemperatureUnit.CELCIUS.getSymbol();
	}
	
	/**
	 * Indicate whether some other object is "equal to" this one.
	 * 
 	 * @param	other
	 * 				The other object to compare with.
	 */
	@Override
	public boolean equals(Object object){
		if(!(object instanceof Temperature))
			return false;
		return compareTo((Temperature)object) == 0; 
	}

	/**
	 *  Return a hash code value for the object.
	 */
	@Override
	public int hashCode(){
		return new Double(getValueInCelcius()).hashCode(); 
	}
}
