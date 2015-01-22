package project.squares;
import project.dungeons.CompositeDungeon;
import project.dungeons.Level;
import project.dungeons.Position;
import project.misc.Direction;
import project.squares.SquareImpl;
import project.squares.borders.Border;
import project.squares.borders.OpenBorder;
import project.squares.borders.Wall;
import project.temperature.Temperature;
import project.temperature.TemperatureOutOfRangeException;
import project.temperature.TemperatureUnit;
import static org.junit.Assert.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.*;

/**
 * A test class for the square class.
 * 
 * @version	1.0
 * @author 	Stef Noten & Jasper Hilven
 *
 */
public class SquareTest {

	/**
	 * A variable referencing a default square, that is, constructed with the default 
	 * constructor.
	 */
	private Square standardSquare;
	
	/**
	 * A variable representing a composite dungeon with 3 levels stapled on each other.
	 */
	CompositeDungeon<Square> compDungeon;

	/**
	 * A variable referencing a default square, that is, constructed with the default 
	 * constructor, with wall borders set only in the north direction.
	 */
	private SquareImpl squareBorderNorth;

	/**
	 * A variable referencing a temperature of 0°C.
	 */
	private static final Temperature temp0C = new Temperature(0);
	/**
	 * A variable referencing a temperature of -5°C.
	 */
	private static final Temperature tempMin5C = new Temperature(-5);
	/**
	 * A variable referencing a temperature of 30°C.
	 */
	private static final Temperature temp30C = new Temperature(30);
	/**
	 * A variable referencing a temperature of -210°C.
	 */
	private static final Temperature tempInv = new Temperature(-210);

	/**
	 * Set up a mutable text fixture.
	 * @post	standardSquare will reference a standard square, constructed with the default constructor.
	 * @post	squareBorderNorth will reference a standard square, constructed with the default constructor,
	 * 			with a wall borders set the north.
	 * 			| for each direction in Direction.Values()
	 * 			| if(direction == Direction.NORTH
	 * 			|	then squareBorderNorth.getBorderAt(direction)instanceof Wall  
	 * 			| else
	 * 			|   squareBorderNorth.getBorderAt(direction) instanceof OpenBorder
	 *@post		The squareBorderNorth will have a temperature of 125 and a humidity of 100.
	 *			| squareBorderNorth.getTemperature().equals(new Temperature(125) && 
	 *			| squareBorderNorth.getHumidity.equals(new BigDecimal(100));
	 */
	@Before
	public void setUpMutableTextFixture() throws Exception
	{
		compDungeon = new CompositeDungeon<Square>();
		compDungeon.setSubDungeonAt(new Level<Square>(), new Position());
		compDungeon.setSubDungeonAt(new Level<Square>(), new Position(0, 0, 1));
		compDungeon.setSubDungeonAt(new Level<Square>(), new Position(0, 0, 2));
		standardSquare = new SquareImpl();
		squareBorderNorth = new SquareImpl(new Temperature(125), new BigDecimal(100),false,Direction.NORTH);
	}

	@Test
	public void extendedConstructor_LegalCase() throws Exception {
		Temperature temperature = temp0C;
		BigDecimal humidity = new BigDecimal(1.857);
		Square square = new SquareImpl(temperature, humidity,false,Direction.CEILING);

		assertEquals(-200, square.getMinTemperature().getValueInCelcius(), 0);
		assertEquals(5000, square.getMaxTemperature().getValueInCelcius(), 0);
		assertEquals(temperature, square.getTemperature());
		assertTrue(square.getHumidity().compareTo(humidity.setScale(2, RoundingMode.HALF_UP)) == 0);
		assertFalse(square.hasSlipperyMaterial());
		assertTrue(square.getBorderAt(Direction.CEILING) instanceof Wall);
		assertTrue(square.getBorderAt(Direction.FLOOR) instanceof OpenBorder);
	}

	@Test(expected=NullPointerException.class)
	public void extendedConstructor_NonEffectiveTemperature() throws Exception {
		new SquareImpl(null, new BigDecimal(0),false);
	}

	@Test(expected=TemperatureOutOfRangeException.class)
	public void extendedConstructor_InvalidTemperature() throws Exception {
		new SquareImpl(tempInv, new BigDecimal(0),false);
	}

	@Test
	public void changeTemperature_LegalCase() throws Exception {
		standardSquare.changeTemperature(temp0C);
		assertEquals(temp0C, standardSquare.getTemperature());
	}

	@Test(expected=NullPointerException.class)
	public void changeTemperature_NonEffectiveTemperature() throws Exception {
		standardSquare.changeTemperature(null);
	}

	@Test(expected=TemperatureOutOfRangeException.class)
	public void changeTemperature_InvalidTemperature() throws Exception {
		standardSquare.changeTemperature(tempInv);
	}

	@Test
	public void matchTemperatureBoundaries_TrueCase() throws Exception {
		assertTrue(SquareImpl.matchesTemperatureBoundaries(temp0C, tempMin5C, temp30C));
	}

	@Test
	public void matchTemperatureBoundaries_FalseCase() throws Exception {
		assertFalse(SquareImpl.matchesTemperatureBoundaries(tempMin5C, temp0C, temp30C));
		assertFalse(SquareImpl.matchesTemperatureBoundaries(temp0C, temp30C, tempMin5C));
	}

	@Test(expected=NullPointerException.class)
	public void matchTemperatureBoundaries_NonEffectiveTemperature() throws Exception {
		SquareImpl.matchesTemperatureBoundaries(null, temp0C, temp30C);
	}

	@Test(expected=NullPointerException.class)
	public void matchTemperatureBoundaries_NonEffectiveMinTemperature() throws Exception {
		SquareImpl.matchesTemperatureBoundaries(tempMin5C, null, temp30C);
	}

	@Test(expected=NullPointerException.class)
	public void matchTemperatureBoundaries_NonEffectiveMaxTemperature() throws Exception {
		SquareImpl.matchesTemperatureBoundaries(tempMin5C, temp0C, null);
	}

	@Test
	public void setMinTemperature_LegalCase() throws Exception {
		standardSquare.setMinTemperature(temp0C);
		assertEquals(temp0C, standardSquare.getMinTemperature());
	}

	@Test(expected=IllegalArgumentException.class)
	public void setMinTemperature_MinTemperatureGreaterThanMaxTemperature() throws Exception {
		standardSquare.setMinTemperature(new Temperature(5010));
	}

	@Test(expected=TemperatureOutOfRangeException.class)
	public void setMinTemperature_MinTemperatureGreaterThanTemperature() throws Exception {
		standardSquare.setMinTemperature(temp30C);
	}

	@Test(expected=NullPointerException.class)
	public void setMinTemperature_NonEffectiveMinTemperature() throws Exception {
		standardSquare.setMinTemperature(null);
	}

	@Test
	public void setMaxTemperature_LegalCase() throws Exception {
		standardSquare.setMaxTemperature(temp30C);
		assertEquals(temp30C, standardSquare.getMaxTemperature());
	}

	@Test(expected=IllegalArgumentException.class)
	public void setMaxTemperature_MaxTemperatureSmallerThanMinTemperature() throws Exception {
		standardSquare.setMaxTemperature(new Temperature(-210));
	}

	@Test(expected=TemperatureOutOfRangeException.class)
	public void setMaxTemperature_MaxTemperatureSmallerThanTemperature() throws Exception {
		standardSquare.setMaxTemperature(temp0C);
	}

	@Test(expected=NullPointerException.class)
	public void setMaxTemperature_NonEffectiveMaxTemperature() throws Exception {
		standardSquare.setMaxTemperature(null);
	}

	@Test
	public void getColdDamage_ZeroCase() {
		assertEquals(0, standardSquare.getColdDamage());
	}

	@Test
	public void getColdDamage_PositiveCase() {
		standardSquare.changeTemperature(new Temperature(-34));
		assertEquals(2, standardSquare.getColdDamage());
	}

	@Test
	public void getHeatDamage_ZeroCase() {
		assertEquals(0, standardSquare.getHeatDamage());
	}

	@Test
	public void getHeatDamage_PositiveCase() {
		Temperature temperature = new Temperature(
				SquareImpl.getHeatDamageBoundaryTemperature().getValueInCelcius() +
				2.9 / SquareImpl.getHeatDamagePerDegreeCelcius());
		standardSquare.changeTemperature(temperature);
		assertEquals(2, standardSquare.getHeatDamage());
	}

	@Test
	public void getHeatDamage_BigTemperatureCase() {
		Temperature bigTemperature = new Temperature(TemperatureUnit.CELCIUS.getMaximumTemperature());
		standardSquare.setMaxTemperature(bigTemperature);
		standardSquare.changeTemperature(bigTemperature);
		assertEquals(Integer.MAX_VALUE, standardSquare.getHeatDamage());
	}

	@Test
	public void setHeatDamageBoundaryTemperature_LegalCase() {
		SquareImpl.setHeatDamageBoundaryTemperature(temp30C);
		assertEquals(temp30C, SquareImpl.getHeatDamageBoundaryTemperature());
	}

	@Test
	public void setHeatDamagePerDegreeCelcius_LegalCase() throws Exception {
		SquareImpl.setHeatDamagePerDegreeCelcius(1);
		assertEquals(1, SquareImpl.getHeatDamagePerDegreeCelcius(), 0);
	}

	@Test(expected=IllegalArgumentException.class)
	public void setHeatDamagePerDegreeCelcius_NegativeValue() throws Exception {
		SquareImpl.setHeatDamagePerDegreeCelcius(-1);
	}

	@Test
	public void isValidHeatDamagePerDegreeCelcius_TrueCase() {
		assertTrue(SquareImpl.isValidHeatDamagePerDegreeCelcius(1));
	}

	@Test
	public void isValidHeatDamagePerDegreeCelcius_FalseCase() {
		assertFalse(SquareImpl.isValidHeatDamagePerDegreeCelcius(-1));
	}

	@Test
	public void getRustDamage_ZeroCase() {
		standardSquare.changeHumidity(new BigDecimal(10));
		assertEquals(0, standardSquare.getRustDamage());
	}

	@Test
	public void getRustDamage_PositiveCase() throws Exception {
		standardSquare.changeHumidity(new BigDecimal(47.35));
		assertEquals(2, standardSquare.getRustDamage());
	}

	@Test
	public void changeHumidity_LegalCase() {
		standardSquare.changeHumidity(new BigDecimal(20.346));
		assertEquals(20.35, standardSquare.getHumidity().doubleValue(), 0);
	}

	@Test
	public void isValidHumidity_TrueCase() {
		assertTrue(SquareImpl.isValidHumidity(new BigDecimal(20.345)));
	}

	@Test
	public void isValidHumidity_FalseCase() {
		assertFalse(SquareImpl.isValidHumidity(null));
		assertFalse(SquareImpl.isValidHumidity(new BigDecimal(-1)));
		assertFalse(SquareImpl.isValidHumidity(new BigDecimal(101)));
	}

	@Test
	public void isSlippery_TrueCase() {
		Square slipperSquare = new SquareImpl(temp30C, BigDecimal.ZERO, true,Direction.CEILING);
		assertTrue(slipperSquare.isSlippery());
		slipperSquare = new SquareImpl(new Temperature(25), new BigDecimal(100), false,Direction.CEILING);
		assertTrue(slipperSquare.isSlippery());
		slipperSquare = new SquareImpl(tempMin5C, new BigDecimal(11), false,Direction.CEILING);
		assertTrue(slipperSquare.isSlippery());
	}

	@Test
	public void isSlippery_FalseCase() {
		assertFalse(standardSquare.isSlippery());
	}

	@Test
	public void getInhabitability() {
		Temperature tempForHeatDamage2 = new Temperature(
				SquareImpl.getHeatDamageBoundaryTemperature().getValueInCelcius() + 
				2.3 * SquareImpl.getHeatDamagePerDegreeCelcius());
		standardSquare.changeTemperature(tempForHeatDamage2);
		assertEquals(-0.39606, standardSquare.getInhabitability(), 1E-5);

		Temperature tempForColdDamage4 = new Temperature(-47);
		standardSquare.changeTemperature(tempForColdDamage4);
		assertEquals(-2, standardSquare.getInhabitability(), 0);
	}

	@Test
	public void setHeatCapacity_LegalCase() throws Exception {
		SquareImpl.setHeatCapacity(0.3124);
		assertEquals(0.3124, SquareImpl.getHeatCapacity(), 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setHeatCapacity_InvalidValue() throws Exception {
		SquareImpl.setHeatCapacity(0.5);
	}

	@Test
	public void isValidHeatCapacity_TrueCase() {
		assertTrue(SquareImpl.isValidHeatCapacity(0.3));
	}

	@Test
	public void isValidHeatCapacity_FalseCase() {
		assertFalse(SquareImpl.isValidHeatCapacity(0.05));
		assertFalse(SquareImpl.isValidHeatCapacity(0.45));
	}
	
	@Test
	public void getBorders_LegalCase() {
		assertTrue( squareBorderNorth.getBorders().get(Direction.NORTH) instanceof Wall);
		assertTrue( squareBorderNorth.getBorders().get(Direction.SOUTH) instanceof OpenBorder);
	}

	@Test
	public void setBorderAt_LegalCase() {
		Border newWall = new Wall(true, false);
		newWall.build(squareBorderNorth, Direction.SOUTH);
		assertTrue(squareBorderNorth.getBorderAt(Direction.SOUTH) == newWall);
		assertTrue(squareBorderNorth.getBorderAt(Direction.NORTH) instanceof Wall);
		assertTrue(squareBorderNorth.getBorderAt(Direction.EAST) instanceof OpenBorder);
		assertTrue(squareBorderNorth.getBorderAt(Direction.WEST) instanceof OpenBorder);
		assertTrue(squareBorderNorth.getBorderAt(Direction.FLOOR) instanceof OpenBorder);
		assertTrue(squareBorderNorth.getBorderAt(Direction.CEILING) instanceof OpenBorder);
	}
	
	@Test
	public void canHaveAsBorders_LegalCase() {
		Map<Direction, Border> noBorders = new HashMap<Direction, Border>();
		Map<Direction, Border> doorInFloorBorders = new HashMap<Direction, Border>();
			doorInFloorBorders.put(Direction.FLOOR, new Wall(true, false));
			addOpenBorders(doorInFloorBorders);
		Map<Direction, Border> toManyDoorBorders = new HashMap<Direction, Border>();	
		toManyDoorBorders.put(Direction.FLOOR, new Wall(true, false));
			toManyDoorBorders.put(Direction.CEILING, new Wall(true, false));
			toManyDoorBorders.put(Direction.WEST, new Wall(true, false));
			toManyDoorBorders.put(Direction.NORTH, new Wall(true, false));
			addOpenBorders(toManyDoorBorders);
			Map<Direction, Border> goodBorders = new HashMap<Direction, Border>();
		 goodBorders.put(Direction.FLOOR,new Wall(false, false));
		 addOpenBorders(goodBorders);
		Map<Direction, Border> terminatedBorders= new HashMap<Direction, Border>();
			(new Wall(false, false)).build(squareBorderNorth, Direction.SOUTH);
			Border terminatedBorder = squareBorderNorth.getBorderAt(Direction.NORTH);
			terminatedBorder.terminate();
			terminatedBorders.put(Direction.NORTH, terminatedBorder);
			addOpenBorders(terminatedBorders);
		 HashMap<Direction, Border> nullMap = new HashMap<Direction, Border>();
		 	addNullBorders(nullMap);
		assertFalse(squareBorderNorth.canHaveAsBorders(noBorders));
		assertFalse(squareBorderNorth.canHaveAsBorders(doorInFloorBorders));
		assertFalse(squareBorderNorth.canHaveAsBorders(toManyDoorBorders));
		assertTrue(squareBorderNorth.canHaveAsBorders(goodBorders));
		assertFalse(squareBorderNorth.canHaveAsBorders(terminatedBorders));
	}
	
	
	private static void addOpenBorders(Map<Direction, Border>  map){
		for (Direction direction : Direction.values()) {
			if(!map.containsKey(direction))
				map.put(direction, new OpenBorder());
		}
		
	}

	private static void addNullBorders(Map<Direction, Border>  map){
		for (Direction direction : Direction.values()) {
			if(!map.containsKey(direction))
				map.put(direction, null);
		}
		
	}
	@Test
	public void hasProperBorders_LegalCase() {
		assertTrue(squareBorderNorth.hasProperBorders());
	}
	
	@Test
	public void getDominantBordersAt_LegalCase() {
		SquareImpl addSquare = new SquareImpl();
		
		Temperature temp = new Temperature(0);
		BigDecimal hum = BigDecimal.ZERO;
		Square squareAllWalls = new SquareImpl(temp, hum, false, Direction.values());
		
		// becomes a wall
		new Wall(true, false).build(addSquare, Direction.CEILING);
		compDungeon.setSquareAt(new Position(3, 1, 2), 
				new SquareImpl(temp, hum, false, Direction.FLOOR));
		
		// becomes a wall
		new OpenBorder().build(addSquare, Direction.FLOOR);
		compDungeon.setSquareAt(new Position(3, 1, 0), squareAllWalls);
		
		// becomes a wall
		new Wall(false, false).build(addSquare, Direction.WEST);
		compDungeon.setSquareAt(new Position(2, 1, 1), 
				new SquareImpl(temp, hum, false, Direction.EAST));
		
		// becomes a door
		new OpenBorder().build(addSquare, Direction.EAST);
		SquareImpl doorWest = new SquareImpl();
		new Wall(true, false).build(doorWest, Direction.WEST);
		compDungeon.setSquareAt(new Position(4, 1, 1), doorWest); 

		// becomes an open border
		new OpenBorder().build(addSquare, Direction.SOUTH);
		compDungeon.setSquareAt(new Position(3, 0, 1), 
				new SquareImpl());
	
		// becomes a door
		new Wall(true, false).build(addSquare, Direction.NORTH);
		SquareImpl doorSouth = new SquareImpl();
		new Wall(true, false).build(doorSouth, Direction.SOUTH);
		compDungeon.setSquareAt(new Position(3, 2, 1), doorSouth);
		
		Map<Direction, Border> map = addSquare.getDominantBorders(compDungeon.getNeighboursAt(new Position(3, 1, 1)));
		assertTrue(isWall(map, Direction.CEILING, false));
		assertTrue(isWall(map, Direction.FLOOR, false));
		assertTrue(isWall(map, Direction.WEST, false));
		assertTrue(isWall(map, Direction.EAST, true));
		assertTrue(map.get(Direction.SOUTH) instanceof OpenBorder);
		assertTrue(isWall(map, Direction.NORTH, true));
	}
	/**
	 * Check if a given square has a wall in the given direction that is or is not a door,
	 * determined by the hasDoor parameter.
	 */
	private boolean isWall(Map<Direction, Border> map, Direction direction, boolean hasDoor) {
		Border border = map.get(direction);
		return (border instanceof Wall) && (((Wall)border).hasDoor() == hasDoor);
	}

	@Test(expected = NullPointerException.class)
	public void getDominantBordersAt_NonEffectiveNeighbours() {
		new SquareImpl().getDominantBorders(null);
	}
	
	@Test(expected = NullPointerException.class)
	public void getDominantBordersAt_NonEffectiveNeighbourInNeighbours() {
		HashMap<Direction, Square> neighbours = new HashMap<Direction, Square>();
		for (Direction dir : Direction.values()) {
			neighbours.put(dir, dir == Direction.NORTH ? null : new SquareImpl());
		}
		new SquareImpl().getDominantBorders(neighbours);
	}

	@Test
	public void getWallsAt_LegalCase() {
		HashMap<Direction, Border> justOpenBorders = squareBorderNorth.getWallsAt();
		HashMap<Direction, Border> justWalls = squareBorderNorth.getWallsAt(Direction.NORTH,Direction.CEILING,Direction.EAST,Direction.FLOOR,Direction.SOUTH,Direction.WEST);
		for (Direction direction : Direction.values()) {
			assertTrue(justOpenBorders.get(direction) instanceof OpenBorder);
			assertTrue(justWalls.get(direction) instanceof Wall);
		}
		HashMap<Direction, Border> northBorder = squareBorderNorth.getWallsAt(Direction.NORTH);
		assertTrue(northBorder.get(Direction.NORTH) instanceof Wall);
		assertTrue(northBorder.get(Direction.SOUTH) instanceof OpenBorder);
	}

	@Test
	public void hasNeighbours_LegalCase() {
		assertFalse(squareBorderNorth.hasNeighbours());
		new OpenBorder().build(squareBorderNorth, Direction.WEST, new SquareImpl());
		assertTrue(squareBorderNorth.hasNeighbours());
	}

	@Test
	public void getSquaresInSpace_LegalCase() {
		SquareImpl square1 = new SquareImpl();
		SquareImpl square2 = new SquareImpl();
		SquareImpl square3 = new SquareImpl();
		SquareImpl square4 = new SquareImpl();
		SquareImpl square5 = new SquareImpl();
		assertEquals(1, square1.getSquaresInSpace().size());
		new OpenBorder().build(square1, Direction.EAST,square2);
		assertTrue(square1.getSquaresInSpace().contains(square2));
		new OpenBorder().build(square2, Direction.EAST,square3);
		assertTrue(square1.getSquaresInSpace().contains(square3));
		new OpenBorder().build(square3, Direction.EAST,square4);
		assertEquals(4, square1.getSquaresInSpace().size());
		new OpenBorder().build(square4, Direction.SOUTH,square5);
		assertEquals(5, square1.getSquaresInSpace().size());
		assertTrue(square1.getSquaresInSpace().contains(square5));
		
	}
	@Test
	public void isInSpace_LegalCase() {
		SquareImpl square1 = new SquareImpl();
		SquareImpl square2 = new SquareImpl();
		SquareImpl square3 = new SquareImpl();
		SquareImpl square4 = new SquareImpl();
		SquareImpl square5 = new SquareImpl();
		new OpenBorder().build(square1, Direction.EAST,square2);
		new OpenBorder().build(square2, Direction.EAST,square3);
		new OpenBorder().build(square3, Direction.EAST,square4);
		new OpenBorder().build(square4, Direction.SOUTH,square5);
		assertTrue(square1.isInSpace(square5));
	}

	@Test
	public void merge_LegalCase() {
		SquareImpl coldSquare = new SquareImpl(new Temperature(-3), new BigDecimal(10), false, Direction.FLOOR);
		SquareImpl hotSquare = new SquareImpl(new Temperature(103), new BigDecimal(12), false, Direction.FLOOR);
		new OpenBorder().build(coldSquare, Direction.NORTH,hotSquare);
		assertEquals(50, hotSquare.getTemperature().getValueInCelcius(), 0);
		assertEquals(hotSquare.getTemperature().getValueInCelcius(), coldSquare.getTemperature().getValueInCelcius(), 0);
		assertEquals(11, hotSquare.getHumidity().intValue());
		assertEquals(hotSquare.getHumidity(), coldSquare.getHumidity());
		assertTrue(hotSquare.isMerged());
	}	
	
	@Test
	public void canNavigateTo_TrueCase() {
		constructSpacedDungeon();
		Square square1 = compDungeon.getSquareAt(new Position(1, 0, 0));
		Square square2 = compDungeon.getSquareAt(new Position(13, 0, 0));
		
		for (int x = 1; x < 10; x++) {
			for (int y = 0; y < 5; y++) {
				assertTrue(square1.canNavigateTo(compDungeon.getSquareAt(new Position(x, y, 0))));
				assertTrue(square2.canNavigateTo(compDungeon.getSquareAt(new Position(x, y, 0))));
			}
		}
		assertTrue(square1.canNavigateTo(compDungeon.getSquareAt(new Position(3, 3, 1))));
		assertTrue(square2.canNavigateTo(compDungeon.getSquareAt(new Position(3, 3, 1))));
		
		assertTrue(square1.canNavigateTo(compDungeon.getSquareAt(new Position(10, 0, 0))));
		assertTrue(square2.canNavigateTo(compDungeon.getSquareAt(new Position(10, 0, 0))));
		
		assertTrue(square2.canNavigateTo(compDungeon.getSquareAt(new Position(11, 0, 0))));
		for (int x = 12; x < 20; x++) {
			for (int y = 0; y < 5; y++) {
				assertTrue(square2.canNavigateTo(compDungeon.getSquareAt(new Position(x, y, 0))));
			}
		}
		assertTrue(square2.canNavigateTo(compDungeon.getSquareAt(new Position(20, 0, 0))));
	}
	
	@Test
	public void canNavigateTo_FalseCase() {
		constructSpacedDungeon();
		Square square1 = compDungeon.getSquareAt(new Position(1, 0, 0));
		
		assertFalse(square1.canNavigateTo(compDungeon.getSquareAt(new Position(11, 0, 0))));
		for (int x = 12; x < 20; x++) {
			for (int y = 0; y < 5; y++) {
				assertFalse(square1.canNavigateTo(compDungeon.getSquareAt(new Position(x, y, 0))));
			}
		}
		assertFalse(square1.canNavigateTo(compDungeon.getSquareAt(new Position(20, 0, 0))));
	}
	
	private void constructSpacedDungeon() {
		int i = 1;
		while (i < 10) {
			for (int j = 0; j < 5; j++)
				compDungeon.setSquareAt(new Position(i, j, 0), new SquareImpl());
			i++;
		}
		compDungeon.setSquareAt(new Position(3, 3, 1), new SquareImpl(Direction.EAST));
		
		Square doorSquare = new SquareImpl();
		doorSquare.setBorderAt(Direction.WEST, new Wall(true, false));		
		compDungeon.setSquareAt(new Position(10, 0, 0), doorSquare);
		((Wall)doorSquare.getBorderAt(Direction.WEST)).openDoor();
		compDungeon.setSquareAt(new Position(11, 0, 0), new SquareImpl(Direction.WEST));
		i = 12;
		while (i < 20) {
			for (int j = 0; j < 5; j++)
				compDungeon.setSquareAt(new Position(i, j, 0), new SquareImpl());
			i++;
		}
		
		List<Square> teleportationTargets = new ArrayList<Square>();
		teleportationTargets.add(compDungeon.getSquareAt(new Position(1, 0, 0)));
		compDungeon.setSquareAt(new Position(i++, 0, 0), new TeleportationSquareImpl(teleportationTargets));
	}
}
