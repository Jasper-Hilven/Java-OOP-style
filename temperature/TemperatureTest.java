package project.temperature;

import static org.junit.Assert.*;
import org.junit.*;


/**
 *A class for checking the temperature class.
 * 
 * @version 1.3
 * @author Stef Noten & Jasper Hilven
 */
public class TemperatureTest {

	/**
	 * Instance variable referencing a valid temperature.
	 */
	private final Temperature validTemperature = new Temperature(280, TemperatureUnit.KELVIN);
	/**
	 * A variables referencing the Celcius temperature unit.
	 */
	private final static TemperatureUnit celciusUnit = TemperatureUnit.CELCIUS;
	/**
	 * A variables referencing the Farenheit temperature unit.
	 */
	private final static TemperatureUnit farenheitUnit = TemperatureUnit.FARENHEIT;
	/**
	 * A variables referencing the Kelvin temperature unit.
	 */
	private final static TemperatureUnit kelvinUnit = TemperatureUnit.KELVIN;

	@Test(expected = NullPointerException.class)
	public void Temperature_NullPointerException() throws Exception {
		new Temperature(0, null);
	}

	@Test(expected = InvalidTemperatureException.class)
	public void Temperature_InvalidTemperatureException() throws Exception {
		new Temperature(Double.NaN);

	}

	@Test
	public void Temperature_HandleValidInputCorrectly() {
		double testValue = 245;
		Temperature validvalueTemperature1 = new Temperature(testValue, celciusUnit);
		Temperature validvalueTemperature2 = new Temperature(testValue, farenheitUnit);
		assertEquals(testValue, validvalueTemperature1.getValue(celciusUnit), 0);
		assertEquals(testValue, validvalueTemperature2.getValue(farenheitUnit), 0);

	}

	@Test(expected = NullPointerException.class)
	public void getValue_NullPointerException() throws Exception {
		validTemperature.getValue(null);
	}

	@Test
	public void getValue_ValidCase() {
		assertEquals(farenheitUnit.convertFromCelcius(validTemperature.getValueInCelcius()),
				validTemperature.getValue(farenheitUnit), 0);
		assertEquals(kelvinUnit.convertFromCelcius(validTemperature.getValueInCelcius()),
				validTemperature.getValue(kelvinUnit), 0);
	}

	@Test
	public void compareTo_HandleValidInput() {
		assertTrue(validTemperature.compareTo(new Temperature(validTemperature.getValueInCelcius())) == 0);
		assertTrue(validTemperature.compareTo(new Temperature(-273)) > 0);
		assertTrue(new Temperature(-273).compareTo(validTemperature) < 0);
	}

	@Test(expected = NullPointerException.class)
	public void compareTo_NullPointerException() {
		validTemperature.compareTo(null);
	}

	@Test
	public void toString_handleValidInput() {
		assertEquals(validTemperature.toString(), Double.toString(validTemperature.getValueInCelcius()) + celciusUnit.getSymbol());
	}
}
