package project.squares.borders;

import static org.junit.Assert.*;

import org.junit.*;

import project.misc.*;
import project.squares.Square;
import project.squares.SquareImpl;

/**
 * A test class for the border class.
 * 
 * @version	1.0
 * @author 	Stef Noten & Jasper Hilven
 */
public class BorderTest {

	private Border border;
	
	@Before
	public void setUpMutableTextFixture() {
		border = new Wall(true, true);
	}
	
	@Test
	public void Constructor_Default() {
		assertEquals(BorderState.UNINITIALISED, border.getState());
	}

	@Test
	public void build_LegalCase() {
		SquareImpl square1 = new SquareImpl(), square2 = new SquareImpl();
		border.build(square1, Direction.EAST, square2);
		assertTrue(border.getState() == BorderState.INITIALISED);
		assertTrue(border.hasProperNeighbours());
		assertEquals(square1, border.getNeighbour1());
		assertEquals(square2, border.getNeighbour2());
		assertTrue(square1.isMerged());
		assertTrue(square2.isMerged());
	}

	@Test(expected = IllegalStateException.class)
	public void build_IllegalState() {
		border.build(new SquareImpl(), Direction.EAST);
		border.build(new SquareImpl(), Direction.EAST);
	}

	@Test(expected = IllegalArgumentException.class)
	public void build_IllegalArgument() {
		new OpenBorder().build(new SquareImpl(), Direction.FLOOR);
	}

	@Test
	public void canBuild_FalseCase() {
		assertFalse(new OpenBorder().canBuild(new SquareImpl(), Direction.FLOOR, null));
	}
	
	@Test
	public void canBuild_TrueCase() {
		Square square1 = new SquareImpl(), square2 = new SquareImpl();
		assertTrue(new OpenBorder().canBuild(square1, Direction.EAST, square2));
	}

	@Test(expected = IllegalStateException.class)
	public void canBuild_IllegalState() {
		border.build(new SquareImpl(), Direction.EAST);
		border.canBuild(new SquareImpl(), Direction.EAST, null);
	}

	@Test
	public void hasProperNeighbours_TrueCase() {
		assertTrue(border.hasProperNeighbours());
		border.build(new SquareImpl(), Direction.EAST, new SquareImpl());
		assertTrue(border.hasProperNeighbours());
		border.terminate();
		assertTrue(border.hasProperNeighbours());
	}

	@Test
	public void areValidNeighbours_FalseCase() {
		assertFalse(Border.areValidNeighbours(new SquareImpl(), Direction.EAST, null, BorderState.UNINITIALISED));
		assertFalse(Border.areValidNeighbours(null, null, null, BorderState.INITIALISED));
	}

	@Test
	public void areValidNeighbours_TrueCase() {
		assertTrue(Border.areValidNeighbours(new SquareImpl(), Direction.EAST, new SquareImpl(), BorderState.INITIALISED));
		assertTrue(Border.areValidNeighbours(new SquareImpl(), Direction.EAST, null, BorderState.INITIALISED));
		assertTrue(Border.areValidNeighbours(null, null, null, BorderState.UNINITIALISED));
		assertTrue(Border.areValidNeighbours(null, null, null, BorderState.TERMINATED));
	}

	@Test
	public void canHaveAsState_TrueCase() {
		assertTrue(border.canHaveAsState(BorderState.UNINITIALISED));
		assertTrue(border.canHaveAsState(BorderState.INITIALISED));
		assertTrue(border.canHaveAsState(BorderState.TERMINATED));
		border.build(new SquareImpl(), Direction.EAST);
		assertTrue(border.canHaveAsState(BorderState.INITIALISED));
		assertTrue(border.canHaveAsState(BorderState.TERMINATED));
		border.terminate();
		assertTrue(border.canHaveAsState(BorderState.TERMINATED));
	}

	public void canHaveAsState_FalseCase() {
		border.build(new SquareImpl(), Direction.EAST);
		assertFalse(border.canHaveAsState(BorderState.UNINITIALISED));
		border.terminate();
		assertFalse(border.canHaveAsState(BorderState.UNINITIALISED));
		assertFalse(border.canHaveAsState(BorderState.INITIALISED));
	}

	@Test
	public void terminate_LegalCase() {
		SquareImpl square1 = new SquareImpl(), square2 = new SquareImpl();
		border.build(square1, Direction.EAST, square2);
		border.terminate();
		assertEquals(null, border.getNeighbour1());
		assertEquals(null, border.getNeighbour2());
		assertTrue(square1.getBorderAt(Direction.EAST) instanceof OpenBorder);		
	}

	@Test
	public void canTerminate_TrueCase() {
		assertTrue(border.canTerminate());
		border.build(new SquareImpl(), Direction.EAST, new SquareImpl());
		assertTrue(border.canTerminate());
	}

	@Test
	public void canTerminate_FalseCase() {
		assertFalse(new SquareImpl().getBorderAt(Direction.FLOOR).canTerminate());
		Border border2 = new Wall(false, false);
		border2.build(new SquareImpl(), Direction.CEILING, new SquareImpl());
		assertFalse(border2.canTerminate());
	}
	
	@Test
	public void getUninitialisedCopy() {
		border.build(new SquareImpl(), Direction.EAST);
		assertEquals(BorderState.UNINITIALISED, border.getUninitialisedCopy().getState());
	}
}
