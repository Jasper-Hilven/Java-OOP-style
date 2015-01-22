package project.dungeons;
import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.*;

import project.misc.*;
import project.squares.*;
import project.temperature.*;

/**
 * A test class for the dungeon class.
 * 
 * @version	1.0
 * @author 	Stef Noten & Jasper Hilven
 *
 */
public class DungeonTest {
	
	private Dungeon<Square> dungeon;
	private CompositeDungeon<Square> compDungeon;
	private SquareImpl squareAllWalls, squareAllWalls2;
	private static Position pos0_1_0, pos1_1_0; 
	private static Temperature temp;
	private static BigDecimal hum;
	
	@BeforeClass
	public static void setUpImmutableTextFixture() {
		temp = new Temperature(0);
		hum = BigDecimal.ZERO;
		pos0_1_0 = new Position(0, 1, 0);
		pos1_1_0 = new Position(1, 1, 0);
	}
	@Before
	public void setUpMutableTextFixture() {
		dungeon = new Level<Square>();
		compDungeon = new CompositeDungeon<Square>();
		compDungeon.setSubDungeonAt(new Level<Square>(49, 49), new Position());
		compDungeon.setSubDungeonAt(new Level<Square>(50, 50), new Position(50, 0, 0));
		
		squareAllWalls = new SquareImpl(temp, hum, false, Direction.values());
		squareAllWalls2 = new SquareImpl(temp, hum, false, Direction.values());
	}
	
	@Test
	public void Constructor_Default() {
		assertTrue(this.compDungeon.getMaximumPosition().isIdenticalTo(new Position(100, 100, 100)));
		assertTrue(this.dungeon.getSquares() != null);
		assertEquals(0, this.dungeon.getNbSquares());
	}

	@Test
	public void areAdjacentSquaresConnected_Default() {
		dungeon.setSquareAt(pos0_1_0, squareAllWalls);
		dungeon.setSquareAt(pos1_1_0, squareAllWalls2);
		assertTrue(dungeon.areAdjacentSquaresConnected());
	}
	
	@Test
	public void setSquareAt_LegalCase() {
		dungeon.setSquareAt(pos0_1_0, squareAllWalls);
		dungeon.setSquareAt(pos1_1_0, squareAllWalls2);
		assertEquals(squareAllWalls, dungeon.getSquareAt(pos0_1_0));
		assertEquals(squareAllWalls2, squareAllWalls.getNeighbour(Direction.EAST));
	}
	
	@Test(expected = NullPointerException.class)
	public void setSquareAt_NullPointer() {
		dungeon.setSquareAt(null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setSquareAt_IllegalArgument() {
		dungeon.setSquareAt(new Position(0, 0, 0), squareAllWalls);
	}
	
	@Test
	public void canSetSquareAt_TrueCase() {
		assertTrue(dungeon.canSetSquareAt(pos0_1_0, squareAllWalls));
	}
	
	@Test
	public void canSetSquareAt_FalseCase() {
		assertFalse(dungeon.canSetSquareAt(new Position(1, 1, 1), squareAllWalls));
	}
	
	@Test(expected = NullPointerException.class)
	public void canSetSquareAt_NullPointer() {
		dungeon.canSetSquareAt(null, null);
	}
	
	@Test
	public void getSquareAt_LegalCase() {
		dungeon.setSquareAt(pos0_1_0, squareAllWalls);
		assertEquals(squareAllWalls, dungeon.getSquareAt(pos0_1_0));
	}
	
	@Test(expected = NullPointerException.class)
	public void getSquareAt_NullPointer() {
		dungeon.getSquareAt(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getSquareAt_IllegalArgument() {
		dungeon.getSquareAt(pos0_1_0);
	}

	@Test
	public void removeSquareAt_LegalCase() {
		dungeon.setSquareAt(pos0_1_0, squareAllWalls);
		dungeon.removeSquareAt(pos0_1_0);
		assertFalse(dungeon.hasSquareAt(pos0_1_0));
	}
	
	@Test(expected = NullPointerException.class)
	public void removeSquareAt_NullPointer() {
		dungeon.removeSquareAt(null);
	}

	@Test
	public void hasSquareAt_TrueCase() {
		dungeon.setSquareAt(pos0_1_0, squareAllWalls);
		assertTrue(dungeon.hasSquareAt(pos0_1_0));
	}
	
	@Test
	public void hasSquareAt_FalseCase() {
		assertFalse(dungeon.hasSquareAt(pos0_1_0));
	}
	
	@Test(expected = NullPointerException.class)
	public void hasSquareAt_NullPointer() {
		dungeon.hasSquareAt(null);
	}

	@Test
	public void setMaximumPosition_LegalCase() {
		compDungeon.setMaximumPosition(new Position(200, 200, 200));
		assertEquals(new Position(200, 200, 200), compDungeon.getMaximumPosition());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void setMaximumPosition_IllegalArgument() {
		compDungeon.setMaximumPosition(null);
	}
	
	@Test
	public void canHaveAsMaximumPosition_TrueCase() {
		assertTrue(compDungeon.canHaveAsMaximumPosition(new Position(200, 200, 200)));
	}

	@Test
	public void canHaveAsMaximumPosition_FalseCase() {
		assertFalse(compDungeon.canHaveAsMaximumPosition(new Position(80, 200, 200)));
		assertFalse(compDungeon.canHaveAsMaximumPosition(null));
		assertFalse(compDungeon.getSubDungeonAt(new Position()).canHaveAsMaximumPosition(new Position(51, 50, 0)));
	}
}
