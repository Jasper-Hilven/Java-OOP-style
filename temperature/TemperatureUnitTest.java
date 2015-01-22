package project.temperature;

import static org.junit.Assert.*;
import org.junit.*;


/**
 * A test class to test the TemperatureUnit class.
 * 
 * @version 1.2
 * @author Stef Noten & Jasper Hilven
 */
public class TemperatureUnitTest {

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

	@Test
	public void convertTo_HandleValidInput() throws Exception {
		assertEquals(23.13, celciusUnit.convertTo(celciusUnit, 23.13), 0);
		assertEquals(23.13, kelvinUnit.convertTo(kelvinUnit, 23.13), 0);
		assertEquals(23.13, farenheitUnit.convertTo(farenheitUnit, 23.13), 0);
		assertEquals(23.13 - TemperatureUnit.ABSOLUTE_ZERO_IN_CELCIUS, 
				celciusUnit.convertTo(kelvinUnit, 23.13), 0);
	}

	@Test(expected = NullPointerException.class)
	public void convertTo_NullPointerException() throws Exception {
		celciusUnit.convertTo(null, 0);
	}

	@Test(expected = InvalidTemperatureException.class)
	public void convertTo_InvalidTemperatureException() throws Exception {
		kelvinUnit.convertTo(celciusUnit, Double.NEGATIVE_INFINITY);
	}
	
	@Test
	public void convertFromCelcius_HandleValidInput() throws Exception {
		assertEquals(273.15, kelvinUnit.convertFromCelcius(0), 0);
		assertEquals(18 * 1.8 + 32, farenheitUnit.convertFromCelcius(18), 0);
	}

	@Test(expected = InvalidTemperatureException.class)
	public void convertFromCelcius_InvalidTemperatureException() throws Exception {
		farenheitUnit.convertFromCelcius(Double.MAX_VALUE);
	}

	@Test
	public void convertToCelcius_HandleValidInput() throws Exception {
		assertEquals(200, celciusUnit.convertToCelcius(200), 0);
		assertEquals(-273.15, kelvinUnit.convertToCelcius(0), 0);
		assertEquals(0, farenheitUnit.convertToCelcius(32), 0);
	}

	@Test(expected = InvalidTemperatureException.class)
	public void convertToCelcius_InvalidTemperatureException() throws Exception {
		celciusUnit.convertToCelcius(Double.NaN);
	}

	@Test
	public void canHaveAsValue_TrueCase() {
		assertTrue(kelvinUnit.canHaveAsValue(0));
		assertTrue(celciusUnit.canHaveAsValue(-273.15));
		assertTrue(farenheitUnit.canHaveAsValue(0));
	}
	
	@Test
	public void canHaveAsValue_FalseCase() {
		assertFalse(kelvinUnit.canHaveAsValue(-1));
		assertFalse(celciusUnit.canHaveAsValue(-273.16));
		assertFalse(farenheitUnit.canHaveAsValue(Double.NEGATIVE_INFINITY));
	}
}
